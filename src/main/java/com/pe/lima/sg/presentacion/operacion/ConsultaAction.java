package com.pe.lima.sg.presentacion.operacion;



import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conFechaEmisionAnioMes;
import static com.pe.lima.sg.dao.operacion.ComprobanteSpecifications.conCodigoEmpresa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pe.lima.sg.bean.facturador.VentaxClienteBean;
import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.dao.operacion.IComprobanteDAO;
import com.pe.lima.sg.dao.operacion.INotaDAO;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.entity.operacion.TblNota;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.POIWrite;
import com.pe.lima.sg.presentacion.util.PageWrapper;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;
/*
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;



/**
 * Clase que se encarga de la administracion de las Notas
 *
 * 			
 */
@Controller
public class ConsultaAction extends BaseOperacionPresentacion<TblNota> {

	@Autowired
	private IComprobanteDAO comprobanteDao;


	@Autowired
	private INotaDAO notaDao;

	//private static final String FILE_NAME = "ReporteVentaxCliente.xlsx";

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaAction.class);

	private String urlPaginado = "/operacion/consultas/paginado/"; 

	@Override
	public BaseOperacionDAO<TblNota, Integer> getDao() {
		return notaDao;
	}	



	/**
	 * Se encarga de mostrar la pagina de consulta de Venta por Cliente
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/consultas/ventaxcliente", method = RequestMethod.GET)
	public String mostrarVentaxCliente(Model model, String path,  PageableSG pageable,HttpServletRequest request) {
		Filtro filtro = null;
		String strFecha	= null;
		try{
			LOGGER.debug("[mostrarVentaxCliente] Inicio");
			path = "operacion/consulta/con_ventaxcliente";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			strFecha = UtilSGT.getFecha("yyyy-MM-dd");
			filtro.setAnio(new Integer(strFecha.substring(0,4)));
			filtro.setMes(strFecha.substring(5,7));
			this.mListarFacturaBoleta(model, filtro, pageable, this.urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroConsulta", filtro);
			LOGGER.debug("[mostrarVentaxCliente] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarVentaxCliente] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}

	/**
	 * Se encarga de buscar la informacion de los comprobantes
	 * seleccionado
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/consultas/ventaxcliente/q", method = RequestMethod.POST)
	public String buscarVentaxCliente(Model model, Filtro filtro,  PageableSG pageable, String path, HttpServletRequest request) {
		path = "operacion/consulta/con_ventaxcliente";
		try{
			LOGGER.debug("[buscarVentaxCliente] Inicio");
			this.mListarFacturaBoleta(model, filtro, pageable, urlPaginado, request);
			request.getSession().setAttribute("sessionFiltroConsulta", filtro);
			model.addAttribute("filtro", filtro);
			LOGGER.debug("[buscarVentaxCliente] Fin");
		}catch(Exception e){
			LOGGER.debug("[buscarVentaxCliente] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{

		}
		LOGGER.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}

	/*** Listado de Nota ***/
	private void mListarFacturaBoleta(Model model, Filtro filtro,  PageableSG pageable, String url, HttpServletRequest request){
		List<TblComprobante> entidades = new ArrayList<TblComprobante>();
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "numero"));
		Integer codigoEntidad = null;
		try{
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			Specification<TblComprobante> criterio = Specifications.where(conFechaEmisionAnioMes(filtro.getAnio().toString().concat("-").concat(filtro.getMes())))
					.and(conCodigoEmpresa(codigoEntidad));
			pageable.setSort(sort);
			//entidades = notaDao.findAll(criterio);
			Page<TblComprobante> entidadPage = comprobanteDao.findAll(criterio, pageable);
			PageWrapper<TblComprobante> page = new PageWrapper<TblComprobante>(entidadPage, url, pageable);
			model.addAttribute("registros", page.getContent());
			model.addAttribute("page", page);
			LOGGER.debug("[mListarFacturaBoleta] entidades:"+entidades);

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			entidades = null;
		}
	}




	@RequestMapping(value = "/operacion/consultas/paginado/{page}/{size}/{operacion}", method = RequestMethod.GET)
	public String paginarEntidad(@PathVariable Integer page, @PathVariable Integer size, @PathVariable String operacion, Model model,  PageableSG pageable, HttpServletRequest request) {
		Filtro filtro = null;
		String path = null;
		try{
			LOGGER.debug("[paginarEntidad] Inicio");
			path = "operacion/consulta/con_ventaxcliente";
			if (pageable!=null){
				if (pageable.getLimit() == 0){
					pageable.setLimit(size);
				}
				if (pageable.getOffset()== 0){
					pageable.setOffset(page*size);
				}
				if (pageable.getOperacion() ==null || pageable.getOperacion().equals("")){
					pageable.setOperacion(operacion);
				}

			}
			filtro = (Filtro)request.getSession().getAttribute("sessionFiltroConsulta");
			model.addAttribute("filtro", filtro);
			this.mListarFacturaBoleta(model, filtro, pageable, this.urlPaginado, request);

			LOGGER.debug("[paginarEntidad] Fin");
		}catch(Exception e){
			LOGGER.debug("[paginarEntidad] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}
		return path;
	}


	@Override
	public TblNota getNuevaEntidad() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	@RequestMapping(value="/operacion/consultas/ventaxclienteXLS",method= RequestMethod.GET)
	public @ResponseBody void ventaxClienteXLS(HttpServletResponse response, HttpServletRequest request)
	{
		try{
			InputStream jasperStream = this.getClass().getResourceAsStream("report/xxx.jrxml");
			JasperDesign design = JRXmlLoader.load(jasperStream);
			JasperReport report = JasperCompileManager.compileReport(design);
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			List<VentaxClienteBean> listaVentasxCliente = mObtenerVentasxCliente(request);
			JRDataSource jRDataSource = new JRBeanCollectionDataSource(listaVentasxCliente);
			parameterMap.put("datasource",jRDataSource);
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameterMap, jRDataSource);
			response.setHeader("Content-Disposition","inline; filename=reporteventacliente.xls");
			final OutputStream outputStream = response.getOutputStream();
			JRXlsExporter exporterXLS = new JRXlsExporter();
			exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
			exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputStream);
			exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
			exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);

			exporterXLS.exportReport();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/*
	 * Listado de comprobantes
	 */
	public List<VentaxClienteBean> mObtenerVentasxCliente(HttpServletRequest request){
		List<VentaxClienteBean> lista 			= null;
		VentaxClienteBean bean					= null;
		List<TblComprobante> listadoComprobante	= null;
		List<TblNota> listaNota					= null;
		String strAnioMes						= null;
		Filtro filtro							= null;
		Integer negativo						= -1;
		Integer codigoEntidad = null;
		try{
			filtro = (Filtro)request.getSession().getAttribute("sessionFiltroConsulta");
			strAnioMes = filtro.getAnio().toString().concat("-").concat(filtro.getMes().concat("%"));
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			listadoComprobante = comprobanteDao.findAllxAnioMes(strAnioMes, codigoEntidad);
			if (listadoComprobante!=null && listadoComprobante.size()>0){
				lista = new ArrayList<VentaxClienteBean>();
				for(TblComprobante comprobante : listadoComprobante){
					bean = new VentaxClienteBean();
					bean.setFechaEmision(comprobante.getFechaEmision());
					bean.setNombreCliente(comprobante.getNombreCliente());
					if (comprobante.getTipoComprobante().equals(Constantes.TIPO_COMPROBANTE_FACTURA)){
						bean.setNombreTipoComprobante("FACTURA");
					}
					if (comprobante.getTipoComprobante().equals(Constantes.TIPO_COMPROBANTE_BOLETA)){
						bean.setNombreTipoComprobante("BOLETA");
					}
					bean.setNumero(comprobante.getNumero());
					bean.setNumeroComprobante(comprobante.getSerie() + "-"+comprobante.getNumero());
					bean.setNumeroDocumento(comprobante.getNumeroDocumento());
					bean.setTotalIgv(comprobante.getTotalIgv());
					bean.setTotalImporte(comprobante.getTotalImporte());
					bean.setTotalOpGravada(comprobante.getTotalOpGravada());
					lista.add(bean);
				}
			}
			listaNota = notaDao.findAllxAnioMes(strAnioMes);
			if (listaNota !=null && listaNota.size()>0){
				if (lista==null){
					lista = new ArrayList<VentaxClienteBean>();
				}
				for(TblNota nota: listaNota){
					bean = new VentaxClienteBean();
					bean.setFechaEmision(nota.getFechaEmision());
					bean.setNombreCliente(nota.getNombreCliente());
					if (nota.getTipoComprobante().equals(Constantes.TIPO_COMPROBANTE_NOTA_CREDITO)){
						bean.setNombreTipoComprobante("NOTA CREDITO");
						bean.setTotalIgv(new BigDecimal(nota.getTotalIgv().doubleValue()*negativo));
						bean.setTotalImporte(new BigDecimal(nota.getTotalImporte().doubleValue()*negativo));
						bean.setTotalOpGravada(new BigDecimal(nota.getTotalOpGravada().doubleValue()*negativo));
					}
					if (nota.getTipoComprobante().equals(Constantes.TIPO_COMPROBANTE_NOTA_DEBITO)){
						bean.setNombreTipoComprobante("NOTA DEBITO");
						bean.setTotalIgv(nota.getTotalIgv());
						bean.setTotalImporte(nota.getTotalImporte());
						bean.setTotalOpGravada(nota.getTotalOpGravada());
					}
					bean.setNumero(nota.getNumero());
					bean.setNumeroComprobante(nota.getSerie() + "-"+nota.getNumero());
					bean.setNumeroDocumento(nota.getNumeroDocumento());
					
					lista.add(bean);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return lista;
	}

	@RequestMapping(value = "/operacion/consultas/ventaxclienteXLS", method = RequestMethod.GET)
	public void excelReporteVentaxCliente(Model model, HttpServletRequest request,  HttpServletResponse response) {
		List<VentaxClienteBean> lista 			= null;
		XSSFWorkbook workbook = new XSSFWorkbook();
		int rowNum = 0;
		Row row = null;
		Cell cell = null;
		int colNum = 0;
		//String path = "operacion/consulta/con_ventaxcliente";
		//Filtro filtro = null;
		try{
			LOGGER.debug("[excelReporteVentaxCliente] Inicio");
			response.setHeader("Content-Disposition", "attachment; filename=\"ReporteVentaxCliente.xlsx\"");
		    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		    
			lista = mObtenerVentasxCliente(request);
			XSSFSheet sheet = workbook.createSheet("RegVtas");
			if (lista!=null && lista.size()>0){
				
				row = sheet.createRow(rowNum++);
				cell = row.createCell(colNum++);
				cell.setCellValue("DISTRIBUCIONES KENOR S.A.");
				
				colNum = 0;
				//Nombre de las columnas
				row = sheet.createRow(rowNum++);
				cell = row.createCell(colNum++);
				cell.setCellValue("Fecha");
				cell = row.createCell(colNum++);
				cell.setCellValue("Comprobante");
				cell = row.createCell(colNum++);
				cell.setCellValue("Nro Documento");
				cell = row.createCell(colNum++);
				cell.setCellValue("R.U.C.");
				cell = row.createCell(colNum++);
				cell.setCellValue("Nombre de Cliente");
				cell = row.createCell(colNum++);
				cell.setCellValue("Valor Venta");
				cell = row.createCell(colNum++);
				cell.setCellValue("I.G.V.");
				cell = row.createCell(colNum++);
				cell.setCellValue("Total");
				for(VentaxClienteBean venta: lista){
					colNum = 0;
					row = sheet.createRow(rowNum++);
					cell = row.createCell(colNum++);
					cell.setCellValue(venta.getFechaEmision());
					cell = row.createCell(colNum++);
					cell.setCellValue(venta.getNombreTipoComprobante());
					cell = row.createCell(colNum++);
					cell.setCellValue(venta.getNumeroComprobante());
					cell = row.createCell(colNum++);
					cell.setCellValue(venta.getNumeroDocumento());
					cell = row.createCell(colNum++);
					cell.setCellValue(venta.getNombreCliente());
					cell = row.createCell(colNum++);
					cell.setCellValue(venta.getTotalOpGravada().doubleValue());
					cell = row.createCell(colNum++);
					cell.setCellValue(venta.getTotalIgv().doubleValue());
					cell = row.createCell(colNum++);
					cell.setCellValue(venta.getTotalImporte().doubleValue());
					
				}
				 try {
			            //FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
					 	ServletOutputStream outputStream = response.getOutputStream();
			            workbook.write(outputStream);
			            workbook.close();
			        } catch (FileNotFoundException e) {
			            e.printStackTrace();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
			}

			LOGGER.debug("[excelReporteVentaxCliente] Fin");
		}catch(Exception e){
			LOGGER.debug("[excelReporteVentaxCliente] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			lista = null;
		}
		//return path;

	}
	@RequestMapping(value = "/operacion/consultas/ventaxclienteXLS2", method = RequestMethod.GET)
	public void excelReporteVentaxCliente2(Model model, HttpServletRequest request,  HttpServletResponse response,  PageableSG pageable) {
		List<VentaxClienteBean> lista 			= null;
		XSSFWorkbook workbook = new XSSFWorkbook();
		int rowNum = 0;
		Row row = null;
		Cell cell = null;
		int colNum = 0;
		//String path = "operacion/consulta/con_ventaxcliente";
		//Filtro filtro = null;
		POIWrite xls 							= null;
		HSSFColor cAzul 						= null;
		HSSFColor cAzul_claro 					= null;
		HSSFColor cRosado 						= null;
		HSSFColor cRosado_claro 				= null;
		HSSFCellStyle tituloBlanco 				= null;
		HSSFCellStyle tituloBlancoLeft			= null;	
		HSSFCellStyle tituloAzul 				= null;
		HSSFCellStyle tituloRosado 				= null;
		HSSFCellStyle tituloAzulClaro 			= null;
		HSSFCellStyle tituloRosadoClaro 		= null;
		HSSFCellStyle fString 					= null;
		//HSSFCellStyle fInt 						= null;
		HSSFCellStyle fDouble 					= null;
		//HSSFCellStyle fFecha 					= null;
		//HSSFCellStyle fInt_CENTER 				= null;
		HSSFCellStyle fString_CENTER 			= null;
		
		String strNombreHoja					= null;
		int intFila								= 4;
		int intColumna							= 0;
		BigDecimal decTotalVenta					= new BigDecimal("0");
		BigDecimal decTotalIgv						= new BigDecimal("0");
		BigDecimal decTotalImporte					= new BigDecimal("0");
		/*String path = "operacion/consulta/con_ventaxcliente";
		Filtro filtro = null;*/
		try{
			LOGGER.debug("[excelReporteVentaxCliente] Inicio");
			response.setHeader("Content-Disposition", "attachment; filename=\"ReporteVentaxCliente.xls\"");
		    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		    
			lista = mObtenerVentasxCliente(request);
			
			xls = new POIWrite();
			strNombreHoja = "Registro de Venta";
			xls.nuevoLibro(strNombreHoja);
			
			//colores
			cAzul 			= xls.obtenerColorIndice(141, 180, 227, (short) 0x12);
			cAzul_claro 	= xls.obtenerColorIndice(197, 217, 241, (short) 0x13);
			cRosado 		= xls.obtenerColorIndice(217, 151, 149, (short) 0x14);
			cRosado_claro 	= xls.obtenerColorIndice(230, 185, 184, (short) 0x15);
			//estilos
			tituloBlancoLeft 	= xls.nuevoEstilo("Tahoma", 8, true, false, "text",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_LEFT, 1);
			tituloBlanco 	= xls.nuevoEstilo("Tahoma", 8, true, false, "text",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_CENTER, 1);
			tituloAzul 		= xls.nuevoEstilo("Tahoma", 8, true, false, "text",HSSFColor.BLACK.index, cAzul.getIndex(),HSSFCellStyle.ALIGN_CENTER, 1);
			tituloRosado 	= xls.nuevoEstilo("Tahoma", 8, true, false, "text",HSSFColor.BLACK.index, cRosado.getIndex(),HSSFCellStyle.ALIGN_CENTER, 1);

			tituloAzulClaro 	= xls.nuevoEstilo("Tahoma", 8, true, false, "text",HSSFColor.BLACK.index, cAzul_claro.getIndex(),HSSFCellStyle.ALIGN_CENTER, 1);
			tituloRosadoClaro 	= xls.nuevoEstilo("Tahoma", 8, true, false,"text", HSSFColor.BLACK.index, cRosado_claro.getIndex(),HSSFCellStyle.ALIGN_CENTER, 1);

			fString 		= xls.nuevoEstilo("Tahoma", 8, false, false, "",POIWrite.NONE, POIWrite.NONE, POIWrite.NONE);
			//fInt 			= xls.nuevoEstilo("Tahoma", 8, false, false, "#",POIWrite.NONE, POIWrite.NONE, POIWrite.NONE);
			fDouble 		= xls.nuevoEstilo("Tahoma", 8, false, false, "#0.00",POIWrite.NONE, POIWrite.NONE, POIWrite.NONE);
			//fFecha 			= xls.nuevoEstilo("Tahoma", 8, false, false, "dd/MM/yyyy",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_CENTER);
			//fInt_CENTER 	= xls.nuevoEstilo("Tahoma", 8, false, false, "#",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_CENTER);
			fString_CENTER 	= xls.nuevoEstilo("Tahoma", 8, false, false, "",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_CENTER);
			// obteniendo las hoja
			xls.obtenerHoja(strNombreHoja);
			
			
			//Titulo 1
			xls.adicionarCeldaTitulo(0, 0, "DISTRIBUCIONES KENOR S.A. ",tituloBlancoLeft);
			xls.adicionarRangoCeldas(0, 1, 3, "", tituloBlanco);
			xls.combinarCeldas		(0, 0, 2, 3);
			//Titulo 2
			xls.adicionarCeldaTitulo(2, 1, "REGISTRO DE VENTAS ",tituloBlanco);
			xls.adicionarRangoCeldas(2, 2, 6, "", tituloBlanco);
			xls.combinarCeldas		(2, 1, 2, 6);
			int filaEncabezado = intFila;
			
			
			/** COMPROBANTE*/
			xls.adicionarCeldaTitulo(filaEncabezado, 0, "DATOS DEL COMPROBANTE",tituloAzul);
			xls.adicionarRangoCeldas(filaEncabezado, 1, 2, "", tituloAzul);
			xls.combinarCeldas		(filaEncabezado, 0, filaEncabezado, 2);
			intFila++;
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Fecha",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Tipo Comprobante",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Nro de Documento",tituloAzulClaro);
			/** DATOS EL CLIENTE**/
			xls.adicionarCeldaTitulo(filaEncabezado, 3, "DATOS DEL CLIENTE",tituloRosado);
			xls.adicionarRangoCeldas(filaEncabezado, 4, 4, "", tituloRosado);
			xls.combinarCeldas		(filaEncabezado, 3, filaEncabezado, 4);

			xls.adicionarCeldaTitulo(intFila, intColumna++, "R.U.C.",tituloRosadoClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Nombre del Cliente",tituloRosadoClaro);
			/** DATOS DE LA VENTA **/
			xls.adicionarCeldaTitulo(filaEncabezado, 5, "DATOS DEL COMPROBANTE",tituloAzul);
			xls.adicionarRangoCeldas(filaEncabezado, 6, 7, "", tituloAzul);
			xls.combinarCeldas		(filaEncabezado, 5, filaEncabezado, 7);

			xls.adicionarCeldaTitulo(intFila, intColumna++, "Valor Venta",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "I.G.V.",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Total",tituloAzulClaro);
			intFila++;
			
			if (lista!=null && lista.size()>0){
				int i= -1;
				for(VentaxClienteBean venta: lista){
					i++;
					xls.adicionarCelda(intFila+i	, 0		,venta.getFechaEmision()						, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 1		,venta.getNombreTipoComprobante()				, fString			);
					xls.adicionarCelda(intFila+i	, 2		,venta.getNumeroComprobante()					, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 3		,venta.getNumeroDocumento()		  				, fString			);
					xls.adicionarCelda(intFila+i	, 4		,venta.getNombreCliente() 						, fString			);
					xls.adicionarCelda(intFila+i	, 5		,venta.getTotalOpGravada().doubleValue()		, fDouble			);
					xls.adicionarCelda(intFila+i	, 6		,venta.getTotalIgv().doubleValue() 				, fDouble			);
					xls.adicionarCelda(intFila+i	, 7		,venta.getTotalImporte().doubleValue() 			, fDouble			);
					
					decTotalVenta = decTotalVenta.add(venta.getTotalOpGravada());
					decTotalIgv = decTotalIgv.add(venta.getTotalIgv());
					decTotalImporte = decTotalImporte.add(venta.getTotalImporte());
				}
				xls.adicionarCeldaTitulo(intFila + lista.size(), 4, "TOTAL GENERAL",tituloAzul);
				xls.adicionarCelda(intFila + lista.size(), 5, decTotalVenta.doubleValue() ,fDouble);
				xls.adicionarCelda(intFila + lista.size(), 6, decTotalIgv.doubleValue() ,fDouble);
				xls.adicionarCelda(intFila + lista.size(), 7, decTotalImporte.doubleValue() ,fDouble);
				
			}
			//Ajustando columnas
			xls.ajustarColumnas(0, intColumna);
			
			// cerrando el libro
			xls.cerrarLibro(response.getOutputStream());
			/*filtro = (Filtro) request.getSession().getAttribute("sessionFiltroConsulta");
		 	this.mListarFacturaBoleta(model, filtro, pageable, this.urlPaginado);
			request.getSession().setAttribute("sessionFiltroConsulta", filtro);
			*/

			LOGGER.debug("[excelReporteVentaxCliente] Fin");
		}catch(Exception e){
			LOGGER.debug("[excelReporteVentaxCliente] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			lista = null;
		}
		//return path;

	}
}