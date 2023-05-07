package com.pe.lima.sg.presentacion.util;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblTributoGeneral;
import com.pe.lima.sg.entity.operacion.TblTributoGeneralNota;


public class UtilSGT {
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilSGT.class);
	/**
	 * Adiciona a una fecha Date un numero de dias
	 */
	public static Date addDays(Date date, int days)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); //minus number would decrement the days
		return cal.getTime();
	}
	/**
	 * Obtiene una fecha en formato String
	 */
	public static String getDateStringFormat(Date date){
		SimpleDateFormat dt1 = null;
		try{
			dt1 = new SimpleDateFormat("yyyy-MM-dd");

		}catch(Exception e){
			e.printStackTrace();
		}

		return  dt1.format(date);
	}
	/**
	 * Obtiene una fecha en formato String
	 */
	public static String getDateStringFormatYYYMMDD(Date date){
		SimpleDateFormat dt1 = null;
		try{
			dt1 = new SimpleDateFormat("yyyyMMdd");

		}catch(Exception e){
			e.printStackTrace();
		}

		return  dt1.format(date);
	}
	/**
	 * 
	 * Obtiene los dias de diferencia
	 */
	public static Integer getDiasFechas(String strFechaInicio, String strFechaFin){
		SimpleDateFormat dateFormat = null;
		int dias=0;
		try{
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Date fechaInicial=dateFormat.parse(strFechaInicio);
			Date fechaFinal=dateFormat.parse(strFechaFin);

			dias=(int) ((fechaFinal.getTime()-fechaInicial.getTime())/86400000)+1;
		}catch(Exception e){
			dias = 0;
		}
		return dias;
	}
	/**
	 * Obtiene una fecha de tipo Date
	 */
	public static Date getDateFormat(Date date){
		Date date1 = null;
		try{
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
			date1 = dt1.parse( dt1.format(date));
		}catch(Exception e){
			e.printStackTrace();
		}

		return date1;
	}
	/**
	 * Obtiene una fecha Date a partir de una cadena
	 *
	 */
	public static Date getDatetoString(String strFecha){
		DateFormat formatter ; 
		Date date = null; 
		try{
			formatter = new SimpleDateFormat("dd/MM/yyyy");
			date = formatter.parse(strFecha);
		}catch(Exception e){
			e.printStackTrace();
		}
		return date;


	}
	/**
	 * Obtiene la informacion de el fecha.
	 *
	 */
	public static String getFecha(String formato) {    	
		SimpleDateFormat FMT    = new SimpleDateFormat( formato );
		/* yyyy       = ano actual
		 * MM         = mes en dos digitos
		 * MMM        = Mes en espanol Ej: ene
		 * WW         = Semana del mes Ej: 02
		 * yyyy-MM-dd = Ano, Mes dia separados por '-' Ej: 2004-10-09
		 * dd         = Dia en 2 digitos Ej: 02
		 * yyyy/MM/dd = Ano, Mes dia separados por '/' Ej: 2004/10/09
		 * yyyyMMdd   = Ano, mes, dia sin separacion
		 * yyyy/MM/dd kk:mm:ss
		 * yyyy-MM-dd kk:mm:ss
		 */        
		String           Fecha  = FMT.format(new Date());        
		return Fecha.toUpperCase();
	}
	/**
	 * Completa con ceros la cadena, el numero de ceros se calcula con el tamaño
	 */
	public static String completarCeros(String cadena, Integer tamano){

		int intTotal = 0;
		LOGGER.debug("cadena :"+cadena + ", tamano :"+tamano);
		try{
			intTotal = tamano - cadena.length();
			LOGGER.debug("intTotal :"+intTotal);
			if (intTotal >0){
				for(int i=0; i< intTotal; i++){
					cadena = Constantes.CADENA_CERO + cadena;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return cadena;
	}
	/**
	 * Obtiene el año de un DATE
	 *
	 */
	public static Integer getAnioDate(Date datFecha){
		Integer anio = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(datFecha);
		anio = cal.get(Calendar.YEAR);
		return anio;
	}
	/**
	 * Obtiene el mes de una fecha
	 * 
	 */
	public static Integer getMes(String strFecha){
		Integer mes = null;
		if (strFecha!=null){
			mes = new Integer (strFecha.substring(4, 5)) - 1 ;
		}else{
			mes = Calendar.getInstance().get(Calendar.MONTH) ;
		}
		LOGGER.debug("[getMes] mes:"+mes);
		return mes;
	}
	/**
	 * Listado de años (año inicio hasta año fin)
	 * 
	 */
	public static Map<String, Object> getListaAnio(Integer intAnioInicio, Integer intAnioFin) {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		try{
			LOGGER.debug("[getListaAnio] inicio");
			for(Integer intAnio=intAnioInicio; intAnio <= intAnioFin ; intAnio++){
				resultados.put(intAnio.toString(), intAnio.toString());
			}

			LOGGER.debug("[getListaAnio] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}

		return resultados;
	}
	/*
	 * Fecha Inicio Primer Trimestre
	 */
	public static Date getPrimerTrimestreInicio(Integer anio){
		String strFecha = "01/01/" + anio;
		return getDatetoString(strFecha);
	}
	/*
	 * Fecha Fin Primer Trimestre
	 */
	public static Date getPrimerTrimestreFin(Integer anio){
		String strFecha = "31/03/" + anio;
		return getDatetoString(strFecha);
	}
	/*
	 * Fecha Fin Segundo Trimestre
	 */
	public static Date getSegundoTrimestreFin(Integer anio){
		String strFecha = "30/06/" + anio;
		return getDatetoString(strFecha);
	}
	/*
	 * Fecha Inicio Tercer Trimestre
	 */
	public static Date getTercerTrimestreInicio(Integer anio){
		String strFecha = "01/07/" + anio;
		return getDatetoString(strFecha);
	}
	/*
	 * Fecha Inicio Segundo Trimestre
	 */
	public static Date getSegundoTrimestreInicio(Integer anio){
		String strFecha = "01/04/" + anio;
		return getDatetoString(strFecha);
	}
	/*
	 * Fecha Fin Tercer Trimestre
	 */
	public static Date getTercerTrimestreFin(Integer anio){
		String strFecha = "30/09/" + anio;
		return getDatetoString(strFecha);
	}
	/*
	 * Fecha Inicio Cuarto Trimestre
	 */
	public static Date getCuartoTrimestreInicio(Integer anio){
		String strFecha = "01/10/" + anio;
		return getDatetoString(strFecha);
	}
	/*
	 * Fecha Fin Cuarto Trimestre
	 */
	public static Date getCuartoTrimestreFin(Integer anio){
		String strFecha = "31/12/" + anio;
		return getDatetoString(strFecha);
	}
	/*
	 * Obtiene la fecha del ultimo dia del mes, la fecha se pasa como parametro
	 */
	public static String getLastDate(String strFecha){
		Integer intMes = null;
		Integer intAnio	= null;
		String resultado = null;
		LOGGER.debug("[getLastDate] Inicio - strFecha:"+strFecha);
		if (strFecha!=null && !strFecha.equals("") && strFecha.length()==10){

			intMes = new Integer(strFecha.substring(5, 7))-1;
			intAnio = new Integer(strFecha.substring(0, 4));
			if (intMes == -1){
				intMes = 11;
				intAnio= intAnio -1;
			}

			LOGGER.debug("[getLastDate] intMes:"+intMes+" - intAnio:"+intAnio);
			if (intMes.compareTo(0)==0){
				resultado = "31/01/"+intAnio.toString();
			}else if (intMes.compareTo(1)==0){
				if(intAnio % 4 == 0){
					resultado = "29/02/"+intAnio.toString();
				}else{
					resultado = "28/02/"+intAnio.toString();
				}
			}else if(intMes.compareTo(2)==0){
				resultado = "31/03/"+intAnio.toString();
			}else if  (intMes.compareTo(3)==0){
				resultado = "30/04/"+intAnio.toString();
			}else if (intMes.compareTo(4)==0){
				resultado = "31/05/"+intAnio.toString();
			}else if (intMes.compareTo(5)==0){
				resultado = "30/06/"+intAnio.toString();
			}else if (intMes.compareTo(6)==0){
				resultado = "31/07/"+intAnio.toString();
			}else if (intMes.compareTo(7)==0){
				resultado = "31/08/"+intAnio.toString();
			}else if (intMes.compareTo(8)==0){
				resultado = "30/09/"+intAnio.toString();
			}else if (intMes.compareTo(9)==0){
				resultado = "31/10/"+intAnio.toString();
			}else if (intMes.compareTo(10)==0){
				resultado = "30/11/"+intAnio.toString();
			}else if (intMes.compareTo(11)==0){
				resultado = "31/12/"+intAnio.toString();
			}else {
				resultado = "Seleccionar";
			}

		}

		LOGGER.debug("[getLastDate] Fin - resultado:"+resultado);
		return resultado;
	}
	/*
	 * Obtiene el ultimo día del mes
	 */
	public static String getLastDay(Integer intMes, Integer intAnio){
		String resultado = null;
		if (intMes.compareTo(1)==0){
			resultado = "31/01/"+intAnio.toString();
		}else if (intMes.compareTo(2)==0){
			if(intAnio % 4 == 0){
				resultado = "29/02/"+intAnio.toString();
			}else{
				resultado = "28/02/"+intAnio.toString();
			}
		}else if(intMes.compareTo(3)==0){
			resultado = "31/03/"+intAnio.toString();
		}else if  (intMes.compareTo(4)==0){
			resultado = "30/04/"+intAnio.toString();
		}else if (intMes.compareTo(5)==0){
			resultado = "31/05/"+intAnio.toString();
		}else if (intMes.compareTo(6)==0){
			resultado = "30/06/"+intAnio.toString();
		}else if (intMes.compareTo(7)==0){
			resultado = "31/07/"+intAnio.toString();
		}else if (intMes.compareTo(8)==0){
			resultado = "31/08/"+intAnio.toString();
		}else if (intMes.compareTo(9)==0){
			resultado = "30/09/"+intAnio.toString();
		}else if (intMes.compareTo(10)==0){
			resultado = "31/10/"+intAnio.toString();
		}else if (intMes.compareTo(11)==0){
			resultado = "30/11/"+intAnio.toString();
		}else if (intMes.compareTo(12)==0){
			resultado = "31/12/"+intAnio.toString();
		}else {
			resultado = "Seleccionar";
		}
		return resultado;
	}

	/*
	 * Obtiene el primer día del mes
	 */
	public static String getFistDay(String strMes, Integer intAnio){
		String resultado = null;
		resultado = "01/"+strMes+"/"+intAnio;
		return resultado;
	}
	public static boolean validarFinMes(Date fecha){
		String strFecha = null;
		String strMes	= null;
		String strDia	= null;
		String strAnio	= null;
		String strDiaAux= null;
		boolean resultado = false;
		try{
			strFecha = getDateStringFormat(fecha);
			strAnio = strFecha.substring(0,3);
			strMes = strFecha.substring(5, 6);
			strDia = strFecha.substring(8);
			LOGGER.debug("[validarFinMes] Mes:"+strMes+ " Dia:"+strDia);
			strDiaAux = getOnlyLastDay(new Integer(strMes), new Integer(strAnio));
			LOGGER.debug("[validarFinMes] Last Day:"+strDiaAux);
			if (strDiaAux.equals(strDia)){
				resultado = true;
			}
		}catch(Exception e){

		}
		return resultado;
	}
	/*
	 * Obtiene el ultimo día del mes
	 */
	public static String getOnlyLastDay(Integer intMes, Integer intAnio){
		String resultado = null;
		if (intMes.compareTo(0)==0){
			resultado = "31";
		}else if (intMes.compareTo(1)==0){
			if(intAnio % 4 == 0){
				resultado = "29";
			}else{
				resultado = "28";
			}
		}else if(intMes.compareTo(2)==0){
			resultado = "31";
		}else if  (intMes.compareTo(3)==0){
			resultado = "30";
		}else if (intMes.compareTo(4)==0){
			resultado = "31";
		}else if (intMes.compareTo(5)==0){
			resultado = "30";
		}else if (intMes.compareTo(6)==0){
			resultado = "31";
		}else if (intMes.compareTo(7)==0){
			resultado = "31";
		}else if (intMes.compareTo(8)==0){
			resultado = "30";
		}else if (intMes.compareTo(9)==0){
			resultado = "31";
		}else if (intMes.compareTo(10)==0){
			resultado = "30";
		}else if (intMes.compareTo(11)==0){
			resultado = "31";
		}else {
			resultado = "Seleccionar";
		}
		return resultado;
	}

	public static Date getDatePrevious(Date fecha){
		Date fechaPrevious = null;
		String strFecha = null;
		String strMes	= null;
		String strAnio	= null;
		String strFechaPrevious = null;
		Integer intAnio	= null;
		Integer intMes	= null;

		try{
			strFecha = getDateStringFormat(fecha);
			strAnio = strFecha.substring(0,4);
			strMes = strFecha.substring(5, 7);
			intMes = new Integer(strMes)-1;
			intAnio = new Integer(strAnio);
			if (intMes < 0){
				intMes = 12;
				intAnio = intAnio -1;

			}
			strFechaPrevious = getLastDay(intMes, intAnio);
			fechaPrevious = getDatetoString(strFechaPrevious);


		}catch(Exception e){
			e.printStackTrace();
		}finally{
			strFecha = null;
			strMes	= null;
			strAnio	= null;
			strFechaPrevious = null;
			intAnio	= null;
			intMes	= null;
		}
		return fechaPrevious;
	}

	public static Double obtenerFactorPrimerCobro(Date Fecha){
		Double dblFactor		= null;
		String strFecha			= "";
		String strDia			= null;
		String strMes			= null;
		String strAnio			= null;
		String strDiaFin		= null;
		Integer intMes			= null;
		Integer intAnio			= null;
		try{
			LOGGER.debug("[obtenerFactorPrimerCobro] Inicio");

			strFecha = getDateStringFormat(Fecha);
			strDia = strFecha.substring(8, 10);
			strMes = strFecha.substring(5, 7);
			strAnio = strFecha.substring(0, 4);
			LOGGER.debug("[obtenerFactorPrimerCobro] strFecha:"+strFecha+" strDia:"+strDia+" strMes:"+strMes+" strAnio:"+strAnio);
			intMes = new Integer(strMes)-1;
			intAnio = new Integer(strAnio); 
			if (intMes.compareTo(-1)==0){
				intMes = 11;
				intAnio = intAnio -1;

			}
			strDiaFin = getOnlyLastDay(intMes, intAnio);
			LOGGER.debug("[obtenerFactorPrimerCobro] intMes:"+intMes+" intAnio:"+intAnio);
			LOGGER.debug("[obtenerFactorPrimerCobro] strDiaFin:"+strDiaFin);
			dblFactor = (new Double(strDiaFin) - new Double(strDia) + 1 ) / new Double(strDiaFin);

			LOGGER.debug("[obtenerFactorPrimerCobro] Fin - intTotalDias:"+dblFactor);
		}catch(Exception e){
			e.printStackTrace();

		}
		return dblFactor;
	}

	/*
	 * Obtiene el mes en curso
	 */
	public static String getMoth(){
		String strMes = null;
		Integer intMes = null;
		intMes = Calendar.getInstance().get(Calendar.MONDAY)+1;
		if (intMes>11){
			strMes = "01";
		}else{
			strMes = intMes.toString().length()==1? "0"+intMes.toString(): intMes.toString();
		}
		return strMes;
	}
	/*
	 * Convierte de fecha dd/MM/yyyy a yyyy-mm-dd
	 */
	public static String getYYYYMMDD(String fecha){//dd/mm/yyyy
		String nuevaFecha = null;
		nuevaFecha = fecha.substring(6,10) + "-"+fecha.substring(3,5)+"-"+fecha.substring(0,2);
		return nuevaFecha;
	}
	/*
	 * Convierte de fecha yyyy-mm-dd a dd/MM/yyyy
	 */
	public static String getddMMyyyy(String fecha){//yyyy-mm-dd
		String nuevaFecha = null;
		nuevaFecha = fecha.substring(8,10) + "/" + fecha.substring(5,7) + "/" +fecha.substring(0,4);
		return nuevaFecha;
	}
	//*************************************************************************************
	// FACTURADOR 1.2
	//*************************************************************************************

	/* Obtiene el codigo internacional : posicion 1, nombre: posicion 2*/
	public static String getDatoTributo(String cadena, Integer posicion){
		String dato = "";
		if (cadena!=null && cadena.length()>0){
			String[] lista = cadena.split("|");
			if (lista != null && lista.length>0){
				dato = lista[posicion-1];
			}
		}
		return dato;		
	}
	/*Obtiene el codigo de la afectacion*/
	public static String getDatoAfectacion(String cadena){
		String dato = "";
		if (cadena!=null && cadena.length()>0){
			dato = cadena.substring(0, 4);
		}
		return dato;		
	}
	/*Obtener la Hora del sistema HH:MM:SS*/
	public static String getHora(){
		String hora = "";
		String minuto;
		String segundo;
		Calendar rightNow = Calendar.getInstance();
		hora = rightNow.get(Calendar.HOUR_OF_DAY) + "";
		if (hora.length()<2){
			hora = "0"+hora;
		}
		minuto = rightNow.get(Calendar.MINUTE) + "";
		if (minuto.length()<2){
			minuto = "0"+minuto;
		}
		segundo = rightNow.get(Calendar.SECOND) + "";
		if (segundo.length()<2){
			segundo = "0"+segundo;
		}
		hora = hora +":"+ minuto+":"+segundo;
		return hora;
	}

	/*Retorna el tipo de afectacion*/
	public static String getTipoAfectacion(String codigoAfectacion){
		String resultado = "";

		switch (codigoAfectacion){
		case "10":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_GRAVADO;
			break;
		case "11":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_GRAVADO;
			break;
		case "12":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_GRAVADO;
			break;
		case "13":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_GRAVADO;
			break;
		case "14":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_GRAVADO;
			break;
		case "15":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_GRAVADO;
			break;
		case "16":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_GRAVADO;
			break;
		case "17":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_GRAVADO;
			break;
		case "20":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_EXONERADO;
			break;
		case "21":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_EXONERADO;
			break;
		case "30":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_INAFECTO;
			break;
		case "31":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_INAFECTO;
			break;
		case "32":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_INAFECTO;
			break;
		case "33":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_INAFECTO;
			break;
		case "34":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_INAFECTO;
			break;
		case "35":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_INAFECTO;
			break;
		case "36":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_INAFECTO;
			break;
		case "37":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_INAFECTO;
			break;
		case "40":
			resultado = Constantes.SUNAT_TIPO_AFECTACION_EXPORTACION;
			break;

		}

		return resultado;
	}
	public static String getTributoGravado(String tipo){
		String resultado = null;
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_CODIGO))
			resultado = "1000";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_NOMBRE))
			resultado = "IGV";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_TIPO))
			resultado = "VAT";
		return resultado;
	}
	public static String getTributoExonerado(String tipo){
		String resultado = null;
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_CODIGO))
			resultado = "9997";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_NOMBRE))
			resultado = "EXO";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_TIPO))
			resultado = "VAT";
		return resultado;
	}
	public static String getTributoGratuito(String tipo){
		String resultado = null;
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_CODIGO))
			resultado = "9996";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_NOMBRE))
			resultado = "GRA";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_TIPO))
			resultado = "FRE";
		return resultado;
	}
	public static String getTributoInafecto(String tipo){
		String resultado = null;
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_CODIGO))
			resultado = "9998";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_NOMBRE))
			resultado = "INA";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_TIPO))
			resultado = "FRE";
		return resultado;
	}
	public static String getTributoExportacion(String tipo){
		String resultado = null;
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_CODIGO))
			resultado = "9995";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_NOMBRE))
			resultado = "EXP";
		if (tipo.equals(Constantes.SUNAT_TRIBUTO_CODIGO))
			resultado = "FRE";
		return resultado;
	}
	
	/*Retorna el tipo de afectacion*/
	public static String getTributo(String codigoAfectacion, String tipo){
		String resultado = "";

		switch (codigoAfectacion){
		case "10":
			resultado = getTributoGravado(tipo);
			break;
		case "11":
			resultado = getTributoGravado(tipo);
			break;
		case "12":
			resultado = getTributoGravado(tipo);
			break;
		case "13":
			resultado = getTributoGravado(tipo);
			break;
		case "14":
			resultado = getTributoGravado(tipo);
			break;
		case "15":
			resultado = getTributoGravado(tipo);
			break;
		case "16":
			resultado = getTributoGravado(tipo);
			break;
		case "17":
			resultado = getTributoGravado(tipo);
			break;
		case "20":
			resultado = getTributoExonerado(tipo);
			break;
		case "21":
			resultado = getTributoGratuito(tipo);
			break;
		case "30":
			resultado = getTributoInafecto(tipo);
			break;
		case "31":
			resultado = getTributoInafecto(tipo);
			break;
		case "32":
			resultado = getTributoInafecto(tipo);
			break;
		case "33":
			resultado = getTributoInafecto(tipo);
			break;
		case "34":
			resultado = getTributoInafecto(tipo);
			break;
		case "35":
			resultado = getTributoInafecto(tipo);
			break;
		case "36":
			resultado = getTributoInafecto(tipo);
			break;
		case "37":
			resultado = getTributoGratuito(tipo);
			break;
		case "40":
			resultado = getTributoExportacion(tipo);
			break;

		}

		return resultado;
	}
	/*Retorna el tipo de afectacion*/
	public static BigDecimal getMontoTributoBaseImponible(String codigoAfectacion, TblDetalleComprobante detalle){
		BigDecimal resultado = null;

		switch (codigoAfectacion){
		case "10":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "11":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "12":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "13":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "14":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "15":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "16":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "17":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "20":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "21":
			resultado = new BigDecimal("0"); //Luego se asigna el valor, solicitado en la version 1.3
			break;
		case "30":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "31":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "32":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "33":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "34":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "35":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "36":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;
		case "37":
			resultado = new BigDecimal("0");
			break;
		case "40":
			resultado = detalle.getPrecioTotal().subtract(detalle.getTribMontoIgv());
			break;

		}

		return resultado;
	}
	/*Retorna el tipo de afectacion*/
	public static void getMontoAfectacion(TblDetalleComprobante detalle, TblTributoGeneral tributoIgv, TblTributoGeneral tributoExo, TblTributoGeneral tributoIna, TblTributoGeneral tributoGra){
		
		switch (detalle.getTipoAfectacion()){
		case "10":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "11":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "12":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "13":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "14":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "15":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "16":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "17":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "20":
			tributoExo.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoExo.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "21":
			tributoGra.setBaseImponible(tributoGra.getBaseImponible().add(detalle.getValorReferencia().multiply(detalle.getCantidad())));
			tributoGra.setMontoTributoItem(new BigDecimal("0"));
			break;
		case "30":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "31":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "32":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "33":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "34":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "35":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "36":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "37":
			tributoGra.setBaseImponible(tributoGra.getBaseImponible().add(detalle.getValorReferencia().multiply(detalle.getCantidad())));
			tributoGra.setMontoTributoItem(new BigDecimal("0"));
			break;
		case "40":
			
			break;

		}
	}
	
	/*Retorna el tipo de afectacion*/
	public static void getMontoAfectacionNota(TblDetalleComprobante detalle, TblTributoGeneralNota tributoIgv, TblTributoGeneralNota tributoExo, TblTributoGeneralNota tributoIna, TblTributoGeneralNota tributoGra){
		
		switch (detalle.getTipoAfectacion()){
		case "10":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "11":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "12":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "13":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "14":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "15":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "16":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "17":
			tributoIgv.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIgv.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "20":
			tributoExo.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoExo.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "21":
			/*tributoGra.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoGra.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));*/
			tributoGra.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getValorReferencia().multiply(detalle.getCantidad())));
			tributoGra.setMontoTributoItem(new BigDecimal("0"));
			break;
		case "30":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "31":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "32":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "33":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "34":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "35":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "36":
			tributoIna.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoIna.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));
			break;
		case "37":
			/*tributoGra.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getTribBaseImponibleIgv()));
			tributoGra.setMontoTributoItem(tributoIgv.getMontoTributoItem().add(detalle.getTribMontoIgv()));*/
			tributoGra.setBaseImponible(tributoIgv.getBaseImponible().add(detalle.getValorReferencia().multiply(detalle.getCantidad())));
			tributoGra.setMontoTributoItem(new BigDecimal("0"));
			break;
		case "40":
			
			break;

		}
	}
	
	public static BigDecimal getRoundDecimal(BigDecimal numero,  int decimal){
		if (numero == null){
			numero = new BigDecimal("0");
		}
		return numero.setScale(decimal, BigDecimal.ROUND_HALF_EVEN);
	}
	public static String getRoundDecimalString(BigDecimal numero,  int decimal){
		if (numero == null){
			numero = new BigDecimal("0");
		}
		return numero.setScale(decimal, BigDecimal.ROUND_HALF_EVEN).toString();
	}
	public static BigDecimal getCalculoDescuento(BigDecimal total, BigDecimal descuento, int decimal){
		BigDecimal calculo = null;
		if (total == null){
			total = new BigDecimal("0");
		}
		if (descuento == null){
			descuento = new BigDecimal("0");
		}
		calculo = total.multiply(descuento);
		calculo = calculo.divide(new BigDecimal("100"), 4, RoundingMode.HALF_EVEN);
		calculo = total.subtract(calculo);
		return calculo.setScale(decimal, BigDecimal.ROUND_HALF_EVEN);
	}
	public static List<String> getNombreArchivos(String nombreArchivo){
		ArrayList<String> lista = new ArrayList<>();
		String prefijo = null;
		if (nombreArchivo !=null && nombreArchivo.length()>0){
			prefijo = nombreArchivo.substring(0, nombreArchivo.indexOf("."));
			lista.add(nombreArchivo);
			lista.add(prefijo+".DET");
			lista.add(prefijo+".TRI");
			lista.add(prefijo+".LEY");
		}
		return lista;
	}

	public static void eliminarArchivo(List<String> lista, String ruta){
		File file = null;
		try{
			for(String nombre:lista){
				file = new File(ruta +nombre);
				if (file.delete()){
					System.out.println("Archivo Eliminado:"+ruta+nombre);
				}else{
	    			System.out.println("Delete operation is failed.");
	    		}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**/
	 public static boolean isFechaCorteOse(String fechaCorte) {
		 String fechaBase = "2022-08-20";

		 SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		 try {
			 Date fecha1 = formato.parse(fechaBase);
			 Date fecha2 = formato.parse(fechaCorte);

			 if(fecha1.equals(fecha2)){
				 System.out.println( "fechaBase ["+fechaBase+"] es igual a fechaCorte["+fechaCorte+"]" );
				 return false;
			 }else if(fecha1.after(fecha2)){
				 System.out.println( "fechaBase ["+fechaBase+"] es mayor a fechaCorte["+fechaCorte+"]" );
				 return false;
			 }else if(fecha1.before(fecha2)){
				 System.out.println( "fechaBase ["+fechaBase+"] es menor a fechaCorte["+fechaCorte+"]" );
				 return true;

			 }
		 }catch(Exception e) {
			 e.printStackTrace();
			 return false;
		 }
		 return false;
	 }
}
