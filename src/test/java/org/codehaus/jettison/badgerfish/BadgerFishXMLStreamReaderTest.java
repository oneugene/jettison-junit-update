import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.codehaus.jettison.AbstractXMLStreamReader;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.json.JSONObject;

public class BadgerFishXMLStreamReaderTest {

    @Test
    public void testRootWithText() throws Exception {
        JSONObject obj = new JSONObject("{ \"alice\": { \"$\" : \"bob\" } }");
        AbstractXMLStreamReader reader = new BadgerFishXMLStreamReader(obj);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        
        assertEquals(-1, reader.getLocation().getLineNumber());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("bob", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.END_DOCUMENT, reader.next());
    }
   
    @Test
    public void testTwoChildren() throws Exception {
        JSONObject obj = new JSONObject(
                        "{ \"alice\": { \"bob\" : { \"$\": \"charlie\" }," +
                        " \"david\": { \"$\": \"edgar\"} } }");
        AbstractXMLStreamReader reader = new BadgerFishXMLStreamReader(obj);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("bob", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("charlie", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("bob", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("david", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("edgar", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("david", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
    }
    
    @Test
    public void testTwoChildrenWithSameName() throws Exception {
        JSONObject obj = new JSONObject(
        "{ \"alice\": { \"bob\" : [ {\"$\": \"charlie\" }, {\"$\": \"david\" } ] } }");
        AbstractXMLStreamReader reader = new BadgerFishXMLStreamReader(obj);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("bob", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("charlie", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("bob", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("bob", reader.getName().getLocalPart());
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("david", reader.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("bob", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
    }

    @Test
    public void testAttributeAndText() throws Exception {
        JSONObject obj = new JSONObject(
        "{ \"alice\": { \"$\" : \"bob\", \"@charlie\" : \"david\" } }");
        AbstractXMLStreamReader reader = new BadgerFishXMLStreamReader(obj);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("bob", reader.getText());
        
        assertEquals(1, reader.getAttributeCount());
        assertEquals("charlie", reader.getAttributeLocalName(0));
        assertEquals("david", reader.getAttributeValue(0));
        assertEquals("", reader.getAttributeNamespace(0));
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
    }

    @Test
    public void testDefaultNamespace() throws Exception {
        JSONObject obj = new JSONObject(
            "{ \"alice\": { \"$\" : \"bob\", \"@xmlns\": { \"$\" : \"http:\\/\\/some-namespace\"} } }");
        AbstractXMLStreamReader reader = new BadgerFishXMLStreamReader(obj);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("bob", reader.getText());
        
        assertEquals(0, reader.getAttributeCount());
        assertEquals(1, reader.getNamespaceCount());
        assertEquals("http://some-namespace", reader.getNamespaceURI(0));
        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("http://some-namespace", reader.getNamespaceURI(""));
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
    }
    
    @Test
    public void testPrefixedNamespace() throws Exception {
        JSONObject obj = new JSONObject(
            "{ \"alice\": { \"$\" : \"bob\", \"@xmlns\": { \"$\" : \"http:\\/\\/some-namespace\", \"charlie\" : \"http:\\/\\/some-other-namespace\" } } }");
        AbstractXMLStreamReader reader = new BadgerFishXMLStreamReader(obj);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("bob", reader.getText());
        
        assertEquals(0, reader.getAttributeCount());
        assertEquals(2, reader.getNamespaceCount());
        
        // namespaces are ordered differently on different platforms/jvms, 
        // so we can't really test the order
//        assertEquals("http://some-namespace", reader.getNamespaceURI(0));
//        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("http://some-namespace", reader.getNamespaceURI(""));
       
//        assertEquals("http://some-other-namespace", reader.getNamespaceURI(1));
//        assertEquals("charlie", reader.getNamespacePrefix(1));
        assertEquals("http://some-other-namespace", reader.getNamespaceURI("charlie"));
         
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
    }
    

    
    @Test
    public void testPrefixedElements() throws Exception {
        JSONObject obj = new JSONObject(
            "{ \"alice\" : { " +
              "\"bob\" : { " +
                "\"$\" : \"david\" , " +
                "\"@xmlns\" : {" +
                  "\"charlie\" : \"http:\\/\\/some-other-namespace\" , " +
                  "\"$\" : \"http:\\/\\/some-namespace\"} " +
                "} , " +
                "\"charlie:edgar\" : { " +
                  "\"$\" : \"frank\" , " +
                  "\"@xmlns\" : {" +
                    "\"charlie\":\"http:\\/\\/some-other-namespace\", " +
                    "\"$\" : \"http:\\/\\/some-namespace\"" +
                  "} " +
                "}, " +
                "\"@xmlns\" : { " +
                  "\"charlie\" : \"http:\\/\\/some-other-namespace\", " +
                  "\"$\" : \"http:\\/\\/some-namespace\"" +
                "} " +
              "} }");
        AbstractXMLStreamReader reader = new BadgerFishXMLStreamReader(obj);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("bob", reader.getLocalName());
        
        assertEquals(0, reader.getAttributeCount());
        assertEquals(2, reader.getNamespaceCount());
        
//        assertEquals("http://some-namespace", reader.getNamespaceURI(0));
//        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("http://some-namespace", reader.getNamespaceURI(""));
        
//        assertEquals("http://some-other-namespace", reader.getNamespaceURI(1));
//        assertEquals("charlie", reader.getNamespacePrefix(1));
        assertEquals("http://some-other-namespace", reader.getNamespaceURI("charlie"));
       
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("david", reader.getText());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("bob", reader.getName().getLocalPart());
        
        // ----
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("edgar", reader.getLocalName());
        assertEquals("charlie", reader.getPrefix());
        assertEquals("http://some-other-namespace", reader.getNamespaceURI());
        
        assertEquals(0, reader.getAttributeCount());
        assertEquals(2, reader.getNamespaceCount());
        
//        assertEquals("http://some-namespace", reader.getNamespaceURI(0));
//        assertEquals("", reader.getNamespacePrefix(0));
        assertEquals("http://some-namespace", reader.getNamespaceURI(""));
        
//        assertEquals("http://some-other-namespace", reader.getNamespaceURI(1));
//        assertEquals("charlie", reader.getNamespacePrefix(1));
        assertEquals("http://some-other-namespace", reader.getNamespaceURI("charlie"));
       
        
        assertEquals(XMLStreamReader.CHARACTERS, reader.next());
        assertEquals("frank", reader.getText());
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("edgar", reader.getName().getLocalPart());
        
        
        assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
    }

    @Test
    public void testElementWithInvalidValue() throws Exception {
        JSONObject obj = new JSONObject("{ \"alice\": { \"foo\" : \"bob\" } }");
        AbstractXMLStreamReader reader = new BadgerFishXMLStreamReader(obj);
        
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("alice", reader.getName().getLocalPart());
        
        assertThrows(XMLStreamException.class, () -> {
        	reader.next();
        });
    }
}