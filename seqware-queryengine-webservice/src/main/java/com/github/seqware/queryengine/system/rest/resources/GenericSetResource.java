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
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author dyuen
 */
public abstract class GenericSetResource<T extends MolSetInterface> extends GenericElementResource<T> {
    
    /**
     * Create a totally new object given a specification without an associated
     * ID. 
     *
     * @param element
     * @return
     */
    @POST
    @ApiOperation(value = "Create a totally new set by JSON" , notes = "This can only be done by an authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(code = INVALID_INPUT, message = "Invalid input")})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSet(
            @ApiParam(value = "Set that needs to be added to the store", required = true) T set) {
        // make this an overrideable method in the real version
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        modelManager.objectCreated(set);
        modelManager.close();
        return Response.ok().entity(set).build();
    }
    
}
