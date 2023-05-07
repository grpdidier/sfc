package com.pe.lima.sg.dao.mantenimiento;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;

public final class EmpresaSpecifications {
	
	private EmpresaSpecifications() {}

	
	public static Specification<TblEmpresa> conNombreComercial(String strNombre) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNombre);
			return cb.like(root.<String> get("nombreComercial"), valor);
		};
	}
	public static Specification<TblEmpresa> conRucEmpresa(String strRuc) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strRuc);
			return cb.like(root.<String> get("ruc"), valor);
		};
	}
	
	
	public static Specification<TblEmpresa> conEstadoEmpresa(String strEstado) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strEstado);
			return cb.like(root.<String> get("estado"), valor);
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
