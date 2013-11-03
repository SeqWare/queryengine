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
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import net.sf.samtools.BAMFileWriter;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
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
  public Response getSAMReadListing(
          @ApiParam(value = "rowkey that needs to be updated", required = true)
          @PathParam("sgid") String sgid,
          @ApiParam(value = "contig to limit to", required = true)
          @QueryParam("contig") String contig,
          @ApiParam(value = "start position", required = true)
          @QueryParam("start") String start,
          @ApiParam(value = "stop position", required = true)
          @QueryParam("stop") String stop) throws InvalidIDException {

    
    // FIXME: is final correct here?
    final ReadSet readSet = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, ReadSet.class);
    if (readSet == null) {
      // A genuinely bad request:
      // (see also http://www.biodas.org/documents/spec-1.6.html#response)
      throw new InvalidIDException(INVALID_ID, "ID not found");
    } else {
      try {
        //CloseableIterator<SAMRecord> set = readSet.scan("20", 1, 63000000);
        final CloseableIterator<SAMRecord> set = readSet.scan(contig, Integer.parseInt(start), Integer.parseInt(stop));
        
        StreamingOutput stream = new StreamingOutput() {
          @Override
          public void write(OutputStream os) throws IOException, WebApplicationException {
            Writer writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(readSet.getHeader().getTextHeader());
            while (set.hasNext()) {
              SAMRecord rec = set.next();
              writer.write(rec.getSAMString());
            }
            writer.flush();
          }
        };

        return Response.ok(stream).build();

      } catch (IOException ex) {
        Logger.getLogger(ReadSetResource.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return (null);
  }
  
  /**
   * Return the reads that belong to the specified read set in BAM
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
  @Produces("application/bam")
  public Response getBAMReadListing(
          @ApiParam(value = "rowkey that needs to be updated", required = true)
          @PathParam("sgid") String sgid,
          @ApiParam(value = "contig to limit to", required = true)
          @QueryParam("contig") String contig,
          @ApiParam(value = "start position", required = true)
          @QueryParam("start") String start,
          @ApiParam(value = "stop position", required = true)
          @QueryParam("stop") String stop) throws InvalidIDException {

    
    // FIXME: is final correct here?
    final ReadSet readSet = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, ReadSet.class);
    if (readSet == null) {
      // A genuinely bad request:
      // (see also http://www.biodas.org/documents/spec-1.6.html#response)
      throw new InvalidIDException(INVALID_ID, "ID not found");
    } else {
      try {
        //CloseableIterator<SAMRecord> set = readSet.scan("20", 1, 63000000);
        final CloseableIterator<SAMRecord> set = readSet.scan(contig, Integer.parseInt(start), Integer.parseInt(stop));
        
        StreamingOutput stream = new StreamingOutput() {
          @Override
          public void write(OutputStream os) throws IOException, WebApplicationException {
            BAMFileWriter writer = new BAMFileWriter(os, new File("/tmp/foo.bam"));
            //Writer writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.setHeader(readSet.getHeader());
            while (set.hasNext()) {
              SAMRecord rec = set.next();
              writer.addAlignment(rec);
              Logger.getLogger(ReadSetResource.class.getName()).log(Level.SEVERE, "READ: "+rec.getReadName());
            }
            writer.close();
          }
        };

        return Response.ok(stream).build();

      } catch (IOException ex) {
        Logger.getLogger(ReadSetResource.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return (null);
  }
  
}