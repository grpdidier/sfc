package com.pe.lima.sg.service.seguridad;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author: Gregorio Rodriguez P.
 * @Clase: IntentoService
 * @Descripcion: Implementacion de los metodos de la interfaz de IntentoService
 *
 */



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.lima.sg.dao.seguridad.IIntentoDAO;
import com.pe.lima.sg.entity.seguridad.TblIntento;
@Service
public class IntentoService  {
	private static final Logger LOGGER = LoggerFactory.getLogger(IntentoService.class);
	
	@Autowired
	private IIntentoDAO intentoDao;
	
	
	 /**
	  * getAllIntentos : Lista todos los intentos
	  * */
	public List<TblIntento> getAllIntentos() {
		LOGGER.debug("[getAllIntentos] Inicio");
		List<TblIntento> listaIntento = null;
		try{
			listaIntento = intentoDao.findAll();
		}catch(Exception e){
			listaIntento = null;
		}
		LOGGER.debug("[getAllIntentos] Fin");
		return listaIntento;
	}

	 /**
	  * getIntentoById : Obtiene un intento
	  * */
	public TblIntento getIntentoById(Integer id) {
		LOGGER.debug("[getIntentoById] Inicio");
		TblIntento intento = null;
		try{
			intento = intentoDao.findOne(id);
		}catch(Exception e){
			e.printStackTrace();
			intento = null;
		}
		LOGGER.debug("[getIntentoById] Fin");
		return intento;
	}

	/**
	  * addIntento : Registra un intento
	  * */
	public boolean addIntento(TblIntento intento) {
		LOGGER.debug("[addIntento] Inicio");
		boolean resultado = false;
		try{
			intentoDao.save(intento);
       	 resultado = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		LOGGER.debug("[addIntento] Fin");
		return resultado;
	}

	/**
	  * addIntento : Actualiza un intento
	  * */
	public boolean updateIntento(TblIntento intento) {
		LOGGER.debug("[updateIntento] Inicio");
		boolean resultado = false;
		try{
			intentoDao.save(intento);
			resultado = true;
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}
		LOGGER.debug("[updateIntento] Fin:"+resultado);
		return resultado;
	}

	/**
	  * deleteIntento : Elimina un intento
	  * */
	public boolean deleteIntento(TblIntento intento) {
		LOGGER.debug("[deleteIntento] Inicio");
		boolean resultado = false;
		try{
			intentoDao.delete(intento);
			resultado = true;
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}
		LOGGER.debug("[deleteIntento] Fin:"+resultado);
		return resultado;
				
	}

}
