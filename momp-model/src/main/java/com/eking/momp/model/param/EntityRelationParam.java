package com.eking.momp.model.param;

import lombok.Data;

@Data
public class EntityRelationParam {

    private Integer modelRelationId;

    private String entityModel;

    private String entityId;

    private String targetEntityModel;

    private String targetEntityId;
}
