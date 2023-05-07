package com.pe.lima.sg.business.implement;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.pe.lima.sg.business.inter.IComprobanteBusiness;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblLeyenda;
import com.pe.lima.sg.entity.operacion.TblSunatCabecera;
import com.pe.lima.sg.entity.operacion.TblSunatDetalle;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.NumberToLetterConverter;
@SpringBootApplication
@ComponentScan ({"com.pe.lima.sg.business.inter", "com.pe.lima.sg.business.implement"})
public class ComprobanteBusiness implements IComprobanteBusiness {

	@Override
	public TblSunatCabecera obtenerSunatCabecera(TblComprobante comprobante) {
		TblSunatCabecera cabecera = null;
		try{
			cabecera = new TblSunatCabecera();
			//Tipo Operacion
			cabecera.setTipoOperacion(comprobante.getTipoOperacion());
			//Fecha Emision
			cabecera.setFechaEmision(comprobante.getFechaEmision());
			//Domicilio Fiscal
			cabecera.setDomicilioFiscal(new Integer(comprobante.getCodigoDomicilio()));
			//Datos del Cliente
			cabecera.setTipoDocumentoUsuario(comprobante.getTipoDocumento());
			cabecera.setNumeroDocumento(comprobante.getNumeroDocumento());
			cabecera.setRazonSocial(comprobante.getNombreCliente());
			//Moneda
			cabecera.setTipoMoneda(comprobante.getMoneda());
			//Descuentos Globales
			cabecera.setSumaDescuento(comprobante.getDescuentosGlobales());
			cabecera.setSumaCargo(comprobante.getTotalOtrosCargos());
			cabecera.setTotalDescuento(comprobante.getTotalDescuento());
			//Total valor de venta - Operaciones gravadas
			cabecera.setOperacionGravada(comprobante.getTotalOpGravada());
			//Total valor de venta - Operaciones inafectas
			cabecera.setOperacionInafecta(comprobante.getTotalOpInafecta());
			//Total valor de venta - Operaciones exoneradas
			cabecera.setOperacionExonerada(comprobante.getTotalOpExonerada());
			//Sumatoria IGV
			cabecera.setMontoIgv(comprobante.getTotalIgv());
			//Sumatoria ISC
			cabecera.setMontoIsc(comprobante.getSumatoriaIsc());
			//Sumatoria otros tributos
			cabecera.setOtrosTributos(comprobante.getSumatorioaOtrosTributos());
			//Importe total de la venta, cesión en uso o del servicio prestado
			cabecera.setImporteTotal(comprobante.getTotalImporte());
			//Nombre archivo
			cabecera.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_CABECERA);
			//Comprobante
			cabecera.setTblComprobante(comprobante);
		
		}catch(Exception e){
			e.printStackTrace();
			cabecera = null;
		}
		return cabecera;
	}

	@Override
	public TblSunatDetalle obtenerSunatDetalle(TblSunatCabecera cabecera, TblComprobante comprobante, TblDetalleComprobante detalleComprobante) {
		TblSunatDetalle detalle 				= null;
		BigDecimal temporal						= null;
		try{
			//registro del detalle del comprobante
			detalle = new TblSunatDetalle();
			//Código de unidad de medida por ítem
			detalle.setCodigoUnidad(detalleComprobante.getUnidadMedida());
			//Cantidad de unidades por ítem
			detalle.setCantidad(detalleComprobante.getCantidad().toString());
			//Código de producto
			detalle.setCodigoProducto("");
			//Codigo producto SUNAT
			detalle.setCodigoProductoSunat("");
			//Descripción detallada del servicio prestado, bien vendido o cedido en uso, indicando las características.
			detalle.setDescripcion(detalleComprobante.getDescripcion());
			//Valor unitario por ítem
			temporal = this.obtenerTotalMontoGravada(detalleComprobante.getPrecioFinal(), Constantes.SUNAT_IGV, new Integer(0));
			temporal = temporal.divide(new BigDecimal(detalleComprobante.getCantidad().toString()) , 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			
			detalle.setValorUnitario(temporal.toString());
			//Descuentos por item
			BigDecimal descuento = new BigDecimal((detalleComprobante.getPrecioTotal().doubleValue()*detalleComprobante.getDescuento().doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
			detalle.setDescuento(descuento.toString());
			//Monto de IGV por ítem
			//detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), Constantes.SUNAT_IGV).toString());
			detalle.setMontoIgv(this.obtenerTotalImpuestoGravada(detalleComprobante.getPrecioFinal(), Constantes.SUNAT_IGV, new Integer(0)).toString());
			//Afectación al IGV por ítem
			detalle.setAfectacionIgv(detalleComprobante.getTipoAfectacion());
			//Monto de ISC por ítem
			detalle.setMontoIsc("0");
			//Tipo de sistema ISC
			detalle.setTipoIsc("");
			//Precio de venta unitario por item
			detalle.setPrecioVentaUnitario(detalleComprobante.getPrecioTotal().toString());
			//Valor de venta por ítem
			detalle.setValorVentaItem(detalleComprobante.getPrecioFinal().toString());
			//Archivo
			detalle.setNombreArchivo(Constantes.SUNAT_RUC_EMISOR+"-"+comprobante.getTipoComprobante()+"-"+comprobante.getSerie()+"-"+comprobante.getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_DETALLE);
			detalle.setTblSunatCabecera(cabecera);
			
		}catch(Exception e){
			e.printStackTrace();
			detalle = null;
		}
		return detalle;
	}
	/*
	 * Impuesto Gravada
	 */
	public BigDecimal obtenerTotalImpuestoGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100;
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			resultado = new BigDecimal(monto.doubleValue()*(igv)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			
		}catch(Exception e){
			resultado = new BigDecimal("0");
			e.printStackTrace();
		}
		return resultado;
	}
	/*
	 * Monto Gravada
	 */
	public BigDecimal obtenerTotalMontoGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100; 
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			resultado = new BigDecimal(monto.doubleValue()*(100)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			//resultado = new BigDecimal(monto.doubleValue()*(100+igv)/100).setScale(2, RoundingMode.HALF_UP);
			
		}catch(Exception e){
			resultado = new BigDecimal("0");
			e.printStackTrace();
		}
		return resultado;
	}

	public boolean generarArchivoCabecera(TblSunatCabecera cabecera, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		//String FILENAME = "G:\\data0\\facturador\\DATA\\"+cabecera.getNombreArchivo();
		//String FILENAME = Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + cabecera.getNombreArchivo();
		String FILENAME = entidad.getSunatData() + cabecera.getNombreArchivo();
		try{
			cadena = cabecera.getTipoOperacion() + Constantes.SUNAT_PIPE +
					 cabecera.getFechaEmision() + Constantes.SUNAT_PIPE +
					 cabecera.getDomicilioFiscal() + Constantes.SUNAT_PIPE +
					 cabecera.getTipoDocumentoUsuario() + Constantes.SUNAT_PIPE +
					 cabecera.getNumeroDocumento() + Constantes.SUNAT_PIPE +
					 cabecera.getRazonSocial()	+ Constantes.SUNAT_PIPE +
					 cabecera.getTipoMoneda() + Constantes.SUNAT_PIPE +
					 cabecera.getSumaDescuento() + Constantes.SUNAT_PIPE +
					 cabecera.getSumaCargo() + Constantes.SUNAT_PIPE +
					 cabecera.getTotalDescuento() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionGravada() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionInafecta() + Constantes.SUNAT_PIPE +
					 cabecera.getOperacionExonerada() + Constantes.SUNAT_PIPE +
					 cabecera.getMontoIgv() + Constantes.SUNAT_PIPE +
					 cabecera.getMontoIsc() + Constantes.SUNAT_PIPE +
					 cabecera.getOtrosTributos() + Constantes.SUNAT_PIPE +
					 cabecera.getImporteTotal();
			bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
			bufferedWriter.write(cadena); 
			resultado = true;
					
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}finally{
			try{
				if (bufferedWriter !=null){
					bufferedWriter.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		return resultado;
	}
	
	/*
	 * Genera un archivo plano Detalle
	 */
	public boolean generarArchivoDetalle(List<TblSunatDetalle> listaDetalle, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;//Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
		try{
			for(TblSunatDetalle detalle:listaDetalle){
				if (FILENAME == null){
					//FILENAME = Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
					FILENAME = entidad.getSunatData() + detalle.getNombreArchivo();
					bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
				}else{
					bufferedWriter.newLine();
				}
				cadena = detalle.getCodigoUnidad() + Constantes.SUNAT_PIPE +
						 detalle.getCantidad() + Constantes.SUNAT_PIPE +
						 detalle.getCodigoProducto() + Constantes.SUNAT_PIPE +
						 detalle.getCodigoProductoSunat() + Constantes.SUNAT_PIPE +
						 detalle.getDescripcion() + Constantes.SUNAT_PIPE +
						 detalle.getValorUnitario()	+ Constantes.SUNAT_PIPE +
						 detalle.getDescuento() + Constantes.SUNAT_PIPE +
						 detalle.getMontoIgv() + Constantes.SUNAT_PIPE +
						 detalle.getAfectacionIgv() + Constantes.SUNAT_PIPE +
						 detalle.getMontoIsc() + Constantes.SUNAT_PIPE +
						 detalle.getTipoIsc() + Constantes.SUNAT_PIPE +
						 detalle.getPrecioVentaUnitario() + Constantes.SUNAT_PIPE +
						 detalle.getValorVentaItem();
				bufferedWriter.write(cadena); 
				
			}
			resultado = true;
					
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}finally{
			try{
				if (bufferedWriter !=null){
					bufferedWriter.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return resultado;
	}
	/*
	 * Genera un archivo plano Leyenda
	 */
	public boolean generarArchivoLeyenda(TblLeyenda leyenda, TblComprobante comprobante, String nombreFile, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		//String FILENAME = "G:\\data0\\facturador\\DATA\\"+cabecera.getNombreArchivo();
		//String FILENAME = Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + nombreFile;
		String FILENAME = entidad.getSunatData() + nombreFile;
		try{
			
			
			bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
			//Leyenda de moneda
			if (comprobante.getTotalImporte().doubleValue() > 0){
				cadena = Constantes.SUNAT_LEYENDA_MONTO_LETRAS_1000 + 
						Constantes.SUNAT_PIPE + NumberToLetterConverter.convertNumberToLetter(comprobante.getTotalImporte().doubleValue(), comprobante.getMoneda());
			}else{
				//Tomamos el valor de Operaciones Gratuitas
				cadena = Constantes.SUNAT_LEYENDA_MONTO_LETRAS_1000 + 
						Constantes.SUNAT_PIPE + NumberToLetterConverter.convertNumberToLetter(comprobante.getValorOpGratuitas().doubleValue(), comprobante.getMoneda());
			}
			
			bufferedWriter.write(cadena);
			//Leyenda adicional
			if (leyenda !=null && !leyenda.getCodigoSunat().equals("")){
				bufferedWriter.newLine();
				cadena = leyenda.getCodigoSunat() + Constantes.SUNAT_PIPE +	leyenda.getDescripcionSunat();
				bufferedWriter.write(cadena); 
			}
			resultado = true;
					
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}finally{
			try{
				if (bufferedWriter !=null){
					bufferedWriter.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		return resultado;
	}
	/*
	 * Genera un archivo de detalle adicional
	 */
	public boolean generarArchivoAdicionalDetalle(List<TblDetalleComprobante> listaDetalle, Filtro entidad){
		boolean resultado = false;
		String cadena = null;
		BufferedWriter bufferedWriter = null;
		String FILENAME = null;//Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + detalle.getNombreArchivo();
		try{
			for(TblDetalleComprobante detalle:listaDetalle){
				if (detalle.getValorReferencia()!=null && detalle.getValorReferencia().doubleValue()>0){
					if (FILENAME == null){
						//FILENAME =  Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA + Constantes.SUNAT_RUC_EMISOR+"-"+entidad.getComprobante().getTipoComprobante()+"-"+entidad.getComprobante().getSerie()+"-"+entidad.getComprobante().getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_ADICIONAL_DETALLE;
						FILENAME = entidad.getSunatData() + Constantes.SUNAT_RUC_EMISOR+"-"+entidad.getComprobante().getTipoComprobante()+"-"+entidad.getComprobante().getSerie()+"-"+entidad.getComprobante().getNumero()+"."+Constantes.SUNAT_ARCHIVO_EXTENSION_ADICIONAL_DETALLE;
						bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
					}else{
						bufferedWriter.newLine();
					}
					cadena = detalle.getValorReferencia().toString() + Constantes.SUNAT_PIPE + "GRATIS";
					bufferedWriter.write(cadena); 
				}
				
			}
			resultado = true;
					
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}finally{
			try{
				if (bufferedWriter !=null){
					bufferedWriter.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return resultado;
	}
}
