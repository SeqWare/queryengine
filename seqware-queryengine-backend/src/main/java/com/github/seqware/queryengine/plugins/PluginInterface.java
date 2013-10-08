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
package com.github.seqware.queryengine.plugins;

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import java.io.Serializable;

/**
 * This will describe the interface custom plugins must conform to. Details are
 * backend specific of course. I think we should try to make these plugins
 * compatible with GATK if possible. It would be really cool to be able to drop
 * in, say, LociWalkers from GATK without modification. I'm not sure how
 * practical this this, thought. We can, at least, use their same basic
 * interface, see
 * http://www.broadinstitute.org/gsa/wiki/index.php/Your_first_walker:
 *
 * Here's an extremely basic interface, we will really need to work on this and
 * figure out what the object inputs are for these methods and what they should
 * return (just using ReturnValue as placeholder).
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface PluginInterface extends Serializable {
    
    public enum ResultMechanism {
        COUNTER, /** return a counter incremented on the context */
        SGID, /** return an id value that has been manipulated */
        BATCHEDFEATURESET, /** version a featureset and then return it */
        FILE /** return a specific file */
    }

    /**
     * Initialize this plug-in, this is called once when the plug-in is starting
     * a job. Not currently used. 
     *
     * @param set input feature set that we will be operating on
     * @param parameters parameters that the plug-in will require
     * @return a {@link com.github.seqware.queryengine.plugins.PluginInterface.ReturnValue} object.
     */
    public void init(FeatureSet set, Object... parameters);

    /**
     * Not currently used.
     * @return a {@link com.github.seqware.queryengine.plugins.PluginInterface.ReturnValue} object.
     */
    public boolean test();

    /**
     * Not currently used. Could be used to verify that parameters to a plugin are valid. 
     *
     * @return a {@link com.github.seqware.queryengine.plugins.PluginInterface.ReturnValue} object.
     */
    public boolean verifyParameters();

    /**
     * Not currently used. Could be used to verify that a plugin properly tears down its environment.
     *
     * @return a {@link com.github.seqware.queryengine.plugins.PluginInterface.ReturnValue} object.
     */ 
    public boolean cleanup();
    
    /**
     * Non-blocking call to determine whether the result is ready.
     *
     * @return a boolean.
     */
    public boolean isComplete();
    
    /**
     * Used by the query engine back-end to store parameters for a plugin. 
     * These parameters allow us to initialize a plugin when running on a remote cluster. 
     * This group of parameters is intended for use the back-end internally. 
     * @return 
     */
    public Object[] getInternalParameters();
    
    /**
     * Describe which result mechanism this plugin uses 
     * @return 
     */
    public ResultMechanism getResultMechanism();
    
    /**
     * Return the class of the final output for a plugin.
     * 
     * When returning an SGID, we need to know what the corresponding class for a SGID. 
     * @return 
     */
    public Class<?> getResultClass();
    
    /**
     * Pass-through to the mapreduce job's setOutputFormatClass
     * @return 
     */
    public Class<?> getOutputClass();
}
