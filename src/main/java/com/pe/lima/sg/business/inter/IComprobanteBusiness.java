package com.pe.lima.sg.business.inter;

import java.util.List;

import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblLeyenda;
import com.pe.lima.sg.entity.operacion.TblSunatCabecera;
import com.pe.lima.sg.entity.operacion.TblSunatDetalle;
import com.pe.lima.sg.presentacion.Filtro;

public interface IComprobanteBusiness{
	
	public TblSunatCabecera obtenerSunatCabecera(TblComprobante comprobante);
	
	public TblSunatDetalle obtenerSunatDetalle(TblSunatCabecera cabecera, TblComprobante comprobante, TblDetalleComprobante detalleComprobante);
	
	public boolean generarArchivoCabecera(TblSunatCabecera cabecera, Filtro entidad);
	
	public boolean generarArchivoDetalle(List<TblSunatDetalle> listaDetalle, Filtro entidad);
	
	public boolean generarArchivoLeyenda(TblLeyenda leyenda, TblComprobante comprobante, String nombreFile, Filtro entidad);
	
	public boolean generarArchivoAdicionalDetalle(List<TblDetalleComprobante> listaDetalle, Filtro entidad);
}
