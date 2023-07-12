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

import com.pe.lima.sg.bean.remision.SaldoMercaderiaBean;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.UtilSGT;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class RemisionReporteSaldoMercaderiaDao {
	
	@Value("${spring.datasource.url}")
	private String urlTienda;
	
	@Value("${spring.datasource.username}")
	private String nombreUsuario;
	
	@Value("${spring.datasource.password}")
	private String credencialUsuario;
	
	private final String userUrl	= "?user=";
	private final String userPass	= "&password=";
	
	private String url;
	
	public List<SaldoMercaderiaBean> getReporteSaldoMercaderiaXls(Filtro filtro){

		SaldoMercaderiaBean saldoMercaderiaBean = null;
		List<SaldoMercaderiaBean> listaSaldoMercaderiaBean 	= null;
		Connection con = null;
		//String url = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//String query = null;
		try{
			url = urlTienda + userUrl + nombreUsuario + userPass + credencialUsuario;
			con = DriverManager.getConnection(url);
			pstmt = this.sqlConsultaSaldoMercaderiaXls(filtro, con);
			
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
					saldoMercaderiaBean.setSaldo(rs.getBigDecimal("saldo"));
					
					listaSaldoMercaderiaBean.add(saldoMercaderiaBean);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
			listaSaldoMercaderiaBean = null;
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
		return listaSaldoMercaderiaBean;
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

	private PreparedStatement sqlConsultaSaldoMercaderiaXls(Filtro filtro, Connection con) throws SQLException{
		String query = null;
		PreparedStatement pstmt = null;

		query = this.sqlAllConsultaSaldoMercaderiaXls();
		System.out.println("query:"+query);
		pstmt = con.prepareStatement(query);
		pstmt.setString(1, formateaFechaYYYYMMDD(filtro.getFechaInicio()));
		pstmt.setString(2, formateaFechaYYYYMMDD(filtro.getFechaFin()));
		pstmt.setInt(3, filtro.getCodigoEdificacion());
		log.debug(query);
		return pstmt;
	}
	
	private String formateaFechaYYYYMMDD(String fechaInicio) {
		Date fecha = UtilSGT.getDatetoString(fechaInicio);
		String fechaString = UtilSGT.getDateStringFormat(fecha);
		return fechaString;
	}

	private String sqlAllConsultaSaldoMercaderiaXls() throws SQLException{
		String query = null;
		query = "select com.fecha_emision, " + 
				"	com.serie, " + 
				"    com.numero, " + 
				"    com.numero_documento, " + 
				"    com.nombre_cliente, " + 
				"    det.descripcion, " + 
				"    det.cantidad - COALESCE(det.cantidad_guia,0) as saldo " + 
				"from ope.tbl_comprobante com, " + 
				"     ope.tbl_detalle_comprobante det " + 
				"where com.codigo_comprobante = det.codigo_comprobante " + 
				"	and com.estado = '1' " + 
				"    and com.fecha_emision >= ?  " + 
				"    and com.fecha_emision <= ? " + 
				"    and com.codigo_entidad = ? " + 
				"order by 1,2,3";
				
		return query;
	}
	
	
	
	
	
}
