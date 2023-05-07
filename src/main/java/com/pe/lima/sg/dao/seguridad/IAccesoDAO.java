package com.pe.lima.sg.dao.seguridad;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.entity.seguridad.TblAcceso;

public interface IAccesoDAO extends BaseDAO<TblAcceso, Integer> {
	@Query(value = "select acc.*, usu.* from seg.tbl_acceso acc, seg.tbl_usuario usu where acc.codigoUsuario = usu.codigoUsuario AND ( UPPER(usu.login) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL ) AND ( UPPER(usu.nombre) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL ) AND (usu.estado = :filtro OR :filtro IS NULL ) ORDER BY 1", nativeQuery = true)
	List<TblAcceso> listarConFiltros(@Param("filtro") String filtro);

	
}
