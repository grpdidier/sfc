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
import com.pe.lima.sg.dao.operacion.ILeyendaNotaDAO;
import com.pe.lima.sg.dao.operacion.INotaDAO;
import com.pe.lima.sg.dao.operacion.ISunatCabeceraNotaDAO;
import com.pe.lima.sg.dao.operacion.ISunatDetalleNotaDAO;
import com.pe.lima.sg.dao.operacion.ISunatTributoGeneralNotaDAO;
import com.pe.lima.sg.dao.operacion.ITributoGeneralNotaDAO;
import com.pe.lima.sg.db.util.IOperacionFacturador;
import com.pe.lima.sg.db.util.OperacionFacturadorImp;
import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.operacion.ImpuestoBolsa;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturadorNota;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleNota;
import com.pe.lima.sg.entity.operacion.TblLeyenda;
import com.pe.lima.sg.entity.operacion.TblLeyendaNota;
import com.pe.lima.sg.entity.operacion.TblNota;
import com.pe.lima.sg.entity.operacion.TblSunatCabeceraNota;
import com.pe.lima.sg.entity.operacion.TblSunatDetalleNota;
import com.pe.lima.sg.entity.operacion.TblSunatTributoGeneralNota;
import com.pe.lima.sg.entity.operacion.TblTributoGeneralNota;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.pdf.NotaKenorPdf;
//import com.pe.lima.sg.presentacion.pdf.NotaPdf;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.NumberToLetterConverter;
import com.pe.lima.sg.presentacion.util.OperacionUtil;
import com.pe.lima.sg.presentacion.util.PageWrapper;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;

/**
 * Clase que se encarga de la administracion de las Notas
 *
 * 			
 */
@Controller
public class NotaSfs12Action extends BaseOperacionPresentacion<TblNota> {

	@Autowired
	private IComprobanteDAO comprobanteDao;
	
	@Autowired
	private IDetalleComprobanteDAO detalleComprobanteDao;
	
	@Autowired
	private INotaDAO notaDao;

	@Autowired
	private IDetalleNotaDAO detalleNotaDao;
	
	@Autowired
	private ITributoGeneralNotaDAO tributoGeneralNotaDAO;
	
	@Autowired
	private ISunatCabeceraNotaDAO sunatCabeceraNotaDao;
	
	@Autowired
	private ISunatDetalleNotaDAO sunatDetalleNotaDao;

	@Autowired
	private ILeyendaNotaDAO leyendaNotaDao;
	
	@Autowired
	private ISunatTributoGeneralNotaDAO sunatTributoGeneralNotaDAO;
	
	@Autowired
	private IProductoDAO productoDao;

	
	@Autowired
	private IBandejaFacturadorNotaDAO bandejaFacturadorNotaDao;

	@Autowired
	private ICatalogoDAO catalogoDao;
	
	@Autowired
	private IEmpresaDAO empresaDao;
	

	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotaSfs12Action.class);
	

	private String urlPaginado = "/operacion/sfs12notas/paginado/"; 
	
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
	@RequestMapping(value = "/operacion/sfs12notas", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path,  PageableSG pageable,HttpServletRequest request) {
		Filtro filtro = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/sfs12nota/sfs12not_listado";
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
	@RequestMapping(value = "/operacion/sfs12notas/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/sfs12nota/sfs12not_listado";
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
	@RequestMapping(value = "operacion/sfs12notas/nuevo", method = RequestMethod.GET)
	public String crearNota(Model model, HttpServletRequest request) {
		Filtro filtro 							= null;
		Map<String, TblParametro> mapParametro	= null;
		try{
			LOGGER.debug("[crearNota] Inicio");
			filtro = new Filtro();
			filtro.setNota(new TblNota());
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			OperacionUtil.asignarParametros(filtro, mapParametro, request);
			filtro.setFechaEmision(UtilSGT.getFecha("dd/MM/yyyy"));
			filtro.setHoraEmision(UtilSGT.getHora());
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
		return "operacion/sfs12nota/sfs12not_nuevo";
	}
	
	/*
	 * Campos de auditoria para la Nota
	 */
	@Override
	public void preGuardar(TblNota entidad, HttpServletRequest request) {
		entidad.setAuditoriaCreacion(request);
		entidad.setTblEmpresa(empresaDao.findOne(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ));
	}
	/*
	 * Campos de auditoria para  el detalle de la nota
	 */
	public void preGuardarListado(Filtro filtro, HttpServletRequest request){
		this.preGuardar(filtro.getNota(), request);
		for(TblDetalleNota detalle: filtro.getListaDetalleNota()){
			detalle.setAuditoriaCreacion(request);
		}
	}
	/*
	 * Campos de auditoria para la leyenda
	 */
	public void preGuardarLeyenda(TblLeyenda entidad, HttpServletRequest request){
		entidad.setAuditoriaCreacion(request);
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
			total = notaDao.totalNota(entidad.getTipoNota(), entidad.getSerie(), entidad.getNumero(), codigoEmpresa);
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
				
				detalleNota.setUnidadMedida(detalleComprobante.getUnidadMedida());
				detalleNota.setCantidad(UtilSGT.getRoundDecimal(detalleComprobante.getCantidad(),2));
				detalleNota.setCodigoProducto(detalleComprobante.getCodigoProducto());
				detalleNota.setCodigoProductoSunat(detalleComprobante.getCodigoProductoSunat());
				detalleNota.setDescripcion(detalleComprobante.getDescripcion());
				detalleNota.setPrecioUnitario(UtilSGT.getRoundDecimal(detalleComprobante.getPrecioUnitario(),4));
				detalleNota.setSumTributosItem(UtilSGT.getRoundDecimal(detalleComprobante.getSumTributosItem(),2));
				detalleNota.setTribCodTipoTributoIgv(detalleComprobante.getTribCodTipoTributoIgv());
				detalleNota.setTribMontoIgv(UtilSGT.getRoundDecimal(detalleComprobante.getTribMontoIgv(),2));
				detalleNota.setTribBaseImponibleIgv(UtilSGT.getRoundDecimal(detalleComprobante.getTribBaseImponibleIgv(),2));
				detalleNota.setTribNombreTributo(detalleComprobante.getTribNombreTributo());
				detalleNota.setTribCodTipoTributo(detalleComprobante.getTribCodTipoTributo());
				detalleNota.setTribAfectacionIgv(detalleComprobante.getTribAfectacionIgv());
				detalleNota.setTribPorcentajeIgv(detalleComprobante.getTribPorcentajeIgv());
				detalleNota.setIscCodTipoTributoIsc(detalleComprobante.getIscCodTipoTributoIsc());
				detalleNota.setIscMontoIsc(UtilSGT.getRoundDecimal(detalleComprobante.getIscMontoIsc(),2));
				detalleNota.setIscBaseImponibleIsc(UtilSGT.getRoundDecimal(detalleComprobante.getIscBaseImponibleIsc(),2));
				detalleNota.setIscNombreTributo(detalleComprobante.getIscNombreTributo());
				detalleNota.setIscCodTipoTributo(detalleComprobante.getIscCodTipoTributo());
				detalleNota.setIscTipoSistema(detalleComprobante.getIscTipoSistema());
				detalleNota.setIscPorcentaje(detalleComprobante.getIscPorcentaje());
				detalleNota.setOtroCodTipoTributoOtro(detalleComprobante.getOtroCodTipoTributoOtro());
				detalleNota.setOtroMontoTributo(UtilSGT.getRoundDecimal(detalleComprobante.getOtroMontoTributo(),2));
				detalleNota.setOtroBaseImponibleOtro(UtilSGT.getRoundDecimal(detalleComprobante.getOtroBaseImponibleOtro(),2));
				detalleNota.setOtroNombreTributo(detalleComprobante.getOtroNombreTributo());
				detalleNota.setOtroCodTipoTributo(detalleComprobante.getOtroCodTipoTributo());
				detalleNota.setOtroPorcentaje(detalleComprobante.getOtroPorcentaje());
				detalleNota.setPrecioVentaUnitario(UtilSGT.getRoundDecimal(detalleComprobante.getPrecioVentaUnitario(),2));
				detalleNota.setValorVentaItem(UtilSGT.getRoundDecimal(detalleComprobante.getValorVentaItem(),2));
				detalleNota.setValorReferencialUnitario(UtilSGT.getRoundDecimal(detalleComprobante.getValorReferencialUnitario(),2));
				detalleNota.setValorUnitario(UtilSGT.getRoundDecimal(detalleComprobante.getValorUnitario(),4));
				
				detalleNota.setDescuento(UtilSGT.getRoundDecimal(detalleComprobante.getDescuento(),2));
				detalleNota.setMoneda(detalleComprobante.getMoneda());
				detalleNota.setPrecioFinal(UtilSGT.getRoundDecimal(detalleComprobante.getPrecioFinal(),2));
				detalleNota.setPrecioTotal(UtilSGT.getRoundDecimal(detalleComprobante.getPrecioTotal(),2));
				detalleNota.setTipoAfectacion(detalleComprobante.getTipoAfectacion());
				detalleNota.setValorReferencia(UtilSGT.getRoundDecimal(detalleComprobante.getValorReferencia(),2));
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
		nota.setSumatoriaIsc(UtilSGT.getRoundDecimal(comprobante.getSumatoriaIsc(), 2));
		nota.setSumatorioaOtrosTributos(UtilSGT.getRoundDecimal(comprobante.getSumatorioaOtrosTributos(), 2));
		nota.setTblComprobante(comprobante);
		nota.setTipoComprobanteAfecta(comprobante.getTipoComprobante());
		
		nota.setTotalIgv(UtilSGT.getRoundDecimal(comprobante.getTotalIgv(), 2));
		nota.setTotalImporte(UtilSGT.getRoundDecimal(comprobante.getTotalImporte(), 2));
		nota.setTotalOpExonerada(UtilSGT.getRoundDecimal(comprobante.getTotalOpExonerada(), 2));
		nota.setTotalOpGravada(UtilSGT.getRoundDecimal(comprobante.getTotalOpGravada(), 2));
		nota.setTotalOpInafecta(UtilSGT.getRoundDecimal(comprobante.getTotalOpInafecta(), 2));
		nota.setTotalOtrosCargos(UtilSGT.getRoundDecimal(comprobante.getTotalOtrosCargos(), 2));
		
		nota.setSumTributo(UtilSGT.getRoundDecimal(comprobante.getSumTributo(), 2));
		nota.setTotValorVenta(UtilSGT.getRoundDecimal(comprobante.getTotValorVenta(), 2));
		nota.setTotPrecioVenta(UtilSGT.getRoundDecimal(comprobante.getTotPrecioVenta(), 2));
		nota.setTotDescuento(UtilSGT.getRoundDecimal(comprobante.getTotDescuento(), 2));
		nota.setSumOtrosCargos(UtilSGT.getRoundDecimal(comprobante.getSumOtrosCargos(), 2));
		nota.setTotAnticipo(UtilSGT.getRoundDecimal(comprobante.getTotAnticipos(), 2));
		nota.setTotalImporte(UtilSGT.getRoundDecimal(comprobante.getTotalImporte(), 2));
		
		return nota;
	}
	
	public void setAsignarDatosAdicionalesNota(Filtro entidad){
		
		entidad.getNota().setTblComprobante(entidad.getComprobante());
		
		entidad.getNota().setMoneda(entidad.getComprobante().getMoneda());
		entidad.getNota().setTipoDocumento(entidad.getComprobante().getTipoDocumento());
		entidad.getNota().setNumeroDocumento(entidad.getComprobante().getNumeroDocumento());
		entidad.getNota().setNombreCliente(entidad.getComprobante().getNombreCliente());
		
		
		entidad.getNota().setFechaEmision( UtilSGT.getDateStringFormat(UtilSGT.getDatetoString(entidad.getFechaEmision())) );
		entidad.getNota().setTipoComprobante(entidad.getTipoComprobanteNota());
		entidad.getNota().setSerie(entidad.getSerieNota());
		entidad.getNota().setNumero(entidad.getNumeroNota());
		
		entidad.getNota().setTipoOperacion(entidad.getComprobante().getTipoOperacion());
		entidad.getNota().setHoraEmision(entidad.getHoraEmision());
		entidad.getNota().setCodigoDomicilio(entidad.getComprobante().getCodigoDomicilio());
		entidad.getNota().setVersionUbl(Constantes.SUNAT_VERSION_UBL);
		entidad.getNota().setCustomizacionDocumento(Constantes.SUNAT_CUSTOMIZACION);
		
		
	}
	
	public void preGuardarListadoTributosNota(Filtro filtro, HttpServletRequest request){
		LOGGER.debug("[preGuardarListadoTributosNota] Inicio" );
		
		for(TblTributoGeneralNota detalle: filtro.getListaTributoNota()){
			detalle.setAuditoriaCreacion(request);
		}
		LOGGER.debug("[preGuardarListadoTributosNota] Fin" );
	}
	private TblSunatTributoGeneralNota registroTributoSunatNota(TblSunatCabeceraNota cabecera, TblNota nota, TblTributoGeneralNota tblTributoGeneral, HttpServletRequest request, Filtro entidad){
		TblSunatTributoGeneralNota tblSunatTributoGeneralNota = new TblSunatTributoGeneralNota();

		tblSunatTributoGeneralNota.setIdentificadorTributo(tblTributoGeneral.getIdentificadorTributo());
		tblSunatTributoGeneralNota.setNombreTributo(tblTributoGeneral.getNombreTributo());
		tblSunatTributoGeneralNota.setCodigoTipoTributo(tblTributoGeneral.getCodigoTipoTributo());
		tblSunatTributoGeneralNota.setBaseImponible(UtilSGT.getRoundDecimalString(tblTributoGeneral.getBaseImponible(),2));
		tblSunatTributoGeneralNota.setMontoTributoItem(UtilSGT.getRoundDecimalString(tblTributoGeneral.getMontoTributoItem(),2));

		tblSunatTributoGeneralNota.setAuditoriaCreacion(request);
		
		tblSunatTributoGeneralNota.setTblSunatCabeceraNota(cabecera);
		tblSunatTributoGeneralNota.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+nota.getTipoComprobante()+"-"+nota.getSerie()+"-"+nota.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_TRIBUTO);
		sunatTributoGeneralNotaDAO.save(tblSunatTributoGeneralNota);

		return tblSunatTributoGeneralNota;
	}
	/**
	 * Se encarga de guardar la informacion del Nota
	 * 
	 * @param notaBean
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/sfs12notas/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, Filtro entidad, HttpServletRequest request) {
		String path = "operacion/sfs12nota/sfs12not_listado";
		boolean exitoso 						= false;
		TblNota nota				= null;
		TblSunatCabeceraNota cabecera				= null;
		TblSunatDetalleNota detalleSunat			= null;
		List<TblSunatDetalleNota> listaDetalle		= new ArrayList<TblSunatDetalleNota>();
		List<TblDetalleNota> listaDetNota 			= null;
		List<TblDetalleComprobante> listaDetComp = null;
		String nombreLeyendaFile					= "";
		List<TblSunatTributoGeneralNota> listaTriSunat = new ArrayList<>();
		TblSunatTributoGeneralNota tributoSunat		= null; 
		//TblComprobante comprobante					= null;
		TblTributoGeneralNota tributoIgv			= new TblTributoGeneralNota();
		TblTributoGeneralNota tributoExo			= new TblTributoGeneralNota();
		TblTributoGeneralNota tributoIna			= new TblTributoGeneralNota();
		TblTributoGeneralNota tributoGra			= new TblTributoGeneralNota();
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			//listaDetNota  = (ArrayList<TblDetalleNota>)request.getSession().getAttribute("listaDetalleSession");
			listaDetComp  = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			entidad.setListaDetalle(listaDetComp);
			entidad.setListaTributo(new ArrayList<>());
			entidad.setListaTributoNota(new ArrayList<>());
			
			//Asignar Comprobante a Nota
			//OperacionUtil.setDatosComprobanteSFS12(entidad);
			//entidad.setNota(this.mAsignarComprobanteANota(entidad.getNota(), entidad.getComprobante()));
			//Datos Adicionales
			this.setAsignarDatosAdicionalesNota(entidad);
			//Asignar Detalle de Comprobante a Detalle de Nota
			listaDetNota = this.mAsignarDetalleComprobanteANota(listaDetComp);
			entidad.setListaDetalleNota(listaDetNota);
			
			
			if (this.validarNegocioListado(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				
				//this.calculoDetalleComprobante(entidad);
				//OperacionUtil.calculoCabeceraComprobante(entidad);
				//OperacionUtil.setDatosComprobanteSFS12(entidad);
				
				OperacionUtil.calculoDetalleComprobante(entidad);
				OperacionUtil.calculoCabeceraComprobante(entidad);
				OperacionUtil.setDatosComprobanteSFS12(entidad);
				OperacionUtil.setDatosDetalleComprobanteSFS12(entidad);
				OperacionUtil.setDatosTributosNotaSFS12(entidad, tributoIgv, tributoExo, tributoIna, tributoGra);
				
				
				listaDetNota = this.mAsignarDetalleComprobanteANota(entidad.getListaDetalle());
				entidad.setListaDetalleNota(listaDetNota);
				
				OperacionUtil.setDatosComprobanteSFS12(entidad);
				//Asignar Comprobante a Nota
				entidad.setNota(this.mAsignarComprobanteANota(entidad.getNota(), entidad.getComprobante()));

				this.preGuardarListado(entidad, request);
				this.preGuardarLeyenda(entidad.getLeyendaSunat(), request);
				this.preGuardarListadoTributosNota(entidad, request);
				entidad.getNota().setCodigoVerificacion(UUID.randomUUID().toString());
				//Guardar el nota
				exitoso = super.guardar(entidad.getNota(), model);
				//Buscar Nota
				if (exitoso){
					nota = notaDao.obtenerNota(entidad.getNota().getCodigoVerificacion());
					for(TblDetalleNota detalle: entidad.getListaDetalleNota()){
						LOGGER.debug("[guardarEntidad] unidad:" +detalle.getUnidadMedida());
						detalle.setTblNota(nota);
						detalleNotaDao.save(detalle);
						exitoso = true;
					}
					//registro de los tributos
					for(TblTributoGeneralNota detalle: entidad.getListaTributoNota()){
						detalle.setTblNota(nota);
						tributoGeneralNotaDAO.save(detalle);
						exitoso = true;
					}
					//Leyenda
					nombreLeyendaFile = Constantes.SUNAT_RUC_EMISOR+"-"+nota.getTipoComprobante()+"-"+nota.getSerie()+"-"+nota.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA;
					if(exitoso){
						
						if (entidad.getLeyendaNotaSunat()!=null && !entidad.getLeyendaNotaSunat().getCodigoSunat().equals("")){
							entidad.getLeyendaNotaSunat().setTblNota(entidad.getNota());
							entidad.getLeyendaNotaSunat().setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+nota.getTipoNota()+"-"+nota.getSerie()+"-"+nota.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA);
							leyendaNotaDao.save(entidad.getLeyendaNotaSunat());
						}
						exitoso = true;
					}
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
							//registro de los tributos para la sunat
							for(TblTributoGeneralNota tblTributoGeneralNota: entidad.getListaTributoNota()){
								tributoSunat = this.registroTributoSunatNota(cabecera,nota, tblTributoGeneralNota, request, entidad);
								if (detalleSunat !=null){
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
									if (this.generarArchivoTributoNota(listaTriSunat, entidad)){
										if (this.generarArchivoLeyenda(entidad.getLeyendaSunat(),nota, nombreLeyendaFile, entidad)){
											//Generar adicional del detalle
											/*if (this.generarArchivoAdicionalDetalle(entidad)){
												model.addAttribute("respuesta", "Se generó el registro exitosamente");
											}else{
												model.addAttribute("respuesta", "Se generó un error en la creacion del detalle adicional [gratuita] del archivo SUNAT");
											}*/
											/*Registramos la bandeja del facturador*/
											registrarBandejaFacturadorNota(nota);
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
							path = "operacion/sfs12nota/sfs12not_nuevo";
							model.addAttribute("filtro", entidad);
						}
						//Generar Archivo
						path = "operacion/sfs12nota/sfs12not_listado";
						this.mListarNota(model, entidad,new PageableSG(), this.urlPaginado, request);
					}else{
						path = "operacion/sfs12nota/sfs12not_nuevo";
						model.addAttribute("filtro", entidad);
					}
				}
				
				
			}else{
				path = "operacion/sfs12nota/sfs12not_nuevo";
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
	
	private void registrarBandejaFacturadorNota(TblNota nota) {
		TblBandejaFacturadorNota bandejaFacturador = null;
		
		bandejaFacturador = getDatosBandejaFacturador(nota);
		bandejaFacturador.setTblNota(nota);
		LOGGER.debug("[registrarBandejaFacturadorNota] NEW bandejaFacturadorNotaDao.save:"+nota.getCodigoNota());
		bandejaFacturadorNotaDao.save(bandejaFacturador);
		nota.setEstadoOperacion(bandejaFacturador.getSituacion());
		notaDao.save(nota);
		
	}

	private TblBandejaFacturadorNota getDatosBandejaFacturador(TblNota nota) {
		TblBandejaFacturadorNota bandejaFacturador = new TblBandejaFacturadorNota();
		bandejaFacturador.setNumeroRuc(Constantes.SUNAT_RUC_EMISOR);
		bandejaFacturador.setTipoDocumento(nota.getTipoComprobante());
		bandejaFacturador.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
		bandejaFacturador.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR +"-"+nota.getSerie()+"-"+nota.getNumero());
		bandejaFacturador.setSituacion("01");
		bandejaFacturador.setTipoArchivo("TXT");
		bandejaFacturador.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
		return bandejaFacturador;
	}







	/*
	 * Registro de los datos de la cabecera para la sunat
	 */
	public TblSunatCabeceraNota registrarCabeceraSunat(TblNota nota, HttpServletRequest request, Filtro entidad){
		TblSunatCabeceraNota cabecera = null;
		try{
			cabecera = new TblSunatCabeceraNota();
			/**Tipo de operacion*/
			cabecera.setTipoOperacion(nota.getTipoOperacion());
			/**Hora de emision*/
			cabecera.setHoraEmision(nota.getHoraEmision());
			/**Codigo Domicilio Fiscal*/
			cabecera.setCodigoDomicilio(nota.getCodigoDomicilio());
			/**Sumatoria Tributos*/
			cabecera.setSumTributo(UtilSGT.getRoundDecimalString(nota.getSumTributo(), 2));
			/**Total valor de venta*/
			cabecera.setTotValorVenta(UtilSGT.getRoundDecimalString(nota.getTotValorVenta(), 2));
			/**Total precio de venta*/
			cabecera.setTotPrecioVenta(UtilSGT.getRoundDecimalString(nota.getTotPrecioVenta(), 2));
			/**Total Descuento*/
			cabecera.setTotDescuento(UtilSGT.getRoundDecimalString(nota.getTotDescuento(), 2));
			/**Sumatoria otros cargos*/
			cabecera.setSumOtrosCargos(UtilSGT.getRoundDecimalString(nota.getSumOtrosCargos(), 2));
			/**Total anticipos*/
			cabecera.setTotAnticipo(UtilSGT.getRoundDecimalString(nota.getTotAnticipo(), 2));
			/**Importe total de la venta*/
			cabecera.setImporteTotal(UtilSGT.getRoundDecimal(nota.getTotalImporte(), 2));
			/**version Ubl - Customizacion*/
			cabecera.setVersionUbl(nota.getVersionUbl());
			cabecera.setCutomizacionDocumento(nota.getCustomizacionDocumento());
			
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
			//cabecera.setSumaCargo(nota.getTotalOtrosCargos());
			//cabecera.setTotalDescuento(nota.getTotalDescuento());
			//Total valor de venta - Operaciones gravadas
			//cabecera.setOperacionGravada(this.obtenerTotalMontoGravada(nota.getTotalImporte(), Constantes.SUNAT_IGV));
			//cabecera.setOperacionGravada(this.obtenerTotalMontoGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//cabecera.setOperacionGravada(nota.getTotalOpGravada());
			//Total valor de venta - Operaciones inafectas
			//cabecera.setOperacionInafecta(nota.getTotalOpInafecta());
			//Total valor de venta - Operaciones exoneradas
			//cabecera.setOperacionExonerada(nota.getTotalOpExonerada());
			//Sumatoria IGV
			//cabecera.setMontoIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), Constantes.SUNAT_IGV));
			//cabecera.setMontoIgv(this.obtenerTotalImpuestoGravada(nota.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//cabecera.setMontoIgv(nota.getTotalIgv());
			//Sumatoria ISC
			//cabecera.setMontoIsc(nota.getSumatoriaIsc());
			//Sumatoria otros tributos
			//cabecera.setOtrosTributos(nota.getSumatorioaOtrosTributos());
			//Importe total de la venta, cesión en uso o del servicio prestado
			//cabecera.setImporteTotal(nota.getTotalImporte());
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
	
	
	private TblSunatDetalleNota calculoDetalleSunatNacional(TblSunatCabeceraNota cabecera, TblNota nota, TblDetalleNota detalleNota, HttpServletRequest request, Filtro entidad){
		TblSunatDetalleNota detalle 				= null;
		//BigDecimal temporal						= null;
		//String strTipoSistemaISC				= null;
		try{
			//registro del detalle del nota
			detalle = new TblSunatDetalleNota();
			//Código de unidad de medida por ítem
			detalle.setCodigoUnidad(detalleNota.getUnidadMedida());
			//Cantidad de unidades por ítem
			detalle.setCantidad(UtilSGT.getRoundDecimalString(detalleNota.getCantidad(), 2));
			//Código de producto
			detalle.setCodigoProducto(detalleNota.getCodigoProducto());
			//Codigo producto SUNAT
			detalle.setCodigoProductoSunat(Constantes.SUNAT_SIN_CODIGO);
			//Descripción detallada del servicio prestado, bien vendido o cedido en uso, indicando las características.
			detalle.setDescripcion(detalleNota.getDescripcion());
			//Valor unitario por ítem
			detalle.setValorUnitario(UtilSGT.getRoundDecimalString(detalleNota.getValorUnitario(),4));
			//Sumatoria tributos por item
			detalle.setSumTributosItem(UtilSGT.getRoundDecimalString(detalleNota.getSumTributosItem(),2));

			//Tributo: Códigos de tipos de tributos IGV
			detalle.setTribCodTipoTributoIgv(detalleNota.getTribCodTipoTributoIgv());
			//Tributo: Monto de IGV por ítem
			detalle.setTribMontoIgv(UtilSGT.getRoundDecimalString(detalleNota.getTribMontoIgv(),2));
			//Tributo: Base Imponible IGV por Item
			detalle.setTribBaseImponibleIgv(UtilSGT.getRoundDecimalString(detalleNota.getTribBaseImponibleIgv(),2));
			//Tributo: Nombre de tributo por item
			detalle.setTribNombreTributo(detalleNota.getTribNombreTributo());
			//Tributo: Código de tipo de tributo por Item
			detalle.setTribCodTipoTributo(detalleNota.getTribCodTipoTributo());
			//Tributo: Afectación al IGV por ítem
			detalle.setTribAfectacionIgv(detalleNota.getTribAfectacionIgv());
			//Tributo: Porcentaje de IGV
			detalle.setTribPorcentajeIgv(detalleNota.getTribPorcentajeIgv());
			//Tributo ISC: Códigos de tipos de tributos ISC
			detalle.setIscCodTipoTributoIsc(detalleNota.getIscCodTipoTributoIsc());
			//Tributo ISC: Monto de ISC por ítem
			detalle.setIscMontoIsc(UtilSGT.getRoundDecimalString(detalleNota.getIscMontoIsc(),2));
			//Tributo ISC: Base Imponible ISC por Item
			detalle.setIscBaseImponibleIsc(UtilSGT.getRoundDecimalString(detalleNota.getIscBaseImponibleIsc(),2));
			//Tributo ISC: Nombre de tributo por item
			detalle.setIscNombreTributo(detalleNota.getIscNombreTributo());
			//Tributo ISC: Código de tipo de tributo por Item
			detalle.setIscCodTipoTributo(detalleNota.getIscCodTipoTributo());
			//Tributo ISC: Tipo de sistema ISC
			detalle.setIscTipoSistema(detalleNota.getIscTipoSistema());
			//Tributo ISC: Porcentaje de ISC
			detalle.setIscPorcentaje(detalleNota.getIscPorcentaje().toString());
			//Tributo Otro: Códigos de tipos de tributos OTRO
			detalle.setOtroCodTipoTributoOtro(detalleNota.getOtroBaseImponibleOtro().toString());
			//Tributo Otro: Monto de tributo OTRO por iItem
			detalle.setOtroMontoTributo(UtilSGT.getRoundDecimalString(detalleNota.getOtroMontoTributo(),2));
			//Tributo Otro: Base Imponible de tributo OTRO por Item
			detalle.setOtroBaseImponibleOtro(UtilSGT.getRoundDecimalString(detalleNota.getOtroBaseImponibleOtro(),2));
			//Tributo Otro:  Nombre de tributo OTRO por item
			detalle.setOtroNombreTributo(detalleNota.getOtroNombreTributo());
			//Tributo Otro: Código de tipo de tributo OTRO por Item
			detalle.setOtroCodTipoTributo(detalleNota.getOtroCodTipoTributo());
			//Tributo Otro: Porcentaje de tributo OTRO por Item
			detalle.setOtroPorcentaje(detalleNota.getOtroPorcentaje());
			//Precio de venta unitario 
			detalle.setPrecioVentaUnitario(UtilSGT.getRoundDecimalString(detalleNota.getPrecioVentaUnitario(),2));
			//Valor de venta por Item 
			detalle.setValorVentaItem(UtilSGT.getRoundDecimalString(detalleNota.getValorVentaItem(),2));
			//Valor REFERENCIAL unitario (gratuitos) 
			detalle.setValorReferencialUnitario(UtilSGT.getRoundDecimalString(detalleNota.getValorReferencialUnitario(),2));

			//Resto de datos obsoletos	
			
			//Descuentos por item
			//BigDecimal descuento = new BigDecimal((detalleNota.getPrecioTotal().doubleValue()*detalleNota.getDescuento().doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
			//detalle.setDescuento(descuento.toString());
			//Monto de IGV por ítem
			//detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleNota.getPrecioFinal(), Constantes.SUNAT_IGV).toString());
			///detalle.setMontoIgv(OperacionUtil.obtenerTotalImpuestoGravada(detalleNota.getPrecioFinal(), entidad.getValorIGV(), entidad.getValorServicio()).toString());
			//Afectación al IGV por ítem
			//detalle.setAfectacionIgv(detalleNota.getTipoAfectacion());
			//Monto de ISC por ítem
			//detalle.setMontoIsc("0");
			//Tipo de sistema ISC
			//strTipoSistemaISC = OperacionUtil.obtenerParametro(request, Constantes.PARAMETRO_TIPO_SISTEMA_CALCULO_ISC);
			//detalle.setTipoIsc(strTipoSistemaISC);
			//Precio de venta unitario por item
			//detalle.setPrecioVentaUnitario(detalleNota.getPrecioTotal().toString());
			//Valor de venta por ítem
			//detalle.setValorVentaItem(detalleNota.getPrecioFinal().toString());
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

	/*
	 * Se asigna los datos del campo de auditoria
	 */
	public void preGuardarSunatDetalleNota(TblSunatDetalleNota entidad, HttpServletRequest request) {
		entidad.setAuditoriaCreacion(request);

	}
	
	/*
	 * Se asigna los datos del campo de auditoria
	 */
	public void preGuardarSunatCabeceraNota(TblSunatCabeceraNota entidad, HttpServletRequest request) {
		entidad.setAuditoriaCreacion(request);

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
			cadena = cabecera.getTipoOperacion() + Constantes.SUNAT_PIPE +
					 cabecera.getFechaEmision() + Constantes.SUNAT_PIPE +
					 cabecera.getHoraEmision() + Constantes.SUNAT_PIPE +
					// cabecera.getCodigoDomicilio() + Constantes.SUNAT_PIPE +
					 "0000" + Constantes.SUNAT_PIPE +
					 cabecera.getTipoDocumentoUsuario() + Constantes.SUNAT_PIPE +
					 cabecera.getNumeroDocumento() + Constantes.SUNAT_PIPE +
					 cabecera.getRazonSocial()	+ Constantes.SUNAT_PIPE +
					 cabecera.getTipoMoneda() + Constantes.SUNAT_PIPE +
					 cabecera.getTipoNota() + 	Constantes.SUNAT_PIPE +
					 cabecera.getDescripcionTipoNota() + Constantes.SUNAT_PIPE +
					 cabecera.getCodigoComprobanteAfecta() + Constantes.SUNAT_PIPE +
					 cabecera.getSerieNumeroAfecta() + Constantes.SUNAT_PIPE +
					 cabecera.getSumTributo() + Constantes.SUNAT_PIPE +
					 cabecera.getTotValorVenta() + Constantes.SUNAT_PIPE +
					 cabecera.getTotPrecioVenta() + Constantes.SUNAT_PIPE +
					 cabecera.getTotDescuento() + Constantes.SUNAT_PIPE +
					 cabecera.getSumOtrosCargos() + Constantes.SUNAT_PIPE +
					 cabecera.getTotAnticipo() + Constantes.SUNAT_PIPE +
					 cabecera.getImporteTotal() + Constantes.SUNAT_PIPE +
					 cabecera.getVersionUbl() + Constantes.SUNAT_PIPE +
					 cabecera.getCutomizacionDocumento();// + Constantes.SUNAT_PIPE +
					 /*cabecera.getSumaCargo() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionGravada() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionInafecta() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionExonerada() + Constantes.SUNAT_PIPE +
					 cabecera.getMontoIgv() + Constantes.SUNAT_PIPE +
					 cabecera.getMontoIsc() + Constantes.SUNAT_PIPE +
					 cabecera.getOtrosTributos() + Constantes.SUNAT_PIPE +
					 cabecera.getImporteTotal();*/
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
		/*ICBPER:06.06.2020 Inicio*/
		ImpuestoBolsa impuestoBolsa = new ImpuestoBolsa();
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
						 /*detalle.getDescuento() + Constantes.SUNAT_PIPE +
						 detalle.getMontoIgv() + Constantes.SUNAT_PIPE +
						 detalle.getAfectacionIgv() + Constantes.SUNAT_PIPE +
						 detalle.getMontoIsc() + Constantes.SUNAT_PIPE +
						 detalle.getTipoIsc() + Constantes.SUNAT_PIPE +
						 detalle.getPrecioVentaUnitario() + Constantes.SUNAT_PIPE +
						 detalle.getValorVentaItem();*/
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
	 * Genera un archivo plano Tributo
	 */
	public boolean generarArchivoTributoNota(List<TblSunatTributoGeneralNota> listaTributo, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;
		try{
			for(TblSunatTributoGeneralNota tributo:listaTributo){
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
	 * Se encarga de direccionar a la pantalla de creacion del Nota
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12notas/adicionarDetalle", method = RequestMethod.POST)
	public String adicionarDetalle(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12nota/sfs12not_nuevo";
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
			OperacionUtil.calculoDetalleComprobante(entidad);
			OperacionUtil.calculoCabeceraComprobante(entidad);
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
	 * Regresa a la pantalla de nota
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12notas/regresar", method = RequestMethod.POST)
	public String regresarNota(Model model, Filtro filtro, String path, HttpServletRequest request) {
		
		List<TblDetalleComprobante> listaDetalle = null;
		try{
			LOGGER.debug("[regresarComprobante] Inicio");
			listaDetalle = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			filtro = (Filtro) request.getSession().getAttribute("filtroSession");
			filtro.setListaDetalle(listaDetalle);	
			LOGGER.debug("[regresarContrato] oPERACION:"+filtro.getStrOperacion());
			path = "operacion/sfs12nota/sfs12not_nuevo";
			
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
	@RequestMapping(value = "/operacion/sfs12notas/productos", method = RequestMethod.POST)
	public String mostrarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12nota/sfs12not_pro_listado";
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
 
	@RequestMapping(value = "/operacion/sfs12notas/productos/q", method = RequestMethod.POST)
	public String listarProducto(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12nota/sfs12not_pro_listado";
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
	@RequestMapping(value = "/operacion/sfs12notas/productos/seleccionar/{id}", method = RequestMethod.GET)
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
				filtro.getDetalleComprobante().setCodigoProducto(producto.getCodigoEmpresa());
				filtro.getDetalleComprobante().setMoneda(producto.getTblCatalogo().getCodigoSunat());
			}
			filtro.setListaDetalle(listaDetalle);	
			model.addAttribute("filtro", filtro);
			
			path = "operacion/sfs12nota/sfs12not_nuevo";

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			producto = null;
			filtro 	= null;
		}
		return path;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operacion/sfs12notas/eliminar/{id}", method = RequestMethod.GET)
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
			OperacionUtil.calculoDetalleComprobante(filtro);
			OperacionUtil.calculoCabeceraComprobante(filtro);
			path = "operacion/sfs12nota/sfs12not_nuevo";

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
	@RequestMapping(value = "/operacion/sfs12notas/pdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
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
	@RequestMapping(value = "/operacion/sfs12notas/ver/actualizar", method = RequestMethod.POST)
	public String actualizarEstadoNota(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/sfs12nota/sfs12not_ver";
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
				bandejaBean.setTipoDocumento(nota.getTipoComprobante());
				bandejaBean.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
				bandejaRptaBean = operacionFacturador.consultarBandejaNota(bandejaBean, filtro);
				if (bandejaRptaBean!=null){
					bandejaFacturadorNota = this.setDatosBandeja(bandejaRptaBean);
					this.preGuardarBandeja(bandejaFacturadorNota, request);
					bandejaFacturadorNota.setTblNota(nota);
					/*try{
						bandejaFacturadorNotaDao.deleteByComprobante(nota.getCodigoNota());
					}catch(Exception e){
						e.printStackTrace();
					}*/
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
					//bandejaBean.setTipoDocumento(nota.getTipoNota());
					bandejaBean.setTipoDocumento(nota.getTipoNota());
					bandejaBean.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
					bandejaRptaBean = operacionFacturador.consultarBandejaNota(bandejaBean,filtro);
					if (bandejaRptaBean!=null){
						this.editarDatosBandeja(bandejaRptaBean, bandejaFacturadorNota);
						this.preEditarBandeja(bandejaFacturadorNota, request);
						bandejaFacturadorNota.setTblNota(nota);
						//bandejaFacturadorNotaDao.save(bandejaFacturadorNota);
						//Eliminando comprobante de la bandeja
						//bandejaFacturadorNotaDao.deleteByComprobante(nota.getCodigoNota());
						//Eliminando comprobante de la bandeja
						/*try{
							bandejaFacturadorNotaDao.deleteByComprobante(nota.getCodigoNota());
						}catch(Exception e){
							e.printStackTrace();
						}*/
						try{
							//Registrando nuevos datos del comprobante en bandeja
							bandejaFacturadorNotaDao.save(bandejaFacturadorNota);
						}catch(Exception e){
							e.printStackTrace();
						}
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
	@RequestMapping(value = "/operacion/sfs12notas/ver/{id}", method = RequestMethod.GET)
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
			
			OperacionUtil.asignarParametros(filtro, mapParametro, request);
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
				//bandejaBean.setTipoDocumento(nota.getTipoNota());
				bandejaBean.setTipoDocumento(nota.getTipoNota());
				bandejaBean.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
				bandejaRptaBean = operacionFacturador.consultarBandejaNota(bandejaBean,filtro);
				if (bandejaRptaBean!=null){
					bandejaFacturadorNota = this.setDatosBandeja(bandejaRptaBean);
					this.preGuardarBandeja(bandejaFacturadorNota, request);
					bandejaFacturadorNota.setTblNota(nota);
					try{
						bandejaFacturadorNotaDao.deleteByComprobante(nota.getCodigoNota());
					}catch(Exception e){
						e.printStackTrace();
					}
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
					//bandejaBean.setTipoDocumento(nota.getTipoNota());
					bandejaBean.setTipoDocumento(nota.getTipoNota());
					bandejaBean.setNumeroDocumento(nota.getSerie()+"-"+nota.getNumero());
					bandejaRptaBean = operacionFacturador.consultarBandejaNota(bandejaBean,filtro);
					if (bandejaRptaBean!=null){
						this.editarDatosBandeja(bandejaRptaBean, bandejaFacturadorNota);
						this.preEditarBandeja(bandejaFacturadorNota, request);
						bandejaFacturadorNota.setTblNota(nota);
						//Eliminando comprobante de la bandeja
						try{
							bandejaFacturadorNotaDao.deleteByComprobante(nota.getCodigoNota());
						}catch(Exception e){
							e.printStackTrace();
						}
						try{
							//Registrando nuevos datos del comprobante en bandeja
							bandejaFacturadorNotaDao.save(bandejaFacturadorNota);
						}catch(Exception e){
							e.printStackTrace();
						}
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
			path = "operacion/sfs12nota/sfs12not_ver";

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
	@RequestMapping(value = "/operacion/sfs12notas/leyenda", method = RequestMethod.POST)
	public String asignarLeyenda(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12nota/sfs12not_nuevo";
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
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "operacion/sfs12notas/all/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarNota(@PathVariable Integer id, HttpServletRequest request, Model model, PageableSG pageable) {
		TblNota entidad						= null;
		List<TblDetalleNota> listaDetEntidad = null;
		TblSunatCabeceraNota sunat						= null;
		List<TblSunatDetalleNota> listaDetSunat			= null;
		List<TblSunatTributoGeneralNota> listaTribSunat	= null;
		List<TblTributoGeneralNota> listaTributo		= null;
		TblLeyendaNota leyenda							= null;
		String path 								= null;
		Filtro filtro 								= null;
		String nombreArchivo						= null;
		List<String> listaArchivo					= null;
		Map<String, TblParametro> mapParametro		= null;
		TblParametro parametro 						= null;
		String ruta									= null;
		try{
			LOGGER.debug("[eliminarNota] Inicio");
			
			//Buscando
			entidad = notaDao.findOne(id);
			//29.04.2018: Estado de la nota vacio y no permite el registro (BUG)
			if (entidad!=null && (entidad.getEstadoOperacion() == null || !entidad.getEstadoOperacion().equals(Constantes.SUNAT_ESTADO_OPERACION_ACEPTADO))){
				listaDetEntidad = detalleNotaDao.listarxNotaDetalle(id);
				leyenda = leyendaNotaDao.getxNota(id);
				sunat = sunatCabeceraNotaDao.findByCodigoDocumento(id);
				if (sunat !=null && sunat.getCodigoCabecera()>0){
					nombreArchivo = sunat.getNombreArchivo();
					listaDetSunat = sunatDetalleNotaDao.findByCodigoCabecera(sunat.getCodigoCabecera());
					listaTribSunat = sunatTributoGeneralNotaDAO.findByCodigoCabecera(sunat.getCodigoCabecera());
					listaTributo = tributoGeneralNotaDAO.listarxNota(id);
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
					for(TblSunatDetalleNota detalle:listaDetSunat){
						sunatDetalleNotaDao.delete(detalle);
					}
				}
				//Borrando
				if (listaTribSunat !=null){
					for(TblSunatTributoGeneralNota detalle: listaTribSunat){
						try{
							sunatTributoGeneralNotaDAO.delete(detalle);
						}catch(Exception e1){
							e1.printStackTrace();
						}

					}
				}
				if(sunat!=null){
					sunatCabeceraNotaDao.delete(sunat);
				}
				if (leyenda!=null){
					leyendaNotaDao.delete(leyenda);
				}
				if (listaDetEntidad !=null){
					for(TblDetalleNota detalle: listaDetEntidad){
						detalleNotaDao.delete(detalle);
					}
				}
				if (listaTributo!=null){
					for(TblTributoGeneralNota detalle:listaTributo){
						try{
							tributoGeneralNotaDAO.delete(detalle);
						}catch(Exception e1){
							e1.printStackTrace();
						}
					}
				}
				notaDao.delete(entidad);
				model.addAttribute("respuesta", "Debe eliminó satisfactoriamente");
			}else{
				model.addAttribute("respuesta", "La Factura fue generado y aceptado por la SUNAT, no se puede eliminar");
			}
			
			path = "operacion/sfs12nota/sfs12not_listado";
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
	
	
	@RequestMapping(value = "/operacion/sfs12notas/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/sfs12nota/sfs12not_listado";
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
	@RequestMapping(value = "/operacion/sfs12notas/mostrar/comprobante", method = RequestMethod.POST)
	public String mostrarComprobanteParaAsignar(Model model, Filtro filtro,  PageableSG pageable, HttpServletRequest request) {
		String path = null;
		path = "operacion/sfs12nota/sfs12not_com_listado";
		try{
			LOGGER.debug("[mostrarComprobanteParaAsignar] Inicio");
			request.getSession().setAttribute("filtroSession", filtro);
			filtro = new Filtro();
			filtro.setRuc(Constantes.SUNAT_RUC_EMISOR); //Se asigna en el logueo
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
	@RequestMapping(value = "/operacion/sfs12notas/mostrar/comprobante/q", method = RequestMethod.POST)
	public String buscarComprobanteParaAsignar(Model model, Filtro filtro,  PageableSG pageable, HttpServletRequest request) {
		String path = null;
		path = "operacion/sfs12nota/sfs12not_com_listado";
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
	@RequestMapping(value = "/operacion/sfs12notas/asignar/seleccion/{id}", method = RequestMethod.GET)
	public String asignarComprobanteANota(@PathVariable Integer id, Model model, String path,  HttpServletRequest request) {
		Filtro filtro = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/sfs12nota/sfs12not_nuevo";
			filtro = (Filtro)request.getSession().getAttribute("filtroSession");
			filtro.setComprobante(comprobanteDao.findOne(id));
			filtro.setListaDetalle(detalleComprobanteDao.listarxComprobantePDF(id));
			for(TblDetalleComprobante detalle: filtro.getListaDetalle()){
				detalle.setPrecioUnitario(UtilSGT.getRoundDecimal(detalle.getPrecioUnitario(), 4));
				detalle.setCantidad(UtilSGT.getRoundDecimal(detalle.getCantidad(), 2));
			}
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
	@RequestMapping(value = "/operacion/sfs12notas/mostrarNota", method = RequestMethod.POST)
	public String mostrarTipoNota(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/sfs12nota/sfs12not_nuevo";
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
	@RequestMapping(value = "/operacion/sfs12notas/nuevo/detalle/actualizar", method = RequestMethod.POST)
	public String actualizarDetalleNota(Model model, Filtro filtro,  String path, HttpServletRequest request) {
		path = "operacion/sfs12nota/sfs12not_nuevo";
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
				OperacionUtil.calculoDetalleComprobante(filtro);
				listaDetComp.set(indice, filtro.getDetalleComprobante());
				filtro.setListaDetalle(listaDetComp);
				OperacionUtil.calculoCabeceraComprobante(filtro);
				filtro.setListaDetalle(listaDetComp);
				
				model.addAttribute("filtro", filtro);
				model.addAttribute("listaDetalleSession", filtro.getListaDetalle());
				request.getSession().setAttribute("listaDetalleSession",filtro.getListaDetalle());
				request.getSession().setAttribute("filtroSession",filtro);
				
			}else{
				model.addAttribute("filtro", filtro);
				path = "operacion/sfs12nota/sfs12not_nuevo_detalle";
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
	/*@RequestMapping(value = "/operacion/sfs12notas/detalle/editar/{id}", method = RequestMethod.GET)
	public String editarDetalleNota(@PathVariable Integer id,  HttpServletRequest request, Model model, PageableSG pageable) {
		String path 								= null;
		Filtro filtro 								= null;
		try{
			LOGGER.debug("[editarDetalleNota] Inicio");
			filtro = (Filtro)request.getSession().getAttribute("filtroSession");
			filtro.setDetalleComprobante(filtro.getListaDetalle().get(id));
			filtro.setIndice(id);
			path = "operacion/sfs12nota/sfs12not_nuevo_detalle";
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
	@RequestMapping(value = "/operacion/sfs12notas/detalle/editar", method = RequestMethod.POST)
	public String editarDetalleNota(Filtro filtro,  HttpServletRequest request, Model model, PageableSG pageable) {
		String path 								= null;
		List<TblDetalleComprobante> listaDetComp 	= null;
		try{
			LOGGER.debug("[editarDetalleNota] Inicio");
			listaDetComp  = (ArrayList<TblDetalleComprobante>)request.getSession().getAttribute("listaDetalleSession");
			filtro.setDetalleComprobante(listaDetComp.get(filtro.getIndice()));
			path = "operacion/sfs12nota/sfs12not_nuevo_detalle";
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