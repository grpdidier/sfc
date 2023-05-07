package com.pe.lima.sg.dao.operacion;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;
import com.pe.lima.sg.entity.operacion.TblConfiguracion;

public final class ConfiguracionSpecifications {
	
	private ConfiguracionSpecifications() {}

	
	
	public static Specification<TblConfiguracion> conEstado(String strEstado) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strEstado);
			return cb.like(root.<String> get("estado"), valor);
		};
	}
	
	public static Specification<TblConfiguracion> conCodigoEmpresa(Integer intCodigoEmpresa) {
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
