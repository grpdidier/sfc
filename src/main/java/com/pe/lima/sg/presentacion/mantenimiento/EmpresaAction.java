package com.pe.lima.sg.presentacion.mantenimiento;

import static com.pe.lima.sg.dao.mantenimiento.EmpresaSpecifications.conEstadoEmpresa;
import static com.pe.lima.sg.dao.mantenimiento.EmpresaSpecifications.conNombreComercial;
import static com.pe.lima.sg.dao.mantenimiento.EmpresaSpecifications.conRucEmpresa;

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
import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.util.Constantes;

/**
 * Clase Bean que se encarga de la administracion de la Empresa
 *
 * 			
 */
@Controller
//@PreAuthorize("hasAuthority('CRUD')")
public class EmpresaAction extends BaseOperacionPresentacion<TblEmpresa> {
	
	@Autowired
	private IEmpresaDAO empresaDao;
		
	private static final Logger LOGGER = LoggerFactory.getLogger(EmpresaAction.class);
	
	@Override
	public BaseOperacionDAO<TblEmpresa, Integer> getDao() {
		return empresaDao;
	}	
	
	/**
	 * Se encarga de listar todos los empresas
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/empresas", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path) {
		TblEmpresa entidad = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "mantenimiento/empresa/emp_listado";
			entidad = new TblEmpresa();
			model.addAttribute("entidad", entidad);
			this.listarEmpresas(model, entidad);
			
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
	 * Se encarga de buscar la informacion de la empresa segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @param empresaBean
	 * @return
	 */
	@RequestMapping(value = "/empresas/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, TblEmpresa entidad, String path,HttpServletRequest request) {
		path = "mantenimiento/empresa/emp_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarEmpresas(model, entidad);
			model.addAttribute("entidad", entidad);
			//Se usa al regresar al listado desde el nuevo o editar
			request.getSession().setAttribute("EmpresaFiltroSession", entidad);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Empresas ***/
	private void listarEmpresas(Model model, TblEmpresa entidad){
		List<TblEmpresa> entidades = new ArrayList<TblEmpresa>();
		try{
			if (entidad.getNombreComercial() == null){
				entidad.setNombreComercial("");
			}
			if (entidad.getRuc() == null){
				entidad.setRuc("");
			}
			Specification<TblEmpresa> criterio = Specifications.where(conRucEmpresa((entidad.getRuc())))
					.and(conNombreComercial(entidad.getNombreComercial().toUpperCase()))
					.and(conEstadoEmpresa(Constantes.ESTADO_REGISTRO_ACTIVO));
			entidades = empresaDao.findAll(criterio);
			LOGGER.debug("[listarEmpresa] entidades:"+entidades);
			model.addAttribute("registros", entidades);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de edicion del Empresa
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "empresa/editar/{id}", method = RequestMethod.GET)
	public String editarEdificio(@PathVariable Integer id, Model model) {
		TblEmpresa entidad 			= null;
		try{
			entidad = empresaDao.findOne(id);
			model.addAttribute("entidad", entidad);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "mantenimiento/empresa/emp_edicion";
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Empresa
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "empresa/nuevo", method = RequestMethod.GET)
	public String crearEdificio(Model model) {
		TblEmpresa entidad = null;
		try{
			LOGGER.debug("[crearEdificio] Inicio");
			entidad = new TblEmpresa();
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[crearEdificio] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "mantenimiento/empresa/emp_nuevo";
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
	public void preGuardar(TblEmpresa entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preGuardar] Inicio" );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(this.obtenerUsuarioSession(request));
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			//MAYUSCULAS
			entidad.setNombreComercial(entidad.getNombreComercial().toUpperCase());
			entidad.setRazonSocial(entidad.getRazonSocial().toUpperCase());
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblEmpresa entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total 	= null;
		try{
			if (entidad.getRuc()== null || entidad.getRuc().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el RUC");
				return exitoso;
			}
			if (entidad.getNombreComercial() == null || entidad.getNombreComercial().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el nombre comercial");
				return exitoso;
			}
			if (entidad.getRazonSocial() == null || entidad.getRazonSocial().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la razón social");
				return exitoso;
			}
			if (entidad.getDireccion() == null || entidad.getDireccion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la dirección");
				return exitoso;
			}
			if (entidad.getDomicilioFiscal() == null || entidad.getDomicilioFiscal().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el docimilio fiscal");
				return exitoso;
			}
			if (entidad.getUbigeoDomFiscal() == null || entidad.getUbigeoDomFiscal().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el Ubigeo");
				return exitoso;
			}
			if (entidad.getDepartamento() == null || entidad.getDepartamento().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el departamento");
				return exitoso;
			}
			if (entidad.getProvincia() == null || entidad.getProvincia().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la provincia");
				return exitoso;
			}
			if (entidad.getDistrito() == null || entidad.getDistrito().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el distrito");
				return exitoso;
			}
			//Validando la existencia del empresa
			total = empresaDao.totalRucEmpresa(entidad.getRuc());
			if (total > 0){
				exitoso = false;
				model.addAttribute("respuesta", "El número de RUC del Empresa existe, debe modificarlo para continuar...");
			}
			
		}catch(Exception e){
			exitoso = false;
		}finally{
			total = null;
		}
		return exitoso;
	}
	/**
	 * Se encarga de guardar la informacion del Empresa
	 * 
	 * @param empresaBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "empresa/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, TblEmpresa entidad, HttpServletRequest request, String path) {
		path = "mantenimiento/empresa/emp_listado";
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			if (this.validarNegocio(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.preGuardar(entidad, request);
					
				boolean exitoso = super.guardar(entidad, model);
				LOGGER.debug("[guardarEntidad] Guardado..." );
				if (exitoso){
					this.listarEmpresas(model, entidad);
					model.addAttribute("entidad", new TblEmpresa());
				}else{
					path = "mantenimiento/empresa/emp_nuevo";
					model.addAttribute("entidad", entidad);
				}
			
			}else{
				path = "mantenimiento/empresa/emp_nuevo";
				model.addAttribute("entidad", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
		
	}
	
	@Override
	public void preEditar(TblEmpresa entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preEditar] Inicio" );
			entidad.setFechaModificacion(new Date(System.currentTimeMillis()));
			entidad.setIpModificacion(request.getRemoteAddr());
			entidad.setUsuarioModificacion(this.obtenerUsuarioSession(request));
			//MAYUSCULAS
			entidad.setNombreComercial(entidad.getNombreComercial().toUpperCase());
			entidad.setRazonSocial(entidad.getRazonSocial().toUpperCase());
			LOGGER.debug("[preEditar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "empresa/nuevo/editar", method = RequestMethod.POST)
	public String editarEntidad(TblEmpresa entidad, Model model, HttpServletRequest request) {
		String path 				= "mantenimiento/empresa/emp_listado";;
		TblEmpresa entidadEnBd 		= null;
		try{
			// Se actualizan solo los campos del formulario
			entidadEnBd = empresaDao.findOne(entidad.getCodigoEntidad());
			
			entidadEnBd.setDistrito(entidad.getDistrito());
			entidadEnBd.setProvincia(entidad.getProvincia());
			entidadEnBd.setDepartamento(entidad.getDepartamento());
			entidadEnBd.setUbigeoDomFiscal(entidad.getUbigeoDomFiscal());
			entidadEnBd.setDireccion(entidad.getDistrito());
			entidadEnBd.setDomicilioFiscal(entidad.getDomicilioFiscal());
			entidadEnBd.setRazonSocial(entidad.getRazonSocial());
			entidadEnBd.setNombreComercial(entidad.getNombreComercial());
			this.preEditar(entidadEnBd, request);
			boolean exitoso = super.guardar(entidadEnBd, model);
			LOGGER.debug("[guardarEntidad] Guardado..." );
			if (exitoso){
				this.listarEmpresas(model, entidadEnBd);
				model.addAttribute("entidad", new TblEmpresa());
			}else{
				path = "mantenimiento/empresa/emp_edicion";
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
	 * Se encarga de la eliminacion logica del Empresa
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "empresa/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarEntidad(@PathVariable Integer id, HttpServletRequest request, Model model) {
		TblEmpresa entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[eliminarEntidad] Inicio");
			entidad = empresaDao.findOne(id);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			this.preEditar(entidad, request);
			empresaDao.save(entidad);
			model.addAttribute("respuesta", "Eliminación exitosa");
			entidad = (TblEmpresa)request.getSession().getAttribute("EmpresaFiltroSession");
			if (entidad ==null){
				entidad = new TblEmpresa();
			}
			this.listarEmpresas(model, entidad);
			model.addAttribute("entidad", entidad);
			path = "mantenimiento/empresa/emp_listado";
			LOGGER.debug("[eliminarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
	

	@Override
	public TblEmpresa getNuevaEntidad() {
		return new TblEmpresa();
	}
		
	@RequestMapping(value = "empresa/regresar", method = RequestMethod.GET)
	public String regresarEntidad(HttpServletRequest request, Model model) {
		TblEmpresa entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[regresarEntidad] Inicio");
			entidad = (TblEmpresa)request.getSession().getAttribute("EmpresaFiltroSession");
			if (entidad == null){
				entidad = new TblEmpresa();
			}
			this.listarEmpresas(model, entidad);
			model.addAttribute("entidad", entidad);
			path = "mantenimiento/empresa/emp_listado";
			LOGGER.debug("[regresarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
}
