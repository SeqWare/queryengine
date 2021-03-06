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
package com.github.seqware.queryengine.impl.protobufIO;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.dto.QESupporting;
import com.github.seqware.queryengine.dto.QESupporting.AtomPB;
import com.github.seqware.queryengine.dto.QESupporting.FeatureAtomPB;
import com.github.seqware.queryengine.dto.QueryEngine;
import com.github.seqware.queryengine.dto.QueryEngine.ACLPB;
import com.github.seqware.queryengine.dto.QueryEngine.MoleculePB;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.interfaces.ACL;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * <p>UtilIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class UtilIO {

    private static final TagIO tagIO = new TagIO();

    /**
     * Handle de-serialization of the core atom
     *
     * @param atompb a {@link com.github.seqware.queryengine.dto.QESupporting.AtomPB} object.
     * @param atomImpl a {@link com.github.seqware.queryengine.model.impl.AtomImpl} object.
     */
    public static void handlePB2Atom(QESupporting.AtomPB atompb, AtomImpl atomImpl) {
        for (QESupporting.TagPB t : atompb.getTagsList()) {
            atomImpl.associateTag(tagIO.pb2m(t));
        }
        SGID pID = atompb.hasPrecedingID() ? SGIDIO.pb2m(atompb.getPrecedingID()) : null;
        atomImpl.impersonate(SGIDIO.pb2m(atompb.getSgid()), pID);
        atomImpl.setExternalSerializationVersion(atompb.getSerializationConstant());
    }

    /**
     * Handle serialization of the core atom
     *
     * @param atompb a {@link com.github.seqware.queryengine.dto.QESupporting.AtomPB} object.
     * @param atomImpl a {@link com.github.seqware.queryengine.model.impl.AtomImpl} object.
     * @return a {@link com.github.seqware.queryengine.dto.QESupporting.AtomPB} object.
     */
    public static AtomPB handleAtom2PB(QESupporting.AtomPB atompb, AtomImpl atomImpl) {
        QESupporting.AtomPB.Builder builder = atompb.newBuilderForType();
        builder.setSerializationConstant(atomImpl.getExternalSerializationVersion());
        for (Iterator it = atomImpl.getTags().iterator(); it.hasNext();) {
            //TODO: weird, we shouldn't have to cast here
            Tag t = (Tag) it.next();
            builder.addTags(tagIO.m2pb(t));
        }
        if (atomImpl.getSGID() != null){
            builder.setSgid(SGIDIO.m2pb(atomImpl.getSGID()));
        }
        //builder.setDate(atomImpl.getTimestamp().getTime());
        if (Constants.TRACK_VERSIONING){
            if (atomImpl.getPrecedingSGID() != null) {
                builder.setPrecedingID(SGIDIO.m2pb(atomImpl.getPrecedingSGID()));
            }
        }
        return builder.build();
    }

    /**
     * Handle de-serialization of the core atom just for Features FIXME: this
     * should be collapsible with normal atom
     *
     * @param atompb a {@link com.github.seqware.queryengine.dto.QESupporting.FeatureAtomPB} object.
     * @param feature a {@link com.github.seqware.queryengine.model.Feature} object.
     */
    public static void handlePB2Atom(QESupporting.FeatureAtomPB atompb, Feature feature) {
        for (QESupporting.TagPB t : atompb.getTagsList()) {
            feature.associateTag(tagIO.pb2m(t));
        }
        SGID pID = atompb.hasPrecedingID() ? FSGIDIO.pb2m(atompb.getPrecedingID()) : null;
        feature.impersonate(FSGIDIO.pb2m(atompb.getSgid()), pID);
        feature.setExternalSerializationVersion(atompb.getSerializationConstant());
    }

    /**
     * Handle serialization of the core atom just for Features
     *
     * @param atompb a {@link com.github.seqware.queryengine.dto.QESupporting.FeatureAtomPB} object.
     * @param feature a {@link com.github.seqware.queryengine.model.Feature} object.
     * @return a {@link com.github.seqware.queryengine.dto.QESupporting.FeatureAtomPB} object.
     */
    public static FeatureAtomPB handleAtom2PB(QESupporting.FeatureAtomPB atompb, Feature feature) {
        QESupporting.FeatureAtomPB.Builder builder = atompb.newBuilderForType();
        builder.setSerializationConstant(feature.getExternalSerializationVersion());
        int allTagsSize = 0;
        for (Tag t : feature.getTags()) {
            QESupporting.TagPB m2pb = tagIO.m2pb(t);
            builder.addTags(m2pb);
            if (Constants.OUTPUT_METRICS) {
                allTagsSize += m2pb.toByteArray().length;
                Logger.getLogger(HBaseStorage.class.getName()).info("Tag serialized to " + m2pb.toByteArray().length + " bytes");
            }
        }
        if (Constants.OUTPUT_METRICS) {
            Logger.getLogger(HBaseStorage.class.getName()).info("Total tag size is " + allTagsSize + " bytes");
        }
        assert(feature.getSGID() instanceof FSGID);
        QESupporting.FSGIDPB m2pb = FSGIDIO.m2pb((FSGID) feature.getSGID());
        if (Constants.OUTPUT_METRICS) {
            Logger.getLogger(HBaseStorage.class.getName()).info("FSGID size is " + m2pb.toByteArray().length + " bytes");
        }
        builder.setSgid(m2pb);
        //builder.setDate(feature.getTimestamp().getTime());
        if (Constants.TRACK_VERSIONING){
            if (feature.getPrecedingSGID() != null) {
                builder.setPrecedingID(FSGIDIO.m2pb((FSGID) feature.getPrecedingSGID()));
            }
        }
        return builder.build();
    }

    /**
     * This should handle de-serialization of molecule
     *
     * @param molImpl a {@link com.github.seqware.queryengine.model.impl.MoleculeImpl} object.
     * @param molpb a {@link com.github.seqware.queryengine.dto.QueryEngine.MoleculePB} object.
     */
    public static void handlePB2Mol(QueryEngine.MoleculePB molpb, MoleculeImpl molImpl) {
        ACL.Builder builder = ACL.newBuilder();
        if (molpb.hasAcl()) {
            builder.setRights(molpb.getAcl().getRightsList());
            if (molpb.getAcl().hasUser()) {
                SGID sgid = SGIDIO.pb2m(molpb.getAcl().getUser());
                builder.setOwner(sgid);
            }
            if (molpb.getAcl().hasGrp()) {
                SGID sgid = SGIDIO.pb2m(molpb.getAcl().getGrp());
                builder.setGroup(sgid);
            }
            molImpl.setPermissions(builder.build());
        }
        // handle TTLable
        molImpl.setTTL(molpb.getExpiryTime(), molpb.getCascade());
    }

    /**
     * This should handle serialization of molecule
     *
     * @param molImpl a {@link com.github.seqware.queryengine.model.impl.MoleculeImpl} object.
     * @param molpb a {@link com.github.seqware.queryengine.dto.QueryEngine.MoleculePB} object.
     * @return a {@link com.github.seqware.queryengine.dto.QueryEngine.MoleculePB} object.
     */
    public static MoleculePB handleMol2PB(QueryEngine.MoleculePB molpb, MoleculeImpl molImpl) {
        MoleculePB.Builder builder = molpb.newBuilderForType();

        // handle ACLable
        ACLPB.Builder aclBuilder = molpb.getAcl().newBuilderForType();
        aclBuilder.addAllRights(molImpl.getPermissions().getAccess());
        if (molImpl.getPermissions().getOwnerSGID() != null) {
            aclBuilder.setUser(SGIDIO.m2pb(molImpl.getPermissions().getOwnerSGID()));
        }
        if (molImpl.getPermissions().getGroupSGID() != null) {
            aclBuilder.setGrp(SGIDIO.m2pb(molImpl.getPermissions().getGroupSGID()));
        }
        builder.setAcl(aclBuilder);
        // handle TTLable
        builder.setCascade(molImpl.getCascade());
        builder.setExpiryTime(molImpl.getExpiryTime());
        return builder.build();
    }
}
