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

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.plugins.runners.hbasemr.MRHBasePluginRunner;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_ID;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_INPUT;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_SET;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.jcraft.jsch.Logger;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.mortbay.log.Log;

/**
 * Group resource.
 *
 * @author dyuen
 */
@Path("/group")
@Api(value = "/group", description = "Operations about groups")
@Produces({"application/json"})
public class GroupResource extends GenericMutableSetResource<Group, User> {

  @Override
  public final String getClassName() {
    return "Group";
  }

  @Override
  public final Class getModelClass() {
    return Group.class;
  }

  @Override
  public final SeqWareIterable getElements() {
    return SWQEFactory.getQueryInterface().getGroups();
  }

  @GET
  @Override
  @Path(value = "/{sgid}")
  @ApiOperation(value = "Find a specific element by rowkey in JSON", notes = "Add extra notes here", response = Group.class)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
    @ApiResponse(code = INVALID_SET, message = "set not found")})
  @Produces(MediaType.APPLICATION_JSON)
  public final Response featureByIDRequest(
          @ApiParam(value = "id of set to be fetched", required = true)
          @PathParam(value = "sgid") String sgid) throws InvalidIDException {
    return super.featureByIDRequest(sgid);
  }

  @PUT
  @Path("/{sgid}")
  @ApiOperation(value = "Update an existing element", notes = "This can only be done by an authenticated user.")
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  @Override
  public Response updateElement(
          @ApiParam(value = "rowkey that need to be deleted", required = true) @PathParam("sgid") String sgid,
          @ApiParam(value = "Updated user object", required = true) Group group) {

    CreateUpdateManager modelManager = SWQEFactory.getModelManager();
    QueryInterface query = SWQEFactory.getQueryInterface();

    return(super.updateElement(sgid, group));

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
    @ApiOperation(value = "Delete an existing Group", notes = "This can only be done by an authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
        @ApiResponse(code = INVALID_SET, message = "Element not found")})
    public Response deleteElement(
            @ApiParam(value = "rowkey that need to be deleted", required = true) @PathParam("sgid") String sgid) {
      return(super.deleteElement(sgid));
    }
  

  @POST
  @Path("/{sgid}")
  @ApiOperation(value = "Create an element in the set", notes = "This can only be done by an authenticated user.", response = User.class)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_INPUT, message = "Invalid input")})
  @Override
  public final Response addElement(
          @ApiParam(value = "set to add an element to", required = true)
          @PathParam("sgid") String sgid,
          @ApiParam(value = "element that needs to be added to the store", required = true) User element) {
    return super.addElement(sgid, element);
  }

  @POST
  @ApiOperation(value = "Create a totally new Group by JSON", notes = "This can only be done by an authenticated user.")
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_INPUT, message = "Invalid input")})
  @Consumes(MediaType.APPLICATION_JSON)
  @Override
  public Response addSet(
          @ApiParam(value = "Group that needs to be added to the store", required = true) Group set) {
    System.out.println("addSet " + set.getDescription() + " " + set.getName());
    // make this an overrideable method in the real version
    return super.addSet(set);
  }
}