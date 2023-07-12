package com.pe.lima.sg.presentacion.pdf;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class XMLReader {
    public static void main(String[] args) {
        try {
            // Crear un objeto DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Crear un objeto DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parsear el archivo XML y obtener el objeto Document
            Document document = builder.parse(new File("target/R-20602620337-09-TTT1-00000005.xml"));

            // Obtener el elemento raíz
            Element root = document.getDocumentElement();

            // Obtener el elemento "elemento1"
            Element elemento1 = (Element) root.getElementsByTagName("cbc:DocumentDescription").item(0);

            // Obtener el valor del elemento "elemento1"
            String valorElemento1 = elemento1.getTextContent();

            // Imprimir el valor del elemento "elemento1"
            System.out.println("Valor del elemento1: " + valorElemento1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
