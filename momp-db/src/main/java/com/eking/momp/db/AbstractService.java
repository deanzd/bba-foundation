package com.eking.momp.db;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eking.momp.common.Constant;
import com.eking.momp.common.UserContextHoder;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.AlreadyExistsException;
import com.eking.momp.common.exception.ForbiddenException;
import com.eking.momp.common.service.LocaleService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractService<M extends BaseMapper<T>, T extends BaseModel<T>> {

    @Autowired
    private M mapper;
    @Autowired
    protected LocaleService localeService;

    protected List<T> listObjs() {
        QueryWrapper<T> qw = new QueryWrapper<>();
        return this.listObjs(qw);
    }

    protected List<T> listObjs(QueryWrapper<T> qw) {
        initQw(qw);
        return mapper.selectList(qw);
    }

    protected List<T> listObjs(String sort, Boolean asc, @NonNull QueryWrapper<T> qw) {
        if (sort != null && sort.length() > 0) {
            if (Boolean.TRUE.equals(asc)) {
                qw.orderByAsc(sort);
            } else {
                qw.orderByDesc(sort);
            }
        }
        return this.listObjs(qw);
    }

    protected T saveObj(T entity) {
        entity.setCreateTime(LocalDateTime.now());
		entity.setCreateUser(UserContextHoder.getUsername());
        entity.setUpdateTime(LocalDateTime.now());
		entity.setUpdateUser(UserContextHoder.getUsername());
        mapper.insert(entity);
        return entity;
    }

    protected T updateObjById(T entity) {
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdateUser(UserContextHoder.getUsername());
        mapper.updateById(entity);
        return entity;
    }

    protected Optional<T> deleteObj(Serializable id) {
        T entity = mapper.selectById(id);
        if (entity == null || entity.getDeleted() != null) {
            return Optional.empty();
        }
        entity.setDeleted(LocalDateTime.now());
        mapper.updateById(entity);
        return Optional.of(entity);
    }

    protected Optional<T> getOneObj(QueryWrapper<T> qw) {
        List<T> entities = listObjs(qw);
        return entities.isEmpty() ? Optional.empty() : Optional.of(entities.get(0));
    }

    protected Optional<T> getObjById(Serializable id) {
        T entity = mapper.selectById(id);
        if (entity == null || entity.getDeleted() != null) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    /**
     * 根据属性键值对查询
     */
    protected List<T> listObjByProps(@NonNull PropBean... props) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        for (PropBean prop : props) {
            qw.eq(prop.getColumn(), prop.getValue());
        }
        return this.listObjs(qw);
    }

    protected List<T> listObjByProps(String sortBy, Boolean asc, @NonNull PropBean... props) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        for (PropBean prop : props) {
            qw.eq(prop.getColumn(), prop.getValue());
        }
        return this.listObjs(sortBy, asc, qw);
    }

    protected void checkObjExisted(String label, @NonNull PropBean... props) {
        this.checkObjExisted(null, label, props);
    }

    protected void checkObjExisted(Integer id, String label, @NonNull PropBean... props) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        initQw(qw);
        if (id != null) {
            qw.ne("id", id);
        }
        for (PropBean prop : props) {
            qw.eq(prop.getColumn(), prop.getValue());
        }
        this.getOneObj(qw).ifPresent(t -> {
            throw new AlreadyExistsException(localeService.getLabel(label));
        });
    }

    protected void checkRoleAcl(String... roleAcl) {
        List<String> needRolesList = Arrays.asList(roleAcl);
        boolean access = UserContextHoder.getRoles().stream().anyMatch(needRolesList::contains);
        if (!access) {
            throw new ForbiddenException();
        }
    }

    private void initQw(@NonNull QueryWrapper<T> qw) {
        qw.isNull(Constant.FIELD_DELETED);
    }

}
