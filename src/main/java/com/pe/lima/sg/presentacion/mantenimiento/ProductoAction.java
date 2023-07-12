package com.pe.lima.sg.presentacion.mantenimiento;

import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conEstadoProducto;
import static com.pe.lima.sg.dao.mantenimiento.CatalogoSpecifications.conCodigoSunat;
import static com.pe.lima.sg.dao.mantenimiento.CatalogoSpecifications.conCodigoTipoCatalogo;
import static com.pe.lima.sg.dao.mantenimiento.CatalogoSpecifications.conEstadoCatalogo;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conCodigoProducto;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conNombreProducto;
import static com.pe.lima.sg.dao.mantenimiento.ProductoSpecifications.conCodigoEmpresa;

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
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.dao.mantenimiento.IProductoDAO;
import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.util.Constantes;

/**
 * Clase Bean que se encarga de la administracion de los productos
 *
 * 			
 */
@Controller
//@PreAuthorize("hasAuthority('CRUD')")
public class ProductoAction extends BaseOperacionPresentacion<TblProducto> {
	
	@Autowired
	private IProductoDAO productoDao;
	
	@Autowired
	private IEmpresaDAO empresaDao;
	
	@Autowired
	private ICatalogoDAO catalogoDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductoAction.class);
	
	@Override
	public BaseOperacionDAO<TblProducto, Integer> getDao() {
		return productoDao;
	}	
	
	/**
	 * Se encarga de listar todos los productos
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/productos", method = RequestMethod.GET)
	public String traerRegistros(Model model, String path, HttpServletRequest request) {
		TblProducto entidad = null;
		try{
			LOGGER.debug("[traerRegistros] Inicio");
			path = "mantenimiento/producto/pro_listado";
			entidad = new TblProducto();
			entidad.setTblCatalogo(new TblCatalogo());
			model.addAttribute("entidad", entidad);
			this.listarProductos(model, entidad, request);
			
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
	 * Se encarga de buscar la informacion del producto segun el filtro
	 * seleccionado
	 * 
	 * @param model
	 * @param productoBean
	 * @return
	 */
	@RequestMapping(value = "/productos/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, TblProducto entidad, String path,HttpServletRequest request) {
		path = "mantenimiento/producto/pro_listado";
		try{
			LOGGER.debug("[traerRegistrosFiltrados] Inicio");
			this.listarProductos(model, entidad, request);
			model.addAttribute("entidad", entidad);
			//Se usa al regresar al listado desde el nuevo o editar
			request.getSession().setAttribute("ProductoFiltroSession", entidad);
			LOGGER.debug("[traerRegistrosFiltrados] Fin");
		}catch(Exception e){
			LOGGER.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	/*** Listado de Productos ***/
	private void listarProductos(Model model, TblProducto entidad, HttpServletRequest request){
		List<TblProducto> entidades = new ArrayList<TblProducto>();
		try{
			if (entidad.getNombre() == null){
				entidad.setNombre("");
			}
			Specification<TblProducto> criterio = Specifications.where(conCodigoProducto((entidad.getCodigoEmpresa())))
					.and(conNombreProducto(entidad.getNombre().toUpperCase()))
					.and(conCodigoEmpresa( ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ))
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
	
	
	/**
	 * Se encarga de direccionar a la pantalla de edicion del Producto
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "producto/editar/{id}", method = RequestMethod.GET)
	public String editarEdificio(@PathVariable Integer id, Model model) {
		TblProducto entidad 			= null;
		try{
			entidad = productoDao.findOne(id);
			if (entidad.getUnidadMedida()!=null) {
				entidad.setUnidadMedida(entidad.getUnidadMedida().toUpperCase());
			}
			model.addAttribute("entidad", entidad);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad = null;
		}
		return "mantenimiento/producto/pro_edicion";
	}
	
	
	/**
	 * Se encarga de direccionar a la pantalla de creacion del Producto
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "producto/nuevo", method = RequestMethod.GET)
	public String crearEdificio(Model model) {
		TblProducto entidad = null;
		try{
			LOGGER.debug("[crearEdificio] Inicio");
			entidad = new TblProducto();
			entidad.setTblCatalogo(new TblCatalogo());
			model.addAttribute("entidad", entidad);
			LOGGER.debug("[crearEdificio] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "mantenimiento/producto/pro_nuevo";
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
	public void preGuardar(TblProducto entidad, HttpServletRequest request) {
		try{
			LOGGER.debug("[preGuardar] Inicio" );
			entidad.setFechaCreacion(new Date(System.currentTimeMillis()));
			entidad.setIpCreacion(request.getRemoteAddr());
			entidad.setUsuarioCreacion(this.obtenerUsuarioSession(request));
			entidad.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			entidad.setTblEmpresa(empresaDao.findOne(((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ));
			//MAYUSCULAS
			entidad.setNombre(entidad.getNombre().toUpperCase());
			LOGGER.debug("[preGuardar] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/** Validamos logica del negocio**/
	@Override
	public boolean validarNegocio(Model model,TblProducto entidad, HttpServletRequest request){
		boolean exitoso = true;
		Integer total 	= null;
		try{
			if (entidad.getTblCatalogo()== null || entidad.getTblCatalogo().getCodigoSunat().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar el tipo de unidad");
				return exitoso;
			}
			if (entidad.getCodigoEmpresa() == null || entidad.getCodigoEmpresa().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el codigo de producto");
				return exitoso;
			}
			if (entidad.getNombre() == null || entidad.getNombre().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el nombre del producto");
				return exitoso;
			}
			if (entidad.getPrecio() == null || entidad.getPrecio().doubleValue()<=0){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el precio");
				return exitoso;
			}
			if (entidad.getUnidadMedida()== null || entidad.getUnidadMedida().equals("-1")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe seleccionar la Unidad de Medida");
				return exitoso;
			}
			//Validando la existencia del producto
			total = productoDao.totalNombreProducto(entidad.getNombre());
			if (total > 0){
				exitoso = false;
				model.addAttribute("respuesta", "El nombre del Producto existe, debe modificarlo para continuar...");
			}
			
		}catch(Exception e){
			exitoso = false;
		}finally{
			total = null;
		}
		return exitoso;
	}
	/**
	 * Se encarga de guardar la informacion del Producto
	 * 
	 * @param productoBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "producto/nuevo/guardar", method = RequestMethod.POST)
	public String guardarEntidad(Model model, TblProducto entidad, HttpServletRequest request, String path) {
		path = "mantenimiento/producto/pro_listado";
		List<TblCatalogo> listaCatalogo = null;
		try{
			LOGGER.debug("[guardarEntidad] Inicio" );
			if (this.validarNegocio(model, entidad, request)){
				LOGGER.debug("[guardarEntidad] Pre Guardar..." );
				this.preGuardar(entidad, request);
				Specification<TblCatalogo> criterio = Specifications.where(conCodigoSunat((entidad.getTblCatalogo().getCodigoSunat())))
						.and(conCodigoTipoCatalogo(Constantes.TIPO_CATALAGO_COD_TIPO_MONEDA))
						.and(conEstadoCatalogo(Constantes.ESTADO_REGISTRO_ACTIVO));
				listaCatalogo = catalogoDao.findAll(criterio);
				if (listaCatalogo != null && listaCatalogo.size()>0){
					entidad.setTblCatalogo(listaCatalogo.get(0));
					
					boolean exitoso = super.guardar(entidad, model);
					LOGGER.debug("[guardarEntidad] Guardado..." );
					if (exitoso){
						this.listarProductos(model, entidad, request);
						model.addAttribute("entidad", new TblProducto());
					}else{
						path = "mantenimiento/producto/pro_nuevo";
						model.addAttribute("entidad", entidad);
					}
				}else{

					model.addAttribute("respuesta", "No se encontró el tipo de documento en el catalogo");
					model.addAttribute("entidad", entidad);
				}
			}else{
				path = "mantenimiento/producto/pro_nuevo";
				model.addAttribute("entidad", entidad);
			}
			
			LOGGER.debug("[guardarEntidad] Fin" );
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
		
	}
	
	@Override
	public void preEditar(TblProducto entidad, HttpServletRequest request) {
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
	
	@RequestMapping(value = "producto/nuevo/editar", method = RequestMethod.POST)
	public String editarEntidad(TblProducto entidad, Model model, HttpServletRequest request) {
		String path 				= "mantenimiento/producto/pro_listado";;
		TblProducto entidadEnBd 		= null;
		try{
			// Se actualizan solo los campos del formulario
			entidadEnBd = productoDao.findOne(entidad.getCodigoProducto());
			entidadEnBd.setCodigoEmpresa(entidad.getCodigoEmpresa());
			entidadEnBd.setPrecio(entidad.getPrecio());
			entidadEnBd.setNombre(entidad.getNombre());
			entidadEnBd.setPeso(entidad.getPeso());
			entidadEnBd.setUnidadMedida(entidad.getUnidadMedida());
			this.preEditar(entidadEnBd, request);
			boolean exitoso = super.guardar(entidadEnBd, model);
			LOGGER.debug("[guardarEntidad] Guardado..." );
			if (exitoso){
				this.listarProductos(model, entidadEnBd, request);
				model.addAttribute("entidad", new TblProducto());
			}else{
				path = "mantenimiento/producto/pro_edicion";
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
	 * Se encarga de la eliminacion logica del Producto
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "producto/eliminar/{id}", method = RequestMethod.GET)
	public String eliminarEntidad(@PathVariable Integer id, HttpServletRequest request, Model model) {
		TblProducto entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[eliminarEntidad] Inicio");
			entidad = productoDao.findOne(id);
			entidad.setEstado(Constantes.ESTADO_REGISTRO_INACTIVO);
			this.preEditar(entidad, request);
			productoDao.save(entidad);
			model.addAttribute("respuesta", "Eliminación exitosa");
			entidad = (TblProducto)request.getSession().getAttribute("ProductoFiltroSession");
			if (entidad ==null){
				entidad = new TblProducto();
			}
			this.listarProductos(model, entidad, request);
			model.addAttribute("entidad", entidad);
			path = "mantenimiento/producto/pro_listado";
			LOGGER.debug("[eliminarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
	

	@Override
	public TblProducto getNuevaEntidad() {
		return new TblProducto();
	}
		
	@RequestMapping(value = "producto/regresar", method = RequestMethod.GET)
	public String regresarEntidad(HttpServletRequest request, Model model) {
		TblProducto entidad			= null;
		String path 				= null;
		try{
			LOGGER.debug("[regresarEntidad] Inicio");
			entidad = (TblProducto)request.getSession().getAttribute("ProductoFiltroSession");
			if (entidad == null){
				entidad = new TblProducto();
			}
			this.listarProductos(model, entidad, request);
			model.addAttribute("entidad", entidad);
			path = "mantenimiento/producto/pro_listado";
			LOGGER.debug("[regresarEntidad] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidad 	= null;
		}
		return path;
	}
}
