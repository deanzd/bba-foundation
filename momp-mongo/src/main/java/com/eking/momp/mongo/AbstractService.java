package com.eking.momp.mongo;

import com.eking.momp.common.Constant;
import com.eking.momp.common.UserContextHoder;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.service.LocaleService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public abstract class AbstractService<R extends MongoRepository<M, String>, M extends BaseModel> {

    @Autowired
    protected R repository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    protected LocaleService localeService;

    private Class<M> collectionClass;

    private String collectionName;

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void init() {
        collectionClass = (Class<M>) (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1]);
        collectionName = collectionClass.getAnnotation(Document.class).value();
    }

    protected M saveEntity(M entity) {
        entity.setCreateTime(LocalDateTime.now());
		entity.setCreateUser(UserContextHoder.getUsername());
        entity.setUpdateTime(LocalDateTime.now());
		entity.setUpdateUser(UserContextHoder.getUsername());
        return repository.save(entity);
    }

    protected M updateEntity(M entity) {
        entity.setUpdateTime(LocalDateTime.now());
		entity.setUpdateUser(UserContextHoder.getUsername());
        return repository.save(entity);
    }

    protected void deleteEntity(@NonNull String id) {
        this.getEntityById(id).ifPresent(entity -> {
            entity.setDeleted(LocalDateTime.now());
            repository.save(entity);
        });
    }

    protected Optional<M> getEntityById(@NonNull String id) {
        return repository.findById(id).map(m -> {
            if (m.getDeleted() != null) {
                return null;
            }
            return m;
        });
    }

    protected List<M> listObjs(PropBean... props) {
        Criteria criteria = new Criteria();
        for (PropBean prop : props) {
            criteria.and(prop.getColumn()).is(prop.getValue());
        }
        Query query = Query.query(criteria);
        return this.listObjs(query);
    }

    protected List<M> listObjs(@NonNull Query query) {
        initQuery(query);
        return mongoTemplate.find(query, collectionClass, collectionName);
    }

    protected Page<M> getPage(int pageIndex, int pageSize, String sortBy, Boolean asc, Criteria criteria) {
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);
        Query query = Query.query(criteria).with(pageable);
        if (sortBy != null) {
            if (Boolean.TRUE.equals(asc)) {
                query.with(Sort.by(Sort.Direction.ASC, sortBy));
            } else {
                query.with(Sort.by(Sort.Direction.DESC, sortBy));
            }
        }
        initQuery(query);
        List<M> content = mongoTemplate.find(query, collectionClass, collectionName);
        return PageableExecutionUtils.getPage(content, pageable, () -> mongoTemplate.count(query, collectionName));
    }

    private void initQuery(@NonNull Query query) {
        query.addCriteria(Criteria.where(Constant.FIELD_DELETED).is(null));
    }
}
