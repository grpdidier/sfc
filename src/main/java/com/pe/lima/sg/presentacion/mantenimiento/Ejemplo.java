package com.pe.lima.sg.presentacion.mantenimiento;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class Ejemplo {

	public static void main(String[] args) {
		try{
			System.out.println("[Inicio]");
			 Class.forName("org.sqlite.JDBC");
		        Connection conn = DriverManager.getConnection("jdbc:sqlite:D:/Bill/BDFacturador.db");
		        Statement stat = conn.createStatement();
		        /*stat.executeUpdate("drop table if exists people;");
		        stat.executeUpdate("create table people (name, occupation);");
		        PreparedStatement prep = conn.prepareStatement(
		            "insert into people values (?, ?);");

		        prep.setString(1, "Gandhi");
		        prep.setString(2, "politics");
		        prep.addBatch();
		        prep.setString(1, "Turing");
		        prep.setString(2, "computers");
		        prep.addBatch();
		        prep.setString(1, "Wittgenstein");
		        prep.setString(2, "smartypants");
		        prep.addBatch();

		        conn.setAutoCommit(false);
		        prep.executeBatch();
		        conn.setAutoCommit(true);*/

		        ResultSet rs = stat.executeQuery("select count(1) as total from TXXXX_BANDFACT;");
		        while (rs.next()) {
		            System.out.println("total = " + rs.getString("total"));
		        }
		        rs.close();
		        conn.close();
		        System.out.println("[Fin]");
			}catch(Exception e){
				e.printStackTrace();
			}

	}

}
