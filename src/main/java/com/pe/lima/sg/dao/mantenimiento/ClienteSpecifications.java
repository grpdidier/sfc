package com.pe.lima.sg.dao.mantenimiento;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.mantenimiento.TblCliente;
import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;

public final class ClienteSpecifications {
	
	private ClienteSpecifications() {}

	
	public static Specification<TblCliente> conNombreCliente(String strNombre) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNombre);
			return cb.like(root.<String> get("nombre"), valor);
		};
	}
	public static Specification<TblCliente> conNumeroDocumento(String strNumeroDocumento) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNumeroDocumento);
			return cb.like(root.<String> get("numeroDocumento"), valor);
		};
	}
	
	
	public static Specification<TblCliente> conEstadoCliente(String strEstado) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strEstado);
			return cb.like(root.<String> get("estado"), valor);
		};
	}
	
	public static Specification<TblCliente> conCodigoEmpresa(Integer intCodigoEmpresa) {
		return (root, query, cb) -> {
			return cb.equal(root.<TblEmpresa> get("tblEmpresa").<Integer> get("codigoEntidad"), intCodigoEmpresa);
		};
	}
	
	/**
	 * Para la busqueda por Like %
	 * @param String
	 * @return String
	 */
	private static String obtenerValorString(String valor) {
		if (valor == null || valor.isEmpty()) {
			return "%";
		} else {
			return "%" + valor + "%";
		}
	}
}
