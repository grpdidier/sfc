package com.pe.lima.sg.presentacion.mantenimiento;

import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conEstadoCliente;
import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conNombreCliente;
import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conNumeroDocumento;
import static com.pe.lima.sg.dao.mantenimiento.ClienteSpecifications.conCodigoEmpresa;

import static com.pe.lima.sg.dao.mantenimiento.CatalogoSpecifications.conEstadoCatalogo;
import static com.pe.lima.sg.dao.mantenimiento.CatalogoSpecifications.conCodigoTipoCatalogo;
import static com.pe.lima.sg.dao.mantenimiento.CatalogoSpecifications.conCodigoSunat;

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
import com.pe.lima.sg.dao.mantenimiento.ICatalogoDAO;
import com.pe.lima.sg.dao.mantenimiento.IClienteDAO;
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.entity.mantenimiento.TblCliente;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.util.Constantes;

/**
 * Clase Bean que se encarga de la administracion de los clientes
 *
 * 			
 */
@Controller
//@PreAuthorize("hasAuthority('CRUD')")
public class ClienteAction extends BaseOperacionPresentacion<TblCliente> {
	
	@Autowired
	private IClienteDAO clienteDao;
	
	@Autowired
	private IEmpresaDAO empresaDao;
	
	@Autowired
	private ICatalogoDAO catalogoDao;
	
	@Override
	public BaseOperacionDAO<TblCliente, Integer> getDao() {
		return clienteDao;
	}	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseOperacionPresentacion.class);
	
	/**
	 * Se encarga de listar todos los clientes
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/clientes", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path, HttpServletRequest request) {
		TblCliente entidad = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "mantenimiento/cliente/cli_listado";
			entidad = new TblCliente();
			entidad.setTblCatalogo(new TblCatalogo());
			entidad.setNombre("");
			model.addAttribute("entidad", entidad);
			this.listarClientes(model, entidad, request);
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
	 * Se encarga de buscar la informacion del cliente segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @param clienteBean
	 * @return
	 */
	@RequestMapping(value = "/clientes/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, TblCliente entidad, String path, HttpServletRequest request) {
		path = "mantenimiento/cliente/cli_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarClientes(model, entidad, request);
			model.addAttribute("entidad", entidad);
			//Se usa al regresar al listado desde el nuevo o editar
			request.getSession().setAttribute("ClienteFiltroSession", entidad);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Clientes ***/
	private void listarClientes(Model model, TblCliente entidad, HttpServletRequest request){
		List<TblCliente> entidades 	= new ArrayList<TblCliente>();
		Integer codigoEntidad		= null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			
			Specification<TblCliente> criterio = Specifications.where(conNumeroDocumento((entidad.getNumeroDocumento())))
					.and(conCodigoEmpresa(codigoEntidad))
					.and(conNombreCliente(entidad.getNombre().toUpperCase()))
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
	
	
	/**
	 * Se encarga de direccionar a la pantalla de edicion del Cliente
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "cliente/editar/{id}", method = RequestMethod.GET)
	public String editarEdificio(@PathVariable Integer id, Model model) {
		TblCliente entidad 			= null;
		try{
			entidad = clienteDao.findOne(id);
			model.addAttribute("entidad", entidad);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "mantenimiento/cliente/cli_edicion";
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Cliente
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "cliente/nuevo", method = RequestMethod.GET)
	public String crearEdificio(Model model) {
		TblCliente entidad = null;
		try{
			LOGGER.debug("[crearEdificio] Inicio");
			entidad = new TblCliente();
			entidad.setTblCatalogo(new TblCatalogo());
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[crearEdificio] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "mantenimiento/cliente/cli_nuevo";
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
	public void preGuardar(TblCliente entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preGuardar] Inicio" );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(this.obtenerUsuarioSession(request));
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			entidad.setNombre(entidad.getNombre().toUpperCase());
			entidad.setTblEmpresa(empresaDao.findOne(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ));
			
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblCliente entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total 	= null;
		try{
			if (entidad.getTblCatalogo()== null || entidad.getTblCatalogo().getCodigoSunat().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar el tipo de documento");
				return exitoso;
			}
			if (entidad.getNumeroDocumento() == null || entidad.getNumeroDocumento().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el número de documento");
				return exitoso;
			}else{
				entidad.setNumeroDocumento(entidad.getNumeroDocumento().trim());
			}
			if (entidad.getNombre() == null || entidad.getNombre().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el nombre / razón social");
				return exitoso;
			}
			if (entidad.getDireccion() == null || entidad.getDireccion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la dirección del cliente");
				return exitoso;
			}
			//Validando la existencia del cliente
			//total = clienteDao.totalNumeroDocumento(entidad.getNumeroDocumento());
			total = clienteDao.totalNumeroDocumentoxEmpresa(entidad.getNumeroDocumento(), ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() );
			if (total > 0){
				exitoso = false;
				model.addAttribute("respuesta", "El número de documento existe, debe modificarlo para continuar...");
			}
			
		}catch(Exception e){
			exitoso = false;
		}finally{
			total = null;
		}
		return exitoso;
	}
	/**
	 * Se encarga de guardar la informacion del Cliente
	 * 
	 * @param clienteBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "cliente/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, TblCliente entidad, HttpServletRequest request, String path) {
		path = "mantenimiento/cliente/cli_listado";
		List<TblCatalogo> listaCatalogo = null;
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			if (this.validarNegocio(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.preGuardar(entidad, request);
				Specification<TblCatalogo> criterio = Specifications.where(conCodigoSunat((entidad.getTblCatalogo().getCodigoSunat())))
						.and(conCodigoTipoCatalogo(Constantes.TIPO_CATALAGO_COD_TIPO_DOCUMENTO))
						.and(conEstadoCatalogo(Constantes.ESTADO_REGISTRO_ACTIVO));
				listaCatalogo = catalogoDao.findAll(criterio);
				if (listaCatalogo != null && listaCatalogo.size()>0){
					entidad.setTblCatalogo(listaCatalogo.get(0));
					boolean exitoso = super.guardar(entidad, model);
					LOGGER.debug("[guardarEntidad] Guardado..." );
					if (exitoso){
						this.listarClientes(model, entidad, request);
						model.addAttribute("entidad", new TblCliente());
					}else{
						path = "mantenimiento/cliente/cli_nuevo";
						model.addAttribute("entidad", entidad);
					}
				}else{

					model.addAttribute("respuesta", "No se encontró el tipo de documento en el catalogo");
				}
				
			}else{
				path = "mantenimiento/cliente/cli_nuevo";
				model.addAttribute("entidad", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
		
	}
	
	@Override
	public void preEditar(TblCliente entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preEditar] Inicio" );
			entidad.setFechaModificacion(new Date(System.currentTimeMillis()));
			entidad.setIpModificacion(request.getRemoteAddr());
			entidad.setUsuarioModificacion(this.obtenerUsuarioSession(request));
			entidad.setNombre(entidad.getNombre().toUpperCase());
			LOGGER.debug("[preEditar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "cliente/nuevo/editar", method = RequestMethod.POST)
	public String editarEntidad(TblCliente entidad, Model model, HttpServletRequest request) {
		String path 				= "mantenimiento/cliente/cli_listado";;
		TblCliente entidadEnBd 		= null;
		try{
			// Se actualizan solo los campos del formulario
			entidadEnBd = clienteDao.findOne(entidad.getCodigoCliente());
			
			entidadEnBd.setDireccion(entidad.getDireccion());
			entidadEnBd.setNombre(entidad.getNombre());
			entidadEnBd.setEmail(entidad.getEmail());
			
			this.preEditar(entidadEnBd, request);
			boolean exitoso = super.guardar(entidadEnBd, model);
			LOGGER.debug("[guardarEntidad] Guardado..." );
			if (exitoso){
				this.listarClientes(model, entidadEnBd, request);
				model.addAttribute("entidad", new TblCliente());
			}else{
				path = "mantenimiento/cliente/cli_edicion";
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
	 * Se encarga de la eliminacion logica del Cliente
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "cliente/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarEntidad(@PathVariable Integer id, HttpServletRequest request, Model model) {
		TblCliente entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[eliminarEntidad] Inicio");
			entidad = clienteDao.findOne(id);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			this.preEditar(entidad, request);
			clienteDao.save(entidad);
			model.addAttribute("respuesta", "Eliminación exitosa");
			entidad = (TblCliente)request.getSession().getAttribute("ClienteFiltroSession");
			if (entidad ==null){
				entidad = new TblCliente();
			}
			this.listarClientes(model, entidad, request);
			model.addAttribute("entidad", entidad);
			path = "mantenimiento/cliente/cli_listado";
			LOGGER.debug("[eliminarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
	

	@Override
	public TblCliente getNuevaEntidad() {
		return new TblCliente();
	}
		
	@RequestMapping(value = "cliente/regresar", method = RequestMethod.GET)
	public String regresarEntidad(HttpServletRequest request, Model model) {
		TblCliente entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[regresarEntidad] Inicio");
			entidad = (TblCliente)request.getSession().getAttribute("ClienteFiltroSession");
			if (entidad==null){
				entidad = new TblCliente();
			}
			this.listarClientes(model, entidad, request);
			model.addAttribute("entidad", entidad);
			path = "mantenimiento/cliente/cli_listado";
			LOGGER.debug("[regresarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
}
