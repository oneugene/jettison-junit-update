/**
 * Copyright 2006 Envoi Solutions LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;

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
    // The remaining methods from the original class have been omitted for brevity.
}
```