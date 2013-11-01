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
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.ReadSet;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
   * Return the reads that belong to the specified read set in SAM
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
  public String getSAMReadListing(
          @ApiParam(value = "rowkey that needs to be updated", required = true)
          @PathParam("sgid") String sgid,
          @ApiParam(value = "contig to limit to", required = true)
          @PathParam("contig") String contig,
          @ApiParam(value = "start position", required = true)
          @PathParam("start") Integer start,
          @ApiParam(value = "stop position", required = true)
          @PathParam("stop") Integer stop) throws InvalidIDException {

    // LEFT OFF HERE: need to figure out how to write back data
    ReadSet readSet = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, ReadSet.class);
    if (readSet == null) {
      // A genuinely bad request:
      // (see also http://www.biodas.org/documents/spec-1.6.html#response)
      return ("PROBLEM!");
      //throw new InvalidIDException(INVALID_ID, "ID not found");
    }
    StringBuilder sb = new StringBuilder();
    int count = -500;

    if (readSet != null) {
      try {
        // DENIS, take a look here, why does this always throw a null pointer exception
        count = readSet.scanCount(contig, start, stop);
      } catch (IOException ex) {
        Logger.getLogger(ReadSetResource.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    sb.append("COUNT: ");
    sb.append(count);
    sb.append(" ReadSet: ");
    sb.append(readSet);
    return sb.toString();
  }
}