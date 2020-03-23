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
 * @since 2019-06-13
 */
@Getter
@Setter
public class DimensionRelation extends BaseModel<DimensionRelation> {

	private static final long serialVersionUID = 1L;

	private Integer dimensionId;

	private Integer modelRelationId;

}
