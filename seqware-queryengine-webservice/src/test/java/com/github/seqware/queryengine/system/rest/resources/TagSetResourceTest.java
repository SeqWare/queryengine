package com.github.seqware.queryengine.system.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class TagSetResourceTest {
  public static final String WEBSERVICE_URL = "http://localhost:8889/seqware-queryengine-webservice/api/";

  public TagSetResourceTest() {
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

  @Test
  public void testGetClassName() {
    TagResource instance = new TagResource();
    String expResult = "TagSet";
    String result = instance.getClassName();
    assertEquals(expResult, result);
  }

  /**
   * Test of getModelClass method, of class TagSetResource.
   */
  @Test
  public void testGetModelClass() {
    TagResource instance = new TagResource();
    Class expResult = TagSet.class;
    Class result = instance.getModelClass();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetElements() {
    
  }
  /**
   * Test of getElements method, of class TagSetResource.
   */
  @Test
  public void testFeatureByIDRequest() {
    TagResource instance = new TagResource();
    SeqWareIterable expResult = null;
    SeqWareIterable result = instance.getElements();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
  @Test
  public void testAddSet() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tagset");
    String group = "{\n"
            + "  \"name\": \"Funky TagSet\",\n"
            + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, group);
    Assert.assertTrue("Request failed:" + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    Assert.assertTrue("Returned entity incorrect" + output, output.contains("Funky name") && output.contains("Funky TagSet"));
  }
}
