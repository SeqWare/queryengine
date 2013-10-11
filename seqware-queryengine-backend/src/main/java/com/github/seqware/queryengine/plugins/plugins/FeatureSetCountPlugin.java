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
package com.github.seqware.queryengine.plugins.plugins;

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.runners.MapperInterface;
import com.github.seqware.queryengine.plugins.recipes.LongValuePlugin;
import com.github.seqware.queryengine.plugins.runners.ReducerInterface;
import java.util.Collection;
import java.util.Map;

/**
 * Counts the number of Features in a FeatureSet
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class FeatureSetCountPlugin extends LongValuePlugin {

    @Override
    public void map(long position, Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<Object, Object> mapperInterface) {
        for (Feature f : atoms.values().iterator().next()) {
            
            if (f.getStart() != position){
                continue;
            }
            // why can't I increment this by the size directly on the cluster?
            mapperInterface.incrementCounter();
        }
    }

    @Override
    public void reduce(Object reduceKey, Iterable<Object> reduceValues, ReducerInterface<Object, Object> reducerInterface) {
        /** do nothing */
    }
}
