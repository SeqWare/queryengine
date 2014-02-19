package com.github.seqware.queryengine.model.impl.hbasemrlazy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.impl.LazyMolSet;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.model.restModels.FeatureSetFacade;
import javax.xml.bind.annotation.XmlTransient;

/**
 * An "lazy" representation of a feature set. This forces individual members to
 * persist and manage their own membership. This version adds M/R for counting.
 *
 * @author dyuen
 * @version $Id: $Id
 */
@JsonSerialize(as=FeatureSetFacade.class)
public class MRLazyFeatureSet extends LazyFeatureSet implements LazyMolSet<FeatureSet, Feature> {

    private String displayName = null;
  
    /**
     * Creates an lazy M/R using feature set.
     */
    protected MRLazyFeatureSet() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    @XmlTransient
    public long getCount() {
        QueryFuture<Long> featureSetCount = SWQEFactory.getQueryInterface().getFeatureSetCount(0, this);
        return featureSetCount.get();
    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.FeatureSet.Builder} object.
     */
    public static FeatureSet.Builder newBuilder() {
        return new MRLazyFeatureSet.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public MRLazyFeatureSet.Builder toBuilder() {
        MRLazyFeatureSet.Builder b = new MRLazyFeatureSet.Builder();
        b.aSet = (MRLazyFeatureSet) this.copy(true);
        return b;
    }

  @Override
  public String getDisplayName() {
    return(displayName);
  }

    public static class Builder extends LazyFeatureSet.Builder {

        public Builder() {
            aSet = new MRLazyFeatureSet();
        }
    }
}
