package com.github.seqware.queryengine.system.rest.resources;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        com.github.seqware.queryengine.system.rest.resources.AboutResourceTest.class,
        com.github.seqware.queryengine.system.rest.resources.TagResourceTest.class,
        com.github.seqware.queryengine.system.rest.resources.TagSetResourceTest.class,
        com.github.seqware.queryengine.system.rest.resources.PluginResourceTest.class,
        com.github.seqware.queryengine.system.rest.resources.GroupResourceTest.class,
        com.github.seqware.queryengine.system.rest.resources.UserResourceTest.class
})

public class QEWSResourceTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
}

