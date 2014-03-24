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
package com.github.seqware.queryengine.plugins.runners.hbasemr;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.kernel.RPNStack.FeatureAttribute;
import com.github.seqware.queryengine.kernel.RPNStack.Parameter;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.plugins.recipes.FilteredFileOutputPlugin;
import com.github.seqware.queryengine.plugins.runners.JobRunParameterInterface;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.runners.MapperInterface;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.runners.PluginRunnerInterface;
import com.github.seqware.queryengine.plugins.runners.ReducerInterface;
import com.github.seqware.queryengine.plugins.plugins.FeatureFilter;
import com.github.seqware.queryengine.plugins.plugins.FeatureSetCountPlugin;
import com.github.seqware.queryengine.plugins.PrefilteredPlugin;

import static com.github.seqware.queryengine.util.FSGID.PositionSeparator;

import com.github.seqware.queryengine.system.exporters.QueryVCFDumper;
import com.github.seqware.queryengine.util.SGID;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;

import net.sourceforge.seqware.common.util.Rethrow;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableInputFormatBase;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.mapreduce.TableSplit;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.log4j.Logger;

/**
 * Abstract implementation of an abstract map/reduce-based plug-in runner for a
 * HBase back-end.
 *
 * @author dyuen, bso
 * @version $Id: $Id
 * @param <ReturnType>
 */
public final class MRHBasePluginRunner<ReturnType> implements PluginRunnerInterface<ReturnType> {

    /**
     * Constant
     * <code>INT_PARAMETERS="int_parameters"</code>
     */
    public static final String INT_PARAMETERS = "int_parameters";
    /**
     * Constant
     * <code>EXT_PARAMETERS="ext_parameters"</code>
     */
    public static final String EXT_PARAMETERS = "ext_parameters";
    public static final int EXTERNAL_PARAMETERS = 0;
    public static final int INTERNAL_PARAMETERS = 1;
    public static final int NUM_AND_SOURCE_FEATURE_SETS = 2;
    public static final int DESTINATION_FEATURE_SET = 3;
    public static final int SETTINGS_MAP = 4;
    public static final int PLUGIN_CLASS = 5;
    private static final int START_LIST = 0;
    private static final int STOP_LIST = 1;
    private static final int SEQ_LIST = 2;
    private static boolean START_STOP_PAIRS_EXIST = false;
    private static List<FeatureSet> thisInputSet = new ArrayList<FeatureSet>();
    private static Object[] thisParameter = new Object[0];
    private static List<String> allSeqIDs = new ArrayList<String>(){{
    	add("1");
	    add("2");
	    add("3");
	    add("4");
	    add("5");
	    add("6");
	    add("7");
	    add("8");
	    add("9");
	    add("10");
	    add("11");
	    add("12");
	    add("13");
	    add("14");
	    add("15");
	    add("16");
	    add("17");
	    add("18");
	    add("19");
	    add("20");
	    add("21");
	    add("22");
	    add("X");
	    add("Y");}};
    
    public static List<FeatureSet> convertBase64StrToFeatureSets(final String sourceSets) {
        byte[] data = (byte[]) Base64.decodeBase64(sourceSets);
        ByteBuffer buf = ByteBuffer.wrap(data);
        int numSets = buf.getInt();
        List<FeatureSet> sSets = new ArrayList<>();
        for(int i = 0; i < numSets ; i++){
            // get size of blob
            int bSize = buf.getInt();
            byte[] dst = new byte[bSize];
            buf.get(dst);
            FeatureSet deserialize = SWQEFactory.getSerialization().deserialize(dst, FeatureSet.class);
            sSets.add(deserialize);
        }
        return sSets;
    }
        
    protected Job job;
    private static MapReducePlugin mapReducePlugin;
    private static FeatureSet outputSet;

    /**
     * 
     * @param mapReducePlugin the particular plugin to instantiate and run
     * @param inputSet a feature set to operate on
     * @param parameters parameters that we should serialize for the plugin developer
     */
    
    /**
     * 
     * @param mapReducePlugin the particular plugin to instantiate and run
     * @param reference a reference (has to be provided in lieu of a feature set) 
     * @param inputSet a set of feature sets to operate on
     * @param parameters an arbitrary number of external parameters for plugin developers to provide to their plugins
     */
    public MRHBasePluginRunner(MapReducePlugin mapReducePlugin, Reference reference, List<FeatureSet> inputSet, Object... parameters) {
        // handle null inputSet
        if (inputSet == null){
            inputSet = new ArrayList<>();
        }
        // we should either have a reference or more than one input set
        assert(reference != null || inputSet.size() > 0);
        // all feature sets should have the same reference
        if (inputSet.size() > 0){    
            SGID ref = inputSet.iterator().next().getReference().getSGID();
            for(FeatureSet set : inputSet){
             assert(set.getReferenceID().equals(ref));   
            }
        }
        
        SGID referenceSGID = reference != null ? reference.getSGID() : inputSet.iterator().next().getReferenceID();
        
        this.mapReducePlugin = mapReducePlugin;
        try {
            CreateUpdateManager manager = SWQEFactory.getModelManager();
            //outputSet should attach to the original reference
            this.outputSet = manager.buildFeatureSet().setReferenceID(referenceSGID).build();
            manager.close();

            // do setup for Map/Reduce from the HBase API
            String tableName = generateTableName(outputSet);
            String destTableName = generateTableName(outputSet);

            Configuration conf = new Configuration();
            HBaseStorage.configureHBaseConfig(conf);
            HBaseConfiguration.addHbaseResources(conf);

            // we need to pass the parameters for a featureset, maybe we can take advantage of our serializers
            byte[][] sSet = new byte[inputSet.size()][];//SWQEFactory.getSerialization().serialize(inputSet);
            for(int i = 0; i < sSet.length; i++){
                sSet[i] = SWQEFactory.getSerialization().serialize(inputSet.get(i));
            }
            byte[] dSet = SWQEFactory.getSerialization().serialize(outputSet);

            String[] str_params = serializeParametersToString(parameters, mapReducePlugin, sSet, dSet);

            File file = new File(new URI(Constants.Term.DEVELOPMENT_DEPENDENCY.getTermValue(String.class)));
            if (file.exists()) {
                conf.setStrings("tmpjars", Constants.Term.DEVELOPMENT_DEPENDENCY.getTermValue(String.class));
            }
            conf.setStrings(EXT_PARAMETERS, str_params);
            conf.set("mapreduce.map.java.opts", "-Xmx4096m  -verbose:gc");
            conf.set("mapreduce.reduce.java.opts", "-Xmx4096m  -verbose:gc");
            conf.set("mapreduce.map.ulimit", "4194304");
            conf.set("mapreduce.reduce.ulimit", "4194304");
            conf.set("mapreduce.map.memory.mb", "4096");
            conf.set("mapreduce.reduce.memory.mb", "4096");
            conf.set("mapreduce.map.memory.physical.mb", "4096");
            conf.set("mapreduce.reduce.memory.physical.mb", "4096");

            conf.set("mapred.job.map.memory.mb", "4096");
            conf.set("mapred.job.reduce.memory.mb", "4096");

            // the above settings all seem to be ignored by hboot
            // TODO: only this one works, but as far I know, we're using mapreduce not mapred.
            // Strange
            conf.set("mapred.child.java.opts", "-Xmx2048m -verbose:gc");

            this.job = new Job(conf, mapReducePlugin.getClass().getSimpleName());

            Scan scan = new Scan();
            scan.setMaxVersions();       // we need all version data
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
            scan.setCacheBlocks(false);  // don't set to true for MR jobs
            
            for(FeatureSet set : inputSet){
                byte[] qualiferBytes = Bytes.toBytes(set.getSGID().getUuid().toString());
                scan.addColumn(HBaseStorage.getTEST_FAMILY_INBYTES(), qualiferBytes);
            }
            
        	thisInputSet = inputSet;
        	thisParameter = parameters;

            // this might be redundant, check this!!!! 
            // scan.setFilter(new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(qualiferBytes)));

            // handle the part that changes from job to job
            // pluginInterface.performVariableInit(tableName, destTableName, scan);
        	
        	Logger.getLogger(MRHBasePluginRunner.class).info("MRHBasePluginRunner recognizes current mapper class as...: " + mapReducePlugin.getClass().getSimpleName());
        	
        	if (Constants.MULTIPLE_SCAN_RANGES == true && QueryVCFDumper.QUERYVCFDUMPER_RUN == true){
                TableMapReduceUtil.initTableMapperJob(
                        tableName,
                		scan, // Scan instance to control CF and attribute selection
                        PluginRunnerMapper.class, // mapper
                        mapReducePlugin.getMapOutputKeyClass(), // mapper output key 
                        mapReducePlugin.getMapOutputValueClass(), // mapper output value
                        job,
                        true, 
                        MRHBasePluginRunner.QueryRegionTableInput.class);
        	} else if (Constants.MULTIPLE_SCAN_RANGES == false){
                TableMapReduceUtil.initTableMapperJob(
                        tableName,
                		scan, // Scan instance to control CF and attribute selection
                        PluginRunnerMapper.class, // mapper
                        mapReducePlugin.getMapOutputKeyClass(), // mapper output key 
                        mapReducePlugin.getMapOutputValueClass(), // mapper output value
                        job,
                        true);
        	}

            TableMapReduceUtil.initTableReducerJob(tableName, PluginRunnerReducer.class, job);

            if (mapReducePlugin.getOutputClass() != null) {
                job.setOutputFormatClass(mapReducePlugin.getOutputClass());
            }
            job.setReducerClass(MRHBasePluginRunner.PluginRunnerReducer.class);    // reducer class

            if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.FILE) {
                FileContext fileContext = FileContext.getFileContext(this.job.getConfiguration());
                FileSystem fs = FileSystem.get(job.getConfiguration());
                Path path = new Path(fs.getHomeDirectory(), new BigInteger(20, new SecureRandom()).toString(32) + mapReducePlugin.toString());
                path = fileContext.makeQualified(path);
                TextOutputFormat.setOutputPath(job, path);  // adjust directories as required
            }

            job.setJarByClass(MRHBasePluginRunner.class);
            TableMapReduceUtil.addDependencyJars(job);
            TableMapReduceUtil.addDependencyJars(conf, MRHBasePluginRunner.class, MRHBasePluginRunner.PluginRunnerMapper.class, MRHBasePluginRunner.PluginRunnerReducer.class);
            // submit the job, but do not block
            job.submit();
        } catch (URISyntaxException | InterruptedException | ClassNotFoundException | IOException ex) {
            Logger.getLogger(MRHBasePluginRunner.class.getName()).fatal(null, ex);
        }
    }

    private static String generateTableName(FeatureSet sourceSet) {
        LazyFeatureSet lfSet = (LazyFeatureSet) sourceSet;
        String prefix = lfSet.getTablename();
        String tableName = HBaseStorage.TEST_TABLE_PREFIX + HBaseStorage.SEPARATOR + prefix;
        return tableName;
    }

    @Override
    public ReturnType get() {
        try {
            job.waitForCompletion(true);
            if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.COUNTER) {
                return (ReturnType) Long.valueOf(job.getCounters().findCounter(MapperInterface.Counters.ROWS).getValue());
            } else if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.SGID) {
                SGID resultSGID = outputSet.getSGID();
                Class<? extends Atom> resultClass = (Class<? extends Atom>) mapReducePlugin.getResultClass();
                return (ReturnType) SWQEFactory.getQueryInterface().getLatestAtomBySGID(resultSGID, resultClass);
            } else if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.BATCHEDFEATURESET) {
                FeatureSet build = updateAndGet(outputSet);
                return (ReturnType) build;
            } else if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.FILE) {
                Path outputPath = TextOutputFormat.getOutputPath(job);
                FileSystem fs = FileSystem.get(job.getConfiguration());
                Path localPath = new Path(Files.createTempDir().toURI());
                fs.copyToLocalFile(outputPath, localPath);
                
                File outputFile = new File(localPath.toUri());
                return (ReturnType) outputFile;
            } else {
                throw new UnsupportedOperationException();


            }
        } catch (IOException | InterruptedException | ClassNotFoundException ex) {
            Logger.getLogger(MRHBasePluginRunner.class
                    .getName()).error(null, ex);
        }
        return null;
    }

    public boolean isComplete() {
        try {
            return job.isComplete();


        } catch (IOException ex) {
            Logger.getLogger(MRHBasePluginRunner.class
                    .getName()).error(null, ex);
        }
        return false;
    }

    @Override
    public PluginInterface getPlugin() {
        return mapReducePlugin;
    }

    /**
     * 
     * @param ranges Is the determined Range Query.
     */
    public static void checkRangeQueryExists(List<List<String>> ranges){
    	try{
	    	List<String> startList = new ArrayList<String>();
	    	List<String> stopList = new ArrayList<String>();
	    	if (ranges != null){
	        	startList = ranges.get(START_LIST);
	        	stopList = ranges.get(STOP_LIST);
	        	Logger.getLogger(MRHBasePluginRunner.class).debug("___startList: " + startList);
	        	Logger.getLogger(MRHBasePluginRunner.class).debug("___stopList: " + stopList);
	    		if (startList.size() == stopList.size() 
	    				&& startList.size() % 2 == 0 
	    				&& startList.size() != 0){
	    			START_STOP_PAIRS_EXIST = true;
	    		}
	    	}
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * 
     * @param parameters This is taken from the MRHBasePluginRunner constructor.
     * @return List of lists. First list is the start range lists, second list is stop range lists, third is list of possible seqID(s).
     */
    public static List<List<String>> determineRangeQuery(Object... parameters){
    	try{
    		List<List<String>> ranges = new ArrayList<List<String>>();
    		
	    	RPNStack rpnStack = new RPNStack();
	        for (Object o : parameters){
	        	if (o instanceof RPNStack){
	        		rpnStack = (RPNStack) o;
	        		break;
	        	} 
	        }
	        
	        FeatureAttribute thisFeature = null;
	        List<String> startList = new ArrayList<String>();
	        List<String> stopList = new ArrayList<String>();
	        List<String> seqList = new ArrayList<String>();
	        
	        //Assumes that there is always a start and stop pair in query
			for (Parameter parameter : rpnStack.getParameters()){
				if (parameter instanceof FeatureAttribute){
					thisFeature = (FeatureAttribute) parameter;
					if (parameter.getName().equals("start")){
	    				startList = thisFeature.getStartList();
					} else if (parameter.getName().equals("stop")){
	    				stopList = thisFeature.getStopList();
					} else if (parameter.getName().equals("seqid")){
						seqList = thisFeature.getSeqIDList();
					}
				}
			}
	
			//All start and stop positions are paired and there is at least one pair.
			if (startList.size() == stopList.size() 
					&& startList.size() % 2 == 0 
					&& startList.size() != 0){
				ranges.add(startList);
				ranges.add(stopList);
				ranges.add(seqList);
				return ranges;
			} else {
				return null;
			}
    	} catch (Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
    /**
     * 
     * @param inputSet This is used current to get all SeqID's in the feature set.
     * @param ranges The determined Range Query.
     * @return scanPositions The list of rows in the current feature set for use in QueryRegionTableInput.
     * @return null If START_STOP_PARIS_EXIST is FALSE.
     */
    public static List<List<String>> generateRegionList(List<FeatureSet> inputSet, List<List<String>> ranges) {
    	try {
			if (START_STOP_PAIRS_EXIST == true 
					&& ranges != null
					&& ranges.get(START_LIST) != null 
					&& ranges.get(STOP_LIST) != null){
		        List<String> startList = new ArrayList<String>();
		        List<String> stopList = new ArrayList<String>();
		        List<String> seqList = new ArrayList<String>();
				List<String> startPosList = new ArrayList<String>();
				List<String> stopPosList = new ArrayList<String>();
				List<String> seqIDList = new ArrayList<String>();
				List<List<String>> scanPositions = new ArrayList<List<String>>();
				int startDigitLength;
				int startDigitLengthDifference;
				int stopDigitLength;
				int stopDigitLengthDifference;
				Map<Integer, List<String>> comparatorStrings = new HashMap<Integer, List<String>>();
				int count = 0; /*need unique key placeholder for every key in comparatorStrings;*/
				String finalStartString = new String();
				String finalStopString = new String();
				String referenceString = new String();
		    	
		    	startList = ranges.get(START_LIST);
		    	stopList = ranges.get(STOP_LIST);
		    	seqList = ranges.get(SEQ_LIST);
		    	
				for (int i = 1; i < startList.size(); i += 2){
					startPosList.add(startList.get(i));
					stopPosList.add(stopList.get(i));
				}
				
		    	//Generate 15 digit start and end position.
		    	for (int i = 0 ; i < startPosList.size(); i++){
			    	String zeroPad = new String();
		        	startDigitLength = startPosList.get(i).length();
		        	startDigitLengthDifference = HBaseStorage.PAD - startDigitLength;
		        	stopDigitLength = stopPosList.get(i).length();
		        	stopDigitLengthDifference = HBaseStorage.PAD - stopDigitLength;
		    		for (int j=0; j<startDigitLengthDifference; j++){
		    			zeroPad += "0";
		    		}
		    		startPosList.set(i, zeroPad + startPosList.get(i));
		    		zeroPad = "";
		    		for (int j=0; j<stopDigitLengthDifference; j++){
		    			zeroPad += "0";
		    		}
		    		stopPosList.set(i, zeroPad + stopPosList.get(i));
		    		zeroPad = "";
		    	}
				
		    	//Define what the seqID list will be, has the user defined specific seqID(s)?
				if (!seqList.isEmpty()){
					for (int i = 1; i < seqList.size(); i += 2){
						seqIDList.add(seqList.get(i).replaceAll("\"", ""));
					}
				} else if (seqList.isEmpty()){
					seqIDList = allSeqIDs;
				}
		    	
				referenceString = outputSet.getReference().getDisplayName();

		    	//Generate the list of comparator inputs (rows names)
		    	//Map<i'th combination, List<start and stop row names>>
				Logger.getLogger(MRHBasePluginRunner.class).debug("seqIDs to be processed :" + seqIDList);
				for (int i = 0; i < startPosList.size(); i++){
					for(String seqID : seqIDList){
						count++;
						Logger.getLogger(MRHBasePluginRunner.class).debug("Processing seqID :" + seqID);
			    		finalStartString = referenceString + "." + seqID + ":" + startPosList.get(i);
			    		finalStopString = referenceString + "." + seqID + ":" + stopPosList.get(i);
			    		comparatorStrings.put(count, 
			    				Arrays.asList(
			    						finalStartString,
			    						finalStopString));
					}
				}

		    	//Put together the List of list<String>
		    	for (int i : comparatorStrings.keySet()){
		    		List<String> stringHolder = new ArrayList<String>();
		    		finalStartString = comparatorStrings.get(i).get(0);		 /* Retrieve the start row from this pair */
		    		finalStopString = comparatorStrings.get(i).get(1); 		/* Retrieve the stop row from this pair */
		    		stringHolder.add(finalStartString);
		    		stringHolder.add(finalStopString);
		    		Logger.getLogger(MRHBasePluginRunner.class).debug("Pairs of start and stops : " + stringHolder);
		    		scanPositions.add(stringHolder);
		    	}
		    	return scanPositions;
			} else {
				return null;
			}
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
    }
    
    public static FeatureSet updateAndGet(FeatureSet outputSet) {
        // after processing, outputSet will actually have been versioned several times, we need the latest one
        FeatureSet latestAtomBySGID = SWQEFactory.getQueryInterface().getLatestAtomBySGID(outputSet.getSGID(), FeatureSet.class);
        //        //TODO: remove the need for this hacky hack, there is some versioning issue here
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        SGID sgid = latestAtomBySGID.getSGID();

        sgid.setBackendTimestamp(
                new Date());
        FeatureSet build = latestAtomBySGID.toBuilder().build();

        build.impersonate(sgid, latestAtomBySGID.getSGID());
        if (Constants.TRACK_VERSIONING) {
            build.setPrecedingVersion(build);
        }

        modelManager.persist(build);

        modelManager.close();
        return build;
    }

    public static String[] serializeParametersToString(Object[] parameters, PluginInterface mapReducePlugin, byte[][] sSet, byte[] dSet) {
        int num_guaranteed_parameters = 6;
        String[] str_params = new String[num_guaranteed_parameters];
        byte[] ext_serials = SerializationUtils.serialize(parameters);
        byte[] int_serials = SerializationUtils.serialize(mapReducePlugin.getInternalParameters());
        str_params[EXTERNAL_PARAMETERS] = Base64.encodeBase64String(ext_serials);
        str_params[INTERNAL_PARAMETERS] = Base64.encodeBase64String(int_serials);
        ByteBuffer bBuffer = ByteBuffer.allocate(1024*1024); // one MB should be enough for now
        bBuffer.putInt(sSet.length);
        for(byte[] arr : sSet){
            bBuffer.putInt(arr.length);
            bBuffer.put(arr);
        }
        str_params[NUM_AND_SOURCE_FEATURE_SETS] = Base64.encodeBase64String(bBuffer.array());
        str_params[DESTINATION_FEATURE_SET] = Base64.encodeBase64String(dSet);
        str_params[SETTINGS_MAP] = Base64.encodeBase64String(SerializationUtils.serialize(new Object[]{Constants.getSETTINGS_MAP()}));
        str_params[PLUGIN_CLASS] = Base64.encodeBase64String(SerializationUtils.serialize(mapReducePlugin.getClass()));

        return str_params;
    }
    
    public static class QueryRegionTableInput extends TableInputFormat{
    	
    	QueryRegionTableInput(){}
    	
    	@Override
    	public List<InputSplit> getSplits(JobContext context) throws IOException{
    		try{
    			Scan scan = getScan();
	    		List<InputSplit> splits = new ArrayList<InputSplit>();
	    		List<List<String>> rangeQuery = determineRangeQuery(MRHBasePluginRunner.thisParameter);
	    		String currentMapperName = mapReducePlugin.getClass().getSimpleName();
	    		
	    		//This will switch the START_STOP_PAIRS_EXIST to true or false
	    		checkRangeQueryExists(rangeQuery);
	    		
            	Logger.getLogger(MRHBasePluginRunner.class).debug("___Running FeaturesByAttributesPlugin? : " + currentMapperName.equals("FeaturesByAttributesPlugin"));
            	Logger.getLogger(MRHBasePluginRunner.class).debug("___START_STOP_PAIRS_EXIST? : " + START_STOP_PAIRS_EXIST);
            	Logger.getLogger(MRHBasePluginRunner.class).debug("___RPNStack.allStartsStopsArePaired? : " + RPNStack.allStartsStopsArePaired);

                if (currentMapperName.equals("FeaturesByAttributesPlugin")
                		&& START_STOP_PAIRS_EXIST == true
                		&& RPNStack.allStartsStopsArePaired == true){
                	
                    //Use the multiple range input to split the table, we want the shortened scan range resulting from this.
                	Logger.getLogger(MRHBasePluginRunner.class).info("Applying custom splits to the table....");
                    List<List<String>> rowList = new ArrayList<List<String>>(); 
                    rowList = generateRegionList(MRHBasePluginRunner.thisInputSet, rangeQuery);
                    if (rowList != null){
	                    for (List<String> thisPair : rowList){
	                    	byte[] startRowByte = thisPair.get(0).getBytes();
	                    	byte[] stopRowByte = thisPair.get(1).getBytes();
	                    	scan.setStartRow(startRowByte);
	                    	scan.setStopRow(stopRowByte);
	                    	scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, 
	                    			scan.getAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME));
	                    	setScan(scan);
	                    	
	        	    		for(InputSplit subSplit : super.getSplits(context)){
	        	    			splits.add((InputSplit) ReflectionUtils.copy(context.getConfiguration(),
	        	    					(TableSplit) subSplit, new TableSplit()));
	        	    		}
	                    }
                    }
                } else {
                	//Table will be split as one table, as if no custom split has been applied.
                	Logger.getLogger(MRHBasePluginRunner.class).info("Applying default splits to the table....");
                    scan.setStartRow(scan.getStartRow());
                    scan.setStopRow(scan.getStopRow());
                    scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, 
                    		scan.getAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME));
                    setScan(scan);
                    
    	    		for(InputSplit subSplit : super.getSplits(context)){
    	    			splits.add((InputSplit) ReflectionUtils.copy(context.getConfiguration(),
    	    					(TableSplit) subSplit, new TableSplit()));
    	    		}
                }
	    		return splits;
    		} catch (Exception e){
    			e.printStackTrace();
    			return null;
    		}
    	}
    }
    
    public static class PluginRunnerReducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT> extends TableReducer<KEYIN, VALUEIN, KEYOUT> implements ReducerInterface<KEYOUT, VALUEOUT> {

        public PluginRunnerReducer() {
            super();
        }
        private PluginRunnerReducer.Context context;
        private MapReducePlugin mapReducePlugin;
        protected Object[] ext_parameters;
        protected Object[] int_parameters;
        protected List<FeatureSet> sourceSets;
        protected FeatureSet destSet;

        @Override
        protected void reduce(KEYIN key, Iterable<VALUEIN> values, Context context) throws IOException, InterruptedException {
            this.context = context;
            mapReducePlugin.reduce(key, values, this);
        }

        @Override
        public void write(KEYOUT keyout, VALUEOUT valueout) {
            try {
                context.write(keyout, valueout);
            } catch (    IOException | InterruptedException ex) {
                Logger.getLogger(MRHBasePluginRunner.class.getName()).error(null, ex);
            }
        }

        @Override
        public Object[] getExt_parameters() {
            return ext_parameters;
        }

        @Override
        public Object[] getInt_parameters() {
            return int_parameters;
        }

        @Override
        public List<FeatureSet> getSourceSets() {
            return sourceSets;
        }

        @Override
        public FeatureSet getDestSet() {
            return destSet;
        }

        @Override
        public void setExt_parameters(Object[] params) {
            this.ext_parameters = params;
        }

        @Override
        public void setInt_parameters(Object[] params) {
            this.int_parameters = params;
        }

        @Override
        public void setSourceSets(List<FeatureSet> sets) {
            this.sourceSets = sets;
        }

        @Override
        public void setDestSet(FeatureSet set) {
            this.destSet = set;
        }

        @Override
        protected void setup(Reducer.Context context) {
            Logger.getLogger(FeatureSetCountPlugin.class.getName()).info("Setting up reducer");
            Class plugin = MRHBasePluginRunner.transferConfiguration(context, this);
            try {
                mapReducePlugin = (MapReducePlugin) plugin.newInstance();
            } catch (    InstantiationException | IllegalAccessException ex) {
                Rethrow.rethrow(ex);
            }
            mapReducePlugin.reduceInit();
        }

        @Override
        protected void cleanup(org.apache.hadoop.mapreduce.Reducer.Context context) throws IOException, InterruptedException {
            mapReducePlugin.reduceCleanup();
        }
    }

    public static class PluginRunnerMapper<KEYOUT, VALUEOUT> extends TableMapper<KEYOUT, VALUEOUT> implements MapperInterface<KEYOUT, VALUEOUT> {

        public PluginRunnerMapper() {
            super();
        }
        private MapReducePlugin mapReducePlugin;
        private PluginRunnerMapper.Context context;
        
        // cache for SGID to FeatureSet lookup
        LoadingCache<SGID, FeatureSet> sgid2featureset = CacheBuilder.newBuilder()
       .maximumSize(1000)
       .expireAfterWrite(30, TimeUnit.MINUTES)
       .build(
           new CacheLoader<SGID, FeatureSet>() {
             @Override
             public FeatureSet load(SGID key)  {
               QueryInterface query = SWQEFactory.getQueryInterface();
               return query.getLatestAtomBySGID(key, FeatureSet.class);
             }
           });
        
        /**
         * parameters that will be usable by the user (the writer of the
         * queries)
         */
        protected Object[] ext_parameters;
        /**
         * parameters that will be handled by the plug-in developer but will not
         * be available to the user of the plug-in
         */
        protected Object[] int_parameters;
        /**
         * the feature set that we will be reading
         */
        protected List<FeatureSet> sourceSets;
        /**
         * the feature set that we will be writing to, may be null
         */
        protected FeatureSet destSet;

        @Override
        public void incrementCounter() {
            context.getCounter(Counters.ROWS).increment(1L);
        }

        @Override
        public void write(KEYOUT keyout, VALUEOUT valueout) {
            try {
                context.write(keyout, valueout);
            } catch (    IOException | InterruptedException ex) {
                Logger.getLogger(MRHBasePluginRunner.class.getName()).error(null, ex);
            }
        }

        @Override
        protected void setup(Mapper.Context context) {
            this.baseMapperSetup(context);
            mapReducePlugin.mapInit(this);
        }

        @Override
        protected void cleanup(org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException {
            mapReducePlugin.mapCleanup();
        }

        @Override
        protected void map(ImmutableBytesWritable row, Result values, Mapper.Context context) throws IOException, InterruptedException {
            this.context = context;
            List<SGID> sourceSetIDs = new ArrayList<>();
            for(FeatureSet sSet : sourceSets){
                sourceSetIDs.add(sSet.getSGID());
            }
            Logger.getLogger(MRHBasePluginRunner.class.getName()).trace("Dealing with "+sourceSetIDs.size()+" featuresets");
            Map<SGID, List<FeatureList>> grabFeatureListsGivenRow = HBaseStorage.grabFeatureListsGivenRow(values, sourceSetIDs, SWQEFactory.getSerialization());
            Map<FeatureSet, Collection<Feature>> consolidatedMap = new HashMap<FeatureSet, Collection<Feature>>();
            for(Entry<SGID, List<FeatureList>> e : grabFeatureListsGivenRow.entrySet()){
               Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(e.getValue());
               Logger.getLogger(MRHBasePluginRunner.class.getName()).trace("Consolidated to  " + consolidateRow.size() + " features");
               // try to get grab featureset given SGID
               consolidatedMap.put(sgid2featureset.getUnchecked(e.getKey()), consolidateRow);
            }
            // figure out current row
            String rowKey = Bytes.toString(row.get());
            rowKey = rowKey.substring(rowKey.indexOf(PositionSeparator)+1);
            Long position = Long.valueOf(rowKey);
 
            // grab binned features if applicable
            if (Constants.OVERLAP_MODE == Constants.OVERLAP_STRATEGY.BINNING) {
                Logger.getLogger(MRHBasePluginRunner.class.getName()).trace("Checking binning with "+sourceSetIDs.size()+" featuresets");
                // grab an arbitrary feature set in order to determine tablename
                FeatureSet get = sourceSets.get(0);
                assert (get instanceof LazyFeatureSet);
                LazyFeatureSet lfSet = (LazyFeatureSet) get;
                String tableName = lfSet.getTablename();
                StorageInterface storage = SWQEFactory.getStorage();
                if (storage instanceof HBaseStorage) {
                    Logger.getLogger(MRHBasePluginRunner.class.getName()).trace("Looking for bins in table "+ tableName);
                    ((HBaseStorage) storage).grabBinnedFeatures(Bytes.toString(row.get()), tableName, SWQEFactory.getSerialization(), consolidatedMap);
                }
            }
            
            consolidatedMap = handlePreFilteredPlugins(consolidatedMap, mapReducePlugin, ext_parameters);
            mapReducePlugin.map(position, consolidatedMap, this);
        }

        @Override
        public Object[] getExt_parameters() {
            return ext_parameters;
        }

        @Override
        public Object[] getInt_parameters() {
            return int_parameters;
        }

        @Override
        public List<FeatureSet> getSourceSets() {
            return sourceSets;
        }

        @Override
        public FeatureSet getDestSet() {
            return destSet;
        }

        private void baseMapperSetup(Context context) {
            Logger.getLogger(FeatureSetCountPlugin.class.getName()).info("Setting up mapper");
            Class plugin = MRHBasePluginRunner.transferConfiguration(context, this);
            try {
                mapReducePlugin = (MapReducePlugin) plugin.newInstance();
            } catch (    InstantiationException | IllegalAccessException ex) {
                Rethrow.rethrow(ex);
            }
        }

        @Override
        public void setExt_parameters(Object[] params) {
            this.ext_parameters = params;
        }

        @Override
        public void setInt_parameters(Object[] params) {
            this.int_parameters = params;
        }

        @Override
        public void setSourceSets(List<FeatureSet> sets) {
            this.sourceSets = sets;
        }

        @Override
        public void setDestSet(FeatureSet set) {
            this.destSet = set;
        }
    }

    public static Class transferConfiguration(JobContext context, JobRunParameterInterface inter) {
        Configuration conf = context.getConfiguration();
        String[] strings = conf.getStrings(MRHBasePluginRunner.EXT_PARAMETERS);
        Logger.getLogger(PluginRunnerMapper.class.getName()).info("QEMapper configured with: host: " + Constants.Term.HBASE_PROPERTIES.getTermValue(Map.class).toString() + " namespace: " + Constants.Term.NAMESPACE.getTermValue(String.class));
        final String mapParameter = strings[SETTINGS_MAP];
        if (mapParameter != null && !mapParameter.isEmpty()) {
            Map<String, String> settingsMap = (Map<String, String>) ((Object[]) SerializationUtils.deserialize(Base64.decodeBase64(mapParameter)))[EXTERNAL_PARAMETERS];
            if (settingsMap != null) {
                Logger.getLogger(FeatureSetCountPlugin.class.getName()).info("Settings map retrieved with " + settingsMap.size() + " entries");
                Constants.setSETTINGS_MAP(settingsMap);
            }
        }
        
        Logger.getLogger(PluginRunnerMapper.class.getName()).info("QEMapper configured with: host: " + Constants.Term.HBASE_PROPERTIES.getTermValue(Map.class).toString() + " namespace: " + Constants.Term.NAMESPACE.getTermValue(String.class));
        final String externalParameters = strings[EXTERNAL_PARAMETERS];
        if (externalParameters != null && !externalParameters.isEmpty()) {
            inter.setExt_parameters((Object[]) SerializationUtils.deserialize(Base64.decodeBase64(externalParameters)));
        }
        final String internalParameters = strings[INTERNAL_PARAMETERS];
        if (internalParameters != null && !internalParameters.isEmpty()) {
            inter.setInt_parameters((Object[]) SerializationUtils.deserialize(Base64.decodeBase64(internalParameters)));
        }
        final String sourceSets = strings[NUM_AND_SOURCE_FEATURE_SETS];
        if (sourceSets != null && !sourceSets.isEmpty()) {
            List<FeatureSet> sSets = convertBase64StrToFeatureSets(sourceSets);
            inter.setSourceSets(sSets);
        }
        final String destSetParameter = strings[DESTINATION_FEATURE_SET];
        if (destSetParameter != null && !destSetParameter.isEmpty()) {
            inter.setDestSet(SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(destSetParameter), FeatureSet.class));
        }
        final String pluginParameter = strings[PLUGIN_CLASS];
        if (pluginParameter != null && !pluginParameter.isEmpty()) {
            Object deserialize = SerializationUtils.deserialize(Base64.decodeBase64(pluginParameter));
            Class plugin = (Class)deserialize;
            return plugin;
        }
        throw new RuntimeException("Could not determine plugin to run");
    }

     public static Map<FeatureSet, Collection<Feature>> handlePreFilteredPlugins(Map<FeatureSet, Collection<Feature>> consolidatedMap, MapReducePlugin mapReducePlugin, Object[] ext_parameters) {
            // for PreFilteredPlugins, we can do some prefiltering before the FeatureSets and features hit the actual plugin
            if (mapReducePlugin instanceof PrefilteredPlugin){
                FeatureFilter filter = ((PrefilteredPlugin)mapReducePlugin).getFilter();
                Map<FeatureSet, Collection<Feature>> filteredMap = new HashMap<>();
                for(Entry<FeatureSet, Collection<Feature>> e : consolidatedMap.entrySet()){
                    for(Feature f : e.getValue() ){
                        if (!filter.featurePasses(e.getKey(), f, ext_parameters)){
                            continue;
                        }
                        if (!filteredMap.containsKey(e.getKey())){
                            filteredMap.put(e.getKey(), new ArrayList<Feature>());
                        }
                        filteredMap.get(e.getKey()).add(f);
                    }
                }
                consolidatedMap = filteredMap;
            }
            return consolidatedMap;
        }
}
