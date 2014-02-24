package com.github.seqware.queryengine.model.test;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Assert;
import org.junit.Test;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.util.SGID;

public class TableSetupTest {
	static FeatureSet aSet;
	static Feature a1;
	static File testVCFFile = null;
	static String randomRef = null;
	public void setupTest(){
		CreateUpdateManager manager = SWQEFactory.getModelManager();
		aSet = manager.buildFeatureSet().setReference(manager.buildReference().setName("DummyReference").build()).build();
		a1 = manager.buildFeature().setSeqid("chr1").setStart(100).setStop(101).setScore(100.0).setStrand(Feature.Strand.NEGATIVE).setSource("human").setPhase(".").build();
		aSet.add(a1);
		manager.flush();
	}
//	@Test
//	public void testVCFImport(){
//        SecureRandom random = new SecureRandom();
//        SWQEFactory.getSerialization();
//		CreateUpdateManager manager = SWQEFactory.getModelManager();
//		randomRef = "Random_ref_" + new BigInteger(20, random).toString(32);
//		testVCFFile = new File("/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test.vcf");
//		SGID main = FeatureImporter.naiveRun(new String[]{"VCFVariantImportWorker", "1", "false", randomRef, testVCFFile.getAbsolutePath()});        
//        FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(main, FeatureSet.class);
//        
//	}
}
