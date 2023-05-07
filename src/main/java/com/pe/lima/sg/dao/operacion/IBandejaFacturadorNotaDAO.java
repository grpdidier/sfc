package com.pe.lima.sg.dao.operacion;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturadorNota;

public interface IBandejaFacturadorNotaDAO extends BaseOperacionDAO<TblBandejaFacturadorNota, Integer> {
	
	@Query(value = "select * from ope.tbl_bandeja_facturador_nota where codigo_nota = :codigoNota AND estado = '1' ", nativeQuery = true)
	TblBandejaFacturadorNota buscarOneByNota(@Param("codigoNota") Integer intCodigoNota);
	
	@Query(value = "delete from ope.tbl_bandeja_facturador_nota where codigo_nota = :codigoNota AND estado = '1' ", nativeQuery = true)
	TblBandejaFacturadorNota deleteByComprobante(@Param("codigoNota") Integer intCodigoNota);
	
	
	@Query(value = "delete from ope.tbl_bandeja_facturador_nota where numero_ruc = :numeroRuc AND tipo_documento = :tipoDocumento AND numero_documento = :numeroDocumento", nativeQuery = true)
	void eliminarRegistro(@Param("numeroRuc") String strNumeroRuc, @Param("tipoDocumento") String strTipoDocumento, @Param("numeroDocumento") String strNumeroDocumento);
	
}
