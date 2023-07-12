package com.pe.lima.sg.entity.mantenimiento;
// Generated 26/11/2017 05:22:50 PM by Hibernate Tools 4.3.5.Final

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import com.pe.lima.sg.presentacion.util.Constantes;

/**
 * TblSerie generated by hbm2java
 */
@Entity
@Table(name = "tbl_transporte", schema = "mae")
public class TblTransporte implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int codigoTransporte;
	private String nombre;
	private String ruc;
	private String marca;
	private String modelo;
	private String placa;
	private String numeroCertificadoInscripcion;
	private String numeroLicencia;
	
	private String estado;
	private Date fechaCreacion;
	private Date fechaModificacion;
	private Integer usuarioCreacion;
	private Integer usuarioModificacion;
	private String ipCreacion;
	private String ipModificacion;
	private String numeroRegistroMtc;
	private String remolque;
	
	public TblTransporte() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "codigo_transporte", unique = true, nullable = false)
	public int getCodigoTransporte() {
		return this.codigoTransporte;
	}

	public void setCodigoTransporte(int codigoTransporte) {
		this.codigoTransporte = codigoTransporte;
	}

	@Column(name = "nombre", length = 256)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	@Column(name = "estado", length = 1)
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
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

	
	
	public void setAuditoriaCreacion(HttpServletRequest request){
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			this.setFechaCreacion(new Date(System.currentTimeMillis()));
			this.setIpCreacion(request.getRemoteAddr());
			this.setEstado(Constantes.ESTADO_REGISTRO_ACTIVO);
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			this.setUsuarioCreacion(idUsuario);
		}catch(Exception e){
			e.printStackTrace();
			this.setUsuarioCreacion(0);
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}
	}
	
	public void setAuditoriaModificacion(HttpServletRequest request){
		Integer idUsuario 		= null;
		String strIdUsuario 	= null;
		try{
			this.setFechaModificacion(new Date(System.currentTimeMillis()));
			this.setIpModificacion(request.getRemoteAddr());
			strIdUsuario = (String)request.getSession().getAttribute("id_usuario");
			if (strIdUsuario==null){
				idUsuario = 1;
			}else{
				idUsuario = new Integer(strIdUsuario);
			}
			this.setUsuarioModificacion(idUsuario);
		}catch(Exception e){
			e.printStackTrace();
			this.setUsuarioModificacion(0);
		}finally{
			idUsuario 		= null;
			strIdUsuario	= null;
		}
	}


	public TblTransporte(int codigoTransporte, String nombre, String marca, String modelo, String placa,
			String numeroCertificadoInscripcion, String numeroLicencia, String estado, Date fechaCreacion,
			Date fechaModificacion, Integer usuarioCreacion, Integer usuarioModificacion, String ipCreacion,
			String ipModificacion) {
		super();
		this.codigoTransporte = codigoTransporte;
		this.nombre = nombre;
		this.marca = marca;
		this.modelo = modelo;
		this.placa = placa;
		this.numeroCertificadoInscripcion = numeroCertificadoInscripcion;
		this.numeroLicencia = numeroLicencia;
		this.estado = estado;
		this.fechaCreacion = fechaCreacion;
		this.fechaModificacion = fechaModificacion;
		this.usuarioCreacion = usuarioCreacion;
		this.usuarioModificacion = usuarioModificacion;
		this.ipCreacion = ipCreacion;
		this.ipModificacion = ipModificacion;
	}

	@Column(name = "marca", length = 128)
	public String getMarca() {
		return marca;
	}


	public void setMarca(String marca) {
		this.marca = marca;
	}

	@Column(name = "placa", length = 32)
	public String getPlaca() {
		return placa;
	}


	public void setPlaca(String placa) {
		this.placa = placa;
	}

	@Column(name = "numero_cert_inscripcion", length = 64)
	public String getNumeroCertificadoInscripcion() {
		return numeroCertificadoInscripcion;
	}


	public void setNumeroCertificadoInscripcion(String numeroCertificadoInscripcion) {
		this.numeroCertificadoInscripcion = numeroCertificadoInscripcion;
	}

	@Column(name = "numero_licencia", length = 64)
	public String getNumeroLicencia() {
		return numeroLicencia;
	}


	public void setNumeroLicencia(String numeroLicencia) {
		this.numeroLicencia = numeroLicencia;
	}

	@Column(name = "modelo", length = 64)
	public String getModelo() {
		return modelo;
	}


	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Column(name = "ruc", length = 16)
	public String getRuc() {
		return ruc;
	}


	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	@Column(name = "numero_registro_mtc", length = 20)
	public String getNumeroRegistroMtc() {
		return numeroRegistroMtc;
	}


	public void setNumeroRegistroMtc(String numeroRegistroMtc) {
		this.numeroRegistroMtc = numeroRegistroMtc;
	}


	@Column(name = "remolque", length = 32)
	public String getRemolque() {
		return remolque;
	}


	public void setRemolque(String remolque) {
		this.remolque = remolque;
	}
	
}
