package com.eking.momp.model.service.impl;

import com.eking.momp.common.Constant;
import com.eking.momp.common.bean.PageBean;
import com.eking.momp.common.exception.ElasticSearchIOException;
import com.eking.momp.model.dto.EntityIdDto;
import com.eking.momp.model.dto.ModelDto;
import com.eking.momp.model.dto.ModelFieldDto;
import com.eking.momp.model.service.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class EntitySearchServiceImpl implements EntitySearchService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ModelService modelService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private ModelFieldService modelFieldService;
    @Autowired
    private LayerService layerService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private static final String ALIAS_NAME = "momp_entity_index";
    private static final String FIELD_MODEL_NAME = "%model_name%";
    private static final String FIELD_LAYER_NAME = "%layer_name%";
    private static final String FAILD_UPDATE_KEY = "search::faild::entity_id";
    private static final int TIMEOUT_MINITES = 5;


    @Override
    public void update(String index, String id) throws IOException {
        createIndexIfAbsent(index);
        ModelDto model = modelService.getByCode(index);
        String layerName = model.getLayerId() == null ? null : layerService.getById(model.getLayerId()).getName();
        Map<String, Object> entity = entityService.getById(index, id);

        Map<String, Object> params = getParams(model.getId(), entity, model.getName(), layerName);

        IndexRequest request = new IndexRequest(index)
                .id(id)
                .create(false)
                .source(params);
        client.index(request, RequestOptions.DEFAULT);
    }

    @Override
    public void delete(String index, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        if (client.exists(getRequest, RequestOptions.DEFAULT)) {
            DeleteRequest deleteRequest = new DeleteRequest(index, id);
            client.delete(deleteRequest, RequestOptions.DEFAULT);
        }
    }

    @Override
    public void updateIndex(String index) throws IOException {
        ModelDto model = modelService.getByCode(index);
        String layerName = model.getLayerId() == null ? null : layerService.getById(model.getLayerId()).getName();
        List<Map<String, Object>> entities = entityService.listAll(index);
        List<ModelFieldDto> fields = modelFieldService.listByModelId(model.getId());

        this.recreateIndex(index);
        List<String> faildIds = new ArrayList<>();
        BulkRequest bulkRequest = new BulkRequest(index).timeout(TimeValue.timeValueMinutes(TIMEOUT_MINITES));
        for (int i = 0; i < entities.size(); i++) {
            Map<String, Object> entity = entities.get(i);
            String id = entity.get("id").toString();
            Map<String, Object> params = getParams(entity, fields, model.getName(), layerName);

            IndexRequest indexRequest = new IndexRequest()
                    .id(id)
                    .create(true)
                    .source(params);
            bulkRequest.add(indexRequest);

            if ((i + 1) % 4000 == 0 || i == entities.size() - 1) {
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (bulkResponse.hasFailures()) {
                    for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                        if (bulkItemResponse.isFailed()) {
                            String failedId = bulkItemResponse.getFailure().getId();
                            faildIds.add(failedId);
                        }
                    }
                }
                bulkRequest = new BulkRequest(index).timeout(TimeValue.timeValueMinutes(TIMEOUT_MINITES));
            }
        }
        if (faildIds.size() > 0) {
            redisTemplate.opsForSet().add(FAILD_UPDATE_KEY, faildIds.toArray(new String[0]));
        }
    }

    @Override
    public void rebuildIndex(String modelCode) {
        try {
            if (modelCode != null) {
                this.updateIndex(modelCode);
            } else {
                List<ModelDto> models = modelService.list();
                for (ModelDto model : models) {
                    this.updateIndex(model.getCode());
                }
            }
        } catch (IOException e) {
            throw new ElasticSearchIOException(e);
        }
    }

    @Override
    public PageBean<EntityIdDto> search(String keyword, String lastId) {
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .sort("_id")
                .query(QueryBuilders.queryStringQuery("\"" + keyword + "\""))
                .size(Constant.PAGE_SIZE);
        if (lastId == null) {
            builder.from(0);
            builder.trackTotalHits(true);
        } else {
            builder.searchAfter(new String[]{lastId});
        }
        SearchRequest request = new SearchRequest(ALIAS_NAME);
        request.source(builder);
        SearchResponse response;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticSearchIOException(e);
        }
        List<EntityIdDto> entityIdDtos = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            EntityIdDto entityIdDto = new EntityIdDto(hit.getIndex(), hit.getId());
            entityIdDtos.add(entityIdDto);
        }
        long total = response.getHits().getTotalHits().value;
        return PageBean.of(entityIdDtos, total, total == 0 ? 1 :
                (int) Math.ceil((double) total / (double) Constant.PAGE_SIZE));
    }

    private void recreateIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (exists) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
            client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        }
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index).alias(new Alias(ALIAS_NAME));
        client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }

    private void createIndexIfAbsent(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(index).alias(new Alias(ALIAS_NAME));
            client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }
    }

    private Map<String, Object> getParams(Map<String, Object> entity, List<ModelFieldDto> fields, String modelName,
                                          String layerName) {
        return this.getParams(null, entity, fields, modelName, layerName);
    }

    private Map<String, Object> getParams(Integer modelId, Map<String, Object> entity, String modelName,
                                          String layerName) {
        return this.getParams(modelId, entity, null, modelName, layerName);
    }

    private Map<String, Object> getParams(Integer modelId, Map<String, Object> entity, List<ModelFieldDto> fields,
                                          String modelName, String layerName) {
        Map<String, Object> params = new HashMap<>();
        if (CollectionUtils.isEmpty(fields)) {
            fields = modelFieldService.listByModelId(modelId);
        }
        fields.stream()
                .filter(field -> Boolean.TRUE.equals(field.getSearchKey()))
                .filter(field -> !"boolean".equals(field.getDataType()))
                .filter(field -> !"enum".equals(field.getDataType()))
                .forEach(field -> {
                    String key = field.getCode();
                    Object value = entity.get(field.getCode());
                    String dataType = field.getDataType();

                    if (value != null && !value.toString().isEmpty()) {
                        if ("datetime".equals(dataType) || "date".equals(dataType)) {
                            LocalDateTime time;
                            if (value instanceof Date) {
                                Date date = (Date) value;
                                time = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                                String format = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                params.put(key, format);
                            } else if (value instanceof LocalDateTime) {
                                time = (LocalDateTime) value;
                                String format = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                params.put(key, format);
                            } else {
                                params.put(key, value);
                            }
                        } else {
                            params.put(key, value);
                        }
                    }
                });
        params.put(FIELD_MODEL_NAME, modelName);
        if (!StringUtils.isEmpty(layerName)) {
            params.put(FIELD_LAYER_NAME, layerName);
        }
        return params;
    }
}
