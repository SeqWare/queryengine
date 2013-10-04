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
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.MapperInterface;
import com.github.seqware.queryengine.plugins.ReducerInterface;
import com.github.seqware.queryengine.plugins.plugins.DonorsToMutationsAndGenesAggregationPlugin.SerializableText;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
public class DonorsToMutationsAndGenesAggregationPlugin extends PrefilterByAttributesPlugin<SerializableText, SerializableText, SerializableText, SerializableText, File> {

  private SerializableText text = new SerializableText();
  private SerializableText textKey = new SerializableText();

  @Override
  public Class getMapOutputKeyClass() {
    return SerializableText.class;
  }

  @Override
  public Class getMapOutputValueClass() {
    return SerializableText.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getInternalParameters() {
    return new Object[0];
  }

  @Override
  public void map(Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<SerializableText, SerializableText> mapperInterface) {
    
    // the key is donorID, the value is a hash of mutation ID -> geneArray
    Map<FeatureSet, HashMap<String, ArrayList<String>>> results = new HashMap<FeatureSet, HashMap<String, ArrayList<String>>>();
    
    // for each feature set corresponding to a donor
    for (FeatureSet fs : atoms.keySet()) {
      
      // for each mutation
      for (Feature f : atoms.get(fs)) {
        // mutation ID
        String id = null;
        // gene ID
        ArrayList<String> genes = new ArrayList<String>();
        for (Tag t : f.getTags()) {
          if ("id".equals(t.getKey())) {
            id = t.getValue().toString();
          }
          if ("EnsemblGene".equals(t.getKey())) {
              genes.addAll(Arrays.asList(t.getValue().toString().split(",")));
          }
        }
        // now load this feature set (donor) -> mutation id -> genes list 
        HashMap<String, ArrayList<String>> value = results.get(fs);
        if (value == null) {
          value = new HashMap<String, ArrayList<String>>();
        }
        value.put(id, genes);
        
        results.put(fs, value);
        
      }
    }
    
    // now iterate and add to results, start with feature_set
    for (FeatureSet fs : results.keySet()) {
      boolean first = true;
      StringBuilder valueStr = new StringBuilder();
      //valueStr.append(fs+"\t");
      for (String mutation : results.get(fs).keySet()) {
        // formating
        if (first) {
          first = false;
        } else {
          valueStr.append(";");
        }
        valueStr.append(mutation).append("::");
        boolean first2 = true;
        StringBuilder geneList = new StringBuilder();
        for (String gene : results.get(fs).get(mutation)) {
          if (first2) {
            first2 = false;
          } else {
            valueStr.append(",");
          }
          valueStr.append(gene);
        }
      }
      Tag tagByKey = fs.getTagByKey("donor");
      String donor = (String)tagByKey.getValue();
      textKey.set(donor);
      text.set(valueStr.toString());
      mapperInterface.write(textKey, text);
    }
  }

  @Override
  public void reduce(SerializableText key, Iterable<SerializableText> values, ReducerInterface<SerializableText, SerializableText> reducerInterface) {
    // key is feature set, value is mutation->gene that can just be cat'd
    SerializableText newVal = new SerializableText();
    StringBuilder newValSB = new StringBuilder();
    newValSB.append(key).append("\t");
    boolean first = true;
    for (SerializableText val : values) {
      if (first) {
        first = false;
      } else {
        newValSB.append(";");
      }
      newValSB.append(val.toString());
    }
    newVal.set(newValSB.toString());
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

  public static class SerializableText extends Text implements Serializable {

    public SerializableText() {
      super();
    }
  }

  @Override
  public Class<?> getOutputClass() {
    return TextOutputFormat.class;
  }
}
