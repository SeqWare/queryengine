package com.github.seqware.queryengine.system.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.seqware.queryengine.Benchmarking;
import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.impl.HBaseStorage;

public class QueryVCFDumperBenchmarkTest implements Benchmarking{
	
    private Configuration config;
	private HTable table;
	private Map<String, HTable> tableMap = new HashMap<String, HTable>();
	
	@BeforeClass
	public void setUpTest(){
		tableMap = retriveFeatureTableMap();
		for (Entry<String,HTable> e : tableMap.entrySet()){
			System.out.println(e.getKey());
		}
	}
	
	@Test
	public void testSingleRange(){
		//TODO: Write range test for true, false Overlaps
		
		setNaiveConstant(false);
		//TESTS
		
		setNaiveConstant(true);
		//TESTS
		
	}
	
	@Test
	public void testMultiRange(){
		//TODO: Write range test for true, false Overlaps
		
		setNaiveConstant(false);
		//TESTS
		
		setNaiveConstant(true);
		//TESTS
	}
	
	public void setNaiveConstant(boolean b){
		if (b == true){
			Constants.NAIVE_OVERLAPS = true;
		} else if (b == false){
			Constants.NAIVE_OVERLAPS = false;
		}
	}
	
	public Map<String, HTable> retriveFeatureTableMap(){
		try{
	        this.config = HBaseConfiguration.create();
	        HBaseAdmin hba = new HBaseAdmin(config);
	        
	        HTableDescriptor[] listTables = hba.listTables(HBaseStorage.TEST_TABLE_PREFIX + "[.]Feature[.].*");
	        
	        for (HTableDescriptor des : listTables){
	        	tableMap.put(des.getNameAsString(), 
	        			new HTable(config, des.getNameAsString()));
	        }
	        return tableMap;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
