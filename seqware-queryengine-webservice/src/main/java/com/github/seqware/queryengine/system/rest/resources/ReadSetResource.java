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
import com.github.seqware.queryengine.model.ReadSet;
import com.github.seqware.queryengine.model.User;
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
 * Readset resource.
 *
 * @author boconnor
 */
@Path("/readset")
@Api(value = "/readset", description = "Operations about readsets"/*, listingPath="/resources/readset"*/)
@Produces({"application/json"})
public class ReadSetResource extends GenericElementResource<ReadSet> {

    @Override
    public final String getClassName() {
        return "ReadSet";
    }

    @Override
    public final Class getModelClass() {
        return ReadSet.class;
    }
    
    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getReadSets();
    }
    
    /**
     * Return the reads that belong to the specified read set in
     * SAM
     *
     * @param sgid rowkey of readset to operate on
     * @return
     */
    @GET
    @Path("/{sgid}")
    @ApiOperation(value = "List reads in a readset in SAM", notes = "This can only be done by an authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
        @ApiResponse(code = INVALID_SET, message = "Element not found")})
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSAMReadListing(
            @ApiParam(value = "rowkey that needs to be updated", required = true)
            @PathParam("sgid") String sgid,
            @ApiParam(value = "contig to limit to", required = true)
            @PathParam("contig") String contig,
            @ApiParam(value = "start position", required = true)
            @PathParam("start") Integer start,
            @ApiParam(value = "stop position", required = true)
            @PathParam("stop") Integer stop
            ) {
        // make this an overrideable method in the real version
        //userData.addUser("hello read!");
      // LEFT OFF HERE: need to figure out how to write back data
        return Response.ok("HelloWOrld").entity("").build();
    }
}