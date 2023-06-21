package com.pe.lima.sg.bean.remision;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaAsociadaBean {
	private String serieFactura;
	private String numeroFactura;
	private String rucCliente;
	private String nombreCliente;
	private String descripcion;
	private String unidadMedida;
	private BigDecimal cantidad; 
	private int codigoComprobante;
	private int codigoDetalleComprobante;
	
}
