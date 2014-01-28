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
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.restModels.FeatureSetFacade;
import com.github.seqware.queryengine.system.exporters.VCFDumper;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_ID;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_INPUT;
import static com.github.seqware.queryengine.system.rest.resources.GenericElementResource.INVALID_SET;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;

/**
 * FeatureSet
 * resource.
 *
 * @author
 * dyuen
 */
@Path("/featureset")
@Api(value = "/featureset", description = "Operations about featuresets"/**
         * ,
         * listingPath
         * =
         * "/resources/featureset"
         */
        )
@Produces({"application/json"})
public class FeatureSetResource extends GenericSetResource<FeatureSetFacade> {

  @Override
  public final String getClassName() {
    return "FeatureSet";
  }

  @Override
  public final Class getModelClass() {
    return FeatureSet.class;
  }

  @Override
  public final SeqWareIterable getElements() {
    return SWQEFactory.getQueryInterface().getFeatureSets();
  }

  /**
   * Create
   * new
   * pluginrun
   * event
   * to
   * create
   * a
   * query,
   * monitor
   * the
   * query,
   * and
   * return
   * a
   * new
   * feature
   * set
   * when
   * ready.
   *
   * @param
   * sgid
   * rowkey
   * of
   * featureset
   * to
   * operate
   * on
   * @param
   * query
   * query
   * in
   * our
   * query
   * language
   * @param
   * ttl
   * time
   * in
   * hours
   * for
   * the
   * results
   * to
   * live
   * @return
   */
  @POST
  @Path("/{sgid}/query")
  @ApiOperation(value = "Create new pluginrun event to monitor query", notes = "This can only be done by an authenticated user.", position = 100)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  public Response runQuery(
          @ApiParam(value = "rowkey that needs to be updated", required = true)
          @PathParam("sgid") String sgid,
          @ApiParam(value = "query", required = true)
          @QueryParam(value = "query") String query,
          @ApiParam(value = "ttl", required = false)
          @QueryParam(value = "ttl") int ttl) {
    // make this an overrideable method in the real version
    //userData.addUser(user);
    return Response.ok().entity("").build();
  }

  /*
   * Return
   * the
   * features
   * that
   * belong
   * to
   * the
   * specified
   * feature
   * set
   * in
   * VCF
   * 
   * FIXME: the Swagger API will not send the correct Content-Type header (text/plain) to get this resouces
   * and it, as a result, hits the JSON resource instead.  See https://github.com/ryankennedy/swagger-jaxrs-doclet/issues/44
   * Not sure if there's a way to make this work and I just don't know the magic syntax.  In the mean time use a
   * tool like "Dev HTTP Client" chrome plugin which allows you to add the content type header.
   *
   * @param
   * sgid
   * rowkey
   * of
   * featureset
   * to
   * operate
   * on
   * @return
   */
  @GET
  @Path("/{sgid}")
  @ApiOperation(value = "List features in a featureset in VCF", notes = "This can only be done by an authenticated user.", position = 70)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  @Produces(MediaType.TEXT_PLAIN)
  public Response getVCFFeatureListing(
          @ApiParam(value = "rowkey that needs to be updated", required = true)
          @PathParam("sgid") String sgid) throws InvalidIDException {
    // make this an overrideable method in the real version
    //userData.addUser(user);
    final FeatureSet set = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, FeatureSet.class);
    if (set == null) {
      throw new InvalidIDException(INVALID_ID, "ID not found");
    } else {

      try {

        StreamingOutput stream = new StreamingOutput() {
          @Override
          public void write(OutputStream os) throws IOException, WebApplicationException {
           
            BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(os));
            
            // FIXME: need a cleaner way to call the dumper code
            VCFDumper.dumpVCFFromFeatureSetToStream(set, bos, false);
            
            /* Writer writer = new BufferedWriter(new OutputStreamWriter(os));
            //writer.write(readSet.getHeader().getTextHeader());
            Iterator<Feature> iterator = set.getFeatures();
            while (iterator.hasNext()) {
              Feature f = iterator.next();
              writer.write(f.getSeqid() + ":" + f.getStart() + "-" + f.getStop() + "\n");
            }
            writer.flush();*/
          }
        };

        return Response.ok(stream).build();

      } catch (Exception ex) {
        Logger.getLogger(ReadSetResource.class.getName()).log(Level.SEVERE, null, ex);
      }

    }
    return Response.ok().entity("").build();
  }

  /**
   * Return
   * a
   * specific
   * feature
   * in
   * JSON
   *
   * @param
   * sgid
   * rowkey
   * of
   * featureset
   * to
   * operate
   * on
   * @return
   */
  @GET
  @Path("/{sgid}/{fsgid}")
  @ApiOperation(value = "Get a specific feature in a featureset in JSON", notes = "This can only be done by an authenticated user.", position = 80)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getJSONFeature(
          @ApiParam(value = "rowkey of featureset to find feature in", required = true)
          @PathParam("sgid") String sgid,
          @ApiParam(value = "rowkey of feature", required = true)
          @QueryParam(value = "sgid") String fsgid) {
    // make this an overrideable method in the real version
    //userData.addUser(user);
    return Response.ok().entity("").build();
  }

  /**
   * Upload
   * a
   * raw
   * variant
   * file
   * to
   * create
   * a
   * new
   * featureset
   *
   * @param
   * sgid
   * rowkey
   * of
   * ontology
   * to
   * create
   * @return
   */
  @POST
  @ApiOperation(value = "Create a new featureset with a raw data file", notes = "This can only be done by an authenticated user.", position = 110)
  @ApiResponses(value = {
    @ApiResponse(code = RESOURCE_EXISTS, message = "Resource already exists")})
  @Consumes(MediaType.TEXT_PLAIN)
  public Response uploadRawVCFfile(
          @ApiParam(value = "format of input", required = true, allowableValues = "VCF,GFF3,GVF")
          @DefaultValue(value = "VCF")
          @QueryParam(value = "format") String format,
          @ApiParam(value = "VCF-formated body that needs to be created", required = true) String body) {

    try {
      /*
       * FIXME: this is a really naive approach, just write it out as a file and load using
       * the import tool from the query engine backend. In the future this should be an asyncrhonous 
       * process with the file uploaded to a admin configured location possibly on HDFS then 
       * the upload should take place as a plugin, reporting back a token that the calling 
       * user can occationally check in on.
       */
      BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/foo.vcf"));
      bw.write(body);
      bw.close();
      // FIXME: totally hard coded!
      FeatureImporter.naiveRun(new String[]{"VCFVariantImportWorker", "1", "false", "hg19", "/tmp/foo.vcf"});

    } catch (IOException ex) {
      Logger.getLogger(FeatureSetResource.class.getName()).log(Level.SEVERE, null, ex);
    }

    return Response.ok().entity("").build();

    /* CreateUpdateManager modelManager = SWQEFactory.getModelManager();
     modelManager.objectCreated(set);
     modelManager.close();
     return Response.ok().entity(set).build();*/

  }

  @GET
  @Override
  @Path(value = "/{sgid}")
  @ApiOperation(value = "Find a specific element by rowkey in JSON", notes = "Add extra notes here", response = FeatureSet.class, position = 90)
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
  @ApiOperation(value = "Update an existing element", notes = "This can only be done by an authenticated user.", position = 230)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  @Override
  public Response updateElement(
          @ApiParam(value = "rowkey that need to be deleted", required = true) @PathParam("sgid") String sgid,
          @ApiParam(value = "Updated user object", required = true) FeatureSetFacade group) {

    return super.updateElement(sgid, group);

  }

  /**
   * Update
   * an
   * existing
   * element.
   *
   * @param
   * sgid
   * @param
   * user
   * @return
   */
  @DELETE
  @Path("/{sgid}")
  @ApiOperation(value = "Delete an existing FeatureSet", notes = "This can only be done by an authenticated user.", position = 310)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_ID, message = "Invalid element supplied"),
    @ApiResponse(code = INVALID_SET, message = "Element not found")})
  @Override
  public Response deleteElement(
          @ApiParam(value = "rowkey that need to be deleted", required = true) @PathParam("sgid") String sgid) {
    return super.deleteElement(sgid);
  }

  @POST
  @ApiOperation(value = "Create a totally new FeatureSet by JSON", notes = "This can only be done by an authenticated user.", position = 50000)
  @ApiResponses(value = {
    @ApiResponse(code = INVALID_INPUT, message = "Invalid input")})
  @Consumes(MediaType.APPLICATION_JSON)
  @Override
  public Response addSet(
          @ApiParam(value = "ReferenceSet that needs to be added to the store", required = true) FeatureSetFacade set) {
    return super.addSet(set);
  }
}
