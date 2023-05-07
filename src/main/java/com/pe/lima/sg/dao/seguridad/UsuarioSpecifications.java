package com.pe.lima.sg.dao.seguridad;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.seguridad.TblUsuario;

public final class UsuarioSpecifications {
	
	private UsuarioSpecifications() {}

	
	public static Specification<TblUsuario> conNombre(String strNombre) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNombre);
			return cb.like(root.<String> get("nombre"), valor);
		};
	}
	public static Specification<TblUsuario> conLogin(String strLogin) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strLogin);
			return cb.like(root.<String> get("login"), valor);
		};
	}
	public static Specification<TblUsuario> conEstadoUsuario(String strEstadoUsuario) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strEstadoUsuario);
			return cb.like(root.<String> get("estadoUsuario"), valor);
		};
	}
	
	public static Specification<TblUsuario> conEstado(String strEstado) {
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
