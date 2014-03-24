package com.github.seqware.queryengine.system.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.seqware.queryengine.Benchmarking;
import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.Constants.OVERLAP_STRATEGY;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.system.ReferenceCreator;
import com.github.seqware.queryengine.system.importers.SOFeatureImporter;
import com.github.seqware.queryengine.util.SGID;

public class QueryVCFDumperBenchmarkTest implements Benchmarking{
	
    private Configuration config;
	private HTable table;
	private Map<String, HTable> tableMap = new HashMap<String, HTable>();
	private final String JAR_NAME = "seqware-distribution-1.0.7-SNAPSHOT-qe-full.jar";
	private static String randomRef = null;
    private static Reference reference = null;
	private static HashMap<Reference,SGID> originalSet = null;
	private static List<File> testingFiles = new ArrayList<File>();
	private static final String SINGLE_RANGE_QUERY = "";
	private static final String MULTI_RANGE_QUERY = "";
	private static final String DOWNLOAD_DIR = "/home/seqware";
	
	@BeforeClass
	public void setUpTest(){
		//TODO: specify config
        this.config = HBaseConfiguration.create();
        
		//TODO: Download File
        String[] vcfs = new String[]{
                "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/phase1/analysis_results/consensus_call_sets/indels/ALL.wgs.VQSR_V2_GLs_polarized_biallelic.20101123.indels.sites.vcf.gz"
            };
        testingFiles = download(vcfs);
        
//		tableMap = retriveFeatureTableMap();
//		for (Entry<String,HTable> e : tableMap.entrySet()){
//			System.out.println(e.getKey());
//		}
	}
	
	@Test
	public void testSingleScan(){
		Constants.MULTIPLE_SCAN_RANGES = false;
		//TODO: Write range test for true, false Overlaps

		setNaiveConstant(false);
		
		//TODO: Add timer
//		importToBackend(testingFiles);
		
		//TESTS
		resetAllTables();
		
		setNaiveConstant(true);
		
		//TODO: Add timer
//		importToBackend(testingFiles);
		
		//TESTS
//		resetAllTables();
	}
	
	@Test
	public void testMultiScan(){
		Constants.MULTIPLE_SCAN_RANGES = true;
		//TODO: Write range test for true, false Overlaps
		
		setNaiveConstant(false);

		//TODO: Add timer
//		importToBackend(testingFiles);
		
		//TESTS
		resetAllTables();
		
		setNaiveConstant(true);
		
		//TODO: Add timer
//		importToBackend(testingFiles);
		
		//TESTS
//		resetAllTables();
	}
	
	public void setOverlapStratagy(OVERLAP_STRATEGY strategy){
		Constants.OVERLAP_MODE = strategy;
	}
	
	public void resetAllTables(){
		try{
			HBaseAdmin hba = new HBaseAdmin(config);
			hba.disableTables("b.*");
			hba.deleteTables("b.*");
			hba.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public Map<String, HTable> retriveFeatureTableMap(){
		try{
	        HBaseAdmin hba = new HBaseAdmin(config);
	        
	        HTableDescriptor[] listTables = hba.listTables(HBaseStorage.TEST_TABLE_PREFIX + "[.]Feature[.].*");
	        
	        for (HTableDescriptor des : listTables){
	        	tableMap.put(des.getNameAsString(), 
	        			new HTable(config, des.getNameAsString()));
	        }
	        hba.close();
	        return tableMap;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
    private static void downloadFile(String file, File downloadDir, List<File> filesToReturnGZCompressed) throws IOException, MalformedURLException, URISyntaxException {
        URL newURL = new URL(file);
        String name = newURL.toString().substring(newURL.toString().lastIndexOf("/"));
        File targetFile = new File(downloadDir, name);
        if (!targetFile.exists()){
            System.out.println("Downloading " + newURL.getFile() + " to " + targetFile.getAbsolutePath());
            FileUtils.copyURLToFile(newURL, targetFile);
        }     
        filesToReturnGZCompressed.add(targetFile);
    }
    
    private static List<File> download(String[] files) {
        List<File> filesToReturnGZCompressed = new ArrayList<File>();
        List<File> filesToReturnGZUnCompressed = new ArrayList<File>();
        // always use the same directory so we do not re-download on repeated runs
        File downloadDir = new File(DOWNLOAD_DIR);
        for (String file : files) {
            try {
                downloadFile(file, downloadDir, filesToReturnGZCompressed);
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        for (File thisGZCompressedFile : filesToReturnGZCompressed){
        	try{
	        	File thisGZUncompressedFile = new File(thisGZCompressedFile.getAbsolutePath());
	        	gzDecompressor(thisGZCompressedFile, thisGZUncompressedFile);
	        	filesToReturnGZUnCompressed.add(thisGZUncompressedFile);
        	} catch (Exception e){
        		e.printStackTrace();
        	}
        }
        return filesToReturnGZUnCompressed;
    }
	
    private void importToBackend(List<File> files){
    	try{
	    	for (File f : files){
	    		Assert.assertTrue("Cannot read VCF file for test", f.exists() && f.canRead());
	            List<String> argList = new ArrayList<String>();
	            randomRef = "Random_ref_" + new BigInteger(20, new SecureRandom()).toString(32);
	            SGID refID = ReferenceCreator.mainMethod(new String[]{randomRef});
	            reference = SWQEFactory.getQueryInterface().getAtomBySGID(Reference.class, refID);
	            
	            argList.addAll(Arrays.asList(new String[]{"-w", "VCFVariantImportWorker",
	                    "-i", f.getAbsolutePath(),
	                    "-r", reference.getSGID().getRowKey()}));
	            
	            originalSet.put(reference,
	            		(SOFeatureImporter.runMain(argList.toArray(new String[argList.size()]))));
	            
	            Assert.assertTrue("Could not import VCF for test", originalSet != null);
	    	}
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
    private static void gzDecompressor(File filePathGZ, File thisGZUncompressedFile) throws IOException{
  	  String filename = filePathGZ
  				.getName()
  				.substring(0, filePathGZ.getName().indexOf("."));
  	  byte[] buf = 
  			  new byte[1024];
        int len;
  	  String outFilename = DOWNLOAD_DIR + filename + ".vcf";
  	  FileInputStream instream = 
  			  new FileInputStream(filePathGZ);
        GZIPInputStream ginstream = 
      		  new GZIPInputStream(instream);
        FileOutputStream outstream = 
      		  new FileOutputStream(outFilename);
        System.out.println("Decompressing... " + filePathGZ);
        while ((len = ginstream.read(buf)) > 0) 
       {
         outstream.write(buf, 0, len);
       }
        outstream.close();
        ginstream.close();
        thisGZUncompressedFile = new File(outFilename);
        
//  	  return outFilename;
    }
}
