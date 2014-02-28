package com.github.seqware.queryengine.system.exporters;

import java.util.Date;

import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.plugins.contribs.MutationsToDonorsAggregationPlugin;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.factory.SWQEFactory;

public class ArbitraryPluginRunner {

	private String[] args;
	
	public static void main(String[] args) {
		ArbitraryPluginRunner dump = new ArbitraryPluginRunner(args);
		dump.export();
	}
	
	public void export(){
		if (args.length < 3){
			System.err.println(args.length + " arguments found.");
			System.out.println("VCFDumper <reference name> <class name> <outputfile>");
			System.exit(-1);
		}
		
		String referenceName = args[0];
		Reference ref = null;
		for (Reference r : SWQEFactory.getQueryInterface().getReferences()){
			if (referenceName.equals(r.getName())){
				ref = r;
				break;
			}
		}
		
        // aggregations of donors/project counts by mutation
        System.out.println("Finding Mutations to affected donors/project count aggregation");
        long start = new Date().getTime();
        Utility.dumpFromMapReducePlugin("MUTATION\tMUTATION_ID\tDONORS/PROJECTS_AFFECTED\n", ref, null, MutationsToDonorsAggregationPlugin.class, (args.length == 3 ? args[2] : null));
        long stop = new Date().getTime();
        float diff = ((stop - start) / 1000) / 60;
        System.out.println("Minutes to query: "+diff);
		
		if (ref == null){
			System.out.println("Reference was not found.");
			System.exit(-2);
		}
	}
	

	
	public ArbitraryPluginRunner(String[] args){
		this.args = args;
	}
}
