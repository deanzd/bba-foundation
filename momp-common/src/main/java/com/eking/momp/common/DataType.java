package com.eking.momp.common;

import com.eking.momp.common.exception.BusinessException;
import lombok.Getter;

@Getter
public enum DataType {
    VARCHAR("varchar"),
    INT("int"),
    BOOLEAN("boolean"),
    DOUBLE("double"),
    DATE("date"),
    DATETIME("datetime"),
    ENUM("enum"),
    TEXT("text");

    private final String text;

    DataType(String text) {
        this.text = text;
    }

    public static DataType of(String text) {
        for (DataType dataType : DataType.values()) {
            if (dataType.getText().equals(text)) {
                return dataType;
            }
        }
        throw new BusinessException("Data type not found:" + text);
    }

    @Override
    public String toString() {
        return text;
    }
}
