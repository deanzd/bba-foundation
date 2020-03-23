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
 * @since 2019-06-21
 */
@Getter
@Setter
public class Layer extends BaseOrderModel<Layer> {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;

	private String description;

	private String iconImage;

}
