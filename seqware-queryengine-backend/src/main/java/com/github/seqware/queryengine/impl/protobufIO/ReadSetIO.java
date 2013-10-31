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

import com.github.seqware.queryengine.dto.QueryEngine;
import com.github.seqware.queryengine.dto.QueryEngine.ReadSetPB;
import com.github.seqware.queryengine.dto.QueryEngine.UserPB;
import com.github.seqware.queryengine.model.ReadSet;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;

/**
 * <p>UserIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class ReadSetIO implements ProtobufTransferInterface<ReadSetPB, ReadSet>{

    /** {@inheritDoc} */
    @Override
    public ReadSet pb2m(ReadSetPB readSetpb) {
        ReadSet.Builder builder = ReadSet.newBuilder();
        builder = readSetpb.hasReadSetName() ? builder.setReadSetName(readSetpb.getReadSetName()) : builder;
        builder = readSetpb.hasReadSetPath() ? builder.setReadSetPath(readSetpb.getReadSetPath()) : builder;
        builder = readSetpb.hasReadSetIndexPath() ? builder.setReadSetIndexPath(readSetpb.getReadSetIndexPath()) : builder;
        ReadSet readSet = builder.build();
        UtilIO.handlePB2Atom(readSetpb.getAtom(), readSet);
        UtilIO.handlePB2Mol(readSetpb.getMol(), readSet);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && readSetpb.hasPrecedingVersion()){
           readSet.setPrecedingVersion(pb2m(readSetpb.getPrecedingVersion()));
        }
        return readSet;
    }
    

    /** {@inheritDoc} */
    @Override
    public ReadSetPB m2pb(ReadSet sgid) {
        QueryEngine.ReadSetPB.Builder builder = QueryEngine.ReadSetPB.newBuilder();
        builder = sgid.getReadSetName() != null ? builder.setReadSetName(sgid.getReadSetName()) : builder;
        builder = sgid.getReadSetPath() != null ? builder.setReadSetPath(sgid.getReadSetPath()) : builder;
        builder = sgid.getReadSetIndexPath() != null ? builder.setReadSetIndexPath(sgid.getReadSetIndexPath()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), sgid));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), (MoleculeImpl)sgid));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && sgid.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        ReadSetPB readSetpb = builder.build();
        return readSetpb;
    }

    /** {@inheritDoc} */
    @Override
    public ReadSet byteArr2m(byte[] arr) {
        try {
            ReadSetPB readSetpb = ReadSetPB.parseFrom(arr);
            return pb2m(readSetpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal( "Invalid PB", ex);
        }
        return null;
    }
}
