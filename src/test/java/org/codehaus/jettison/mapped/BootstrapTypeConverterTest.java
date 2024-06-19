package org.codehaus.jettison.mapped;

import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.StringWriter;

class BootstrapTypeConverterTest {

    @Test
    void testBootstrapConverter() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setTypeConverter(new ReplacementTypeConverter());
        StringWriter strWriter = new StringWriter();
        MappedNamespaceConvention con = new MappedNamespaceConvention(cfg);
        AbstractXMLStreamWriter w = new MappedXMLStreamWriter(con, strWriter);
        w.writeStartDocument();
        w.writeStartElement("root");

        w.writeStartElement("subchild1");
        w.writeCharacters("Not success");
        w.writeEndElement();

        w.writeEndElement();
        w.writeEndDocument();

        w.close();
        strWriter.close();
        String expected = "{\"root\":{\"subchild1\":\"success\"}}";
        String actual = strWriter.toString();
        Assertions.assertEquals(expected, actual);
    }

    static class ReplacementTypeConverter implements TypeConverter {
        public Object convertToJSONPrimitive(String text) {
            return "success";
        }
    }

}