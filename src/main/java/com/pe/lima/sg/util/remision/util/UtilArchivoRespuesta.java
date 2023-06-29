package com.pe.lima.sg.util.remision.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UtilArchivoRespuesta {
	 public static String obtenerCDRXML(String base64ZipFile, String outputFolderPath) {
	        //String base64ZipFile = "UEsDBBQAAAAIAJwx1laHDwWdzwMAAI0NAAAcAAAAUi0yMDYwMjYyMDMzNy0wOS1UVFQxLTIwLnhtbJ1XXXPaOhB9z6/wOM/GsgHTeAwZAukM0zbTIaT3vgp7A75jS76SnJD++kr+QjYmKcmT2V2dPXt2tZoEt4c0MV6A8ZiSqekMkGkACWkUk93UfNp8tb6Yt7OrADN/nmVJHGIhA9fAM0o4GPIw4T5mUzNnxKeYx9wnOAXu8wzC+LmK9/Nt4vNwDyn2Dzzqg7Jcs0KDg7gQbkHTlJL7gwCiypA/JSQQwY+g4Tb8FOidDA97AfHnAOe7HYMdFtAHGvGpuRci82379fV18DocULazXYSQjW5sGRPxeHddR3OKsya+TMQH0qXsxUH1YQN5gYRmYJuzK8MIpLr+0933RiyurD320lw5NGmJ/BK1U7of4x3BImfVLPwV/+PxCgCiFXmmulk6FphQIrVM4t+Fnj9A7GlkzJMdZbHYp2dSObaDVCoLDqEVOiNy/Y+MVuIrrU27k6Xh/9fwaFRXYqWUwTXj2OJ77I69E/A1PAOT1wmMp/VqappttwzYMEz4M2Up77p054ekWvLWDY8sXtfWJXYh/GWSSmj7fF3BMt4BFxeqLTW87te4QfyFkxy6DbCbDrRmzu4fuuMwnIIF3+CtZ0j/HaObJRb4hJRyLICJch3A7CB/h8ffgd0N6ANYcZ4DewQW46Sng8eQB7l+ihQ83/4HoVDrqEyh+fsBSvSHPN0Cm92ov/Jgy37S4PfZlf6uLoF9omHZiULyZuHY5zdO6euuqVNrMXWB3PnK+qt83FbLmTtAgX1irUMXORc0rZaNtDt1dNdRH5CfKETecIzH1hjhiTUaeSNrC84Xy3MmI2c78cbj8aQE0c8p3Zaq5y5yhxbyLNetghpPK3YTy+4hz3f0sMJYh9WvaC9qy9k90cVu2YtgHPqdJtXVK/PD09391/lio1fZOkXZ20/MxFvTX+UqLKtINrZ5LrWhqJBc5CHk3YwnE6eNrlr+AcoxS3v0C+zCVBM3Hucluh6o4R/NpbGvqiKbXEOxwEmj1VwIHO5T7bkswtSQMoKTns1UTux61SOssra4nYEpne9T0euoelrKQuV8LJZrIwIjYzlscSUMFdooAImAdWt/txMfdPPdXlZc20mLlGsIIX75NBXXc9FwOLmYyknasvc0zJW89e3R+LRNze2ruiYzbzYbx3JRff2OjvaBEmVBI3ld21e1sOnBS+Ahi7OC+Hxx/3MzX1Yzrnu0intoHyvqjGktYot2h219WE+n3ncuH/icJZaQ7/XtHvP9/2yqvityPaf0oe5nVA9DnMXS+clVI5fluZn4cNk0c9Fh0GVdSxzY/f9Mza7+AFBLAQI0AxQAAAAIAJwx1laHDwWdzwMAAI0NAAAcAAAAAAAAAAEAAACkgQAAAABSLTIwNjAyNjIwMzM3LTA5LVRUVDEtMjAueG1sUEsFBgAAAAABAAEASgAAAAkEAAAAAA=="; // Archivo ZIP codificado en Base64
	        //String outputFolderPath = "/target/"; // Ruta de salida para guardar el archivo XML extraído
		 	String xmlRespuesta = null;
	        try {
	            // Decodificar el archivo ZIP desde Base64 a bytes
	            byte[] zipBytes = Base64.getDecoder().decode(base64ZipFile.getBytes(StandardCharsets.UTF_8));

	            // Crear un flujo de entrada para el archivo ZIP decodificado
	            ByteArrayInputStream zipInputStream = new ByteArrayInputStream(zipBytes);
	            ZipInputStream zipIn = new ZipInputStream(zipInputStream);

	            // Recorrer las entradas del archivo ZIP
	            ZipEntry entry = zipIn.getNextEntry();
	            while (entry != null) {
	                // Verificar si la entrada es un archivo XML
	                if (!entry.isDirectory() && entry.getName().endsWith(".xml")) {
	                    // Crear la ruta completa para el archivo XML de salida
	                    String outputFilePath = outputFolderPath +"\\"+ entry.getName();
	                    xmlRespuesta = entry.getName();
	                    // Crear directorios necesarios en caso de que no existan
	                    File outputFile = new File(outputFilePath);
	                    outputFile.getParentFile().mkdirs();

	                    // Crear un flujo de salida para el archivo XML
	                    OutputStream outputStream = Files.newOutputStream(outputFile.toPath(), StandardOpenOption.CREATE);

	                    // Escribir el contenido del archivo XML en el flujo de salida
	                    byte[] buffer = new byte[4096];
	                    int bytesRead;
	                    while ((bytesRead = zipIn.read(buffer)) != -1) {
	                        outputStream.write(buffer, 0, bytesRead);
	                    }

	                    // Cerrar el flujo de salida
	                    outputStream.close();

	                    System.out.println("Archivo XML extraído y guardado en: " + outputFilePath);
	                }

	                zipIn.closeEntry();
	                entry = zipIn.getNextEntry();
	            }

	            // Cerrar los flujos de entrada
	            zipIn.close();
	            zipInputStream.close();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return xmlRespuesta;
	    }
	 
}


