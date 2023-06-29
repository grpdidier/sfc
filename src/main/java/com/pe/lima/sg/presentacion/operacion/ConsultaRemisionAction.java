package com.pe.lima.sg.presentacion.operacion;

import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conCodigoEmpresaRemision;
import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conEstadoRemision;
import static com.pe.lima.sg.dao.operacion.RemisionSpecifications.conNumeroRemision;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
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

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.bean.remision.ComprobanteBean;
import com.pe.lima.sg.bean.remision.FacturaAsociadaBean;
import com.pe.lima.sg.bean.remision.RemisionBean;
import com.pe.lima.sg.dao.operacion.IComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleRemisionDAO;
import com.pe.lima.sg.dao.operacion.IFacturaAsociadaDAO;
import com.pe.lima.sg.dao.operacion.IRemisionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
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
import com.pe.lima.sg.util.remision.util.GuiaRemisionService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class ConsultaRemisionAction {

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
	private GuiaRemisionService guiaRemisionService;


	private String urlPaginadoRemision = "/operacion/remision/consulta/principal/paginado/"; 

	@RequestMapping(value = "/operacion/remision/consulta", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		try{
			log.debug("[traerRegistros] Inicio");
			path = "operacion/consulta/rem_consulta_listado";
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

	@RequestMapping(value = "/operacion/remision/consulta/q", method = RequestMethod.POST)
	public String traerRegistrosFiltradosRemision(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/consulta/rem_consulta_listado";
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


	@RequestMapping(value = "/operacion/remision/consulta/ver/{id}", method = RequestMethod.GET)
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
			path = "operacion/consulta/rem_consulta_ver";
			remision = remisionDao.findOne(id);
			listaFacturaAsociada = facturaAsociadaDao.findAllxIdRemision(remision.getCodigoRemision());
			if (listaFacturaAsociada!=null) {
				listaComprobante = new ArrayList<>();
				for(TblFacturaAsociada factura: listaFacturaAsociada) {
					comprobanteBean = new ComprobanteBean();
					comprobanteBean.setComprobante(comprobanteDao.findOne(factura.getCodigoComprobante()));
					listaDetRemision = detalleRemisionDao.findAllxIdFacturaAsociada(factura.getCodigoFacturaAsociada());
					listaDetComprobante = detalleComprobanteDao.listarxComprobanteTodos(factura.getCodigoComprobante());
					for(TblDetalleComprobante detComprobante: listaDetComprobante) {
						mapDetComprobante.put(detComprobante.getCodigoDetalle(), detComprobante);
					}
					comprobanteBean.setListaDetalle(new ArrayList<>());
					for(TblDetalleRemision detRemision: listaDetRemision) {
						TblDetalleComprobante detalleComprobante = mapDetComprobante.get(detRemision.getCodigoDetalleComprobante());
						detalleComprobante.setCantidad(detRemision.getCantidad());
						comprobanteBean.getListaDetalle().add(detalleComprobante);
						
					}
					
					listaComprobante.add(comprobanteBean);
				}
			}
			//Visualizamos la información como una lista
			if (listaComprobante != null) {
				listaFacAsoVer = new ArrayList<>();
				for(ComprobanteBean comprobante: listaComprobante) {
					if (comprobante.getListaDetalle()!=null) {
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

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/remision/consulta/xml/{id}", method = RequestMethod.GET)
	public String obtenerXMLGuia(@PathVariable Integer id, Model model, HttpServletRequest request) {
		RemisionBean entidad 				= null;
		String path							= null;
		try{
			log.debug("[obtenerXMLGuia] Inicio");
			path = "operacion/consulta/rem_consulta_listado";
			String rutaXml = obtenerRutaDirectorioXML(request);
			entidad = new RemisionBean();
			TblRemision remision = remisionDao.findOne(id);
			List<TblFacturaAsociada> listaFacturaAsociada = facturaAsociadaDao.findAllxIdRemision(remision.getCodigoRemision());
			entidad.setListaTblComprobante(new ArrayList<>());
			for(TblFacturaAsociada asociada: listaFacturaAsociada) {
				TblComprobante comprobante = comprobanteDao.findOne(asociada.getCodigoComprobante());
				entidad.getListaTblComprobante().add(comprobante);
			}
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
			entidad.setPesoBruto(obtenerPesoBruto(listaFacturaAsociada));
			//Llamada a la generación XML
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
	@RequestMapping(value = "/operacion/remision/consulta/descargar/xml/{id}", method = RequestMethod.GET)
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
	@RequestMapping(value = "/operacion/remision/consulta/descargar/cdr/{id}", method = RequestMethod.GET)
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
	@RequestMapping(value = "/operacion/remision/consulta/pdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
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
	@RequestMapping(value = "/operacion/remision/consulta/principal/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarPrincipalEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			log.debug("[paginarPrincipalEntidad] Inicio");
			path = "operacion/consulta/rem_consulta_listado";
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
	
	/*Retorna a la pantalla de guias de remision*/
	@RequestMapping(value = "/operacion/consulta/regresar", method = RequestMethod.GET)
	/**
	 * filtro							: Filtro de la pagina de Inicio							--> sessionFiltroRemisionCriterio
	 * page								: Paginado - page										--> PageRemisionPrincipal
	 * registros						: Listado de Remision [TblRemision]						--> ListadoConsultaRemision
	 * 
	 */
	public String regresarRemision(Model model, Filtro filtro, String path, HttpServletRequest request) {
		try{
			log.debug("[regresarRemision] Inicio");
			path = "operacion/consulta/rem_consulta_listado";
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
