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
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author dyuen
 */
public abstract class GenericMutableSetResource<T extends MolSetInterface, S extends Atom> extends GenericSetResource<T> {
    
    /**
     * Create a totally new element inside the set given a specification without an associated
     * ID.
     * 
     * Unfortunately, you will need to copy these annotations and add a response of class S in your subclass.
     * Java annotations do not inherit and Swagger doesn't seem to handle generic response classes 
     * https://groups.google.com/forum/#!topic/swagger-swaggersocket/eO2d6qD9K9g
     *
     * @param element
     * @return
     */
    @POST
    @Path("/{sgid}")
    @ApiOperation(value = "Create an element in the set" , notes = "This can only be done by an authenticated user.")
    //,response=User.class)
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_INPUT, message = "Invalid input")})
    public Response addElement(
            @ApiParam(value = "set to add an element to", required = true)  @PathParam("rowKey") String rowKey,
            @ApiParam(value = "element that needs to be added to the store", required = true) S element) {
        // make this an overrideable method in the real version
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        MolSetInterface latestAtomByRowKey = (MolSetInterface)SWQEFactory.getQueryInterface().getLatestAtomByRowKey(rowKey, getModelClass());
        latestAtomByRowKey.add(element);
        modelManager.objectCreated(element);
        modelManager.close();
        
        return Response.ok().entity("SUCCESS").build();
    }
}
