package com.github.seqware.queryengine.system.rest.resources;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class AboutResourceTest {
  public static final String WEBSERVICE_URL = "http://localhost:8889/seqware-queryengine-webservice/api/";

  public AboutResourceTest() {
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
   * Test of backendRequest method, of class AboutResource.
   */
  @Test
  public void testBackendRequest() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "about/debug");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed:" + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    // Could change to adhere to json schema
    Assert.assertTrue("Returned entity incorrect: " + output, output.contains("backend") && output.contains("modelManager"));
  }

  /**
   * Test of versionRequest method, of class AboutResource.
   */
  @Test
  public void testVersionRequest() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "about/versions");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed:" + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    Assert.assertTrue("Returned entity incorrect: " + output, output=="");
  }
}
