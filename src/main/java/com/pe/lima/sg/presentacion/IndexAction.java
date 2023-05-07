package com.pe.lima.sg.presentacion;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pe.lima.sg.entity.seguridad.TblOpcion;
import com.pe.lima.sg.presentacion.util.ListaUtilAction;


@Controller
public class IndexAction {

	@Autowired
	private ListaUtilAction listaUtil;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexAction.class);
	
	//@Autowired
	//private IUsuarioDAO usuario;

	/**
	 * Se encarga de cargar la pagina principal
	 * 
	 * @param httpSession
	 * @param authentication
	 * @return
	 */
	@RequestMapping("/")
	String cargarPagina(HttpSession httpSession, Authentication authentication) {
		LOGGER.debug("[cargarPagina] Inicio" );
		if (authentication != null) {
			User currentUser = (User) authentication.getPrincipal();
			LOGGER.debug("[cargarPagina] User:"+currentUser.getUsername() );
			/*Optional<TblUsuario> entidad = usuario.findOneByLogin(currentUser.getUsername());
			if (entidad != null && entidad.get() !=null && entidad.get().getNombre() != null){
				httpSession.setAttribute("usuario", entidad.get().getNombre());
				httpSession.setAttribute("perfil", entidad.get().getTblPerfil().getNombre());
				httpSession.setAttribute("id_usuario", entidad.get().getCodigoUsuario());
					
			}else{*/
				httpSession.setAttribute("usuario", "Pepito Perez");
				httpSession.setAttribute("perfil", "Administrator");
				httpSession.setAttribute("id_usuario", "1");
				
			/*}*/
			this.cargarDatos(httpSession);
			LOGGER.debug("[cargarPagina] Tipo Caducidad:"+httpSession.getAttribute("sesMapTiposCaducidad"));
			
			LOGGER.debug("[cargarPagina] Estado:"+httpSession.getAttribute("sesMapEstadousuario"));
			
			
			//this.cargarMenu(httpSession, entidad.get().getTblPerfil().getCodigoPerfil());
			
		}
		LOGGER.debug("[cargarPagina] Fin" );
		//return "index";
		return "inicio";
	}


	/**
	 * Se encarga de cargar en sesion los valores de las lista desplegables
	 * 
	 * @param httpSession
	 *            Sesion del usuario
	 */
	private void cargarDatos(HttpSession httpSession) {
		LOGGER.debug("[cargarDatos] Inicio" );
		Map<String, Object> map = null;
		map = listaUtil.obtenerValoresTipoCaducidad();
		httpSession.setAttribute("sesMapTiposCaducidad", map);
		map = listaUtil.obtenerValoresEstadoUsuario();
		httpSession.setAttribute("sesMapEstadousuario", map);
		
		LOGGER.debug("[cargarDatos] Fin" );
	}
	/**
	 * Obtener arbol de ID de las opciones
	 * 
	 * @param opciones
	 * @return
	 */
	public List<Integer> obtenerArbol(List<TblOpcion> opciones) {
		List<TblOpcion> asignados = new ArrayList<TblOpcion>();
		Item raiz = new Item(0);
		crearArbol(opciones, asignados, raiz);
		return crearMenu(raiz);
	}
	
	public List<Integer> crearMenu(Item item) {
		List<Integer> ids = new ArrayList<Integer>();

		for (Item hijo : item.hijos) {
			if (hijo.path == null) {
				ids.add(hijo.id);
				ids.addAll(crearMenu(hijo));
			} else {
				ids.add(new Integer(hijo.id));
			}
		}
		return ids;
	}
	/**
	 * Crea el arbol de opciones por el modulo
	 */
	public void crearArbol(List<TblOpcion> opciones, List<TblOpcion> asignados, Item seleccionado) {
		for (TblOpcion opcion : opciones) {

			if (opcion.getModulo().longValue() == seleccionado.id
					&& estaAsignado(asignados, opcion.getCodigoOpcion()) == false) {
				Item hijo = new Item(opcion, seleccionado);
				seleccionado.hijos.add(hijo);
				asignados.add(opcion);
				crearArbol(opciones, asignados, hijo);
			}
		}

		if ((opciones.size() > asignados.size()) && seleccionado.padre != null) {
			crearArbol(opciones, asignados, seleccionado.padre);
		}
	}
	
	class Item {
		Integer id;
		String label;
		String path;
		Item padre;
		boolean asignado = false;
		LinkedList<Item> hijos = new LinkedList<IndexAction.Item>();

		public Item(Integer id) {
			// TODO Auto-generated constructor stub
			this.id = id;
		}

		public Item(TblOpcion segTblOpcion, Item padre) {
			// TODO Auto-generated constructor stub
			this.id = segTblOpcion.getCodigoOpcion();
			this.label = segTblOpcion.getNombre();
			this.path = segTblOpcion.getRuta();
			this.padre = padre;
		}
	}
	/**
	 * Verifica..
	 * 
	 * @param asignados
	 * @param id
	 * @return
	 */
	public boolean estaAsignado(List<TblOpcion> asignados, long id) {
		for (TblOpcion opcion : asignados) {
			if (opcion.getCodigoOpcion() == id) {
				return true;
			}
		}
		return false;
	}

}
