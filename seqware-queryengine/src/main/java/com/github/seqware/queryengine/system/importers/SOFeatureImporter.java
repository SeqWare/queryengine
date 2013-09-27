package com.github.seqware.queryengine.system.importers;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * Importer using a new interface to the parameters in order to support SO
 * importing and the specification of a tag set and reference to attach to.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class SOFeatureImporter extends Importer {
    
    public static final String PRAGMA_QE_TAG_FORMAT = "seqware.queryengine.tag";
    
    /** Constant <code>SECONDARY_INDEX_HACK=false</code> */
    public static final boolean SECONDARY_INDEX_HACK = false;

    /** Constant <code>ADHOC_TAGSETS_PARAM='a'</code> */
    public static final char ADHOC_TAGSETS_PARAM = 'a';
    /** Constant <code>COMPRESSED_PARAM='c'</code> */
    public static final char COMPRESSED_PARAM = 'c';
    /** Constant <code>INPUT_FILES_PARAM='i'</code> */
    public static final char INPUT_FILES_PARAM = 'i';
    /** Constant <code>NUMBER_THREADS_PARAM='t'</code> */
    public static final char NUMBER_THREADS_PARAM = 't';
    /** Constant <code>OUTPUT_FILE_PARAM='o'</code> */
    public static final char OUTPUT_FILE_PARAM = 'o';
    /** Constant <code>REFERENCE_ID_PARAM='r'</code> */
    public static final char REFERENCE_ID_PARAM = 'r';
    /** Constant <code>TAGSETS_PARAM='s'</code> */
    public static final char TAGSETS_PARAM = 's';
    /** Constant <code>VALUE_SEPARATOR_PARAM=','</code> */
    public static final char VALUE_SEPARATOR_PARAM = ',';
    /** Constant <code>WORKER_CHAR_PARAM='w'</code> */
    public static final char WORKER_CHAR_PARAM = 'w';
    /** Constant <code>BATCH_SIZE_PARAM='b'</code> */
    public static final char BATCH_SIZE_PARAM = 'b';
    /** Constant <code>FEATURE_SET_PARAM='f'</code> */
    public static final char FEATURE_SET_PARAM = 'f';
    /** Constant <code>SECONDARY_INDEX_PARAM='x'</code> */
    public static final char SECONDARY_INDEX_PARAM = 'x';
    
    /** Constant <code>BATCH_SIZE=100000</code> */
    public static final int BATCH_SIZE = 100000;

    /**
     * Command-line interface
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        SGID mainMethod = SOFeatureImporter.runMain(args);
        if (mainMethod == null) {
            System.exit(FeatureImporter.EXIT_CODE_INVALID_FILE);
        }
    }
    
    public static int countLines(File aFile) throws IOException {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(aFile));
            while ((reader.readLine()) != null);
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Interface for mock-testing
     *
     * @param args an array of {@link java.lang.String} objects.
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public static SGID runMain(String[] args) {
        // create Options object
        Options options = new Options();
        Option option1 = OptionBuilder.withArgName("worker").withDescription("(required) the work module and thus the type of file we are working with").isRequired().hasArgs(1).create(WORKER_CHAR_PARAM);
        options.addOption(option1);
        Option option2 = OptionBuilder.withArgName("threads").withDescription("(optional: default 1) the number of threads to use in our import").hasArgs(1).create(NUMBER_THREADS_PARAM);
        options.addOption(option2);
        Option option3 = OptionBuilder.withArgName("compressed").withDescription("(optional) whether we are working with compressed input").create(COMPRESSED_PARAM);
        options.addOption(option3);
        Option option4 = OptionBuilder.withArgName("reference").withDescription("(required) the reference ID to attach our FeatureSet to").isRequired().hasArgs(1).create(REFERENCE_ID_PARAM);
        options.addOption(option4);
        Option option5 = OptionBuilder.withArgName("inputFile").withDescription("(required) input file").hasArg().isRequired().create(INPUT_FILES_PARAM);
        options.addOption(option5);
        Option option6 = OptionBuilder.withArgName("outputFile").withDescription("(optional) output file with our resulting key values").hasArgs(1).create(OUTPUT_FILE_PARAM);
        options.addOption(option6);
        Option option7 = OptionBuilder.withArgName("tagSet").withDescription("(optional) comma separated TagSet IDs, new Tags will be linked to the first set that they appear, these TagSets will not be modified").withValueSeparator(VALUE_SEPARATOR_PARAM).hasArgs().create(TAGSETS_PARAM);
        options.addOption(option7);
        Option option8 = OptionBuilder.withArgName("adHocTagSet").withDescription("(optional) an ID for an ad hoc TagSet, Tags will either be found or added to this set, a new TagSet will be generated if this option is not used").hasArgs().create(ADHOC_TAGSETS_PARAM);
        options.addOption(option8);
        Option option9 = OptionBuilder.withArgName("batch_size").withDescription("(optional) batch-size for the number of features in memory to keep before a flush, will automatically be chosen if not specified, we use " +BATCH_SIZE+ " for now").hasArgs(1).create(BATCH_SIZE_PARAM);
        options.addOption(option9);
        Option option10 = OptionBuilder.withArgName("featureSet").withDescription("(optional) for benchmarking for now, append features to an existing featureset").hasArgs(1).create(FEATURE_SET_PARAM);
        options.addOption(option10);
        if (SOFeatureImporter.SECONDARY_INDEX_HACK){
            Option option11 = OptionBuilder.withArgName("secondaryIndex").withDescription("(optional) spit out a secondary index to STDOUT").hasArgs(1).create(SECONDARY_INDEX_PARAM);
            options.addOption(option11);
        }

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, args);

            String worker = cmd.getOptionValue(WORKER_CHAR_PARAM);
            int threads = Integer.valueOf(cmd.getOptionValue(NUMBER_THREADS_PARAM, "1"));
            boolean compressed = Boolean.valueOf(cmd.hasOption(COMPRESSED_PARAM));
            // process referenceID
            SGID referenceSGID = Utility.parseSGID(cmd.getOptionValue(REFERENCE_ID_PARAM));
            
            int batch_size = Integer.valueOf(cmd.getOptionValue(BATCH_SIZE_PARAM, String.valueOf(BATCH_SIZE)));
            
            String inputFile = cmd.getOptionValue(INPUT_FILES_PARAM);
            List<String> inputFiles = new ArrayList<String>();
            // number of lines per split file
            if (threads > 1){
                Log.info("splitting up file");
                List<File> files = new ArrayList<File>();
                File iFile = new File(inputFile);
                int linesInFile = SOFeatureImporter.countLines(iFile);
                int linesPerSegment = linesInFile/threads;
                // split up file
                LineIterator lineIterator = FileUtils.lineIterator(iFile);
                
                int lineCount = 0;
                File tmpFile = File.createTempFile("segment", "vcf");
                while(lineIterator.hasNext()){
                    List<String> lines = new ArrayList<String>();
                    while (lineCount++ < linesPerSegment && lineIterator.hasNext()){
                        lines.add(lineIterator.next());
                    }
                    lineCount = 0;
                    FileUtils.writeLines(tmpFile, lines);
                    if (lineIterator.hasNext()){
                        files.add(tmpFile);
                        tmpFile = File.createTempFile("segment", "vcf");
                    }
                }
                files.add(tmpFile);
                
                for(File f : files){
                    inputFiles.add(f.getAbsolutePath());
                    f.deleteOnExit();
                }
            } else{
                inputFiles.add(inputFile);
            }

            File outputFile = null;
            if (cmd.hasOption(OUTPUT_FILE_PARAM)){
                String outputFilename = cmd.getOptionValue(OUTPUT_FILE_PARAM);
                outputFile = Utility.checkOutput(outputFilename);
            }
            
            List<SGID> tagSetSGIDs = new ArrayList<SGID>();
            if (Constants.TRACK_TAGSET && cmd.hasOption(TAGSETS_PARAM)) {
                List<String> tagSetIDs = new ArrayList<String>();
                tagSetIDs.addAll(Arrays.asList(cmd.getOptionValues(TAGSETS_PARAM)));
                for (String ID : tagSetIDs) {
                    tagSetSGIDs.add(Utility.parseSGID(ID));
                }
            }
            
            // for benchmarking
            SGID featureSetID = cmd.hasOption(FEATURE_SET_PARAM) ? Utility.parseSGID(cmd.getOptionValue(FEATURE_SET_PARAM)) : null;

            // process ad hoc tag set
            SGID adhocSGID = cmd.hasOption(ADHOC_TAGSETS_PARAM) ? Utility.parseSGID(cmd.getOptionValue(ADHOC_TAGSETS_PARAM)) : null;

            String secondaryIndex = null;
            if (SOFeatureImporter.SECONDARY_INDEX_HACK){
                secondaryIndex = cmd.getOptionValue(SECONDARY_INDEX_PARAM);
            }
            
            SGID mainMethod = FeatureImporter.performImport(referenceSGID, threads, inputFiles, worker, compressed, outputFile, tagSetSGIDs, adhocSGID, batch_size, featureSetID, secondaryIndex);
            if (mainMethod == null) {
                return null;
            }
            return mainMethod;

        } catch (IOException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SOFeatureImporter.class.getSimpleName(), options);
            Logger.getLogger(SOFeatureImporter.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (MissingOptionException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SOFeatureImporter.class.getSimpleName(), options);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (ParseException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SOFeatureImporter.class.getSimpleName(), options);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }
        return null;
    }
}
