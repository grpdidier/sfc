package com.pe.lima.sg.presentacion.rs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pe.lima.sg.bean.remision.GuiaConsignacionBean;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.UtilSGT;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class RemisionReporteGuiaConsingacionDao {
	
	@Value("${spring.datasource.url}")
	private String urlTienda;
	
	@Value("${spring.datasource.username}")
	private String nombreUsuario;
	
	@Value("${spring.datasource.password}")
	private String credencialUsuario;
	
	private final String userUrl	= "?user=";
	private final String userPass	= "&password=";
	
	private String url;
	
	public List<GuiaConsignacionBean> getReporteGuiaxConsignacionXls(Filtro filtro){

		GuiaConsignacionBean guiaConsignacionBean = null;
		List<GuiaConsignacionBean> listaGuiaConsignacionBean	= null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			url = urlTienda + userUrl + nombreUsuario + userPass + credencialUsuario;
			con = DriverManager.getConnection(url);
			pstmt = this.sqlConsultaGuiaxConsignacionXls(filtro, con);
			
			rs = pstmt.executeQuery();

			if (rs !=null){
				listaGuiaConsignacionBean = new ArrayList<GuiaConsignacionBean>();
				while(rs.next()){
					guiaConsignacionBean = new GuiaConsignacionBean();
					guiaConsignacionBean.setFechaEmision(rs.getString("fecha_emision"));
					guiaConsignacionBean.setSerie(rs.getString("serie"));
					guiaConsignacionBean.setNumero(rs.getString("numero"));
					guiaConsignacionBean.setRuc(rs.getString("numero_documento_cliente"));
					guiaConsignacionBean.setCliente(rs.getString("nombre_cliente"));
					String[] producto = obtenerProducto(rs.getString("descripcion"));
					guiaConsignacionBean.setCodigoProducto(producto[0]);
					guiaConsignacionBean.setNombreProducto(producto[1]);
					guiaConsignacionBean.setCantidad(rs.getBigDecimal("cantidad"));
					
					listaGuiaConsignacionBean.add(guiaConsignacionBean);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
			listaGuiaConsignacionBean = null;
		}finally{
			guiaConsignacionBean = null;
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
		return listaGuiaConsignacionBean;
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

	private PreparedStatement sqlConsultaGuiaxConsignacionXls(Filtro filtro, Connection con) throws SQLException{
		String query = null;
		PreparedStatement pstmt = null;

		query = this.sqlAllConsultaGuiaxConsignacionXls();
		System.out.println("query:"+query);
		pstmt = con.prepareStatement(query);
		pstmt.setString(1, formateaFechaYYYYMMDD(filtro.getFechaInicio()));
		pstmt.setString(2, formateaFechaYYYYMMDD(filtro.getFechaFin()));
		pstmt.setInt(3, filtro.getCodigoEdificacion());
		pstmt.setString(4, validaRazonSocial(filtro.getRazonSocial()));
		log.info("[sqlConsultaGuiaxConsignacionXls] fecha Inicio:"+formateaFechaYYYYMMDD(filtro.getFechaInicio()));
		log.info("[sqlConsultaGuiaxConsignacionXls] fecha Fin:"+formateaFechaYYYYMMDD(filtro.getFechaFin()));
		log.info("[sqlConsultaGuiaxConsignacionXls] Empresa:"+filtro.getCodigoEdificacion());
		
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

	private String sqlAllConsultaGuiaxConsignacionXls() throws SQLException{
		String query = null;
		query = "select rem.fecha_emision, " + 
				"	rem.serie, " + 
				"    rem.numero, " + 
				"    rem.numero_documento_cliente, " + 
				"    rem.nombre_cliente, " + 
				"    det.descripcion, " + 
				"    det.cantidad " + 
				"from ope.tbl_remision rem, " + 
				"	ope.tbl_factura_asociada fac, " + 
				"	ope.tbl_detalle_remision det " + 
				"where rem.codigo_remision = fac.codigo_remision " + 
				"	 and fac.codigo_factura_asociada = det.codigo_factura_asociada " + 
				"    and rem.estado = '1' " + 
				"    and rem.motivo_traslado = '05' "+
				"    and to_char(to_date(rem.fecha_emision,'dd/mm/yyyy'),'yyyy-mm-dd')>= ?  " + 
				"    and to_char(to_date(rem.fecha_emision,'dd/mm/yyyy'),'yyyy-mm-dd')<= ? " + 
				"    and rem.codigo_entidad = ? " +
				"    and upper(rem.nombre_cliente) like ? " +
				"order by 1,2,3";
				
		return query;
	}
	
	
	
	
	
}
