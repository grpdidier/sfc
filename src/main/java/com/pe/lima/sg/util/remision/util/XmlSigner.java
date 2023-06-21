package com.pe.lima.sg.util.remision.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


public class XmlSigner {
	private static final String PRIVATE_KEY_ALIAS = "20602620337";
    private static final String PRIVATE_KEY_PASS = "20602620337";
    private static final String KEY_STORE_PASS = "Admin74123";
    private static final String KEY_STORE_TYPE = "JKS";
    private static final String JSKPATH = "target/llamaKeystore.jks";
    private static final String archivoOrigen = "target/20602620337-09-TTT1-00000012-SinFirma.xml";
    private static final String archivoFinal = "target/20602620337-09-TTT1-00000012.xml";
    private static final String jksPath = JSKPATH;
    private static final String jksPassword = KEY_STORE_PASS;
    private static final String alias = PRIVATE_KEY_ALIAS;
    private static final String keyPassword = PRIVATE_KEY_PASS;
    
    public static void main(String[] args) {
        try {
            // Cargar el archivo XML a firmar
            Document document = loadXmlDocument(archivoOrigen);

            // Cargar el certificado y la clave privada del almacén de claves JKS
           
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream(jksPath), jksPassword.toCharArray());
            X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
            PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keyPassword.toCharArray());

            // Crear una instancia de XMLSignatureFactory
            XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");

            // Crear una referencia al elemento que se desea firmar
            Reference reference = factory.newReference("", factory.newDigestMethod(DigestMethod.SHA256, null),
                    Collections.singletonList(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null, null);

            // Crear la información firmada
            SignedInfo signedInfo = factory.newSignedInfo(factory.newCanonicalizationMethod(
                    CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    Collections.singletonList(reference));

            // Crear el objeto KeyInfo con el certificado
            KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
            X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(cert));
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

            // Crear el contexto de firma
           // DOMSignContext signContext = new DOMSignContext(privateKey, document.getDocumentElement());

            // Crear el objeto XMLSignature y firmar el documento
            //XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);
            // Crear el contexto de firma
            Element ublExtensionsElement = (Element) document.getElementsByTagName("ext:UBLExtensions").item(0);
            Element ublExtensionElement = (Element) ublExtensionsElement.getElementsByTagName("ext:UBLExtension").item(0);
            Element extensionContentElement = (Element) ublExtensionElement.getElementsByTagName("ext:ExtensionContent").item(0);
            DOMSignContext signContext = new DOMSignContext(privateKey, extensionContentElement);

            // Crear el objeto XMLSignature y firmar el documento
            XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);
            ublExtensionsElement.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
            // Firmar el documento
            signature.sign(signContext);

            // Guardar el documento firmado
            saveXmlDocument(document, archivoFinal);

            System.out.println("El documento XML se ha firmado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Document loadXmlDocument(String path) throws Exception {
        // Cargar el archivo XML como un objeto Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new FileInputStream(path)));
    }

    private static void saveXmlDocument(Document document, String path) throws Exception {
        // Guardar el objeto Document como un archivo XML
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        Result output = new StreamResult(new File(path));
        Source input = new DOMSource(document);

        transformer.transform(input, output);
    }
    
}
