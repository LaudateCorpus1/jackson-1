package org.codehaus.jackson.map.deser;


import main.BaseTest;
import org.apache.xalan.transformer.XalanProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.interop.TestIllegalTypes;
import org.codehaus.jackson.map.module.DeserializationSecurityModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test case(s) to guard against handling of types that are overly general to handle
 * due to security constraints.
 */
public class TestDeserializationSecurityModule extends BaseTest
{
    ObjectMapper mapper;

    private static final String EXPECTED_ERROR_MESSAGE = "Prevented for security reasons";

    static class BeanWithMap
    {
        public Map map = new HashMap<>();
    }

    static class BeanWithMapWithType
    {
        public Map <String, String> map = new HashMap<String, String>();
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        mapper = new ObjectMapper();
        mapper.withModule(new DeserializationSecurityModule());
    }

    public void testBlocksObjectDeserialization() throws IOException
    {
        try
        {
            mapper.enableDefaultTyping();
            mapper.readValue(TestIllegalTypes.JSON, TestIllegalTypes.Bean1599.class);
            fail();
        }
        catch (JsonMappingException e) {
            verifyException(e, EXPECTED_ERROR_MESSAGE);
        }
    }

    public void testBlocksObjectDeserializationUsingAnnotation() throws IOException
    {
        try
        {
            mapper.readValue(TestIllegalTypes.JSON, TestIllegalTypes.Bean1599WithAnnotation.class);
            fail();
        }
        catch (JsonMappingException e) {
            verifyException(e, EXPECTED_ERROR_MESSAGE);
        }
    }

    public void testBlocksMapObjectDeserialization() throws IOException
    {
        mapper.enableDefaultTyping();
        BeanWithMap bean = new BeanWithMap();
        bean.map.put("example", Exception.class);
        String JSON = new ObjectMapper().enableDefaultTyping().writeValueAsString(
                bean);
        try
        {
            mapper.readValue(JSON, BeanWithMap.class);
            fail();
        }
        catch (JsonMappingException e)
        {
            verifyException(e, EXPECTED_ERROR_MESSAGE);
        }
    }

    public void testDoesNotBlockMapWithTypeseDeserialization() throws IOException
    {
        mapper.enableDefaultTyping();
        BeanWithMapWithType bean = new BeanWithMapWithType();
        bean.map.put("example", "eg");
        String JSON = new ObjectMapper().enableDefaultTyping().writeValueAsString(
                bean);
        mapper.readValue(JSON, BeanWithMapWithType.class);
    }

}
