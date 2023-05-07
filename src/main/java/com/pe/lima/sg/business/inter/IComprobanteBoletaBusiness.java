package com.pe.lima.sg.business.inter;

import java.util.List;

import org.springframework.ui.Model;

import com.pe.lima.sg.bean.facturador.EdsConfiguracionBean;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblConfiguracion;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;

public interface IComprobanteBoletaBusiness extends IComprobanteBusiness{

	public TblComprobante asignarDatosBoletaEDS(EdsConfiguracionBean edsConfiguracionBean, TblConfiguracion tblConfiguracion);

	public TblDetalleComprobante asignarDatosProductoBoletaEDS(EdsConfiguracionBean edsConfiguracionBean, TblConfiguracion tblConfiguracion);

	public boolean validarDatosBoleta(TblComprobante tblComprobante, Model model);

	public void calculoPrecioFinalProductoBoleta(TblDetalleComprobante tblDetalleComprobante);

	public void calculoImpuestosBoleta(TblComprobante tblComprobante, List<TblDetalleComprobante> listaDetalleComprobante);

}
