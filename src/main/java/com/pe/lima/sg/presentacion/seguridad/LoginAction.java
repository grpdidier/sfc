package com.pe.lima.sg.presentacion.seguridad;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pe.lima.sg.dao.seguridad.IUsuarioDAO;
import com.pe.lima.sg.entity.seguridad.TblUsuario;
/**
 * 
 * @author Guido Cafiel
 * 
 */
@Controller
public class LoginAction {
	
	@Autowired
	IUsuarioDAO usuarioDao;
	
	//@Autowired
	//CorreoService notificar;
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginAction.class);

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
		return new ModelAndView("login", "error", error);
	}
	
	@RequestMapping(value = "/inicio", method = RequestMethod.GET)
	public String inicio(Model model, String path) {
		LOGGER.debug("[inicio] Panel de Control");
		return path;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(@RequestParam Optional<String> error) {
		LOGGER.debug("[login] Inicio");
		LOGGER.debug("[login] Fin");
		return new ModelAndView("login", "error", error);
	}
	
	@RequestMapping(value = "/restablecer", method = RequestMethod.GET)
	public String restablecerClave(Model model, @RequestParam Optional<String> error) {
		model.addAttribute("usuario", new TblUsuario());
		return "restablecer";
	}
	
	@RequestMapping(value = "/restablecer/cambio", method = RequestMethod.POST)
	public String validarCambioClave(TblUsuario segTblUsuario, Model model, HttpSession session) {
		TblUsuario encontrado = usuarioDao.findOneByLogin(segTblUsuario.getLogin());
		if (encontrado == null || encontrado.getLogin().equals("")) {
			session.setAttribute("mensaje", "Login inválido, no existe el Usuario.");
			return "redirect:/restablecer?error";
		}
		//this.notificarUsuarioExistente(encontrado.get());
		model.addAttribute("usuario", encontrado);
		session.setAttribute("ok", "Proceso Exitoso.");
		return "restablecer";
	}
	
	@RequestMapping(value = "/restablecer/cambio", method = RequestMethod.GET)
	public String cambiarClave(@RequestParam Optional<String> token, @RequestParam Optional<String> error, Model model, HttpSession session) {
		String mensaje = "";
		model.addAttribute("usuario", new TblUsuario());
		if (token != null && token.isPresent() && !token.get().isEmpty()) {
			String operadorToken = ":";
			String[] parte = token.get().split(operadorToken);
			if (parte.length != 3) {
				mensaje = " Token No valido - longitud ";
			} else {
				TblUsuario usuario = usuarioDao.findOne(new Integer(parte[0]));
				if (usuario != null) {
					String hash = usuario.getLogin()+usuario.getEmail();
					boolean valido = new BCryptPasswordEncoder().matches(hash, parte[1]);
					if (valido) {
						Date hoy = new Date();
						long fecha = new Long(parte[2]) + 86400000;
						Date fechaToken = new Date(fecha);
						
						if (hoy.before(fechaToken)) {
							model.addAttribute("usuario", usuario);
						} else {
							mensaje = " Token No valido - ya expiró ";
						}
					} else {
						mensaje = " Token No valido - hash ";
					}
				} else {
					mensaje = " Token No valido - datos del usuario ";
				}
			}
		}
		if (!mensaje.isEmpty()) {
			session.setAttribute("mensaje", mensaje);
			return "redirect:/restablecer?error";
		}
		return "cambio_clave";
	}
	
	@RequestMapping(value = "/restablecer/confirmar", method = RequestMethod.POST)
	public String confirmarCambioClave(@RequestParam Optional<String> error, TblUsuario segTblUsuario, Model model, HttpSession session, HttpServletRequest request) {
		model.addAttribute("usuario", new TblUsuario());
		if (segTblUsuario.getClave().equals(segTblUsuario.getConfirmarClave())) {
			String pattern = "((?=.*[a-z])(?=.*\\d)(?=.*[@#$%])(?=.*[A-Z]).{8})";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(segTblUsuario.getConfirmarClave());
			boolean matches = m.matches();
			if (matches) {
				String clave = segTblUsuario.getConfirmarClave();
				segTblUsuario = usuarioDao.findOne(segTblUsuario.getCodigoUsuario());
				segTblUsuario.setClave(new BCryptPasswordEncoder().encode(clave));
				segTblUsuario.setUsuarioModificacion(segTblUsuario.getCodigoUsuario());
				segTblUsuario.setFechaModificacion(new Date());
				String header = request.getHeader("X-FORWARDED-FOR");
				String ip = request.getRemoteAddr();
				
				if (header != null && !header.isEmpty()) {
					ip = header.split("\\s*,\\s*", 2)[0];
					
				}
				segTblUsuario.setIpModificacion(ip);
				usuarioDao.save(segTblUsuario);
				session.setAttribute("mensaje", "Cambio Exitoso.");
			} else {
				session.setAttribute("mensaje", "La nueva contraseña no es valida.");
				session.setAttribute("error", true);
			}
		} else {
			session.setAttribute("mensaje", "La clave no corresponde");
			session.setAttribute("error", true);
		}
		return "cambio_clave";
	}
	
	/**
	 * Se encarga de notificar al usuario existente para el cambio de clave.
	 * 
	 * @param usuarioBean
	 */
	/*private void notificarUsuarioExistente(TblUsuario entidad) {
		String operadorContenido = "|";
		String operadorToken = ":";
		//Genera el token de validacion
		StringBuilder token = new StringBuilder();
		token.append(entidad.getCodigoUsuario());
		token.append(operadorToken);
		String hash = new BCryptPasswordEncoder().encode(entidad.getLogin()+entidad.getEmail());
		token.append(hash);
		token.append(operadorToken);
		token.append(new Date().getTime());
		String asunto = "SICOM GNCV - RESTABLECER CONTRASEÑA";
		String url = "http://localhost:8080/restablecer/cambio?token=";
		String contenido = entidad.getNombre() + operadorContenido + url;
		//Enviar notificacion al usuario nuevo de sus credenciales
		//CorreoDTO msg = new CorreoDTO(null, entidad.getCorreo(), asunto, contenido, token.toString(), CorreoDTO.CORREO_CAMBIO_CLAVE);
		//notificar.enviar(msg);
	}*/
	
}
