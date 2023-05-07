package com.pe.lima.sg.presentacion.operacion;


import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conEstado;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conNumero;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conSerie;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conTipoComprobante;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conCodigoEmpresa;


import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conEstadoCliente;
import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conNombreCliente;
import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conNumeroDocumento;


import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conEstadoProducto;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conCodigoProducto;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conNombreProducto;



import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.pe.lima.sg.bean.facturador.BandejaFacturadorBean;
import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.dao.mantenimiento.ICatalogoDAO;
import com.pe.lima.sg.dao.mantenimiento.IClienteDAO;
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.dao.mantenimiento.IProductoDAO;
import com.pe.lima.sg.dao.operacion.IBandejaFacturadorDAO;
import com.pe.lima.sg.dao.operacion.IComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.ILeyendaDAO;
import com.pe.lima.sg.dao.operacion.ISunatCabeceraDAO;
import com.pe.lima.sg.dao.operacion.ISunatDetalleDAO;
import com.pe.lima.sg.db.util.IOperacionFacturador;
import com.pe.lima.sg.db.util.OperacionFacturadorImp;
import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
import com.pe.lima.sg.entity.mantenimiento.TblCliente;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturador;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblLeyenda;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.entity.operacion.TblSunatCabecera;
import com.pe.lima.sg.entity.operacion.TblSunatDetalle;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.pdf.ComprobanteKenorPdf;
//import com.pe.lima.sg.presentacion.pdf.ComprobantePdf;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.NumberToLetterConverter;
import com.pe.lima.sg.presentacion.util.PageWrapper;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;

/**
 * Clase que se encarga de la administracion de los comprobantes
 *
 * 			
 */
@Controller
public class ComprobanteAction extends BaseOperacionPresentacion<TblComprobante> {

	@Autowired
	private IComprobanteDAO comprobanteDao;

	@Autowired
	private IDetalleComprobanteDAO detalleComprobanteDao;

	@Autowired
	private ISunatCabeceraDAO sunatCabeceraDao;
	
	@Autowired
	private ISunatDetalleDAO sunatDetalleDao;
	
	@Autowired
	private IClienteDAO clienteDao;

	@Autowired
	private IProductoDAO productoDao;

	@Autowired
	private ILeyendaDAO leyendaDao;
	
	@Autowired
	private IBandejaFacturadorDAO bandejaFacturadorDao;

	@Autowired
	private ICatalogoDAO catalogoDao;
	
	@Autowired
	private IEmpresaDAO empresaDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ComprobanteAction.class);
	
	/*@Autowired
	private IOperacionFacturador operacionFacturador;*/

	private String urlPaginado = "/operacion/comprobantes/paginado/"; 
	
	@Override
	public BaseOperacionDAO<TblComprobante, Integer> getDao() {
		return comprobanteDao;
	}	


	/**
	 * Se encarga de listar todos los arbitrios x tienda
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/comprobantes", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/comprobante/com_listado";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setNumero("");;
			this.listarComprobante(model, filtro, pageable, this.urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroCriterio", filtro);
			LOGGER.debug("[traerRegistros] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistros] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}
	
	/**
	 * Se encarga de buscar la informacion del Concepto segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/comprobantes/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/comprobante/com_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarComprobante(model, filtro, pageable, urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroCriterio", filtro);
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Comprobante ***/
	private void listarComprobante(Model model, Filtro filtro,  PageableSG pageable, String url, HttpServletRequest request){
		List<TblComprobante> entidades = new ArrayList<TblComprobante>();
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "numero"));
		Integer codigoEntidad	= null;
		try{
			
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			
			Specification<TblComprobante> criterio = Specifications.where(conNumero(filtro.getNumero()))
					.and(conSerie(filtro.getSerie()))
					.and(conCodigoEmpresa(codigoEntidad))
					.and(conTipoComprobante(filtro.getTipoComprobante()))
					.and(conEstado(Constantes.ESTADO_REGISTRO_ACTIVO));
			pageable.setSort(sort);
			//entidades = comprobanteDao.findAll(criterio);
			Page<TblComprobante> entidadPage = comprobanteDao.findAll(criterio, pageable);
			PageWrapper<TblComprobante> page = new PageWrapper<TblComprobante>(entidadPage, url, pageable);
			model.addAttribute("registros", page.getContent());
			model.addAttribute("page", page);
			LOGGER.debug("[listarComprobante] entidades:"+entidades);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}


	
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Comprobante
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/comprobantes/nuevo", method = RequestMethod.GET)
	public String crearComprobante(Model model, HttpServletRequest request) {
		Filtro filtro 							= null;
		Map<String, TblParametro> mapParametro	= null;
		//TblParametro parametro =null;
		try{
			LOGGER.debug("[crearComprobante] Inicio");
			filtro = new Filtro();
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			this.asignarParametros(filtro, mapParametro,request);
			model.addAttribute("filtro", filtro);
			request.getSession().setAttribute("listaDetalleSession", new ArrayList<TblDetalleComprobante>());
			LOGGER.debug("[crearComprobante] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			 mapParametro			= null;
			 filtro 				= null;
		}
		return "operacion/comprobante/com_nuevo";
	}
	
	private void asignarParametros(Filtro entidad, Map<String, TblParametro> mapParametro, HttpServletRequest request){
		TblParametro parametro =null;
		try{
			entidad.setComprobante(new TblComprobante());
			entidad.setDetalleComprobante(new TblDetalleComprobante());
			if (mapParametro!=null){
				//IGV
				parametro = mapParametro.get(Constantes.PARAMETRO_IGV);
				if (parametro!=null){
					entidad.setValorIGV(parametro.getCantidad());
					entidad.setNombreIGV(parametro.getDato());
				}else{
					entidad.setValorIGV(Constantes.SUNAT_IGV);
				}
				//SERVICIO
				parametro = mapParametro.get(Constantes.PARAMETRO_SERVICIO);
				if (parametro!=null){
					entidad.setValorServicio(parametro.getCantidad());
					entidad.setNombreServicio(parametro.getDato());
				}else{
					entidad.setValorServicio(Constantes.SUNAT_SERVICIO);
				}
				//Tipo de Operacion
				parametro = mapParametro.get(Constantes.PARAMETRO_TIPO_OPERACION);
				if (parametro!=null){
					entidad.getComprobante().setTipoOperacion(parametro.getDato());
				}else{
					entidad.getComprobante().setTipoOperacion("");
				}
				//Codigo el domicilio fiscal
				parametro = mapParametro.get(Constantes.PARAMETRO_CODIGO_DOMICILIO_FISCAL);
				if (parametro!=null){
					entidad.getComprobante().setCodigoDomicilio(parametro.getDato());
				}else{
					entidad.getComprobante().setCodigoDomicilio("");
				}
				//Tipo de comprobante
				parametro = mapParametro.get(Constantes.PARAMETRO_TIPO_COMPROBANTE);
				if (parametro!=null){
					entidad.getComprobante().setTipoComprobante(parametro.getDato());
				}else{
					entidad.getComprobante().setTipoComprobante("");
				}
				//Moneda
				parametro = mapParametro.get(Constantes.PARAMETRO_MONEDA);
				if (parametro!=null){
					entidad.getComprobante().setMoneda(parametro.getDato());
				}else{
					entidad.getComprobante().setMoneda("");
				}
				//Serie
				parametro = mapParametro.get(Constantes.PARAMETRO_SERIE);
				if (parametro!=null){
					entidad.getComprobante().setSerie(parametro.getDato());
				}else{
					entidad.getComprobante().setSerie("");
				}
				//Unidad medida
				parametro = mapParametro.get(Constantes.PARAMETRO_UNIDAD_MEDIDA);
				if (parametro!=null){
					entidad.getDetalleComprobante().setUnidadMedida(parametro.getDato());
				}else{
					entidad.getDetalleComprobante().setUnidadMedida("");
				}
				//afectacion igv
				parametro = mapParametro.get(Constantes.PARAMETRO_AFECTACION_IGV);
				if (parametro!=null){
					entidad.getDetalleComprobante().setTipoAfectacion(parametro.getDato());
				}else{
					entidad.getDetalleComprobante().setTipoAfectacion("");
				}
				//Ruta del repositorio Data
				parametro = mapParametro.get(Constantes.PARAMETRO_SUNAT_DATA);
				if (parametro!=null){
					entidad.setSunatData(parametro.getDato());
				}else{
					entidad.setSunatData(Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA);
				}
				//Ruta de la Base de Datos
				parametro = mapParametro.get(Constantes.PARAMETRO_SUNAT_BD);
				if (parametro!=null){
					entidad.setSunatBD(parametro.getDato());
				}else{
					entidad.setSunatBD(Constantes.SUNAT_FACTURADOR_DB_LOCAL);
				}
				
				//RUC de la empresa
				/*parametro = mapParametro.get(Constantes.PARAMETRO_RUC_EMPRESA);
				if (parametro!=null){
					Constantes.SUNAT_RUC_EMISOR = parametro.getDato();
				}else{
					Constantes.SUNAT_RUC_EMISOR = "SIN - RUC";
				}*/
				//entidad.setRuc(Constantes.SUNAT_RUC_EMISOR);
				entidad.setRuc(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getRuc());
				
				//SERIE DE LA FACTURA
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void preGuardar(TblComprobante entidad, HttpServletRequest request) {
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			LOGGER.debug("[preGuardar] Inicio" );
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardar] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			LOGGER.debug("[preGuardar] idUsuario:"+idUsuario );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(idUsuario);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			entidad.setTblEmpresa(empresaDao.findOne(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ));
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}
	}
	/*
	 * Campos de auditoria
	 */
	public void preGuardarListado(Filtro filtro, HttpServletRequest request){
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardar] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			this.preGuardar(filtro.getComprobante(), request);
			for(TblDetalleComprobante detalle: filtro.getListaDetalle()){
				detalle.setFechaCreacion(new Date(System.currentTimeMillis()));
				detalle.setIpCreacion(request.getRemoteAddr());
				detalle.setUsuarioCreacion(idUsuario);
				detalle.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}
	}
	/*
	 * Campos de auditoria
	 */
	public void preGuardarLeyenda(TblLeyenda entidad, HttpServletRequest request){
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardar] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(idUsuario);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}
	}
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblComprobante entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total	= null;
		Integer codigoEntidad = null;
		try{
			//Tipo de Operacion 
			if (entidad.getTipoOperacion() == null || entidad.getTipoOperacion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de operación");
				return exitoso;
			}
			//Codigo Domicilio Fiscal
			if (entidad.getCodigoDomicilio() == null || entidad.getCodigoDomicilio().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el domicilio fiscal o anexo");
				return exitoso;
			}
			//Fecha Emision
			if (entidad.getFechaEmision() == null || entidad.getFechaEmision().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la fecha de emisión");
				return exitoso;
			}
			//Tipo de Comprobante
			if (entidad.getTipoComprobante() == null || entidad.getTipoComprobante().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de comprobante");
				return exitoso;
			}
			//Serie
			if (entidad.getSerie() == null || entidad.getSerie().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la serie del comprobante");
				return exitoso;
			}
			//Numero
			if (entidad.getNumero() == null || entidad.getNumero().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el número del comprobante");
				return exitoso;
			}else{
				entidad.setNumero(UtilSGT.completarCeros(entidad.getNumero(), Constantes.SUNAT_LONGITUD_NUMERO));
			}
			//Validando existencia del comprobante
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			total = comprobanteDao.totalComprobante(entidad.getTipoComprobante(), entidad.getSerie(), entidad.getNumero(), codigoEntidad);
			if (total > 0){
				exitoso = false;
				//model.addAttribute("respuesta", "El número y la serie para el comprobante ingresado ya existe ["+entidad.getSerie()+"-"+entidad.getNumero()+"]");
				model.addAttribute("respuesta", "Se encontró un comprobante anteriormente registrado Tipo ["+entidad.getTipoComprobante()+"] - Serie ["+entidad.getSerie()+"] - Numero ["+entidad.getNumero()+"]");
				return exitoso;
			}
			//Moneda
			if (entidad.getMoneda() == null || entidad.getMoneda().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de moneda");
				return exitoso;
			}
			//Tipo Documento
			if (entidad.getTipoDocumento() == null || entidad.getTipoDocumento().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de documento del cliente");
				return exitoso;
			}
			//Numero Documento
			if (entidad.getNumeroDocumento()== null || entidad.getNumeroDocumento().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el numero del documento del cliente");
				return exitoso;
			}
			//Nombre o Razon Social
			if (entidad.getNombreCliente()== null || entidad.getNombreCliente().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el nombre o razón social del cliente");
				return exitoso;
			}

				
		}catch(Exception e){
			exitoso = false;
		}finally{
			codigoEntidad = null;
			total = null;
		}
		return exitoso;
	}
	/*
	 * Validacio del negocio
	 */
	public boolean validarNegocioListado(Model model, Filtro entidad, HttpServletRequest request){
		boolean exitoso = true;
		
		try{
			LOGGER.debug("[validarNegocioListado] Inicio");
			if (this.validarNegocio(model, entidad.getComprobante(), request)){
				if (entidad.getListaDetalle() == null || entidad.getListaDetalle().size() <= 0){
					exitoso = false;
					model.addAttribute("respuesta", "Debe ingresar el detalle del comprobante");
					return exitoso;
				}
				if (entidad.getLeyendaSunat().getCodigoSunat() != null && !entidad.getLeyendaSunat().getCodigoSunat().equals("")){
					if (entidad.getLeyendaSunat().getDescripcionSunat() == null || entidad.getLeyendaSunat().getDescripcionSunat().equals("")){
						exitoso = false;
						model.addAttribute("respuestaLeyenda", "Debe ingresar la descripción de la leyenda");
						return exitoso;
					}
					
				}
				
			}else{
				exitoso = false;
			}
			LOGGER.debug("[validarNegocioListado] Fin");
		}catch(Exception e){
			exitoso = false;
		}
		return exitoso;
	}
	
	
	@Override
	public TblComprobante getNuevaEntidad() {
		return new TblComprobante();
	}
	/**
	 * Se encarga de guardar la informacion del Comprobante
	 * 
	 * @param comprobanteBean
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/comprobantes/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, Filtro entidad, HttpServletRequest request) {
		String path = "operacion/comprobante/com_listado";
		boolean exitoso 						= false;
		TblComprobante comprobante				= null;
		TblSunatCabecera cabecera				= null;
		TblSunatDetalle detalleSunat			= null;
		List<TblSunatDetalle> listaDetalle		= new ArrayList<TblSunatDetalle>();
		List<TblDetalleComprobante> listaDetComp = null;
		String nombreLeyendaFile				= "";
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			listaDetComp  = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalle(listaDetComp);
			entidad.getComprobante().setFechaEmision( UtilSGT.getDateStringFormat(UtilSGT.getDatetoString(entidad.getFechaEmision())) );
			if (entidad.getFechaVencimiento()!=null && !entidad.getFechaVencimiento().equals("")){
				entidad.getComprobante().setFechaVencimiento(UtilSGT.getDateStringFormat(UtilSGT.getDatetoString(entidad.getFechaVencimiento())));
			}else{
				entidad.getComprobante().setFechaVencimiento("");
			}
			if (this.validarNegocioListado(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.calculoDetalleComprobante(entidad);
				this.calculoCabeceraComprobante(entidad);
				this.preGuardarListado(entidad, request);
				this.preGuardarLeyenda(entidad.getLeyendaSunat(), request);
				
				//entidad.getComprobante().setCodigoVerificacion(UtilSGT.getFecha("yyyy-MM-dd kk:mm:ss") + entidad.getComprobante().getSerie()+entidad.getComprobante().getNumero());
				entidad.getComprobante().setCodigoVerificacion(UUID.randomUUID().toString());
				
				//Guardar el comprobante
				exitoso = super.guardar(entidad.getComprobante(), model);
				//Buscar Comprobante
				if (exitoso){
					comprobante = comprobanteDao.obtenerComprobante(entidad.getComprobante().getCodigoVerificacion());
					for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
						LOGGER.debug("[guardarEntidad] unidad:" +detalle.getUnidadMedida());
						detalle.setTblComprobante(comprobante);
						detalleComprobanteDao.save(detalle);
						exitoso = true;
					}
					//Leyenda
					if(exitoso){
						nombreLeyendaFile = Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA;
						if (entidad.getLeyendaSunat()!=null && !entidad.getLeyendaSunat().getCodigoSunat().equals("")){
							entidad.getLeyendaSunat().setTblComprobante(comprobante);
							entidad.getLeyendaSunat().setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA);
							leyendaDao.save(entidad.getLeyendaSunat());
						}
						exitoso = true;
					}
					LOGGER.debug("[guardarEntidad] Inicio del registro de la cabecera Sunat:"+exitoso);
					if (exitoso){
						//Grabar Datos para la sunat
						cabecera = this.registrarCabeceraSunat(comprobante, request, entidad);
						LOGGER.debug("[guardarEntidad] cabecera:"+cabecera);
						if (cabecera !=null){
							LOGGER.debug("[guardarEntidad] Inicio del registro del detalle sunat.....:");
							for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
								LOGGER.debug("[guardarEntidad] Antes del detalle:"+detalle);
								detalleSunat = this.registrarDetalleSunat(cabecera, comprobante, detalle, request, entidad);
								LOGGER.debug("[guardarEntidad] Despues del registro.............: "+detalle);
								if (detalleSunat !=null){
									exitoso = true;
									listaDetalle.add(detalleSunat);
								}else{
									exitoso = false;
									break;
								}
							}
						}
						LOGGER.debug("[guardarEntidad] Inicio de la generacion de los archivos:"+exitoso);
						if (exitoso){
							if (this.generarArchivoCabecera(cabecera, entidad)){
								if (this.generarArchivoDetalle(listaDetalle, entidad)){
									if (this.generarArchivoLeyenda(entidad.getLeyendaSunat(),comprobante, nombreLeyendaFile, entidad)){
										//Generar adicional del detalle
										if (this.generarArchivoAdicionalDetalle(entidad)){
											model.addAttribute("respuesta", "Se generó el registro exitosamente");
										}else{
											model.addAttribute("respuesta", "Se generó un error en la creacion del detalle adicional [gratuita] del archivo SUNAT");
										}
										
									}else{
										model.addAttribute("respuesta", "Se generó un error en la creacion de la leyenda del archivo SUNAT");
									}
									
								}else{
									model.addAttribute("respuesta", "Se generó un error en la creacion del detalle del archivo SUNAT");
								}
							}else{
								model.addAttribute("respuesta", "Se generó un error en el regitro de la cabecera del archivo SUNAT");
							}
							
						}else{
							model.addAttribute("respuesta", "Se generó un error en el regitro del detalle de datos a la SUNAT");
							path = "operacion/comprobante/com_nuevo";
							model.addAttribute("filtro", entidad);
						}
						//Generar Archivo
						path = "operacion/comprobante/com_listado";
						this.listarComprobante(model, entidad,new PageableSG(), this.urlPaginado, request);
					}else{
						path = "operacion/comprobante/com_nuevo";
						model.addAttribute("filtro", entidad);
					}
				}
				
				
			}else{
				path = "operacion/comprobante/com_nuevo";
				model.addAttribute("filtro", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("respuesta", "Se generó un error :"+e.getMessage());
		}finally{
			comprobante				= null;
		}
		return path;
		
	}
	
	/*
	 * Registro de los datos de la cabecera para la sunat
	 */
	public TblSunatCabecera registrarCabeceraSunat(TblComprobante comprobante, HttpServletRequest request, Filtro entidad){
		TblSunatCabecera cabecera = null;
		try{
			cabecera = new TblSunatCabecera();
			//Tipo Operacion
			cabecera.setTipoOperacion(comprobante.getTipoOperacion());
			//Fecha Emision
			//cabecera.setFechaEmision(UtilSGT.getFecha("yyyy-MM-dd"));
			cabecera.setFechaEmision(comprobante.getFechaEmision());
			//Domicilio Fiscal
			cabecera.setDomicilioFiscal(new Integer(comprobante.getCodigoDomicilio()));
			//Datos del Cliente
			cabecera.setTipoDocumentoUsuario(comprobante.getTipoDocumento());
			cabecera.setNumeroDocumento(comprobante.getNumeroDocumento());
			cabecera.setRazonSocial(comprobante.getNombreCliente());
			//Moneda
			cabecera.setTipoMoneda(comprobante.getMoneda());
			//Descuentos Globales
			cabecera.setSumaDescuento(comprobante.getDescuentosGlobales());
			cabecera.setSumaCargo(comprobante.getTotalOtrosCargos());
			cabecera.setTotalDescuento(comprobante.getTotalDescuento());
			//Total valor de venta - Operaciones gravadas
			//cabecera.setOperacionGravada(this.obtenerTotalMontoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			//cabecera.setOperacionGravada(this.obtenerTotalMontoGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			cabecera.setOperacionGravada(comprobante.getTotalOpGravada());
			//Total valor de venta - Operaciones inafectas
			cabecera.setOperacionInafecta(comprobante.getTotalOpInafecta());
			//Total valor de venta - Operaciones exoneradas
			cabecera.setOperacionExonerada(comprobante.getTotalOpExonerada());
			//Sumatoria IGV
			//cabecera.setMontoIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			//cabecera.setMontoIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			cabecera.setMontoIgv(comprobante.getTotalIgv());
			//Sumatoria ISC
			cabecera.setMontoIsc(comprobante.getSumatoriaIsc());
			//Sumatoria otros tributos
			cabecera.setOtrosTributos(comprobante.getSumatorioaOtrosTributos());
			//Importe total de la venta, cesión en uso o del servicio prestado
			cabecera.setImporteTotal(comprobante.getTotalImporte());
			//Nombre archivo
			cabecera.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_CABECERA);
			this.preGuardarSunatCabecera(cabecera, request);
			//CxC Documento
			cabecera.setTblComprobante(comprobante);
			//Registro de la cabecera de la sunat
			sunatCabeceraDao.save(cabecera);
			cabecera = sunatCabeceraDao.findByCodigoDocumento(comprobante.getCodigoComprobante());
		}catch(Exception e){
			e.printStackTrace();
			cabecera = null;
		}
		return cabecera;
	}
	
	/*
	 * Registro de los datos del detalle para la sunat
	 */
	public TblSunatDetalle registrarDetalleSunat(TblSunatCabecera cabecera, TblComprobante comprobante, TblDetalleComprobante detalleComprobante, HttpServletRequest request, Filtro entidad){
		TblSunatDetalle detalle 				= null;
		try{
			if (comprobante.getTipoOperacion().equals(Constantes.SUNAT_TIPO_OPERACION_EXPORTACION)){
				detalle = this.calculoDetalleSunatExportacion(cabecera, comprobante, detalleComprobante, request, entidad);
			}else{
				detalle = this.calculoDetalleSunatNacional(cabecera, comprobante, detalleComprobante, request, entidad);	
			}
			
			sunatDetalleDao.save(detalle);
		}catch(Exception e){
			e.printStackTrace();
			detalle = null;
		}
		return detalle;
	}
	/*
	 * Calculo de venta Exportacion - Detalle Sunat
	 */
	private TblSunatDetalle calculoDetalleSunatExportacion(TblSunatCabecera cabecera, TblComprobante comprobante, TblDetalleComprobante detalleComprobante, HttpServletRequest request, Filtro entidad){
		TblSunatDetalle detalle 				= null;
		BigDecimal temporal						= null;
		try{
			//registro del detalle del comprobante
			detalle = new TblSunatDetalle();
			//Código de unidad de medida por ítem
			detalle.setCodigoUnidad(detalleComprobante.getUnidadMedida());
			//Cantidad de unidades por ítem
			detalle.setCantidad(detalleComprobante.getCantidad().toString());
			//Código de producto
			detalle.setCodigoProducto("");
			//Codigo producto SUNAT
			detalle.setCodigoProductoSunat("");
			//Descripción detallada del servicio prestado, bien vendido o cedido en uso, indicando las características.
			detalle.setDescripcion(detalleComprobante.getDescripcion());
			//Valor unitario por ítem
			//detalle.setValorUnitario(cabecera.getOperacionGravada().toString());
			//detalle.setValorUnitario(this.obtenerTotalMontoGravada(detalleComprobante.getPrecioTotal(), Constantes.SUNAT_IGV).toString());
			temporal = this.obtenerTotalMontoGravada(detalleComprobante.getPrecioFinal(), 0 , entidad.getValorServicio()); //IGV = 0
			temporal = temporal.divide(new BigDecimal(detalleComprobante.getCantidad().toString()), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			
			detalle.setValorUnitario(temporal.toString());
			//Descuentos por item
			BigDecimal descuento = new BigDecimal((detalleComprobante.getPrecioTotal().doubleValue()*detalleComprobante.getDescuento().doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
			detalle.setDescuento(descuento.toString());
			//Monto de IGV por ítem
			//detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), Constantes.SUNAT_IGV).toString());
			detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), 0 , entidad.getValorServicio()).toString()); //IGV = 0
			//Afectación al IGV por ítem
			detalle.setAfectacionIgv(detalleComprobante.getTipoAfectacion());
			//Monto de ISC por ítem
			detalle.setMontoIsc("0");
			//Tipo de sistema ISC
			detalle.setTipoIsc("");
			//Precio de venta unitario por item
			detalle.setPrecioVentaUnitario(detalleComprobante.getPrecioTotal().toString());
			//Valor de venta por ítem
			detalle.setValorVentaItem(detalleComprobante.getPrecioFinal().toString());
			//Archivo
			detalle.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_DETALLE);
			//Falta auditoria
			this.preGuardarSunatDetalle(detalle, request);
			detalle.setTblSunatCabecera(cabecera);
			sunatDetalleDao.save(detalle);
		}catch(Exception e){
			e.printStackTrace();
			detalle = null;
		}
		return detalle;
	}
	
	private TblSunatDetalle calculoDetalleSunatNacional(TblSunatCabecera cabecera, TblComprobante comprobante, TblDetalleComprobante detalleComprobante, HttpServletRequest request, Filtro entidad){
		TblSunatDetalle detalle 				= null;
		BigDecimal temporal						= null;
		try{
			//registro del detalle del comprobante
			detalle = new TblSunatDetalle();
			//Código de unidad de medida por ítem
			detalle.setCodigoUnidad(detalleComprobante.getUnidadMedida());
			//Cantidad de unidades por ítem
			detalle.setCantidad(detalleComprobante.getCantidad().toString());
			//Código de producto
			detalle.setCodigoProducto("");
			//Codigo producto SUNAT
			detalle.setCodigoProductoSunat("");
			//Descripción detallada del servicio prestado, bien vendido o cedido en uso, indicando las características.
			detalle.setDescripcion(detalleComprobante.getDescripcion());
			//Valor unitario por ítem
			//detalle.setValorUnitario(cabecera.getOperacionGravada().toString());
			//detalle.setValorUnitario(this.obtenerTotalMontoGravada(detalleComprobante.getPrecioTotal(), Constantes.SUNAT_IGV).toString());
			temporal = this.obtenerTotalMontoGravada(detalleComprobante.getPrecioFinal(), entidad.getValorIGV(), entidad.getValorServicio());
			temporal = temporal.divide(new BigDecimal(detalleComprobante.getCantidad().toString()) , 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			
			detalle.setValorUnitario(temporal.toString());
			//Descuentos por item
			BigDecimal descuento = new BigDecimal((detalleComprobante.getPrecioTotal().doubleValue()*detalleComprobante.getDescuento().doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
			detalle.setDescuento(descuento.toString());
			//Monto de IGV por ítem
			//detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), Constantes.SUNAT_IGV).toString());
			detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), entidad.getValorIGV(), entidad.getValorServicio()).toString());
			//Afectación al IGV por ítem
			detalle.setAfectacionIgv(detalleComprobante.getTipoAfectacion());
			//Monto de ISC por ítem
			detalle.setMontoIsc("0");
			//Tipo de sistema ISC
			detalle.setTipoIsc("");
			//Precio de venta unitario por item
			detalle.setPrecioVentaUnitario(detalleComprobante.getPrecioTotal().toString());
			//Valor de venta por ítem
			detalle.setValorVentaItem(detalleComprobante.getPrecioFinal().toString());
			//Archivo
			detalle.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_DETALLE);
			//Falta auditoria
			this.preGuardarSunatDetalle(detalle, request);
			detalle.setTblSunatCabecera(cabecera);
			sunatDetalleDao.save(detalle);
		}catch(Exception e){
			e.printStackTrace();
			detalle = null;
		}
		return detalle;
	}
	
	/*
	 * Se asigna los datos del campo de auditoria
	 */
	public void preGuardarSunatDetalle(TblSunatDetalle entidad, HttpServletRequest request) {
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			LOGGER.debug("[preGuardarSunatDetalle] Inicio" );
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardarSunatDetalle] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			LOGGER.debug("[preGuardarSunatDetalle] idUsuario:"+idUsuario );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(idUsuario);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			LOGGER.debug("[preGuardarSunatDetalle] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}

	}
	/*
	 * Monto Gravada
	 */
	public BigDecimal obtenerTotalMontoGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100; 
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			resultado = new BigDecimal(monto.doubleValue()*(100)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			//resultado = new BigDecimal(monto.doubleValue()*(100+igv)/100).setScale(2, RoundingMode.HALF_UP);
			
		}catch(Exception e){
			resultado = new BigDecimal("0");
			e.printStackTrace();
		}
		return resultado;
	}
	/*
	 * Impuesto Gravada
	 */
	public BigDecimal obtenerTotalImpuestoGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100;
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			resultado = new BigDecimal(monto.doubleValue()*(igv)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			
		}catch(Exception e){
			resultado = new BigDecimal("0");
			e.printStackTrace();
		}
		return resultado;
	}
	/*
	 * Impuesto Gravada
	 */
	public BigDecimal obtenerTotalOtrosTributosGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100;
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			resultado = new BigDecimal(monto.doubleValue()*(servicio)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			
		}catch(Exception e){
			resultado = new BigDecimal("0");
			e.printStackTrace();
		}
		return resultado;
	}
	/*
	 * Se asigna los datos del campo de auditoria
	 */
	public void preGuardarSunatCabecera(TblSunatCabecera entidad, HttpServletRequest request) {
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			LOGGER.debug("[preGuardarSunatCabecera] Inicio" );
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardarSunatCabecera] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			LOGGER.debug("[preGuardarSunatCabecera] idUsuario:"+idUsuario );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(idUsuario);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			LOGGER.debug("[preGuardarSunatCabecera] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}

	}
	/*
	 * Genera un archivo plano Cabecera
	 */
	public boolean generarArchivoCabecera(TblSunatCabecera cabecera, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		//String FILENAME = "G:\\data0\\facturador\\DATA\\"+cabecera.getNombreArchivo();
		//String FILENAME = Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + cabecera.getNombreArchivo();
		String FILENAME = entidad.getSunatData() + cabecera.getNombreArchivo();
		LOGGER.debug("[generarArchivoCabecera] filename: "+FILENAME);
		try{
			cadena = cabecera.getTipoOperacion() + Constantes.SUNAT_PIPE +
					 cabecera.getFechaEmision() + Constantes.SUNAT_PIPE +
					 cabecera.getDomicilioFiscal() + Constantes.SUNAT_PIPE +
					 cabecera.getTipoDocumentoUsuario() + Constantes.SUNAT_PIPE +
					 cabecera.getNumeroDocumento() + Constantes.SUNAT_PIPE +
					 cabecera.getRazonSocial()	+ Constantes.SUNAT_PIPE +
					 cabecera.getTipoMoneda() + Constantes.SUNAT_PIPE +
					 cabecera.getSumaDescuento() + Constantes.SUNAT_PIPE +
					 cabecera.getSumaCargo() + Constantes.SUNAT_PIPE +
					 cabecera.getTotalDescuento() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionGravada() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionInafecta() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionExonerada() + Constantes.SUNAT_PIPE +
					 cabecera.getMontoIgv() + Constantes.SUNAT_PIPE +
					 cabecera.getMontoIsc() + Constantes.SUNAT_PIPE +
					 cabecera.getOtrosTributos() + Constantes.SUNAT_PIPE +
					 cabecera.getImporteTotal();
			bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
			bufferedWriter.write(cadena); 
			resultado = true;
					
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}finally{
			try{
				if (bufferedWriter !=null){
					bufferedWriter.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		return resultado;
	}
	/*
	 * Genera un archivo plano Leyenda
	 */
	public boolean generarArchivoLeyenda(TblLeyenda leyenda, TblComprobante comprobante, String nombreFile, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		//String FILENAME = "G:\\data0\\facturador\\DATA\\"+cabecera.getNombreArchivo();
		//String FILENAME = Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + nombreFile;
		String FILENAME = entidad.getSunatData() + nombreFile;
		try{
			
			
			bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
			//Leyenda de moneda
			if (comprobante.getTotalImporte().doubleValue() > 0){
				cadena = Constantes.SUNAT_LEYENDA_MONTO_LETRAS_1000 + 
						Constantes.SUNAT_PIPE + NumberToLetterConverter.convertNumberToLetter(comprobante.getTotalImporte().doubleValue(), comprobante.getMoneda());
			}else{
				//Tomamos el valor de Operaciones Gratuitas
				cadena = Constantes.SUNAT_LEYENDA_MONTO_LETRAS_1000 + 
						Constantes.SUNAT_PIPE + NumberToLetterConverter.convertNumberToLetter(comprobante.getValorOpGratuitas().doubleValue(), comprobante.getMoneda());
			}
			
			bufferedWriter.write(cadena);
			//Leyenda adicional
			if (leyenda !=null && !leyenda.getCodigoSunat().equals("")){
				bufferedWriter.newLine();
				cadena = leyenda.getCodigoSunat() + Constantes.SUNAT_PIPE +	leyenda.getDescripcionSunat();
				bufferedWriter.write(cadena); 
			}
			resultado = true;
					
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}finally{
			try{
				if (bufferedWriter !=null){
					bufferedWriter.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		return resultado;
	}
	/*
	 * Genera un archivo de detalle adicional
	 */
	public boolean generarArchivoAdicionalDetalle(Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;//Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
		try{
			for(TblDetalleComprobante detalle:entidad.getListaDetalle()){
				if (detalle.getValorReferencia()!=null && detalle.getValorReferencia().doubleValue()>0){
					if (FILENAME == null){
						//FILENAME =  Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + Constantes.SUNAT_RUC_EMISOR+"-"+entidad.getComprobante().getTipoComprobante()+"-"+entidad.getComprobante().getSerie()+"-"+entidad.getComprobante().getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_ADICIONAL_DETALLE;
						FILENAME = entidad.getSunatData() + Constantes.SUNAT_RUC_EMISOR+"-"+entidad.getComprobante().getTipoComprobante()+"-"+entidad.getComprobante().getSerie()+"-"+entidad.getComprobante().getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_ADICIONAL_DETALLE;
						bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
					}else{
						bufferedWriter.newLine();
					}
					cadena = detalle.getValorReferencia().toString() + Constantes.SUNAT_PIPE + "GRATIS";
					bufferedWriter.write(cadena); 
				}
				
			}
			resultado = true;
					
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}finally{
			try{
				if (bufferedWriter !=null){
					bufferedWriter.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return resultado;
	}
	/*
	 * Genera un archivo plano Detalle
	 */
	public boolean generarArchivoDetalle(List<TblSunatDetalle> listaDetalle, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;//Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
		try{
			for(TblSunatDetalle detalle:listaDetalle){
				if (FILENAME == null){
					//FILENAME = Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
					FILENAME = entidad.getSunatData() + detalle.getNombreArchivo();
					bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
				}else{
					bufferedWriter.newLine();
				}
				cadena = detalle.getCodigoUnidad() + Constantes.SUNAT_PIPE +
						 detalle.getCantidad() + Constantes.SUNAT_PIPE +
						 detalle.getCodigoProducto() + Constantes.SUNAT_PIPE +
						 detalle.getCodigoProductoSunat() + Constantes.SUNAT_PIPE +
						 detalle.getDescripcion() + Constantes.SUNAT_PIPE +
						 detalle.getValorUnitario()	+ Constantes.SUNAT_PIPE +
						 detalle.getDescuento() + Constantes.SUNAT_PIPE +
						 detalle.getMontoIgv() + Constantes.SUNAT_PIPE +
						 detalle.getAfectacionIgv() + Constantes.SUNAT_PIPE +
						 detalle.getMontoIsc() + Constantes.SUNAT_PIPE +
						 detalle.getTipoIsc() + Constantes.SUNAT_PIPE +
						 detalle.getPrecioVentaUnitario() + Constantes.SUNAT_PIPE +
						 detalle.getValorVentaItem();
				bufferedWriter.write(cadena); 
				
			}
			resultado = true;
					
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}finally{
			try{
				if (bufferedWriter !=null){
					bufferedWriter.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return resultado;
	}
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Comprobante
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/adicionarDetalle", method = RequestMethod.POST)
	public String adicionarDetalle(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/comprobante/com_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[adicionarDetalle] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalle(listaDetalle);
			if (this.validarDetalle(model, entidad, request)){
				if (entidad.getListaDetalle()==null){
					entidad.setListaDetalle(new ArrayList<TblDetalleComprobante>());
					entidad.getListaDetalle().add(entidad.getDetalleComprobante());
					LOGGER.debug("[adicionarDetalle] Primera vez");
				}else{
					entidad.getListaDetalle().add(entidad.getDetalleComprobante());
					LOGGER.debug("[adicionarDetalle] Adicionando...");
				}
			}
			this.calculoDetalleComprobante(entidad);
			this.calculoCabeceraComprobante(entidad);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[adicionarDetalle] Fin");
		}catch(Exception e){
			LOGGER.debug("[adicionarDetalle] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			listaDetalle = null;
		}
		LOGGER.debug("[adicionarDetalle] Fin");
		return path;
	}
	
	/*
	 * Calculo del Detalle 
	 */
	public void calculoDetalleComprobante(Filtro entidad){
		TblDetalleComprobante detalle = null;
		try{
			detalle = entidad.getDetalleComprobante();
			//detalle.setPrecioTotal(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));
			detalle.setPrecioTotal(detalle.getPrecioUnitario().multiply(detalle.getCantidad()));
			if (detalle.getDescuento()!=null && detalle.getDescuento().doubleValue()>0){
				detalle.setPrecioFinal(new BigDecimal(detalle.getPrecioTotal().doubleValue() - detalle.getPrecioTotal().doubleValue()*detalle.getDescuento().doubleValue()/100).setScale(2, RoundingMode.HALF_UP));
			}else{
				detalle.setPrecioFinal(detalle.getPrecioTotal());
			}
				
		}catch(Exception e){
			
		}
	}
	/*
	 * Calculo de los totales	  
	 */
	public void calculoCabeceraComprobante(Filtro entidad){
		TblComprobante comprobante = null;
		try{
			comprobante = entidad.getComprobante();
			LOGGER.debug(comprobante.getTipoOperacion());
			if (comprobante.getTipoOperacion().equals(Constantes.SUNAT_TIPO_OPERACION_EXPORTACION)){
				this.calculoCabeceraComprobateExtranjero(entidad);
			}else{
				this.calculoCabeceraComprobanteNacional(entidad);
			}
			
			/*comprobante = entidad.getComprobante();
			//inicializa
			comprobante.setDescuentosGlobales(new BigDecimal("0"));
			comprobante.setTotalImporte(new BigDecimal("0"));
			
			for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				comprobante.setDescuentosGlobales(comprobante.getDescuentosGlobales().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));
				
				//Total Importe: Precio Unitario x Cantidad - Descuento
				comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()).setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setTotalImporte(comprobante.getTotalImporte().subtract(comprobante.getDescuentosGlobales()));
			}
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			comprobante.setTotalDescuento(comprobante.getDescuentosGlobales().add(comprobante.getTotalOtrosCargos()));
			
			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total sin igv
			comprobante.setTotalOpGravada(this.obtenerTotalMontoGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total de otros tributos: servicio
			comprobante.setSumatorioaOtrosTributos(this.obtenerTotalOtrosTributosGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			*/
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void calculoCabeceraComprobanteNacional(Filtro entidad){
		TblComprobante comprobante = null;
		try{
			comprobante = entidad.getComprobante();
			//inicializa
			comprobante.setDescuentosGlobales(new BigDecimal("0"));
			comprobante.setTotalImporte(new BigDecimal("0"));
			comprobante.setValorOpGratuitas(new BigDecimal("0"));
			for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				comprobante.setDescuentosGlobales(comprobante.getDescuentosGlobales().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));
				
				//Total Importe: Precio Unitario x Cantidad - Descuento
				comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setTotalImporte(comprobante.getTotalImporte().subtract(comprobante.getDescuentosGlobales()));
				//Total de valor de referencia
				comprobante.setValorOpGratuitas(comprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
			}
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			comprobante.setTotalDescuento(comprobante.getDescuentosGlobales().add(comprobante.getTotalOtrosCargos()));
			
			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total sin igv
			comprobante.setTotalOpGravada(this.obtenerTotalMontoGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total de otros tributos: servicio
			comprobante.setSumatorioaOtrosTributos(this.obtenerTotalOtrosTributosGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void calculoCabeceraComprobateExtranjero(Filtro entidad){
		TblComprobante comprobante = null;
		try{
			comprobante = entidad.getComprobante();
			//inicializa
			comprobante.setDescuentosGlobales(new BigDecimal("0"));
			comprobante.setTotalImporte(new BigDecimal("0"));
			for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				comprobante.setDescuentosGlobales(comprobante.getDescuentosGlobales().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));
				
				//Total Importe: Precio Unitario x Cantidad - Descuento
				comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setTotalImporte(comprobante.getTotalImporte().subtract(comprobante.getDescuentosGlobales()));
			}
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			comprobante.setTotalDescuento(comprobante.getDescuentosGlobales().add(comprobante.getTotalOtrosCargos()));
			
			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), 0, entidad.getValorServicio())); // IGV = 0
			//Calculo del total sin igv
			comprobante.setTotalOpGravada(new BigDecimal("0"));// IGV = 0
			comprobante.setTotalOpInafecta(this.obtenerTotalMontoGravada(comprobante.getTotalImporte(), 0 , entidad.getValorServicio()));
			//Calculo del total de otros tributos: servicio
			comprobante.setSumatorioaOtrosTributos(this.obtenerTotalOtrosTributosGravada(comprobante.getTotalImporte(), 0 , entidad.getValorServicio()));// IGV = 0
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Validacio del detalle
	 */
	public boolean validarDetalle(Model model, Filtro entidad, HttpServletRequest request){
		boolean exitoso = true;
		try{
			if (entidad.getComprobante().getTipoOperacion() == null || entidad.getComprobante().getTipoOperacion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar el tipo de operación");
				return exitoso;
			}
			if (entidad.getDetalleComprobante().getUnidadMedida() == null || entidad.getDetalleComprobante().getUnidadMedida().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la unidad de medida");
				return exitoso;
			}
			if (entidad.getDetalleComprobante().getDescripcion() == null || entidad.getDetalleComprobante().getDescripcion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el producto");
				return exitoso;
			}
			if (entidad.getDetalleComprobante().getCantidad() == null || entidad.getDetalleComprobante().getCantidad().doubleValue() <= 0){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la cantidad");
				return exitoso;
			}
			if (entidad.getDetalleComprobante().getTipoAfectacion() == null || entidad.getDetalleComprobante().getTipoAfectacion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de afectación");
				return exitoso;
			}
			if (entidad.getDetalleComprobante().getPrecioUnitario() == null || entidad.getDetalleComprobante().getPrecioUnitario().doubleValue() <= 0){
				if (entidad.getDetalleComprobante().getValorReferencia() == null || entidad.getDetalleComprobante().getValorReferencia().doubleValue() <= 0){
					exitoso = false;
					model.addAttribute("respuesta", "Debe ingresar el precio unitario");
					return exitoso;
				}
				
			}
			LOGGER.debug("entidad.getComprobante().getTipoOperacion():"+entidad.getComprobante().getTipoOperacion());
			LOGGER.debug("entidad.getDetalleComprobante().getTipoAfectacion():"+entidad.getDetalleComprobante().getTipoAfectacion());
			if (entidad.getComprobante().getTipoOperacion().equals("02")){
				if (!entidad.getDetalleComprobante().getTipoAfectacion().equals("40")){
					exitoso = false;
					model.addAttribute("respuesta", "El tipo de afectación y tipo de operación deben corresponder a Exportación");
					return exitoso;
				}
			}
			
			
		}catch(Exception e){
			exitoso = false;
		}
		return exitoso;
	}
	/*
	 * Listado de Clientes
	 */
	@RequestMapping(value = "/operacion/comprobantes/clientes", method = RequestMethod.POST)
	public String mostrarClientes(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/comprobante/com_cli_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			//this.listarCliente(model, filtro);
			request.getSession().setAttribute("filtroSession", filtro);
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Cliente ***/
	private void listarCliente(Model model, Filtro filtro, HttpServletRequest request){
		List<TblCliente> entidades = new ArrayList<TblCliente>();
		Integer codigoEntidad = null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			Specification<TblCliente> criterio = Specifications.where(conNumeroDocumento((filtro.getNumero())))
					.and(conNombreCliente(filtro.getNombre().toUpperCase()))
					.and(com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conCodigoEmpresa(codigoEntidad))
					.and(conEstadoCliente(Constantes.ESTADO_REGISTRO_ACTIVO));
			entidades = clienteDao.findAll(criterio);
			LOGGER.debug("[listarCliente] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
	@RequestMapping(value = "/operacion/comprobantes/clientes/q", method = RequestMethod.POST)
	public String listarClientes(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/comprobante/com_cli_listado";
		try{
			LOGGER.debug("[listarClientes] Inicio");
			if (validarNegocioCliente(model, filtro)){
				this.listarCliente(model, filtro, request);
			}else{
				model.addAttribute("registros", new ArrayList<TblCliente>());
			}
			//request.getSession().setAttribute("filtroSession", filtro);
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[listarClientes] Fin");
		}catch(Exception e){
			LOGGER.debug("[listarClientes] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[listarClientes] Fin");
		return path;
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
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/clientes/seleccionar/{id}", method = RequestMethod.GET)
	public String asignarClienteGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		TblCliente cliente 					= null;
		Filtro filtro						= null;
		String path							= null;
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			cliente = clienteDao.findOne(id);
			//Se mantiene en Session el contrato hasta retornar
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			if (cliente!=null ){
				filtro.getComprobante().setNombreCliente(cliente.getNombre());
				filtro.getComprobante().setNumeroDocumento(cliente.getNumeroDocumento());
				filtro.getComprobante().setTipoDocumento(cliente.getTblCatalogo().getCodigoSunat());
				filtro.getComprobante().setDireccionCliente(cliente.getDireccion());
			}else{
				filtro.getComprobante().setNombreCliente("");
				filtro.getComprobante().setNumeroDocumento("");
				filtro.getComprobante().setTipoDocumento("");
				filtro.getComprobante().setDireccionCliente("");
			}
			filtro.setListaDetalle(listaDetalle);	
			model.addAttribute("filtro", filtro);
			path = "operacion/comprobante/com_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cliente = null;
			filtro 	= null;
		}
		return path;
	}
	/*
	 * Regresa a la pantalla de comprobante
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/regresar", method = RequestMethod.POST)
	public String regresarComprobante(Model model, Filtro filtro, String path, HttpServletRequest request) {
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[regresarComprobante] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			filtro.setListaDetalle(listaDetalle);	
			LOGGER.debug("[regresarContrato] oPERACION:"+filtro.getStrOperacion());
			path = "operacion/comprobante/com_nuevo";
			
			model.addAttribute("filtro", filtro);
			
			LOGGER.debug("[regresarComprobante] Fin");
		}catch(Exception e){
			LOGGER.debug("[regresarComprobante] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}

	/*
	 * Listado de Producto
	 */
	@RequestMapping(value = "/operacion/comprobantes/productos", method = RequestMethod.POST)
	public String mostrarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/comprobante/com_pro_listado";
		try{
			LOGGER.debug("[mostrarProducto] Inicio");
			this.buscarProducto(model, filtro,request);
			request.getSession().setAttribute("filtroSession", filtro);
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[mostrarProducto] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[mostrarProducto] Fin");
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
			LOGGER.debug("[listarProducto] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
 
	@RequestMapping(value = "/operacion/comprobantes/productos/q", method = RequestMethod.POST)
	public String listarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/comprobante/com_pro_listado";
		try{
			LOGGER.debug("[listarProducto] Inicio");
			this.buscarProducto(model, filtro,request);
			//request.getSession().setAttribute("filtroSession", filtro);
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[listarProducto] Fin");
		}catch(Exception e){
			LOGGER.debug("[listarProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[listarProducto] Fin");
		return path;
	}
	/*
	 * Asignar Cliente
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/productos/seleccionar/{id}", method = RequestMethod.GET)
	public String asignarProductoGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		TblProducto producto				= null;
		Filtro filtro						= null;
		String path							= null;
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			producto = productoDao.findOne(id);
			//Se mantiene en Session el contrato hasta retornar
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			LOGGER.debug("[asignarProductoGet] catalogo:"+producto.getTblCatalogo().getNombre());
			filtro.getDetalleComprobante().setDescripcion("");
			filtro.getDetalleComprobante().setPrecioUnitario(new BigDecimal("0"));
			filtro.getDetalleComprobante().setMoneda("");
			if (producto!=null ){
				filtro.getDetalleComprobante().setDescripcion(producto.getCodigoEmpresa() + ":" + producto.getNombre());
				filtro.getDetalleComprobante().setPrecioUnitario(producto.getPrecio());
				filtro.setCodigoProducto(producto.getCodigoEmpresa());
				filtro.getDetalleComprobante().setMoneda(producto.getTblCatalogo().getCodigoSunat());
			}
			filtro.setListaDetalle(listaDetalle);	
			model.addAttribute("filtro", filtro);
			
			path = "operacion/comprobante/com_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			producto = null;
			filtro 	= null;
		}
		return path;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarDetalleGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		Filtro filtro						= null;
		String path							= null;
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			
			if (listaDetalle!=null && listaDetalle.size()>id){
				listaDetalle.remove(id.intValue());
				
			}
			request.getSession().setAttribute("listaDetalleSession", listaDetalle);
			filtro.setListaDetalle(listaDetalle);
			model.addAttribute("filtro", filtro);
			this.calculoDetalleComprobante(filtro);
			this.calculoCabeceraComprobante(filtro);
			path = "operacion/comprobante/com_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			listaDetalle = null;
		}
		return path;
	}
	/*
	 * Muestra el comprobante como solo lectura
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/pdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> pdfComprobante(@PathVariable Integer id, HttpServletRequest request) {
		Filtro filtro								= null;
		List<TblDetalleComprobante> listaDetalle 	= null;
		TblComprobante comprobante					= null;
		HttpHeaders headers 						= new HttpHeaders();
		ByteArrayInputStream bis 					= null;
		TblSunatCabecera sunatCabecera				= null;
		List<TblSunatDetalle> listaDetalleSunat		= null;
		//ComprobantePdf comprobantePdf				= new ComprobantePdf();
		ComprobanteKenorPdf comprobantePdf			= new ComprobanteKenorPdf();
		TblLeyenda leyenda							= null;
		TblCatalogo domicilio						= null;
		try{
			filtro = new Filtro();
			sunatCabecera = sunatCabeceraDao.findByCodigoDocumento(id);
			if (sunatCabecera!=null && sunatCabecera.getCodigoCabecera()>0){
				listaDetalleSunat = sunatDetalleDao.findByCodigoCabecera(sunatCabecera.getCodigoCabecera());
			}
			comprobante = comprobanteDao.findOne(id);
			listaDetalle = detalleComprobanteDao.listarxComprobante(id);
			leyenda = leyendaDao.getxComprobante(id);
			filtro.setLeyendaSunat(leyenda);
			filtro.setComprobante(comprobante);
			filtro.setListaDetalle(listaDetalle);
			filtro.setSunatCabecera(sunatCabecera);
			filtro.setListaDetalleSunat(listaDetalleSunat);
			filtro.setAppRutaContexto(request.getContextPath());
			filtro.setListaParametro((List<ParametroFacturadorBean>)request.getSession().getAttribute("SessionListParametro"));
			//Datos del Punto de Facturacion
			domicilio = catalogoDao.getCatalogoxCodigoSunatxTipo(comprobante.getCodigoDomicilio(), Constantes.TIPO_CATALAGO_COD_DOMICILIO_FISCAL);
			if (domicilio !=null){
				filtro.setNombreDomicilioFiscal(domicilio.getNombre());
			}else{
				filtro.setNombreDomicilioFiscal("-");
			}
			bis = comprobantePdf.comprobanteReporte(filtro);
			
			
	        //headers.add("Content-Disposition", "inline; filename=Comprobante.pdf");
			headers.add("Content-Disposition", "attachment; filename="+comprobante.getSerie()+"-"+comprobante.getNumero()+".pdf");
	        

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			filtro 				= null;
			listaDetalle 		= null;
			comprobante			= null;
			
		}
		return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
	}
	
	private TblBandejaFacturador setDatosBandeja(BandejaFacturadorBean bandeja){
		TblBandejaFacturador bandejaFacturador = new TblBandejaFacturador();
		try{
			bandejaFacturador.setNumeroRuc(bandeja.getNumeroRuc());
			bandejaFacturador.setTipoDocumento(bandeja.getTipoDocumento());
			bandejaFacturador.setNumeroDocumento(bandeja.getNumeroDocumento());
			bandejaFacturador.setFechaCargue(bandeja.getFechaCarga());
			bandejaFacturador.setFechaGeneracion(bandeja.getFechaGeneracion());
			bandejaFacturador.setFechaEnvio(bandeja.getFechaEnvio());
			bandejaFacturador.setObservacion(bandeja.getObservacion());
			bandejaFacturador.setNombreArchivo(bandeja.getNombreArchivo());
			bandejaFacturador.setSituacion(bandeja.getSituacion());
			bandejaFacturador.setTipoArchivo(bandeja.getTipoArchivo());
			bandejaFacturador.setFirmaDigital(bandeja.getFirmaDigital());
			bandejaFacturador.setEstado(Constantes.ESTADO_ACTIVO);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return bandejaFacturador;
	}
	
	private TblBandejaFacturador editarDatosBandeja(BandejaFacturadorBean bandeja, TblBandejaFacturador bandejaFacturador){
	
		try{
			bandejaFacturador.setNumeroRuc(bandeja.getNumeroRuc());
			bandejaFacturador.setTipoDocumento(bandeja.getTipoDocumento());
			bandejaFacturador.setNumeroDocumento(bandeja.getNumeroDocumento());
			bandejaFacturador.setFechaCargue(bandeja.getFechaCarga());
			bandejaFacturador.setFechaGeneracion(bandeja.getFechaGeneracion());
			bandejaFacturador.setFechaEnvio(bandeja.getFechaEnvio());
			bandejaFacturador.setObservacion(bandeja.getObservacion());
			bandejaFacturador.setNombreArchivo(bandeja.getNombreArchivo());
			bandejaFacturador.setSituacion(bandeja.getSituacion());
			bandejaFacturador.setTipoArchivo(bandeja.getTipoArchivo());
			bandejaFacturador.setFirmaDigital(bandeja.getFirmaDigital());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return bandejaFacturador;
	}
	
	/*
	 * Se asigna los datos del campo de auditoria
	 */
	private void preGuardarBandeja(TblBandejaFacturador entidad, HttpServletRequest request) {
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			LOGGER.debug("[preGuardarBandeja] Inicio" );
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardarBandeja] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			LOGGER.debug("[preGuardarBandeja] idUsuario:"+idUsuario );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(idUsuario);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			LOGGER.debug("[preGuardarBandeja] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}

	}
	private void preEditarBandeja(TblBandejaFacturador entidad, HttpServletRequest request) {
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			LOGGER.debug("[preEditarBandeja] Inicio" );
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preEditarBandeja] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			LOGGER.debug("[preEditarBandeja] idUsuario:"+idUsuario );
			entidad.setFechaModificacion(new Date(System.currentTimeMillis()));
			entidad.setIpModificacion(request.getRemoteAddr());
			entidad.setUsuarioModificacion(idUsuario);
			LOGGER.debug("[preEditarBandeja] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}

	}
	@RequestMapping(value = "/operacion/comprobantes/ver/actualizar", method = RequestMethod.POST)
	public String actualizarEstadoComprobante(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/comprobante/com_ver";
		TblComprobante comprobante					= null;
		TblBandejaFacturador bandejaFacturador		= null;
		BandejaFacturadorBean bandejaBean			= null;
		BandejaFacturadorBean bandejaRptaBean		= null;
		IOperacionFacturador operacionFacturador	= new OperacionFacturadorImp();
		try{
			LOGGER.debug("[actualizarEstadoComprobante] Inicio");
			filtro = (Filtro)request.getSession().getAttribute("filtroVerSession");
			comprobante = filtro.getComprobante();
			bandejaFacturador = bandejaFacturadorDao.buscarOneByComprobante(comprobante.getCodigoComprobante());
			if (bandejaFacturador == null){
				bandejaBean = new BandejaFacturadorBean();
				bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
				bandejaBean.setTipoDocumento(comprobante.getTipoComprobante());
				bandejaBean.setNumeroDocumento(comprobante.getSerie()+"-"+comprobante.getNumero());
				bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean, filtro);
				if (bandejaRptaBean!=null){
					bandejaFacturador = this.setDatosBandeja(bandejaRptaBean);
					this.preGuardarBandeja(bandejaFacturador, request);
					bandejaFacturador.setTblComprobante(comprobante);
					bandejaFacturadorDao.save(bandejaFacturador);
					filtro.setBandejaFacturador(bandejaFacturador);
					//Actualizando estado en el comprobante
					comprobante.setEstadoOperacion(bandejaFacturador.getSituacion());
					comprobanteDao.save(comprobante);
				}else{
					bandejaFacturador = new TblBandejaFacturador();
					bandejaFacturador.setObservacion("En proceso de lectura por el facturador... (tiempo de espera 1 min.)");
					filtro.setBandejaFacturador(bandejaFacturador);
				}
				
			}else{
				//Validando el estado
				if (bandejaFacturador.getSituacion().equals("01") || bandejaFacturador.getSituacion().equals("02")){
					bandejaBean = new BandejaFacturadorBean();
					bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
					bandejaBean.setTipoDocumento(comprobante.getTipoComprobante());
					bandejaBean.setNumeroDocumento(comprobante.getSerie()+"-"+comprobante.getNumero());
					bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean,filtro);
					if (bandejaRptaBean!=null){
						this.editarDatosBandeja(bandejaRptaBean, bandejaFacturador);
						this.preEditarBandeja(bandejaFacturador, request);
						bandejaFacturador.setTblComprobante(comprobante);
						//Eliminando comprobante de la bandeja
						bandejaFacturadorDao.deleteByComprobante(comprobante.getCodigoComprobante());
						//Registrando nuevos datos del comprobante en bandeja
						bandejaFacturadorDao.save(bandejaFacturador);
						filtro.setBandejaFacturador(bandejaFacturador);
						//Actualizando estado en el comprobante
						comprobante.setEstadoOperacion(bandejaFacturador.getSituacion());
						comprobanteDao.save(comprobante);
					}else{
						bandejaFacturador = new TblBandejaFacturador();
						bandejaFacturador.setObservacion("En proceso de lectura por el facturador... (tiempo de espera 1 min.)");
						filtro.setBandejaFacturador(bandejaFacturador);
					}
				}
				filtro.setBandejaFacturador(bandejaFacturador);
			}
			request.getSession().setAttribute("filtroVerSession", filtro);
			
			
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[actualizarEstadoComprobante] Fin");
		}catch(Exception e){
			LOGGER.debug("[listarProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			filtro 				= null;
			comprobante			= null;
			bandejaFacturador	= null;
			bandejaBean			= null;
			bandejaRptaBean		= null;
		}
		LOGGER.debug("[listarProducto] Fin");
		return path;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/ver/{id}", method = RequestMethod.GET)
	public String verComprobante(@PathVariable Integer id, Model model, HttpServletRequest request) {
		Filtro filtro								= null;
		String path									= null;
		List<TblDetalleComprobante> listaDetalle 	= null;
		TblComprobante comprobante					= null;
		TblLeyenda leyenda							= null;
		TblBandejaFacturador bandejaFacturador		= null;
		BandejaFacturadorBean bandejaBean			= null;
		BandejaFacturadorBean bandejaRptaBean		= null;
		IOperacionFacturador operacionFacturador	= new OperacionFacturadorImp();
		Map<String, TblParametro> mapParametro		= null;
		//TblParametro parametro =null;
		
		try{
			filtro = new Filtro();
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			
			this.asignarParametros(filtro, mapParametro,request);
			comprobante = comprobanteDao.findOne(id);
			listaDetalle = detalleComprobanteDao.listarxComprobante(id);
			leyenda = leyendaDao.getxComprobante(id);
			filtro.setComprobante(comprobante);
			filtro.setListaDetalle(listaDetalle);
			filtro.setLeyendaSunat(leyenda);
			//Fechas
			filtro.setFechaEmision(comprobante.getFechaEmision());
			filtro.setFechaVencimiento(comprobante.getFechaVencimiento());
			bandejaFacturador = bandejaFacturadorDao.buscarOneByComprobante(id);
			if (bandejaFacturador == null){
				bandejaBean = new BandejaFacturadorBean();
				bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
				bandejaBean.setTipoDocumento(comprobante.getTipoComprobante());
				bandejaBean.setNumeroDocumento(comprobante.getSerie()+"-"+comprobante.getNumero());
				bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean,filtro);
				if (bandejaRptaBean!=null){
					bandejaFacturador = this.setDatosBandeja(bandejaRptaBean);
					this.preGuardarBandeja(bandejaFacturador, request);
					bandejaFacturador.setTblComprobante(comprobante);
					bandejaFacturadorDao.save(bandejaFacturador);
					filtro.setBandejaFacturador(bandejaFacturador);
					//Actualizando estado en el comprobante
					comprobante.setEstadoOperacion(bandejaFacturador.getSituacion());
					comprobanteDao.save(comprobante);
				}else{
					bandejaFacturador = new TblBandejaFacturador();
					bandejaFacturador.setObservacion("En proceso de lectura por el facturador... (tiempo de espera 1 min.)");
					filtro.setBandejaFacturador(bandejaFacturador);
				}
				
			}else{
				//Validando el estado
				if (bandejaFacturador.getSituacion().equals("01") || bandejaFacturador.getSituacion().equals("02")){
					bandejaBean = new BandejaFacturadorBean();
					bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
					bandejaBean.setTipoDocumento(comprobante.getTipoComprobante());
					bandejaBean.setNumeroDocumento(comprobante.getSerie()+"-"+comprobante.getNumero());
					bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean,filtro);
					if (bandejaRptaBean!=null){
						this.editarDatosBandeja(bandejaRptaBean, bandejaFacturador);
						this.preEditarBandeja(bandejaFacturador, request);
						bandejaFacturador.setTblComprobante(comprobante);
						//Eliminando comprobante de la bandeja
						bandejaFacturadorDao.deleteByComprobante(comprobante.getCodigoComprobante());
						//Registrando nuevos datos del comprobante en bandeja
						bandejaFacturadorDao.save(bandejaFacturador);
						filtro.setBandejaFacturador(bandejaFacturador);
						//Actualizando estado en el comprobante
						comprobante.setEstadoOperacion(bandejaFacturador.getSituacion());
						comprobanteDao.save(comprobante);
					}else{
						bandejaFacturador = new TblBandejaFacturador();
						bandejaFacturador.setObservacion("En proceso de lectura por el facturador... (tiempo de espera 1 min.)");
						filtro.setBandejaFacturador(bandejaFacturador);
					}
				}
				filtro.setBandejaFacturador(bandejaFacturador);
				
			}
			request.getSession().setAttribute("filtroVerSession", filtro);
			
			model.addAttribute("filtro", filtro);
			path = "operacion/comprobante/com_ver";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			filtro 				= null;
			listaDetalle 		= null;
			comprobante			= null;
			bandejaFacturador	= null;
			bandejaBean			= null;
			bandejaRptaBean		= null;
		}
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/leyenda", method = RequestMethod.POST)
	public String asignarLeyenda(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/comprobante/com_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[adicionarDetalle] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalle(listaDetalle);
			entidad.getLeyendaSunat().setDescripcionSunat(this.getDescripcionLeyenda(entidad.getLeyendaSunat().getCodigoSunat(), request));
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[adicionarDetalle] Fin");
		}catch(Exception e){
			LOGGER.debug("[adicionarDetalle] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[adicionarDetalle] Fin");
		return path;
	}
	/*
	 * Obtiene la descripcion de la leyenda
	 */
	@SuppressWarnings("unchecked")
	private String getDescripcionLeyenda(String strCodigoLeyenda, HttpServletRequest request){
		String resultado 						= null;
		Map<String, Object> mapTipoLeyenda		= null;
		Map<String, TblParametro> mapParametro	= null;
		TblParametro parametro 					= null; 
		String strDetraccion					= null;
		try{

			mapTipoLeyenda = (Map<String, Object>)request.getSession().getAttribute("SessionMapTipoLeyendaCodigo");
			if (strCodigoLeyenda.equals(Constantes.SUNAT_LEYENDA_DETRACCION_3001)){
				mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
				if (mapParametro !=null){
					parametro = mapParametro.get(Constantes.PARAMETRO_DETRACCION);
					if (parametro!=null){
						strDetraccion =parametro.getDato();
					}else{
						strDetraccion = "Por registrar Parametro";
					}
				}else{
					strDetraccion = "Por definir Parametros";
				}
				resultado = strDetraccion;
			}else{
				resultado = (String)mapTipoLeyenda.get(strCodigoLeyenda);
				if (resultado !=null && resultado.length()>6){
					//resultado = resultado.substring(6);
					resultado = "";
				}
				
			}
			
		}catch(Exception e){
			resultado = "";
			e.printStackTrace();
		}finally{
			mapTipoLeyenda	= null;
			mapParametro	= null;
			parametro 		= null; 
			strDetraccion	= null;
		}
		return resultado;
	}
	
	/**
	 * Se encarga de la eliminacion fisica del comprobante
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "operacion/comprobantes/all/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarComprobante(@PathVariable Integer id, HttpServletRequest request, Model model, PageableSG pageable) {
		TblComprobante entidad						= null;
		List<TblDetalleComprobante> listaDetEntidad = null;
		TblSunatCabecera sunat						= null;
		List<TblSunatDetalle> listaDetSunat			= null;
		TblLeyenda leyenda							= null;
		String path 								= null;
		Filtro filtro 								= null;
		try{
			LOGGER.debug("[eliminarComprobante] Inicio");
			
			//Buscando
			entidad = comprobanteDao.findOne(id);
			if (!entidad.getEstadoOperacion().equals(Constantes.SUNAT_ESTADO_OPERACION_ACEPTADO)){
				listaDetEntidad = detalleComprobanteDao.listarxComprobante(id);
				leyenda = leyendaDao.getxComprobante(id);
				sunat = sunatCabeceraDao.findByCodigoDocumento(id);
				if (sunat !=null && sunat.getCodigoCabecera()>0){
					listaDetSunat = sunatDetalleDao.findByCodigoCabecera(sunat.getCodigoCabecera());
				}
				//Borrando
				if (listaDetSunat!=null){
					for(TblSunatDetalle detalle:listaDetSunat){
						try{
							sunatDetalleDao.delete(detalle);
						}catch(Exception e1){
							e1.printStackTrace();
						}
					}
				}
				if(sunat!=null){
					try{
						sunatCabeceraDao.delete(sunat);
					}catch(Exception e1){
						e1.printStackTrace();
					}
					
				}
				if (leyenda!=null){
					try{
						leyendaDao.delete(leyenda);
					}catch(Exception e1){
						e1.printStackTrace();
					}
					
				}
				if (listaDetEntidad !=null){
					for(TblDetalleComprobante detalle: listaDetEntidad){
						try{
							detalleComprobanteDao.delete(detalle);
						}catch(Exception e1){
							e1.printStackTrace();
						}
						
					}
				}
				//Borrando la bandeja del facturador
				try{
					bandejaFacturadorDao.deleteByComprobante(id);
				}catch(Exception e1){
					e1.printStackTrace();
				}
				
				//Eliminando el comprobante
				try{
					comprobanteDao.delete(entidad);
				}catch(Exception e1){
					e1.printStackTrace();
				}
				
				model.addAttribute("respuesta", "Debe eliminó satisfactoriamente");
			}else{
				model.addAttribute("respuesta", "La Factura fue generado y aceptado por la SUNAT, no se puede eliminar");
			}
			
			path = "operacion/comprobante/com_listado";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setNumero("");;
			this.listarComprobante(model, filtro, pageable, this.urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroCriterio", filtro);
			LOGGER.debug("[eliminarComprobante] Fin");
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("respuesta", "Error en el proceso de eliminación: "+e.getMessage());
		}finally{
			entidad 		= null;
			listaDetEntidad	= null;
			leyenda			= null;
			sunat			= null;
			listaDetSunat	= null;
			filtro		= null;
		}
		return path;
	}
	
	
	@RequestMapping(value = "/operacion/comprobantes/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/comprobante/com_listado";
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
			
			LOGGER.debug("[traerRegistros] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistros] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}
		return path;
	}
	
	
}