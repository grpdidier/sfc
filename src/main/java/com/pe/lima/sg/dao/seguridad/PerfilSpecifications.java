package com.pe.lima.sg.dao.seguridad;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.seguridad.TblPerfil;

public final class PerfilSpecifications {
	
	private PerfilSpecifications() {}

	
	public static Specification<TblPerfil> conNombre(String strNombre) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNombre);
			return cb.like(root.<String> get("nombre"), valor);
		};
	}
	
	public static Specification<TblPerfil> conEstadoPerfil(String strEstadoPerfil) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strEstadoPerfil);
			return cb.like(root.<String> get("estadoPerfil"), valor);
		};
	}
	
	public static Specification<TblPerfil> conEstado(String strEstado) {
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
