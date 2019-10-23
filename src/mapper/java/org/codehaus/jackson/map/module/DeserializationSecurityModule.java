package org.codehaus.jackson.map.module;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.deser.SecurityBeanDeserializer;


public class DeserializationSecurityModule extends Module
{

    @Override
    public String getModuleName()
    {
        return this.getClass().getName();
    }

    @Override
    public Version version()
    {
        return new Version(1, 9, 13, "");
    }

    @Override
    public void setupModule(final SetupContext context)
    {
        context.addDeserializers(new SecurityBeanDeserializer());
    }
}
