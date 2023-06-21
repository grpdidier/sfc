package com.pe.lima.sg.util.remision.util;

import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.X509Certificate;

public class XMLSigner2 {
	private static final String archivoOrigen = "target/20602620337-09-TTT1-00000017-SinFirma.xml";
    private static final String archivoFinal = "target/20602620337-09-TTT1-00000017.xml";
    
    private static final String KEYSTORE_PATH = "target/llamaKeystore.jks";
    private static final String KEYSTORE_PASSWORD = "Admin74123";
    private static final String KEY_ALIAS = "20602620337";
    private static final String KEY_PASSWORD = "20602620337";

    public static void main(String[] args) {
        try {
            // Inicializar Apache XML Security
            Init.init();

            // Cargar el archivo XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            org.w3c.dom.Document document = dbf.newDocumentBuilder().parse(new FileInputStream(archivoOrigen));

            // Cargar el archivo JKS
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream(KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());

            // Obtener la clave privada y el certificado del alias
            PrivateKey privateKey = (PrivateKey) keystore.getKey(KEY_ALIAS, KEY_PASSWORD.toCharArray());
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(KEY_ALIAS);

            // Crear el objeto XMLSignature
            XMLSignature signature = new XMLSignature(document, "", XMLSignature.ALGO_ID_SIGNATURE_RSA);
            signature.getElement().setAttribute("xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
            

            // Crear las etiquetas ext:UBLExtensions, ext:UBLExtension y ext:ExtensionContent
            org.w3c.dom.Element ublExtensionsElement = document.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtensions");
            org.w3c.dom.Element ublExtensionElement = document.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtension");
            org.w3c.dom.Element extensionContentElement = document.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:ExtensionContent");

            // Insertar las etiquetas ext:UBLExtensions, ext:UBLExtension y ext:ExtensionContent antes de la firma
            org.w3c.dom.Element rootElement = document.getDocumentElement();
            rootElement.insertBefore(ublExtensionsElement, rootElement.getFirstChild());

            // Construir la estructura de las etiquetas ext:UBLExtensions, ext:UBLExtension y ext:ExtensionContent
            extensionContentElement.appendChild(signature.getElement());
            ublExtensionElement.appendChild(extensionContentElement);
            ublExtensionsElement.appendChild(ublExtensionElement);

            // Configurar la clave privada y el certificado en XMLSignature
            signature.addKeyInfo(certificate);
            signature.addKeyInfo(certificate.getPublicKey());

            // Añadir las transformaciones necesarias
            Transforms transforms = new Transforms(document);
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            signature.addDocument("", transforms, "http://www.w3.org/2000/09/xmldsig#sha1");

            // Firmar el documento
            signature.sign(privateKey);
            // Obtener la etiqueta DespatchAdvice
            org.w3c.dom.Element despatchAdviceElement = (org.w3c.dom.Element) document.getElementsByTagName("DespatchAdvice").item(0);

            // Agregar el namespace "xmlns:ds" a la etiqueta DespatchAdvice
            despatchAdviceElement.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");

            // Guardar el archivo XML firmado
            XMLUtils.outputDOM(document, new FileOutputStream(archivoFinal), true);

            System.out.println("Firma agregada correctamente al archivo XML.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
