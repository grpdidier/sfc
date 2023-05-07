package com.pe.lima.sg.entity.operacion;

public class ImpuestoBolsa {
	/*Tributos ICBPER: Impuesto al Consumo de Bolsas Plasticas*/
	String codigoTipo;
	String montoxItem;
	String cantidadBolsasxItem;
	String nombrexItem;
	String codigoTipoxItem;
	String montoxUnidad;
	
	
	public ImpuestoBolsa() {
		this.codigoTipo = "-";						//Sin ICBPER Por defecto guión --> 7152
		this.montoxItem = "0.00";					//TaxAmount
		this.cantidadBolsasxItem = "0";				//BaseUnitMeasure
		this.nombrexItem = "ICBPER";				//ICBPER						
		this.codigoTipoxItem = "OTH";				//OTH
		this.montoxUnidad = "0.20";					//'0.10 año 2019 ;    0.20 año 2020 …
	}
	public ImpuestoBolsa(String tipoTributo) {
		if (tipoTributo.equals("9996")){
			this.codigoTipo = "9996";						//Sin ICBPER Por defecto guión --> 7152
			this.montoxItem = "0.00";					//TaxAmount
			this.cantidadBolsasxItem = "0";				//BaseUnitMeasure
			this.nombrexItem = "GRA";				//ICBPER						
			this.codigoTipoxItem = "FRE";				//OTH
			this.montoxUnidad = "0.20";					//'0.10 año 2019 ;    0.20 año 2020 …
		}else{
			this.codigoTipo = "-";						//Sin ICBPER Por defecto guión --> 7152
			this.montoxItem = "0.00";					//TaxAmount
			this.cantidadBolsasxItem = "0";				//BaseUnitMeasure
			this.nombrexItem = "ICBPER";				//ICBPER						
			this.codigoTipoxItem = "OTH";				//OTH
			this.montoxUnidad = "0.20";					//'0.10 año 2019 ;    0.20 año 2020 …
		}
	}

	
	
	public ImpuestoBolsa(String codigoTipo, String montoxItem, String cantidadBolsasxItem, String nombrexItem,
			String codigoTipoxItem, String montoxUnidad) {
		super();
		this.codigoTipo = codigoTipo;
		this.montoxItem = montoxItem;
		this.cantidadBolsasxItem = cantidadBolsasxItem;
		this.nombrexItem = nombrexItem;
		this.codigoTipoxItem = codigoTipoxItem;
		this.montoxUnidad = montoxUnidad;
	}


	public String getCodigoTipo() {
		return codigoTipo;
	}


	public void setCodigoTipo(String codigoTipo) {
		this.codigoTipo = codigoTipo;
	}


	public String getMontoxItem() {
		return montoxItem;
	}


	public void setMontoxItem(String montoxItem) {
		this.montoxItem = montoxItem;
	}


	public String getCantidadBolsasxItem() {
		return cantidadBolsasxItem;
	}


	public void setCantidadBolsasxItem(String cantidadBolsasxItem) {
		this.cantidadBolsasxItem = cantidadBolsasxItem;
	}


	public String getNombrexItem() {
		return nombrexItem;
	}


	public void setNombrexItem(String nombrexItem) {
		this.nombrexItem = nombrexItem;
	}


	public String getCodigoTipoxItem() {
		return codigoTipoxItem;
	}


	public void setCodigoTipoxItem(String codigoTipoxItem) {
		this.codigoTipoxItem = codigoTipoxItem;
	}


	public String getMontoxUnidad() {
		return montoxUnidad;
	}


	public void setMontoxUnidad(String montoxUnidad) {
		this.montoxUnidad = montoxUnidad;
	}
	
	
	
	
}
