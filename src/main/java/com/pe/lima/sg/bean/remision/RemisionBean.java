package com.pe.lima.sg.bean.remision;

import java.math.BigDecimal;
import java.util.List;

import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleRemision;
import com.pe.lima.sg.entity.operacion.TblFacturaAsociada;
import com.pe.lima.sg.entity.operacion.TblRemision;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemisionBean {
	//Información de la remision
	private TblRemision remision;
	//Datos de la factura [datos intermedios]
	private TblComprobante comprobante;
	//Datos del detalle de la factura  [datos intermedios]
	private List<TblDetalleComprobante> listaDetalle;
	//Datos de las facturas asociada [presentacion en el html]
	private List<FacturaAsociadaBean> listaFacturaAsociada;
	//Lista de la facturas [contiene info para el registro]
	private List<ComprobanteBean> listaComprobante;
	//Lista de comprobantes para el xml
	private List<TblComprobante> listaTblComprobante;
	//Para mostrar el total del peso de la remision
	private BigDecimal totalPesoGuia;
	
	//private TblDetalleRemision detalleRemision;
	private List<TblDetalleRemision> listaDetalleRemision;
	private String ruc;
	
	
	//private TblDetalleComprobante detalleComprobante;
	
	//Datos para la guia xml
	private String razonSocial;
	private String descripcionMotivo;
	private BigDecimal pesoBruto;
	private String direccionPartida;
	private String ubigeoPartida;
	//Datos retornados de la guia xml
	private String nombreArchivoXML;
	//Datos para el PDF
	private List<ParametroFacturadorBean> listaParametro;
	private List<TblFacturaAsociada> listaTblFacturaAsociada;
	//Datos para mensaje de los servicios
	private String mensajeRpta;
	//para eliminar
	private Integer indiceElemento;
	//Datos del transporte
	private String datosTransporte;
}
