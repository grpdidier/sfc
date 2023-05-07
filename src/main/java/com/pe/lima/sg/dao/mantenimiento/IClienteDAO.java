package com.pe.lima.sg.dao.mantenimiento;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblCliente;

public interface IClienteDAO extends BaseOperacionDAO<TblCliente, Integer> {
	
	@Query(value = "select count(1)  from mae.tbl_cliente where numero_documento = :numeroDocumento AND estado = '1' ", nativeQuery = true)
	Integer totalNumeroDocumento(@Param("numeroDocumento") String strNumeroDocumento);

	@Query(value = "select count(1)  from mae.tbl_cliente where numero_documento = :numeroDocumento AND estado = '1'  and codigo_entidad = :codigoEmpresa", nativeQuery = true)
	Integer totalNumeroDocumentoxEmpresa(@Param("numeroDocumento") String strNumeroDocumento, @Param("codigoEmpresa") Integer intCodigoEmpresa);

}
