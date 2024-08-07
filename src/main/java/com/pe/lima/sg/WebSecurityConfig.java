package com.pe.lima.sg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.dao.mantenimiento.ICatalogoDAO;
import com.pe.lima.sg.dao.mantenimiento.IEmpresaDAO;
import com.pe.lima.sg.dao.mantenimiento.IParametroDAO;
import com.pe.lima.sg.dao.mantenimiento.IProductoDAO;
import com.pe.lima.sg.dao.mantenimiento.ISerieDAO;
import com.pe.lima.sg.dao.mantenimiento.ITransporteDAO;
import com.pe.lima.sg.dao.mantenimiento.IUbigeoDAO;
import com.pe.lima.sg.dao.operacion.IConfiguracionDAO;
import com.pe.lima.sg.dao.seguridad.IOpcionDAO;
import com.pe.lima.sg.dao.seguridad.IUsuarioDAO;
import com.pe.lima.sg.db.util.IOperacionFacturador;
import com.pe.lima.sg.db.util.OperacionFacturadorImp;
import com.pe.lima.sg.entity.mantenimiento.TblCatalogo;
import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;
import com.pe.lima.sg.entity.mantenimiento.TblParametro;
import com.pe.lima.sg.entity.mantenimiento.TblProducto;
import com.pe.lima.sg.entity.mantenimiento.TblSerie;
import com.pe.lima.sg.entity.mantenimiento.TblTransporte;
import com.pe.lima.sg.entity.mantenimiento.TblUbigeo;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblConfiguracion;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.seguridad.TblOpcion;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
import com.pe.lima.sg.presentacion.Filtro;
import com.pe.lima.sg.presentacion.util.Constantes;
import com.pe.lima.sg.presentacion.util.ListaUtilAction;
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

	@Autowired
	AuthenticationSuccessHandler successHandler;
	
	@Autowired
	ITransporteDAO transporteDao;
	
	@Autowired
	private ICatalogoDAO catalogoDao;
	
	@Autowired
	private IConfiguracionDAO configuracionDao;
	
	@Autowired
	private ListaUtilAction listaUtil;
	
	@Autowired
	private IUbigeoDAO ubigeoDao;
	
	@Autowired
	private ISerieDAO serieDao;
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);
		
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
    	return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	LOGGER.debug("[configure] Inicio");
        /*http
                .authorizeRequests()
                    .antMatchers("/resources/**", "/registration").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login").usernameParameter("login").passwordParameter("clave")
                    .permitAll()
                    .and()
                .logout()
                    .permitAll();*/
    	http.csrf().disable()
		// Permite iframe del mismo origen
		.headers().frameOptions().disable().and()
		// Permite el acceso a css
		.authorizeRequests().antMatchers("/css/**").permitAll()
		// Permite el acceso a images
		.antMatchers("/images/**").permitAll()
		// Permite el acceso a js
		.antMatchers("/js/**").permitAll()
		// Permite el acceso a java
		.antMatchers("/java/**").permitAll()
		// Permite el acceso a la página de NoUser
		.antMatchers("/webjars/**").permitAll()
		// Permite el acceso a la página de restablecer contraseña
		.antMatchers("/restablecer/**").permitAll()
		// Cualquier solicitud debe ser autenticada
		.anyRequest().authenticated().and()
		// La página de login debe ser pública
		.formLogin().loginPage("/login").usernameParameter("login").passwordParameter("clave").successHandler(successHandler).permitAll().and()
		// El recurso de logout debe ser público
		.logout().logoutRequestMatcher(new AntPathRequestMatcher("/salir")).logoutSuccessUrl("/login");
		//.logout().permitAll();
    	LOGGER.debug("[configure] Fin");
    }

    @Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
    	LOGGER.debug("[authenticationSuccessHandler] Inicio");
		return new AuthenticationSuccessHandler() {
			
			RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
			
			@Autowired
			IUsuarioDAO usuR;
			
			@Autowired
			IParametroDAO parametroDao;
			
			@Autowired
			IProductoDAO productoDao;
			
			@Autowired
			IEmpresaDAO empresaDao;
			
			@Autowired
			private IOpcionDAO opcionDao;
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				LOGGER.debug("[onAuthenticationSuccess] Inicio");
				if (authentication.isAuthenticated()) {
					String login = authentication.getName();
					TblUsuario u = usuR.findOneByLogin(login);
					LOGGER.debug("[onAuthenticationSuccess] login:"+login);
					LOGGER.debug("[onAuthenticationSuccess] Usuario:"+u);
					if (u == null) {
						redirectStrategy.sendRedirect(request, response, "/login");
						
						new SecurityContextLogoutHandler().logout(request, response, authentication);
						SecurityContextHolder.getContext().setAuthentication(null);
						return;
					}else{
						if (u !=null && u.getTblEmpresa() == null){
							u.setTblEmpresa(new TblEmpresa());
							u.getTblEmpresa().setNombreComercial("PENDIENTE");
							u.getTblEmpresa().setRuc("000000000");
							
						}
						request.getSession().setAttribute("UsuarioSession", u);
						request.getSession().setAttribute("id_usuario", u.getCodigoUsuario());
						LOGGER.debug("[onAuthenticationSuccess] id_usuario:"+u.getCodigoUsuario());
						/*Cargamos las variables de session*/
						request.getSession().setAttribute("SessionMapEstadoUsuario", ListaUtilAction.obtenerValoresEstadoUsuario2());
						LOGGER.debug("[onAuthenticationSuccess] SessionMapEstadoUsuario");
						/*Lista de Tipo de Operacion*/
						this.obtenerValoresCatalogo(request);
						/*Cargamos el menu asociado al usuario*/
						List<TblOpcion> listaOpciones 	= null;
						String strCadena = "";
						if (u.getTblPerfil() != null){
							listaOpciones = opcionDao.listarOpcionesPerfil(u.getTblPerfil().getCodigoPerfil());
							strCadena = this.generarArbol(listaOpciones);
						}
						request.getSession().setAttribute("SessionMenu", strCadena);
					}
				}
				LOGGER.debug("[onAuthenticationSuccess] Fin");
				
				redirectStrategy.sendRedirect(request, response, "/");
			}
			

		    /**
			 * Generamos el arbol del menu
			 */
			private String generarArbol(List<TblOpcion> listaOpciones){
				String strResultado	= null;
				LOGGER.debug("[generarArbol] Inicio ");
				try{
					strResultado = Constantes.MENU_CABECERA_INI_PRINCIPAL;
					strResultado = strResultado + this.getOpcionesRecursivo(1000, listaOpciones);
					strResultado = strResultado + Constantes.MENU_CABECERA_FIN;
				}catch(Exception e){
					
				}
				LOGGER.debug("[generarArbol] Fin - strResultado: "+strResultado);
				return strResultado;
			}
			
			private String getOpcionesRecursivo(Integer intModulo, List<TblOpcion> listaOpciones){
				String strResultado = "";
				LOGGER.debug("[getOpcionesRecursivo] Inicio - Modulo: "+intModulo);
				for(TblOpcion opcion: listaOpciones){
					if (opcion.getModulo().compareTo(intModulo)==0){
						//Se valida si es nodo u hoja
						if (opcion.getRuta()==null || opcion.getRuta().equals("")){
							//Si es nodo
							//strResultado = strResultado + "<li id=\"child_node_"+opcion.getCodigoOpcion()+"\"> "+opcion.getNombre()+"</a> ";
							strResultado = strResultado + "<li>"+ opcion.getNombre();
							String strTemporal = getOpcionesRecursivo(opcion.getCodigoOpcion(),listaOpciones);
							if (strTemporal.length()>0){
								strResultado = strResultado +"	<ul>" + strTemporal + "</ul>";
							}
							//strResultado = strResultado + getOpcionesRecursivo(opcion.getCodigoOpcion(),listaOpciones);
							strResultado = strResultado + "</li>";
							
						}else{
							//Si es hoja
							strResultado = strResultado + "<li id=\"child_node_"+opcion.getCodigoOpcion()+"\"> <a href=\"#\" onclick=\"jsOpcionMenu('"+opcion.getRuta()+"');\">"+opcion.getNombre()+"</a> </li>";
							//strResultado = strResultado + "<li>"+ opcion.getNombre() + " <img src=\"/images/iconos/hoja.png\" alt=\"Hoja\" width=\"20px\"/> </li>";
							
						}
					}
				}
				LOGGER.debug("[getOpcionesRecursivo] Fin - resultado: "+strResultado);
				return strResultado;
			}
			public void asignarParametros(Filtro entidad, Map<String, TblParametro> mapParametro){
				TblParametro parametro =null;
				try{
					entidad.setComprobante(new TblComprobante());
					entidad.setDetalleComprobante(new TblDetalleComprobante());
					if (mapParametro!=null){
						
						//Ruta del repositorio Data
						parametro = mapParametro.get(Constantes.PARAMETRO_SUNAT_DATA);
						if (parametro!=null){
							entidad.setSunatData(parametro.getDato());
						}else{
							entidad.setSunatData(Constantes.SUNAT_FACTURADOR_RUTA_PRUEBA);
						}
						//Ruta de la Base de Datos
						parametro = mapParametro.get(Constantes.PARAMETRO_SUNAT_BD);
						if (parametro!=null){
							entidad.setSunatBD(parametro.getDato());
						}else{
							entidad.setSunatBD(Constantes.SUNAT_FACTURADOR_DB_LOCAL);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			 /**
			 * Listado del Catalogo
			 * 
			 */
		    public void obtenerValoresCatalogo(HttpServletRequest request) {
				//Adicionar los catalogo necesarios
				Map<String, Object> mapTipoOperacion 	= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoOperacionSfs12	= new LinkedHashMap<String, Object>();
				Map<String, Object> mapDomicilioFiscal	= new LinkedHashMap<String, Object>();
				Map<String, String> mapDomicilioFiscalSession	= new HashMap<String, String>();
				Map<String, Object> mapTipoDocumento	= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoMoneda		= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoUnidad		= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoAfectacion	= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoSistema		= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoComprobante	= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoLeyenda		= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoLeyendaCodigo= new LinkedHashMap<String, Object>();
				Map<String, Object> mapNotaCredito	   	= new LinkedHashMap<String, Object>();
				Map<String, Object> mapNotaDebito		= new LinkedHashMap<String, Object>();
				//Tipo de Comprobante: factura y boleta
				Map<String, Object> mapTipoFacturaBoleta	= new LinkedHashMap<String, Object>();
				//Tipo de comprobante: nota debito y credito
				Map<String, Object> mapTipoNota				= new LinkedHashMap<String, Object>();
				Map<String, Object> mapTipoMotivoTraslado	= new LinkedHashMap<String, Object>();
				Map<String, String> mapTipoMotivoTrasladoSesion	= new HashMap<String, String>();
				//Transporte
				Map<String, String> mapTransporte	= new LinkedHashMap<String, String>();
				//Domicilio Partida
				Map<String, Object> mapDomicilioPartidaDysalim		= new LinkedHashMap<String, Object>();
				Map<String, String> mapDomicilioPartidaDysalimDatos	= new HashMap<String, String>();
				Map<String, Object> mapDomicilioPartidaDistcen		= new LinkedHashMap<String, Object>();
				Map<String, String> mapDomicilioPartidaDistcenDatos	= new HashMap<String, String>();
				//Configuracion de la guia de remision
				Map<String, String> mapConfiguracionGreDysalim		= new HashMap<String, String>();
				Map<String, String> mapConfiguracionGreDistcen		= new HashMap<String, String>();
				
				List<TblCatalogo> listaCatalogo 		= null;
				IOperacionFacturador operacionFacturador		= null;
				List<ParametroFacturadorBean> listaParametro 	= null;
				List<TblParametro> listaParametroSistema		= null;
				Map<String, TblParametro> mapParametro			= null;
				Filtro filtro									= null;
				List<TblConfiguracion> listaConfiguracion		= null;
				List<TblProducto> listaProductoSistema			= null;
				Map<String, TblProducto> mapProducto			= null;
				List<TblEmpresa> listaEmpresa					= null;
				Map<String, Object> mapEmpresa					= null;
				Integer codigoEntidad 							= null;
				List<TblSerie> listaSerie 						= null;
				try{
					LOGGER.debug("[obtenerValoresCatalogo] inicio");
					//Para la EDS
					try{
						listaConfiguracion = configuracionDao.listarAllActivos();
						if (listaConfiguracion!=null){
							//Configuración por defecto para la EDS
							request.getSession().setAttribute("SessionListConfiguracion",listaConfiguracion);
						}
					}catch(Exception e){
						request.getSession().setAttribute("SessionListConfiguracion",null);
					}
					
					//Cargando los parametros del sistema
					codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
					
					listaCatalogo = catalogoDao.listarAllActivos();
					if (listaCatalogo!=null){
						
						for(TblCatalogo entidad: listaCatalogo){
							//Tipo de Operación
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_OPERACION)){
								mapTipoOperacion.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Tipo de Operación Nueva version (SFS 1.2.)
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_OPERACION_SFS_1_2)){
								mapTipoOperacionSfs12.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Domicilio Fiscal
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_DOMICILIO_FISCAL)){
								mapDomicilioFiscal.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
								mapDomicilioFiscalSession.put(entidad.getCodigoSunat(), entidad.getNombre());
							}
							//Tipo Documento
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_DOCUMENTO)){
								mapTipoDocumento.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Tipo Moneda
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_MONEDA)){
								mapTipoMoneda.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Tipo Unidad
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_UNIDAD)){
								mapTipoUnidad.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Tipo Afectacion
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_AFECTACION)){
								mapTipoAfectacion.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Tipo Sistema
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_SISTEMA)){
								mapTipoSistema.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Tipo Comprobante
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_COMPROBANTE)){
								mapTipoComprobante.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
								//Factura - Boleta
								if (entidad.getCodigoSunat().equals(Constantes.TIPO_COMPROBANTE_FACTURA) || entidad.getCodigoSunat().equals(Constantes.TIPO_COMPROBANTE_BOLETA) || entidad.getCodigoSunat().equals(Constantes.TIPO_COMPROBANTE_GUIA_REMISION)){
									mapTipoFacturaBoleta.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
								}
								//Nota Credito y Debito
								if (entidad.getCodigoSunat().equals(Constantes.TIPO_COMPROBANTE_NOTA_CREDITO) || entidad.getCodigoSunat().equals(Constantes.TIPO_COMPROBANTE_NOTA_DEBITO)){
									mapTipoNota.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
								}
							}
							//Tipo Leyenda
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_TIPO_LEYENDA)){
								mapTipoLeyenda.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
								mapTipoLeyendaCodigo.put(entidad.getCodigoSunat(), entidad.getCodigoSunat() + " : " +entidad.getNombre());
							}
							//Tipo Nota Credito
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_NOTA_CREDITO)){
								mapNotaCredito.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Tipo Nota Debito
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_NOTA_DEBITO)){
								mapNotaDebito.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
							}
							//Tipo Motivo Traslado
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_MOTIVO_TRASLADO)){
								mapTipoMotivoTraslado.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
								mapTipoMotivoTrasladoSesion.put(entidad.getCodigoSunat() , entidad.getNombre());
							}
							//Domicilio Partida
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_DIRECCION_PARTIDA)){
								if (entidad.getCodigoEmpresa().compareTo(Constantes.ENTIDAD_DYSALIM)==0) {
									mapDomicilioPartidaDysalim.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
									mapDomicilioPartidaDysalimDatos.put(entidad.getCodigoSunat(),entidad.getNombre());
								}
								if (entidad.getCodigoEmpresa().compareTo(Constantes.ENTIDAD_DISTCEN)==0) {
									mapDomicilioPartidaDistcen.put(entidad.getCodigoSunat() + " : " +entidad.getNombre(), entidad.getCodigoSunat());
									mapDomicilioPartidaDistcenDatos.put(entidad.getCodigoSunat(),entidad.getNombre());
								}
							}
							//Configuación de Guias de Remision
							if (entidad.getTblTipoCatalogo().getNombre().equals(Constantes.TIPO_CATALAGO_CONFIGURACION_GUIA)){
								if (entidad.getCodigoEmpresa().compareTo(Constantes.ENTIDAD_DYSALIM)==0) {
									mapConfiguracionGreDysalim.put(entidad.getCodigoSunat(),entidad.getDescripcion());
								}
								if (entidad.getCodigoEmpresa().compareTo(Constantes.ENTIDAD_DISTCEN)==0) {
									mapConfiguracionGreDistcen.put(entidad.getCodigoSunat(),entidad.getDescripcion());
								}
							}
						}
						//Configuracion de guias

						if (codigoEntidad.compareTo(Constantes.ENTIDAD_DYSALIM)==0) {
							request.getSession().setAttribute("SessionMapConfiguracionGuiaRemision",mapConfiguracionGreDysalim);
						}
						if (codigoEntidad.compareTo(Constantes.ENTIDAD_DISTCEN)==0) {
							request.getSession().setAttribute("SessionMapConfiguracionGuiaRemision",mapConfiguracionGreDistcen);
						}
						//Domicilio de Partida
						if (codigoEntidad.compareTo(Constantes.ENTIDAD_DYSALIM)==0) {
							request.getSession().setAttribute("SessionMapDomicilioPartida",mapDomicilioPartidaDysalim);
							request.getSession().setAttribute("SessionMapDomicilioPartidaDatos",mapDomicilioPartidaDysalimDatos);
						}
						if (codigoEntidad.compareTo(Constantes.ENTIDAD_DISTCEN)==0) {
							request.getSession().setAttribute("SessionMapDomicilioPartida",mapDomicilioPartidaDistcen);
							request.getSession().setAttribute("SessionMapDomicilioPartidaDatos",mapDomicilioPartidaDistcenDatos);
						}
						//Tipo de Operacion
						request.getSession().setAttribute("SessionMapTipoOperacion",mapTipoOperacion);
						//Tipo de Operacion SFS 1.2.
						request.getSession().setAttribute("SessionMapTipoOperacionSFS12",mapTipoOperacionSfs12);
						
						//Domicilio Fiscal
						request.getSession().setAttribute("SessionMapDomicilioFiscal",mapDomicilioFiscal);
						request.getSession().setAttribute("SessionMapDomicilioFiscalDescripcion",mapDomicilioFiscalSession);
						
						//Tipo Documento
						request.getSession().setAttribute("SessionMapTipoDocumento",mapTipoDocumento);
						//Tipo Moneda
						request.getSession().setAttribute("SessionMapTipoMoneda",mapTipoMoneda);
						//Tipo Unidad
						request.getSession().setAttribute("SessionMapTipoUnidad",mapTipoUnidad);
						//Tipo Afectacion
						request.getSession().setAttribute("SessionMapTipoAfectacion",mapTipoAfectacion);
						//Tipo Sistema
						request.getSession().setAttribute("SessionMapTipoSistema",mapTipoSistema);
						//Tipo Comprobante
						request.getSession().setAttribute("SessionMapTipoComprobante",mapTipoComprobante);
						//Factura y Boleta
						request.getSession().setAttribute("SessionMapTipoComprobanteFacturaBoleta",mapTipoFacturaBoleta);
						//Notas
						request.getSession().setAttribute("SessionMapTipoComprobanteNota",mapTipoNota);
						//Tipo Comprobante
						request.getSession().setAttribute("SessionMapTipoLeyenda",mapTipoLeyenda);
						//Tipo Leyenda
						request.getSession().setAttribute("SessionMapTipoLeyendaCodigo",mapTipoLeyendaCodigo);
						//Tipo Nota Credito
						request.getSession().setAttribute("SessionMapNotaCredito",mapNotaCredito);
						//Tipo Nota Debito
						request.getSession().setAttribute("SessionMapNotaDebito",mapNotaDebito);
						//Tipo Motivo Traslado
						request.getSession().setAttribute("SessionMapMotivoTraslado",mapTipoMotivoTraslado);
						request.getSession().setAttribute("SessionMapMotivoTrasladoDescripcion",mapTipoMotivoTrasladoSesion);
						
						//Lista completa de la tabla de catalogo
						request.getSession().setAttribute("SessionListCatalogo", listaCatalogo);
						//Numeros de serie
						//codigoEntidad = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getCodigoEntidad() ;
						listaSerie = serieDao.buscarAllxTipo(Constantes.TIPO_COMPROBANTE_GUIA_REMISION, codigoEntidad);
						request.getSession().setAttribute("SessionListSeriexEmpresa", listaSerie);
					}
					
					
					listaParametroSistema = parametroDao.listarAllActivos(codigoEntidad);
					if (listaParametroSistema!=null){
						mapParametro = new HashMap<String, TblParametro>();
						for(TblParametro parametro:listaParametroSistema){
							mapParametro.put(parametro.getNombre(), parametro);
							//ruc de la empresa
							/*if (parametro.getNombre().equals(Constantes.PARAMETRO_RUC_EMPRESA)){
								Constantes.SUNAT_RUC_EMISOR = parametro.getDato();
								System.out.println("Constantes.SUNAT_RUC_EMISOR: "+Constantes.SUNAT_RUC_EMISOR);
							}*/
						}
					}
					Constantes.SUNAT_RUC_EMISOR = ((TblUsuario)request.getSession().getAttribute("UsuarioSession") ).getTblEmpresa().getRuc();
					System.out.println("***************************** Constantes.SUNAT_RUC_EMISOR: "+Constantes.SUNAT_RUC_EMISOR);
					request.getSession().setAttribute("SessionMapParametroSistema",mapParametro);
					//Cargar todos los producto
					
					listaProductoSistema = productoDao.listarAllActivos(codigoEntidad);
					if (listaProductoSistema!=null){
						mapProducto = new HashMap<String, TblProducto>();
						for(TblProducto producto:listaProductoSistema){
							mapProducto.put(producto.getCodigoEmpresa(), producto);
						}
					}
					request.getSession().setAttribute("SessionListaProductoxEntidad",listaProductoSistema);
					request.getSession().setAttribute("SessionMapProductoSistema",mapProducto);
					//Cargando los parametros del Facturador
					try{
						filtro = new Filtro();
						this.asignarParametros(filtro, mapParametro);
						operacionFacturador = new OperacionFacturadorImp();
						listaParametro = operacionFacturador.consultarParametro(Constantes.SUNAT_PARAMETRO_SISTEMA, filtro);
						request.getSession().setAttribute("SessionListParametro",listaParametro);
					}catch(Exception e){
						request.getSession().setAttribute("SessionListParametro",listaParametro);
					}
					
					//Cargando los meses
					request.getSession().setAttribute("SessionMapMes", listaUtil.obtenerValoresMeses());
					
					//Cargando los años
					request.getSession().setAttribute("SessionMapAnio", listaUtil.obtenerAnios());
					
					//Cargando los años
					request.getSession().setAttribute("SessionFormasPago", listaUtil.obtenerValoresFormasPago());
					
					//Tipo de transporte
					request.getSession().setAttribute("SessionTipoTransporteGuia", listaUtil.obtenerTipoDeTransporte());
					
					/*Ubigeo*/
					request.getSession().setAttribute("SessionMapDepartamentoInei", obtenerDepartamento());
					request.getSession().setAttribute("SessionMapProvinciaInei", obtenerTodasLasProvincias());
					request.getSession().setAttribute("SessionMapDistritoInei", obtenerTodosLosDistritos());
					
					/*Unidad de Medida*/
					request.getSession().setAttribute("SessionUnidadMedidaGuia", listaUtil.obtenerValoresUnidadMedida());
					
					//Cargar todas las empresa
					listaEmpresa = empresaDao.listarAllActivos();
					if (listaEmpresa!=null){
						mapEmpresa = new HashMap<String, Object>();
						for(TblEmpresa empresa:listaEmpresa){
							mapEmpresa.put(empresa.getRuc() + " : " + empresa.getNombreComercial(), empresa.getRuc());
							System.out.println("Empresa:"+empresa.getNombreComercial());
						}
					}
					request.getSession().setAttribute("SessionMapEmpresa",mapEmpresa);
					//Cargamos los transportes
					List<TblTransporte> listaTransporte = transporteDao.buscarAllxEstado();
					if (listaTransporte != null) {
						mapTransporte = new LinkedHashMap<String, String>();
						for(TblTransporte transporte: listaTransporte) {
							/*String dato = ListaUtilAction.AddSpacio( transporte.getRuc(),16);
							dato = dato + ListaUtilAction.AddSpacio(transporte.getNombre(),80);
							dato = dato + ListaUtilAction.AddSpacio(transporte.getMarca(),20);
							dato = dato + ListaUtilAction.AddSpacio(transporte.getPlaca(),10);
							dato = dato + ListaUtilAction.AddSpacio(transporte.getNumeroCertificadoInscripcion(),32);
							dato = dato + ListaUtilAction.AddSpacio(transporte.getNumeroLicencia(),32);*/
							String dato;
							dato = transporte.getRuc();
							dato = dato + ":"+ transporte.getNombre();
							dato = dato + ":"+ transporte.getMarca();
							dato = dato + ":"+ transporte.getPlaca();
							dato = dato + ":"+ transporte.getNumeroCertificadoInscripcion();
							dato = dato + ":"+ transporte.getNumeroLicencia();
							mapTransporte.put(dato, dato);
						}
					}
					request.getSession().setAttribute("SessionMapTransporte",mapTransporte);
					
					/*Cargamos los tipos de origen de la factura*/
					request.getSession().setAttribute("SessionTipoOrigenFactura", listaUtil.obtenerTipoOrigenFactura());
					
					
					LOGGER.debug("[obtenerValoresCatalogo] Fin");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					listaCatalogo = null;
				}
			}
			
		};
	}
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	LOGGER.debug("[configureGlobal] Inicio");
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
  
   

	public Map<String, Object> obtenerDepartamento() {
		Map<String, Object> resultados = new LinkedHashMap<String, Object>();
		List<TblUbigeo> listaDepartamento = null;
		try{
			LOGGER.debug("[obtenerDepartamento] inicio");
			listaDepartamento = ubigeoDao.obtenerDepartamento();
			if (listaDepartamento!=null){
				for(TblUbigeo entidad: listaDepartamento){
					LOGGER.debug("[obtenerDepartamento] departamento:"+entidad.getNombre());
					resultados.put(entidad.getNombre(), entidad.getCodigoInei());
				}
				
			}
			LOGGER.debug("[obtenerDepartamento] Fin");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			listaDepartamento = null;
		}
		
		return resultados;
	}
	
	public Map<String,List<TblUbigeo>> obtenerTodasLasProvincias(){
		Map<String,List<TblUbigeo>> resultados = new HashMap<>();
		List<TblUbigeo> listaConsulta = null;
		List<TblUbigeo> listaProvincia = null;
		
		try {
			listaConsulta = ubigeoDao.obtenerProvincia();
			for(TblUbigeo entidad:listaConsulta) {
				listaProvincia = resultados.get(entidad.getCodigoPadreInei());
				if (listaProvincia == null) {
					listaProvincia = new ArrayList<>();
					listaProvincia.add(entidad);
					resultados.put(entidad.getCodigoPadreInei(), listaProvincia);
				}else {
					listaProvincia.add(entidad);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return resultados;
	}

	public Map<String,List<TblUbigeo>> obtenerTodosLosDistritos(){
		Map<String,List<TblUbigeo>> resultados = new HashMap<>();
		List<TblUbigeo> listaConsulta = null;
		List<TblUbigeo> listaDistritos = null;
		
		try {
			listaConsulta = ubigeoDao.obtenerDistrito();
			for(TblUbigeo entidad:listaConsulta) {
				listaDistritos = resultados.get(entidad.getCodigoPadreInei());
				if (listaDistritos == null) {
					listaDistritos = new ArrayList<>();
					listaDistritos.add(entidad);
					resultados.put(entidad.getCodigoPadreInei(), listaDistritos);
				}else {
					listaDistritos.add(entidad);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return resultados;
	}
	
}