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
    
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "group/" + setKey );
    String user = "{"
        + "\"emailAddress\": \"testEmail@email.com\","
        + "\"firstName\": \"testFirstName\""
        + "\"lastName\": \"testLastName\","
        + "\"password\": \"testPassword\""
        + "}";
    ClientResponse response2 = webResource2.type("application/json").post(ClientResponse.class, group);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
    String output2 = response.getEntity(String.class);
    String elementKey = extractRowKey(output2);
    client.destroy();
  }
  
  @AfterClass
  public static void tearDownClass() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey);
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
