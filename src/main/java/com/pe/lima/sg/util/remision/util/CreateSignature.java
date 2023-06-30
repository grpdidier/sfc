package com.pe.lima.sg.util.remision.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.springframework.stereotype.Service;

import com.pe.lima.sg.bean.remision.RemisionBean;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class CreateSignature {
	



    public void firmar(String nameFileInicio , String fileName, File privateKeyFile, RemisionBean remision) throws Exception {
    	String PRIVATE_KEY_ALIAS = remision.getPrivateKeyAlias();//"20602620337";
        String PRIVATE_KEY_PASS = remision.getPrivateKeyPass();//"20602620337";
        String KEY_STORE_PASS = remision.getKeyStorePass();//"Admin74123";
        String KEY_STORE_TYPE = remision.getKeyStoreType();//"JKS";
        
        try {
        	log.info("[firmar] Inicio");
            // Inicializar Apache XML Security
            Init.init();

            // Cargar el archivo XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            org.w3c.dom.Document document = dbf.newDocumentBuilder().parse(new FileInputStream(nameFileInicio));
            //Corregir el formato de la hh:mm:ss
            // Obtener el nodo de la etiqueta cbc:IssueTime
            org.w3c.dom.Node issueTimeNode = document.getElementsByTagName("cbc:IssueTime").item(0);
            // Modificar el valor de la etiqueta cbc:IssueTime
            issueTimeNode.setTextContent(obtenerHHMMSS(issueTimeNode.getTextContent()));

            // Cargar el archivo JKS
            KeyStore keystore = loadKeyStore(privateKeyFile,KEY_STORE_PASS,KEY_STORE_TYPE);

            // Obtener la clave privada y el certificado del alias
            PrivateKey privateKey = (PrivateKey) keystore.getKey(PRIVATE_KEY_ALIAS, PRIVATE_KEY_PASS.toCharArray());
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(PRIVATE_KEY_ALIAS);

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
            log.info("[firmar] fileName:"+fileName);
            // Guardar el archivo XML firmado
            XMLUtils.outputDOM(document, new FileOutputStream(fileName), true);

            System.out.println("Firma agregada correctamente al archivo XML.");
            log.info("[firmar] fin");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[firmar] Error:"+e.getMessage());
            throw e;
        }
    }
    private  String obtenerHHMMSS(String textContent) {
    	return textContent.substring(0,8);

	}
	/*public  void FirmarConError(String nameFileInicio , String fileName, File privateKeyFile) throws IOException {
    	OutputStream fileOutputStream = null;
        try {
        	ByteArrayOutputStream signedOutputStream = firmarArchivo(nameFileInicio, privateKeyFile);
            fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(signedOutputStream.toByteArray());
            fileOutputStream.flush();
        }catch(Exception e) {
        	e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }*/
    /*public  ByteArrayOutputStream firmarArchivo(String nameFileInicio, File privateKeyFile) throws Exception {
    	
    	final InputStream xmlFile = new FileInputStream(nameFileInicio);
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        
        NodeList nodes = doc.getElementsByTagName("cbc:UBLVersionID");
        
        Element rootElement = doc.createElement("ext:UBLExtensions");
        doc.getDocumentElement().appendChild(rootElement);
        Element elemento1 = doc.createElement("ext:UBLExtension");
        rootElement.appendChild(elemento1);
        Element elemento2 = doc.createElement("ext:ExtensionContent");
        elemento1.appendChild(elemento2);
        
        nodes.item(0).getParentNode().insertBefore(rootElement, nodes.item(0));
 
        Init.init();
        ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "");
        final KeyStore keyStore = loadKeyStore(privateKeyFile);
        final XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA);
        final Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
        final Key privateKey = keyStore.getKey(PRIVATE_KEY_ALIAS, PRIVATE_KEY_PASS.toCharArray());
        final X509Certificate cert = (X509Certificate)keyStore.getCertificate(PRIVATE_KEY_ALIAS);
        sig.addKeyInfo(cert);
        sig.addKeyInfo(cert.getPublicKey());
        sig.sign(privateKey);
        
        
        elemento2.appendChild(sig.getElement());
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS).canonicalizeSubtree(doc));
        
        String signedOutputStream = outputStream.toString();
        
        final ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        outputStream2.write(signedOutputStream.getBytes());
        return outputStream2;
    }*/
    


    private  KeyStore loadKeyStore(File privateKeyFile, String KEY_STORE_PASS,String KEY_STORE_TYPE) throws Exception {
        final InputStream fileInputStream = new FileInputStream(privateKeyFile);
        try {
            final KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(fileInputStream, KEY_STORE_PASS.toCharArray());
            return keyStore;
        }
        finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }


}
