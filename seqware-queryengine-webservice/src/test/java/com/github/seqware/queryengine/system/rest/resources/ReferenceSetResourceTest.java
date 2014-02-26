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

import com.github.seqware.queryengine.model.ReferenceSet;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ReferenceSetResourceTest {
  public static final String WEBSERVICE_URL = "http://localhost:8889/seqware-queryengine-webservice/api/";
  public static String setKey;
  
  public ReferenceSetResourceTest() {
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

  @Test
  public void testGetClassName() {
    ReferenceSetResource instance = new ReferenceSetResource();
    String expResult = "ReferenceSet";
    String result = instance.getClassName();
    assertEquals(expResult, result);
  }

  /**
   * Test of getModelClass method, of class ReferenceSetResource.
   */
  @Test
  public void testGetModelClass() {
    ReferenceSetResource instance = new ReferenceSetResource();
    Class expResult = ReferenceSet.class;
    Class result = instance.getModelClass();
    assertEquals(expResult, result);
  }
  
  @Test
  public void testGetReferenceSets() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  
  @Test
  public void testGetReferenceSet() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset/" + setKey);
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testPutReferenceSet() {
    //Create a new ReferenceSet
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset");
    String referenceSet = "{\n"
            + "  \"organism\": \"TestPutOrganism\",\n"
            + "  \"name\": \"TestReference\"\n"
            + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, referenceSet);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    String rowkey = extractRowKey(output);
    Assert.assertTrue("Returned entity incorrect" + output, output.contains(rowkey) && output.contains("TestPutOrganism"));
    
    //Update the ReferenceSet
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "referenceset/" + rowkey);
    String put = "{\n"
            + "  \"organism\": \"ChangedOrganism\",\n"
            + "  \"name\": \"ChangedReference\"\n"
            + "}"; 
    ClientResponse response2 = webResource2.type("application/json").put(ClientResponse.class, put);
    Assert.assertTrue("Put failed:" + response.getStatus(), response2.getStatus() == 200);
    String output2 =  response2.getEntity(String.class);
    Assert.assertTrue("Output does not contain the PUT update: " + output2, output2.contains(rowkey) && output2.contains("ChangedOrganism"));
    
    //Delete the ReferenceSet
    webResource2.delete();
    //ClientResponse response3 = webResource.type("application/json").get(ClientResponse.class);
    //Assert.assertTrue("Request failed: " + response3.getStatus(), response3.getStatus() == 200);
    //String output3 = response3.getEntity(String.class);
    //Assert.assertTrue("Could not delete entity:" + response.getStatus(), !output3.contains(rowkey));
    client.destroy();
  }
  
  @Test
  public void testGetTags() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset/" + setKey + "/tags");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testGetPermissions() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset/" + setKey + "/permissions");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  /*
  @Test
  public void testPutPermissions() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset/" + setKey + "/permissions");
    String permissions = "{\n"
            + ",\n" 
            + ",\n"
            + "\n"
            + "}"
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
  }
  */
  @Test
  public void testGetVersion() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "referenceset/" + setKey + "/version");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    String version = extractVersion(output);
    Assert.assertTrue("Invalid Version returned: " + version, Integer.parseInt(version)==1);
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
  
  protected static String extractVersion(String output) {
    // grab version via regular expression
    Pattern pattern = Pattern.compile("version\":(.*?)}");
    Matcher matcher = pattern.matcher(output);
    matcher.find();
    String version = matcher.group(1);
    return version;
  }
}
