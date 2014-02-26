package com.github.seqware.queryengine.system.rest.resources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.util.SeqWareIterable;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ReferenceResourceTest {
  public static final String WEBSERVICE_URL = "http://localhost:8889/seqware-queryengine-webservice/api/";
  public static String setKey;
  public static String elementKey;
  
  public ReferenceResourceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    //Create a Test ReferenceSet
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset" );
    String referenceSet = "{"
        + "\"organism\": \"TestOraganism\","
        + "\"name\": \"TestReferenceSet\""
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, referenceSet);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    setKey = extractRowKey(output);
    
    //Create a test Reference
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "referenceset/" + setKey);
    String reference = "{"
        + "\"name\": \"TestReference\""
        + "}";
    ClientResponse response2 = webResource2.type("application/json").post(ClientResponse.class, reference);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
    String output2 = response2.getEntity(String.class);
    elementKey = extractRowKey(output2);
    client.destroy();
  }
  
  @AfterClass
  public static void tearDownClass() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset/" + setKey);
    webResource.delete();
    client.destroy();
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of getClassName method, of class ReferenceResource.
   */
  @Test
  public void testGetClassName() {
    ReferenceResource instance = new ReferenceResource();
    String expResult = "Reference";
    String result = instance.getClassName();
    Assert.assertEquals(expResult, result);
  }

  /**
   * Test of getModelClass method, of class ReferenceResource.
   */
  @Test
  public void testGetModelClass() {
    ReferenceResource instance = new ReferenceResource();
    Class expResult = Reference.class;
    Class result = instance.getModelClass();
    Assert.assertEquals(expResult, result);
  }
  
  @Test
  public void testGetReferences() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "reference" );
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testGetReference() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "reference/" + elementKey );
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testPutReference() {
    //Create Reference
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset/" + setKey);
    String reference = "{"
        + "\"name\": \"TestPutReference\""
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, reference);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String referenceKey = extractRowKey(response.getEntity(String.class));
    
    //Update Reference
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "reference/" + referenceKey );
    String put = "{"
        + "\"name\": \"ChangedReference\""
        + "}";
    ClientResponse response2 = webResource2.type("application/json").put(ClientResponse.class, put);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
    
    //Destroy Reference
    webResource2.delete();
    ClientResponse response3 = webResource2.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output3 = response3.getEntity(String.class);
    Assert.assertTrue("Could not delete entity:" + response.getStatus(), !output3.contains(referenceKey));
    client.destroy();
  }
  
  @Test
  public void testGetTags() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "reference/" + elementKey + "/tags");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  
  @Test
  public void testGetVersion() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "reference/" + elementKey + "/version");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    client.destroy();
  }
  
  @Test
  public void testGetPermissions() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "reference/" + elementKey + "/permissions");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    client.destroy();
  }
  
  protected static String extractRowKey(String output) {
    Pattern pattern = Pattern.compile("rowKey\":\"(.*?)\"");
    Matcher matcher = pattern.matcher(output);
    matcher.find();
    String rowkey = matcher.group(1);
    return rowkey;
  }

}
