package com.pe.lima.sg.entity.mantenimiento;
// Generated 4/08/2018 05:37:14 PM by Hibernate Tools 5.2.3.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TblEmpresa generated by hbm2java
 */
@Entity
@Table(name = "tbl_empresa", schema = "mae")
public class TblEmpresa implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int codigoEntidad;
	private String nombreComercial;
	private String razonSocial;
	private String ruc;
	private String direccion;
	private String domicilioFiscal;
	private String ubigeoDomFiscal;
	private String departamento;
	private String provincia;
	private String distrito;
	private String estado;
	private Integer usuarioCreacion;
	private Integer usuarioModificacion;
	private Date fechaCreacion;
	private Date fechaModificacion;
	private String ipCreacion;
	private String ipModificacion;

	public TblEmpresa() {
	}

	public TblEmpresa(int codigoEntidad) {
		this.codigoEntidad = codigoEntidad;
	}

	public TblEmpresa(int codigoEntidad, String nombreComercial, String razonSocial, String ruc, String direccion,
			String domicilioFiscal, String ubigeoDomFiscal, String departamento, String provincia, String distrito,
			String estado, Integer usuarioCreacion, Integer usuarioModificacion, Date fechaCreacion,
			Date fechaModificacion, String ipCreacion, String ipModificacion) {
		this.codigoEntidad = codigoEntidad;
		this.nombreComercial = nombreComercial;
		this.razonSocial = razonSocial;
		this.ruc = ruc;
		this.direccion = direccion;
		this.domicilioFiscal = domicilioFiscal;
		this.ubigeoDomFiscal = ubigeoDomFiscal;
		this.departamento = departamento;
		this.provincia = provincia;
		this.distrito = distrito;
		this.estado = estado;
		this.usuarioCreacion = usuarioCreacion;
		this.usuarioModificacion = usuarioModificacion;
		this.fechaCreacion = fechaCreacion;
		this.fechaModificacion = fechaModificacion;
		this.ipCreacion = ipCreacion;
		this.ipModificacion = ipModificacion;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "codigo_entidad", unique = true, nullable = false)
	public int getCodigoEntidad() {
		return this.codigoEntidad;
	}

	public void setCodigoEntidad(int codigoEntidad) {
		this.codigoEntidad = codigoEntidad;
	}

	@Column(name = "nombre_comercial", length = 512)
	public String getNombreComercial() {
		return this.nombreComercial;
	}

	public void setNombreComercial(String nombreComercial) {
		this.nombreComercial = nombreComercial;
	}

	@Column(name = "razon_social", length = 512)
	public String getRazonSocial() {
		return this.razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	@Column(name = "ruc", length = 64)
	public String getRuc() {
		return this.ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	@Column(name = "direccion", length = 512)
	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Column(name = "domicilio_fiscal", length = 512)
	public String getDomicilioFiscal() {
		return this.domicilioFiscal;
	}

	public void setDomicilioFiscal(String domicilioFiscal) {
		this.domicilioFiscal = domicilioFiscal;
	}

	@Column(name = "ubigeo_dom_fiscal", length = 128)
	public String getUbigeoDomFiscal() {
		return this.ubigeoDomFiscal;
	}

	public void setUbigeoDomFiscal(String ubigeoDomFiscal) {
		this.ubigeoDomFiscal = ubigeoDomFiscal;
	}

	@Column(name = "departamento", length = 128)
	public String getDepartamento() {
		return this.departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	@Column(name = "provincia", length = 128)
	public String getProvincia() {
		return this.provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	@Column(name = "distrito", length = 128)
	public String getDistrito() {
		return this.distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
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

}
