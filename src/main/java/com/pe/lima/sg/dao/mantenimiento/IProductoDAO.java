package com.pe.lima.sg.dao.mantenimiento;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;

public interface IProductoDAO extends BaseOperacionDAO<TblProducto, Integer> {
	
	@Query(value = "select count(1)  from mae.tbl_producto where nombre = :nombre AND estado = '1' ", nativeQuery = true)
	Integer totalNombreProducto(@Param("nombre") String strNombre);
	
	@Query(value = "select * from mae.tbl_producto where codigo_entidad = :codigoEntidad  AND estado = '1'  ORDER BY nombre", nativeQuery = true)
	List<TblProducto> listarAllActivos(@Param("codigoEntidad") Integer intCodigoEntidad);
	
	@Query(value = "select * from mae.tbl_producto where codigo_entidad = :codigoEntidad  and codigo_empresa = :codigoProducto AND estado = '1'  ORDER BY nombre", nativeQuery = true)
	List<TblProducto> listarxCodigoProducto(@Param("codigoEntidad") Integer intCodigoEntidad, @Param("codigoProducto") String strCodigoProducto);
	
}
