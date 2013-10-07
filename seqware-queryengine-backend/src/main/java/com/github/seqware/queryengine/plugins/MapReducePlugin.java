package com.github.seqware.queryengine.plugins;

import com.github.seqware.queryengine.plugins.runners.MapperInterface;
import com.github.seqware.queryengine.plugins.runners.ReducerInterface;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import java.util.Collection;
import java.util.Map;

/**
 * An abstracted map-reduce interface. These interfaces will eventually restrict
 * our plug-ins. Currently, they are just place-holders.
 *
 * Implementation orients itself on HBase's TableMapper, TableReduce.
 *
 * @author jbaran
 * @version $Id: $Id
 */
public abstract class MapReducePlugin<MAPREDUCEKEY, MAPREDUCEVALUE, REDUCEKEYOUT, REDUCEVALUEOUT> implements PluginInterface {

    /**
     * Called during the map phase of map/reduce
     * @param atoms a map between featuresets found and collections of atoms available at a particular position
     * @param mapperInterface interface that allows the plug-in to emit keys and values for use by the reducer
     */
    public abstract void map(Map<FeatureSet, Collection<Feature>> atoms, MapperInterface<MAPREDUCEKEY, MAPREDUCEVALUE> mapperInterface);

    /**
     * Reduce implementation that takes mapped atoms and processes them.
     *
     * @param reduceKey Atoms that were selected during the mapping step.
     * @param reduceValues Atoms that are created as a result of the reduce
     * step.
     * @return a ReturnValue object.
     */
    public abstract void reduce(MAPREDUCEKEY reduceKey, Iterable<MAPREDUCEVALUE> reduceValues, ReducerInterface<REDUCEKEYOUT, REDUCEVALUEOUT> reducerInterface);

    @Override
    public Object[] getInternalParameters(){
        return null; /** do nothing */
    }
    
    /**
     * <p>reduceInit.</p>
     *
     * @return a ReturnValue object.
     */
    public void reduceInit() {
        /**
         * empty method that can be overridden
         */
    }

    /**
     * <p>mapInit.</p>
     *
     * @return a ReturnValue object.
     */
    public void mapInit(MapperInterface mapperInterface) {
        /**
         * empty method that can be overridden
         */
    }
    
    /**
     * <p>mapperCleanup.</p>
     *
     * @return a ReturnValue object.
     */
    public void mapCleanup() {
        /**
         * empty method that can be overridden
         */
    }

    /**
     * <p>reducerCleanup.</p>
     *
     * @return a ReturnValue object.
     */
    public void reduceCleanup() {
        /**
         * empty method that can be overridden
         */
    }

    @Override
    public boolean test() {
        /**
         * empty method that can be overridden
         */
        return true;
    }

    @Override
    public boolean verifyParameters() {
        /**
         * empty method that can be overridden
         */
        return true;
    }

    @Override
    public boolean cleanup() {
        /**
         * empty method that can be overridden
         */
        return true;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void init(FeatureSet set, Object... parameters) {
        /** .. */
    }

    /**
     * Silly hack due to how Java Generic are type-erased at compile time. 
     * This class should match MAPREDUCEKEY (if actually emitting keys/values for use by a reducer)
     * @return 
     */
    public Class getMapOutputKeyClass(){
        return null;
    }

    /**
     * Silly hack due to how Java Generic are type-erased at compile time. 
     * This class should match MAPREDUCEVALUE (if actually emitting keys/values for use by a reducer)
     * @return 
     */
    public Class getMapOutputValueClass(){
        return null;
    }

    @Override
    public Class<?> getOutputClass() {
        return null;
    }
}
