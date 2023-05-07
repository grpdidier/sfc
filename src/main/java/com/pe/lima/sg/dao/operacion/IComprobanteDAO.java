package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblComprobante;

public interface IComprobanteDAO extends BaseOperacionDAO<TblComprobante, Integer> {
	
	@Query(value = "select * from ope.tbl_comprobante where codigo_verificacion = :codigoVerificacion AND estado = '1' ", nativeQuery = true)
	TblComprobante obtenerComprobante(@Param("codigoVerificacion") String strCodigoVerificacion);
	
	@Query(value = "select count(1)  from ope.tbl_comprobante where codigo_entidad = :codigoEmpresa AND tipo_comprobante = :tipoComprobante AND serie = :serie AND numero = :numero AND estado = '1' AND estado_operacion in ('02','03','04')", nativeQuery = true)
	Integer totalComprobante(@Param("tipoComprobante") String strTipoComprobante, @Param("serie") String strSerie, @Param("numero") String strNumero, @Param("codigoEmpresa") Integer intCodigoEmpresa);
	
	@Query(value = "select * from ope.tbl_comprobante where codigo_entidad = :codigoEmpresa AND fecha_emision like :anioMes   AND estado = '1' AND estado_operacion in ('07','03','04')", nativeQuery = true)
	List<TblComprobante> findAllxAnioMes(@Param("anioMes") String strAnioMes, @Param("codigoEmpresa") Integer intCodigoEmpresa);
	
	@Query(value = "select * from ope.tbl_comprobante where codigo_entidad = :codigoEmpresa AND estado = '1' AND estado_operacion in ('00','01','02')", nativeQuery = true)
	List<TblComprobante> findAllxEstado(@Param("codigoEmpresa") Integer intCodigoEmpresa);
	
}
