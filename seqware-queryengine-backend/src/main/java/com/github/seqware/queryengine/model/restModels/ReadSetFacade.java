/*
 * Copyright (C) 2013 SeqWare
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
package com.github.seqware.queryengine.model.restModels;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.seqware.queryengine.model.Molecule;
import com.github.seqware.queryengine.model.ReadSet;
import com.github.seqware.queryengine.model.Read;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 *
 * @author dyuen
 */
@JsonDeserialize(as=ReadSet.class)
public interface ReadSetFacade extends MolSetInterface<ReadSet, Read>{


    /**
     * @return the readSetName
     */
    public String getReadSetName();
    
}
