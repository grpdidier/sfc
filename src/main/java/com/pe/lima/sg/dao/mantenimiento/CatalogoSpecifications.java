package com.pe.lima.sg.dao.mantenimiento;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
import com.pe.lima.sg.entity.mantenimiento.TblTipoCatalogo;

public final class CatalogoSpecifications {
	
	private CatalogoSpecifications() {}

	
	public static Specification<TblCatalogo> conCodigoSunat(String strCodigoSunat) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strCodigoSunat);
			return cb.like(root.<String> get("codigoSunat"), valor);
		};
	}
	public static Specification<TblCatalogo> conCodigoTipoCatalogo(Integer intCodigoTipoCatalogo) {
		return (root, query, cb) -> {
			return cb.equal(root.<TblTipoCatalogo>get("tblTipoCatalogo").<Integer> get("codigoTipoCatalogo"), intCodigoTipoCatalogo);
		};
	}
	
	
	public static Specification<TblCatalogo> conEstadoCatalogo(String strEstado) {
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
