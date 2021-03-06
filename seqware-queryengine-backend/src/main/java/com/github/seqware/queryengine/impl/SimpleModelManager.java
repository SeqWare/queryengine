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
package com.github.seqware.queryengine.impl;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.backInterfaces.BackEndInterface;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.CreateUpdateManager.State;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.PluginRun.Builder;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.inMemory.*;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import java.util.*;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * A Simple implementation of the CreateUpdateManager interface. We can make
 * this more efficient later.
 *
 * The current idea is that we try to minimize the interaction with the user by
 * using Hibernate/JPA-like semantics.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class SimpleModelManager implements CreateUpdateManager {

    private final Map<String, AtomStatePair> dirtySet = new HashMap<>();
    private final BackEndInterface backend = (BackEndInterface)SWQEFactory.getQueryInterface();

    /**
     * <p>buildFeatureSetInternal.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.FeatureSet.Builder} object.
     */
    public FeatureSet.Builder buildFeatureSetInternal() {
        FeatureSet.Builder fSet = LazyFeatureSet.newBuilder();
        return fSet;
    }

    /**
     * Flush objects to the back-end giving a working list
     *
     * @param workingList a {@link java.util.List} object.
     */
    protected void flushObjects(List<Entry<String, AtomStatePair>> workingList) {
        // stupid workaround, if someone really leans on the flush() command after doing very little, 
        // they can come back fast enough to start duplicating timestamp values, which leads to really bizarre behaviour
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleModelManager.class.getName()).fatal("Exception when waiting to flush objects", ex);
        }

        // create separate working lists for objects destined for different tables
        Map<String, List<Atom>> sortedStore = new HashMap<>();
        Map<String, List<Atom>> sortedUpdate = new HashMap<>();
        for (Entry<String, AtomStatePair> e : workingList) {
            AtomImpl atom = (AtomImpl) e.getValue().atom;
            String cl = atom.getHBasePrefix();
            if (e.getValue().getState() == State.NEW_VERSION) {
                if (!sortedUpdate.containsKey(cl)) {
                    sortedUpdate.put(cl, new ArrayList<Atom>());
                }
                sortedUpdate.get(cl).add(e.getValue().getAtom());
            } else {
                if (!sortedStore.containsKey(cl)) {
                    sortedStore.put(cl, new ArrayList<Atom>());
                }
                sortedStore.get(cl).add(e.getValue().getAtom());
            }
        }
        // we need to transparently go through the tables of individual features and place them within FeatureLists
        // and then extract them back-out when we are done. This should be transparent to the user
        for (Entry<String, List<Atom>> e : sortedStore.entrySet()) {
            createBuckets(e);
        }
        for (Entry<String, List<Atom>> e : sortedUpdate.entrySet()) {
            createBuckets(e);
        }


        // order in order to avoid problems when sets are flushed before their elements (leading to unpopulated 
        // timestamp values) (order is now irrelevant since timestamps are generated locally)
        //Class[] classOrder = {Feature.class, Tag.class, User.class, Reference.class, PluginRun.class, FeatureSet.class, Group.class, TagSet.class, ReferenceSet.class, Plugin.class};
        for (Entry<String, List<Atom>> e : sortedStore.entrySet()) {
            List<Atom> s1 = e.getValue();
            if (s1 != null && !s1.isEmpty()) {
                backend.store(s1.toArray(new Atom[s1.size()]));
            }
        }
        for (Entry<String, List<Atom>> e : sortedUpdate.entrySet()) {
            List<Atom> s2 = e.getValue();
            if (s2 != null && !s2.isEmpty()) {
                backend.update(s2.toArray(new Atom[s2.size()]));
            }
        }

        // we need to get rid of the buckets
        for (Entry<String, List<Atom>> e : sortedStore.entrySet()) {
            removeBuckets(e);
        }
        for (Entry<String, List<Atom>> e : sortedUpdate.entrySet()) {
            removeBuckets(e);
        }
    }

    private void createBuckets(Entry<String, List<Atom>> e) {
        if (e.getKey().startsWith(FeatureList.prefix + StorageInterface.SEPARATOR)) {
            if (Constants.OVERLAP_MODE == Constants.OVERLAP_STRATEGY.NAIVE_OVERLAPS || Constants.OVERLAP_MODE == Constants.OVERLAP_STRATEGY.BINNING) {
                boolean isBinning = Constants.OVERLAP_MODE == Constants.OVERLAP_STRATEGY.BINNING;
                List<Atom> features = e.getValue();
                // position (or bin position) in genome -> feature set ID -> feature list
                Map<String, Map<SGID, FeatureList>> map = new HashMap<>();
                for (Atom a : features) {
                    Feature f = (Feature) a;
                    // ensure that we have a FeatureList available for every feature set / position (or bin position) covered by the feature
                    for (long i = f.getStart(); i < f.getStop(); i++) {
                        String position = f.getSeqid() + i;
                        SGID featureSetID = ((FSGID) f.getSGID()).getFeatureSetID();
                        addToMap(map, position, featureSetID, f, i, false);
                        if (isBinning && SWQEFactory.getStorage() instanceof HBaseStorage){
                            long binPosition =  (i/Constants.BIN_SIZE)*Constants.BIN_SIZE;
                            position = f.getSeqid() +"_bin_"+ binPosition;
                            addToMap(map, position, featureSetID, f, binPosition, true);
                        }
                    }
                }
                // grab all FeatureLists and set them as the value
                List<Atom> bucketList = new ArrayList<>();
                for(Map<SGID, FeatureList> innerMap : map.values()){
                    bucketList.addAll(innerMap.values());
                }
                e.setValue(bucketList);
            } else {
                assert(Constants.OVERLAP_MODE == Constants.OVERLAP_STRATEGY.NONE);

                // sort Features and place them within buckets
                List<Atom> features = e.getValue();
                // sort based on the row key, this should place features with the same start position next to each other
                Collections.sort(features, new Comparator() {
                    @Override
                    public int compare(Object t, Object t1) {
                        assert (t instanceof Feature);
                        assert (t1 instanceof Feature);
                        Feature f0 = (Feature) t;
                        Feature f1 = (Feature) t1;
                        // first separate by featureset
                        if (!((FSGID) f0.getSGID()).getFeatureSetID().equals(((FSGID) f1.getSGID()).getFeatureSetID())) {
                            return ((FSGID) f0.getSGID()).getFeatureSetID().getRowKey().compareTo(((FSGID) f1.getSGID()).getFeatureSetID().getRowKey());
                        }
                        // then by rowkey
                        return f0.getSGID().getRowKey().compareTo(f1.getSGID().getRowKey());
                    }
                });
                // go through and upgrade to buckets
                List<Atom> bucketList = new ArrayList<>(features.size());
                FeatureList featureList = null;
                String lastRowKey = "";
                for (Atom a : features) {
                    Feature f = (Feature) a;
                    // start a new bucket if this is a new rowkey
                    if (!f.getSGID().getRowKey().equals(lastRowKey)) {
                        if (featureList != null) {
                            bucketList.add(featureList);
                        }
                        featureList = new FeatureList();
                        lastRowKey = f.getSGID().getRowKey();
                    }
                    assert (featureList != null && featureList.getFeatures().isEmpty() || featureList != null && featureList.getSGID().getRowKey().equals(f.getSGID().getRowKey()));
                    featureList.add(f);
                    // upgrade the featureList with this redundant information on the way in
                    featureList.impersonate(new FSGID(featureList.getSGID(), (FSGID) f.getSGID()), Constants.TRACK_VERSIONING ? featureList.getPrecedingSGID() : null);
                }
                // handle the last remaining bucket
                if (featureList != null && featureList.getFeatures().size() > 0) {
                    FSGID listfsgid = (FSGID) featureList.getSGID();
                    FSGID firstElement = (FSGID) featureList.getFeatures().get(0).getSGID();
                    assert (listfsgid.getRowKey().equals(firstElement.getRowKey()) && listfsgid.getReferenceName().equals(firstElement.getReferenceName())
                            && listfsgid.getFeatureSetID().equals(firstElement.getFeatureSetID()));
                    bucketList.add(featureList);
                }
                e.setValue(bucketList);
            }
        }
    }

    /**
     * Add a new feature list to the map and add to it a feature
     * @param map
     * @param position a string used to distinguish between buckets
     * @param featureSetID
     * @param feature
     * @param i a position that will be used to determine the rowkey of the bin
     * @param binning create aggregation bins
     */
    private void addToMap(Map<String, Map<SGID, FeatureList>> map, String uniqueString, SGID featureSetID, Feature feature, long position, boolean binning) {
        if (!map.containsKey(uniqueString)) {
            map.put(uniqueString, new HashMap<SGID, FeatureList>());
        }
        if (!map.get(uniqueString).containsKey(featureSetID)) {
            FeatureList featureList = new FeatureList(binning);
            featureList.impersonate(new FSGID(featureList.getSGID(), (FSGID) feature.getSGID(), feature.getSeqid(), position), Constants.TRACK_VERSIONING ? featureList.getPrecedingSGID() : null);
            map.get(uniqueString).put(featureSetID, featureList);
        }
        FeatureList featureList = map.get(uniqueString).get(featureSetID);
        // need to assign feature a new SGID, so the multiple locations don't get confusing
        featureList.add(feature);
    }

    private void removeBuckets(Entry<String, List<Atom>> e) {
        if (e.getKey().startsWith(FeatureList.prefix + StorageInterface.SEPARATOR)) {
            List<Atom> bucketList = e.getValue();
            // go through and upgrade to buckets
            List<Atom> features = new ArrayList<>(bucketList.size());
            for (Atom bucket : bucketList) {
                FeatureList list = (FeatureList) bucket;
                if (list.isAggregate_bin()){
                    // ignore bins
                    continue;
                }
                for (Feature f : list.getFeatures()) {
                    features.add(f);
                }
            }
            e.setValue(features);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void persist(Atom p) {
        AtomImpl pImpl = (AtomImpl) p;
        if (this.dirtySet.containsKey(p.getSGID().toString())) {
            Logger.getLogger(SimpleModelManager.class.getName()).warn("Attempted to persist a managed object, ignored it");
            return;
        }
        // we also have to make sure that the correct manager is associated with this Atom
        pImpl.setManager(this);
        this.dirtySet.put(p.getSGID().toString(), new AtomStatePair(p, State.MANAGED));
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        dirtySet.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        // close connection with all objects
        for (AtomStatePair p : dirtySet.values()) {
            ((AtomImpl) p.atom).setManager(null);
        }
        this.flush(false);
        this.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void flush() {
        this.flush(true);
    }

    /**
     * Normally, when doing a flush, we want to maintain the state of objects
     * that
     *
     * @param maintainState update flushed objects with the current state of
     * things
     */
    protected void flush(boolean maintainState) {
        List<Entry<String, AtomStatePair>> workingList = grabObjectsToBeFlushed();
        flushObjects(workingList);
        if (maintainState) {
            manageFlushedObjects(workingList);
        }
    }

    /**
     * <p>manageFlushedObjects.</p>
     *
     * @param workingList a {@link java.util.List} object.
     */
    protected void manageFlushedObjects(List<Entry<String, AtomStatePair>> workingList) {
        // reset dirty map and put back the objects from the working list
        for (Entry<String, AtomStatePair> e : workingList) {
            // looks redundant
            // dirtySet.remove(e.getKey());
            // e.getValue().setState(State.MANAGED);
            if (e.getValue().getAtom() instanceof MolSetInterface) {
                ((MolSetInterface) e.getValue().getAtom()).rebuild();
            }
            e.getValue().setState(State.MANAGED);
            dirtySet.put(e.getKey().toString(), e.getValue());
        }
    }

    /**
     * <p>grabObjectsToBeFlushed.</p>
     *
     * @return a {@link java.util.List} object.
     */
    protected List<Entry<String, AtomStatePair>> grabObjectsToBeFlushed() {
        // update dirty objects
        // TODO: to deal with the possible semantics of the back-end timestamp, we need to
        // remove objects from a map before they change and then put them back afterwards
        List<Entry<String, AtomStatePair>> workingList = new ArrayList<>();
        for (Entry<String, AtomStatePair> p : dirtySet.entrySet()) {
            if (p.getValue().getState() == State.NEW_CREATION || p.getValue().getState() == State.NEW_VERSION) {
                workingList.add(p);
            }
        }
        for (Entry<String, AtomStatePair> e : workingList) {
            dirtySet.remove(e.getKey());
        }
        return workingList;
    }

    /** {@inheritDoc} */
    @Override
    public FeatureSet.Builder buildFeatureSet() {
        FeatureSet.Builder fSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            fSet = buildFeatureSetInternal();
        }
        assert(fSet != null);
        fSet.setManager(this);
        return fSet;
    }
    
    /** {@inheritDoc} */
    @Override
    public ReadSet.Builder buildReadSet() {
        ReadSet.Builder rSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            rSet = ReadSet.newBuilder().setManager(this);
        }
        assert(rSet != null);
        rSet.setManager(this);
        return rSet;
    }

    /** {@inheritDoc} */
    @Override
    public Reference.Builder buildReference() {
        Reference.Builder ref = null;
        if (backend instanceof SimplePersistentBackEnd) {
            ref = InMemoryReference.newBuilder().setManager(this);
        }
        assert (ref != null);
        return ref;
    }

    /** {@inheritDoc} */
    @Override
    public ReferenceSet.Builder buildReferenceSet() {
        ReferenceSet.Builder rSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            rSet = InMemoryReferenceSet.newBuilder().setManager(this);
        }
        assert (rSet != null);
        return rSet;
    }

    /** {@inheritDoc} */
    @Override
    public TagSet.Builder buildTagSet() {
        TagSet.Builder tSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            tSet = InMemoryTagSet.newBuilder().setManager(this);
        }
        assert (tSet != null);
        return tSet;
    }

    /** {@inheritDoc} */
    @Override
    public Plugin.Builder buildPlugin() {
        Plugin.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            aSet = InMemoryPlugin.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    /** {@inheritDoc} */
    @Override
    public User.Builder buildUser() {
        User.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            aSet = User.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    /** {@inheritDoc} */
    @Override
    public Group.Builder buildGroup() {
        Group.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            aSet = InMemoryGroup.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    /** {@inheritDoc} */
    @Override
    public Tag.Builder buildTag() {
        Tag.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            aSet = Tag.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    /** {@inheritDoc} */
    @Override
    public Feature.Builder buildFeature() {
        Feature.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            aSet = Feature.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }
    
    /** {@inheritDoc} */
    @Override
    public Read.Builder buildRead() {
        Read.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            aSet = Read.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    /** {@inheritDoc} */
    @Override
    public void objectCreated(Atom source) {
        atomStateChange(source, State.NEW_CREATION);
    }

    /** {@inheritDoc} */
    @Override
    public State getState(Atom a) {
        AtomStatePair get = this.dirtySet.get(a.getSGID().toString());
        if (get == null) {return null;}
        return get.state;
    }
    
    /** {@inheritDoc} */
    @Override
    public void atomStateChange(Atom source, State state) {
        // check for valid state transitions
        boolean validTransition = false;
        if (this.dirtySet.containsKey(source.getSGID().toString())) {
            State current = this.dirtySet.get(source.getSGID().toString()).getState();
            if (current == State.MANAGED && state == State.NEW_VERSION) {
                validTransition = true;
            } else if (current == State.MANAGED && state == State.NEW_CREATION) {
                validTransition = true;
            } else if (current == State.UNMANAGED && state == State.MANAGED) {
                validTransition = true;
            } else if (current == State.NEW_CREATION && state == State.MANAGED) {
                validTransition = true;
            } else if (state == State.UNMANAGED){
                // anything should be able to be unmanaged
                validTransition = true;
            }
        } else {
            // assume all other transitions are valid for now
            validTransition = true;
        }
        if (validTransition) {
            this.dirtySet.put(source.getSGID().toString(), new AtomStatePair(source, state));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Builder buildPluginRun() {
        PluginRun.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            return PluginRun.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public void update(Atom originalversion, Atom updatedVersion) {
        ((AtomImpl)updatedVersion).impersonate(originalversion.getSGID());
        ((AtomImpl)updatedVersion).setTimestamp(new Date());
        this.atomStateChange(updatedVersion, CreateUpdateManager.State.NEW_VERSION);
    }

  @Override
  public void delete(Atom p) {
    this.backend.delete(p);
  }

    protected static class AtomStatePair {

        protected AtomStatePair(Atom atom, State state) {
            this.atom = atom;
            this.state = state;
        }
        private Atom atom;
        private State state;

        /**
         * @return the atom
         */
        protected Atom getAtom() {
            return atom;
        }

        /**
         * @param atom the atom to set
         */
        protected void setAtom(Atom p) {
            this.atom = p;
        }

        /**
         * @return the state
         */
        protected State getState() {
            return state;
        }

        /**
         * @param state the state to set
         */
        protected void setState(State state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return state.toString() + " " + atom.toString();
        }
    }
}
