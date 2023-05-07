package com.pe.lima.sg.dao.mantenimiento;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;

public interface IEmpresaDAO extends BaseOperacionDAO<TblEmpresa, Integer> {
	
	@Query(value = "select count(1)  from mae.tbl_empresa where ruc = :ruc AND estado = '1' ", nativeQuery = true)
	Integer totalRucEmpresa(@Param("ruc") String strRuc);
	
	@Query(value = "select * from mae.tbl_empresa where estado = '1'  ORDER BY nombre_comercial", nativeQuery = true)
	List<TblEmpresa> listarAllActivos();
	
	@Query(value = "Select * from mae.tbl_empresa where ruc = :ruc AND estado = '1' ", nativeQuery = true)
	TblEmpresa getEmpresaxRUC(@Param("ruc") String strRuc);
	
}
