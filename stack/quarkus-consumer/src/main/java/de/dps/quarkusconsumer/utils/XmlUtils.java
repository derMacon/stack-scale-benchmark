package de.dps.quarkusconsumer.utils;

import de.dps.quarkusconsumer.service.ExtractionService;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

public class XmlUtils {

    private final static Logger LOG = Logger.getLogger(XmlUtils.class);

    public static boolean validateAgainstXSD(String xmlStr, String xsdPath) {
        LOG.info("xmlUtils - xmlStr: " + xmlStr + "; xsdPath: " + xsdPath);
        String xsdContent = ResourceUtils.readResource(ExtractionService.class, xsdPath);

        try {
            InputStream xsdInput = IOUtils.toInputStream(xsdContent, "UTF-8");
            InputStream xmlInput = IOUtils.toInputStream(xmlStr, "UTF-8");

            LOG.info("xsdInput null: " + xsdInput == null);
            LOG.info("xmlInput null: " + xmlInput == null);

//            com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl ref =
//                    new com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl();

            SchemaFactory factory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            LOG.info("schema factory: " + factory);

            StreamSource src = new StreamSource(xsdInput);
            LOG.info("streamSource: " + src);
            Schema schema = factory.newSchema(src);
            LOG.info("schema: " + schema);

            Validator validator = schema.newValidator();
            LOG.info("validator: " + validator);
            validator.validate(new StreamSource(xmlInput));
            return true;
        } catch (Exception ex) {
            // todo exception handling
            LOG.info("xml utils exc: " + ex.getMessage());
            return false;
        }

    }


    /**
     * Extracts an element from a xml content using a xpath specification
     *
     * @param xPath      path to the element to extract
     * @param xmlContent content from which the element should be extracted
     * @return element from the xml content
     */
    public static String extractElem(String xPath, String xmlContent) {
        String output = null;
        try {
            InputStream in = IOUtils.toInputStream(xmlContent, "UTF-8");
            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(in);
            //create XPathExpression object
            XPathExpression expr = XPathFactory
                    .newInstance()
                    .newXPath()
                    .compile(xPath);
            //evaluate expression result on XML document
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            output = nodes.item(0).getFirstChild().getNodeValue();
        } catch (XPathExpressionException e) {
            // todo exception handling
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

}
