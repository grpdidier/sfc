package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblDetalleRemision;

public interface IDetalleRemisionDAO extends BaseOperacionDAO<TblDetalleRemision, Integer> {
	@Query(value = "select * from ope.tbl_detalle_remision dc where  codigo_factura_asociada = :codigoFacturaAsociada", nativeQuery = true)
	List<TblDetalleRemision> findAllxIdFacturaAsociada(@Param("codigoFacturaAsociada") Integer intCodigoFacturaAsociada);
	
	@Query(value = "select dc.* from ope.tbl_detalle_remision dc, ope.tbl_factura_asociada fa where codigo_remision = :codigoRemision and dc.codigo_factura_asociada = fa.codigo_factura_asociada order by 1 desc", nativeQuery = true)
	List<TblDetalleRemision> findAllxIdRemision(@Param("codigoRemision") Integer intCodigoRemision);
	
}
