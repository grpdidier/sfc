package com.pe.lima.sg.dao.seguridad;


import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseDAO;
import com.pe.lima.sg.entity.seguridad.TblOpcion;

public interface IOpcionDAO extends BaseDAO<TblOpcion, Integer> {
	
	@Query(value = "select * from seg.tbl_opcion where ( UPPER(nombre) LIKE '%'||UPPER(:nombre)||'%' OR :nombre IS NULL ) AND (estado = :estado OR :estado IS NULL ) ORDER BY 1", nativeQuery = true)
	List<TblOpcion> findByNombreEstado(	@Param("nombre") String strNombre,
										@Param("estado") String chrEstado);

	@Query(value = "select * from seg.tbl_opcion where ( UPPER(nombre) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL ) AND ( UPPER(ruta) LIKE '%'||UPPER(:filtro)||'%' OR :filtro IS NULL ) AND (estado = :filtro OR :filtro IS NULL ) ORDER BY 1", nativeQuery = true)
	List<TblOpcion> listarConFiltros(@Param("filtro") String filtro);
	
	@Query(value = "select * from seg.tbl_opcion where estado = '1' ORDER BY nivel, nombre", nativeQuery = true)
	List<TblOpcion> listarAllActivos();
	
	@Query(value = "select * from seg.tbl_opcion where modulo = :modulo AND estado = '1' ORDER BY nombre", nativeQuery = true)
	List<TblOpcion> listarAllSubModulos(@Param("modulo") Integer intModulo);
	
	@Query(value = "select count(1) from seg.tbl_opcion where TRIM(UPPER(nombre)) = TRIM(UPPER(:nombre)) AND estado = '1' ", nativeQuery = true)
	Integer countOneByLogin(@Param("nombre") String strNombre);
	
	
	@Query(value = "select opc.* from seg.tbl_opcion opc, seg.tbl_perfil_opcion pop  where pop.codigo_perfil = :perfil AND opc.codigo_opcion = pop.codigo_opcion ORDER BY nombre", nativeQuery = true)
	List<TblOpcion> listarOpcionesPerfil(@Param("perfil") Integer intCodigoPerfil);
}
