package com.pe.lima.sg.presentacion.operacion;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.pe.lima.sg.bean.remision.GuiaConsignacionBean;
import com.pe.lima.sg.bean.remision.RespuestaReporteBean;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.rs.RemisionReporteGuiaConsingacionDao;
import com.pe.lima.sg.presentacion.util.POIWrite;
import com.pe.lima.sg.presentacion.util.PageableSG;
import com.pe.lima.sg.presentacion.util.UtilSGT;

import lombok.extern.slf4j.Slf4j;


/**
 * Clase que se encarga de la administracion de las Notas
 *
 * 			
 */
@Controller
@Slf4j
public class RemisionReporteGuiaConsignacionAction  {

	@Autowired
	private RemisionReporteGuiaConsingacionDao remisionReporteGuiaConsingacionDao;

	/**
	 * Se encarga de mostrar la pagina de consulta de Venta por Cliente
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/remision/reporte/guia/consignacion", method = RequestMethod.GET)
	public String mostrarConsignacion(Model model, String path,  PageableSG pageable,HttpServletRequest request) {
		Filtro filtro = null;
		String strFecha	= null;
		try{
			log.debug("[mostrarConsignacion] Inicio");
			path = "operacion/consulta/rem_rep_consignacion";
			TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setCodigoEdificacion(usuario.getTblEmpresa().getCodigoEntidad());
			strFecha = UtilSGT.getFecha("dd/MM/yyyy");
			filtro.setFechaInicio(UtilSGT.getDateStringFormatddMMyyyy(UtilSGT.addDays(new Date(), -30)));
			filtro.setFechaFin(strFecha);
			request.getSession().setAttribute("sessionFiltroConsultaSaldo", filtro);
			log.debug("[mostrarConsignacion] Fin");
		}catch(Exception e){
			log.debug("[mostrarConsignacion] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}

	@RequestMapping(value = "/operacion/remision/reporte/guia/consignacion/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/consulta/rem_rep_consignacion";
		RespuestaReporteBean respuestaReporteBean	= null;
		try{
			log.debug("[traerRegistrosFiltrados] Inicio");
			if (this.validarCriterio(filtro,model)){
				TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
				filtro.setCodigoEdificacion(usuario.getTblEmpresa().getCodigoEntidad());
				
				this.buscarGuiaxConsignacion(model,filtro, request);
				
			}else{
				model.addAttribute("filtro", filtro);
				model.addAttribute("registros", respuestaReporteBean);
				//Datos del reporte a imprimir en el PDF
				request.getSession().setAttribute("reporteSaldoMercaderiaXls", null);
				
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
	private void buscarGuiaxConsignacion(Model model, Filtro filtro, HttpServletRequest request) {
		List<GuiaConsignacionBean> listaGuiaConsignacionBean 	= null;
		RespuestaReporteBean respuestaReporteBean	= null;
		List<RespuestaReporteBean> listaRespuesta	= null;
		listaGuiaConsignacionBean = remisionReporteGuiaConsingacionDao.getReporteGuiaxConsignacionXls(filtro);
		if (listaGuiaConsignacionBean!=null && !listaGuiaConsignacionBean.isEmpty()) {
			listaRespuesta = new ArrayList<RespuestaReporteBean>();
			respuestaReporteBean = new RespuestaReporteBean();
			respuestaReporteBean.setDescripcion("Exito en la busqueda de saldo de mercaderia pendiente por enviar");
			respuestaReporteBean.setTotalRegistro(listaGuiaConsignacionBean.size());
			listaRespuesta.add(respuestaReporteBean);
			model.addAttribute("registros", listaRespuesta);
			request.getSession().setAttribute("reporteGuiaConsignacionXls", listaGuiaConsignacionBean);
			request.getSession().setAttribute("sessionFiltroConsultaSaldo", filtro);
		}else {
			model.addAttribute("respuesta", "No se encontró ningún dato");
		}
		
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
	@RequestMapping(value = "/operacion/remision/reporte/guia/consignacionxls", method = RequestMethod.GET)
	public void excelReporteGuiaxConsignacion(Model model, HttpServletRequest request,  HttpServletResponse response) {
		List<GuiaConsignacionBean> listaGuiaConsignacionBean 	= null;

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
		int intFila								= 6;
		int intColumna							= 0;
		
		try{
			log.debug("[excelReporteGuiaxConsignacion] Inicio");
			response.setHeader("Content-Disposition", "attachment; filename=\""+"GuiaxConsignacion"+ UtilSGT.getFecha("yyyy-MM-dd kk:mm:ss").replace(':', '-')+".xls\"");
		    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		    TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
		    listaGuiaConsignacionBean = (List<GuiaConsignacionBean>)request.getSession().getAttribute("reporteGuiaConsignacionXls");
			
			xls = new POIWrite();
			strNombreHoja = "GUIA";
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
			xls.adicionarCeldaTitulo(0, 0, usuario.getTblEmpresa().getNombreComercial(),tituloBlancoLeft);
			xls.adicionarRangoCeldas(0, 1, 3, "", tituloBlanco);
			xls.combinarCeldas		(0, 0, 2, 3);
			//Titulo 2
			xls.adicionarCeldaTitulo(2, 1, "REGISTRO DE GUIAS DE REMISION A CONSIGNACION",tituloBlanco);
			xls.adicionarRangoCeldas(2, 2, 6, "", tituloBlanco);
			xls.combinarCeldas		(2, 1, 2, 6);
			
			Filtro filtro = (Filtro)request.getSession().getAttribute("sessionFiltroConsultaSaldo");
			
			xls.adicionarCeldaTitulo(4, 1, "Desde " + filtro.getFechaInicio() + " a " + filtro.getFechaFin(),tituloBlanco);
			xls.adicionarRangoCeldas(4, 2, 6, "", tituloBlanco);
			xls.combinarCeldas		(4, 1, 2, 6);
			int filaEncabezado = intFila;
			
			
			/** COMPROBANTE*/
			xls.adicionarCeldaTitulo(filaEncabezado, 0, "GUIA DE REMISION REMITENTE",tituloAzul);
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

			xls.adicionarCeldaTitulo(intFila, intColumna++, "Código",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Descripción del Producto",tituloAzulClaro);
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Cantidad",tituloAzulClaro);
			intFila++;
			
			if (listaGuiaConsignacionBean!=null && listaGuiaConsignacionBean.size()>0){
				int i= -1;
				for(GuiaConsignacionBean saldo: listaGuiaConsignacionBean){
					i++;
					xls.adicionarCelda(intFila+i	, 0		,saldo.getFechaEmision()						, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 1		,saldo.getSerie()								, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 2		,saldo.getNumero()								, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 3		,saldo.getRuc()	  								, fString			);
					xls.adicionarCelda(intFila+i	, 4		,saldo.getCliente()		 						, fString			);
					xls.adicionarCelda(intFila+i	, 5		,saldo.getCodigoProducto()						, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 6		,saldo.getNombreProducto()						, fString			);
					xls.adicionarCelda(intFila+i	, 7		,saldo.getCantidad().doubleValue() 				, fDouble			);
				
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

			log.debug("[excelReporteGuiaxConsignacion] Fin");
		}catch(Exception e){
			log.debug("[excelReporteGuiaxConsignacion] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			listaGuiaConsignacionBean = null;
		}
		//return path;

	}
}