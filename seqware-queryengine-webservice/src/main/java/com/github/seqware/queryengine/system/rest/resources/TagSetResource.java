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
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * TagSet resource.
 *
 * @author dyuen
 */
@Path("/tagset")
@Api(value = "/tagset", description = "Operations about tagsets"/*, listingPath="/resources.json/tagset"*/)
@Produces({"application/json"})
public class TagSetResource extends GenericMutableSetResource<TagSet, Tag> {

    @Override
    public final String getClassName() {
        return "TagSet";
    }

    @Override
    public final Class getModelClass() {
        return TagSet.class;
    }
    
    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getTagSets();
    }

  @Override
  public void saveSet(TagSet set) {
    CreateUpdateManager um = SWQEFactory.getModelManager();
    um.buildTagSet().setName("foo").build();
    um.close();
  }
    
    /**
     * Upload an OBO file to create a new tagset for an ontology
     * @param sgid rowkey of ontology to create
     * @return 
     * 
     * LEFT OFF HERE: best way to implement this?
     * 
     */
    @POST
    @ApiOperation(value = "Create new ontology from an OBO file", notes = "This can only be done by an authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(code = RESOURCE_EXISTS, message = "Resource already exists")})
    @Consumes(MediaType.TEXT_PLAIN)
    public Response uploadOBO(
            @ApiParam(value = "rowkey that needs to be updated", required = true) 
            @QueryParam("sgid") String sgid
            ) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }
}