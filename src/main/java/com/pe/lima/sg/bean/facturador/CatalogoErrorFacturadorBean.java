package com.pe.lima.sg.bean.facturador;

public class CatalogoErrorFacturadorBean implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private String codigoError;
	private String nombreError;
	private String estado;
	
	
	public String getCodigoError() {
		return codigoError;
	}
	public void setCodigoError(String codigoError) {
		this.codigoError = codigoError;
	}
	public String getNombreError() {
		return nombreError;
	}
	public void setNombreError(String nombreError) {
		this.nombreError = nombreError;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	

}
