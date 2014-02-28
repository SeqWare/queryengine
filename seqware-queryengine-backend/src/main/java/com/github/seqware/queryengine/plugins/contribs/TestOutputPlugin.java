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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Text;

/**
 * This plug-in implements a quick and dirty export using Map/Reduce
 *
 *
 * @author bso
 * @version $Id: $Id
 */
public class TestOutputPlugin extends FilteredFileOutputPlugin{
   	private Text text = new Text();
    private Text textKey = new Text();
    Set<Feature> featuresAtCurrentLocation = new HashSet<Feature>();
    @Override
	public void map(long position, Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<Text, Text> mapperInterface) {
		for (FeatureSet fs : atoms.keySet()){
			for (Feature f : atoms.get(fs)){
				if (f.getStart() == position){
					featuresAtCurrentLocation.add(f);
				}
			}
		}
		
		for (FeatureSet fs : atoms.keySet()){
			for (Feature f : atoms.get(fs)){
				
				for (Feature positionFeature : featuresAtCurrentLocation){
					String indelRange = convertToIndelRange(positionFeature.getStart(), positionFeature.getStop());
					String indelStart = convertLongToString(positionFeature.getStart());
					text.set(indelRange);
					textKey.set(indelStart);
					mapperInterface.write(textKey, text);
				}
			}
		}
		
	}
	@Override
	public void reduce(Text reduceKey, Iterable<Text> reduceValues, ReducerInterface<Text, Text> reducerInterface) {
		Set<Text> seenSet = new HashSet<Text>();
		String newFeatStr = "";
		for (Text val : reduceValues){
			seenSet.add(val);
			String[] fsArr = val.toString().split(",");
			for (String curr : fsArr){
				newFeatStr += curr;
			}
		}
		text.set(reduceKey.toString() + "\t" + newFeatStr);
		reducerInterface.write(text,null);
		
	}
	
	private String convertToIndelRange(long start, long stop){
		String startPos = String.valueOf(start);
		String endPos = String.valueOf(stop);
		return(startPos.substring(0, startPos.indexOf(".")) + 
				endPos.substring(0, endPos.indexOf(".")));
	}
	
	private String convertLongToString(long start){
		String startPos = String.valueOf(start);
		return(startPos.substring(0, startPos.indexOf(".")));
	}
}