package com.penninkhof.odata;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class XsltTest {

    public static void main(String[] args) throws IOException, URISyntaxException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(resource("V2-to-V4-CSDL.xsl"));
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(resource("test.xml"));
        File file = new File("output.xml");
        transformer.transform(text, new StreamResult(file));

        Source xslt2 = new StreamSource(resource("V4-CSDL-to-OpenAPI.xsl"));
        Transformer transformer2 = factory.newTransformer(xslt2);
        transformer2.setParameter("openapi-version", "3.0.0");
        Source text2 = new StreamSource(file);

        transformer2.transform(text2, new StreamResult(new File("swagger.json")));
    }

    private static InputStream resource(String file) {
        return XsltTest.class.getClassLoader().getResourceAsStream("xslt/"+file);
    }
}