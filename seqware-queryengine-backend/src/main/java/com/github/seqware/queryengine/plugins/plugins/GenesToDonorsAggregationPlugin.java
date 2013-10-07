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
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.plugins.MapperInterface;
import com.github.seqware.queryengine.plugins.ReducerInterface;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * This plug-in implements a quick and dirty export using Map/Reduce
 *
 * TODO: Copy from HDFS and parse key value file to VCF properly.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class GenesToDonorsAggregationPlugin extends PrefilterByAttributesPlugin<Text, Text, Text, Text> {

  private Text text = new Text();
  private Text textKey = new Text();

  @Override
  public Class getMapOutputKeyClass() {
    return Text.class;
  }

  @Override
  public Class getMapOutputValueClass() {
    return Text.class;
  }

  @Override
  public void map(Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<Text, Text> mapperInterface) {
    // map is gene -> List of donors::project
    HashMap<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();
    for (FeatureSet fs : atoms.keySet()) {
      for (Feature f : atoms.get(fs)) {
        String id = null;
        String[] genes = null;
        for (Tag t : f.getTags()) {
          if ("id".equals(t.getKey())) {
            id = t.getValue().toString();
          }
          if ("EnsemblGene".equals(t.getKey())) {
            genes = t.getValue().toString().split(",");
          }
        }
        if (genes != null) {
          for (String gene : genes) {
            //String varID = f.getSeqid()+":"+f.getStart()+"-"+f.getStop()+"_"+ref+"->"+var+"\t"+id;
            ArrayList<String> otherFS = results.get(gene);
            if (otherFS == null) {
              otherFS = new ArrayList<String>();
            }
            // need to convert SGID to hashed value
            Tag tagByKey = fs.getTagByKey("donor");
            String donor = (String)tagByKey.getValue();
            tagByKey = fs.getTagByKey("project");
            String project = (String)tagByKey.getValue();
            String value = donor + "::" + project;
            
            if (!otherFS.contains(value)) {
              otherFS.add(value);
            }
            results.put(gene, otherFS);
          }
        }
      }
    }
    // now iterate and add to results, currVar is gene
    for (String currVar : results.keySet()) {
      boolean first = true;
      StringBuilder valueStr = new StringBuilder();
      for (String currFS : results.get(currVar)) {
        if (first) {
          first = false;
          valueStr.append(currFS);
        } else {
          valueStr.append(",").append(currFS);
        }
      }
      textKey.set(currVar); // key is gene
      text.set(valueStr.toString()); // value is list of donors
      mapperInterface.write(textKey, text); 
    }
  }

  @Override
  public void reduce(Text key, Iterable<Text> values, ReducerInterface<Text, Text> reducerInterface) {
    // val are the values for a given gene, in this case it's a comma sep list
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
      // HELP, not sure what's going in here, why are you writing the text?
      //reducerInterface.write(val, text);
    }
    Text newVal = new Text();
    newVal.set(key.toString() + "\t" + newFeatStr);
    reducerInterface.write(newVal, null);
  }

  @Override
  public ResultMechanism getResultMechanism() {
    return ResultMechanism.FILE;
  }

  @Override
  public Class<?> getResultClass() {
    return File.class;
  }

  @Override
  public Class<?> getOutputClass() {
    return TextOutputFormat.class;
  }
}
