package com.pe.lima.sg.presentacion.operacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pe.lima.sg.bean.remision.MovimientoBean;
import com.pe.lima.sg.bean.remision.RespuestaReporteBean;
import com.pe.lima.sg.bean.remision.SaldoMercaderiaBean;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.rs.ReporteMovimientoGreAConsigancionDao;
import com.pe.lima.sg.presentacion.util.POIWrite;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;

import lombok.extern.slf4j.Slf4j;


/**
 * Clase que se encarga de la administracion del reporte a movimientos de guias de remision a consignacion
 *
 * 			
 */
@Controller
@Slf4j
public class ReporteMovimientoGreAConsigancionAction  {

	@Autowired
	private ReporteMovimientoGreAConsigancionDao reporteMovimientoGreAConsigancionDao;

	/**
	 * Se encarga de mostrar la pagina de consulta de Venta por Cliente
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/reporte/movimiento/gre/consignacion", method = RequestMethod.GET)
	public String mostrarMovimiento(Model model, String path,  PageableSG pageable,HttpServletRequest request) {
		Filtro filtro = null;
		String strFecha	= null;
		try{
			log.debug("[mostrarMovimiento] Inicio");
			path = "operacion/consulta/sfs_rep_mov_gre_consigancion";
			TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setCodigoEdificacion(usuario.getTblEmpresa().getCodigoEntidad());
			strFecha = UtilSGT.getFecha("dd/MM/yyyy");
			filtro.setFechaInicio(UtilSGT.getDateStringFormatddMMyyyy(UtilSGT.addDays(new Date(), -30)));
			filtro.setFechaFin(strFecha);
			request.getSession().setAttribute("sessionFiltroMovimientoGre", filtro);
			log.debug("[mostrarMovimiento] Fin");
		}catch(Exception e){
			log.debug("[mostrarMovimiento] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}

	@RequestMapping(value = "/operacion/reporte/movimiento/gre/consignacion/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/consulta/sfs_rep_mov_gre_consigancion";
		//List<SaldoMercaderiaBean> listaSaldoMercaderia 	= null;
		RespuestaReporteBean respuestaReporteBean	= null;
		try{
			log.debug("[traerRegistrosFiltrados] Inicio");
			if (this.validarCriterio(filtro,model)){
				TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
				filtro.setCodigoEdificacion(usuario.getTblEmpresa().getCodigoEntidad());
				
				this.buscarMovimientoGreAConsignacion(model,filtro, request);
				
			}else{
				model.addAttribute("filtro", filtro);
				model.addAttribute("registros", respuestaReporteBean);
				//Datos del reporte a imprimir en el PDF
				request.getSession().setAttribute("reporteGreAConsignacionXls", null);
				
			}
			
			
		}catch(Exception e){
			log.debug("[traerRegistrosFiltrados] Error: "+e.getMessage());
			e.printStackTrace();
			model.addAttribute("respuesta", "Se produco un Error:"+e.getMessage());
		}finally{
			//listaSaldoMercaderia		= null;
			respuestaReporteBean	= null;
		}
		log.debug("[traerRegistrosFiltrados] Fin");
		return path;
	}
	@SuppressWarnings("unchecked")
	private void buscarMovimientoGreAConsignacion(Model model, Filtro filtro, HttpServletRequest request) {
		List<MovimientoBean> listaBean 	= null;
		RespuestaReporteBean respuestaReporteBean	= null;
		List<RespuestaReporteBean> listaRespuesta	= null;
		Map<String, TblProducto> mapProducto		= null;
		mapProducto = (Map<String, TblProducto>) request.getSession().getAttribute("SessionMapProductoSistema");
		listaBean = reporteMovimientoGreAConsigancionDao.getReporteMovimientoGreAConsignacionXls(filtro,mapProducto);
		if (listaBean!=null && !listaBean.isEmpty()) {
			listaRespuesta = new ArrayList<RespuestaReporteBean>();
			respuestaReporteBean = new RespuestaReporteBean();
			respuestaReporteBean.setDescripcion("Exito en la busqueda de movimiento de guias de remision a consignacion");
			Integer totalRegistros = obtenerTotalRegistros(listaBean);
			respuestaReporteBean.setTotalRegistro(totalRegistros);
			listaRespuesta.add(respuestaReporteBean);
			model.addAttribute("registros", listaRespuesta);
			request.getSession().setAttribute("reporteGreAConsignacionXls", listaBean);
			request.getSession().setAttribute("sessionFiltroMovimientoGre", filtro);
		}else {
			model.addAttribute("respuesta", "No se encontró ningún dato");
		}
		
	}



	private Integer obtenerTotalRegistros(List<MovimientoBean> listaBean) {
		Integer total = 0;
		for(MovimientoBean movimiento: listaBean) {
			total = total + movimiento.getListaDatos().size();
		}
		return total;
	}

	public boolean validarCriterio(Filtro filtro,Model model){
		boolean resultado = true;
		try{
			
			
			//Validando la fecha
			if (filtro.getFechaInicio() == null || filtro.getFechaInicio().length()< 8){
				resultado = false;
				model.addAttribute("respuesta", "Debe ingresar la fecha de inicio del reporte");
			}
			if (filtro.getFechaFin() == null || filtro.getFechaFin().length()< 8){
				resultado = false;
				model.addAttribute("respuesta", "Debe ingresar la fecha de fin del reporte");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return resultado;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@RequestMapping(value = "/operacion/reporte/movimiento/gre/consignacion/xls", method = RequestMethod.GET)
	public void excelReporteMovimientoGreAConsignacion(Model model, HttpServletRequest request,  HttpServletResponse response) {
		List<MovimientoBean> listaBean 			= null;
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
		HSSFCellStyle fDouble 					= null;
		HSSFCellStyle fDouble_BOLD				= null;
		HSSFCellStyle fString_CENTER 			= null;
		HSSFCellStyle fString_CENTER_BOLD		= null;
		HSSFCellStyle fString_LEFT				= null;
		String strNombreHoja					= null;
		int intFila								= 6;
		int intColumna							= 0;
		
		try{
			log.debug("[excelReporteMovimientoGreAConsignacion] Inicio");
			response.setHeader("Content-Disposition", "attachment; filename=\""+"MovimientoGreAConsignacion"+ UtilSGT.getFecha("yyyy-MM-dd kk:mm:ss").replace(':', '-')+".xls\"");
		    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		    TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
		    listaBean = (List<MovimientoBean>)request.getSession().getAttribute("reporteGreAConsignacionXls");
			
			xls = new POIWrite();
			strNombreHoja = "MOVIMIENTO";
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
			fDouble_BOLD 	= xls.nuevoEstilo("Tahoma", 8, true, false, "#0.00",POIWrite.NONE, POIWrite.NONE, POIWrite.NONE);
			//fFecha 			= xls.nuevoEstilo("Tahoma", 8, false, false, "dd/MM/yyyy",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_CENTER);
			//fInt_CENTER 	= xls.nuevoEstilo("Tahoma", 8, false, false, "#",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_CENTER);
			fString_CENTER 	= xls.nuevoEstilo("Tahoma", 8, false, false, "",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_CENTER);
			fString_CENTER_BOLD = xls.nuevoEstilo("Tahoma", 8, true, false, "",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_CENTER);
			fString_LEFT	= xls.nuevoEstilo("Tahoma", 8, true, false, "",POIWrite.NONE, POIWrite.NONE, HSSFCellStyle.ALIGN_LEFT);
			// obteniendo las hoja
			xls.obtenerHoja(strNombreHoja);
			
			
			//Titulo 1
			xls.adicionarCeldaTitulo(0, 0, usuario.getTblEmpresa().getNombreComercial(),tituloBlancoLeft);
			xls.adicionarRangoCeldas(0, 1, 3, "", tituloBlanco);
			xls.combinarCeldas		(0, 0, 2, 3);
			//Titulo 2
			xls.adicionarCeldaTitulo(2, 1, "REPORTE MOVIMIENTO DE GUIAS DE REMISION A CONSIGNACION",tituloBlanco);
			xls.adicionarRangoCeldas(2, 2, 6, "", tituloBlanco);
			xls.combinarCeldas		(2, 1, 2, 6);
			
			Filtro filtro = (Filtro)request.getSession().getAttribute("sessionFiltroMovimientoGre");
			
			xls.adicionarCeldaTitulo(4, 1, "Desde " + filtro.getFechaInicio() + " a " + filtro.getFechaFin() + " - "+filtro.getRazonSocial(),tituloBlanco);
			xls.adicionarRangoCeldas(4, 2, 6, "", tituloBlanco);
			xls.combinarCeldas		(4, 1, 2, 6);
			
			int filaEncabezado = intFila;
			
			
			/** COMPROBANTE*/
			xls.adicionarCeldaTitulo(filaEncabezado, 0, "DATOS DEL DOCUMENTO",tituloAzul);
			xls.adicionarRangoCeldas(filaEncabezado, 1, 2, "", tituloAzul);
			xls.combinarCeldas		(filaEncabezado, 0, filaEncabezado, 2);
			intFila++;
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Fecha",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Serie",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Número",tituloAzulClaro);
			/** DATOS EL CLIENTE**/
			xls.adicionarCeldaTitulo(filaEncabezado, 3, "DATOS DEL CLIENTE",tituloRosado);
			xls.adicionarRangoCeldas(filaEncabezado, 4, 4, "", tituloRosado);
			xls.combinarCeldas		(filaEncabezado, 3, filaEncabezado, 4);

			xls.adicionarCeldaTitulo(intFila, intColumna++, "R.U.C.",tituloRosadoClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Nombre Razón Social",tituloRosadoClaro);
			/** DATOS DE LA VENTA **/
			xls.adicionarCeldaTitulo(filaEncabezado, 5, "DATOS PRODUCTO",tituloAzul);
			xls.adicionarRangoCeldas(filaEncabezado, 6, 7, "", tituloAzul);
			xls.combinarCeldas		(filaEncabezado, 5, filaEncabezado, 7);

			xls.adicionarCeldaTitulo(intFila, intColumna++, "Cantidad",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Precio",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Total",tituloAzulClaro);
			intFila++;
			
			if (listaBean!=null && listaBean.size()>0){
				int i= -1;
				for(MovimientoBean movimiento: listaBean){
					i++;
					xls.adicionarCelda		(intFila+i	, 0		,movimiento.getTotales().getNombreProducto()		, fString_LEFT	);
					xls.adicionarRangoCeldas(intFila+i  , 1		, 7		, ""										, fString_LEFT);
					xls.combinarCeldas		(intFila+i  , 0		, intFila+i											, 7);
					for(SaldoMercaderiaBean saldo:movimiento.getListaDatos()) {
						i++;
						xls.adicionarCelda(intFila+i	, 0		,saldo.getFechaEmision()						, fString_CENTER	);
						xls.adicionarCelda(intFila+i	, 1		,saldo.getSerie()								, fString_CENTER	);
						xls.adicionarCelda(intFila+i	, 2		,saldo.getNumero()								, fString_CENTER	);
						xls.adicionarCelda(intFila+i	, 3		,saldo.getRuc()	  								, fString			);
						xls.adicionarCelda(intFila+i	, 4		,saldo.getCliente()		 						, fString			);
						xls.adicionarCelda(intFila+i	, 5		,saldo.getCantidad().doubleValue()				, fDouble	);
						xls.adicionarCelda(intFila+i	, 6		,saldo.getPrecio().doubleValue()				, fDouble			);
						xls.adicionarCelda(intFila+i	, 7		,saldo.getTotal().doubleValue()					, fDouble			);
					}
					i++;
					xls.adicionarCelda(intFila+i	, 4		,"TOTAL:"						, fString_CENTER_BOLD	);
					xls.adicionarCelda(intFila+i	, 5		,movimiento.getTotales().getTotalCantidad().doubleValue()						, fDouble_BOLD	);
					xls.adicionarCelda(intFila+i	, 7		,movimiento.getTotales().getTotal().doubleValue()								, fDouble_BOLD	);
					
				}
				
				
			}
			//Ajustando columnas
			xls.ajustarColumnas(0, intColumna);
			
			// cerrando el libro
			xls.cerrarLibro(response.getOutputStream());
			/*filtro = (Filtro) request.getSession().getAttribute("sessionFiltroConsulta");
		 	this.mListarFacturaBoleta(model, filtro, pageable, this.urlPaginado);
			request.getSession().setAttribute("sessionFiltroConsulta", filtro);
			*/

			log.debug("[excelReporteMovimientoGreAConsignacion] Fin");
		}catch(Exception e){
			log.debug("[excelReporteMovimientoGreAConsignacion] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			listaBean = null;
		}
		//return path;

	}
}