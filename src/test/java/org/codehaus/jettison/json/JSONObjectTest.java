package org.codehaus.jettison.json;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONObjectTest {

    @Test
    public void testEquals() throws Exception {
        JSONObject aJsonObj = new JSONObject("{\"x\":\"y\"}");
        JSONObject bJsonObj = new JSONObject("{\"x\":\"y\"}");
        Assertions.assertEquals(aJsonObj,bJsonObj);
    }
    
    @Test
    public void testToLong() throws Exception {
        String json = "{\"key\":\"10001325703114005\"}";
        JSONObject jsonObject = new JSONObject(json);
        long actual = jsonObject.getLong("key");
        long expected = 10001325703114005L;
        Assertions.assertTrue(expected < Long.MAX_VALUE);
        Assertions.assertEquals(expected, actual);
    }
    
    @Test
    public void testNotEquals() throws Exception {
        JSONObject aJsonObj = new JSONObject("{\"x\":\"y\"}");
        JSONObject bJsonObj = new JSONObject("{\"x\":\"b\"}");
        Assertions.assertTrue(!aJsonObj.equals(bJsonObj));
    }
    
    @Test
    public void testAppend() throws Exception {
        JSONObject obj = new JSONObject();
        obj.append("arr", "val1");
        obj.append("arr", "val2");
        obj.append("arr", "val3");
        Assertions.assertEquals("{\"arr\":[\"val1\",\"val2\",\"val3\"]}", obj.toString());
    }
    
    @Test
    public void testInvalidArraySequence() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{\"a\":[");
        });
    }
    
    @Test
    public void testInvalidArraySequence2() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{\"a\":[,");
        });
    }
    
    @Test
    public void testInvalidArraySequence3() {
        Assertions.assertThrows(JSONException.class, () -> {
            String corruptedJSON = "{\"a\":[[\"b\",{\"revision\": 760839}],";
            new JSONObject(corruptedJSON);
        });
    }
    
    @Test
    public void testNullInQuotesGetString() throws Exception {
        JSONObject obj = new JSONObject("{\"a\":\"null\"}");
        Assertions.assertEquals("null", obj.getString("a"));
    }
    
    @Test
    public void testExplicitNullGetString() throws Exception {
        JSONObject obj = new JSONObject("{\"a\":null}");
        Assertions.assertNull(obj.getString("a"));
    }
    @Test
    public void testExplicitNullIsNull() throws Exception {
        JSONObject obj = new JSONObject("{\"a\":null}");
        Assertions.assertTrue(obj.isNull("a"));
    }
    @Test
    public void testMissingIsNull() throws Exception {
        JSONObject obj = new JSONObject("{\"a\":null}");
        Assertions.assertTrue(obj.isNull("b"));
    }
    
    @Test
    public void testSlashEscapingTurnedOnByDefault() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("key", "http://example.com/foo");
        Assertions.assertEquals("{\"key\":\"http:\\/\\/example.com\\/foo\"}", obj.toString());
        
        obj = new JSONObject();
        obj.put("key", "\\\\");
        Assertions.assertEquals("{\"key\":\"\\\\\\\\\"}", obj.toString());
    }
    
    @Test
    public void testForwardSlashEscapingModifiedBySetter() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("key", "http://example.com/foo");
        Assertions.assertEquals("{\"key\":\"http:\\/\\/example.com\\/foo\"}", obj.toString());
        obj.setEscapeForwardSlashAlways(false);
        Assertions.assertEquals("{\"key\":\"http://example.com/foo\"}", obj.toString());
        obj.setEscapeForwardSlashAlways(true);
        Assertions.assertEquals("{\"key\":\"http:\\/\\/example.com\\/foo\"}", obj.toString());
    }

    @Test
    public void testMalformedObject() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{/");
        });
    }

    @Test
    public void testMalformedObject2() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{x");
        });
    }

    @Test
    public void testMalformedObject3() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{/x");
        });
    }

    @Test
    public void testMalformedObject4() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{/*");
        });
    }

    @Test
    public void testMalformedObject5() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{//");
        });
    }

    @Test
    public void testMalformedArray() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{[/");
        });
    }

    @Test
    public void testIssue52() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("t",map);
        new JSONObject(map);
    }

    @Test
    public void testIssue52Recursive() {
        Assertions.assertThrows(JSONException.class, () -> {
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> map2 = new HashMap<>();
            map.put("t", map2);
            map2.put("t", map);
            new JSONObject(map);
        });
    }

    @Test
    public void testFuzzerTestCase() {
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{\"G\":[30018084,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,38,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,0]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,340282366920938463463374607431768211458,6,1,1]}:[32768,1,1,6,1,0]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,340282366920938463463374607431768211458,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,9 68,1,127,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,6,32768,1,1,6,1,9223372036854775807]}:[3,1,6,32768,1,1,6,1,1]}:[3,1,10,32768,1,1,6,1,1]}");
        });
    }

    @Test
    public void testFuzzerTestCase2() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append("{\"key\":");
        }
        Assertions.assertThrows(JSONException.class, () -> {
            new JSONObject(sb.toString());
        });
    }

    @Test
    public void testIssue58() throws JSONException {
        Map<String, Object> map = new HashMap<>();
        map.put("request", "{\"exclude\":[\".\",\"?\",\"+\",\"*\",\"|\",\"{\",\"}\",\"[\",\"]\",\"(\",\")\",\"\\\"\",\"\\\\\",\"#\",\"@\",\"&\",\"<\",\">\",\"~\"]}");
        JSONObject jsonObject = new JSONObject(map);
        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
    }
}