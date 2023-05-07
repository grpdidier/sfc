package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblSunatTributoGeneralNota;

public interface ISunatTributoGeneralNotaDAO extends BaseOperacionDAO<TblSunatTributoGeneralNota, Integer> {
	
	@Query(value = "select * from ope.tbl_sunat_tributo_general_nota where codigo_cabecera = :codigoCabecera AND estado = '1' ", nativeQuery = true)
	List<TblSunatTributoGeneralNota> findByCodigoCabecera(@Param("codigoCabecera") int intCodigoCabecera);
}
