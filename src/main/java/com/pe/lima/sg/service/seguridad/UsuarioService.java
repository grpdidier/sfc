package com.pe.lima.sg.service.seguridad;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author: Gregorio Rodriguez P.
 * @Clase: UsuarioService
 * @Descripcion: Implementacion de los metodos de la interfaz de UsuarioService
 *
 */



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.lima.sg.dao.seguridad.IUsuarioDAO;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
@Service
public class UsuarioService  {
	private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioService.class);
	@Autowired
	private IUsuarioDAO usuarioDao;
	
	 /**
	  * getAllUsuarios : Lista todos los usuarios
	  * */
	public List<TblUsuario> getAllUsuarios() {
		LOGGER.debug("[getAllUsuarios] Inicio");
		List<TblUsuario> listaUsuario = null;
		try{
			listaUsuario = usuarioDao.findAll();
		}catch(Exception e){
			listaUsuario = null;
		}
		LOGGER.debug("[getAllUsuarios] Fin");
		return listaUsuario;
	}

	 /**
	  * getUsuarioById : Obtiene un usuario
	  * */
	public TblUsuario getUsuarioById(Integer id) {
		LOGGER.debug("[getUsuarioById] Inicio");
		TblUsuario usuario = null;
		try{
			usuario = usuarioDao.findOne(id);
		}catch(Exception e){
			usuario = null;
		}
		LOGGER.debug("[getUsuarioById] Fin");
		return usuario;
	}
	

	/**
	  * addUsuario : Registra un usuario
	  * */
	public boolean addUsuario(TblUsuario usuario) {
		LOGGER.debug("[addUsuario] Inicio");
		List<TblUsuario>  lista = null;
		boolean resultado = false;
		try{
			lista = usuarioDao.findByNombreEstado(usuario.getNombre(), usuario.getEstado());
			 if (lista != null && lista.size() > 0) {
				 resultado = false;
	         } else {
	        	 usuarioDao.save(usuario);
	        	 resultado = true;
	         }
		}catch(Exception e){
			e.printStackTrace();
			LOGGER.debug("[addUsuario] Error:"+e.getMessage());
		}
		LOGGER.debug("[addUsuario] Fin");
		return resultado;
	}

	/**
	  * updateUsuario : Actualiza un usuario
	  * */
	public boolean updateUsuario(TblUsuario usuario) {
		LOGGER.debug("[updateUsuario] Inicio");
		boolean resultado = false;
		try{
			usuarioDao.save(usuario);
			resultado = true;
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
			LOGGER.debug("[updateUsuario] Error:"+e.getMessage());
		}
		LOGGER.debug("[updateUsuario] Fin");
		return resultado;
	}

	/**
	  * deleteUsuario : Elimina un usuario
	  * */
	public boolean deleteUsuario(TblUsuario usuario) {
		LOGGER.debug("[deleteUsuario] Inicio");
		boolean resultado = false;
		try{
			usuarioDao.delete(usuario);
			resultado = true;
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
			LOGGER.debug("[deleteUsuario] Error:"+e.getMessage());
		}
		LOGGER.debug("[deleteUsuario] Fin");
		return resultado;
				
	}
	
	/**
	  * getUsuarioByNombreEstado : Lista todos los usuarios por nombre y estado
	  * */
	public List<TblUsuario> getUsuarioByNombreEstado(TblUsuario usuario) {
		LOGGER.debug("[getUsuarioByNombreEstado] Inicio");
		List<TblUsuario> listaUsuario = null;
		try{
			listaUsuario = usuarioDao.findByNombreEstado(usuario.getLogin(), usuario.getEstado());
		}catch(Exception e){
			listaUsuario = null;
		}
		LOGGER.debug("[getUsuarioByNombreEstado] Fin");
		return listaUsuario;
	}
	/**
	  * getUsuarioByFiltros : Lista todos los usuarios por Filtros
	  * */
	/*public List<TblUsuario> getUsuarioByFiltros(TblUsuario usuario) {
		LOGGER.debug("[getUsuarioByFiltros] Inicio");
		List<TblUsuario> listaUsuario = null;
		try{
			listaUsuario = usuarioDao.listarConFiltros(usuario.getLogin(), usuario.getNombre(), usuario.getEstado());
		}catch(Exception e){
			listaUsuario = null;
		}
		LOGGER.debug("[getUsuarioByFiltros] Fin");
		return listaUsuario;
	}	*/
	
	/**
	  * getUsuarioByFiltros : Lista todos los usuarios por Filtros
	  * */
	public List<TblUsuario> getUsuarioByFiltros(String strDato) {
		LOGGER.debug("[getUsuarioByFiltros] Inicio");
		List<TblUsuario> listaUsuario = null;
		try{
			listaUsuario = usuarioDao.listarConFiltros(strDato);
		}catch(Exception e){
			listaUsuario = null;
		}
		LOGGER.debug("[getUsuarioByFiltros] Fin");
		return listaUsuario;
	}	

}
