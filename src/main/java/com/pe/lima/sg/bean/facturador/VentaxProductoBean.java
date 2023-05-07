package com.pe.lima.sg.bean.facturador;

import java.math.BigDecimal;

public class VentaxProductoBean implements java.io.Serializable{

	
	private static final long serialVersionUID = 1L;

	private String 	codigoProducto;
	private String 	descripcion;
	private BigDecimal cantidad;
	private BigDecimal subTotal;
	private BigDecimal total;
	
	
	
	public VentaxProductoBean() {
		super();
		
	}
	public VentaxProductoBean(String codigoProducto, String descripcion, BigDecimal cantidad, BigDecimal subTotal,
			BigDecimal total) {
		super();
		this.codigoProducto = codigoProducto;
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.subTotal = subTotal;
		this.total = total;
	}
	public String getCodigoProducto() {
		return codigoProducto;
	}
	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public BigDecimal getCantidad() {
		return cantidad;
	}
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
	public BigDecimal getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
}
