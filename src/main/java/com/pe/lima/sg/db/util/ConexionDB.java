package com.pe.lima.sg.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {

	/*
	 * Abre la conexion con la base de datos
	 */
	public static Connection openDataBase(String rutaBD){
		Connection connection = null;
		try{
			
			/*Verificación de los Driver del JDBC*/
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				//SysOutPrintlnSBP.writeLn("[AbriConexionDB2] No se encontró JDBC Driver...!!!");
				e.printStackTrace();
			}
			try {
				System.out.println("Ruta:"+rutaBD);
				connection = DriverManager.getConnection("jdbc:sqlite:"+rutaBD);

			} catch (SQLException e) {
				//SysOutPrintlnSBP.writeLn("[AbriConexionDB2] Falla en la conexión, revisar la consola...!!!");
				e.printStackTrace();
			}
		}catch(Exception e){
			connection = null;
			e.printStackTrace();
		}
		return connection;
	}
	/*
	 * Cierra la conexion con la base de datos
	 */
	public static boolean closeDataBase(Connection connection){
		boolean resultado = false;
		try{
			
			if (connection != null) {
				try {
					/*Cerramos la conexion a la base de datos*/
					connection.close();
					resultado = true;
				} catch (SQLException e) {
					e.printStackTrace();
					resultado = false;
				}
				
			} else {
				resultado = false;
			}
		}catch(Exception e){
			e.printStackTrace();
			resultado = false;
		}
		return resultado;
	}
	
}
