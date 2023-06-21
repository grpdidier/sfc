package com.pe.lima.sg.presentacion.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.bean.remision.RemisionBean;
import com.pe.lima.sg.entity.operacion.TblDetalleRemision;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;

public class GuiaRemisionPdf {
	 public  ByteArrayInputStream comprobanteReporte(RemisionBean entidad) throws MalformedURLException, IOException {

	        Document document = new Document();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        
	        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
	        Integer tamanioAltura = 15;
	        try {

	            PdfPTable table = new PdfPTable(1);
	            table.setWidthPercentage(100);
	            table.setWidths(new int[]{1});

	            Font headFontBold12 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
	            Font headFont10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
	            Font headFonBoldt10 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
	            Font headFonBoldt8 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
	            Font headFont8 = FontFactory.getFont(FontFactory.HELVETICA, 8);
	            
	            //Datos de la Empresa 
	            PdfPCell cellEmpresa = new PdfPCell();
	            cellEmpresa.setBorder(Rectangle.NO_BORDER);
	            //Datos del Logo y de la Empresa
	            PdfPTable tableEmpresa = new PdfPTable(2);
	            tableEmpresa.setWidthPercentage(100);
	            tableEmpresa.setWidths(new int[]{3, 2});
	            //Imagen del Logo
	            //Image image = Image.getInstance(entidad.getAppRutaContexto() +"./src/main/resources/static/images/iconos/logo.png");
	            //image.scaleAbsolute(100, 100);
	            //PdfPCell cellLogo = new PdfPCell(image, false);
	            //cellLogo.setBorder(Rectangle.NO_BORDER);
	            //tableEmpresa.addCell(cellLogo);
	            //Dato de la Empresa
	            PdfPTable tableDatoEmpresa	= new PdfPTable(1);
	            tableDatoEmpresa.setWidthPercentage(100);
	            
	            // -Razon social
	            tableDatoEmpresa.addCell(this.getDatoCeldaNoBorde(entidad.getRazonSocial(), headFontBold12, Element.ALIGN_LEFT, 20));
            	
	            // -Linea en blanco
	            //tableDatoEmpresa.addCell(this.getDatoCeldaNoBorde("", headFontBold12, Element.ALIGN_LEFT, 20));
	            // -Direccion del emisor
	            //tableDatoEmpresa.addCell(this.getDatoCeldaNoBorde(this.getParametro(entidad, Constantes.SUNAT_PARAMETRO_DIRECCION_EMISOR).toUpperCase(), headFont8, Element.ALIGN_LEFT, 20));
	            // -Punto de Emision
	            //tableDatoEmpresa.addCell(this.getDatoCeldaNoBorde(entidad.getDireccionPartida(), headFont8, Element.ALIGN_LEFT, 20));
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
	            tableComprobante.addCell(this.getDatoCeldaNoBorde(this.getNombreComprobante(entidad.getRemision().getTipoComprobante()), headFontBold12, Element.ALIGN_CENTER, 20));
	            // -Numero de Ruc
	            tableComprobante.addCell(this.getDatoCeldaNoBorde("RUC:" + this.getParametro(entidad, Constantes.SUNAT_PARAMETRO_RUC_EMISOR), headFontBold12, Element.ALIGN_CENTER, 20));
	            // -Numero del comprobante
	            tableComprobante.addCell(this.getDatoCeldaNoBorde(entidad.getRemision().getSerie()+"-"+entidad.getRemision().getNumero(), headFontBold12, Element.ALIGN_CENTER, 20));
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
	            
	            
	            
	            //Datos del transporte
	            PdfPTable tableFecha = new PdfPTable(2);
	            tableFecha.setWidthPercentage(100);
	            tableFecha.setWidths(new int[]{1,1});
	            
	            //Fecha de Emision
	            PdfPTable tableFechaEmision = new PdfPTable(2);
	            tableFechaEmision.setWidthPercentage(100);
	            tableFechaEmision.setWidths(new int[]{1,1});
	            tableFechaEmision.addCell(this.getDatoCeldaNoBorde("Fecha de Emisión", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableFechaEmision.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getFechaEmision(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            
	            PdfPCell cellFechaEmision = new PdfPCell();
	            cellFechaEmision.addElement(tableFechaEmision);
	            cellFechaEmision.setBorder(Rectangle.NO_BORDER);
	            tableFecha.addCell(cellFechaEmision);
	            //Fecha Traslado
	            PdfPTable tableFechaTraslado = new PdfPTable(2);
	            tableFechaTraslado.setWidthPercentage(100);
	            tableFechaTraslado.setWidths(new int[]{1,1});
	            tableFechaTraslado.addCell(this.getDatoCeldaNoBorde("Fecha de Inicio de Traslado", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableFechaTraslado.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getFechaInicioTraslado(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            
	            PdfPCell cellFechaTraslado = new PdfPCell();
	            cellFechaTraslado.addElement(tableFechaTraslado);
	            cellFechaTraslado.setBorder(Rectangle.NO_BORDER);
	            tableFecha.addCell(cellFechaTraslado);
	            
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableFecha);
	            table.addCell(datoCell);
	            
	            
	            
	            
	            //Datos del Cliente y Datos Generales
	            PdfPTable tableCliente = new PdfPTable(2);
	            tableCliente.setWidthPercentage(100);
	            tableCliente.setWidths(new int[]{1,3});
	            // -Domicilio de Partida
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Domicilio de Partida", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getDireccionPartida(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            // -Domicilio de Llegada
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Domicilio de Llegada", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getDomicilioLlegada(), headFont8, Element.ALIGN_LEFT, tamanioAltura));	            
	            // -Cliente
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Destinatario", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getNombreCliente(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            // -Nro Ruc
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Nro Documento / RUC", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getNumeroDocumentoCliente(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            // -Transportista
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Transportista", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getNombreTransportista(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            // -Nro Ruc
	            tableCliente.addCell(this.getDatoCeldaNoBorde("Nro Documento / RUC", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableCliente.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getNumeroDocumentoTransportista(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableCliente);
	            table.addCell(datoCell);
	            
	            //Linea
	            BaseColor myColor = WebColors.getRGBColor("#98c1c0");
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            Paragraph para = null;
	            para = new Paragraph("", headFonBoldt10);
	            para.setLeading(0, 1);
	            datoCell.setMinimumHeight(2);;
	            datoCell.addElement(para);
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setBackgroundColor(myColor);
	            //Quinto Fila 
	            table.addCell(datoCell);
	            //Titulo de los datos del transporte
	            PdfPTable tableTransporteTitulo = new PdfPTable(1);
	            tableTransporteTitulo.setWidthPercentage(100);
	            tableTransporteTitulo.setWidths(new int[]{1});
	            tableTransporteTitulo.addCell(this.getDatoCeldaNoBorde("UNIDAD DE TRANSPORTE /  CONDUCTOR", headFonBoldt8, Element.ALIGN_CENTER,tamanioAltura));
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableTransporteTitulo);
	            table.addCell(datoCell);
	            //Datos del transporte
	            PdfPTable tableTransporte = new PdfPTable(2);
	            tableTransporte.setWidthPercentage(100);
	            tableTransporte.setWidths(new int[]{1,1});
	            
	            //Marca y Nro Inscripcion
	            PdfPTable tableTransporteMarca = new PdfPTable(2);
	            tableTransporteMarca.setWidthPercentage(100);
	            tableTransporteMarca.setWidths(new int[]{1,1});
	            tableTransporteMarca.addCell(this.getDatoCeldaNoBorde("Marca", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableTransporteMarca.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getMarca(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            tableTransporteMarca.addCell(this.getDatoCeldaNoBorde("Certificado de Inscripción", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableTransporteMarca.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getNumeroCertInscripcion(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            PdfPCell cellTransporteMarca = new PdfPCell();
	            cellTransporteMarca.addElement(tableTransporteMarca);
	            cellTransporteMarca.setBorder(Rectangle.NO_BORDER);
	            tableTransporte.addCell(cellTransporteMarca);
	            //Placa y Licencia
	            PdfPTable tableTransportePlaca = new PdfPTable(2);
	            tableTransportePlaca.setWidthPercentage(100);
	            tableTransportePlaca.setWidths(new int[]{1,1});
	            tableTransportePlaca.addCell(this.getDatoCeldaNoBorde("Placa", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableTransportePlaca.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getPlaca(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            tableTransportePlaca.addCell(this.getDatoCeldaNoBorde("Licencia de Conducir", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableTransportePlaca.addCell(this.getDatoCeldaNoBorde(": "+entidad.getRemision().getNumeroLicencia(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            PdfPCell cellTransportePlaca = new PdfPCell();
	            cellTransportePlaca.addElement(tableTransportePlaca);
	            cellTransportePlaca.setBorder(Rectangle.NO_BORDER);
	            tableTransporte.addCell(cellTransportePlaca);
	            
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableTransporte);
	            table.addCell(datoCell);
	            //Motivo
	            PdfPTable tableMotivo = new PdfPTable(2);
	            tableMotivo.setWidthPercentage(100);
	            tableMotivo.setWidths(new int[]{1,3});
	            tableMotivo.addCell(this.getDatoCeldaNoBorde("Motivo de Traslado", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableMotivo.addCell(this.getDatoCeldaNoBorde(": "+entidad.getDescripcionMotivo(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableMotivo);
	            table.addCell(datoCell);
	            
	            
	            //Formato de la lista
	            PdfPTable tableDetalle = new PdfPTable(3);
	            tableDetalle.setWidthPercentage(100);
	            tableDetalle.setWidths(new int[]{1,1,8});
	            
	            tableDetalle.addCell(this.getDatoCelda("Cantidad", headFonBoldt8, Element.ALIGN_CENTER, 20));
	            tableDetalle.addCell(this.getDatoCelda("Unidad", headFonBoldt8, Element.ALIGN_CENTER, 20));
	            tableDetalle.addCell(this.getDatoCelda("Descripción", headFonBoldt8, Element.ALIGN_CENTER, 20));
	            for(TblDetalleRemision detalle: entidad.getListaDetalleRemision()){
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde(formatter.format(detalle.getCantidad()), headFont8, Element.ALIGN_CENTER, tamanioAltura));
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde("PAQ", headFont8, Element.ALIGN_CENTER, tamanioAltura));
	            	tableDetalle.addCell(this.getDatoCeldaNoBorde(detalle.getDescripcion(), headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            }
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableDetalle);
	            //Tercera Fila con datos del producto
	            table.addCell(datoCell);
	            //Linea
	            BaseColor myColorDetalle = WebColors.getRGBColor("#98c1c0");
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            Paragraph paraDetalle = null;
	            paraDetalle = new Paragraph("", headFonBoldt8);
	            paraDetalle.setLeading(0, 1);
	            datoCell.setMinimumHeight(5);;
	            datoCell.addElement(paraDetalle);
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setBackgroundColor(myColorDetalle);
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
	            
	            tableCalculo.addCell(this.getDatoCeldaNoBorde("", headFonBoldt8, Element.ALIGN_LEFT,tamanioAltura));
	            tableCalculo.addCell(this.getDatoCeldaNoBorde("", headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            //tableCalculo.addCell(this.getDatoCeldaNoBorde("Otros Tributos", headFonBoldt10, Element.ALIGN_LEFT,20));
	            //tableCalculo.addCell(this.getDatoCeldaNoBorde(this.getSimboloMoneda(entidad.getComprobante().getMoneda())+entidad.getSunatCabecera().getOtrosTributos(), headFont10, Element.ALIGN_LEFT, 20));
	            tableCalculo.addCell(this.getDatoCelda("Peso Total", headFonBoldt8, Element.ALIGN_LEFT, tamanioAltura));
	            tableCalculo.addCell(this.getDatoCelda("Tn", headFont8, Element.ALIGN_LEFT, tamanioAltura));
	            
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
	            
	           
	            
	            
	            
	            @SuppressWarnings("unused")
				PdfWriter pw=PdfWriter.getInstance(document, out);
	            document.open();

	            //Observacion
	            datoCell = new PdfPCell(this.getDatoCeldaNoBorde("Observación:.............................................................................................................................................................................................", headFont8, Element.ALIGN_LEFT, 20));
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setMinimumHeight(30);
	            table.addCell(datoCell);
	            //Firmas
	            PdfPTable tableFirmas = new PdfPTable(2);
	            tableFirmas.setWidthPercentage(100);
	            tableFirmas.setWidths(new int[]{1,1});
	            tableFirmas.addCell(this.getDatoCeldaNoBorde("___________________________________", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde("___________________________________", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde(entidad.getRazonSocial(), headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde("Conformidad del Cliente", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde(" ", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde(" ", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde(" ", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde("___________________________________", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde(" ", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	            tableFirmas.addCell(this.getDatoCeldaNoBorde("Sr(a) (ta)", headFont8, Element.ALIGN_CENTER,tamanioAltura));
	           
	            datoCell = new PdfPCell();
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.addElement(tableFirmas);
	            table.addCell(datoCell);
	            
	            
	            
	            
	            String oseTexto = "Representaciones Impresa de la Guía de Remisión Electronica que puede ser validada en : http://www.sunat.gob.pe";
	            datoCell = new PdfPCell(this.getDatoCeldaNoBorde(oseTexto, headFont8, Element.ALIGN_LEFT, 20));

	            
	            datoCell.setBorder(Rectangle.NO_BORDER);
	            datoCell.setMinimumHeight(50);
	            table.addCell(datoCell);
	            document.add(table);
	            
	            document.close();
	            
	        } catch (DocumentException ex) {
	        
	            System.out.println((ComprobanteKenorPdf.class.getName()) + " Error: "+ex.getMessage());
	        }

	        return new ByteArrayInputStream(out.toByteArray());
	    }
	 
	 /*Obtiene la forma de pago*/
	 public String getFormaPago(Filtro entidad){
		 String formaPago = "Contado";
		 if (entidad != null && entidad.getFormaPago()!=null && entidad.getFormaPago().getTipo()!=null){
			 formaPago = entidad.getFormaPago().getTipo();
		 }
		 return formaPago;
	 }
	 /*
	  * Recupera un dato de la lista de Parametros
	  */
	 public String getParametro(RemisionBean entidad, String strParametro){
		 String resultado = "";
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
			 }else if (strTipo.equals(Constantes.TIPO_COMPROBANTE_GUIA_REMISION)){
				 resultado = "GUIA DE REMISION REMITENTE";
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
	 /*
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
		}*/
	 
}
