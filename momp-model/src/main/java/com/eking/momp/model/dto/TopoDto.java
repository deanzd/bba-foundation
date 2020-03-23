package com.eking.momp.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TopoDto<M, R> implements Serializable {
    private static final long serialVersionUID = 895858877405228733L;
    private M models;
    private R relations;

    private TopoDto(M models, R relations) {
        this.models = models;
        this.relations = relations;
    }

    public static <M, R> TopoDto<M, R> of(M models, R relations) {
        return new TopoDto<>(models, relations);
    }
}
