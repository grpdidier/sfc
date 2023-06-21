package com.pe.lima.sg.bean.remision;

import java.util.List;

import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComprobanteBean {
	//Datos de la factura
	private TblComprobante comprobante;
	//Datos del detalle del comprobante
	private List<TblDetalleComprobante> listaDetalle;
}
