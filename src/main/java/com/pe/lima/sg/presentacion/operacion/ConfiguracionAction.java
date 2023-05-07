package com.pe.lima.sg.presentacion.operacion;



import static com.pe.lima.sg.dao.operacion.ConfiguracionSpecifications.conEstado;
import static com.pe.lima.sg.dao.operacion.ConfiguracionSpecifications.conCodigoEmpresa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.dao.operacion.IConfiguracionDAO;
import com.pe.lima.sg.entity.operacion.TblConfiguracion;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.util.Constantes;

/**
 * Clase Bean que se encarga de la administracion de los configuracions
 *
 * 			
 */
@Controller
//@PreAuthorize("hasAuthority('CRUD')")
public class ConfiguracionAction extends BaseOperacionPresentacion<TblConfiguracion> {
	
	@Autowired
	private IConfiguracionDAO configuracionDao;
	
	@Autowired
	private IEmpresaDAO empresaDao;
	
	
	@Override
	public BaseOperacionDAO<TblConfiguracion, Integer> getDao() {
		return configuracionDao;
	}	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseOperacionPresentacion.class);
	
	/**
	 * Se encarga de listar todos los configuracions
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/configuracions", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path, HttpServletRequest request) {
		TblConfiguracion entidad = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "operacion/configuracion/cnf_listado";
			entidad = new TblConfiguracion();
			model.addAttribute("entidad", entidad);
			this.listarConfiguracions(model, entidad, request);
			LOGGER.debug("[traerRegistros] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistros] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		
		return path;
	}
	/**
	 * Se encarga de buscar la informacion del configuracion segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @param configuracionBean
	 * @return
	 */
	@RequestMapping(value = "/configuracions/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, TblConfiguracion entidad, String path, HttpServletRequest request) {
		path = "operacion/configuracion/cnf_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarConfiguracions(model, entidad, request);
			model.addAttribute("entidad", entidad);
			//Se usa al regresar al listado desde el nuevo o editar
			request.getSession().setAttribute("ConfiguracionFiltroSession", entidad);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Configuracions ***/
	private void listarConfiguracions(Model model, TblConfiguracion entidad, HttpServletRequest request){
		List<TblConfiguracion> entidades = new ArrayList<TblConfiguracion>();
		Integer codigoEntidad		= null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			
			Specification<TblConfiguracion> criterio = Specifications.where(conEstado(Constantes.ESTADO_REGISTRO_ACTIVO))
					.and(conCodigoEmpresa(codigoEntidad));
					
			entidades = configuracionDao.findAll(criterio);
			LOGGER.debug("[listarConfiguracion] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de edicion del Configuracion
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "configuracion/editar/{id}", method = RequestMethod.GET)
	public String editarEdificio(@PathVariable Integer id, Model model) {
		TblConfiguracion entidad 			= null;
		try{
			entidad = configuracionDao.findOne(id);
			model.addAttribute("entidad", entidad);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "operacion/configuracion/cnf_edicion";
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Configuracion
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "configuracion/nuevo", method = RequestMethod.GET)
	public String crearEdificio(Model model) {
		TblConfiguracion entidad = null;
		try{
			LOGGER.debug("[crearEdificio] Inicio");
			entidad = new TblConfiguracion();
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[crearEdificio] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "operacion/configuracion/cnf_nuevo";
	}
	/*
	 * Obtiene el usuario de la session
	 */
	private Integer obtenerUsuarioSession(HttpServletRequest request){
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[obtenerUsuarioSession] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
		}catch(Exception e){
			idUsuario = 0;
		}

		LOGGER.debug("[obtenerUsuarioSession] idUsuario:"+idUsuario );
		
		return idUsuario;
	}
	@Override
	public void preGuardar(TblConfiguracion entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preGuardar] Inicio" );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(this.obtenerUsuarioSession(request));
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			entidad.setTblEmpresa(empresaDao.findOne(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ));
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblConfiguracion entidad, HttpServletRequest request){
		boolean exitoso = true;
		try{
			if (entidad.getTipoOperacion()== null || entidad.getTipoOperacion().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar el tipo de operación");
				return exitoso;
			}
			if (entidad.getCodigoDomicilio()== null || entidad.getCodigoDomicilio().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar el domicilio fiscal");
				return exitoso;
			}
			if (entidad.getMoneda()== null || entidad.getMoneda().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar la moneda");
				return exitoso;
			}
			if (entidad.getSerie() == null || entidad.getSerie().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la serie");
				return exitoso;
			}
			if (entidad.getNumeroDocumentoCliente() == null || entidad.getNumeroDocumentoCliente().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el número documento del cliente");
				return exitoso;
			}
			if (entidad.getTipoDocumentoCliente()== null || entidad.getTipoDocumentoCliente().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar el tipo de documento del cliente");
				return exitoso;
			}
			if (entidad.getNombreCliente() == null || entidad.getNombreCliente().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el nombre del cliente");
				return exitoso;
			}
			if (entidad.getDireccion() == null || entidad.getDireccion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la dirección del cliente");
				return exitoso;
			}
			if (entidad.getUnidadMedida()== null || entidad.getUnidadMedida().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar la unidad de medida");
				return exitoso;
			}
			if (entidad.getAfectacionIgv()== null || entidad.getAfectacionIgv().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar la afectación");
				return exitoso;
			}
			
			
		}catch(Exception e){
			exitoso = false;
		}finally{
		}
		return exitoso;
	}
	/**
	 * Se encarga de guardar la informacion del Configuracion
	 * 
	 * @param configuracionBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "configuracion/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, TblConfiguracion entidad, HttpServletRequest request, String path) {
		path = "operacion/configuracion/cnf_listado";
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			if (this.validarNegocio(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.preGuardar(entidad, request);
				
				boolean exitoso = super.guardar(entidad, model);
				LOGGER.debug("[guardarEntidad] Guardado..." );
				if (exitoso){
					this.listarConfiguracions(model, entidad, request);
					model.addAttribute("entidad", new TblConfiguracion());
				}else{
					path = "operacion/configuracion/cnf_nuevo";
					model.addAttribute("entidad", entidad);
				}
				
				
			}else{
				path = "operacion/configuracion/cnf_nuevo";
				model.addAttribute("entidad", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
		
	}
	
	@Override
	public void preEditar(TblConfiguracion entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preEditar] Inicio" );
			entidad.setFechaModificacion(new Date(System.currentTimeMillis()));
			entidad.setIpModificacion(request.getRemoteAddr());
			entidad.setUsuarioModificacion(this.obtenerUsuarioSession(request));
			LOGGER.debug("[preEditar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "configuracion/nuevo/editar", method = RequestMethod.POST)
	public String editarEntidad(TblConfiguracion entidad, Model model, HttpServletRequest request) {
		String path 				= "operacion/configuracion/cnf_listado";;
		TblConfiguracion entidadEnBd 		= null;
		try{
			// Se actualizan solo los campos del formulario
			entidadEnBd = configuracionDao.findOne(entidad.getCodigoConfiguracion());
			
			entidadEnBd.setTipoOperacion((entidad.getTipoOperacion()));
			entidadEnBd.setCodigoDomicilio(entidad.getCodigoDomicilio());
			entidadEnBd.setMoneda(entidad.getMoneda());
			entidadEnBd.setSerie(entidad.getSerie());
			entidadEnBd.setNumeroDocumentoCliente(entidad.getNumeroDocumentoCliente());
			entidadEnBd.setTipoDocumentoCliente(entidad.getTipoDocumentoCliente());
			entidadEnBd.setNombreCliente(entidad.getNombreCliente());
			entidadEnBd.setDireccion(entidad.getDireccion());
			entidadEnBd.setUnidadMedida(entidad.getUnidadMedida());
			entidadEnBd.setAfectacionIgv(entidad.getAfectacionIgv());
			
			this.preEditar(entidadEnBd, request);
			boolean exitoso = super.guardar(entidadEnBd, model);
			LOGGER.debug("[guardarEntidad] Guardado..." );
			if (exitoso){
				this.listarConfiguracions(model, entidadEnBd, request);
				model.addAttribute("entidad", new TblConfiguracion());
			}else{
				path = "operacion/configuracion/cnf_edicion";
				model.addAttribute("entidad", entidad);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidadEnBd 		= null;
		}
		return path;
		
	}
	
	/**
	 * Se encarga de la eliminacion logica del Configuracion
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "configuracion/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarEntidad(@PathVariable Integer id, HttpServletRequest request, Model model) {
		TblConfiguracion entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[eliminarEntidad] Inicio");
			entidad = configuracionDao.findOne(id);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			this.preEditar(entidad, request);
			configuracionDao.save(entidad);
			model.addAttribute("respuesta", "Eliminación exitosa");
			entidad = (TblConfiguracion)request.getSession().getAttribute("ConfiguracionFiltroSession");
			if (entidad ==null){
				entidad = new TblConfiguracion();
			}
			this.listarConfiguracions(model, entidad, request);
			model.addAttribute("entidad", entidad);
			path = "operacion/configuracion/cnf_listado";
			LOGGER.debug("[eliminarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
	

	@Override
	public TblConfiguracion getNuevaEntidad() {
		return new TblConfiguracion();
	}
		
	@RequestMapping(value = "configuracion/regresar", method = RequestMethod.GET)
	public String regresarEntidad(HttpServletRequest request, Model model) {
		TblConfiguracion entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[regresarEntidad] Inicio");
			entidad = (TblConfiguracion)request.getSession().getAttribute("ConfiguracionFiltroSession");
			if (entidad==null){
				entidad = new TblConfiguracion();
			}
			this.listarConfiguracions(model, entidad, request);
			model.addAttribute("entidad", entidad);
			path = "operacion/configuracion/cnf_listado";
			LOGGER.debug("[regresarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
}
