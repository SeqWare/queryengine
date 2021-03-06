package net.sourceforge.seqware.queryengine.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.SecondaryCursorIterator;

/**
 * <p>CompareDatabasesWithROCOld class.</p>
 *
 * @author boconnor
 *
 * Purpose:
 *
 * This is an example only and not a supported program.
 *
 * This is an example program that compares two databases and uses one as the
 * known good to compute true and false positive rates at different coverage
 * cutoffs.  It's useful when evaluating the performance of variant calling
 * while comparing to a known answer (such as Sanger sequencing or SNP chip).
 *
 * The process works like this:
 *
 * # identify each 300 base window around heterozygous SNVs (+/-150 bases) in the known good database (or whatever range you specify)
 * # for each of these windows, pull this region back from the unknown database
 * # for each of the SNV positions within this window in unknown database meeting criteria check to see if the variant is seen in known good variants and bin the result as appropriate
 * ## based on coverage
 * ## true positive: both unknown and known good agree (can agree based on seen variant or seen variant AND right zygosity)
 * ## false positive: unknown is not reference but known good makes no SNV call there
 * # generate table that can be used to create a ROC curve for these TPR/FPR as a function of coverage (or some other criteria)
 *
 * FIXME: is this filtering by SNV type correctly?  Don't want to compare to indels for example!
 * @version $Id: $Id
 */
public class CompareDatabasesWithROCOld {
  
  final static int BIN_SIZE = 1000;

  /**
   * A simple program that takes a list of posit
   *
   * @param args an array of {@link java.lang.String} objects.
   */
  public static void main(String[] args) {

    if (args.length < 19) {
      System.out.println("CompareDatabasesWithROC <db_dir_gold_standard> <db_dir_unknown> <cache_size_gold_standard> <cache_size_unknown> <locks_gold_standard>" +
      		"<locks_unknown> <lookup_tag_gold_standard> <other_tags_for_lookup (not currently used)> <range_around_known_good_event> <min_coverage_gold_standard> <min_coverage_unknown> " +
      		"<max_coverage_gold_standard> <max_coverage_unknown> <min_observations_gold_standard> <min_observations_unkown> " +
          "<min_observations_per_strand_gold_standard> <min_observations_per_strand_unknown> " +
          "<minSNPPhred> <output_ROC_data_report_file>");
      System.exit(-1);
    }

    try {
      
      // read vars
      String dbDirA = args[0];
      String dbDirB = args[1];
      long cacheSizeA =  Long.parseLong(args[2]);
      long cacheSizeB =  Long.parseLong(args[3]);
      int locksA = Integer.parseInt(args[4]);
      int locksB = Integer.parseInt(args[5]);
      String tag = args[6];
      String tags = args[7];
      int range = Integer.parseInt(args[8]);
      int minCoverageA = Integer.parseInt(args[9]);
      int minCoverageB = Integer.parseInt(args[10]);
      int maxCoverageA = Integer.parseInt(args[11]);
      int maxCoverageB = Integer.parseInt(args[12]);
      int minObservationsA = Integer.parseInt(args[13]);
      int minObservationsB = Integer.parseInt(args[14]);
      int minObservationsPerStrandA = Integer.parseInt(args[15]);
      int minObservationsPerStrandB = Integer.parseInt(args[16]);
      int minSNPPhred = Integer.parseInt(args[17]);
      String summaryOutputFile = args[18];
      
      // keep track of the variant IDs already processed
      HashMap<Long, String> processed = new HashMap<Long, String>();
      
      //output
      BufferedWriter summaryWriter = new BufferedWriter(new FileWriter(new File(summaryOutputFile)));
      
      //data vars
      HashMap<Integer, HashMap<String, Integer>> counts = new HashMap<Integer, HashMap<String, Integer>>();
      
      // create factory
      BerkeleyDBFactory factory = new BerkeleyDBFactory();
      BerkeleyDBStore storeA = null;
      BerkeleyDBStore storeB = null;

      // open database
      SeqWareSettings settings = new SeqWareSettings();
      settings.setStoreType("berkeleydb-mismatch-store");
      settings.setFilePath(dbDirA);
      settings.setCacheSize(cacheSizeA);
      settings.setMaxLockers(locksA);
      settings.setMaxLockObjects(locksA);
      settings.setMaxLocks(locksA);
      settings.setCreateMismatchDB(false);
      settings.setCreateConsequenceAnnotationDB(false);
      settings.setCreateDbSNPAnnotationDB(false);
      settings.setCreateCoverageDB(false);
      settings.setReadOnly(true);
      storeA = factory.getStore(settings);

      // open database b
      settings = new SeqWareSettings();
      settings.setStoreType("berkeleydb-mismatch-store");
      settings.setFilePath(dbDirB);
      settings.setCacheSize(cacheSizeB);
      settings.setMaxLockers(locksB);
      settings.setMaxLockObjects(locksB);
      settings.setMaxLocks(locksB);
      settings.setCreateMismatchDB(false);
      settings.setCreateConsequenceAnnotationDB(false);
      settings.setCreateDbSNPAnnotationDB(false);
      settings.setCreateCoverageDB(false);
      settings.setReadOnly(true); 
      storeB = factory.getStore(settings);
      
      
      // query gold standard, compare to unknown
      if (storeA != null && storeB != null) {
        
        System.out.println("Opened both stores OK.");
        
        SecondaryCursorIterator itA = storeA.getMismatchesByTag(tag);
        
        System.out.println("Looking for tag: "+tag);
        
        int i = 1000;
        while (itA.hasNext()) {
          
          i--;
          Variant var = (Variant) itA.next();
          
          if (i <= 0) { 
            i = 1000; 
            System.out.println("Looking at variant: "+var.getContig()+":"+var.getStartPosition()+"-"+var.getStopPosition()+" "+var.getReferenceBase()+"->"+var.getCalledBase());
            System.out.println("  info: read count: "+var.getReadCount()+" called base count: "+var.getCalledBaseCount()+" forward: "+var.getCalledBaseCountForward()+" reverse: "+var.getCalledBaseCountReverse()+
                " phred: "+var.getConsensusCallQuality());
            
            // DEBUG
            //break;
          }
          
          // if these are all true then this is a position that should be examined
          int start = var.getStartPosition() - range;
          if (start < 0) { start = 0; }
          int stop = var.getStopPosition() + range;
          
          // stores the list of variants in the unkown that are within this range
          HashMap<Integer, Integer> variantsInUnknown = new HashMap<Integer, Integer>();
          
          if (var.getReadCount() >= minCoverageA && var.getReadCount() <= maxCoverageA && 
              var.getCalledBaseCount() >= minObservationsA && var.getCalledBaseCountForward() >= minObservationsPerStrandA &&
              var.getCalledBaseCountReverse() >= minObservationsPerStrandA && var.getConsensusCallQuality() >= minSNPPhred) {
            
            //System.out.println("Considering variant from known good: "+var.getContig()+":"+var.getStartPosition()+"-"+var.getStopPosition()+" ref: "+var.getReferenceBase()+" -> var: "+var.getCalledBase()+" zygosity: "+var.getZygosity());
         
            // CALCULATE TRUE POSITIVES AND FALSE POSITIVES
            // now lookup the variants within this range in the unknown dataset
            LocatableSecondaryCursorIterator itB = storeB.getMismatches(var.getContig(), start, stop);
            //System.out.println("Looking at range "+start+"-"+stop+" in unknown");
            
            while(itB.hasNext()) {
              
              Variant varB = (Variant)itB.next();
              long id = Long.parseLong(varB.getId());
              
              if (varB.getType() == Variant.SNV && !processed.containsKey(new Long(id))) {
                
                // store info that this was seen
                variantsInUnknown.put(varB.getStartPosition(), varB.getReadCount());
                processed.put(id, "1");
                
                //System.out.println("  Considering variant from unknown: "+varB.getContig()+":"+varB.getStartPosition()+"-"+varB.getStopPosition()+" ref: "+varB.getReferenceBase()+" -> var: "+varB.getCalledBase());
                
                boolean foundMatch = false;
                
                LocatableSecondaryCursorIterator itSpecificA = storeA.getMismatches(varB.getContig(), varB.getStartPosition(), varB.getStopPosition());
                
                //if (itSpecificA.hasNext()) {
                //  System.out.println("  Can find corresponding A as I should");
                //}
                
                while(itSpecificA.hasNext()) {
                  Variant specificA = (Variant)itSpecificA.next();
                  // FIXME: this is a hack, I think I'm storing the venter variants in the wrong field!!!!
                  if ((specificA.getCalledBase().equals(varB.getCalledBase()) && specificA.getReferenceBase().equals(varB.getReferenceBase())) || (specificA.getCalledBase().equals(varB.getReferenceBase()) && specificA.getReferenceBase().equals(varB.getCalledBase()))) { 
                    foundMatch = true; 
                    //System.out.println("    Found match! "+specificA.getCalledBase()+" "+varB.getCalledBase());
                  }
                }
                itSpecificA.close();
                
                Integer coverage = new Integer(varB.getReadCount());
                HashMap<String, Integer> result = counts.get(coverage);
                if (result == null) { result = new HashMap<String, Integer>(); counts.put(coverage, result); }
                if (foundMatch) {
                  Integer tpCounts = result.get("tp");
                  if (tpCounts == null) { tpCounts = new Integer(1); }
                  else { tpCounts++; }
                  result.put("tp", tpCounts);
                } else {
                  Integer fpCounts = result.get("fp");
                  if (fpCounts == null) { fpCounts = new Integer(1); }
                  else { fpCounts++; }
                  result.put("fp", fpCounts);
                }
                // FIXME: isn't really used
                Integer total = result.get("total");
                if (total == null) { total = new Integer(1); }
                else { total++; }
                result.put("total", total);
                              
              }
            }
            itB.close();
            
            
            // CALCULATE TRUE NEGATIVES AND FALSE NEGATIVES
            
            /*
             * FALSE NEGATIVE
             * 1) find all the variants in gold standard within the range
             * 2) for each, check to see if in the unknown a) the variant was not called called in unknown and b) coverage at that position meets cutoff
             * 3) if both are true then increment false negative
             */
            // find all the known good variants in this region
            LocatableSecondaryCursorIterator itATN = storeA.getMismatches(var.getContig(), start, stop);
            
            // need to do this since the API works in blocks
            int startCov = start / BIN_SIZE;
            int stopCov = stop / BIN_SIZE;
            startCov = startCov * BIN_SIZE;
            stopCov = (stopCov * BIN_SIZE) + (BIN_SIZE - 1);
            
            // now get coverage for this region
            LocatableSecondaryCursorIterator covIt = storeB.getCoverages(var.getContig(), startCov, stopCov);
            // iterate over coverages and add to common structure
            HashMap<Integer, Integer> coverages = new HashMap<Integer, Integer>();
            while(covIt.hasNext()) {
              Coverage cov = (Coverage)covIt.next();
              for (Integer pos : cov.getCoverage().keySet()) {
                coverages.put(pos, cov.getCoverage(pos));
              }
            }
            covIt.close();
            // now have a unified coverage object for this region even if it spans multiple coverage regions!
            
            // keep a hash of locations where known good has variants
            HashMap<Integer, String> variantsInKnownGood = new HashMap<Integer, String>();
            
            // now iterate over know good variants in region
            while(itATN.hasNext()) {
              
              Variant varATN = (Variant)itATN.next();
              
              if (varATN.getType() == Variant.SNV) {
              
                // add this location to a list of locations where the known good has variant calls
                variantsInKnownGood.put(varATN.getStartPosition(), varATN.getCalledBase());
                
                LocatableSecondaryCursorIterator itBTN = storeB.getMismatches(varATN.getContig(), varATN.getStartPosition(), varATN.getStopPosition());
                boolean match = false;
                // lookup to see if called in unknown too
                while(itBTN.hasNext()) {
                  Variant varBTN = (Variant)itBTN.next();
                  if ((varATN.getCalledBase().equals(varBTN.getCalledBase()) && varATN.getReferenceBase().equals(varBTN.getReferenceBase())) || 
                      (varATN.getCalledBase().equals(varBTN.getReferenceBase()) && varATN.getReferenceBase().equals(varBTN.getCalledBase()))) { 
                    match = true; 
                    //System.out.println("    Found match! "+specificA.getCalledBase()+" "+varB.getCalledBase());
                  }
                }
                itBTN.close();
                
                // lookup coverage in unknown
                boolean isCoveredByUnknown = false;
                Integer coverageAtB = coverages.get(new Integer(varATN.getStartPosition()));
                if (coverageAtB != null && coverageAtB > 0) { isCoveredByUnknown = true; }
                
                // if not called but coverage still good increment the false negative
                if (!match && isCoveredByUnknown) {
                  HashMap<String, Integer> result = counts.get(coverageAtB);
                  if (result == null) { result = new HashMap<String, Integer>(); counts.put(coverageAtB, result); }
                  Integer fnCounts = result.get("fn");
                  if (fnCounts == null) { fnCounts = new Integer(1); }
                  else { fnCounts++; }
                  result.put("fn", fnCounts);
                }
              
              }
              
            }
            itATN.close();
            // at this point all the false negatives should be recorded so I can calculate TPR
            
            /*
             * TRUE NEGATIVE
             * 1) find all the positions in gold standard within range that don't have variants
             * 2) check to see if coverage meets cutoff and no variant called at each position of range
             * 3) if both are true then increment true negative
             * 
             */
            for(int pos=start; pos<=stop; pos++) {
              if(!variantsInKnownGood.containsKey(new Integer(pos)) && !variantsInUnknown.containsKey(new Integer(pos))) {
                Integer coverageAtB = coverages.get(new Integer(pos));
                if (coverageAtB != null && coverageAtB > 0) {
                  HashMap<String, Integer> result = counts.get(coverageAtB);
                  if (result == null) { result = new HashMap<String, Integer>(); counts.put(coverageAtB, result); }
                  Integer tnCounts = result.get("tn");
                  if (tnCounts == null) { tnCounts = new Integer(1); }
                  else { tnCounts++; }
                  result.put("tn", tnCounts);
                }
              }
            }
            
          }
        }
        itA.close();
        
        // at this point I should have a fully populated hashmap structure that relates coverage
        // to the tp/fp/total and now I need to print out this information
        for (Integer count : counts.keySet()) {
          HashMap<String, Integer> result = counts.get(count);
          summaryWriter.write("Cov: "+count+" tp: "+result.get("tp")+" fp: "+result.get("fp")+" tn: "+result.get("tn")+" fn: "+result.get("fn")+"\n");
        }
      
        // close everything
        storeA.close();
        storeB.close();
      }
      
      summaryWriter.close();
    } catch (Exception e) {
      
    }
  }
}
