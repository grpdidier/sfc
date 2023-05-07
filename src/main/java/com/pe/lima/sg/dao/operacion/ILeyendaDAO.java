package com.pe.lima.sg.dao.operacion;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblLeyenda;

public interface ILeyendaDAO extends BaseOperacionDAO<TblLeyenda, Integer> {
	
	@Query(value = "select * from ope.tbl_leyenda where codigo_comprobante = :codigoComprobante AND estado = '1' ", nativeQuery = true)
	TblLeyenda getxComprobante(@Param("codigoComprobante") Integer intCodigoComprobante);
	
}
