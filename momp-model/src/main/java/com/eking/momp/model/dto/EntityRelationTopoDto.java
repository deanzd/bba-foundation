package com.eking.momp.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class EntityRelationTopoDto implements Serializable {

    private static final long serialVersionUID = 4712097272613641474L;
    private String id;

    private String entityId;//处理后的ID

    private String targetEntityId;//处理后的ID

    private ModelRelationDetailDto modelRelation;

}
