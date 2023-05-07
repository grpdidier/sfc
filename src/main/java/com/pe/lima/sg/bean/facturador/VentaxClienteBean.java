package com.pe.lima.sg.bean.facturador;

import java.math.BigDecimal;

public class VentaxClienteBean implements java.io.Serializable{

	
	private static final long serialVersionUID = 1L;
	
	private String fechaEmision;
	private String tipoComprobante;
	private String nombreTipoComprobante; 	//Nuevo
	private String serie;
	private String numero;
	private String numeroComprobante;		//Serie + Numero
	private String numeroDocumento;
	private String nombreCliente;
	private BigDecimal totalOpGravada			= new BigDecimal("0");
	private BigDecimal totalIgv					= new BigDecimal("0");
	private BigDecimal totalImporte				= new BigDecimal("0");
	
	
	
	
	public VentaxClienteBean() {
		super();
	}


	public VentaxClienteBean(String fechaEmision, String tipoComprobante, String nombreTipoComprobante, String serie,
			String numero, String numeroComprobante, String numeroDocumento, String nombreCliente,
			BigDecimal totalOpGravada, BigDecimal totalIgv, BigDecimal totalImporte) {
		super();
		this.fechaEmision = fechaEmision;
		this.tipoComprobante = tipoComprobante;
		this.nombreTipoComprobante = nombreTipoComprobante;
		this.serie = serie;
		this.numero = numero;
		this.numeroComprobante = numeroComprobante;
		this.numeroDocumento = numeroDocumento;
		this.nombreCliente = nombreCliente;
		this.totalOpGravada = totalOpGravada;
		this.totalIgv = totalIgv;
		this.totalImporte = totalImporte;
	}
	
	
	public String getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(String fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public String getTipoComprobante() {
		return tipoComprobante;
	}
	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}
	public String getNombreTipoComprobante() {
		return nombreTipoComprobante;
	}
	public void setNombreTipoComprobante(String nombreTipoComprobante) {
		this.nombreTipoComprobante = nombreTipoComprobante;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getNumeroComprobante() {
		return numeroComprobante;
	}
	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	public String getNombreCliente() {
		return nombreCliente;
	}
	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
	public BigDecimal getTotalOpGravada() {
		return totalOpGravada;
	}
	public void setTotalOpGravada(BigDecimal totalOpGravada) {
		this.totalOpGravada = totalOpGravada;
	}
	public BigDecimal getTotalIgv() {
		return totalIgv;
	}
	public void setTotalIgv(BigDecimal totalIgv) {
		this.totalIgv = totalIgv;
	}
	public BigDecimal getTotalImporte() {
		return totalImporte;
	}
	public void setTotalImporte(BigDecimal totalImporte) {
		this.totalImporte = totalImporte;
	}
	
}
