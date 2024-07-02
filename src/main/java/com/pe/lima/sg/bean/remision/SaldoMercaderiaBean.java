package com.pe.lima.sg.bean.remision;

import java.math.BigDecimal;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaldoMercaderiaBean {
	private String fechaEmision;
	private String serie;
	private String numero;
	private String ruc;
	private String numeroRuc;
	private String cliente;
	private String codigoProducto;
	private String nombreProducto;
	private BigDecimal saldo;
	//Datos adicionales para Reporte de movimientos de guias de remision a consiganción
	private BigDecimal cantidad;
	private BigDecimal precio;
	private BigDecimal total;

}
