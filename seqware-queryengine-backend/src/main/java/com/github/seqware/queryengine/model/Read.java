package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.util.FSGID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Reads need to be worked on. This is an incomplete 'filler' class
 * that is used such that the ReadSets are properly working.
 */
public class Read extends AtomImpl<Read> {
    
    private final static String[] reservedAttributeNames = new String[] { "qname", "flag", "rname", "pos", "mapq", "cigar", "rnext", "pnext", "tlen", "seq", "qual" };
    
    private String qname;
    private boolean flag;
    private String rname;
    private int pos;
    private int mapq;
    private String cigar;
    private String rnext;
    private int pnext;
    private String seq;
    private String qual;
    
    public enum AdditionalAttributeType { STRING, FLOAT, DOUBLE, LONG, INTEGER };
    
    static {
        // Array has to be sorted, so that it is possible to use a binary search on it:
        Arrays.sort(reservedAttributeNames);
    }
    
    /**
     * Additional attributes can be freely added via a map.
     */
    private HashMap<String, AdditionalAttributeType> additionalAttributes = null;

    private Read() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return Read.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        assert(this.getSGID() instanceof FSGID);
        FSGID fsgid = (FSGID) this.getSGID();
        return fsgid.getTablename();
    }
    
    @Override
    public String getDisplayName() {
      return "read";
    }
    public String getQname() {
        return qname;
    }
    
    public boolean getFlag() {
        return flag;
    }
    
    public String getRname() {
        return rname;
    }
    
    public int getPos() {
        return pos;
    }
    
    public int getMapq() {
        return mapq;
    }
    
    public String getCigar() {
        return cigar;
    }
    
    public String getRnext() {
        return rnext;
    }
    
    public int getPnext() {
        return pnext;
    }
    
    public String getSeq() {
        return seq;
    }
    
    public String getQual() {
        return qual;
    }
    /**
     * Sets an additional attribute not covered by GVF. It is not permitted to have an additional
     * attribute with the same name as the instance variables (case insensitive).
     *
     * @param name Attribute name, which cannot be a GVF attribute (start, stop, pragma, etc).
     * @param value Value of the variable to be set.
     */
    public void setAdditionalAttribute(String name, AdditionalAttributeType value) {
        if (this.additionalAttributes == null)
            this.additionalAttributes = new HashMap<String, AdditionalAttributeType>();

        this.additionalAttributes.put(name, value);
    }

    /**
     * Returns the value of an additional attribute or null of the attribute does not exist.
     *
     * @param attribute The name of the attribute whose value should be returned.
     * @return Value of the attribute or null.
     */
    public AdditionalAttributeType getAdditionalAttribute(String attribute) {
        if (this.additionalAttributes == null)
            return null;

        return this.additionalAttributes.get(attribute);
    }

    /**
     * Returns the names of the additional attributes stored.
     *
     * @return Set of additional attribute names.
     */
    public Set<String> getAdditionalAttributeNames() {
        if (this.additionalAttributes == null)
            return new HashSet<String>();

        return this.additionalAttributes.keySet();
    }

    /**
     * Generic implementation for retrieving the value of a GVF or additional attribute.
     *
     * @param name Name of the attribute to be retrieved, which can be either "start", "stop", etc, or an additional attribute name.
     * @return The value of the attribute, or null if the attribute is not present in this read.
     */
    public Object getAttribute(String name) {
        String nameLowerCase = name.toLowerCase();
        return this.getAdditionalAttribute(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.Read.Builder} object.
     */
    public static Read.Builder newBuilder() {
        return new Read.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public Read.Builder toBuilder() {
        Read.Builder b = new Read.Builder();
        b.read = (Read) this.copy(true);
        return b;
    }

    public static class Builder extends BaseBuilder {

        private Read read = new Read();

        @Override
        public Read build() {
// with lazy molecule sets, it makes less sense to notify that a read is created on build
//            if (read.getManager() != null) {
//                read.getManager().objectCreated(read);
//            }
            return read;
        }

        @Override
        public Builder setManager(CreateUpdateManager aThis) {
            read.setManager(aThis);
            return this;
        }

        @Override
        public Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Read does not support custom rowkey.");
        }
    }
}
