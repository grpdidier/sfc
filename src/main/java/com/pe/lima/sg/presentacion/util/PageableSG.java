package com.pe.lima.sg.presentacion.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableSG implements Pageable {


	public PageableSG() {
		super();
	}

	private int limit = 5;
	private int offset = 0;
	private String operacion = "";
	private Sort sort;

	public PageableSG(int limit, int offset) {
		if (limit < 0)
			throw new IllegalArgumentException("Limit must not be less than zero!");

		if (offset < 0)
			throw new IllegalArgumentException("Offset must not be less than zero!");

		this.offset = offset;
		this.limit = limit;
	}

	
	@Override
	public int getPageNumber() {
		return 0;
	}

	@Override
	public int getPageSize() {
		return limit;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public Pageable next() {
		return new PageableSG(getOffset()* getPageSize(), getPageSize());
	}

	@Override
	public Pageable previousOrFirst() {
		return new PageableSG(getOffset()* getPageSize(), getPageSize());
	}

	@Override
	public Pageable first() {
		return new PageableSG(0, getPageSize());
	}

	@Override
	public boolean hasPrevious() {
		return false;
	}


	public int getLimit() {
		return limit;
	}


	public void setLimit(int limit) {
		this.limit = limit;
	}


	public void setOffset(int offset) {
		this.offset = offset;
	}


	public String getOperacion() {
		return operacion;
	}


	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}


	public void setSort(Sort sort) {
		this.sort = sort;
	}



}
