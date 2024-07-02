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
	private BigDecimal  peso;
	//para validar al cliente
	private String numeroCliente;
	//para buscar los datos del producto
	private String codigoProducto;
	private BigDecimal cantidadFacturada;
	//para la generacion de factura con guia de remision
	private String serieRemision;
	private String numeroRemision;
	private Integer codigoDetalleRemision;
	private Integer codigoRemision;
	
}
