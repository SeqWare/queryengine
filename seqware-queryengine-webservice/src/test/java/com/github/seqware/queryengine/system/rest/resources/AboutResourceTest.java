package com.github.seqware.queryengine.system.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ws.rs.core.Response;

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
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset");
    String group = "{\n"
        + "  \"name\": \"Funky name\",\n"
        + "  \"organism\": \"Funky organism\"\n"
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, group);
    Assert.assertTrue("Request failed:" + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    Assert.assertTrue("Returned entity incorrect" + output, output.contains("Funky name") && output.contains("Funky organism"));
    
    AboutResource instance = new AboutResource();
    Response expResult = null;
    Response result = instance.backendRequest();
    assertEquals(expResult, result);
  }

  /**
   * Test of versionRequest method, of class AboutResource.
   */
  @Test
  public void testVersionRequest() {
    AboutResource instance = new AboutResource();
    Response expResult = null;
    Response result = instance.versionRequest();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
}
