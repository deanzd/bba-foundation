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
public class Model extends BaseOrderModel<Model> {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;

	private String description;

	private String iconImage;

	private Integer layerId;

}
