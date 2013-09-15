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
import com.github.seqware.queryengine.system.exporters.VCFDumper;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * This plug-in implements a quick and dirty export using Map/Reduce
 *
 * TODO: Copy from HDFS and parse key value file to VCF properly.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class VCFDumperPlugin extends MapReducePlugin<VCFDumperPlugin.SerializableText, VCFDumperPlugin.SerializableText, VCFDumperPlugin.SerializableText, VCFDumperPlugin.SerializableText, VCFDumperPlugin.SerializableText, VCFDumperPlugin.SerializableText, File> {

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

    @Override
    public int getNumReduceTasks() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getInternalParameters() {
        return new Object[0];
    }

  @Override
  public void map(Collection<Feature> atom, MapperInterface<SerializableText, SerializableText> mapperInterface) {
    //do nothing
  }

    @Override
    public void map(Map<String, Collection<Feature>> collections, MapperInterface<SerializableText, SerializableText> mapperInterface) {
      HashMap<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();
      for(String fs : collections.keySet()) {
        for (Feature f : collections.get(fs)) {
          String ref = null;
          String var = null;
          String id = null;
          for(Tag t : f.getTags()) {
              //buffer.append(t.getKey()+"="+t.getValue()+":");
              if ("ref_base".equals(t.getKey())) { ref = t.getValue().toString(); }
              if ("call_base".equals(t.getKey())) { var = t.getValue().toString(); }
              if ("id".equals(t.getKey())) { id = t.getValue().toString(); }
          }
          String varID = f.getSeqid()+":"+f.getStart()+"-"+f.getStop()+"_"+ref+"->"+var+"\t"+id;
          ArrayList otherFS = results.get(varID);
          if (otherFS == null) { otherFS = new ArrayList<String>(); }
          if (!otherFS.contains(f)) { otherFS.add(fs); }
          results.put(varID, otherFS);
        }
      }
      // now iterate and add to results
      for (String currVar : results.keySet()) {
        boolean first = true;
        StringBuilder valueStr = new StringBuilder();
        for (String currFS : results.get(currVar)) {
          
          if (first) { first=false; valueStr.append(currFS); }
          else { valueStr.append(","+currFS); }
        }
        textKey.set(currVar);
        text.set(currVar+"\t"+valueStr.toString());
        mapperInterface.write(textKey, text); 
      }
    }

    @Override
    public void reduce(SerializableText key, Iterable<SerializableText> values, ReducerInterface<SerializableText, SerializableText> reducerInterface) {
        for (SerializableText val : values) {
          // HELP, not sure what's going in here, why are you writing the text?
          //reducerInterface.write(val, text);
          reducerInterface.write(val, null);
        }
    }

    @Override
    public ResultMechanism getResultMechanism() {
        return ResultMechanism.FILE;
    }

    @Override
    public Class<?> getResultClass() {
        return File.class;
    }
    
    public static class SerializableText extends Text implements Serializable{
        public SerializableText(){
            super();
        }
    }
    
    @Override
    public Class<?> getOutputClass() {
        return TextOutputFormat.class;
    }
}
