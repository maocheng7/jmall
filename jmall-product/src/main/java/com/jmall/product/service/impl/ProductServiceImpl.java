package com.jmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jmall.api.product.dto.SkuDTO;
import com.jmall.common.core.constant.MqConstants;
import com.jmall.common.core.constant.RedisConstants;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.PageResult;
import com.jmall.common.core.result.ResultCode;
import com.jmall.common.redis.utils.CacheUtils;
import com.jmall.product.dto.SpuSaveDTO;
import com.jmall.product.entity.ProductSku;
import com.jmall.product.entity.ProductSpu;
import com.jmall.product.mapper.ProductSkuMapper;
import com.jmall.product.mapper.ProductSpuMapper;
import com.jmall.product.service.ProductService;
import com.jmall.product.vo.ProductDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 * <p>
 * 上/下架时通过 RocketMQ 通知搜索服务同步 ES 索引。
 * </p>
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductSpuMapper spuMapper;
    private final ProductSkuMapper skuMapper;
    private final CacheUtils cacheUtils;
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveSpu(SpuSaveDTO dto) {
        ProductSpu spu;
        if (dto.getId() != null) {
            spu = spuMapper.selectById(dto.getId());
            if (spu == null) {
                throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
            }
            fillSpu(spu, dto);
            spuMapper.updateById(spu);
            // 删除旧 SKU 后重建
            skuMapper.delete(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getSpuId, spu.getId()));
        } else {
            spu = new ProductSpu();
            fillSpu(spu, dto);
            spu.setSpuNo("SPU" + System.currentTimeMillis());
            spu.setSales(0);
            spu.setStatus(0);
            spu.setAuditStatus(1);
            spuMapper.insert(spu);
        }

        // 保存 SKU
        if (dto.getSkus() != null && !dto.getSkus().isEmpty()) {
            for (SpuSaveDTO.SkuItem item : dto.getSkus()) {
                ProductSku sku = new ProductSku();
                sku.setSpuId(spu.getId());
                sku.setSkuNo("SKU" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
                sku.setName(item.getName());
                sku.setSpecValue(item.getSpecValue());
                sku.setImage(item.getImage());
                sku.setPrice(item.getPrice());
                sku.setOriginalPrice(item.getOriginalPrice());
                sku.setCostPrice(item.getCostPrice());
                sku.setWeight(item.getWeight() != null ? item.getWeight() : java.math.BigDecimal.ZERO);
                sku.setStatus(item.getStatus() != null ? item.getStatus() : 1);
                skuMapper.insert(sku);
            }
        }
        // 清缓存
        cacheUtils.delete(RedisConstants.PRODUCT_DETAIL_KEY + spu.getId());
        log.info("保存商品: spuId={}, name={}", spu.getId(), spu.getName());
        return spu.getId();
    }

    @Override
    public ProductDetailVO getDetail(Long spuId) {
        String cacheKey = RedisConstants.PRODUCT_DETAIL_KEY + spuId;
        Object cached = cacheUtils.get(cacheKey);
        if (cached instanceof ProductDetailVO) {
            return (ProductDetailVO) cached;
        }

        ProductSpu spu = spuMapper.selectById(spuId);
        if (spu == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        List<ProductSku> skus = skuMapper.selectList(
                new LambdaQueryWrapper<ProductSku>()
                        .eq(ProductSku::getSpuId, spuId)
                        .eq(ProductSku::getStatus, 1));

        ProductDetailVO vo = new ProductDetailVO();
        vo.setSpu(spu);
        vo.setSkus(skus);
        cacheUtils.set(cacheKey, vo, RedisConstants.PRODUCT_DETAIL_TTL);
        return vo;
    }

    @Override
    public PageResult<ProductSpu> pageOnSale(Long categoryId, String keyword, int page, int size) {
        Page<ProductSpu> p = new Page<>(page, size);
        LambdaQueryWrapper<ProductSpu> wrapper = new LambdaQueryWrapper<ProductSpu>()
                .eq(ProductSpu::getStatus, 1)
                .eq(ProductSpu::getAuditStatus, 1);
        if (categoryId != null) {
            wrapper.eq(ProductSpu::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ProductSpu::getName, keyword)
                    .or().like(ProductSpu::getSearchKeyword, keyword));
        }
        wrapper.orderByDesc(ProductSpu::getSales).orderByDesc(ProductSpu::getCreateTime);
        Page<ProductSpu> result = spuMapper.selectPage(p, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public PageResult<ProductSpu> pageByMerchant(Long merchantId, Integer status, int page, int size) {
        Page<ProductSpu> p = new Page<>(page, size);
        LambdaQueryWrapper<ProductSpu> wrapper = new LambdaQueryWrapper<ProductSpu>()
                .eq(ProductSpu::getMerchantId, merchantId);
        if (status != null) {
            wrapper.eq(ProductSpu::getStatus, status);
        }
        wrapper.orderByDesc(ProductSpu::getCreateTime);
        Page<ProductSpu> result = spuMapper.selectPage(p, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onShelf(Long spuId) {
        ProductSpu spu = spuMapper.selectById(spuId);
        if (spu == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        // 至少有一个可用 SKU
        long skuCount = skuMapper.selectCount(
                new LambdaQueryWrapper<ProductSku>()
                        .eq(ProductSku::getSpuId, spuId)
                        .eq(ProductSku::getStatus, 1));
        if (skuCount == 0) {
            throw new BusinessException(ResultCode.PRODUCT_SHELF_ERROR, "无可用SKU，无法上架");
        }
        spuMapper.update(null, new LambdaUpdateWrapper<ProductSpu>()
                .eq(ProductSpu::getId, spuId)
                .set(ProductSpu::getStatus, 1));
        cacheUtils.delete(RedisConstants.PRODUCT_DETAIL_KEY + spuId);
        // 通知搜索服务同步 ES
        sendProductMq(MqConstants.TOPIC_PRODUCT_UP, spuId);
        log.info("商品上架: spuId={}", spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offShelf(Long spuId) {
        ProductSpu spu = spuMapper.selectById(spuId);
        if (spu == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        spuMapper.update(null, new LambdaUpdateWrapper<ProductSpu>()
                .eq(ProductSpu::getId, spuId)
                .set(ProductSpu::getStatus, 0));
        cacheUtils.delete(RedisConstants.PRODUCT_DETAIL_KEY + spuId);
        sendProductMq(MqConstants.TOPIC_PRODUCT_DOWN, spuId);
        log.info("商品下架: spuId={}", spuId);
    }

    @Override
    public ProductSku getSkuById(Long skuId) {
        ProductSku sku = skuMapper.selectById(skuId);
        if (sku == null) {
            throw new BusinessException(ResultCode.SKU_NOT_FOUND);
        }
        return sku;
    }

    @Override
    public List<ProductSku> listSkuByIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return new ArrayList<>();
        }
        return skuMapper.selectBatchIds(skuIds);
    }

    @Override
    public boolean checkSkuOnSale(Long skuId) {
        ProductSku sku = skuMapper.selectById(skuId);
        if (sku == null || sku.getStatus() != 1) {
            return false;
        }
        ProductSpu spu = spuMapper.selectById(sku.getSpuId());
        return spu != null && spu.getStatus() == 1 && spu.getAuditStatus() == 1;
    }

    @Override
    public SkuDTO toSkuDTO(ProductSku sku) {
        if (sku == null) {
            return null;
        }
        ProductSpu spu = spuMapper.selectById(sku.getSpuId());
        SkuDTO dto = new SkuDTO();
        dto.setSkuId(sku.getId());
        dto.setSpuId(sku.getSpuId());
        dto.setProductName(spu != null ? spu.getName() : sku.getName());
        dto.setSpecValue(sku.getSpecValue());
        dto.setPrice(sku.getPrice());
        dto.setOriginalPrice(sku.getOriginalPrice());
        dto.setImageUrl(sku.getImage() != null ? sku.getImage() : (spu != null ? spu.getMainImage() : null));
        dto.setMerchantId(spu != null ? spu.getMerchantId() : null);
        dto.setStatus(sku.getStatus());
        // stock 字段由库存服务补充，这里不填
        return dto;
    }

    private void fillSpu(ProductSpu spu, SpuSaveDTO dto) {
        spu.setMerchantId(dto.getMerchantId());
        spu.setShopId(dto.getShopId());
        spu.setCategoryId(dto.getCategoryId());
        spu.setBrandId(dto.getBrandId());
        spu.setName(dto.getName());
        spu.setSubtitle(dto.getSubtitle());
        spu.setMainImage(dto.getMainImage());
        spu.setImages(dto.getImages());
        spu.setDetail(dto.getDetail());
        spu.setSearchKeyword(dto.getSearchKeyword());
    }

    private void sendProductMq(String topic, Long spuId) {
        try {
            rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(spuId).build());
            log.info("商品MQ消息发送成功: topic={}, spuId={}", topic, spuId);
        } catch (Exception e) {
            // MQ 失败不影响主流程，搜索可后续补偿
            log.error("商品MQ消息发送失败: topic={}, spuId={}, err={}", topic, spuId, e.getMessage());
        }
    }
}
