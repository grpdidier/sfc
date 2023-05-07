package com.pe.lima.sg.presentacion.seguridad;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import static com.pe.lima.sg.dao.seguridad.PerfilSpecifications.conEstado;
import static com.pe.lima.sg.dao.seguridad.PerfilSpecifications.conNombre;
import static com.pe.lima.sg.dao.seguridad.PerfilSpecifications.conEstadoPerfil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.dao.seguridad.IOpcionDAO;
import com.pe.lima.sg.dao.seguridad.IPerfilDAO;
import com.pe.lima.sg.dao.seguridad.IPerfilOpcionDAO;
//import com.pe.lima.sg.dao.seguridad.IPerfilOpcionDAO;
import com.pe.lima.sg.entity.seguridad.TblOpcion;
import com.pe.lima.sg.entity.seguridad.TblPerfil;
import com.pe.lima.sg.entity.seguridad.TblPerfilOpcion;
import com.pe.lima.sg.entity.seguridad.TblPerfilOpcionId;
import com.pe.lima.sg.presentacion.BasePresentacion;
import com.pe.lima.sg.presentacion.Campo;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.ListaUtilAction;
import com.pe.lima.sg.presentacion.util.PageWrapper;
import com.pe.lima.sg.presentacion.util.PageableSG;

/**
 * Clase Bean que se encarga de la administracion de los perfiles
 *
 * 			
 */
@Controller
//@PreAuthorize("hasAuthority('CRUD')")
public class PerfilAction extends BasePresentacion<TblPerfil> {
	
	@Autowired
	private IPerfilDAO perfilDao;
	
	@Autowired
	private IOpcionDAO opcionDao;
	
	@Autowired
	private IPerfilOpcionDAO perfilOpcionDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PerfilAction.class);
	
	//@Autowired
	//private RolDao rol;
	
	/*@Autowired
	private IndexAction index;*/
	
	@Autowired
	private ListaUtilAction listaUtil;
	
	private String urlPaginado = "/perfil/paginado/"; 
	
	@SuppressWarnings("rawtypes")
	@Override
	public BaseDAO getDao() {
		return perfilDao;
	}
	
	/**
	 * Se encarga de listar todos los perfiles
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/perfiles", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path,  PageableSG pageable,HttpServletRequest request) {
		Map<String, Object> campos = null;
		Filtro filtro = null;
		
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "seguridad/perfil/per_listado";
			campos = configurarCamposConsulta();
			model.addAttribute("contenido", campos);
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setEstado("");
			this.listarPerfiles(model, filtro, pageable,urlPaginado);
			request.getSession().setAttribute("sessionFiltroCriterio", filtro);
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
			LOGGER.debug("[traerRegistros] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistros] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			campos		= null;
			filtro = null;
		}
		
		return path;
	}
	/**
	 * Se encarga de buscar la informacion del Perfil segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @param perfilBean
	 * @return
	 */
	@RequestMapping(value = "/perfiles/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro,  PageableSG pageable, String path,HttpServletRequest request) {
		Map<String, Object> campos = null;
		path = "seguridad/perfil/per_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarPerfiles(model, filtro, pageable, "/perfil/paginado/");
			request.getSession().setAttribute("sessionFiltroCriterio", filtro);
			campos = configurarCamposConsulta();
			model.addAttribute("contenido", campos);
			model.addAttribute("filtro", filtro);
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			campos		= null;
		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Usuarios ***/
	private void listarPerfiles(Model model, Filtro filtro,  PageableSG pageable, String url){
		//List<TblPerfil> entidades = new ArrayList<TblPerfil>();
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "nombre"));
		try{
			Specification<TblPerfil> criterio = Specifications.where(conNombre(filtro.getNombre().toUpperCase()))
					.and(conEstadoPerfil(filtro.getEstado()))
					.and(conEstado(Constantes.ESTADO_REGISTRO_ACTIVO)); 
			
			pageable.setSort(sort);
			Page<TblPerfil> entidadPage = perfilDao.findAll(criterio, pageable);
			
			//entidades = perfilDao.listarCriterios(filtro.getNombre(), filtro.getEstado());
			PageWrapper<TblPerfil> page = new PageWrapper<TblPerfil>(entidadPage, url, pageable);
			LOGGER.debug("[listarPerfiles] entidades:"+page.getContent());
			
			model.addAttribute("registros", page.getContent());
			model.addAttribute("page", page);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//entidades = null;
		}
	}
	/**
	 * Se encarga de direccionar a la pantalla de configuracion de Opciones por
	 * Perfil
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	/*@RequestMapping(value = "perfil/opcion/{id}", method = RequestMethod.GET)
	public String irConfigurarOpcion(@PathVariable Integer id, Model model, HttpSession session) {
		TblPerfil entidad = perfilDao.findOne(id);
		// Consulta todos las opciones
		List<TblOpcion> opciones = opcion.findAll();
		// Consulta configuracion existente
		Set<TblPerfilOpcion> entidadesExistentes = entidad.getTblPerfilOpcions();
		if (entidadesExistentes != null && !entidadesExistentes.isEmpty()) {
			// Si existen registros marco los bean como seleccionado
			for (TblPerfilOpcion segTblOpcxper : entidadesExistentes) {
				for (TblOpcion segTblOpcion : opciones) {
					if (segTblOpcxper.getId().getCodigoOpcion()==(segTblOpcion.getCodigoOpcion()) ) {
						segTblOpcion.setSeleccionado(true);
						break;
					}
				}
			}
		}
		// Construye el arbol del menu
		List<TblOpcion> entidadesOrdenadas = new ArrayList<TblOpcion>();
		for (Integer idOpcion : index.obtenerArbol(opciones)) {
			for (TblOpcion segTblOpcion : opciones) {
				if (idOpcion.equals(segTblOpcion.getCodigoOpcion())) {
					entidadesOrdenadas.add(segTblOpcion);
					break;
				}
			}
		}
		
		model.addAttribute("perfil", entidad);
		model.addAttribute("opcionesPerfiles", entidadesOrdenadas);
		session.setAttribute("opcionesPerfiles", entidadesOrdenadas);
		Map<String, Object> campos = configurarCamposOpcion(entidad);
		model.addAttribute("contenido", campos);
		return "seguridad/perfil/per_opcion";
	}*/
	
	/**
	 * Se encarga de direccionar a la pantalla de configuracion de Privilegios por
	 * Perfil
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	/*@RequestMapping(value = "perfil/privilegio/{id}", method = RequestMethod.GET)
	public String irConfigurarPrivilegios(@PathVariable BigDecimal id, Model model) {
		TblPerfil entidad = perfilDao.findOne(id);
		// Consulta todos los roles
		List<SegTblRol> roles = rol.findRoles();
		// Consulta configuracion existente
		Set<SegTblRol> rolesExistentes = entidad.getSegTblRols();
		if (rolesExistentes != null && !rolesExistentes.isEmpty()) {
			// Si existen registros marco los bean como seleccionado
			for (SegTblRol segTblRolExistente : rolesExistentes) {
				for (SegTblRol segTblRol : roles) {
					if (segTblRolExistente.getCodigo().equals(segTblRol.getCodigo())) {
						segTblRol.setSeleccionado(true);
						break;
					}
				}
			}
		}
		
		model.addAttribute("perfil", entidad);
		model.addAttribute("roles", roles);
		Map<String, Object> campos = configurarCamposOpcion(entidad);
		model.addAttribute("contenido", campos);
		return "seguridad/perfil/per_privilegio";
	}*/
	
	/**
	 * Se encarga de direccionar a la pantalla de edicion del Perfil
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "perfil/editar/{id}", method = RequestMethod.GET)
	public String editarPerfil(@PathVariable Integer id, Model model) {
		TblPerfil entidad 			= null;
		try{
			entidad = perfilDao.findOne(id);
			model.addAttribute("entidad", entidad);
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "seguridad/perfil/per_edicion";
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Perfil
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "perfil/nuevo", method = RequestMethod.GET)
	public String crearPerfil(Model model) {
		try{
			LOGGER.debug("[crearPerfil] Inicio");
			model.addAttribute("entidad", new TblPerfil());
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
			LOGGER.debug("[crearPerfil] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "seguridad/perfil/per_nuevo";
	}
	
	@Override
	public void preGuardar(TblPerfil entidad, HttpServletRequest request) {
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
			entidad.setNombre(entidad.getNombre().toUpperCase());
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}
		
	}
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblPerfil entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total 	= null;
		try{
			//Validando la existencia del perfil
			total = perfilDao.countOneByLogin(entidad.getNombre());
			if (total > 0){
				exitoso = false;
				model.addAttribute("respuesta", "El Perfil existe, debe modificarlo para continuar...");
			}
			
		}catch(Exception e){
			exitoso = false;
		}finally{
			total = null;
		}
		return exitoso;
	}
	/**
	 * Se encarga de guardar la informacion del Perfil
	 * 
	 * @param perfilBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "perfil/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, TblPerfil entidad, HttpServletRequest request, String path) {
		Map<String, Object> campos 	= null;
		path 						= "seguridad/perfil/per_listado";
		Filtro filtro				= new Filtro();
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			if (this.validarNegocio(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.preGuardar(entidad, request);
				boolean exitoso = super.guardar(entidad, model);
				LOGGER.debug("[guardarEntidad] Guardado..." );
				if (exitoso){
					filtro.setNombre(entidad.getNombre());
					this.listarPerfiles(model, filtro, new PageableSG(), urlPaginado);
					//List<TblPerfil> entidades = perfilDao.buscarOneByNombre(entidad.getNombre());
					//model.addAttribute("registros", entidades);
					listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
					campos = configurarCamposConsulta();
					model.addAttribute("contenido", campos);
					model.addAttribute("filtro", new Filtro());
				}else{
					path = "seguridad/perfil/per_nuevo";
					listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
					model.addAttribute("entidad", entidad);
					
				}
			}else{
				path = "seguridad/perfil/per_nuevo";
				listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
				model.addAttribute("entidad", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			campos 			= null;
			filtro			= null;
		}
		return path;
		
	}
	
	@Override
	public void preEditar(TblPerfil entidad, HttpServletRequest request) {
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			LOGGER.debug("[preEditar] Inicio" );
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			LOGGER.debug("[preGuardar] id_usuario:" +request.getSession().getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			LOGGER.debug("[preEditar] Inicio:"+idUsuario );
			entidad.setFechaModificacion(new Date(System.currentTimeMillis()));
			entidad.setIpModificacion(request.getRemoteAddr());
			entidad.setUsuarioModificacion(idUsuario);
			entidad.setNombre(entidad.getNombre().toUpperCase());
			LOGGER.debug("[preEditar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario 	= null;
		}
	}
	
	@RequestMapping(value = "perfil/nuevo/editar", method = RequestMethod.POST)
	public String editarEntidad(TblPerfil entidad, Model model, HttpServletRequest request) {
		Map<String, Object> campos 	= null;
		String path 				= "seguridad/perfil/per_listado";;
		TblPerfil entidadEnBd 		= null;
		Filtro filtro 				= new Filtro();
		try{
			// Se actualizan solo los campos del formulario
			entidadEnBd = perfilDao.findOne(entidad.getCodigoPerfil());
			entidadEnBd.setEstadoPerfil(entidad.getEstadoPerfil());
			entidadEnBd.setDescripcion(entidad.getDescripcion());
			this.preEditar(entidadEnBd, request);
			boolean exitoso = super.guardar(entidadEnBd, model);
			LOGGER.debug("[guardarEntidad] Guardado..." );
			if (exitoso){
				filtro.setNombre(entidadEnBd.getNombre());
				this.listarPerfiles(model, filtro, new PageableSG(), urlPaginado);
				//List<TblPerfil> entidades = perfilDao.buscarOneByNombre(entidadEnBd.getNombre());
				//model.addAttribute("registros", entidades);
				listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
				campos = configurarCamposConsulta();
				model.addAttribute("contenido", campos);
				model.addAttribute("filtro", new Filtro());
			}else{
				path = "seguridad/perfil/per_edicion";
				listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
				model.addAttribute("entidad", entidad);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			filtro			= null;
			entidadEnBd		= null;
			campos			= null;
		}
		return path;
		
	}
	
	/**
	 * Se encarga de la eliminacion logica del Perfil
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "perfil/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarPerfil(@PathVariable Integer id, HttpServletRequest request, Model model) {
		TblPerfil entidad			= null;
		String path 				= null;
		Map<String, Object> campos 	= null;
		try{
			LOGGER.debug("[eliminarPerfil] Inicio");
			entidad = perfilDao.findOne(id);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			this.preEditar(entidad, request);
			perfilDao.save(entidad);
			model.addAttribute("respuesta", "Eliminación exitosa");
			//List<TblPerfil> entidades = perfilDao.listarAllActivos();
			//LOGGER.debug("[eliminarPerfil] entidades:"+entidades);
			//model.addAttribute("registros", entidades);
			this.listarPerfiles(model, new Filtro(), new PageableSG(), urlPaginado);
			path = "seguridad/perfil/per_listado";
			model.addAttribute("filtro", new Filtro());
			campos = configurarCamposConsulta();
			model.addAttribute("contenido", campos);
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
			LOGGER.debug("[eliminarPerfil] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
			campos		= null;
		}
		return path;
	}
	
	/**
	 * Se encarga de asociar una Opcion al Perfil
	 * 
	 * @param idPerfil
	 * @param idOpcion
	 * @param request
	 * @return
	 */
	/*@SuppressWarnings("unchecked")
	@RequestMapping(value = "perfil/opcion/asociar/{idPerfil}/{idOpcion}", method = RequestMethod.GET)
	public String asociarOpcion(@PathVariable("idPerfil") BigDecimal idPerfil,
			@PathVariable("idOpcion") BigDecimal idOpcion, Model model, HttpServletRequest request) {
		BigDecimal idUsuario = (BigDecimal) request.getSession().getAttribute("id_usuario");
		String ip = request.getRemoteAddr();
		SegTblOpcxper entidad = new SegTblOpcxper();
		entidad.setId(new SegTblOpcxperId());
		entidad.getId().setCodigoPerfil(idPerfil);
		entidad.getId().setCodigoOpcion(idOpcion);		
		entidad.getId().setCodigoSistema(new BigDecimal(1)); // Solo hay un
															// sistema definido
		entidad.setEstado('1'); // Estado activo
		entidad.setPrivilegio("");
		entidad.setFechaCreacion(new Date());
		entidad.setUsuarioCreacion(idUsuario);
		entidad.setIpCreacion(ip);
		SegTblOpcxper entidadExiste = perfilOpcion.findOne(entidad.getId());
		if (entidadExiste != null) {
			entidadExiste.setEstado('1');
			perfilOpcion.save(entidadExiste);
		} else {
			perfilOpcion.save(entidad);	
		}
		
		List<SegTblOpcion> entidadesOrdenadas = (List<SegTblOpcion>) request.getSession().getAttribute("opcionesPerfiles");
		for (SegTblOpcion segTblOpcion : entidadesOrdenadas) {
			if (entidad.getId().getCodigoOpcion().equals(segTblOpcion.getId().getCodigo())) {
				segTblOpcion.setSeleccionado(true);
				break;
			}
		}
		SegTblPerfil entidadPerfil = perfil.findOne(idPerfil);
		model.addAttribute("perfil", entidadPerfil);
		model.addAttribute("opcionesPerfiles", entidadesOrdenadas);
		Map<String, Object> campos = configurarCamposOpcion(entidadPerfil);
		model.addAttribute("contenido", campos);
		return "seguridad/perfil/per_opcion";
	}*/
	
	
	
	/**
	 * Se encarga de asociar un Rol al Perfil
	 * 
	 * @param idPerfil
	 * @param idRol
	 * @return
	 */
	/*@RequestMapping("perfil/rol/asociar/{idPerfil}/{idRol}")
	public String asociarRol(@PathVariable("idPerfil") BigDecimal idPerfil,
			@PathVariable("idRol") String idRol, Model model) {
		SegTblPerfil entidad = perfil.findOne(idPerfil);
		Set<SegTblRol> rolExistentes = entidad.getSegTblRols();
		SegTblRol rolNuevo = rol.findOne(idRol);
		rolExistentes.add(rolNuevo);
		perfil.save(entidad);
		Map<String, Object> campos = configurarCamposConsulta();
		model.addAttribute("contenido", campos);
		return "redirect:/perfil/privilegio/" + idPerfil;
	}
	*/
	/**
	 * Se encarga de desasociar un Rol al Perfil
	 * 
	 * @param idPerfil
	 * @param idRol
	 * @return
	 */
	/*@RequestMapping("perfil/rol/desasociar/{idPerfil}/{idRol}")
	public String desasociarRol(@PathVariable("idPerfil") BigDecimal idPerfil,
			@PathVariable("idRol") String idRol, Model model) {
		SegTblPerfil entidad = perfil.findOne(idPerfil);
		Set<SegTblRol> rolExistentes = entidad.getSegTblRols();
		SegTblRol rolEliminar = rol.findOne(idRol);
		rolExistentes.remove(rolEliminar);
		perfil.save(entidad);
		Map<String, Object> campos = configurarCamposConsulta();
		model.addAttribute("contenido", campos);
		return "redirect:/perfil/privilegio/" + idPerfil;
	}*/

	@Override
	public TblPerfil getNuevaEntidad() {
		return new TblPerfil();
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
		campo = new Campo("combo_estado", "estado", false);
		campos.put("Estado Perfil", campo);
		return campos;
	}
	
	/**
	 * Se encarga de configurar los campos del formulario de nuevo
	 * @param perfil 
	 * @return
	 */
	/*private Map<String, Object> configurarCamposOpcion(TblPerfil perfil) {
		// El Map debe tener la estructura: String del label , Campo 
		Map<String,Object> campos = new LinkedHashMap<>();
		Campo campo = null;
		campo = new Campo("label", perfil.getNombre());
		campos.put("Nombre del Perfil", campo);
		campo = new Campo("imagen_estado_registro", "estado");
		campos.put("Estado Registro", campo);
		campo = new Campo("label", perfil.getDescripcion());
		campos.put("Descripción", campo);
		return campos;
	}*/
	/**
	 * Se encarga de direccionar a la pantalla de Opcion
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "perfil/opcion/{id}", method = RequestMethod.GET)
	public String editarOpcion(@PathVariable Integer id, Model model, HttpSession session) {
		TblPerfil entidad 			= null;
		List<TblOpcion> listaOpciones 	= null;
		List<TblOpcion> listaOpcionesNueva 	= null;
		Integer intPos = new Integer(0);
		TblOpcion opc	= null;
		try{
			LOGGER.debug("[editarOpcion] Inicio");
			entidad = perfilDao.findOne(id);
			model.addAttribute("entidad", entidad);
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
			listaOpciones = opcionDao.listarAllActivos();
			opc = this.getOpcionRaiz();
			listaOpcionesNueva = new ArrayList<TblOpcion>();
			
			// Consulta configuracion existente
			Set<TblPerfilOpcion> entidadesExistentes = entidad.getTblPerfilOpcions();
			if (entidadesExistentes != null && !entidadesExistentes.isEmpty()) {
				// Si existen registros marco los bean como seleccionado
				for (TblPerfilOpcion tblPerfilOpcion : entidadesExistentes) {
					for (TblOpcion tblOpcion : listaOpciones) {
						if (tblPerfilOpcion.getId().getCodigoOpcion() == tblOpcion.getCodigoOpcion()) {
							tblOpcion.setSeleccionado(true);
							break;
						}
					}
				}
			}
			
			
			this.orderOpcion(listaOpciones, intPos, listaOpcionesNueva, opc.getCodigoOpcion());
			LOGGER.debug("[editarOpcion] listaOpcionesNueva:"+listaOpcionesNueva.size());
			model.addAttribute("registros", listaOpcionesNueva);
			session.setAttribute("perfilesOpciones", listaOpcionesNueva);
			LOGGER.debug("[editarOpcion] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "seguridad/perfil/per_opcion";
	}
	/**
	 * Genera la lista de opciones ordenada por niveles y modulo
	 * 
	 * @param listado
	 * @param intPos
	 * @param listaOpciones
	 * @param intModulo
	 */
	private void orderOpcion(List<TblOpcion> listado, Integer intPos, List<TblOpcion> listaOpciones, Integer intModulo){
		TblOpcion opc	= null;
		try{
			LOGGER.debug("[orderOpcion] Inicio : intPos"+intPos);
			while (intPos < listado.size()){
				opc = listado.get(intPos);
				if (opc.getModulo().compareTo(intModulo)==0){
					listaOpciones.add(opc);
					intPos++;
					LOGGER.debug("[orderOpcion] Llamando a : "+ intPos);
					this.orderOpcion(listado, intPos, listaOpciones, opc.getCodigoOpcion());
				}else{
					intPos++;
				}
			}
			LOGGER.debug("[orderOpcion] Fin : intPos"+intPos);
		}catch(Exception e){
			
		}finally{
			opc	= null;
		}
	}
	/**
	 * Genera la opcion raiz del sistema
	 * 
	 * @return TblOpcion
	 */
	private TblOpcion getOpcionRaiz(){
		TblOpcion opcion = new TblOpcion();
		opcion.setNombre("Sistema de Gestion de Tiendas");
		opcion.setCodigoOpcion(1000);
		opcion.setNivel(0);
		opcion.setModulo(0);
		return opcion;
	}

	/**
	 * Se encarga de asociar una Opcion al Perfil
	 * 
	 * @param idPerfil
	 * @param idOpcion
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "perfil/opcion/asociar/{idPerfil}/{idOpcion}", method = RequestMethod.GET)
	public String asociarOpcion(@PathVariable("idPerfil") Integer idPerfil,
								@PathVariable("idOpcion") Integer idOpcion, Model model, HttpSession session, HttpServletRequest request) {
		Integer idUsuario = null;
		String ip = null;
		TblPerfilOpcion entidad = null;
		List<TblOpcion> entidadesOrdenadas = null;
		TblPerfil entidadPerfil = null;
		String strIdUsuario = null;
		try{
			LOGGER.debug("[asociarOpcion] Inicio");
			strIdUsuario = (String)session.getAttribute("id_usuario");
			LOGGER.debug("[asociarOpcion] id_usuario:" +session.getAttribute("id_usuario"));
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			
			//idUsuario = (Integer) request.getSession().getAttribute("id_usuario");
			ip = request.getRemoteAddr();
			entidad = new TblPerfilOpcion();
			entidad.setId(new TblPerfilOpcionId());
			entidad.getId().setCodigoPerfil(idPerfil);
			entidad.getId().setCodigoOpcion(idOpcion);	
			entidad.setPrivilegio("");
			entidad.setFechaCreacion(new Date());
			entidad.setUsuarioCreacion(idUsuario);
			entidad.setIpCreacion(ip);
			TblPerfilOpcion entidadExiste = perfilOpcionDao.findOne(entidad.getId());
			if (entidadExiste != null) {
				perfilOpcionDao.save(entidadExiste);
			} else {
				perfilOpcionDao.save(entidad);	
			}
			
			entidadesOrdenadas = (List<TblOpcion>) request.getSession().getAttribute("perfilesOpciones");
			for (TblOpcion tblOpcion : entidadesOrdenadas) {
				if (entidad.getId().getCodigoOpcion() == tblOpcion.getCodigoOpcion()) {
					tblOpcion.setSeleccionado(true);
					break;
				}
			}
			entidadPerfil = perfilDao.findOne(idPerfil);
			model.addAttribute("entidad", entidadPerfil);
			//model.addAttribute("perfilesOpciones", entidadesOrdenadas);
			model.addAttribute("registros", entidadesOrdenadas);
			session.setAttribute("perfilesOpciones", entidadesOrdenadas);
			LOGGER.debug("[asociarOpcion] Fin");
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			idUsuario 			= null;
			ip 					= null;
			entidad 			= null;
			entidadesOrdenadas 	= null;
			entidadPerfil 		= null;
		}
		
		
		return "seguridad/perfil/per_opcion";
	}
	/**
	 * Se encarga de desasociar una Opcion al Perfil
	 * 
	 * @param idPerfil
	 * @param idOpcion
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "perfil/opcion/desasociar/{idPerfil}/{idOpcion}", method = RequestMethod.GET)
	public String desasociarOpcion(@PathVariable("idPerfil") Integer idPerfil,
			@PathVariable("idOpcion") Integer idOpcion, Model model, HttpSession session, HttpServletRequest request) {		
		TblPerfilOpcion entidad = null;
		List<TblOpcion> entidadesOrdenadas = null;
		TblPerfil entidadPerfil = null;
		try{
			LOGGER.debug("[desasociarOpcion] Inicio");
			entidad = new TblPerfilOpcion();
			entidad.setId(new TblPerfilOpcionId());
			entidad.getId().setCodigoPerfil(idPerfil);
			entidad.getId().setCodigoOpcion(idOpcion);
			
			entidad = perfilOpcionDao.findOne(entidad.getId());
			perfilOpcionDao.delete(entidad);
			
			entidadesOrdenadas = (List<TblOpcion>) request.getSession().getAttribute("perfilesOpciones");
			for (TblOpcion tblOpcion : entidadesOrdenadas) {
				if (entidad.getId().getCodigoOpcion() == tblOpcion.getCodigoOpcion()) {
					tblOpcion.setSeleccionado(false);
					break;
				}
			}
			entidadPerfil = perfilDao.findOne(idPerfil);
			model.addAttribute("entidad", entidadPerfil);
			//model.addAttribute("perfilesOpciones", entidadesOrdenadas);
			model.addAttribute("registros", entidadesOrdenadas);
			session.setAttribute("perfilesOpciones", entidadesOrdenadas);
			LOGGER.debug("[desasociarOpcion] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 			= null;
			entidadesOrdenadas 	= null;
			entidadPerfil 		= null;
		}
		
		
		return "seguridad/perfil/per_opcion";
	}
	
	@RequestMapping(value = "/perfil/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarPerfil(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Map<String, Object> campos = null;
		Filtro filtro = null;
		String path = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "seguridad/perfil/per_listado";
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
			campos = configurarCamposConsulta();
			model.addAttribute("contenido", campos);
			filtro = (Filtro)request.getSession().getAttribute("sessionFiltroCriterio");
			model.addAttribute("filtro", filtro);
			filtro.setEstadoUsuario("-1");
			this.listarPerfiles(model, filtro, pageable,"/perfil/paginado/");
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
			LOGGER.debug("[traerRegistros] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistros] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			campos		= null;
			filtro = null;
		}
		return path;
	}
}
