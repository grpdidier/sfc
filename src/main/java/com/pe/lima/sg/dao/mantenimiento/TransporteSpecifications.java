package com.pe.lima.sg.dao.mantenimiento;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.mantenimiento.TblTransporte;

public final class TransporteSpecifications {
	
	private TransporteSpecifications() {}

	
	public static Specification<TblTransporte> conNombreTransporte(String strNombre) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNombre);
			return cb.like(root.<String> get("nombre"), valor);
		};
	}
	public static Specification<TblTransporte> conRucTransporte(String strRucTransporte) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strRucTransporte);
			return cb.like(root.<String> get("ruc"), valor);
		};
	}
	public static Specification<TblTransporte> conMarcaTransporte(String strMarcaTransporte) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strMarcaTransporte);
			return cb.like(root.<String> get("marca"), valor);
		};
	}
	public static Specification<TblTransporte> conPlacaTransporte(String strPlacaTransporte) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strPlacaTransporte);
			return cb.like(root.<String> get("placa"), valor);
		};
	}
	
	public static Specification<TblTransporte> conEstadoTransporte(String strEstado) {
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
