package com.pe.lima.sg.service.seguridad;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author: Gregorio Rodriguez P.
 * @Clase: PerfilService
 * @Descripcion: Implementacion de los metodos de la interfaz de PerfilService
 *
 */



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.lima.sg.dao.seguridad.IPerfilDAO;
import com.pe.lima.sg.entity.seguridad.TblPerfil;
@Service
public class PerfilService  {
	private static final Logger LOGGER = LoggerFactory.getLogger(PerfilService.class);
	@Autowired
	private IPerfilDAO perfilDao;
	
	 /**
	  * getAllPerfil : Lista todos los perfiles
	  * */
	public List<TblPerfil> getAllPerfil() {
		LOGGER.debug("[getAllPerfil] Inicio");
		List<TblPerfil> listaPerfil = null;
		try{
			listaPerfil = perfilDao.findAll();
		}catch(Exception e){
			listaPerfil = null;
		}
		LOGGER.debug("[getAllPerfil] Fin");
		return listaPerfil;
	}

	 /**
	  * getPerfilById : Obtiene un Perfil
	  * */
	public TblPerfil getPerfilById(Integer id) {
		LOGGER.debug("[getPerfilById] Inicio");
		TblPerfil perfil = null;
		try{
			perfil = perfilDao.findOne(id);
		}catch(Exception e){
			e.printStackTrace();
			perfil = null;
		}
		LOGGER.debug("[getPerfilById] Fin");
		return perfil;
	}

	/**
	  * addPerfil : Registra un Perfil
	  * */
	public boolean addPerfil(TblPerfil perfil) {
		LOGGER.debug("[addPerfil] Inicio");
		List<TblPerfil>  lista = null;
		boolean resultado = false;
		try{
			lista = perfilDao.findByNombreEstado(perfil.getNombre(), perfil.getEstado());
			 if (lista != null && lista.size() > 0) {
				 LOGGER.debug("[addPerfil] Existe el elemento:"+lista.size());
				 resultado = false;
	         } else {
	        	 perfilDao.save(perfil);
	        	 resultado = true;
	         }
		}catch(Exception e){
			e.printStackTrace();
			LOGGER.debug("[addPerfil] Error:"+e.getMessage());
		}
		LOGGER.debug("[addPerfil] Fin");
		return resultado;
	}

	/**
	  * updatePerfil : Actualiza un Perfil
	  * */
	public boolean updatePerfil(TblPerfil perfil) {
		LOGGER.debug("[updatePerfil] Inicio");
		boolean resultado = false;
		try{
			perfilDao.save(perfil);
			resultado = true;
		}catch(Exception e){
			e.printStackTrace();
			LOGGER.debug("[updatePerfil] Error:"+e.getMessage());
			resultado = false;
		}
		LOGGER.debug("[updatePerfil] Fin:"+resultado);
		return resultado;
	}

	/**
	  * deletePerfil : Elimina un perfil
	  * */
	public boolean deletePerfil(TblPerfil perfil) {
		LOGGER.debug("[updatePerfil] Inicio");
		boolean resultado = false;
		try{
			perfilDao.delete(perfil);
			resultado = true;
		}catch(Exception e){
			e.printStackTrace();
			LOGGER.debug("[updatePerfil] Error:"+e.getMessage());
			resultado = false;
		}
		LOGGER.debug("[updatePerfil] Fin:"+resultado);
		return resultado;
				
	}
	/**
	  * getPerfilByNombreEstado : Lista todos los perfiles por nombre y estado
	  * */
	public List<TblPerfil> getPerfilByNombreEstado(TblPerfil perfil) {
		LOGGER.debug("[getUsuarioByNombreEstado] Inicio");
		List<TblPerfil> listaPerfil = null;
		try{
			listaPerfil = perfilDao.findByNombreEstado(perfil.getNombre(), perfil.getEstado());
		}catch(Exception e){
			listaPerfil = null;
		}
		LOGGER.debug("[getUsuarioByNombreEstado] Fin");
		return listaPerfil;
	}

}
