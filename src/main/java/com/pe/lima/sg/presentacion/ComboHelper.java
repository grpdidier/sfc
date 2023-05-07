package com.pe.lima.sg.presentacion;
/*
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;*/
import org.springframework.stereotype.Controller;

@Controller
public class ComboHelper {

	/*@Autowired
	AgenteDao agenteDao;

	@Autowired
	CicloDao cicloDao;

	@Autowired
	MarcaDao marcaDao;

	@Autowired
	ModeloDao modeloDao;

	@Autowired
	EquipoCertificacionDao equipoCertificacionDao;

	@ResponseBody
	@RequestMapping(value = "selectAgentes/{codigo}", method = RequestMethod.GET)
	public List<Combo> comboAgentes(@PathVariable BigDecimal codigo) {
		List<MaeTblAgente> lista = agenteDao.listarPorTipoAgente(codigo);
		List<Combo> options = new LinkedList<>();
		for (MaeTblAgente agente : lista) {
			options.add(new Combo(agente.getCodigo() + "", agente.getNombreComercial()));
		}

		return options;
	}

	/*@ResponseBody
	@RequestMapping(value = "selectMarcaPorTipoAgente/{codigo}", method = RequestMethod.GET)
	public List<Combo> comboMarcaPorTipo(@PathVariable BigDecimal codigo) {
		List<MaeTblMarca> lista = marcaDao.listarPorTipoEquipo(codigo);
		List<Combo> options = new LinkedList<>();
		for (MaeTblMarca marca : lista) {
			options.add(new Combo(marca.getCodigo() + "", marca.getNombre()));
		}

		return options;
	}

	@ResponseBody
	@RequestMapping(value = "selectModeloPorMarca/{codigo}", method = RequestMethod.GET)
	public List<Combo> comboModeloPorMarca(@PathVariable BigDecimal codigo) {
		List<MaeTblModelo> lista = modeloDao.listarPorMarca(codigo);
		List<Combo> options = new LinkedList<>();
		for (MaeTblModelo modelo : lista) {
			options.add(new Combo(modelo.getCodigo() + "", modelo.getNombre()));
		}

		return options;
	}

	@ResponseBody
	@RequestMapping(value = "selectCertificacionLote/{certificador}/{certificado}", method = RequestMethod.GET)
	public List<Combo> comboCertificacionPorCertificadorCertificadoLote(@PathVariable BigDecimal certificador,
			@PathVariable BigDecimal certificado) {
		List<CnvTblEquipoCertificacion> lista = equipoCertificacionDao.listarCertificacionesLoteImportador(certificador,
				certificado);
		List<Combo> options = new LinkedList<>();
		for (CnvTblEquipoCertificacion certificacion : lista) {
			options.add(new Combo(certificacion.getCodigo() + "", certificacion.getCodigoAutorizacion()
					+ " -  Seriales: " + certificacion.getSerialInicio() + " a " + certificacion.getSerialFin()));
		}

		return options;
	}

	@ResponseBody
	@RequestMapping(value = "selectCertificacionMarca/{certificador}/{certificado}", method = RequestMethod.GET)
	public List<Combo> comboCertificacionPorCertificadorCertificadoMarca(@PathVariable BigDecimal certificador,
			@PathVariable BigDecimal certificado) {
		List<CnvTblEquipoCertificacion> lista = equipoCertificacionDao
				.listarCertificacionesMarcaImportador(certificador, certificado);
		List<Combo> options = new LinkedList<>();
		for (CnvTblEquipoCertificacion certificacion : lista) {
			options.add(new Combo(certificacion.getCodigo() + "", certificacion.getCodigoAutorizacion() + " - Fechas: "
					+ certificacion.getFechaInicio() + " a " + certificacion.getFechaFin()));
		}

		return options;
	}

	@ResponseBody
	@RequestMapping(value = "traerCertificacion/{certificacion}", method = RequestMethod.GET)
	public Combo traerCertificacion(@PathVariable BigDecimal certificacion) {

		CnvTblEquipoCertificacion miCert = equipoCertificacionDao.findOne(certificacion);

		return new Combo(miCert.getCodigoArchivo(), miCert.getCodigoArchivo());
	}

	public class Combo implements Serializable {

		private static final long serialVersionUID = 1L;
		private String value;
		private String label;

		Combo(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}*/
}
