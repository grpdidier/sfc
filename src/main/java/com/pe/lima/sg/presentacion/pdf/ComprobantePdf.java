package com.pe.lima.sg.presentacion.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.UtilSGT;

public class ComprobantePdf {
	 public  ByteArrayInputStream comprobanteReporte(Filtro entidad) throws MalformedURLException, IOException {

	        Document document = new Document();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        //PdfPCell dato = null;
	        String fechaCambio = null;
	        try {

	            PdfPTable table = new PdfPTable(1);
	            table.setWidthPercentage(100);
	            table.setWidths(new int[]{1});

	            //Font headFontBold14 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
	            Font headFontBold12 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
	            //Font headFont12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
	            Font headFont10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
	            Font headFonBoldt10 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
	            //Font headFont8 = FontFactory.getFont(FontFactory.HELVETICA, 8);
	            
	            //Datos de la Empresa 
	            PdfPCell cellEmpresa = new PdfPCell();
	            cellEmpresa.setBorder(Rectangle.NO_BORDER);
	            //Datos del Logo y de la Empresa
	            PdfPTable tableEmpresa = new PdfPTable(3);
	            tableEmpresa.setWidthPercentage(100);
	            tableEmpresa.setWidths(new int[]{1, 2, 2});
	            //Imagen del Logo
	            Image image = Image.getInstance(entidad.getAppRutaContexto() +"./src/main/resources/static/images/iconos/logo.png");
	            image.scaleAbsolute(100, 100);
	            PdfPCell cellLogo = new PdfPCell(image, false);
	            cellLogo.setBorder(Rectangle.NO_BORDER);
	            tableEmpresa.addCell(cellLogo);
	            //Dato de la Empresa
	            PdfPTable tableDatoEmpresa	= new PdfPTable(1);
	            tableDatoEmpresa.setWidthPercentage(100);
	            // -Nombre comercial
	            tableDatoEmpresa.addCell(this.getDatoCeldaNoBorde(this.getParametro(entidad, Constantes.SUNAT_PARAMETRO_NOMBRE_COMERCIAL).toUpperCase(), headFontBold12, Element.ALIGN_LEFT, 20));
	            // -Razon social
	            tableDatoEmpresa.addCell(this.getDatoCeldaNoBorde(this.getParametro(entidad, Constantes.SUNAT_PARAMETRO_RAZON_SOCIAL).toUpperCase(), headFontBold12, Element.ALIGN_LEFT, 20));
	            // -Linea en blanco
	            tableDatoEmpresa.addCell(this.getDatoCeldaNoBorde("", headFontBold12, Element.ALIGN_LEFT, 20));
	            // -Direccion del emisor
	            tableDatoEmpresa.addCell(this.getDatoCeldaNoBorde(this.getParametro(entidad, Constantes.SUNAT_PARAMETRO_DIRECCION_EMISOR).toUpperCase(), headFont10, Element.ALIGN_LEFT, 20));
	            // Seteamos la celda sin bordes y asignamos a la tabla empresa (Logo y  Datos Empresa)
	            PdfPCell cellDatoEmpresa = new PdfPCell();
	            cellDatoEmpresa.addElement(tableDatoEmpresa);
	            cellDatoEmpresa.setBorder(Rectangle.NO_BORDER);
	            tableEmpresa.addCell(cellDatoEmpresa);
	            
	            //Datos del Comprobante
	            PdfPCell cellComprobante = new PdfPCell();
	            
	            PdfPTable tableComprobante	= new PdfPTable(1);
	            tableComprobante.setWidthPercentage(100);
	            // -Tipo de comprobante
	            tableComprobante.addCell(this.getDatoCeldaNoBorde(this.getNombreComprobante(entidad.getComprobante().getTipoComprobante()), headFontBold12, Element.ALIGN_CENTER, 30));
	            // -Numero de Ruc
	            tableComprobante.addCell(this.getDatoCeldaNoBorde("RUC:" + this.getParametro(entidad, Constantes.SUNAT_PARAMETRO_RUC_EMISOR), headFontBold12, Element.ALIGN_CENTER, 30));
	            // -Numero del comprobante
	            tableComprobante.addCell(this.getDatoCeldaNoBorde(entidad.getComprobante().getSerie()+"-"+entidad.getComprobante().getNumero(), headFontBold12, Element.ALIGN_CENTER, 30));
	            //Eliminamos el borde de la celda
	            cellEmpresa.addElement(tableEmpresa);
	            cellEmpresa.setBorder(Rectangle.NO_BORDER);
	            cellComprobante.addElement(tableComprobante);
	            //cellComprobante.setBorder(Rectangle.NO_BORDER); -Mantenemos el borde para el comprobante
	            tableEmpresa.addCell(cellComprobante);
	            PdfPCell datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableEmpresa);
	            //Primera Fila con datos de logo, empresa y comprobante
	            table.addCell(datoCell);
	            //table.addCell(cellComprobante);
	            
	            //Datos del Cliente y Datos Generales
	            PdfPTable tableCliente = new PdfPTable(2);
	            tableCliente.setWidthPercentage(100);
	            tableCliente.setWidths(new int[]{1,3});
	            // -Fecha Emision
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Fecha de Emisión", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getComprobante().getFechaEmision(), headFont10, Element.ALIGN_LEFT, 20));
	            fechaCambio = entidad.getComprobante().getFechaEmision();
	            // -Fecha Vencimiento
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Fecha de Vencimiento", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getComprobante().getFechaVencimiento(), headFont10, Element.ALIGN_LEFT, 20));
	           // -Cliente
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Señor(es)", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getComprobante().getNombreCliente(), headFont10, Element.ALIGN_LEFT, 20));
	            // -Nro Ruc
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Nro Documento / RUC", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getComprobante().getNumeroDocumento(), headFont10, Element.ALIGN_LEFT, 20));
	            // -Direccion
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Dirección Cliente", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+ entidad.getComprobante().getDireccionCliente(), headFont10, Element.ALIGN_LEFT, 20));
	            // -Moneda
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Moneda", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getComprobante().getMoneda(), headFont10, Element.ALIGN_LEFT, 20));
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableCliente);
	            //Segunda Fila con datos del cliente
	            table.addCell(datoCell);
	            //Formato de la lista
	            PdfPTable tableDetalle = new PdfPTable(6);
	            tableDetalle.setWidthPercentage(100);
	            tableDetalle.setWidths(new int[]{1,1,4,1,1,1});
	            
	            tableDetalle.addCell(this.getDatoCelda("Cantidad", headFonBoldt10, Element.ALIGN_CENTER, 30));
	            tableDetalle.addCell(this.getDatoCelda("U. M.", headFonBoldt10, Element.ALIGN_CENTER, 30));
	            //tableDetalle.addCell(this.getDatoCelda("Código", headFonBoldt10, Element.ALIGN_CENTER, 30));
	            tableDetalle.addCell(this.getDatoCelda("Descripción", headFonBoldt10, Element.ALIGN_CENTER, 30));
	            //tableDetalle.addCell(this.getDatoCelda("Valor Ref.", headFonBoldt10, Element.ALIGN_CENTER, 30));
	            tableDetalle.addCell(this.getDatoCelda("P.U", headFonBoldt10, Element.ALIGN_CENTER, 30));
	            tableDetalle.addCell(this.getDatoCelda("Dsct (%)", headFonBoldt10, Element.ALIGN_CENTER, 30));
	            tableDetalle.addCell(this.getDatoCelda("Total", headFonBoldt10, Element.ALIGN_CENTER, 30));
	            for(TblDetalleComprobante detalle: entidad.getListaDetalle()){
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde(detalle.getCantidad().setScale(2, BigDecimal.ROUND_HALF_EVEN).toString(), headFont10, Element.ALIGN_CENTER, 35));
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde(detalle.getUnidadMedida(), headFont10, Element.ALIGN_CENTER, 35));
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde(detalle.getDescripcion(), headFont10, Element.ALIGN_LEFT, 35));
	            	//tableDetalle.addCell(this.getDatoCeldaNoBorde(detalle.getValorReferencia().toString(), headFont10, Element.ALIGN_CENTER, 35));
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde(detalle.getPrecioUnitario().toString(), headFont10, Element.ALIGN_CENTER, 35));
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde(detalle.getDescuento().toString(), headFont10, Element.ALIGN_CENTER, 35));
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde(detalle.getPrecioFinal().toString(), headFont10, Element.ALIGN_CENTER, 35));
	            }
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableDetalle);
	            //Tercera Fila con datos del producto
	            table.addCell(datoCell);
	            //Linea
	            BaseColor myColor = WebColors.getRGBColor("#98c1c0");
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            Paragraph para = null;
	            para = new Paragraph("", headFonBoldt10);
	            para.setLeading(0, 1);
	            datoCell.setMinimumHeight(5);;
	            datoCell.addElement(para);
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setBackgroundColor(myColor);
	            //Quinto Fila 
	            table.addCell(datoCell);
	            //Datos de los totales
	            PdfPTable tableTotal = new PdfPTable(2);
	            tableTotal.setWidthPercentage(100);
	            tableTotal.setWidths(new int[]{1,1});
	            
	            //Datos Leyenda
	            PdfPTable tableLeyenda = new PdfPTable(1);
	            tableLeyenda.setWidthPercentage(100);
	            tableLeyenda.setWidths(new int[]{1});
	            //Datos Calculo de totales
	            PdfPTable tableCalculo = new PdfPTable(2);
	            tableCalculo.setWidthPercentage(100);
	            tableCalculo.setWidths(new int[]{1,1});
	            
	            tableCalculo.addCell(this.getDatoCeldaNoBorde("Descuento", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde(this.getSimboloMoneda(entidad.getComprobante().getMoneda())+entidad.getSunatCabecera().getTotalDescuento(), headFont10, Element.ALIGN_LEFT, 20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde("Op. Gravadas", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde(this.getSimboloMoneda(entidad.getComprobante().getMoneda())+entidad.getSunatCabecera().getOperacionGravada(), headFont10, Element.ALIGN_LEFT, 20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde("Op. Inafectas", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde(this.getSimboloMoneda(entidad.getComprobante().getMoneda())+entidad.getSunatCabecera().getOperacionInafecta(), headFont10, Element.ALIGN_LEFT, 20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde("Op. Exoneradas", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde(this.getSimboloMoneda(entidad.getComprobante().getMoneda())+entidad.getSunatCabecera().getOperacionExonerada(), headFont10, Element.ALIGN_LEFT, 20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde("I.G.V.", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde(this.getSimboloMoneda(entidad.getComprobante().getMoneda())+entidad.getSunatCabecera().getMontoIgv(), headFont10, Element.ALIGN_LEFT, 20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde("Otros Tributos", headFonBoldt10, Element.ALIGN_LEFT,20));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde(this.getSimboloMoneda(entidad.getComprobante().getMoneda())+entidad.getSunatCabecera().getOtrosTributos(), headFont10, Element.ALIGN_LEFT, 20));
	            tableCalculo.addCell(this.getDatoCelda("Importe Total", headFonBoldt10, Element.ALIGN_LEFT, 30));
	            tableCalculo.addCell(this.getDatoCelda(this.getSimboloMoneda(entidad.getComprobante().getMoneda())+entidad.getSunatCabecera().getImporteTotal(), headFont10, Element.ALIGN_LEFT, 30));
	            
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableLeyenda);
	            tableTotal.addCell(datoCell);
	            
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableCalculo);
	            tableTotal.addCell(datoCell);
	            
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableTotal);
	            table.addCell(datoCell);
	            
	           
	            
	            
	            
	            PdfWriter pw=PdfWriter.getInstance(document, out);
	            document.open();
	            datoCell = new PdfPCell(this.getDatoCeldaNoBorde("", headFonBoldt10, Element.ALIGN_LEFT, 30));
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setMinimumHeight(50);
	            table.addCell(datoCell);
	            
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(getBarcode(document, pw,  this.getParametro(entidad, Constantes.SUNAT_PARAMETRO_RUC_EMISOR) +":", entidad.getComprobante().getSerie()+"-"+entidad.getComprobante().getNumero()));
	            datoCell.setMinimumHeight(50);
	            table.addCell(datoCell);
	            
	            
	            
	            datoCell = new PdfPCell(this.getDatoCeldaNoBorde(entidad.getLeyendaSunat().getCodigoSunat() + " : " + entidad.getLeyendaSunat().getDescripcionSunat(), headFont10, Element.ALIGN_LEFT, 30));
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setMinimumHeight(50);
	            table.addCell(datoCell);
	            
	            
	            datoCell = new PdfPCell(this.getDatoCeldaNoBorde("", headFonBoldt10, Element.ALIGN_LEFT, 30));
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setMinimumHeight(50);
	            table.addCell(datoCell);
	            
	            String sunatTexto = "Representación Impresa del Comprobante de Venta Electrónico generada desde el sistema facturador SUNAT. Puede verificarla usando su clave SOL.";
	            String oseTexto = "Representaciones impresa del comprobante electronico que puede ser validada en : https://consulta.ose.pe/";
	            if (UtilSGT.isFechaCorteOse(fechaCambio)) {
	            	datoCell = new PdfPCell(this.getDatoCeldaNoBorde(oseTexto, headFont10, Element.ALIGN_LEFT, 30));
	            }else {
	            	datoCell = new PdfPCell(this.getDatoCeldaNoBorde(sunatTexto, headFont10, Element.ALIGN_LEFT, 30));
	            }
	            //datoCell = new PdfPCell(this.getDatoCeldaNoBorde("Representaciones impresa del comprobante electronico que puede ser validada en : https://consulta.ose.pe/", headFont10, Element.ALIGN_LEFT, 30));
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setMinimumHeight(50);
	            table.addCell(datoCell);
	            document.add(table);
	            
	            document.close();
	            
	        } catch (DocumentException ex) {
	        
	            System.out.println((ComprobantePdf.class.getName()) + " Error: "+ex.getMessage());
	        }

	        return new ByteArrayInputStream(out.toByteArray());
	    }
	 
	 /*
	  * Recupera un dato de la lista de Parametros
	  */
	 public String getParametro(Filtro entidad, String strParametro){
		 String resultado = null;
		 List<ParametroFacturadorBean> listaParametro 	= null;
		 try{
			 listaParametro = entidad.getListaParametro();
			 if (listaParametro!=null){
				 for(ParametroFacturadorBean parametro: listaParametro){
					 if (parametro.getCodigoParametro().equals(strParametro)){
						 resultado = parametro.getValorParametro();
						 break;
					 }
				 }
			 }
		 }catch(Exception e){
			 resultado = "";
			 e.printStackTrace();
		 }
		 return resultado;
	 }
	 /*
	  * Nombre del comprobante
	  */
	 public String getNombreComprobante(String strTipo){
		 String resultado = null;
		 try{
			 if (strTipo.equals(Constantes.SUNAT_CODIGO_COMPROBANTE_FACTURA)){
				 resultado = "FACTURA ELECTRONICA";
			 }else if (strTipo.equals(Constantes.SUNAT_CODIGO_COMPROBANTE_BOLETA)){
				 resultado = "BOLETA ELECTRONICA";
			 }else if (strTipo.equals(Constantes.SUNAT_CODIGO_COMPROBANTE_NOTA_CREDITO)){
				 resultado = "NOTA CREDITO ELECTRONICA";
			 }else if (strTipo.equals(Constantes.SUNAT_CODIGO_COMPROBANTE_NOTA_DEBITO)){
				 resultado = "NOTA DEBITO ELECTRONICA";
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return resultado;
	 }
	 /*
	  * Formatea el dato sin bordes
	  */
	 public PdfPCell getDatoCeldaNoBorde(String StrDato, Font tipoLetra, int intAlineado, int intAltura){
		 Paragraph para = null;
		 PdfPCell cell = null;
		 para = new Paragraph(StrDato, tipoLetra);
         para.setLeading(0, 1);
         para.setAlignment(intAlineado);
         cell = new PdfPCell();
         cell.setMinimumHeight(intAltura);
         cell.addElement(para);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setHorizontalAlignment(Element.ALIGN_LEFT);
         cell.setBorder(Rectangle.NO_BORDER);
         return cell;
	 }
	 public PdfPCell getDatoCelda(String StrDato, Font tipoLetra, int intAlineado, int intAltura){
		 BaseColor myColor = WebColors.getRGBColor("#98c1c0");
		 Paragraph para = null;
		 PdfPCell cell = null;
		 para = new Paragraph(StrDato, tipoLetra);
         para.setLeading(0, 1);
         para.setAlignment(intAlineado);
         cell = new PdfPCell();
         cell.setMinimumHeight(intAltura);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setHorizontalAlignment(Element.ALIGN_LEFT);
         cell.addElement(para);
         cell.setBorder(Rectangle.NO_BORDER);
         cell.setBackgroundColor(myColor);
         
         return cell;
	 }
	 
	 public String getSimboloMoneda(String strMoneda){
		 String resultado = "";
		 	if (strMoneda.equals(Constantes.SUNAT_TIPO_MONEDA_SOLES)){
		 		resultado = "S/         ";
		 	}else if (strMoneda.equals(Constantes.SUNAT_TIPO_MONEDA_DOLAR)){
		 		resultado = "$          ";
		 	}else{
		 		resultado = "";
		 	}
		 return resultado;
	 }
	 
	 private static Image getBarcode(Document document,  PdfWriter pdfWriter, String servicio,String  codigoTransaccion){
		 	PdfContentByte cimg = pdfWriter.getDirectContent();
		   	Barcode128 code128 = new Barcode128();
		   	//code128.setCode(servicio + addZeroLeft(codigoTransaccion));
		   	code128.setCode(servicio + codigoTransaccion);
		   	code128.setCodeType(Barcode128.CODE128);
			code128.setTextAlignment(Element.ALIGN_CENTER);
			Image image = code128.createImageWithBarcode(cimg, null, null);
			float scaler = ((document.getPageSize().getWidth() - document.leftMargin()  - document.rightMargin() - 0) / image.getWidth()) * 40;
			image.scalePercent(scaler);
			image.setAlignment(Element.ALIGN_CENTER);
			return image;
		}
}
