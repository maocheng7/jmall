package com.jmall.product.controller;

import com.jmall.common.core.result.Result;
import com.jmall.product.entity.Category;
import com.jmall.product.service.CategoryService;
import com.jmall.product.vo.CategoryTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 *
 * @author jmall
 */
@Tag(name = "商品分类")
@RestController
@RequestMapping("/api/product/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "分类树（用户端公开）")
    @GetMapping("/tree")
    public Result<List<CategoryTreeVO>> tree() {
        return Result.success(categoryService.getTree());
    }

    @Operation(summary = "新增分类（管理端）")
    @PostMapping
    public Result<Category> add(@Valid @RequestBody Category category) {
        return Result.success(categoryService.add(category));
    }

    @Operation(summary = "更新分类（管理端）")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.update(category);
        return Result.success();
    }

    @Operation(summary = "删除分类（管理端）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
