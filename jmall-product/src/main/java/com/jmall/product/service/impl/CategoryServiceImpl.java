package com.jmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.ResultCode;
import com.jmall.common.redis.utils.CacheUtils;
import com.jmall.common.core.constant.RedisConstants;
import com.jmall.product.entity.Category;
import com.jmall.product.mapper.CategoryMapper;
import com.jmall.product.service.CategoryService;
import com.jmall.product.vo.CategoryTreeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现（树形结构 + Redis 缓存）
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CacheUtils cacheUtils;

    @Override
    @SuppressWarnings("unchecked")
    public List<CategoryTreeVO> getTree() {
        // 优先读缓存
        Object cached = cacheUtils.get(RedisConstants.PRODUCT_CATEGORY_TREE_KEY);
        if (cached instanceof List) {
            return (List<CategoryTreeVO>) cached;
        }

        List<Category> all = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort));
        List<CategoryTreeVO> tree = buildTree(all, 0L);
        cacheUtils.set(RedisConstants.PRODUCT_CATEGORY_TREE_KEY, tree, 3600L);
        return tree;
    }

    @Override
    public Category add(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        // 计算层级
        if (category.getParentId() == 0L) {
            category.setLevel(1);
        } else {
            Category parent = getById(category.getParentId());
            category.setLevel(parent.getLevel() + 1);
        }
        categoryMapper.insert(category);
        clearTreeCache();
        log.info("新增分类: id={}, name={}", category.getId(), category.getName());
        return category;
    }

    @Override
    public void update(Category category) {
        getById(category.getId());
        categoryMapper.updateById(category);
        clearTreeCache();
    }

    @Override
    public void delete(Long id) {
        getById(id);
        long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(ResultCode.CATEGORY_HAS_CHILDREN);
        }
        categoryMapper.deleteById(id);
        clearTreeCache();
        log.info("删除分类: id={}", id);
    }

    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        return category;
    }

    private List<CategoryTreeVO> buildTree(List<Category> all, Long parentId) {
        Map<Long, List<Category>> group = all.stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        return buildChildren(group, parentId);
    }

    private List<CategoryTreeVO> buildChildren(Map<Long, List<Category>> group, Long parentId) {
        List<Category> children = group.getOrDefault(parentId, List.of());
        List<CategoryTreeVO> result = new ArrayList<>();
        for (Category c : children) {
            CategoryTreeVO vo = new CategoryTreeVO();
            vo.setId(c.getId());
            vo.setParentId(c.getParentId());
            vo.setName(c.getName());
            vo.setLevel(c.getLevel());
            vo.setSort(c.getSort());
            vo.setIcon(c.getIcon());
            vo.setImage(c.getImage());
            vo.setStatus(c.getStatus());
            vo.setChildren(buildChildren(group, c.getId()));
            result.add(vo);
        }
        return result;
    }

    private void clearTreeCache() {
        cacheUtils.delete(RedisConstants.PRODUCT_CATEGORY_TREE_KEY);
    }
}
