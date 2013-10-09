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
import com.github.seqware.queryengine.plugins.runners.ReducerInterface;
import com.github.seqware.queryengine.plugins.recipes.FilteredFileOutputPlugin;
import com.github.seqware.queryengine.system.exporters.VCFDumper;
import java.util.Collection;
import java.util.Map;
import org.apache.hadoop.io.Text;

/**
 * This plug-in implements a quick and dirty export using Map/Reduce
 *
 * TODO: Copy from HDFS and parse key value file to VCF properly.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class VCFDumperPlugin extends FilteredFileOutputPlugin {

    private Text text = new Text();
    private Text textKey = new Text();
    
    /** {@inheritDoc} */
    @Override
    public FeatureFilter getFilter() {
        // we create a filter that passes all features in order to export all features
        return new FeaturesAllPlugin.FeaturesAllFilter();
    }

    @Override
    public void map(Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<Text, Text> mapperInterface) {
        for (Feature f : atoms.values().iterator().next()) {
            StringBuilder buffer = new StringBuilder();
            VCFDumper.outputFeatureInVCF(buffer, f);
            text.set(buffer.toString());     
            textKey.set(f.getSGID().getRowKey());
            // the map function emits SGID , rows of a VCF file in pairs
            mapperInterface.write(textKey, text);
        }
    }

    @Override
    public void reduce(Text key, Iterable<Text> values, ReducerInterface<Text, Text> reducerInterface) {
        // the reducer simply emits the rows of the VCF file
        for (Text val : values) {
            reducerInterface.write(val, text);
        }
    }
}
