package com.eking.momp.common.exception;

import com.eking.momp.common.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -8114239930317645452L;

    private ResourceType resourceType;

    private Serializable dataId;

}
