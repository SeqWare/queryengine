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
package com.github.seqware.queryengine.plugins.inmemory;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.plugins.JobRunParameterInterface;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.MapperInterface;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.PluginRunnerInterface;
import com.github.seqware.queryengine.plugins.ReducerInterface;
import com.github.seqware.queryengine.plugins.hbasemr.MRHBasePluginRunner;
import static com.github.seqware.queryengine.plugins.hbasemr.MRHBasePluginRunner.EXT_PARAMETERS;
import static com.github.seqware.queryengine.plugins.hbasemr.MRHBasePluginRunner.serializeParametersToString;
import com.github.seqware.queryengine.util.SGID;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.seqware.common.util.Rethrow;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;

/**
 * Base class for all in-memory plug-in runners.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public final class InMemoryPluginRunner<ResultType> implements PluginRunnerInterface<ResultType>, MapperInterface, ReducerInterface, JobRunParameterInterface {

    private PluginInterface pluginInterface;
    private Long counter = 0L;
    private final String[] serializedParameters;
    private Object[] ext_parameters;
    private Object[] int_parameters;
    private List<FeatureSet> sourceSet;
    private FeatureSet destSet;

    public InMemoryPluginRunner(PluginInterface pluginInterface, Reference reference, List<FeatureSet> inputSet, Object[] parameters) {
        if (reference != null) {
            throw new UnsupportedOperationException();
        }

        this.pluginInterface = pluginInterface;
        CreateUpdateManager manager = SWQEFactory.getModelManager();
        //outputSet should attach to the original reference
        FeatureSet outputSet = manager.buildFeatureSet().setReferenceID(inputSet.iterator().next().getReferenceID()).build();
        manager.close();

        byte[][] sSet = new byte[inputSet.size()][];//SWQEFactory.getSerialization().serialize(inputSet);
        for (int i = 0; i < sSet.length; i++) {
            sSet[i] = SWQEFactory.getSerialization().serialize(inputSet.get(i));
        }
        byte[] dSet = SWQEFactory.getSerialization().serialize(outputSet);
        // pretend to serialize parameters 
        this.serializedParameters = MRHBasePluginRunner.serializeParametersToString(parameters, pluginInterface, sSet, dSet);
        // pretend to de-serialize 
        Configuration conf = new Configuration();
        String[] str_params = serializeParametersToString(parameters, pluginInterface, sSet, dSet);
        conf.setStrings(EXT_PARAMETERS, str_params);
        Job job = null;
        try {
            job = new Job(conf);
        } catch (IOException ex) {
            Rethrow.rethrow(ex);
        }
        Class plugin = MRHBasePluginRunner.transferConfiguration(job, this);

        // this is not currently asynchronous
        if (pluginInterface instanceof MapReducePlugin) {
            MapReducePlugin mrPlugin = null;
            try {
                mrPlugin = (MapReducePlugin) plugin.newInstance();
            } catch (InstantiationException ex) {
                Rethrow.rethrow(ex);
            } catch (IllegalAccessException ex) {
                Rethrow.rethrow(ex);
            }

            mrPlugin.mapInit(this);
            Map<FeatureSet, Collection<Feature>> map = new HashMap<FeatureSet, Collection<Feature>>();
            for (FeatureSet set : inputSet) {
                List<Feature> list = new ArrayList();
                Iterables.addAll(list, set);
                map.put(set, list);
            }
            // mimic filtering 
            map = MRHBasePluginRunner.handlePreFilteredPlugins(map, mrPlugin, this.ext_parameters);

            mrPlugin.map(map, this);
            mrPlugin.mapCleanup();

            mrPlugin.reduceInit();
            // TODO: make this pass through functional in order to simulate MapReduce
            for (Feature f : inputSet.iterator().next()) {
                mrPlugin.reduce(null, null, this);
            }
            mrPlugin.reduceCleanup();

            mrPlugin.cleanup();
        } else {
            throw new UnsupportedOperationException("Scan plugins not supported yet");
        }
    }

    @Override
    public ResultType get() {
        if (pluginInterface.getResultMechanism() == PluginInterface.ResultMechanism.COUNTER) {
            return (ResultType) counter;
        } else if (pluginInterface.getResultMechanism() == PluginInterface.ResultMechanism.SGID) {
            SGID resultSGID = this.getDestSet().getSGID();
            Class<? extends Atom> resultClass = (Class<? extends Atom>) pluginInterface.getResultClass();
            return (ResultType) SWQEFactory.getQueryInterface().getLatestAtomBySGID(resultSGID, resultClass);
        } else if (pluginInterface.getResultMechanism() == PluginInterface.ResultMechanism.BATCHEDFEATURESET) {
            FeatureSet build = MRHBasePluginRunner.updateAndGet(this.getDestSet());
            return (ResultType) build;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void incrementCounter() {
        counter++;
    }

    @Override
    public Object[] getExt_parameters() {
        return this.ext_parameters;
    }

    @Override
    public Object[] getInt_parameters() {
        return this.int_parameters;
    }

    @Override
    public void write(Object textKey, Object text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FeatureSet> getSourceSets() {
        return this.sourceSet;
    }

    @Override
    public FeatureSet getDestSet() {
        return this.destSet;
    }

    @Override
    public PluginInterface getPlugin() {
        return this.getPlugin();
    }

    @Override
    public void setExt_parameters(Object[] params) {
        this.ext_parameters = params;
    }

    @Override
    public void setInt_parameters(Object[] params) {
        this.int_parameters = params;
    }

    @Override
    public void setSourceSets(List<FeatureSet> set) {
        this.sourceSet = set;
    }

    @Override
    public void setDestSet(FeatureSet set) {
        this.destSet = set;
    }
}
