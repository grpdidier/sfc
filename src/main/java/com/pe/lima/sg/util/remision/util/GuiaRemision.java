package com.pe.lima.sg.util.remision.util;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.state.ESuccess;
import com.helger.xml.XMLFactory;
import com.helger.xsds.xmldsig.CanonicalizationMethodType;
import com.helger.xsds.xmldsig.DigestMethodType;
import com.helger.xsds.xmldsig.KeyInfoType;
import com.helger.xsds.xmldsig.ReferenceType;
import com.helger.xsds.xmldsig.SignatureMethodType;
import com.helger.xsds.xmldsig.SignatureType;
import com.helger.xsds.xmldsig.SignatureValueType;
import com.helger.xsds.xmldsig.SignedInfoType;
import com.helger.xsds.xmldsig.TransformType;
import com.helger.xsds.xmldsig.TransformsType;
import com.helger.xsds.xmldsig.X509DataType;
import com.pe.lima.sg.bean.remision.RemisionBean;
import com.pe.lima.sg.entity.operacion.TblComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleComprobante;
import com.pe.lima.sg.entity.operacion.TblDetalleRemision;
import com.pe.lima.sg.util.remision.ubl21.UBL21Marshaller;

import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AttachmentType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DespatchLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DespatchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ExternalReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderLineReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ShipmentStageType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ShipmentType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CustomerAssignedAccountIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DeliveredQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DespatchAdviceTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DocumentTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DocumentTypeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.GrossWeightMeasureType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.HandlingCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.InformationType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.LineIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.LineType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.RegistrationNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.StartDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TransportModeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_21.ExtensionContentType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_21.UBLExtensionType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_21.UBLExtensionsType;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_21.DespatchAdviceType;;
@Slf4j
@Service
public class GuiaRemision {
	/*public static void main(String[] args) {
		//generarXML();
		try {
			generarGuiaRemisionXML();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/

	public static void generarGuiaRemisionXML(RemisionBean remision) throws Exception {
		log.info("[generarGuiaRemisionXML] Inicio");
		final DespatchAdviceType aDespatchAdvice = new DespatchAdviceType();
		
		datosGuiaRemision(aDespatchAdvice, remision);
		//String fileNamePath = "target/20602620337-09-TTT1-2.xml";
		String fileNamePrevio = obtenerNombreArchivoGuiaPrevio(remision);
		String fileName = obtenerNombreArchivoGuia(remision);
		final ESuccess eSuccessDespatch = UBL21Marshaller.despatchAdvice().write (aDespatchAdvice,new File (fileNamePrevio));
		if (eSuccessDespatch.isSuccess()) {
			log.info("[generarGuiaRemisionXML] Archivo creado:"+fileNamePrevio);
			//CreateSignature.Firmar("target/20602620337-09-TTT1-2.xml", "target/20602620337-09-TTT1-2_firmado.xml", new File("target/llamaKeystore.jks"));
			CreateSignature.firmar(fileNamePrevio, fileName, new File("target/llamaKeystore.jks"));
			remision.setNombreArchivoXML(fileName);
		}
		log.info("[generarGuiaRemisionXML] Fin");
	}
	private static String obtenerNombreArchivoGuia(RemisionBean remision) {
		String fileName = "target/"+ remision.getRuc().concat("-09-").concat(remision.getRemision().getSerie()).concat("-").concat(remision.getRemision().getNumero()).concat(".xml");
		return fileName;
	}
	private static String obtenerNombreArchivoGuiaPrevio(RemisionBean remision) {
		String fileName = "target/"+ remision.getRuc().concat("-09-").concat(remision.getRemision().getSerie()).concat("-").concat(remision.getRemision().getNumero()).concat("-SinFirma.xml");
		return fileName;
	}
	/*private byte[] firmarArchivo(String fileName) throws Exception {
		PrivateKey privateKey = XmlHelper.getPrivateKey(appConfig.getPathFiles() + "/resources/cert/CERT-DEV.pem"); //privateKey.pem
		X509Certificate cert =  XmlHelper.getX509Certificate(appConfig.getPathFiles() + "/resources/cert/CERT-DEV.crt"); //ende.crt

		return XmlHelper.firmarDsig(fileName, privateKey, cert);
	}*/
	
	private static void datosGuiaRemision(DespatchAdviceType aDespatchAdvice, RemisionBean remision) throws Exception {
		log.info("[datosGuiaRemision] Inicio");
		asignarDatosBaseGuiaRemision(aDespatchAdvice, remision);
		/*DATOS DEL REMITENTE*/
		asignarDatosRemitente(aDespatchAdvice, remision);
		/*DATOS DEL DOCUMENTO RELACIONADO*/
		asignarDocumentoRelacionado(aDespatchAdvice, remision);
		/*DATOS DEL DESTINATARIO*/
		/*M.16.Numero de documento de identidad del destinatario*/
		asignarDatosDestinatario(aDespatchAdvice, remision);
		/*DATOS DE ENVIO*/
		asignarDatosEnvio(aDespatchAdvice, remision);
		/*BIENES A TRANSPORTAR*/
		asignarDatosDelBien(aDespatchAdvice, remision);
		log.info("[datosGuiaRemision] Fin");
	}
	private static void asignarDocumentoRelacionado(DespatchAdviceType aDespatchAdvice, RemisionBean remision) {
		
		List<DocumentReferenceType> aListDocument = new ArrayList<>();
		aDespatchAdvice.setAdditionalDocumentReference(aListDocument);
		
		for(TblComprobante comprobante: remision.getListaTblComprobante()) {
			DocumentReferenceType documentReferenceType = new DocumentReferenceType();
			DocumentTypeType documentType = new DocumentTypeType();
			documentReferenceType.setDocumentType(documentType);
			documentType.setValue("FACTURA");
			DocumentTypeCodeType documentTypeCodeType = new DocumentTypeCodeType();
			documentReferenceType.setDocumentTypeCode(documentTypeCodeType);
			documentTypeCodeType.setValue("01"); //Factura
			documentTypeCodeType.setListAgencyName("PE:SUNAT");
			documentTypeCodeType.setListName("Documento relacionado al transporte");
			documentTypeCodeType.setListURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo61");
			IDType idType = new IDType();
			documentReferenceType.setID(idType);
			idType.setValue(comprobante.getSerie()+"-"+comprobante.getNumero());
			PartyType partyType = new PartyType();
			documentReferenceType.setIssuerParty(partyType);
			PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
			partyType.addPartyIdentification(partyIdentificationType);
			//partyIdentificationType.setID(remision.getRuc());
			IDType idTypeRuc = new IDType();
			partyIdentificationType.setID(idTypeRuc);
			//idTypeRuc.setSchemeAgencyID("6");
			idTypeRuc.setSchemeID("6");
			idTypeRuc.setSchemeName("Documento de Identidad");
			idTypeRuc.setSchemeAgencyName("PE:SUNAT");
			idTypeRuc.setSchemeURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
			idTypeRuc.setValue(remision.getRuc());
			aListDocument.add(documentReferenceType);
		}
	}
	private static void asignarDatosDelBien(DespatchAdviceType aDespatchAdvice, RemisionBean remision) {
		Integer contador = 0;
		
		final List<DespatchLineType> aListDespatchLineType = new ArrayList<>();
		for(TblDetalleRemision detalle:remision.getListaDetalleRemision()) {
			contador++;
			final DespatchLineType despatchLineType = obtenerDatosDelBien(detalle,contador);
			aListDespatchLineType.add(despatchLineType);
		}
		
		aDespatchAdvice.setDespatchLine(aListDespatchLineType);
		
	}
	private static DespatchLineType obtenerDatosDelBien(TblDetalleRemision detalle, Integer contador) {
		/*C.Número de orden del item*/
		final DespatchLineType despatchLineType = new DespatchLineType();
		final IDType iDType = new IDType();
		//iDType.setValue("1");
		iDType.setValue(contador.toString());
		despatchLineType.setID(iDType);
		/*C.Cantidad del bien*/
		final DeliveredQuantityType deliveredQuantityType = new DeliveredQuantityType();
		deliveredQuantityType.setUnitCode("NIU");
		deliveredQuantityType.setUnitCodeListID("UN/ECE rec 20");
		deliveredQuantityType.setUnitCodeListAgencyName("United Nations Economic Commission for Europe");
		//deliveredQuantityType.setValue(new BigDecimal("1.0"));
		deliveredQuantityType.setValue(detalle.getCantidad());
		despatchLineType.setDeliveredQuantity(deliveredQuantityType);
		/*Referencia sin validacion*/
		final OrderLineReferenceType orderLineReferenceType = new OrderLineReferenceType();
		final LineIDType lineIDType = new LineIDType();
		//lineIDType.setValue("1");
		lineIDType.setValue(contador.toString());
		orderLineReferenceType.setLineID(lineIDType);
		final List<OrderLineReferenceType> aListOrderLineReferenceType = new ArrayList<>();
		aListOrderLineReferenceType.add(orderLineReferenceType);
		despatchLineType.setOrderLineReference(aListOrderLineReferenceType);
		/*C.Descripción detallada del bien*/
		final ItemType itemType = new ItemType();
		final DescriptionType descriptionType = new DescriptionType();
		//descriptionType.setValue("DETALLE DEL PRODUCTO 1");
		descriptionType.setValue(detalle.getDescripcion());
		final List<DescriptionType> aListDescriptionType = new ArrayList<>();
		aListDescriptionType.add(descriptionType);
		itemType.setDescription(aListDescriptionType);
		despatchLineType.setItem(itemType);
		/*C.Código del bien*/
		final ItemIdentificationType itemIdentificationType = new ItemIdentificationType();
		final IDType iDTypeItem = new IDType();
		//iDTypeItem.setValue("001");
		iDTypeItem.setValue(detalle.getCodigoProducto());
		itemIdentificationType.setID(iDTypeItem);
		itemType.setSellersItemIdentification(itemIdentificationType);
		return despatchLineType;
	}
	//request.getSession().setAttribute("SessionMapMotivoTraslado"
	private static void asignarDatosEnvio(DespatchAdviceType aDespatchAdvice, RemisionBean remision) {
		/*M.Identificador del traslado*/
		final ShipmentType shipmentType = new ShipmentType();
		aDespatchAdvice.setShipment(shipmentType);
		shipmentType.setID("SUNAT_Envio");
		/*M.Motivo del traslado*/
		final HandlingCodeType handlingCodeType = new HandlingCodeType();
		shipmentType.setHandlingCode(handlingCodeType);
		handlingCodeType.setListAgencyName("PE:SUNAT");
		handlingCodeType.setListName("Motivo de traslado");
		handlingCodeType.setListURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo20");
		//handlingCodeType.setValue("01");
		handlingCodeType.setValue(remision.getRemision().getMotivoTraslado());
		/*C.Descripción de motivo de traslado*/
		final InformationType informationType = new InformationType();
		//informationType.setValue("VENTA");
		/*Solo aplica si el motivo es 08 o 09*/
		/*informationType.setValue(remision.getDescripcionMotivo());
		final List<InformationType> aListInformation = new ArrayList<>();
		aListInformation.add(informationType);
		shipmentType.setInformation(aListInformation);*/
		/*M.Peso bruto total de la carga*/
		final GrossWeightMeasureType grossWeightMeasureType = new GrossWeightMeasureType();
		shipmentType.setGrossWeightMeasure(grossWeightMeasureType);
		grossWeightMeasureType.setUnitCode("KGM");
		//grossWeightMeasureType.setValue(new BigDecimal("1.0"));
		grossWeightMeasureType.setValue(remision.getPesoBruto());
		/*M.Modalidad de traslado*/
		final ShipmentStageType shipmentStageType = new ShipmentStageType();
		final TransportModeCodeType transportModeCodeType = new TransportModeCodeType();
		transportModeCodeType.setListName("Modalidad de traslado");
		transportModeCodeType.setListAgencyName("PE:SUNAT");
		transportModeCodeType.setListURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo18");
		transportModeCodeType.setValue("01");
		shipmentStageType.setTransportModeCode(transportModeCodeType);
		final List<ShipmentStageType> aListShipmentStageType = new ArrayList<>();
		aListShipmentStageType.add(shipmentStageType);
		shipmentType.setShipmentStage(aListShipmentStageType);
		/*C.Fecha Inicio de traslado*/
		final PeriodType periodType = new PeriodType();
		final StartDateType startDateType = new StartDateType();
		//startDateType.setValue(PDTFactory.getCurrentLocalDate());
		startDateType.setValue(PDTFactory.getCurrentLocalDate());
		periodType.setStartDate(convertirFechaAStartDate(remision.getRemision().getFechaInicioTraslado()));
		shipmentStageType.setTransitPeriod(periodType);
		/*Datos del transportista*/
		/*C.Número de RUC transportista*/
		final PartyType partyType = new PartyType();
		final PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
		final IDType aIDType = new IDType();
		aIDType.setSchemeID("6");
		//aIDType.setValue("20000000001");
		aIDType.setValue(remision.getRemision().getNumeroDocumentoCliente());
		partyIdentificationType.setID(aIDType);
		final List<PartyIdentificationType> aListPartyIdentificationType = new ArrayList<>();
		aListPartyIdentificationType.add(partyIdentificationType);
		partyType.setPartyIdentification(aListPartyIdentificationType);
		/*C.Apellidos y Nombres o denominación o razón social del transportista*/
		final PartyLegalEntityType partyLegalEntityType = new PartyLegalEntityType();
		final RegistrationNameType registrationNameType = new RegistrationNameType();
		//registrationNameType.setValue("EMPRESA DE TRANSPORTE PRUEBA SA");
		registrationNameType.setValue(remision.getRemision().getNombreCliente());
		partyLegalEntityType.setRegistrationName(registrationNameType);
		final List<PartyLegalEntityType> aListPartyLegalEntityType = new ArrayList<>();
		aListPartyLegalEntityType.add(partyLegalEntityType);
		partyType.setPartyLegalEntity(aListPartyLegalEntityType);
		final List<PartyType> aListPartyType = new ArrayList<>();
		aListPartyType.add(partyType);
		shipmentStageType.setCarrierParty(aListPartyType);
		/*Dirección del punto de partida*/
		/*M.Ubigeo de partida*/
		final DeliveryType deliveryType = new DeliveryType();
		final DespatchType despatchType = new DespatchType();
		final AddressType addressTypeDespatch = obtenerDireccionPartida(remision);
		despatchType.setDespatchAddress(addressTypeDespatch);
		deliveryType.setDespatch(despatchType);
		/*Dirección del punto de llegada*/
		final AddressType addressTypeDelivery = obtenerDireccionLlegada(remision);
		deliveryType.setDeliveryAddress(addressTypeDelivery);
		shipmentType.setDelivery(deliveryType);

	}
	private static StartDateType convertirFechaAStartDate(String fechaEmision) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaLocalDate = LocalDate.parse(fechaEmision, formatter);
		return new StartDateType(fechaLocalDate);
	}
	private static AddressType obtenerDireccionPartida(RemisionBean remision) {
		final AddressType addressType = new AddressType();
		final IDType iDType = new IDType();
		iDType.setSchemeAgencyName("PE:INEI");
		iDType.setSchemeName("Ubigeos");
		iDType.setValue("151021");
		addressType.setID(iDType);
		/*M.Dirección completa y detallada de partida*/
		final AddressLineType addressLineType = new AddressLineType();
		final LineType lineType = new LineType();
		//lineType.setValue("DIRECCION PARTIDA");
		lineType.setValue(remision.getDireccionPartida());
		log.info("[obtenerDireccionPartida] direccion:"+remision.getDireccionPartida());
		addressLineType.setLine(lineType);
		final List<AddressLineType> aListAddressLineType = new ArrayList<>();
		aListAddressLineType.add(addressLineType);
		addressType.setAddressLine(aListAddressLineType);
		return addressType;
	}
	private static AddressType obtenerDireccionLlegada(RemisionBean remision) {
		final AddressType addressType = new AddressType();
		final IDType iDType = new IDType();
		iDType.setSchemeAgencyName("PE:INEI");
		iDType.setSchemeName("Ubigeos");
		iDType.setValue("211101");
		addressType.setID(iDType);
		/*M.Dirección completa y detallada de partida*/
		final AddressLineType addressLineType = new AddressLineType();
		final LineType lineType = new LineType();
		//lineType.setValue("DIRECCION LLEGADA");
		lineType.setValue(remision.getRemision().getDomicilioLlegada());
		addressLineType.setLine(lineType);
		final List<AddressLineType> aListAddressLineType = new ArrayList<>();
		aListAddressLineType.add(addressLineType);
		addressType.setAddressLine(aListAddressLineType);
		return addressType;
	}

	private static void asignarDatosBaseGuiaRemision(DespatchAdviceType aDespatchAdvice, RemisionBean remision) {
		
		
		/*M.1.Versión del UB*/
		aDespatchAdvice.setUBLVersionID("2.1");
		/*M.2.Versión de la estructura del documento*/
		aDespatchAdvice.setCustomizationID("2.0");
		/*M.3.Numeracion, conformada por serie y numero correlativo*/
		//aDespatchAdvice.setID("TTT1-2");
		aDespatchAdvice.setID(remision.getRemision().getSerie()+"-"+remision.getRemision().getNumero());
		/*M.4.Fecha de emisión*/
		//aDespatchAdvice.setIssueDate(PDTFactory.getCurrentLocalDate());
		aDespatchAdvice.setIssueDate(convertirFechaALocalDate(remision.getRemision().getFechaEmision()));
		/*M.4.Hora de emision*/
		//aDespatchAdvice.setIssueTime(PDTFactory.getCurrentLocalTime());
		aDespatchAdvice.setIssueTime(convertirHoraALocalTime());
		
		/*M.5.: Tipo de documento (Guia)*/
		final DespatchAdviceTypeCodeType despatchAdviceTypeCodeType = new DespatchAdviceTypeCodeType();
		despatchAdviceTypeCodeType.setListAgencyName("PE:SUNAT");
		despatchAdviceTypeCodeType.setListName("Tipo de Documento");
		despatchAdviceTypeCodeType.setListURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
		despatchAdviceTypeCodeType.setValue("09");
		aDespatchAdvice.setDespatchAdviceTypeCode(despatchAdviceTypeCodeType);

	}

	private static IssueTimeType convertirHoraALocalTime() {
		Date fecha = new Date();
		LocalTime hora = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
		return new IssueTimeType(hora);

	}
	private static IssueDateType convertirFechaALocalDate(String fechaEmision) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaLocalDate = LocalDate.parse(fechaEmision, formatter);
		return new IssueDateType(fechaLocalDate);
	}
	private static void asignarDatosRemitente(DespatchAdviceType aDespatchAdvice, RemisionBean remision) {
		/*M.13.Numero de documento de identidad del remitente */
		final SupplierPartyType supplierPartyType = new SupplierPartyType();
		aDespatchAdvice.setDespatchSupplierParty(supplierPartyType);
		final CustomerAssignedAccountIDType customerAssignedAccountIDType = new CustomerAssignedAccountIDType();
		supplierPartyType.setCustomerAssignedAccountID(customerAssignedAccountIDType);
		//customerAssignedAccountIDType.setValue("20602620337");
		customerAssignedAccountIDType.setValue(remision.getRuc());
		/*M.14.Tipo de documento de identidad del remitente */
		customerAssignedAccountIDType.setSchemeID("6");
		final PartyType partyTypeIdentification = new PartyType();
		supplierPartyType.setParty(partyTypeIdentification);
		List<PartyIdentificationType> listPartyIdentification2 = new ArrayList<>();
		partyTypeIdentification.setPartyIdentification(listPartyIdentification2);
		final PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
		listPartyIdentification2.add(partyIdentificationType);
		final IDType iDType = new IDType();
		partyIdentificationType.setID(iDType);
		iDType.setSchemeID("6");
		iDType.setSchemeName("Documento de Identidad");
		iDType.setSchemeAgencyName("PE:SUNAT");
		iDType.setSchemeURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
		iDType.setValue("20602620337");
		/*M.15.Apellidos y nombres, denominación o razón social del remitente*/
		final List<PartyLegalEntityType> listLegalEntity = new ArrayList<>();
		partyTypeIdentification.setPartyLegalEntity(listLegalEntity);
		final PartyLegalEntityType partyLegalEntityType = new PartyLegalEntityType();
		//partyLegalEntityType.setRegistrationName("Dysalim S.A.");
		partyLegalEntityType.setRegistrationName(remision.getRazonSocial());
		listLegalEntity.add(partyLegalEntityType);
		
	}
	
	private static void asignarDatosDestinatario(DespatchAdviceType aDespatchAdvice, RemisionBean remision) {
		/*M.16.Numero de documento de identidad del destinatario */
		final CustomerPartyType customerPartyType = new CustomerPartyType();
		aDespatchAdvice.setDeliveryCustomerParty(customerPartyType);
		final CustomerAssignedAccountIDType customerAssignedAccountIDType = new CustomerAssignedAccountIDType();
		customerPartyType.setCustomerAssignedAccountID(customerAssignedAccountIDType);
		//customerAssignedAccountIDType.setValue("20155945860");
		customerAssignedAccountIDType.setValue(remision.getRemision().getNumeroDocumentoCliente());
		/*M.17.Tipo de documento de identidad */
		customerAssignedAccountIDType.setSchemeID("6");
		final PartyType partyTypeIdentification = new PartyType();
		customerPartyType.setParty(partyTypeIdentification);
		List<PartyIdentificationType> listPartyIdentification2 = new ArrayList<>();
		partyTypeIdentification.setPartyIdentification(listPartyIdentification2);
		final PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
		listPartyIdentification2.add(partyIdentificationType);
		final IDType iDType = new IDType();
		partyIdentificationType.setID(iDType);
		iDType.setSchemeID("6");
		iDType.setSchemeName("Documento de Identidad");
		iDType.setSchemeAgencyName("PE:SUNAT");
		iDType.setSchemeURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
		iDType.setValue("20155945860");
		/*M.18.Apellidos y nombres, denominación o razón social del destinatario*/
		final List<PartyLegalEntityType> listLegalEntity = new ArrayList<>();
		partyTypeIdentification.setPartyLegalEntity(listLegalEntity);
		final PartyLegalEntityType partyLegalEntityType = new PartyLegalEntityType();
		//partyLegalEntityType.setRegistrationName("PONTIFICIA UNIVERSIDAD CATOLICA DEL PERU");
		partyLegalEntityType.setRegistrationName(remision.getRemision().getNombreCliente());
		listLegalEntity.add(partyLegalEntityType);
		
	}


	private static void generarXML() {
		final String sCurrency = "EUR";
		
		//Factura
		/*final InvoiceType aInvoice = new InvoiceType ();
		aInvoice.setID("Dummy Invoice number");

		aInvoice.setIssueDate (PDTFactory.getCurrentLocalDate());
		
	    aInvoice.setAccountingSupplierParty (aSupplier);

	    final CustomerPartyType aCustomer = new CustomerPartyType ();
	    aInvoice.setAccountingCustomerParty (aCustomer);

	    final MonetaryTotalType aMT = new MonetaryTotalType ();
	    aMT.setPayableAmount (BigDecimal.TEN).setCurrencyID (sCurrency);
	    aInvoice.setLegalMonetaryTotal (aMT);

	    final InvoiceLineType aLine = new InvoiceLineType ();
	    aLine.setID ("1");

	    final ItemType aItem = new ItemType ();
	    aLine.setItem (aItem);

	    aLine.setLineExtensionAmount (BigDecimal.TEN).setCurrencyID (sCurrency);

	    aInvoice.addInvoiceLine (aLine);*/
	    
		
		
		
		//Guia de despacho - Guia de Remision
		final DespatchAdviceType aDespatchAdvice = new DespatchAdviceType();
		final UBLExtensionsType uBLExtensionsType = new UBLExtensionsType();
		final UBLExtensionType uBLExtensionType = new UBLExtensionType();
		final ExtensionContentType  extensionContentType = new ExtensionContentType ();
		final SignatureType signatureType = new SignatureType();

		final SignedInfoType signedInfoType = new SignedInfoType();
		final CanonicalizationMethodType canonicalizationMethodType = new CanonicalizationMethodType(); 
		final SignatureMethodType signatureMethodType = new SignatureMethodType(); 
		List<ReferenceType> aList = new ArrayList<>();
		ReferenceType referenceType = new ReferenceType();
		final TransformsType transformsType  = new TransformsType ();
		final TransformType transformType  = new TransformType ();
		final DigestMethodType digestMethodType = new DigestMethodType();
		final SignatureValueType signatureValueType = new SignatureValueType();
		final KeyInfoType keyInfoType = new KeyInfoType(); 
		final NoteType noteType = new NoteType();
		List<NoteType> listaNoteType = new ArrayList<>();
		List<oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SignatureType> listSignature = new ArrayList<>();
		oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SignatureType signature = new oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SignatureType();
		final PartyType partyType = new PartyType();
		List<PartyIdentificationType> listPartyIdentification = new ArrayList<>();
		final PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
		List<PartyNameType> listPartyName = new ArrayList<>();
		final PartyNameType partyNameType = new PartyNameType();
		final AttachmentType  attachmentType  = new AttachmentType ();
		final ExternalReferenceType externalReferenceType = new ExternalReferenceType();
		final SupplierPartyType supplierPartyType = new SupplierPartyType();
		final CustomerAssignedAccountIDType customerAssignedAccountIDType = new CustomerAssignedAccountIDType();
		final PartyType partyTypeIdentification = new PartyType();
		List<PartyIdentificationType> listPartyIdentification2 = new ArrayList<>();
		final IDType iDType = new IDType();
		List<PartyLegalEntityType> listLegalEntity = new ArrayList<>();
		PartyLegalEntityType partyLegalEntityType = new PartyLegalEntityType();
		final CustomerPartyType customerPartyType = new CustomerPartyType();
		final ShipmentType shipmentType = new ShipmentType();
		final HandlingCodeType handlingCodeType = new HandlingCodeType();
		final GrossWeightMeasureType grossWeightMeasureType = new GrossWeightMeasureType();
		List<DespatchLineType> listDespatchLine = new ArrayList<>();
		final DespatchLineType despatchLineType = new DespatchLineType();
		final DeliveredQuantityType deliveredQuantityType = new DeliveredQuantityType();
		List<OrderLineReferenceType> listOrderLineReferenceType = new ArrayList<>();
		final OrderLineReferenceType orderLineReferenceType = new OrderLineReferenceType();
		final ItemType itemType = new ItemType();
		List<DescriptionType> listDescriptionType = new ArrayList<>();
		final DescriptionType descriptionType = new DescriptionType();
		final ItemIdentificationType itemIdentificationType = new ItemIdentificationType();
		String str = "PANKAJ";
		byte datos[] = str.getBytes();
		List<Object> listObject = new ArrayList<>();
		List<Object> listObject2 = new ArrayList<>();
		final X509DataType x509Data = new X509DataType();
		final SignatureValueType signatureValueType2 = new SignatureValueType();
		
		final Document aDoc = XMLFactory.newDocument ();
		final String sNamespaceURI = "urn:AIFE:Facture:Extension";
	    final Node eRoot = aDoc.appendChild (aDoc.createElementNS (sNamespaceURI, "FactureExtension"));
	    final Node eCategoryCode = eRoot.appendChild (aDoc.createElementNS (sNamespaceURI, "CategoryCode"));
	    eCategoryCode.appendChild (aDoc.createTextNode ("XX"));
	    
		
		aDespatchAdvice.setUBLExtensions(uBLExtensionsType);
		uBLExtensionsType.addUBLExtension(uBLExtensionType);
		uBLExtensionType.setExtensionContent(extensionContentType);
		extensionContentType.setAny(aDoc.getDocumentElement ());
		
		signatureType.setId("Sign");
		signatureType.setSignedInfo(signedInfoType);
		signedInfoType.setCanonicalizationMethod(canonicalizationMethodType);
		signedInfoType.setSignatureMethod(signatureMethodType);
		signedInfoType.setReference(aList);
		referenceType.setTransforms(transformsType);
		referenceType.setDigestMethod(digestMethodType);
		referenceType.setDigestValue(datos); //TODO: PENDIENTE 1: DigestValue
		aList.add(referenceType);
		
		signatureType.setSignatureValue(signatureValueType);
		signatureValueType.setValue(datos); //PENDIENTE 2: SignatureValue
		
		signatureType.setKeyInfo(keyInfoType);
		keyInfoType.setContent(listObject);//PENDIENTE 3: Certificado
		listObject.add(x509Data);
		x509Data.setX509IssuerSerialOrX509SKIOrX509SubjectName(listObject2);
		listObject2.add("Hola");
		signatureType.setSignatureValue(signatureValueType2);
		signatureValueType.setValue(datos);
		
		canonicalizationMethodType.setAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
		signatureMethodType.setAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
		transformType.setAlgorithm("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
		digestMethodType.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
		//PENDIENTE 1: DigestValue
		
		
		
		
		
		aDespatchAdvice.setUBLVersionID("2.1");
		aDespatchAdvice.setCustomizationID("2.0");
		aDespatchAdvice.setID("TTT1-1");
		aDespatchAdvice.setIssueDate(PDTFactory.getCurrentLocalDate());
		aDespatchAdvice.setIssueTime(PDTFactory.getCurrentLocalTime());
		aDespatchAdvice.setDespatchAdviceTypeCode("09");
		noteType.setValue("Obs: observaciones");
		listaNoteType.add(noteType);
		aDespatchAdvice.setNote(listaNoteType);
		aDespatchAdvice.setSignature(listSignature);
		signature.setID("20000000001");
		signature.setSignatoryParty(partyType);
		partyType.setPartyIdentification(listPartyIdentification);
		partyIdentificationType.setID("20000000001");
		listPartyIdentification.add(partyIdentificationType);
		partyType.setPartyName(listPartyName);
		partyNameType.setName("EMPRESA DE PRUEBA SA");
		listPartyName.add(partyNameType);
		signature.setDigitalSignatureAttachment(attachmentType);
		attachmentType.setExternalReference(externalReferenceType);
		externalReferenceType.setURI("20000000001");
		listSignature.add(signature);
		/*DespatchSupplierParty*/
		aDespatchAdvice.setDespatchSupplierParty(supplierPartyType);
		supplierPartyType.setCustomerAssignedAccountID(customerAssignedAccountIDType);
		customerAssignedAccountIDType.setSchemeID("6");
		customerAssignedAccountIDType.setValue("20000000001");
		supplierPartyType.setParty(partyTypeIdentification);
		partyTypeIdentification.setPartyIdentification(listPartyIdentification2);
		listPartyIdentification2.add(partyIdentificationType);
		partyIdentificationType.setID(iDType);
		iDType.setSchemeID("6");
		iDType.setSchemeName("Documento de Identidad");
		iDType.setSchemeAgencyName("PE:SUNAT");
		iDType.setSchemeURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
		iDType.setValue("20000000001");
		partyTypeIdentification.setPartyLegalEntity(listLegalEntity);
		partyLegalEntityType.setRegistrationName("EMPRESA DE PRUEBA SA");
		listLegalEntity.add(partyLegalEntityType);
		/*DeliveryCustomerParty*/
		aDespatchAdvice.setDeliveryCustomerParty(customerPartyType);
		//Temporal solo para las pruebas
		customerPartyType.setCustomerAssignedAccountID(customerAssignedAccountIDType);
		customerPartyType.setParty(partyTypeIdentification);
		/*Shipment*/
		aDespatchAdvice.setShipment(shipmentType);
		shipmentType.setID("SUNAT_Envio");
		shipmentType.setHandlingCode(handlingCodeType);
		handlingCodeType.setListAgencyName("PE:SUNAT");
		handlingCodeType.setListName("Motivo de traslado");
		handlingCodeType.setListURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo20");
		handlingCodeType.setValue("01");
		shipmentType.setGrossWeightMeasure(grossWeightMeasureType);
		grossWeightMeasureType.setUnitCode("KGM");
		grossWeightMeasureType.setValue(new BigDecimal("1.0"));
		/*DespatchLine*/
		aDespatchAdvice.setDespatchLine(listDespatchLine);
		listDespatchLine.add(despatchLineType);
		despatchLineType.setID("1");
		despatchLineType.setDeliveredQuantity(deliveredQuantityType);
		deliveredQuantityType.setUnitCode("NIU");
		deliveredQuantityType.setUnitCodeListID("UN/ECE rec 20");
		deliveredQuantityType.setUnitCodeListAgencyName("nited Nations Economic Commission for Europe");
		deliveredQuantityType.setValue(new BigDecimal("1.0"));
		despatchLineType.setOrderLineReference(listOrderLineReferenceType);
		listOrderLineReferenceType.add(orderLineReferenceType);
		orderLineReferenceType.setLineID("2");
		despatchLineType.setItem(itemType);
		itemType.setDescription(listDescriptionType);
		descriptionType.setValue("DETALLE DEL PRODUCTO 1");
		listDescriptionType.add(descriptionType);
		itemType.setSellersItemIdentification(itemIdentificationType);
		itemIdentificationType.setID("001");
		
		
		
		
		
		transformsType.addTransform(transformType);
		
		
		
		
		
		
		
		
		
		
		/*final ESuccess eSuccess = UBL21Marshaller.invoice ()
                .write (aInvoice,
                        new File ("target/dummy-invoice-no-second-fraction.xml"));*/
		
		final ESuccess eSuccessDespatch = UBL21Marshaller.despatchAdvice()
                .write (aDespatchAdvice,
                        new File ("target/20000000001-09-TTT1-1.xml"));
		
		
		
		
	}

}
