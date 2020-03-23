package com.eking.momp.model.dto;

import com.eking.momp.db.BaseDto;
import com.eking.momp.db.model.ModelField;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelFieldDto extends BaseDto {

    private static final long serialVersionUID = -7649990449038540833L;
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

	private Integer showOrder;

	public ModelFieldDto(ModelField field) {
		super(field);
		this.code = field.getCode();
		this.modelId = field.getModelId();
		this.name = field.getName();
		this.description = field.getDescription();
		this.required = field.getRequired();
		this.dataType = field.getDataType();
		this.verifyRegex = field.getVerifyRegex();
		this.showInTable = field.getShowInTable();
		this.showInList = field.getShowInList();
		this.searchKey = field.getSearchKey();
		this.showOrder = field.getShowOrder();
	}
}
