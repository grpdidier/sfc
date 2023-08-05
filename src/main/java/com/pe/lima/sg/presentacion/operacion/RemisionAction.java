package com.pe.lima.sg.presentacion.operacion;

import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conEstadoCliente;
import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conNumeroDocumento;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conCodigoProducto;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conEstadoProducto;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conNombreProducto;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conCodigoEmpresa;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conEstado;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conNumero;
import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conCodigoEmpresaRemision;
import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conEstadoRemision;
import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conNumeroRemision;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.bean.remision.ComprobanteBean;
import com.pe.lima.sg.bean.remision.FacturaAsociadaBean;
import com.pe.lima.sg.bean.remision.RemisionBean;
import com.pe.lima.sg.dao.mantenimiento.IClienteDAO;
import com.pe.lima.sg.dao.mantenimiento.IProductoDAO;
import com.pe.lima.sg.dao.mantenimiento.ISerieDAO;
import com.pe.lima.sg.dao.mantenimiento.ITransporteDAO;
import com.pe.lima.sg.dao.operacion.IComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleRemisionDAO;
import com.pe.lima.sg.dao.operacion.IFacturaAsociadaDAO;
import com.pe.lima.sg.dao.operacion.IRemisionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblCliente;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.mantenimiento.TblSerie;
import com.pe.lima.sg.entity.mantenimiento.TblTransporte;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleRemision;
import com.pe.lima.sg.entity.operacion.TblFacturaAsociada;
import com.pe.lima.sg.entity.operacion.TblRemision;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.pdf.GuiaRemisionPdf;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.PageWrapper;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;
import com.pe.lima.sg.util.remision.util.GuiaRemisionService;
import com.pe.lima.sg.util.remision.util.UtilArchivoRespuesta;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class RemisionAction {
	@Autowired
	private ISerieDAO serieDao;
	@Autowired
	private IComprobanteDAO comprobanteDao;
	@Autowired
	private IDetalleComprobanteDAO detalleComprobanteDao;
	@Autowired
	private IRemisionDAO remisionDao;
	@Autowired
	private IDetalleRemisionDAO detalleRemisionDao;
	@Autowired
	private IFacturaAsociadaDAO facturaAsociadaDao;
	@Autowired
	private ITransporteDAO transporteDao;
	@Autowired
	private GuiaRemisionService guiaRemisionService;
	@Autowired
	private IClienteDAO clienteDao;
	@Autowired
	private IProductoDAO productoDao;

	
	private String urlPaginado = "/operacion/remision/paginado/"; 

	private String urlPaginadoRemision = "/operacion/remision/principal/paginado/"; 

	@RequestMapping(value = "/operacion/remision", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		try{
			log.debug("[traerRegistros] Inicio");
			path = "operacion/remision/rem_listado";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setNumero("");
			//this.listarComprobante(model, filtro, pageable, this.urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroRemisionCriterio", filtro);
			log.debug("[traerRegistros] Fin");
		}catch(Exception e){
			log.debug("[traerRegistros] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}
		return path;
	}

	@RequestMapping(value = "/operacion/remision/q", method = RequestMethod.POST)
	public String traerRegistrosFiltradosRemision(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/remision/rem_listado";
		try{
			log.debug("[traerRegistrosFiltradosRemision] Inicio");
			this.listarRemision(model, filtro, pageable, urlPaginadoRemision, request);
			request.getSession().setAttribute("sessionFiltroRemisionCriterio", filtro);
			model.addAttribute("filtro", filtro);
			log.debug("[traerRegistrosFiltradosRemision] Fin");
		}catch(Exception e){
			log.debug("[traerRegistrosFiltradosRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		log.debug("[traerRegistrosFiltradosRemision] Fin");
		return path;
	}
	/*** Listado de Remisiones ***/
	private void listarRemision(Model model, Filtro filtro,  PageableSG pageable, String url, HttpServletRequest request){
		List<TblRemision> entidades = new ArrayList<TblRemision>();
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "numero"));
		Integer codigoEntidad	= null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;

			Specification<TblRemision> criterio = Specifications.where(conNumeroRemision(filtro.getNumero()))
					.and(conCodigoEmpresaRemision(codigoEntidad))
					.and(conEstadoRemision(Constantes.ESTADO_REGISTRO_ACTIVO));
			pageable.setSort(sort);

			Page<TblRemision> entidadPage = remisionDao.findAll(criterio, pageable);
			PageWrapper<TblRemision> page = new PageWrapper<TblRemision>(entidadPage, url, pageable);
			model.addAttribute("registros", page.getContent());
			request.getSession().setAttribute("ListadoConsultaRemision",page.getContent());
			model.addAttribute("page", page);
			request.getSession().setAttribute("PageRemisionPrincipal",page);
			log.debug("[listarRemision] entidades:"+entidades);
			request.getSession().setAttribute("SessionPrincipalPageabeSG", pageable);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}

	@RequestMapping(value = "operacion/remision/nuevo", method = RequestMethod.GET)
	public String crearRemision(Model model, HttpServletRequest request) {
		RemisionBean entidad = null;
		try{
			log.debug("[crearRemision] Inicio");
			entidad = new RemisionBean();
			entidad.setRemision(new TblRemision());
			entidad.getRemision().setTipoTransporte(Constantes.TIPO_TRANSPORTE_PUBLICO);
			this.obtenerSerieRemision(entidad, request);
			this.inicializaDatosRemision(entidad);
			entidad.setRuc(Constantes.SUNAT_RUC_EMISOR);
			model.addAttribute("entidad", entidad);
			request.getSession().setAttribute("guiaRemisionSession", entidad);
			log.debug("[crearComprobante] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 				= null;
		}
		return "operacion/remision/rem_nuevo";
	}
	/*********************************************************************************************************************************/
	/*Sección de Asociacion de Facturas
	/*********************************************************************************************************************************/
	@RequestMapping(value = "/operacion/remision/facturas", method = RequestMethod.POST)
	public String traerRegistrosFactura(Model model, String path,  PageableSG pageable, HttpServletRequest request, RemisionBean entidad) {
		Filtro filtro = null;
		RemisionBean entidadOriginal = null;
		try{
			log.debug("[traerRegistrosFactura] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			recuperarNombreProducto(entidad,entidadOriginal);
			entidad.setListaFacturaAsociada(entidadOriginal.getListaFacturaAsociada());
			entidad.setListaComprobante(entidadOriginal.getListaComprobante());
			//entidad.setTotalPesoGuia(entidadOriginal.getTotalPesoGuia());
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			path = "operacion/remision/rem_listado_factura";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setNumero("");;
			//this.listarComprobante(model, filtro, pageable, this.urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroCriterio", filtro);
			log.debug("[traerRegistrosFactura] Fin");
		}catch(Exception e){
			log.debug("[traerRegistrosFactura] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}
	//Recupera el valor obtenido
	private void recuperarNombreProducto(RemisionBean entidad, RemisionBean entidadOriginal) {
		if (entidad.getListaFacturaAsociada()!=null && !entidad.getListaFacturaAsociada().isEmpty()) {
			for(int indice=0; indice < entidad.getListaFacturaAsociada().size(); indice++) {
				FacturaAsociadaBean factura = entidad.getListaFacturaAsociada().get(indice);
				entidadOriginal.getListaFacturaAsociada().get(indice).setDescripcion(factura.getDescripcion());				
			}
		}
		
	}

	@RequestMapping(value = "/operacion/remision/factura/q", method = RequestMethod.POST)
	public String traerRegistrosFiltradosFactura(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/remision/rem_listado_factura";
		try{
			log.debug("[traerRegistrosFiltradosFactura] Inicio");
			this.listarComprobante(model, filtro, pageable, urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroCriterio", filtro);
			model.addAttribute("filtro", filtro);
			log.debug("[traerRegistrosFiltradosFactura] Fin");
		}catch(Exception e){
			log.debug("[traerRegistrosFiltradosFactura] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		log.debug("[traerRegistrosFiltradosFactura] Fin");
		return path;
	}
	/*** Listado de Comprobante ***/
	private void listarComprobante(Model model, Filtro filtro,  PageableSG pageable, String url, HttpServletRequest request){
		List<TblComprobante> entidades = new ArrayList<TblComprobante>();
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "numero"));
		Integer codigoEntidad	= null;
		try{
			//this.actualizarEstadoComprobanteSfs12(filtro, request);
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;

			Specification<TblComprobante> criterio = Specifications.where(conNumero(filtro.getNumero()))
					.and(conCodigoEmpresa(codigoEntidad))
					.and(com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conNombreCliente(filtro.getNombre()))
					.and(conEstado(Constantes.ESTADO_REGISTRO_ACTIVO));
			pageable.setSort(sort);
			//entidades = comprobanteDao.findAll(criterio);
			Page<TblComprobante> entidadPage = comprobanteDao.findAll(criterio, pageable);
			PageWrapper<TblComprobante> page = new PageWrapper<TblComprobante>(entidadPage, url, pageable);
			model.addAttribute("registros", page.getContent());
			request.getSession().setAttribute("ListadoConsultaFactura",page.getContent());
			model.addAttribute("page", page);
			request.getSession().setAttribute("pageConsultaFactura",page);
			log.debug("[listarComprobante] entidades:"+entidades);

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
	@SuppressWarnings("unchecked")
	private Map<String, TblProducto> obtenerMapProductoSession(HttpServletRequest request){
		return (Map<String, TblProducto>)request.getSession().getAttribute("SessionMapProductoSistema");
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/seleccion/{id}", method = RequestMethod.GET)
	public String seleccionaraRegistroFactura(@PathVariable Integer id, HttpServletRequest request, Model model, PageableSG pageable) {
		TblComprobante comprobante					= null;
		String path 								= null;
		List<TblComprobante> lista 					= null;
		List<TblDetalleComprobante> listaDetalle 	= null;
		RemisionBean entidad 						= null;
		Map<String, TblProducto> mapProducto		= obtenerMapProductoSession(request);
		try{
			log.debug("[seleccionaraRegistroFactura] Inicio");
			path = "operacion/remision/rem_previo_nuevo";
			entidad = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			lista = (List<TblComprobante>)request.getSession().getAttribute("ListadoConsultaFactura");
			comprobante = lista.get(id);
			if (entidad.getListaComprobante()== null || entidad.getListaComprobante().isEmpty()) {
				entidad.setComprobante(comprobante);
				entidad.getRemision().setDomicilioLlegada(entidad.getComprobante().getDireccionCliente());
				log.debug("[seleccionaraRegistroFactura] --> Direccion Llegada:"+entidad.getRemision().getDomicilioLlegada());
				entidad.getRemision().setUbigeoLlegada(obtenerUbigeoLlegada(entidad.getComprobante().getNumeroDocumento(),entidad.getComprobante().getTblEmpresa().getCodigoEntidad()));
			}else {
				entidad.setComprobante(comprobante);
			}
			
			if(!mismoClienteSeleccionado(entidad)) {
				model.addAttribute("respuesta", "La factura Seleccionada corresponde a otro cliente, no se podrá asociar ["+entidad.getListaFacturaAsociada().get(0).getNombreCliente()+"]");
			}
			//entidad.getRemision().setNumeroDocumentoTransportista(entidad.getComprobante().getNumeroDocumento());
			//entidad.getRemision().setNombreTransportista(entidad.getComprobante().getNombreCliente());
			listaDetalle = detalleComprobanteDao.listarxComprobante(comprobante.getCodigoComprobante());
			//Actualizar cantidades
			actualizarCantidadComprobante(listaDetalle, mapProducto);
			entidad.setListaDetalle(listaDetalle);
			model.addAttribute("entidad", entidad);
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			log.debug("[seleccionaraRegistroConsulta] Fin:"+entidad.getRemision().getDomicilioLlegada());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;			
		}
		return path;
	}
	private String obtenerUbigeoLlegada(String numeroDocumento, int codigoEntidad) {
		List<TblCliente> listaCliente = clienteDao.listarClientexDocumentoxEmpresa(numeroDocumento, codigoEntidad);
		String ubigeo = "";
		if (listaCliente!=null && !listaCliente.isEmpty()) {
			log.debug("[obtenerUbigeoLlegada] Tamanio Lista Cliente:"+listaCliente.size());
			TblCliente cliente = listaCliente.get(0);
			ubigeo = cliente.getDistrito();
		}
		return ubigeo;
	}

	private void actualizarCantidadComprobante(List<TblDetalleComprobante> listaDetalle, Map<String, TblProducto> mapProducto) {
		for(TblDetalleComprobante detalle: listaDetalle) {
			detalle.setCantidad(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP));
			obtenerUnidadMedidaYPeso(detalle, mapProducto);
			BigDecimal cantidadGuia = detalle.getCantidadGuia();
			if (cantidadGuia !=null) {
				detalle.setCantidad(detalle.getCantidad().subtract(cantidadGuia));
				detalle.setCantidad(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP));
			}
		}

	}
	/*asigna unidad de medida del producto para ser visualidado en la pantalla*/
	private void obtenerUnidadMedidaYPeso(TblDetalleComprobante detalle, Map<String, TblProducto> mapProducto) {
		String unidadMedida = null;
		TblProducto producto = null;
		producto = mapProducto.get(detalle.getCodigoProducto());
		if (producto != null) {
			unidadMedida = producto.getUnidadMedida().toUpperCase();
			detalle.setUnidadMedida(unidadMedida);
			detalle.setValorReferencia(producto.getPeso());
		}
	}
	@RequestMapping(value = "/operacion/remision/nuevo/asociar", method = RequestMethod.POST)
	public String asociarFactura(Model model, RemisionBean entidad,  String path, HttpServletRequest request,  PageableSG pageable) {
		path = "operacion/remision/rem_nuevo";
		RemisionBean entidadOriginal 			= null;
		FacturaAsociadaBean facturaAsociada		= new FacturaAsociadaBean();
		TblComprobante comprobante				= null;
		//Map<String, TblProducto> mapProducto	= obtenerMapProductoSession(request);
		BigDecimal totalPesoGuia				= new BigDecimal("0");
		try{
			log.debug("[asociarFactura] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			if (mismoClienteSeleccionado(entidadOriginal)) {
				//Validamos la misma factura
				if (distintaFacturaSeleccionada(entidadOriginal)) {
					//Obtenemos las cantidades de la pantalla
					for(int indice=0; indice < entidad.getListaDetalle().size(); indice++) {
						entidadOriginal.getListaDetalle().get(indice).setCantidad(entidad.getListaDetalle().get(indice).getCantidad());
						if (entidadOriginal.getListaDetalle().get(indice).getCantidad()!= null && entidadOriginal.getListaDetalle().get(indice).getValorReferencia() !=null) {
							entidadOriginal.getListaDetalle().get(indice).setValorReferencia(entidad.getListaDetalle().get(indice).getCantidad().multiply(entidadOriginal.getListaDetalle().get(indice).getValorReferencia()));
						}else {
							entidadOriginal.getListaDetalle().get(indice).setValorReferencia(new BigDecimal("0"));
						}
						
					}
					//Quitamos los productos que son cero
					quitarDetalleComprobanteConValorCero(entidadOriginal);
					//obtenerDetalleRemision(entidadOriginal, request);
					if (entidadOriginal.getListaFacturaAsociada()==null) {
						log.debug("[asociarFactura] Inicializamos la primera vez");
						entidadOriginal.setListaFacturaAsociada(new ArrayList<>());
					}else {
						log.debug("[asociarFactura] Tamaño de la lista: "+entidadOriginal.getListaFacturaAsociada().size());
						for(FacturaAsociadaBean factura:entidadOriginal.getListaFacturaAsociada()) {
							totalPesoGuia = totalPesoGuia.add(factura.getPeso());
						}
						log.debug("[asociarFactura] totalPesoGuia: "+totalPesoGuia);
					}
					comprobante = entidadOriginal.getComprobante();
					if (!entidadOriginal.getListaDetalle().isEmpty()) {
						for(TblDetalleComprobante detalleComprobante: entidadOriginal.getListaDetalle()) {
							if (detalleComprobante.getCantidad().compareTo(new BigDecimal("0"))>0) {
								facturaAsociada = new FacturaAsociadaBean();
								facturaAsociada.setSerieFactura(comprobante.getSerie());
								facturaAsociada.setNumeroFactura(comprobante.getNumero());
								facturaAsociada.setCodigoComprobante(comprobante.getCodigoComprobante());
								facturaAsociada.setCodigoDetalleComprobante(detalleComprobante.getCodigoDetalle());
								facturaAsociada.setNombreCliente(comprobante.getNombreCliente());
								facturaAsociada.setDescripcion(detalleComprobante.getDescripcion());
								facturaAsociada.setUnidadMedida(detalleComprobante.getUnidadMedida());
								facturaAsociada.setCantidad(detalleComprobante.getCantidad());
								//facturaAsociada.setPeso(calcularPesoProducto(detalleComprobante,mapProducto));
								facturaAsociada.setPeso(detalleComprobante.getValorReferencia());
								entidadOriginal.getListaFacturaAsociada().add(facturaAsociada);
								totalPesoGuia = totalPesoGuia.add(facturaAsociada.getPeso());
								facturaAsociada.setNumeroCliente(comprobante.getNumeroDocumento());
							}
						}
						log.debug("[asociarFactura] totalPesoGuia: "+totalPesoGuia);
						log.debug("[asociarFactura] Luego...Tamaño de la lista: "+entidadOriginal.getListaFacturaAsociada().size());
					}
					entidadOriginal.setTotalPesoGuia(totalPesoGuia);
					//Almacenamos la información de la factura y su detalle
					if (entidadOriginal.getListaComprobante() == null) {
						entidadOriginal.setListaComprobante(new ArrayList<>());
						log.debug("[asociarFactura] Inicializamos la primera vez a ComprobanteBean");
					}else {
						log.debug("[asociarFactura] Tamaño de la lista de ComprobanteBean:"+entidadOriginal.getListaComprobante().size());
					}
					ComprobanteBean comprobanteParaRegistro = new ComprobanteBean();
					comprobanteParaRegistro.setComprobante(entidadOriginal.getComprobante());
					comprobanteParaRegistro.setListaDetalle(entidadOriginal.getListaDetalle());
					entidadOriginal.getListaComprobante().add(comprobanteParaRegistro);
				}else {
					log.error("[asociarFactura] No se puede asociar porque la misma factura");
					model.addAttribute("respuesta", "No se puede asociar la misma factura");
					path = "operacion/remision/rem_previo_nuevo";
				}
			}else {
				log.error("[asociarFactura] No se puede asociar porque son clientes diferentes");
				model.addAttribute("respuesta", "No se puede asociar porque son clientes diferentes");
				path = "operacion/remision/rem_previo_nuevo";
			}
			model.addAttribute("entidad", entidadOriginal);
			request.getSession().setAttribute("guiaRemisionSession", entidadOriginal);
			
			log.debug("[asociarFactura] entidadOriginal:"+entidadOriginal.getRemision().getDomicilioLlegada());
			log.debug("[asociarFactura] Luego...Tamaño de la lista de ComprobanteBean:"+entidadOriginal.getListaComprobante().size());
			log.debug("[asociarFactura] Fin");
		}catch(Exception e){
			log.debug("[asociarFactura] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
			path = "operacion/remision/rem_previo_nuevo";
		}
		log.debug("[asociarFactura] Fin");
		return path;
	}
	//Elimina de la lista los productos cuyo valor sea CERO
	private void quitarDetalleComprobanteConValorCero(RemisionBean entidadOriginal) {
		List<TblDetalleComprobante> listaDetalleComprobante = new ArrayList<>();
		if (entidadOriginal.getListaDetalle()!= null && !entidadOriginal.getListaDetalle().isEmpty()) {
			for(TblDetalleComprobante tblDetalleComprobante: entidadOriginal.getListaDetalle()) {
				if (tblDetalleComprobante.getCantidad().compareTo(new BigDecimal("0"))>0) {
					listaDetalleComprobante.add(tblDetalleComprobante);
				}
			}
		}
		entidadOriginal.setListaDetalle(listaDetalleComprobante);
	}

	private boolean distintaFacturaSeleccionada(RemisionBean entidadOriginal) {
		boolean resultado = true;
		if (entidadOriginal!= null && entidadOriginal.getListaFacturaAsociada()!=null && !entidadOriginal.getListaFacturaAsociada().isEmpty()) {
			for(FacturaAsociadaBean facturaAsociada:entidadOriginal.getListaFacturaAsociada()) {
				if (entidadOriginal.getComprobante().getNumero().equals(facturaAsociada.getNumeroFactura())) {
					return false;
				}
			}
			
		}else {
			return true;
		}
		return resultado;
	}
	private boolean mismoClienteSeleccionado(RemisionBean entidadOriginal) {
		if (entidadOriginal!= null && entidadOriginal.getListaFacturaAsociada()!=null && !entidadOriginal.getListaFacturaAsociada().isEmpty()) {
			FacturaAsociadaBean facturaAsociada		= entidadOriginal.getListaFacturaAsociada().get(0);
			if (entidadOriginal.getComprobante().getNumeroDocumento().equals(facturaAsociada.getNumeroCliente())) {
				return true;
			}else {
				return false;
			}
			
		}else {
			return true;
		}
	}

	/*private BigDecimal calcularPesoProducto(TblDetalleComprobante detalleComprobante, Map<String, TblProducto> mapProducto) {
		//TblProducto producto = null;
		//producto = mapProducto.get(detalleComprobante.getCodigoProducto());
		//if (producto !=null) {
			//log.error("[calcularPesoProducto] Error en el producto:"+detalleComprobante.getCodigoProducto()+ " Detalle Comprobante:"+detalleComprobante.getCodigoDetalle());
			return detalleComprobante.getCantidad().multiply(detalleComprobante.getValorReferencia()).setScale(2, RoundingMode.HALF_UP);
		//}
		
		//return new BigDecimal("0");
	}*/
	/*********************************************************************************************************************************/
	/*Sección de Eliminar un detalle de la guia y eliminar la guia
	/*********************************************************************************************************************************/
	@RequestMapping(value = "/operacion/remision/detalle/eliminar", method = RequestMethod.POST)
	public String eliminarDetalleProducto(Model model, RemisionBean entidad, HttpServletRequest request) {
		RemisionBean entidadOriginal		= null;
		String path							= null;
		try{
			log.debug("[eliminarDetalleProducto] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			
			if (entidadOriginal.getListaFacturaAsociada()!=null && entidadOriginal.getListaFacturaAsociada().size()>entidad.getIndiceElemento()){
				removerProductoDeListadeComprobante(entidadOriginal, entidadOriginal.getListaFacturaAsociada().get(entidad.getIndiceElemento().intValue()));
				entidadOriginal.getListaFacturaAsociada().remove(entidad.getIndiceElemento().intValue());
			}
			
			entidad.setListaFacturaAsociada(entidadOriginal.getListaFacturaAsociada());
			entidad.setListaComprobante(entidadOriginal.getListaComprobante());
			entidad.setTotalPesoGuia(entidadOriginal.getTotalPesoGuia());
			
			model.addAttribute("entidad", entidad);
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			path = "operacion/remision/rem_nuevo";
			log.debug("[eliminarDetalleProducto] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}

	//Removemos el elemento de la lista que se tiene para realizar la grabación de los datos
	private void removerProductoDeListadeComprobante(RemisionBean entidadOriginal,FacturaAsociadaBean facturaAsociadaBean) {
		String numeroFactura = facturaAsociadaBean.getNumeroFactura();
		String nombreProducto = facturaAsociadaBean.getDescripcion();
		int indexComprobante = -1;
		log.info("[removerProductoDeListadeComprobante] Inicio:"+numeroFactura+":"+nombreProducto);
		for(ComprobanteBean comprobante:entidadOriginal.getListaComprobante()) {
			indexComprobante++;
			String numeroFacturaParaRegistro = comprobante.getComprobante().getNumero();
			log.info("[removerProductoDeListadeComprobante] numeroFacturaParaRegistro:"+numeroFacturaParaRegistro);
			if (numeroFactura.equals(numeroFacturaParaRegistro)){
				int indexDetalle = -1;
				for(TblDetalleComprobante detalle: comprobante.getListaDetalle()) {
					indexDetalle++;
					log.info("[removerProductoDeListadeComprobante] detalle.getDescripcion():"+detalle.getDescripcion());
					if (detalle.getDescripcion().equals(nombreProducto)) {
						comprobante.getListaDetalle().remove(indexDetalle);
						log.info("[removerProductoDeListadeComprobante] Eliminado:"+indexDetalle+" Tamaño Lista Detalle:"+comprobante.getListaDetalle().size());
						break;
					}
				}
				if (comprobante.getListaDetalle().size()==0) {
					log.info("[removerProductoDeListadeComprobante] Tamaño de Lista Comprobante:"+entidadOriginal.getListaComprobante().size());
					entidadOriginal.getListaComprobante().remove(indexComprobante);
					break;
				}
			}
		}
		log.info("[removerProductoDeListadeComprobante] Fin:getListaComprobante().size:"+entidadOriginal.getListaComprobante().size());
	}

	@RequestMapping(value = "operacion/remision/all/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarRemision(@PathVariable Integer id, Model model, HttpServletRequest request,  PageableSG pageable) {
		String path = "operacion/remision/rem_listado";
		try{
			log.debug("[eliminarRemision] Inicio");
			//Eliminacion de la remision
			TblRemision remision = remisionDao.findOne(id);
			remision.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			remision.setAuditoriaModificacion(request);
			remision = remisionDao.save(remision);
			log.debug("[eliminarRemision] Eliminado Remision:"+remision.getCodigoRemision());
			if (!remision.getMotivoTraslado().equals("05") && !remision.getMotivoTraslado().equals("13")) {
				//Reversion de las cantidades
				List<TblDetalleRemision> listaDetalleRemision = detalleRemisionDao.findAllxIdRemision(id);
				for(TblDetalleRemision detalle: listaDetalleRemision) {
					TblDetalleComprobante detComprobante = detalleComprobanteDao.findOne(detalle.getCodigoDetalleComprobante());
					detComprobante.setCantidadGuia(detComprobante.getCantidadGuia().subtract(detalle.getCantidad()));
					detComprobante.setAuditoriaModificacion(request);
					detComprobante = detalleComprobanteDao.save(detComprobante);
					log.debug("[eliminarRemision] Revertido cantidad del detalle de comprobante:"+detComprobante.getCodigoDetalle());
				}
			}
			decrementarNumeroSerie(request);
			Filtro filtro = (Filtro)request.getSession().getAttribute("sessionFiltroRemisionCriterio");
			this.listarRemision(model, filtro, pageable, urlPaginadoRemision, request);
			model.addAttribute("filtro", filtro);

			log.debug("[eliminarRemision] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}
	/*@RequestMapping(value = "/operacion/remision/detalle/editar/{id}", method = RequestMethod.GET)
	public String editarDetalleProducto(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 				= null;
		String path							= null;
		try{
			log.debug("[editarDetalleProducto] Inicio");
			entidad = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			if (entidad.getListaDetalle()!=null && entidad.getListaDetalle().size()>0){
				entidad.setDetalleComprobante(entidad.getListaDetalle().get(id.intValue()));
				entidad.getDetalleComprobante().setCantidad(entidad.getDetalleComprobante().getCantidad().setScale(2, RoundingMode.HALF_UP));
				log.debug("[editarDetalleProducto] Ok");
			}else {
				entidad.setDetalleComprobante(new TblDetalleComprobante());
				log.debug("[editarDetalleProducto] No se encontro");
			}
			model.addAttribute("entidad", entidad);
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			request.getSession().setAttribute("indiceEdicionProductoRemision",id);
			path = "operacion/remision/rem_edicion_producto";
			log.debug("[editarDetalleProducto] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}*/
	/*@RequestMapping(value = "/operacion/remision/detalle/actualizar", method = RequestMethod.POST)
	public String actualizarDetalleProducto(Model model, RemisionBean entidad,  String path, HttpServletRequest request) {
		path = "operacion/remision/rem_nuevo";
		RemisionBean entidadOriginal = null;
		Integer indice = null;
		try{
			log.debug("[actualizarDetalleProducto] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			indice = (Integer)request.getSession().getAttribute("indiceEdicionProductoRemision");
			entidadOriginal.getListaDetalle().get(indice.intValue()).setCantidad(entidad.getDetalleComprobante().getCantidad());
			model.addAttribute("entidad", entidadOriginal);
			request.getSession().setAttribute("guiaRemisionSession",entidadOriginal);
			log.debug("[actualizarDetalleProducto] Fin");
		}catch(Exception e){
			log.debug("[actualizarDetalleProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		log.debug("[actualizarDetalleProducto] Fin");
		return path;
	}*/
	/*********************************************************************************************************************************/
	/*Sección de Grabacion de la guia de remision
	/*********************************************************************************************************************************/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/nuevo/guardar", method = RequestMethod.POST)
	public String guardarRemision(Model model, RemisionBean entidad,  String path, HttpServletRequest request,  PageableSG pageable) {
		path = "operacion/remision/rem_listado";
		RemisionBean entidadOriginal = null;
		try{
			log.debug("[guardarRemision] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			List<TblProducto> listaProductoSistema = (List<TblProducto>)request.getSession().getAttribute("SessionListaProductoxEntidad");
			actualizarFaltantes(entidadOriginal,entidad,request);
			//obtener serie
			obtenerSerieRemision(entidadOriginal, request);
			if (okDatosRemision(entidadOriginal, model)) {
				entidadOriginal.getRemision().setAuditoriaCreacion(request);
				
				TblRemision remision = remisionDao.save(entidadOriginal.getRemision());
				log.debug("[guardarRemision] remision registrado:"+remision.getCodigoRemision());
				log.debug("[guardarRemision] Lista ComprobanteBean tamaño:"+entidadOriginal.getListaComprobante().size());
				grabarFacturaAsociadaDetallexComprobante(entidadOriginal, remision,listaProductoSistema,request, entidad);

				//Actualizar serie
				incrementarNumeroSerie(request);
				Filtro filtro = new Filtro();
				filtro.setNumero(remision.getNumero());
				request.getSession().setAttribute("sessionFiltroRemisionCriterio", filtro);
				model.addAttribute("filtro", filtro);
				listarRemision(model, filtro, pageable, urlPaginadoRemision, request);
				model.addAttribute("respuesta", "Se grabó exitosamente");
			}else {
				path = "operacion/remision/rem_nuevo";
				model.addAttribute("entidad", entidadOriginal);
			}
			log.debug("[guardarRemision] Fin");
		}catch(Exception e){
			log.debug("[guardarRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
			path = "operacion/remision/rem_nuevo";
		}
		log.debug("[guardarRemision] Fin");
		return path;
	}
	/*Graba la factura asociada y el detalle de la remision*/
	private void grabarFacturaAsociadaDetallexComprobante(RemisionBean entidadOriginal,TblRemision remision, List<TblProducto> listaProductoSistema, HttpServletRequest request,RemisionBean entidadFormulario) {
		Integer indiceFacturaAsociada = 0;
		for(ComprobanteBean comprobanteBean: entidadOriginal.getListaComprobante()) {
			TblFacturaAsociada facturaAsociada = new TblFacturaAsociada();
			facturaAsociada.setTblRemision(remision);
			List<TblDetalleRemision> listaDetalleRemision = obtenerDetalleRemision(comprobanteBean.getListaDetalle(), request, indiceFacturaAsociada,entidadFormulario);
			indiceFacturaAsociada = indiceFacturaAsociada + listaDetalleRemision.size();
			//BigDecimal totalPesoFactura = calcularPesoProductoxFactura(listaDetalleRemision,listaProductoSistema);
			BigDecimal totalPesoFactura = calcularPesoProductoxFactura(listaDetalleRemision);
			//BigDecimal totalPesoFactura = entidadOriginal.getTotalPesoGuia();
			facturaAsociada.setPesoTotal(totalPesoFactura); 
			facturaAsociada.setCodigoComprobante(comprobanteBean.getComprobante().getCodigoComprobante());
			facturaAsociada.setObservacion(comprobanteBean.getComprobante().getSerie()+"-"+comprobanteBean.getComprobante().getNumero());
			TblFacturaAsociada tblFacturaAsociada = facturaAsociadaDao.save(facturaAsociada);
			log.debug("[grabarFacturaAsociadaDetallexComprobante] tblFacturaAsociada registrada:"+tblFacturaAsociada.getCodigoFacturaAsociada());
			
			for(TblDetalleRemision detRemision : listaDetalleRemision) {
				detRemision.setTblFacturaAsociada(tblFacturaAsociada);
				TblDetalleRemision tblDetalleRemision = detalleRemisionDao.save(detRemision);
				log.debug("[grabarFacturaAsociadaDetallexComprobante] detalle remision registrado:"+tblDetalleRemision.getCodigoDetalleRemision());
			}
		}

		//actualizar en el detalle de comprobante las cantidades de la guia: cuando no es consignacion y otros
		if (!entidadOriginal.getRemision().getMotivoTraslado().equals("05") && !entidadOriginal.getRemision().getMotivoTraslado().equals("13")){
			for(ComprobanteBean comprobanteBean: entidadOriginal.getListaComprobante()) {
				for(TblDetalleComprobante detComprobante: comprobanteBean.getListaDetalle()) {
					TblDetalleComprobante detalleComprobanteActualizar = detalleComprobanteDao.findOne(detComprobante.getCodigoDetalle());
					BigDecimal cantidadOriginal = detalleComprobanteActualizar.getCantidadGuia()==null? new BigDecimal("0"):detalleComprobanteActualizar.getCantidadGuia();
					detalleComprobanteActualizar.setCantidadGuia(cantidadOriginal.add(detComprobante.getCantidad()));
					detalleComprobanteActualizar.setAuditoriaModificacion(request);
					TblDetalleComprobante detNuevo = detalleComprobanteDao.save(detalleComprobanteActualizar);
					log.debug("[guardarRemision] detalle comprobante actualizado:"+detNuevo.getCodigoDetalle());
				}
			}
		}
	}
	private void actualizarFaltantes(RemisionBean entidadOriginal, RemisionBean entidad, HttpServletRequest request) {
		TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
		entidadOriginal.getRemision().setCodigoEntidad(usuario.getTblEmpresa().getCodigoEntidad());
		entidadOriginal.getRemision().setNombreConductor(entidad.getRemision().getNombreConductor());
		entidadOriginal.getRemision().setApellidoConductor(entidad.getRemision().getApellidoConductor());
		entidadOriginal.getRemision().setNumeroDNIConductor(entidad.getRemision().getNumeroDNIConductor());
		entidadOriginal.getRemision().setUbigeoLlegada(entidad.getRemision().getUbigeoLlegada());
		entidadOriginal.getRemision().setMotivoTraslado(entidad.getRemision().getMotivoTraslado());
		entidadOriginal.getRemision().setFechaEmision(entidad.getRemision().getFechaEmision());
		entidadOriginal.getRemision().setFechaInicioTraslado(entidad.getRemision().getFechaInicioTraslado());
		entidadOriginal.getRemision().setHoraInicioTraslado(entidad.getRemision().getHoraInicioTraslado());
		entidadOriginal.getRemision().setCodigoDomicilioPartida(entidad.getRemision().getCodigoDomicilioPartida());
		entidadOriginal.getRemision().setDomicilioLlegada(entidad.getRemision().getDomicilioLlegada());
		entidadOriginal.getRemision().setNumeroDocumentoTransportista(entidad.getRemision().getNumeroDocumentoTransportista());
		entidadOriginal.getRemision().setNombreTransportista(entidad.getRemision().getNombreTransportista());
		entidadOriginal.getRemision().setNumeroCertInscripcion(entidad.getRemision().getNumeroCertInscripcion());
		entidadOriginal.getRemision().setNumeroLicencia(entidad.getRemision().getNumeroLicencia());
		entidadOriginal.getRemision().setMarca(entidad.getRemision().getMarca());
		entidadOriginal.getRemision().setPlaca(entidad.getRemision().getPlaca());
		entidadOriginal.getRemision().setRemolque(entidad.getRemision().getRemolque());
		entidadOriginal.getRemision().setObservacion(entidad.getRemision().getObservacion());
		entidadOriginal.getRemision().setEstadoOperacion("00");
		entidadOriginal.getRemision().setNumeroRegistroMtc(entidad.getRemision().getNumeroRegistroMtc());
		entidadOriginal.setTotalPesoGuia(entidad.getTotalPesoGuia());
		entidadOriginal.getRemision().setPesoTotal(entidad.getTotalPesoGuia());
		if (entidadOriginal.getListaComprobante()!= null && !entidadOriginal.getListaComprobante().isEmpty()) {
			ComprobanteBean comprobanteBean = entidadOriginal.getListaComprobante().get(0);
			entidadOriginal.getRemision().setTipoDocumentoCliente(comprobanteBean.getComprobante().getTipoDocumento());
			entidadOriginal.getRemision().setNumeroDocumentoCliente(comprobanteBean.getComprobante().getNumeroDocumento());
			entidadOriginal.getRemision().setNombreCliente(comprobanteBean.getComprobante().getNombreCliente());
		}
	}
	private BigDecimal calcularPesoProductoxFactura(List<TblDetalleRemision> listaDetalleRemision) {
		BigDecimal totalPeso = new BigDecimal("0");
		for(TblDetalleRemision tblDetalleRemision:listaDetalleRemision) {
			totalPeso = totalPeso.add(tblDetalleRemision.getPeso());
		}
		return totalPeso;
	}
	/*********************************************************************************************************************************/
	/*Sección de ver la guia
	/*********************************************************************************************************************************/
	@RequestMapping(value = "/operacion/remision/ver/{id}", method = RequestMethod.GET)
	public String verRemision(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 							= null;
		String path										= null;
		TblRemision remision 							= null;
		List<TblFacturaAsociada> listaFacturaAsociada 	= null;
		//List<ComprobanteBean> listaComprobante			= null;
		//ComprobanteBean comprobanteBean					= null;
		List<FacturaAsociadaBean> listaFacAsoVer		= null;
		FacturaAsociadaBean facturaBean					= null;
		List<TblDetalleRemision> listaDetRemision		= null;
		//List<TblDetalleComprobante> listaDetComprobante	= null;
		//Map<Integer,TblDetalleComprobante> mapDetComprobante = new HashMap<>();
		BigDecimal total 								= new BigDecimal("0");
		try{
			log.debug("[verRemision] Inicio");
			//Leer los comprobantes - detalle y armar la presentación
			entidad = new RemisionBean();
			path = "operacion/remision/rem_ver";
			remision = remisionDao.findOne(id);
			listaFacturaAsociada = facturaAsociadaDao.findAllxIdRemision(remision.getCodigoRemision());
			if (listaFacturaAsociada!=null) {
				listaFacAsoVer = new ArrayList<>();
				for(TblFacturaAsociada factura: listaFacturaAsociada) {
					listaDetRemision = detalleRemisionDao.findAllxIdFacturaAsociada(factura.getCodigoFacturaAsociada());
					for(TblDetalleRemision detRemision: listaDetRemision) {
						facturaBean = new FacturaAsociadaBean();
						String[] serieNumero = obtenerDatosSerieNumero(factura.getObservacion(),factura.getCodigoComprobante());
						facturaBean.setSerieFactura(serieNumero[0]);
						facturaBean.setNumeroFactura(serieNumero[1]);
						facturaBean.setCodigoComprobante(factura.getCodigoComprobante());
						facturaBean.setNombreCliente(remision.getNombreCliente());
						facturaBean.setDescripcion(detRemision.getDescripcion());
						facturaBean.setUnidadMedida(detRemision.getUnidadMedida());
						facturaBean.setCantidad(detRemision.getCantidad());
						facturaBean.setPeso(detRemision.getPeso()==null?new BigDecimal("0"):detRemision.getPeso());
						listaFacAsoVer.add(facturaBean);
						
					}
					total = total.add(factura.getPesoTotal());
				}
			}
			entidad.setRemision(remision);
			entidad.setListaFacturaAsociada(listaFacAsoVer);
			if (remision.getPesoTotal()!=null) {
				entidad.setTotalPesoGuia(remision.getPesoTotal());
			}else {
				entidad.setTotalPesoGuia(total);
			}
			
			model.addAttribute("entidad", entidad);
			
			log.debug("[verRemision] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}
	private String[] obtenerDatosSerieNumero(String observacion, Integer codigoComprobante) {
		String[] serieNumero = new String[2];
		if (codigoComprobante > 0) {
			serieNumero = observacion.split("-");
		}else {
			serieNumero[0] = "";
			serieNumero[1] = "";
		}
		return serieNumero;
	}
	/*********************************************************************************************************************************/
	/*Sección de Tipo de Transporte
	/*********************************************************************************************************************************/
	@RequestMapping(value = "/operacion/remision/tipotransporte/actualizar", method = RequestMethod.POST)
	public String actualizarTipoTransporte(Model model, RemisionBean entidad, HttpServletRequest request) {
		RemisionBean entidadOriginal		= null;
		String path							= null;
		try{
			log.debug("[actualizarTipoTransporte] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			entidad.setListaFacturaAsociada(entidadOriginal.getListaFacturaAsociada());
			entidad.setListaComprobante(entidadOriginal.getListaComprobante());
			//entidad.setComprobante(entidadOriginal.getComprobante());
			entidad.setTotalPesoGuia(entidadOriginal.getTotalPesoGuia());
			model.addAttribute("entidad", entidad);
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			path = "operacion/remision/rem_nuevo";
			log.debug("[actualizarTipoTransporte] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}
	/*********************************************************************************************************************************/
	/*Sección de invocacion a las APIS
	/*********************************************************************************************************************************/
	@SuppressWarnings("unchecked")
	private void obtenerConfiguracionApi(RemisionBean entidadOriginal, HttpServletRequest request) {
		Map<String, String> mapConfiguracionGre = (Map<String, String>)request.getSession().getAttribute("SessionMapConfiguracionGuiaRemision");	
		
		entidadOriginal.setKeystore(mapConfiguracionGre.get(Constantes.CONF_01_REMISION_KEYSTORE_JKS));
	    entidadOriginal.setPrivateKeyAlias(mapConfiguracionGre.get(Constantes.CONF_02_REMISION_PRIVATE_KEY_ALIAS));
	    entidadOriginal.setPrivateKeyPass(mapConfiguracionGre.get(Constantes.CONF_03_REMISION_PRIVATE_KEY_PASS));
	    entidadOriginal.setKeyStorePass(mapConfiguracionGre.get(Constantes.CONF_04_REMISION_KEY_STORE_PASS));
	    entidadOriginal.setKeyStoreType(mapConfiguracionGre.get(Constantes.CONF_05_REMISION_KEY_STORE_TYPE));
		entidadOriginal.setApiTokenSunatUrl(mapConfiguracionGre.get(Constantes.CONF_06_REMISION_API_TOKEN_SUNAT_URL));
		entidadOriginal.setApiTokenSunatClientId(mapConfiguracionGre.get(Constantes.CONF_07_REMISION_API_TOKEN_SUNAT_CLIENT_ID));
		entidadOriginal.setApiTokenSunatClientSecret(mapConfiguracionGre.get(Constantes.CONF_08_REMISION_API_TOKEN_SUNAT_CLIENT_SECRET));
		entidadOriginal.setApiTokenSunatUsername(mapConfiguracionGre.get(Constantes.CONF_09_REMISION_API_TOKEN_SUNAT_USERNAME));       	
		entidadOriginal.setApiTokenSunatPassword(mapConfiguracionGre.get(Constantes.CONF_10_REMISION_API_TOKEN_SUNAT_PASSWORD));
		entidadOriginal.setApiTicketSunatUrl(mapConfiguracionGre.get(Constantes.CONF_11_REMISION_API_TICKET_SUNAT_URL));
		entidadOriginal.setApiEnvioSunatUrl(mapConfiguracionGre.get(Constantes.CONF_12_REMISION_API_ENVIO_SUNAT_URL));
		
	}

	/**private BigDecimal calcularPesoProductoxFactura(List<TblDetalleRemision> listaDetalleRemision,	List<TblProducto> listaProductoSistema) {
		BigDecimal totalPeso = new BigDecimal("0");
		Map<String, TblProducto> mapProducto = new HashMap<>();
		for(TblProducto producto: listaProductoSistema) {
			mapProducto.put(producto.getCodigoEmpresa(), producto);
		}
		for(TblDetalleRemision detalle: listaDetalleRemision) {
			//TblProducto producto = mapProducto.get(detalle.getCodigoProducto());
			//if (producto!=null) {
				//log.debug("[calcularPesoProductoxFactura] Producto:"+producto.getNombre()+" Peso: "+producto.getPeso()+" Cantidad:"+detalle.getCantidad());
				//totalPeso = totalPeso.add(producto.getPeso().multiply(detalle.getCantidad()));
				log.debug("[calcularPesoProductoxFactura] totalPeso:"+totalPeso);
			//}else {
			//	log.error("[calcularPesoProductoxFactura] Producto no encontrado:"+detalle.getCodigoProducto());
			//}
		}
		return totalPeso;
	}*/

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/xml/{id}", method = RequestMethod.GET)
	public String obtenerXMLGuia(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 				= null;
		String path							= null;
		try{
			log.debug("[obtenerXMLGuia] Inicio");
			path = "operacion/remision/rem_listado";
			String rutaXml = obtenerRutaDirectorioXML(request);
			entidad = new RemisionBean();
			TblRemision remision = remisionDao.findOne(id);
			List<TblFacturaAsociada> listaFacturaAsociada = facturaAsociadaDao.findAllxIdRemision(remision.getCodigoRemision());
			entidad.setListaTblComprobante(new ArrayList<>());
			for(TblFacturaAsociada asociada: listaFacturaAsociada) {
				TblComprobante comprobante = comprobanteDao.findOne(asociada.getCodigoComprobante());
				entidad.getListaTblComprobante().add(comprobante);
			}
			//TblComprobante comprobante = comprobanteDao.findOne(1);//TODO:ERROR(remision.getCodigoComprobante());
			List<TblDetalleRemision> listaDetalleRemision = detalleRemisionDao.findAllxIdRemision(remision.getCodigoRemision());
			entidad.setRemision(remision);
			//entidad.setComprobante(comprobante);
			entidad.setListaDetalleRemision(listaDetalleRemision);
			//Datos adicionales para la guia XML
			TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
			entidad.setRuc(usuario.getTblEmpresa().getRuc());
			entidad.setRazonSocial(usuario.getTblEmpresa().getRazonSocial());
			Map<String, String> mapTipoMotivoTraslado = (Map<String, String>)request.getSession().getAttribute("SessionMapMotivoTrasladoDescripcion");
			String descripcionMotivo = mapTipoMotivoTraslado.get(entidad.getRemision().getMarca());
			entidad.setDescripcionMotivo(descripcionMotivo);
			Map<String, String> mapDomicilioPartidaDatos = (Map<String, String>)request.getSession().getAttribute("SessionMapDomicilioPartidaDatos");
			String direccionPartida = mapDomicilioPartidaDatos.get(entidad.getRemision().getCodigoDomicilioPartida());
			String[] ubigeo = entidad.getRemision().getCodigoDomicilioPartida().split(":");
			entidad.setUbigeoPartida(ubigeo[1]);
			log.debug("[obtenerXMLGuia] direccion Partida Ubigeo:"+ubigeo[1]);
			log.debug("[obtenerXMLGuia] direccionFiscal:"+direccionPartida);
			entidad.setDireccionPartida(direccionPartida);
			//entidad.setPesoBruto(obtenerPesoBruto(listaFacturaAsociada));
			entidad.setPesoBruto(entidad.getRemision().getPesoTotal());
			//Llamada a la generación XML
			obtenerConfiguracionApi(entidad,request);
			guiaRemisionService.generarGuiaRemisionXML(entidad,rutaXml);
			//Guardamos el nombre
			remision.setEstadoOperacion(Constantes.ESTADO_XML_GENERADO);
			remision.setRutaXML(entidad.getNombreArchivoXML());
			remisionDao.save(remision);
			//Refrescando la pantalla
			Integer page = obtenerPage(request);
			Integer size = obtenerSize(request);
			String operacion = obtenerOperacion(request);
			PageableSG pageable = (PageableSG)request.getSession().getAttribute("SessionPrincipalPageabeSG");
			log.debug("[obtenerXMLGuia] page:"+page+" size:"+size+" operacion:"+operacion+" pageable:"+pageable);
			this.paginarPrincipalEntidad(page, size, operacion, model, pageable, request);
			log.debug("[obtenerXMLGuia] Fin");
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("respuesta", "Error:"+e.getMessage());
		}
		return path;
	}


	@SuppressWarnings("unchecked")
	private String obtenerRutaDirectorioXML(HttpServletRequest request) {
		Map<String, TblParametro> mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
		TblParametro TblParametro =  mapParametro.get(Constantes.RUTA_XML_GUIA_REMISION);
		String rutaXml = TblParametro.getDato();
		return rutaXml;
	}

	private BigDecimal obtenerPesoBruto(List<TblFacturaAsociada> listaFacturaAsociada) {
		BigDecimal totalPeso = new BigDecimal("0");
		for(TblFacturaAsociada factura: listaFacturaAsociada) {
			totalPeso = totalPeso.add(factura.getPesoTotal());
		}
		return totalPeso;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/operacion/remision/descargar/xml/{id}", method = RequestMethod.GET)
	public ResponseEntity descargarXMLGuia(@PathVariable Integer id, Model model, HttpServletRequest request)  {
		byte[] content = null;
		HttpHeaders headers = null;
		try {
			List<TblRemision> lista = (List<TblRemision>)request.getSession().getAttribute("ListadoConsultaRemision");
			String filePath = ((TblRemision)lista.get(id)).getRutaXML();
			File file = new File(filePath);
			headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData(file.getName(), file.getName());
			headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

			content = Files.readAllBytes(new File(filePath).toPath());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity(content, headers, HttpStatus.OK);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/operacion/remision/descargar/cdr/{id}", method = RequestMethod.GET)
	public ResponseEntity descargarCDRGuia(@PathVariable Integer id, Model model, HttpServletRequest request)  {
		byte[] content = null;
		HttpHeaders headers = null;
		try {
			List<TblRemision> lista = (List<TblRemision>)request.getSession().getAttribute("ListadoConsultaRemision");
			String filePath = ((TblRemision)lista.get(id)).getRutaCDR();
			File file = new File(filePath);
			headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData(file.getName(), file.getName());
			headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

			content = Files.readAllBytes(new File(filePath).toPath());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity(content, headers, HttpStatus.OK);
	}
	/*
	 * Muestra el comprobante como solo lectura
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/pdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> pdfComprobante(@PathVariable Integer id, HttpServletRequest request) {
		RemisionBean entidad 						= null;
		HttpHeaders headers 						= new HttpHeaders();
		ByteArrayInputStream bis 					= null;
		GuiaRemisionPdf guiaPdf						= new GuiaRemisionPdf();

		try{
			log.info("[pdfComprobante] Inicio");
			entidad = new RemisionBean();
			TblRemision remision = remisionDao.findOne(id);
			//TblComprobante comprobante = comprobanteDao.findOne(1);//TODO:ERROR(remision.getCodigoComprobante());
			List<TblFacturaAsociada> listaFacturaAsociada = facturaAsociadaDao.findAllxIdRemision(id);
			List<TblDetalleRemision> listaDetalleRemision = detalleRemisionDao.findAllxIdRemision(id);
			entidad.setRemision(remision);
			//entidad.setComprobante(comprobante);
			entidad.setListaTblFacturaAsociada(listaFacturaAsociada);
			entidad.setListaDetalleRemision(listaDetalleRemision);
			//Datos adicionales para la guia XML
			TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
			entidad.setRuc(usuario.getTblEmpresa().getRuc());
			entidad.setRazonSocial(usuario.getTblEmpresa().getRazonSocial());
			Map<String, String> mapTipoMotivoTraslado = (Map<String, String>)request.getSession().getAttribute("SessionMapMotivoTrasladoDescripcion");
			String descripcionMotivo = mapTipoMotivoTraslado.get(entidad.getRemision().getMotivoTraslado());
			log.info("[pdfComprobante] descripcionMotivo:"+descripcionMotivo +" codigo:"+entidad.getRemision().getMotivoTraslado());
			entidad.setDescripcionMotivo(descripcionMotivo);
			Map<String, String> mapDomicilioPartidaDatos = (Map<String, String>)request.getSession().getAttribute("SessionMapDomicilioPartidaDatos");
			String direccionPartida = mapDomicilioPartidaDatos.get(entidad.getRemision().getCodigoDomicilioPartida());
			entidad.setDireccionPartida(direccionPartida);
			entidad.setPesoBruto(obtenerPesoBruto(listaFacturaAsociada));
			entidad.setListaParametro((List<ParametroFacturadorBean>)request.getSession().getAttribute("SessionListParametro"));
			bis = guiaPdf.comprobanteReporte(entidad);
			headers.add("Content-Disposition", "attachment; filename="+remision.getSerie()+"-"+remision.getNumero()+".pdf");
			log.info("[pdfComprobante] Fin");

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad				= null;
		}
		return ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/envio/xml/{id}", method = RequestMethod.GET)
	public String envioXMLGuia(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 				= null;
		String path							= null;
		try{
			log.debug("[envioXMLGuia] Inicio");
			Map<String, TblParametro> mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			TblParametro TblParametro =  mapParametro.get(Constantes.RUTA_XML_GUIA_REMISION);
			String rutaXml = TblParametro.getDato();
			path = "operacion/remision/rem_listado";
			entidad = new RemisionBean();
			TblRemision remision = remisionDao.findOne(id);
			obtenerConfiguracionApi(entidad, request);
			String accessToken = obtenerTokenGuiaRemision(remision,entidad);
			String ticket = obtenerTicketGuia(accessToken, remision,entidad);
			entidad.setRemision(remision);
			if (ticket != null) {
				//Guardamos el ticket
				remision.setTicket(ticket);
				remision.setEstadoOperacion(Constantes.ESTADO_XML_ENVIADO);
				remisionDao.save(remision);
				String status = obtenerEstadoGuia(accessToken, ticket,entidad, rutaXml,model);
				log.debug("[envioXMLGuia] status:"+status);
				if (status.equals("0")) {
					model.addAttribute("respuesta", "Envio y CDR Generado");
				}else if (status.equals("99")) {
					model.addAttribute("respuesta", "Envio y CDR con ERROR Generado: "+status);
				}else if (status.equals("98")) {
					model.addAttribute("respuesta", "En Proceso: "+status);
				}
			}else {
				model.addAttribute("respuesta", "Error en el envio:"+entidad.getMensajeRpta());
			}
			//Refrescando la pantalla
			Integer page = obtenerPage(request);
			Integer size = obtenerSize(request);
			String operacion = obtenerOperacion(request);
			PageableSG pageable = (PageableSG)request.getSession().getAttribute("SessionPrincipalPageabeSG");
			log.debug("[obtenerXMLGuia] page:"+page+" size:"+size+" operacion:"+operacion+" pageable:"+pageable);
			this.paginarPrincipalEntidad(page, size, operacion, model, pageable, request);
			log.debug("[obtenerXMLGuia] Fin");
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("respuesta", "Error en el envio:"+entidad.getMensajeRpta());
		}
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/estado/xml/{id}", method = RequestMethod.GET)
	public String estadoXMLGuia(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 				= null;
		String path							= null;
		try{
			log.debug("[estadoXMLGuia] Inicio");
			Map<String, TblParametro> mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			TblParametro TblParametro =  mapParametro.get(Constantes.RUTA_XML_GUIA_REMISION);
			String rutaXml = TblParametro.getDato();
			path = "operacion/remision/rem_listado";
			entidad = new RemisionBean();
			TblRemision remision = remisionDao.findOne(id);
			obtenerConfiguracionApi(entidad, request);
			String accessToken = obtenerTokenGuiaRemision(remision,entidad);
			
			entidad.setRemision(remision);
			if (entidad.getRemision().getTicket() != null) {

				String status = obtenerEstadoGuia(accessToken, entidad.getRemision().getTicket(),entidad, rutaXml,model);
				log.debug("[envioXMLGuia] status:"+status);
				if (status.equals("0")) {
					model.addAttribute("respuesta", "Envio y CDR Generado");
				}else if (status.equals("99")) {
					model.addAttribute("respuesta", "Envio y CDR con ERROR Generado: "+status);
				}else if (status.equals("98")) {
					model.addAttribute("respuesta", "En Proceso: "+status);
				}
			}else {
				model.addAttribute("respuesta", "Error en el envio:"+entidad.getMensajeRpta());
			}
			//Refrescando la pantalla
			Integer page = obtenerPage(request);
			Integer size = obtenerSize(request);
			String operacion = obtenerOperacion(request);
			PageableSG pageable = (PageableSG)request.getSession().getAttribute("SessionPrincipalPageabeSG");
			log.debug("[obtenerXMLGuia] page:"+page+" size:"+size+" operacion:"+operacion+" pageable:"+pageable);
			this.paginarPrincipalEntidad(page, size, operacion, model, pageable, request);
			log.debug("[estadoXMLGuia] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}
	private  String obtenerTokenGuiaRemision(TblRemision remision,RemisionBean entidad) throws Exception {
		String accessToken = null;
		try { 
			log.info("[obtenerTokenGuiaRemision] Inicio");
			/*Se establece el cliente POST para el servidor de autenticación */				   
			String resource = entidad.getApiTokenSunatUrl(); 
			CloseableHttpClient httpclient = HttpClients.createDefault();
			//HttpClient httpclient = httpClientSecury();
			HttpPost httpPost = new HttpPost(resource);

			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

			/*Se agregan los datos de autenticación para la plataforma Efact OSE*/
			List<NameValuePair> params = new ArrayList<>(); 
			params.add(new BasicNameValuePair("grant_type", "password")); 
			params.add(new BasicNameValuePair("client_id", entidad.getApiTokenSunatClientId())); 
			params.add(new BasicNameValuePair("client_secret", entidad.getApiTokenSunatClientSecret()));
			params.add(new BasicNameValuePair("username", entidad.getApiTokenSunatUsername())); 
			params.add(new BasicNameValuePair("password", entidad.getApiTokenSunatPassword())); 
			params.add(new BasicNameValuePair("scope", "https://api-cpe.sunat.gob.pe")); 
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			/*Se envía la petición y se recibe el json con el token*/
			String json= null; 
			json = httpclient.execute(httpPost, new StringResponseHandler());
			System.out.println("json: " + json); 
			// En caso de enviar datos correcto se obtiene el token de autenticación 
			ObjectMapper mapper = new ObjectMapper(); 
			JsonNode rootNodeToker = mapper.readTree(json); 
			accessToken = rootNodeToker.path("access_token").asText();
			log.info("[obtenerTokenGuiaRemision] accessToken:"+accessToken);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			entidad.setMensajeRpta(e.getMessage());
			accessToken = null;
			throw e;
		} catch(Exception ex) {
			entidad.setMensajeRpta(ex.getMessage());
			accessToken = null;
			throw ex;
		}
		return accessToken;
	}
	private  String obtenerTicketGuia(String accessToken, TblRemision remision,RemisionBean entidad) throws ClientProtocolException, IOException {
		String ticket=null; 
		JsonNode nameNode = null;
		ObjectMapper mapper = new ObjectMapper();
		log.info("[obtenerTicketGuia] Inicio");
		if (accessToken == null) {
			log.info("[obtenerTicketGuia] Error en el accessToken");
			return null;
		}
		try {
			File file = new File(remision.getRutaXML());
			String nombreSinExtension = file.getName().substring(0,file.getName().indexOf("."));

			String resource = entidad.getApiTicketSunatUrl()+nombreSinExtension; 
			log.info("[obtenerTicketGuia] resource:"+resource);

			//Path source = Paths.get("D:\\03.Gregorio\\06.2023\\02.Willy\\02.GuiaRemision\\02.DatosDysalim\\20602620337-09-TTT1-2.xml");
			String rutaAbsoluta = file.getAbsolutePath();
			Path source = Paths.get(rutaAbsoluta);
			//String zipFileName = "D:\\03.Gregorio\\06.2023\\02.Willy\\02.GuiaRemision\\02.DatosDysalim\\20602620337-09-TTT1-2.zip";
			String zipFileName = rutaAbsoluta.replace(".xml", ".zip");

			generarZipFile(source, zipFileName);


			final HttpPost httpPost = new HttpPost(resource);
			httpPost.setHeader("Authorization", "Bearer " + accessToken);
			//String nombreArchivo = "20602620337-09-TTT1-2.zip";
			String nombreArchivo = nombreSinExtension+".zip";
			String archivoBase64 = obtenerBase64File(zipFileName);
			String hashArchivo = obtenerHashZipFile(zipFileName);
			final String json = "{\"archivo\":{\"nomArchivo\":\""+nombreArchivo+"\", \"arcGreZip\":\""+archivoBase64+"\",\"hashZip\":\""+hashArchivo+"\"} }";
			final StringEntity entity = new StringEntity(json);
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");


			CloseableHttpClient httpclient = HttpClients.createDefault();

			String json2= null; 

			json2 = httpclient.execute(httpPost, new StringResponseHandler());
			nameNode = mapper.readTree(json2);
			System.out.println("json2: " + json2); 
			System.out.println("ticket: " + nameNode.get("numTicket")); 
			ticket = nameNode.get("numTicket").asText();
			log.info("[obtenerTicketGuia] Fin");
		}catch(Exception e) {
			entidad.setMensajeRpta(e.getMessage());
			e.printStackTrace();
			ticket = null;
		}
		return ticket;
	}
	private  String obtenerEstadoGuia(String accessToken, String ticket,RemisionBean entidad, String rutaXml, Model model) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		JsonNode nameNode = null;
		String codRespuesta = null;
		/*Se establece el cliente GET para el servidor de autenticación*/
		HttpGet httpget = new HttpGet(entidad.getApiEnvioSunatUrl() + ticket);
		/*Se agrega un Header de autorización con el token recibido por el servidor de autenticación*/
		httpget.setHeader("Authorization", "Bearer " + accessToken);
		/* Se envía la petición a la plataforma Efact OSE*/
		try {
			log.info("[obtenerEstadoGuia] Inicio");
			HttpResponse response = httpclient.execute(httpget);
			System.out.println("response: " + response );
			String data = EntityUtils.toString(response.getEntity());
			ObjectMapper mapper = new ObjectMapper();
			nameNode = mapper.readTree(data);
			log.info("[obtenerEstadoGuia] nameNode:"+nameNode);
			codRespuesta = nameNode.get("codRespuesta").asText();
			if (codRespuesta.equals("0")) {
				TblRemision remision = remisionDao.findOne(entidad.getRemision().getCodigoRemision());
				if (nameNode.get("arcCdr")!=null) {
					String arcCdr = nameNode.get("arcCdr").asText();
					String xmlFileRespuesta = UtilArchivoRespuesta.obtenerCDRXML(arcCdr, rutaXml);
					remision.setRutaCDR(rutaXml+"\\"+xmlFileRespuesta);
					remision.setEstadoOperacion(Constantes.ESTADO_XML_CON_CDR);
					remisionDao.save(remision);
				}
			}
			if (codRespuesta.equals("99")) {
				TblRemision remision = remisionDao.findOne(entidad.getRemision().getCodigoRemision());
				if (nameNode.get("arcCdr")!=null) {
					String arcCdr = nameNode.get("arcCdr").asText();
					String xmlFileRespuesta = UtilArchivoRespuesta.obtenerCDRXML(arcCdr, rutaXml);
					remision.setRutaCDR(rutaXml+"\\"+xmlFileRespuesta);
					remision.setEstadoOperacion(Constantes.ESTADO_XML_CON_CDR_ERROR);
					remisionDao.save(remision);
				}else {
					model.addAttribute("detalleRespuesta", nameNode.get("codRespuesta").asText() + nameNode.get("error").get("desError").asText());
				}
			}

			System.out.println("data: " + data );
			int status = response.getStatusLine().getStatusCode(); 
			System.out.println("STATUS CODE: " + status );
			log.info("[obtenerEstadoGuia] status:"+status);
			log.info("[obtenerEstadoGuia] Fin");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			entidad.setMensajeRpta(e.getMessage());
			e.printStackTrace();
			return null;
		} 
		
		
		
		/*Se valida el código de estado de la petición*/
		return nameNode.get("codRespuesta").asText();

	}
	public  String obtenerBase64File(String zipFileName) throws IOException {
		byte[] fileContent = FileUtils.readFileToByteArray(new File(zipFileName));
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        System.out.println("Zip Base 64::"+encodedString);
       return encodedString;
	}
	public  String obtenerHashZipFile(String zipFileName) throws IOException {
		File file = new File(zipFileName);
        ByteSource byteSource = com.google.common.io.Files.asByteSource(file);
        HashCode hc = byteSource.hash(Hashing.sha256());
        String checksum = hc.toString();
        System.out.println("Hash de SHA:"+checksum);
        return checksum;
	}
	public  void generarZipFile(Path source, String zipFileName)
			throws IOException {

		if (!Files.isRegularFile(source)) {
			System.err.println("Please provide a file.");
			return;
		}

		try (
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName));
				FileInputStream fis = new FileInputStream(source.toFile());
				) {

			ZipEntry zipEntry = new ZipEntry(source.getFileName().toString());
			zos.putNextEntry(zipEntry);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			zos.closeEntry();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private  class StringResponseHandler implements ResponseHandler<String> { 
		@Override 
		public String handleResponse(HttpResponse response) throws IOException { 
			int status = response.getStatusLine().getStatusCode(); 
			if (status >= 200 && status < 300) { 
				HttpEntity entity = response.getEntity(); 
				return entity != null ? EntityUtils.toString(entity) : null; 
			} else { 
				throw new ClientProtocolException("Unexpected response status: " + status); 
			} 
		}
	}
	
	private Integer obtenerPage(HttpServletRequest request) {
		Integer page = (Integer)request.getSession().getAttribute("SessionPrincipalIntegerPage");
		if (page == null) {
			page = 0;
		}
		return page;
	}
	private String obtenerOperacion(HttpServletRequest request) {
		String operacion =  (String)request.getSession().getAttribute("SessionPrincipalStringOperacion");
		if (operacion == null) {
			operacion = "C";
		}
		return operacion;
	}
	private Integer obtenerSize(HttpServletRequest request) {
		Integer size = (Integer)request.getSession().getAttribute("SessionPrincipalIntegerSize");
		if (size == null) {
			size = 5;
		}
		return size;
	}


	private List<TblDetalleRemision> obtenerDetalleRemision(List<TblDetalleComprobante> listDetalleComprobante, HttpServletRequest request, Integer indiceFacturaAsociada, RemisionBean entidadFormulario) {
		TblDetalleRemision detRemision = null;
		RemisionBean entidadOriginal = entidadFormulario;//(RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
		//int indice = 0;
		List<FacturaAsociadaBean> listaFacturaAsociada = entidadOriginal.getListaFacturaAsociada();
		
		List<TblDetalleRemision> listaDetalleRemision = new ArrayList<>();
		for(TblDetalleComprobante detComprobante:listDetalleComprobante) {
			if (detComprobante.getCantidad().compareTo(new BigDecimal("0"))>0) {
				detRemision = new TblDetalleRemision();
				detRemision.setDescripcion(listaFacturaAsociada.get(indiceFacturaAsociada).getDescripcion());
				//detRemision.setDescripcion(detComprobante.getDescripcion());
				log.info("[obtenerDetalleRemision] Antes:"+detComprobante.getDescripcion()+ " Ahora:"+listaFacturaAsociada.get(indiceFacturaAsociada).getDescripcion());
				indiceFacturaAsociada++;
				detRemision.setCantidad(detComprobante.getCantidad());
				detRemision.setPeso(detComprobante.getValorReferencia());
				detRemision.setUnidadMedida(detComprobante.getUnidadMedida());
				detRemision.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
				detRemision.setCodigoProducto(detComprobante.getCodigoProducto());
				detRemision.setAuditoriaCreacion(request);
				detRemision.setCodigoDetalleComprobante(detComprobante.getCodigoDetalle());
				listaDetalleRemision.add(detRemision);
			}
		}
		return listaDetalleRemision;

	}



	private void obtenerSerieRemision(RemisionBean entidad , HttpServletRequest request){
		Integer codigoEntidad 					= null;
		List<TblSerie> listaSerie 				= null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			listaSerie = serieDao.buscarAllxTipo(Constantes.TIPO_COMPROBANTE_GUIA_REMISION, codigoEntidad);
			if (listaSerie!=null){
				for(TblSerie serie:listaSerie){
					entidad.getRemision().setSerie(serie.getPrefijoSerie());
					entidad.getRemision().setNumero(String.format("%08d", serie.getSecuencialSerie()));
					break;
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void incrementarNumeroSerie(HttpServletRequest request) {
		Integer codigoEntidad 					= null;
		List<TblSerie> listaSerie 				= null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			listaSerie = serieDao.buscarAllxTipo(Constantes.TIPO_COMPROBANTE_GUIA_REMISION, codigoEntidad);
			if (listaSerie!=null){
				for(TblSerie serie:listaSerie){
					Integer numero = serie.getSecuencialSerie();
					numero++;
					String numeroFormateado = String.format("%08d", numero);
					serie.setNumeroComprobante(numeroFormateado);
					serie.setSecuencialSerie(numero);
					serieDao.save(serie);
					break;
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}
	private void decrementarNumeroSerie(HttpServletRequest request) {
		Integer codigoEntidad 					= null;
		List<TblSerie> listaSerie 				= null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			listaSerie = serieDao.buscarAllxTipo(Constantes.TIPO_COMPROBANTE_GUIA_REMISION, codigoEntidad);
			if (listaSerie!=null){
				for(TblSerie serie:listaSerie){
					Integer numero = serie.getSecuencialSerie();
					numero--;
					String numeroFormateado = String.format("%08d", numero);
					serie.setNumeroComprobante(numeroFormateado);
					serie.setSecuencialSerie(numero);
					serieDao.save(serie);
					break;
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}
	private void inicializaDatosRemision(RemisionBean entidad ){
		entidad.getRemision().setFechaEmision(UtilSGT.getFecha("dd/MM/yyyy"));
		entidad.getRemision().setFechaInicioTraslado(UtilSGT.getFecha("dd/MM/yyyy"));
		entidad.getRemision().setHoraInicioTraslado(UtilSGT.getHora());
		entidad.getRemision().setMotivoTraslado(Constantes.SUNAT_TIPO_OPERACION_VENTA_INTERNA);
		entidad.getRemision().setTipoComprobante(Constantes.TIPO_COMPROBANTE_GUIA_REMISION);
		entidad.getRemision().setCodigoDomicilioPartida(Constantes.CODIGO_DOMICILIO_PARTIDAD);
	}
	private boolean okDatosRemision(RemisionBean entidad, Model model) {
		boolean exitoso = true;
		if (entidad.getRemision().getMotivoTraslado().equals("-1")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe seleccionar el motivo del traslado");
			return exitoso;
		}
		if (entidad.getRemision().getMotivoTraslado().equals("13")) {
			if (entidad.getRemision().getObservacion() == null || entidad.getRemision().getObservacion().equals("")) {
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la Observacion. ");
				return exitoso;
			}
		}
		if (entidad.getRemision().getSerie().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar la serie");
			return exitoso;
		}
		if (entidad.getRemision().getNumero().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar el número de la guía de remisión");
			return exitoso;
		}
		if (entidad.getRemision().getFechaEmision().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar la fecha de remisión");
			return exitoso;
		}
		if (entidad.getRemision().getFechaInicioTraslado().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar la fecha de inicio de traslado");
			return exitoso;
		}
		if (entidad.getRemision().getTipoComprobante().equals("-1")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe seleccionar el tipo de comprobante");
			return exitoso;
		}
		if (entidad.getRemision().getCodigoDomicilioPartida().equals("-1")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe seleccionar el domicilio de partida");
			return exitoso;
		}
		if (entidad.getRemision().getDomicilioLlegada().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar el domicilio de llegada");
			return exitoso;
		}

		if (entidad.getRemision().getNumeroDocumentoTransportista().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar el RUC del transportista");
			return exitoso;
		}
		if (entidad.getRemision().getNombreTransportista().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar el nombre del transportista");
			return exitoso;
		}
		if (entidad.getRemision().getMarca().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar la marca del vehículo");
			return exitoso;
		}
		if (entidad.getRemision().getPlaca().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar la placa del vehículo");
			return exitoso;
		}
		if (entidad.getRemision().getNumeroCertInscripcion().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar el número de certificado de inscripción");
			return exitoso;
		}
		if (entidad.getRemision().getNumeroLicencia().trim().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar el número de licencia");
			return exitoso;
		}
		if (entidad.getListaFacturaAsociada().isEmpty()){
			exitoso = false;
			model.addAttribute("respuesta", "Debe ingresar la lista de factura - productos");
			return exitoso;
		}
		if (entidad.getRemision().getNombreCliente() == null || entidad.getRemision().getNombreCliente().isEmpty() ){
			exitoso = false;
			model.addAttribute("respuesta", "Debe seleccionar al cliente");
			return exitoso;
		}
		return exitoso;
	}
	/*********************************************************************************************************************************/
	/*Sección de Paginado
	/*********************************************************************************************************************************/
	@RequestMapping(value = "/operacion/remision/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			log.debug("[paginarEntidad] Inicio");
			path = "operacion/remision/rem_listado_factura";
			if (pageable!=null){
				if (pageable.getLimit() == 0){
					pageable.setLimit(size);
				}
				if (pageable.getOffset()== 0){
					pageable.setOffset(page*size);
				}
				if (pageable.getOperacion() ==null || pageable.getOperacion().equals("")){
					pageable.setOperacion(operacion);
				}

			}
			filtro = (Filtro)request.getSession().getAttribute("sessionFiltroCriterio");
			model.addAttribute("filtro", filtro);
			this.listarComprobante(model, filtro, pageable,this.urlPaginado, request);

			log.debug("[paginarEntidad] Fin");
		}catch(Exception e){
			log.debug("[paginarEntidad] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}
		return path;
	}
	@RequestMapping(value = "/operacion/remision/principal/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarPrincipalEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			log.debug("[paginarPrincipalEntidad] Inicio");
			path = "operacion/remision/rem_listado";
			if (pageable!=null){
				if (pageable.getLimit() == 0){
					pageable.setLimit(size);
				}
				if (pageable.getOffset()== 0){
					pageable.setOffset(page*size);
				}
				if (pageable.getOperacion() ==null || pageable.getOperacion().equals("")){
					pageable.setOperacion(operacion);
				}

			}
			filtro = (Filtro)request.getSession().getAttribute("sessionFiltroRemisionCriterio");
			model.addAttribute("filtro", filtro);
			this.listarRemision(model, filtro, pageable,this.urlPaginadoRemision, request);
			request.getSession().setAttribute("SessionPrincipalIntegerPage", page);
			request.getSession().setAttribute("SessionPrincipalIntegerSize", size);
			request.getSession().setAttribute("SessionPrincipalStringOperacion", operacion);

			log.debug("[paginarPrincipalEntidad] Fin");
		}catch(Exception e){
			log.debug("[paginarPrincipalEntidad] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}
		return path;
	}
	/*********************************************************************************************************************************/
	/*Sección de Retorno
	/*********************************************************************************************************************************/
	/*** Retorna a la consulta de facturas ***/
	@RequestMapping(value = "/operacion/regresar/listafactura", method = RequestMethod.GET)
	public String regresarListadoFactura(Model model, Filtro filtro, String path, HttpServletRequest request) {
		try{
			log.debug("[regresarListadoFactura] Inicio");
			path = "operacion/remision/rem_listado_factura";
			model.addAttribute("entidad", request.getSession().getAttribute("guiaRemisionSession"));
			model.addAttribute("filtro", request.getSession().getAttribute("sessionFiltroCriterio"));
			model.addAttribute("registros", request.getSession().getAttribute("ListadoConsultaFactura"));
			model.addAttribute("page", request.getSession().getAttribute("pageConsultaFactura"));
			log.debug("[regresarListadoFactura] Fin");
		}catch(Exception e){
			log.debug("[regresarListadoFactura] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}
	/*Retorna a la pantalla de nueva remision*/
	@RequestMapping(value = "/operacion/remision/regresar", method = RequestMethod.GET)
	public String regresarNuevoRemision(Model model, Filtro filtro, String path, HttpServletRequest request) {
		try{
			log.debug("[regresarComprobante] Inicio");
			path = "operacion/remision/rem_nuevo";
			model.addAttribute("entidad", request.getSession().getAttribute("guiaRemisionSession"));
			log.debug("[regresarComprobante] Fin");
		}catch(Exception e){
			log.debug("[regresarComprobante] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}
	/*Retorna a la pantalla de guias de remision*/
	@RequestMapping(value = "/operacion/regresar", method = RequestMethod.GET)
	public String regresarRemision(Model model, Filtro filtro, String path, HttpServletRequest request) {
		try{
			log.debug("[regresarRemision] Inicio");
			path = "operacion/remision/rem_listado";
			model.addAttribute("filtro", request.getSession().getAttribute("sessionFiltroRemisionCriterio"));
			model.addAttribute("page", request.getSession().getAttribute("PageRemisionPrincipal"));
			model.addAttribute("registros", request.getSession().getAttribute("ListadoConsultaRemision"));

			log.debug("[regresarRemision] Fin");
		}catch(Exception e){
			log.debug("[regresarRemision] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}
	/*********************************************************************************************************************************/
	/*Motivo de Traslado
	/*********************************************************************************************************************************/
	@RequestMapping(value = "/operacion/remision/motivotraslado/actualizar", method = RequestMethod.POST)
	public String actualizarMotivoTraslado(Model model, RemisionBean entidad, HttpServletRequest request) {
		String path							= null;
		try{
			log.debug("[actualizarMotivoTraslado] Inicio");
			
			path = "operacion/remision/rem_nuevo";
			String motivoTraslado = entidad.getRemision().getMotivoTraslado();
			String serie = entidad.getRemision().getSerie();
			String numero = entidad.getRemision().getNumero();
			entidad = new RemisionBean();
			entidad.setRemision(new TblRemision());
			entidad.getRemision().setTipoTransporte(Constantes.TIPO_TRANSPORTE_PUBLICO);
			//this.obtenerSerieRemision(entidad, request);
			this.inicializaDatosRemision(entidad);
			entidad.getRemision().setMotivoTraslado(motivoTraslado);
			entidad.getRemision().setSerie(serie);
			entidad.getRemision().setNumero(numero);
			entidad.setRuc(Constantes.SUNAT_RUC_EMISOR);
			model.addAttribute("entidad", entidad);
			request.getSession().setAttribute("guiaRemisionSession", entidad);
			
			log.debug("[actualizarMotivoTraslado] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}
	
	/*********************************************************************************************************************************/
	/*Muestra la pantalla para asignar transporte
	/*********************************************************************************************************************************/
	@RequestMapping(value = "/operacion/remision/transporte", method = RequestMethod.POST)
	public String traerRegistrosTransporte(Model model, String path, HttpServletRequest request, RemisionBean entidad) {
		Filtro filtro = null;
		RemisionBean entidadOriginal = null;
		try{
			log.debug("[traerRegistrosTransporte] Inicio");
			path = "operacion/remision/rem_listado_transporte";
			
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			this.recuperarNombreProducto(entidad, entidadOriginal);
			entidad.setListaFacturaAsociada(entidadOriginal.getListaFacturaAsociada());
			entidad.setListaComprobante(entidadOriginal.getListaComprobante());
			//entidad.setTotalPesoGuia(entidadOriginal.getTotalPesoGuia());
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setNumero("");;
			log.debug("[traerRegistrosTransporte] Fin");
		}catch(Exception e){
			log.debug("[traerRegistrosTransporte] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}
		return path;
	}
	@RequestMapping(value = "/operacion/remision/transporte/q", method = RequestMethod.POST)
	public String traerRegistrosFiltradosTransporte(Model model, Filtro filtro,  String path, HttpServletRequest request) {
		path = "operacion/remision/rem_listado_transporte";
		String[] criterio = null;
		List<TblTransporte> listaTransporte = null;
		try{
			log.debug("[traerRegistrosFiltradosTransporte] Inicio");
			criterio = obtenerCriterios(filtro);
			listaTransporte = transporteDao.buscarTransportexCriterios(criterio[0], criterio[1], criterio[2],criterio[3]);
			request.getSession().setAttribute("ListadoConsultaTransporte",listaTransporte);
			model.addAttribute("filtro", filtro);
			model.addAttribute("registros", listaTransporte);
			log.debug("[traerRegistrosFiltradosTransporte] Fin");
		}catch(Exception e){
			log.debug("[traerRegistrosFiltradosTransporte] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		log.debug("[traerRegistrosFiltradosTransporte] Fin");
		return path;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/transporte/seleccion/{id}", method = RequestMethod.GET)
	public String seleccionaraRegistroTransporte(@PathVariable Integer id, HttpServletRequest request, Model model) {
		String path 								= null;
		RemisionBean entidad 						= null;
		List<TblTransporte> listaTransporte 		= null;
		TblTransporte transporte					= null;
		try{
			log.debug("[seleccionaraRegistroTransporte] Inicio");
			path = "operacion/remision/rem_nuevo";
			entidad = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			listaTransporte = (List<TblTransporte>)request.getSession().getAttribute("ListadoConsultaTransporte");
			transporte = listaTransporte.get(id);
			
			entidad.getRemision().setNumeroDocumentoTransportista(transporte.getRuc());
			entidad.getRemision().setNombreTransportista(transporte.getNombre());
			entidad.getRemision().setMarca(transporte.getMarca());
			entidad.getRemision().setPlaca(transporte.getPlaca());
			entidad.getRemision().setRemolque(transporte.getRemolque()==null?"":transporte.getRemolque());
			entidad.getRemision().setNumeroCertInscripcion(transporte.getNumeroCertificadoInscripcion());
			entidad.getRemision().setNumeroLicencia(transporte.getNumeroLicencia());
			entidad.getRemision().setTipoDocumentoTransportista("RUC");
			entidad.getRemision().setEstadoOperacion("00");
			entidad.getRemision().setNumeroRegistroMtc(transporte.getNumeroRegistroMtc());
			entidad.getRemision().setRemolque(transporte.getRemolque()==null?"":transporte.getRemolque());
			model.addAttribute("entidad", entidad);
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			log.debug("[seleccionaraRegistroTransporte] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;			
		}
		return path;
	}
	
	private String[] obtenerCriterios(Filtro filtro) {
		String[] criterio = new String[4];
		criterio[0] = validarCriterio(filtro.getRuc());
		criterio[1] = validarCriterio(filtro.getNombre());
		criterio[2] = validarCriterio(filtro.getMarca());
		criterio[3] = validarCriterio(filtro.getPlaca());
		return criterio;
	}
	private String validarCriterio(String dato) {
		if (dato== null || dato.isEmpty()) {
			return "%";
		}else {
			return dato.trim().toUpperCase()+"%";
		}
	}	
	
	/*********************************************************************************************************************************/
	/*Muestra la pantalla para el ingreso de productos y cliente*/
	/*********************************************************************************************************************************/
	@RequestMapping(value = "/operacion/remision/comprobantes/nuevo", method = RequestMethod.POST)
	public String traerNuevoProductoClienteComprobante(Model model, String path, HttpServletRequest request, RemisionBean entidad) {
		RemisionBean entidadOriginal = null;
		try{
			log.debug("[traerNuevoProductoClienteComprobante] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			this.recuperarNombreProducto(entidad, entidadOriginal);
			entidad.setListaFacturaAsociada(entidadOriginal.getListaFacturaAsociada());
			entidad.setListaComprobante(entidadOriginal.getListaComprobante());
			//entidad.setTotalPesoGuia(entidadOriginal.getTotalPesoGuia());
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			request.getSession().setAttribute("guiaRemisionSinComprobanteSession", new RemisionBean());
			path = "operacion/remision/rem_com_nuevo";
			model.addAttribute("entidad", request.getSession().getAttribute("guiaRemisionSinComprobanteSession"));
			
			log.debug("[traerNuevoProductoClienteComprobante] Fin");
		}catch(Exception e){
			log.debug("[traerNuevoProductoClienteComprobante] Error:"+e.getMessage());
			e.printStackTrace();
		}

		return path;
	}
	/*
	 * Regresa a la pantalla de el ingreso de productos y cliente
	 */
	@RequestMapping(value = "/operacion/remision/comprobantes/regresar", method = RequestMethod.GET)
	public String regresarComprobanteNuevo(Model model, RemisionBean entidad, String path, HttpServletRequest request) {
		try{
			log.debug("[regresarComprobanteNuevo] Inicio");
			
			path = "operacion/remision/rem_nuevo";			
			model.addAttribute("entidad", request.getSession().getAttribute("guiaRemisionSession"));
			
			log.debug("[regresarComprobanteNuevo] Fin");
		}catch(Exception e){
			log.debug("[regresarComprobanteNuevo] Error:"+e.getMessage());
			e.printStackTrace();
		}
		return path;
	}
	/*
	 * Listado de Clientes
	 */
	@RequestMapping(value = "/operacion/remision/comprobantes/clientes", method = RequestMethod.POST)
	public String mostrarClientes(Model model, RemisionBean entidad, String path, HttpServletRequest request) {
		path = "operacion/remision/rem_cli_listado";
		try{
			log.debug("[traerRegistrosFiltrados] Inicio");
			request.getSession().setAttribute("guiaRemisionSinComprobanteSession", entidad);
			model.addAttribute("filtro", new Filtro());
			log.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			log.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		log.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*
	 * Regresa a la pantalla de el ingreso de productos y cliente
	 */
	@RequestMapping(value = "/operacion/remision/comprobantes/regresar", method = RequestMethod.POST)
	public String regresarProductoClienteComprobante(Model model, RemisionBean entidad, String path, HttpServletRequest request) {
		try{
			log.debug("[ProductoClienteComprobante] Inicio");
			
			path = "operacion/remision/rem_com_nuevo";			
			model.addAttribute("entidad", request.getSession().getAttribute("guiaRemisionSinComprobanteSession"));
			
			log.debug("[ProductoClienteComprobante] Fin");
		}catch(Exception e){
			log.debug("[regresarComprobante] Error:"+e.getMessage());
			e.printStackTrace();
		}
		return path;
	}
	@RequestMapping(value = "/operacion/remision/comprobantes/clientes/q", method = RequestMethod.POST)
	public String listarClientes(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/remision/rem_cli_listado";
		try{
			log.debug("[listarClientes] Inicio");
			if (validarNegocioCliente(model, filtro)){
				this.listarCliente(model, filtro, request);
			}else{
				model.addAttribute("registros", new ArrayList<TblCliente>());
			}
			//request.getSession().setAttribute("filtroSession", filtro);
			model.addAttribute("filtro", filtro);
			log.debug("[listarClientes] Fin");
		}catch(Exception e){
			log.debug("[listarClientes] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		log.debug("[listarClientes] Fin");
		return path;
	}
	/*** Listado de Cliente ***/
	private void listarCliente(Model model, Filtro filtro, HttpServletRequest request){
		List<TblCliente> entidades = new ArrayList<TblCliente>();
		Integer codigoEntidad = null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			Specification<TblCliente> criterio = Specifications.where(conNumeroDocumento((filtro.getNumero())))
					.and(com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conNombreCliente(filtro.getNombre().toUpperCase()))
					.and(com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conCodigoEmpresa(codigoEntidad))
					.and(conEstadoCliente(Constantes.ESTADO_REGISTRO_ACTIVO));
			entidades = clienteDao.findAll(criterio);
			log.debug("[listarCliente] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
	
	private boolean validarNegocioCliente(Model model, Filtro filtro) {
		boolean exitoso = true;
		try{
			
			if ((filtro.getNombre() == null ||filtro.getNombre().equals("")) &&
				(filtro.getNumero() == null ||filtro.getNumero().equals(""))
				){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar algún criterio de búsqueda");
				return exitoso;
			}
			
		}catch(Exception e){
			exitoso = false;
		}
		return exitoso;
	}
	/*
	 * Asignar Cliente
	 */
	@RequestMapping(value = "/operacion/remision/comprobantes/clientes/seleccionar/{id}", method = RequestMethod.GET)
	public String asignarClienteGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		TblCliente cliente 					= null;
		String path							= null;
		RemisionBean remisionBean	 		= null;
		try{
			remisionBean = (RemisionBean)request.getSession().getAttribute("guiaRemisionSinComprobanteSession");
			cliente = clienteDao.findOne(id);
			//Se mantiene en Session el contrato hasta retornar
			
			if (cliente!=null ){
				remisionBean.getComprobante().setNombreCliente(cliente.getNombre());
				remisionBean.getComprobante().setNumeroDocumento(cliente.getNumeroDocumento());
				remisionBean.getComprobante().setTipoDocumento(cliente.getTblCatalogo().getCodigoSunat());
				remisionBean.getComprobante().setDireccionCliente(cliente.getDireccion());
				remisionBean.getRemision().setUbigeoLlegada(cliente.getDistrito());
			}else{
				remisionBean.getComprobante().setNombreCliente("");
				remisionBean.getComprobante().setNumeroDocumento("");
				remisionBean.getComprobante().setTipoDocumento("");
				remisionBean.getComprobante().setDireccionCliente("");
				remisionBean.getRemision().setUbigeoLlegada("");
			}
			model.addAttribute("entidad", remisionBean);
			request.getSession().setAttribute("guiaRemisionSinComprobanteSession",remisionBean);
			path = "operacion/remision/rem_com_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cliente = null;
		}
		return path;
	}

	/*
	 * Listado de Producto
	 */
	@RequestMapping(value = "/operacion/remision/comprobantes/productos", method = RequestMethod.POST)
	public String mostrarProducto(Model model, RemisionBean entidad, String path, HttpServletRequest request) {
		path = "operacion/remision/rem_pro_listado";
		try{
			log.debug("[mostrarProducto] Inicio");
			//request.getSession().setAttribute("filtroSession", entidad);
			model.addAttribute("filtro", new Filtro());
			log.debug("[mostrarProducto] Fin");
		}catch(Exception e){
			log.debug("[mostrarProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		log.debug("[mostrarProducto] Fin");
		return path;
	}
	/*** Listado de Producto ***/
	private void buscarProducto(Model model, Filtro filtro, HttpServletRequest request){
		List<TblProducto> entidades = new ArrayList<TblProducto>();
		Integer codigoEntidad = null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			Specification<TblProducto> criterio = Specifications.where(conCodigoProducto((filtro.getCodigoFiltro())))
					.and(conNombreProducto(filtro.getNombre().toUpperCase()))
					.and(com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conCodigoEmpresa(codigoEntidad))
					.and(conEstadoProducto(Constantes.ESTADO_REGISTRO_ACTIVO));
			entidades = productoDao.findAll(criterio);
			log.debug("[listarProducto] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
 
	@RequestMapping(value = "/operacion/remision/comprobantes/productos/q", method = RequestMethod.POST)
	public String listarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/remision/rem_pro_listado";
		try{
			log.debug("[listarProducto] Inicio");
			this.buscarProducto(model, filtro,request);
			//request.getSession().setAttribute("filtroSession", filtro);
			model.addAttribute("filtro", filtro);
			log.debug("[listarProducto] Fin");
		}catch(Exception e){
			log.debug("[listarProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		log.debug("[listarProducto] Fin");
		return path;
	}
	/*
	 * Asignar Cliente
	 */
	@RequestMapping(value = "/operacion/remision/comprobantes/productos/seleccionar/{id}", method = RequestMethod.GET)
	public String asignarProductoGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		TblProducto producto				= null;
		String path							= null;
		RemisionBean remisionBean			= null;
		try{
			remisionBean = (RemisionBean)request.getSession().getAttribute("guiaRemisionSinComprobanteSession");
			producto = productoDao.findOne(id);
			//Se mantiene en Session el contrato hasta retornar
			remisionBean.setDetalleComprobante(new TblDetalleComprobante());
			log.debug("[asignarProductoGet] catalogo:"+producto.getTblCatalogo().getNombre());
			remisionBean.getDetalleComprobante().setDescripcion("");
			remisionBean.getDetalleComprobante().setPrecioUnitario(new BigDecimal("0"));
			remisionBean.getDetalleComprobante().setMoneda("");
			if (producto!=null ){
				remisionBean.getDetalleComprobante().setDescripcion(producto.getCodigoEmpresa() + ":" + producto.getNombre());
				remisionBean.getDetalleComprobante().setPrecioUnitario(producto.getPrecio());
				remisionBean.getDetalleComprobante().setUnidadMedida(producto.getUnidadMedida());
				remisionBean.getDetalleComprobante().setCodigoProducto(producto.getCodigoEmpresa());
				remisionBean.getDetalleComprobante().setMoneda(producto.getTblCatalogo().getCodigoSunat());
				remisionBean.getDetalleComprobante().setValorReferencia(producto.getPeso());
			}
			
			model.addAttribute("entidad", remisionBean);
			request.getSession().setAttribute("guiaRemisionSinComprobanteSession",remisionBean);
			
			path = "operacion/remision/rem_com_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			producto = null;
		}
		return path;
	}

	@RequestMapping(value = "/operacion/remision/comprobantes/adicionarDetalle", method = RequestMethod.POST)
	public String agregarProducto(Model model, RemisionBean entidad, String path, HttpServletRequest request) {
		path = "operacion/remision/rem_com_nuevo";
		RemisionBean remisionBean			= null;
		try{
			log.debug("[agregarProducto] Inicio");
			remisionBean = (RemisionBean)request.getSession().getAttribute("guiaRemisionSinComprobanteSession");
			if (okDatosParaAgregar(entidad,remisionBean,model)) {
				if (remisionBean.getListaDetalle()== null) {
					remisionBean.setListaDetalle(new ArrayList<>());
				}
				TblDetalleComprobante detalle = remisionBean.getDetalleComprobante();
				detalle.setCantidad(entidad.getDetalleComprobante().getCantidad());
				detalle.setValorReferencia(detalle.getCantidad().multiply(entidad.getDetalleComprobante().getValorReferencia()));
				remisionBean.getListaDetalle().add(detalle);
				remisionBean.setDetalleComprobante(new TblDetalleComprobante());
				request.getSession().setAttribute("guiaRemisionSinComprobanteSession",remisionBean);
			}
			
			model.addAttribute("entidad", remisionBean);
			log.debug("[agregarProducto] Fin");
		}catch(Exception e){
			log.debug("[agregarProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		log.debug("[agregarProducto] Fin");
		return path;
	}

	private boolean okDatosParaAgregar(RemisionBean entidad, RemisionBean remisionBean,Model model) {
		boolean respuesta = true;
		if(entidad.getDetalleComprobante().getCantidad()==null || entidad.getDetalleComprobante().getCantidad().compareTo(new BigDecimal("0"))<=0) {
			model.addAttribute("respuesta", "Debe ingresar la cantidad");
			respuesta = false;
		}else {
			if (remisionBean.getDetalleComprobante().getDescripcion()==null || remisionBean.getDetalleComprobante().getDescripcion().isEmpty()) {
				model.addAttribute("respuesta", "Debe seleccionar un producto");
				respuesta = false;
			}
		}
		return respuesta;
	}
	@RequestMapping(value = "/operacion/remision/comprobantes/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarDetalleComprobanteRemision(@PathVariable Integer id, Model model, HttpServletRequest request,  PageableSG pageable) {
		String path = "operacion/remision/rem_com_nuevo";
		RemisionBean remisionBean			= null;
		try{
			log.debug("[eliminarDetalleComprobanteRemision] Inicio");
			remisionBean = (RemisionBean)request.getSession().getAttribute("guiaRemisionSinComprobanteSession");
			remisionBean.getListaDetalle().remove(id.intValue());
			request.getSession().setAttribute("guiaRemisionSinComprobanteSession",remisionBean);
			model.addAttribute("entidad", remisionBean);
			
			log.debug("[eliminarDetalleComprobanteRemision] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}
	

	@RequestMapping(value = "/operacion/remision/comprobantes/nuevo/asociar", method = RequestMethod.POST)
	public String asociarNuevoDocumento(Model model, RemisionBean entidad,  String path, HttpServletRequest request) {
		path = "operacion/remision/rem_nuevo";
		RemisionBean entidadOriginal 			= null;
		FacturaAsociadaBean facturaAsociada		= new FacturaAsociadaBean();
		TblComprobante comprobante				= null;
		//Map<String, TblProducto> mapProducto	= obtenerMapProductoSession(request);
		BigDecimal totalPesoGuia				= new BigDecimal("0");
		try{
			log.debug("[asociarNuevoDocumento] Inicio");
			entidad = (RemisionBean)request.getSession().getAttribute("guiaRemisionSinComprobanteSession");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			if (entidadOriginal.getListaFacturaAsociada()==null) {
				log.debug("[asociarNuevoDocumento] Inicializamos la primera vez");
				entidadOriginal.setListaFacturaAsociada(new ArrayList<>());
			}else {
				log.debug("[asociarNuevoDocumento] Tamaño de la lista: "+entidadOriginal.getListaFacturaAsociada().size());
				for(FacturaAsociadaBean factura:entidadOriginal.getListaFacturaAsociada()) {
					totalPesoGuia = totalPesoGuia.add(factura.getPeso());
				}
				log.debug("[asociarNuevoDocumento] totalPesoGuia: "+totalPesoGuia);
			}
			//Recuperamos los datos de comprobante y detalle de comprobante para pasarle al ComprobanteBean original
			comprobante = entidad.getComprobante();
			if (!entidad.getListaDetalle().isEmpty()) {
				for(TblDetalleComprobante detalleComprobante: entidad.getListaDetalle()) {
					if (detalleComprobante.getCantidad().compareTo(new BigDecimal("0"))>0) {
						facturaAsociada = new FacturaAsociadaBean();
						facturaAsociada.setSerieFactura("-");
						facturaAsociada.setNumeroFactura("-");
						facturaAsociada.setCodigoComprobante(0);
						facturaAsociada.setCodigoDetalleComprobante(0);
						facturaAsociada.setNombreCliente(comprobante.getNombreCliente());
						facturaAsociada.setDescripcion(detalleComprobante.getDescripcion());
						facturaAsociada.setUnidadMedida(detalleComprobante.getUnidadMedida());
						facturaAsociada.setCantidad(detalleComprobante.getCantidad());
						//facturaAsociada.setPeso(calcularPesoProducto(detalleComprobante,mapProducto));
						facturaAsociada.setPeso(detalleComprobante.getValorReferencia());
						entidadOriginal.getListaFacturaAsociada().add(facturaAsociada);
						totalPesoGuia = totalPesoGuia.add(facturaAsociada.getPeso());
						facturaAsociada.setNumeroCliente(comprobante.getNumeroDocumento());
						entidadOriginal.getRemision().setDomicilioLlegada(comprobante.getDireccionCliente());
						entidadOriginal.getRemision().setUbigeoLlegada(entidad.getRemision().getUbigeoLlegada());
					}
				}
				log.debug("[asociarNuevoDocumento] totalPesoGuia: "+totalPesoGuia);
				log.debug("[asociarNuevoDocumento] Luego...Tamaño de la lista: "+entidadOriginal.getListaFacturaAsociada().size());
			}
			entidadOriginal.setTotalPesoGuia(totalPesoGuia);
			//Almacenamos la información de la factura y su detalle
			if (entidadOriginal.getListaComprobante() == null) {
				entidadOriginal.setListaComprobante(new ArrayList<>());
				log.debug("[asociarNuevoDocumento] Inicializamos la primera vez a ComprobanteBean");
			}else {
				log.debug("[asociarNuevoDocumento] Tamaño de la lista de ComprobanteBean:"+entidadOriginal.getListaComprobante().size());
			}
			ComprobanteBean comprobanteParaRegistro = new ComprobanteBean();
			comprobanteParaRegistro.setComprobante(entidad.getComprobante());
			comprobanteParaRegistro.setListaDetalle(entidad.getListaDetalle());
			entidadOriginal.getListaComprobante().add(comprobanteParaRegistro);


			model.addAttribute("entidad", entidadOriginal);
			request.getSession().setAttribute("guiaRemisionSession", entidadOriginal);

			log.debug("[asociarNuevoDocumento] entidadOriginal:"+entidadOriginal.getRemision().getDomicilioLlegada());
			log.debug("[asociarNuevoDocumento] Luego...Tamaño de la lista de ComprobanteBean:"+entidadOriginal.getListaComprobante().size());
			log.debug("[asociarNuevoDocumento] Fin");
		}catch(Exception e){
			log.debug("[asociarNuevoDocumento] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
			path = "operacion/remision/rem_com_nuevo";
		}
		log.debug("[asociarNuevoDocumento] Fin");
		return path;
	}
}
