package com.pe.lima.sg.bean.remision;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalesBean {
	private String nombreProducto;
	private BigDecimal totalCantidad = new BigDecimal("0");
	private BigDecimal total = new BigDecimal("0");
}
