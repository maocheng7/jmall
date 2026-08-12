package com.jmall.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jmall.api.merchant.dto.ShopDTO;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.merchant.entity.Merchant;
import com.jmall.merchant.entity.MerchantApply;
import com.jmall.merchant.entity.Shop;
import com.jmall.merchant.mapper.MerchantApplyMapper;
import com.jmall.merchant.mapper.MerchantMapper;
import com.jmall.merchant.mapper.ShopMapper;
import com.jmall.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
 private final MerchantMapper merchantMapper; private final MerchantApplyMapper applyMapper; private final ShopMapper shopMapper;
 @Override @Transactional public MerchantApply apply(MerchantApply a) { a.setStatus(0); applyMapper.insert(a); return a; }
 @Override public List<MerchantApply> listApply(Long userId) { return applyMapper.selectList(new LambdaQueryWrapper<MerchantApply>().eq(MerchantApply::getUserId,userId).orderByDesc(MerchantApply::getApplyTime)); }
 @Override @Transactional public Shop saveShop(Shop shop) { if (shop.getMerchantId()==null || !isApproved(shop.getMerchantId())) throw new BusinessException("商家未通过审核"); shopMapper.insert(shop); return shop; }
 @Override @Transactional public Shop updateShop(Long id, Shop input) { Shop old=shopMapper.selectById(id); if(old==null) throw new BusinessException("店铺不存在"); input.setId(id); input.setMerchantId(old.getMerchantId()); shopMapper.updateById(input); return shopMapper.selectById(id); }
 @Override public ShopDTO getShopByMerchantId(Long merchantId) { Shop s=shopMapper.selectOne(new LambdaQueryWrapper<Shop>().eq(Shop::getMerchantId,merchantId).eq(Shop::getStatus,1)); return toDTO(s); }
 @Override public ShopDTO getShopById(Long id) { return toDTO(shopMapper.selectById(id)); }
 @Override public boolean isApproved(Long id) { return merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getId,id).eq(Merchant::getStatus,1))>0; }
 private ShopDTO toDTO(Shop s) { if(s==null)return null; ShopDTO d=new ShopDTO(); d.setShopId(s.getId()); d.setMerchantId(s.getMerchantId()); d.setShopName(s.getShopName()); d.setLogo(s.getLogo()); d.setDescription(s.getDescription()); d.setStatus(s.getStatus()); return d; }
}
