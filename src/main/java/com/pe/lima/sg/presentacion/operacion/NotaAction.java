package com.pe.lima.sg.presentacion.operacion;



import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conCodigoProducto;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conEstadoProducto;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conNombreProducto;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conEstado;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conNumero;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conSerie;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conTipoComprobante;
import static com.pe.lima.sg.dao.operacion.NotaSpecifications.conEstadoNota;
import static com.pe.lima.sg.dao.operacion.NotaSpecifications.conNumeroNota;
import static com.pe.lima.sg.dao.operacion.NotaSpecifications.conSerieNota;
import static com.pe.lima.sg.dao.operacion.NotaSpecifications.conTipoComprobanteNota;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pe.lima.sg.bean.facturador.BandejaFacturadorNotaBean;
import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.dao.mantenimiento.ICatalogoDAO;
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.dao.mantenimiento.IProductoDAO;
import com.pe.lima.sg.dao.operacion.IBandejaFacturadorNotaDAO;
import com.pe.lima.sg.dao.operacion.IComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleNotaDAO;
import com.pe.lima.sg.dao.operacion.INotaDAO;
import com.pe.lima.sg.dao.operacion.ISunatCabeceraNotaDAO;
import com.pe.lima.sg.dao.operacion.ISunatDetalleNotaDAO;
import com.pe.lima.sg.db.util.IOperacionFacturador;
import com.pe.lima.sg.db.util.OperacionFacturadorImp;
import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturadorNota;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleNota;
import com.pe.lima.sg.entity.operacion.TblLeyenda;
import com.pe.lima.sg.entity.operacion.TblNota;
import com.pe.lima.sg.entity.operacion.TblSunatCabeceraNota;
import com.pe.lima.sg.entity.operacion.TblSunatDetalleNota;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.pdf.NotaKenorPdf;
//import com.pe.lima.sg.presentacion.pdf.NotaPdf;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.NumberToLetterConverter;
import com.pe.lima.sg.presentacion.util.PageWrapper;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;

/**
 * Clase que se encarga de la administracion de las Notas
 *
 * 			
 */
@Controller
public class NotaAction extends BaseOperacionPresentacion<TblNota> {

	@Autowired
	private IComprobanteDAO comprobanteDao;
	
	@Autowired
	private IDetalleComprobanteDAO detalleComprobanteDao;
	
	@Autowired
	private INotaDAO notaDao;

	@Autowired
	private IDetalleNotaDAO detalleNotaDao;

	@Autowired
	private ISunatCabeceraNotaDAO sunatCabeceraNotaDao;
	
	@Autowired
	private ISunatDetalleNotaDAO sunatDetalleNotaDao;
	
	//@Autowired
	//private IClienteDAO clienteDao;

	@Autowired
	private IProductoDAO productoDao;

	
	@Autowired
	private IBandejaFacturadorNotaDAO bandejaFacturadorNotaDao;

	@Autowired
	private ICatalogoDAO catalogoDao;
	
	@Autowired
	private IEmpresaDAO empresaDao;
	
	
	//@Autowired
	//private ILeyendaNotaDAO leyendaNotaDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotaAction.class);
	
	/*@Autowired
	private IOperacionFacturador operacionFacturador;*/

	private String urlPaginado = "/operacion/notas/paginado/"; 
	
	@Override
	public BaseOperacionDAO<TblNota, Integer> getDao() {
		return notaDao;
	}	

	
	
	
	
	

	/**
	 * Se encarga de listar todos las notas de la empresa
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/notas", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path,  PageableSG pageable,HttpServletRequest request) {
		Filtro filtro = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/nota/not_listado";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setNumero("");;
			this.mListarNota(model, filtro, pageable, this.urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroNotaCriterio", filtro);
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
	 * Se encarga de buscar la informacion las notas segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/notas/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/nota/not_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.mListarNota(model, filtro, pageable, urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroNotaCriterio", filtro);
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
	
	/*** Listado de Nota ***/
	private void mListarNota(Model model, Filtro filtro,  PageableSG pageable, String url, HttpServletRequest request){
		List<TblNota> entidades = new ArrayList<TblNota>();
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "numero"));
		Integer codigoEntidad = null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			Specification<TblNota> criterio = Specifications.where(conNumeroNota(filtro.getNumero()))
					.and(conSerieNota(filtro.getSerie()))
					.and(com.pe.lima.sg.dao.operacion.NotaSpecifications.conCodigoEmpresa(codigoEntidad))
					.and(conTipoComprobanteNota(filtro.getTipoComprobante()))
					.and(conEstadoNota(Constantes.ESTADO_REGISTRO_ACTIVO));
			pageable.setSort(sort);
			//entidades = notaDao.findAll(criterio);
			Page<TblNota> entidadPage = notaDao.findAll(criterio, pageable);
			PageWrapper<TblNota> page = new PageWrapper<TblNota>(entidadPage, url, pageable);
			model.addAttribute("registros", page.getContent());
			model.addAttribute("page", page);
			LOGGER.debug("[listarNota] entidades:"+entidades);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}


	
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Nota
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/notas/nuevo", method = RequestMethod.GET)
	public String crearNota(Model model, HttpServletRequest request) {
		Filtro filtro 							= null;
		Map<String, TblParametro> mapParametro	= null;
		try{
			LOGGER.debug("[crearNota] Inicio");
			filtro = new Filtro();
			filtro.setNota(new TblNota());
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			this.mAsignarParametros(filtro, mapParametro, request);
			model.addAttribute("filtro", filtro);
			request.getSession().setAttribute("filtroSession", filtro);
			request.getSession().setAttribute("listaDetalleSession", new ArrayList<TblDetalleComprobante>());
			LOGGER.debug("[crearNota] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			 mapParametro			= null;
			 filtro 				= null;
		}
		return "operacion/nota/not_nuevo";
	}
	/*
	 * Setea los parametros por defecto de la nota
	 */
	private void mAsignarParametros(Filtro entidad, Map<String, TblParametro> mapParametro, HttpServletRequest request){
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
				}
				entidad.setRuc(Constantes.SUNAT_RUC_EMISOR);*/
				entidad.setRuc(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getRuc());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/*
	private void asignarParametrosNota(Filtro entidad, Map<String, TblParametro> mapParametro){
		TblParametro parametro =null;
		try{
			entidad.setNota(new TblNota());
			entidad.setDetalleNota(new TblDetalleNota());
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
				/*parametro = mapParametro.get(Constantes.PARAMETRO_TIPO_OPERACION);
				if (parametro!=null){
					entidad.getNota().setTipoOperacion(parametro.getDato());
				}else{
					entidad.getNota().setTipoOperacion("");
				} *fin/
				//Codigo el domicilio fiscal
				parametro = mapParametro.get(Constantes.PARAMETRO_CODIGO_DOMICILIO_FISCAL);
				if (parametro!=null){
					entidad.getNota().setCodigoDomicilio(parametro.getDato());
				}else{
					entidad.getNota().setCodigoDomicilio("");
				}
				//Tipo de nota
				parametro = mapParametro.get(Constantes.PARAMETRO_TIPO_COMPROBANTE);
				if (parametro!=null){
					entidad.getNota().setTipoNota(parametro.getDato());
				}else{
					entidad.getNota().setTipoNota("");
				}
				//Moneda
				parametro = mapParametro.get(Constantes.PARAMETRO_MONEDA);
				if (parametro!=null){
					entidad.getNota().setMoneda(parametro.getDato());
				}else{
					entidad.getNota().setMoneda("");
				}
				//Serie
				parametro = mapParametro.get(Constantes.PARAMETRO_SERIE);
				if (parametro!=null){
					entidad.getNota().setSerie(parametro.getDato());
				}else{
					entidad.getNota().setSerie("");
				}
				//Unidad medida
				parametro = mapParametro.get(Constantes.PARAMETRO_UNIDAD_MEDIDA);
				if (parametro!=null){
					entidad.getDetalleNota().setUnidadMedida(parametro.getDato());
				}else{
					entidad.getDetalleNota().setUnidadMedida("");
				}
				//afectacion igv
				parametro = mapParametro.get(Constantes.PARAMETRO_AFECTACION_IGV);
				if (parametro!=null){
					entidad.getDetalleNota().setTipoAfectacion(parametro.getDato());
				}else{
					entidad.getDetalleNota().setTipoAfectacion("");
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
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	/*
	 * Campos de auditoria para la Nota
	 */
	@Override
	public void preGuardar(TblNota entidad, HttpServletRequest request) {
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
	 * Campos de auditoria para  el detalle de la nota
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
			this.preGuardar(filtro.getNota(), request);
			for(TblDetalleNota detalle: filtro.getListaDetalleNota()){
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
	 * Campos de auditoria para la leyenda
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
	public boolean validarNegocio(Model model,TblNota entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total	= null;
		Integer codigoEmpresa = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad();
		try{
			//Tipo de Operacion 
			/*if (entidad.getTipoOperacion() == null || entidad.getTipoOperacion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de operación");
				return exitoso;
			}*/
			//Codigo Domicilio Fiscal
			/*if (entidad.getCodigoDomicilio() == null || entidad.getCodigoDomicilio().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el domicilio fiscal o anexo");
				return exitoso;
			}*/
			//Comprobante asociado
			if (entidad.getTblComprobante()== null || entidad.getTblComprobante().getCodigoComprobante() <=0){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar los datos del comprobante asociados a la nota");
				return exitoso;
			}
			
			//Fecha Emision
			if (entidad.getFechaEmision() == null || entidad.getFechaEmision().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la fecha de emisión");
				return exitoso;
			}
			//Tipo de Nota
			if (entidad.getTipoComprobante() == null || entidad.getTipoComprobante().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de nota");
				return exitoso;
			}
			//Serie
			if (entidad.getSerie() == null || entidad.getSerie().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el numero de la serie de la nota");
				return exitoso;
			}
			//Numero
			if (entidad.getNumero() == null || entidad.getNumero().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el número del nota");
				return exitoso;
			}else{
				entidad.setNumero(UtilSGT.completarCeros(entidad.getNumero(), Constantes.SUNAT_LONGITUD_NUMERO));
			}
			//Validando existencia del nota
			total = notaDao.totalNota(entidad.getTipoNota(), entidad.getSerie(), entidad.getNumero(),codigoEmpresa);
			if (total > 0){
				exitoso = false;
				//model.addAttribute("respuesta", "El número y la serie para el nota ingresado ya existe ["+entidad.getSerie()+"-"+entidad.getNumero()+"]");
				model.addAttribute("respuesta", "Se encontró un nota anteriormente registrado Tipo ["+entidad.getTipoNota()+"] - Serie ["+entidad.getSerie()+"] - Numero ["+entidad.getNumero()+"]");
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
			//Descripcion de la nota
			if (entidad.getDescripcionNota()== null || entidad.getDescripcionNota().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la descripcion de la nota");
				return exitoso;
			}
				
		}catch(Exception e){
			exitoso = false;
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
			if (this.validarNegocio(model, entidad.getNota(), request)){
				if (entidad.getListaDetalleNota() == null || entidad.getListaDetalleNota().size() <= 0){
					exitoso = false;
					model.addAttribute("respuesta", "Debe ingresar el detalle de la nota");
					return exitoso;
				}
				/*if (entidad.getLeyendaSunat().getCodigoSunat() == null || entidad.getLeyendaSunat().getCodigoSunat().equals("")){
					exitoso = false;
					model.addAttribute("respuesta", "Debe seleccionar la leyenda");
					return exitoso;
				}*/
				
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
	public TblNota getNuevaEntidad() {
		return new TblNota();
	}
	/*
	 * Asignamos los detalles del comprobante al detalle de la nota
	 */
	public List<TblDetalleNota> mAsignarDetalleComprobanteANota(List<TblDetalleComprobante> listaDetComp){
		List<TblDetalleNota> listaDetNota 	= null;
		TblDetalleNota detalleNota 			= null;
		
		if (listaDetComp!=null && listaDetComp.size()>0){
			for(TblDetalleComprobante detalleComprobante:listaDetComp){
				detalleNota = new TblDetalleNota();
				detalleNota.setCantidad(detalleComprobante.getCantidad());
				detalleNota.setDescripcion(detalleComprobante.getDescripcion());
				detalleNota.setDescuento(detalleComprobante.getDescuento());
				detalleNota.setMoneda(detalleComprobante.getMoneda());
				detalleNota.setPrecioFinal(detalleComprobante.getPrecioFinal());
				detalleNota.setPrecioTotal(detalleComprobante.getPrecioTotal());
				detalleNota.setPrecioUnitario(detalleComprobante.getPrecioUnitario());
				detalleNota.setTipoAfectacion(detalleComprobante.getTipoAfectacion());
				detalleNota.setUnidadMedida(detalleComprobante.getUnidadMedida());
				detalleNota.setValorReferencia(detalleComprobante.getValorReferencia());
				if (listaDetNota == null) {
					listaDetNota = new ArrayList<TblDetalleNota>();
					listaDetNota.add(detalleNota);
				}else{
					listaDetNota.add(detalleNota);
				}
			}
		}
		
		return listaDetNota;
	}
	/*
	 * Asigna los datos del comprobante a la nota
	 */
	public TblNota mAsignarComprobanteANota(TblNota nota, TblComprobante comprobante){
		nota.setCodigoDomicilio(comprobante.getCodigoDomicilio());
		nota.setDireccionCliente(comprobante.getDireccionCliente());
		nota.setMoneda(comprobante.getMoneda());
		nota.setTipoDocumento(comprobante.getTipoDocumento());
		nota.setNombreCliente(comprobante.getNombreCliente());
		nota.setNumeroDocumento(comprobante.getNumeroDocumento());
		nota.setSumatoriaIsc(comprobante.getSumatoriaIsc());
		nota.setSumatorioaOtrosTributos(comprobante.getSumatorioaOtrosTributos());
		nota.setTblComprobante(comprobante);
		nota.setTipoComprobanteAfecta(comprobante.getTipoComprobante());
		nota.setTotalIgv(comprobante.getTotalIgv());
		nota.setTotalImporte(comprobante.getTotalImporte());
		nota.setTotalOpExonerada(comprobante.getTotalOpExonerada());
		nota.setTotalOpGravada(comprobante.getTotalOpGravada());
		nota.setTotalOpInafecta(comprobante.getTotalOpInafecta());
		nota.setTotalOtrosCargos(comprobante.getTotalOtrosCargos());
		
		return nota;
	}
	/*
	 * Calculo del Detalle 
	 */
	public void calculoDetalleComprobante(Filtro entidad){
		TblDetalleComprobante detalle = null;
		try{
			detalle = entidad.getDetalleComprobante();
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
		//TblComprobante comprobante = null;
		try{
			//comprobante = entidad.getComprobante();
			//LOGGER.debug(comprobante.getTipoOperacion());
			
			this.calculoCabeceraComprobanteNacional(entidad);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/*
	 * Calculo de la cabecera
	 */
	private void calculoCabeceraComprobanteNacional(Filtro entidad){
		TblComprobante comprobante = null;
		try{
			comprobante = entidad.getComprobante();
			//inicializa
			comprobante.setDescuentosGlobales(new BigDecimal("0"));
			comprobante.setTotalImporte(new BigDecimal("0"));
			comprobante.setValorOpGratuitas(new BigDecimal("0"));
			if (entidad.getListaDetalle()!=null && entidad.getListaDetalle().size()>0){
				for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
					//Descuento : Precio Total - Precio Final (monto de descuento)
					comprobante.setDescuentosGlobales(comprobante.getDescuentosGlobales().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));

					//Total Importe: Precio Unitario x Cantidad - Descuento
					comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
					comprobante.setTotalImporte(comprobante.getTotalImporte().subtract(comprobante.getDescuentosGlobales()));
					//Total de valor de referencia
					comprobante.setValorOpGratuitas(comprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				}
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
	/**
	 * Se encarga de guardar la informacion del Nota
	 * 
	 * @param notaBean
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/notas/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, Filtro entidad, HttpServletRequest request) {
		String path = "operacion/nota/not_listado";
		boolean exitoso 						= false;
		TblNota nota				= null;
		TblSunatCabeceraNota cabecera				= null;
		TblSunatDetalleNota detalleSunat			= null;
		List<TblSunatDetalleNota> listaDetalle		= new ArrayList<TblSunatDetalleNota>();
		List<TblDetalleNota> listaDetNota 			= null;
		List<TblDetalleComprobante> listaDetComp = null;
		String nombreLeyendaFile					= "";
		//TblComprobante comprobante					= null;
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			//listaDetNota  = (ArrayList<TblDetalleNota>)request.getSession().getAttribute("listaDetalleSession");
			listaDetComp  = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalle(listaDetComp);
			
			//Asignar Detalle de Comprobante a Detalle de Nota
			listaDetNota = this.mAsignarDetalleComprobanteANota(listaDetComp);
			entidad.setListaDetalleNota(listaDetNota);
			//Asignar Comprobante a Nota
			entidad.setNota(this.mAsignarComprobanteANota(entidad.getNota(), entidad.getComprobante()));
			//Datos Adicionales
			entidad.getNota().setFechaEmision( UtilSGT.getDateStringFormat(UtilSGT.getDatetoString(entidad.getFechaEmision())) );
			entidad.getNota().setTipoComprobante(entidad.getTipoComprobanteNota());
			entidad.getNota().setSerie(entidad.getSerieNota());
			entidad.getNota().setNumero(entidad.getNumeroNota());
			
			if (this.validarNegocioListado(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				
				//this.calculoDetalleComprobante(entidad);
				this.calculoCabeceraComprobante(entidad);
				
				listaDetNota = this.mAsignarDetalleComprobanteANota(entidad.getListaDetalle());
				entidad.setListaDetalleNota(listaDetNota);
				//Asignar Comprobante a Nota
				entidad.setNota(this.mAsignarComprobanteANota(entidad.getNota(), entidad.getComprobante()));
				
				
				/*this.calculoDetalleNota(entidad);
				this.calculoCabeceraNota(entidad);*/
				
				
				this.preGuardarListado(entidad, request);
				this.preGuardarLeyenda(entidad.getLeyendaSunat(), request);
				entidad.getNota().setCodigoVerificacion(UUID.randomUUID().toString());
				//Guardar el nota
				exitoso = super.guardar(entidad.getNota(), model);
				//notaDao.save(entidad.getNota());
				//exitoso = true;
				//Buscar Nota
				if (exitoso){
					nota = notaDao.obtenerNota(entidad.getNota().getCodigoVerificacion());
					for(TblDetalleNota detalle: entidad.getListaDetalleNota()){
						LOGGER.debug("[guardarEntidad] unidad:" +detalle.getUnidadMedida());
						detalle.setTblNota(nota);
						detalleNotaDao.save(detalle);
						exitoso = true;
					}
					//Leyenda
					nombreLeyendaFile = Constantes.SUNAT_RUC_EMISOR+"-"+nota.getTipoComprobante()+"-"+nota.getSerie()+"-"+nota.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA;
					/*if(exitoso){
						
						if (entidad.getLeyendaSunat()!=null && !entidad.getLeyendaSunat().getCodigoSunat().equals("")){
							entidad.getLeyendaSunat().setTblComprobante(entidad.getComprobante());
							entidad.getLeyendaSunat().setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+nota.getTipoNota()+"-"+nota.getSerie()+"-"+nota.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA);
							leyendaNotaDao.save(entidad.getLeyendaSunat());
						}
						exitoso = true;
					}*/
					LOGGER.debug("[guardarEntidad] Inicio del registro de la cabecera Sunat:"+exitoso);
					if (exitoso){
						//Grabar Datos para la sunat
						cabecera = this.registrarCabeceraSunat(nota, request, entidad);
						LOGGER.debug("[guardarEntidad] cabecera:"+cabecera);
						if (cabecera !=null){
							LOGGER.debug("[guardarEntidad] Inicio del registro del detalle sunat.....:");
							for(TblDetalleNota detalle: entidad.getListaDetalleNota()){
								LOGGER.debug("[guardarEntidad] Antes del detalle:"+detalle);
								detalleSunat = this.registrarDetalleSunat(cabecera, nota, detalle, request, entidad);
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
									if (this.generarArchivoLeyenda(entidad.getLeyendaSunat(),nota, nombreLeyendaFile, entidad)){
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
							path = "operacion/nota/not_nuevo";
							model.addAttribute("filtro", entidad);
						}
						//Generar Archivo
						path = "operacion/nota/not_listado";
						this.mListarNota(model, entidad,new PageableSG(), this.urlPaginado, request);
					}else{
						path = "operacion/nota/not_nuevo";
						model.addAttribute("filtro", entidad);
					}
				}
				
				
			}else{
				path = "operacion/nota/not_nuevo";
				model.addAttribute("filtro", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("respuesta", "Se generó un error :"+e.getMessage());
		}finally{
			nota				= null;
		}
		return path;
		
	}
	
	/*
	 * Registro de los datos de la cabecera para la sunat
	 */
	public TblSunatCabeceraNota registrarCabeceraSunat(TblNota nota, HttpServletRequest request, Filtro entidad){
		TblSunatCabeceraNota cabecera = null;
		try{
			cabecera = new TblSunatCabeceraNota();
			//Codigo Tipo Nota 
			cabecera.setTipoNota(nota.getTipoNota());
			//Descripcion
			cabecera.setDescripcionTipoNota(nota.getDescripcionNota());
			//Tipo Documento que modifica
			cabecera.setCodigoComprobanteAfecta(nota.getTblComprobante().getTipoComprobante());
			//Serie-Numero que afecta
			cabecera.setSerieNumeroAfecta(nota.getTblComprobante().getSerie()+"-"+nota.getTblComprobante().getNumero());

			
			//Tipo Operacion
			//cabecera.setTipoOperacion(nota.getTipoOperacion());
			//Fecha Emision
			//cabecera.setFechaEmision(UtilSGT.getFecha("yyyy-MM-dd"));
			cabecera.setFechaEmision(nota.getFechaEmision());
			//Domicilio Fiscal
			//cabecera.setDomicilioFiscal(new Integer(nota.getCodigoDomicilio()));
			//Datos del Cliente
			cabecera.setTipoDocumentoUsuario(nota.getTipoDocumento());
			cabecera.setNumeroDocumento(nota.getNumeroDocumento());
			cabecera.setRazonSocial(nota.getNombreCliente());
			//Moneda
			cabecera.setTipoMoneda(nota.getMoneda());
			//Descuentos Globales
			//cabecera.setSumaDescuento(nota.getDescuentosGlobales());
			cabecera.setSumaCargo(nota.getTotalOtrosCargos());
			//cabecera.setTotalDescuento(nota.getTotalDescuento());
			//Total valor de venta - Operaciones gravadas
			//cabecera.setOperacionGravada(this.obtenerTotalMontoGravada(nota.getTotalImporte(), Constantes.SUNAT_IGV));
			//cabecera.setOperacionGravada(this.obtenerTotalMontoGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			cabecera.setOperacionGravada(nota.getTotalOpGravada());
			//Total valor de venta - Operaciones inafectas
			cabecera.setOperacionInafecta(nota.getTotalOpInafecta());
			//Total valor de venta - Operaciones exoneradas
			cabecera.setOperacionExonerada(nota.getTotalOpExonerada());
			//Sumatoria IGV
			//cabecera.setMontoIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), Constantes.SUNAT_IGV));
			//cabecera.setMontoIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			cabecera.setMontoIgv(nota.getTotalIgv());
			//Sumatoria ISC
			cabecera.setMontoIsc(nota.getSumatoriaIsc());
			//Sumatoria otros tributos
			cabecera.setOtrosTributos(nota.getSumatorioaOtrosTributos());
			//Importe total de la venta, cesión en uso o del servicio prestado
			cabecera.setImporteTotal(nota.getTotalImporte());
			//Nombre archivo
			cabecera.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+nota.getTipoComprobante()+"-"+nota.getSerie()+"-"+nota.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_CABECERA_NOTA);
			this.preGuardarSunatCabeceraNota(cabecera, request);
			//CxC Documento
			cabecera.setTblNota(nota);
			//Registro de la cabecera de la sunat
			sunatCabeceraNotaDao.save(cabecera);
			cabecera = sunatCabeceraNotaDao.findByCodigoDocumento(nota.getCodigoNota());
		}catch(Exception e){
			e.printStackTrace();
			cabecera = null;
		}
		return cabecera;
	}
	
	/*
	 * Registro de los datos del detalle para la sunat
	 */
	public TblSunatDetalleNota registrarDetalleSunat(TblSunatCabeceraNota cabecera, TblNota nota, TblDetalleNota detalleNota, HttpServletRequest request, Filtro entidad){
		TblSunatDetalleNota detalle 				= null;
		try{
			/*if (nota.getTipoOperacion().equals(Constantes.SUNAT_TIPO_OPERACION_EXPORTACION)){
				detalle = this.calculoDetalleSunatExportacion(cabecera, nota, detalleNota, request, entidad);
			}else{*/
				detalle = this.calculoDetalleSunatNacional(cabecera, nota, detalleNota, request, entidad);	
			/*}*/
			
			sunatDetalleNotaDao.save(detalle);
		}catch(Exception e){
			e.printStackTrace();
			detalle = null;
		}
		return detalle;
	}
	/*
	 * Calculo de venta Exportacion - Detalle Sunat
	 */
	/*private TblSunatDetalleNota calculoDetalleSunatExportacion(TblSunatCabeceraNota cabecera, TblNota nota, TblDetalleNota detalleNota, HttpServletRequest request, Filtro entidad){
		TblSunatDetalleNota detalle 				= null;
		BigDecimal temporal						= null;
		try{
			//registro del detalle del nota
			detalle = new TblSunatDetalleNota();
			//Código de unidad de medida por ítem
			detalle.setCodigoUnidad(detalleNota.getUnidadMedida());
			//Cantidad de unidades por ítem
			detalle.setCantidad(detalleNota.getCantidad().toString());
			//Código de producto
			detalle.setCodigoProducto("");
			//Codigo producto SUNAT
			detalle.setCodigoProductoSunat("");
			//Descripción detallada del servicio prestado, bien vendido o cedido en uso, indicando las características.
			detalle.setDescripcion(detalleNota.getDescripcion());
			//Valor unitario por ítem
			//detalle.setValorUnitario(cabecera.getOperacionGravada().toString());
			//detalle.setValorUnitario(this.obtenerTotalMontoGravada(detalleNota.getPrecioTotal(), Constantes.SUNAT_IGV).toString());
			temporal = this.obtenerTotalMontoGravada(detalleNota.getPrecioFinal(), 0 , entidad.getValorServicio()); //IGV = 0
			temporal = temporal.divide(new BigDecimal(detalleNota.getCantidad().toString()), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			
			detalle.setValorUnitario(temporal.toString());
			//Descuentos por item
			BigDecimal descuento = new BigDecimal((detalleNota.getPrecioTotal().doubleValue()*detalleNota.getDescuento().doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
			detalle.setDescuento(descuento.toString());
			//Monto de IGV por ítem
			//detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleNota.getPrecioFinal(), Constantes.SUNAT_IGV).toString());
			detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleNota.getPrecioFinal(), 0 , entidad.getValorServicio()).toString()); //IGV = 0
			//Afectación al IGV por ítem
			detalle.setAfectacionIgv(detalleNota.getTipoAfectacion());
			//Monto de ISC por ítem
			detalle.setMontoIsc("0");
			//Tipo de sistema ISC
			detalle.setTipoIsc("");
			//Precio de venta unitario por item
			detalle.setPrecioVentaUnitario(detalleNota.getPrecioTotal().toString());
			//Valor de venta por ítem
			detalle.setValorVentaItem(detalleNota.getPrecioFinal().toString());
			//Archivo
			detalle.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+nota.getTipoNota()+"-"+nota.getSerie()+"-"+nota.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_DETALLE);
			//Falta auditoria
			this.preGuardarSunatDetalleNota(detalle, request);
			detalle.setTblSunatCabeceraNota(cabecera);
			sunatDetalleNotaDao.save(detalle);
		}catch(Exception e){
			e.printStackTrace();
			detalle = null;
		}
		return detalle;
	}*/
	
	private TblSunatDetalleNota calculoDetalleSunatNacional(TblSunatCabeceraNota cabecera, TblNota nota, TblDetalleNota detalleNota, HttpServletRequest request, Filtro entidad){
		TblSunatDetalleNota detalle 				= null;
		BigDecimal temporal						= null;
		String strTipoSistemaISC				= null;
		try{
			//registro del detalle del nota
			detalle = new TblSunatDetalleNota();
			//Código de unidad de medida por ítem
			detalle.setCodigoUnidad(detalleNota.getUnidadMedida());
			//Cantidad de unidades por ítem
			detalle.setCantidad(detalleNota.getCantidad().toString());
			//Código de producto
			detalle.setCodigoProducto("");
			//Codigo producto SUNAT
			detalle.setCodigoProductoSunat("");
			//Descripción detallada del servicio prestado, bien vendido o cedido en uso, indicando las características.
			detalle.setDescripcion(detalleNota.getDescripcion());
			//Valor unitario por ítem
			//detalle.setValorUnitario(cabecera.getOperacionGravada().toString());
			//detalle.setValorUnitario(this.obtenerTotalMontoGravada(detalleNota.getPrecioTotal(), Constantes.SUNAT_IGV).toString());
			temporal = this.obtenerTotalMontoGravada(detalleNota.getPrecioFinal(), entidad.getValorIGV(), entidad.getValorServicio());
			temporal = temporal.divide(new BigDecimal(detalleNota.getCantidad().toString()) , 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			
			detalle.setValorUnitario(temporal.toString());
			//Descuentos por item
			BigDecimal descuento = new BigDecimal((detalleNota.getPrecioTotal().doubleValue()*detalleNota.getDescuento().doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
			detalle.setDescuento(descuento.toString());
			//Monto de IGV por ítem
			//detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleNota.getPrecioFinal(), Constantes.SUNAT_IGV).toString());
			detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleNota.getPrecioFinal(), entidad.getValorIGV(), entidad.getValorServicio()).toString());
			//Afectación al IGV por ítem
			detalle.setAfectacionIgv(detalleNota.getTipoAfectacion());
			//Monto de ISC por ítem
			detalle.setMontoIsc("0");
			//Tipo de sistema ISC
			strTipoSistemaISC = this.obtenerParametro(request, Constantes.PARAMETRO_TIPO_SISTEMA_CALCULO_ISC);
			detalle.setTipoIsc(strTipoSistemaISC);
			//Precio de venta unitario por item
			detalle.setPrecioVentaUnitario(detalleNota.getPrecioTotal().toString());
			//Valor de venta por ítem
			detalle.setValorVentaItem(detalleNota.getPrecioFinal().toString());
			//Archivo
			detalle.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+nota.getTipoComprobante()+"-"+nota.getSerie()+"-"+nota.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_DETALLE);
			//Falta auditoria
			this.preGuardarSunatDetalleNota(detalle, request);
			detalle.setTblSunatCabeceraNota(cabecera);
			sunatDetalleNotaDao.save(detalle);
		}catch(Exception e){
			e.printStackTrace();
			detalle = null;
		}
		return detalle;
	}
	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String obtenerParametro(HttpServletRequest request, String strParametro){
		Map<String, TblParametro> mapParametro	= null;
		TblParametro parametro 					= null; 
		String resultado						= "";
		mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
		if (mapParametro !=null){
			parametro = mapParametro.get(strParametro);
			if (parametro!=null){
				resultado =parametro.getDato();
			}else{
				resultado = "01";
			}
		}else{
			resultado = "01";
		}
		return resultado;
	}
	/*
	 * Se asigna los datos del campo de auditoria
	 */
	public void preGuardarSunatDetalleNota(TblSunatDetalleNota entidad, HttpServletRequest request) {
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			LOGGER.debug("[preGuardarSunatDetalleNota] Inicio" );
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardarSunatDetalleNota] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			LOGGER.debug("[preGuardarSunatDetalleNota] idUsuario:"+idUsuario );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(idUsuario);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			LOGGER.debug("[preGuardarSunatDetalleNota] Fin" );
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
	public void preGuardarSunatCabeceraNota(TblSunatCabeceraNota entidad, HttpServletRequest request) {
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			LOGGER.debug("[preGuardarSunatCabeceraNota] Inicio" );
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardarSunatCabeceraNota] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			LOGGER.debug("[preGuardarSunatCabeceraNota] idUsuario:"+idUsuario );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(idUsuario);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			LOGGER.debug("[preGuardarSunatCabeceraNota] Fin" );
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
	public boolean generarArchivoCabecera(TblSunatCabeceraNota cabecera, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = entidad.getSunatData() + cabecera.getNombreArchivo();
		LOGGER.debug("[generarArchivoCabecera] filename: "+FILENAME);
		try{
			cadena = cabecera.getFechaEmision() + Constantes.SUNAT_PIPE +
					 cabecera.getTipoNota() + 	Constantes.SUNAT_PIPE +
					 cabecera.getDescripcionTipoNota() + Constantes.SUNAT_PIPE +
					 cabecera.getCodigoComprobanteAfecta() + Constantes.SUNAT_PIPE +
					 cabecera.getSerieNumeroAfecta() + Constantes.SUNAT_PIPE +
					 cabecera.getTipoDocumentoUsuario() + Constantes.SUNAT_PIPE +
					 cabecera.getNumeroDocumento() + Constantes.SUNAT_PIPE +
					 cabecera.getRazonSocial()	+ Constantes.SUNAT_PIPE +
					 cabecera.getTipoMoneda() + Constantes.SUNAT_PIPE +
					 cabecera.getSumaCargo() + Constantes.SUNAT_PIPE +
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
	public boolean generarArchivoLeyenda(TblLeyenda leyenda, TblNota nota, String nombreFile, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = entidad.getSunatData() + nombreFile;
		try{
			
			
			bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
			//Leyenda de moneda
			if (nota.getTotalImporte().doubleValue() > 0){
				cadena = Constantes.SUNAT_LEYENDA_MONTO_LETRAS_1000 + 
						Constantes.SUNAT_PIPE + NumberToLetterConverter.convertNumberToLetter(nota.getTotalImporte().doubleValue(), nota.getMoneda());
				
				bufferedWriter.write(cadena);
				//Leyenda adicional
				if (leyenda !=null && !leyenda.getCodigoSunat().equals("")){
					bufferedWriter.newLine();
					cadena = leyenda.getCodigoSunat() + Constantes.SUNAT_PIPE +	leyenda.getDescripcionSunat();
					bufferedWriter.write(cadena); 
				}
			}else{
				// No se realiza nada
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
	 * Genera un archivo plano Leyenda
	 */
	public boolean generarArchivoAdicionalDetalle(Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;//Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
		try{
			for(TblDetalleNota detalle:entidad.getListaDetalleNota()){
				if (detalle.getValorReferencia()!=null && detalle.getValorReferencia().doubleValue()>0){
					if (FILENAME == null){
						FILENAME = entidad.getSunatData() + Constantes.SUNAT_RUC_EMISOR+"-"+entidad.getNota().getTipoNota()+"-"+entidad.getNota().getSerie()+"-"+entidad.getNota().getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_ADICIONAL_DETALLE;
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
	public boolean generarArchivoDetalle(List<TblSunatDetalleNota> listaDetalle, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;//Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
		try{
			for(TblSunatDetalleNota detalle:listaDetalle){
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
	 * Se encarga de direccionar a la pantalla de creacion del Nota
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/adicionarDetalle", method = RequestMethod.POST)
	public String adicionarDetalle(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/nota/not_nuevo";
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
	/*public void calculoDetalleNota(Filtro entidad){
		TblDetalleNota detalle = null;
		try{
			detalle = entidad.getDetalleNota();
			detalle.setPrecioTotal(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));
			if (detalle.getDescuento()!=null && detalle.getDescuento().doubleValue()>0){
				detalle.setPrecioFinal(new BigDecimal(detalle.getPrecioTotal().doubleValue() - detalle.getPrecioTotal().doubleValue()*detalle.getDescuento().doubleValue()/100).setScale(2, RoundingMode.HALF_UP));
			}else{
				detalle.setPrecioFinal(detalle.getPrecioTotal());
			}
				
		}catch(Exception e){
			
		}
	}*/
	/*
	 * Calculo de los totales	  
	 */
	/*public void calculoCabeceraNota(Filtro entidad){
		//TblNota nota = null;
		try{
			//nota = entidad.getNota();
			/*LOGGER.debug(nota.getTipoOperacion());
			if (nota.getTipoOperacion().equals(Constantes.SUNAT_TIPO_OPERACION_EXPORTACION)){
				this.calculoCabeceraComprobateExtranjero(entidad);
			}else{*/
			/*	this.calculoCabeceraNotaNacional(entidad);
			/*}*/
			
			/*nota = entidad.getNota();
			//inicializa
			nota.setDescuentosGlobales(new BigDecimal("0"));
			nota.setTotalImporte(new BigDecimal("0"));
			
			for(TblDetalleNota detalle: entidad.getListaDetalle()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				nota.setDescuentosGlobales(nota.getDescuentosGlobales().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));
				
				//Total Importe: Precio Unitario x Cantidad - Descuento
				nota.setTotalImporte(nota.getTotalImporte().add(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()).setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				nota.setTotalImporte(nota.getTotalImporte().subtract(nota.getDescuentosGlobales()));
			}
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			nota.setTotalDescuento(nota.getDescuentosGlobales().add(nota.getTotalOtrosCargos()));
			
			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//nota.setTotalIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), Constantes.SUNAT_IGV));
			nota.setTotalIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total sin igv
			nota.setTotalOpGravada(this.obtenerTotalMontoGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total de otros tributos: servicio
			nota.setSumatorioaOtrosTributos(this.obtenerTotalOtrosTributosGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			*/
		/*}catch(Exception e){
			
		}
		
	}*/
	
	/*private void calculoCabeceraNotaNacional(Filtro entidad){
		TblNota nota = null;
		try{
			nota = entidad.getNota();
			//inicializa
			//nota.setDescuentosGlobales(new BigDecimal("0"));
			nota.setTotalImporte(new BigDecimal("0"));
			//nota.setValorOpGratuitas(new BigDecimal("0"));
			for(TblDetalleNota detalle: entidad.getListaDetalleNota()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				//nota.setDescuentosGlobales(nota.getDescuentosGlobales().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));
				
				//Total Importe: Precio Unitario x Cantidad - Descuento
				nota.setTotalImporte(nota.getTotalImporte().add(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()).setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				//nota.setTotalImporte(nota.getTotalImporte().subtract(nota.getDescuentosGlobales()));
				//Total de valor de referencia
				//nota.setValorOpGratuitas(nota.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(new BigDecimal(detalle.getCantidad()).setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
			}
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			//nota.setTotalDescuento(nota.getDescuentosGlobales().add(nota.getTotalOtrosCargos()));
			
			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//nota.setTotalIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), Constantes.SUNAT_IGV));
			nota.setTotalIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total sin igv
			nota.setTotalOpGravada(this.obtenerTotalMontoGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total de otros tributos: servicio
			nota.setSumatorioaOtrosTributos(this.obtenerTotalOtrosTributosGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	/*private void calculoCabeceraComprobateExtranjero(Filtro entidad){
		TblNota nota = null;
		try{
			nota = entidad.getNota();
			//inicializa
			//nota.setDescuentosGlobales(new BigDecimal("0"));
			nota.setTotalImporte(new BigDecimal("0"));
			for(TblDetalleNota detalle: entidad.getListaDetalleNota()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				//nota.setDescuentosGlobales(nota.getDescuentosGlobales().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));
				
				//Total Importe: Precio Unitario x Cantidad - Descuento
				nota.setTotalImporte(nota.getTotalImporte().add(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()).setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				//nota.setTotalImporte(nota.getTotalImporte().subtract(nota.getDescuentosGlobales()));
			}
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			//nota.setTotalDescuento(nota.getDescuentosGlobales().add(nota.getTotalOtrosCargos()));
			
			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//nota.setTotalIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), Constantes.SUNAT_IGV));
			nota.setTotalIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), 0, entidad.getValorServicio())); // IGV = 0
			//Calculo del total sin igv
			nota.setTotalOpGravada(new BigDecimal("0"));// IGV = 0
			nota.setTotalOpInafecta(this.obtenerTotalMontoGravada(nota.getTotalImporte(), 0 , entidad.getValorServicio()));
			//Calculo del total de otros tributos: servicio
			nota.setSumatorioaOtrosTributos(this.obtenerTotalOtrosTributosGravada(nota.getTotalImporte(), 0 , entidad.getValorServicio()));// IGV = 0
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	/*
	 * Validacion del detalle
	 */
	public boolean validarDetalle(Model model, Filtro entidad, HttpServletRequest request){
		boolean exitoso = true;
		try{
			/*if (entidad.getComprobante().getTipoOperacion() == null || entidad.getComprobante().getTipoOperacion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar el tipo de operación");
				return exitoso;
			}*/
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
			/*LOGGER.debug("entidad.getComprobante().getTipoOperacion():"+entidad.getComprobante().getTipoOperacion());
			LOGGER.debug("entidad.getDetalleComprobante().getTipoAfectacion():"+entidad.getDetalleComprobante().getTipoAfectacion());
			if (entidad.getComprobante().getTipoOperacion().equals("02")){
				if (!entidad.getDetalleComprobante().getTipoAfectacion().equals("40")){
					exitoso = false;
					model.addAttribute("respuesta", "El tipo de afectación y tipo de operación deben corresponder a Exportación");
					return exitoso;
				}
			}*/
			
			
		}catch(Exception e){
			exitoso = false;
		}
		return exitoso;
	}
	/*
	 * Listado de Clientes
	 */
	/*@RequestMapping(value = "/operacion/notas/clientes", method = RequestMethod.POST)
	public String mostrarClientes(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/nota/not_cli_listado";
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
	}*/
	/*** Listado de Cliente ***/
	/*private void listarCliente(Model model, Filtro filtro){
		List<TblCliente> entidades = new ArrayList<TblCliente>();
		try{
			Specification<TblCliente> criterio = Specifications.where(conNumeroDocumento((filtro.getNumero())))
					.and(conNombreCliente(filtro.getNombre().toUpperCase()))
					.and(conEstadoCliente(Constantes.ESTADO_REGISTRO_ACTIVO));
			entidades = clienteDao.findAll(criterio);
			LOGGER.debug("[listarCliente] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}*/
	/*@RequestMapping(value = "/operacion/notas/clientes/q", method = RequestMethod.POST)
	public String listarClientes(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/nota/not_cli_listado";
		try{
			LOGGER.debug("[listarClientes] Inicio");
			if (validarNegocioCliente(model, filtro)){
				this.listarCliente(model, filtro);
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
	}*/
	/*private boolean validarNegocioCliente(Model model, Filtro filtro) {
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
	}*/
	/*
	 * Asignar Cliente
	 */
	/*@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/clientes/seleccionar/{id}", method = RequestMethod.GET)
	public String asignarClienteGet(@PathVariable Integer id, Model model, HttpServletRequest request) {
		TblCliente cliente 					= null;
		Filtro filtro						= null;
		String path							= null;
		List<TblDetalleNota> listaDetalle = null;
		try{
			listaDetalle = (ArrayList<TblDetalleNota>)request.getSession().getAttribute("listaDetalleSession");
			cliente = clienteDao.findOne(id);
			//Se mantiene en Session el contrato hasta retornar
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			if (cliente!=null ){
				filtro.getNota().setNombreCliente(cliente.getNombre());
				filtro.getNota().setNumeroDocumento(cliente.getNumeroDocumento());
				filtro.getNota().setTipoDocumento(cliente.getTblCatalogo().getCodigoSunat());
				filtro.getNota().setDireccionCliente(cliente.getDireccion());
			}else{
				filtro.getNota().setNombreCliente("");
				filtro.getNota().setNumeroDocumento("");
				filtro.getNota().setTipoDocumento("");
				filtro.getNota().setDireccionCliente("");
			}
			filtro.setListaDetalleNota(listaDetalle);	
			model.addAttribute("filtro", filtro);
			path = "operacion/nota/not_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cliente = null;
			filtro 	= null;
		}
		return path;
	}*/
	/*
	 * Regresa a la pantalla de nota
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/regresar", method = RequestMethod.POST)
	public String regresarNota(Model model, Filtro filtro, String path, HttpServletRequest request) {
		
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[regresarComprobante] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			filtro.setListaDetalle(listaDetalle);	
			LOGGER.debug("[regresarContrato] oPERACION:"+filtro.getStrOperacion());
			path = "operacion/nota/not_nuevo";
			
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
	@RequestMapping(value = "/operacion/notas/productos", method = RequestMethod.POST)
	public String mostrarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/nota/not_pro_listado";
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
 
	@RequestMapping(value = "/operacion/notas/productos/q", method = RequestMethod.POST)
	public String listarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/nota/not_pro_listado";
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
	 * Asignar Producto
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/productos/seleccionar/{id}", method = RequestMethod.GET)
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
			
			path = "operacion/nota/not_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			producto = null;
			filtro 	= null;
		}
		return path;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/eliminar/{id}", method = RequestMethod.GET)
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
			path = "operacion/nota/not_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			listaDetalle = null;
		}
		return path;
	}
	/*
	 * Muestra el nota como solo lectura
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/pdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> pdfNota(@PathVariable Integer id, HttpServletRequest request) {
		Filtro filtro								= null;
		List<TblDetalleNota> listaDetalle 			= null;
		TblNota nota								= null;
		HttpHeaders headers 						= new HttpHeaders();
		ByteArrayInputStream bis 					= null;
		TblSunatCabeceraNota sunatCabeceraNota		= null;
		List<TblSunatDetalleNota> listaDetalleSunat	= null;
		//NotaPdf notaPdf				= new NotaPdf();
		NotaKenorPdf notaPdf						= new NotaKenorPdf();
		//TblLeyenda leyenda							= null;
		TblCatalogo domicilio						= null;
		try{
			filtro = new Filtro();
			sunatCabeceraNota = sunatCabeceraNotaDao.findByCodigoDocumento(id);
			if (sunatCabeceraNota!=null && sunatCabeceraNota.getCodigoCabecera()>0){
				listaDetalleSunat = sunatDetalleNotaDao.findByCodigoCabecera(sunatCabeceraNota.getCodigoCabecera());
			}
			nota = notaDao.findOne(id);
			listaDetalle = detalleNotaDao.listarxNotaDetalle(id);
			//leyenda = leyendaDao.getxNota(id);
			//filtro.setLeyendaSunat(leyenda);
			filtro.setNota(nota);
			filtro.setListaDetalleNota(listaDetalle);
			filtro.setSunatCabeceraNota(sunatCabeceraNota);
			filtro.setListaDetalleSunatNota(listaDetalleSunat);
			filtro.setAppRutaContexto(request.getContextPath());
			filtro.setListaParametro((List<ParametroFacturadorBean>)request.getSession().getAttribute("SessionListParametro"));
			//Datos del Punto de Facturacion
			String codigoDomicilio = nota.getCodigoDomicilio();
			if (codigoDomicilio == null) {
				filtro.setNombreDomicilioFiscal("-");
			}else{
				domicilio = catalogoDao.getCatalogoxCodigoSunatxTipo(codigoDomicilio, Constantes.TIPO_CATALAGO_COD_DOMICILIO_FISCAL);
				if (domicilio !=null){
					filtro.setNombreDomicilioFiscal(domicilio.getNombre());
				}else{
					filtro.setNombreDomicilioFiscal("-");
				}
			}
			
			bis = notaPdf.notaReporte(filtro);
			
			
	        //headers.add("Content-Disposition", "inline; filename=Nota.pdf");
			headers.add("Content-Disposition", "attachment; filename="+nota.getSerie()+"-"+nota.getNumero()+".pdf");
	        

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			filtro 				= null;
			listaDetalle 		= null;
			nota			= null;
			
		}
		return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
	}
	
	private TblBandejaFacturadorNota setDatosBandeja(BandejaFacturadorNotaBean bandeja){
		TblBandejaFacturadorNota bandejaFacturadorNota = new TblBandejaFacturadorNota();
		try{
			bandejaFacturadorNota.setNumeroRuc(bandeja.getNumeroRuc());
			bandejaFacturadorNota.setTipoDocumento(bandeja.getTipoDocumento());
			bandejaFacturadorNota.setNumeroDocumento(bandeja.getNumeroDocumento());
			bandejaFacturadorNota.setFechaCargue(bandeja.getFechaCarga());
			bandejaFacturadorNota.setFechaGeneracion(bandeja.getFechaGeneracion());
			bandejaFacturadorNota.setFechaEnvio(bandeja.getFechaEnvio());
			bandejaFacturadorNota.setObservacion(bandeja.getObservacion());
			bandejaFacturadorNota.setNombreArchivo(bandeja.getNombreArchivo());
			bandejaFacturadorNota.setSituacion(bandeja.getSituacion());
			bandejaFacturadorNota.setTipoArchivo(bandeja.getTipoArchivo());
			bandejaFacturadorNota.setFirmaDigital(bandeja.getFirmaDigital());
			bandejaFacturadorNota.setEstado(Constantes.ESTADO_ACTIVO);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return bandejaFacturadorNota;
	}
	
	private TblBandejaFacturadorNota editarDatosBandeja(BandejaFacturadorNotaBean bandeja, TblBandejaFacturadorNota bandejaFacturadorNota){
	
		try{
			bandejaFacturadorNota.setNumeroRuc(bandeja.getNumeroRuc());
			bandejaFacturadorNota.setTipoDocumento(bandeja.getTipoDocumento());
			bandejaFacturadorNota.setNumeroDocumento(bandeja.getNumeroDocumento());
			bandejaFacturadorNota.setFechaCargue(bandeja.getFechaCarga());
			bandejaFacturadorNota.setFechaGeneracion(bandeja.getFechaGeneracion());
			bandejaFacturadorNota.setFechaEnvio(bandeja.getFechaEnvio());
			bandejaFacturadorNota.setObservacion(bandeja.getObservacion());
			bandejaFacturadorNota.setNombreArchivo(bandeja.getNombreArchivo());
			bandejaFacturadorNota.setSituacion(bandeja.getSituacion());
			bandejaFacturadorNota.setTipoArchivo(bandeja.getTipoArchivo());
			bandejaFacturadorNota.setFirmaDigital(bandeja.getFirmaDigital());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return bandejaFacturadorNota;
	}
	
	/*
	 * Se asigna los datos del campo de auditoria
	 */
	private void preGuardarBandeja(TblBandejaFacturadorNota entidad, HttpServletRequest request) {
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
	private void preEditarBandeja(TblBandejaFacturadorNota entidad, HttpServletRequest request) {
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
	@RequestMapping(value = "/operacion/notas/ver/actualizar", method = RequestMethod.POST)
	public String actualizarEstadoNota(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/nota/not_ver";
		TblNota nota					= null;
		TblBandejaFacturadorNota bandejaFacturadorNota		= null;
		BandejaFacturadorNotaBean bandejaBean			= null;
		BandejaFacturadorNotaBean bandejaRptaBean		= null;
		IOperacionFacturador operacionFacturador	= new OperacionFacturadorImp();
		try{
			LOGGER.debug("[actualizarEstadoNota] Inicio");
			filtro = (Filtro)request.getSession().getAttribute("filtroVerSession");
			nota = filtro.getNota();
			bandejaFacturadorNota = bandejaFacturadorNotaDao.findOne(nota.getCodigoNota());
			if (bandejaFacturadorNota == null){
				bandejaBean = new BandejaFacturadorNotaBean();
				bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
				bandejaBean.setTipoDocumento(nota.getTipoNota());
				bandejaBean.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
				bandejaRptaBean = operacionFacturador.consultarBandejaNota(bandejaBean, filtro);
				if (bandejaRptaBean!=null){
					bandejaFacturadorNota = this.setDatosBandeja(bandejaRptaBean);
					this.preGuardarBandeja(bandejaFacturadorNota, request);
					bandejaFacturadorNota.setTblNota(nota);
					bandejaFacturadorNotaDao.save(bandejaFacturadorNota);
					filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
					//Actualizando estado en el nota
					nota.setEstadoOperacion(bandejaFacturadorNota.getSituacion());
					notaDao.save(nota);
				}else{
					bandejaFacturadorNota = new TblBandejaFacturadorNota();
					bandejaFacturadorNota.setObservacion("En proceso de lectura por el facturador... (tiempo de espera 1 min.)");
					filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
				}
				
			}else{
				//Validando el estado
				if (bandejaFacturadorNota.getSituacion().equals("01") || bandejaFacturadorNota.getSituacion().equals("02")){
					bandejaBean = new BandejaFacturadorNotaBean();
					bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
					bandejaBean.setTipoDocumento(nota.getTipoNota());
					bandejaBean.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
					bandejaRptaBean = operacionFacturador.consultarBandejaNota(bandejaBean,filtro);
					if (bandejaRptaBean!=null){
						this.editarDatosBandeja(bandejaRptaBean, bandejaFacturadorNota);
						this.preEditarBandeja(bandejaFacturadorNota, request);
						bandejaFacturadorNota.setTblNota(nota);
						bandejaFacturadorNotaDao.save(bandejaFacturadorNota);
						//Eliminando comprobante de la bandeja
						bandejaFacturadorNotaDao.deleteByComprobante(nota.getCodigoNota());
						filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
						//Actualizando estado en el nota
						nota.setEstadoOperacion(bandejaFacturadorNota.getSituacion());
						notaDao.save(nota);
					}else{
						bandejaFacturadorNota = new TblBandejaFacturadorNota();
						bandejaFacturadorNota.setObservacion("En proceso de lectura por el facturador... (tiempo de espera 1 min.)");
						filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
					}
				}
				filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
			}
			request.getSession().setAttribute("filtroVerSession", filtro);
			
			
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[actualizarEstadoNota] Fin");
		}catch(Exception e){
			LOGGER.debug("[listarProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			filtro 				= null;
			nota			= null;
			bandejaFacturadorNota	= null;
			bandejaBean			= null;
			bandejaRptaBean		= null;
		}
		LOGGER.debug("[listarProducto] Fin");
		return path;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/ver/{id}", method = RequestMethod.GET)
	public String verNota(@PathVariable Integer id, Model model, HttpServletRequest request) {
		Filtro filtro								= null;
		String path									= null;
		List<TblDetalleNota> listaDetalle 	= null;
		TblNota nota					= null;
		TblLeyenda leyenda							= null;
		TblBandejaFacturadorNota bandejaFacturadorNota		= null;
		BandejaFacturadorNotaBean bandejaBean			= null;
		BandejaFacturadorNotaBean bandejaRptaBean		= null;
		IOperacionFacturador operacionFacturador	= new OperacionFacturadorImp();
		Map<String, TblParametro> mapParametro		= null;
		//TblParametro parametro =null;
		
		try{
			filtro = new Filtro();
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			
			this.mAsignarParametros(filtro, mapParametro, request);
			nota = notaDao.findOne(id);
			listaDetalle = detalleNotaDao.listarxNotaDetalle(id);
			//leyenda = leyendaDao.getxNota(id);
			filtro.setNota(nota);
			filtro.setListaDetalleNota(listaDetalle);
			filtro.setLeyendaSunat(leyenda);
			//Fechas
			filtro.setFechaEmision(nota.getFechaEmision());
			//filtro.setFechaVencimiento(nota.getFechaVencimiento());
			bandejaFacturadorNota = bandejaFacturadorNotaDao.buscarOneByNota(id);
			if (bandejaFacturadorNota == null){
				bandejaBean = new BandejaFacturadorNotaBean();
				bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
				bandejaBean.setTipoDocumento(nota.getTipoNota());
				bandejaBean.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
				bandejaRptaBean = operacionFacturador.consultarBandejaNota(bandejaBean,filtro);
				if (bandejaRptaBean!=null){
					bandejaFacturadorNota = this.setDatosBandeja(bandejaRptaBean);
					this.preGuardarBandeja(bandejaFacturadorNota, request);
					bandejaFacturadorNota.setTblNota(nota);
					bandejaFacturadorNotaDao.save(bandejaFacturadorNota);
					filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
					//Actualizando estado en el nota
					nota.setEstadoOperacion(bandejaFacturadorNota.getSituacion());
					notaDao.save(nota);
				}else{
					bandejaFacturadorNota = new TblBandejaFacturadorNota();
					bandejaFacturadorNota.setObservacion("En proceso de lectura por el facturador... (tiempo de espera 1 min.)");
					filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
				}
				
			}else{
				//Validando el estado
				if (bandejaFacturadorNota.getSituacion().equals("01") || bandejaFacturadorNota.getSituacion().equals("02")){
					bandejaBean = new BandejaFacturadorNotaBean();
					bandejaBean.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
					bandejaBean.setTipoDocumento(nota.getTipoNota());
					bandejaBean.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
					bandejaRptaBean = operacionFacturador.consultarBandejaNota(bandejaBean,filtro);
					if (bandejaRptaBean!=null){
						this.editarDatosBandeja(bandejaRptaBean, bandejaFacturadorNota);
						this.preEditarBandeja(bandejaFacturadorNota, request);
						bandejaFacturadorNota.setTblNota(nota);
						//Eliminando comprobante de la bandeja
						bandejaFacturadorNotaDao.deleteByComprobante(nota.getCodigoNota());
						//Registrando nuevos datos del comprobante en bandeja						
						bandejaFacturadorNotaDao.save(bandejaFacturadorNota);
						filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
						//Actualizando estado en el nota
						nota.setEstadoOperacion(bandejaFacturadorNota.getSituacion());
						notaDao.save(nota);
					}else{
						bandejaFacturadorNota = new TblBandejaFacturadorNota();
						bandejaFacturadorNota.setObservacion("En proceso de lectura por el facturador... (tiempo de espera 1 min.)");
						filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
					}
				}
				filtro.setBandejaFacturadorNota(bandejaFacturadorNota);
				
			}
			request.getSession().setAttribute("filtroVerSession", filtro);
			
			model.addAttribute("filtro", filtro);
			path = "operacion/nota/not_ver";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			filtro 				= null;
			listaDetalle 		= null;
			nota			= null;
			bandejaFacturadorNota	= null;
			bandejaBean			= null;
			bandejaRptaBean		= null;
		}
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/leyenda", method = RequestMethod.POST)
	public String asignarLeyenda(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/nota/not_nuevo";
		List<TblDetalleNota> listaDetalle = null;
		try{
			LOGGER.debug("[adicionarDetalle] Inicio");
			listaDetalle = (ArrayList<TblDetalleNota>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalleNota(listaDetalle);
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
	 * Se encarga de la eliminacion fisica del nota
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "operacion/notas/all/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarNota(@PathVariable Integer id, HttpServletRequest request, Model model, PageableSG pageable) {
		TblNota entidad						= null;
		List<TblDetalleNota> listaDetEntidad = null;
		TblSunatCabeceraNota sunat						= null;
		List<TblSunatDetalleNota> listaDetSunat			= null;
		//TblLeyenda leyenda							= null;
		String path 								= null;
		Filtro filtro 								= null;
		try{
			LOGGER.debug("[eliminarNota] Inicio");
			
			//Buscando
			entidad = notaDao.findOne(id);
			//29.04.2018: Estado de la nota vacio y no permite el registro (BUG)
			if (entidad!=null && (entidad.getEstadoOperacion() == null || !entidad.getEstadoOperacion().equals(Constantes.SUNAT_ESTADO_OPERACION_ACEPTADO))){
				listaDetEntidad = detalleNotaDao.listarxNotaDetalle(id);
				//leyenda = leyendaDao.getxNota(id);
				sunat = sunatCabeceraNotaDao.findByCodigoDocumento(id);
				if (sunat !=null && sunat.getCodigoCabecera()>0){
					listaDetSunat = sunatDetalleNotaDao.findByCodigoCabecera(sunat.getCodigoCabecera());
				}
				//Borrando
				if (listaDetSunat!=null){
					for(TblSunatDetalleNota detalle:listaDetSunat){
						sunatDetalleNotaDao.delete(detalle);
					}
				}
				if(sunat!=null){
					sunatCabeceraNotaDao.delete(sunat);
				}
				/*if (leyenda!=null){
					leyendaDao.delete(leyenda);
				}*/
				if (listaDetEntidad !=null){
					for(TblDetalleNota detalle: listaDetEntidad){
						detalleNotaDao.delete(detalle);
					}
				}
				notaDao.delete(entidad);
				model.addAttribute("respuesta", "Debe eliminó satisfactoriamente");
			}else{
				model.addAttribute("respuesta", "La Factura fue generado y aceptado por la SUNAT, no se puede eliminar");
			}
			
			path = "operacion/nota/not_listado";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setNumero("");;
			this.mListarNota(model, filtro, pageable, this.urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroNotaCriterio", filtro);
			LOGGER.debug("[eliminarNota] Fin");
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("respuesta", "Error en el proceso de eliminación: "+e.getMessage());
		}finally{
			entidad 		= null;
			listaDetEntidad	= null;
			//leyenda			= null;
			sunat			= null;
			listaDetSunat	= null;
			filtro		= null;
		}
		return path;
	}
	
	
	@RequestMapping(value = "/operacion/notas/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/nota/not_listado";
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
			filtro = (Filtro)request.getSession().getAttribute("sessionFiltroNotaCriterio");
			model.addAttribute("filtro", filtro);
			this.mListarNota(model, filtro, pageable,this.urlPaginado, request);
			
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
	 * Muestra el page para el listado de comprobantes (Factura, Boletas) para realizar la asignación de Comprobante en la nota 
	 *
	 */
	@RequestMapping(value = "/operacion/notas/mostrar/comprobante", method = RequestMethod.POST)
	public String mostrarComprobanteParaAsignar(Model model, Filtro filtro,  PageableSG pageable, HttpServletRequest request) {
		String path = null;
		path = "operacion/nota/not_com_listado";
		try{
			LOGGER.debug("[mostrarComprobanteParaAsignar] Inicio");
			request.getSession().setAttribute("filtroSession", filtro);
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			model.addAttribute("registros", null);
			model.addAttribute("page", null);

			LOGGER.debug("[mostrarComprobanteParaAsignar] Fin");
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
	 * Realiza la busqueda de comprobantes para poder asignar a la Nota
	 */
	@RequestMapping(value = "/operacion/notas/mostrar/comprobante/q", method = RequestMethod.POST)
	public String buscarComprobanteParaAsignar(Model model, Filtro filtro,  PageableSG pageable, HttpServletRequest request) {
		String path = null;
		path = "operacion/nota/not_com_listado";
		try{
			LOGGER.debug("[mostrarComprobanteParaAsignar] Inicio");
			this.mListarComprobante(model, filtro, pageable, this.urlPaginado, request);
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[mostrarComprobanteParaAsignar] Fin");
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
	private void mListarComprobante(Model model, Filtro filtro,  PageableSG pageable, String url,  HttpServletRequest request){
		List<TblComprobante> entidades = new ArrayList<TblComprobante>();
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "numero"));
		Integer codigoEntidad = null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			Specification<TblComprobante> criterio = Specifications.where(conNumero(filtro.getNumero()))
					.and(conSerie(filtro.getSerie()))
					.and(com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conCodigoEmpresa(codigoEntidad))
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
	@RequestMapping(value = "/operacion/comprobantes/asignar/seleccion/{id}", method = RequestMethod.GET)
	public String asignarComprobanteANota(@PathVariable Integer id, Model model, String path,  HttpServletRequest request) {
		Filtro filtro = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/nota/not_nuevo";
			filtro = (Filtro)request.getSession().getAttribute("filtroSession");
			filtro.setComprobante(comprobanteDao.findOne(id));
			filtro.setListaDetalle(detalleComprobanteDao.listarxComprobante(id));
			request.getSession().setAttribute("filtroSession", filtro);
			request.getSession().setAttribute("listaDetalleSession", filtro.getListaDetalle());
			
			model.addAttribute("filtro", filtro);
			//request.getSession().setAttribute("sessionFiltroNotaCriterio", filtro);
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
	@RequestMapping(value = "/operacion/notas/mostrarNota", method = RequestMethod.POST)
	public String mostrarTipoNota(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/nota/not_nuevo";
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[mostrarTipoNota] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalle(listaDetalle);
			request.getSession().setAttribute("filtroSession", entidad);
			request.getSession().setAttribute("listaDetalleSession", entidad.getListaDetalle());
			model.addAttribute("filtro", entidad);
			LOGGER.debug("[mostrarTipoNota] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarTipoNota] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			listaDetalle = null;
		}
		LOGGER.debug("[mostrarTipoNota] Fin");
		return path;
	}
	/*
	 * Valida el detalle de la nota
	 */
	public boolean mValidarDetalleNota(Model model,Filtro filtro){
		boolean resultado = true;
		//Precio unitario
		if (filtro.getDetalleComprobante().getPrecioUnitario() == null || filtro.getDetalleComprobante().getPrecioUnitario().doubleValue()<=0){
			resultado = false;
			model.addAttribute("respuesta", "Debe ingresar el precio unitario");
			return resultado;
		}
		//cantidad
		if (filtro.getDetalleComprobante().getCantidad() == null || filtro.getDetalleComprobante().getCantidad().doubleValue()<=0){
			resultado = false;
			model.addAttribute("respuesta", "Debe ingresar la cantidad");
			return resultado;
		}
		//Descuento
		if (filtro.getDetalleComprobante().getDescuento() == null || filtro.getDetalleComprobante().getDescuento().doubleValue()<0){
			resultado = false;
			model.addAttribute("respuesta", "Debe ingresar el % de descuento");
			return resultado;
		}
		//Valor referencia
		if (filtro.getDetalleComprobante().getValorReferencia() == null || filtro.getDetalleComprobante().getValorReferencia().doubleValue()<0){
			resultado = false;
			model.addAttribute("respuesta", "Debe ingresar el valor de referencia");
			return resultado;
		}
		return resultado;
	}
	/**
	 * Actualiza los datos del detalle de la nota
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/notas/nuevo/detalle/actualizar", method = RequestMethod.POST)
	public String actualizarDetalleNota(Model model, Filtro filtro,  String path, HttpServletRequest request) {
		path = "operacion/nota/not_nuevo";
		TblDetalleComprobante detalleComprobante = null;
		List<TblDetalleComprobante> listaDetComp = null;
		Integer indice							 = null;
		try{
			LOGGER.debug("[actualizarDetalleNota] Inicio");
			indice = filtro.getIndice();
			listaDetComp  = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			
			if (this.mValidarDetalleNota(model, filtro)){
				detalleComprobante = filtro.getDetalleComprobante();
				filtro = (Filtro)request.getSession().getAttribute("filtroSession"); 
				filtro.setDetalleComprobante(detalleComprobante);
				this.calculoDetalleComprobante(filtro);
				listaDetComp.set(indice, filtro.getDetalleComprobante());
				filtro.setListaDetalle(listaDetComp);
				this.calculoCabeceraComprobante(filtro);
				filtro.setListaDetalle(listaDetComp);
				
				model.addAttribute("filtro", filtro);
				model.addAttribute("listaDetalleSession", filtro.getListaDetalle());
				request.getSession().setAttribute("listaDetalleSession",filtro.getListaDetalle());
				request.getSession().setAttribute("filtroSession",filtro);
				
			}else{
				model.addAttribute("filtro", filtro);
				path = "operacion/nota/not_nuevo_detalle";
			}
			
			LOGGER.debug("[actualizarDetalleNota] Fin");
		}catch(Exception e){
			LOGGER.debug("[actualizarDetalleNota] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*
	 * Edita un detalle de la lista de detalles de la nota
	 */
	@SuppressWarnings("unchecked")
	/*@RequestMapping(value = "/operacion/notas/detalle/editar/{id}", method = RequestMethod.GET)
	public String editarDetalleNota(@PathVariable Integer id,  HttpServletRequest request, Model model, PageableSG pageable) {
		String path 								= null;
		Filtro filtro 								= null;
		try{
			LOGGER.debug("[editarDetalleNota] Inicio");
			filtro = (Filtro)request.getSession().getAttribute("filtroSession");
			filtro.setDetalleComprobante(filtro.getListaDetalle().get(id));
			filtro.setIndice(id);
			path = "operacion/nota/not_nuevo_detalle";
			request.getSession().setAttribute("filtroSession",filtro);
			model.addAttribute("filtro", filtro);
			
			LOGGER.debug("[editarDetalleNota] Fin");
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("respuesta", "Error en la edicion del detalle "+e.getMessage());
		}finally{
			filtro		= null;
		}
		return path;
	}*/
	@RequestMapping(value = "/operacion/notas/detalle/editar", method = RequestMethod.POST)
	public String editarDetalleNota(Filtro filtro,  HttpServletRequest request, Model model, PageableSG pageable) {
		String path 								= null;
		List<TblDetalleComprobante> listaDetComp 	= null;
		try{
			LOGGER.debug("[editarDetalleNota] Inicio");
			listaDetComp  = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			filtro.setDetalleComprobante(listaDetComp.get(filtro.getIndice()));
			path = "operacion/nota/not_nuevo_detalle";
			request.getSession().setAttribute("filtroSession",filtro);
			model.addAttribute("filtro", filtro);
			
			LOGGER.debug("[editarDetalleNota] Fin");
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("respuesta", "Error en la edicion del detalle "+e.getMessage());
		}finally{
			filtro		= null;
		}
		return path;
	}
}