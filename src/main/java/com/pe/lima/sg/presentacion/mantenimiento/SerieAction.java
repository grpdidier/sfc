package com.pe.lima.sg.presentacion.mantenimiento;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.dao.mantenimiento.ISerieDAO;
import com.pe.lima.sg.entity.mantenimiento.TblSerie;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.Campo;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.UtilSGT;

/**
 * Clase Bean que se encarga de la administracion de los series
 *
 * 			
 */
@Controller
public class SerieAction extends BaseOperacionPresentacion<TblSerie> {
	
	@Autowired
	private ISerieDAO serieDao;
	
	@Autowired
	private IEmpresaDAO empresaDao;
	

	private static final Logger LOGGER = LoggerFactory.getLogger(SerieAction.class);
	
	
	@Override
	public BaseOperacionDAO<TblSerie,Integer> getDao() {
		return serieDao;
	}
	
	/**
	 * Se encarga de listar todos los series
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/series", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path, HttpServletRequest request) {
		Filtro filtro = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "mantenimiento/serie/ser_listado";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			this.listarSeries(model, filtro,request);
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
	 * Se encarga de buscar la informacion del Serie segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @param serieBean
	 * @return
	 */
	@RequestMapping(value = "/series/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "mantenimiento/serie/ser_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarSeries(model, filtro, request);
			
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Series ***/
	private void listarSeries(Model model, Filtro filtro, HttpServletRequest request){
		List<TblSerie> entidades = new ArrayList<TblSerie>();
		Integer codigoEntidad = null;
		Integer numeroSerie = 0;
		String tipo = "";
		String serie = "";
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			if (filtro.getNumero() != null && !filtro.getNumero().isEmpty()){
				try{
					numeroSerie = Integer.valueOf(filtro.getNumero());
				}catch(Exception e){
					numeroSerie = 0;
				}
			}
			if (filtro.getTipoComprobante() != null && !filtro.getTipoComprobante().isEmpty()){
				tipo = filtro.getTipoComprobante();
			}
			if (filtro.getSerie() != null && !filtro.getSerie().isEmpty()){
				serie = filtro.getSerie();
			}
			entidades = serieDao.listarCriterios(tipo, serie, numeroSerie, codigoEntidad);
			LOGGER.debug("[listarSeries] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de edicion del Serie
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "serie/editar/{id}", method = RequestMethod.GET)
	public String editarSerie(@PathVariable Integer id, Model model) {
		TblSerie entidad 			= null;
		try{
			entidad = serieDao.findOne(id);
			model.addAttribute("entidad", entidad);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "mantenimiento/serie/ser_edicion";
	}
	
	/**
	 * Se encarga de direccionar a la pantalla de edicion del Serie
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "serie/editar/seguridad/{id}/{operacion}", method = RequestMethod.GET)
	public String mostrarSeguridadSerie(@PathVariable Integer id, @PathVariable String operacion,Model model) {
		TblSerie entidad 			= null;
		try{
			//entidad = serieDao.findOne(id);
			entidad = new TblSerie();
			entidad.setCodigoSerie(id);
			entidad.setDescripcion(operacion);
			model.addAttribute("entidad", entidad);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "mantenimiento/serie/ser_seguridad";
	}
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Serie
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "serie/nuevo", method = RequestMethod.GET)
	public String crearSerie(Model model) {
		try{
			LOGGER.debug("[crearSerie] Inicio");
			model.addAttribute("entidad", new TblSerie());
			LOGGER.debug("[crearSerie] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "mantenimiento/serie/ser_nuevo";
	}
	
	@Override
	public void preGuardar(TblSerie entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preGuardar] Inicio" );
			entidad.setAuditoriaCreacion(request);
			entidad.setTblEmpresa(empresaDao.findOne(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ));
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblSerie entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total 	= null;
		Integer codigoEntidad = null;
		try{
			//Validando la existencia del serie
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			total = serieDao.countOneByNombre(entidad.getTipoComprobante(), entidad.getPrefijoSerie(), codigoEntidad);
			if (total > 0){
				exitoso = false;
				model.addAttribute("respuesta", "El Serie existe, debe modificarlo para continuar...");
			}
			if (entidad.getTipoComprobante() == ""){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar el tipo de comprobante");
			}
			if (entidad.getPrefijoSerie() == ""){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la serie");
			}
			if (entidad.getSecuencialSerie() == null || entidad.getSecuencialSerie() <= 0){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el secuencial");
			}
		}catch(Exception e){
			exitoso = false;
		}finally{
			total = null;
		}
		return exitoso;
	}
	
	/**
	 * Se encarga de guardar la informacion del Serie
	 * 
	 * @param serieBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "serie/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, TblSerie entidad, HttpServletRequest request, String path) {
		path = "mantenimiento/serie/ser_listado";
		Integer codigoEntidad		= null;
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			if (this.validarNegocio(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.preGuardar(entidad, request);
				entidad.setNumeroComprobante(UtilSGT.completarCeros(entidad.getSecuencialSerie().toString(), 8));
				boolean exitoso = super.guardar(entidad, model);
				LOGGER.debug("[guardarEntidad] Guardado..." );
				if (exitoso){
					codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
					List<TblSerie> entidades = serieDao.buscarOneByNombre(entidad.getTipoComprobante(), entidad.getPrefijoSerie(), codigoEntidad);
					model.addAttribute("registros", entidades);
					model.addAttribute("filtro", new Filtro());
				}else{
					path = "mantenimiento/serie/ser_nuevo";
					model.addAttribute("entidad", entidad);
					
				}
			}else{
				path = "mantenimiento/serie/ser_nuevo";
				model.addAttribute("entidad", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
		
	}
	
	@Override
	public void preEditar(TblSerie entidad, HttpServletRequest request) {

		try{
			LOGGER.debug("[preEditar] Inicio" );
			entidad.setAuditoriaModificacion(request);
			entidad.setTblEmpresa(empresaDao.findOne(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ));
			LOGGER.debug("[preEditar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "serie/nuevo/editar", method = RequestMethod.POST)
	public String editarEntidad(TblSerie entidad, Model model, HttpServletRequest request) {
		Map<String, Object> campos 	= null;
		String path 				= "mantenimiento/serie/ser_listado";;
		TblSerie entidadEnBd 	= null;
		Integer codigoEntidad		= null;
		try{
			// Se actualizan solo los campos del formulario
			entidad.setNumeroComprobante(UtilSGT.completarCeros(entidad.getSecuencialSerie().toString(), 8));
			entidadEnBd = serieDao.findOne(entidad.getCodigoSerie());
			entidadEnBd.setSecuencialSerie(entidad.getSecuencialSerie());
			entidadEnBd.setTipoComprobante(entidad.getTipoComprobante());
			entidadEnBd.setNumeroComprobante(entidad.getNumeroComprobante());
			entidadEnBd.setDescripcion(entidad.getDescripcion());
			this.preEditar(entidadEnBd, request);
			boolean exitoso = super.guardar(entidadEnBd, model);
			LOGGER.debug("[guardarEntidad] Guardado..." );
			if (exitoso){
				codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
				List<TblSerie> entidades = serieDao.buscarOneByNombre(entidad.getTipoComprobante(), entidad.getPrefijoSerie(), codigoEntidad);
				model.addAttribute("registros", entidades);
				campos = configurarCamposConsulta();
				model.addAttribute("contenido", campos);
				model.addAttribute("filtro", new Filtro());
			}else{
				path = "mantenimiento/serie/ser_edicion";
				model.addAttribute("entidad", entidad);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
		
	}
	
	/**
	 * Se encarga de la eliminacion logica del Serie
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "serie/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarSerie(@PathVariable Integer id, HttpServletRequest request, Model model) {
		TblSerie entidad			= null;
		String path 				= null;
		Map<String, Object> campos 	= null;
		Integer codigoEntidad		= null;
		try{
			LOGGER.debug("[eliminarSerie] Inicio");
			entidad = serieDao.findOne(id);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			this.preEditar(entidad, request);
			serieDao.save(entidad);
			model.addAttribute("respuesta", "Eliminación exitosa");
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			List<TblSerie> entidades = serieDao.listarAllActivos(codigoEntidad);
			LOGGER.debug("[eliminarSerie] entidades:"+entidades);
			model.addAttribute("registros", entidades);
			path = "mantenimiento/serie/ser_listado";
			model.addAttribute("filtro", new Filtro());
			campos = configurarCamposConsulta();
			model.addAttribute("contenido", campos);
			LOGGER.debug("[eliminarSerie] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
			campos		= null;
		}
		return path;
	}
	

	@Override
	public TblSerie getNuevaEntidad() {
		return new TblSerie();
	}
	
	/**
	 * Se encarga de configurar los campos del formulario de consulta
	 * @return
	 */
	private Map<String, Object> configurarCamposConsulta() {
		// El Map debe tener la estructura: String del label , Campo 
		Map<String,Object> campos = new LinkedHashMap<>();
		Campo campo = null;
		campo = new Campo("text", "nombre", false);
		campos.put("Nombre", campo);
		campo = new Campo("text", "dato", false);
		campos.put("Dato", campo);
		return campos;
	}
	
	
}
