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
import com.github.seqware.queryengine.model.ReadSet;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import com.github.seqware.queryengine.system.importers.ReadImporter;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;
import com.github.seqware.queryengine.model.restModels.ReadSetFacade;
/**
 * Readset resource.
 *
 * @author boconnor
 */
@Path("/readset")
@Api(value = "/readset", description = "Operations about readsets"/*, listingPath="/resources/readset"*/)
@Produces({"application/json"})
public class ReadSetResource extends GenericSetResource<ReadSetFacade> {
  
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
   * Return the reads that belong to the specified read set in SAM format
   *
   * @param sgid rowkey of readset to operate on
   * @return
   */
  @GET
  @Path("/{sgid}")
  @ApiOperation(value = "List reads in a readset in SAM format, use the header 'Accept: text/sam' for this resource.", notes = "This can only be done by an authenticated user.")
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  @Produces("text/sam")
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
   * Return the reads that belong to the specified read set in BAM format
   *
   * @param sgid rowkey of readset to operate on
   * @return
   */
  @GET
  @Path("/{sgid}")
  @ApiOperation(value = "List reads in a readset in BAM format, use the header 'Accept: application/bam' for this resource.", notes = "This can only be done by an authenticated user.")
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
  
  @PUT
  @Path("/{sgid}")
  @ApiOperation(value = "Update an existing element", notes = "This can only be done by an authenticated user.", position = 230)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  @Override
  public Response updateElement(
          @ApiParam(value = "rowkey that need to be deleted", required = true) @PathParam("sgid") String sgid,
          @ApiParam(value = "Updated user object", required = true) ReadSetFacade group) {

    return super.updateElement(sgid, group);

  }
  
  @POST
  @ApiOperation(value = "Create a totally new ReadSet by JSON", notes = "This can only be done by an authenticated user.", position = 50000)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_INPUT, message = "Invalid input")})
  @Consumes(MediaType.APPLICATION_JSON)
  @Override
  public Response addSet(
          @ApiParam(value = "ReferenceSet that needs to be added to the store", required = true) ReadSetFacade set) {
    return super.addSet(set);
  }
  
  @POST
  @Path("/upload")
  @ApiOperation(value = "Create a new readset with a raw data file", notes = "This can only be done by an authenticated user.", position = 110)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response uploadRawSAMfile(
          @ApiParam(value = "file to upload") @FormDataParam("file") InputStream file,
          @ApiParam(value = "file detail") @FormDataParam("file") FormDataContentDisposition fileDisposition) {
      SGID sgid = null;
      try {
          /*
           * FIXME: this is a really naive approach, just write it out as a file and load using
           * the import tool from the query engine backend. In the future this should be an asyncrhonous 
           * process with the file uploaded to a admin configured location possibly on HDFS then 
           * the upload should take place as a plugin, reporting back a token that the calling 
           * user can occationally check in on.
           */

          String fileName = fileDisposition.getName();
          BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/" + fileName));
          IOUtils.copy(file, bw, "UTF-8");
          bw.close();

          sgid = ReadImporter.naiveRun(new String[]{"SAMReadImportWorker", "1", "false", "/tmp/" + fileName});
          //Delete the uploaded vcf file
          File temp = new File("/tmp/" + fileName);
          temp.delete();
      } catch (IOException ex) {
          Logger.getLogger(ReadSetResource.class.getName()).log(Level.SEVERE, null, ex);
      }

      HashMap<String, String> map = new HashMap<String, String>();
      map.put("sgid", sgid.toString());
      return Response.ok().entity(map).build();
  }
  
}