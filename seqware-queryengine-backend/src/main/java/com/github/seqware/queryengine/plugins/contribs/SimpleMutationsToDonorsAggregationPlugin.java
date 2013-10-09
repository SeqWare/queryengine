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
package com.github.seqware.queryengine.plugins.contribs;

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.runners.MapperInterface;
import com.github.seqware.queryengine.plugins.runners.ReducerInterface;
import com.github.seqware.queryengine.plugins.recipes.FilteredFileOutputPlugin;
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
public class SimpleMutationsToDonorsAggregationPlugin extends FilteredFileOutputPlugin {

    private Text text = new Text();
    private Text textKey = new Text();

    @Override
    public void map(Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<Text, Text> mapperInterface) {
        // map is mutation \t mutation ID ->   List of donor::project
        // "10:100008435-100008436_G->A   MU1157731" -> {"DO29264::LAML-KR","DO29242::LAML-KR","DO14015::COAD-US"}
        for (FeatureSet fs : atoms.keySet()) {
            for (Feature f : atoms.get(fs)) {
                // create "10:100008435-100008436_G->A"
                String ref = f.getTagByKey("ref_base").getValue().toString();
                String var = f.getTagByKey("call_base").getValue().toString();
                String id = f.getTagByKey("id").getValue().toString();     
                // varID becomes "10:100008435-100008436_G->A MU1157731"
                String varID = f.getSeqid() + ":" + f.getStart() + "-" + f.getStop() + "_" + ref + "->" + var + "\t" + id;
                // create DO29264::LAML-KR
                String donor = (String) fs.getTagByKey("donor").getValue();
                String project = (String) fs.getTagByKey("project").getValue();
                String value = donor + "::" + project;
                textKey.set(varID);
                text.set(value.toString());
                // ( "10:100008435-100008436_G->A MU1157731" , "DO29264::LAML-KR,DO29242::LAML-KR,DO14015::COAD-US")
                mapperInterface.write(textKey, text);
            }
        }
    }
    
    @Override
    public void reduce(Text key, Iterable<Text> values, ReducerInterface<Text, Text> reducerInterface) {
        // values 
        String newFeatStr = "";
        boolean first = true;
        for (Text val : values) {
            String[] fsArr = val.toString().split(",");
            for (String currFS : fsArr) {
                if (first) {
                    first = false;
                    newFeatStr += currFS;
                } else {
                    newFeatStr += "," + currFS;
                }
            }
        }

        text.set(key.toString() + "\t" + newFeatStr);
        reducerInterface.write(text, null);
    }
}
