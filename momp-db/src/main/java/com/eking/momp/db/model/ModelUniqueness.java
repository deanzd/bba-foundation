package com.eking.momp.db.model;

import com.eking.momp.db.BaseModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Dean
 * @since 2019-07-11
 */
@Getter
@Setter
public class ModelUniqueness extends BaseModel<ModelUniqueness> {

	private static final long serialVersionUID = 1L;

	private Integer modelId;
	
	private Boolean required;

}
