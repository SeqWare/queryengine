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
import com.github.seqware.queryengine.model.restModels.FeatureSetFacade;
import com.github.seqware.queryengine.plugins.PluginList;

/**
 * Plugin resource.
 *
 * @author dyuen
 */
@Path("/plugin")
@Api(value = "/plugin", description = "Operations about plugins"/*, listingPath="/resources/plugin"*/)
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
    @Path(value = "/{sgid}/run")
    @ApiOperation(value = "Run a specific plugin by rowkey with JSON parameters", notes = "Add extra notes here"/*, responseClass = "com.github.seqware.queryengine.model.Atom"*/)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public final Response runPlugin(
            @ApiParam(value = "id of plugin to run", required = true)
            @PathParam(value = "sgid") String sgid,
            @ApiParam(value = "Parameters of the Plugin to be run", required = true) FeatureSetFacade set) throws InvalidIDException {
        ArbitraryPluginRunner pluginRunner = new ArbitraryPluginRunner();
        // Check whether the dsn contains the type of store, or not:
        //        if (!dsn.matches("^[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*$"))
        //            return this.getUnsupportedOperationResponse();
        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            throw new InvalidIDException(INVALID_ID, "ID not found");
        }

        return Response.ok("ok".toString())/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
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
        obj.put("displayName", ts);
        //obj.put("timestamp", ts.getTimestamp().toString());
        stringList.add(obj);
        }
//        for (Atom ts : getElements()) {
//            HashMap<String, String> obj = new HashMap<String, String>();
//            obj.put("sgid", ts.getSGID().getUuid().toString());
//            obj.put("displayName", ts.getDisplayName());
//            obj.put("timestamp", ts.getTimestamp().toString());
//            stringList.add(obj);
//        }
        return Response.ok().entity(stringList)/*.header("Access-Control-Allow-Origin", "*").header("X-DAS-Status", "200")*/.build();
    }
//    
//    @GET
//    @Path(value = "/{sgid}")
//    @ApiOperation(value = "Find a specific element by rowkey in JSON", notes = "Add extra notes here", position = 20)
//    @ApiResponses(value = {
//        @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
//        @ApiResponse(code = INVALID_SET, message = "set not found")})
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response featureByIDRequest(
//            @ApiParam(value = "id of set to be fetched", required = true)
//            @PathParam(value = "sgid") String sgid) throws InvalidIDException {
//        // Check whether the dsn contains the type of store, or not:
//        //        if (!dsn.matches("^[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*$"))
//        //            return this.getUnsupportedOperationResponse();
//        //Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
//        //if (latestAtomByRowKey == null) {
//            // A genuinely bad request:
//            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
//        //    throw new InvalidIDException(INVALID_ID, "ID not found");
//       // }
//        return Response.ok().entity(latestAtomByRowKey)/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
//    }
}