package com.jmall.search.controller;

import com.jmall.common.core.result.PageResult;
import com.jmall.common.core.result.Result;
import com.jmall.search.entity.ProductEsDoc;
import com.jmall.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 搜索控制器
 *
 * @author jmall
 */
@Tag(name = "商品搜索")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "商品搜索", description = "支持关键词/分类/品牌/价格区间/排序")
    @GetMapping
    public Result<PageResult<ProductEsDoc>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "default") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(searchService.search(keyword, categoryId, brandId, minPrice, maxPrice, sort, page, size));
    }
}
