package com.pe.lima.sg.entity.operacion;
// default package
// Generated 17/06/2023 05:18:59 PM by Hibernate Tools 5.2.3.Final

import java.math.BigDecimal;
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
 * TblDetalleRemision generated by hbm2java
 */
@Entity
@Table(name = "tbl_detalle_remision", schema = "ope")
public class TblDetalleRemision implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int codigoDetalleRemision;
	private TblFacturaAsociada tblFacturaAsociada;
	private String descripcion;
	private BigDecimal cantidad;
	private String unidadMedida;
	private String estado;
	private Date fechaCreacion;
	private Date fechaModificacion;
	private Integer usuarioCreacion;
	private Integer usuarioModificacion;
	private String ipCreacion;
	private String ipModificacion;
	private String codigoProducto;
	private Integer codigoDetalleComprobante;
	private BigDecimal peso;
	private BigDecimal cantidadFacturada;
	private BigDecimal precioReporte;
	
	public TblDetalleRemision() {
	}

	public TblDetalleRemision(int codigoDetalleRemision, TblFacturaAsociada tblFacturaAsociada) {
		this.codigoDetalleRemision = codigoDetalleRemision;
		this.tblFacturaAsociada = tblFacturaAsociada;
	}

	public TblDetalleRemision(int codigoDetalleRemision, TblFacturaAsociada tblFacturaAsociada, String descripcion,
			BigDecimal cantidad, String unidadMedida, String estado, Date fechaCreacion, Date fechaModificacion,
			Integer usuarioCreacion, Integer usuarioModificacion, String ipCreacion, String ipModificacion,
			String codigoProducto, Integer codigoDetalleComprobante) {
		this.codigoDetalleRemision = codigoDetalleRemision;
		this.tblFacturaAsociada = tblFacturaAsociada;
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.unidadMedida = unidadMedida;
		this.estado = estado;
		this.fechaCreacion = fechaCreacion;
		this.fechaModificacion = fechaModificacion;
		this.usuarioCreacion = usuarioCreacion;
		this.usuarioModificacion = usuarioModificacion;
		this.ipCreacion = ipCreacion;
		this.ipModificacion = ipModificacion;
		this.codigoProducto = codigoProducto;
		this.codigoDetalleComprobante = codigoDetalleComprobante;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "codigo_detalle_remision", unique = true, nullable = false)
	public int getCodigoDetalleRemision() {
		return this.codigoDetalleRemision;
	}

	public void setCodigoDetalleRemision(int codigoDetalleRemision) {
		this.codigoDetalleRemision = codigoDetalleRemision;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_factura_asociada", nullable = false)
	@JsonBackReference(value="tablaManage-TablaBack")
	public TblFacturaAsociada getTblFacturaAsociada() {
		return this.tblFacturaAsociada;
	}

	public void setTblFacturaAsociada(TblFacturaAsociada tblFacturaAsociada) {
		this.tblFacturaAsociada = tblFacturaAsociada;
	}

	@Column(name = "descripcion", length = 500)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "cantidad", precision = 23, scale = 10)
	public BigDecimal getCantidad() {
		return this.cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	@Column(name = "unidad_medida", length = 8)
	public String getUnidadMedida() {
		return this.unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
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

	@Column(name = "codigo_producto", length = 30)
	public String getCodigoProducto() {
		return this.codigoProducto;
	}

	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}

	@Column(name = "codigo_detalle_comprobante")
	public Integer getCodigoDetalleComprobante() {
		return this.codigoDetalleComprobante;
	}

	public void setCodigoDetalleComprobante(Integer codigoDetalleComprobante) {
		this.codigoDetalleComprobante = codigoDetalleComprobante;
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

	@Column(name = "peso", precision = 12, scale = 2)
	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}
	@Column(name = "cantidad_facturada", precision = 23, scale = 10)
	public BigDecimal getCantidadFacturada() {
		return cantidadFacturada;
	}

	public void setCantidadFacturada(BigDecimal cantidadFacturada) {
		this.cantidadFacturada = cantidadFacturada;
	}

	@Column(name = "precio_reporte", precision = 12, scale = 2)
	public BigDecimal getPrecioReporte() {
		return precioReporte;
	}

	public void setPrecioReporte(BigDecimal precioReporte) {
		this.precioReporte = precioReporte;
	}
}
