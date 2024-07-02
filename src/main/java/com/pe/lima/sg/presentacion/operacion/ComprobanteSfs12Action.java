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
import java.util.HashMap;
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
import com.pe.lima.sg.bean.remision.FacturaAsociadaBean;
import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.dao.mantenimiento.ICatalogoDAO;
import com.pe.lima.sg.dao.mantenimiento.IClienteDAO;
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.dao.mantenimiento.IProductoDAO;
import com.pe.lima.sg.dao.mantenimiento.ISerieDAO;
import com.pe.lima.sg.dao.operacion.IBandejaFacturadorDAO;
import com.pe.lima.sg.dao.operacion.IComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleFormaPagoDAO;
import com.pe.lima.sg.dao.operacion.IDetalleRemisionDAO;
import com.pe.lima.sg.dao.operacion.IFacturaAsociadaDAO;
import com.pe.lima.sg.dao.operacion.IFormaPagoDAO;
import com.pe.lima.sg.dao.operacion.ILeyendaDAO;
import com.pe.lima.sg.dao.operacion.IRemisionDAO;
import com.pe.lima.sg.dao.operacion.ISunatCabeceraDAO;
import com.pe.lima.sg.dao.operacion.ISunatDetalleDAO;
import com.pe.lima.sg.dao.operacion.ISunatTributoGeneralDAO;
import com.pe.lima.sg.dao.operacion.ITributoGeneralDAO;
import com.pe.lima.sg.db.util.IOperacionFacturador;
import com.pe.lima.sg.db.util.OperacionFacturadorImp;
import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
import com.pe.lima.sg.entity.mantenimiento.TblCliente;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.mantenimiento.TblSerie;
import com.pe.lima.sg.entity.operacion.ImpuestoBolsa;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturador;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleFormaPago;
import com.pe.lima.sg.entity.operacion.TblDetalleRemision;
import com.pe.lima.sg.entity.operacion.TblFacturaAsociada;
import com.pe.lima.sg.entity.operacion.TblFormaPago;
import com.pe.lima.sg.entity.operacion.TblLeyenda;
import com.pe.lima.sg.entity.operacion.TblRemision;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.entity.operacion.TblSunatCabecera;
import com.pe.lima.sg.entity.operacion.TblSunatDetalle;
import com.pe.lima.sg.entity.operacion.TblSunatTributoGeneral;
import com.pe.lima.sg.entity.operacion.TblTributoGeneral;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.pdf.ComprobanteKenorPdf;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.NumberToLetterConverter;
import com.pe.lima.sg.presentacion.util.OperacionUtil;
import com.pe.lima.sg.presentacion.util.PageWrapper;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;

/**
 * Clase que se encarga de la administracion de los comprobantes
 *
 * 			
 */
@Controller
public class ComprobanteSfs12Action extends BaseOperacionPresentacion<TblComprobante> {

	@Autowired
	private IComprobanteDAO comprobanteDao;
	
	@Autowired
	private ISerieDAO serieDao;

	@Autowired
	private IDetalleComprobanteDAO detalleComprobanteDao;

	@Autowired
	private ITributoGeneralDAO tributoGeneralDAO;

	@Autowired
	private ISunatTributoGeneralDAO sunatTributoGeneralDAO;


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

	@Autowired
	private IFormaPagoDAO formaPagoDao;
	
	@Autowired
	private IDetalleFormaPagoDAO detalleFormaPagoDao;
	
	@Autowired
	private IRemisionDAO remisionDao;
	
	@Autowired
	private IFacturaAsociadaDAO facturaAsociadaDao;
	
	@Autowired
	private IDetalleRemisionDAO detalleRemisionDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ComprobanteSfs12Action.class);

	/*@Autowired
	private IOperacionFacturador operacionFacturador;*/

	private String urlPaginado = "/operacion/sfs12/paginado/"; 

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
	@RequestMapping(value = "/operacion/sfs12", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/sfs12/sfs_listado";
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
	//Inicializamos el filtro para mostrarlos en la pagina inicial
	private void inicializaFiltroFormaPago(Filtro filtro, HttpServletRequest request){
		filtro.setFormaPago(new TblFormaPago());
		filtro.getFormaPago().setTipo(Constantes.FORMA_PAGO_CONTADO);
		filtro.getFormaPago().setMoneda(Constantes.SUNAT_TIPO_MONEDA_SOLES);
		filtro.getFormaPago().setMonto(new BigDecimal("0.00"));
		filtro.setDetalleFormaPago(new TblDetalleFormaPago());
		filtro.getDetalleFormaPago().setMoneda(Constantes.SUNAT_TIPO_MONEDA_SOLES);
		filtro.getDetalleFormaPago().setMonto(new BigDecimal("0.00"));
		request.getSession().setAttribute("listaDetalleFormaPagoSession", new ArrayList<TblDetalleFormaPago>());
	}

	/**
	 * Se encarga de buscar la informacion del Concepto segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/sfs12/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_listado";
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
			this.actualizarEstadoComprobanteSfs12(filtro, request);
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
	 * Se encarga de direccionar a la pantalla de tipo de Comprobante
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "operacion/sfs12/nuevo", method = RequestMethod.GET)
	public String crearComprobante(Model model, HttpServletRequest request) {
		Filtro filtro 							= null;
		try{
			LOGGER.debug("[crearComprobante] Inicio");
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			
			LOGGER.debug("[crearComprobante] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			filtro 				= null;
		}
		return "operacion/sfs12/sfs_tipo";
	}
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Comprobante
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "operacion/sfs12/nuevo/tipo", method = RequestMethod.POST)
	public String crearPorTipoComprobante(Model model, Filtro entidad, HttpServletRequest request) {
		Filtro filtro 							= new Filtro();
		String path 							= "";
		
		try{
			LOGGER.debug("[crearComprobante] Inicio");
			if (entidad.getTipoComprobante().equals(Constantes.FACTURA_CON_INGRESO_DE_PRODUCTO)) {
				InicializarParametrosFactura(request,filtro);
				model.addAttribute("filtro", filtro);
				request.getSession().setAttribute("listaDetalleSession", new ArrayList<TblDetalleComprobante>());
				path = "operacion/sfs12/sfs_nuevo";
			}else if (entidad.getTipoComprobante().equals(Constantes.FACTURA_DE_GUIA_DE_REMISION)) {
				InicializarParametrosFactura(request,filtro);
				model.addAttribute("filtro", filtro);
				request.getSession().setAttribute("listaDetalleSession", new ArrayList<TblDetalleComprobante>());
				path = "operacion/sfs12/sfs_gre_nuevo";
			}else {
				path = "operacion/sfs12/sfs_tipo";
				model.addAttribute("respuesta", "Seleccionar una opción valida");
			}
			
			LOGGER.debug("[crearComprobante] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			filtro 				= null;
		}
		return path;
	}
	
	@SuppressWarnings("unchecked")
	private void InicializarParametrosFactura(HttpServletRequest request, Filtro filtro) {
		Map<String, TblParametro> mapParametro	= null;
		mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
		OperacionUtil.asignarParametros(filtro, mapParametro,request);
		this.obtenerSerie(filtro, request);
		this.inicializaDatosComprobante(filtro);
		//Inicializamos el filtro
		inicializaFiltroFormaPago(filtro,request);
		
	}


	/*Obtiene las series*/
	public void obtenerSerie(Filtro filtro , HttpServletRequest request){
		Integer codigoEntidad 					= null;
		List<TblSerie> listaSerie 				= null;
		Map<String, String> mapSerie			= null;
		boolean encontrado						= false;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			if(filtro.getFlagSerieAutomatica().equals(Constantes.ESTADO_ACTIVO)){
				if(filtro.getComprobante().getTipoComprobante().equals(Constantes.TIPO_COMPROBANTE_FACTURA)){
					listaSerie = serieDao.buscarAllxTipo(Constantes.TIPO_COMPROBANTE_FACTURA, codigoEntidad);
					if (listaSerie!=null){
						mapSerie = new HashMap<String, String>();
						for(TblSerie serie:listaSerie){
							mapSerie.put(serie.getPrefijoSerie(), serie.getPrefijoSerie());
							if (filtro.getComprobante().getSerie()!=null && filtro.getComprobante().getSerie().equals(serie.getPrefijoSerie())){
								filtro.getComprobante().setNumero(serie.getNumeroComprobante());
								encontrado = true;
							}
						}
						if (!encontrado){
							TblSerie serie =listaSerie.get(0);
							filtro.getComprobante().setSerie(serie.getPrefijoSerie());
							filtro.getComprobante().setNumero(serie.getNumeroComprobante());
						}
						request.getSession().setAttribute("SessionMapSerieComprobanteFacturaBoleta",mapSerie);
						
					}
				}else {
					listaSerie = serieDao.buscarAllxTipo(Constantes.TIPO_COMPROBANTE_BOLETA, codigoEntidad);
					if (listaSerie!=null){
						mapSerie = new HashMap<String, String>();
						for(TblSerie serie:listaSerie){
							mapSerie.put(serie.getPrefijoSerie(), serie.getPrefijoSerie());
							if (filtro.getComprobante().getSerie()!=null && filtro.getComprobante().getSerie().equals(serie.getPrefijoSerie())){
								filtro.getComprobante().setNumero(serie.getNumeroComprobante());
								encontrado = true;
							}
						}
						request.getSession().setAttribute("SessionMapSerieComprobanteFacturaBoleta",mapSerie);
						if (!encontrado){
							TblSerie serie =listaSerie.get(0);
							filtro.getComprobante().setSerie(serie.getPrefijoSerie());
							filtro.getComprobante().setNumero(serie.getNumeroComprobante());
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	private void inicializaDatosComprobante(Filtro filtro ){
		filtro.setHoraEmision(UtilSGT.getHora());
		filtro.setFechaEmision(UtilSGT.getFecha("dd/MM/yyyy"));
		filtro.setFechaVencimiento(UtilSGT.getFecha("dd/MM/yyyy"));
		filtro.setFlagMostrarDetalleFormaPago(Constantes.TIPO_NO);
	}


	@Override
	public void preGuardar(TblComprobante entidad, HttpServletRequest request) {
		LOGGER.debug("[preGuardar] Inicio" );
		entidad.setAuditoriaCreacion(request);
		entidad.setTblEmpresa(empresaDao.findOne(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ));
		LOGGER.debug("[preGuardar] Fin" );

	}
	/*
	 * Campos de auditoria
	 */
	public void preGuardarListado(Filtro filtro, HttpServletRequest request){
		LOGGER.debug("[preGuardarListado] Inicio");

		this.preGuardar(filtro.getComprobante(), request);
		for(TblDetalleComprobante detalle: filtro.getListaDetalle()){
			detalle.setAuditoriaCreacion(request);
		}
		LOGGER.debug("[preGuardarListado] Fin");
	}
	/*
	 * Campos de auditoria para la forma de pago
	 */
	public void preGuardarFormaPago(Filtro filtro, HttpServletRequest request, ArrayList<TblDetalleFormaPago> listaDetalleFormaPago){
		LOGGER.debug("[preGuardarFormaPago] Inicio");
		filtro.getFormaPago().setAuditoriaCreacion(request);
		
		for(TblDetalleFormaPago detalle: listaDetalleFormaPago){
			detalle.setAuditoriaCreacion(request);
		}
		LOGGER.debug("[preGuardarFormaPago] Fin");
	}
	public void preGuardarListadoTributos(Filtro filtro, HttpServletRequest request){
		LOGGER.debug("[preGuardarListadoTributos] Inicio" );
		
		this.preGuardar(filtro.getComprobante(), request);
		for(TblTributoGeneral detalle: filtro.getListaTributo()){
			detalle.setAuditoriaCreacion(request);
		}
		LOGGER.debug("[preGuardarListadoTributos] Fin" );
	}
	/*
	 * Campos de auditoria
	 */
	public void preGuardarLeyenda(TblLeyenda entidad, HttpServletRequest request){
		entidad.setAuditoriaCreacion(request);
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
	 * Validacion del negocio
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
				if (entidad.getComprobante().getValorOpGratuitas() != null && entidad.getComprobante().getValorOpGratuitas().doubleValue() > 0 ){
					for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
						if (!detalle.getTipoAfectacion().equals(Constantes.SUNAT_EXONERADO_OPERACION_GRATUITA) &&
								!detalle.getTipoAfectacion().equals(Constantes.SUNAT_INAFECTO_OPERACION_GRATUITA)){
							model.addAttribute("respuesta", "Si existe Valor referencial unitario en la operacion no onerosas con monto mayor a cero, la operacion debe ser gratuita ");
							exitoso = false;
							return exitoso;
							
						}
					}
				}
				//Validación de la forma de pago
				if (this.validarFormaPago(model, entidad, request)){
					return exitoso;
				}else{
					exitoso = false;
					return exitoso;
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
	/*Validación de la forma de pago*/
	@SuppressWarnings("unchecked")
	private boolean validarFormaPago(Model model, Filtro entidad, HttpServletRequest request){
		boolean exitoso = true;
		ArrayList<TblDetalleFormaPago> listaDetalleFormaPago = null;
		BigDecimal total = null;
		if (entidad.getFormaPago()== null || entidad.getFormaPago().getTipo() == null || entidad.getFormaPago().getTipo().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe seleccionar la Forma de Pago");
			return exitoso;
		}
		//if (entidad.getFormaPago().getMonto() == null || entidad.getFormaPago().getMonto().compareTo(new BigDecimal("0")) <=0){
		if (entidad.getFormaPago().getMonto() == null ){
			exitoso = false;
			model.addAttribute("respuesta", "El Monto Neto no puede estar vacio");
			return exitoso;
		}
		if (entidad.getFormaPago().getMoneda() == null || entidad.getFormaPago().getMoneda().equals("")){
			exitoso = false;
			model.addAttribute("respuesta", "Debe seleccionar la Moneda de la Forma de Pago");
			return exitoso;
		}
		if (entidad.getFormaPago().getTipo().equals(Constantes.FORMA_PAGO_CREDITO)){
			listaDetalleFormaPago = (ArrayList<TblDetalleFormaPago>)request.getSession().getAttribute("listaDetalleFormaPagoSession");
			if (listaDetalleFormaPago==null || listaDetalleFormaPago.size()<=0){
				exitoso = false;
				model.addAttribute("respuesta", "Debe Ingresar el detalle de la Forma de Pago");
				return exitoso;
			}
			total = new BigDecimal("0");
			for(TblDetalleFormaPago detalle: listaDetalleFormaPago){
				total = total.add(detalle.getMonto());
			}
			if (!(total.compareTo(entidad.getFormaPago().getMonto()) == 0)){
				exitoso = false;
				model.addAttribute("respuesta", "El Monto Neto no corresponde con la suma de los Montos de Pago ");
				return exitoso;
			}
		}
		return exitoso;
	}
	@Override
	public TblComprobante getNuevaEntidad() {
		return new TblComprobante();
	}
	/*Actualizamos la serie luego de registrar el comprobante*/
	public void actualizarSerie(String tipoComprobante, String numeroSerie, String numeroComprobante, Integer entidad, HttpServletRequest request){
		List<TblSerie> listaSerie = null;
		TblSerie serie = null;
		Integer nuevoNumero = null;
		listaSerie = serieDao.buscarOneByNombre(tipoComprobante, numeroSerie, entidad);
		if(listaSerie!=null && !listaSerie.isEmpty()){
			nuevoNumero = Integer.parseInt(numeroComprobante)+1;
			serie = listaSerie.get(0);
			serie.setAuditoriaModificacion(request);
			serie.setNumeroComprobante(UtilSGT.completarCeros(nuevoNumero.toString(), Constantes.SUNAT_LONGITUD_NUMERO));
			serie.setSecuencialSerie(nuevoNumero);
			serieDao.save(serie);
		}
	}
	/* Generamos los nombre de los archivos de forma de pago*/
	private void generarNombreFormaPago(Filtro entidad, TblComprobante comprobante){
		entidad.setNombreFileFormaPago(Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_FORMA_PAGO);
		entidad.setNombreFileDetalleFormaPago(Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_DETALLE_FORMA_PAGO);
		
	}
	/**
	 * Se encarga de guardar la informacion del Comprobante
	 * 
	 * @param comprobanteBean
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/sfs12/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, Filtro entidad, HttpServletRequest request) {
		String path = "operacion/sfs12/sfs_listado";
		boolean exitoso 						= false;
		TblComprobante comprobante				= null;
		TblSunatCabecera cabecera				= null;
		TblSunatDetalle detalleSunat			= null;
		List<TblSunatDetalle> listaDetalle		= new ArrayList<TblSunatDetalle>();
		List<TblDetalleComprobante> listaDetComp = null;
		List<TblSunatTributoGeneral> listaTriSunat = new ArrayList<>();
		TblSunatTributoGeneral tributoSunat		= null;
		String nombreLeyendaFile				= "";
		TblTributoGeneral tributoIgv			= new TblTributoGeneral();
		TblTributoGeneral tributoExo			= new TblTributoGeneral();
		TblTributoGeneral tributoIna			= new TblTributoGeneral();
		TblTributoGeneral tributoGra			= new TblTributoGeneral();
		ArrayList<TblDetalleFormaPago> listaDetalleFormaPago = null;
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			listaDetComp  = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			listaDetalleFormaPago = (ArrayList<TblDetalleFormaPago>)request.getSession().getAttribute("listaDetalleFormaPagoSession");
			entidad.setListaDetalle(listaDetComp);
			entidad.setListaDetalleFormaPago(listaDetalleFormaPago);
			
			entidad.setListaTributo(new ArrayList<>());
			entidad.getComprobante().setFechaEmision( UtilSGT.getDateStringFormat(UtilSGT.getDatetoString(entidad.getFechaEmision())) );
			entidad.getComprobante().setHoraEmision(entidad.getHoraEmision());
			if (entidad.getFechaVencimiento()!=null && !entidad.getFechaVencimiento().equals("")){
				entidad.getComprobante().setFechaVencimiento(UtilSGT.getDateStringFormat(UtilSGT.getDatetoString(entidad.getFechaVencimiento())));
			}else{
				entidad.getComprobante().setFechaVencimiento("");
			}
			
			if (this.validarNegocioListado(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				OperacionUtil.calculoDetalleComprobante(entidad);
				OperacionUtil.calculoCabeceraComprobante(entidad);
				OperacionUtil.setDatosComprobanteSFS12(entidad);
				OperacionUtil.setDatosDetalleComprobanteSFS12(entidad);
				OperacionUtil.setDatosTributosComprobanteSFS12(entidad, tributoIgv, tributoExo, tributoIna, tributoGra);
				this.preGuardarListado(entidad, request);
				this.preGuardarFormaPago(entidad, request, listaDetalleFormaPago);
				this.preGuardarLeyenda(entidad.getLeyendaSunat(), request);
				this.preGuardarListadoTributos(entidad, request);
				entidad.getComprobante().setCodigoVerificacion(UUID.randomUUID().toString());

				//Guardar el comprobante
				exitoso = super.guardar(entidad.getComprobante(), model);
				//Buscar Comprobante
				if (exitoso){
					this.actualizarSerie(entidad.getComprobante().getTipoComprobante(), entidad.getComprobante().getSerie(), entidad.getComprobante().getNumero(), entidad.getComprobante().getTblEmpresa().getCodigoEntidad(), request);
					comprobante = comprobanteDao.obtenerComprobante(entidad.getComprobante().getCodigoVerificacion());
					for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
						LOGGER.debug("[guardarEntidad] unidad:" +detalle.getUnidadMedida());
						detalle.setTblComprobante(comprobante);
						detalleComprobanteDao.save(detalle);
						exitoso = true;
					}
					//registro de los tributos
					for(TblTributoGeneral detalle: entidad.getListaTributo()){
						detalle.setTblComprobante(comprobante);
						detalle.setCodigoComprobante(comprobante.getCodigoComprobante());
						tributoGeneralDAO.save(detalle);
						exitoso = true;
					}
					//Leyenda
					if(exitoso){
						nombreLeyendaFile = Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA;
						if (entidad.getLeyendaSunat()!=null && !entidad.getLeyendaSunat().getCodigoSunat().equals("")){
							entidad.getLeyendaSunat().setTblComprobante(comprobante);
							entidad.getLeyendaSunat().setNombreArchivo(entidad.getRuc()+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA);
							leyendaDao.save(entidad.getLeyendaSunat());
						}
						exitoso = true;
					}
					//Forma de Pago
					entidad.getFormaPago().setTblComprobante(comprobante);
					TblFormaPago tblFormaPago = formaPagoDao.save(entidad.getFormaPago());
					if (entidad.getFormaPago().getTipo().equals(Constantes.FORMA_PAGO_CREDITO)){
						for(TblDetalleFormaPago detalle: listaDetalleFormaPago){
							detalle.setTblFormaPago(tblFormaPago);
							detalleFormaPagoDao.save(detalle);
						}
					}
					LOGGER.debug("[guardarEntidad] Inicio del registro de la cabecera Sunat:"+exitoso);
					if (exitoso){
						
						//Grabar Datos para la sunat
						cabecera = this.registrarCabeceraSunat(comprobante, request, entidad);
						LOGGER.debug("[guardarEntidad] cabecera:"+cabecera);
						if (cabecera !=null){
							LOGGER.debug("[guardarEntidad] Inicio del registro del detalle sunat.....:");
							//registro del detalle de la sunat
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
							//registro de los tributos para la sunat
							for(TblTributoGeneral tblTributoGeneral: entidad.getListaTributo()){
								tributoSunat = this.registroTributoSunat(cabecera, tblTributoGeneral, request, entidad);
								if (tributoSunat !=null){
									exitoso = true;
									listaTriSunat.add(tributoSunat);
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
									if (this.generarArchivoTributo(listaTriSunat, entidad)){
										if (this.generarArchivoLeyenda(entidad.getLeyendaSunat(),comprobante, nombreLeyendaFile, entidad)){
											//Generar adicional del detalle
											/**SE DEBE ANALIZAR LA NECESIDAD DEL ARCHIVO ADICIONAL - SUNAT EN LA VERSION 1.2 MODIFICO EL ARCHIVO POR ELLO SE COMENTA*/
											/*if (this.generarArchivoAdicionalDetalle(entidad)){
												model.addAttribute("respuesta", "Se generó el registro exitosamente");
											}else{
												model.addAttribute("respuesta", "Se generó un error en la creacion del detalle adicional [gratuita] del archivo SUNAT");
											}*/
											//Forma de Pago
											generarNombreFormaPago(entidad, comprobante);
											if (this.generarArchivoFormaPago(entidad)){
												if (entidad.getFormaPago().getTipo().equals(Constantes.FORMA_PAGO_CREDITO)){
													int contador = 0;
													for(TblDetalleFormaPago detalle: listaDetalleFormaPago){
														if (!this.generarArchivoDetalleFormaPago(entidad, detalle,contador)){
															model.addAttribute("respuesta", "Se generó un error en la creacion del detalle de forma de pago del archivo SUNAT");
														}
														contador++;
													}
												}
											}else{
												model.addAttribute("respuesta", "Se generó un error en la creacion de la forma de pago del archivo SUNAT");
											}
											
										}else{
											model.addAttribute("respuesta", "Se generó un error en la creacion de la leyenda del archivo SUNAT");
										}
									}else{
										model.addAttribute("respuesta", "Se generó un error en la creacion del tributo en el archivo SUNAT");
									}

								}else{
									model.addAttribute("respuesta", "Se generó un error en la creacion del detalle del archivo SUNAT");
								}
							}else{
								model.addAttribute("respuesta", "Se generó un error en el regitro de la cabecera del archivo SUNAT");
							}

						}else{
							model.addAttribute("respuesta", "Se generó un error en el regitro del detalle de datos a la SUNAT");
							path = "operacion/sfs12/sfs_nuevo";
							model.addAttribute("filtro", entidad);
						}
						//Generar Archivo
						path = "operacion/sfs12/sfs_listado";
						this.listarComprobante(model, entidad,new PageableSG(), this.urlPaginado, request);
					}else{
						path = "operacion/sfs12/sfs_nuevo";
						model.addAttribute("filtro", entidad);
					}
				}


			}else{
				path = "operacion/sfs12/sfs_nuevo";
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
	
	/**
	 * Se encarga de guardar la informacion del Comprobante
	 * 
	 * @param comprobanteBean
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/sfs12/nuevo/guardar/Gre", method = RequestMethod.POST)
	public String guardarEntidadGuiaRemision(Model model, Filtro entidad, HttpServletRequest request) {
		String path = "operacion/sfs12/sfs_listado";
		boolean exitoso 						= false;
		TblComprobante comprobante				= null;
		TblSunatCabecera cabecera				= null;
		TblSunatDetalle detalleSunat			= null;
		List<TblSunatDetalle> listaDetalle		= new ArrayList<TblSunatDetalle>();
		List<TblDetalleComprobante> listaDetComp = null;
		List<TblSunatTributoGeneral> listaTriSunat = new ArrayList<>();
		TblSunatTributoGeneral tributoSunat		= null;
		String nombreLeyendaFile				= "";
		TblTributoGeneral tributoIgv			= new TblTributoGeneral();
		TblTributoGeneral tributoExo			= new TblTributoGeneral();
		TblTributoGeneral tributoIna			= new TblTributoGeneral();
		TblTributoGeneral tributoGra			= new TblTributoGeneral();
		ArrayList<TblDetalleFormaPago> listaDetalleFormaPago = null;
		Map<Integer, Integer> mapFacturaAsociada = new HashMap<>();
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			listaDetComp  = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			listaDetalleFormaPago = (ArrayList<TblDetalleFormaPago>)request.getSession().getAttribute("listaDetalleFormaPagoSession");
			entidad.setListaDetalle(listaDetComp);
			entidad.setListaDetalleFormaPago(listaDetalleFormaPago);
			
			entidad.setListaTributo(new ArrayList<>());
			entidad.getComprobante().setFechaEmision( UtilSGT.getDateStringFormat(UtilSGT.getDatetoString(entidad.getFechaEmision())) );
			entidad.getComprobante().setHoraEmision(entidad.getHoraEmision());
			if (entidad.getFechaVencimiento()!=null && !entidad.getFechaVencimiento().equals("")){
				entidad.getComprobante().setFechaVencimiento(UtilSGT.getDateStringFormat(UtilSGT.getDatetoString(entidad.getFechaVencimiento())));
			}else{
				entidad.getComprobante().setFechaVencimiento("");
			}
			
			if (this.validarNegocioListado(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				OperacionUtil.calculoDetalleComprobante(entidad);
				OperacionUtil.calculoCabeceraComprobante(entidad);
				OperacionUtil.setDatosComprobanteSFS12(entidad);
				OperacionUtil.setDatosDetalleComprobanteSFS12(entidad);
				OperacionUtil.setDatosTributosComprobanteSFS12(entidad, tributoIgv, tributoExo, tributoIna, tributoGra);
				this.preGuardarListado(entidad, request);
				this.preGuardarFormaPago(entidad, request, listaDetalleFormaPago);
				this.preGuardarLeyenda(entidad.getLeyendaSunat(), request);
				this.preGuardarListadoTributos(entidad, request);
				entidad.getComprobante().setCodigoVerificacion(UUID.randomUUID().toString());

				//Guardar el comprobante
				exitoso = super.guardar(entidad.getComprobante(), model);
				//Buscar Comprobante
				if (exitoso){
					this.actualizarSerie(entidad.getComprobante().getTipoComprobante(), entidad.getComprobante().getSerie(), entidad.getComprobante().getNumero(), entidad.getComprobante().getTblEmpresa().getCodigoEntidad(), request);
					comprobante = comprobanteDao.obtenerComprobante(entidad.getComprobante().getCodigoVerificacion());
					for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
						LOGGER.debug("[guardarEntidad] SerieNumeroRemision:" +detalle.getSerieNumeroRemision());
						detalle.setTblComprobante(comprobante);
						detalleComprobanteDao.save(detalle);
						exitoso = true;
						//actualizar la cantidad en la remision
						TblDetalleRemision detalleRemision = actualizarCantidadRemision(detalle,request);
						if (detalleRemision!= null) {
							mapFacturaAsociada.put(detalleRemision.getTblFacturaAsociada().getTblRemision().getCodigoRemision(), detalleRemision.getTblFacturaAsociada().getTblRemision().getCodigoRemision());
						}
					}
					//Si se completo todos los productos de la guia se activa el flag para no ser mostrado en la lista
					actualizarFlagCantidadProductoGuiaRemision(mapFacturaAsociada,request);
					//registro de los tributos
					for(TblTributoGeneral detalle: entidad.getListaTributo()){
						detalle.setTblComprobante(comprobante);
						detalle.setCodigoComprobante(comprobante.getCodigoComprobante());
						tributoGeneralDAO.save(detalle);
						exitoso = true;
					}
					//Leyenda
					if(exitoso){
						nombreLeyendaFile = Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA;
						if (entidad.getLeyendaSunat()!=null && !entidad.getLeyendaSunat().getCodigoSunat().equals("")){
							entidad.getLeyendaSunat().setTblComprobante(comprobante);
							entidad.getLeyendaSunat().setNombreArchivo(entidad.getRuc()+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA);
							leyendaDao.save(entidad.getLeyendaSunat());
						}
						exitoso = true;
					}
					//Forma de Pago
					entidad.getFormaPago().setTblComprobante(comprobante);
					TblFormaPago tblFormaPago = formaPagoDao.save(entidad.getFormaPago());
					if (entidad.getFormaPago().getTipo().equals(Constantes.FORMA_PAGO_CREDITO)){
						for(TblDetalleFormaPago detalle: listaDetalleFormaPago){
							detalle.setTblFormaPago(tblFormaPago);
							detalleFormaPagoDao.save(detalle);
						}
					}
					LOGGER.debug("[guardarEntidad] Inicio del registro de la cabecera Sunat:"+exitoso);
					if (exitoso){
						
						//Grabar Datos para la sunat
						cabecera = this.registrarCabeceraSunat(comprobante, request, entidad);
						LOGGER.debug("[guardarEntidad] cabecera:"+cabecera);
						if (cabecera !=null){
							LOGGER.debug("[guardarEntidad] Inicio del registro del detalle sunat.....:");
							//registro del detalle de la sunat
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
							//registro de los tributos para la sunat
							for(TblTributoGeneral tblTributoGeneral: entidad.getListaTributo()){
								tributoSunat = this.registroTributoSunat(cabecera, tblTributoGeneral, request, entidad);
								if (tributoSunat !=null){
									exitoso = true;
									listaTriSunat.add(tributoSunat);
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
									if (this.generarArchivoTributo(listaTriSunat, entidad)){
										if (this.generarArchivoLeyenda(entidad.getLeyendaSunat(),comprobante, nombreLeyendaFile, entidad)){
											//Generar adicional del detalle
											/**SE DEBE ANALIZAR LA NECESIDAD DEL ARCHIVO ADICIONAL - SUNAT EN LA VERSION 1.2 MODIFICO EL ARCHIVO POR ELLO SE COMENTA*/
											/*if (this.generarArchivoAdicionalDetalle(entidad)){
												model.addAttribute("respuesta", "Se generó el registro exitosamente");
											}else{
												model.addAttribute("respuesta", "Se generó un error en la creacion del detalle adicional [gratuita] del archivo SUNAT");
											}*/
											//Forma de Pago
											generarNombreFormaPago(entidad, comprobante);
											if (this.generarArchivoFormaPago(entidad)){
												if (entidad.getFormaPago().getTipo().equals(Constantes.FORMA_PAGO_CREDITO)){
													int contador = 0;
													for(TblDetalleFormaPago detalle: listaDetalleFormaPago){
														if (!this.generarArchivoDetalleFormaPago(entidad, detalle,contador)){
															model.addAttribute("respuesta", "Se generó un error en la creacion del detalle de forma de pago del archivo SUNAT");
														}
														contador++;
													}
												}
											}else{
												model.addAttribute("respuesta", "Se generó un error en la creacion de la forma de pago del archivo SUNAT");
											}
											
										}else{
											model.addAttribute("respuesta", "Se generó un error en la creacion de la leyenda del archivo SUNAT");
										}
									}else{
										model.addAttribute("respuesta", "Se generó un error en la creacion del tributo en el archivo SUNAT");
									}

								}else{
									model.addAttribute("respuesta", "Se generó un error en la creacion del detalle del archivo SUNAT");
								}
							}else{
								model.addAttribute("respuesta", "Se generó un error en el regitro de la cabecera del archivo SUNAT");
							}

						}else{
							model.addAttribute("respuesta", "Se generó un error en el regitro del detalle de datos a la SUNAT");
							path = "operacion/sfs12/sfs_gre_nuevo";
							model.addAttribute("filtro", entidad);
						}
						//Generar Archivo
						path = "operacion/sfs12/sfs_listado";
						this.listarComprobante(model, entidad,new PageableSG(), this.urlPaginado, request);
					}else{
						path = "operacion/sfs12/sfs_gre_nuevo";
						model.addAttribute("filtro", entidad);
					}
				}


			}else{
				path = "operacion/sfs12/sfs_gre_nuevo";
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
	private void actualizarFlagCantidadProductoGuiaRemision(Map<Integer, Integer> mapFacturaAsociada,HttpServletRequest request) {
		TblRemision remision 							= null;
		List<TblFacturaAsociada> listaFacturaAsociada 	= null;
		List<TblDetalleRemision> listaDetRemision		= null;
		boolean flagOkCantidad							= true;
		if (mapFacturaAsociada != null && mapFacturaAsociada.size()>0) {
			for (Map.Entry<Integer, Integer> entry : mapFacturaAsociada.entrySet()) {
				Integer codigoGuiaRemision = entry.getKey();
				LOGGER.debug("[actualizarFlagCantidadProductoGuiaRemision] codigoGuiaRemision: " + codigoGuiaRemision);
	            remision = remisionDao.findOne(codigoGuiaRemision);
	    		listaFacturaAsociada = facturaAsociadaDao.findAllxIdRemision(remision.getCodigoRemision());
	    		flagOkCantidad	= true;
	    		if (listaFacturaAsociada!=null) {

	    			for(TblFacturaAsociada factura: listaFacturaAsociada) {
	    				listaDetRemision = detalleRemisionDao.findAllxIdFacturaAsociada(factura.getCodigoFacturaAsociada());
	    				for(TblDetalleRemision detRemision: listaDetRemision) {
	    					if (detRemision.getCantidad().compareTo(detRemision.getCantidadFacturada()==null?new BigDecimal("0"):detRemision.getCantidadFacturada())!=0) {
	    						flagOkCantidad	= false;
	    						LOGGER.debug("[actualizarFlagCantidadProductoGuiaRemision] cantidad: " + detRemision.getCantidad() + " - cantidadFacturada: "+detRemision.getCantidadFacturada());
	    						break;
	    					}
	    				}
	    				if (!flagOkCantidad) {
	    					break;
	    				}
	    			}
	    			if (flagOkCantidad) {
	    				remision.setFlagFacturaOk(Constantes.ESTADO_ACTIVO);
	    				remision.setAuditoriaModificacion(request);
	    				remisionDao.save(remision);
	    			}
	    		}
	            
	        }
		}
		
	}


	/*Actualizar las cantidades de los productos en la guia de remision*/
	private TblDetalleRemision actualizarCantidadRemision(TblDetalleComprobante detalle,HttpServletRequest request) {
		LOGGER.debug("[actualizarCantidadRemision] Inicio" );
		LOGGER.debug("[actualizarCantidadRemision] Codigo y Serie:" +detalle.getCodigoDetalleRemision()+ ":"+ detalle.getSerieNumeroRemision());
		TblDetalleRemision detalleRemision = detalleRemisionDao.findOne(detalle.getCodigoDetalleRemision());
		if (detalleRemision!= null && detalleRemision.getCantidadFacturada()!= null) {
			detalleRemision.setCantidadFacturada(detalleRemision.getCantidadFacturada().add(detalle.getCantidad()));
			detalleRemision.setAuditoriaModificacion(request);
			detalleRemisionDao.save(detalleRemision);
		}else if (detalleRemision!= null && detalleRemision.getCantidadFacturada() == null) {
			detalleRemision.setCantidadFacturada(detalle.getCantidad());
			detalleRemision.setAuditoriaModificacion(request);
			detalleRemisionDao.save(detalleRemision);
		}else {
			LOGGER.debug("[actualizarCantidadRemision] ERRROR HUSTON HAVE A PROBLEM........." );
		}
		LOGGER.debug("[actualizarCantidadRemision] Fin" );
		return detalleRemision;
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
			cabecera.setHoraEmision(comprobante.getHoraEmision());
			cabecera.setFechaVencimiento(comprobante.getFechaVencimiento());
			//Domicilio Fiscal
			cabecera.setDomicilioFiscal(new Integer(comprobante.getCodigoDomicilio()));
			//Datos del Cliente
			cabecera.setTipoDocumentoUsuario(comprobante.getTipoDocumento());
			cabecera.setNumeroDocumento(comprobante.getNumeroDocumento());
			cabecera.setRazonSocial(comprobante.getNombreCliente());
			//Moneda
			cabecera.setTipoMoneda(comprobante.getMoneda());

			//sumatoria tributos
			cabecera.setSumTributo(UtilSGT.getRoundDecimalString(comprobante.getSumTributo(), 2));
			//total valor venta
			cabecera.setTotValorVenta(UtilSGT.getRoundDecimalString(comprobante.getTotValorVenta(), 2));
			//total precio venta
			cabecera.setTotPrecioVenta(UtilSGT.getRoundDecimalString(comprobante.getTotPrecioVenta(), 2));
			//total descuento
			cabecera.setTotDescuento(UtilSGT.getRoundDecimalString(comprobante.getTotalDescuento(), 2));
			//sumatoria otros cargos
			cabecera.setSumOtrosCargos(UtilSGT.getRoundDecimalString(comprobante.getSumOtrosCargos(), 2));
			//anticipos
			cabecera.setTotAnticipos(UtilSGT.getRoundDecimalString(comprobante.getTotAnticipos(), 2));
			//importe total de la venta
			cabecera.setImpTotalVenta(UtilSGT.getRoundDecimalString(comprobante.getImpTotalVenta(), 2));

			cabecera.setVersionUbl(comprobante.getVersionUbl());
			cabecera.setCustomizacionDoc(comprobante.getCustomizacionDoc());


			//Lo que sigue esta obsoleto
			//Descuentos Globales
			cabecera.setSumaDescuento(UtilSGT.getRoundDecimal(comprobante.getDescuentosGlobales(),2));
			cabecera.setSumaCargo(UtilSGT.getRoundDecimal(comprobante.getTotalOtrosCargos(),2));
			cabecera.setTotalDescuento(UtilSGT.getRoundDecimal(comprobante.getTotalDescuento(),2));
			cabecera.setOperacionGravada(UtilSGT.getRoundDecimal(comprobante.getTotalOpGravada(),2));
			//Total valor de venta - Operaciones inafectas
			cabecera.setOperacionInafecta(UtilSGT.getRoundDecimal(comprobante.getTotalOpInafecta(),2));
			//Total valor de venta - Operaciones exoneradas
			cabecera.setOperacionExonerada(UtilSGT.getRoundDecimal(comprobante.getTotalOpExonerada(),2));
			//Sumatoria IGV
			cabecera.setMontoIgv(UtilSGT.getRoundDecimal(comprobante.getTotalIgv(),2));
			//Sumatoria ISC
			cabecera.setMontoIsc(UtilSGT.getRoundDecimal(comprobante.getSumatoriaIsc(),2));
			//Sumatoria otros tributos
			cabecera.setOtrosTributos(UtilSGT.getRoundDecimal(comprobante.getSumatorioaOtrosTributos(),2));
			//Importe total de la venta, cesión en uso o del servicio prestado
			cabecera.setImporteTotal(UtilSGT.getRoundDecimal(comprobante.getTotalImporte(),2));
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
			temporal = OperacionUtil.obtenerTotalMontoGravada(detalleComprobante.getPrecioFinal(), 0 , entidad.getValorServicio()); //IGV = 0
			temporal = temporal.divide(new BigDecimal(detalleComprobante.getCantidad().toString()), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);

			detalle.setValorUnitario(temporal.toString());
			//Descuentos por item
			BigDecimal descuento = new BigDecimal((detalleComprobante.getPrecioTotal().doubleValue()*detalleComprobante.getDescuento().doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
			detalle.setDescuento(descuento.toString());
			//Monto de IGV por ítem
			//detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), Constantes.SUNAT_IGV).toString());
			detalle.setMontoIgv(OperacionUtil.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), 0 , entidad.getValorServicio()).toString()); //IGV = 0
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
	private TblSunatTributoGeneral registroTributoSunat(TblSunatCabecera cabecera, TblTributoGeneral tblTributoGeneral, HttpServletRequest request, Filtro entidad){
		TblSunatTributoGeneral tblSunatTributoGeneral = new TblSunatTributoGeneral();

		tblSunatTributoGeneral.setIdentificadorTributo(tblTributoGeneral.getIdentificadorTributo());
		tblSunatTributoGeneral.setNombreTributo(tblTributoGeneral.getNombreTributo());
		tblSunatTributoGeneral.setCodigoTipoTributo(tblTributoGeneral.getCodigoTipoTributo());
		tblSunatTributoGeneral.setBaseImponible(UtilSGT.getRoundDecimalString(tblTributoGeneral.getBaseImponible(), 2));
		tblSunatTributoGeneral.setMontoTributoItem(UtilSGT.getRoundDecimalString(tblTributoGeneral.getMontoTributoItem(), 2));

		this.preGuardarSunatTributo(tblSunatTributoGeneral, request);
		tblSunatTributoGeneral.setTblSunatCabecera(cabecera);
		tblSunatTributoGeneral.setCodigoCabecera(cabecera.getCodigoCabecera());
		tblSunatTributoGeneral.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+entidad.getComprobante().getTipoComprobante()+"-"+entidad.getComprobante().getSerie()+"-"+entidad.getComprobante().getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_TRIBUTO);
		sunatTributoGeneralDAO.save(tblSunatTributoGeneral);

		return tblSunatTributoGeneral;
	}
	private TblSunatDetalle calculoDetalleSunatNacional(TblSunatCabecera cabecera, TblComprobante comprobante, TblDetalleComprobante detalleComprobante, HttpServletRequest request, Filtro entidad){
		TblSunatDetalle detalle 				= null;
		//BigDecimal temporal						= null;
		try{
			//registro del detalle del comprobante
			detalle = new TblSunatDetalle();
			//Código de unidad de medida por ítem
			detalle.setCodigoUnidad(detalleComprobante.getUnidadMedida());
			//Cantidad de unidades por ítem
			detalle.setCantidad(UtilSGT.getRoundDecimalString(detalleComprobante.getCantidad(), 2));
			//Código de producto
			detalle.setCodigoProducto(detalleComprobante.getCodigoProducto());
			//Codigo producto SUNAT
			detalle.setCodigoProductoSunat(Constantes.SUNAT_SIN_CODIGO);
			//Descripción detallada del servicio prestado, bien vendido o cedido en uso, indicando las características.
			detalle.setDescripcion(detalleComprobante.getDescripcion());

			//valor unitario
			detalle.setValorUnitario(UtilSGT.getRoundDecimalString(detalleComprobante.getValorUnitario(), 4));
			//Sumatoria tributos por item
			detalle.setSumTributosItem(UtilSGT.getRoundDecimalString(detalleComprobante.getSumTributosItem(), 2));

			//Tributo: Códigos de tipos de tributos IGV
			detalle.setTribCodTipoTributoIgv(detalleComprobante.getTribCodTipoTributoIgv());
			//Tributo: Monto de IGV por ítem
			detalle.setTribMontoIgv(UtilSGT.getRoundDecimalString(detalleComprobante.getTribMontoIgv(), 2));
			//Tributo: Base Imponible IGV por Item
			detalle.setTribBaseImponibleIgv(UtilSGT.getRoundDecimalString(detalleComprobante.getTribBaseImponibleIgv(), 2));
			//Tributo: Nombre de tributo por item
			detalle.setTribNombreTributo(detalleComprobante.getTribNombreTributo());
			//Tributo: Código de tipo de tributo por Item
			detalle.setTribCodTipoTributo(detalleComprobante.getTribCodTipoTributo());
			//Tributo: Afectación al IGV por ítem
			detalle.setTribAfectacionIgv(detalleComprobante.getTribAfectacionIgv());
			//Tributo: Porcentaje de IGV
			detalle.setTribPorcentajeIgv(detalleComprobante.getTribPorcentajeIgv());
			//Tributo ISC: Códigos de tipos de tributos ISC
			detalle.setIscCodTipoTributoIsc(detalleComprobante.getIscCodTipoTributoIsc());
			//Tributo ISC: Monto de ISC por ítem
			detalle.setIscMontoIsc(UtilSGT.getRoundDecimalString(detalleComprobante.getIscMontoIsc(), 2));
			//Tributo ISC: Base Imponible ISC por Item
			detalle.setIscBaseImponibleIsc(UtilSGT.getRoundDecimalString(detalleComprobante.getIscBaseImponibleIsc(), 2));
			//Tributo ISC: Nombre de tributo por item
			detalle.setIscNombreTributo(detalleComprobante.getIscNombreTributo());
			//Tributo ISC: Código de tipo de tributo por Item
			detalle.setIscCodTipoTributo(detalleComprobante.getIscCodTipoTributo());
			//Tributo ISC: Tipo de sistema ISC
			detalle.setIscTipoSistema(detalleComprobante.getIscTipoSistema());
			//Tributo ISC: Porcentaje de ISC
			detalle.setIscPorcentaje(detalleComprobante.getIscPorcentaje().toString());
			//Tributo Otro: Códigos de tipos de tributos OTRO
			detalle.setOtroCodTipoTributoOtro(detalleComprobante.getOtroBaseImponibleOtro().toString());
			//Tributo Otro: Monto de tributo OTRO por iItem
			detalle.setOtroMontoTributo(UtilSGT.getRoundDecimalString(detalleComprobante.getOtroMontoTributo(), 2));
			//Tributo Otro: Base Imponible de tributo OTRO por Item
			detalle.setOtroBaseImponibleOtro(UtilSGT.getRoundDecimalString(detalleComprobante.getOtroBaseImponibleOtro(), 2));
			//Tributo Otro:  Nombre de tributo OTRO por item
			detalle.setOtroNombreTributo(detalleComprobante.getOtroNombreTributo());
			//Tributo Otro: Código de tipo de tributo OTRO por Item
			detalle.setOtroCodTipoTributo(detalleComprobante.getOtroCodTipoTributo());
			//Tributo Otro: Porcentaje de tributo OTRO por Item
			detalle.setOtroPorcentaje(detalleComprobante.getOtroPorcentaje());
			//Precio de venta unitario 
			detalle.setPrecioVentaUnitario(UtilSGT.getRoundDecimalString(detalleComprobante.getPrecioVentaUnitario(), 4));
			//Valor de venta por Item 
			detalle.setValorVentaItem(UtilSGT.getRoundDecimalString(detalleComprobante.getValorVentaItem(), 2));
			//Valor REFERENCIAL unitario (gratuitos) 
			detalle.setValorReferencialUnitario(UtilSGT.getRoundDecimalString(detalleComprobante.getValorReferencialUnitario(), 2));

			//Resto de datos obsoletos		


			//Valor unitario por ítem
			//detalle.setValorUnitario(cabecera.getOperacionGravada().toString());
			//detalle.setValorUnitario(this.obtenerTotalMontoGravada(detalleComprobante.getPrecioTotal(), Constantes.SUNAT_IGV).toString());
			//temporal = this.obtenerTotalMontoGravada(detalleComprobante.getPrecioFinal(), entidad.getValorIGV(), entidad.getValorServicio());
			//temporal = temporal.divide(new BigDecimal(detalleComprobante.getCantidad().toString()) , 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);

			//detalle.setValorUnitario(temporal.toString());
			//Descuentos por item
			BigDecimal descuento = new BigDecimal((detalleComprobante.getPrecioTotal().doubleValue()*detalleComprobante.getDescuento().doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
			detalle.setDescuento(descuento.toString());
			//Monto de IGV por ítem
			//detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), Constantes.SUNAT_IGV).toString());
			detalle.setMontoIgv(UtilSGT.getRoundDecimalString(detalleComprobante.getTribMontoIgv(), 2));
			//Afectación al IGV por ítem
			detalle.setAfectacionIgv(detalleComprobante.getTipoAfectacion());
			//Monto de ISC por ítem
			detalle.setMontoIsc("0");
			//Tipo de sistema ISC
			detalle.setTipoIsc("");
			//Precio de venta unitario por item
			//detalle.setPrecioVentaUnitario(detalleComprobante.getPrecioTotal().toString());
			//Valor de venta por ítem
			//detalle.setValorVentaItem(detalleComprobante.getPrecioFinal().toString());
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
		entidad.setAuditoriaCreacion(request);
	}

	public void preGuardarSunatTributo(TblSunatTributoGeneral entidad, HttpServletRequest request) {
		entidad.setAuditoriaCreacion(request);

	}
	
	
	/*
	 * Se asigna los datos del campo de auditoria
	 */
	public void preGuardarSunatCabecera(TblSunatCabecera entidad, HttpServletRequest request) {
		entidad.setAuditoriaCreacion(request);

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
					cabecera.getHoraEmision() + Constantes.SUNAT_PIPE +
					cabecera.getFechaVencimiento() + Constantes.SUNAT_PIPE +
					//cabecera.getDomicilioFiscal() + Constantes.SUNAT_PIPE +
					"0000" + Constantes.SUNAT_PIPE +
					cabecera.getTipoDocumentoUsuario() + Constantes.SUNAT_PIPE +
					cabecera.getNumeroDocumento() + Constantes.SUNAT_PIPE +
					cabecera.getRazonSocial()	+ Constantes.SUNAT_PIPE +
					cabecera.getTipoMoneda() + Constantes.SUNAT_PIPE +
					cabecera.getSumTributo() + Constantes.SUNAT_PIPE +
					cabecera.getTotValorVenta() + Constantes.SUNAT_PIPE +
					cabecera.getTotPrecioVenta() + Constantes.SUNAT_PIPE +
					cabecera.getTotDescuento() + Constantes.SUNAT_PIPE +
					cabecera.getSumOtrosCargos() + Constantes.SUNAT_PIPE +
					cabecera.getTotAnticipos() + Constantes.SUNAT_PIPE +
					cabecera.getImpTotalVenta() + Constantes.SUNAT_PIPE +
					cabecera.getVersionUbl() + Constantes.SUNAT_PIPE +
					cabecera.getCustomizacionDoc();

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
	 * Genera un archivo plano Forma de Pago
	 */
	public boolean generarArchivoFormaPago(Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = entidad.getSunatData() + entidad.getNombreFileFormaPago();
		LOGGER.debug("[generarArchivoFormaPago] filename: "+FILENAME);
		try{
			cadena = entidad.getFormaPago().getTipo()+ Constantes.SUNAT_PIPE +
					entidad.getFormaPago().getMonto()+ Constantes.SUNAT_PIPE +
					entidad.getFormaPago().getMoneda();

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
	 * Genera un archivo plano Detalle de Forma de Pago
	 */
	public boolean generarArchivoDetalleFormaPago(Filtro entidad, TblDetalleFormaPago detalle, int contador){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = entidad.getSunatData() + entidad.getNombreFileDetalleFormaPago();
		LOGGER.debug("[generarArchivoDetalleFormaPago] filename: "+FILENAME);
		try{
			cadena = UtilSGT.getRoundDecimal(detalle.getMonto(), 2) + Constantes.SUNAT_PIPE +
					UtilSGT.getDateStringFormat(detalle.getFecha())+ Constantes.SUNAT_PIPE +
					detalle.getMoneda();

			bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
			if (contador > 0){
				bufferedWriter.newLine();
			}
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
	/*public boolean generarArchivoAdicionalDetalle(Filtro entidad){
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
	}**/
	/*
	 * Genera un archivo plano Detalle
	 */
	public boolean generarArchivoDetalle(List<TblSunatDetalle> listaDetalle, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;//Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
		/*ICBPER:06.06.2020 Inicio*/
		ImpuestoBolsa impuestoBolsa = new ImpuestoBolsa();
		String codigoProducto = null;
		try{
			for(TblSunatDetalle detalle:listaDetalle){
				if (FILENAME == null){
					//FILENAME = Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
					FILENAME = entidad.getSunatData() + detalle.getNombreArchivo();
					bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
				}else{
					bufferedWriter.newLine();
				}
				
				//impuestoBolsa = new ImpuestoBolsa(detalle.getTribCodTipoTributoIgv());
				//Valida si existe el código del producto y le asigna el valor que se encuentra en Descripcion
				codigoProducto = obtenerCodigoProducto(detalle);
				cadena = detalle.getCodigoUnidad() + Constantes.SUNAT_PIPE +
						detalle.getCantidad() + Constantes.SUNAT_PIPE +
						//detalle.getCodigoProducto() + Constantes.SUNAT_PIPE +
						codigoProducto + Constantes.SUNAT_PIPE +
						detalle.getCodigoProductoSunat() + Constantes.SUNAT_PIPE +
						detalle.getDescripcion() + Constantes.SUNAT_PIPE +
						detalle.getValorUnitario()	+ Constantes.SUNAT_PIPE +
						detalle.getSumTributosItem()+ Constantes.SUNAT_PIPE +
						detalle.getTribCodTipoTributoIgv()	+ Constantes.SUNAT_PIPE +
						detalle.getTribMontoIgv()	+ Constantes.SUNAT_PIPE +
						detalle.getTribBaseImponibleIgv()	+ Constantes.SUNAT_PIPE +
						detalle.getTribNombreTributo()	+ Constantes.SUNAT_PIPE +
						detalle.getTribCodTipoTributo() + Constantes.SUNAT_PIPE +
						detalle.getTribAfectacionIgv()	+ Constantes.SUNAT_PIPE +
						detalle.getTribPorcentajeIgv()	+ Constantes.SUNAT_PIPE +
						detalle.getIscCodTipoTributoIsc()	+ Constantes.SUNAT_PIPE +
						detalle.getIscMontoIsc() + Constantes.SUNAT_PIPE +
						detalle.getIscBaseImponibleIsc() + Constantes.SUNAT_PIPE +
						detalle.getIscNombreTributo() + Constantes.SUNAT_PIPE +
						detalle.getIscCodTipoTributo()	+ Constantes.SUNAT_PIPE +
						detalle.getIscTipoSistema() + Constantes.SUNAT_PIPE +
						detalle.getIscPorcentaje()	+ Constantes.SUNAT_PIPE +
						detalle.getOtroCodTipoTributoOtro() + Constantes.SUNAT_PIPE +
						detalle.getOtroMontoTributo()	+ Constantes.SUNAT_PIPE +
						detalle.getOtroBaseImponibleOtro()	+ Constantes.SUNAT_PIPE +
						detalle.getOtroNombreTributo()	+ Constantes.SUNAT_PIPE +
						detalle.getOtroCodTipoTributo() + Constantes.SUNAT_PIPE +
						detalle.getIscPorcentaje()	+ Constantes.SUNAT_PIPE +
						/*Impuesto a la Bolsa Plastica*/
						impuestoBolsa.getCodigoTipo()	+ Constantes.SUNAT_PIPE +
						impuestoBolsa.getMontoxItem()	+ Constantes.SUNAT_PIPE +
						impuestoBolsa.getCantidadBolsasxItem()	+ Constantes.SUNAT_PIPE +
						impuestoBolsa.getNombrexItem()	+ Constantes.SUNAT_PIPE +
						impuestoBolsa.getCodigoTipoxItem()	+ Constantes.SUNAT_PIPE +
						impuestoBolsa.getMontoxUnidad()	+ Constantes.SUNAT_PIPE +
						/*Fin Impuesto a la Bolsa Plastica*/
						detalle.getPrecioVentaUnitario() + Constantes.SUNAT_PIPE +
						detalle.getValorVentaItem() + Constantes.SUNAT_PIPE +
						detalle.getValorReferencialUnitario();
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
	//Valida si el codigo es null, si es asi lo obtiene de la descripcion
	private String obtenerCodigoProducto(TblSunatDetalle detalle) {
		String codigoProducto = null;
		if (detalle.getCodigoProducto() == null || detalle.getCodigoProducto().isEmpty()) {
			codigoProducto = obtenerCodigoDeDescripcion(detalle.getDescripcion());
		}else {
			codigoProducto = detalle.getCodigoProducto();
		}
		return codigoProducto;
	}


	private String obtenerCodigoDeDescripcion(String descripcion) {
		String codigoProducto = null;
		if (descripcion != null && !descripcion.isEmpty()) {
			Integer pos = descripcion.indexOf(":");
			if (pos >=0) {
				codigoProducto = descripcion.substring(0, pos);
			}else {
				codigoProducto = "--";
			}
		}else {
			codigoProducto = "-";
		}
		return codigoProducto;
	}


	/*
	 * Genera un archivo plano Tributo
	 */
	public boolean generarArchivoTributo(List<TblSunatTributoGeneral> listaTributo, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;
		try{
			for(TblSunatTributoGeneral tributo:listaTributo){
				if (FILENAME == null){
					FILENAME = entidad.getSunatData() + tributo.getNombreArchivo();
					bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
				}else{
					bufferedWriter.newLine();
				}
				cadena = tributo.getIdentificadorTributo() + Constantes.SUNAT_PIPE +
						tributo.getNombreTributo() + Constantes.SUNAT_PIPE +
						tributo.getCodigoTipoTributo() + Constantes.SUNAT_PIPE +
						tributo.getBaseImponible() + Constantes.SUNAT_PIPE +
						tributo.getMontoTributoItem();
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
	@RequestMapping(value = "/operacion/sfs12/adicionarDetalle", method = RequestMethod.POST)
	public String adicionarDetalle(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[adicionarDetalle] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			persistirDetalleFormaPago(entidad, request);
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
			OperacionUtil.calculoDetalleComprobante(entidad);
			OperacionUtil.calculoCabeceraComprobante(entidad);
			//Asignamos el monto del importe a la forma de pago
			asignarMontoFormaPago(entidad);
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
	

	/**
	 * Se encarga de direccionar a la pantalla de creacion del Comprobante
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/guia/seleccionar/Gre/{id}", method = RequestMethod.GET)
	public String adicionarDetalleGuiaRemision(@PathVariable Integer id, Model model, HttpServletRequest request) {
		String path = "operacion/sfs12/sfs_gre_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		Filtro entidad = null;
		TblDetalleComprobante tblDetalleComprobante = null;
		try{
			LOGGER.debug("[adicionarDetalleGuiaRemision] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad = (Filtro)request.getSession().getAttribute("filtroSession");
			persistirDetalleFormaPago(entidad, request);
			entidad.setListaDetalle(listaDetalle);
			if (entidad.getListaDetalle()==null){
				entidad.setListaDetalle(new ArrayList<TblDetalleComprobante>());
			}
			//Obtener la lista del detalle de la guia de remision
			List<FacturaAsociadaBean> listaFacAsoVer = obtenerDetalleGuiaRemision(id);
			if (listaFacAsoVer!= null) {
				for(FacturaAsociadaBean facturaAsociadaBean: listaFacAsoVer) {
					tblDetalleComprobante = obtenerDatosProducto(facturaAsociadaBean, request);
					if (tblDetalleComprobante.getCantidad().compareTo(new BigDecimal("0"))>0) {
						entidad.getListaDetalle().add(tblDetalleComprobante);
						entidad.setDetalleComprobante(tblDetalleComprobante);
						LOGGER.debug("[adicionarDetalleGuiaRemision] Adicionando:"+facturaAsociadaBean.getDescripcion()+" : "+facturaAsociadaBean.getCantidad());
						OperacionUtil.calculoDetalleComprobante(entidad);
						OperacionUtil.calculoCabeceraComprobante(entidad);
						//Asignamos el monto del importe a la forma de pago
						asignarMontoFormaPago(entidad);
					}
				}
			}
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[adicionarDetalleGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[adicionarDetalleGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			listaDetalle = null;
		}
		LOGGER.debug("[adicionarDetalleGuiaRemision] Fin");
		return path;
	}

	private TblDetalleComprobante obtenerDatosProducto(FacturaAsociadaBean facturaAsociadaBean, HttpServletRequest request) {
		TblDetalleComprobante tblDetalleComprobante = null;
		tblDetalleComprobante = new TblDetalleComprobante();
		tblDetalleComprobante.setDescripcion(facturaAsociadaBean.getDescripcion());
		tblDetalleComprobante.setUnidadMedida(facturaAsociadaBean.getUnidadMedida());
		tblDetalleComprobante.setCantidad(obtenerCantidadAFacturar(facturaAsociadaBean));
		TblProducto producto = obtenerDatosProducto(facturaAsociadaBean.getCodigoProducto(),request);
		tblDetalleComprobante.setMoneda(Constantes.SUNAT_TIPO_MONEDA_SOLES);
		tblDetalleComprobante.setPrecioUnitario(producto.getPrecio());
		tblDetalleComprobante.setTipoAfectacion(Constantes.SUNAT_AFECTACION_IGV_GRAVADO_OPE_ONEROSO);
		tblDetalleComprobante.setUnidadMedida(facturaAsociadaBean.getUnidadMedida());
		tblDetalleComprobante.setSerieNumeroRemision(facturaAsociadaBean.getSerieRemision()+"-"+facturaAsociadaBean.getNumeroRemision());
		tblDetalleComprobante.setCodigoDetalleRemision(facturaAsociadaBean.getCodigoDetalleRemision());
		tblDetalleComprobante.setCodigoRemision(facturaAsociadaBean.getCodigoRemision());
		return tblDetalleComprobante;
	}


	private TblProducto obtenerDatosProducto(String codigoProducto, HttpServletRequest request) {
		Integer intCodigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
		List<TblProducto> listaProducto = productoDao.listarxCodigoProducto(intCodigoEntidad, codigoProducto);
		if (listaProducto != null) {
			return listaProducto.get(0);
		}
		return null;
	}


	private BigDecimal obtenerCantidadAFacturar(FacturaAsociadaBean facturaAsociadaBean) {
		if (facturaAsociadaBean!=null) {
			if (facturaAsociadaBean.getCantidadFacturada()!=null) {
				return facturaAsociadaBean.getCantidad().subtract(facturaAsociadaBean.getCantidadFacturada()).setScale(2, RoundingMode.HALF_UP);
			}else {
				return facturaAsociadaBean.getCantidad().setScale(2, RoundingMode.HALF_UP);
			}
				
		}
		return new BigDecimal("0");
	}


	private List<FacturaAsociadaBean> obtenerDetalleGuiaRemision(Integer id) {
		TblRemision remision 							= null;
		List<TblFacturaAsociada> listaFacturaAsociada 	= null;
		List<FacturaAsociadaBean> listaFacAsoVer		= null;
		List<TblDetalleRemision> listaDetRemision		= null;
		FacturaAsociadaBean facturaBean					= null;
		remision = remisionDao.findOne(id);
		listaFacturaAsociada = facturaAsociadaDao.findAllxIdRemision(remision.getCodigoRemision());
		if (listaFacturaAsociada!=null) {
			listaFacAsoVer = new ArrayList<>();
			for(TblFacturaAsociada factura: listaFacturaAsociada) {
				listaDetRemision = detalleRemisionDao.findAllxIdFacturaAsociada(factura.getCodigoFacturaAsociada());
				for(TblDetalleRemision detRemision: listaDetRemision) {
					facturaBean = new FacturaAsociadaBean();
					facturaBean.setSerieRemision(remision.getSerie());
					facturaBean.setNumeroRemision(remision.getNumero());
					facturaBean.setCodigoComprobante(factura.getCodigoComprobante());
					facturaBean.setNombreCliente(remision.getNombreCliente());
					facturaBean.setDescripcion(detRemision.getDescripcion());
					facturaBean.setUnidadMedida(Constantes.SUNAT_UNIDAD_MEDIDA);
					facturaBean.setCantidad(detRemision.getCantidad());
					facturaBean.setPeso(detRemision.getPeso()==null?new BigDecimal("0"):detRemision.getPeso());
					facturaBean.setCodigoProducto(detRemision.getCodigoProducto());
					facturaBean.setCantidadFacturada(detRemision.getCantidadFacturada());
					facturaBean.setCodigoDetalleRemision(detRemision.getCodigoDetalleRemision());
					facturaBean.setCodigoRemision(remision.getCodigoRemision());
					listaFacAsoVer.add(facturaBean);
				}
				
			}
		}
		return listaFacAsoVer;
	}
	

	/*Asignación de montos en forma de pago*/
	private void asignarMontoFormaPago(Filtro entidad){
		entidad.getFormaPago().setMonto(entidad.getComprobante().getTotalImporte());
	}

	
	/*Carga la pantalla de editar  */
	@RequestMapping(value = "/operacion/sfs12/editar/{id}", method = RequestMethod.GET)
	public String editarDetalle(@PathVariable Integer id, Model model, HttpServletRequest request) {
		Filtro entidad								= null;
		String path 								= "operacion/sfs12/sfs_edicion_producto";
		TblDetalleComprobante detalle				= null;
		try{
			LOGGER.debug("[editarDetalle] Inicio");
			entidad = (Filtro)request.getSession().getAttribute("filtroSession");
			//Asignamos el monto del importe a la forma de pago
			asignarMontoFormaPago(entidad);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			detalle = entidad.getListaDetalle().get(id);
			entidad.setDetalleComprobante(detalle);
			request.getSession().setAttribute("flagProducto","MODIFICAR");
			request.getSession().setAttribute("idEdicion", id);
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[editarDetalle] Fin");
		}catch(Exception e){
			LOGGER.debug("[editarDetalle] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
		}
		LOGGER.debug("[editarDetalle] Fin");
		return path;
	}
	/*Carga la pantalla de editar  */
	@RequestMapping(value = "/operacion/sfs12/editar/Gre/{id}", method = RequestMethod.GET)
	public String editarDetalleGuiaRemision(@PathVariable Integer id, Model model, HttpServletRequest request) {
		Filtro entidad								= null;
		String path 								= "operacion/sfs12/sfs_gre_edicion_producto";
		TblDetalleComprobante detalle				= null;
		try{
			LOGGER.debug("[editarDetalleGuiaRemision] Inicio");
			entidad = (Filtro)request.getSession().getAttribute("filtroSession");
			//Asignamos el monto del importe a la forma de pago
			asignarMontoFormaPago(entidad);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			detalle = entidad.getListaDetalle().get(id);
			entidad.setDetalleComprobante(detalle);
			request.getSession().setAttribute("flagProducto","MODIFICAR");
			request.getSession().setAttribute("idEdicion", id);
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[editarDetalleGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[editarDetalleGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
		}
		LOGGER.debug("[editarDetalleGuiaRemision] Fin");
		return path;
	}
	

	/*
	 * Validacion del detalle
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
			if (entidad.getDetalleComprobante().getValorReferencia() != null && entidad.getDetalleComprobante().getValorReferencia().doubleValue()>0) {
				if (!entidad.getDetalleComprobante().getTipoAfectacion().equals(Constantes.SUNAT_EXONERADO_OPERACION_GRATUITA) &&
						!entidad.getDetalleComprobante().getTipoAfectacion().equals(Constantes.SUNAT_INAFECTO_OPERACION_GRATUITA)){
					model.addAttribute("respuesta", "Si existe Valor referencial unitario en la operacion no onerosas con monto mayor a cero, la operacion debe ser gratuita ");
					exitoso = false;
					return exitoso;
					
				}
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
	@RequestMapping(value = "/operacion/sfs12/clientes", method = RequestMethod.POST)
	public String mostrarClientes(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_cli_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
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
	/*
	 * Listado de Clientes para la pantalla de Factura x guia de remision
	 */
	@RequestMapping(value = "/operacion/sfs12/clientes/Gre", method = RequestMethod.POST)
	public String mostrarClientesParaGuiaRemision(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_gre_cli_listado";
		try{
			LOGGER.debug("[mostrarClientesParaGuiaRemision] Inicio");
			request.getSession().setAttribute("filtroSession", filtro);
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[mostrarClientesParaGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarClientesParaGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[mostrarClientesParaGuiaRemision] Fin");
		return path;
	}
	/*
	 * Listado de Guias de Remision para la pantalla de Factura x guia de remision
	 */
	@RequestMapping(value = "/operacion/sfs12/guias/Gre", method = RequestMethod.POST)
	public String mostrarGuiaRemision(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_gre_rem_listado";
		Filtro filtroRemision = new Filtro();
		try{
			LOGGER.debug("[mostrarGuiaRemision] Inicio");
			request.getSession().setAttribute("filtroSession", filtro);
			filtroRemision.setRuc(filtro.getComprobante().getNumeroDocumento());			
			model.addAttribute("filtro", filtroRemision);
			LOGGER.debug("[mostrarGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[mostrarGuiaRemision] Fin");
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
	@RequestMapping(value = "/operacion/sfs12/clientes/q", method = RequestMethod.POST)
	public String listarClientes(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_cli_listado";
		try{
			LOGGER.debug("[listarClientes] Inicio");
			if (validarNegocioCliente(model, filtro)){
				this.listarCliente(model, filtro, request);
			}else{
				model.addAttribute("registros", new ArrayList<TblCliente>());
			}
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
	/*Busqueda de clientes para la pantalla de factura con guia de remision*/
	@RequestMapping(value = "/operacion/sfs12/clientes/q/Gre", method = RequestMethod.POST)
	public String listarClientesConGuiaRemision(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_gre_cli_listado";
		try{
			LOGGER.debug("[listarClientesConGuiaRemision] Inicio");
			if (validarNegocioCliente(model, filtro)){
				this.listarCliente(model, filtro, request);
			}else{
				model.addAttribute("registros", new ArrayList<TblCliente>());
			}
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[listarClientesConGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[listarClientesConGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[listarClientesConGuiaRemision] Fin");
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
	@RequestMapping(value = "/operacion/sfs12/clientes/seleccionar/{id}", method = RequestMethod.GET)
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
			persistirDetalleFormaPago(filtro, request);
			model.addAttribute("filtro", filtro);
			path = "operacion/sfs12/sfs_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cliente = null;
			filtro 	= null;
		}
		return path;
	}
	/*
	 * Asignar Cliente para la Factura con Guia de Remision
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/clientes/seleccionar/Gre/{id}", method = RequestMethod.GET)
	public String asignarClienteGuiaRemisionGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		TblCliente cliente 					= null;
		Filtro filtro						= null;
		String path							= null;
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			LOGGER.debug("[asignarClienteGuiaRemisionGet] Inicio:"+id);
			cliente = clienteDao.findOne(id);
			//Se mantiene en Session el contrato hasta retornar
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			LOGGER.debug("[asignarClienteGuiaRemisionGet] cliente:"+cliente);
			if (cliente!=null ){
				filtro.getComprobante().setNombreCliente(cliente.getNombre());
				filtro.getComprobante().setNumeroDocumento(cliente.getNumeroDocumento());
				filtro.getComprobante().setTipoDocumento(cliente.getTblCatalogo().getCodigoSunat());
				filtro.getComprobante().setDireccionCliente(cliente.getDireccion());
				filtro.setFlagBusquedaGuias(Constantes.ESTADO_ACTIVO);
			}else{
				filtro.getComprobante().setNombreCliente("");
				filtro.getComprobante().setNumeroDocumento("");
				filtro.getComprobante().setTipoDocumento("");
				filtro.getComprobante().setDireccionCliente("");
				filtro.setFlagBusquedaGuias(Constantes.ESTADO_INACTIVO);
			}
			filtro.setListaDetalle(listaDetalle);	
			persistirDetalleFormaPago(filtro, request);
			model.addAttribute("filtro", filtro);
			path = "operacion/sfs12/sfs_gre_nuevo";
			LOGGER.debug("[asignarClienteGuiaRemisionGet] Fin");

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
	@RequestMapping(value = "/operacion/sfs12/regresar", method = RequestMethod.POST)
	public String regresarComprobante(Model model, Filtro filtro, String path, HttpServletRequest request) {
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[regresarComprobante] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			filtro.setListaDetalle(listaDetalle);	
			persistirDetalleFormaPago(filtro, request);
			LOGGER.debug("[regresarContrato] Operacion:"+filtro.getStrOperacion());
			path = "operacion/sfs12/sfs_nuevo";

			model.addAttribute("filtro", filtro);
			request.getSession().setAttribute("flagProducto","");
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
	 * Regresa a la pantalla de comprobante x guia de remision
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/regresar/Gre", method = RequestMethod.POST)
	public String regresarComprobanteConGuiaRemision(Model model, Filtro filtro, String path, HttpServletRequest request) {
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[regresarComprobanteConGuiaRemision] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			filtro.setListaDetalle(listaDetalle);	
			persistirDetalleFormaPago(filtro, request);
			LOGGER.debug("[regresarComprobanteConGuiaRemision] Operacion:"+filtro.getStrOperacion());
			path = "operacion/sfs12/sfs_gre_nuevo";

			model.addAttribute("filtro", filtro);
			request.getSession().setAttribute("flagProducto","");
			LOGGER.debug("[regresarComprobanteConGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[regresarComprobanteConGuiaRemision] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}

	/*
	 * Listado de Producto
	 */
	@RequestMapping(value = "/operacion/sfs12/productos", method = RequestMethod.POST)
	public String mostrarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_pro_listado";
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

	@RequestMapping(value = "/operacion/sfs12/productos/q", method = RequestMethod.POST)
	public String listarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_pro_listado";
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
	@RequestMapping(value = "/operacion/sfs12/productos/seleccionar/{id}", method = RequestMethod.GET)
	public String asignarProductoGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		TblProducto producto				= null;
		Filtro filtro						= null;
		String path							= null;
		List<TblDetalleComprobante> listaDetalle = null;
		String flagProducto					= null;
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
				filtro.getDetalleComprobante().setCodigoProducto(producto.getCodigoEmpresa());
				filtro.getDetalleComprobante().setMoneda(producto.getTblCatalogo().getCodigoSunat());
			}
			filtro.setListaDetalle(listaDetalle);	
			persistirDetalleFormaPago(filtro, request);
			model.addAttribute("filtro", filtro);
			flagProducto = (String)request.getSession().getAttribute("flagProducto");
			if (flagProducto != null && flagProducto.equals("MODIFICAR")) {
				path = "operacion/sfs12/sfs_edicion_producto";
			}else {
				path = "operacion/sfs12/sfs_nuevo";
			}
			

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			producto = null;
			filtro 	= null;
		}
		return path;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/eliminar/{id}", method = RequestMethod.GET)
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
			persistirDetalleFormaPago(filtro, request);
			model.addAttribute("filtro", filtro);
			OperacionUtil.calculoDetalleComprobante(filtro);
			OperacionUtil.calculoCabeceraComprobante(filtro);
			asignarMontoFormaPago(filtro);
			path = "operacion/sfs12/sfs_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			listaDetalle = null;
		}
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/eliminar/Gre/{id}", method = RequestMethod.GET)
	public String eliminarDetalleGetGuiaRemision(@PathVariable Integer id, Model model, HttpServletRequest request) {
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
			persistirDetalleFormaPago(filtro, request);
			model.addAttribute("filtro", filtro);
			OperacionUtil.calculoDetalleComprobante(filtro);
			OperacionUtil.calculoCabeceraComprobante(filtro);
			asignarMontoFormaPago(filtro);
			path = "operacion/sfs12/sfs_gre_nuevo";

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
	@RequestMapping(value = "/operacion/sfs12/pdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
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
			listaDetalle = detalleComprobanteDao.listarxComprobantePDF(id);
			leyenda = leyendaDao.getxComprobante(id);
			filtro.setFormaPago(formaPagoDao.obtenerFormaPago(id));
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

	

	/*
	 * Se asigna los datos del campo de auditoria
	 */
	private void preGuardarBandeja(TblBandejaFacturador entidad, HttpServletRequest request) {
		entidad.setAuditoriaCreacion(request);

	}
	private void preEditarBandeja(TblBandejaFacturador entidad, HttpServletRequest request) {
		entidad.setAuditoriaCreacion(request);

	}
	@RequestMapping(value = "/operacion/sfs12/ver/actualizar", method = RequestMethod.POST)
	public String actualizarEstadoComprobante(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_ver";
		TblComprobante comprobante					= null;
		TblBandejaFacturador bandejaFacturador		= null;
		BandejaFacturadorBean bandejaBean			= null;
		BandejaFacturadorBean bandejaRptaBean		= null;
		IOperacionFacturador operacionFacturador	= new OperacionFacturadorImp();
		String rucEntidad							= null;
		try{
			LOGGER.debug("[actualizarEstadoComprobante] Inicio");
			filtro = (Filtro)request.getSession().getAttribute("filtroVerSession");
			rucEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getRuc();
			comprobante = filtro.getComprobante();
			bandejaFacturador = bandejaFacturadorDao.buscarOneByComprobante(comprobante.getCodigoComprobante());
			if (bandejaFacturador == null){
				bandejaBean = new BandejaFacturadorBean();
				bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
				bandejaBean.setTipoDocumento(comprobante.getTipoComprobante());
				bandejaBean.setNumeroDocumento(comprobante.getSerie()+"-"+comprobante.getNumero());
				bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean, filtro);
				if (bandejaRptaBean!=null){
					bandejaFacturador = OperacionUtil.setDatosBandeja(bandejaRptaBean, comprobante, rucEntidad);
					this.preGuardarBandeja(bandejaFacturador, request);
					bandejaFacturador.setTblComprobante(comprobante);
					try{
						bandejaFacturadorDao.deleteByComprobante(comprobante.getCodigoComprobante());
					}catch(Exception e){
						e.printStackTrace();
					}
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
				if (bandejaFacturador!= null && bandejaFacturador.getSituacion()!= null && (bandejaFacturador.getSituacion().equals("01") || bandejaFacturador.getSituacion().equals("02"))){
					bandejaBean = new BandejaFacturadorBean();
					bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
					bandejaBean.setTipoDocumento(comprobante.getTipoComprobante());
					bandejaBean.setNumeroDocumento(comprobante.getSerie()+"-"+comprobante.getNumero());
					bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean,filtro);
					if (bandejaRptaBean!=null){
						OperacionUtil.editarDatosBandeja(bandejaRptaBean, bandejaFacturador);
						this.preEditarBandeja(bandejaFacturador, request);
						bandejaFacturador.setTblComprobante(comprobante);
						//Eliminando comprobante de la bandeja
						try{
							bandejaFacturadorDao.deleteByComprobante(comprobante.getCodigoComprobante());
						}catch(Exception e){
							e.printStackTrace();
						}
						try{
						//Registrando nuevos datos del comprobante en bandeja
						bandejaFacturadorDao.save(bandejaFacturador);
						}catch(Exception e){
							e.printStackTrace();
						}
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
	public void actualizarEstadoComprobanteSfs12(Filtro filtro, HttpServletRequest request) {
		List<TblComprobante> listaComprobante = null; 
		//TblComprobante comprobante					= null;
		TblBandejaFacturador bandejaFacturador		= null;
		BandejaFacturadorBean bandejaBean			= null;
		BandejaFacturadorBean bandejaRptaBean		= null;
		IOperacionFacturador operacionFacturador	= new OperacionFacturadorImp();
		Integer codigoEntidad 						= null;
		String rucEntidad							= null;
		Map<String, TblParametro> mapParametro		= null;
		try{
			LOGGER.debug("[actualizarEstadoComprobanteSfs12] Inicio");


			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			OperacionUtil.asignarParametros(filtro, mapParametro,request);
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			rucEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getRuc();
			listaComprobante = comprobanteDao.findAllxEstado(codigoEntidad);
			if (listaComprobante !=null && listaComprobante.size()> 0){
				for(TblComprobante comprobanteEstado : listaComprobante){
					bandejaFacturador = bandejaFacturadorDao.buscarOneByComprobante(comprobanteEstado.getCodigoComprobante());
					LOGGER.debug("[actualizarEstadoComprobanteSfs12] bandejaFacturador:"+comprobanteEstado.getCodigoComprobante());
					if (bandejaFacturador == null){
						bandejaBean = new BandejaFacturadorBean();
						bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
						bandejaBean.setTipoDocumento(comprobanteEstado.getTipoComprobante());
						bandejaBean.setNumeroDocumento(comprobanteEstado.getSerie()+"-"+comprobanteEstado.getNumero());
						bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean, filtro);
						if (bandejaRptaBean!=null){
							bandejaFacturador = OperacionUtil.setDatosBandeja(bandejaRptaBean, comprobanteEstado, rucEntidad);
							this.preGuardarBandeja(bandejaFacturador, request);
							bandejaFacturador.setTblComprobante(comprobanteEstado);
							/*try{
								bandejaFacturadorDao.deleteByComprobante(comprobanteEstado.getCodigoComprobante());
							}catch(Exception e){
								System.out.println("[actualizarEstadoComprobanteSfs12] Delete:"+e.getMessage());
							}*/
							LOGGER.debug("[actualizarEstadoComprobanteSfs12] bandejaFacturadorDao.save:"+comprobanteEstado.getCodigoComprobante());
							bandejaFacturadorDao.save(bandejaFacturador);
							filtro.setBandejaFacturador(bandejaFacturador);
							//Actualizando estado en el comprobante
							comprobanteEstado.setEstadoOperacion(bandejaFacturador.getSituacion());
							comprobanteDao.save(comprobanteEstado);
							LOGGER.debug("[actualizarEstadoComprobanteSfs12] comprobanteDao.save:"+bandejaFacturador.getSituacion());
						}else {
							bandejaFacturador = getDatosBandejaFacturador(bandejaBean);
							bandejaFacturador.setTblComprobante(comprobanteEstado);
							LOGGER.debug("[actualizarEstadoComprobanteSfs12] NEW bandejaFacturadorDao.save:"+comprobanteEstado.getCodigoComprobante());
							bandejaFacturadorDao.save(bandejaFacturador);
							filtro.setBandejaFacturador(bandejaFacturador);
							//Actualizando estado en el comprobante
							comprobanteEstado.setEstadoOperacion(bandejaFacturador.getSituacion());
							comprobanteDao.save(comprobanteEstado);
							LOGGER.debug("[actualizarEstadoComprobanteSfs12] NEW comprobanteDao.save:"+bandejaFacturador.getSituacion());
						}

					}else{
						//Validando el estado
						if (bandejaFacturador.getSituacion().equals("01") || bandejaFacturador.getSituacion().equals("02")){
							bandejaBean = new BandejaFacturadorBean();
							bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
							bandejaBean.setTipoDocumento(comprobanteEstado.getTipoComprobante());
							bandejaBean.setNumeroDocumento(comprobanteEstado.getSerie()+"-"+comprobanteEstado.getNumero());
							bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean,filtro);
							if (bandejaRptaBean!=null){
								OperacionUtil.editarDatosBandeja(bandejaRptaBean, bandejaFacturador);
								this.preEditarBandeja(bandejaFacturador, request);
								bandejaFacturador.setTblComprobante(comprobanteEstado);
								//Eliminando comprobante de la bandeja
								/*try{
									bandejaFacturadorDao.deleteByComprobante(comprobanteEstado.getCodigoComprobante());
								}catch(Exception e){
									System.out.println("[actualizarEstadoComprobanteSfs12] Delete:"+e.getMessage());
								}*/
								try{
								//Registrando nuevos datos del comprobante en bandeja
									bandejaFacturadorDao.save(bandejaFacturador);
									LOGGER.debug("[actualizarEstadoComprobanteSfs12] update - bandejaFacturadorDao.save - CodigoBandeja:"+bandejaFacturador.getCodigoBandeja());
								}catch(Exception e){
									System.out.println("[actualizarEstadoComprobanteSfs12] Save:"+e.getMessage());
								}
								filtro.setBandejaFacturador(bandejaFacturador);
								//Actualizando estado en el comprobante
								comprobanteEstado.setEstadoOperacion(bandejaFacturador.getSituacion());
								comprobanteDao.save(comprobanteEstado);
								LOGGER.debug("[actualizarEstadoComprobanteSfs12] update - comprobanteDao.save - CodigoComprobante:"+comprobanteEstado.getCodigoComprobante());
							}
						}
						
					}
				}
				
			}
			
			LOGGER.debug("[actualizarEstadoComprobanteSfs12] Fin");
		}catch(Exception e){
			LOGGER.debug("[actualizarEstadoComprobanteSfs12] Error: "+e.getMessage());
			e.printStackTrace();
			
		}finally{
			filtro 				= null;
			//comprobante			= null;
			bandejaFacturador	= null;
			bandejaBean			= null;
			bandejaRptaBean		= null;
		}
		LOGGER.debug("[listarProducto] Fin");
		
	}

	private TblBandejaFacturador getDatosBandejaFacturador(BandejaFacturadorBean bandejaBean) {
		TblBandejaFacturador bandejaFacturador = new TblBandejaFacturador();
		
		bandejaFacturador.setNumeroRuc(bandejaBean.getNumeroRuc());
		bandejaFacturador.setTipoDocumento(bandejaBean.getTipoDocumento());
		bandejaFacturador.setNumeroDocumento(bandejaBean.getNumeroDocumento());
		bandejaFacturador.setNombreArchivo(bandejaBean.getNumeroRuc()+"-"+bandejaBean.getTipoDocumento()+"-"+bandejaBean.getNumeroDocumento());
		bandejaFacturador.setSituacion("01");
		bandejaFacturador.setTipoArchivo("TXT");
		bandejaFacturador.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
		return bandejaFacturador;
	}


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/ver/{id}", method = RequestMethod.GET)
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
		String rucEntidad 							= null;

		try{
			filtro = new Filtro();
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			rucEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getRuc();
			OperacionUtil.asignarParametros(filtro, mapParametro,request);
			comprobante = comprobanteDao.findOne(id);
			listaDetalle = detalleComprobanteDao.listarxComprobantePDF(id);
			leyenda = leyendaDao.getxComprobante(id);
			filtro.setComprobante(comprobante);
			filtro.setListaDetalle(listaDetalle);
			filtro.setLeyendaSunat(leyenda);
			filtro.setFormaPago(formaPagoDao.obtenerFormaPago(id));
			if (filtro.getFormaPago()!=null) {
				filtro.setListaDetalleFormaPago(detalleFormaPagoDao.listarDetalleFormaPago(filtro.getFormaPago().getCodigoForma()));
			}
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
					bandejaFacturador = OperacionUtil.setDatosBandeja(bandejaRptaBean,comprobante,rucEntidad);
					this.preGuardarBandeja(bandejaFacturador, request);
					bandejaFacturador.setTblComprobante(comprobante);
					try{
						bandejaFacturadorDao.deleteByComprobante(comprobante.getCodigoComprobante());
					}catch(Exception e){
						e.printStackTrace();
					}
					try{
						//Registrando nuevos datos del comprobante en bandeja
						bandejaFacturadorDao.save(bandejaFacturador);
					}catch(Exception e){
						e.printStackTrace();
					}
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
				if (bandejaFacturador!= null && bandejaFacturador.getSituacion() != null && (bandejaFacturador.getSituacion().equals("01") || bandejaFacturador.getSituacion().equals("02"))){
					bandejaBean = new BandejaFacturadorBean();
					bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
					bandejaBean.setTipoDocumento(comprobante.getTipoComprobante());
					bandejaBean.setNumeroDocumento(comprobante.getSerie()+"-"+comprobante.getNumero());
					bandejaRptaBean = operacionFacturador.consultarBandejaComprobantes(bandejaBean,filtro);
					if (bandejaRptaBean!=null){
						OperacionUtil.editarDatosBandeja(bandejaRptaBean, bandejaFacturador);
						this.preEditarBandeja(bandejaFacturador, request);
						bandejaFacturador.setTblComprobante(comprobante);
						//Eliminando comprobante de la bandeja
						/*try{
							bandejaFacturadorDao.deleteByComprobante(comprobante.getCodigoComprobante());
						}catch(Exception e){
							e.printStackTrace();
						}*/
						try{
						//Registrando nuevos datos del comprobante en bandeja
							bandejaFacturadorDao.save(bandejaFacturador);
						}catch(Exception e){
							e.printStackTrace();
						}
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
			path = "operacion/sfs12/sfs_ver";

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
	@RequestMapping(value = "/operacion/sfs12/leyenda", method = RequestMethod.POST)
	public String asignarLeyenda(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[asignarLeyenda] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalle(listaDetalle);
			entidad.getLeyendaSunat().setDescripcionSunat(OperacionUtil.getDescripcionLeyenda(entidad.getLeyendaSunat().getCodigoSunat(), request));
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			persistirDetalleFormaPago(entidad, request);
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[asignarLeyenda] Fin");
		}catch(Exception e){
			LOGGER.debug("[asignarLeyenda] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[asignarLeyenda] Fin");
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/leyenda/Gre", method = RequestMethod.POST)
	public String asignarLeyendaConGuiaRemision(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_gre_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[asignarLeyendaConGuiaRemision] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalle(listaDetalle);
			entidad.getLeyendaSunat().setDescripcionSunat(OperacionUtil.getDescripcionLeyenda(entidad.getLeyendaSunat().getCodigoSunat(), request));
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			persistirDetalleFormaPago(entidad, request);
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[asignarLeyendaConGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[asignarLeyendaConGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[asignarLeyendaConGuiaRemision] Fin");
		return path;
	}

	/**
	 * Se encarga de la eliminacion fisica del comprobante
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/sfs12/all/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarComprobante(@PathVariable Integer id, HttpServletRequest request, Model model, PageableSG pageable) {
		TblComprobante entidad						= null;
		List<TblDetalleComprobante> listaDetEntidad = null;
		TblSunatCabecera sunat						= null;
		List<TblSunatDetalle> listaDetSunat			= null;
		List<TblTributoGeneral> listaTributo		= null;
		List<TblSunatTributoGeneral> listaTribSunat	= null;
		TblLeyenda leyenda							= null;
		String path 								= null;
		Filtro filtro 								= null;
		String nombreArchivo						= null;
		List<String> listaArchivo					= null;
		Map<String, TblParametro> mapParametro		= null;
		TblParametro parametro 						= null;
		String ruta									= null;
		try{
			LOGGER.debug("[eliminarComprobante] Inicio");

			//Buscando
			entidad = comprobanteDao.findOne(id);
			if (!entidad.getEstadoOperacion().equals(Constantes.SUNAT_ESTADO_OPERACION_ACEPTADO)){
				listaDetEntidad = detalleComprobanteDao.listarxComprobantePDF(id);
				leyenda = leyendaDao.getxComprobante(id);
				sunat = sunatCabeceraDao.findByCodigoDocumento(id);
				listaTributo = tributoGeneralDAO.listarxComprobante(id);

				if (sunat !=null && sunat.getCodigoCabecera()>0){
					nombreArchivo = sunat.getNombreArchivo();
					listaDetSunat = sunatDetalleDao.findByCodigoCabecera(sunat.getCodigoCabecera());
					listaTribSunat = sunatTributoGeneralDAO.findByCodigoCabecera(sunat.getCodigoCabecera());
				}
				//Eliminando los archivos
				mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
				parametro = mapParametro.get(Constantes.PARAMETRO_SUNAT_DATA);
				if (parametro!=null){
					ruta = parametro.getDato();
				}
				listaArchivo = UtilSGT.getNombreArchivos(nombreArchivo);
				UtilSGT.eliminarArchivo(listaArchivo, ruta);
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
				//Borrando
				if (listaTribSunat !=null){
					for(TblSunatTributoGeneral detalle: listaTribSunat){
						try{
							sunatTributoGeneralDAO.delete(detalle);
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
					//Revertimos los descuentos de las cantidades en las remisiones y fuera el caso
					revertirCantidadEnGuiaRemision(listaDetEntidad,request);
					for(TblDetalleComprobante detalle: listaDetEntidad){
						try{
							detalleComprobanteDao.delete(detalle);
						}catch(Exception e1){
							e1.printStackTrace();
						}

					}
				}
				if (listaTributo!=null){
					for(TblTributoGeneral detalle:listaTributo){
						try{
							tributoGeneralDAO.delete(detalle);
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

				model.addAttribute("respuesta", "Se eliminó satisfactoriamente");
			}else{
				model.addAttribute("respuesta", "La Factura fue generado y aceptado por la SUNAT, no se puede eliminar");
			}

			path = "operacion/sfs12/sfs_listado";
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


	private void revertirCantidadEnGuiaRemision(List<TblDetalleComprobante> listaDetEntidad, HttpServletRequest request) {
		LOGGER.info("[revertirCantidadEnGuiaRemision] Inicio");
		if (listaDetEntidad!= null) {
			for(TblDetalleComprobante detalleComprobante : listaDetEntidad) {
				if (detalleComprobante.getCodigoDetalleRemision() !=null && detalleComprobante.getCodigoDetalleRemision() > 0) {
					TblDetalleRemision detalleRemision = detalleRemisionDao.findOne(detalleComprobante.getCodigoDetalleRemision());
					if (detalleRemision != null) {
						detalleRemision.setAuditoriaModificacion(request);
						LOGGER.info("[revertirCantidadEnGuiaRemision] CodigoDetalle:"+detalleComprobante.getCodigoDetalleRemision()+ " Cantidad Facturada:"+detalleRemision.getCantidadFacturada()+ " Cantidad a Descontar:"+detalleComprobante.getCantidad());
						detalleRemision.setCantidadFacturada(detalleRemision.getCantidadFacturada().subtract(detalleComprobante.getCantidad()));
						detalleRemisionDao.save(detalleRemision);
						if (detalleComprobante.getCodigoRemision() != null && detalleComprobante.getCodigoRemision()>0) {
							TblRemision remision = remisionDao.findOne(detalleComprobante.getCodigoRemision());
							if (remision != null) {
								remision.setAuditoriaModificacion(request);
								remision.setFlagFacturaOk(Constantes.ESTADO_INACTIVO);
								remisionDao.save(remision);
							}
						}
					}
				}
			}
		}
		LOGGER.info("[revertirCantidadEnGuiaRemision] Fin");
	}


	@RequestMapping(value = "/operacion/sfs12/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/sfs12/sfs_listado";
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/comprobantes/tipoComprobanteSerie", method = RequestMethod.POST)
	public String asignarSeriexComprobante(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_nuevo";
		Map<String, TblParametro> mapParametro	= null;
		TblParametro parametro =null;
		try{
			LOGGER.debug("[asignarSeriexComprobante] Inicio");
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			//Serie
			parametro = mapParametro.get(Constantes.PARAMETRO_SERIE);
			if (parametro!=null){
				filtro.getComprobante().setSerie(parametro.getDato());
			}else{
				filtro.getComprobante().setSerie("");
			}
			this.obtenerSerie(filtro, request);
			persistirDetalleFormaPago(filtro, request);
			request.getSession().setAttribute("filtroSession", filtro);
			request.getSession().setAttribute("listaDetalleSession", filtro.getListaDetalle());
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[asignarSeriexComprobante] Fin");
		}catch(Exception e){
			LOGGER.debug("[asignarSeriexComprobante] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[asignarSeriexComprobante] Fin");
		return path;
	}
	@RequestMapping(value = "/operacion/comprobantes/numeroComprobanteSerie", method = RequestMethod.POST)
	public String asignarNumeroxSeriexComprobante(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_nuevo";
		Integer codigoEntidad 					= null;
		List<TblSerie> listaSerie 				= null;
		TblSerie serie							= null;
		try{
			LOGGER.debug("[numeroComprobanteSerie] Inicio");
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			listaSerie = serieDao.buscarOneByNombre(filtro.getComprobante().getTipoComprobante(), filtro.getComprobante().getSerie(), codigoEntidad);
			if (listaSerie !=null){
				serie = listaSerie.get(0);
				filtro.getComprobante().setNumero(serie.getNumeroComprobante());
			}
			persistirDetalleFormaPago(filtro, request);
			request.getSession().setAttribute("filtroSession", filtro);
			request.getSession().setAttribute("listaDetalleSession", filtro.getListaDetalle());
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[numeroComprobanteSerie] Fin");
		}catch(Exception e){
			LOGGER.debug("[numeroComprobanteSerie] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[numeroComprobanteSerie] Fin");
		return path;
	}
	@RequestMapping(value = "/operacion/comprobantes/numeroComprobanteSerie/Gre", method = RequestMethod.POST)
	public String asignarNumeroxSeriexComprobanteParaFactura(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_gre_nuevo";
		Integer codigoEntidad 					= null;
		List<TblSerie> listaSerie 				= null;
		TblSerie serie							= null;
		try{
			LOGGER.debug("[asignarNumeroxSeriexComprobanteParaFactura] Inicio");
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			listaSerie = serieDao.buscarOneByNombre(filtro.getComprobante().getTipoComprobante(), filtro.getComprobante().getSerie(), codigoEntidad);
			if (listaSerie !=null){
				serie = listaSerie.get(0);
				filtro.getComprobante().setNumero(serie.getNumeroComprobante());
			}
			persistirDetalleFormaPago(filtro, request);
			request.getSession().setAttribute("filtroSession", filtro);
			request.getSession().setAttribute("listaDetalleSession", filtro.getListaDetalle());
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[asignarNumeroxSeriexComprobanteParaFactura] Fin");
		}catch(Exception e){
			LOGGER.debug("[asignarNumeroxSeriexComprobanteParaFactura] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[asignarNumeroxSeriexComprobanteParaFactura] Fin");
		return path;
	}
	/*public void sendEmailWithoutTemplating() throws CannotSendEmailException, URISyntaxException, IOException{
		   final Email email = DefaultEmail.builder()
		        .from(new InternetAddress("daisy.minchola.m@gmail.com", "Daisy Minchola"))
		        .to(Lists.newArrayList(new InternetAddress("gregorio.rodriguez.p@gmail.com", "Gregorio Rodriguez")))
		        .subject("Prueba")
		        .body("Firmamentum autem stabilitatis constantiaeque eius, quam in amicitia quaerimus, fides est.")
		        .attachment(getPdfWithAccentedCharsAttachment("Factura"))
		        .attachment(getCsvForecastAttachment("forecast"))
		        .encoding("UTF-8").build();

		   String template = "plantilla/emailTemplate.html";
		   Map<String, Object> modelObject = ImmutableMap.of(
	                "title", "Emperor",
	                "name", "Cleon I"
	        );
		   emailService.send(email, template, modelObject);
		}*/
	
	/*private EmailAttachment getCsvForecastAttachment(String filename) {
	    final String testData = 
	                "years from now,death probability\n1,0.9\n2,0.95\n3,1.0";
	    final DefaultEmailAttachment attachment = DefaultEmailAttachment.builder()
	                .attachmentName(filename + ".csv")
	                .attachmentData(testData.getBytes(Charset.forName("UTF-8")))
	                .mediaType(MediaType.TEXT_PLAIN).build();
	    return attachment;
	}*/
	
	 /*private EmailAttachment getPdfWithAccentedCharsAttachment(String filename) throws URISyntaxException, IOException {
	        //ClassLoader classLoader = getClass().getClassLoader();
	        //File pdfFile = new File(classLoader.getResource("attachments" + File.separator + "FC01-00000003_revisado.pdf").toURI());
	        File pdfFile = new File("D:\\archivoEjemplo\\GFC01-00000003_revisado.pdf");

	        final DefaultEmailAttachment attachment = DefaultEmailAttachment.builder()
	                .attachmentName(filename + ".pdf")
	                .attachmentData(Files.readAllBytes(pdfFile.toPath()))
	                .mediaType(MediaType.APPLICATION_PDF).build();
	        return attachment;
	    }*/
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Comprobante
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/adicionarDetalleFormaPago", method = RequestMethod.POST)
	public String adicionarDetalleFormaPago(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_nuevo";
		TblDetalleFormaPago detalleFormaPago 		= null;
		String mensajeValidacion 					= null;
		ArrayList<TblDetalleFormaPago> listaDetalle = null;
		try{
			LOGGER.debug("[adicionarDetalleFormaPago] Inicio");
			LOGGER.debug("[mostrarDetalleFormaPago] Flag:"+entidad.getFlagMostrarDetalleFormaPago());
			mensajeValidacion = validarDetalleFormaPago(entidad.getDetalleFormaPago());
			if (mensajeValidacion.equals("")){
				listaDetalle = (ArrayList<TblDetalleFormaPago>)request.getSession().getAttribute("listaDetalleFormaPagoSession");
				detalleFormaPago = new TblDetalleFormaPago();
				detalleFormaPago.setMoneda(entidad.getDetalleFormaPago().getMoneda());
				detalleFormaPago.setMonto(entidad.getDetalleFormaPago().getMonto());
				detalleFormaPago.setFecha(entidad.getDetalleFormaPago().getFecha());
				listaDetalle.add(detalleFormaPago);
				request.getSession().setAttribute("listaDetalleFormaPagoSession", listaDetalle);
				entidad.setListaDetalleFormaPago(listaDetalle);
			}else{
				model.addAttribute("respuesta", mensajeValidacion);
			}
			persistirDetalle(entidad, request);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[mostrarDetalleFormaPago] Flag:"+entidad.getFlagMostrarDetalleFormaPago());
			LOGGER.debug("[adicionarDetalleFormaPago] Fin");
		}catch(Exception e){
			LOGGER.debug("[adicionarDetalleFormaPago] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[adicionarDetalleFormaPago] Fin");
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/adicionarDetalleFormaPago/Gre", method = RequestMethod.POST)
	public String adicionarDetalleFormaPagoGuiaRemision(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_gre_nuevo";
		TblDetalleFormaPago detalleFormaPago 		= null;
		String mensajeValidacion 					= null;
		ArrayList<TblDetalleFormaPago> listaDetalle = null;
		try{
			LOGGER.debug("[adicionarDetalleFormaPagoGuiaRemision] Inicio");
			LOGGER.debug("[adicionarDetalleFormaPagoGuiaRemision] Flag:"+entidad.getFlagMostrarDetalleFormaPago());
			mensajeValidacion = validarDetalleFormaPago(entidad.getDetalleFormaPago());
			if (mensajeValidacion.equals("")){
				listaDetalle = (ArrayList<TblDetalleFormaPago>)request.getSession().getAttribute("listaDetalleFormaPagoSession");
				detalleFormaPago = new TblDetalleFormaPago();
				detalleFormaPago.setMoneda(entidad.getDetalleFormaPago().getMoneda());
				detalleFormaPago.setMonto(entidad.getDetalleFormaPago().getMonto());
				detalleFormaPago.setFecha(entidad.getDetalleFormaPago().getFecha());
				listaDetalle.add(detalleFormaPago);
				request.getSession().setAttribute("listaDetalleFormaPagoSession", listaDetalle);
				entidad.setListaDetalleFormaPago(listaDetalle);
			}else{
				model.addAttribute("respuesta", mensajeValidacion);
			}
			persistirDetalle(entidad, request);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[adicionarDetalleFormaPagoGuiaRemision] Flag:"+entidad.getFlagMostrarDetalleFormaPago());
			LOGGER.debug("[adicionarDetalleFormaPagoGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[adicionarDetalleFormaPagoGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[adicionarDetalleFormaPagoGuiaRemision] Fin");
		return path;
	}
	/*Valida el detalle de forma de pago*/
	private String validarDetalleFormaPago(TblDetalleFormaPago detalleFormaPago){
		String resultado = "";
		if (detalleFormaPago.getMonto()== null || detalleFormaPago.getMonto().compareTo(new BigDecimal("0"))<=0){
			resultado = "El Monto de Pago no puede ser menor o igua a CERO";
			return resultado;
		}
		if (detalleFormaPago.getFecha() == null || detalleFormaPago.getFecha().equals("")){
			resultado = "Debe ingresar la Fecha de Pago";
			return resultado;
		}
		if (detalleFormaPago.getMoneda() == null || detalleFormaPago.getMoneda().equals("")){
			resultado = "Debe seleccionar la Moneda de Pago";
			return resultado;
		}
		return resultado;
	}
	
	/**
	 * Muestra el detalle de la forma de pago al Credito
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/sfs12/mostrarDetalleFormaPago", method = RequestMethod.POST)
	public String mostrarDetalleFormaPago(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_nuevo";
		try{
			LOGGER.debug("[mostrarDetalleFormaPago] Inicio");
			LOGGER.debug("[mostrarDetalleFormaPago] Flag:"+entidad.getFlagMostrarDetalleFormaPago());
			if (entidad.getFormaPago().getTipo().equals(Constantes.FORMA_PAGO_CREDITO)){
				entidad.setFlagMostrarDetalleFormaPago(Constantes.TIPO_SI);
				entidad.setDetalleFormaPago(new TblDetalleFormaPago());
				entidad.getDetalleFormaPago().setMoneda(Constantes.SUNAT_TIPO_MONEDA_SOLES);
			}else{
				entidad.setFlagMostrarDetalleFormaPago(Constantes.TIPO_NO);
			}
			persistirDetalleFormaPago(entidad, request);
			persistirDetalle(entidad, request);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[mostrarDetalleFormaPago] Flag:"+entidad.getFlagMostrarDetalleFormaPago());
			LOGGER.debug("[mostrarDetalleFormaPago] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarDetalleFormaPago] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[mostrarDetalleFormaPago] Fin");
		return path;
	}
	/**
	 * Muestra el detalle de la forma de pago al Credito para la Factura con Guia de Remision
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/sfs12/mostrarDetalleFormaPago/Gre", method = RequestMethod.POST)
	public String mostrarDetalleFormaPagoConGuiaRemision(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_gre_nuevo";
		try{
			LOGGER.debug("[mostrarDetalleFormaPagoConGuiaRemision] Inicio");
			LOGGER.debug("[mostrarDetalleFormaPagoConGuiaRemision] Flag:"+entidad.getFlagMostrarDetalleFormaPago());
			if (entidad.getFormaPago().getTipo().equals(Constantes.FORMA_PAGO_CREDITO)){
				entidad.setFlagMostrarDetalleFormaPago(Constantes.TIPO_SI);
				entidad.setDetalleFormaPago(new TblDetalleFormaPago());
				entidad.getDetalleFormaPago().setMoneda(Constantes.SUNAT_TIPO_MONEDA_SOLES);
			}else{
				entidad.setFlagMostrarDetalleFormaPago(Constantes.TIPO_NO);
			}
			persistirDetalleFormaPago(entidad, request);
			persistirDetalle(entidad, request);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[mostrarDetalleFormaPagoConGuiaRemision] Flag:"+entidad.getFlagMostrarDetalleFormaPago());
			LOGGER.debug("[mostrarDetalleFormaPagoConGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarDetalleFormaPagoConGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[mostrarDetalleFormaPagoConGuiaRemision] Fin");
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/eliminarDetalleFormaPago/{id}", method = RequestMethod.GET)
	public String eliminarDetalleFormaPagoGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		Filtro filtro						= null;
		String path							= null;
		List<TblDetalleFormaPago> listaDetalle = null;
		try{
			listaDetalle = (ArrayList<TblDetalleFormaPago>)request.getSession().getAttribute("listaDetalleFormaPagoSession");
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");

			if (listaDetalle!=null && listaDetalle.size()>id){
				listaDetalle.remove(id.intValue());

			}
			request.getSession().setAttribute("listaDetalleFormaPagoSession", listaDetalle);
			persistirDetalle(filtro, request);
			filtro.setListaDetalleFormaPago(listaDetalle);
			model.addAttribute("filtro", filtro);
			path = "operacion/sfs12/sfs_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			listaDetalle = null;
		}
		return path;
	}
	/*Mantenemos la lista de detalle de la forma de pago*/
	@SuppressWarnings("unchecked")
	private void persistirDetalleFormaPago(Filtro entidad, HttpServletRequest request){
		ArrayList<TblDetalleFormaPago> listaDetalleFormaPago = null;
		listaDetalleFormaPago = (ArrayList<TblDetalleFormaPago>)request.getSession().getAttribute("listaDetalleFormaPagoSession");

		entidad.setListaDetalleFormaPago(listaDetalleFormaPago);
	}
	/*Mantenemos la lista de detalle de la forma de pago*/
	@SuppressWarnings("unchecked")
	private void persistirDetalle(Filtro entidad, HttpServletRequest request){
		ArrayList<TblDetalleComprobante> listaDetalle = null;
		listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");

		entidad.setListaDetalle(listaDetalle);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/modificar", method = RequestMethod.POST)
	public String modificarDetalle(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		Integer id = null;
		try{
			id = (Integer)request.getSession().getAttribute("idEdicion");
			LOGGER.debug("[modificarDetalle] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			persistirDetalleFormaPago(entidad, request);
			
			entidad.setListaDetalle(listaDetalle);
			if (this.validarDetalle(model, entidad, request)){
				LOGGER.debug("[modificarDetalle] id:"+id);
				entidad.setListaDetalle(actualizarListaDetalle(entidad, id, listaDetalle));
				request.getSession().setAttribute("flagProducto","");
			}else {
				path = "operacion/sfs12/sfs_edicion_producto";
			}
			OperacionUtil.calculoDetalleComprobante(entidad);
			OperacionUtil.calculoCabeceraComprobante(entidad);
			//Asignamos el monto del importe a la forma de pago
			asignarMontoFormaPago(entidad);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());

			model.addAttribute("filtro", entidad);
			LOGGER.debug("[modificarDetalle] Fin");
		}catch(Exception e){
			LOGGER.debug("[modificarDetalle] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			listaDetalle = null;
		}
		LOGGER.debug("[modificarDetalle] Fin");
		return path;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12/modificar/Gre", method = RequestMethod.POST)
	public String modificarDetalleGuiaRemision(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12/sfs_gre_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		Integer id = null;
		try{
			id = (Integer)request.getSession().getAttribute("idEdicion");
			LOGGER.debug("[modificarDetalleGuiaRemision] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			persistirDetalleFormaPago(entidad, request);
			
			entidad.setListaDetalle(listaDetalle);
			//if (this.validarDetalle(model, entidad, request)){
				LOGGER.debug("[modificarDetalleGuiaRemision] id:"+id);
				entidad.setListaDetalle(actualizarListaDetalle(entidad, id, listaDetalle));
				request.getSession().setAttribute("flagProducto","");
			//}else {
			//	path = "operacion/sfs12/sfs_edicion_producto";
			//}
			OperacionUtil.calculoDetalleComprobante(entidad);
			OperacionUtil.calculoCabeceraComprobante(entidad);
			//Asignamos el monto del importe a la forma de pago
			asignarMontoFormaPago(entidad);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());

			model.addAttribute("filtro", entidad);
			LOGGER.debug("[modificarDetalleGuiaRemision] Fin");
		}catch(Exception e){
			LOGGER.debug("[modificarDetalleGuiaRemision] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			listaDetalle = null;
		}
		LOGGER.debug("[modificarDetalleGuiaRemision] Fin");
		return path;
	}
	
	private List<TblDetalleComprobante> actualizarListaDetalle(Filtro entidad, Integer id, List<TblDetalleComprobante> listaDetalle ){
		List<TblDetalleComprobante> nuevaLista = new ArrayList<>();
		LOGGER.debug("[actualizarListaDetalle] Inicio");
		for(int index = 0; index < listaDetalle.size(); index++) {
			if (index == id) {
				LOGGER.debug("[actualizarListaDetalle] Elemento Encontrado:"+id);
				nuevaLista.add(entidad.getDetalleComprobante());
			}else {
				nuevaLista.add(listaDetalle.get(index));
			}
		}
		LOGGER.debug("[actualizarListaDetalle] Fin");
		return nuevaLista;
	}
}