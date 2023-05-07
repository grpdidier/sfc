package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblTributoGeneralNota;

public interface ITributoGeneralNotaDAO extends BaseOperacionDAO<TblTributoGeneralNota, Integer> {
	
	@Query(value = "select * from ope.tbl_tributo_general_nota where codigo_nota = :codigoNota AND estado = '1' ", nativeQuery = true)
	List<TblTributoGeneralNota> listarxNota(@Param("codigoNota") Integer intCodigoNota);
	
}
