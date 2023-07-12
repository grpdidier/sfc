package com.pe.lima.sg.presentacion.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
//import org.apache.commons.lang3.StringUtils;
/**
 * Clase Bean que se encarga de la consruccion de las listas desplegables para
 * los combos de seleccion que se utilizan en las vistas
 *
 * @author Gregorio Rodriguez P.
 * @version 20/09/2017
 *
 */
@Service
public class ListaUtilAction {


	/**
	 * Constructor por defecto.
	 */
	public ListaUtilAction() {
	}

	/*Codigo por reemplazar con el de la Session*/
	public void cargarDatosModel(Model model, String nombreListado){
		try{
			if (nombreListado.equals(Constantes.MAP_ESTADO_USUARIO)){
				model.addAttribute("mapEstadoUsuario", obtenerValoresEstadoUsuario());
			}else if(nombreListado.equals(Constantes.MAP_TIPO_CADUCIDAD)){
				model.addAttribute("mapTipoCaducidad", obtenerValoresTipoCaducidad());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_TIENDA)){
				model.addAttribute("mapTipoTienda", obtenerValoresTipoTienda());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_PISO)){
				model.addAttribute("mapTipoPiso", obtenerValoresTipoPiso());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_CONCEPTO)){
				model.addAttribute("mapTipoConcepto", obtenerValoresTipoConcepto());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_PERSONA)){
				model.addAttribute("mapTipoPersona", obtenerValoresTipoPersona());
			}else if (nombreListado.equals(Constantes.MAP_ESTADO_CIVIL)){
				model.addAttribute("mapEstadoCivil", obtenerValoresEstadoCivil());
			}else if (nombreListado.equals(Constantes.MAP_SI_NO)){
				model.addAttribute("mapSiNo", obtenerValoresSINO());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_MONEDA)){
				model.addAttribute("mapTipoMoneda", obtenerValoresMoneda());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_COBRO)){
				model.addAttribute("mapTipoCobro", obtenerValoresTipoCobroAlquiler());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_GARANTIA)){
				model.addAttribute("mapTipoGarantia", obtenerValoresTipoGarantia());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_DOCUMENTO)){
				model.addAttribute("mapTipoDocumento", obtenerValoresTipoDocumento());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_RUBRO)){
				model.addAttribute("mapTipoRubro", obtenerValoresTipoRubro());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_PERIODO_ADELANTO)){
				model.addAttribute("mapTipoPeriodoAdelanto", obtenerValoresTipoPeriodoAdelanto());
			}else if (nombreListado.equals(Constantes.MAP_ESTADO_ASIGNACION)){
				model.addAttribute("mapEstadoAsignacion", obtenerValoresAsignacion());
			}else if (nombreListado.equals(Constantes.MAP_MESES)){
				model.addAttribute("mapMeses", obtenerValoresMeses());
			}else if (nombreListado.equals(Constantes.MAP_TIPO_COBRO_CXC)){
				model.addAttribute("mapTipoCobroCxC", obtenerValoresTipoCobro());
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	/**
	 * Listado de tipos de caducidad
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoCaducidad() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.TIPO_CADUCIDAD_INDEFINIDO, Constantes.TIPO_CADUCIDAD_INDEFINIDO);
		resultados.put(Constantes.TIPO_CADUCIDAD_CADUCA_30, Constantes.TIPO_CADUCIDAD_CADUCA_30);
		return resultados;
	}

	/**
	 * Listado de tipos de caducidad
	 * 
	 */
	public Map<String, Object> obtenerValoresEstadoUsuario() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_ESTADO_USUARIO_ACTIVO, Constantes.ESTADO_USUARIO_ACTIVO);
		resultados.put(Constantes.DESC_ESTADO_USUARIO_INACTIVO, Constantes.ESTADO_USUARIO_INACTIVO);
		return resultados;
	}
	/**
	 * Listado de tipos de Concepto
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoConcepto() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_CONCEPTO_INGRESO, Constantes.TIPO_CONCEPTO_INGRESO);
		resultados.put(Constantes.DESC_TIPO_CONCEPTO_GASTO, Constantes.TIPO_CONCEPTO_GASTO);
		return resultados;
	}
	/**
	 * Listado de Asignado - Pendiente
	 * 
	 */
	public Map<String, Object> obtenerValoresAsignacion() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.ESTADO_ASIGNADO, Constantes.ESTADO_ASIGNADO);
		resultados.put(Constantes.ESTADO_PENDIENTE, Constantes.ESTADO_PENDIENTE);
		return resultados;
	}
	/**
	 * Listado de tipos de tienda
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoTienda() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_TIENDA_TIENDA, Constantes.TIPO_TIENDA_TIENDA);
		resultados.put(Constantes.DESC_TIPO_TIENDA_ALMACEN, Constantes.TIPO_TIENDA_ALMACEN);
		resultados.put(Constantes.DESC_TIPO_TIENDA_BODEGA, Constantes.TIPO_TIENDA_BODEGA);
		return resultados;
	}
	/**
	 * Listado de Meses
	 * 
	 */
	public Map<String, Object> obtenerValoresMeses() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_MES_01, Constantes.MES_01);
		resultados.put(Constantes.DESC_MES_02, Constantes.MES_02);
		resultados.put(Constantes.DESC_MES_03, Constantes.MES_03);
		resultados.put(Constantes.DESC_MES_04, Constantes.MES_04);
		resultados.put(Constantes.DESC_MES_05, Constantes.MES_05);
		resultados.put(Constantes.DESC_MES_06, Constantes.MES_06);
		resultados.put(Constantes.DESC_MES_07, Constantes.MES_07);
		resultados.put(Constantes.DESC_MES_08, Constantes.MES_08);
		resultados.put(Constantes.DESC_MES_09, Constantes.MES_09);
		resultados.put(Constantes.DESC_MES_10, Constantes.MES_10);
		resultados.put(Constantes.DESC_MES_11, Constantes.MES_11);
		resultados.put(Constantes.DESC_MES_12, Constantes.MES_12);
		return resultados;
	}
	
	/**
	 * Listado de tipos de cobro
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoCobro() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_COBRO_ALQUILER, Constantes.TIPO_COBRO_ALQUILER);
		resultados.put(Constantes.DESC_TIPO_COBRO_SERVICIO, Constantes.TIPO_COBRO_SERVICIO);
		resultados.put(Constantes.DESC_TIPO_COBRO_LUZ, Constantes.TIPO_COBRO_LUZ);
		resultados.put(Constantes.DESC_TIPO_COBRO_ARBITRIO, Constantes.TIPO_COBRO_ARBITRIO);
		
		return resultados;
	}
	
	/**
	 * Listado de tipos de persona
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoPersona() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_PERSONA_NATURAL, Constantes.TIPO_PERSONA_NATURAL);
		resultados.put(Constantes.DESC_TIPO_PERSONA_JURIDICA, Constantes.TIPO_PERSONA_JURIDICA);
		return resultados;
	}
	/**
	 * Listado de estado civil
	 * 
	 */
	public Map<String, Object> obtenerValoresEstadoCivil() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_ESTADO_CIVIL_SOLTERO, Constantes.ESTADO_CIVIL_SOLTERO);
		resultados.put(Constantes.DESC_ESTADO_CIVIL_CASADO, Constantes.ESTADO_CIVIL_CASADO);
		resultados.put(Constantes.DESC_ESTADO_CIVIL_VIUDO, Constantes.ESTADO_CIVIL_VIUDO);
		resultados.put(Constantes.DESC_ESTADO_CIVIL_DIVORCIADO, Constantes.ESTADO_CIVIL_DIVORCIADO);
		return resultados;
	}
	/**
	 * Listado de estado tipo de moneda
	 * 
	 */
	public Map<String, Object> obtenerValoresMoneda() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_MONEDA_SOL, Constantes.MONEDA_SOL);
		resultados.put(Constantes.DESC_MONEDA_DOLAR, Constantes.MONEDA_DOLAR);
		return resultados;
	}
	/**
	 * Listado de tipo de cobro
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoCobroAlquiler() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_COBRO_INICIO, Constantes.TIPO_COBRO_INICIO);
		resultados.put(Constantes.DESC_TIPO_COBRO_FIN, Constantes.TIPO_COBRO_FIN);
		resultados.put(Constantes.DESC_TIPO_COBRO_QUINCENA, Constantes.TIPO_COBRO_QUINCENA);
		resultados.put(Constantes.DESC_TIPO_COBRO_FECHA, Constantes.TIPO_COBRO_FECHA);
		return resultados;
	}
	/**
	 * Listado de tipo de garantia
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoGarantia() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_GARANTIA_CON, Constantes.TIPO_GARANTIA_CON);
		resultados.put(Constantes.DESC_TIPO_GARANTIA_SIN, Constantes.TIPO_GARANTIA_SIN);
		resultados.put(Constantes.DESC_TIPO_GARANTIA_LLAVE, Constantes.TIPO_GARANTIA_LLAVE);
		return resultados;
	}
	/**
	 * Listado de tipo de documento
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoDocumento() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_DOC_BOLETA, Constantes.TIPO_DOC_BOLETA);
		resultados.put(Constantes.DESC_TIPO_DOC_FACTURA, Constantes.TIPO_DOC_FACTURA);
		resultados.put(Constantes.DESC_TIPO_DOC_INTERNO, Constantes.TIPO_DOC_INTERNO);
		return resultados;
	}
	/**
	 * Listado de tipo de rubro
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoRubro() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_RUBRO_CONTRATO, Constantes.TIPO_RUBRO_CONTRATO);
		resultados.put(Constantes.DESC_TIPO_RUBRO_SERVICIO, Constantes.TIPO_RUBRO_SERVICIO);
		return resultados;
	}
	
	/**
	 * Listado de tipo de periodo de adelantos
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoPeriodoAdelanto() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_0, Constantes.PERIODO_ADELANTO_0);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_1, Constantes.PERIODO_ADELANTO_1);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_2, Constantes.PERIODO_ADELANTO_2);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_3, Constantes.PERIODO_ADELANTO_3);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_4, Constantes.PERIODO_ADELANTO_4);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_5, Constantes.PERIODO_ADELANTO_5);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_6, Constantes.PERIODO_ADELANTO_6);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_7, Constantes.PERIODO_ADELANTO_7);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_8, Constantes.PERIODO_ADELANTO_8);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_9, Constantes.PERIODO_ADELANTO_9);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_10, Constantes.PERIODO_ADELANTO_10);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_11, Constantes.PERIODO_ADELANTO_11);
		resultados.put(Constantes.DESC_PERIODO_ADELANTO_12, Constantes.PERIODO_ADELANTO_12);
		return resultados;
	}

	/**
	 * Listado de tipos de persona
	 * 
	 */
	public Map<String, Object> obtenerValoresSINO() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_SI, Constantes.TIPO_SI);
		resultados.put(Constantes.DESC_TIPO_NO, Constantes.TIPO_NO);
		return resultados;
	}
	/**
	 * Listado de Piso
	 * 
	 */
	public Map<String, Object> obtenerValoresTipoPiso() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put("SOTANO 1", "SOTANO 1");
		resultados.put("SOTANO 2", "SOTANO 2");
		resultados.put("SOTANO 3", "SOTANO 3");
		resultados.put("PISO 1", "PISO 1");
		resultados.put("PISO 2", "PISO 2");
		resultados.put("PISO 3", "PISO 3");
		resultados.put("PISO 4", "PISO 4");
		resultados.put("PISO 5", "PISO 5");
		resultados.put("PISO 6", "PISO 6");
		resultados.put("PISO 7", "PISO 7");
		resultados.put("PISO 8", "PISO 8");
		resultados.put("PISO 9", "PISO 9");
		resultados.put("PISO 10", "PISO 10");
		return resultados;
	}
	
	/**
	 * Listado de tipos de caducidad
	 * 
	 */
	public static Map<String, Object> obtenerValoresEstadoUsuario2() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_ESTADO_USUARIO_ACTIVO, Constantes.ESTADO_USUARIO_ACTIVO);
		resultados.put(Constantes.DESC_ESTADO_USUARIO_INACTIVO, Constantes.ESTADO_USUARIO_INACTIVO);
		return resultados;
	}
	
	/**
	 * Listado de años
	 * 
	 */
	public  Map<Integer, Object> obtenerAnios() {
		Map<Integer, Object> resultados = new LinkedHashMap<Integer, Object>();
		String strFecha	= null;
		Integer anio = null;
		strFecha = UtilSGT.getFecha("yyyy-MM-dd");
		anio = new Integer(strFecha.substring(0,4));
		for (Integer i=2018; i<=anio; i++){
			resultados.put(i, i);
		}
		return resultados;
	}
	
	/**
	 * Listado de Formas de Pago
	 * 
	 */
	public Map<String, Object> obtenerValoresFormasPago() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put("CREDITO", "Credito");
		resultados.put("CONTADO", "Contado");
		return resultados;
	}
	
	public static String AddSpacio(String dato, Integer longitudDeseada) {
		String resultado = "";
		if (dato!=null) {
			if (dato.length()<longitudDeseada) {
				//resultado = StringUtils.rightPad(dato, longitudDeseada);
				resultado = String.format("%-"+longitudDeseada+"s", dato)+":";
			}
			if (dato.length()==longitudDeseada) {
				resultado = dato+":";
			}
			if (dato.length()>longitudDeseada) {
				resultado = dato.substring(0,longitudDeseada)+":";
			}
		}
        return resultado;
    }
	/**
	 * Listado de tipos de transporte
	 * 
	 */
	public Map<String, Object> obtenerTipoDeTransporte() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put(Constantes.DESC_TIPO_TRANSPORTE_PUBLICO, Constantes.TIPO_TRANSPORTE_PUBLICO);
		resultados.put(Constantes.DESC_TIPO_TRANSPORTE_PRIVADO, Constantes.TIPO_TRANSPORTE_PRIVADO);
		return resultados;
	}
	
	/**
	 * Listado de Formas de Pago
	 * 
	 */
	public Map<String, Object> obtenerValoresUnidadMedida() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		resultados.put("CAJA", "CAJA");
		resultados.put("DISPLAY", "DISPLAY");
		resultados.put("DOCENA", "DOCENA");
		resultados.put("PAQUETE", "PAQUETE");
		resultados.put("UNIDAD", "UNIDAD");
		return resultados;
	}
}
