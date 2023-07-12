package com.pe.lima.sg.dao.mantenimiento;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.entity.mantenimiento.TblTransporte;

public interface ITransporteDAO extends BaseOperacionDAO<TblTransporte, Integer> {
	

	@Query(value = "select * from mae.tbl_transporte where estado = '1' ", nativeQuery = true)
	List<TblTransporte> buscarAllxEstado();
	
	@Query(value = "select * from mae.tbl_transporte where estado = '1' and ruc like :ruc and UPPER(nombre) like :nombre and UPPER(marca) like :marca and UPPER(placa) like :placa order by nombre, marca, placa", nativeQuery = true)
	List<TblTransporte> buscarTransportexCriterios(@Param("ruc") String strRuc, @Param("nombre") String strNombre, @Param("marca") String strMarca, @Param("placa") String strPlaca);
		
	@Query(value = "select count(1)  from mae.tbl_transporte where ruc = :ruc AND marca = :marca AND placa = :placa AND numero_cert_inscripcion = :nroInscripcion AND numero_licencia = :nroLicencia AND estado = '1' ", nativeQuery = true)
	Integer totalDatosTransporte(@Param("ruc") String strRuc, @Param("marca") String strMarca, @Param("placa") String strPlaca, @Param("nroInscripcion") String strNroInscripcion, @Param("nroLicencia") String strNroLicencia );
	
}
