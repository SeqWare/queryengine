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
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.plugins.runners.MapperInterface;
import com.github.seqware.queryengine.plugins.runners.ReducerInterface;
import com.github.seqware.queryengine.plugins.recipes.FilteredFileOutputPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.hadoop.io.Text;

/**
 * This plug-in implements a quick and dirty export using Map/Reduce
 *
 * TODO: Copy from HDFS and parse key value file to VCF properly.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class MutationsToDonorsAggregationPlugin extends FilteredFileOutputPlugin {

    private Text text = new Text();
    private Text textKey = new Text();

    @Override
    public void map(long position, Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<Text, Text> mapperInterface) {
      // map is mutation \t mutation ID ->   List of donor::project
      // "10:100008435-100008436_G->A   MU1157731" -> {"DO29264::LAML-KR","DO29242::LAML-KR","DO14015::COAD-US"}
      HashMap<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();
      for(FeatureSet fs : atoms.keySet()) {
        for (Feature f : atoms.get(fs)) {
            
            if (f.getStart() != position){
                continue;
            }
            
          // create "10:100008435-100008436_G->A"
          String ref = null;
          String var = null;
          String id = null;
          // example of iterating through tags 
          for(Tag t : f.getTags()) {
              if ("ref_base".equals(t.getKey())) { ref = t.getValue().toString(); }
              if ("call_base".equals(t.getKey())) { var = t.getValue().toString(); }
              if ("id".equals(t.getKey())) { id = t.getValue().toString(); }
          }
          // varID becomes "10:100008435-100008436_G->A MU1157731"
          String varID = f.getSeqid()+":"+f.getStart()+"-"+f.getStop()+"_"+ref+"->"+var+"\t"+id;
          ArrayList<String> otherFS = results.get(varID);
          if (otherFS == null) { otherFS = new ArrayList<String>(); }
          // create DO29264::LAML-KR
          Tag tagByKey = fs.getTagByKey("donor");
          String donor = (String)tagByKey.getValue();
          tagByKey = fs.getTagByKey("project");
          String project = (String)tagByKey.getValue();
          String value = donor + "::" + project;
          if (!otherFS.contains(value)) { otherFS.add(value); }
          results.put(varID, otherFS);
        }
      }
      // now iterate and emit into map/reduce 
      for (Entry<String, ArrayList<String>> entry : results.entrySet()) {
        boolean first = true;
        StringBuilder valueStr = new StringBuilder();
        for (String currFS : entry.getValue()) {
          if (first) { first=false; valueStr.append(currFS); }
          else { valueStr.append(",").append(currFS); }
        }
        textKey.set(entry.getKey());
        text.set(entry.getKey()+"\t"+valueStr.toString());
        // ( "10:100008435-100008436_G->A MU1157731" , "10:100008435-100008436_G->A MU1157731 DO29264::LAML-KR,DO29242::LAML-KR,DO14015::COAD-US")
        mapperInterface.write(textKey, text); 
      }
    }
    
    @Override
    public void reduce(Text key, Iterable<Text> values, ReducerInterface<Text, Text> reducerInterface) {
        // values 
        for (Text val : values) {
          String[] valArr = val.toString().split("\t");
          String[] fsArr = valArr[2].split(",");
          String newFeatStr = "";
          boolean first = true;
          for(String currFS : fsArr) {
            if (first) { first = false; newFeatStr += currFS; }
            else { newFeatStr += ","+currFS; }
          }
          
          val.set(valArr[0]+"\t"+valArr[1]+"\t"+newFeatStr);
          reducerInterface.write(val, null);
        }
    }
}
