package com.pe.lima.sg.util.remision.util;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class LeerArchivoFirmadoXML {
    public static void main(String[] args) {
        try {
            // Cadena Base64 que representa el archivo XML firmado
            String base64String = "UEsDBBQAAAAIAHquz1ZmusjezQMAAI0NAAAcAAAAUi0yMDYwMjYyMDMzNy0wOS1UVFQxLTE3LnhtbJ1X23KjOBB9z1dQ5Blz8SVjCjvl2Jkq1+6kphxndl9laGO2QGIlkTj79SsBwgLjZBw/QXfr9OnTTasc3B+z1HgFyhKCZ6Y7cEwDcEiiBMcz82X73fpm3s9vAkT9RZ6nSYi4CNwAywlmYIjDmPmIzsyCYp8gljAfowyYz3IIk30d7xe71GfhATLkH1nUB2V5Zo0GR34l3JJkGcGPRw5YliFeBSRgzk6g4S78EuiDCA97AdHXABdxTCFGHPpAIzYzD5znvm2/vb0N3oYDQmPbcxzHdqa2iIlYEt+qaEZQ3sRXidhAuKS9PCgfbMCvkJIcbHN+YxiBUNd/efizEYtJa4+9MtcOTVosnrhyCvdzEmPEC1rPwm/xPx2vASBa4z3RzcKxRJhgoWWa/Ffq+QP4gUTGIo0JTfghu5DKtV1HprLgGFqhO8K3f4loKb7U2rQ7WRr+vw3vjFQlVkYo3FKGLHZA3nhyBr6BPVDxOYHxslnPTLPtFgFbijDbE5qxrkt3fkqqJa9qeGQxVVuX2JXw10kqoO3LdQWrJAbGr1RbaHjbr3GD+AulBXQbYDcdaM2c3T90p2E4Bwv+gPeeIf177ExXiKMzUtKxBMqrdQDzo3gPT++B3Q3oA1gzVgB9BpqgtKeDp5AnsX7KFKzY/QMhl+uoSqH5+wEq9Kci2wGdT+WvOtiynzX4Y3aVv6tLYJ9pWHWilLx";

            // Decodificar la cadena Base64 a bytes
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);

            // Convertir los bytes a una cadena de texto
            String xmlString = new String(decodedBytes, StandardCharsets.UTF_8);
            System.out.println(xmlString);
            System.out.println("**********************************************************************");
            // Eliminar caracteres no válidos del inicio del archivo XML
            xmlString = xmlString.replaceAll("^([\\W]+)<","<");
            System.out.println(xmlString);

            // Crear un objeto DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Analizar la cadena XML utilizando InputSource
            InputSource inputSource = new InputSource(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
            Document doc = builder.parse(inputSource);

            // Obtener el contenido del archivo XML sin la firma
            NodeList nodeList = doc.getElementsByTagNameNS("*", "*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                System.out.println(nodeList.item(i).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


