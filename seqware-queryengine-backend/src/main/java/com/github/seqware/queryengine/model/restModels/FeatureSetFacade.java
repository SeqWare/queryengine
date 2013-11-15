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
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.impl.hbasemrlazy.MRLazyFeatureSet;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 *
 * @author dyuen
 */
@JsonDeserialize(as=MRLazyFeatureSet.class)
public interface FeatureSetFacade extends MolSetInterface<FeatureSet, Feature>{


    /**
     * Get the description associated with this FeatureSet
     *
     * @return the description associated with this FeatureSet
     */
    public abstract String getDescription();
    
    /**
     * Get the reference for this featureSet
     *
     * @return reference for the feature set
     */
    public abstract Reference getReference();
    
}
