package com.jmall.product.service;

import com.jmall.product.entity.Category;
import com.jmall.product.vo.CategoryTreeVO;

import java.util.List;

/**
 * 分类服务
 *
 * @author jmall
 */
public interface CategoryService {

    /** 获取分类树 */
    List<CategoryTreeVO> getTree();

    /** 新增分类 */
    Category add(Category category);

    /** 更新分类 */
    void update(Category category);

    /** 删除分类（有子分类时禁止） */
    void delete(Long id);

    /** 根据ID查询 */
    Category getById(Long id);
}
