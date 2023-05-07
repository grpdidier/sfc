
package com.pe.lima.sg.presentacion;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Se encarga de representar un campo en un formulario
 * 
 * @author Gregorio Rodriguez
 * @version 19/09/2017
 *
 */
public class Campo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String tipo;
	private String valor;
	private LinkedHashMap<String, Object> opciones;
	private boolean required;
	private String id;

	/**
	 * @param tipo
	 * @param valor
	 */
	public Campo(String tipo, String valor) {
		super();
		this.tipo = tipo;
		this.valor = valor;
		this.opciones = new LinkedHashMap<>();
		this.required = false;
	}
	
	public Campo(String tipo, String valor, String id) {
		super();
		this.tipo = tipo;
		this.valor = valor;
		this.opciones = new LinkedHashMap<>();
		this.required = false;
		this.id = id;
	}

	public Campo(String tipo, String valor, boolean required) {
		super();
		this.tipo = tipo;
		this.valor = valor;
		this.opciones = new LinkedHashMap<>();
		this.required = required;
	}

	public Campo(String tipo, String valor, LinkedHashMap<String, Object> opciones) {
		super();
		this.tipo = tipo;
		this.valor = valor;
		this.opciones = opciones;
		this.required = false;
	}

	public Campo(String tipo, String valor, LinkedHashMap<String, Object> opciones, String id) {
		super();
		this.tipo = tipo;
		this.valor = valor;
		this.opciones = opciones;
		this.required = false;
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public LinkedHashMap<String, Object> getOpciones() {
		return opciones;
	}

	public void setOpciones(LinkedHashMap<String, Object> opciones) {
		this.opciones = opciones;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
