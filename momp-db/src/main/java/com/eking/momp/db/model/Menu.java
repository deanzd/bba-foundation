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
 * @since 2019-06-21
 */
@Getter
@Setter
public class Menu extends BaseModel<Menu> {

	private static final long serialVersionUID = 1L;

	private String name;

	private String path;

	private String icon;

	private Integer parentId;

	private Integer permissionId;

	private String dynamicChildrenCode;

}
