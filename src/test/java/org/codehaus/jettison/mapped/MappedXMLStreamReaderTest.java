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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MappedXMLStreamReaderTest {
    @Test
    public void testStreamReader() throws Exception {
        JSONObject obj = 
            new JSONObject("{ " +
                           "\"root\" : { " +
                           "\"child1\" : \"child1\"" +
                           "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("child1", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart()); 
    }
    
    @Test
    public void testStreamReaderNullTextAsString() throws Exception {
        JSONObject obj = 
            new JSONObject("{ " +
                           "\"root\" : { " +
                           "\"child1\" : \"null\"" +
                           "} }");
        Configuration c = new Configuration();
        c.setReadNullAsString(true);
        MappedNamespaceConvention con = new MappedNamespaceConvention(c);
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("null", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart()); 
    }
    
    @Test
    public void testStreamReaderNullTextAsNull() throws Exception {
        JSONObject obj = 
            new JSONObject("{ " +
                           "\"root\" : { " +
                           "\"child1\" : \"null\"" +
                           "} }");
        Configuration c = new Configuration();
        MappedNamespaceConvention con = new MappedNamespaceConvention(c);
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals(null, reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart()); 
    }

    @Test
    public void testMultipleChildren() throws Exception {
        JSONObject obj = 
            new JSONObject("{ " +
                           "\"root\" : { " +
                           "\"child1\" : \"child1\"," +
                           "\"child2\" : \"child2\"" +
                           "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("child1", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child2", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("child2", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child2", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart()); 
    }
    
    @Test
    public void testMultipleChildrenWithSameName() throws Exception {
        JSONObject obj = 
            new JSONObject("{ " +
                           "\"root\" : { " +
                           "\"child1\" : \"child1\"," +
                           "\"child1\" : \"child11\"" +
                           "} }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("child1", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("child11", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart()); 
    }
    
    @Test
    public void testNestedArrayOfChildren() throws Exception {
        JSONObject obj = 
            new JSONObject("{" +
            		"\"root\":" +
            				"{\"child1\":" +
            					"[{\"subchild2\":" +
            						"[\"first sub2\",\"second sub2\",\"third sub2\"]}" +
            					",\"sub1\",\"sub2\"]" +
            				",\"child2\":\"child2\"}" +
            		"}");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("subchild2", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("first sub2", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("subchild2", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("subchild2", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("second sub2", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("subchild2", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("subchild2", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("third sub2", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("subchild2", reader.getName().getLocalPart());
              
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
                  
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("sub1", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("sub2", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child2", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("child2", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child2", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
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
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals("http://foo/", reader.getName().getNamespaceURI());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals("http://foo/", reader.getName().getNamespaceURI());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("childtext", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals("http://foo/", reader.getName().getNamespaceURI());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart()); 
        assertEquals("http://foo/", reader.getName().getNamespaceURI());
    }
    
    @Test
    public void testDefaultNamespace() throws Exception {
        JSONObject obj = new JSONObject("{ " + "\"root\" : { "
                + "\"child1\" : \"childtext\"," + "} }");

        Map xtoj = new HashMap();
        xtoj.put("http://foo/", "");
        MappedNamespaceConvention con = new MappedNamespaceConvention(
                new Configuration(xtoj));
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);

        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals("http://foo/", reader.getName().getNamespaceURI());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals("http://foo/", reader.getName().getNamespaceURI());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("childtext", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("child1", reader.getName().getLocalPart());
        assertEquals("http://foo/", reader.getName().getNamespaceURI());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("root", reader.getName().getLocalPart());
        assertEquals("http://foo/", reader.getName().getNamespaceURI());
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
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Names", reader.getName().getLocalPart());
                
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("1", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("2", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Names", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
    }
    
    @Test
    public void testArrayWithSameJSONObjects2() throws Exception {
        String str = "{\"theBook\":[{\"Name\":\"1\"}, {\"Name\":\"2\"}]}";
        Configuration config = new Configuration();
        config.setPrimitiveArrayKeys(Collections.singleton("theBook"));
        MappedXMLInputFactory factory = new MappedXMLInputFactory(config);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(str));
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
                
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("1", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("2", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
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
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Names", reader.getName().getLocalPart());
                
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("value", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("11", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("value", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("value", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("12", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("value", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("value", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("21", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("value", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("value", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("22", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("value", reader.getName().getLocalPart());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Names", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
    }
    
    @Test
    public void testExceptionLocation() throws Exception {
        String str = "{\"junk";
        Configuration config = new Configuration();
        MappedXMLInputFactory factory = new MappedXMLInputFactory(config);
        assertThrows(XMLStreamException.class, () -> {
            factory.createXMLStreamReader(new StringReader(str));
        });
    }
    
    @Test
    public void testArrayWithNotSameJSONObjects() throws Exception {
        String str = "{\"theBook\":[{\"Name\":\"1\"}, {\"Bar\":\"2\"}]}";
        Configuration config = new Configuration();
        config.setPrimitiveArrayKeys(Collections.singleton("theBook"));
        MappedXMLInputFactory factory = new MappedXMLInputFactory(config);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(str));
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
                
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("1", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Name", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("theBook", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Bar", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("2", reader.getText());

        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("Bar", reader.getName().getLocalPart());
        
       