package com.eking.momp.common.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropBean {
	
	private String column;
	
	private Object value;
	
	private String text;
	
	public static PropBean of(String column, Object value) {
		PropBean property = new PropBean();
		property.setColumn(column);
		property.setValue(value);
		return property;
	}

	public static PropBean of(String column, Object value, String text) {
		PropBean property = new PropBean();
		property.setColumn(column);
		property.setValue(value);
		property.setText(text);
		return property;
	}
}
	
