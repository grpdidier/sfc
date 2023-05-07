package com.pe.lima.sg.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.pe.lima.sg.bean.facturador.BandejaFacturadorBean;
import com.pe.lima.sg.bean.facturador.BandejaFacturadorNotaBean;
import com.pe.lima.sg.bean.facturador.CatalogoErrorFacturadorBean;
import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.presentacion.Filtro;

public class OperacionFacturadorImp implements IOperacionFacturador {

	/*
	 * Consulta la bandeja del facturador para obtener la informacion del proceso
	 */
	@Override
	public BandejaFacturadorBean consultarBandejaComprobantes(BandejaFacturadorBean bandeja, Filtro entidad)  {
		BandejaFacturadorBean resultado = null;
		Connection connection 			= null;
		//Statement statement 			= null;
		PreparedStatement preparedStatement = null;
		String strSQL					= null;
		ResultSet rs					= null;
		try{
			connection = ConexionDB.openDataBase(entidad.getSunatBD());
			strSQL = " SELECT "
					+ "NUM_RUC, "
					+ "TIP_DOCU, "
					+ "NUM_DOCU, "
					+ "FEC_CARG, "
					+ "FEC_GENE, "
					+ "FEC_ENVI, "
					+ "DES_OBSE, "
					+ "NOM_ARCH, "
					+ "IND_SITU, "
					+ "TIP_ARCH, "
					+ "FIRM_DIGITAL "
					+ "FROM DOCUMENTO "
					+ "WHERE NUM_RUC = ? AND TIP_DOCU = ? AND NUM_DOCU = ?";
			preparedStatement = connection.prepareStatement(strSQL);
			//PK: Nro Ruc, Tipo Documento y Numero del Documento
			preparedStatement.setString(1, bandeja.getNumeroRuc());
			preparedStatement.setString(2, bandeja.getTipoDocumento());
			preparedStatement.setString(3, bandeja.getNumeroDocumento());
			rs = preparedStatement.executeQuery();
			while (rs!= null && rs.next()) {
				resultado = new BandejaFacturadorBean();
				resultado.setFechaCarga		(rs.getString("FEC_CARG"));
				resultado.setFechaEnvio		(rs.getString("FEC_ENVI"));
				resultado.setFechaGeneracion(rs.getString("FEC_GENE"));
				resultado.setFirmaDigital	(rs.getString("FIRM_DIGITAL"));
				resultado.setNombreArchivo	(rs.getString("NOM_ARCH"));
				resultado.setNumeroDocumento(rs.getString("NUM_DOCU"));
				resultado.setNumeroRuc		(rs.getString("NUM_RUC"));
				resultado.setObservacion	(rs.getString("DES_OBSE"));
				resultado.setSituacion		(rs.getString("IND_SITU"));
				resultado.setTipoArchivo	(rs.getString("TIP_ARCH"));
				resultado.setTipoDocumento	(rs.getString("TIP_DOCU"));

			}
			ConexionDB.closeDataBase(connection);
		}catch(Exception e){
			resultado = new BandejaFacturadorBean();
		}
		return resultado;
	}
	/*
	 * Obtiene un Bean de Errores
	 */
	@Override
	public CatalogoErrorFacturadorBean consultarCatalogoError(CatalogoErrorFacturadorBean error, Filtro entidad) throws Exception {
		CatalogoErrorFacturadorBean resultado = null;
		Connection connection 			= null;
		//Statement statement 			= null;
		PreparedStatement preparedStatement = null;
		String strSQL					= null;
		ResultSet rs					= null;
		try{
			connection = ConexionDB.openDataBase(entidad.getSunatBD());
			strSQL = "SELECT "
					+ "COD_CATAERRO, "
					+ "NOM_CATAERRO, "
					+ "IND_ESTADO "
					+ "FROM ERROR "
					+ "WHERE COD_CATAERRO = ?";
			preparedStatement = connection.prepareStatement(strSQL);
			//PK: Codigo del error
			preparedStatement.setString(1, error.getCodigoError());
			rs = preparedStatement.executeQuery();

			while (rs!= null && rs.next()) {
				resultado = new CatalogoErrorFacturadorBean();	
				resultado.setCodigoError	(rs.getString("COD_CATAERRO"));
				resultado.setNombreError	(rs.getString("NOM_CATAERRO"));
				resultado.setEstado			(rs.getString("IND_ESTADO"));
			}
		}catch(Exception e){
			resultado = null;
			throw e;
		}
		return resultado;
	}
	/*
	 * Obtiene un Bean de Parametros
	 */
	@Override
	public List<ParametroFacturadorBean> consultarParametro(String idParametro, Filtro entidad) throws Exception {
		List<ParametroFacturadorBean> listado 	= null;
		ParametroFacturadorBean parametro		= null;
		Connection connection 					= null;
		//Statement statement 			= null;
		PreparedStatement preparedStatement 	= null;
		String strSQL							= null;
		ResultSet rs							= null;
		try{
			System.out.println("[consultarParametro] BD: "+entidad.getSunatBD());
			connection = ConexionDB.openDataBase(entidad.getSunatBD());
			strSQL = "SELECT "
					+ "ID_PARA, "
					+ "COD_PARA, "
					+ "NOM_PARA, "
					+ "TIP_PARA, "
					+ "VAL_PARA, "
					+ "IND_ESTA_PARA "
					+ "FROM PARAMETRO "
					+ "WHERE ID_PARA = ? ";
			preparedStatement = connection.prepareStatement(strSQL);
			//PK: Codigo del error
			preparedStatement.setString(1, idParametro);
			rs = preparedStatement.executeQuery();

			while (rs!= null && rs.next()) {
				parametro = new ParametroFacturadorBean();	
				parametro.setIdParametro		(rs.getString("ID_PARA"));
				parametro.setCodigoParametro	(rs.getString("COD_PARA"));
				parametro.setNombreParametro	(rs.getString("NOM_PARA"));
				parametro.setTipoParametro		(rs.getString("TIP_PARA"));
				parametro.setValorParametro		(rs.getString("VAL_PARA"));
				parametro.setIndicadorParametro	(rs.getString("IND_ESTA_PARA"));
				if (listado == null){
					listado = new ArrayList<ParametroFacturadorBean>();
					listado.add(parametro);
				}else{
					listado.add(parametro);
				}
			}
		}catch(Exception e){
			listado = null;
			throw e;
		}
		return listado;
	}
	@Override
	public BandejaFacturadorNotaBean consultarBandejaNota(BandejaFacturadorNotaBean bandeja, Filtro entidad) throws Exception {
		BandejaFacturadorNotaBean resultado = null;
		Connection connection 			= null;
		//Statement statement 			= null;
		PreparedStatement preparedStatement = null;
		String strSQL					= null;
		ResultSet rs					= null;
		try{
			connection = ConexionDB.openDataBase(entidad.getSunatBD());
			strSQL = " SELECT "
					+ "NUM_RUC, "
					+ "TIP_DOCU, "
					+ "NUM_DOCU, "
					+ "FEC_CARG, "
					+ "FEC_GENE, "
					+ "FEC_ENVI, "
					+ "DES_OBSE, "
					+ "NOM_ARCH, "
					+ "IND_SITU, "
					+ "TIP_ARCH, "
					+ "FIRM_DIGITAL "
					+ "FROM DOCUMENTO "
					+ "WHERE NUM_RUC = ? AND TIP_DOCU = ? AND NUM_DOCU = ?";
			preparedStatement = connection.prepareStatement(strSQL);
			//PK: Nro Ruc, Tipo Documento y Numero del Documento
			preparedStatement.setString(1, bandeja.getNumeroRuc());
			preparedStatement.setString(2, bandeja.getTipoDocumento());
			preparedStatement.setString(3, bandeja.getNumeroDocumento());
			rs = preparedStatement.executeQuery();
			while (rs!= null && rs.next()) {
				resultado = new BandejaFacturadorNotaBean();
				resultado.setFechaCarga		(rs.getString("FEC_CARG"));
				resultado.setFechaEnvio		(rs.getString("FEC_ENVI"));
				resultado.setFechaGeneracion(rs.getString("FEC_GENE"));
				resultado.setFirmaDigital	(rs.getString("FIRM_DIGITAL"));
				resultado.setNombreArchivo	(rs.getString("NOM_ARCH"));
				resultado.setNumeroDocumento(rs.getString("NUM_DOCU"));
				resultado.setNumeroRuc		(rs.getString("NUM_RUC"));
				resultado.setObservacion	(rs.getString("DES_OBSE"));
				resultado.setSituacion		(rs.getString("IND_SITU"));
				resultado.setTipoArchivo	(rs.getString("TIP_ARCH"));
				resultado.setTipoDocumento	(rs.getString("TIP_DOCU"));
	
			}
			ConexionDB.closeDataBase(connection);
		}catch(Exception e){
			resultado = null;
			throw e;
		}
		return resultado;
	}


}
