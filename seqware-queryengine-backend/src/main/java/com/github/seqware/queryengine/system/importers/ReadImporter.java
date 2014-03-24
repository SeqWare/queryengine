package com.github.seqware.queryengine.system.importers;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.ReadSet;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.workers.ImportWorker;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.sourceforge.seqware.common.util.Log;
import org.apache.log4j.Logger;


/**
 * A copy of the FeatureImporter, attempting to work for ReadSets
 * @author jho
 *
 */
public class ReadImporter {
  /**
   * Constant
   * <code>EXIT_CODE_INVALID_ARGS=1</code>
   */
  public final static int EXIT_CODE_INVALID_ARGS = 1;
  /**
   * Constant
   * <code>EXIT_CODE_EXISTING_NAME=5</code>
   */
  public final static int EXIT_CODE_EXISTING_NAME = 5;
  /**
   * Constant
   * <code>EXIT_CODE_INVALID_FILE=10</code>
   */
  public final static int EXIT_CODE_INVALID_FILE = 10;
  /**
   * Constant
   * <code>READ_SET_ID="ReadSetID"</code>
   */
  public final static String READ_SET_ID = "ReadSetID";
  
  /**
   * This method does the actual work of importing given properly parsed
   * parameters
   *
   * @param referenceID a {@link com.github.seqware.queryengine.util.SGID}
   * object.
   * @param inputFiles a {@link java.util.List} object.
   * @param workerModule a {@link java.lang.String} object.
   * @param compressed a boolean.
   * @param outputFile a {@link java.io.File} object.
   * @param tagSetSGIDs a {@link java.util.List} object.
   * @param batch_size a int.
   * @return SGID if successful, null if not
   * @param adhocTagSetID a {@link com.github.seqware.queryengine.util.SGID}
   * object.
   * @param existingReadSet a
   * {@link com.github.seqware.queryengine.util.SGID} object.
   * @param secondaryIndex a {@link java.lang.String} object.
   */
  protected static SGID performImport(int threadCount, List<String> inputFiles, String workerModule, boolean compressed, 
          File outputFile, int batch_size, SGID existingReadSet, String secondaryIndex) {
        // objects to access the mutation datastore
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        // create a centralized ReadSet
        ReadSet readSet;
        if (existingReadSet == null) {
            readSet = modelManager.buildReadSet().build();
        } else {
            readSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(existingReadSet, ReadSet.class);
            Logger.getLogger(ReadImporter.class.getName()).info("Appending to existing ReadSet: " + readSet.getSGID().getRowKey());
        }
  
//        TagSet adHocSet;
//        if (Constants.TRACK_TAGSET) {
//      
//            // process ad hoc set if given, create a new one if there is not
//            if (adhocTagSetID != null) {
//                adHocSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(adhocTagSetID, TagSet.class);
//            } else {
//                adHocSet = modelManager.buildTagSet().setName("ad hoc tag set for ReadSet " + readSet.getSGID().getRowKey()).build();
//            }
//        }
        // we don't really need the central model manager past this point 
        modelManager.close();
      
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        // a pointer to this object (for thread coordination)
      
        try {
      
      //      // settings
      //      SeqWareSettings settings = new SeqWareSettings();
      //      settings.setStoreType("berkeleydb-mismatch-store");
      //      settings.setFilePath(dbDir);
      //      settings.setCacheSize(cacheSize);
      //      settings.setCreateMismatchDB(create);
      //      settings.setCreateConsequenceAnnotationDB(create);
      //      settings.setCreateDbSNPAnnotationDB(create);
      //      settings.setCreateCoverageDB(create);
      //      settings.setMaxLockers(locks);
      //      settings.setMaxLockObjects(locks);
      //      settings.setMaxLocks(locks);
      
            // store object
            // store = factory.getStore(settings);
      
            List<Future<?>> futures = new ArrayList<Future<?>>(inputFiles.size());
      
      
            Iterator<String> it = inputFiles.iterator();
            //ImportWorker[] workerArray = new ImportWorker[inputFiles.size()];
            int index = 0;
            while (it.hasNext()) {
      
                // print message
                String input = (String) it.next();
                Logger.getLogger(ReadImporter.class.getName()).info("Starting worker thread to process file: " + input);
      
                // make a worker and launch it
                Class processorClass = Class.forName("com.github.seqware.queryengine.system.importers.workers." + workerModule);
                ImportWorker worker = (ImportWorker) processorClass.newInstance();
                worker.setWorkerName("PileupWorker" + index);
      //              worker.setPmi(pmi);
      //              worker.setStore(modelManager);
                worker.setInput(input);
                worker.setFeatureSetID(readSet.getSGID());
                worker.setBatch_size(batch_size);
                worker.setKeyIndex(secondaryIndex);
      
                // FIXME: most of the rest aren't used, I should consider cleaning this up
                worker.setCompressed(compressed);
                worker.setMinCoverage(0);
                worker.setMaxCoverage(0);
                worker.setMinSnpQuality(0);
                worker.setIncludeSNV(false);
                worker.setFastqConvNum(0);
                worker.setIncludeIndels(false);
                worker.setIncludeCoverage(false);
                worker.setBinSize(0);
      
                futures.add(pool.submit(worker));
                index++;
      
            }
      
            // finally close, checkpoint is part of the process
            // modelManager.close();
            //store.close();
      
            Logger.getLogger(ReadImporter.class.getName()).info("Joining threads");
            // join the threads, wait for each to finish
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException ex) {
                    Log.fatal(ex);
                    throw new RuntimeException(ex);
                } catch (ExecutionException ex) {
                    Log.fatal(ex);
                    throw new RuntimeException(ex);
                }
            }
            Logger.getLogger(ReadImporter.class.getName()).info("Threads finished");
            pool.shutdown();
      
        } // TODO: clearly this should be expanded to include closing database etc 
        catch (Exception e) {
            Logger.getLogger(ReadImporter.class.getName()).fatal("Exception thrown with file: \n", e);
            return null;
        }
      
      
        // clean-up
        SWQEFactory.getStorage().closeStorage();
        System.out.println("ReadSet written with an ID of:");
        String outputID = readSet.getSGID().getRowKey();
        System.out.println(outputID);
        Map<String, String> keyValues = new HashMap<String, String>();
        keyValues.put(READ_SET_ID, outputID);
        Utility.writeKeyValueFile(outputFile, keyValues);
        return readSet.getSGID();
    }
  
    public static SGID naiveRun(String[] args) {
        if (args.length < 3) {
            System.err.println("Only " + args.length + " arguments found");
            //System.out.println("FeatureImporter <worker_module> <db_dir> <create_db> <cacheSize> <locks> "
            //        + "<max_thread_count> <compressed_input> <input_file(s)>");
            System.out.println("ReadImporter <worker_module> <max_thread_count> <compressed_input> <input_file1[,input_file(s)]> [output_file]");
            System.exit(EXIT_CODE_INVALID_ARGS);
        }
  
        String workerModule = args[0];
        int threadCount = Integer.parseInt(args[1]);
        boolean compressed = false;
        if ("true".equals(args[2])) {
            compressed = true;
        }
    
//        String referenceID = args[3];
//        SGID referenceSGID = null;
//    
//        for (Reference reference : SWQEFactory.getQueryInterface().getReferences()) {
//            if (reference.getName().equals(referenceID)) {
//                referenceSGID = reference.getSGID();
//                break;
//            }
//        }
//        // see if this referenceID already exists
//        if (referenceSGID == null) {
//            CreateUpdateManager modelManager = SWQEFactory.getModelManager();
//            Reference ref = modelManager.buildReference().setName(referenceID).build();
//            referenceSGID = ref.getSGID();
//            modelManager.flush();
//        }
//    
        ArrayList<String> inputFiles = new ArrayList<String>();
        inputFiles.addAll(Arrays.asList(args[3].split(",")));
    
        // handle output
        File outputFile = null;
        if (args.length == 5) {
            try {
                outputFile = Utility.checkOutput(args[5]);
            } catch (IOException ex) {
                System.exit(ReadImporter.EXIT_CODE_INVALID_ARGS);
            }
        }
  
        return performImport(threadCount, inputFiles, workerModule, compressed, outputFile, 100000, null, null);
    }
}