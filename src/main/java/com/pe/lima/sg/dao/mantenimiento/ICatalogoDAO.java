package com.pe.lima.sg.dao.mantenimiento;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
public interface ICatalogoDAO extends BaseOperacionDAO<TblCatalogo, Integer> {
	
	@Query(value = "select * from mae.tbl_catalogo where estado = '1' ", nativeQuery = true)
	List<TblCatalogo> listarAllActivos();
	
	@Query(value = "select * from mae.tbl_catalogo where codigo_sunat = :codigoSunat AND codigo_tipo_catalogo = :codigoTipoCatalogo AND estado = '1' ", nativeQuery = true)
	TblCatalogo getCatalogoxCodigoSunatxTipo(@Param("codigoSunat") String strCodigoSunat, @Param("codigoTipoCatalogo") Integer intCodigoTipoCatalogo);
}
