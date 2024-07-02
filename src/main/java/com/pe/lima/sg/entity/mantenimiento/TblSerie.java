package com.pe.lima.sg.entity.mantenimiento;
// Generated 26/11/2017 05:22:50 PM by Hibernate Tools 4.3.5.Final

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
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pe.lima.sg.presentacion.util.Constantes;

/**
 * TblSerie generated by hbm2java
 */
@Entity
@Table(name = "tbl_serie", schema = "mae")
public class TblSerie implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int codigoSerie;
	private String tipoComprobante;
	private String prefijoSerie;
	private Integer secuencialSerie;
	private String numeroComprobante;
	private String estado;
	private Date fechaCreacion;
	private Date fechaModificacion;
	private Integer usuarioCreacion;
	private Integer usuarioModificacion;
	private String ipCreacion;
	private String ipModificacion;
	private String descripcion;
	private TblEmpresa tblEmpresa;
	private String tipoOperacion;
	
	public TblSerie() {
	}

	public TblSerie(int codigoSerie) {
		this.codigoSerie = codigoSerie;
	}

	public TblSerie(int codigoSerie, String tipoComprobante, String prefijoSerie, Integer secuencialSerie, String numeroComprobante,
			String estado, Date fechaCreacion, Date fechaModificacion, Integer usuarioCreacion,
			Integer usuarioModificacion, String ipCreacion, String ipModificacion, String descripcion, TblEmpresa tblEmpresa) {
		this.tipoComprobante = tipoComprobante;
		this.codigoSerie = codigoSerie;
		this.prefijoSerie = prefijoSerie;
		this.secuencialSerie = secuencialSerie;
		this.numeroComprobante = numeroComprobante;
		this.estado = estado;
		this.fechaCreacion = fechaCreacion;
		this.fechaModificacion = fechaModificacion;
		this.usuarioCreacion = usuarioCreacion;
		this.usuarioModificacion = usuarioModificacion;
		this.ipCreacion = ipCreacion;
		this.ipModificacion = ipModificacion;
		this.descripcion = descripcion;
		this.tblEmpresa = tblEmpresa;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "codigo_serie", unique = true, nullable = false)
	public int getCodigoSerie() {
		return this.codigoSerie;
	}

	public void setCodigoSerie(int codigoSerie) {
		this.codigoSerie = codigoSerie;
	}

	@Column(name = "prefijo_serie", length = 4)
	public String getPrefijoSerie() {
		return this.prefijoSerie;
	}

	public void setPrefijoSerie(String prefijoSerie) {
		this.prefijoSerie = prefijoSerie;
	}

	@Column(name = "secuencial_serie")
	public Integer getSecuencialSerie() {
		return this.secuencialSerie;
	}

	public void setSecuencialSerie(Integer secuencialSerie) {
		this.secuencialSerie = secuencialSerie;
	}

	@Column(name = "numero_comprobante", length = 8)
	public String getNumeroComprobante() {
		return this.numeroComprobante;
	}

	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
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

	@Column(name = "tipo_comprobante", length = 2)
	public String getTipoComprobante() {
		return tipoComprobante;
	}

	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}
	
	@Column(name = "descripcion", length = 256)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_entidad", nullable = false)
	@JsonBackReference(value="tablaManage-TablaBack")
	public TblEmpresa getTblEmpresa() {
		return this.tblEmpresa;
	}

	public void setTblEmpresa(TblEmpresa tblEmpresa) {
		this.tblEmpresa = tblEmpresa;
	}

	@Column(name = "tipo_operacion", length = 16)
	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
}
