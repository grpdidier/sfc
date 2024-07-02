package com.pe.lima.sg.bean.remision;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoBean {
	private List<SaldoMercaderiaBean> listaDatos;
	private TotalesBean totales;
}
