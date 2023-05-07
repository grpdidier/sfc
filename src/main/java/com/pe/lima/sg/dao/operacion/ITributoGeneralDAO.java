package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblTributoGeneral;

public interface ITributoGeneralDAO extends BaseOperacionDAO<TblTributoGeneral, Integer> {
	
	@Query(value = "select * from ope.tbl_tributo_general where codigo_comprobante = :codigoComprobante AND estado = '1' ", nativeQuery = true)
	List<TblTributoGeneral> listarxComprobante(@Param("codigoComprobante") Integer intCodigoComprobante);
	
}
