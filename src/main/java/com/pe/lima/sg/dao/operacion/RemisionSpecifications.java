package com.pe.lima.sg.dao.operacion;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.operacion.TblRemision;

public final class RemisionSpecifications {
	
	private RemisionSpecifications() {}

	
	public static Specification<TblRemision> conNumeroRemision(String strNumero) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNumero);
			return cb.like(root.<String> get("numero"), valor);
		};
	}
	public static Specification<TblRemision> conSerieRemision(String strSerie) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strSerie);
			return cb.like(root.<String> get("serie"), valor);
		};
	}
	public static Specification<TblRemision> conTipoComprobanteRemision(String strTipoComprobante) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strTipoComprobante);
			return cb.like(root.<String> get("tipoComprobante"), valor);
		};
	}
	
	public static Specification<TblRemision> conEstadoRemision(String strEstado) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strEstado);
			return cb.like(root.<String> get("estado"), valor);
		};
	}
	
	
	
	public static Specification<TblRemision> conCodigoEmpresaRemision(Integer intCodigoEmpresa) {
		return (root, query, cb) -> {
			return cb.equal(root.<Integer> get("codigoEntidad"), intCodigoEmpresa);
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
