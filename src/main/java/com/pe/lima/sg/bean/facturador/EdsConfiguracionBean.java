package com.pe.lima.sg.bean.facturador;

import java.math.BigDecimal;

public class EdsConfiguracionBean implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private String strTipoVenta;
	private String strTipoComprobante;
	private String strTipoProducto;
	private BigDecimal valorVenta;
	private BigDecimal precioUnitario;
	private String strRuc;
	private String strRazonSocial;
	
	
	
	public String getStrTipoVenta() {
		return strTipoVenta;
	}
	public void setStrTipoVenta(String strTipoVenta) {
		this.strTipoVenta = strTipoVenta;
	}
	public String getStrTipoComprobante() {
		return strTipoComprobante;
	}
	public void setStrTipoComprobante(String strTipoComprobante) {
		this.strTipoComprobante = strTipoComprobante;
	}
	public String getStrTipoProducto() {
		return strTipoProducto;
	}
	public void setStrTipoProducto(String strTipoProducto) {
		this.strTipoProducto = strTipoProducto;
	}
	public BigDecimal getValorVenta() {
		return valorVenta;
	}
	public void setValorVenta(BigDecimal valorVenta) {
		this.valorVenta = valorVenta;
	}
	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public String getStrRuc() {
		return strRuc;
	}
	public void setStrRuc(String strRuc) {
		this.strRuc = strRuc;
	}
	public String getStrRazonSocial() {
		return strRazonSocial;
	}
	public void setStrRazonSocial(String strRazonSocial) {
		this.strRazonSocial = strRazonSocial;
	}
	
	

}
