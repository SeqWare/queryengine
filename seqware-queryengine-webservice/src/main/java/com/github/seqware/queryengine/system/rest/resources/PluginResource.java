/*
 * Copyright (C) 2012 SeqWare
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
package com.github.seqware.queryengine.system.rest.resources;

import com.github.seqware.queryengine.factory.SWQEFactory;

import com.github.seqware.queryengine.system.exporters.ArbitraryPluginRunner;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Plugin;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_ID;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_INPUT;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_SET;
import com.github.seqware.queryengine.plugins.PluginList;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;

/**
 * Plugin resource.
 *
 * @author dyuen
 */
@Path("/plugin")
@Api(value = "/plugin", description = "Operations about plugins"/*, listingPath="/resources/plugin"*/)
//@Produces({"application/json"})
@Produces({"application/json"})
public class PluginResource {

    public final String getClassName() {
        return "Plugin";
    }

    public final Class getModelClass() {
        return Plugin.class;
    }
    
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getPlugins();
    }
    
    /**
     * Retrieve resources.
     *
     * @param sgid rowkey of the plugin to be run
     * @return listing of resources
     */
    @POST
    @Path(value = "/{name}/run")
    @ApiOperation(value = "Run a specific plugin by name with specific parameters", notes = "Add extra notes here")
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid name supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    @Produces(MediaType.APPLICATION_JSON)
    public final Response runPlugin(
            @ApiParam(value = "id of plugin to run", required = true)
            @PathParam(value = "name") String name,
            @ApiParam(value = "reference dataset to use", required = true)
            @QueryParam(value = "reference") String reference,
            @ApiParam(value = "output", required = true)
            @QueryParam(value = "output") String output) throws InvalidIDException {
        ArbitraryPluginRunner pluginRunner = new ArbitraryPluginRunner();
        
        //Construct the command to the ArbitraryPluginRunner
        String[] cd = new String[6];
        cd[0] = "-p";
        cd[1] = name;
        cd[2] = "-r";
        cd[3] = reference;
        cd[4] = "-o";
        cd[5] = "/tmp/" + output;
        int process = 0;
        try {
          process = ArbitraryPluginRunner.runArbitraryPluginRunner(cd);
        } catch (Exception ex) {
          System.out.println(ex.getMessage());
          return Response.ok(ex.getMessage()).build(); //("Error running Plugin: " + ex.getMessage()).toString()
        }
        
        String status = Integer.toString(process);
        String response = "";
        Charset charset = Charset.forName("UTF-8");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("/tmp/" + output), charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                response = response + line + " ";
            }
            reader.close();
            //Delete the temporary output file
            File temp = new File("tmp/" + output);
            temp.delete();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        HashMap<String, String> jsonResp = new HashMap<String, String>();
        jsonResp.put("status", status);
        jsonResp.put("output", response);
        
        return Response.ok().entity(jsonResp).build();
    }
    
    @GET
    @ApiOperation(value = "List all available elements by rowkey", notes = "This lists the raw rowkeys used to uniquely identify each chain of entities.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pluginsRequest() {
        PluginList plugins = new PluginList();
        List<HashMap<String, String>> stringList = new ArrayList<HashMap<String, String>>();
        List<String> pluginList = plugins.list;
        for (String ts : pluginList) {
          HashMap<String, String> obj = new HashMap<String, String>();
          //obj.put("sgid", ts.getSGID().getUuid().toString());
          obj.put("pluginName", ts);
          //obj.put("timestamp", ts.getTimestamp().toString());
          stringList.add(obj);
        }
        return Response.ok().entity(stringList)/*.header("Access-Control-Allow-Origin", "*").header("X-DAS-Status", "200")*/.build();
    }
    
    @GET
    @Path(value = "/{name}")
    @ApiOperation(value = "Find a specific plugin by name", notes = "Add extra notes here", position = 20)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid name supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response pluginRequest(
            @ApiParam(value = "id of set to be fetched", required = true)
            @PathParam(value = "name") String name) throws InvalidIDException {
        PluginList plugins = new PluginList();
        HashMap<String, String> requestedPlugin = new HashMap<String, String>();
        List<String> pluginList = plugins.list;
        for (String ts : pluginList) {
          if (ts.equals(name)) {
            HashMap<String, String> obj = new HashMap<String, String>();
            //obj.put("sgid", ts.getSGID().getUuid().toString());
            obj.put("pluginName", ts);
            //obj.put("timestamp", ts.getTimestamp().toString());
            requestedPlugin = obj;
            break;
          }
        }
        return Response.ok().entity(requestedPlugin)/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }
}