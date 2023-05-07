package com.pe.lima.sg.presentacion;


import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.pe.lima.sg.dao.BaseDAO;

public abstract class BasePresentacion<E> {

	@SuppressWarnings("rawtypes")
	public abstract BaseDAO getDao();
	
	public abstract E getNuevaEntidad();

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	/* ********************* Operation of Business Validate ***************************************************** */
	
	public boolean validarNegocio(Model model, E entidad, HttpServletRequest request){
		boolean exitoso = true;
		return exitoso;
	}
	
	/* ********************* Operation of Save ***************************************************** */
	public void preGuardar(E entidad, HttpServletRequest request) {

	}

	protected boolean guardar(E entidad, Model model) {
		boolean exitoso = true;
		try {
			getDao().save(entidad);
			model.addAttribute("respuesta", "Guardado exitoso");
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("respuesta", "Error al intentar guardar");
			exitoso = false;
		} finally {
			model.addAttribute("entidad", getNuevaEntidad());
		}
		return exitoso;
	}
		
	public void postGuardar(E entidad, HttpServletRequest request) {

	}

	public String guardarEntidad(Model model, E entidad, HttpServletRequest request, String path) {

		preGuardar(entidad, request);

		guardar(entidad, model);

		postGuardar(entidad, request);

		return path;
	}
	
	
	/* ********************* Operation of Edit ***************************************************** */
	public void preEditar(E entidad, HttpServletRequest request) {

	}

	public void editar(E entidad, Model model) {
		E entidadModificada = null;
		try {
			entidadModificada = (E) getDao().save(entidad);
			model.addAttribute("respuesta", "Edición exitosa");
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("respuesta", "Error al intentar editar");
		} finally {
			model.addAttribute("entidad", entidad);
		}
	}

	public void postEditar(E entidad, HttpServletRequest request) {

	}

	public String editarEntidad(Model model, E entidad, HttpServletRequest request, String path) {

		preEditar(entidad, request);

		editar(entidad, model);

		postEditar(entidad, request);

		return path;
	}
	/* ********************* Operation of Delete ***************************************************** */
	public void preEliminar(E entidad, HttpServletRequest request) {

	}

	public void eliminar(E entidad, Model model) {
		try {
			getDao().delete(entidad);
			model.addAttribute("respuesta", "Borrado exitoso");
		} catch (Exception e) {
			model.addAttribute("respuesta", "Error al intentar borrar");
		}
	}

	public void postEliminar(E entidad, HttpServletRequest request) {

	}
	
	
	public String eliminarLogico(Model model, E entidad, HttpServletRequest request, String path) {

		preEliminar(entidad, request);

		editar(entidad, model);

		postEliminar(entidad, request);

		return path;
	}

	public String eliminarEntidad(Model model, E entidad, HttpServletRequest request, String path) {

		preEditar(entidad, request);

		editar(entidad, model);

		postEditar(entidad, request);

		return path;
	}
	/* ********************* Operation of List ***************************************************** */
	/*public String traerRegistros(Model model, Filtro filtro, String path) {
		SysOutPrintln.write("[traerRegistros] Inicio");
		List<E> entidades = new ArrayList<>();
		if (filtro.getDato() != null) {
			entidades = getDao().listarConFiltros(filtro.getDato());
		}
		SysOutPrintln.write("[traerRegistros] entidades:"+entidades);
		model.addAttribute("registros", entidades);
		model.addAttribute("filtro", filtro);
		SysOutPrintln.write("[traerRegistros] Fin");
		return path;

	}*/
	/* ********************* Operation of Validate ***************************************************** */
	public boolean isValid(String data) {
		if (data != null && !data.equals("")) {
			return true;
		}

		return false;
	}

}
