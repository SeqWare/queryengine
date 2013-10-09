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
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.plugins.contribs.DonorsToMutationsAndGenesAggregationPlugin;
import com.github.seqware.queryengine.plugins.contribs.GenesToDonorsAggregationPlugin;
import com.github.seqware.queryengine.plugins.contribs.MutationsToDonorsAggregationPlugin;
import com.github.seqware.queryengine.plugins.contribs.SimpleMutationsToDonorsAggregationPlugin;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker;
import java.util.Date;

/**
 * This will dump VCF files given a FeatureSet that was originally imported from
 * a VCF file.
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ICGCAggregator {
    /** Constant <code>VCF="VCFVariantImportWorker.VCF"</code> */
    public static final String VCF = Constants.TRACK_TAGSET ? VCFVariantImportWorker.VCF : null;

    private String[] args;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        ICGCAggregator dumper = new ICGCAggregator(args);
        dumper.export();
    }

    /**
     * <p>export.</p>
     */
    public void export() {

        if (args.length != 4) {
            System.err.println(args.length + " arguments found");
            System.out.println("VCFDumper <referenceName> <mutationOutputFile> <geneOutputFile> <donorOutputFile>");
            System.exit(-1);
        }

        // parse a SGID from a String representation, we need a more elegant solution here
        String referenceName = args[0];
        
        // MUTATION CENTRIC
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
        
        // aggregations of donors/project counts by mutation
        System.out.println("Finding Mutations to affected donors/project count aggregation");
        long start = new Date().getTime();
        Utility.dumpFromMapReducePlugin("MUTATION\tMUTATION_ID\tDONORS/PROJECTS_AFFECTED\n", ref, null, MutationsToDonorsAggregationPlugin.class, (args.length == 4 ? args[1] : null));
        long stop = new Date().getTime();
        float diff = ((stop - start) / 1000) / 60;
        System.out.println("Minutes to query: "+diff);
        
        // aggregations of affected donors/projects count by gene
        System.out.println("Finding Genes to affected donors/project count aggregation");
        start = new Date().getTime();
        Utility.dumpFromMapReducePlugin("GENE\tDONORS/PROJECTS_AFFECTED\n", ref, null, GenesToDonorsAggregationPlugin.class, (args.length == 4 ? args[2] : null));
        stop = new Date().getTime();
        diff = ((stop - start) / 1000) / 60;
        System.out.println("Minutes to query: "+diff);
        
        // aggregations of donors to total count SSM mutations for that donor
        System.out.println("Finding Donors to genes and mutations count aggregation");
        start = new Date().getTime();
        Utility.dumpFromMapReducePlugin("DONORS\tGENES/MUTATIONS_AFFECTING\n", ref, null, DonorsToMutationsAndGenesAggregationPlugin.class, (args.length == 4 ? args[3] : null));
        stop = new Date().getTime();
        diff = ((stop - start) / 1000) / 60;
        System.out.println("Minutes to query: "+diff);
        
        // aggregations of donors to total count of genes affected by SSM mutations

    }

    /**
     * <p>Constructor for VCFDumper.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public ICGCAggregator(String[] args) {
        this.args = args;
    }
}

