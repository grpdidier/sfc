package com.pe.lima.sg.presentacion.operacion;



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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pe.lima.sg.bean.facturador.EdsConfiguracionBean;
import com.pe.lima.sg.business.inter.IComprobanteBoletaBusiness;
import com.pe.lima.sg.business.inter.IComprobanteFacturaBusiness;
import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.dao.operacion.IComprobanteDAO;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.ISunatCabeceraDAO;
import com.pe.lima.sg.dao.operacion.ISunatDetalleDAO;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblConfiguracion;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblSunatCabecera;
import com.pe.lima.sg.entity.operacion.TblSunatDetalle;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;

/**
 * Clase Bean que se encarga de la administracion de los configuracions
 *
 * 			
 */
@Controller
//@PreAuthorize("hasAuthority('CRUD')")
public class CombustibleAction extends BaseOperacionPresentacion<TblComprobante> {
	
	@Autowired
	private IComprobanteDAO comprobanteDao;
	
	@Autowired
	private ISunatCabeceraDAO sunatCabeceraDao;
	
	@Autowired
	private ISunatDetalleDAO sunatDetalleDao;
	
	@Autowired
	private IComprobanteBoletaBusiness comprobanteBoletaBusiness;

	@Autowired
	private IComprobanteFacturaBusiness comprobanteFacturaBusiness;
	
	@Autowired
	private IDetalleComprobanteDAO detalleComprobanteDao;

	@Override
	public BaseOperacionDAO<TblComprobante, Integer> getDao() {
		return comprobanteDao;
	}	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseOperacionPresentacion.class);
	
	/**
	 * Se encarga de listar todos los configuracions
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/eds/nuevo", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path, HttpServletRequest request) {
		Filtro entidad = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			request.getSession().setAttribute("SessionEdsConfiguracion", new EdsConfiguracionBean());
			entidad = new Filtro();
			
			path = "operacion/eds/eds_c1";
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[traerRegistros] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistros] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		
		return path;
	}

	@RequestMapping(value = "/eds/comprobante/q1", method = RequestMethod.POST)
	public String mostrarComprobante(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/eds/eds_c2";
		EdsConfiguracionBean eds	= null;
		try{
			LOGGER.debug("[mostrarComprobante] Inicio");
			eds = (EdsConfiguracionBean)request.getSession().getAttribute("SessionEdsConfiguracion");
			eds.setStrTipoVenta(entidad.getDato());
			request.getSession().setAttribute("SessionEdsConfiguracion", eds);
			LOGGER.debug("[mostrarComprobante] Tipo:"+entidad.getDato());
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[mostrarComprobante] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarComprobante] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produjo un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[mostrarComprobante] Fin");
		return path;
	}
	
	@RequestMapping(value = "/eds/comprobante/q2", method = RequestMethod.POST)
	public String mostrarProducto(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/eds/eds_c3";
		EdsConfiguracionBean eds	= null;
		try{
			LOGGER.debug("[mostrarProducto] Inicio");
			eds = (EdsConfiguracionBean)request.getSession().getAttribute("SessionEdsConfiguracion");
			eds.setStrTipoComprobante(entidad.getDato());
			request.getSession().setAttribute("SessionEdsConfiguracion", eds);
			LOGGER.debug("[mostrarProducto] Tipo:"+entidad.getDato());
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[mostrarProducto] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarProducto] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produjo un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[mostrarProducto] Fin");
		return path;
	}
	@RequestMapping(value = "/eds/regresar", method = RequestMethod.GET)
	public String regresarComprobante(Model model, String path, HttpServletRequest request) {
		Filtro entidad = null;
		EdsConfiguracionBean eds	= null;
		try{
			LOGGER.debug("[regresarComprobante] Inicio");
			eds = (EdsConfiguracionBean)request.getSession().getAttribute("SessionEdsConfiguracion");
			eds.setStrTipoComprobante("");
			request.getSession().setAttribute("SessionEdsConfiguracion", eds);
			entidad = new Filtro();
			path = "operacion/eds/eds_c2";
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[regresarComprobante] Fin");
		}catch(Exception e){
			LOGGER.debug("[regresarComprobante] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/eds/comprobante/q3", method = RequestMethod.POST)
	public String mostrarDatoComprobante(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/eds/eds_c4b";
		EdsConfiguracionBean eds						= null;
		Map<String, TblProducto> mapProducto			= null;
		TblProducto producto							= null;
		try{
			LOGGER.debug("[mostrarDatoComprobante] Inicio");
			eds = (EdsConfiguracionBean)request.getSession().getAttribute("SessionEdsConfiguracion");
			eds.setStrTipoProducto(entidad.getDato());
			mapProducto = (Map<String, TblProducto>)request.getSession().getAttribute("SessionMapProductoSistema");
			producto = mapProducto.get(entidad.getDato());
			eds.setPrecioUnitario(producto.getPrecio());
			if (eds.getStrTipoComprobante().equals("FACTURA")){
				path = "operacion/eds/eds_c4f";
			}
			request.getSession().setAttribute("SessionEdsConfiguracion", eds);
			LOGGER.debug("[mostrarDatoComprobante] Tipo:"+entidad.getDato());
			entidad.setDato("");
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[mostrarDatoComprobante] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarDatoComprobante] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produjo un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[mostrarDatoComprobante] Fin");
		return path;
	}
	@RequestMapping(value = "/eds/regresar/producto", method = RequestMethod.GET)
	public String regresarProducto(Model model, String path, HttpServletRequest request) {
		Filtro entidad = null;
		EdsConfiguracionBean eds	= null;
		try{
			LOGGER.debug("[regresarProducto] Inicio");
			eds = (EdsConfiguracionBean)request.getSession().getAttribute("SessionEdsConfiguracion");
			eds.setStrTipoProducto("");
			request.getSession().setAttribute("SessionEdsConfiguracion", eds);
			entidad = new Filtro();
			path = "operacion/eds/eds_c3";
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[regresarProducto] Fin");
		}catch(Exception e){
			LOGGER.debug("[regresarProducto] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		
		return path;
	}
	@Override
	public TblComprobante getNuevaEntidad() {
		// TODO Auto-generated method stub
		return null;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/eds/comprobante/q4", method = RequestMethod.POST)
	public String generarComprobante(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/eds/eds_c5";
		EdsConfiguracionBean edsConfiguracionBean	= null;
		TblConfiguracion tblConfiguracion 			= null;
		List<TblConfiguracion> listaConfiguracion 	= null;
		TblComprobante tblComprobante				= null;
		TblDetalleComprobante tblDetalleComprobante	= null;
		List<TblDetalleComprobante> listaDetalle	= null;
		TblComprobante comprobante					= null;
		TblSunatCabecera tblSunatCabecera			= null;
		TblSunatCabecera sunatCabecera				= null;
		TblSunatDetalle sunatDetalle				= null;
		List<TblSunatDetalle> listaSunatDetalle		= new ArrayList<TblSunatDetalle>();
		boolean exitoso 							= false;
		Map<String, TblParametro> mapParametro		= null;
		String nombreLeyendaFile					= null;
		
		try{
			LOGGER.debug("[generarComprobante] Inicio");
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			this.asignarParametros(entidad, mapParametro);
			
			edsConfiguracionBean = (EdsConfiguracionBean)request.getSession().getAttribute("SessionEdsConfiguracion");
			edsConfiguracionBean.setValorVenta(new BigDecimal(entidad.getDato()));
			
			listaConfiguracion = (List<TblConfiguracion>)request.getSession().getAttribute("SessionListConfiguracion");
			if (listaConfiguracion != null && listaConfiguracion.size()>0){
				tblConfiguracion = listaConfiguracion.get(0);
				tblComprobante =  comprobanteBoletaBusiness.asignarDatosBoletaEDS(edsConfiguracionBean, tblConfiguracion);
				tblDetalleComprobante = comprobanteBoletaBusiness.asignarDatosProductoBoletaEDS(edsConfiguracionBean, tblConfiguracion);
				listaDetalle = new ArrayList<TblDetalleComprobante>();
				listaDetalle.add(tblDetalleComprobante);
				//Calculo de los valores
				if (listaDetalle!=null){
					for(TblDetalleComprobante detalleComprobante: listaDetalle){
						comprobanteBoletaBusiness.calculoPrecioFinalProductoBoleta(detalleComprobante);
					}
				}
				
				comprobanteBoletaBusiness.calculoImpuestosBoleta(tblComprobante, listaDetalle);
				//Campos de auditoria
				this.preGuardarListado(tblComprobante, listaDetalle, request);
				//Codigo para luego buscar la boleta
				tblComprobante.setCodigoVerificacion(UUID.randomUUID().toString());
				//Grabacion de la boleta
				comprobanteDao.save(tblComprobante);
				//buscamos la boleta que se grabo
				comprobante = comprobanteDao.obtenerComprobante(tblComprobante.getCodigoVerificacion());
				if (comprobante !=null){
					for(TblDetalleComprobante detalle: listaDetalle){
						detalle.setTblComprobante(comprobante);
						detalleComprobanteDao.save(detalle);
					}
				}
				//Registro en las tablas de la sunat
				tblSunatCabecera = comprobanteBoletaBusiness.obtenerSunatCabecera(comprobante);
				this.preGuardarSunatCabecera(tblSunatCabecera, request);
				sunatCabeceraDao.save(tblSunatCabecera);
				sunatCabecera = sunatCabeceraDao.findByCodigoDocumento(comprobante.getCodigoComprobante());
				if (sunatCabecera!=null){
					for(TblDetalleComprobante detalle: listaDetalle){
						LOGGER.debug("[guardarEntidad] Antes del detalle:"+detalle);
						sunatDetalle = comprobanteBoletaBusiness.obtenerSunatDetalle(sunatCabecera, comprobante, detalle);
						this.preGuardarSunatDetalle(sunatDetalle, request);
						sunatDetalleDao.save(sunatDetalle);
						LOGGER.debug("[guardarEntidad] Despues del registro.............: "+detalle);
						if (sunatDetalle !=null){
							exitoso = true;
							listaSunatDetalle.add(sunatDetalle);
						}else{
							exitoso = false;
							break;
						}
					}
					LOGGER.debug("[guardarEntidad] Inicio de la generacion de los archivos:"+exitoso);
					if (exitoso){
						if (comprobanteBoletaBusiness.generarArchivoCabecera(sunatCabecera, entidad)){
							if (comprobanteBoletaBusiness.generarArchivoDetalle(listaSunatDetalle, entidad)){
								nombreLeyendaFile = Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA;
								if (comprobanteBoletaBusiness.generarArchivoLeyenda(entidad.getLeyendaSunat(),comprobante, nombreLeyendaFile, entidad)){
									//Generar adicional del detalle
									if (comprobanteBoletaBusiness.generarArchivoAdicionalDetalle(listaDetalle, entidad)){
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
						path = "operacion/eds/eds_c5";
						model.addAttribute("filtro", entidad);
					}
				}
			}
			
			
			entidad.setDato("");
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[generarComprobante] Fin");
		}catch(Exception e){
			LOGGER.debug("[generarComprobante] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produjo un Error:"+e.getMessage());
		}finally{
			edsConfiguracionBean	= null;
			tblConfiguracion 		= null;
			listaConfiguracion 		= null;
			tblComprobante			= null;
			tblDetalleComprobante	= null;
			listaDetalle			= null;
			comprobante				= null;
			tblSunatCabecera		= null;
			sunatCabecera			= null;
			sunatDetalle			= null;
			listaSunatDetalle		= new ArrayList<TblSunatDetalle>();
			mapParametro			= null;
			nombreLeyendaFile		= null;
		}
		LOGGER.debug("[generarComprobante] Fin");
		return path;
	}
	/*
	 * Campos de auditoria
	 */
	public void preGuardarListado(TblComprobante tblComprobante, List<TblDetalleComprobante> listaDetalle, HttpServletRequest request){
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
			this.preGuardar(tblComprobante, request);
			for(TblDetalleComprobante detalle: listaDetalle){
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
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}
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
	private void asignarParametros(Filtro entidad, Map<String, TblParametro> mapParametro){
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
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/eds/comprobante/q4/factura", method = RequestMethod.POST)
	public String mostrarDatoRucComprobante(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/eds/eds_c4f1";
		EdsConfiguracionBean edsConfiguracionBean		= null;
		try{
			LOGGER.debug("[mostrarDatoRucComprobante] Inicio");
			edsConfiguracionBean = (EdsConfiguracionBean)request.getSession().getAttribute("SessionEdsConfiguracion");
			edsConfiguracionBean.setValorVenta(new BigDecimal(entidad.getDato()));
			request.getSession().setAttribute("SessionEdsConfiguracion", edsConfiguracionBean);
			
			LOGGER.debug("[mostrarDatoRucComprobante] valor Venta:"+entidad.getDato());
			entidad.setDato("");
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[mostrarDatoRucComprobante] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarDatoRucComprobante] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produjo un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[mostrarDatoRucComprobante] Fin");
		return path;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/eds/comprobante/q4/factura/registro", method = RequestMethod.POST)
	public String generarFactura(Model model, Filtro entidad, String path, HttpServletRequest request) {
		path = "operacion/eds/eds_c5";
		EdsConfiguracionBean edsConfiguracionBean	= null;
		TblConfiguracion tblConfiguracion 			= null;
		List<TblConfiguracion> listaConfiguracion 	= null;
		TblComprobante tblComprobante				= null;
		TblDetalleComprobante tblDetalleComprobante	= null;
		List<TblDetalleComprobante> listaDetalle	= null;
		TblComprobante comprobante					= null;
		TblSunatCabecera tblSunatCabecera			= null;
		TblSunatCabecera sunatCabecera				= null;
		TblSunatDetalle sunatDetalle				= null;
		List<TblSunatDetalle> listaSunatDetalle		= new ArrayList<TblSunatDetalle>();
		boolean exitoso 							= false;
		Map<String, TblParametro> mapParametro		= null;
		String nombreLeyendaFile					= null;
		
		try{
			LOGGER.debug("[generarFactura] Inicio");
			mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
			this.asignarParametros(entidad, mapParametro);
			
			edsConfiguracionBean = (EdsConfiguracionBean)request.getSession().getAttribute("SessionEdsConfiguracion");
			edsConfiguracionBean.setStrRuc(entidad.getRuc());
			edsConfiguracionBean.setStrRazonSocial(entidad.getRazonSocial());
			
			listaConfiguracion = (List<TblConfiguracion>)request.getSession().getAttribute("SessionListConfiguracion");
			if (listaConfiguracion != null && listaConfiguracion.size()>0){
				tblConfiguracion = listaConfiguracion.get(0);
				tblComprobante =  comprobanteFacturaBusiness.asignarDatosFacturaEDS(edsConfiguracionBean, tblConfiguracion);
				tblDetalleComprobante = comprobanteFacturaBusiness.asignarDatosProductoFacturaEDS(edsConfiguracionBean, tblConfiguracion);
				listaDetalle = new ArrayList<TblDetalleComprobante>();
				listaDetalle.add(tblDetalleComprobante);
				//Calculo de los valores
				if (listaDetalle!=null){
					for(TblDetalleComprobante detalleComprobante: listaDetalle){
						comprobanteFacturaBusiness.calculoPrecioFinalProductoFactura(detalleComprobante);
					}
				}
				
				comprobanteFacturaBusiness.calculoImpuestosFactura(tblComprobante, listaDetalle);
				//Campos de auditoria
				this.preGuardarListado(tblComprobante, listaDetalle, request);
				//Codigo para luego buscar la boleta
				tblComprobante.setCodigoVerificacion(UUID.randomUUID().toString());
				//Grabacion de la boleta
				comprobanteDao.save(tblComprobante);
				//buscamos la boleta que se grabo
				comprobante = comprobanteDao.obtenerComprobante(tblComprobante.getCodigoVerificacion());
				if (comprobante !=null){
					for(TblDetalleComprobante detalle: listaDetalle){
						detalle.setTblComprobante(comprobante);
						detalleComprobanteDao.save(detalle);
					}
				}
				//Registro en las tablas de la sunat
				tblSunatCabecera = comprobanteFacturaBusiness.obtenerSunatCabecera(comprobante);
				this.preGuardarSunatCabecera(tblSunatCabecera, request);
				sunatCabeceraDao.save(tblSunatCabecera);
				sunatCabecera = sunatCabeceraDao.findByCodigoDocumento(comprobante.getCodigoComprobante());
				if (sunatCabecera!=null){
					for(TblDetalleComprobante detalle: listaDetalle){
						LOGGER.debug("[generarFactura] Antes del detalle:"+detalle);
						sunatDetalle = comprobanteFacturaBusiness.obtenerSunatDetalle(sunatCabecera, comprobante, detalle);
						this.preGuardarSunatDetalle(sunatDetalle, request);
						sunatDetalleDao.save(sunatDetalle);
						LOGGER.debug("[generarFactura] Despues del registro.............: "+detalle);
						if (sunatDetalle !=null){
							exitoso = true;
							listaSunatDetalle.add(sunatDetalle);
						}else{
							exitoso = false;
							break;
						}
					}
					LOGGER.debug("[generarFactura] Inicio de la generacion de los archivos:"+exitoso);
					if (exitoso){
						if (comprobanteFacturaBusiness.generarArchivoCabecera(sunatCabecera, entidad)){
							if (comprobanteFacturaBusiness.generarArchivoDetalle(listaSunatDetalle, entidad)){
								nombreLeyendaFile = Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_LEYENDA;
								if (comprobanteFacturaBusiness.generarArchivoLeyenda(entidad.getLeyendaSunat(),comprobante, nombreLeyendaFile, entidad)){
									//Generar adicional del detalle
									if (comprobanteFacturaBusiness.generarArchivoAdicionalDetalle(listaDetalle, entidad)){
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
						path = "operacion/eds/eds_c5";
						model.addAttribute("filtro", entidad);
					}
				}
			}
			
			
			entidad.setDato("");
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[generarFactura] Fin");
		}catch(Exception e){
			LOGGER.debug("[generarFactura] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produjo un Error:"+e.getMessage());
		}finally{
			edsConfiguracionBean	= null;
			tblConfiguracion 		= null;
			listaConfiguracion 		= null;
			tblComprobante			= null;
			tblDetalleComprobante	= null;
			listaDetalle			= null;
			comprobante				= null;
			tblSunatCabecera		= null;
			sunatCabecera			= null;
			sunatDetalle			= null;
			listaSunatDetalle		= new ArrayList<TblSunatDetalle>();
			mapParametro			= null;
			nombreLeyendaFile		= null;
		}
		LOGGER.debug("[generarFactura] Fin");
		return path;
	}
}
