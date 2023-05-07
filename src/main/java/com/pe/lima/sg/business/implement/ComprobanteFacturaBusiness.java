package com.pe.lima.sg.business.implement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.ui.Model;

import com.pe.lima.sg.bean.facturador.EdsConfiguracionBean;
import com.pe.lima.sg.business.inter.IComprobanteFacturaBusiness;
import com.pe.lima.sg.dao.mantenimiento.ISerieDAO;
import com.pe.lima.sg.entity.mantenimiento.TblSerie;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblConfiguracion;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.UtilSGT;
@SpringBootApplication
@ComponentScan ({"com.pe.lima.sg.business.inter", "com.pe.lima.sg.business.implement"})
public class ComprobanteFacturaBusiness extends ComprobanteBusiness implements IComprobanteFacturaBusiness{
	@Autowired
	private ISerieDAO serieDao;
	
	/**
	 * Asigna los datos de la boleta
	 */
	@Override
	public TblComprobante asignarDatosFacturaEDS(EdsConfiguracionBean edsConfiguracionBean, TblConfiguracion tblConfiguracion) {
		TblComprobante tblComprobante = new TblComprobante();
		TblSerie serieFactura					= null;
		
		try{
			//Tipo de Operacion
			tblComprobante.setTipoOperacion(tblConfiguracion.getTipoOperacion());
			//Codigo Domicilio Fiscal
			tblComprobante.setCodigoDomicilio(tblConfiguracion.getCodigoDomicilio());
			//Fecha Emision
			tblComprobante.setFechaEmision(UtilSGT.getFecha("yyyy-MM-dd"));
			//Fecha Vencimiento
			tblComprobante.setFechaVencimiento(UtilSGT.getFecha("yyyy-MM-dd"));
			//Tipo de Comprobante
			tblComprobante.setTipoComprobante(Constantes.TIPO_COMPROBANTE_FACTURA);
			//Serie
			tblComprobante.setSerie(tblConfiguracion.getSerie());
			//Numero
			serieFactura = serieDao.buscarOneByTipoComprobante(Constantes.TIPO_COMPROBANTE_FACTURA);
			serieFactura.setNumeroComprobante(serieFactura.getNumeroComprobante() + 1);
			serieDao.save(serieFactura);
			tblComprobante.setNumero(UtilSGT.completarCeros(serieFactura.getNumeroComprobante().toString(),Constantes.SUNAT_LONGITUD_NUMERO));
			//Moneda
			tblComprobante.setMoneda(tblConfiguracion.getMoneda());
			//Tipo Documento
			tblComprobante.setTipoDocumento(Constantes.SUNAT_PARAMETRO_RUC);
			//Numero Documento
			tblComprobante.setNumeroDocumento(edsConfiguracionBean.getStrRuc());
			//Nombre o Razon Social
			tblComprobante.setNombreCliente(edsConfiguracionBean.getStrRazonSocial());
			//Direccion del cliente
			tblComprobante.setDireccionCliente(tblConfiguracion.getDireccion());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			serieFactura = null;
		}
		return tblComprobante;
	}

	/**
	 * Asigna los datos del detalle de la boleta
	 */
	@Override
	public TblDetalleComprobante asignarDatosProductoFacturaEDS(EdsConfiguracionBean edsConfiguracionBean, TblConfiguracion tblConfiguracion) {
		TblDetalleComprobante tblDetalleComprobante = new TblDetalleComprobante();
		try{
			//Unidad de medida
			tblDetalleComprobante.setUnidadMedida(tblConfiguracion.getUnidadMedida());
			//Producto
			tblDetalleComprobante.setDescripcion(edsConfiguracionBean.getStrTipoProducto());
			//Precio Unitario
			tblDetalleComprobante.setPrecioUnitario(edsConfiguracionBean.getPrecioUnitario());
			//Cantidad
			tblDetalleComprobante.setCantidad(edsConfiguracionBean.getValorVenta().divide(edsConfiguracionBean.getPrecioUnitario()).setScale(2, RoundingMode.HALF_UP)); //TODO: CAMBIAR
			//Tipo de Afectacion
			tblDetalleComprobante.setTipoAfectacion(tblConfiguracion.getAfectacionIgv());
			//Total
			tblDetalleComprobante.setPrecioFinal(edsConfiguracionBean.getValorVenta());
			tblDetalleComprobante.setPrecioTotal(edsConfiguracionBean.getValorVenta());
			//Descuento
			tblDetalleComprobante.setDescuento(new BigDecimal("0"));
			//Valor de referencia
			tblDetalleComprobante.setValorReferencia(new BigDecimal("0"));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		return tblDetalleComprobante;
	}
	
	/**
	 * Valida los datos de la cabecera de la boleta
	 */
	@Override
	public boolean validarDatosFactura(TblComprobante entidad, Model model) {
		boolean exitoso = true;
		try{
			//Tipo de Operacion 
			if (entidad.getTipoOperacion() == null || entidad.getTipoOperacion().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de operación");
				return exitoso;
			}
			//Codigo Domicilio Fiscal
			if (entidad.getCodigoDomicilio() == null || entidad.getCodigoDomicilio().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el domicilio fiscal o anexo");
				return exitoso;
			}
			//Fecha Emision
			if (entidad.getFechaEmision() == null || entidad.getFechaEmision().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la fecha de emisión");
				return exitoso;
			}
			//Tipo de Comprobante
			if (entidad.getTipoComprobante() == null || entidad.getTipoComprobante().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de comprobante");
				return exitoso;
			}
			//Serie
			if (entidad.getSerie() == null || entidad.getSerie().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar la serie del comprobante");
				return exitoso;
			}
			//Numero
			if (entidad.getNumero() == null || entidad.getNumero().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el número del comprobante");
				return exitoso;
			}else{
				entidad.setNumero(UtilSGT.completarCeros(entidad.getNumero(), Constantes.SUNAT_LONGITUD_NUMERO));
			}
			//Validando existencia del comprobante
			/*total = comprobanteDao.totalComprobante(entidad.getTipoComprobante(), entidad.getSerie(), entidad.getNumero());
			if (total > 0){
				exitoso = false;
				//model.addAttribute("respuesta", "El número y la serie para el comprobante ingresado ya existe ["+entidad.getSerie()+"-"+entidad.getNumero()+"]");
				model.addAttribute("respuesta", "Se encontró un comprobante anteriormente registrado Tipo ["+entidad.getTipoComprobante()+"] - Serie ["+entidad.getSerie()+"] - Numero ["+entidad.getNumero()+"]");
				return exitoso;
			}*/
			//Moneda
			if (entidad.getMoneda() == null || entidad.getMoneda().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de moneda");
				return exitoso;
			}
			//Tipo Documento
			if (entidad.getTipoDocumento() == null || entidad.getTipoDocumento().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el tipo de documento del cliente");
				return exitoso;
			}
			//Numero Documento
			if (entidad.getNumeroDocumento()== null || entidad.getNumeroDocumento().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el numero del documento del cliente");
				return exitoso;
			}
			//Nombre o Razon Social
			if (entidad.getNombreCliente()== null || entidad.getNombreCliente().equals("")){
				exitoso = false;
				model.addAttribute("respuesta", "Debe ingresar el nombre o razón social del cliente");
				return exitoso;
			}

				
		}catch(Exception e){
			exitoso = false;
		}
		return exitoso;
	}

	/**
	 * Calcula el precio final de un producto (un detalle del comprobante)
	 */
	@Override
	public void calculoPrecioFinalProductoFactura(TblDetalleComprobante tblDetalleComprobante) {
		try{
			tblDetalleComprobante.setPrecioTotal(tblDetalleComprobante.getPrecioUnitario().multiply(tblDetalleComprobante.getCantidad()));
			if (tblDetalleComprobante.getDescuento()!=null && tblDetalleComprobante.getDescuento().doubleValue()>0){
				tblDetalleComprobante.setPrecioFinal(new BigDecimal(tblDetalleComprobante.getPrecioTotal().doubleValue() - tblDetalleComprobante.getPrecioTotal().doubleValue()*tblDetalleComprobante.getDescuento().doubleValue()/100).setScale(2, RoundingMode.HALF_UP));
			}else{
				tblDetalleComprobante.setPrecioFinal(tblDetalleComprobante.getPrecioTotal());
			}
				
		}catch(Exception e){
			
		}
		
	}

	/**
	 * Calculo de los impuestos, descuentos y totales de la boleta
	 */
	@Override
	public void calculoImpuestosFactura(TblComprobante tblComprobante, List<TblDetalleComprobante> listaDetalleComprobante) {
		
		try{
			//inicializa
			tblComprobante.setDescuentosGlobales(new BigDecimal("0"));
			tblComprobante.setTotalImporte(new BigDecimal("0"));
			tblComprobante.setValorOpGratuitas(new BigDecimal("0"));
			for(TblDetalleComprobante detalle: listaDetalleComprobante){
				//Descuento : Precio Total - Precio Final (monto de descuento)
				tblComprobante.setDescuentosGlobales(tblComprobante.getDescuentosGlobales().add(detalle.getPrecioTotal().subtract(detalle.getPrecioFinal())));
				
				//Total Importe: Precio Unitario x Cantidad - Descuento
				tblComprobante.setTotalImporte(tblComprobante.getTotalImporte().add(detalle.getPrecioUnitario().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
				tblComprobante.setTotalImporte(tblComprobante.getTotalImporte().subtract(tblComprobante.getDescuentosGlobales()));
				//Total de valor de referencia
				tblComprobante.setValorOpGratuitas(tblComprobante.getValorOpGratuitas().add(detalle.getValorReferencia().multiply(detalle.getCantidad().setScale(2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP));
			}
			//Total otros cargos: no se aplica, se deja cero por defaul
			//Total Descuento: Descuento Global + Sumatoria otros Cargos
			tblComprobante.setTotalDescuento(tblComprobante.getDescuentosGlobales().add(tblComprobante.getTotalOtrosCargos()));
			
			//Se calcula el IGV y el valor total de venta
			//TODO: Validar el tipo de afectacion para obtener el valor del IGV
			//Calculo del monto del igv
			tblComprobante.setTotalIgv(this.obtenerTotalImpuestoGravada(tblComprobante.getTotalImporte(), Constantes.SUNAT_IGV, 0));
			//Calculo del total sin igv
			tblComprobante.setTotalOpGravada(this.obtenerTotalMontoGravada(tblComprobante.getTotalImporte(), Constantes.SUNAT_IGV, 0));
			//Calculo del total de otros tributos: servicio
			tblComprobante.setSumatorioaOtrosTributos(this.obtenerTotalOtrosTributosGravada(tblComprobante.getTotalImporte(), Constantes.SUNAT_IGV, 0));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
	 * Impuesto Gravada
	 */
	public BigDecimal obtenerTotalOtrosTributosGravada(BigDecimal monto, Integer igv, Integer servicio){
		BigDecimal resultado = null;
		Integer totalImpuesto = 100;
		try{
			totalImpuesto = totalImpuesto + igv + servicio;
			resultado = new BigDecimal(monto.doubleValue()*(servicio)/totalImpuesto).setScale(2, RoundingMode.HALF_UP);
			
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

}
