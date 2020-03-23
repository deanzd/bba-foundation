package com.eking.momp.menu.dto;

import java.util.List;

import com.eking.momp.db.model.Menu;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuDto {
	@JsonIgnore
	private Integer id;

	private String name;

	private String path;

	private String icon;

	private List<MenuDto> children;

	public MenuDto(Menu menu) {
		this.id = menu.getId();
		this.name = menu.getName();
		this.path = menu.getPath();
		this.icon = menu.getIcon();
	}

	public MenuDto(String name, String path, String icon) {
		super();
		this.name = name;
		this.path = path;
		this.icon = icon;
	}
}
