package com.pe.lima.sg.dao.seguridad;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.entity.seguridad.TblUsuario;

public interface IUsuarioDAO extends BaseDAO<TblUsuario, Integer> {
	
	@Query(value = "select * from seg.tbl_usuario where UPPER(login) = UPPER(:login) AND estado = '1' ", nativeQuery = true)
	TblUsuario findOneByLogin(@Param("login") String strLogin);
		
	@Query(value = "select * from seg.tbl_usuario where ( UPPER(login) LIKE '%'||UPPER(:login)||'%' OR :login IS NULL ) AND (estado = :estado OR :estado IS NULL ) AND estado = '1' ORDER BY login, nombre ", nativeQuery = true)
	List<TblUsuario> findByNombreEstado(@Param("login") String strLogin,
										@Param("estado") String chrEstado);
	
	@Query(value = "select * from seg.tbl_usuario where ( UPPER(login) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL ) AND ( UPPER(nombre) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL ) AND (estado = :filtro OR :filtro IS NULL ) AND estado = '1' ORDER BY login, nombre ", nativeQuery = true)
	List<TblUsuario> listarConFiltros(@Param("filtro") String filtro);
	
	@Query(value = "select * from seg.tbl_usuario where UPPER(login) = UPPER(:login) AND estado = '1' ", nativeQuery = true)
	List<TblUsuario> buscarOneByLogin(@Param("login") String strLogin);
	
	@Query(value = "select count(1) from seg.tbl_usuario where UPPER(login) = UPPER(:login) AND estado = '1' ", nativeQuery = true)
	Integer countOneByLogin(@Param("login") String strLogin);
	

	@Query(value = "select * from seg.tbl_usuario where ( UPPER(login) LIKE '%'||UPPER(:login)||'%' OR :login IS NULL ) AND ( UPPER(nombre) LIKE '%'||UPPER(:nombre)||'%' OR :nombre IS NULL ) AND (estado_usuario = :estado_usuario OR :estado_usuario = '-1' )  AND estado = '1' ORDER BY login, nombre ", nativeQuery = true)
	List<TblUsuario> listarCriterios (@Param("login") String strLogin,
									  @Param("nombre") String strNombre,
									  @Param("estado_usuario") String chrEstadoUsuario);
	
	@Query(value = "select * from seg.tbl_usuario where estado = '1'  ORDER BY login, nombre", nativeQuery = true)
	List<TblUsuario> listarAllActivos();
	
	@Query(value = "select * from seg.tbl_usuario where codigo_usuario = :codigo AND estado = '1' ", nativeQuery = true)
	TblUsuario findOneByCodigo(@Param("codigo") Integer intCodigo);
	
	@Query(value = "select * from seg.tbl_usuario where ( UPPER(login) LIKE '%'||UPPER(:login)||'%' OR :login IS NULL ) AND ( UPPER(nombre) LIKE '%'||UPPER(:nombre)||'%' OR :nombre IS NULL ) AND (estado_usuario = :estado_usuario OR :estado_usuario = '-1' )  AND estado = '1' AND codigo_entidad >= 10 ORDER BY login, nombre ", nativeQuery = true)
	List<TblUsuario> listarCriteriosPWD (@Param("login") String strLogin,
									  @Param("nombre") String strNombre,
									  @Param("estado_usuario") String chrEstadoUsuario);
	
}
