package com.pe.lima.sg.dao.mantenimiento;


import org.springframework.data.jpa.domain.Specification;

import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;

public final class ProductoSpecifications {
	
	private ProductoSpecifications() {}

	
	public static Specification<TblProducto> conNombreProducto(String strNombre) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strNombre);
			return cb.like(root.<String> get("nombre"), valor);
		};
	}
	public static Specification<TblProducto> conCodigoProducto(String strCodigoProducto) {
		return (root, query, cb) -> {
			String valor = obtenerValorString(strCodigoProducto);
			return cb.like(root.<String> get("codigoEmpresa"), valor);
		};
	}
	
	public static Specification<TblProducto> conCodigoEmpresa(Integer intCodigoEmpresa) {
		return (root, query, cb) -> {
			return cb.equal(root.<TblEmpresa> get("tblEmpresa").<Integer> get("codigoEntidad"), intCodigoEmpresa);
		};
	}
	
	
	public static Specification<TblProducto> conEstadoProducto(String strEstado) {
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
