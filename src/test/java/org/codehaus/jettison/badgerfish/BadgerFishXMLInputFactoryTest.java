package org.codehaus.jettison.badgerfish;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.codehaus.jettison.badgerfish.BadgerFishXMLInputFactory;

public class BadgerFishXMLInputFactoryTest {
   
    private static final String XML_JSON =
        "{\"d:root\":{\"child\":{\"@xsi:type\":\"d:ChildType\",\"name\":{\"$\":\"Dummy\"}},\"@xmlns\":{\"d\":\"http:\\/\\/www.example.com\\/dummy\",\"xsi\":\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema-instance\"}}}";

    @Test
    public void testRoundTrip() throws Exception {

        BadgerFishXMLInputFactory xif = new BadgerFishXMLInputFactory();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(XML_JSON));
        while (reader.hasNext()) {
            reader.next();
        }
    }
}