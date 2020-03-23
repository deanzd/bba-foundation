package com.eking.momp.db.model;

import com.eking.momp.db.BaseModel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ModelUniquenessItem extends BaseModel<ModelUniquenessItem> {

	private static final long serialVersionUID = 1L;

	private Integer modelUniquenessId;

	private Integer modelFieldId;

}
