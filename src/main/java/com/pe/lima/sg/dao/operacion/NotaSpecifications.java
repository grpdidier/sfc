package com.pe.lima.sg.dao.operacion;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;
import com.pe.lima.sg.entity.operacion.TblNota;

public final class NotaSpecifications {
	
	private NotaSpecifications() {}

	
	public static Specification<TblNota> conNumeroNota(String strNumero) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNumero);
			return cb.like(root.<String> get("numero"), valor);
		};
	}
	public static Specification<TblNota> conSerieNota(String strSerie) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strSerie);
			return cb.like(root.<String> get("serie"), valor);
		};
	}
	public static Specification<TblNota> conTipoComprobanteNota(String strTipoComprobante) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strTipoComprobante);
			return cb.like(root.<String> get("tipoComprobante"), valor);
		};
	}
	
	public static Specification<TblNota> conEstadoNota(String strEstado) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strEstado);
			return cb.like(root.<String> get("estado"), valor);
		};
	}
	
	public static Specification<TblNota> conCodigoEmpresa(Integer intCodigoEmpresa) {
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
