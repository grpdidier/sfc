package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblFacturaAsociada;

public interface IFacturaAsociadaDAO extends BaseOperacionDAO<TblFacturaAsociada, Integer> {
	
	@Query(value = "select * from ope.tbl_factura_asociada  where  codigo_remision = :codigoRemision", nativeQuery = true)
	List<TblFacturaAsociada> findAllxIdRemision(@Param("codigoRemision") Integer intCodigoRemision);
}
