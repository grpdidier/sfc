package com.pe.lima.sg.service.seguridad;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author: Gregorio Rodriguez P.
 * @Clase: AccesoService
 * @Descripcion: Implementacion de los metodos de la interfaz de AccesoService
 *
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.lima.sg.dao.seguridad.IAccesoDAO;
import com.pe.lima.sg.entity.seguridad.TblAcceso;
@Service
public class AccesoService  {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccesoService.class);
	@Autowired
	private IAccesoDAO accesoDao;
	
	 /**
	  * getAllAccesos : Lista todos los accesos
	  * */
	public List<TblAcceso> getAllAccesos() {
		LOGGER.debug("[getAllAccesos] Inicio");
		List<TblAcceso> listaAcceso = null;
		try{
			listaAcceso = accesoDao.findAll();
		}catch(Exception e){
			listaAcceso = null;
		}
		LOGGER.debug("[getAllAccesos] Fin");
		return listaAcceso;
	}

	 /**
	  * getAccesoById : Obtiene un acceso
	  * */
	public TblAcceso getAccesoById(Integer id) {
		LOGGER.debug("[getAccesoById] Inicio");
		TblAcceso acceso = null;
		try{
			acceso = accesoDao.findOne(id);
		}catch(Exception e){
			e.printStackTrace();
			acceso = null;
		}
		LOGGER.debug("[getAccesoById] Fin");
		return acceso;
	}

	/**
	  * addAcceso : Registra un acceso
	  * */
	public boolean addAcceso(TblAcceso acceso) {
		LOGGER.debug("[addAcceso] Inicio");
		boolean resultado = false;
		try{
			accesoDao.save(acceso);
       	 resultado = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		LOGGER.debug("[addAcceso] Fin:"+resultado);
		return resultado;
	}

	/**
	  * addAcceso : Actualiza un acceso
	  * */
	public boolean updateAcceso(TblAcceso acceso) {
		LOGGER.debug("[updateAcceso] Inicio");
		boolean resultado = false;
		try{
			accesoDao.save(acceso);
			resultado = true;
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}
		LOGGER.debug("[updateAcceso] Fin:"+resultado);
		return resultado;
	}

	/**
	  * deleteAcceso : Elimina un acceso
	  * */
	public boolean deleteAcceso(TblAcceso acceso) {
		LOGGER.debug("[deleteAcceso] Inicio");
		boolean resultado = false;
		try{
			accesoDao.delete(acceso);
			resultado = true;
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}
		LOGGER.debug("[deleteAcceso] Fin:"+resultado);
		return resultado;
				
	}

}
