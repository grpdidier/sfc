package com.pe.lima.sg.presentacion.operacion;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pe.lima.sg.bean.facturador.VentaxClienteBean;
import com.pe.lima.sg.bean.facturador.VentaxProductoBean;
import com.pe.lima.sg.dao.BaseOperacionDAO;
import com.pe.lima.sg.dao.operacion.IDetalleComprobanteDAO;
import com.pe.lima.sg.dao.operacion.INotaDAO;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblNota;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.BaseOperacionPresentacion;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.POIWrite;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;

/**
 * Clase que se encarga de la administracion de las Notas
 *
 * 			
 */
@Controller
public class ConsultaVentaxProductoAction extends BaseOperacionPresentacion<TblNota> {

	/*@Autowired
	private IComprobanteDAO comprobanteDao;*/


	@Autowired
	private INotaDAO notaDao;
	
	@Autowired
	private IDetalleComprobanteDAO detalleComprobanteDao;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaVentaxProductoAction.class);


	@Override
	public BaseOperacionDAO<TblNota, Integer> getDao() {
		return notaDao;
	}	



	/**
	 * Se encarga de mostrar la pagina de consulta de Venta por Producto
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/consultas/ventaxproducto", method = RequestMethod.GET)
	public String mostrarVentaxProducto(Model model, String path,  PageableSG pageable,HttpServletRequest request) {
		Filtro filtro = null;
		String strFecha	= null;
		List<TblDetalleComprobante> listaDetalle	= null;
		VentaxProductoBean bean		= null;
		List<VentaxProductoBean> listaDatos	= null;
		int contador = 0;
		Integer codigoEntidad = null;
		try{
			LOGGER.debug("[mostrarVentaxProducto] Inicio");
			model.addAttribute("registros", null);
			path = "operacion/consulta/con_ventaxproducto";
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			strFecha = UtilSGT.getFecha("yyyy-MM-dd");
			filtro.setFechaInicio(UtilSGT.getDateStringFormat(UtilSGT.addDays(new Date(), -15)));
			filtro.setFechaFin(strFecha);
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			listaDetalle = detalleComprobanteDao.findAllxRangoFecha(filtro.getFechaInicio(), filtro.getFechaFin(), codigoEntidad);
			if(listaDetalle!=null && listaDetalle.size()>0){
				listaDetalle = this.mTotalizarProducto(listaDetalle);
				listaDatos = new ArrayList<VentaxProductoBean>();
				for(TblDetalleComprobante detalle: listaDetalle){
					if (contador == 5){
						break;
					}
					bean = new VentaxProductoBean();
					bean.setCodigoProducto(detalle.getDescripcion().substring(0,5));
					bean.setDescripcion(detalle.getDescripcion().substring(6,detalle.getDescripcion().length()-7));
					bean.setCantidad(detalle.getCantidad());
					bean.setTotal(detalle.getPrecioFinal());
					listaDatos.add(bean);
					contador++;
				}
				model.addAttribute("respuesta", "Total Elementos: "+listaDetalle.size()+ ", realice la exportación a Excel para ver la información.");
				
				model.addAttribute("registros", listaDatos);
			}
			filtro.setFechaInicio(UtilSGT.getddMMyyyy(filtro.getFechaInicio()));
			filtro.setFechaFin(UtilSGT.getddMMyyyy(filtro.getFechaFin()));
			
			request.getSession().setAttribute("sessionFiltroConsulta", filtro);
			LOGGER.debug("[mostrarVentaxProducto] Fin");
		}catch(Exception e){
			LOGGER.debug("[mostrarVentaxProducto] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}
	/*
	 * Totaliza los valores
	 */
	public List<TblDetalleComprobante> mTotalizarProducto(List<TblDetalleComprobante> lista){
		Map<String, TblDetalleComprobante> mapProducto = new HashMap<String, TblDetalleComprobante>();
		List<TblDetalleComprobante> listaDetalle = new ArrayList<TblDetalleComprobante>();
		TblDetalleComprobante detTemporal = null;
		for(TblDetalleComprobante detalle: lista){
			detTemporal = mapProducto.get(detalle.getDescripcion());
			if (detTemporal ==null){
				mapProducto.put(detalle.getDescripcion(), detalle);
			}else{
				detTemporal.setCantidad(detTemporal.getCantidad().add(detalle.getCantidad()));
				detTemporal.setPrecioFinal(detTemporal.getPrecioFinal().add(detalle.getPrecioFinal()));
			}
		}
		Iterator it = mapProducto.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        listaDetalle.add((TblDetalleComprobante)pair.getValue());
	    }
		
		return listaDetalle;
	}
	
	/**
	 * Se encarga de buscar la informacion de los comprobantes
	 * seleccionado
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/consultas/ventaxproducto/q", method = RequestMethod.POST)
	public String buscarVentaxCliente(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/consulta/con_ventaxproducto";
		List<TblDetalleComprobante> listaDetalle	= null;
		VentaxProductoBean bean		= null;
		List<VentaxProductoBean> listaDatos	= null;
		int contador = 0;
		Integer codigoEntidad = null;
		try{
			LOGGER.debug("[buscarVentaxCliente] Inicio");
			model.addAttribute("registros", null);
			codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
			listaDetalle = detalleComprobanteDao.findAllxRangoFecha(UtilSGT.getYYYYMMDD(filtro.getFechaInicio()), UtilSGT.getYYYYMMDD(filtro.getFechaFin()), codigoEntidad);
			if(listaDetalle!=null && listaDetalle.size()>0){
				listaDetalle = this.mTotalizarProducto(listaDetalle);
				listaDatos = new ArrayList<VentaxProductoBean>();
				for(TblDetalleComprobante detalle: listaDetalle){
					if (contador == 5){
						break;
					}
					bean = new VentaxProductoBean();
					bean.setCodigoProducto(detalle.getDescripcion().substring(0,5));
					bean.setDescripcion(detalle.getDescripcion().substring(6,detalle.getDescripcion().length()));
					bean.setCantidad(detalle.getCantidad());
					bean.setTotal(detalle.getPrecioFinal());
					listaDatos.add(bean);
					contador++;
				}
				model.addAttribute("respuesta", "Total Elementos: "+listaDetalle.size()+ ", realice la exportación a Excel para ver la información.");
				model.addAttribute("registros", listaDatos);
			}
			
			
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


	@Override
	public TblNota getNuevaEntidad() {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "/operacion/consultas/ventaxproductoXLS2", method = RequestMethod.GET)
	public void excelReporteVentaxProducto2(Model model, HttpServletRequest request,  HttpServletResponse response,  PageableSG pageable) {
		List<VentaxClienteBean> lista 			= null;
		XSSFWorkbook workbook = new XSSFWorkbook();
		int rowNum = 0;
		Row row = null;
		Cell cell = null;
		int colNum = 0;
		//String path = "operacion/consulta/con_ventaxproducto";
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
		HSSFCellStyle fInt 						= null;
		HSSFCellStyle fDouble 					= null;
		//HSSFCellStyle fFecha 					= null;
		//HSSFCellStyle fInt_CENTER 				= null;
		HSSFCellStyle fString_CENTER 			= null;
		List<TblDetalleComprobante> listaDetalle	= null;
		VentaxProductoBean bean		= null;
		List<VentaxProductoBean> listaDatos	= null;
		String strNombreHoja					= null;
		int intFila								= 4;
		int intColumna							= 0;
		double intTotalCantidad				= 0;
		BigDecimal decsubTotal					= new BigDecimal("0");
		BigDecimal decTotal						= new BigDecimal("0");
		BigDecimal calculoSubTotal				= new BigDecimal("0");
		/*String path = "operacion/consulta/con_ventaxproducto";*/
		Filtro filtro = null;
		Integer codigoEntidad = null;
		try{
			LOGGER.debug("[excelReporteVentaxProducto2] Inicio");
			response.setHeader("Content-Disposition", "attachment; filename=\"ReporteVentaxProducto.xls\"");
		    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		    codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
		    filtro = (Filtro)request.getSession().getAttribute("sessionFiltroConsulta");
			listaDetalle = detalleComprobanteDao.findAllxRangoFecha(UtilSGT.getYYYYMMDD(filtro.getFechaInicio()), UtilSGT.getYYYYMMDD(filtro.getFechaFin()),codigoEntidad);
			if(listaDetalle!=null && listaDetalle.size()>0){
				listaDetalle = this.mTotalizarProducto(listaDetalle);
				listaDatos = new ArrayList<VentaxProductoBean>();
				for(TblDetalleComprobante detalle: listaDetalle){
					
					bean = new VentaxProductoBean();
					bean.setCodigoProducto(detalle.getDescripcion().substring(0,4));
					bean.setDescripcion(detalle.getDescripcion().substring(5,detalle.getDescripcion().length()));
					bean.setCantidad(detalle.getCantidad());
					bean.setTotal(detalle.getPrecioFinal());
					listaDatos.add(bean);
				}
				
			}
			
			xls = new POIWrite();
			strNombreHoja = "Venta por Producto";
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
			fInt 			= xls.nuevoEstilo("Tahoma", 8, false, false, "#",POIWrite.NONE, POIWrite.NONE, POIWrite.NONE);
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
			xls.adicionarCeldaTitulo(2, 1, "REPORTE DE VENTA POR PRODUCTO ["+filtro.getFechaInicio() +" al "+ filtro.getFechaFin() +"]",tituloBlanco);
			xls.adicionarRangoCeldas(2, 2, 3, "", tituloBlanco);
			xls.combinarCeldas		(2, 1, 2, 3);
			int filaEncabezado = intFila;
			
			
			/** PRODUCTO*/
			xls.adicionarCeldaTitulo(filaEncabezado, 0, "DATOS DEL PRODUCTO",tituloAzul);
			xls.adicionarRangoCeldas(filaEncabezado, 1, 1, "", tituloAzul);
			xls.combinarCeldas		(filaEncabezado, 0, filaEncabezado, 1);
			intFila++;
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Codigo",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Descripcion Producto",tituloAzulClaro);
			/** DATOS DE LA VENTA **/
			xls.adicionarCeldaTitulo(filaEncabezado, 2, "DATOS DE LA VENTA",tituloRosado);
			xls.adicionarRangoCeldas(filaEncabezado, 3, 4, "", tituloRosado);
			xls.combinarCeldas		(filaEncabezado, 2, filaEncabezado, 4);

			xls.adicionarCeldaTitulo(intFila, intColumna++, "Cantidad",tituloRosadoClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Sub Total",tituloRosadoClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Total",tituloRosadoClaro);
			
			intFila++;
			
			if (listaDatos!=null && listaDatos.size()>0){
				int i= -1;
				for(VentaxProductoBean venta: listaDatos){
					i++;
					calculoSubTotal = venta.getTotal().multiply(new BigDecimal("100"));
					calculoSubTotal = calculoSubTotal.divide(new BigDecimal("118"), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
							
					xls.adicionarCelda(intFila+i	, 0		,venta.getCodigoProducto()						, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 1		,venta.getDescripcion()							, fString			);
					xls.adicionarCelda(intFila+i	, 2		,venta.getCantidad().doubleValue()				, fInt			);
					xls.adicionarCelda(intFila+i	, 3		,calculoSubTotal.doubleValue()	 				, fDouble			);
					xls.adicionarCelda(intFila+i	, 4		,venta.getTotal().doubleValue()					, fDouble			);
					
					intTotalCantidad = intTotalCantidad + venta.getCantidad().doubleValue();
					decsubTotal = decsubTotal.add(calculoSubTotal);
					decTotal = decTotal.add(venta.getTotal());
				}
				xls.adicionarCeldaTitulo(intFila + listaDatos.size(), 2, "TOTAL GENERAL",tituloAzul);
				//xls.adicionarCelda(intFila + lista.size(), 5, decTotalVenta.doubleValue() ,fDouble);
				xls.adicionarCelda(intFila + listaDatos.size(), 3, decsubTotal.doubleValue() ,fDouble);
				xls.adicionarCelda(intFila + listaDatos.size(), 4, decTotal.doubleValue() ,fDouble);
				
			}
			//Ajustando columnas
			xls.ajustarColumnas(0, intColumna);
			
			// cerrando el libro
			xls.cerrarLibro(response.getOutputStream());
			/*filtro = (Filtro) request.getSession().getAttribute("sessionFiltroConsulta");
		 	this.mListarFacturaBoleta(model, filtro, pageable, this.urlPaginado);
			request.getSession().setAttribute("sessionFiltroConsulta", filtro);
			*/

			LOGGER.debug("[excelReporteVentaxProducto2] Fin");
		}catch(Exception e){
			LOGGER.debug("[excelReporteVentaxProducto2] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			lista = null;
		}
		//return path;

	}
}