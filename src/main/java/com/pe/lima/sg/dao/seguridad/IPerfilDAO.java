package com.pe.lima.sg.dao.seguridad;


import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.entity.seguridad.TblPerfil;

public interface IPerfilDAO extends BaseDAO<TblPerfil, Integer> {
	
	@Query(value = "select * from seg.tbl_perfil where ( UPPER(nombre) LIKE '%'||UPPER(:nombre)||'%' OR :nombre IS NULL ) AND (estado = :estado OR :estado IS NULL ) ORDER BY nombre", nativeQuery = true)
	List<TblPerfil> findByNombreEstado(	@Param("nombre") String strNombre,
										@Param("estado") String chrEstado);
	
	@Query(value = "select * from seg.tbl_perfil where ( UPPER(nombre) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL ) AND ( UPPER(descripcion) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL ) AND (estado = :filtro OR :filtro IS NULL ) ORDER BY nombre", nativeQuery = true)
	List<TblPerfil> listarConFiltros(	@Param("filtro") String filtro);
	
	@Query(value = "select * from seg.tbl_perfil where ( UPPER(nombre) LIKE '%'||UPPER(:nombre)||'%' OR :nombre IS NULL ) AND (estado_perfil = :estadoPerfil OR :estadoPerfil = '-1') AND estado = '1' ORDER BY nombre", nativeQuery = true)
	List<TblPerfil> listarCriterios(	@Param("nombre") String strNombre,
										@Param("estadoPerfil") String chrEstado);
	
	@Query(value = "select * from seg.tbl_perfil where estado = '1'  ORDER BY nombre", nativeQuery = true)
	List<TblPerfil> listarAllActivos();
	
	@Query(value = "select count(1) from seg.tbl_perfil where TRIM(UPPER(nombre)) = TRIM(UPPER(:nombre)) AND estado = '1' ", nativeQuery = true)
	Integer countOneByLogin(@Param("nombre") String strNombre);
	
	@Query(value = "select * from seg.tbl_perfil where UPPER(nombre) = UPPER(:nombre) AND estado = '1'  ORDER BY nombre ", nativeQuery = true)
	List<TblPerfil> buscarOneByNombre(@Param("nombre") String strNombre);
	
}
