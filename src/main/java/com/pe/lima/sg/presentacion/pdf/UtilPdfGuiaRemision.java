package com.pe.lima.sg.presentacion.pdf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Image;

public class UtilPdfGuiaRemision {
	public static String obtenerURLDelCDR(String pathFileXmlCdr) {
		String urlQR = null;
		try {
			// Crear un objeto DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			// Crear un objeto DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Parsear el archivo XML y obtener el objeto Document
			Document document = builder.parse(new File(pathFileXmlCdr));

			// Obtener el elemento raíz
			Element root = document.getDocumentElement();

			// Obtener el elemento "cbc:DocumentDescription"
			Element elemento = (Element) root.getElementsByTagName("cbc:DocumentDescription").item(0);

			// Obtener el valor del elemento "elemento1"
			urlQR = elemento.getTextContent();

		} catch (Exception e) {
			e.printStackTrace();
			urlQR = "ERROR:"+e.getMessage();

		}
		return urlQR;
	}
	public static Image generarImagenQR(String url) {

		//String filePath = "qr_code.pdf"; // Ruta del archivo PDF
		Image qrPdfImage = null;
		int size = 140; // Tamaño del QR

		// Configurar los parámetros del QR
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		try {
			// Generar el código QR
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size, size, hints);

			// Crear la imagen del QR
			BufferedImage qrImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			qrImage.createGraphics();

			// Rellenar la imagen del QR con los módulos del código QR
			Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, size, size);
			graphics.setColor(Color.BLACK);

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (bitMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}

			// Insertar la imagen del QR en el documento PDF
			qrPdfImage = Image.getInstance(qrImage, null);
			//qrPdfImage.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
			//document.add(qrPdfImage);

		} catch (WriterException | IOException e) {
			System.out.println("Ha ocurrido un error al generar el código QR: " + e.getMessage());
			qrPdfImage = null;
		} catch (Exception e) {
			System.out.println("Ha ocurrido un error al generar el archivo PDF: " + e.getMessage());
			qrPdfImage = null;
		}
		return qrPdfImage;
	}
}
