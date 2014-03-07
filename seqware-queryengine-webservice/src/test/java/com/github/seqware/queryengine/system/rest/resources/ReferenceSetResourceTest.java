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
  public static String setKey;
  public static String tagSetKey;
  
  public ReferenceSetResourceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    //Create a Test ReferenceSet
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset" );
    String referenceSet = "{"
        + "\"organism\": \"TestOraganism\","
        + "\"name\": \"TestReferenceSet\""
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, referenceSet);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    setKey = extractRowKey(output);
    client.destroy();
    
    //Create a TagSet for this test
    WebResource webResource2 = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "tagset");
    String tagset = "{\n"
            + "  \"name\": \"TestReferenceSetTagSet\"\n"
            + "}";
    ClientResponse response2 = webResource2.type("application/json").post(ClientResponse.class, tagset);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
    String output2 = response2.getEntity(String.class);
    tagSetKey = extractRowKey(output2);
  }
  
  @AfterClass
  public static void tearDownClass() {
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset/" + setKey);
    webResource.delete();
    WebResource webResource2 = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "tagset/" + tagSetKey);
    webResource2.delete();
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
  
  // GET referenceset/
  @Test
  public void testGetReferenceSets() {
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  // GET referenceset/{sgid}
  @Test
  public void testGetReferenceSet() {
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset/" + setKey);
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  //POST   referenceset/
  //PUT    referenceset/{sgid}
  //DELETE referenceset/{sgid}
  @Test
  public void testPutReferenceSet() {
    //Create a new ReferenceSet
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset");
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
    WebResource webResource2 = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset/" + rowkey);
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
  public void testPutTag() {
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset/" + setKey + "/tag?tagset_id=" + tagSetKey + "&key=referenceset");
    ClientResponse response = webResource.type("application/json").put(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  //GET referenceset/{sgid}/tags
  @Test
  public void testGetTags() {
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset/" + setKey + "/tags");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  //GET referenceset/{sgid}/permissions  
  @Test
  public void testGetPermissions() {
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset/" + setKey + "/permissions");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }

  //GET referenceset/{sgid}/version
  @Test
  public void testGetVersion() {
    Client client = Client.create();
    WebResource webResource = client.resource(QEWSResourceTestSuite.WEBSERVICE_URL + "referenceset/" + setKey + "/version");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    String version = extractVersion(output);
    Assert.assertTrue("Invalid Version returned: " + version, Integer.parseInt(version)==1);
    client.destroy();
  }
  
  protected static String extractRowKey(String output) {
    Pattern pattern = Pattern.compile("rowKey\":\"(.*?)\"");
    Matcher matcher = pattern.matcher(output);
    matcher.find();
    String rowkey = matcher.group(1);
    return rowkey;
  }
  
  protected static String extractVersion(String output) {
    Pattern pattern = Pattern.compile("version\":(.*?)}");
    Matcher matcher = pattern.matcher(output);
    matcher.find();
    String version = matcher.group(1);
    return version;
  }
}
