/*
 * POIExcel.java
 *
 * Created on 21 de septiembre de 2005, 04:22 PM
 */


package com.pe.lima.sg.presentacion.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.util.CellRangeAddress;


// TODO: Auto-generated Javadoc
/**
 * LA Class POIWrite.
 *
 * @author  mfontalvo
 */
public class POIWrite {
    
    /** La Constante fileName. de File name. */
    private String       fileName;
    
    /** La Constante wb. de Wb. */
    private HSSFWorkbook wb;
    
    /** La Constante sheet. de Sheet. */
    private HSSFSheet    sheet;
    
    /** La Constante NONE. */
    public static final int NONE = -1;
    
 
 
    
    /**
     * Creates a new instance of POIExcel.
     *
     * @param file the file
     * @throws Exception the exception
     */
    
    
    public POIWrite(String file) throws Exception{
        nuevoLibro(file);
        
    }
    
    /**
     * Constructor del pOI write object.como nuevo.
     */
    public POIWrite() {
    }

    
    /**
     * Nuevo libro.
     *
     * @param file the file
     * @throws Exception the exception
     */
    public void nuevoLibro (String file) throws Exception{
        wb = new HSSFWorkbook();
        fileName = file;            
        
    }
    
    /**
     * Nuevo libro.
     *
     * @throws Exception the exception
     */
    public void nuevoLibro () throws Exception{
        wb = new HSSFWorkbook();
        fileName = "";    
        
    }
    
    /**
     * Cerrar libro.
     *
     * @throws Exception the exception
     */
    public void cerrarLibro () throws Exception{
        if (wb==null)
            throw new Exception("No se pudo cerrar el libro, primero debera crear el libro");
        
        
        if ( !fileName.equals("") ) {
	        FileOutputStream fileOut = new FileOutputStream(fileName);
	        wb.write(fileOut);
	        fileOut.close(); 
        } else {
        	throw new Exception("No se pudo cerrar el libro, primero debera indicar el nombre del Archivo");
        }
        
        wb = null;
    }
    
    
    /**
     * Cerrar libro.
     *
     * @param out the out
     * @throws Exception the exception
     */
    public void cerrarLibro (OutputStream out) throws Exception{
        if (wb==null)
            throw new Exception("No se pudo cerrar el libro, primero debera crear el libro");
        
        
        if ( out != null ) {
	        wb.write(out);
        }
        wb = null;
    }
    
    /**
     * Obtener hoja.
     *
     * @param hoja the hoja
     * @throws Exception the exception
     */
    public void obtenerHoja(String hoja)throws Exception{
        if (wb==null)
            throw new Exception("No se pudo crear la hoja, primero debera crear el libro");
        
        sheet = wb.getSheet(hoja); 
        if (sheet == null) sheet = wb.createSheet(hoja); 
       
    }    
    
    /**
     * Obtener celda.
     *
     * @param fila the fila
     * @param columna the columna
     * @return the hSSF cell
     * @throws Exception the exception
     */
    public HSSFCell obtenerCelda(int fila, int columna)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
         
        HSSFRow row  = sheet.getRow(fila);
        if (row==null) row = sheet.createRow( (short) fila);
        
        HSSFCell cell   = row.getCell( columna );
        if (cell == null) cell = row.createCell( columna);
        
        return cell;
    }
    
    /**
     * Adicionar celda.
     *
     * @param fila the fila
     * @param columna the columna
     * @param value the value
     * @param style the style
     * @throws Exception the exception
     */
    public void adicionarCelda(int fila, int columna, String value, HSSFCellStyle style)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
         
		HSSFRow row = sheet.getRow(fila);
        if(row == null){
        	row   = sheet.createRow( fila);
        }

        HSSFCell cell = row.createCell ( columna);
        
        if (value == null) value = "";
        cell.setCellValue( new HSSFRichTextString (value) );
        
        if (style!=null) cell.setCellStyle(style);
    }
    
    /**
     * Adicionar celda.
     *
     * @param fila the fila
     * @param columna the columna
     * @param value the value
     * @param style the style
     * @throws Exception the exception
     */
    public void adicionarCelda(int fila, int columna, double value, HSSFCellStyle style)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
         
		HSSFRow row = sheet.getRow(fila);
	        if(row == null){
	        	row   = sheet.createRow(  fila);
	        }
        HSSFCell cell = row.createCell ( columna);
        
        cell.setCellValue(value);
        
        if (style!=null) cell.setCellStyle(style);
    }    
    
    /**
     * Adicionar celda.
     *
     * @param fila the fila
     * @param columna the columna
     * @param value the value
     * @param style the style
     * @throws Exception the exception
     */
    public void adicionarCelda(int fila, int columna, Date value, HSSFCellStyle style)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
         
		HSSFRow row = sheet.getRow(fila);
        if(row == null){
        	row   = sheet.createRow(  fila);
        }
        HSSFCell cell = row.createCell (  columna);
        
        cell.setCellValue(value);
        
        if (style!=null) cell.setCellStyle(style);
    } 
    
    
    /**
     * Adicionar celda titulo.
     *
     * @param fila the fila
     * @param columna the columna
     * @param value the value
     * @param style the style
     * @throws Exception the exception
     */
    public void adicionarCeldaTitulo(int fila, int columna, String value, HSSFCellStyle style )throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
         
		HSSFRow row = sheet.getRow(fila);
        if(row == null){
        	row   = sheet.createRow(  fila);
        }
        HSSFCell cell = row.createCell ( columna);
        
        cell.setCellValue( new HSSFRichTextString (value) );        
        if (style!=null) cell.setCellStyle(style);
        
        sheet.autoSizeColumn((short) columna);
    }
    
    /**
     * Adicionar rango celdas.
     *
     * @param fila the fila
     * @param columnainicial the columnainicial
     * @param columnafinal the columnafinal
     * @param value the value
     * @param style the style
     * @throws Exception the exception
     */
    public void adicionarRangoCeldas(int fila, int columnainicial , int columnafinal, String value, HSSFCellStyle style )throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
         
		HSSFRow row = sheet.getRow(fila);
        if(row == null){
        	row   = sheet.createRow(  fila);
        }
        for( int i = columnainicial; i <= columnafinal; i++ ){
        	HSSFCell cell = row.createCell ( i);

        	cell.setCellValue( new HSSFRichTextString (value) );        
        	if (style!=null) cell.setCellStyle(style);

        	sheet.autoSizeColumn((short) i);
        }
    }
    
    
    
    /**
     * Adicionar celda titulo.
     *
     * @param fila the fila
     * @param columna the columna
     * @param style the style
     * @throws Exception the exception
     */
    public void adicionarCeldaTitulo( int fila, int columna, HSSFCellStyle style )throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
         
		HSSFRow row = sheet.getRow(fila);
        if(row == null){
        	row   = sheet.createRow(  fila);
        }
        HSSFCell cell = row.createCell ( columna);              
        if ( style!=null ) 
        	 cell.setCellStyle(style);        
        sheet.autoSizeColumn((short) columna);
    }
    
    
    
    /**
     * Combinar celdas.
     *
     * @param filaInicial the fila inicial
     * @param columnaInicial the columna inicial
     * @param filaFinal the fila final
     * @param columnaFinal the columna final
     * @throws Exception the exception
     */
    public void combinarCeldas(int filaInicial, int columnaInicial, int filaFinal , int columnaFinal)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
        CellRangeAddress region = new CellRangeAddress(filaInicial, filaInicial, columnaInicial, columnaFinal);         
        //sheet.addMergedRegion(new Region(filaInicial,(short)columnaInicial ,filaFinal,(short)columnaFinal));
        sheet.addMergedRegion(region);
        
        
                
        
    }
    
    /**
     * Cambiar magnificacion.
     *
     * @param x the x
     * @param y the y
     * @throws Exception the exception
     */
    public void cambiarMagnificacion (int x, int y)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo modificar la magnificacion, primero debera crear la hoja");
                 
        sheet.setZoom(x,y);   
    }
    
    /**
     * Cambiar ancho columna.
     *
     * @param columna the columna
     * @param ancho the ancho
     * @throws Exception the exception
     */
    public void cambiarAnchoColumna (int columna, int ancho) throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo cambiar el ancho de la columna, primero debera crear la hoja");
        sheet.setColumnWidth((short) columna, (short) ancho);
        
    }
    
    /*public void cambiarAltoFila (int fila, int alto) throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo cambiar alto de la fila, primero debera crear la hoja");
        sheet.setRowWidth((short) fila, (short) alto);
        
    }*/     
    
    /**
     * Crear panel.
     *
     * @param colSplit the col split
     * @param rowSplit the row split
     * @param leftmostColumn the leftmost column
     * @param topRow the top row
     * @throws Exception the exception
     */
    public void crearPanel (int colSplit, int rowSplit, int leftmostColumn, int topRow)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear el panel, primero debera crear la hoja");
        sheet.createFreezePane(colSplit, rowSplit , leftmostColumn ,topRow );
    }

    
    /**
     * Nuevo estilo.
     *
     * @param name the name
     * @param size the size
     * @param bold the bold
     * @param italic the italic
     * @param formato the formato
     * @param color the color
     * @param fondo the fondo
     * @param align the align
     * @return the hSSF cell style
     * @throws Exception the exception
     */
    public HSSFCellStyle nuevoEstilo (String name, int size, boolean bold, boolean italic , String formato ,int color, int fondo, int align)throws Exception{
        if (wb==null)
            throw new Exception("No se pudo crear el estilo, primero debera crear el libro");
        
        HSSFCellStyle style   = wb.createCellStyle();
        HSSFFont      font    = wb.createFont();
        
        font.setFontHeightInPoints((short) size);
        font.setFontName(name);                                 
        if (bold)   font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setItalic(italic);
        if (color!=NONE) font.setColor((short) color);
        
        
        style.setFont(font);
        if (!formato.equals(""))
            style.setDataFormat(wb.createDataFormat().getFormat(formato)); 
        
        if (fondo!=NONE) {
            style.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor((short)fondo);
        }
        
        if (align !=NONE) style.setAlignment((short)align);
        style.setVerticalAlignment( HSSFCellStyle.VERTICAL_CENTER  );
        return style;
        
    }
    
    
    
    /**
     * Nuevo estilo.
     *
     * @param name the name
     * @param size the size
     * @param bold the bold
     * @param italic the italic
     * @param formato the formato
     * @param color the color
     * @param fondo the fondo
     * @param align the align
     * @param border the border
     * @return the hSSF cell style
     * @throws Exception the exception
     */
    public HSSFCellStyle nuevoEstilo (String name, int size, boolean bold, boolean italic , String formato ,int color, int fondo, int align, int border)throws Exception{
        if (wb==null)
            throw new Exception("No se pudo crear  el estilo, primero debera crear el libro");
        
        HSSFCellStyle style   = wb.createCellStyle();
        HSSFFont      font    = wb.createFont();
        
        font.setFontHeightInPoints((short) size);
        font.setFontName(name);                                 
        if (bold)   font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setItalic(italic);
        if (color!=NONE) font.setColor((short) color);
        
        
        style.setFont(font);
        if (!formato.equals(""))
            style.setDataFormat(wb.createDataFormat().getFormat(formato)); 
        
        if (fondo!=NONE) {
            style.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor((short)fondo);
        }
        
        if (align!=NONE) style.setAlignment((short)align);
        style.setVerticalAlignment( HSSFCellStyle.VERTICAL_CENTER  );
        
        if (border>0){
            style.setBorderBottom((short)border);
            style.setBottomBorderColor(HSSFColor.BLACK.index);
            
            style.setBorderLeft((short)border);
            style.setLeftBorderColor(HSSFColor.BLACK.index);
            
            style.setBorderRight((short)border);
            style.setRightBorderColor(HSSFColor.BLACK.index);
            
            style.setBorderTop((short)border);
            style.setTopBorderColor(HSSFColor.BLACK.index);
        }      
        return style;
        
    }    
    
    
    /**
     * Metodo para obtenecer un nuevo color personalizado.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     * @return the hSSF color
     * @throws Exception the exception
     * @author mfontalvo
     * params rgb, codigo del color
     */
    public HSSFColor obtenerColor (int r, int g, int b ) throws Exception{
        if (wb==null)
            throw new Exception("No se pudo crear el color, primero debera crear el libro");
        
        java.awt.Color color 	= new java.awt.Color(r, g, b);
       
        HSSFColor newColor 		= 
        wb.getCustomPalette().findSimilarColor(
            (byte)color.getRed(),
            (byte)color.getGreen(),
            (byte)color.getBlue()
        );
        
        
        return newColor;
    }
    
    
    /**
     * Metodo para obtenecer un nuevo color personalizado.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     * @param indice the indice
     * @return the hSSF color
     * @throws Exception the exception
     * @author mfontalvo
     * params rgb, codigo del color
     */
    public HSSFColor obtenerColorIndice (int r, int g, int b, short indice ) throws Exception{
        if (wb==null)
            throw new Exception("No se pudo crear el color, primero debera crear el libro");
        
        java.awt.Color color 	= new java.awt.Color(r, g, b);
       
        wb.getCustomPalette().setColorAtIndex( indice,
            (byte)color.getRed(),
            (byte)color.getGreen(),
            (byte)color.getBlue()
        );
        
        HSSFColor newColor 		= wb.getCustomPalette().getColor(indice);        
        
        
        return newColor;
    }
    
    /**
     * Metodo para agregar formulas a una celda, 'Formulas sencillas'.
     *
     * @param fila fila de la hoja actual
     * @param columna columna de la hoja actual
     * @param formula formula que desea agregar
     * @param style estilo de la celda
     * @throws Exception the exception
     * @author mfontalvo
     */
    public void adicionarFormula(int fila, int columna, String formula, HSSFCellStyle style)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");

		HSSFRow row = sheet.getRow(fila);
        if(row == null){
        	row   = sheet.createRow(  fila);
        }
        HSSFCell cell = row.createCell ( columna);

        cell.setCellFormula(formula);

        if (style!=null) cell.setCellStyle(style);
    }   
    
    
    /**
     * Ajustar columnas.
     *
     * @param columnaInicio the columna inicio
     * @param columnaFin the columna fin
     * @throws Exception the exception
     */
    public void ajustarColumnas(int columnaInicio, int columnaFin)throws Exception{
        if (sheet==null)
            throw new Exception("No se pudo crear la celda, primero debera crear la hoja");
        
        
        for ( int i = columnaInicio; i <= columnaFin ; i++ ){
        	sheet.autoSizeColumn((short) i);
        }
    }    
	
	/**
	 * Obtiene la informacion de el wb.
	 *
	 * @return el wb
	 */
	public HSSFWorkbook getWb() {
		return wb;
	}

    /**
     * Abrir libro.
     *
     * @param file the file
     * @throws Exception the exception
     */
    public void abrirLibro (String file) throws Exception{
    	POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
    	wb = new HSSFWorkbook(poifsFileSystem);
    	fileName = file; 
    }
	
	
	
	
}

