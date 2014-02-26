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
  public static String tagKey;
  public static String tagSetKey;
  
  public GroupResourceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    //Create a Test Group
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group" );
    String group = "{"
        + "\"name\": \"TestGroup\","
        + "\"description\": \"Testing the Group Resource\""
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, group);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    setKey = extractRowKey(output);
    
    //Create a TagSet for this test
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "tagset");
    String tagset = "{\n"
            + "  \"name\": \"TestGroupTagSet\"\n"
            + "}";
    ClientResponse response2 = webResource2.type("application/json").post(ClientResponse.class, tagset);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
    String output2 = response2.getEntity(String.class);
    tagSetKey = extractRowKey(output2);
    
    //Create a Tag for the test
    WebResource webResource3 = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
    String tag = "{\n"
        + "\"predicate\": \"GroupTagPredicate\",\n"
        + "\"key\": \"TestGroup\"\n"
        + "}";
    ClientResponse response3 = webResource3.type("application/json").post(ClientResponse.class, tag);
    Assert.assertTrue("Request failed: " + response3.getStatus(), response3.getStatus() == 200);
    String output3 = response3.getEntity(String.class);
    tagKey = extractRowKey(output3);
    client.destroy();
  }
  
  @AfterClass
  public static void tearDownClass() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey);
    webResource.delete();
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "tagset/" + tagSetKey);
    webResource2.delete();
    WebResource webResource3 = client.resource(WEBSERVICE_URL + "tag/" + tagKey);
    webResource3.delete();
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
  
  // GET group
  @Test
  public void testGetGroups() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group" );
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  // GET group/{sgid}
  @Test
  public void testGetGroup() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey);
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  // POST   group
  // PUT    group/{sgid}
  // DELETE group/{sgid}
  @Test
  public void testPutGroup() {
    //Create a new Group
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group");
    String group = "{"
            + "\"name\": \"TestPutGroup\",\n"
            + "\"description\": \"TestDescription\"\n"
            + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, group);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    String rowkey = extractRowKey(output);
    Assert.assertTrue("Returned entity incorrect" + output, output.contains(rowkey) && output.contains("TestPutGroup"));
    
    //Update the Group
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "group/" + rowkey);
    String put = "{\n"
            + "\"name\": \"ChangedGroupName\",\n"
            + "\"description\": \"ChangedDescription\"\n"
            + "}";  
    ClientResponse response2 = webResource2.type("application/json").put(ClientResponse.class, put);
    Assert.assertTrue("Put failed:" + response.getStatus(), response2.getStatus() == 200);
    String output2 =  response2.getEntity(String.class);
    Assert.assertTrue("Output does not contain the PUT update: " + output2, output2.contains(rowkey) && output2.contains("ChangedGroupName"));
    
    //Delete the Group
    webResource2.delete();
    ClientResponse response3 = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response3.getStatus(), response3.getStatus() == 200);
    String output3 = response3.getEntity(String.class);
    Assert.assertTrue("Could not delete entity:" + response.getStatus(), !output3.contains(rowkey));
    client.destroy();
  }
  
  // GET group/{sgid}/permissions
  @Test
  public void testGetPermissions() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey + "/permissions");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  // GET group/{sgid}/tags
  @Test
  public void testGetTags() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey + "/tags");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  // GET group/{sgid}/version
  @Test
  public void testGetVersion() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey + "/version");
    ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  // POST   group/{sgid}
  // DELETE user/{sgid}
  @Test
  public void testCreateElement() {
    //Create a Test User
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey );
    String user = "{"
        + "\"emailAddress\": \"testDummyEmail@email.com\","
        + "\"firstName\": \"TestName\","
        + "\"lastName\": \"testLastName\","
        + "\"password\": \"testPassword\""
        + "}";
    ClientResponse response = webResource.type("application/json").post(ClientResponse.class, user);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    String output = response.getEntity(String.class);
    String userKey = extractRowKey(output);
    
    //Delete the User
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "user/" + userKey );
    ClientResponse response2 = webResource2.delete(ClientResponse.class);
    Assert.assertTrue("Delete failed: "+ response2.getStatus(), response.getStatus() == 200);
    client.destroy();
  }
  
  // PUT group/{sgid}/tag
  // GET group/{sgid}/tags
  @Test
  public void testTagGroup() {
    Client client = Client.create();
    WebResource webResource = client.resource(WEBSERVICE_URL + "group/" + setKey + "/tag?tagset_id=" + tagSetKey +"&key=" + tagKey);
    ClientResponse response = webResource.type("application/json").put(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response.getStatus(), response.getStatus() == 200);
    
    WebResource webResource2 = client.resource(WEBSERVICE_URL + "group/tags?tagset_id=" + tagSetKey + "&key=" + tagKey);
    ClientResponse response2 = webResource2.type("application/json").get(ClientResponse.class);
    Assert.assertTrue("Request failed: " + response2.getStatus(), response2.getStatus() == 200);
  }
  
  protected static String extractRowKey(String output) {
    Pattern pattern = Pattern.compile("rowKey\":\"(.*?)\"");
    Matcher matcher = pattern.matcher(output);
    matcher.find();
    String rowkey = matcher.group(1);
    return rowkey;
  }

}
