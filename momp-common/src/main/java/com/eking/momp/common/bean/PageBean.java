package com.eking.momp.common.bean;

import java.util.List;

import lombok.Data;

@Data
public class PageBean<T> {
	private List<T> rows;
	private long total;
	private long pages;

	private PageBean(List<T> rows, long total, long pages) {
		this.rows = rows;
		this.total = total;
		this.pages = pages;
	}

	public static <T> PageBean<T> of(List<T> rows, long total, long pages) {
		return new PageBean<>(rows, total, pages);
	}
}
