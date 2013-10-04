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
package com.github.seqware.queryengine.system;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.MRHBaseModelManager;
import com.github.seqware.queryengine.impl.MRHBasePersistentBackEnd;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.system.exporters.VCFDumper;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.util.SGID;
import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

/**
 * Utility procedures for interacting outside the API
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class Utility {
    /**
     * Parse a timestamp-less SGID from a String representation
     *
     * @param stringSGID a {@link java.lang.String} object.
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public static SGID parseSGID(String stringSGID) {
        SGID sgid;
        try{
            UUID uuid = UUID.fromString(stringSGID);
            sgid = new SGID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), 0, null);
        } catch (IllegalArgumentException e){
            String fRowKey = stringSGID;
            sgid = new SGID(0,0,0,fRowKey);
        }
        return sgid;
    }
    
    /**
     * Write to output file a tab separated key value file represented by map
     *
     * @param outputFile a {@link java.io.File} object.
     * @param map a {@link java.util.Map} object.
     */
    public static void writeKeyValueFile(File outputFile, Map<String, String> map) {
        if (outputFile != null) {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
                for (Map.Entry<String, String> e : map.entrySet()) {
                    out.println(e.getKey() + "\t" + e.getValue());
                    Logger.getLogger(FeatureImporter.class.getName()).info("Writing "+e.getKey() + " " + e.getValue() +" file");
                }
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(FeatureImporter.class.getName()).fatal("Could not write to output file");
            }
        }
    }
    
    /**
     * Check whether we can create the output for a particular filename
     *
     * @param filename a {@link java.lang.String} object.
     * @return reference to the newly created output file
     * @throws java.io.IOException if any.
     */
    public static File checkOutput(String filename) throws IOException {
        File outputFile = new File(filename);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        try {
            outputFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(FeatureImporter.class.getName()).fatal("Could not create output file");
            throw ex;
        }
        return outputFile;
    }

    /**
     * <p>dumpVCFFromFeatureSetID.</p>
     *
     * @param fSet a {@link com.github.seqware.queryengine.model.FeatureSet} object.
     * @param file a {@link java.lang.String} object.
     */
    public static boolean dumpFromMapReducePlugin(String header, Reference ref, FeatureSet fSet, Class<? extends PluginInterface> arbitraryPlugin, String file, Object ... params) {
        BufferedWriter outputStream = null;
        try {
            if (file != null) {
                outputStream = new BufferedWriter(new FileWriter(file));
            } else {
                outputStream = new BufferedWriter(new OutputStreamWriter(System.out));
            }
            if (header != null) {
                outputStream.append(header);
            }
        } catch (IOException e) {
            Logger.getLogger(Utility.class.getName()).fatal("Exception thrown starting export to file:", e);
            System.exit(-1);
        }
        if (SWQEFactory.getQueryInterface() instanceof MRHBasePersistentBackEnd) {
            if (SWQEFactory.getModelManager() instanceof MRHBaseModelManager) {
                try {
                    QueryFuture<File> future = SWQEFactory.getQueryInterface().getFeaturesByPlugin(0, arbitraryPlugin, ref, params);
                    File get = future.get();
                    Collection<File> listFiles = FileUtils.listFiles(get, new WildcardFileFilter("part*"), DirectoryFileFilter.DIRECTORY);
                    for (File f : listFiles) {
                        BufferedReader in = new BufferedReader(new FileReader(f));
                        IOUtils.copy(in, outputStream);
                        in.close();
                    }
                    get.deleteOnExit();
                    assert (outputStream != null);
                    outputStream.flush();
                    outputStream.close();
                    return true;
                } catch (IOException e) {
                    Logger.getLogger(VCFDumper.class.getName()).fatal("Exception thrown exporting to file:", e);
                    System.exit(-1);
                } catch (Exception e) {
                    Logger.getLogger(VCFDumper.class.getName()).fatal("MapReduce exporting failed, falling-through to normal exporting to file", e);
                }
            }
        }
        return false;
    }
}
