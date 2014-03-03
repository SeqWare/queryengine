package com.github.seqware.queryengine.system.exporters;

import java.util.Date;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.contribs.MutationsToDonorsAggregationPlugin;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.factory.SWQEFactory;

public class ArbitraryPluginRunner {

    /** Constant <code>INPUT_FILES_PARAM='i'</code> */
    public static final char INPUT_FILES_PARAM = 'i';
    /** Constant <code>OUTPUT_FILE_PARAM='o'</code> */
    public static final char OUTPUT_FILE_PARAM = 'o';
    /** Constant <code>REFERENCE_ID_PARAM='r'</code> */
    public static final char REFERENCE_ID_PARAM = 'r';
    /** Constant <code>PLUGIN_CLASS_PARAM='p'</code>*/
    public static final char PLUGIN_CLASS_PARAM = 'p';
    
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
		
		String referenceName = cmd.getOptionValue(REFERENCE_ID_PARAM);
		String plugin = cmd.getOptionValue(REFERENCE_ID_PARAM);
		String outputFile = cmd.getOptionValue(OUTPUT_FILE_PARAM);

		Reference ref = null;
		for (Reference r : SWQEFactory.getQueryInterface().getReferences()){
			if (referenceName.equals(r.getName())){
				ref = r;
				break;
			}
		}

		Class<? extends PluginInterface> arbitraryPluginClass;
		try {
			arbitraryPluginClass = (Class<? extends PluginInterface>) Class.forName(plugin);
	        long start = new Date().getTime();
			System.out.println("Running plugin: " + plugin);
			Utility.dumpFromMapReducePlugin(plugin, ref, null, arbitraryPluginClass, (args.length == 3 ? outputFile : null));
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
		Options option = new Options();
		Option option1 = OptionBuilder.withArgName("outputFile").withDescription("(required output file").hasArgs(1).isRequired.create(OUTPUT_FILE_PARAM);
		options.addOption(option1);
		Option option2 = OptionBuilder.withArgName("reference").withDescription("(required) the reference ID of the FeatureSet to run plugin on").hasArgs(1).isRequired.create(REFERENCE_ID_PARAM);
		options.addOption(option2);
		Option option3 = OptionBuilder.withArgName("pluginClass").withDescription("(required) the plugin to be run, full package path");
		options.addOption(option3);

	}
}
