package com.eking.momp.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseOrderModel<T extends BaseOrderModel<?>> extends BaseModel<T> {

	private static final long serialVersionUID = 1L;

	private Integer showOrder;

}
