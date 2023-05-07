package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblDetalleFormaPago;

public interface IDetalleFormaPagoDAO extends BaseOperacionDAO<TblDetalleFormaPago, Integer> {
	
	@Query(value = "select * from ope.tbl_detalle_forma_pago where codigo_forma = :codigoFormaPago AND estado = '1' ", nativeQuery = true)
	List<TblDetalleFormaPago> listarDetalleFormaPago(@Param("codigoFormaPago") Integer intCodigoFormaPago);
}
