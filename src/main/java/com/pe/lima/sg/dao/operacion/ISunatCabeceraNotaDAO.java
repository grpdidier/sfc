package com.pe.lima.sg.dao.operacion;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblSunatCabeceraNota;

public interface ISunatCabeceraNotaDAO extends BaseOperacionDAO<TblSunatCabeceraNota, Integer> {
	
	@Query(value = "select * from ope.tbl_sunat_cabecera_nota where codigo_nota = :codigoNota AND estado = '1' ", nativeQuery = true)
	TblSunatCabeceraNota findByCodigoDocumento( @Param("codigoNota") Integer intCodigoNota);
	
	
	
}
