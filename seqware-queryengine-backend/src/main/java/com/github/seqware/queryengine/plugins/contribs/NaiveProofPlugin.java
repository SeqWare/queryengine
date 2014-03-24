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

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.runners.MapperInterface;
import com.github.seqware.queryengine.plugins.runners.ReducerInterface;
import com.github.seqware.queryengine.plugins.recipes.FilteredFileOutputPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

/**
 * This plug-in implements exporting using Map/Reduce, to prove that there can be naively imported 
 * data in the backend and a plugin that recognizes this.
 *
 * @author bso
 * @version $Id: $Id
 */
public class NaiveProofPlugin extends FilteredFileOutputPlugin{
    
   	private Text text = new Text();
    private Text textKey = new Text();
    
    @Override
	public void map(long position, Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<Text, Text> mapperInterface) {
    	Set<Feature> featuresAtCurrentLocation = new HashSet<Feature>();
        Logger.getLogger(NaiveProofPlugin.class.getName()).info("Mapping.........");
		for (FeatureSet fs : atoms.keySet()){
			for (Feature f : atoms.get(fs)){
				if ((f.getStart() < position && f.getStop() > position) || (f.getStart() == position)){
                                        Logger.getLogger(NaiveProofPlugin.class.getName()).info("Adding feature for mapping at valid position: " +  f.getDisplayName());
					featuresAtCurrentLocation.add(f);
				}
			}
		}
		
		for (FeatureSet fs : atoms.keySet()){
			for (Feature f : atoms.get(fs)){
				Logger.getLogger(NaiveProofPlugin.class.getName()).info("Size of added features...: "+  featuresAtCurrentLocation.size());
				for (Feature positionFeature : featuresAtCurrentLocation){
					Logger.getLogger(NaiveProofPlugin.class.getName()).info("In the loop.. getting start pos: " +positionFeature.getStart());
					String indelRange = convertToIndelRange(positionFeature.getStart(), positionFeature.getStop());
					Logger.getLogger(NaiveProofPlugin.class.getName()).info("indelRange...(VALUE): " + indelRange);
					String indelStart = convertLongToString(position);
					Logger.getLogger(NaiveProofPlugin.class.getName()).info("indelStart...(KEY): " + indelStart);
					text.set(indelRange);
					textKey.set(indelStart);
					Logger.getLogger(NaiveProofPlugin.class.getName()).info("Running mapperInterface");
					mapperInterface.write(textKey, text);
				}
			}
		}
		
	}
	@Override
	public void reduce(Text reduceKey, Iterable<Text> reduceValues, ReducerInterface<Text, Text> reducerInterface) {
		Logger.getLogger(NaiveProofPlugin.class.getName()).info("Reducing.......");
		Set<Text> seenSet = new HashSet<Text>();
		String newFeatStr = "";
		for (Text val : reduceValues){
            if (seenSet.contains(val)){
                continue;
            }
			seenSet.add(val);
			String[] fsArr = val.toString().split(",");
			for (String curr : fsArr){
				newFeatStr += curr + "; ";
			}
			Logger.getLogger(NaiveProofPlugin.class.getName()).info("REDUCED: "+ newFeatStr);
		}
		text.set("\n" + reduceKey.toString() + "\t" + newFeatStr);
		Logger.getLogger(NaiveProofPlugin.class.getName()).info("Running reducerInterface");
		reducerInterface.write(text,null);
		
	}
	
	public static String convertToIndelRange(long start, long stop){
		String startPos = String.valueOf(start);
		String endPos = String.valueOf(stop);
		return(startPos + "-" + endPos);
	}
	
	public static String convertLongToString(long start){
		String startPos = String.valueOf(start);
		return (startPos);
	}
}