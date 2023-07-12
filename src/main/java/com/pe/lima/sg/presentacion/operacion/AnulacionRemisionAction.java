package com.pe.lima.sg.presentacion.operacion;

import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conCodigoEmpresaRemision;
import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conEstadoRemision;
import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conNumeroRemision;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.bean.remision.FacturaAsociadaBean;
import com.pe.lima.sg.bean.remision.RemisionBean;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleRemisionDAO;
import com.pe.lima.sg.dao.operacion.IFacturaAsociadaDAO;
import com.pe.lima.sg.dao.operacion.IRemisionDAO;
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

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class AnulacionRemisionAction {
	
	@Autowired
	private IDetalleComprobanteDAO detalleComprobanteDao;
	@Autowired
	private IRemisionDAO remisionDao;
	@Autowired
	private IDetalleRemisionDAO detalleRemisionDao;
	@Autowired
	private IFacturaAsociadaDAO facturaAsociadaDao;
	
	private String urlPaginadoRemision = "/operacion/anulacion/remision/principal/paginado/"; 

	@RequestMapping(value = "/operacion/anulacion/remision", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		try{
			log.debug("[traerRegistros] Inicio");
			path = "operacion/anulacion/anu_rem_listado";
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

	@RequestMapping(value = "/operacion/anulacion/remision/q", method = RequestMethod.POST)
	public String traerRegistrosFiltradosRemision(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/anulacion/anu_rem_listado";
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

	
	@RequestMapping(value = "operacion/anulacion/remision/all/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarRemision(@PathVariable Integer id, Model model, HttpServletRequest request,  PageableSG pageable) {
		String path = "operacion/anulacion/anu_rem_listado";
		try{
			log.debug("[eliminarRemision] Inicio");
			//Eliminacion de la remision
			TblRemision remision = remisionDao.findOne(id);
			//remision.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			remision.setAuditoriaModificacion(request);
			remision.setEstadoOperacion(Constantes.ESTADO_XML_ANULADO);
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
			//decrementarNumeroSerie(request);
			Filtro filtro = (Filtro)request.getSession().getAttribute("sessionFiltroRemisionCriterio");
			this.listarRemision(model, filtro, pageable, urlPaginadoRemision, request);
			model.addAttribute("filtro", filtro);

			log.debug("[eliminarRemision] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}
	


	@RequestMapping(value = "/operacion/anulacion/remision/ver/{id}", method = RequestMethod.GET)
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
			path = "operacion/anulacion/anu_rem_ver";
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

	
	private BigDecimal obtenerPesoBruto(List<TblFacturaAsociada> listaFacturaAsociada) {
		BigDecimal totalPeso = new BigDecimal("0");
		for(TblFacturaAsociada factura: listaFacturaAsociada) {
			totalPeso = totalPeso.add(factura.getPesoTotal());
		}
		return totalPeso;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/operacion/anulacion/remision/descargar/xml/{id}", method = RequestMethod.GET)
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
	@RequestMapping(value = "/operacion/anulacion/remision/descargar/cdr/{id}", method = RequestMethod.GET)
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
	@RequestMapping(value = "/operacion/anulacion/remision/pdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> pdfComprobante(@PathVariable Integer id, HttpServletRequest request) {
		RemisionBean entidad 						= null;
		HttpHeaders headers 						= new HttpHeaders();
		ByteArrayInputStream bis 					= null;
		GuiaRemisionPdf guiaPdf						= new GuiaRemisionPdf();

		try{
			log.info("[pdfComprobante] Inicio");
			entidad = new RemisionBean();
			TblRemision remision = remisionDao.findOne(id);
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
	
	
	/*********************************************************************************************************************************/
	/*Sección de Paginado
	/*********************************************************************************************************************************/
	
	@RequestMapping(value = "/operacion/anulacion/remision/principal/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarPrincipalEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			log.debug("[paginarPrincipalEntidad] Inicio");
			path = "operacion/anulacion/anu_rem_listado";
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
	
	/*Retorna a la pantalla de guias de remision*/
	@RequestMapping(value = "/operacion/anulacion/regresar", method = RequestMethod.GET)
	public String regresarRemision(Model model, Filtro filtro, String path, HttpServletRequest request) {
		try{
			log.debug("[regresarRemision] Inicio");
			path = "operacion/anulacion/anu_rem_listado";
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
	
}
