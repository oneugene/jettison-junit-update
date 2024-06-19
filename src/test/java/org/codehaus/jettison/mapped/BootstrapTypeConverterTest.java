import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BootstrapTypeConverterTest {

    @Test
    public void testBootstrapConverter() throws Exception {
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
        assertEquals(expected, actual);
    }

    public static class ReplacementTypeConverter implements TypeConverter
    {
        public Object convertToJSONPrimitive( String text )
        {
            return "success";
        }
    }

}