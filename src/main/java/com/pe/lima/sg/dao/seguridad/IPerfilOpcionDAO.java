package com.pe.lima.sg.dao.seguridad;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.entity.seguridad.TblPerfilOpcion;
import com.pe.lima.sg.entity.seguridad.TblPerfilOpcionId;

public interface IPerfilOpcionDAO extends BaseDAO<TblPerfilOpcion, TblPerfilOpcionId> {

	@Query(value = "select * from seg.tbl_perfil_opcion  ORDER BY 1", nativeQuery = true)
	List<TblPerfilOpcion> listarConFiltros(@Param("filtro") String filtro);
}
