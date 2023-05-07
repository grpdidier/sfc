package com.pe.lima.sg.dao.operacion;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblFormaPago;

public interface IFormaPagoDAO extends BaseOperacionDAO<TblFormaPago, Integer> {
	
	@Query(value = "select * from ope.tbl_forma_pago where codigo_comprobante = :codigoComprobante AND estado = '1' ", nativeQuery = true)
	TblFormaPago obtenerFormaPago(@Param("codigoComprobante") Integer intCodigoComprobante);
	
	
	
}
