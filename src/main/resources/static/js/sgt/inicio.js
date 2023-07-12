function jsLetra(){
	document.forms[0].action = '/menu';
	document.forms[0].submit();
}
/*
 * Invoca a la opción descrita en el menu
 */
function jsOpcionMenu(opcion){
	//alert(opcion);
	document.forms[0].action = '/' + opcion;
	//document.forms[0].action = opcion;
	document.forms[0].method = 'GET';
	document.forms[0].submit();
	
	
}

/*
 * Invoca a la opción descrita en el menu
 */
function jsOpcionModuloListado(opcion, id){
	//alert(opcion);
	document.forms[0].action = '/' + opcion + '/'+id;
	//document.forms[0].action = opcion;
	document.forms[0].method = 'GET';
	document.forms[0].submit();
}
function jsSeleccion(nodo){
	//alert(nodo);
	if (nodo != undefined){
		$('#jstree').jstree(true).select_node(nodo);
		$('#jstree').jstree('select_node', nodo);
		$.jstree.reference('#jstree').select_node(nodo);
	}else{
		alert('No se selecciono');
	}
}

/* *********************************************************************************************************************************
 * MODULO DE COMPROBANTE
 ********************************************************************************************************************************* */
//Busqueda de Cliente
function jsListarCliente(){
	document.forms[0].action = '/operacion/comprobantes/clientes' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsCargarSerieComprobante(){
	document.forms[0].action = '/operacion/comprobantes/tipoComprobanteSerie' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsCargarNumeroComprobante(){
	document.forms[0].action = '/operacion/comprobantes/numeroComprobanteSerie' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Busqueda de Cliente
function jsListarClienteDocumentoVenta(){
	document.forms[0].action = '/operacion/sfs12/clientes' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Busqueda del Cliente por documento
function jsValidarNumeroDocumento(e){
	var charCode = (typeof e.which === "number") ? e.which : e.keyCode;
	var enterKey = 13;
    if (charCode == enterKey){
    	document.forms[0].action = '/operacion/comprobantes/clienteBusqueda' ;
    	document.forms[0].method = 'POST';
    	document.forms[0].submit();
     }
}
function jsValidarNumeroDocumentoVenta(e){
	var charCode = (typeof e.which === "number") ? e.which : e.keyCode;
	var enterKey = 13;
    if (charCode == enterKey){
    	document.forms[0].action = '/operacion/sfs12/clienteBusqueda' ;
    	document.forms[0].method = 'POST';
    	document.forms[0].submit();
     }
}
//Retorna a la pantalla de Comprobante
function jsRegresarComprobante(){
	document.forms[0].action = '/operacion/comprobantes/regresar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
function jsRegresarDocumentoVenta(){
	document.forms[0].action = '/operacion/sfs12/regresar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
//Carga la descripcion de la leyenda
function jsCargarLeyendaDescripcion(){
	document.forms[0].action = '/operacion/comprobantes/leyenda' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
function jsCargarLeyendaDescripcionSFS12(){
	document.forms[0].action = '/operacion/sfs12/leyenda' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
//Busqueda del Producto por codigo
function jsCargarProducto(e){
	var charCode = (typeof e.which === "number") ? e.which : e.keyCode;
	var enterKey = 13;
    if (charCode == enterKey){
    	document.forms[0].action = '/operacion/comprobantes/productoBusqueda' ;
    	document.forms[0].method = 'POST';
    	document.forms[0].submit();
     }
}

//Busqueda de Producto
function jsListarProducto(){
	document.forms[0].action = '/operacion/comprobantes/productos' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Busqueda de Producto
function jsListarProductoDocumentoVenta(){
	document.forms[0].action = '/operacion/sfs12/productos' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Adicion del detalle
function jsAdicionarDetalle(){
	document.forms[0].action = '/operacion/comprobantes/adicionarDetalle' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Adicion del detalle
function jsAdicionarDetalleDocumentoVenta(){
	document.forms[0].action = '/operacion/sfs12/adicionarDetalle' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Modificación del detalle
function jsModificarDetalleDocumentoVenta(){
	document.forms[0].action = '/operacion/sfs12/modificar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsCalcularMontos(e){
	var charCode = (typeof e.which === "number") ? e.which : e.keyCode;
	var enterKey = 13;
    if (charCode == enterKey){
    	alert('Calculo');
     }
}
function jsBuscarClienteComprobantexNombre(e){
	
	if (e.keyCode == 13) {
		document.forms[0].action = '/operacion/sfs12/clientes/q' ;
		document.forms[0].method = 'POST';
		document.forms[0].submit();
	}
}
//Adicion del detalle de Forma de Pago
function jsAdicionarDetalleFormaPago(){
	document.forms[0].action = '/operacion/sfs12/adicionarDetalleFormaPago' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsMostrarDetalleFormaPago(obj){
	document.forms[0].action = '/operacion/sfs12/mostrarDetalleFormaPago' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
/* *********************************************************************************************************************************
 * MODULO DE NOTA
 ********************************************************************************************************************************* */
//Muestra la pagina de comprobantes para asignarlo a la nota
function jsListarComprobanteParaNota(){
	document.forms[0].action = '/operacion/notas/mostrar/comprobante' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Muestra la pagina de comprobantes para asignarlo a la nota
function jsListarComprobanteParaNotaVenta(){
	document.forms[0].action = '/operacion/sfs12notas/mostrar/comprobante' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Retorna a la pantalla de nota
function jsRegresarNota(){
	document.forms[0].action = '/operacion/notas/regresar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
function jsRegresarNotaVenta(){
	document.forms[0].action = '/operacion/sfs12notas/regresar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
function jsMostrarTipoNota(){
	document.forms[0].action = '/operacion/notas/mostrarNota' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
function jsMostrarTipoNotaVenta(){
	document.forms[0].action = '/operacion/sfs12notas/mostrarNota' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
//Busqueda de Producto
function jsListarProductoNota(){
	document.forms[0].action = '/operacion/notas/productos' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsListarProductoNotaVenta(){
	document.forms[0].action = '/operacion/sfs12notas/productos' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Adicion del detalle
function jsAdicionarDetalleNota(){
	document.forms[0].action = '/operacion/notas/adicionarDetalle' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsAdicionarDetalleNotaVenta(){
	document.forms[0].action = '/operacion/sfs12notas/adicionarDetalle' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsEditarDetalleNota(indice){
	document.getElementById('idIndice').value = indice;
	document.forms[0].action = '/operacion/notas/detalle/editar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
	
}
function jsEditarDetalleNotaVenta(indice){
	document.getElementById('idIndice').value = indice;
	document.forms[0].action = '/operacion/sfs12notas/detalle/editar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
	
}
function jsActualizarEstado(){
	document.forms[0].action = '/operacion/sfs12/q' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
/* *********************************************************************************************************************************
 * MODULO DE CONSULTA
 ********************************************************************************************************************************* */

function jsExportarExcelVentaCliente(){
	//alert(opcion);
	document.forms[0].action = '/operacion/consultas/ventaxclienteXLS2';
	//document.forms[0].target = "_blank";
	document.forms[0].method = 'GET';
	document.forms[0].submit();
}


function jsMostrarEfectivo(){
	document.forms[0].action = '/eds/comprobante/q1' ;
	document.getElementById('idDato').value = 'EFECTIVO';
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}

function jsMostrarCredito(){
	document.forms[0].action = '/eds/comprobante/q1' ;
	document.getElementById('idDato').value = 'CREDITO';
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}

function jsMostrarBoleta(){
	document.forms[0].action = '/eds/comprobante/q2' ;
	document.getElementById('idDato').value = 'BOLETA';
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsMostrarFactura(){
	document.forms[0].action = '/eds/comprobante/q2' ;
	document.getElementById('idDato').value = 'FACTURA';
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsMostrarRegresar(){
	document.forms[0].action = '/eds/nuevo' ;
	document.forms[0].method = 'GET';
	document.forms[0].submit();
}
function jsMostrarRegresarComprobante(){
	document.forms[0].action = '/eds/regresar' ;
	document.forms[0].method = 'GET';
	document.forms[0].submit();
}

function jsMostrarProducto(producto){
	document.forms[0].action = '/eds/comprobante/q3' ;
	document.getElementById('idDato').value = producto;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsMostrarRegresarProducto(){
	document.forms[0].action = '/eds/regresar/producto' ;
	document.forms[0].method = 'GET';
	document.forms[0].submit();
}
function jsAsignarNumero(dato){
	objValor = document.getElementById('idValorProducto');
	if (objValor.innerHTML == '0.00'){
		objValor.innerHTML = '';
	}
	if (dato == '.'){
		if (objValor.innerHTML.indexOf('.')== -1){
			objValor.innerHTML = objValor.innerHTML + dato; 
		}
	}else if(dato == 'B'){
		if (objValor.innerHTML.length > 0){
			objValor.innerHTML = objValor.innerHTML.substring(0, objValor.innerHTML.length -1);
		}
	}else{
		objValor.innerHTML = objValor.innerHTML + dato; 
	}
	
	if (objValor.innerHTML == ''){
		objValor.innerHTML = '0.00';
	}
}
function jsGenerarComprobante(){
	objValor = document.getElementById('idValorProducto');
	if (objValor.innerHTML == '0.00'){
		alert('Debe ingresar el valor de la venta en Soles')
		return;
	}else{
		document.forms[0].action = '/eds/comprobante/q4' ;
		document.getElementById('idDato').value = objValor.innerHTML;
		document.forms[0].method = 'POST';
		document.forms[0].submit();
	}
	
}
function jsMostrarNuevoComprobante(){
	document.forms[0].action = '/eds/nuevo';
	document.forms[0].method = 'GET';
	document.forms[0].submit();
}
function jsObtenerCliente(){
	objValor = document.getElementById('idValorProducto');
	if (objValor.innerHTML == '0.00'){
		alert('Debe ingresar el valor de la venta en Soles')
		return;
	}else{
		document.forms[0].action = '/eds/comprobante/q4/factura' ;
		document.getElementById('idDato').value = objValor.innerHTML;
		document.forms[0].method = 'POST';
		document.forms[0].submit();
	}
}
function jsGenerarFactura(producto){
	document.forms[0].action = '/eds/comprobante/q4/factura/registro' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}

function jsOcultarObject(idOcultar, idMostrar){
	obj = document.getElementById(idOcultar);
	obj.style.display = 'none';
	obj = document.getElementById(idMostrar);
	obj.style.display = '';
}
/* *********************************************************************************************************************************
 * MODULO DE GUIA DE REMISION
 ********************************************************************************************************************************* */
//Busqueda de FACTURAS
function jsListarFacturas(){
	document.forms[0].action = '/operacion/remision/facturas' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsRegresarNuevoRemision(){
	document.forms[0].action = '/operacion/remision/regresar' ;
	document.forms[0].method = 'GET';
	document.forms[0].submit();
}
function jsModificarDetalleProductoRemision(){
	document.forms[0].action = '/operacion/remision/detalle/actualizar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Elimina un detalle
function jsEliminarDetalleGuiaRemision(indice){
	document.getElementById('idIndice').value = indice;
	document.forms[0].action = '/operacion/remision/detalle/eliminar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
function jsActualizarTipoTransporte(){
	document.forms[0].action = '/operacion/remision/tipotransporte/actualizar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsListarTransporte(){
	document.forms[0].action = '/operacion/remision/transporte' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsActualizarMotivoTraslado(){
	if (confirm('Los datos ingresados se limpiaran. Esta seguro de modificar el motivo de traslado?')){
		document.forms[0].action = '/operacion/remision/motivotraslado/actualizar' ;
		document.forms[0].method = 'POST';
		document.forms[0].submit();
	}
}
function jsNuevoProductoRemision(){
	document.forms[0].action = '/operacion/remision/comprobantes/nuevo' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
//Busqueda de Cliente
function jsListarClienteRemision(){
	document.forms[0].action = '/operacion/remision/comprobantes/clientes' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsRegresarRemisionSinComprobante(){
	document.forms[0].action = '/operacion/remision/comprobantes/regresar' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();	
}
//Busqueda de Producto
function jsListarProductoRemision(){
	document.forms[0].action = '/operacion/remision/comprobantes/productos' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
//Adicion del detalle
function jsAdicionarDetalleRemision(){
	document.forms[0].action = '/operacion/remision/comprobantes/adicionarDetalle' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
/* *********************************************************************************************************************************
 * MODULO DE CLIENTE
 ********************************************************************************************************************************* */
function jsCargarProvinciaInei(obj){
	document.forms[0].action = '/persona/provincia' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}
function jsCargarDistritoInei(obj){
	document.forms[0].action = '/persona/distrito' ;
	document.forms[0].method = 'POST';
	document.forms[0].submit();
}

function  jsValidarNumerosYPunto(obj){
	var valor = obj.value;
	// Eliminar cualquier carácter que no sea un número o un punto decimal
	var valorNumerico = valor.replace(/[^0-9.]/g, "");
	// Establecer el valor numérico en el campo de texto
	obj.value = valorNumerico;
}