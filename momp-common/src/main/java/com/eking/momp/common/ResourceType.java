package com.eking.momp.common;

import lombok.Getter;

@Getter
public enum ResourceType {
    Model("model"),
    Entity("entity"),
    Layer("layer"),
    Uniqueness("uniqueness"),
    ModelField("modelField"),
    ModelRelation("modelRelation"),
    EntityRelation("entityRelation"),
    Dimension("dimension"),
    RelationType("relationType"),
    User("user"),
    Role("role"),
    Permission("permission");

    private final String label;

    ResourceType(String label) {
        this.label = label;
    }
}