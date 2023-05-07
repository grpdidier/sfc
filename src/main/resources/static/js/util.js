// JavaScript Document
function jsEfecto1(NumTD){

	idTD = document.getElementById("td" + NumTD);
	idTD.className = "col-md-1 mb-3 tdUp";
	idTD.style.cursor='pointer';
}

function jsEfecto2(NumTD){

	idTD = document.getElementById("td" + NumTD);
	idTD.className = "col-md-1 mb-3 tdDown";
	idTD.style.cursor='';
}
function jsOver(obj){
	obj.className = 'xFilaTabla';
}
function jsOut(obj){
	obj.className = obj.className.replace('xFilaTabla', 'textNormalBorder'); 
}
function jsOver_Hand(obj){
	obj.className = 'xFilaTabla';
	obj.style.cursor='hand';
}
function jsOut_Hand(obj){
	obj.className = obj.className.replace('xFilaTabla', '');
	obj.style.cursor='';
}
function jsOverFecha(obj){
	obj.className = 'campoEnlaceOver';
}
function jsOutFecha(obj){
	obj.className = obj.className.replace('campoEnlaceOver', 'campoEnlace');
}
function jsOverFechaLeft(obj){
	obj.className = 'campoEnlaceOverLeft';
}
function jsOutFechaLeft(obj){
	obj.className = obj.className.replace('campoEnlaceOverLeft', 'campoEnlaceLeft');
}

//datos generales - DOCUMENTOS
function jsMostrarHistorialDocumento(numero){
	idTD = document.getElementById("idHistorial-" + numero);
	idTD.style.display = '';
}
function jsOcultarHistorialDocumento(numero){
	idTD = document.getElementById("idHistorial-" + numero);
	idTD.style.display = 'none';
}
function jsMostrarEditarDocumento(numero){
	idTD = document.getElementById("idEditar-" + numero);
	idTD.style.display = '';
}
function jsOcultarEditarDocumento(numero){
	idTD = document.getElementById("idEditar-" + numero);
	idTD.style.display = 'none';
}
function jsMostrarHistorialTecnico(numero){
	idTD = document.getElementById("idHistorialTecnico-" + numero);
	idTD.style.display = '';
}
function jsOcultarHistorialTecnico(numero){
	idTD = document.getElementById("idHistorialTecnico-" + numero);
	idTD.style.display = 'none';
}
//datos generales - ACUERDOS EQUIPOS
function jsMostrarHistorialEquipo(){
	idTD = document.getElementById("idHistorialAcuerdo");
	idTD.style.display = '';
}
function jsOcultarHistorialEquipo(){
	idTD = document.getElementById("idHistorialAcuerdo");
	idTD.style.display = 'none';
}
//datos generales - ACUERDOS LABORATORIO
function jsMostrarHistorialLaboratorio(){
	idTD = document.getElementById("idHistorialLaboratorio");
	idTD.style.display = '';
}
function jsOcultarHistorialLaboratorio(){
	idTD = document.getElementById("idHistorialLaboratorio");
	idTD.style.display = 'none';
}
//administracion de equipos importador
function jsTipoCertificacion( tipo){
	if (tipo=='1'){
		idTD = document.getElementById("idCertificacion");
		idTD.style.display = 'none';
		idTD = document.getElementById("idCertificacionLote");
		idTD.style.display = '';
	}else{
		idTD = document.getElementById("idCertificacion");
		idTD.style.display = '';
		idTD = document.getElementById("idCertificacionLote");
		idTD.style.display = 'none';
	}
	
}

//registro e tecnico talle
function jsValidarCedula(){
	idValidacion = document.getElementById("idValidacionTecnico");
	idValidacion.style.display = 'none';
	idValidacion = document.getElementById("idDatosTecnico");
	idValidacion.style.display = '';
}

//Importador Equipos: Filtro de busqueda
function jsImportadorEquipoFiltro(selectObj){
	var idx = selectObj.selectedIndex; 
	var which = selectObj.options[idx].value; 
	//alert(which);
	if (which=='Cilindro'){
		idTD = document.getElementById("idFiltroImportador");
		idTD.style.display = '';
	}else{
		idTD = document.getElementById("idFiltroImportador");
		idTD.style.display = 'none';
	}
	
}
//Importador de Equipos: Nuevo
function jsTipoCertificacionEquipo(selectObj){
	var idx = selectObj.selectedIndex; 
	var which = selectObj.options[idx].value; 
	//alert(which);
	if (which=='LOTE'){
		idTD = document.getElementById("idCertificacion");
		idTD.style.display = 'none';
		idTD = document.getElementById("idCertificacionLote");
		idTD.style.display = '';
	}else{
		idTD = document.getElementById("idCertificacion");
		idTD.style.display = '';
		idTD = document.getElementById("idCertificacionLote");
		idTD.style.display = 'none';
	}
	
}
//Importador - Ver Historial - Regresa
function goBack() {
    window.history.back();
}
//Importador - Ver Historial - Ver Datos
function jsMostrarHistorialEquipo(numero){
	idTD = document.getElementById("idHistorial"+numero);
	idTD.style.display = '';
}
function jsOcultarHistorialEquipo(numero){
	idTD = document.getElementById("idHistorial"+numero);
	idTD.style.display = 'none';
}
//Importador - P.H. - Ver Datos
function jsMostrarSolicitudPH(numero){
	idTD = document.getElementById("idPH"+numero);
	idTD.style.display = '';
}
function jsOcultarSolicitudPH(numero){
	idTD = document.getElementById("idPH"+numero);
	idTD.style.display = 'none';
}





