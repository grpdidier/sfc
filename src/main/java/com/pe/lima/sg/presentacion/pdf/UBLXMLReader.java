package com.pe.lima.sg.presentacion.pdf;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

public class UBLXMLReader {
   public static void main(String[] args) {
       String filePath = "target/R-20602620337-09-TTT1-00000005.xml"; // Ruta del archivo XML de la respuesta UBL

       try {
           // Crear un objeto DocumentBuilderFactory
           DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

           // Crear un objeto DocumentBuilder
           DocumentBuilder builder = factory.newDocumentBuilder();

           // Parsear el archivo XML y obtener el objeto Document
           Document document = builder.parse(filePath);

           // Crear un objeto XPathFactory
           XPathFactory xPathFactory = XPathFactory.newInstance();

           // Crear un objeto XPath
           XPath xPath = xPathFactory.newXPath();

           // Compilar la expresión XPath para obtener los elementos deseados
           XPathExpression idExpression = xPath.compile("//cbc:ID");
           XPathExpression issueDateExpression = xPath.compile("//cbc:IssueDate");
           XPathExpression typeCodeExpression = xPath.compile("//cbc:DespatchAdviceTypeCode");

           // Evaluar las expresiones XPath en el documento
           String id = evaluateXPathExpression(idExpression, document);
           String issueDate = evaluateXPathExpression(issueDateExpression, document);
           String typeCode = evaluateXPathExpression(typeCodeExpression, document);

           // Imprimir la información obtenida
           System.out.println("Información de la guía de remisión:");
           System.out.println("ID: " + id);
           System.out.println("Fecha de emisión: " + issueDate);
           System.out.println("Tipo de guía de remisión: " + typeCode);

       } catch (Exception e) {
           System.out.println("Ha ocurrido un error al leer el archivo XML: " + e.getMessage());
       }
   }

   private static String evaluateXPathExpression(XPathExpression expression, Document document) throws XPathExpressionException {
       String result = "";
       NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

       if (nodeList.getLength() > 0) {
           Node node = nodeList.item(0);
           result = node.getTextContent();
       }

       return result;
   }
}
