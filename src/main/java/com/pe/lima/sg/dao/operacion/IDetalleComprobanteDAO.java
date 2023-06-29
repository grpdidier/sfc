package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;

public interface IDetalleComprobanteDAO extends BaseOperacionDAO<TblDetalleComprobante, Integer> {
	
	@Query(value = "select * from ope.tbl_detalle_comprobante where codigo_comprobante = :codigoComprobante AND estado = '1' and (cantidad - COALESCE(cantidad_guia, 0)) > 0 order by 1", nativeQuery = true)
	List<TblDetalleComprobante> listarxComprobante(@Param("codigoComprobante") Integer intCodigoComprobante);
	
	@Query(value = "select dc.* from ope.tbl_detalle_comprobante dc, ope.tbl_comprobante co where  co.codigo_entidad = :codigoEmpresa AND co.codigo_comprobante = dc.codigo_comprobante AND co.fecha_emision >= :fechaInicio AND co.fecha_emision <= :fechaFin AND co.estado = '1' AND co.estado_operacion in ('03','04')", nativeQuery = true)
	List<TblDetalleComprobante> findAllxRangoFecha(@Param("fechaInicio") String strFechaInicio, @Param("fechaFin") String strFechaFin, @Param("codigoEmpresa") Integer intCodigoEmpresa);
	
	@Query(value = "select * from ope.tbl_detalle_comprobante where codigo_comprobante = :codigoComprobante AND estado = '1' order by 1", nativeQuery = true)
	List<TblDetalleComprobante> listarxComprobanteTodos(@Param("codigoComprobante") Integer intCodigoComprobante);
	
}
