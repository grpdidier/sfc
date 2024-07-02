package com.pe.lima.sg.presentacion.rs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pe.lima.sg.bean.remision.MovimientoBean;
import com.pe.lima.sg.bean.remision.SaldoMercaderiaBean;
import com.pe.lima.sg.bean.remision.TotalesBean;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.UtilSGT;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ReporteMovimientoGreAConsigancionDao {
	
	@Value("${spring.datasource.url}")
	private String urlTienda;
	
	@Value("${spring.datasource.username}")
	private String nombreUsuario;
	
	@Value("${spring.datasource.password}")
	private String credencialUsuario;
	
	private final String userUrl	= "?user=";
	private final String userPass	= "&password=";
	
	private String url;
	
	public List<MovimientoBean> getReporteMovimientoGreAConsignacionXls(Filtro filtro, Map<String, TblProducto> mapProducto){

		SaldoMercaderiaBean saldoMercaderiaBean = null;
		List<SaldoMercaderiaBean> listaSaldoMercaderiaBean 	= null;
		
		List<MovimientoBean> listaMovimiento = null;
		Connection con = null;
		//String url = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BigDecimal negativo = new BigDecimal("-1");
		//String query = null;
		try{
			url = urlTienda + userUrl + nombreUsuario + userPass + credencialUsuario;
			con = DriverManager.getConnection(url);
			pstmt = this.sqlConsultaMovimientoGreAConsignacionXls(filtro, con);
			
			rs = pstmt.executeQuery();

			if (rs !=null){
				listaSaldoMercaderiaBean = new ArrayList<SaldoMercaderiaBean>();
				while(rs.next()){
					saldoMercaderiaBean = new SaldoMercaderiaBean();
					saldoMercaderiaBean.setFechaEmision(rs.getString("fecha_emision"));
					saldoMercaderiaBean.setSerie(rs.getString("serie"));
					saldoMercaderiaBean.setNumero(rs.getString("numero"));
					saldoMercaderiaBean.setRuc(rs.getString("numero_documento"));
					saldoMercaderiaBean.setCliente(rs.getString("nombre_cliente"));
					String[] producto = obtenerProducto(rs.getString("descripcion"));
					saldoMercaderiaBean.setCodigoProducto(producto[0]);
					saldoMercaderiaBean.setNombreProducto(producto[1]);
					saldoMercaderiaBean.setCantidad(rs.getBigDecimal("cantidad"));
					saldoMercaderiaBean.setPrecio(rs.getBigDecimal("precio_unitario"));
					saldoMercaderiaBean.setTotal(rs.getBigDecimal("precio_total"));
					if ("13".equals(rs.getString("motivo_traslado"))) {
						saldoMercaderiaBean.setCantidad(saldoMercaderiaBean.getCantidad().multiply(negativo));
						saldoMercaderiaBean.setTotal(saldoMercaderiaBean.getTotal().multiply(negativo));
					}
					
					listaSaldoMercaderiaBean.add(saldoMercaderiaBean);
				}
				listaMovimiento = obtenerMovimientoProducto(listaSaldoMercaderiaBean,mapProducto);
			}

		}catch(Exception e){
			e.printStackTrace();
			listaMovimiento = null;
		}finally{
			saldoMercaderiaBean = null;
			rs = null;
			pstmt = null;
			//query = null;
			try{
				if (con !=null && !con.isClosed()){
					con.close();
					con = null;
				}
			}catch(Exception e){
				e.printStackTrace();
				con = null;
			}
		}
		return listaMovimiento;
	}
	
	private List<MovimientoBean> obtenerMovimientoProducto(List<SaldoMercaderiaBean> listaSaldoMercaderiaBean, Map<String, TblProducto> mapProducto) {
		TotalesBean totales = null;
		List<MovimientoBean> listaMovimiento = new ArrayList<>();
		List<SaldoMercaderiaBean> listaDatos = null;
		MovimientoBean movimientoBean = null;
		String productoAnterior = "";
		for(SaldoMercaderiaBean mercaderia:listaSaldoMercaderiaBean) {
			if (mercaderia.getNombreProducto().equals(productoAnterior)) {
				//Asignamos los datos
				asignarPrecioProducto(mercaderia,mapProducto);
				movimientoBean.getListaDatos().add(mercaderia);
				totalizarDatos(mercaderia, totales);
			}else {
				totales = new TotalesBean();
				listaDatos = new ArrayList<>();
				movimientoBean = new MovimientoBean();
				movimientoBean.setTotales(totales);
				movimientoBean.setListaDatos(listaDatos);
				listaMovimiento.add(movimientoBean);
				//Asignamos los datos
				productoAnterior = mercaderia.getNombreProducto();
				asignarPrecioProducto(mercaderia,mapProducto);
				movimientoBean.getListaDatos().add(mercaderia);
				totalizarDatos(mercaderia, totales);				
				
			}
		}
		
		return listaMovimiento;
	}

	private void asignarPrecioProducto(SaldoMercaderiaBean mercaderia, Map<String, TblProducto> mapProducto) {
		if (mercaderia.getTotal().compareTo(new BigDecimal("0"))==0) {
			TblProducto producto = mapProducto.get(mercaderia.getCodigoProducto());
			if (producto != null) {
				mercaderia.setPrecio(producto.getPrecio());
				mercaderia.setTotal(mercaderia.getCantidad().multiply(mercaderia.getPrecio()).setScale(2, RoundingMode.HALF_UP));
			}
				
		}
		
	}

	private void totalizarDatos(SaldoMercaderiaBean mercaderia, TotalesBean totales) {
		totales.setNombreProducto(mercaderia.getNombreProducto());
		totales.setTotalCantidad(totales.getTotalCantidad().add(mercaderia.getCantidad()));
		totales.setTotal(totales.getTotal().add(mercaderia.getTotal()));
	}

	private String[] obtenerProducto(String datoProducto) {
		String[] producto = null;
		if (datoProducto.contains(":")) {
			 producto = datoProducto.split(":");
		}else {
			producto = new String[2];
			producto[0] = "";
			producto[1] = datoProducto;
		}
		return producto;
	}

	private PreparedStatement sqlConsultaMovimientoGreAConsignacionXls(Filtro filtro, Connection con) throws SQLException{
		String query = null;
		PreparedStatement pstmt = null;

		query = this.sqlAllConsultaMovimientoGreAConsignacion();
		System.out.println("query:"+query);
		pstmt = con.prepareStatement(query);
		pstmt.setString(1, formateaFechaYYYYMMDD(filtro.getFechaInicio()));
		pstmt.setString(2, formateaFechaYYYYMMDD(filtro.getFechaFin()));
		pstmt.setInt(3, filtro.getCodigoEdificacion());
		pstmt.setString(4, validaRazonSocial(filtro.getRazonSocial()));
		pstmt.setString(5, formateaFechaYYYYMMDD(filtro.getFechaInicio()));
		pstmt.setString(6, formateaFechaYYYYMMDD(filtro.getFechaFin()));
		pstmt.setInt(7, filtro.getCodigoEdificacion());
		pstmt.setString(8, validaRazonSocial(filtro.getRazonSocial()));
		log.debug(query);
		return pstmt;
	}
	
	private String validaRazonSocial(String razonSocial) {
		String cadena = null;
		if (razonSocial == null || razonSocial.trim().equals("")) {
			cadena = "%";
		}else {
			cadena = "%"+razonSocial.trim().toUpperCase() + "%";
		}
		return cadena;
	}

	private String formateaFechaYYYYMMDD(String fechaInicio) {
		Date fecha = UtilSGT.getDatetoString(fechaInicio);
		String fechaString = UtilSGT.getDateStringFormat(fecha);
		return fechaString;
	}

	private String sqlAllConsultaMovimientoGreAConsignacion() throws SQLException{
		String query = null;
		query = "select * " + 
				" from  " + 
				" ( select to_char(to_date(com.fecha_emision,'yyyy-MM-dd'),'dd/MM/yyyy') as fecha_emision,    " + 
				"    com.serie,    " + 
				"     com.numero,    " + 
				"     com.numero_documento,    " + 
				"     com.nombre_cliente,    " + 
				"     det.descripcion,    " + 
				"     (det.cantidad*(-1)) as cantidad, " + 
				"     det.precio_unitario, " + 
				"     (det.precio_total*(-1)) as precio_total, " + 
				"     '00' as motivo_traslado "+
				" from ope.tbl_comprobante com,    " + 
				"      ope.tbl_detalle_comprobante det   " + 
				" where com.codigo_comprobante = det.codigo_comprobante    " + 
				"    and com.estado = '1'    " + 
				"    and com.estado_operacion = '07'" + //Estado del comprobante: ENVIADO Y VALIDADO POR OSE
				"    and com.fecha_emision >= ?     " + 
				"    and com.fecha_emision <= ?       " + 
				"    and com.codigo_entidad = ?   " + 
				"    and upper(com.nombre_cliente) like ?   " + 
				" Union " + 
				"  " + 
				" select rem.fecha_emision, " + 
				" 	rem.serie, " + 
				"    rem.numero, " + 
				"    rem.numero_documento_cliente as numero_documento, " + 
				"    rem.nombre_cliente, " + 
				"    dre.descripcion, " + 
				"    dre.cantidad, " + 
				"    0 as precio_unitario, " + 
				"    0 as precio_total, " + 
				"    rem.motivo_traslado "+
				" from ope.tbl_remision rem, " + 
				" 	  ope.tbl_factura_asociada aso, " + 
				"      ope.tbl_detalle_remision dre " + 
				" where rem.codigo_remision = aso.codigo_remision " + 
				" 	and aso.codigo_factura_asociada = dre.codigo_factura_asociada " + 
				"   and rem.estado_operacion not in ('06') "+
				"    and rem.estado = '1' " + 
				"    and to_char(to_date(rem.fecha_emision,'dd/MM/yyyy'),'yyyy-MM-dd') >= ?     " + 
				"    and to_char(to_date(rem.fecha_emision,'dd/MM/yyyy'),'yyyy-MM-dd') <= ?       " + 
				"    and aso.codigo_comprobante = 0 " + 
				"    and rem.codigo_entidad = ?   " + 				
				"    and upper(rem.nombre_cliente) like ?  " + 
				" ) tabla " + 
				" order by 6,1";
				
		return query;
	}
	
	
	
	
	
}
