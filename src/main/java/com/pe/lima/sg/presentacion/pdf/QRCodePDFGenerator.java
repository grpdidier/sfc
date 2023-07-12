package com.pe.lima.sg.presentacion.pdf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class QRCodePDFGenerator {
    public static void main(String[] args) {
        String url = "https://www.ejemplo.com"; // Enlace que deseas codificar en el QR
        String filePath = "qr_code.pdf"; // Ruta del archivo PDF

        int size = 200; // Tamaño del QR

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

            // Crear el documento PDF
            Document document = new Document(PageSize.A4);

            // Escribir el código QR en el documento PDF
            Path path = FileSystems.getDefault().getPath(filePath);
            PdfWriter.getInstance(document, new FileOutputStream(path.toString()));
            document.open();

            // Insertar la imagen del QR en el documento PDF
            Image qrPdfImage = Image.getInstance(qrImage, null);
            qrPdfImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrPdfImage);

            // Insertar un texto descriptivo en el documento PDF
            Paragraph description = new Paragraph("Escanee el código QR para acceder al enlace.");
            description.setAlignment(Element.ALIGN_CENTER);
            document.add(description);

            document.close();

            System.out.println("¡El código QR se ha generado correctamente en el archivo PDF!");

        } catch (WriterException | IOException e) {
            System.out.println("Ha ocurrido un error al generar el código QR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error al generar el archivo PDF: " + e.getMessage());
        }
    }
}

