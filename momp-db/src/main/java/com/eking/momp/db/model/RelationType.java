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
 * @since 2019-06-11
 */
@Getter
@Setter
public class RelationType extends BaseModel<RelationType> {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;
	
	private String text;

	private String reverseText;

}
