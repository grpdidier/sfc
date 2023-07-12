package com.pe.lima.sg.bean.remision;

import java.io.Serializable;

public class RespuestaReporteBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private String descripcion;
	private Integer totalRegistro;
	private String tipoPago;
	
	
	public RespuestaReporteBean() {
		super();
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Integer getTotalRegistro() {
		return totalRegistro;
	}
	public void setTotalRegistro(Integer totalRegistro) {
		this.totalRegistro = totalRegistro;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}
	
	
}
