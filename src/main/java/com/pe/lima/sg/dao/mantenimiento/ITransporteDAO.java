package com.pe.lima.sg.dao.mantenimiento;


import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblTransporte;

public interface ITransporteDAO extends BaseOperacionDAO<TblTransporte, Integer> {
	

	@Query(value = "select * from mae.tbl_transporte where estado = '1' ", nativeQuery = true)
	List<TblTransporte> buscarAllxEstado();
}
