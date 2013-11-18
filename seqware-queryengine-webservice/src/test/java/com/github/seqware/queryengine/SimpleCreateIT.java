/*
 * Copyright (C) 2013 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seqware.queryengine.model.Group;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests test simple creation and retrieval of entities from the query engine web service
 * @author dyuen
 */
public class SimpleCreateIT {
    public static final String WEBSERVICE_URL = "http://localhost:8889/seqware-queryengine-webservice/api/";
    
    public SimpleCreateIT() {
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
    public void testCreateGroup() {
        Client client = Client.create();
        WebResource webResource = client.resource(WEBSERVICE_URL + "group");
        String group = "{\"name\": \"Avengers\",\"description\": \"dysfunctional\"}";
        ClientResponse response = webResource.type("application/json").post(ClientResponse.class, group);
        Assert.assertTrue("Request failed:" + response.getStatus(), response.getStatus() == 200);
        String output = response.getEntity(String.class);
        Assert.assertTrue("Returned entity incorrect" + output, output.contains("Avengers") && output.contains("dysfunctional"));
    }
    
    @Test
    public void testCreateGroupWithUser() throws IOException {
        Client client = Client.create();
        WebResource webResource = client.resource(WEBSERVICE_URL + "group");
        String group = "{\"name\": \"Avengers\",\"description\": \"dysfunctional\"}";
        ClientResponse response = webResource.type("application/json").post(ClientResponse.class, group);
        Assert.assertTrue("Request failed:" + response.getStatus(), response.getStatus() == 200);
        String output = response.getEntity(String.class);
        Assert.assertTrue("Returned entity incorrect" + output, output.contains("Avengers") && output.contains("dysfunctional"));
        // now create a User using the returned rowkey
        // grab rowkey via regular expression
        Pattern pattern = Pattern.compile("rowKey\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(output);
        matcher.find();
        String rowkey = matcher.group(1);
        
        String user = "{\n"
                + "  \"password\": \"n/a\",\n"
                + "  \"firstName\": \"Tony\",\n"
                + "  \"lastName\": \"Stark\",\n"
                + "  \"emailAddress\": \"ts@sindustries.com\"\n"
                + "}";
        webResource = client.resource(WEBSERVICE_URL + "group/" + rowkey);
        response = webResource.type("application/json").post(ClientResponse.class, user);
        Assert.assertTrue("Request failed:" + response.getStatus(), response.getStatus() == 200);
        output = response.getEntity(String.class);
        Assert.assertTrue("Returned entity incorrect" + output, output.contains("Tony") && output.contains("Stark"));
    }
    
    @Test
    public void testCreateReferenceSet() {
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
    }
    
    @Test
    public void testCreateReferenceSetWithReference() throws IOException {
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
        // now create a User using the returned rowkey
        // grab rowkey via regular expression
        Pattern pattern = Pattern.compile("rowKey\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(output);
        matcher.find();
        String rowkey = matcher.group(1);
        
        String user = "{\n"
                + "  \"name\": \"Funky reference\"\n"
                + "}";
        webResource = client.resource(WEBSERVICE_URL + "referenceset/" + rowkey);
        response = webResource.type("application/json").post(ClientResponse.class, user);
        Assert.assertTrue("Request failed:" + response.getStatus(), response.getStatus() == 200);
        output = response.getEntity(String.class);
        Assert.assertTrue("Returned entity incorrect" + output, output.contains("Funky reference"));
    }
}