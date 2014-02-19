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
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.model.restModels.TagFacade;
import com.github.seqware.queryengine.model.restModels.UserFacade;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_ID;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_SET;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Tag resource.
 *
 * @author dyuen
 */
@Path("/tag")
@Api(value = "/tag", description = "Operations about tags"/*, listingPath="/resources/tag"*/)
@Produces({"application/json"})
public class TagResource extends GenericElementResource<User> {

    @Override
    public final String getClassName() {
        return "Tag";
    }

    @Override
    public final Class getModelClass() {
        return Tag.class;
    }
    
    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getTags();
    }
    
    @GET
    @Override
    @Path(value = "/{sgid}")
    @ApiOperation(value = "Find a specific element by rowkey in JSON", notes = "Add extra notes here" ,response=TagFacade.class)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid ID supplied"),
        @ApiResponse(code = INVALID_SET, message = "set not found")})
    @Produces(MediaType.APPLICATION_JSON)
    public final Response featureByIDRequest(
            @ApiParam(value = "id of Tag to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) throws InvalidIDException {
        return super.featureByIDRequest(sgid);
    }
}