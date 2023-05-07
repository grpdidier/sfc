package com.pe.lima.sg.dao.operacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblSunatDetalleNota;

public interface ISunatDetalleNotaDAO extends BaseOperacionDAO<TblSunatDetalleNota, Integer> {
	
	@Query(value = "select * from ope.tbl_sunat_detalle_nota where codigo_cabecera = :codigoCabecera AND estado = '1' ", nativeQuery = true)
	List<TblSunatDetalleNota> findByCodigoCabecera( @Param("codigoCabecera") Integer intCodigoCabecera);
	
}
