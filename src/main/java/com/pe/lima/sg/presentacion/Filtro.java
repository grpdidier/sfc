package com.pe.lima.sg.presentacion;

import java.util.ArrayList;
import java.util.List;

import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturador;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturadorNota;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleFormaPago;
import com.pe.lima.sg.entity.operacion.TblDetalleNota;
import com.pe.lima.sg.entity.operacion.TblFormaPago;
import com.pe.lima.sg.entity.operacion.TblLeyenda;
import com.pe.lima.sg.entity.operacion.TblLeyendaNota;
import com.pe.lima.sg.entity.operacion.TblNota;
import com.pe.lima.sg.entity.operacion.TblSunatCabecera;
import com.pe.lima.sg.entity.operacion.TblSunatCabeceraNota;
import com.pe.lima.sg.entity.operacion.TblSunatDetalle;
import com.pe.lima.sg.entity.operacion.TblSunatDetalleNota;
import com.pe.lima.sg.entity.operacion.TblTributoGeneral;
import com.pe.lima.sg.entity.operacion.TblTributoGeneralNota;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filtro {

	
	private String login			= "";
	private String estado   		= "";
	private String estadoUsuario	= "";
	
	private String tipo				= "-1";
	private String dato				= "";
	private String fechaInicio		= null;
	private String fechaFin			= null;
	private String paterno			= "";
	private String materno			= "";
	private String dni				= "";
	private String ruc				= "";
	private String razonSocial		= "";
	private Integer codigo			= null;
	private Integer codigoEdificacion = null; //Inmueble
	private String 	strTienda		= ""; //Local
	private String strOperacion		= "";
	private Integer anio			= null;
	private String mes				= null;
	
	private String serie			= "";
	private String tipoComprobante	= "";
	//Cliente
	private String tipoDocumento	= "";
	private String numero			= "";
	private String nombre			= "";
	//Comprobante
	private String fechaEmision;
	private String fechaVencimiento;
	private String horaEmision;
	private String codigoProducto	= null;
	private String codigoFiltro		= null;
	private TblComprobante	comprobante 						= null;
	private List<TblDetalleComprobante> listaDetalle			= null;
	private TblDetalleComprobante detalleComprobante			= null;
	private TblBandejaFacturador bandejaFacturador				= null;
	private TblSunatCabecera sunatCabecera						= null;
	private List<TblSunatDetalle> listaDetalleSunat				= null;
	private List<ParametroFacturadorBean> listaParametro		= null;
	private List<TblTributoGeneral> listaTributo				= null;
	private List<TblTributoGeneralNota> listaTributoNota		= null;
	private String appRutaContexto								= null;
	private TblLeyenda leyendaSunat								= null;
	//Impuestos
	private String nombreIGV									= null;
	private Integer valorIGV									= null;
	private String nombreServicio								= null;
	private Integer valorServicio								= null;
	//Adicionales para el Reporte
	private String nombreDomicilioFiscal						= null;
	//Adicionales para el repositorio sunat
	private String sunatData									= null;
	private String	sunatBD										= null;
	//Notas
	private TblNota nota										= null;
	private TblDetalleNota detalleNota							= null;
	private List<TblDetalleNota> listaDetalleNota				= null;
	private TblSunatCabeceraNota sunatCabeceraNota				= null;
	private List<TblSunatDetalleNota> listaDetalleSunatNota		= null;
	private TblBandejaFacturadorNota bandejaFacturadorNota		= null;
	private TblLeyendaNota leyendaNotaSunat						= null;
	private Integer indice										= null;
	
	//Campos temporales para no tener conflicto con el comprobante en la pantalla de notas
	private String tipoComprobanteNota							= null;
	private String serieNota									= null;
	private String numeroNota									= null;
	//Configuracion de la serie
	private String flagSerieAutomatica							= null;
	//Adicion para la forma de pago
	private TblFormaPago formaPago								= null;
	//Adicion del detalle de forma de pago
	private  List<TblDetalleFormaPago> listaDetalleFormaPago 	= new ArrayList<TblDetalleFormaPago>();
	private TblDetalleFormaPago detalleFormaPago 				= null;
	private String flagMostrarDetalleFormaPago					= null;
	private String nombreFileFormaPago							= null;
	private String nombreFileDetalleFormaPago					= null;
	public Filtro() {
		// TODO Auto-generated constructor stub
	}


}
