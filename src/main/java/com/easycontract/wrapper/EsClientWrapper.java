package com.easycontract.wrapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class EsClientWrapper {

    private final ElasticsearchClient client;

    public EsClientWrapper(ElasticsearchClient client) {
        this.client = client;
    }

    //---------------------- 基础 CRUD ----------------------

    /**
     * 创建/更新文档
     * @param index 索引名
     * @param id    文档ID
     * @param doc   文档对象
     */
    public <T> void saveDocument(String index, String id, T doc) throws IOException {
        IndexRequest<T> request = IndexRequest.of(b -> b
                .index(index)
                .id(id)
                .document(doc)
        );
        client.index(request);
    }

    /**
     * 根据ID获取文档
     * @param index 索引名
     * @param id    文档ID
     * @param clazz 返回类型Class
     */
    public <T> T getDocumentById(String index, String id, Class<T> clazz) throws IOException {
        GetRequest request = GetRequest.of(b -> b
                .index(index)
                .id(id)
        );
        GetResponse<T> response = client.get(request, clazz);
        return response.source();
    }

    /**
     * 根据ID删除文档
     * @param index 索引名
     * @param id    文档ID
     */
    public void deleteDocument(String index, String id) throws IOException {
        DeleteRequest request = DeleteRequest.of(b -> b
                .index(index)
                .id(id)
        );
        client.delete(request);
    }

    //---------------------- 搜索操作 ----------------------

    /**
     * 匹配查询
     * @param index   索引名
     * @param field   查询字段
     * @param keyword 关键词
     * @param clazz   返回类型Class
     */
    public <T> List<T> matchQuery(String index, String field, String keyword, Class<T> clazz) throws IOException {
        SearchResponse<T> response = client.search(s -> s
                .index(index)
                .query(q -> q
                        .match(m -> m
                                .field(field)
                                .query(keyword)
                        )
                ), clazz);

        return extractHits(response);
    }

    /**
     * 范围查询
     * @param index 索引名
     * @param field 字段名
     * @param from  起始值
     * @param to    结束值
     */
    public <T> List<T> rangeQuery(String index, String field, Number from, Number to, Class<T> clazz) throws IOException {
        SearchResponse<T> response = client.search(s -> s
                .index(index)
                .query(q -> q
                        .range(r -> r
                                .field(field)
                                .gte(JsonData.of(from))
                                .lte(JsonData.of(to))
                        )
                ), clazz);

        return extractHits(response);
    }

    //---------------------- 批量操作 ----------------------

    /**
     * 批量插入文档
     * @param index 索引名
     * @param docs  Map格式文档列表 (key=id, value=文档对象)
     */
    public <T> void bulkInsert(String index, Map<String, T> docs) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Map.Entry<String, T> entry : docs.entrySet()) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(index)
                            .id(entry.getKey())
                            .document(entry.getValue())
                    )
            );
        }

        client.bulk(br.build());
    }

    /**
     * 根据ID获取文档的指定字段
     */
    public <T> T getDocumentFieldsById(String index, String id, List<String> fields, Class<T> clazz) throws IOException {
        GetRequest request = GetRequest.of(b -> b
                .index(index)
                .id(id)
                .source(s -> s  // s 是 SourceConfigParam.Builder
                        .fields(fields) // 直接传递字段列表
                )
        );

        GetResponse<T> response = client.get(request, clazz);
        return response.source();
    }

    /**
     * 在搜索时指定返回字段
     */
    public <T> List<T> searchWithFields(String index,
                                        Function<Query.Builder, ObjectBuilder<Query>> queryBuilder,
                                        List<String> fields,
                                        Class<T> clazz) throws IOException {
        SearchRequest request = SearchRequest.of(b -> b
                .index(index)
                .source(s -> s  // 直接配置 source
                        .filter(f -> f
                                .includes(fields)
                        )
                )
                .query(queryBuilder)  // 使用 Function 传递查询构建逻辑
        );

        SearchResponse<T> response = client.search(request, clazz);
        return extractHits(response);
    }

    //---------------------- 辅助方法 ----------------------

    private <T> List<T> extractHits(SearchResponse<T> response) {
        List<T> results = new ArrayList<>();
        for (Hit<T> hit : response.hits().hits()) {
            results.add(hit.source());
        }
        return results;
    }
}
