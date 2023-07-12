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

import com.pe.lima.sg.bean.remision.RespuestaReporteBean;
import com.pe.lima.sg.bean.remision.SaldoMercaderiaBean;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.rs.RemisionReporteSaldoMercaderiaDao;
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
public class RemisionReporteSaldoMercaderiaAction  {

	@Autowired
	private RemisionReporteSaldoMercaderiaDao remisionReporteSaldoMercaderiaDao;


		



	/**
	 * Se encarga de mostrar la pagina de consulta de Venta por Cliente
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/operacion/remision/reporte/saldo/mercaderiaxenviar", method = RequestMethod.GET)
	public String mostrarSaldo(Model model, String path,  PageableSG pageable,HttpServletRequest request) {
		Filtro filtro = null;
		String strFecha	= null;
		try{
			log.debug("[mostrarSaldo] Inicio");
			path = "operacion/consulta/rem_rep_saldoxenviar";
			TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
			filtro = new Filtro();
			model.addAttribute("filtro", filtro);
			filtro.setCodigoEdificacion(usuario.getTblEmpresa().getCodigoEntidad());
			strFecha = UtilSGT.getFecha("dd/MM/yyyy");
			filtro.setFechaInicio(UtilSGT.getDateStringFormatddMMyyyy(UtilSGT.addDays(new Date(), -30)));
			filtro.setFechaFin(strFecha);
			request.getSession().setAttribute("sessionFiltroConsultaSaldo", filtro);
			log.debug("[mostrarSaldo] Fin");
		}catch(Exception e){
			log.debug("[mostrarSaldo] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			filtro = null;
		}

		return path;
	}

	@RequestMapping(value = "/operacion/remision/reporte/saldo/mercaderiaxenviar/q", method = RequestMethod.POST)
	public String traerRegistrosFiltrados(Model model, Filtro filtro, String path, HttpServletRequest request) {
		path = "operacion/consulta/rem_rep_saldoxenviar";
		//List<SaldoMercaderiaBean> listaSaldoMercaderia 	= null;
		RespuestaReporteBean respuestaReporteBean	= null;
		try{
			log.debug("[traerRegistrosFiltrados] Inicio");
			if (this.validarCriterio(filtro,model)){
				TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
				filtro.setCodigoEdificacion(usuario.getTblEmpresa().getCodigoEntidad());
				
				this.buscarSaldoMercaderiaxEnviar(model,filtro, request);
				
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
	private void buscarSaldoMercaderiaxEnviar(Model model, Filtro filtro, HttpServletRequest request) {
		List<SaldoMercaderiaBean> listaSaldoMercaderiaBean 	= null;
		RespuestaReporteBean respuestaReporteBean	= null;
		List<RespuestaReporteBean> listaRespuesta	= null;
		listaSaldoMercaderiaBean = remisionReporteSaldoMercaderiaDao.getReporteSaldoMercaderiaXls(filtro);
		if (listaSaldoMercaderiaBean!=null && !listaSaldoMercaderiaBean.isEmpty()) {
			listaRespuesta = new ArrayList<RespuestaReporteBean>();
			respuestaReporteBean = new RespuestaReporteBean();
			respuestaReporteBean.setDescripcion("Exito en la busqueda de saldo de mercaderia pendiente por enviar");
			respuestaReporteBean.setTotalRegistro(listaSaldoMercaderiaBean.size());
			listaRespuesta.add(respuestaReporteBean);
			model.addAttribute("registros", listaRespuesta);
			request.getSession().setAttribute("reporteSaldoMercaderiaXls", listaSaldoMercaderiaBean);
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
	@RequestMapping(value = "/operacion/remision/reporte/saldo/mercaderiaxenviarxls", method = RequestMethod.GET)
	public void excelReporteSaldoxMecaderia(Model model, HttpServletRequest request,  HttpServletResponse response) {
		List<SaldoMercaderiaBean> listaSaldoMercaderiaBean 	= null;
		//XSSFWorkbook workbook = new XSSFWorkbook();
		//int rowNum = 0;
		//Row row = null;
		//Cell cell = null;
		//int colNum = 0;
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
		
		try{
			log.debug("[excelReporteSaldoxMecaderia] Inicio");
			response.setHeader("Content-Disposition", "attachment; filename=\""+"SaldoxEnviar"+ UtilSGT.getFecha("yyyy-MM-dd kk:mm:ss").replace(':', '-')+".xls\"");
		    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		    TblUsuario usuario = (TblUsuario)request.getSession().getAttribute("UsuarioSession");
		    listaSaldoMercaderiaBean = (List<SaldoMercaderiaBean>)request.getSession().getAttribute("reporteSaldoMercaderiaXls");
			
			xls = new POIWrite();
			strNombreHoja = "SALDO";
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
			xls.adicionarCeldaTitulo(2, 1, "SALDO DE MERCADERIA PENDIENTE POR ENVIAR",tituloBlanco);
			xls.adicionarRangoCeldas(2, 2, 6, "", tituloBlanco);
			xls.combinarCeldas		(2, 1, 2, 6);
			int filaEncabezado = intFila;
			
			
			/** COMPROBANTE*/
			xls.adicionarCeldaTitulo(filaEncabezado, 0, "DATOS DEL COMPROBANTE",tituloAzul);
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
			xls.adicionarCeldaTitulo(intFila, intColumna++, "Saldo",tituloAzulClaro);
			intFila++;
			
			if (listaSaldoMercaderiaBean!=null && listaSaldoMercaderiaBean.size()>0){
				int i= -1;
				for(SaldoMercaderiaBean saldo: listaSaldoMercaderiaBean){
					i++;
					xls.adicionarCelda(intFila+i	, 0		,saldo.getFechaEmision()						, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 1		,saldo.getSerie()								, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 2		,saldo.getNumero()								, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 3		,saldo.getRuc()	  								, fString			);
					xls.adicionarCelda(intFila+i	, 4		,saldo.getCliente()		 						, fString			);
					xls.adicionarCelda(intFila+i	, 5		,saldo.getCodigoProducto()						, fString_CENTER	);
					xls.adicionarCelda(intFila+i	, 6		,saldo.getNombreProducto()						, fString			);
					xls.adicionarCelda(intFila+i	, 7		,saldo.getSaldo().doubleValue() 				, fDouble			);
				
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

			log.debug("[excelReporteSaldoxMecaderia] Fin");
		}catch(Exception e){
			log.debug("[excelReporteSaldoxMecaderia] Error:"+e.getMessage());
			e.printStackTrace();
		}finally{
			listaSaldoMercaderiaBean = null;
		}
		//return path;

	}
}