package org.codehaus.jackson.map.interop;

import java.io.*;

import org.codehaus.jackson.map.*;

/**
 * Test case(s) to guard against handling of types that are illegal to handle
 * due to security constraints.
 */
public class TestIllegalTypes extends BaseMapTest
{
    static class Bean1599
    {
        public int id;
        public Object obj;
    }

    public void testIssue1599() throws Exception
    {
        final String JSON = (
                "{'id': 124,\n"
                        + " 'obj':[ 'com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl',\n"
                        + "  {\n"
                        + "    'transletBytecodes' : [ 'AAIAZQ==' ],\n"
                        + "    'transletName' : 'a.b',\n"
                        + "    'outputProperties' : { }\n"
                        + "  }\n"
                        + " ]\n"
                        + "}").replace('\'','"');

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
}