package com.pe.lima.sg.dao.operacion;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;
import com.pe.lima.sg.entity.operacion.TblComprobante;

public final class ComprobanteSpecifications {
	
	private ComprobanteSpecifications() {}

	
	public static Specification<TblComprobante> conNumero(String strNumero) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNumero);
			return cb.like(root.<String> get("numero"), valor);
		};
	}
	public static Specification<TblComprobante> conSerie(String strSerie) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strSerie);
			return cb.like(root.<String> get("serie"), valor);
		};
	}
	public static Specification<TblComprobante> conTipoComprobante(String strTipoComprobante) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strTipoComprobante);
			return cb.like(root.<String> get("tipoComprobante"), valor);
		};
	}
	public static Specification<TblComprobante> conNombreCliente(String strNombreCliente) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNombreCliente);
			return cb.like(root.<String> get("nombreCliente"), valor);
		};
	}
	public static Specification<TblComprobante> conEstado(String strEstado) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strEstado);
			return cb.like(root.<String> get("estado"), valor);
		};
	}
	
	public static Specification<TblComprobante> conFechaEmisionAnioMes(String strAnioMes) { // yyyy-MM
		return (root, query, cb) -> {
			String valor = obtenerValorString(strAnioMes);
			return cb.like(root.<String> get("fechaEmision"), valor);
		};
	}
	
	public static Specification<TblComprobante> conCodigoEmpresa(Integer intCodigoEmpresa) {
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
