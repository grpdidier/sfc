package com.pe.lima.sg.presentacion.mantenimiento;

import static com.pe.lima.sg.dao.mantenimiento.TransporteSpecifications.conEstadoTransporte;
import static com.pe.lima.sg.dao.mantenimiento.TransporteSpecifications.conMarcaTransporte;
import static com.pe.lima.sg.dao.mantenimiento.TransporteSpecifications.conNombreTransporte;
import static com.pe.lima.sg.dao.mantenimiento.TransporteSpecifications.conPlacaTransporte;
import static com.pe.lima.sg.dao.mantenimiento.TransporteSpecifications.conRucTransporte;

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
import com.pe.lima.sg.dao.mantenimiento.ITransporteDAO;
import com.pe.lima.sg.entity.mantenimiento.TblTransporte;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.util.Constantes;

/**
 * Clase Bean que se encarga de la administracion de los transportes
 *
 * 			
 */
@Controller
//@PreAuthorize("hasAuthority('CRUD')")
public class TransporteAction extends BaseOperacionPresentacion<TblTransporte> {
	
	@Autowired
	private ITransporteDAO transporteDao;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransporteAction.class);
	
	@Override
	public BaseOperacionDAO<TblTransporte, Integer> getDao() {
		return transporteDao;
	}	
	
	/**
	 * Se encarga de listar todos los transportes
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/transportes", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path, HttpServletRequest request) {
		TblTransporte entidad = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "mantenimiento/transporte/tra_listado";
			entidad = new TblTransporte();
			model.addAttribute("entidad", entidad);
			this.listarTransportes(model, entidad, request);
			
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
	 * Se encarga de buscar la informacion del transporte segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @param transporteBean
	 * @return
	 */
	@RequestMapping(value = "/transportes/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, TblTransporte entidad, String path,HttpServletRequest request) {
		path = "mantenimiento/transporte/tra_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarTransportes(model, entidad, request);
			model.addAttribute("entidad", entidad);
			//Se usa al regresar al listado desde el nuevo o editar
			request.getSession().setAttribute("TransporteFiltroSession", entidad);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Transportes ***/
	private void listarTransportes(Model model, TblTransporte entidad, HttpServletRequest request){
		List<TblTransporte> entidades = new ArrayList<TblTransporte>();
		try{
			validaNullEntidad(entidad);
			Specification<TblTransporte> criterio = Specifications.where(conNombreTransporte((entidad.getNombre().toUpperCase())))
					.and(conRucTransporte(entidad.getRuc()))
					.and(conMarcaTransporte( entidad.getMarca().toUpperCase() ))
					.and(conPlacaTransporte( entidad.getPlaca().toUpperCase() ))
					.and(conEstadoTransporte(Constantes.ESTADO_REGISTRO_ACTIVO));
			entidades = transporteDao.findAll(criterio);
			LOGGER.debug("[listarTransporte] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
	
	
	private void validaNullEntidad(TblTransporte entidad) {
		if (entidad.getNombre() == null){
			entidad.setNombre("");
		}
		if (entidad.getPlaca() == null){
			entidad.setPlaca("");
		}
		if (entidad.getRuc() == null){
			entidad.setRuc("");
		}
		if (entidad.getMarca() == null){
			entidad.setMarca("");
		}
		
	}

	/**
	 * Se encarga de direccionar a la pantalla de edicion del Transporte
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "transporte/editar/{id}", method = RequestMethod.GET)
	public String editarEdificio(@PathVariable Integer id, Model model) {
		TblTransporte entidad 			= null;
		try{
			entidad = transporteDao.findOne(id);
			model.addAttribute("entidad", entidad);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "mantenimiento/transporte/tra_edicion";
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Transporte
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "transporte/nuevo", method = RequestMethod.GET)
	public String crearTransporte(Model model) {
		TblTransporte entidad = null;
		try{
			LOGGER.debug("[crearTransporte] Inicio");
			entidad = new TblTransporte();
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[crearTransporte] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "mantenimiento/transporte/tra_nuevo";
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
	public void preGuardar(TblTransporte entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preGuardar] Inicio" );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(this.obtenerUsuarioSession(request));
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			
			//MAYUSCULAS
			entidad.setNombre(entidad.getNombre().toUpperCase());
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblTransporte entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total 	= null;
		try{

			if (entidad.getNombre() == null || entidad.getNombre().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el nombre del transporte");
				return exitoso;
			}
			if (entidad.getRuc() == null || entidad.getRuc().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el RUC");
				return exitoso;
			}
			if (entidad.getMarca() == null || entidad.getMarca().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la Marca");
				return exitoso;
			}
			if (entidad.getPlaca() == null || entidad.getPlaca().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la Placa");
				return exitoso;
			}
			if (entidad.getNumeroCertificadoInscripcion() == null || entidad.getNumeroCertificadoInscripcion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el Nro de Certificado de Inscripcion");
				return exitoso;
			}
			if (entidad.getNumeroLicencia() == null || entidad.getNumeroLicencia().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el Nro de Licencia");
				return exitoso;
			}
			if (entidad.getCodigoTransporte()<=0) {
				//Validando la existencia del transporte
				total = transporteDao.totalDatosTransporte(entidad.getRuc().toUpperCase(), entidad.getMarca().toUpperCase(), entidad.getPlaca().toUpperCase(), entidad.getNumeroCertificadoInscripcion().toUpperCase(), entidad.getNumeroLicencia().toUpperCase());
				if (total > 0){
					exitoso = false;
					model.addAttribute("respuesta", "El nombre del Transporte existe, debe modificarlo para continuar...");
				}
			}
			convertirTodoEnMayusculas(entidad);
			
		}catch(Exception e){
			exitoso = false;
		}finally{
			total = null;
		}
		return exitoso;
	}
	private void convertirTodoEnMayusculas(TblTransporte entidad) {
		entidad.setNombre(entidad.getNombre().toUpperCase());
		entidad.setRuc(entidad.getRuc().toUpperCase());
		entidad.setMarca(entidad.getMarca().toUpperCase());
		entidad.setPlaca(entidad.getPlaca().toUpperCase());
		entidad.setNumeroCertificadoInscripcion(entidad.getNumeroCertificadoInscripcion().toUpperCase());
		entidad.setNumeroLicencia(entidad.getNumeroLicencia().toUpperCase());
		if (entidad.getRemolque()!=null && !entidad.getRemolque().isEmpty()) {
			entidad.setRemolque(entidad.getRemolque().toUpperCase());
		}else {
			entidad.setRemolque("");
		}
	}

	/**
	 * Se encarga de guardar la informacion del Transporte
	 * 
	 * @param transporteBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "transporte/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, TblTransporte entidad, HttpServletRequest request, String path) {
		path = "mantenimiento/transporte/tra_listado";
		//List<TblCatalogo> listaCatalogo = null;
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			if (this.validarNegocio(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.preGuardar(entidad, request);
			
					
				boolean exitoso = super.guardar(entidad, model);
				LOGGER.debug("[guardarEntidad] Guardado..." );
				if (exitoso){
					this.listarTransportes(model, entidad, request);
					model.addAttribute("entidad", new TblTransporte());
				}else{
					path = "mantenimiento/transporte/tra_nuevo";
					model.addAttribute("entidad", entidad);
					}
	
			}else{
				path = "mantenimiento/transporte/tra_nuevo";
				model.addAttribute("entidad", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
		
	}
	
	@Override
	public void preEditar(TblTransporte entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preEditar] Inicio" );
			entidad.setFechaModificacion(new Date(System.currentTimeMillis()));
			entidad.setIpModificacion(request.getRemoteAddr());
			entidad.setUsuarioModificacion(this.obtenerUsuarioSession(request));
			//MAYUSCULAS
			entidad.setNombre(entidad.getNombre().toUpperCase());
			LOGGER.debug("[preEditar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "transporte/nuevo/editar", method = RequestMethod.POST)
	public String editarEntidad(TblTransporte entidad, Model model, HttpServletRequest request) {
		String path 				= "mantenimiento/transporte/tra_listado";;
		TblTransporte entidadEnBd 		= null;
		try{
			if (this.validarNegocio(model, entidad, request)){
				// Se actualizan solo los campos del formulario
				entidadEnBd = transporteDao.findOne(entidad.getCodigoTransporte());
				entidadEnBd.setNombre(entidad.getNombre());
				entidadEnBd.setRuc(entidad.getRuc());
				entidadEnBd.setMarca(entidad.getMarca());
				entidadEnBd.setPlaca(entidad.getPlaca());
				entidadEnBd.setNumeroCertificadoInscripcion(entidad.getNumeroCertificadoInscripcion());
				entidadEnBd.setNumeroLicencia(entidad.getNumeroLicencia());
				entidadEnBd.setNumeroRegistroMtc(entidad.getNumeroRegistroMtc());
				entidadEnBd.setRemolque(entidad.getRemolque());
				this.preEditar(entidadEnBd, request);
				boolean exitoso = super.guardar(entidadEnBd, model);
				LOGGER.debug("[guardarEntidad] Guardado..." );
				if (exitoso){
					this.listarTransportes(model, entidadEnBd, request);
					model.addAttribute("entidad", new TblTransporte());
				}else{
					path = "mantenimiento/transporte/tra_edicion";
					model.addAttribute("entidad", entidad);
				}
			} else {
				path = "mantenimiento/transporte/tra_edicion";
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
	 * Se encarga de la eliminacion logica del Transporte
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "transporte/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarEntidad(@PathVariable Integer id, HttpServletRequest request, Model model) {
		TblTransporte entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[eliminarEntidad] Inicio");
			entidad = transporteDao.findOne(id);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			this.preEditar(entidad, request);
			transporteDao.save(entidad);
			model.addAttribute("respuesta", "Eliminación exitosa");
			entidad = (TblTransporte)request.getSession().getAttribute("TransporteFiltroSession");
			if (entidad ==null){
				entidad = new TblTransporte();
			}
			this.listarTransportes(model, entidad, request);
			model.addAttribute("entidad", entidad);
			path = "mantenimiento/transporte/tra_listado";
			LOGGER.debug("[eliminarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
	

	@Override
	public TblTransporte getNuevaEntidad() {
		return new TblTransporte();
	}
		
	@RequestMapping(value = "transporte/regresar", method = RequestMethod.GET)
	public String regresarEntidad(HttpServletRequest request, Model model) {
		TblTransporte entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[regresarEntidad] Inicio");
			entidad = (TblTransporte)request.getSession().getAttribute("TransporteFiltroSession");
			if (entidad == null){
				entidad = new TblTransporte();
			}
			this.listarTransportes(model, entidad, request);
			model.addAttribute("entidad", entidad);
			path = "mantenimiento/transporte/tra_listado";
			LOGGER.debug("[regresarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
}
