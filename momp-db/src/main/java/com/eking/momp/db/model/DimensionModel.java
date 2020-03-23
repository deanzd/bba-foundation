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
 * @since 2019-06-13
 */
@Getter
@Setter
public class DimensionModel extends BaseOrderModel<DimensionModel> {

	private static final long serialVersionUID = 1L;

	private Integer dimensionId;

	private Integer modelId;

}
