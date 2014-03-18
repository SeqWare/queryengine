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

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.system.exporters.QueryVCFDumper;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiOperation;
import java.util.Map;
import java.util.HashMap;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import com.github.seqware.queryengine.model.QueryVCFParameters;
//import org.apache.commons.lang3.ArrayUtils;

@Path("/query")
@Api(value = "/query", description = "Metadata about this service")
public class QueryVCFResource  {

    @POST
    @Path(value = "/run")
    @ApiOperation(value = "Run the QueryVCFDumper", notes = "Generates a VCF file output according to the user's query")
    @Consumes({"application/json"})
    //@Multipart(value = "root", type = "application/octet-stream") 
    public Response query(
        @ApiParam(value = "parameters", required = true) QueryVCFParameters parameters) {
      HashMap<String, String> map = new HashMap<String, String>();
        if (parameters.getFeatureSetId().equals("") || parameters.getQuery().equals("")) {
          map.put("features", "none");
          return Response.ok().entity(map).build();
        }
        
        String[] params = new String[6];
        if (!parameters.getKeyValue().equals("")) {
          params = new String[8];
        }
        params[0] = "-f";
        params[1] = parameters.getFeatureSetId();
        params[2] = "-s";
        params[3] = parameters.getQuery();
        if (!parameters.getOutputFile().equals("")) {
          params[4] = "-o";
          params[5] = "/tmp/" + parameters.getOutputFile();
        } else {
          params[4] = "-o";
          params[5] = "/tmp/output.vcf";
        }
        if (!parameters.getKeyValue().equals("")) {
          params[7] = "-k";
          params[8] = parameters.getKeyValue();
        }
            
        QueryVCFDumper dumper = new QueryVCFDumper();
        /*String[] params = new String[6];
        params[0] = "-f";
        params[1] = "cd9709eb-1844-4220-aaac-b9d1dde11d1c";
        params[2] = "-s";
        params[3] = "seqid==\"1\"";
        params[4] = "-o";
        params[5] = "/tmp/output.vcf";*/
        dumper.runMain(params);
        File f = new File("/tmp/" + parameters.getOutputFile());
        if (parameters.getOutputFile().equals("")) {
          f = new File("/tmp/output.vcf");
        }
        
        return Response.ok(f, "application/octet-stream").build();
    }
    
    /*
    @GET
    @Path(value = "/download/{name}")
    @ApiOperation(value = "Run the QueryVCFDumper", notes = "Download the generated VCF file") 
    public Response query(
        @ApiParam(value = "File Name", required = true)
        @PathParam(value = "name") String name) {
        
        File f = new File("/tmp/" + name);
        return Response.ok(f, "application/octet-stream").build();
    }*/
}
