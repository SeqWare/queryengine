package com.github.seqware.queryengine.system.importers.workers;

import com.esotericsoftware.minlog.Log;
import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.model.Read;
import com.github.seqware.queryengine.model.ReadSet;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.system.importers.ReadImporter;
import com.github.seqware.queryengine.system.importers.NaiveReadImporter;
import com.github.seqware.queryengine.util.SGID;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.log4j.Logger;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMRecord;

public class SAMReadImportWorker extends ImportWorker {
  /** Constant <code>SECONDARY_INDEX="sIndex.out"</code> */
    public static final String SECONDARY_INDEX = "sIndex.out";
    /** Constant <code>BAM="BAM"</code> */
    public static final String BAM = "BAM";
    private CreateUpdateManager modelManager;
    private List<TagSet> potentialTagSets = new ArrayList<TagSet>();
    //private TagSet adHocSet;
    //private Map<String, Tag> localCache = new HashMap<String, Tag>();
    private PrintWriter out = null;
    //private TagSet vcfTagSet = null;
    private boolean warnedAboutIDs = false;

    /**
     * <p>Constructor for SAMReadImportWorker.</p>
     */
    public SAMReadImportWorker() {
    }

     /** {@inheritDoc} */
    @Override
    public void run() {
        ReadSet rSet = SWQEFactory.getQueryInterface().getAtomBySGID(ReadSet.class, this.readSetID);
        this.modelManager = SWQEFactory.getModelManager();
        modelManager.persist(rSet);
        File f = new File(input);
        SAMFileReader inputStream;
        try {
        //Attempting to guess the file format
          if (compressed) {
              inputStream = new SAMFileReader(f);
              //inputStream = handleCompressedInput(input);
          } else {
              inputStream = new SAMFileReader(f);
          }
          SAMRecord r;
          Read.Builder rBuilder = modelManager.buildRead();
          SAMRecordIterator iterator = inputStream.iterator();
          int count = 0;
          while ((r = iterator.next()) != null) {
            count++;
            if (count % this.getBatch_size() == 0) {
              modelManager.flush();
              modelManager.clear();
              modelManager.persist(rSet);
            }
            
            //set attributes to rBuilder from SAMRecord
            rBuilder.setQname(r.getReadName());
            rBuilder.setFlag(r.getFlags());
            rBuilder.setRname(r.getReferenceName());
            rBuilder.setPos(r.getAlignmentStart());
            rBuilder.setMapq(r.getMappingQuality());
            rBuilder.setCigar(r.getCigarString());
            rBuilder.setRnext(r.getMateReferenceName());
            rBuilder.setPnext(r.getMateAlignmentStart());
            rBuilder.setSeq(r.getReadString());
            rBuilder.setQual(r.getBaseQualityString());
            Read build = rBuilder.build();
            rSet.add(build);
            
            rBuilder = modelManager.buildRead();
          }
      } catch (Exception e) {
            Logger.getLogger(SAMReadImportWorker.class.getName()).fatal("Exception thrown with file: " + input, e);
            //e.printStackTrace();
            throw new RuntimeException("Error in SAMReadImportWorker");
        } finally {
            // new, this is needed to have the model manager write results to the DB in one big batch
            modelManager.close();
            //pmi.releaseLock();
        }
    }

    public static BufferedReader handleCompressedInput(String input) throws CompressorException, FileNotFoundException {
        BufferedReader inputStream;
        if (input.endsWith("bz2") || input.endsWith("bzip2")) {
            inputStream = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream("bzip2", new BufferedInputStream(new FileInputStream(input)))));
        } else if (input.endsWith("gz") || input.endsWith("gzip")) {
            inputStream = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream("gz", new BufferedInputStream(new FileInputStream(input)))));
        } else {
            throw new RuntimeException("Don't know how to interpret the filename extension for: " + input + " we support 'bz2', 'bzip2', 'gz', and 'gzip'");
        }
        return inputStream;
    }
}