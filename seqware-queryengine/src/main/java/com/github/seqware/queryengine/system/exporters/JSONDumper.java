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
package com.github.seqware.queryengine.system.exporters;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.MRHBaseModelManager;
import com.github.seqware.queryengine.impl.MRHBasePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.plugins.VCFDumperPlugin;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.workers.ImportConstants;
import com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * This will dump VCF files given a FeatureSet that was originally imported from
 * a VCF file.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class JSONDumper {
    /** Constant <code>VCF="VCFVariantImportWorker.VCF"</code> */
    public static final String VCF = Constants.TRACK_TAGSET ? VCFVariantImportWorker.VCF : null;

    private String[] args;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        JSONDumper dumper = new JSONDumper(args);
        dumper.export();
    }

    /**
     * <p>export.</p>
     */
    public void export() {

        if (args.length < 1 || args.length > 2) {
            System.err.println(args.length + " arguments found");
            System.out.println("VCFDumper <featureSetID> [outputFile]");
            System.exit(-1);
        }

        // parse a SGID from a String representation, we need a more elegant solution here
        String featureSetID = args[0];
        SGID sgid = Utility.parseSGID(featureSetID);
        FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(sgid, FeatureSet.class);

        // if this featureSet does not exist
        if (fSet == null) {
            System.out.println("featureSet ID not found");
            System.exit(-2);
        }
        dumpVCFFromFeatureSetID(fSet, (args.length == 2 ? args[1] : null));
    }

    /**
     * <p>Constructor for VCFDumper.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public JSONDumper(String[] args) {
        this.args = args;
    }

    /**
     * <p>outputFeatureInVCF.</p>
     *
     * @param buffer a {@link java.lang.StringBuffer} object.
     * @param feature a {@link com.github.seqware.queryengine.model.Feature} object.
     * @return a boolean.
     */
    
    public static boolean outputFeatureInVCF(StringBuilder buffer, Feature feature, FeatureSet set) {
        boolean caughtNonVCF = false;
         Gson gson = new GsonBuilder().create();
         Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
         Map<String,Object> innerMap = new HashMap<String, Object>();
         innerMap.put("_index", "queryengine");
         innerMap.put("_type", "features");
         innerMap.put("_id", feature.getSGID().getRowKey());
         map.put("index",innerMap);
         
         buffer.append(gson.toJson(map));
         buffer.append("\n");
         
         Gson gson2 = new GsonBuilder().create();
         innerMap.clear();
         innerMap.put("id", feature.getSGID().getRowKey());
         String title = "chr" + feature.getSeqid() + ":" + feature.getStart() + "-" + feature.getStop()+":" 
                 + feature.getTagByKey(VCF,ImportConstants.VCF_REFERENCE_BASE).getValue().toString() 
                 + "->" + feature.getTagByKey(VCF,ImportConstants.VCF_CALLED_BASE).getValue().toString();
         innerMap.put("title", title);
         List<String> databases = new ArrayList<String>();
         if (feature.getTagByKey(VCF, "isDbSNP") != null){
             databases.add("dbsnp");
         } else if (feature.getTagByKey(VCF, "hasSift") != null){
             databases.add("sift");
         }
         innerMap.put("databases",databases);
         List<String> consequences = new ArrayList<String>();
         if (feature.getTagByKey(VCF, "synonymous") != null){
             consequences.add("synonymous");
         } // which tag do we use for "coding"?
         if (consequences.isEmpty()){
            consequences.add("none");
         }
         innerMap.put("consequences", consequences);
         
         innerMap.put("feature_set", set.getSGID().getRowKey());
         // is this correct?
         innerMap.put("variant_type", feature.getStop()-feature.getStart() == 1? "SNV" :"INDEL");
         
         buffer.append(gson2.toJson(innerMap));

        return caughtNonVCF;
    }

    /**
     * <p>dumpVCFFromFeatureSetID.</p>
     *
     * @param fSet a {@link com.github.seqware.queryengine.model.FeatureSet} object.
     * @param file a {@link java.lang.String} object.
     */
    public static void dumpVCFFromFeatureSetID(FeatureSet fSet, String file) {
        BufferedWriter outputStream = null;

        try {
            if (file != null) {
                outputStream = new BufferedWriter(new FileWriter(file));
            } else {
                outputStream = new BufferedWriter(new OutputStreamWriter(System.out));
            }
        } catch (IOException e) {
            Logger.getLogger(JSONDumper.class.getName()).fatal("Exception thrown starting export to file:", e);
            System.exit(-1);
        }
        
        // fall-through if plugin-fails
        try {
            for (Feature feature : fSet) {
                StringBuilder buffer = new StringBuilder();
                boolean caught = outputFeatureInVCF(buffer, feature,fSet);
                outputStream.append(buffer);
                outputStream.newLine();
            }
            outputStream.flush();
        } catch (IOException e) {
            Logger.getLogger(JSONDumper.class.getName()).fatal("Exception thrown exporting to file:", e);
            System.exit(-1);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }
}
