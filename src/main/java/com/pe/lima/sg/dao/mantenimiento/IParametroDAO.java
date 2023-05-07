package com.pe.lima.sg.dao.mantenimiento;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;

public interface IParametroDAO extends BaseDAO<TblParametro, Integer> {
	
	@Query(value = "select * from mae.tbl_parametro where ( UPPER(nombre) LIKE '%'||UPPER(:nombre)||'%' OR :nombre IS NULL ) AND (estado = :estado OR :estado IS NULL ) ORDER BY nombre", nativeQuery = true)
	List<TblParametro> findByNombreEstado(
										@Param("nombre") String strNombre,
										@Param("estado") Character chrEstado);
	
	@Query(value = "select * from mae.tbl_parametro where ( UPPER(nombre) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL )  AND ( UPPER(dato) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL )AND (estado = :filtro OR :filtro IS NULL )  ORDER BY nombre", nativeQuery = true)
	List<TblParametro> listarConFiltros(@Param("filtro") String filtro);

	/*@Query(value = "select * from mae.tbl_parametro where ( UPPER(nombre) LIKE '%'||UPPER(:nombre)||'%' OR :nombre IS NULL )  AND ( UPPER(dato) LIKE '%'||UPPER(:dato)||'%' OR :dato IS NULL )AND (estado = :estado OR :estado IS NULL ) ORDER BY 1", nativeQuery = true)
	List<TblParametro> listarConFiltros(
										@Param("nombre") String strNombre,
										@Param("dato") String strDato,
										@Param("estado") Character chrEstado);*/
	
	@Query(value = "select * from mae.tbl_parametro where codigo_entidad = :codigoEntidad AND ( UPPER(nombre) LIKE '%'||UPPER(:nombre)||'%' OR :nombre IS NULL ) AND ( UPPER(dato) LIKE '%'||UPPER(:dato)||'%' OR :dato IS NULL )  AND estado = '1'  ORDER BY nombre", nativeQuery = true)
	List<TblParametro> listarCriterios(	
										@Param("nombre") String strNombre,
										@Param("dato") String strDato,
										@Param("codigoEntidad") Integer intCodigoEntidad
									  );
	
	@Query(value = "select * from mae.tbl_parametro where codigo_entidad = :codigoEntidad AND estado = '1'  ORDER BY nombre", nativeQuery = true)
	List<TblParametro> listarAllActivos(@Param("codigoEntidad") Integer intCodigoEntidad);
	
	@Query(value = "select count(1) from mae.tbl_parametro where TRIM(UPPER(nombre)) = TRIM(UPPER(:nombre)) AND estado = '1' ", nativeQuery = true)
	Integer countOneByNombre(@Param("nombre") String strNombre);
	
	@Query(value = "select * from mae.tbl_parametro where codigo_entidad = :codigoEntidad AND  UPPER(nombre) = UPPER(:nombre) AND estado = '1'  ORDER BY nombre", nativeQuery = true)
	List<TblParametro> buscarOneByNombre(@Param("nombre") String strNombre, @Param("codigoEntidad") Integer intCodigoEntidad);
	
}
