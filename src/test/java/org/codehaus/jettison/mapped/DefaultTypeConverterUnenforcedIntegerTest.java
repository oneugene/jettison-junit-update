package org.codehaus.jettison.mapped;

import org.codehaus.jettison.AbstractXMLStreamWriter;

import java.io.StringWriter;

public class DefaultTypeConverterUnenforcedIntegerTest {

    /**
     * issue 61
     * @see DefaultTypeConverterEnforcedIntegerTest
     */
    public void testPrimitiveUnenforcedInteger() throws Exception {
        assertFalse( DefaultConverter.ENFORCE_32BIT_INTEGER );

        StringWriter strWriter = new StringWriter();
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        AbstractXMLStreamWriter w = new MappedXMLStreamWriter(con, strWriter);

        w.writeStartDocument();
        w.writeStartElement("root");


        w.writeStartElement("subchild1");
        w.writeCharacters(Long.MAX_VALUE + "");
        w.writeEndElement();

        w.writeStartElement("subchild1");
        w.writeCharacters(Long.MIN_VALUE + "");
        w.writeEndElement();


        w.writeEndElement();
        w.writeEndDocument();

        w.close();
        strWriter.close();
        String expected = "{\"root\":{\"subchild1\":[" + Long.MAX_VALUE + "," + Long.MIN_VALUE + "]}}";
        String actual = strWriter.toString();
        assertEquals(expected, actual);
    }

}