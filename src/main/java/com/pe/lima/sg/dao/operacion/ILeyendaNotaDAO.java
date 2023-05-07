package com.pe.lima.sg.dao.operacion;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblLeyendaNota;

public interface ILeyendaNotaDAO extends BaseOperacionDAO<TblLeyendaNota, Integer> {
	
	@Query(value = "select * from ope.tbl_leyenda_nota where codigo_nota = :codigoNota AND estado = '1' ", nativeQuery = true)
	TblLeyendaNota getxNota(@Param("codigoNota") Integer intCodigoNota);
	
}
