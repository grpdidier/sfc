package com.pe.lima.sg.bean.remision;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

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
	//Datos para el caso de consignacion
	private TblDetalleComprobante detalleComprobante;
	//Datos de las facturas asociada [presentacion en el html]
	private List<FacturaAsociadaBean> listaFacturaAsociada;
	//Lista de la facturas [contiene info para el registro]
	private List<ComprobanteBean> listaComprobante;
	//Lista de comprobantes para el xml
	private List<TblComprobante> listaTblComprobante;
	//Para mostrar el total del peso de la remision
	private BigDecimal totalPesoGuia;

	private List<TblDetalleRemision> listaDetalleRemision;
	private String ruc;
	
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
	
	//Datos para la configuración de las apis de la sunat
	//remision.keystore.jks:01
    private String keystore;
    //remision.private.key.alias:02
    private String privateKeyAlias;
	//remision.private.key.pass:03
    private String privateKeyPass;
	//remision.key.store.pass:04
    private String keyStorePass;
	//remision.key.store.type:05
    private String keyStoreType;
	//remision.api.token.sunat.url:06
	private String apiTokenSunatUrl;
	//remision.api.token.sunat.client.id:07
	private String apiTokenSunatClientId;
	//remision.api.token.sunat.client.secret:08
	private String apiTokenSunatClientSecret;
	//remision.api.token.sunat.username:09
	private String apiTokenSunatUsername;       	
	//remision.api.token.sunat.password:10
	private String apiTokenSunatPassword;
	//remision.api.ticket.sunat.url:11
	private String apiTicketSunatUrl;
	//remision.api.envio.sunat.url:12
	private String apiEnvioSunatUrl;
}
