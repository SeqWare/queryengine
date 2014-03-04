package com.github.seqware.queryengine.model.test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.seqware.queryengine.dto.QueryEngine.FeatureListPB;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.contribs.OverlappingMutationsAggregationPlugin;
import com.github.seqware.queryengine.plugins.plugins.FeatureSetCountPlugin;
import com.github.seqware.queryengine.plugins.plugins.FeaturesByAttributesPlugin;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.impl.MRHBaseModelManager;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.impl.protobufIO.FeatureIO;
import com.github.seqware.queryengine.impl.protobufIO.FeatureListIO;
import com.github.seqware.queryengine.impl.protobufIO.FeatureSetIO;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.kernel.RPNStack.Constant;
import com.github.seqware.queryengine.kernel.RPNStack.FeatureAttribute;
import com.github.seqware.queryengine.kernel.RPNStack.Operation;

/**
 * Unit tests of some plugin runners and basic importing/querying of vcf data.
 * 
 * @author bso
 * @version $Id: $Id
 */
public class VCFImportDataManipulationTestSuite {
	static FeatureSet aSet, bSet, cSet;
	static Feature a1,a2,a3;
	static File testVCFFile = null;
	static String refName = null;

    /**
     * <p>setUpTest.</p>
     */
	//@BeforeClass
	// this will reset all the tables and load the vcf file paths for testing
	public static void setUpTest() throws IOException{
		Configuration config = HBaseConfiguration.create();
		try {
			HBaseAdmin hba = new HBaseAdmin(config);
			hba.disableTables("b.*");
			hba.deleteTables("b.*");
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		}
		
		String curDir = System.getProperty("user.dir");
        SecureRandom random = new SecureRandom();
        refName = "Random_ref_" + new BigInteger(20, random).toString(32);
        testVCFFile = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/smallTest.vcf");

	}
	
    /**
     * <p>testInstallAndRunArbitraryPlugin.</p>
     */
	//@Test
	// Test some implemented plugin that is working
    public void testInstallAndRunArbitraryPlugin() {
        Class<? extends PluginInterface> arbitraryPlugin;
        // only use the M/R plugin for this test if using MR
        if (SWQEFactory.getModelManager() instanceof MRHBaseModelManager) {
            // pretend that the included com.github.seqware.queryengine.plugins.hbasemr.MRFeaturesByAttributesPlugin is an external plug-in
            arbitraryPlugin = FeaturesByAttributesPlugin.class;
        } else {
            // pretend the equivalent for a non-HBase back-end
            arbitraryPlugin = FeaturesByAttributesPlugin.class;
        }
        // get a FeatureSet from the back-end
        QueryFuture<FeatureSet> future = SWQEFactory.getQueryInterface().getFeaturesByPlugin(0, arbitraryPlugin, null, aSet, new RPNStack(
                new Constant("1"), new FeatureAttribute("seqid"), Operation.EQUAL));
        // check that Features are present match
        FeatureSet result = future.get();
        System.out.println("This is the plugin result for FeatureSet " + result.getReference().getDisplayName() + " : ");
        System.out.println("This is the length of the feature set " + result.getReference().getDisplayName() + " : " + result.getCount());
        for (Feature f : result) {
			System.out.println(f.getDisplayName() + 
					", Seqid: " + f.getSeqid() + 
					", Source: " + f.getSource() + 
					", Start: " + f.getStart() + 
					", Stop: " + f.getStop() + 
					", Strand: " + f.getStrand());
        }
        //Assert.assertTrue("Query results wrong, expected 1 and found " + count, count == 1);
    }
	
    /**
     * <p>testVCFImport.</p>
     */
	//@Test
	// This imports the features from a vcf file into HBase
	public void testVCFImport(){
		SGID main;
		FeatureSet fSet;
		CreateUpdateManager manager;
		Iterator<Feature> fIter;
		
        main = FeatureImporter.naiveRun(new String[]{"VCFVariantImportWorker", "1", "false", refName, testVCFFile.getAbsolutePath()});        
        fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(main, FeatureSet.class);
        
        manager = SWQEFactory.getModelManager();
        fIter = fSet.getFeatures();
        aSet = manager.buildFeatureSet().setReference(fSet.getReference()).build();
        while(fIter.hasNext()){
        	Feature f = fIter.next();
        	aSet.add(f);
        	System.out.println("Stop: " + f.getStop());
        }
        manager.flush();
	}
	
    /**
     * <p>complexQueryTest.</p>
     */
    //@Test
	public void complexQueryTest(){
		SimplePersistentBackEnd backend = new SimplePersistentBackEnd(SWQEFactory.getStorage());
		CreateUpdateManager manager = SWQEFactory.getModelManager();
		QueryFuture<FeatureSet> queryFuture = backend.getFeaturesByAttributes(1, aSet, new RPNStack(
				new Constant("chr1"),
                Operation.EQUAL));
		System.out.println(queryFuture.get().getCount());
		cSet = manager.buildFeatureSet().setReference(queryFuture.get().getReference()).build();
		System.out.println("Plugin has run.");
		manager.close();
	}
	
    /**
     * <p>verifyNaiveImport.</p>
     */
	//@Test
	public void verifyNaiveImport(){
		try {
			Configuration config = HBaseConfiguration.create();
			HTableInterface hg19Table = new HTable(config, "batman.hbaseTestTable_v2.Feature.hg_19");
			
			List<Get> getList = new ArrayList<Get>();
			getList.add(new Get(Bytes.toBytes("hg_19.1:000000000000012")));
			getList.add(new Get(Bytes.toBytes("hg_19.1:000000000000013")));
			getList.add(new Get(Bytes.toBytes("hg_19.1:000000000000014")));
			getList.add(new Get(Bytes.toBytes("hg_19.1:000000000000015")));

			for (Get g : getList){
				System.out.println(hg19Table.exists(g));
				Result r = hg19Table.get(g);
				FeatureListIO fLio = new FeatureListIO();
				FeatureIO fIo = new FeatureIO();
				FeatureSetIO fSIo = new FeatureSetIO();
				KeyValue columnLatest = r.getColumnLatest(Bytes.toBytes("d"), Bytes.toBytes("2682ee4b-5d7b-4ad8-b632-a897b5043715"));
				byte[] value = columnLatest.getValue();
				System.out.println(Bytes.toString(value));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
