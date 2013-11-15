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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.ProtobufSerialization;
import com.github.seqware.queryengine.impl.protobufIO.ProtobufTransferInterface;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Molecule;
import com.github.seqware.queryengine.model.ReferenceSet;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryReferenceSet;
import com.github.seqware.queryengine.model.interfaces.ACL;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Describes the basic operations that we wish to define on elements (which may
 * or may not be found inside sets).
 *
 * @author dyuen
 */
public abstract class GenericElementResource<T extends Atom> {

    public static final int INVALID_ID = 400;
    public static final int INVALID_SET = 404;
    public static final int INVALID_INPUT = 405;
    public static final int RESOURCE_EXISTS = 406;
    public static final String QE_STATUS = "QE-Status";

    /**
     * List available resources.
     *
     * @return list of resources
     */
    @GET
    @ApiOperation(value = "List all available elements by rowkey", notes = "This lists the raw rowkeys used to uniquely identify each chain of entities."/*, responseClass = "com.github.seqware.queryengine.model.Atom"*/)
    public Response featuresRequest() {
        // Check whether the dsn contains the type of store, or not:
        //        if (!dsn.matches("^[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*$"))
        //            return this.getUnsupportedOperationResponse();
        List<HashMap<String, String>> stringList = new ArrayList<HashMap<String, String>>();
        for (Atom ts : getElements()) {
            HashMap<String, String> obj = new HashMap<String, String>();
            obj.put("sgid", ts.getSGID().getUuid().toString());
            obj.put("displayName", ts.getDisplayName());
            obj.put("timestamp", ts.getTimestamp().toString());
            stringList.add(obj);
        }
        return Response.ok().entity(stringList)/*.header("Access-Control-Allow-Origin", "*").header("X-DAS-Status", "200")*/.build();
    }

    /**
     * Retrieve resources.
     *
     * @param sgid rowkey of the resource to be accessed
     * @return listing of resources
     */
    @GET
    @Path(value = "/{sgid}")
    @ApiOperation(value = "Find a specific element by rowkey in JSON", notes = "Add extra notes here"/*, responseClass = "com.github.seqware.queryengine.model.Atom"*/)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response featureByIDRequest(
            @ApiParam(value = "id of set to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) throws InvalidIDException {
        // Check whether the dsn contains the type of store, or not:
        //        if (!dsn.matches("^[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*$"))
        //            return this.getUnsupportedOperationResponse();
        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            throw new InvalidIDException(INVALID_ID, "ID not found");
        }
        return Response.ok().entity(latestAtomByRowKey)/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }

    /**
     * Retrieve resources tagged with a particular key on a particular element.
     *
     * @param tagset_id
     * @param tag_key
     * @return
     */
    @GET
    @Path(value = "/tags")
    @ApiOperation(value = "List available elements filtered by a tagset and tag key", notes = "Add extra notes here"/*, responseClass = "com.github.seqware.queryengine.model.Atom"*/)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    public Response taggedRequest(
            @ApiParam(value = "rowkey of tagset to restrict matches to", required = true)
            @QueryParam(value = "tagset_id") String tagset_id,
            @ApiParam(value = "key of the tag to restrict matches to", required = true)
            @QueryParam(value = "tag_key") String tag_key) {
        return Response.ok("inception".toString())/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }

    /**
     * Retrieve resources and list their tags.
     *
     * @param tagset_id
     * @param tag_key
     * @return
     */
    @GET
    @Path(value = "/{sgid}/tags")
    @ApiOperation(value = "Find a specific element by rowkey and list its tags ", notes = "Add extra notes here"/*, responseClass = "com.github.seqware.queryengine.model.Atom"*/)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    public Response tagsOfElementRequest(
            @ApiParam(value = "id of element to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) {
        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_ID).build();
        }
        List<Tag> tags = new ArrayList<Tag>();
        SeqWareIterable<Tag> tagsIterator = latestAtomByRowKey.getTags();
        for (Tag t : tagsIterator) {
            tags.add(t);
        }
        return Response.ok().entity(tags)/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }

    /**
     * Retrieve resources and list their version information.
     *
     * @param tagset_id
     * @param tag_key
     * @return
     */
    @GET
    @Path(value = "/{sgid}/version")
    @ApiOperation(value = "Find a specific element by rowkey and list its version information", notes = "Add extra notes here"/*, responseClass = "com.github.seqware.queryengine.model.Atom"*/)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    public Response versioningOfElementRequest(
            @ApiParam(value = "id of element to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) {

        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_ID).build();
        }
        if (!(latestAtomByRowKey instanceof Molecule)) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_INPUT).build();
        }
        Stack<Atom> versions = new Stack<Atom>();
        versions.add(latestAtomByRowKey);
        while (versions.peek().getPrecedingVersion() != null) {
            Atom precedingVersion = (Atom) versions.peek().getPrecedingVersion();
            versions.add(precedingVersion);
        }
        return Response.ok().entity(versions)/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();


    }

    /**
     * Retrieve resources and list their permissions.
     *
     * @return
     */
    @GET
    @Path(value = "/{sgid}/permissions")
    @ApiOperation(value = "Find a specific element by rowkey and list its permissions ", notes = "Add extra notes here"/*, responseClass = " com.github.seqware.queryengine.model.Atom"*/)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    public Response permissionsOfElementRequest(
            @ApiParam(value = "id of element to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) {
        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_ID).build();
        }
        if (!(latestAtomByRowKey instanceof Molecule)) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_INPUT).build();
        }
        return Response.ok().entity(((Molecule) latestAtomByRowKey).getPermissions())/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }

    /**
     * Update permissions for a particular element
     *
     * @param sgid
     * @param user
     * @return
     */
    @PUT
    @Path("/{sgid}/permissions")
    @ApiOperation(value = "Update permissions for a particular element", notes = "This can only be done by an authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
        @ApiResponse(code = INVALID_SET, message = "Element not found")})
    public Response updateElementPermissions(
            @ApiParam(value = "rowkey that needs to be updated", required = true)
            @PathParam("sgid") String sgid,
            @ApiParam(value = "Group that needs to be added to the store", required = true) ACL acl
            ) {
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        Atom old = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        Atom newAtom = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (newAtom instanceof Molecule){
            Molecule mol = (Molecule) newAtom;
            mol.setPermissions(acl);
        } else{
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_INPUT).build();
        }
        modelManager.update(old, newAtom);
        modelManager.flush();
        return Response.ok().entity(newAtom).build();
    }

    /**
     * Associate a tag with a particular element
     *
     * @param sgid
     * @param user
     * @return
     */
    @PUT
    @Path("/{sgid}/tag")
    @ApiOperation(value = "Tag an existing element", notes = "This can only be done by an authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
        @ApiResponse(code = INVALID_SET, message = "Element not found")})
    public Response tagElement(
            @ApiParam(value = "rowkey that needs to be tagged", required = true)
            @PathParam("sgid") String sgid,
            @ApiParam(value = "rowkey of tagset to pull tag from", required = true)
            @QueryParam(value = "tagset_id") String tagset_id,
            @ApiParam(value = "tag key", required = true)
            @QueryParam(value = "key") String key,
            @ApiParam(value = "tag predicate", required = false)
            @QueryParam(value = "predicate") String predicate,
            @ApiParam(value = "tag value", required = false)
            @QueryParam(value = "value") String value) {
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        Atom old = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        Atom newAtom = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        newAtom.associateTag(Tag.newBuilder().setKey(key).setValue(value).setPredicate(predicate).build());
        modelManager.update(old, newAtom);
        modelManager.flush();
        return Response.ok().entity(newAtom).build();
    }

    /**
     * Update an existing element.
     *
     * @param sgid
     * @param user
     * @return
     */
    @PUT
    @Path("/{sgid}")
    @ApiOperation(value = "Update an existing element", notes = "This can only be done by an authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
        @ApiResponse(code = INVALID_SET, message = "Element not found")})
    public Response updateElement(
            @ApiParam(value = "rowkey that need to be updated", required = true) @PathParam("sgid") String sgid,
            @ApiParam(value = "Updated user object", required = true) T element) {
      
              CreateUpdateManager modelManager = SWQEFactory.getModelManager();
	      T old = (T)SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
	      modelManager.update(old, element);
              modelManager.flush();
              return Response.ok().entity(element).build();
    }
    
        /**
     * Update an existing element.
     *
     * @param sgid
     * @param user
     * @return
     */
    @DELETE
    @Path("/{sgid}")
    @ApiOperation(value = "Delete an existing element", notes = "This can only be done by an authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
        @ApiResponse(code = INVALID_SET, message = "Element not found")})
    public Response deleteElement(
            @ApiParam(value = "rowkey that need to be deleted", required = true) @PathParam("sgid") String sgid) {
      
              CreateUpdateManager modelManager = SWQEFactory.getModelManager();
	      T old = (T)SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
              modelManager.delete(old);
              modelManager.flush();
            
              return Response.ok().entity("").build();
    }

    /**
     * Names the class, useful for comments and debugging output
     *
     * @return
     */
    public abstract String getClassName();

    /**
     * Iterate through the elements of this type
     *
     * @return
     */
    public abstract SeqWareIterable<T> getElements();

    /**
     * Class of the elements in the set, used for casting and type-check
     * operations
     *
     * @return
     */
    public abstract Class getModelClass();

    /**
     * Proto-buffer
     *
     * @return
     */
    public ProtobufTransferInterface gettIO() {
        return ProtobufSerialization.biMap.get(getModelClass());
    }
}
