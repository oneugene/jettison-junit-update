package org.codehaus.jettison.mapped;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;

public class MappedXMLStreamReaderTest {

    @Test
    public void testStreamReader() throws Exception {
        JSONObject obj = new JSONObject("{ " +
                "\"root\" : { " +
                "\"child1\" : \"child1\"" +
                "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testStreamReaderNullTextAsString() throws Exception {
        JSONObject obj = new JSONObject("{ " +
                "\"root\" : { " +
                "\"child1\" : \"null\"" +
                "} }");
        Configuration c = new Configuration();
        c.setReadNullAsString(true);
        MappedNamespaceConvention con = new MappedNamespaceConvention(c);
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("null", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testStreamReaderNullTextAsNull() throws Exception {
        JSONObject obj = new JSONObject("{ " +
                "\"root\" : { " +
                "\"child1\" : \"null\"" +
                "} }");
        Configuration c = new Configuration();
        MappedNamespaceConvention con = new MappedNamespaceConvention(c);
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertNull(reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testMultipleChildren() throws Exception {
        JSONObject obj = new JSONObject("{ " +
                "\"root\" : { " +
                "\"child1\" : \"child1\"," +
                "\"child2\" : \"child2\"" +
                "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child2", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child2", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child2", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testMultipleChildrenWithSameName() throws Exception {
        JSONObject obj = new JSONObject("{ " +
                "\"root\" : { " +
                "\"child1\" : \"child1\"," +
                "\"child1\" : \"child11\"" +
                "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child11", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testNestedArrayOfChildren() throws Exception {
        JSONObject obj = new JSONObject("{" +
                "\"root\":" +
                "{\"child1\":" +
                "[{\"subchild2\":" +
                "[\"first sub2\",\"second sub2\",\"third sub2\"]}" +
                ",\"sub1\",\"sub2\"]" +
                ",\"child2\":\"child2\"}" +
                "}");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("subchild2", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("first sub2", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("subchild2", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("subchild2", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("second sub2", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("subchild2", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("subchild2", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("third sub2", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("subchild2", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("sub1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("sub2", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child2", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child2", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child2", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testNamespaces() throws Exception {
        JSONObject obj =
                new JSONObject("{ " +
                        "\"foo.root\" : { " +
                        "\"foo.child1\" : \"childtext\"," +
                        "} }");

        Map xtoj = new HashMap();
        xtoj.put("http://foo/", "foo");
        MappedNamespaceConvention con = new MappedNamespaceConvention(new Configuration(xtoj));
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals("http://foo/", reader.getName().getNamespaceURI());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals("http://foo/", reader.getName().getNamespaceURI());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("childtext", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals("http://foo/", reader.getName().getNamespaceURI());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals("http://foo/", reader.getName().getNamespaceURI());
    }

    @Test
    public void testDefaultNamespace() throws Exception {
        JSONObject obj = new JSONObject("{ " + "\"root\" : { " + "\"child1\" : \"childtext\"," + "} }");

        Map xtoj = new HashMap();
        xtoj.put("http://foo/", "");
        MappedNamespaceConvention con = new MappedNamespaceConvention(new Configuration(xtoj));
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals("http://foo/", reader.getName().getNamespaceURI());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals("http://foo/", reader.getName().getNamespaceURI());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("childtext", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals("http://foo/", reader.getName().getNamespaceURI());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals("http://foo/", reader.getName().getNamespaceURI());
    }

    @Test
    public void testArrayWithSameJSONObjects() throws Exception {
        String str = "{\"theBook\":"
                + "{"
                + "\"Names\":[{\"Name\":\"1\"}, {\"Name\":\"2\"}]"
                + " }                   "
                + "} ";
        Configuration config = new Configuration();
        config.setPrimitiveArrayKeys(Collections.singleton("Names"));
        MappedXMLInputFactory factory = new MappedXMLInputFactory(config);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(str));

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Names", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("1", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("2", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Names", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

    }

    @Test
    public void testArrayWithSameJSONObjects2() throws Exception {
        String str = "{\"theBook\":[{\"Name\":\"1\"}, {\"Name\":\"2\"}]}";
        Configuration config = new Configuration();
        config.setPrimitiveArrayKeys(Collections.singleton("theBook"));
        MappedXMLInputFactory factory = new MappedXMLInputFactory(config);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(str));

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("1", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("2", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

    }

    @Test
    public void testArrayWithSameJSONObjects3() throws Exception {
        String str = "{\"theBook\":"
                + "{"
                + "\"Names\":["
                + "{\"Name\":[{\"value\":\"11\"}, {\"value\":\"12\"}]},"
                + "{\"Name\":[{\"value\":\"21\"}, {\"value\":\"22\"}]}"
                + "]}"
                + "} ";
        Configuration config = new Configuration();
        Set<String> set = new HashSet<String>();
        set.add("Names");
        set.add("Name");
        config.setPrimitiveArrayKeys(set);
        MappedXMLInputFactory factory = new MappedXMLInputFactory(config);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(str));

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Names", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("value", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("11", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("value", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("value", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("12", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("value", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("value", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("21", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("value", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("value", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("22", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("value", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Names", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

    }

    @Test
    public void testExceptionLocation() throws Exception {
        String str = "{\"junk";
        Configuration config = new Configuration();
        MappedXMLInputFactory factory = new MappedXMLInputFactory(config);
        try {
            factory.createXMLStreamReader(new StringReader(str));
            Assertions.fail("Exception expected");
        } catch (XMLStreamException ex) {
            Location loc = ex.getLocation();
            Assertions.assertNotNull(loc);
            Assertions.assertEquals(0, loc.getLineNumber());
            Assertions.assertEquals(6, loc.getColumnNumber());
        }
    }

    @Test
    public void testArrayWithNotSameJSONObjects() throws Exception {
        String str = "{\"theBook\":[{\"Name\":\"1\"}, {\"Bar\":\"2\"}]}";
        Configuration config = new Configuration();
        config.setPrimitiveArrayKeys(Collections.singleton("theBook"));
        MappedXMLInputFactory factory = new MappedXMLInputFactory(config);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(str));

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("1", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Name", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("Bar", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("2", reader.getText());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("Bar", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("theBook", reader.getName().getLocalPart());
    }


    @Test
    public void testMultipleArrays() throws Exception {
        JSONObject obj = new JSONObject("{ \"root\": "
                + " [ "
                + " { "
                + "     \"relationships\":[\"friend\"] , "
                + "     \"emails\":[{\"value\":\"f@foo.com\"},{\"value\":\"b@bar.com\"}] "
                + " } "
                + " ] "
                + "}}");
        // Expected XML
        String expXml = "<root>"
                + "<relationships>friend</relationships>"
                + "<emails>"
                + "<value>f@foo.com</value>"
                + "</emails>"
                + "<emails>"
                + "<value>b@bar.com</value>"
                + "</emails>"
                + "</root>";

        doTestEvents(obj.toString(), expXml);
    }

    @Test
    public void testSingleArrayWithOneElement() throws Exception {
        JSONObject obj = new JSONObject("{ \"root\": "
                + " [ "
                + " { "
                + "     \"relationship\":\"friend\" , "
                + "     \"email\":\"f@foo.com\" "
                + " } "
                + " , "
                + " { "
                + "     \"relationship\":\"relative\" , "
                + "     \"email\":\"b@foo.com\" "
                + " } "
                + " ] "
                + "}");

        // Expected XML
        String expXml = "<root>"
                + "<relationship>friend</relationship>"
                + "<email>f@foo.com</email>"
                + "<relationship>relative</relationship>"
                + "<email>b@foo.com</email>"
                + "</root>";

        doTestEvents(obj.toString(), expXml);
    }

    @Test
    public void testStreamReaderWithNullValue() throws Exception {
        JSONObject obj = new JSONObject("{ " +
                "\"root\" : { " +
                "\"child1\" : null" +
                "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertNull(reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testGetElementTextNull() throws Exception {
        JSONObject obj = new JSONObject("{ " +
                "\"root\" : null }");
        Configuration conf = new Configuration();
        MappedNamespaceConvention con = new MappedNamespaceConvention(conf);
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertNull(reader.getElementText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }

    @Test
    public void testAttributes() throws Exception {
        JSONObject obj =
                new JSONObject("{ " +
                        "\"root\" : { " +
                        "\"@att\" : \"attvalue\"," +
                        "\"child1\" : \"child1\"" +
                        "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());

        Assertions.assertEquals(1, reader.getAttributeCount());
        Assertions.assertEquals("att", reader.getAttributeLocalName(0));
        Assertions.assertEquals("", reader.getAttributeNamespace(0));
        Assertions.assertEquals("attvalue", reader.getAttributeValue(0));

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testAttributesAsElements() throws Exception {
        JSONObject obj =
                new JSONObject("{ " +
                        "\"root\" : { " +
                        "\"@att\" : \"attvalue\"," +
                        "\"att2\" : \"attvalue\"," +
                        "\"child1\" : \"child1\"" +
                        "} }");
        List<QName> atts = new ArrayList<>();
        atts.add(new QName("att2"));
        Configuration c = new Configuration();
        c.setAttributesAsElements(atts);

        MappedNamespaceConvention con = new MappedNamespaceConvention(c);
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());

        Assertions.assertEquals(1, reader.getAttributeCount());
        Assertions.assertEquals("att", reader.getAttributeLocalName(0));
        Assertions.assertEquals("", reader.getAttributeNamespace(0));
        Assertions.assertEquals("attvalue", reader.getAttributeValue(0));

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("att2", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("attvalue", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testElementNameWithDot() throws Exception {
        JSONObject obj =
                new JSONObject("{ " +
                        "\"org.codehaus.jettison.mapped.root\" : { " +
                        "\"org.codehaus.jettison.mapped.child1\" : \"org.codehaus.jettison.mapped.child1\"" +
                        "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("org.codehaus.jettison.mapped.root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("org.codehaus.jettison.mapped.child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("org.codehaus.jettison.mapped.child1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("org.codehaus.jettison.mapped.child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("org.codehaus.jettison.mapped.root", reader.getName().getLocalPart());
    }


    @Test
    public void testNonStringObjects() throws Exception {
        JSONObject obj = new JSONObject("{\"root\":{\"foo\":true, \"foo2\":3.14, \"foo3\":17}}");

        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("foo", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("true", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("foo2", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("3.14", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("foo3", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("17", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }

    @Test
    public void testNonStringAttributes() throws Exception {
        JSONObject obj =
                new JSONObject("{ " +
                        "\"root\" : { " +
                        "\"@att\" : 1," +
                        "\"child1\" : \"child1\"" +
                        "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());

        Assertions.assertEquals(1, reader.getAttributeCount());
        Assertions.assertEquals("att", reader.getAttributeLocalName(0));
        Assertions.assertEquals("", reader.getAttributeNamespace(0));
        Assertions.assertEquals("1", reader.getAttributeValue(0));

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("child1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("child1", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
    }

    @Test
    public void testComplexElements() throws Exception {
        JSONObject obj = new JSONObject("{\"a\":{\"o\":{\"@class\":\"string\",\"$\":\"1\"}}}");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("a", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("o", reader.getName().getLocalPart());

        Assertions.assertEquals(1, reader.getAttributeCount());
        Assertions.assertEquals("class", reader.getAttributeLocalName(0));
        Assertions.assertEquals("", reader.getAttributeNamespace(0));
        Assertions.assertEquals("string", reader.getAttributeValue(0));

        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("$", reader.getName().getLocalPart());
        Assertions.assertEquals("1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals(XMLStreamReader.END_DOCUMENT, reader.next());

    }

    @Test
    public void testIgnoreComplexElements() throws Exception {
        JSONObject obj = new JSONObject("{\"a\":{\"o\":{\"@class\":\"string\",\"$\":\"1\"}}}");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        MappedXMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        reader.setValueKey(null);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("a", reader.getName().getLocalPart());

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("o", reader.getName().getLocalPart());

        Assertions.assertEquals(1, reader.getAttributeCount());
        Assertions.assertEquals("class", reader.getAttributeLocalName(0));
        Assertions.assertEquals("", reader.getAttributeNamespace(0));
        Assertions.assertEquals("string", reader.getAttributeValue(0));

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("$", reader.getName().getLocalPart());
        Assertions.assertEquals("1", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());

        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        Assertions.assertEquals(XMLStreamReader.END_DOCUMENT, reader.next());

    }

    @Test
    public void testSimple() throws Exception {
        JSONObject obj = new JSONObject("{\"root\":\"json string\"}");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        MappedXMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        Assertions.assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Assertions.assertEquals("root", reader.getName().getLocalPart());
        Assertions.assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        Assertions.assertEquals("json string", reader.getText());
        Assertions.assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
    }

    @Test
    public void testListOfObject() throws Exception {
        String json = "{\"folders\":{" +
                "\"folder\" : [" +
                "{" +
                "\"name\":\"folder1\"," +
                "\"subfolder\":" +
                "[" +
                "{\"name\":\"subfolder1\"}," +
                "{\"name\":\"subfolder2\"}" +
                "]," +
                "\"file\":" +
                "[" +
                "{\"name\":\"file1\"}," +
                "{\"name\":\"file2\"}" +
                "]" +
                "}," +
                "]" +
                "}}";
        // Expected XML
        String expXml = "<folders>" +
                "<folder>" +
                "<name>folder1</name>" +
                "<subfolder>" +
                "<name>subfolder1</name>" +
                "</subfolder>" +
                "<subfolder>" +
                "<name>subfolder2</name>" +
                "</subfolder>" +
                "<file>" +
                "<name>file1</name>" +
                "</file>" +
                "<file>" +
                "<name>file2</name>" +
                "</file>" +
                "</folder>" +
                "</folders>";

        doTestEvents(json, expXml);
    }

    private void doTestEvents(String json, String expXml) throws JSONException, XMLStreamException,
            FactoryConfigurationError {
        JSONObject obj = new JSONObject(new JSONTokener(json));
        Configuration config = new Configuration();


        ArrayList<Integer> refEvents = new ArrayList<>();

        // reference reader
        XMLStreamReader refReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(expXml));
        while (refReader.hasNext()) {
            refEvents.add(refReader.next());
        }
        refReader.close();

        // tested reader
        MappedXMLStreamReader mpdReader = new MappedXMLStreamReader(obj, new MappedNamespaceConvention(config));

        ArrayList<Integer> mpdEvents = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        while (mpdReader.hasNext()) {
            int eventType = mpdReader.next();

            mpdEvents.add(eventType);

            if (eventType == XMLStreamConstants.START_ELEMENT) {
                sb.append("<").append(mpdReader.getName()).append(">");
            } else if (eventType == XMLStreamConstants.CHARACTERS) {
                sb.append(mpdReader.getTextCharacters());
            } else if (eventType == XMLStreamConstants.END_ELEMENT) {
                sb.append("</").append(mpdReader.getName()).append(">");
            }
        }
        mpdReader.close();

        String mpdXml = sb.toString();

        Assertions.assertEquals(expXml, mpdXml);
        Assertions.assertEquals(refEvents, mpdEvents);
    }

}