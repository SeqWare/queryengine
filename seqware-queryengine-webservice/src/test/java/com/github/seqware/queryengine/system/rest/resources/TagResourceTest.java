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

import com.github.seqware.queryengine.model.Tag;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class TagResourceTest {
  public static final String WEBSERVICE_URL = "http://localhost:8889/seqware-queryengine-webservice/api/";
  public static String tagSetKey;
  public static String tagKey;
  
  public TagResourceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    // Create a test Tagset
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tagset");
    String tagset = "{\n"
            + "  \"name\": \"TagResourceTest\"\n"
            + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, tagset);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    tagSetKey = extractRowKey(output);
    
    //Create Tag
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
    String tag = "{\n"
        + "\"predicate\": \"TagResourceTestPredicate\",\n"
        + "\"key\": \"TagResourceTestKey\"\n"
        + "}";
    ClientResponse response2 = webResource2.type("application/json").post(ClientResponse.class, tag);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
    String output2 = response2.getEntity(String.class);
    Assert.assertTrue("Returned entity incorrect" + output2, output2.contains("TagResourceTestPredicate") && output2.contains("TagResourceTestKey"));
    tagKey = extractRowKey(output2);
    client.destroy();
  }
  
  @AfterClass
  public static void tearDownClass() {
    //Drop the created tagset
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
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
   * Test of getClassName method, of class TagResource.
   */
  @Test
  public void testGetClassName() {
    TagResource instance = new TagResource();
    String expResult = "Tag";
    String result = instance.getClassName();
    assertEquals(expResult, result);
  }

  /**
   * Test of getModelClass method, of class TagResource.
   */
  @Test
  public void testGetModelClass() {
    TagResource instance = new TagResource();
    Class expResult = Tag.class;
    Class result = instance.getModelClass();
    assertEquals(expResult, result);
  }
  
  @Test
  public void testCreateTag() {
    //Create the Tag in Tagset
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
    String tag = "{\n"
        + "\"predicate\": \"Test_Tag\",\n"
        + "\"key\": \"Testing\"\n"
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, tag);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    Assert.assertTrue("Returned entity incorrect" + output, output.contains("Testing") && output.contains("Test_Tag"));
    String rowkey = extractRowKey(output);
    
    //Delete the Tag
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "tag/" + rowkey);
    webResource2.delete();
    ClientResponse response2 = webResource2.type("application/json").get(ClientResponse.class);
    String output2 = response2.getEntity(String.class);
    Assert.assertTrue("Deletion failed: " + output2, !output2.contains(rowkey));
  }
  
  @Test
  public void testGetTag() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tag");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    //String output = response.getEntity(String.class);
    client.destroy();
  }
  
  @Test
  public void testGetTagsInTagSet() {
    //Create Tag
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
    String tag = "{\n"
        + "\"predicate\": \"Test_Tag\",\n"
        + "\"key\": \"Testing\"\n"
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, tag);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    Assert.assertTrue("Returned entity incorrect" + output, output.contains("Testing") && output.contains("Test_Tag"));
    String rowkey = extractRowKey(output);
    
    //GET tags/tag
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "tag/tags?tagset_id=" + tagSetKey + "&tag_key=Testing" );
    ClientResponse response2 = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
    //String output2 = response2.getEntity(String.class);
    client.destroy();
    
    //Delete the Tag
    WebResource webResource3 = client.resource(WEBSERVICE_URL + "tag/" + rowkey);
    webResource3.delete();
    ClientResponse response3 = webResource2.type("application/json").get(ClientResponse.class);
    String output3 = response3.getEntity(String.class);
    Assert.assertTrue("Deletion failed: " + output3, !output3.contains(rowkey));
  }
  
  @Test 
  public void testGetTagsByRowkey() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tag/" + tagKey + "/tags");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testGetVersion() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tag/" + tagKey + "/version");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testGetPermissions() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tag/" + tagKey + "/permissions");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  /*
  @Test
  public void testPutTag() {
    //Create the Tag in Tagset
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
    String tag = "{\n"
        + "\"predicate\": \"Test_Tag\",\n"
        + "\"key\": \"Testing\"\n"
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, tag);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    Assert.assertTrue("Returned entity incorrect" + output, output.contains("Testing") && output.contains("Test_Tag"));
    String rowkey = extractRowKey(output);
    
    //Update the Tag
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "tag/" + rowkey);
    String put = 
    ClientResponse response2 = webResource2.type("application/json").put(put);
    
    //Delete the Tag
    WebResource webResource3 = client.resource(WEBSERVICE_URL + "tag/" + rowkey);
    webResource3.delete();
    ClientResponse response3 = webResource3.type("application/json").get(ClientResponse.class);
    String output3 = response3.getEntity(String.class);
    Assert.assertTrue("Deletion failed: " + output3, !output3.contains(rowkey));
  }*/
  
  protected static String extractRowKey(String output) {
    Pattern pattern = Pattern.compile("rowKey\":\"(.*?)\"");
    Matcher matcher = pattern.matcher(output);
    matcher.find();
    String rowkey = matcher.group(1);
    return rowkey;
  }
}
