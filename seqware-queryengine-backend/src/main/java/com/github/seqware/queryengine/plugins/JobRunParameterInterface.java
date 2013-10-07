/*
 * Copyright (C) 2013 SeqWare
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

import com.github.seqware.queryengine.model.FeatureSet;
import java.util.List;

/**
 * Internal back-end interface used to transfer variables from and to various kinds of plugin runners
 * @author dyuen
 */
public interface JobRunParameterInterface {
    
    public Object[] getExt_parameters();

    public Object[] getInt_parameters();

    public List<FeatureSet> getSourceSets();

    public FeatureSet getDestSet();
    
    public void setExt_parameters(Object[] params);

    public void setInt_parameters(Object[] params);

    public void setSourceSets(List<FeatureSet> set);

    public void setDestSet(FeatureSet set);
}
