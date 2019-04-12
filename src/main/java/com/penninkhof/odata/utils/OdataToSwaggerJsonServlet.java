package com.penninkhof.odata.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Transforms odata XML to Swagger json and serves the result.
 * The Java "port" of https://github.com/oasis-tcs/odata-openapi/tree/master/tools
 */
public class OdataToSwaggerJsonServlet extends HttpServlet {

    private final String odataMetadataPath;
    private byte[] swaggerJson;

    public OdataToSwaggerJsonServlet(String odataMetadataPath) {
        this.odataMetadataPath = odataMetadataPath;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if( swaggerJson == null ) {
            try {
                swaggerJson = loadSwaggerJson(req, resp);
            } catch (TransformerException e) {
                throw new IOException("Failed to load swagger JSON", e);
            }
        }
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        IOUtils.write(swaggerJson, resp.getOutputStream());
    }

    private byte[] loadSwaggerJson(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, TransformerException {
        byte[] odataXml = loadOdataXml(req, resp);
        return odata2swagger(odataXml);
    }

    private byte[] odata2swagger(byte[] odataXml) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(getResource("V2-to-V4-CSDL.xsl"));
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(new ByteArrayInputStream(odataXml));
        ByteArrayOutputStream odataV4XmlOut = new ByteArrayOutputStream();
        transformer.transform(text, new StreamResult(odataV4XmlOut));

        Source xslt2 = new StreamSource(getResource("V4-CSDL-to-OpenAPI.xsl"));
        Transformer transformer2 = factory.newTransformer(xslt2);
        transformer2.setParameter("openapi-version", "3.0.0");
        ByteArrayInputStream odataV4XmlIn = new ByteArrayInputStream(odataV4XmlOut.toByteArray());
        Source text2 = new StreamSource(odataV4XmlIn);

        ByteArrayOutputStream swaggerJsonOut = new ByteArrayOutputStream();
        transformer2.transform(text2, new StreamResult(swaggerJsonOut));
        return swaggerJsonOut.toByteArray();
    }

    private InputStream getResource(String file) {
        return getClass().getClassLoader().getResourceAsStream("xslt/"+file);
    }

    private byte[] loadOdataXml(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HttpServletResponseWrapper servletResponseWrapper = new HttpServletResponseWrapper(resp) {

            @Override
            public PrintWriter getWriter() {
                return new PrintWriter(byteArrayOutputStream);
            }

            @Override
            public ServletOutputStream getOutputStream() {
                return new ServletOutputStream() {

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener listener) {

                    }

                    @Override
                    public void write(int i) throws IOException {
                        byteArrayOutputStream.write(i);
                    }
                };
            }
        };
        req.getRequestDispatcher(odataMetadataPath).forward(req, servletResponseWrapper);

        return byteArrayOutputStream.toByteArray();
    }
}
