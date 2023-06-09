package com.pe.lima.sg.entity.seguridad;
// Generated 18/09/2017 01:36:18 PM by Hibernate Tools 5.2.3.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pe.lima.sg.entity.mantenimiento.TblEmpresa;

/**
 * TblUsuario generated by hbm2java
 */
@Entity
@Table(name = "tbl_usuario", schema = "seg")
public class TblUsuario implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int codigoUsuario;
	private TblPerfil tblPerfil;
	private String nombre;
	private String login;
	private String clave;
	private String cambioClave;
	private String email;
	private String tipoCaducidad;
	private Date fechaCaducidad;
	private Integer diasCaducidad;
	private String observacion;
	private String mensajeIntentos;
	private String estadoUsuario;
	private String estado;
	private Integer usuarioCreacion;
	private Integer usuarioModificacion;
	private Date fechaCreacion;
	private Date fechaModificacion;
	private String ipCreacion;
	private String ipModificacion;
	private TblEmpresa tblEmpresa;
	
	private String confirmarClave;

	public TblUsuario() {
	}

	public TblUsuario(int codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}

	public TblUsuario(int codigoUsuario, TblPerfil tblPerfil, String nombre, String login, String clave,String cambioClave, String email,
			String tipoCaducidad, Date fechaCaducidad, Integer diasCaducidad, String observacion,
			String mensajeIntentos, String estadoUsuario, String estado, Integer usuarioCreacion,
			Integer usuarioModificacion, Date fechaCreacion, Date fechaModificacion, String ipCreacion,
			String ipModificacion, TblEmpresa tblEmpresa) {
		this.codigoUsuario = codigoUsuario;
		this.tblPerfil = tblPerfil;
		this.nombre = nombre;
		this.login = login;
		this.clave = clave;
		this.cambioClave = cambioClave;
		this.email = email;
		this.tipoCaducidad = tipoCaducidad;
		this.fechaCaducidad = fechaCaducidad;
		this.diasCaducidad = diasCaducidad;
		this.observacion = observacion;
		this.mensajeIntentos = mensajeIntentos;
		this.estadoUsuario = estadoUsuario;
		this.estado = estado;
		this.usuarioCreacion = usuarioCreacion;
		this.usuarioModificacion = usuarioModificacion;
		this.fechaCreacion = fechaCreacion;
		this.fechaModificacion = fechaModificacion;
		this.ipCreacion = ipCreacion;
		this.ipModificacion = ipModificacion;
		this.tblEmpresa = tblEmpresa;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "codigo_usuario", unique = true, nullable = false)
	public int getCodigoUsuario() {
		return this.codigoUsuario;
	}

	public void setCodigoUsuario(int codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_perfil", nullable = false)
	@JsonBackReference(value="tablaManage-TablaBack")
	public TblPerfil getTblPerfil() {
		return this.tblPerfil;
	}

	public void setTblPerfil(TblPerfil tblPerfil) {
		this.tblPerfil = tblPerfil;
	}

	@Column(name = "nombre", length = 128)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "login", length = 16)
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "clave", length = 32)
	public String getClave() {
		return this.clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	@Column(name = "email", length = 256)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "tipo_caducidad", length = 64)
	public String getTipoCaducidad() {
		return this.tipoCaducidad;
	}

	public void setTipoCaducidad(String tipoCaducidad) {
		this.tipoCaducidad = tipoCaducidad;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_caducidad", length = 13)
	public Date getFechaCaducidad() {
		return this.fechaCaducidad;
	}

	public void setFechaCaducidad(Date fechaCaducidad) {
		this.fechaCaducidad = fechaCaducidad;
	}

	@Column(name = "dias_caducidad")
	public Integer getDiasCaducidad() {
		return this.diasCaducidad;
	}

	public void setDiasCaducidad(Integer diasCaducidad) {
		this.diasCaducidad = diasCaducidad;
	}

	@Column(name = "observacion", length = 512)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "mensaje_intentos", length = 512)
	public String getMensajeIntentos() {
		return this.mensajeIntentos;
	}

	public void setMensajeIntentos(String mensajeIntentos) {
		this.mensajeIntentos = mensajeIntentos;
	}

	@Column(name = "estado_usuario", length = 1)
	public String getEstadoUsuario() {
		return this.estadoUsuario;
	}

	public void setEstadoUsuario(String estadoUsuario) {
		this.estadoUsuario = estadoUsuario;
	}

	@Column(name = "estado", length = 1)
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "usuario_creacion")
	public Integer getUsuarioCreacion() {
		return this.usuarioCreacion;
	}

	public void setUsuarioCreacion(Integer usuarioCreacion) {
		this.usuarioCreacion = usuarioCreacion;
	}

	@Column(name = "usuario_modificacion")
	public Integer getUsuarioModificacion() {
		return this.usuarioModificacion;
	}

	public void setUsuarioModificacion(Integer usuarioModificacion) {
		this.usuarioModificacion = usuarioModificacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_creacion", length = 29)
	public Date getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_modificacion", length = 29)
	public Date getFechaModificacion() {
		return this.fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	@Column(name = "ip_creacion", length = 64)
	public String getIpCreacion() {
		return this.ipCreacion;
	}

	public void setIpCreacion(String ipCreacion) {
		this.ipCreacion = ipCreacion;
	}

	@Column(name = "ip_modificacion", length = 64)
	public String getIpModificacion() {
		return this.ipModificacion;
	}

	public void setIpModificacion(String ipModificacion) {
		this.ipModificacion = ipModificacion;
	}
	@Transient
	public String getConfirmarClave() {
		return confirmarClave;
	}

	public void setConfirmarClave(String confirmarClave) {
		this.confirmarClave = confirmarClave;
	}

	@Column(name = "cambio_clave", length = 128)
	public String getCambioClave() {
		return cambioClave;
	}

	public void setCambioClave(String cambioClave) {
		this.cambioClave = cambioClave;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_entidad", nullable = false)
	@JsonBackReference(value="tablaManage-TablaBack")
	public TblEmpresa getTblEmpresa() {
		return this.tblEmpresa;
	}

	public void setTblEmpresa(TblEmpresa tblEmpresa) {
		this.tblEmpresa = tblEmpresa;
	}
}
