package com.pe.lima.sg.db.util;



import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import com.pe.lima.sg.bean.facturador.BandejaFacturadorBean;
import com.pe.lima.sg.bean.facturador.BandejaFacturadorNotaBean;
import com.pe.lima.sg.bean.facturador.CatalogoErrorFacturadorBean;
import com.pe.lima.sg.bean.facturador.ParametroFacturadorBean;
import com.pe.lima.sg.presentacion.Filtro;

@NoRepositoryBean
public interface IOperacionFacturador {

	public abstract BandejaFacturadorBean consultarBandejaComprobantes(BandejaFacturadorBean bandeja, Filtro entidad)
		    throws Exception;
	
	public abstract CatalogoErrorFacturadorBean consultarCatalogoError(CatalogoErrorFacturadorBean error, Filtro entidad)
		    throws Exception;
	
	public abstract List<ParametroFacturadorBean> consultarParametro(String idParametro, Filtro entidad)
		    throws Exception;
	
	public abstract BandejaFacturadorNotaBean consultarBandejaNota(BandejaFacturadorNotaBean bandeja, Filtro entidad)
		    throws Exception;
}
