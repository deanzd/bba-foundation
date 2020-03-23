package com.eking.momp.model.service;

import com.eking.momp.common.bean.PageBean;
import com.eking.momp.model.dto.EntityIdDto;

import java.io.IOException;
import java.util.List;

public interface EntitySearchService {

    PageBean<EntityIdDto> search(String keyword, String lastId);

    void update(String index, String id) throws IOException;

    void rebuildIndex(String modelCode);

    void delete(String index, String id) throws IOException;

    void updateIndex(String index) throws IOException;

}
