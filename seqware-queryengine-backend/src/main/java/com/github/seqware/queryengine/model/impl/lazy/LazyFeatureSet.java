package com.github.seqware.queryengine.model.impl.lazy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.LazyMolSet;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.LazyReference;
import com.github.seqware.queryengine.util.SGID;
import java.util.*;
import javax.xml.bind.annotation.XmlElement;
import org.apache.log4j.Logger;

/**
 * An "lazy" representation of a feature set. This forces individual members to
 * persist and manage their own membership.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class LazyFeatureSet extends FeatureSet implements LazyMolSet<FeatureSet, Feature> {

    /**
     * Trigger a warning about iteration being expensive only once
     */
    private static boolean EXPENSIVE_ITERATION_WARNED = false;
    /**
     * Associated reference.
     */
    private final LazyReference<Reference> reference = new LazyReference<>(Reference.class);
    /**
     * User defined description of this feature set, can be used to store pragma
     * information for a set of features.
     */
    @XmlElement(name="description")
    @JsonProperty("description")
    private String description = null;

    /**
     * Creates an in-memory feature set.
     */
    protected LazyFeatureSet() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * Get the reference for this featureSet
     * @return 
     */
    @Override
    public Reference getReference() {
        return this.reference.get();
    }

    /** {@inheritDoc}
     * @param feature */
    @Override
    public FeatureSet add(Feature feature) {
        upgradeFeatureSGID(feature);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    private void upgradeFeatureSGID(Feature feature) {
        // try upgrading Feature IDs here, faster than in model manager and FeatureSets should be guaranteed to have references
//        if (!(feature.getSGID() instanceof FSGID)) {
        FSGID fsgid = new FSGID(feature.getSGID(), feature, this);
        processTimestamp(fsgid);
        feature.impersonate(fsgid, Constants.TRACK_VERSIONING ? feature.getPrecedingSGID() : null);
        // we have to use the Feature's manager since there is no guarantee that the set's manager is active
        if (feature.getManager() != null) {
            feature.getManager().atomStateChange(feature, CreateUpdateManager.State.NEW_CREATION);
        }
//        }
    }

    private void processTimestamp(FSGID fsgid) {
        if (!Constants.TRACK_VERSIONING){
            // we want this if this is a new creation, so features get attached to this set (kind of a hack)
            fsgid.setBackendTimestamp(new Date(this.getTimestamp().getTime() - 1));
            return;
        }
        
        // as a convenience, we should have Features in a FeatureSet and the associated FeatureLists take on the time
        // of the FeatureSet
        if (this.getPrecedingSGID() == null) {
            if (getManager() != null && getManager().getState(this) != null && getManager().getState(this) != CreateUpdateManager.State.NEW_CREATION) {
                // we want this if the set has been persisted
                fsgid.setBackendTimestamp(new Date(this.getTimestamp().getTime()));
            } else {
                // we want this if this is a new creation, so features get attached to this set (kind of a hack)
                fsgid.setBackendTimestamp(new Date(this.getTimestamp().getTime() - 1));
            }
        } else {
            fsgid.setBackendTimestamp(this.getTimestamp());
        }
    }

    private void entombFeatureSGID(Feature feature) {
        assert (feature.getSGID() instanceof FSGID);
        FSGID fsgid = (FSGID) feature.getSGID();
        processTimestamp(fsgid);
        fsgid.setTombstone(true);
        if (getManager() != null) {
            getManager().atomStateChange(feature, CreateUpdateManager.State.NEW_CREATION);
        }
    }

    /** {@inheritDoc} */
    @Override
    public FeatureSet add(Feature... elements) {
        for (Feature f : elements) {
            upgradeFeatureSGID(f);
        }
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    /** {@inheritDoc}
     * @param feature */
    @Override
    public FeatureSet remove(Feature feature) {
        entombFeatureSGID(feature);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    /** {@inheritDoc}
     * @param features */
    @Override
    public FeatureSet add(Collection<Feature> features) {
        for (Feature f : features) {
            upgradeFeatureSGID(f);
        }
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    /** {@inheritDoc}
     * @return  */
    @Override
    public Iterator<Feature> getFeatures() {
        return new ConsolidatedFeatureIterator();
    }

    /**
     * FIXME: not really good to return description. Should be short name.
     * @return 
     */
  @Override
  public String getDisplayName() {
    return(description);
  }

    /**
     * Go through the raw iterator from the underlying storage type and batch up
     * the FeatureLists and return them by rows
     */
    public class BatchedRowFeatureListsIterator implements Iterator<List<FeatureList>> {

        /**
         * Wrapped iterator that returns FeatureLists in streams sorted by
         * rowkey then by timestamp
         */
        private final Iterator<FeatureList> iterator;
        /**
         * Stores one FeatureList ahead if from a different row
         */
        private FeatureList lookahead = null;
        // use to verify output from wrapped iterator
        private String rowKey;

        public BatchedRowFeatureListsIterator() {
            this.iterator = SWQEFactory.getStorage().getAllFeatureListsForFeatureSet(LazyFeatureSet.this).iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext() || lookahead != null;
        }

        @Override
        public List<FeatureList> next() {
            List<FeatureList> rowOfFeatureLists = new ArrayList();
            // check the lookahead first to see if anything is cached
            if (lookahead != null) {
                rowOfFeatureLists.add(lookahead);
                lookahead = null;
            }
            // grab anything that is in the current row
            while (iterator.hasNext()) {
                FeatureList nextL = iterator.next();
                // the first time, we need to set the rowkey pre-emptively
                if (rowKey == null) {
                    rowKey = nextL.getSGID().getRowKey();
                }
                // ensure that row keys are ascending
                if (Constants.OVERLAP_MODE  == Constants.OVERLAP_STRATEGY.NONE){
                    assert (nextL.getSGID().getRowKey().compareTo(rowKey) >= 0);
                }
                if (!rowKey.equals(nextL.getSGID().getRowKey())) {
                    // we've moved onto a new row
                    rowKey = nextL.getSGID().getRowKey();
                    lookahead = nextL;
                    break;
                } else {
                    // we're continuing the same row
                    rowOfFeatureLists.add(nextL);
                }
            }
            return rowOfFeatureLists;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * Iterates through Features given FeatureLists sorted by rows
     */
    public class ConsolidatedFeatureIterator implements Iterator<Feature> {

        /**
         * iterates through one row's worth of FeatureLists at a time
         */
        private final BatchedRowFeatureListsIterator iterator;
        /**
         * caches available features
         */
        private final List<Feature> featureCache = new ArrayList<>();

        public ConsolidatedFeatureIterator() {
            this.iterator = new BatchedRowFeatureListsIterator();
        }

        @Override
        public boolean hasNext() {
            // turn crank and fill up the cache
            if (featureCache.isEmpty()) {
                turnCrank();
            }
            return !featureCache.isEmpty();
        }

        @Override
        public Feature next() {
            if (!featureCache.isEmpty()) {
                // if the cache is not empty
                return featureCache.remove(featureCache.size() - 1);
            } else {
                if (featureCache.isEmpty()) {
                    // if the cache is empty,  try to turn the crank
                    turnCrank();
                }
                // return NoSuchElementException iff there is nothing left
                if (featureCache.isEmpty()) {
                    throw new NoSuchElementException();
                }
                return featureCache.remove(featureCache.size() - 1);
            }

        }

        private void turnCrank() {
            while (iterator.hasNext() && featureCache.isEmpty()) {
                List<FeatureList> next = iterator.next();
                Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(next);
                featureCache.addAll(consolidateRow);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * <p>getTablename.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTablename() {
        return FeatureList.prefix + StorageInterface.SEPARATOR + this.reference.get().getName();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Feature> iterator() {
        return getFeatures();
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public long getCount() {
        if (!EXPENSIVE_ITERATION_WARNED) {
            Logger.getLogger(LazyFeatureSet.class.getName()).warn("Iterating through a LazyFeatureSet is expensive, avoid this");
            EXPENSIVE_ITERATION_WARNED = true;
        }
        // expensive, we need to iterate and count
        Iterator<Feature> features = this.getFeatures();
        long count = 0;
        while (features.hasNext()) {
            Feature next = features.next();
            if (((FSGID) next.getSGID()).isTombstone()) {
                continue;
            }
            count++;
        }
        return count;
    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.FeatureSet.Builder} object.
     */
    public static FeatureSet.Builder newBuilder() {
        return new LazyFeatureSet.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public LazyFeatureSet.Builder toBuilder() {
        LazyFeatureSet.Builder b = new LazyFeatureSet.Builder();
        b.aSet = (LazyFeatureSet) this.copy(true);
        return b;
    }

    /** {@inheritDoc}
     * @return  */
    @Override
    @XmlElement(name="description")
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc}
     * @return  */
    @Override
    public SGID getReferenceID() {
        return this.reference.getSGID();
    }

    /** {@inheritDoc}
     * @return  */
    @Override
    public Class getHBaseClass() {
        return FeatureSet.class;
    }

    /** {@inheritDoc}
     * @return  */
    @Override
    public String getHBasePrefix() {
        return FeatureSet.prefix;
    }

    /** {@inheritDoc} */
    @Override
    public void rebuild() {
        /**
         * do nothing
         */
    }

    public static class Builder extends FeatureSet.Builder {

        public Builder() {
            aSet = new LazyFeatureSet();
        }

        @Override
        public FeatureSet build() {
            if (aSet.getReferenceID() == null && aSet.getManager() != null) {
                throw new RuntimeException("Invalid build of Plugin");
            }
            if (aSet.getManager() != null) {
                aSet.getManager().objectCreated(aSet);
            }
            return aSet;
        }

        @Override
        public LazyFeatureSet.Builder setDescription(String description) {
            ((LazyFeatureSet) aSet).description = description;
            return this;
        }

        @Override
        public Builder setReference(Reference reference) {
            ((LazyFeatureSet) aSet).reference.set(reference);
            return this;
        }

        @Override
        public Builder setReferenceID(SGID referenceSGID) {
            ((LazyFeatureSet) aSet).reference.setSGID(referenceSGID);
            return this;
        }
    }
}
