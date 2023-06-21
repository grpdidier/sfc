package com.pe.lima.sg.util.remision.util;
import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLSinger3 {
    private static final String KEYSTORE_PATH = "ruta/al/archivo.jks";
    private static final String KEYSTORE_PASSWORD = "contraseña_del_keystore";
    private static final String KEY_ALIAS = "alias_del_certificado";
    private static final String KEY_PASSWORD = "contraseña_del_certificado";

    public static void main(String[] args) {
        try {
            // Inicializar Apache XML Security
            Init.init();

            // Cargar el archivo XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document document = dbf.newDocumentBuilder().parse(new FileInputStream("ruta/al/archivo.xml"));

            // Cargar el archivo JKS
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream(KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());

            // Obtener la clave privada y el certificado del alias
            PrivateKey privateKey = (PrivateKey) keystore.getKey(KEY_ALIAS, KEY_PASSWORD.toCharArray());
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(KEY_ALIAS);

            // Crear las etiquetas ext:UBLExtensions, ext:UBLExtension y ext:ExtensionContent
            Element ublExtensionsElement = document.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtensions");
            Element ublExtensionElement = document.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtension");
            Element extensionContentElement = document.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:ExtensionContent");

            // Crear el elemento ds:Signature
            Element signatureElement = createSignatureElement(document);

            // Construir la estructura de las etiquetas ext:UBLExtensions, ext:UBLExtension y ext:ExtensionContent
            extensionContentElement.appendChild(signatureElement);
            ublExtensionElement.appendChild(extensionContentElement);
            ublExtensionsElement.appendChild(ublExtensionElement);

            // Insertar las etiquetas ext:UBLExtensions, ext:UBLExtension y ext:ExtensionContent antes de la firma
            Element rootElement = document.getDocumentElement();
            rootElement.insertBefore(ublExtensionsElement, rootElement.getFirstChild());

            // Crear el objeto XMLSignature
            XMLSignature signature = new XMLSignature(document, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);
            signature.getElement().appendChild(signatureElement);

            // Configurar la clave privada y el certificado en XMLSignature
            signature.addKeyInfo(certificate);
            signature.addKeyInfo(certificate.getPublicKey());

            // Añadir las transformaciones necesarias
            Transforms transforms = new Transforms(document);
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            //ignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA256);

            // Firmar el documento
            privateKey = (PrivateKey) keystore.getKey(KEY_ALIAS, KEY_PASSWORD.toCharArray());
            signature.sign(privateKey);

            // Guardar el archivo XML firmado
            XMLUtils.outputDOM(document, new FileOutputStream("ruta/al/archivo_firmado.xml"), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Element createSignatureElement(Document document) {
        String dsPrefix = "ds";
        String dsNamespace = Constants.SignatureSpecNS;

        Element signatureElement = ElementProxy.createElementForFamily(document, dsNamespace, "Signature");
        signatureElement.setAttribute("xmlns:" + dsPrefix, dsNamespace);

        return signatureElement;
    }
}
