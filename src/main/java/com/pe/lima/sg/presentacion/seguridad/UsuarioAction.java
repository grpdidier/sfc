package com.pe.lima.sg.presentacion.seguridad;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.dao.seguridad.IPerfilDAO;
import com.pe.lima.sg.dao.seguridad.IUsuarioDAO;
import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;
import com.pe.lima.sg.entity.seguridad.TblPerfil;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.BasePresentacion;
import com.pe.lima.sg.presentacion.Campo;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.ListaUtilAction;
import com.pe.lima.sg.presentacion.util.UtilSGT;


@Controller
//@PreAuthorize("hasAuthority('CRUD')")
public class UsuarioAction extends BasePresentacion<TblUsuario> {
	
	@Autowired
	private IUsuarioDAO usuarioDao;
	
	@Autowired
	private IPerfilDAO perfilDao;
	
	@Autowired
	private IEmpresaDAO empresaDao;
	
	@Autowired
	private ListaUtilAction listaUtil;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioAction.class);
	
	/*@Autowired
	private CorreoService notificar;*/
	
	@SuppressWarnings("rawtypes")
	@Override
	public BaseDAO getDao() {
		return usuarioDao;
	}
	
	/**
	 * Se encarga de listar todos los usuarios
	 * 
	 * @param model
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "/usuarios", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path) {
		Map<String, Object> campos = null;
		Filtro filtro = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "seguridad/usuario/usu_listado";
			campos = configurarCamposConsulta();
			model.addAttribute("contenido", campos);
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setEstadoUsuario("-1");
			this.listarUsuarios(model, filtro);
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
	 * Se encarga de buscar la informacion del Usuario segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @param filtro
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "/usuarios/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro, String path,HttpServletRequest request) {
		Map<String, Object> campos = null;
		path = "seguridad/usuario/usu_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarUsuarios(model, filtro);
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
	private void listarUsuarios(Model model, Filtro filtro){
		List<TblUsuario> entidades = new ArrayList<TblUsuario>();
		try{
			if (filtro.getEstadoUsuario() !=null && filtro.getEstadoUsuario().equals("")){
				entidades = usuarioDao.listarCriterios(filtro.getLogin(), filtro.getNombre(), "S");
			}else{
				entidades = usuarioDao.listarCriterios(filtro.getLogin(), filtro.getNombre(), filtro.getEstadoUsuario());
			}
			
			LOGGER.debug("[listarUsuarios] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Usuario
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "usuario/nuevo", method = RequestMethod.GET)
	public String irNuevo(Model model) {
		try{
			LOGGER.debug("[irNuevo] Inicio");
			model.addAttribute("entidad", new TblUsuario());
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
			listaUtil.cargarDatosModel(model, Constantes.MAP_TIPO_CADUCIDAD);
			LOGGER.debug("[irNuevo] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "seguridad/usuario/usu_nuevo";
	}
	

	
	/**
	 * Se encarga de direccionar a la pantalla de seleccion del perfil
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "usuario/perfil/{id}", method = RequestMethod.GET)
	public String perfilUsuario(@PathVariable Integer id, Model model) {
		TblUsuario entidad 			= null;
		List<TblPerfil> perfiles	= null;
		try{
			LOGGER.debug("[perfilUsuario] Inicio");
			entidad = usuarioDao.findOne(id);
			perfiles = new ArrayList<TblPerfil>();
			if (entidad.getTblPerfil() == null) {
				perfiles = perfilDao.listarAllActivos();
			} else {
				perfiles.add(entidad.getTblPerfil());
			}
			model.addAttribute("usuario", entidad);
			model.addAttribute("perfiles", perfiles);
			
			LOGGER.debug("[perfilUsuario] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad		= null;
			perfiles	= null;
		}
		
		return "seguridad/usuario/usu_perfil";
	}
	
	/**
	 * Se encarga de asociar el Perfil al Usuario
	 * 
	 * @param usuario
	 * @param perfil
	 * @param segTblUsuario
	 * @param model
	 * @return
	 */
	@RequestMapping("usuario/perfil/asociar/{usuario}/{perfil}")
	public String asociarPerfilUsuario(@PathVariable Integer usuario, @PathVariable Integer perfil,	TblUsuario segTblUsuario, Model model) {
		List<TblPerfil> perfilesNuevos 	= null;
		TblUsuario miUsuario 			= null;
		List<TblPerfil> perfiles 		= null;
		try{
			LOGGER.debug("[asociarPerfilUsuario] Inicio");
			perfilesNuevos = new ArrayList<TblPerfil>();
			// Búsqueda de usuario
			miUsuario = usuarioDao.findOne(usuario);
			// Si el usuario tiene un perfil asociado se elimina esta relacion
			if (miUsuario.getTblPerfil() != null) {
				miUsuario.setTblPerfil(null);
				miUsuario = usuarioDao.save(miUsuario);
				perfiles = perfilDao.findAll();
				model.addAttribute("usuario", miUsuario);
				model.addAttribute("perfiles", perfiles);
				Map<String, Object> campos = configurarCamposPerfil(miUsuario);
				model.addAttribute("contenido", campos);
				LOGGER.debug("[asociarPerfilUsuario] Fin Middle");
				return "seguridad/usuario/usu_perfil";
			}
			
			// Búsqueda de perfil
			TblPerfil miPerfil = perfilDao.findOne(perfil);
			
			miUsuario.setTblPerfil(miPerfil);
			model.addAttribute("usuario", miUsuario);
			usuarioDao.save(miUsuario);
			
			// Si se asocia un nuevo perfil solo se le muestra en pantalla el perfil
			// asociado
			perfilesNuevos.add(miPerfil);
			model.addAttribute("perfiles", perfilesNuevos);
			Map<String, Object> campos = configurarCamposPerfil(miUsuario);
			model.addAttribute("contenido", campos);
			LOGGER.debug("[asociarPerfilUsuario] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			perfilesNuevos	= null;
			miUsuario		= null;
			perfiles		= null;
		}
		
		return "seguridad/usuario/usu_perfil";
	}

	/**
	 * Se encarga de eliminar un usuario
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("usuario/eliminar/{id}")
	public String eliminarUsuario(@PathVariable Integer id, HttpServletRequest request, Model model) {
		TblUsuario entidad			= null;
		String path 				= null;
		Map<String, Object> campos 	= null;
		try{
			LOGGER.debug("[eliminarUsuario] Inicio");
			entidad = usuarioDao.findOne(id);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			this.preEditar(entidad, request);
			usuarioDao.save(entidad);
			model.addAttribute("respuesta", "Eliminación exitosa");
			List<TblUsuario> entidades = usuarioDao.listarAllActivos();
			LOGGER.debug("[eliminarUsuario] entidades:"+entidades);
			model.addAttribute("registros", entidades);
			path = "seguridad/usuario/usu_listado";
			model.addAttribute("filtro", new Filtro());
			campos = configurarCamposConsulta();
			model.addAttribute("contenido", campos);
			LOGGER.debug("[eliminarUsuario] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
			campos		= null;
		}
		
		return path;
	}
	
	@Override
	public void preGuardar(TblUsuario entidad, HttpServletRequest request) {
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
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblUsuario entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total 	= null;
		try{
			//Validando la existencia del Login
			total = usuarioDao.countOneByLogin(entidad.getLogin());
			if (total > 0){
				exitoso = false;
				model.addAttribute("respuesta", "El login existe, debe modificarlo para continuar...");
			}
			if(entidad.getTblEmpresa().getRuc() == null || entidad.getTblEmpresa().getRuc().equals("") || entidad.getTblEmpresa().getRuc().equals("-1")){
				model.addAttribute("respuesta", "Debe seleccionar la empresa");
				exitoso = false;
			}
			
		}catch(Exception e){
			exitoso = false;
		}finally{
			total = null;
		}
		return exitoso;
	}
	
	@RequestMapping(value = "usuario/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, TblUsuario entidad, HttpServletRequest request, String path) {
		Map<String, Object> campos 	= null;
		path = "seguridad/usuario/usu_listado";
		TblEmpresa empresa = null;
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			if (this.validarNegocio(model, entidad, request)){
				empresa = empresaDao.getEmpresaxRUC(entidad.getTblEmpresa().getRuc());
				entidad.setTblEmpresa(empresa);
				this.asignarNegocio(entidad, Constantes.OPERACION_NUEVO);
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.preGuardar(entidad, request);
				boolean exitoso = super.guardar(entidad, model);
				LOGGER.debug("[guardarEntidad] Guardado..." );
				if (exitoso){
					//List<TblUsuario> entidades = usuarioDao.listarAllActivos();
					List<TblUsuario> entidades = usuarioDao.buscarOneByLogin(entidad.getLogin());
					
					model.addAttribute("registros", entidades);
					listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
					campos = configurarCamposConsulta();
					model.addAttribute("contenido", campos);
					model.addAttribute("filtro", new Filtro());
				}else{
					path = "seguridad/usuario/usu_nuevo";
					listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
					listaUtil.cargarDatosModel(model, Constantes.MAP_TIPO_CADUCIDAD);
					model.addAttribute("entidad", entidad);
					
				}
			}else{
				path = "seguridad/usuario/usu_nuevo";
				listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
				listaUtil.cargarDatosModel(model, Constantes.MAP_TIPO_CADUCIDAD);
				model.addAttribute("entidad", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			campos 			= null;
		}
		return path;
		
	}
	
	/**
	 * Asignamos los campos segun la logica del negocio
	 * 
	 */
	private void asignarNegocio(TblUsuario entidad, String strOperacion){
		String claveGenerada 		= null;
		try{
			LOGGER.debug("[asignarNegocio] Inicio" );
			/**Siempre se realiza esta opearción*/
			//Caducidad
			if (entidad.getTipoCaducidad().equals(Constantes.TIPO_CADUCIDAD_INDEFINIDO)){
				entidad.setFechaCaducidad(null);
				entidad.setDiasCaducidad(null);
			}else if (entidad.getTipoCaducidad().equals(Constantes.TIPO_CADUCIDAD_CADUCA_30)){
				entidad.setFechaCaducidad(UtilSGT.addDays( new Date(), Constantes.NUMERO_DIAS_30));
				LOGGER.debug("[asignarNegocio] setFechaCaducidad:"+entidad.getFechaCaducidad());
				entidad.setDiasCaducidad(Constantes.NUMERO_DIAS_30);
			}else{
				entidad.setFechaCaducidad(null);
				entidad.setDiasCaducidad(null);
			}
			/**Se necesita conocer la operación para saber que tarea se realiza*/
			//Clave
			if (strOperacion.equals(Constantes.OPERACION_NUEVO)){
				claveGenerada = generarClave(8);
				entidad.setClave(new BCryptPasswordEncoder().encode(claveGenerada));
				entidad.setObservacion(entidad.getObservacion() + ":" +claveGenerada);
			}else{
				claveGenerada = entidad.getCambioClave();
				//entidad.setObservacion(entidad.getObservacion() + ":" +claveGenerada);
				entidad.setClave(new BCryptPasswordEncoder().encode(claveGenerada));
				
			}
			LOGGER.debug("[asignarNegocio] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			claveGenerada 	= null;
		}
	}
	
	/**
	 * Se encarga de notificar al usuario nuevo para el cambio de clave.
	 * 
	 * @param usuarioBean
	 * @param entidad
	 * @param claveGenerada
	 */
	/*private boolean notificarUsuarioNuevo(SegTblUsuario entidad, String claveGenerada) {
		boolean exitoso = true;
		String operadorContenido = "|";
		String operadorToken = ":";
		//Genera el token de validacion
		StringBuilder token = new StringBuilder();
		token.append(entidad.getCodigo());
		token.append(operadorToken);
		String hash = new BCryptPasswordEncoder().encode(entidad.getLogin()+entidad.getCorreo());
		token.append(hash);
		token.append(operadorToken);
		token.append(new Date().getTime());
		String asunto = "SICOM GNCV - CREACION USUARIO";
		String url = "http://localhost:8080/restablecer/cambio?token=";
		String contenido = entidad.getNombre() + operadorContenido + entidad.getLogin() + operadorContenido + claveGenerada + operadorContenido + url;
		//Enviar notificacion al usuario nuevo de sus credenciales
		CorreoDTO msg = new CorreoDTO(null, entidad.getCorreo(), asunto, contenido, token.toString(), CorreoDTO.CORREO_USUARIO_NUEVO);
		try {
			notificar.enviar(msg);
		} catch (Exception e) {
			System.out.println("ERROR - ENVIANDO NOTIFICACION");
			e.printStackTrace();
			exitoso = false;
		}
		return exitoso;
	}*/

	/**
	 * Genera un nuevo string a partir de una longitud
	 * 
	 * @param longitud del string generado
	 * @return String
	 */
	private String generarClave(int longitud) {
		String AB 			= null;
		SecureRandom rnd 	= null;
		StringBuilder sb 	= null;
		try{
			LOGGER.debug("[generarClave] Inicio" );
			AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			rnd = new SecureRandom();
			sb = new StringBuilder(longitud);
			for (int i = 0; i < longitud; i++)
				sb.append(AB.charAt(rnd.nextInt(AB.length())));
			LOGGER.debug("[generarClave] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			AB 		= null;
			rnd 	= null;
		}
		
		return sb.toString();
	}
	
	/**
	 * Se encarga de direccionar a la pantalla de edicion del Usuario
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "usuario/editar/{id}", method = RequestMethod.GET)
	public String editarUsuario(@PathVariable Integer id, Model model) {
		TblUsuario entidad 			= null;
		//Map<String, Object> campos 	= null;
		try{
			//entidad = usuarioDao.findOne(id);
			entidad = usuarioDao.findOneByCodigo(id);
			//Valida Empresa
			if (entidad.getTblEmpresa()==null){
				entidad.setTblEmpresa(new TblEmpresa());
				entidad.getTblEmpresa().setRuc("-1");
			}
			model.addAttribute("entidad", entidad);
			//campos = configurarCamposEdicion(entidad);
			//model.addAttribute("contenido", campos);
			listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
			listaUtil.cargarDatosModel(model, Constantes.MAP_TIPO_CADUCIDAD);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
			//campos	= null;
		}
		return "seguridad/usuario/usu_edicion";
	}
	
	@Override
	public void preEditar(TblUsuario entidad, HttpServletRequest request) {
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
			LOGGER.debug("[preEditar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			idUsuario 		= null;
			strIdUsuario 	= null;
		}
		
	}
	
	@RequestMapping(value = "usuario/nuevo/editar", method = RequestMethod.POST)
	public String editarEntidad(TblUsuario entidad, Model model, HttpServletRequest request) {
		Map<String, Object> campos 	= null;
		String path 				= "seguridad/usuario/usu_listado";;
		TblUsuario entidadEnBd 		= null;
		TblEmpresa empresaEnBd		= null;
		try{
			// Se actualizan solo los campos del formulario
			//entidadEnBd = usuarioDao.findOne(entidad.getCodigoUsuario());
			entidadEnBd = usuarioDao.findOneByCodigo(entidad.getCodigoUsuario());
			entidadEnBd.setNombre(entidad.getNombre());
			entidadEnBd.setEstadoUsuario(entidad.getEstadoUsuario());
			entidadEnBd.setEmail(entidad.getEmail());
			entidadEnBd.setObservacion(entidad.getObservacion());
			entidadEnBd.setTipoCaducidad(entidad.getTipoCaducidad());
			//Empresa
			empresaEnBd = empresaDao.getEmpresaxRUC(entidad.getTblEmpresa().getRuc());
			entidadEnBd.setTblEmpresa(empresaEnBd);
			
			//nueva clave
			entidadEnBd.setCambioClave(entidad.getCambioClave());
			this.asignarNegocio(entidadEnBd,Constantes.OPERACION_EDITAR);
			this.preEditar(entidadEnBd, request);
			boolean exitoso = super.guardar(entidadEnBd, model);
			LOGGER.debug("[guardarEntidad] Guardado..." );
			if (exitoso){
				//List<TblUsuario> entidades = usuarioDao.listarAllActivos();
				List<TblUsuario> entidades = usuarioDao.buscarOneByLogin(entidadEnBd.getLogin());
				
				model.addAttribute("registros", entidades);
				listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
				campos = configurarCamposConsulta();
				model.addAttribute("contenido", campos);
				model.addAttribute("filtro", new Filtro());
			}else{
				path = "seguridad/usuario/usu_edicion";
				listaUtil.cargarDatosModel(model, Constantes.MAP_ESTADO_USUARIO);
				listaUtil.cargarDatosModel(model, Constantes.MAP_TIPO_CADUCIDAD);
				model.addAttribute("entidad", entidad);
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
	}

	
	
	/*public static String completarConCeros(long numero, long precision) {
		String salida = numero + "";
		if (precision <= salida.length()) {
			return salida;
		}
		for (int i = 0; i <= (precision - salida.length()); i++) {
			salida = "0" + salida;
		}
		return salida;
	}*/

	@Override
	public TblUsuario getNuevaEntidad() {
		return new TblUsuario();
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
		campo = new Campo("text", "login", false);
		campos.put("Login", campo);
		campo = new Campo("combo_estado_usuario", "estadoUsuario", false);
		campos.put("Estado Usuario", campo);
		return campos;
	}
	
	/**
	 * Se encarga de configurar los campos del formulario de nuevo
	 * @return
	 */
	/*private Map<String, Object> configurarCamposNuevo() {
		// El Map debe tener la estructura: String del label , Campo 
		Map<String,Object> campos = new LinkedHashMap<>();
		Campo campo = null;
		
		campo = new Campo("text", "nombre");
		campos.put("Nombre", campo);
		
		campo = new Campo("text", "login");
		campos.put("Login", campo);
		
		campo = new Campo("email", "correo");
		campos.put("Email", campo);
		
		campo = new Campo("combo_caducidad", "");
		campos.put("Caducidad", campo);
		
		campo = new Campo("textarea", "informacionAdicional");
		campos.put("Información Adicional", campo);
		
		return campos;
	}*/
	
	/**
	 * Se encarga de configurar los campos del formulario de edicion
	 * @param usuario 
	 * @return
	 */
	/*private Map<String, Object> configurarCamposEdicion(TblUsuario usuario) {
		// El Map debe tener la estructura: String del label , Campo 
		Map<String,Object> campos = new LinkedHashMap<>();
		Campo campo = null;
		campo = new Campo("text", "nombre");
		campos.put("Nombre", campo);
		campo = new Campo("label", usuario.getLogin());
		campos.put("Login", campo);
		campo = new Campo("combo_estado_usuario", "");
		campos.put("Estado Usuario", campo);
		campo = new Campo("email", "correo");
		campos.put("Email", campo);
		campo = new Campo("combo_caducidad", "");
		campos.put("Caducidad", campo);
		campo = new Campo("textarea", "informacionAdicional");
		campos.put("Información Adicional", campo);
		return campos;
	}*/
	
	/**
	 * Se encarga de configurar los campos del formulario de asociar perfil
	 * @param usuario 
	 * @return
	 */
	private Map<String, Object> configurarCamposPerfil(TblUsuario usuario) {
		// El Map debe tener la estructura: String del label , Campo 
		Map<String,Object> campos = new LinkedHashMap<>();
		Campo campo = null;
		campo = new Campo("label", usuario.getLogin());
		campos.put("Login", campo);
		campo = new Campo("label", usuario.getNombre());
		campos.put("Nombre", campo);
		campo = new Campo("imagen_estado_usuario", "");
		campos.put("Estado Usuario", campo);
		return campos;
	}
	
}
