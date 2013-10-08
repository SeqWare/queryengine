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
package com.github.seqware.queryengine.plugins.recipes;

import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.PluginInterface;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

/**
 * This kind of output emits a single value. 
 * Increment counters via the MapperInterface, do nothing for the Reducer, and then 
 * output that one Long value
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class LongValuePlugin extends MapReducePlugin<Object, Object, Object, Object> {

    @Override
    public ResultMechanism getResultMechanism() {
        return PluginInterface.ResultMechanism.COUNTER;
    }

    @Override
    public Class<?> getResultClass() {
        return Long.class;
    }
    
    @Override
    public Class<?> getOutputClass() {
        return NullOutputFormat.class;
    }
}
