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
    public static final String JSON = aposToQuotes(
            "{'id': 124,\n"
                    + " 'obj':[ 'com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl',\n"
                    + "  {\n"
                    + "    'transletBytecodes' : [ 'AAIAZQ==' ],\n"
                    + "    'transletName' : 'a.b',\n"
                    + "    'outputProperties' : { }\n"
                    + "  }\n"
                    + " ]\n"
                    + "}");

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

    static class PolyWrapper
    {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
                include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object v;
    }

    public void testIssue1599() throws Exception
    {
        final String clsName = "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        try
        {
            mapper.readValue(JSON, Bean1599.class);
            fail("Should not pass");
        }
        catch (JsonMappingException e)
        {
            _verifySecurityException(e, clsName);
        }
    }

    public void testIssue1599WithAnnotation() throws Exception
    {
        final String clsName = "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            mapper.readValue(JSON, Bean1599WithAnnotation.class);
            fail("Should not pass");
        }
        catch (JsonMappingException e)
        {
            _verifySecurityException(e, clsName);
        }
    }

    // // // Tests for [databind#1855]
    public void testJDKTypes1855() throws Exception
    {
        // apparently included by JDK?
        _testIllegalType("com.sun.org.apache.bcel.internal.util.ClassLoader");
    }

    // [databind#1931]
    public void testC3P0Types() throws Exception
    {
        _testIllegalType("com.mchange.v2.c3p0.ComboPooledDataSource"); // [databind#1931]
        _testIllegalType("com.mchange.v2.c3p0.debug.AfterCloseLoggingComboPooledDataSource");
    }

    private void _testIllegalType(Class<?> nasty) throws Exception {
        _testIllegalType(nasty.getName());
    }

    private void _testIllegalType(String clsName) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        // While usually exploited via default typing let's not require
        // it here; mechanism still the same
        String json = aposToQuotes(
                "{'v':['"+clsName+"','/tmp/foobar.txt']}"
        );
        try {
            mapper.readValue(json, PolyWrapper.class);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            _verifySecurityException(e, clsName);
        }
    }

    protected void _verifySecurityException(Throwable t, String clsName) throws Exception
    {
        // 17-Aug-2017, tatu: Expected type more granular in 2.9 (over 2.8)
        _verifyException(t, JsonMappingException.class,
                "Illegal type",
                "to deserialize",
                "prevented for security reasons");
        verifyException(t, clsName);
    }

    protected void _verifyException(Throwable t, Class<?> expExcType,
                                    String... patterns) throws Exception
    {
        Class<?> actExc = t.getClass();
        if (!expExcType.isAssignableFrom(actExc)) {
            fail("Expected Exception of type '"+expExcType.getName()+"', got '"
                    +actExc.getName()+"', message: "+t.getMessage());
        }
        for (String pattern : patterns) {
            verifyException(t, pattern);
        }
    }
}
