package com.github.seqware.queryengine.system.exporters;

import java.util.Date;

import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.plugins.PluginInterface;
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

		Class<? extends PluginInterface> arbitraryPluginClass;
		try {
			arbitraryPluginClass = (Class<? extends PluginInterface>) Class.forName(args[1]);
	        long start = new Date().getTime();
			System.out.println("Running plugin: " + args[1]);
			Utility.dumpFromMapReducePlugin(args[1], ref, null, arbitraryPluginClass, (args.length == 3 ? args[2] : null));
	        long stop = new Date().getTime();
	        float diff = ((stop - start) / 1000) / 60;
	        System.out.println("Minutes to query: "+diff);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (ref == null){
			System.out.println("Reference was not found.");
			System.exit(-2);
		}
	}

	public ArbitraryPluginRunner(String[] args){
		this.args = args;
	}
}
