package com.pe.lima.sg.business.inter;

import java.util.List;

import org.springframework.ui.Model;

import com.pe.lima.sg.bean.facturador.EdsConfiguracionBean;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblConfiguracion;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;

public interface IComprobanteFacturaBusiness extends IComprobanteBusiness{

	public TblComprobante asignarDatosFacturaEDS(EdsConfiguracionBean edsConfiguracionBean, TblConfiguracion tblConfiguracion);

	public TblDetalleComprobante asignarDatosProductoFacturaEDS(EdsConfiguracionBean edsConfiguracionBean, TblConfiguracion tblConfiguracion);

	public boolean validarDatosFactura(TblComprobante tblComprobante, Model model);

	public void calculoPrecioFinalProductoFactura(TblDetalleComprobante tblDetalleComprobante);

	public void calculoImpuestosFactura(TblComprobante tblComprobante, List<TblDetalleComprobante> listaDetalleComprobante);

}
