package com.eking.momp.db.model;

import com.eking.momp.db.BaseOrderModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author Dean
 * @since 2019-06-12
 */
@Getter
@Setter
public class ModelField extends BaseOrderModel<ModelField> {

    private static final long serialVersionUID = 1L;

    private String code;

    private Integer modelId;

    private String name;

    private String description;

    private Boolean required;

    private String dataType;

    private String verifyRegex;

    private Boolean showInTable;

    private Boolean showInList;

    private Boolean searchKey;

}
