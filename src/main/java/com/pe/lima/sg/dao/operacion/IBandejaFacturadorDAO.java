package com.pe.lima.sg.dao.operacion;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturador;

public interface IBandejaFacturadorDAO extends BaseOperacionDAO<TblBandejaFacturador, Integer> {
	
	@Query(value = "select * from ope.tbl_bandeja_facturador where codigo_comprobante = :codigoComprobante AND estado = '1' ", nativeQuery = true)
	TblBandejaFacturador buscarOneByComprobante(@Param("codigoComprobante") Integer intCodigoComprobante);
	
	@Query(value = "delete from ope.tbl_bandeja_facturador where codigo_comprobante = :codigoComprobante AND estado = '1' ", nativeQuery = true)
	TblBandejaFacturador deleteByComprobante(@Param("codigoComprobante") Integer intCodigoComprobante);
	
	
	@Query(value = "delete from ope.tbl_bandeja_facturador where numero_ruc = :numeroRuc AND tipo_documento = :tipoDocumento AND numero_documento = :numeroDocumento", nativeQuery = true)
	void eliminarRegistro(@Param("numeroRuc") String strNumeroRuc, @Param("tipoDocumento") String strTipoDocumento, @Param("numeroDocumento") String strNumeroDocumento);
	
}
