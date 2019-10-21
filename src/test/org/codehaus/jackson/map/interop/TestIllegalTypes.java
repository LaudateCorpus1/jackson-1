package org.codehaus.jackson.map.interop;

import java.io.*;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.*;

/**
 * Test case(s) to guard against handling of types that are illegal to handle
 * due to security constraints.
 */
public class TestIllegalTypes extends BaseMapTest
{
    public static final String JSON = (
            "{'id': 124,\n"
                    + " 'obj':[ 'com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl',\n"
                    + "  {\n"
                    + "    'transletBytecodes' : [ 'AAIAZQ==' ],\n"
                    + "    'transletName' : 'a.b',\n"
                    + "    'outputProperties' : { }\n"
                    + "  }\n"
                    + " ]\n"
                    + "}").replace('\'','"');

    public static class Bean1599
    {
        public int id;
        public Object obj;
    }

    public static class Bean1599WithAnnotation
    {
        public int id;
        @JsonTypeInfo (use = JsonTypeInfo.Id.CLASS)
        public Object obj;
    }


    public void testIssue1599() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        try
        {
            mapper.readValue(JSON, Bean1599.class);
            fail("Should not pass");
        }
        catch (JsonMappingException e)
        {
            verifyException(e, "Illegal type");
            verifyException(e, "to deserialize");
            verifyException(e, "prevented for security reasons");
        }
    }

    public void testIssue1599WithAnnotation() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            mapper.readValue(JSON, Bean1599WithAnnotation.class);
            fail("Should not pass");
        }
        catch (JsonMappingException e)
        {
            verifyException(e, "Illegal type");
            verifyException(e, "to deserialize");
            verifyException(e, "prevented for security reasons");
        }
    }

}