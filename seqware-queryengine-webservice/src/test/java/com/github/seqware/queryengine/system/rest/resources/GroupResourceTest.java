package com.github.seqware.queryengine.system.rest.resources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.seqware.queryengine.model.Group;
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

public class GroupResourceTest {
  public static final String WEBSERVICE_URL = "http://localhost:8889/seqware-queryengine-webservice/api/";
  public static String setKey;
  
  public GroupResourceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
   //Create a Test Group
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group" );
    String group = "{"
        + "\"name\": \"string\","
        + "\"description\": \"string\""
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, group);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    setKey = extractRowKey(output);
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

  /**
   * Test of getClassName method, of class GroupResource.
   */
  @Test
  public void testGetClassName() {
    GroupResource instance = new GroupResource();
    String expResult = "Group";
    String result = instance.getClassName();
    Assert.assertEquals(expResult, result);
  }

  /**
   * Test of getModelClass method, of class GroupResource.
   */
  @Test
  public void testGetModelClass() {
    GroupResource instance = new GroupResource();
    Class expResult = Group.class;
    Class result = instance.getModelClass();
    Assert.assertEquals(expResult, result);
  }
  
  @Test
  public void testGetGroups() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group" );
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
