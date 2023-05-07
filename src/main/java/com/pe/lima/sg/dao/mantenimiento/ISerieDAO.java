package com.pe.lima.sg.dao.mantenimiento;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblSerie;

public interface ISerieDAO extends BaseOperacionDAO<TblSerie, Integer> {
	
	@Query(value = "select * from mae.tbl_serie where tipo_comprobante = :tipoComprobante AND estado = '1' ", nativeQuery = true)
	TblSerie buscarOneByTipoComprobante(@Param("tipoComprobante") String strTipoComprobante);
	
	@Query(value = "select * from mae.tbl_serie where codigo_entidad = :codigoEntidad AND ( tipo_comprobante = :tipoComprobante or :tipoComprobante = '') AND ( UPPER(prefijo_serie) LIKE '%'||UPPER(:serie)||'%' OR :serie = '')  AND ( secuencial_serie = :numero or :numero = 0) AND estado = '1'  ORDER BY tipo_comprobante, prefijo_serie", nativeQuery = true)
	List<TblSerie> listarCriterios(	
										@Param("tipoComprobante") String strTipoComprobante,
										@Param("serie") String strSerie,
										@Param("numero") Integer intNumero,
										@Param("codigoEntidad") Integer intCodigoEntidad
									  );
	
	@Query(value = "select count(1) from mae.tbl_serie where codigo_entidad = :codigoEntidad AND tipo_comprobante = :tipoComprobante AND UPPER(prefijo_serie) LIKE '%'||UPPER(:serie)||'%' AND estado = '1' ", nativeQuery = true)
	Integer countOneByNombre(@Param("tipoComprobante") String strTipoComprobante,
							 @Param("serie") String strSerie,
							 @Param("codigoEntidad") Integer intCodigoEntidad);
	
	@Query(value = "select * from mae.tbl_serie where codigo_entidad = :codigoEntidad AND tipo_comprobante = :tipoComprobante AND UPPER(prefijo_serie) LIKE '%'||UPPER(:serie)||'%' AND estado = '1' ", nativeQuery = true)
	List<TblSerie> buscarOneByNombre(@Param("tipoComprobante") String strTipoComprobante,
									 @Param("serie") String strSerie,
									 @Param("codigoEntidad") Integer intCodigoEntidad);
	
	@Query(value = "select * from mae.tbl_serie where codigo_entidad = :codigoEntidad AND estado = '1'  ORDER BY tipo_comprobante, prefijo_serie", nativeQuery = true)
	List<TblSerie> listarAllActivos(@Param("codigoEntidad") Integer intCodigoEntidad);
	

	@Query(value = "select * from mae.tbl_serie where codigo_entidad = :codigoEntidad AND tipo_comprobante = :tipoComprobante AND  estado = '1' ", nativeQuery = true)
	List<TblSerie> buscarAllxTipo   (@Param("tipoComprobante") String strTipoComprobante,
									 @Param("codigoEntidad") Integer intCodigoEntidad);
}
