package com.pe.lima.sg.dao.seguridad;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.entity.seguridad.TblIntento;

public interface IIntentoDAO extends BaseDAO<TblIntento, Integer> {
	@Query(value = "select * from seg.tbl_intento where ( UPPER(usu.login) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL )  ORDER BY 1", nativeQuery = true)
	List<TblIntento> listarConFiltros(@Param("filtro") String filtro);

	
}
