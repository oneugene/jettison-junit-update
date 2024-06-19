import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

package com.example;

public abstract class DOMTest {

    private DocumentBuilder parser;

    public DOMTest() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(false);
        parser = factory.newDocumentBuilder();
    }

    protected Element parse(String xmlStr) throws Exception {
        return parser.parse(new InputSource(new StringReader(xmlStr))).getDocumentElement();
    }

    protected String printNode(org.w3c.dom.Document doc) throws Exception {
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        return sw.toString().trim();
    }

    @Test
    public void test() throws Exception {
        // Add test cases here
    }
}