package com.eking.momp.mongo;

import com.eking.momp.common.Constant;
import com.eking.momp.common.UserContextHoder;
import com.eking.momp.common.bean.PageBean;
import com.eking.momp.common.bean.PropBean;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractEntityService {
    @Autowired
    private MongoTemplate mongoTemplate;

    protected List<Map<String, Object>> listEntities(@NonNull String model, @NonNull Query query) {
        initQuery(query);
        List<Map> list = mongoTemplate.find(query, Map.class, model);
        List<Map<String, Object>> entities = uncheckedCast(list);
        transEntitiesId(entities);
        return entities;
    }

    protected List<Map<String, Object>> listAllEntities(@NonNull String model) {
        return this.listEntities(model, new Query());
    }

    protected List<Map<String, Object>> listEntities(@NonNull String model, @NonNull PropBean... props) {
        Criteria criteria = new Criteria();
        for (PropBean prop : props) {
            criteria.and(prop.getColumn()).is(prop.getValue());
        }
        Query query = Query.query(criteria);
        return this.listEntities(model, query);
    }

    protected PageBean<Map<String, Object>> page(String model, int pageIndex, int pageSize, String sortBy,
                                                 Boolean asc, Criteria criteria) {
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);
        Query query = Query.query(criteria).with(pageable);
        if (sortBy != null) {
            if (Boolean.TRUE.equals(asc)) {
                query.with(Sort.by(Sort.Direction.ASC, sortBy));
            } else {
                query.with(Sort.by(Sort.Direction.DESC, sortBy));
            }
        }
        List<Map<String, Object>> content = this.listEntities(model, query);
        Page<Map<String, Object>> page = PageableExecutionUtils.getPage(content, pageable,
                () -> mongoTemplate.count(query, model));
        return PageBean.of(page.getContent(), page.getTotalElements(), page.getTotalPages());
    }

    protected Map<String, Object> saveEntity(@NonNull String model, Map<String, Object> params) {
        Map<String, Object> notNullParams = new HashMap<>();
        params.forEach((key, value) -> {
            if (value != null && value.toString().length() > 0) {
                notNullParams.put(key, value);
            }
        });
        notNullParams.put(Constant.FIELD_CREATE_TIME, LocalDateTime.now());
		params.put(Constant.FIELD_CREATE_USER, UserContextHoder.getUsername());
        notNullParams.put(Constant.FIELD_UPDATE_TIME, LocalDateTime.now());
		params.put(Constant.FIELD_UPDATE_USER, UserContextHoder.getUsername());
        Map<String, Object> entity = mongoTemplate.insert(notNullParams, model);
        transEntityId(entity);
        return entity;
    }

    protected Map<String, Object> updateEntity(@NonNull String model, String id, Map<String, Object> params) {
        Query query = Query.query(Criteria.where(Constant.FIELD_ID_MONGO).is(id));
        Update update = new Update();
        update.set(Constant.FIELD_UPDATE_TIME, LocalDateTime.now());
		update.set(Constant.FIELD_UPDATE_USER, UserContextHoder.getUsername());
        params.forEach((key, value) -> {
            if (value == null || value.toString().length() == 0) {
                update.unset(key);
            } else {
                update.set(key, value);
            }
        });
        mongoTemplate.updateFirst(query, update, model);
        return this.getEntityById(model, id).orElse(null);
    }

    protected void deleteEntity(@NonNull String model, @NonNull String id) {
        this.getEntityById(model, id).ifPresent(stringObjectMap -> {
            Query query = Query.query(Criteria.where(Constant.FIELD_ID_MONGO).is(id));
            Update update = Update.update(Constant.FIELD_DELETED, LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, model);
        });
    }

    protected Optional<Map<String, Object>> getEntityById(@NonNull String model, @NonNull String id) {
        Map obj = mongoTemplate.findById(id, Map.class, model);
        if (obj == null || obj.get(Constant.FIELD_DELETED) != null) {
            return Optional.empty();
        }
        Map<String, Object> entity = uncheckedCast(obj);
        transEntityId(entity);
        return Optional.of(entity);
    }

    private void initQuery(@NonNull Query query) {
        query.addCriteria(Criteria.where(Constant.FIELD_DELETED).is(null));
    }

    @SuppressWarnings("unchecked")
    private <T> T uncheckedCast(Object obj) {
        return (T) obj;
    }

    private void transEntityId(Map<String, Object> entity) {
        entity.put("id", entity.get(Constant.FIELD_ID_MONGO).toString());
        entity.remove(Constant.FIELD_ID_MONGO);
    }

    private void transEntitiesId(List<Map<String, Object>> entities) {
        entities.forEach(this::transEntityId);
    }
}
