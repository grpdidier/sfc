package com.pe.lima.sg.dao.operacion;



import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblConfiguracion;


public interface IConfiguracionDAO extends BaseOperacionDAO<TblConfiguracion, Integer> {
	
	@Query(value = "select * from ope.tbl_configuracion where estado = '1' ", nativeQuery = true)
	List<TblConfiguracion> listarAllActivos();
	
}
