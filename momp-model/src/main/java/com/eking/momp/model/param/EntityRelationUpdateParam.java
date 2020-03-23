package com.eking.momp.model.param;

import lombok.Data;

@Data
public class EntityRelationUpdateParam {

    private String entityId;

    private String targetEntityId;
}
