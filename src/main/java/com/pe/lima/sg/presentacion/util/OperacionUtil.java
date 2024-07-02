package com.pe.lima.sg.presentacion.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.pe.lima.sg.bean.facturador.BandejaFacturadorBean;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
import com.pe.lima.sg.entity.operacion.TblBandejaFacturador;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblTributoGeneral;
import com.pe.lima.sg.entity.operacion.TblTributoGeneralNota;
import com.pe.lima.sg.presentacion.Filtro;

public class OperacionUtil {


	public static void asignarParametros(Filtro entidad, Map<String, TblParametro> mapParametro, HttpServletRequest request){
		TblParametro parametro =null;
		try{
			entidad.setComprobante(new TblComprobante());
			entidad.setDetalleComprobante(new TblDetalleComprobante());
			if (mapParametro!=null){
				//IGV
				parametro = mapParametro.get(Constantes.PARAMETRO_IGV);
				if (parametro!=null){
					entidad.setValorIGV(parametro.getCantidad());
					entidad.setNombreIGV(parametro.getDato());
				}else{
					entidad.setValorIGV(Constantes.SUNAT_IGV);
				}
				//SERVICIO
				parametro = mapParametro.get(Constantes.PARAMETRO_SERVICIO);
				if (parametro!=null){
					entidad.setValorServicio(parametro.getCantidad());
					entidad.setNombreServicio(parametro.getDato());
				}else{
					entidad.setValorServicio(Constantes.SUNAT_SERVICIO);
				}
				//Tipo de Operacion
				parametro = mapParametro.get(Constantes.PARAMETRO_TIPO_OPERACION_SFS_12);
				if (parametro!=null){
					entidad.getComprobante().setTipoOperacion(parametro.getDato());
				}else{
					entidad.getComprobante().setTipoOperacion("");
				}
				//Codigo el domicilio fiscal
				parametro = mapParametro.get(Constantes.PARAMETRO_CODIGO_DOMICILIO_FISCAL);
				if (parametro!=null){
					entidad.getComprobante().setCodigoDomicilio(parametro.getDato());
				}else{
					entidad.getComprobante().setCodigoDomicilio("");
				}
				//Tipo de comprobante
				parametro = mapParametro.get(Constantes.PARAMETRO_TIPO_COMPROBANTE);
				if (parametro!=null){
					entidad.getComprobante().setTipoComprobante(parametro.getDato());
				}else{
					entidad.getComprobante().setTipoComprobante("");
				}
				//Moneda
				parametro = mapParametro.get(Constantes.PARAMETRO_MONEDA);
				if (parametro!=null){
					entidad.getComprobante().setMoneda(parametro.getDato());
				}else{
					entidad.getComprobante().setMoneda("");
				}
				//Serie
				parametro = mapParametro.get(Constantes.PARAMETRO_SERIE);
				if (parametro!=null){
					entidad.getComprobante().setSerie(parametro.getDato());
				}else{
					entidad.getComprobante().setSerie("");
				}
				//Unidad medida
				parametro = mapParametro.get(Constantes.PARAMETRO_UNIDAD_MEDIDA);
				if (parametro!=null){
					entidad.getDetalleComprobante().setUnidadMedida(parametro.getDato());
				}else{
					entidad.getDetalleComprobante().setUnidadMedida("");
				}
				//afectacion igv
				parametro = mapParametro.get(Constantes.PARAMETRO_AFECTACION_IGV);
				if (parametro!=null){
					entidad.getDetalleComprobante().setTipoAfectacion(parametro.getDato());
				}else{
					entidad.getDetalleComprobante().setTipoAfectacion("");
				}
				//Ruta del repositorio Data
				parametro = mapParametro.get(Constantes.PARAMETRO_SUNAT_DATA);
				if (parametro!=null){
					entidad.setSunatData(parametro.getDato());
				}else{
					entidad.setSunatData(Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA);
				}
				//Ruta de la Base de Datos
				parametro = mapParametro.get(Constantes.PARAMETRO_SUNAT_BD);
				if (parametro!=null){
					entidad.setSunatBD(parametro.getDato());
				}else{
					entidad.setSunatBD(Constantes.SUNAT_FACTURADOR_DB_LOCAL);
				}

				//RUC de la empresa
				entidad.setRuc(Constantes.SUNAT_RUC_EMISOR);

				//SERIE DE LA FACTURA
				parametro = mapParametro.get(Constantes.PARAMETRO_SERIE_AUTOMATICA);
				if (parametro!=null){
					entidad.setFlagSerieAutomatica(parametro.getDato());
					
				}else{
					entidad.setFlagSerieAutomatica(Constantes.ESTADO_INACTIVO);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void setDatosComprobanteSFS12(Filtro entidad){
		entidad.getComprobante().setSumTributo(entidad.getComprobante().getTotalIgv().add(entidad.getComprobante().getSumatorioaOtrosTributos()));
		entidad.getComprobante().setTotValorVenta(entidad.getComprobante().getTotalOpGravada().add(entidad.getComprobante().getTotalOpExonerada()).add(entidad.getComprobante().getTotalOpInafecta()));
		entidad.getComprobante().setTotPrecioVenta(entidad.getComprobante().getSumTributo().add(entidad.getComprobante().getTotValorVenta()));
		entidad.getComprobante().setTotDescuento(entidad.getComprobante().getTotalDescuento().add(entidad.getComprobante().getDescuentosGlobales()));
		entidad.getComprobante().setSumOtrosCargos(entidad.getComprobante().getTotalOtrosCargos());
		entidad.getComprobante().setImpTotalVenta(entidad.getComprobante().getTotPrecioVenta().add(entidad.getComprobante().getSumOtrosCargos()));
		entidad.getComprobante().setImpTotalVenta(entidad.getComprobante().getImpTotalVenta().subtract(entidad.getComprobante().getTotDescuento()));
		
		/*Asignamos a todos dos decimales*/
		entidad.getComprobante().setSumTributo(UtilSGT.getRoundDecimal(entidad.getComprobante().getSumTributo(), 2));
		entidad.getComprobante().setTotValorVenta(UtilSGT.getRoundDecimal(entidad.getComprobante().getTotValorVenta(), 2));
		entidad.getComprobante().setTotPrecioVenta(UtilSGT.getRoundDecimal(entidad.getComprobante().getTotPrecioVenta(), 2));
		entidad.getComprobante().setTotDescuento(UtilSGT.getRoundDecimal(entidad.getComprobante().getTotDescuento(), 2));
		entidad.getComprobante().setSumOtrosCargos(UtilSGT.getRoundDecimal(entidad.getComprobante().getSumOtrosCargos(), 2));
		entidad.getComprobante().setImpTotalVenta(UtilSGT.getRoundDecimal(entidad.getComprobante().getImpTotalVenta(), 2));
		entidad.getComprobante().setImpTotalVenta(UtilSGT.getRoundDecimal(entidad.getComprobante().getImpTotalVenta(), 2));
		
		//No se esta aplicando los anticipos
		entidad.getComprobante().setVersionUbl(Constantes.SUNAT_VERSION_UBL);
		entidad.getComprobante().setCustomizacionDoc(Constantes.SUNAT_CUSTOMIZACION);
	}

	public static void setDatosDetalleComprobanteSFS12(Filtro entidad){
		for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
			detalle.setCodigoProductoSunat(Constantes.SUNAT_SIN_CODIGO);

			


			detalle.setTribCodTipoTributo(UtilSGT.getTributo(entidad.getDetalleComprobante().getTipoAfectacion(), Constantes.SUNAT_TRIBUTO_TIPO));
			detalle.setTribNombreTributo(UtilSGT.getTributo(entidad.getDetalleComprobante().getTipoAfectacion(), Constantes.SUNAT_TRIBUTO_NOMBRE));
			detalle.setTribCodTipoTributoIgv(UtilSGT.getTributo(entidad.getDetalleComprobante().getTipoAfectacion(), Constantes.SUNAT_TRIBUTO_CODIGO));
			detalle.setTribMontoIgv(obtenerTotalImpuestoGravada(detalle.getPrecioTotal(), entidad.getValorIGV(), entidad.getValorServicio()));
			detalle.setTribBaseImponibleIgv(UtilSGT.getMontoTributoBaseImponible(entidad.getDetalleComprobante().getTipoAfectacion(), detalle));

			detalle.setTribAfectacionIgv(entidad.getDetalleComprobante().getTipoAfectacion());
			detalle.setTribPorcentajeIgv(entidad.getValorIGV().toString());

			detalle.setIscCodTipoTributoIsc(Constantes.SUNAT_SIN_CODIGO);
			detalle.setIscMontoIsc(new BigDecimal("0"));
			detalle.setIscBaseImponibleIsc(new BigDecimal("0"));
			detalle.setIscNombreTributo("");
			detalle.setIscCodTipoTributo("");
			detalle.setIscTipoSistema("");
			detalle.setIscPorcentaje("15");

			detalle.setOtroCodTipoTributoOtro(Constantes.SUNAT_SIN_CODIGO);
			detalle.setOtroMontoTributo(new BigDecimal("0"));
			detalle.setOtroBaseImponibleOtro(new BigDecimal("0"));
			detalle.setOtroNombreTributo("");
			detalle.setOtroCodTipoTributo("");
			detalle.setOtroPorcentaje("15");

			detalle.setSumTributosItem(detalle.getTribMontoIgv().add(detalle.getIscMontoIsc()).add(detalle.getOtroMontoTributo()));
			if (detalle.getTipoAfectacion().equals(Constantes.SUNAT_INAFECTO_OPERACION_GRATUITA) || detalle.getTipoAfectacion().equals(Constantes.SUNAT_EXONERADO_OPERACION_GRATUITA) ) {
				detalle.setValorVentaItem(detalle.getValorReferencia().multiply(detalle.getCantidad())); //ok en 1.3
				//detalle.setValorUnitario(detalle.getValorReferencia()); 
				detalle.setValorUnitario(new BigDecimal("0")); //En la nueva version 1.3 va en cero para 
				//detalle.setPrecioVentaUnitario(detalle.getValorReferencia());
				detalle.setPrecioVentaUnitario(new BigDecimal("0")); //En la nueva version 1.3 va en cero para 
				//detalle.setValorReferencialUnitario(new BigDecimal("0"));
				detalle.setValorReferencialUnitario(detalle.getValorReferencia()); //En la nueva version 1.3 va el valor de referencia
				//En la nueva version solicitan que siempre se debe ingresar un valor mayor a cero
				detalle.setTribBaseImponibleIgv(detalle.getValorReferencia().multiply(detalle.getCantidad()));
			}else{
				detalle.setValorVentaItem(detalle.getTribBaseImponibleIgv());
				if (detalle.getCantidad().compareTo(new BigDecimal("0"))==0) {
					detalle.setValorUnitario(new BigDecimal("0"));
					detalle.setPrecioVentaUnitario(new BigDecimal("0"));
				}else {
					detalle.setValorUnitario(detalle.getTribBaseImponibleIgv().divide(detalle.getCantidad(), 4, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP));
					detalle.setPrecioVentaUnitario(detalle.getTribBaseImponibleIgv().add(detalle.getSumTributosItem()).divide(detalle.getCantidad(), 4, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP));
					
				}
				detalle.setValorReferencialUnitario(new BigDecimal("0"));
			}
			/*Asignamos dos decimales*/
			detalle.setValorVentaItem(UtilSGT.getRoundDecimal(detalle.getValorVentaItem(), 2));
			detalle.setValorUnitario(UtilSGT.getRoundDecimal(detalle.getValorUnitario(), 4));
			detalle.setPrecioVentaUnitario(UtilSGT.getRoundDecimal(detalle.getPrecioVentaUnitario(), 4));
			detalle.setValorReferencialUnitario(UtilSGT.getRoundDecimal(detalle.getValorReferencialUnitario(), 2));
			
			//detalle.setValorReferencialUnitario(new BigDecimal("0"));
			

		}
	}
	public static void setDatosTributosComprobanteSFS12(Filtro entidad, TblTributoGeneral tributoIgv, TblTributoGeneral tributoExo, TblTributoGeneral tributoIna, TblTributoGeneral tributoGra){

		tributoIgv.setIdentificadorTributo(UtilSGT.getTributoGravado(Constantes.SUNAT_TRIBUTO_CODIGO));
		tributoIgv.setNombreTributo(UtilSGT.getTributoGravado(Constantes.SUNAT_TRIBUTO_NOMBRE));
		tributoIgv.setCodigoTipoTributo(UtilSGT.getTributoGravado(Constantes.SUNAT_TRIBUTO_TIPO));
		tributoIgv.setBaseImponible(new BigDecimal("0"));
		tributoIgv.setMontoTributoItem(new BigDecimal("0"));

		tributoExo.setIdentificadorTributo(UtilSGT.getTributoExonerado(Constantes.SUNAT_TRIBUTO_CODIGO));
		tributoExo.setNombreTributo(UtilSGT.getTributoExonerado(Constantes.SUNAT_TRIBUTO_NOMBRE));
		tributoExo.setCodigoTipoTributo(UtilSGT.getTributoExonerado(Constantes.SUNAT_TRIBUTO_TIPO));
		tributoExo.setBaseImponible(new BigDecimal("0"));
		tributoExo.setMontoTributoItem(new BigDecimal("0"));

		tributoGra.setIdentificadorTributo(UtilSGT.getTributoGratuito(Constantes.SUNAT_TRIBUTO_CODIGO));
		tributoGra.setNombreTributo(UtilSGT.getTributoGratuito(Constantes.SUNAT_TRIBUTO_NOMBRE));
		tributoGra.setCodigoTipoTributo(UtilSGT.getTributoGratuito(Constantes.SUNAT_TRIBUTO_TIPO));
		tributoGra.setBaseImponible(new BigDecimal("0"));
		tributoGra.setMontoTributoItem(new BigDecimal("0"));

		tributoIna.setIdentificadorTributo(UtilSGT.getTributoInafecto(Constantes.SUNAT_TRIBUTO_CODIGO));
		tributoIna.setNombreTributo(UtilSGT.getTributoInafecto(Constantes.SUNAT_TRIBUTO_NOMBRE));
		tributoIna.setCodigoTipoTributo(UtilSGT.getTributoInafecto(Constantes.SUNAT_TRIBUTO_TIPO));
		tributoIna.setBaseImponible(new BigDecimal("0"));
		tributoIna.setMontoTributoItem(new BigDecimal("0"));

		for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
			UtilSGT.getMontoAfectacion(detalle, tributoIgv, tributoExo, tributoIna, tributoGra);
		}
		entidad.getListaTributo().add(tributoIgv);
		entidad.getListaTributo().add(tributoExo);
		entidad.getListaTributo().add(tributoIna);
		entidad.getListaTributo().add(tributoGra);

	}

	public static void setDatosTributosNotaSFS12(Filtro entidad, TblTributoGeneralNota tributoIgv, TblTributoGeneralNota tributoExo, TblTributoGeneralNota tributoIna, TblTributoGeneralNota tributoGra){

		tributoIgv.setIdentificadorTributo(UtilSGT.getTributoGravado(Constantes.SUNAT_TRIBUTO_CODIGO));
		tributoIgv.setNombreTributo(UtilSGT.getTributoGravado(Constantes.SUNAT_TRIBUTO_NOMBRE));
		tributoIgv.setCodigoTipoTributo(UtilSGT.getTributoGravado(Constantes.SUNAT_TRIBUTO_TIPO));
		tributoIgv.setBaseImponible(new BigDecimal("0"));
		tributoIgv.setMontoTributoItem(new BigDecimal("0"));

		tributoExo.setIdentificadorTributo(UtilSGT.getTributoExonerado(Constantes.SUNAT_TRIBUTO_CODIGO));
		tributoExo.setNombreTributo(UtilSGT.getTributoExonerado(Constantes.SUNAT_TRIBUTO_NOMBRE));
		tributoExo.setCodigoTipoTributo(UtilSGT.getTributoExonerado(Constantes.SUNAT_TRIBUTO_TIPO));
		tributoExo.setBaseImponible(new BigDecimal("0"));
		tributoExo.setMontoTributoItem(new BigDecimal("0"));

		tributoGra.setIdentificadorTributo(UtilSGT.getTributoGratuito(Constantes.SUNAT_TRIBUTO_CODIGO));
		tributoGra.setNombreTributo(UtilSGT.getTributoGratuito(Constantes.SUNAT_TRIBUTO_NOMBRE));
		tributoGra.setCodigoTipoTributo(UtilSGT.getTributoGratuito(Constantes.SUNAT_TRIBUTO_TIPO));
		tributoGra.setBaseImponible(new BigDecimal("0"));
		tributoGra.setMontoTributoItem(new BigDecimal("0"));

		tributoIna.setIdentificadorTributo(UtilSGT.getTributoInafecto(Constantes.SUNAT_TRIBUTO_CODIGO));
		tributoIna.setNombreTributo(UtilSGT.getTributoInafecto(Constantes.SUNAT_TRIBUTO_NOMBRE));
		tributoIna.setCodigoTipoTributo(UtilSGT.getTributoInafecto(Constantes.SUNAT_TRIBUTO_TIPO));
		tributoIna.setBaseImponible(new BigDecimal("0"));
		tributoIna.setMontoTributoItem(new BigDecimal("0"));

		for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
			UtilSGT.getMontoAfectacionNota(detalle, tributoIgv, tributoExo, tributoIna, tributoGra);
		}
		entidad.getListaTributoNota().add(tributoIgv);
		/**SOLO aplica para las notas el IGV*/
		/*entidad.getListaTributoNota().add(tributoExo);
		entidad.getListaTributoNota().add(tributoIna);
		entidad.getListaTributoNota().add(tributoGra);*/

	}
	/*
	 * Impuesto Gravada
	 */
	public static BigDecimal obtenerTotalImpuestoGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100;
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			//resultado = new BigDecimal(monto.doubleValue()*(igv)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			resultado = monto.multiply(new BigDecimal(igv));
			resultado = resultado.divide(new BigDecimal(totalImpuesto), 4, RoundingMode.HALF_EVEN);
			resultado = UtilSGT.getRoundDecimal(resultado, 2);

		}catch(Exception e){
			resultado = new BigDecimal("0");
			e.printStackTrace();
		}
		return resultado;
	}
	/*
	 * Impuesto Gravada
	 */
	public static BigDecimal obtenerTotalOtrosTributosGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100;
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			//resultado = new BigDecimal(monto.doubleValue()*(servicio)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			resultado = monto.multiply(new BigDecimal(servicio));
			resultado = resultado.divide(new BigDecimal(totalImpuesto), 4, RoundingMode.HALF_EVEN);
			resultado = UtilSGT.getRoundDecimal(resultado, 2);

		}catch(Exception e){
			resultado = new BigDecimal("0");
			e.printStackTrace();
		}
		return resultado;
	}
	/*
	 * Calculo del Detalle 
	 */
	public static void calculoDetalleComprobante(Filtro entidad){
		TblDetalleComprobante detalle = null;
		try{
			detalle = entidad.getDetalleComprobante();
			detalle.setPrecioTotal(detalle.getPrecioUnitario().multiply(detalle.getCantidad()));
			
			if (detalle.getDescuento()!=null && detalle.getDescuento().doubleValue()>0){
				//detalle.setPrecioFinal(new BigDecimal(detalle.getPrecioTotal().doubleValue() - detalle.getPrecioTotal().doubleValue()*detalle.getDescuento().doubleValue()/100).setScale(2, RoundingMode.HALF_UP));
				detalle.setPrecioFinal(UtilSGT.getCalculoDescuento(detalle.getPrecioTotal(), detalle.getDescuento(), 4));
				
			}else{
				detalle.setPrecioFinal(detalle.getPrecioTotal());
			}
			//Luego de la operacion se redondea a 2 decimales
			detalle.setPrecioTotal(UtilSGT.getRoundDecimal(detalle.getPrecioTotal(), 2));
			detalle.setPrecioFinal(UtilSGT.getRoundDecimal(detalle.getPrecioFinal(), 2));
		}catch(Exception e){

		}
	}
	/*
	 * Monto Gravada
	 */
	public static BigDecimal obtenerTotalMontoGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100; 
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			//resultado = new BigDecimal(monto.doubleValue()*(100)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			resultado = monto.multiply(new BigDecimal("100"));
			resultado = resultado.divide(new BigDecimal(totalImpuesto), 4, RoundingMode.HALF_EVEN);
			resultado = UtilSGT.getRoundDecimal(resultado, 2);			
			//resultado = new BigDecimal(monto.doubleValue()*(100+igv)/100).setScale(2, RoundingMode.HALF_UP);

		}catch(Exception e){
			resultado = new BigDecimal("0");
			e.printStackTrace();
		}
		return resultado;
	}

	/*
	 * Calculo de los totales	  
	 */
	public static void calculoCabeceraComprobante(Filtro entidad){
		TblComprobante comprobante = null;
		String tipoAfectacion = null;
		try{
			comprobante = entidad.getComprobante();
			if (entidad.getListaDetalle() != null && entidad.getListaDetalle().size()>0){
				if (comprobante.getTipoOperacion().equals(Constantes.SUNAT_TIPO_OPERACION_EXPORTACION)){
					calculoCabeceraComprobateExtranjero(entidad);
				}else{
					tipoAfectacion = UtilSGT.getTipoAfectacion(entidad.getDetalleComprobante().getTipoAfectacion());
					if (tipoAfectacion.equals(Constantes.SUNAT_TIPO_AFECTACION_GRAVADO)){
						calculoCabeceraComprobanteNacional(entidad);
					}
					if (tipoAfectacion.equals(Constantes.SUNAT_TIPO_AFECTACION_EXONERADO)){
						calculoCabeceraComprobanteExonerado(entidad);
					}
					if (tipoAfectacion.equals(Constantes.SUNAT_TIPO_AFECTACION_INAFECTO)){
						calculoCabeceraComprobanteInafecta(entidad);
					}
					if (tipoAfectacion.equals(Constantes.SUNAT_TIPO_AFECTACION_EXPORTACION)){
						calculoCabeceraComprobanteNacional(entidad);
					}

				}
			}
			

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void calculoCabeceraComprobanteNacional(Filtro entidad){
		TblComprobante comprobante = null;
		try{
			comprobante = entidad.getComprobante();
			//inicializa
			comprobante.setDescuentosGlobales(new BigDecimal("0"));
			comprobante.setTotalImporte(new BigDecimal("0"));
			comprobante.setValorOpGratuitas(new BigDecimal("0"));
			comprobante.setTotalDescuento(new BigDecimal("0"));
			for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				comprobante.setTotalDescuento(comprobante.getTotalDescuento().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));

				//Total Importe: Precio Unitario x Cantidad - Descuento
				//comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad())));
				//comprobante.setTotalImporte(comprobante.getTotalImporte().subtract(comprobante.getTotalDescuento()));
				//Total de valor de referencia
				//comprobante.setValorOpGratuitas(comprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setValorOpGratuitas(comprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad())));
			}
			comprobante.setTotalImporte(UtilSGT.getRoundDecimal(comprobante.getTotalImporte(), 2));
			comprobante.setValorOpGratuitas(UtilSGT.getRoundDecimal(comprobante.getValorOpGratuitas(), 2));
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			//comprobante.setTotalDescuento(comprobante.getDescuentosGlobales().add(comprobante.getTotalOtrosCargos()));
			//Pendiente el calculo del descuento global

			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			comprobante.setTotalIgv(obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			//Calculo del total sin igv
			comprobante.setTotalOpGravada(obtenerTotalMontoGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));
			comprobante.setTotalOpExonerada(new BigDecimal("0"));
			comprobante.setTotalOpInafecta(new BigDecimal("0"));
			//Calculo del total de otros tributos: servicio
			comprobante.setSumatorioaOtrosTributos(obtenerTotalOtrosTributosGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void calculoCabeceraComprobanteExonerado(Filtro entidad){
		TblComprobante comprobante = null;
		try{
			comprobante = entidad.getComprobante();
			//inicializa
			comprobante.setDescuentosGlobales(new BigDecimal("0"));
			comprobante.setTotalImporte(new BigDecimal("0"));
			comprobante.setValorOpGratuitas(new BigDecimal("0"));
			comprobante.setTotalDescuento(new BigDecimal("0"));
			for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				comprobante.setTotalDescuento(comprobante.getTotalDescuento().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));

				//Total Importe: Precio Unitario x Cantidad - Descuento
				//comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad())));
				
				//Total de valor de referencia
				//comprobante.setValorOpGratuitas(comprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setValorOpGratuitas(comprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad())));
			}
			comprobante.setTotalImporte(comprobante.getTotalImporte().subtract(comprobante.getTotalDescuento()));
			
			/*Se redondea a dos decimales*/
			comprobante.setTotalImporte(UtilSGT.getRoundDecimal(comprobante.getTotalImporte(), 2));
			comprobante.setTotalDescuento(UtilSGT.getRoundDecimal(comprobante.getTotalDescuento(), 2));
			comprobante.setValorOpGratuitas(UtilSGT.getRoundDecimal(comprobante.getValorOpGratuitas(), 2));
			
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			//comprobante.setTotalDescuento(comprobante.getDescuentosGlobales().add(comprobante.getTotalOtrosCargos()));

			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			comprobante.setTotalIgv(new BigDecimal("0")); //Por ser exonerado no se aplica IGV
			//Calculo del total sin igv
			comprobante.setTotalOpGravada(new BigDecimal("0")); //Por ser exonerado no aplica total operacion gravaa
			//Se asigna el valor al campo de Total Operacion Exonerada
			comprobante.setTotalOpExonerada(comprobante.getTotalImporte());
			comprobante.setTotalOpInafecta(new BigDecimal("0"));
			//Calculo del total de otros tributos: servicio
			comprobante.setSumatorioaOtrosTributos(obtenerTotalOtrosTributosGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void calculoCabeceraComprobanteInafecta(Filtro entidad){
		TblComprobante comprobante = null;
		try{
			comprobante = entidad.getComprobante();
			//inicializa
			comprobante.setDescuentosGlobales(new BigDecimal("0"));
			comprobante.setTotalImporte(new BigDecimal("0"));
			comprobante.setValorOpGratuitas(new BigDecimal("0"));
			comprobante.setTotalDescuento(new BigDecimal("0"));
			for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				comprobante.setTotalDescuento(comprobante.getTotalDescuento().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));

				//Total Importe: Precio Unitario x Cantidad - Descuento
				//comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad())));
				
				//Total de valor de referencia
				//comprobante.setValorOpGratuitas(comprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setValorOpGratuitas(comprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad())));
			}
			comprobante.setTotalImporte(comprobante.getTotalImporte().subtract(comprobante.getTotalDescuento()));
			/*Redondeo a dos decimales*/
			comprobante.setTotalDescuento(UtilSGT.getRoundDecimal(comprobante.getTotalDescuento(), 2));
			comprobante.setValorOpGratuitas(UtilSGT.getRoundDecimal(comprobante.getTotalDescuento(), 2));
			comprobante.setValorOpGratuitas(UtilSGT.getRoundDecimal(comprobante.getValorOpGratuitas(), 2));
			comprobante.setTotalImporte(UtilSGT.getRoundDecimal(comprobante.getTotalImporte(), 2));
			
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			//comprobante.setTotalDescuento(comprobante.getDescuentosGlobales().add(comprobante.getTotalOtrosCargos()));

			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			comprobante.setTotalIgv(new BigDecimal("0")); //Por ser exonerado no se aplica IGV
			//Calculo del total sin igv
			comprobante.setTotalOpGravada(new BigDecimal("0")); //Por ser exonerado no aplica total operacion gravaa
			//Se asigna el valor al campo de Total Operacion Exonerada
			comprobante.setTotalOpExonerada(new BigDecimal("0"));
			//Se asigna el valor al campo de Total Inafecto
			comprobante.setTotalOpInafecta(comprobante.getTotalImporte());
			//Calculo del total de otros tributos: servicio
			comprobante.setSumatorioaOtrosTributos(obtenerTotalOtrosTributosGravada(comprobante.getTotalImporte(), entidad.getValorIGV(), entidad.getValorServicio()));

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void calculoCabeceraComprobateExtranjero(Filtro entidad){
		TblComprobante comprobante = null;
		try{
			comprobante = entidad.getComprobante();
			//inicializa
			comprobante.setDescuentosGlobales(new BigDecimal("0"));
			comprobante.setTotalImporte(new BigDecimal("0"));
			comprobante.setValorOpGratuitas(new BigDecimal("0"));
			comprobante.setTotalDescuento(new BigDecimal("0"));
			for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				comprobante.setTotalDescuento(comprobante.getTotalDescuento().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));

				//Total Importe: Precio Unitario x Cantidad - Descuento
				comprobante.setTotalImporte(comprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				comprobante.setTotalImporte(comprobante.getTotalImporte().subtract(comprobante.getDescuentosGlobales()));
			}
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			//comprobante.setTotalDescuento(comprobante.getDescuentosGlobales().add(comprobante.getTotalOtrosCargos()));

			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			//comprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), Constantes.SUNAT_IGV));
			comprobante.setTotalIgv(obtenerTotalImpuestoGravada(comprobante.getTotalImporte(), 0, entidad.getValorServicio())); // IGV = 0
			//Calculo del total sin igv
			comprobante.setTotalOpGravada(new BigDecimal("0"));// IGV = 0
			comprobante.setTotalOpInafecta(obtenerTotalMontoGravada(comprobante.getTotalImporte(), 0 , entidad.getValorServicio()));
			//Calculo del total de otros tributos: servicio
			comprobante.setSumatorioaOtrosTributos(obtenerTotalOtrosTributosGravada(comprobante.getTotalImporte(), 0 , entidad.getValorServicio()));// IGV = 0

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static TblBandejaFacturador setDatosBandeja(BandejaFacturadorBean bandeja, TblComprobante comprobanteEstado, String rucEntidad){
		TblBandejaFacturador bandejaFacturador = new TblBandejaFacturador();
		try{
			bandejaFacturador.setNumeroRuc(validateDatos(bandeja.getNumeroRuc(), rucEntidad));
			bandejaFacturador.setTipoDocumento(validateDatos(bandeja.getTipoDocumento(), comprobanteEstado.getTipoComprobante()));
			bandejaFacturador.setNumeroDocumento(validateDatos(bandeja.getNumeroDocumento(), comprobanteEstado.getSerie()+"-"+comprobanteEstado.getNumero()));
			bandejaFacturador.setFechaCargue(bandeja.getFechaCarga());
			bandejaFacturador.setFechaGeneracion(bandeja.getFechaGeneracion());
			bandejaFacturador.setFechaEnvio(bandeja.getFechaEnvio());
			bandejaFacturador.setObservacion(bandeja.getObservacion());
			bandejaFacturador.setNombreArchivo(validateDatos(bandeja.getNombreArchivo(),rucEntidad+"-"+comprobanteEstado.getTipoComprobante()+"-"+comprobanteEstado.getSerie()+"-"+comprobanteEstado.getNumero()));
			bandejaFacturador.setSituacion(validateDatos(bandeja.getSituacion(),"01"));
			bandejaFacturador.setTipoArchivo(validateDatos(bandeja.getTipoArchivo(),"TXT"));
			bandejaFacturador.setFirmaDigital(bandeja.getFirmaDigital());
			bandejaFacturador.setEstado(Constantes.ESTADO_ACTIVO);


		}catch(Exception e){
			e.printStackTrace();
		}
		return bandejaFacturador;
	}
	
	private static String validateDatos(String dataNew, String dataOld) {
		if (dataNew != null && !dataNew.equals("")){
			return dataNew;
		}else {
			return dataOld;
		}
	}

	public static TblBandejaFacturador editarDatosBandeja(BandejaFacturadorBean bandeja, TblBandejaFacturador bandejaFacturador){

		try{
			bandejaFacturador.setNumeroRuc(validateDatos(bandeja.getNumeroRuc(),bandejaFacturador.getNumeroRuc()));
			bandejaFacturador.setTipoDocumento(validateDatos(bandeja.getTipoDocumento(),bandejaFacturador.getTipoDocumento()));
			bandejaFacturador.setNumeroDocumento(validateDatos(bandeja.getNumeroDocumento(),bandejaFacturador.getNumeroDocumento()));
			bandejaFacturador.setFechaCargue(validateDatos(bandeja.getFechaCarga(),bandejaFacturador.getFechaCargue()));
			bandejaFacturador.setFechaGeneracion(validateDatos(bandeja.getFechaGeneracion(),bandejaFacturador.getFechaGeneracion()));
			bandejaFacturador.setFechaEnvio(validateDatos(bandeja.getFechaEnvio(),bandejaFacturador.getFechaEnvio()));
			bandejaFacturador.setObservacion(validateDatos(bandeja.getObservacion(),bandejaFacturador.getObservacion()));
			bandejaFacturador.setNombreArchivo(validateDatos(bandeja.getNombreArchivo(),bandejaFacturador.getNombreArchivo()));
			bandejaFacturador.setSituacion(validateDatos(bandeja.getSituacion(),bandejaFacturador.getSituacion()));
			bandejaFacturador.setTipoArchivo(validateDatos(bandeja.getTipoArchivo(),bandejaFacturador.getTipoArchivo()));
			bandejaFacturador.setFirmaDigital(validateDatos(bandeja.getFirmaDigital(),bandejaFacturador.getFirmaDigital()));


		}catch(Exception e){
			e.printStackTrace();
		}
		return bandejaFacturador;
	}
	/*
	 * Obtiene la descripcion de la leyenda
	 */
	@SuppressWarnings("unchecked")
	public static String getDescripcionLeyenda(String strCodigoLeyenda, HttpServletRequest request){
		String resultado 						= null;
		Map<String, Object> mapTipoLeyenda		= null;
		Map<String, TblParametro> mapParametro	= null;
		TblParametro parametro 					= null; 
		String strDetraccion					= null;
		try{

			mapTipoLeyenda = (Map<String, Object>)request.getSession().getAttribute("SessionMapTipoLeyendaCodigo");
			if (strCodigoLeyenda.equals(Constantes.SUNAT_LEYENDA_DETRACCION_3001)){
				mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
				if (mapParametro !=null){
					parametro = mapParametro.get(Constantes.PARAMETRO_DETRACCION);
					if (parametro!=null){
						strDetraccion =parametro.getDato();
					}else{
						strDetraccion = "Por registrar Parametro";
					}
				}else{
					strDetraccion = "Por definir Parametros";
				}
				resultado = strDetraccion;
			}else{
				resultado = (String)mapTipoLeyenda.get(strCodigoLeyenda);
				if (resultado !=null && resultado.length()>6){
					//resultado = resultado.substring(6);
					resultado = "";
				}

			}

		}catch(Exception e){
			resultado = "";
			e.printStackTrace();
		}finally{
			mapTipoLeyenda	= null;
			mapParametro	= null;
			parametro 		= null; 
			strDetraccion	= null;
		}
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public static String obtenerParametro(HttpServletRequest request, String strParametro){
		Map<String, TblParametro> mapParametro	= null;
		TblParametro parametro 					= null; 
		String resultado						= "";
		mapParametro = (Map<String, TblParametro>)request.getSession().getAttribute("SessionMapParametroSistema");
		if (mapParametro !=null){
			parametro = mapParametro.get(strParametro);
			if (parametro!=null){
				resultado =parametro.getDato();
			}else{
				resultado = "01";
			}
		}else{
			resultado = "01";
		}
		return resultado;
	}

	public static BigDecimal round(BigDecimal d, int scale, boolean roundUp) {
		int mode = (roundUp) ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
		return d.setScale(scale, mode);
	}
}
