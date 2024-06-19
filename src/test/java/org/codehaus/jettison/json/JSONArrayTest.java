package org.codehaus.jettison.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class JSONArrayTest {
    @Test
    public void testInvalidArraySequence() {
        assertThrows(JSONException.class, () -> new JSONArray("[32,"));
    }
    
    @Test
    public void testInvalidArraySequence2() {
        assertThrows(JSONException.class, () -> new JSONArray("[32,34"));
    }
    
    @Test
    public void testEscapingInArrayIsOnByDefault() {
      JSONArray array = new JSONArray();
      array.put("a string with / character");
      String expectedValue = "[\"a string with \\/ character\"]";
      assertEquals(expectedValue, array.toString());
    }
    
    @Test
    public void testEscapingInArrayIsTrunedOff() throws JSONException {
   
      JSONObject obj = new JSONObject();
      obj.put("key", "http://example.com/foo");
      obj.setEscapeForwardSlashAlways(false);

      JSONArray array = new JSONArray();
      array.put("a string with / character");
      array.put(obj);
      array.setEscapeForwardSlashAlways(false);
      
      String expectedValue = "[\"a string with / character\",{\"key\":\"http://example.com/foo\"}]";
      assertEquals(expectedValue, array.toString());
    }

    @Test
    public void testInfiniteLoop() {
        assertThrows(JSONException.class, () -> new JSONArray("[*/*A25] **"));
    }

    @Test
    public void testInfiniteLoop2() {
        assertThrows(JSONException.class, () -> new JSONArray("[/"));
    }

    @Test
    public void testIssue52() throws JSONException {
        JSONObject.setGlobalRecursionDepthLimit(10);
        assertThrows(JSONException.class, () -> new JSONArray("[{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {a:10}]"));
        JSONObject.setGlobalRecursionDepthLimit(500);
    }

    @Test
    public void testIssue60() {
        List<Object> list = new ArrayList<>();
        list.add(list);
        JSONException ex = assertThrows(JSONException.class, () -> new JSONArray(list));
        assertEquals("JSONArray has reached recursion depth limit of 500", ex.getMessage());
    }

}