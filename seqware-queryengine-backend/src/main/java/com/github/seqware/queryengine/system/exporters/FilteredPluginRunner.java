package com.github.seqware.queryengine.system.exporters;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.plugins.PrefilterByAttributesPlugin;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.*;
import org.apache.commons.cli.HackedPosixParser;
import java.io.File;
import java.util.Stack;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

/**
 * Run an arbitrary plugin with the ability to prime an arbitrary FeatureFilter
 * at runtime.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class FilteredPluginRunner {

    /**
     * Constant
     * <code>QUERY_PARAM_STRING='s'</code>
     */
    private static final char QUERY_PARAM_STRING = 's';
    /**
     * Constant
     * <code>OUTPUTFILE_PARAM='o'</code>
     */
    private static final char OUTPUTFILE_PARAM = 'o';
    private static final char REFERENCE_PARAM = 'r';
    private static final char PLUGIN_CLASS = 'p';

    /**
     * Command-line interface
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        File outputFile = FilteredPluginRunner.runMain(args);
        if (outputFile == null || !outputFile.exists()) {
            System.exit(FeatureImporter.EXIT_CODE_INVALID_FILE);
        }
    }

    /**
     * Interface for mock-testing
     *
     * @param args an array of {@link java.lang.String} objects.
     * @return a {@link java.util.Stack} object.
     */
    public static File runMain(String[] args) {
        // create Options object
        Options options = new Options();
        Option option1 = OptionBuilder.withArgName("reference name").withDescription("(required) the name of the reference we will be querying and exporting").isRequired().hasArgs(1).create(REFERENCE_PARAM);
        options.addOption(option1);
        Option option3a = OptionBuilder.withArgName("plugin name").withDescription("(required) full classname for the desired plugin to be run").isRequired().hasArgs(1).create(PLUGIN_CLASS);
        options.addOption(option3a);
        Option option3b = OptionBuilder.withArgName("query string").withDescription("(optional) plain text query").hasArgs(1).create(QUERY_PARAM_STRING);
        options.addOption(option3b);
        Option option4 = OptionBuilder.withArgName("output file").withDescription("(required) output file for the VCF").isRequired().hasArgs(1).create(OUTPUTFILE_PARAM);
        options.addOption(option4);

        try {
            CommandLineParser parser = new HackedPosixParser();
            CommandLine cmd = parser.parse(options, args);

            String referenceName = cmd.getOptionValue(REFERENCE_PARAM);
            // parse a SGID from a String representation, we need a more elegant solution here
            Reference ref = null;
            for (Reference r : SWQEFactory.getQueryInterface().getReferences()) {
                if (referenceName.equals(r.getName())) {
                    ref = r;
                    break;
                }
            }

            // if this featureSet does not exist
            if (ref == null) {
                System.out.println("reference not found");
                System.exit(-2);
            }

            String outputFile = cmd.getOptionValue(OUTPUTFILE_PARAM);
            Class<? extends PluginInterface> plugin = processPluginClass(cmd);
            RPNStack processPlainTextQuery = null;
            if (cmd.getOptionValue(QUERY_PARAM_STRING) != null){
                processPlainTextQuery = processPlainTextQuery(cmd);              
                boolean success = Utility.dumpFromMapReducePlugin(null, ref, null, plugin, outputFile, processPlainTextQuery); 
                // TO-DO: if we wanted to expose additional parameters to the user-provided plugin, we would add them here
                // TO-DO: filtering plugins reserve the first parameter for a RPNStack
            } else{
                boolean success = Utility.dumpFromMapReducePlugin(null, ref, null, plugin, outputFile, new RPNStack());
            }
            return new File(outputFile);

        } catch (MissingOptionException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(FilteredPluginRunner.class.getSimpleName(), options);
            Logger.getLogger(FilteredPluginRunner.class.getName()).fatal(null, e);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (Exception e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(FilteredPluginRunner.class.getSimpleName(), options);
            Logger.getLogger(FilteredPluginRunner.class.getName()).fatal(null, e);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }
        return null;
    }

    private static Class<? extends PluginInterface> processPluginClass(CommandLine cmd) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String paramClassName = cmd.getOptionValue(PLUGIN_CLASS);
        if (paramClassName == null) {
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }

        Class c = Class.forName(paramClassName);
        return c;
    }

    private static RPNStack processPlainTextQuery(CommandLine cmd) {
        String query = cmd.getOptionValue(QUERY_PARAM_STRING);
        try {
            RPNStack compiledQuery = RPNStack.compileQuery(query);
            return compiledQuery;
        } catch (RecognitionException ex) {
            Logger.getLogger(FilteredPluginRunner.class.getName()).fatal(null, ex);
            System.out.println("Error lexing/parsing query");
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }
        return null;
    }
}
