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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.model.ReadSet;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import com.github.seqware.queryengine.tutorial.BrianTest;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;
import net.sourceforge.seqware.common.util.Log;

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
  @ApiOperation(value = "List reads in a readset in SAM format", notes = "This can only be done by an authenticated user.")
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  @Produces(MediaType.TEXT_PLAIN)
  public String getSAMReadListing(
          @ApiParam(value = "rowkey that needs to be updated", required = true)
          @PathParam("sgid") String sgid,
          @ApiParam(value = "contig to limit to", required = true)
          @QueryParam("contig") String contig,
          @ApiParam(value = "start position", required = true)
          @QueryParam("start") String start,
          @ApiParam(value = "stop position", required = true)
          @QueryParam("stop") String stop) throws InvalidIDException {

    ReadSet readSet = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, ReadSet.class);
    StringBuilder sb = new StringBuilder();
    if (readSet == null) {
      // A genuinely bad request:
      // (see also http://www.biodas.org/documents/spec-1.6.html#response)
      throw new InvalidIDException(INVALID_ID, "ID not found");
    } else {
      try {
        //CloseableIterator<SAMRecord> set = readSet.scan("20", 1, 63000000);
        CloseableIterator<SAMRecord> set = readSet.scan(contig, Integer.parseInt(start), Integer.parseInt(stop));
        sb.append(readSet.getHeader().getTextHeader());
        int max = 1000;
        while (set.hasNext()) {
          max--;
          if (max < 0) { break; }
          SAMRecord rec = set.next();
          sb.append(rec.getSAMString());
        }
        return sb.toString();
      } catch (IOException ex) {
        Logger.getLogger(ReadSetResource.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return ("");
  }
}