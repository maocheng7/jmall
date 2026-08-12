package com.jmall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.PageResult;
import com.jmall.common.core.result.ResultCode;
import com.jmall.search.entity.ProductEsDoc;
import com.jmall.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索服务实现（Elasticsearch 8.x Java Client）
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final String INDEX = "jmall_product";

    private final ElasticsearchClient esClient;

    @Override
    public PageResult<ProductEsDoc> search(String keyword, Long categoryId, Long brandId,
                                           BigDecimal minPrice, BigDecimal maxPrice,
                                           String sort, int page, int size) {
        try {
            BoolQuery.Builder bool = new BoolQuery.Builder();
            // 仅上架
            bool.filter(f -> f.term(t -> t.field("status").value(1)));

            if (StringUtils.hasText(keyword)) {
                bool.must(m -> m.multiMatch(mm -> mm
                        .query(keyword)
                        .fields("name^3", "subtitle^2", "searchKeyword")));
            }
            if (categoryId != null) {
                bool.filter(f -> f.term(t -> t.field("categoryId").value(categoryId)));
            }
            if (brandId != null) {
                bool.filter(f -> f.term(t -> t.field("brandId").value(brandId)));
            }
            if (minPrice != null || maxPrice != null) {
                bool.filter(f -> f.range(r -> {
                    r.field("minPrice");
                    if (minPrice != null) r.gte(co.elastic.clients.json.JsonData.of(minPrice));
                    if (maxPrice != null) r.lte(co.elastic.clients.json.JsonData.of(maxPrice));
                    return r;
                }));
            }

            int from = Math.max(page - 1, 0) * size;
            SearchRequest.Builder req = new SearchRequest.Builder()
                    .index(INDEX)
                    .from(from)
                    .size(size)
                    .query(Query.of(q -> q.bool(bool.build())));

            // 排序
            if ("price_asc".equals(sort)) {
                req.sort(s -> s.field(f -> f.field("minPrice").order(SortOrder.Asc)));
            } else if ("price_desc".equals(sort)) {
                req.sort(s -> s.field(f -> f.field("minPrice").order(SortOrder.Desc)));
            } else if ("sales_desc".equals(sort)) {
                req.sort(s -> s.field(f -> f.field("sales").order(SortOrder.Desc)));
            } else {
                // 默认：相关度 + 销量
                req.sort(s -> s.score(sc -> sc.order(SortOrder.Desc)));
                req.sort(s -> s.field(f -> f.field("sales").order(SortOrder.Desc)));
            }

            SearchResponse<ProductEsDoc> response = esClient.search(req.build(), ProductEsDoc.class);
            List<ProductEsDoc> records = new ArrayList<>();
            for (Hit<ProductEsDoc> hit : response.hits().hits()) {
                if (hit.source() != null) {
                    ProductEsDoc doc = hit.source();
                    if (doc.getId() == null) {
                        doc.setId(hit.id());
                    }
                    records.add(doc);
                }
            }
            long total = response.hits().total() != null ? response.hits().total().value() : 0L;
            return PageResult.of(total, page, size, records);
        } catch (Exception e) {
            log.error("ES搜索失败: keyword={}, err={}", keyword, e.getMessage(), e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "搜索服务暂不可用");
        }
    }

    @Override
    public void indexProduct(Long spuId) {
        try {
            // 简化索引：文档 ID = spuId；完整字段可后续通过 product 详情 Feign 补全
            ProductEsDoc doc = new ProductEsDoc();
            doc.setId(String.valueOf(spuId));
            doc.setSpuId(spuId);
            doc.setStatus(1);

            esClient.index(IndexRequest.of(i -> i
                    .index(INDEX)
                    .id(String.valueOf(spuId))
                    .document(doc)));
            log.info("ES 索引商品成功: spuId={}", spuId);
        } catch (Exception e) {
            log.error("ES 索引商品失败: spuId={}, err={}", spuId, e.getMessage(), e);
        }
    }

    @Override
    public void deleteProduct(Long spuId) {
        try {
            esClient.delete(DeleteRequest.of(d -> d
                    .index(INDEX)
                    .id(String.valueOf(spuId))));
            log.info("ES 删除商品成功: spuId={}", spuId);
        } catch (Exception e) {
            log.error("ES 删除商品失败: spuId={}, err={}", spuId, e.getMessage(), e);
        }
    }
}
