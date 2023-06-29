package com.pe.lima.sg.dao.mantenimiento;


import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblUbigeo;

public interface IUbigeoDAO extends BaseOperacionDAO<TblUbigeo, Integer> {
	
	@Query(value = "select * from mae.tbl_ubigeo where codigo_padre_inei = '' order by nombre asc", nativeQuery = true)
	List<TblUbigeo> obtenerDepartamento();

	@Query(value = "select * from mae.tbl_ubigeo where length(codigo_padre_inei) = 2 order by codigo_padre_inei, nombre asc ", nativeQuery = true)
	List<TblUbigeo> obtenerProvincia();
	
	@Query(value = "select * from mae.tbl_ubigeo where length(codigo_padre_inei) = 4 order by codigo_padre_inei, nombre asc ", nativeQuery = true)
	List<TblUbigeo> obtenerDistrito();
	
}
