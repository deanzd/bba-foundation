package com.eking.momp.model.dto;

import com.eking.momp.db.BaseDto;
import com.eking.momp.db.model.ModelUniquenessItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelUniquenessItemDto extends BaseDto {

    private static final long serialVersionUID = 6103146618118373422L;
    private Integer modelUniquenessId;

	private Integer modelFieldId;
	
	//
	private String modelFieldName;
	
	private String modelFieldCode;

	public ModelUniquenessItemDto(ModelUniquenessItem model) {
		super(model);
		this.modelUniquenessId = model.getModelUniquenessId();
		this.modelFieldId = model.getModelFieldId();
	}

}
