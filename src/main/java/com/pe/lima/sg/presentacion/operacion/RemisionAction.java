package com.pe.lima.sg.presentacion.operacion;

import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conCodigoEmpresa;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conEstado;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conNumero;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conSerie;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conTipoComprobante;
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
import java.util.HashMap;
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
import com.pe.lima.sg.dao.mantenimiento.ISerieDAO;
import com.pe.lima.sg.dao.operacion.IComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleRemisionDAO;
import com.pe.lima.sg.dao.operacion.IFacturaAsociadaDAO;
import com.pe.lima.sg.dao.operacion.IRemisionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblSerie;
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
import com.pe.lima.sg.util.remision.util.GuiaRemision;

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

	private String urlPaginado = "/operacion/remision/paginado/"; 

	private String urlPaginadoRemision = "/operacion/remision/principal/paginado/"; 

	@RequestMapping(value = "/operacion/remision", method = RequestMethod.GET)
	/**
	 * sessionFiltroRemisionCriterio	: Filtro de la pagina de Inicio
	 * filtro							: Filtro de la pagina de Inicio
	 * 
	 */
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
	/**
	 * sessionFiltroRemisionCriterio	: Filtro de la pagina de Inicio
	 * filtro							: Filtro de la pagina de Inicio
	 * 
	 */
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
	/**
	 * registros						: Listado de Remision [TblRemision]
	 * ListadoConsultaRemision			: Listado de Remision [TblRemision]
	 * filtro							: Filtro de la pagina de Inicio
	 * page								: Paginado - page
	 * PageRemisionPrincipal			: Paginado - page
	 * SessionPrincipalPageabeSG		: pageable
	 * 
	 */
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
	/**
	 * entidad							: Contiene toda la info de la remision [RemisionBean]
	 * guiaRemisionSession				: Contiene toda la info de la remision [RemisionBean]
	 * 
	 * 
	 */
	@RequestMapping(value = "operacion/remision/nuevo", method = RequestMethod.GET)
	public String crearRemision(Model model, HttpServletRequest request) {
		RemisionBean entidad = null;
		try{
			log.debug("[crearRemision] Inicio");
			entidad = new RemisionBean();
			entidad.setRemision(new TblRemision());
			this.obtenerSerieRemision(entidad, request);
			this.inicializaDatosRemision(entidad);
			entidad.setRuc(Constantes.SUNAT_RUC_EMISOR);
			model.addAttribute("entidad", entidad);
			//request.getSession().setAttribute("listaDetalleSession", new ArrayList<TblDetalleComprobante>());
			request.getSession().setAttribute("guiaRemisionSession", entidad);
			log.debug("[crearComprobante] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 				= null;
		}
		return "operacion/remision/rem_nuevo";
	}
	/**
	 * filtro							: Filtro de la consulta 
	 * sessionFiltroCriterio			: Filtro de la consulta 
	 * guiaRemisionSession				: Contiene toda la info de la remision [RemisionBean]
	 * 
	 * 
	 */
	@RequestMapping(value = "/operacion/remision/facturas", method = RequestMethod.POST)
	public String traerRegistrosFactura(Model model, String path,  PageableSG pageable, HttpServletRequest request, RemisionBean entidad) {
		Filtro filtro = null;
		RemisionBean entidadOriginal = null;
		try{
			log.debug("[traerRegistrosFactura] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			entidad.setListaFacturaAsociada(entidadOriginal.getListaFacturaAsociada());
			entidad.setListaComprobante(entidadOriginal.getListaComprobante());
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

	@RequestMapping(value = "/operacion/remision/factura/q", method = RequestMethod.POST)
	/**
	 * filtro							: Filtro de la consulta de Factura
	 * sessionFiltroCriterio			: Filtro de la consulta de Factura
	 * 
	 * 
	 */
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
	/**
	 * UsuarioSession					: datos del usuario
	 * registros						: Datos de los comprobantes [TblComprobante]
	 * ListadoConsultaFactura			: Datos de los comprobantes [TblComprobante]
	 * page								: Paginado - page
	 * pageConsultaFactura				: Paginado - page
	 * 
	 */
	private void listarComprobante(Model model, Filtro filtro,  PageableSG pageable, String url, HttpServletRequest request){
		List<TblComprobante> entidades = new ArrayList<TblComprobante>();
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "numero"));
		Integer codigoEntidad	= null;
		try{
			//this.actualizarEstadoComprobanteSfs12(filtro, request);
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;

			Specification<TblComprobante> criterio = Specifications.where(conNumero(filtro.getNumero()))
					.and(conCodigoEmpresa(codigoEntidad))
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
	@RequestMapping(value = "/operacion/remision/seleccion/{id}", method = RequestMethod.GET)
	/**
	 * guiaRemisionSession				: Contiene toda la info de la remision [RemisionBean]
	 * entidad							: Contiene toda la info de la remision [RemisionBean]
	 * ListadoConsultaFactura			: Datos de los comprobantes [TblComprobante]
	 * 
	 */
	public String seleccionaraRegistroFactura(@PathVariable Integer id, HttpServletRequest request, Model model, PageableSG pageable) {
		TblComprobante comprobante					= null;
		String path 								= null;
		List<TblComprobante> lista 					= null;
		List<TblDetalleComprobante> listaDetalle 	= null;
		RemisionBean entidad 						= null;
		try{
			log.debug("[seleccionaraRegistroFactura] Inicio");
			path = "operacion/remision/rem_previo_nuevo";
			entidad = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			lista = (List<TblComprobante>)request.getSession().getAttribute("ListadoConsultaFactura");
			comprobante = lista.get(id);
			entidad.setComprobante(comprobante);
			entidad.getRemision().setDomicilioLlegada(entidad.getComprobante().getDireccionCliente());
			//entidad.getRemision().setNumeroDocumentoTransportista(entidad.getComprobante().getNumeroDocumento());
			//entidad.getRemision().setNombreTransportista(entidad.getComprobante().getNombreCliente());
			listaDetalle = detalleComprobanteDao.listarxComprobante(comprobante.getCodigoComprobante());
			//Actualizar cantidades
			actualizarCantidadComprobante(listaDetalle);
			entidad.setListaDetalle(listaDetalle);
			model.addAttribute("entidad", entidad);
			request.getSession().setAttribute("guiaRemisionSession",entidad);
			log.debug("[seleccionaraRegistroConsulta] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;			
		}
		return path;
	}
	private void actualizarCantidadComprobante(List<TblDetalleComprobante> listaDetalle) {
		for(TblDetalleComprobante detalle: listaDetalle) {
			detalle.setCantidad(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP));
			BigDecimal cantidadGuia = detalle.getCantidadGuia();
			if (cantidadGuia !=null) {
				detalle.setCantidad(detalle.getCantidad().subtract(cantidadGuia));
				detalle.setCantidad(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP));
			}
		}

	}
	@RequestMapping(value = "/operacion/remision/nuevo/asociar", method = RequestMethod.POST)
	/**
	 * guiaRemisionSession				: Contiene toda la info de la remision [RemisionBean]
	 * entidad							: Contiene toda la info de la remision [RemisionBean]
	 * ListadoConsultaFactura			: Datos de los comprobantes [TblComprobante]
	 * 
	 */
	public String asociarFactura(Model model, RemisionBean entidad,  String path, HttpServletRequest request,  PageableSG pageable) {
		path = "operacion/remision/rem_nuevo";
		RemisionBean entidadOriginal 		= null;
		FacturaAsociadaBean facturaAsociada	= new FacturaAsociadaBean();
		TblComprobante comprobante			= null;
		try{
			log.debug("[asociarFactura] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			//Obtenemos las cantidades
			for(int indice=0; indice < entidad.getListaDetalle().size(); indice++) {
				entidadOriginal.getListaDetalle().get(indice).setCantidad(entidad.getListaDetalle().get(indice).getCantidad());
			}
			//obtenerDetalleRemision(entidadOriginal, request);
			if (entidadOriginal.getListaFacturaAsociada()==null) {
				log.debug("[asociarFactura] Inicializamos la primera vez");
				entidadOriginal.setListaFacturaAsociada(new ArrayList<>());
			}else {
				log.debug("[asociarFactura] Tamaño de la lista: "+entidadOriginal.getListaFacturaAsociada().size());
			}
			comprobante = entidadOriginal.getComprobante();
			if (!entidadOriginal.getListaDetalle().isEmpty()) {
				for(TblDetalleComprobante detalleComprobante: entidadOriginal.getListaDetalle()) {
					if (detalleComprobante.getCantidad().compareTo(new BigDecimal("0"))>0) {
						facturaAsociada = new FacturaAsociadaBean();
						facturaAsociada.setSerieFactura(comprobante.getSerie());
						facturaAsociada.setNumeroFactura(comprobante.getNumero());
						facturaAsociada.setCodigoComprobante(comprobante.getCodigoComprobante());
						facturaAsociada.setNombreCliente(comprobante.getNombreCliente());
						facturaAsociada.setDescripcion(detalleComprobante.getDescripcion());
						facturaAsociada.setUnidadMedida(detalleComprobante.getUnidadMedida());
						facturaAsociada.setCantidad(detalleComprobante.getCantidad());
						entidadOriginal.getListaFacturaAsociada().add(facturaAsociada);
						}
				}
				log.debug("[asociarFactura] Luego...Tamaño de la lista: "+entidadOriginal.getListaFacturaAsociada().size());
			}
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
			model.addAttribute("entidad", entidadOriginal);
			request.getSession().setAttribute("guiaRemisionSession", entidadOriginal);
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
		for(ComprobanteBean comprobante:entidadOriginal.getListaComprobante()) {
			indexComprobante++;
			String numeroFacturaParaRegistro = comprobante.getComprobante().getNumero();
			if (numeroFactura.equals(numeroFacturaParaRegistro)){
				int indexDetalle = -1;
				for(TblDetalleComprobante detalle: comprobante.getListaDetalle()) {
					indexDetalle++;
					if (detalle.getDescripcion().equals(nombreProducto)) {
						comprobante.getListaDetalle().remove(indexDetalle);
					}
				}
				if (comprobante.getListaDetalle().size()==0) {
					entidadOriginal.getListaComprobante().remove(indexComprobante);
				}
			}
		}
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
			//Reversion de las cantidades
			List<TblDetalleRemision> listaDetalleRemision = detalleRemisionDao.findAllxIdFacturaAsociada(id);
			for(TblDetalleRemision detalle: listaDetalleRemision) {
				TblDetalleComprobante detComprobante = detalleComprobanteDao.findOne(detalle.getCodigoDetalleComprobante());
				detComprobante.setCantidadGuia(detComprobante.getCantidadGuia().subtract(detalle.getCantidad()));
				detComprobante.setAuditoriaModificacion(request);
				detComprobante = detalleComprobanteDao.save(detComprobante);
				log.debug("[eliminarRemision] Revertido cantidad del detalle de comprobante:"+detComprobante.getCodigoDetalle());
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

	@RequestMapping(value = "/operacion/remision/nuevo/guardar", method = RequestMethod.POST)
	public String guardarRemision(Model model, RemisionBean entidad,  String path, HttpServletRequest request,  PageableSG pageable) {
		path = "operacion/remision/rem_listado";
		RemisionBean entidadOriginal = null;
		try{
			log.debug("[guardarRemision] Inicio");
			entidadOriginal = (RemisionBean)request.getSession().getAttribute("guiaRemisionSession");
			actualizarFaltantes(entidadOriginal,entidad,request);
			//obtener serie
			obtenerSerieRemision(entidadOriginal, request);
			if (okDatosRemision(entidadOriginal, model)) {
				entidadOriginal.getRemision().setAuditoriaCreacion(request);
				//entidadOriginal.getRemision();//TODO:ERROR.setCodigoComprobante(entidadOriginal.getComprobante().getCodigoComprobante());
				TblRemision remision = remisionDao.save(entidadOriginal.getRemision());
				log.debug("[guardarRemision] remision registrado:"+remision.getCodigoRemision());
				log.debug("[guardarRemision] Lista ComprobanteBean tamaño:"+entidadOriginal.getListaComprobante().size());
				for(ComprobanteBean comprobanteBean: entidadOriginal.getListaComprobante()) {
					TblFacturaAsociada facturaAsociada = new TblFacturaAsociada();
					facturaAsociada.setTblRemision(remision);
					facturaAsociada.setPesoTotal(new BigDecimal("5")); //TODO: PENDIENTE EL PESO
					facturaAsociada.setCodigoComprobante(comprobanteBean.getComprobante().getCodigoComprobante());
					TblFacturaAsociada tblFacturaAsociada = facturaAsociadaDao.save(facturaAsociada);
					log.debug("[guardarRemision] tblFacturaAsociada registrada:"+tblFacturaAsociada.getCodigoFacturaAsociada());
					List<TblDetalleRemision> listaDetalleRemision = obtenerDetalleRemision(comprobanteBean.getListaDetalle(), request);
					for(TblDetalleRemision detRemision : listaDetalleRemision) {
						detRemision.setTblFacturaAsociada(tblFacturaAsociada);
						TblDetalleRemision tblDetalleRemision = detalleRemisionDao.save(detRemision);
						log.debug("[guardarRemision] detalle remision registrado:"+tblDetalleRemision.getCodigoDetalleRemision());
					}
				}

				//actualizar en el detalle de comprobante las cantidades de la guia
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
	@RequestMapping(value = "/operacion/remision/ver/{id}", method = RequestMethod.GET)
	public String verRemision(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 							= null;
		String path										= null;
		TblRemision remision 							= null;
		List<TblFacturaAsociada> listaFacturaAsociada 	= null;
		List<ComprobanteBean> listaComprobante			= null;
		ComprobanteBean comprobanteBean					= null;
		List<FacturaAsociadaBean> listaFacAsoVer		= null;
		FacturaAsociadaBean facturaBean					= null;
		List<TblDetalleRemision> listaDetRemision		= null;
		List<TblDetalleComprobante> listaDetComprobante	= null;
		Map<Integer,TblDetalleComprobante> mapDetComprobante = new HashMap<>();
		try{
			log.debug("[verRemision] Inicio");
			//Leer los comprobantes - detalle y armar la presentación
			entidad = new RemisionBean();
			path = "operacion/remision/rem_ver";
			remision = remisionDao.findOne(id);
			listaFacturaAsociada = facturaAsociadaDao.findAllxIdRemision(remision.getCodigoRemision());
			if (listaFacturaAsociada!=null) {
				listaComprobante = new ArrayList<>();
				for(TblFacturaAsociada factura: listaFacturaAsociada) {
					comprobanteBean = new ComprobanteBean();
					comprobanteBean.setComprobante(comprobanteDao.findOne(factura.getCodigoComprobante()));
					listaDetRemision = detalleRemisionDao.findAllxIdFacturaAsociada(factura.getCodigoFacturaAsociada());
					listaDetComprobante = detalleComprobanteDao.listarxComprobante(factura.getCodigoComprobante());
					for(TblDetalleComprobante detComprobante: listaDetComprobante) {
						mapDetComprobante.put(detComprobante.getCodigoDetalle(), detComprobante);
					}
					comprobanteBean.setListaDetalle(new ArrayList<>());
					for(TblDetalleRemision detRemision: listaDetRemision) {
						comprobanteBean.getListaDetalle().add(mapDetComprobante.get(detRemision.getCodigoDetalleComprobante()));
					}
					
					listaComprobante.add(comprobanteBean);
				}
			}
			//Visualizamos la información como una lista
			if (listaComprobante != null) {
				listaFacAsoVer = new ArrayList<>();
				for(ComprobanteBean comprobante: listaComprobante) {
					for(TblDetalleComprobante detalleComprobante: comprobante.getListaDetalle()) {
						facturaBean = new FacturaAsociadaBean();
						facturaBean.setSerieFactura(comprobante.getComprobante().getSerie());
						facturaBean.setNumeroFactura(comprobante.getComprobante().getNumero());
						facturaBean.setCodigoComprobante(comprobante.getComprobante().getCodigoComprobante());
						facturaBean.setNombreCliente(comprobante.getComprobante().getNombreCliente());
						facturaBean.setDescripcion(detalleComprobante.getDescripcion());
						facturaBean.setUnidadMedida(detalleComprobante.getUnidadMedida());
						facturaBean.setCantidad(detalleComprobante.getCantidad());
						listaFacAsoVer.add(facturaBean);
					}
				}
			}
			entidad.setRemision(remision);
			entidad.setListaFacturaAsociada(listaFacAsoVer);
			model.addAttribute("entidad", entidad);
			log.debug("[verRemision] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}
	/*** Retorna a la consulta de facturas ***/
	/**
	 * entidad							: Contiene toda la info de la remision [RemisionBean]	--> guiaRemisionSession
	 * filtro							: Filtro de la consulta de Factura						--> sessionFiltroCriterio
	 * registros						: Datos de los comprobantes [TblComprobante]			--> ListadoConsultaFactura
	 * page								: Paginado - page										--> pageConsultaFactura
	 * 
	 */
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
	/**
	 * guiaRemisionSession				: Contiene toda la info de la remision [RemisionBean]
	 * 
	 */
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
	/**
	 * filtro							: Filtro de la pagina de Inicio							--> sessionFiltroRemisionCriterio
	 * page								: Paginado - page										--> PageRemisionPrincipal
	 * registros						: Listado de Remision [TblRemision]						--> ListadoConsultaRemision
	 * 
	 */
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
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/xml/{id}", method = RequestMethod.GET)
	public String obtenerXMLGuia(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 				= null;
		String path							= null;
		try{
			log.debug("[obtenerXMLGuia] Inicio");
			path = "operacion/remision/rem_listado";
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
			Map<String, String> mapDomicilioFiscalSession = (Map<String, String>)request.getSession().getAttribute("SessionMapDomicilioFiscalDescripcion");
			String direccionFiscal = mapDomicilioFiscalSession.get(entidad.getRemision().getCodigoDomicilioPartida());
			log.debug("[obtenerXMLGuia] direccionFiscal:"+direccionFiscal);
			entidad.setDireccionPartida(direccionFiscal);
			entidad.setPesoBruto(new BigDecimal("1"));
			//Llamada a la generación XML
			GuiaRemision.generarGuiaRemisionXML(entidad);
			//Guardamos el nombre
			remision.setEstadoOperacion(Constantes.ESTADO_XML_GENERADO);
			remision.setObservacion(entidad.getNombreArchivoXML());
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
		}
		return path;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/operacion/remision/descargar/xml/{id}", method = RequestMethod.GET)
	public ResponseEntity descargarXMLGuia(@PathVariable Integer id, Model model, HttpServletRequest request)  {
		byte[] content = null;
		HttpHeaders headers = null;
		try {
			List<TblRemision> lista = (List<TblRemision>)request.getSession().getAttribute("ListadoConsultaRemision");
			String filePath = ((TblRemision)lista.get(id)).getObservacion();
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
			TblComprobante comprobante = comprobanteDao.findOne(1);//TODO:ERROR(remision.getCodigoComprobante());
			List<TblDetalleRemision> listaDetalleRemision = detalleRemisionDao.findAllxIdFacturaAsociada(id);
			entidad.setRemision(remision);
			entidad.setComprobante(comprobante);
			entidad.setListaDetalleRemision(listaDetalleRemision);
			//Datos adicionales para la guia XML
			TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
			entidad.setRuc(usuario.getTblEmpresa().getRuc());
			entidad.setRazonSocial(usuario.getTblEmpresa().getRazonSocial());
			Map<String, String> mapTipoMotivoTraslado = (Map<String, String>)request.getSession().getAttribute("SessionMapMotivoTrasladoDescripcion");
			String descripcionMotivo = mapTipoMotivoTraslado.get(entidad.getRemision().getMotivoTraslado());
			log.info("[pdfComprobante] descripcionMotivo:"+descripcionMotivo +" codigo:"+entidad.getRemision().getMotivoTraslado());
			entidad.setDescripcionMotivo(descripcionMotivo);
			Map<String, String> mapDomicilioFiscalSession = (Map<String, String>)request.getSession().getAttribute("SessionMapDomicilioFiscalDescripcion");
			String direccionFiscal = mapDomicilioFiscalSession.get(entidad.getRemision().getCodigoDomicilioPartida());
			entidad.setDireccionPartida(direccionFiscal);
			entidad.setPesoBruto(new BigDecimal("1"));
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
	@RequestMapping(value = "/operacion/remision/envio/xml/{id}", method = RequestMethod.GET)
	public String envioXMLGuia(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 				= null;
		String path							= null;
		try{
			log.debug("[envioXMLGuia] Inicio");
			path = "operacion/remision/rem_listado";
			entidad = new RemisionBean();
			TblRemision remision = remisionDao.findOne(id);
			String accessToken = obtenerTokenGuiaRemision(remision,entidad);
			String ticket = obtenerTicketGuia(accessToken, remision,entidad);
			String status = obtenerEstadoGuia(accessToken, ticket,entidad);
			log.debug("[envioXMLGuia] status:"+status);
			if (status != null) {
				//Guardamos el ticket
				remision.setTicket(ticket);
				remision.setEstadoOperacion(Constantes.ESTADO_XML_GENERADO);
				remisionDao.save(remision);
				model.addAttribute("respuesta", "Envio Realizado:"+status);
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
		}
		return path;
	}
	private  String obtenerTokenGuiaRemision(TblRemision remision,RemisionBean entidad) throws Exception {
		String accessToken = null;
		try { 
			log.info("[obtenerTokenGuiaRemision] Inicio");
			/*Se establece el cliente POST para el servidor de autenticación */				   
			String resource = "https://gre-test.nubefact.com/v1/clientessol/test-85e5b0ae-255c-4891-a595-0b98c65c9854/oauth2/token"; 
			CloseableHttpClient httpclient = HttpClients.createDefault();
			//HttpClient httpclient = httpClientSecury();
			HttpPost httpPost = new HttpPost(resource);

			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

			/*Se agregan los datos de autenticación para la plataforma Efact OSE*/
			List<NameValuePair> params = new ArrayList<>(); 
			params.add(new BasicNameValuePair("grant_type", "password")); 
			params.add(new BasicNameValuePair("client_id", "test-85e5b0ae-255c-4891-a595-0b98c65c9854")); 
			params.add(new BasicNameValuePair("client_secret", "test-Hty/M6QshYvPgItX2P0+Kw=="));
			params.add(new BasicNameValuePair("username", "20602620337MODDATOS")); 
			params.add(new BasicNameValuePair("password", "MODDATOS")); 
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
			File file = new File(remision.getObservacion());
			String nombreSinExtension = file.getName().substring(0,file.getName().indexOf("."));

			String resource = "https://gre-test.nubefact.com/v1/contribuyente/gem/comprobantes/"+nombreSinExtension; 
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
	private  String obtenerEstadoGuia(String accessToken, String ticket,RemisionBean entidad) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		JsonNode nameNode = null;
		/*Se establece el cliente GET para el servidor de autenticación*/
		HttpGet httpget = new HttpGet("https://gre-test.nubefact.com/v1/contribuyente/gem/comprobantes/envios/" + ticket);
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
	private void actualizarFaltantes(RemisionBean entidadOriginal, RemisionBean entidad, HttpServletRequest request) {
		if (!entidad.getDatosTransporte().equals("-1")) {
			String[] arrTransporte = entidad.getDatosTransporte().split(":");
			entidadOriginal.getRemision().setPlaca(arrTransporte[3]);
			entidadOriginal.getRemision().setMarca(arrTransporte[2]);
			entidadOriginal.getRemision().setNumeroCertInscripcion(arrTransporte[4]);
			entidadOriginal.getRemision().setNumeroLicencia(arrTransporte[5]);
			entidadOriginal.getRemision().setNombreTransportista(arrTransporte[1]);
			entidadOriginal.getRemision().setNumeroDocumentoTransportista(arrTransporte[0]);
			entidadOriginal.getRemision().setTipoDocumentoTransportista("RUC");
			entidadOriginal.getRemision().setEstadoOperacion("00");
		}
		TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
		entidadOriginal.getRemision().setCodigoEntidad(usuario.getTblEmpresa().getCodigoEntidad());
		entidadOriginal.getRemision().setTipoDocumentoCliente(entidadOriginal.getComprobante().getTipoDocumento());
		entidadOriginal.getRemision().setNumeroDocumentoCliente(entidadOriginal.getComprobante().getNumeroDocumento());
		entidadOriginal.getRemision().setNombreCliente(entidadOriginal.getComprobante().getNombreCliente());
		

	}

	private List<TblDetalleRemision> obtenerDetalleRemision(List<TblDetalleComprobante> listDetalleComprobante, HttpServletRequest request) {
		TblDetalleRemision detRemision = null;
		List<TblDetalleRemision> listaDetalleRemision = new ArrayList<>();
		for(TblDetalleComprobante detComprobante:listDetalleComprobante) {
			if (detComprobante.getCantidad().compareTo(new BigDecimal("0"))>0) {
				detRemision = new TblDetalleRemision();
				detRemision.setDescripcion(detComprobante.getDescripcion());
				detRemision.setCantidad(detComprobante.getCantidad());
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
		entidad.getRemision().setCodigoDomicilioPartida(Constantes.CODIGO_DOMICILIO_FISCAL);
	}
	private boolean okDatosRemision(RemisionBean entidad, Model model) {
		boolean exitoso = true;
		if (entidad.getRemision().getMotivoTraslado().equals("-1")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe seleccionar el motivo del traslado");
			return exitoso;
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
		return exitoso;
	}
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
}
