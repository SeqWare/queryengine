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
import com.github.seqware.queryengine.dto.QESupporting.TagPB;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;

/**
 * <p>TagIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class TagIO implements ProtobufTransferInterface<TagPB, Tag> {

    /** {@inheritDoc} */
    @Override
    public Tag pb2m(TagPB tag) {
        Tag.Builder builder = Tag.newLightWeightBuilder().setKey(tag.getKey());
        builder = tag.hasPredicate() ? builder.setPredicate(tag.getPredicate()) : builder;
        // tag value
        if (tag.hasVBytes()) {
            builder.setValue(tag.getVBytes());
        } else if (tag.hasVDouble()) {
            builder.setValue(tag.getVDouble());
        } else if (tag.hasVFloat()) {
            builder.setValue(tag.getVFloat());
        } else if (tag.hasVInteger()) {
            builder.setValue(tag.getVInteger());
        } else if (tag.hasVLong()) {
            builder.setValue(tag.getVLong());
        } else if (tag.hasVSGIDRef()) {
            builder.setValue(SGIDIO.pb2m(tag.getVSGIDRef()));
        } else if (tag.hasVString()) {
            builder.setValue(tag.getVString());
        }
        if (tag.hasTagSet()){
            builder.setTagSet(SGIDIO.pb2m(tag.getTagSet()));
        }
        Tag rTag = builder.build();
        UtilIO.handlePB2Atom(tag.getAtom(), rTag);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && tag.hasPrecedingVersion()) {
            rTag.setPrecedingVersion(pb2m(tag.getPrecedingVersion()));
        }
        return rTag;
    }

    /** {@inheritDoc} */
    @Override
    public TagPB m2pb(Tag tag) {
        QESupporting.TagPB.Builder builder = QESupporting.TagPB.newBuilder().setKey(tag.getKey());
        builder = tag.getPredicate() != null ? builder.setPredicate(tag.getPredicate()) : builder;
        // ensure that parent is properly set
        if (Constants.TRACK_TAGSET && tag.getTagSetSGID() == null){
            Logger.getLogger(TagIO.class.getName()).fatal("Tag " + tag.getKey() + " is not owned by a tagset");
            throw new RuntimeException("Tag cannot be flushed without a TagSet");
        } else if (!Constants.TRACK_TAGSET && tag.getTagSetSGID() != null){
            throw new RuntimeException("Tags must be flushed without TagSets");
        }
        // tag value
        if (tag.getValue() != null) {
            if (tag.getvType() == Tag.ValueType.BYTEARR) {
                builder.setVBytes(ByteString.copyFrom((byte[]) tag.getValue()));
            } else if (tag.getvType() == Tag.ValueType.DOUBLE) {
                builder.setVDouble((Double) tag.getValue());
            } else if (tag.getvType() == Tag.ValueType.FLOAT) {
                builder.setVFloat((Float) tag.getValue());
            } else if (tag.getvType() == Tag.ValueType.INTEGER) {
                builder.setVInteger((Integer) tag.getValue());
            } else if (tag.getvType() == Tag.ValueType.LONG) {
                builder.setVLong((Long) tag.getValue());
            } else if (tag.getvType() == Tag.ValueType.SGID) {
                builder.setVSGIDRef(SGIDIO.m2pb((SGID) tag.getValue()));
            } else if (tag.getvType() == Tag.ValueType.STRING) {
                builder.setVString((String) tag.getValue());
            }
        }
        if (tag.getTagSetSGID() != null){
            builder.setTagSet(SGIDIO.m2pb(tag.getTagSetSGID()));
        }
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), tag));
        if (Constants.OUTPUT_METRICS) {
            Logger.getLogger(HBaseStorage.class.getName()).info("Tag atom serialized to " + builder.getAtom().toByteArray().length + " bytes");
        }
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && tag.getPrecedingVersion() != null) {
            builder.setPrecedingVersion(m2pb(tag.getPrecedingVersion()));
        }
        TagPB fMesg = builder.build();
        return fMesg;
    }

    /** {@inheritDoc} */
    @Override
    public Tag byteArr2m(byte[] arr) {
        try {
            TagPB userpb = TagPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal( "Invalid PB", ex);
        }
        return null;
    }
}
