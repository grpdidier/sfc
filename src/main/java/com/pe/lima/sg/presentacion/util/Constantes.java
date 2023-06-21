package com.pe.lima.sg.presentacion.util;

public class Constantes {

	//Version del SFS
	public static String SFS_VERSION						= "1.3.2";
	//Variable para el estado
	public static String ESTADO_ACTIVO 						= "S";
	public static String ESTADO_INACTIVO 					= "N";
	//Variable para el estado de un registro
	public static String ESTADO_REGISTRO_ACTIVO 			= "1";
	public static String ESTADO_REGISTRO_INACTIVO 			= "0";

	//Variable para el estado de un usuario
	public static String ESTADO_USUARIO_ACTIVO 				= "S";
	public static String ESTADO_USUARIO_INACTIVO 			= "N";
	public static String DESC_ESTADO_USUARIO_ACTIVO			= "ACTIVO";
	public static String DESC_ESTADO_USUARIO_INACTIVO		= "INACTIVO";

	//Variables para el tipo de caducidad de la contraseña
	public static String TIPO_CADUCIDAD_INDEFINIDO			= "INDEFINIDO";
	public static String TIPO_CADUCIDAD_CADUCA_30			= "CADUCA 30 DIAS";
	public static Integer NUMERO_DIAS_30					= 30;

	//Variables para las listas de session
	public static String MAP_TIPO_CADUCIDAD					= "mapTipoCaducidad";
	public static String MAP_ESTADO_USUARIO					= "mapEstadoUsuario";
	public static String MAP_TIPO_TIENDA					= "mapTipoTienda";
	public static String MAP_TIPO_PISO						= "mapTipoPiso";
	public static String MAP_TIPO_CONCEPTO					= "mapTipoConcepto";
	public static String MAP_TIPO_PERSONA					= "mapTipoPersona";
	public static String MAP_ESTADO_CIVIL					= "mapEstadoCivil";
	public static String MAP_SI_NO							= "mapSiNo";
	public static String MAP_LISTA_SUMINISTRO				= "mapListaSuministro";
	public static String MAP_LISTA_EDIFICIO					= "mapListaEdificio";
	public static String MAP_TIPO_MONEDA					= "mapTipoMoneda";
	public static String MAP_TIPO_COBRO						= "mapTipoCobro";
	public static String MAP_TIPO_GARANTIA					= "mapTipoGarantia";
	public static String MAP_TIPO_DOCUMENTO					= "mapTipoDocumento";
	public static String MAP_TIPO_RUBRO						= "mapTipoRubro";
	public static String MAP_TIPO_PERIODO_ADELANTO			= "mapTipoPeriodoAdelanto";
	public static String MAP_ESTADO_ASIGNACION				= "mapEstadoAsignacion";
	public static String MAP_MESES							= "mapMeses";
	public static String MAP_TIPO_COBRO_CXC					= "mapTipoCobroCxC";

	//Variables para la operacion
	public static String OPERACION_NUEVO					= "NUEVO";
	public static String OPERACION_EDITAR					= "EDITAR";
	
	//Variable para la validación de parametros
	public static String SEGURIDAD_PARAMETRO_USUARIO		= "RUSIA2018";
	public static String SEGURIDAD_PARAMETRO_CLAVE			= "australia02";

	//Variables para el tipo de tienda
	public static String DESC_TIPO_TIENDA_TIENDA			= "TIENDA";
	public static String DESC_TIPO_TIENDA_ALMACEN			= "ALMACEN";
	public static String DESC_TIPO_TIENDA_BODEGA			= "BODEGA";

	public static String TIPO_TIENDA_TIENDA					= "TI";
	public static String TIPO_TIENDA_ALMACEN				= "AL";
	public static String TIPO_TIENDA_BODEGA					= "BO";

	//Variables para el tipo de concepto
	public static String DESC_TIPO_CONCEPTO_INGRESO			= "INGRESO";
	public static String DESC_TIPO_CONCEPTO_GASTO			= "GASTO";

	public static String TIPO_CONCEPTO_INGRESO				= "IG";
	public static String TIPO_CONCEPTO_GASTO				= "GS";

	//Variables para el cobro
	public static String DESC_TIPO_COBRO_INICIO				= "PRIMER DIA DEL MES";
	public static String DESC_TIPO_COBRO_FIN				= "FIN DE MES";
	public static String DESC_TIPO_COBRO_QUINCENA			= "QUINCENA";
	public static String DESC_TIPO_COBRO_FECHA				= "FECHA ESPECIFICA";

	public static String TIPO_COBRO_INICIO					= "PDM";
	public static String TIPO_COBRO_FIN						= "FDM";
	public static String TIPO_COBRO_QUINCENA				= "QUI";
	public static String TIPO_COBRO_FECHA					= "FES";

	//Variables para el cobro
	public static String DESC_TIPO_GARANTIA_CON				= "CON GARANTIA";
	public static String DESC_TIPO_GARANTIA_SIN				= "SIN GARANTIA";
	public static String DESC_TIPO_GARANTIA_LLAVE			= "LLAVE";
	public static String TIPO_GARANTIA_CON					= "CG";
	public static String TIPO_GARANTIA_SIN					= "SG";
	public static String TIPO_GARANTIA_LLAVE				= "LL";

	//Variables para el cobro
	public static String DESC_TIPO_DOC_FACTURA				= "FACTURA";
	public static String DESC_TIPO_DOC_BOLETA				= "BOLETA";
	public static String DESC_TIPO_DOC_INTERNO				= "INTERNO";
	public static String TIPO_DOC_FACTURA					= "FAC";
	public static String TIPO_DOC_BOLETA					= "BOL";
	public static String TIPO_DOC_INTERNO					= "INT";

	//Variables para el tipo de persona
	public static String DESC_TIPO_PERSONA_NATURAL			= "NATURAL";
	public static String DESC_TIPO_PERSONA_JURIDICA			= "JURIDICA";

	public static String TIPO_PERSONA_NATURAL				= "N";
	public static String TIPO_PERSONA_JURIDICA				= "J";

	//Variables para el estado civil
	public static String DESC_ESTADO_CIVIL_SOLTERO			= "SOLTERO";
	public static String DESC_ESTADO_CIVIL_CASADO			= "CASADO";
	public static String DESC_ESTADO_CIVIL_VIUDO			= "VIUDO";
	public static String DESC_ESTADO_CIVIL_DIVORCIADO		= "DIVORCIADO";

	public static String ESTADO_CIVIL_SOLTERO				= "SO";
	public static String ESTADO_CIVIL_CASADO				= "CA";
	public static String ESTADO_CIVIL_VIUDO					= "VI";
	public static String ESTADO_CIVIL_DIVORCIADO			= "DI";

	//Variable para el tipo SI/NO
	public static String DESC_TIPO_SI						= "SI";
	public static String DESC_TIPO_NO						= "NO";

	public static String TIPO_SI							= "S";
	public static String TIPO_NO							= "N";

	//Variables para el estado de la tienda
	public static String ESTADO_TIENDA_OCUPADO				= "CPD";
	public static String ESTADO_TIENDA_DESOCUPADO			= "DSC";
	public static String DESC_ESTADO_TIENDA_OCUPADO			= "OCUPADO";
	public static String DESC_ESTADO_TIENDA_DESOCUPADO		= "DESCUPADO";

	//Variables para el tipo de moneda
	public static String MONEDA_SOL							= "SO";
	public static String MONEDA_DOLAR						= "DO";
	public static String DESC_MONEDA_SOL					= "SOLES";
	public static String DESC_MONEDA_DOLAR					= "DOLARES";

	//Variables para el tipo de persona
	public static String DESC_TIPO_RUBRO_SERVICIO			= "SERVICIO";
	public static String DESC_TIPO_RUBRO_CONTRATO			= "CONTRATO";

	public static String TIPO_RUBRO_SERVICIO				= "SRV";
	public static String TIPO_RUBRO_CONTRATO				= "CNT";
	
	//Variables para el tipo de cobro
	public static String DESC_TIPO_COBRO_ALQUILER			= "ALQUILER";
	public static String DESC_TIPO_COBRO_SERVICIO			= "SERVICIO";
	public static String DESC_TIPO_COBRO_LUZ				= "LUZ";
	public static String DESC_TIPO_COBRO_ARBITRIO			= "ARBITRIO";
	
	public static String TIPO_COBRO_ALQUILER				= "ALQ";
	public static String TIPO_COBRO_SERVICIO				= "SER";
	public static String TIPO_COBRO_LUZ						= "LUZ";
	public static String TIPO_COBRO_ARBITRIO				= "ARB";


	//Variables del arbol del menu
	public static String MENU_CABECERA_INI					= "<div class=\"innertube\"> <div id=\"jstree_demo_div2\"> <div id=\"jstree2\"> <ul>";
	public static String MENU_CABECERA_INI_PRINCIPAL		= "<div class=\"innertube\"> <div id=\"jstree_demo_div\"> <div id=\"jstree\"> <ul>";
	public static String MENU_CABECERA_FIN					= "</ul> </div> </div> </div>";

	//Variables del listado de Opciones para el perfil
	public static String ROW_OPCION_INI					= "<div class=\"row\">";
	public static String ROW_OPCION_FIN					= "</div>";
	public static String COL_OPCION_INI_01				= "<div class=\"col-md-"; //Falta el tamaño de la columna
	public static String COL_OPCION_INI_02				= "mb-3\">";
	public static String COL_OPCION_FIN					= "</div>";

	//Estados del Contrato
	public static String ESTADO_CONTRATO_PENDIENTE			= "PND";
	public static String ESTADO_CONTRATO_VIGENTE			= "VGN";
	public static String ESTADO_CONTRATO_RENOVADO			= "RNV";
	public static String ESTADO_CONTRATO_FINALIZADO			= "FNL";

	public static String DESC_ESTADO_CONTRATO_PENDIENTE		= "PENDIENTE";
	public static String DESC_ESTADO_CONTRATO_VIGENTE		= "VIGENTE";
	public static String DESC_ESTADO_CONTRATO_RENOVADO		= "RENOVADO";

	//Variables para la operacion
	public static String CADENA_CERO						= "0";
	public static String PREFIJO_CONTRATO					= "CNT";

	//Variables para el trimestre
	public static String TRIMESTRE_PRIMERO					= "TRIMESTRE 1";
	public static String TRIMESTRE_SEGUNDO					= "TRIMESTRE 2";
	public static String TRIMESTRE_TERCERO					= "TRIMESTRE 3";
	public static String TRIMESTRE_CUARTO					= "TRIMESTRE 4";

	//Variables de operacion 
	public static String PAGINADO_ANTERIOR					= "A";
	public static String PAGINADO_SIGUIENTE					= "S";

	//Variables de Parametros
	/*Indica el inmueble que se mostrará por defecto*/
	public static String PAR_INMUEBLE						= "INMUEBLE";
	/*Indica el porcentaje de incremento para el arbitrio*/
	public static String PAR_ARBITRIO_PORCENTAJE_INCREMENTO	= "ARBITRIO_INCREMENTO";
	/*Indica el año de inicio de las listas en el sistema*/
	public static String PAR_ANIO_INICIO					= "ANIO_INICIO";
	/*Indica el periodo de adelanto*/
	public static String PAR_PERIODO_ADELANTO				= "PERIODO_ADELANTO";
	/*Indica el año de inicio de las listas en el sistema*/
	public static String PAR_FIN_CONTRATO					= "FIN_CONTRATO";
	/*Indica la descripcion del cobro del alquiler*/
	public static String PAR_SUNAT_ALQUILER					= "DESCRIPCION_SUNAT_LOCAL_ALQUILER";
	public static String PAR_SUNAT_SERVICIO					= "DESCRIPCION_SUNAT_SERVICIO_ALQUILER";

	//Variables para el periodo de adelanto
	public static String PERIODO_ADELANTO_0					= "0";
	public static String PERIODO_ADELANTO_1					= "1";
	public static String PERIODO_ADELANTO_2					= "2";
	public static String PERIODO_ADELANTO_3					= "3";
	public static String PERIODO_ADELANTO_4					= "4";
	public static String PERIODO_ADELANTO_5					= "5";
	public static String PERIODO_ADELANTO_6					= "6";
	public static String PERIODO_ADELANTO_7					= "7";
	public static String PERIODO_ADELANTO_8					= "8";
	public static String PERIODO_ADELANTO_9					= "9";
	public static String PERIODO_ADELANTO_10				= "10";
	public static String PERIODO_ADELANTO_11				= "11";
	public static String PERIODO_ADELANTO_12				= "12";

	public static String DESC_PERIODO_ADELANTO_0			= "NINGUNO";
	public static String DESC_PERIODO_ADELANTO_1			= "1 MES";
	public static String DESC_PERIODO_ADELANTO_2			= "2 MESES";
	public static String DESC_PERIODO_ADELANTO_3			= "3 MESES";
	public static String DESC_PERIODO_ADELANTO_4			= "4 MESES";
	public static String DESC_PERIODO_ADELANTO_5			= "5 MESES";
	public static String DESC_PERIODO_ADELANTO_6			= "6 MESES";
	public static String DESC_PERIODO_ADELANTO_7			= "7 MESES";
	public static String DESC_PERIODO_ADELANTO_8			= "8 MESES";
	public static String DESC_PERIODO_ADELANTO_9			= "9 MESES";
	public static String DESC_PERIODO_ADELANTO_10			= "10 MESES";
	public static String DESC_PERIODO_ADELANTO_11			= "11 MESES";
	public static String DESC_PERIODO_ADELANTO_12			= "12 MESES";

	//Variables del tipo de servicio
	public static Integer TIPO_SERVICIO_ALQUILER			= 6;

	//Primeros Cobros
	public static String DESC_PRIMEROS_COBROS				= "Registro de primeros cobros";

	//Variable para indentificar la asignacion de arbitrios
	public static String ESTADO_ASIGNADO					= "ASIGNADO";
	public static String ESTADO_PENDIENTE					= "PENDIENTE";
	
	//Variable para los meses
	public static String MES_01								= "01";
	public static String MES_02								= "02";
	public static String MES_03								= "03";
	public static String MES_04								= "04";
	public static String MES_05								= "05";
	public static String MES_06								= "06";
	public static String MES_07								= "07";
	public static String MES_08								= "08";
	public static String MES_09								= "09";
	public static String MES_10								= "10";
	public static String MES_11								= "11";
	public static String MES_12								= "12";
	
	public static String DESC_MES_01						= "ENERO";
	public static String DESC_MES_02						= "FEBRERO";
	public static String DESC_MES_03						= "MARZO";
	public static String DESC_MES_04						= "ABRIL";
	public static String DESC_MES_05						= "MAYO";
	public static String DESC_MES_06						= "JUNIO";
	public static String DESC_MES_07						= "JULIO";
	public static String DESC_MES_08						= "AGOSTO";
	public static String DESC_MES_09						= "SEPTIEMBRE";
	public static String DESC_MES_10						= "OCTUBRE";
	public static String DESC_MES_11						= "NOVIEMBRE";
	public static String DESC_MES_12						= "DICIEMBRE";
	
	//Datos de los documentos
	public static String TIPO_COMPROBANTE_FACTURA			= "01";
	public static String TIPO_COMPROBANTE_BOLETA			= "03";
	public static String TIPO_COMPROBANTE_NOTA_CREDITO		= "07";
	public static String TIPO_COMPROBANTE_NOTA_DEBITO		= "08";
	public static String TIPO_COMPROBANTE_INTERNO			= "99"; //SOLO PARA DOC INTERNOS
	public static String TIPO_COMPROBANTE_GUIA_REMISION		= "09";
	
	//SERIE: Tipo de Operacion
	public static String SERIE_TIPO_OPERACION_MENSUAL			= "MNS";
	public static String SERIE_TIPO_OPERACION_PRIMEROS_COBROS	= "PRM";
	
	//Catalogo 17: Codigo de tipo de Operacion
	public static String SUNAT_TIPO_OPERACION_VENTA_INTERNA					= "01";
	
	public static Integer SUNAT_CODIGO_DOMICILIO_FISCAL						= 0;	
	
	public static String SUNAT_TIPO_DOCUMENTO_REGISTRO_UNICO_CONTRIBUYENTE	= "6";
	public static String SUNAT_TIPO_DOCUMENTO_DOC_NACIONAL_IDENTIDAD		= "1";
	
	public static String SUNAT_TIPO_MONEDA_SOLES							= "PEN";
	public static String SUNAT_TIPO_MONEDA_DOLAR							= "USD";
	
	public static Integer SUNAT_IGV											= 18;
	public static Integer SUNAT_SERVICIO									= 10;
	
	//public static String SUNAT_RUC_EMISOR									= "20386431427";
	public static String SUNAT_RUC_EMISOR									= "NINGUNO";//"20538027295";//"20602620337";//"20534348356";
	
	
	public static String SUNAT_ARCHIVO_EXTENSION_CABECERA					= "CAB";
	public static String SUNAT_ARCHIVO_EXTENSION_DETALLE					= "DET";
	public static String SUNAT_ARCHIVO_EXTENSION_TRIBUTO					= "TRI";
	public static String SUNAT_ARCHIVO_EXTENSION_ADICIONAL_DETALLE			= "ADE";
	public static String SUNAT_ARCHIVO_EXTENSION_LEYENDA					= "LEY";
	public static String SUNAT_ARCHIVO_EXTENSION_CABECERA_NOTA				= "NOT";
	public static String SUNAT_ARCHIVO_EXTENSION_FORMA_PAGO					= "PAG";
	public static String SUNAT_ARCHIVO_EXTENSION_DETALLE_FORMA_PAGO			= "DPA";
	
	public static String SUNAT_UNIDAD_MEDIDA								= "NIU";
	
	public static String SUNAT_CANTIDAD_UNITARIA							= "1";
	
	public static String SUNAT_AFECTACION_IGV_GRAVADO_OPE_ONEROSO			= "10";
	
	public static String SUNAT_PIPE											= "|";
	
	public static Integer SUNAT_LONGITUD_SERIE								= 3;
	public static Integer SUNAT_LONGITUD_NUMERO								= 8;
	
	//public static String SUNAT_FACTURADOR_RUTA								= "G:\\data0\\facturador\\DATA\\";//"D:\\sunat_archivos\\sfs\\DATA\\";//"D:\\data0\\facturador\\DATA\\";
	//public static String SUNAT_FACTURADOR_RUTA_PRUEBA						= "G:\\prueba\\";
	public static String SUNAT_FACTURADOR_RUTA_PRUEBA						= "";//"G:\\data0\\facturador\\DATA\\";//"D:\\sunat_archivos\\sfs\\DATA\\";//"D:\\data0\\facturador\\DATA\\";//"D:\\data01\\";
	public static String SUNAT_FACTURADOR_DB_LOCAL							= "";//"G:\\Jetty\\bd\\BDFacturador.db";//"D:\\SUNAT\\servers\\sfs\\bd\\BDFacturador.db";//"D:\\Jetty\\bd\\BDFacturador.db";//"D:/Bill/BDFacturador.db";
	//borrar para produccion
	//public static String SUNAT_FACTURADOR_DB_LOCAL							= "G:\\Jetty\\bd\\BDFacturador.db";//"D:/Bill/BDFacturador.db";
	
	//Tabla de tipo catalogo y catalogo
	public static String TIPO_CATALAGO_TIPO_OPERACION						= "TIPO_OPERACION";
	public static String TIPO_CATALAGO_DOMICILIO_FISCAL						= "DOMICILIO_FISCAL";
	public static String TIPO_CATALAGO_TIPO_DOCUMENTO						= "TIPO_DOCUMENTO";
	public static String TIPO_CATALAGO_TIPO_MONEDA							= "TIPO_MONEDA";
	public static String TIPO_CATALAGO_TIPO_UNIDAD							= "TIPO_UNIDAD";
	public static String TIPO_CATALAGO_TIPO_AFECTACION						= "TIPO_AFECTACION";
	public static String TIPO_CATALAGO_TIPO_SISTEMA							= "TIPO_SISTEMA";
	public static String TIPO_CATALAGO_TIPO_COMPROBANTE						= "TIPO_COMPROBANTE";
	public static String TIPO_CATALAGO_TIPO_LEYENDA							= "TIPO_LEYENDA";
	public static String TIPO_CATALAGO_NOTA_CREDITO							= "TIPO_NOTA_CREDITO";
	public static String TIPO_CATALAGO_NOTA_DEBITO							= "TIPO_NOTA_DEBITO";
	public static String TIPO_CATALAGO_MOTIVO_TRASLADO						= "TIPO_MOTIVO_TRASLADO";
	
	public static String TIPO_CATALAGO_TIPO_OPERACION_SFS_1_2				= "TIPO_OPERACION_SFS_1_2";
	
	
	public static Integer TIPO_CATALAGO_COD_TIPO_OPERACION					= 1;
	public static Integer TIPO_CATALAGO_COD_DOMICILIO_FISCAL				= 2;
	public static Integer TIPO_CATALAGO_COD_TIPO_DOCUMENTO					= 3;
	public static Integer TIPO_CATALAGO_COD_TIPO_MONEDA						= 4;
	public static Integer TIPO_CATALAGO_COD_TIPO_UNIDAD						= 5;
	public static Integer TIPO_CATALAGO_COD_TIPO_AFECTACION					= 6;
	public static Integer TIPO_CATALAGO_COD_TIPO_SISTEMA					= 7;
	public static Integer TIPO_CATALAGO_COD_TIPO_COMPROBANTE				= 8;
	public static Integer TIPO_CATALAGO_COD_TIPO_LEYENDA					= 9;
	public static Integer TIPO_CATALAGO_COD_MOTIVO_TRASLADO					= 14;
	
	//Parametro de la suna
	public static String  SUNAT_PARAMETRO_SISTEMA							= "PARASIST";
	
	public static String SUNAT_PARAMETRO_NOMBRE_COMERCIAL					= "NOMCOM";
	public static String SUNAT_PARAMETRO_RAZON_SOCIAL						= "RAZON";
	public static String SUNAT_PARAMETRO_DIRECCION_EMISOR					= "DIRECC";
	public static String SUNAT_PARAMETRO_DEPARTAMENTO_EMISOR				= "DEPAR";
	public static String SUNAT_PARAMETRO_PROVINCIA_EMISOR					= "PROVIN";
	public static String SUNAT_PARAMETRO_DISTRITO_EMISOR					= "DISTR";
	public static String SUNAT_PARAMETRO_URBANIZACION_EMISOR				= "URBANIZA";
	public static String SUNAT_PARAMETRO_RUC_EMISOR							= "NUMRUC";
	public static String SUNAT_PARAMETRO_FUNCIONAMIENTO						= "FUNCIO";
	
	
	
	//Comprobantes
	public static String SUNAT_CODIGO_COMPROBANTE_FACTURA					= "01";
	public static String SUNAT_CODIGO_COMPROBANTE_BOLETA					= "03";
	public static String SUNAT_CODIGO_COMPROBANTE_NOTA_CREDITO				= "07";
	public static String SUNAT_CODIGO_COMPROBANTE_NOTA_DEBITO				= "08";
	
	//PARAMETRO
	public static String PARAMETRO_IGV										= "IGV";
	public static String PARAMETRO_SERVICIO									= "SERVICIO";
	public static String PARAMETRO_DETRACCION								= "DETRACCION";
	public static String PARAMETRO_TIPO_SISTEMA_CALCULO_ISC					= "TIPO_ISC";
	
	public static String PARAMETRO_TIPO_OPERACION							= "TIPO_OPERACION";
	public static String PARAMETRO_TIPO_OPERACION_SFS_12					= "TIPO_OPERACION_SFS_12";
	public static String PARAMETRO_CODIGO_DOMICILIO_FISCAL					= "CODIGO_DOMICILIO_FISCAL";
	public static String PARAMETRO_TIPO_COMPROBANTE							= "TIPO_COMPROBANTE";
	public static String PARAMETRO_MONEDA									= "MONEDA";
	public static String PARAMETRO_SERIE									= "SERIE";
	public static String PARAMETRO_UNIDAD_MEDIDA							= "UNIDAD_MEDIDA";
	public static String PARAMETRO_AFECTACION_IGV							= "AFECTACION_IGV";
	public static String PARAMETRO_SUNAT_DATA								= "SUNAT_DATA";
	public static String PARAMETRO_SUNAT_BD									= "SUNAT_BD";
	public static String PARAMETRO_RUC_EMPRESA								= "RUC_EMPRESA";
	public static String PARAMETRO_SERIE_FACTURA							= "SERIE_FACTURA";
	public static String PARAMETRO_SERIE_AUTOMATICA							= "SERIE_AUTOMATICA";
	
	//Leyenda
	public static String SUNAT_LEYENDA_DETRACCION_3001						= "3001";
	public static String SUNAT_LEYENDA_MONTO_LETRAS_1000					= "1000";
	
	//Tipo Operacion
	public static String SUNAT_TIPO_OPERACION_EXPORTACION					= "02";
	
	public static String SUNAT_ESTADO_OPERACION_ACEPTADO					= "03";
	
	//RUC
	public static String SUNAT_PARAMETRO_RUC								= "6";
	
	public static String SUNAT_TIPO_AFECTACION_GRAVADO						= "GRAVADO";
	public static String SUNAT_TIPO_AFECTACION_EXONERADO					= "EXONERADO";
	public static String SUNAT_TIPO_AFECTACION_INAFECTO						= "INAFECTO";
	public static String SUNAT_TIPO_AFECTACION_EXPORTACION					= "EXPORTACION";
	
	public static String SUNAT_VERSION_UBL									= "2.1";
	public static String SUNAT_CUSTOMIZACION								= "2.0";
	
	public static String SUNAT_SIN_CODIGO									= "-";
	
	public static String SUNAT_TRIBUTO_CODIGO								= "1";	
	public static String SUNAT_TRIBUTO_NOMBRE								= "2";	
	public static String SUNAT_TRIBUTO_TIPO									= "3";	
	
	public static String SUNAT_EXONERADO_OPERACION_GRATUITA					= "21";
	public static String SUNAT_INAFECTO_OPERACION_GRATUITA					= "37";
	
	//Variables para la operacion
	public static String FORMA_PAGO_CONTADO									= "Contado";
	public static String FORMA_PAGO_CREDITO									= "Credito";
	
	public static String CODIGO_DOMICILIO_FISCAL							= "03";
	
	public static String ESTADO_XML_GENERADO								= "02";
}
