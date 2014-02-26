package com.github.seqware.queryengine.system.rest.resources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class UserResourceTest {
  public static final String WEBSERVICE_URL = "http://localhost:8889/seqware-queryengine-webservice/api/";
  public static String setKey;
  public static String elementKey;
  public static String tagKey;
  public static String tagSetKey;
  
  public UserResourceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group");
    String group = "{"
        + "\"name\": \"TestUsers\","
        + "\"description\": \"Testing the User Resource\""
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, group);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    String rowkey = extractRowKey(output);
    setKey = rowkey;
    
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "group/" + rowkey );
    String user = "{\n"
        + "\"firstName\": \"string\","
        + "\"emailAddress\": \"string\","
        + "\"lastName\": \"string\","
        + "\"password\": \"string\""
        + "}";
    ClientResponse response2 = webResource2.type("application/json").post(ClientResponse.class, user);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
    String output2 = response2.getEntity(String.class);
    elementKey = extractRowKey(output2);
    client.destroy();
    
    //Create a TagSet for this test
    WebResource webResource3 = client.resource(WEBSERVICE_URL + "tagset");
    String tagset = "{\n"
            + "  \"name\": \"TestGroupTagSet\"\n"
            + "}";
    ClientResponse response3 = webResource3.type("application/json").post(ClientResponse.class, tagset);
    Assert.assertTrue("Request failed: " + response3.getStatus(), response3.getStatus() == 200);
    String output3 = response3.getEntity(String.class);
    tagSetKey = extractRowKey(output3);
    
    //Create a Tag for the test
    WebResource webResource4 = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
    String tag = "{\n"
        + "\"predicate\": \"GroupTagPredicate\",\n"
        + "\"key\": \"TestGroup\"\n"
        + "}";
    ClientResponse response4 = webResource4.type("application/json").post(ClientResponse.class, tag);
    Assert.assertTrue("Request failed: " + response4.getStatus(), response4.getStatus() == 200);
    String output4 = response4.getEntity(String.class);
    tagKey = extractRowKey(output4);
    client.destroy();
  }
  
  @AfterClass
  public static void tearDownClass() {
    Client client = Client.create();
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "user/" + elementKey);
    webResource2.delete();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey);
    webResource.delete();
    
    WebResource webResource3 = client.resource(WEBSERVICE_URL + "tag/" + tagKey);
    webResource3.delete();
    WebResource webResource4 = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
    webResource4.delete();
    client.destroy();
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }
  
  @Test
  public void testTagUser() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "user/" + elementKey + "/tag?tagset_id=" + tagSetKey +"&key=" + tagKey);
    ClientResponse response = webResource.type("application/json").put(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "group/tags?tagset_id=" + tagSetKey + "&key=" + tagKey);
    ClientResponse response2 = webResource2.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
  }
  
  @Test
  public void testPutUser() {
    //Create a new User
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey );
    String user = "{\n"
            + "\"firstName\": \"string\","
            + "\"emailAddress\": \"string\","
            + "\"lastName\": \"string\","
            + "\"password\": \"string\""
            + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, user);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    String rowkey = extractRowKey(output);
    
    //Update the User
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "user/" + rowkey);
    String put = "{\n"
            + "\"firstName\": \"ChangedFirstName\","
            + "\"emailAddress\": \"ChangedEmail@email.com\","
            + "\"lastName\": \"ChangedLastName\","
            + "\"password\": \"ChangedPassword\""
            + "}";
    ClientResponse response2 = webResource2.type("application/json").put(ClientResponse.class, put);
    Assert.assertTrue("Put failed:" + response.getStatus(), response2.getStatus() == 200);
    String output2 =  response2.getEntity(String.class);
    Assert.assertTrue("Output does not contain the PUT update: " + output2, output2.contains(rowkey) && output2.contains("ChangedFirstName"));
    
    //Delete the User
    webResource2.delete();
    ClientResponse response3 = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response3.getStatus(), response3.getStatus() == 200);
    String output3 = response3.getEntity(String.class);
    Assert.assertTrue("Could not delete entity:" + response.getStatus(), !output3.contains(rowkey));
    client.destroy();
  }
  
  @Test
  public void testGetUser() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "user/" + elementKey );
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testGetUsers() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "user" );
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testGetTags() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "user/" + elementKey + "/tags" );
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testGetVersion() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "user/" + elementKey + "/version" );
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  @Test
  public void testGetPermissions() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "user/" + elementKey + "/permissions" );
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
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
