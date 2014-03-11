package com.github.seqware.queryengine.system.rest.resources;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.github.seqware.queryengine.model.Plugin;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class PluginResourceTest {
  
  public PluginResourceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of getClassName method, of class PluginResource.
   */
  @Test
  public void testGetClassName() {
    PluginResource instance = new PluginResource();
    String expResult = "Plugin";
    String result = instance.getClassName();
    assertEquals(expResult, result);
  }

  /**
   * Test of getModelClass method, of class PluginResource.
   */
  @Test
  public void testGetModelClass() {
    PluginResource instance = new PluginResource();
    Class expResult = Plugin.class;
    Class result = instance.getModelClass();
    assertEquals(expResult, result);
  }
  
  //GET plugin
  @Test
  public void testGetPlugins() {
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "plugin");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  protected static String extractRowKey(String output) {
    // now create a Tag using the returned rowkey
    // grab rowkey via regular expression
    Pattern pattern = Pattern.compile("rowKey\":\"(.*?)\"");
    Matcher matcher = pattern.matcher(output);
    matcher.find();
    String rowkey = matcher.group(1);
    return rowkey;
  }
}
