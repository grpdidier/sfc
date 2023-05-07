package com.pe.lima.sg.bean.facturador;

public class BandejaFacturadorBean implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private String 	numeroRuc;
	private	String 	tipoDocumento;
	private String 	numeroDocumento;
	private	String 	fechaCarga;
	private String	fechaGeneracion;
	private String	fechaEnvio;
	private String	Observacion;
	private String	nombreArchivo;
	private	String	situacion;
	private String 	tipoArchivo;
	private String	firmaDigital;
	
	
	public String getNumeroRuc() {
		return numeroRuc;
	}
	public void setNumeroRuc(String numeroRuc) {
		this.numeroRuc = numeroRuc;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	public String getFechaCarga() {
		return fechaCarga;
	}
	public void setFechaCarga(String fechaCarga) {
		this.fechaCarga = fechaCarga;
	}
	public String getFechaGeneracion() {
		return fechaGeneracion;
	}
	public void setFechaGeneracion(String fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}
	public String getFechaEnvio() {
		return fechaEnvio;
	}
	public void setFechaEnvio(String fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
	public String getObservacion() {
		return Observacion;
	}
	public void setObservacion(String observacion) {
		Observacion = observacion;
	}
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	public String getSituacion() {
		return situacion;
	}
	public void setSituacion(String situacion) {
		this.situacion = situacion;
	}
	public String getFirmaDigital() {
		return firmaDigital;
	}
	public void setFirmaDigital(String firmaDigital) {
		this.firmaDigital = firmaDigital;
	}
	public String getTipoArchivo() {
		return tipoArchivo;
	}
	public void setTipoArchivo(String tipoArchivo) {
		this.tipoArchivo = tipoArchivo;
	}
	
	
	
	
}
