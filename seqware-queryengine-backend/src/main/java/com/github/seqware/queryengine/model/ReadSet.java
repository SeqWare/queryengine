package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMFileReader.ValidationStringency;
import net.sf.samtools.util.CloseableIterator;

/**
 * A ReadSet object that allows access to reads from a BAM/SAM file.
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ReadSet extends MoleculeImpl<ReadSet> {

  /**
   * Constant
   * <code>prefix="ReadSet"</code>
   */
  public final static String prefix = "ReadSet";
  private String readSetName;
  private String readSetPath;
  private String readSetIndexPath;
  private File bamFile = null;
  private SAMFileReader inputSam = null;
  private boolean containsbamRecord = false;//false : the alignment of the returned SAMRecords need only overlap the interval of interest. 

  /**
   * Create a new readset
   */
  private ReadSet() {
    super();
  }

  public void open(File bamFile) {
    close();
    this.bamFile = bamFile;
    this.inputSam = new SAMFileReader(this.bamFile);
    this.inputSam.setValidationStringency(ValidationStringency.SILENT);
  }

  public void close() {
    if (inputSam != null) {
      inputSam.close();
    }
    this.inputSam = null;
    this.bamFile = null;
  }

  public int scanCount(String contig, int start, int end) throws IOException {
    int nCount = 0;
    return(500);
    /*
    CloseableIterator<SAMRecord> iter = null;
    try {
      iter = this.inputSam.query(contig, start, end, this.containsbamRecord);
      while (iter.hasNext()) {
        SAMRecord rec = iter.next();
        ++nCount;
      }
      return nCount;
    } catch (Exception e) {
      throw new IOException(e);
    } finally {
      if (iter != null) {
        iter.close();
      }
    }*/
  }
  
  public CloseableIterator<SAMRecord> scan(String contig, int start, int end) throws IOException {
    CloseableIterator<SAMRecord> iter = null;
    try {
      return(this.inputSam.query(contig, start, end, this.containsbamRecord));
    } catch (Exception e) {
      throw new IOException(e);
    } finally {
      if (iter != null) {
        iter.close();
      }
    }
  }

  public String getReadSetName() {
    return readSetName;
  }

  public void setReadSetName(String readSetName) {
    this.readSetName = readSetName;
  }

  public String getReadSetPath() {
    return readSetPath;
  }

  public void setReadSetPath(String readSetPath) {
    this.readSetPath = readSetPath;
  }

  public String getReadSetIndexPath() {
    return readSetIndexPath;
  }

  public void setReadSetIndexPath(String readSetIndexPath) {
    this.readSetIndexPath = readSetIndexPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    ReadSet rhs = (ReadSet) obj;
    return new EqualsBuilder()
            .append(super.getSGID().getRowKey(), rhs.getSGID().getRowKey())
            .append(readSetName, rhs.getReadSetName())
            .append(readSetPath, rhs.getReadSetPath())
            .append(readSetIndexPath, rhs.getReadSetIndexPath())
            .isEquals();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    // you pick a hard-coded, randomly chosen, non-zero, odd number
    // ideally different for each class
    return new HashCodeBuilder(17, 37).
            append(super.getSGID().getRowKey())
            .append(readSetName)
            .append(readSetPath)
            .append(readSetIndexPath)
            .toHashCode();
  }

  /**
   * Create a new ACL builder
   *
   * @return a {@link com.github.seqware.queryengine.model.ReadSet.Builder} object.
   */
  public static ReadSet.Builder newBuilder() {
    return new ReadSet.Builder();
  }

  /**
   * {@inheritDoc}
   *
   * Create an ReadSet builder started with a copy of this
   */
  @Override
  public ReadSet.Builder toBuilder() {
    ReadSet.Builder b = new ReadSet.Builder();
    b.readset = this.copy(true);
    return b;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class getHBaseClass() {
    return ReadSet.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getHBasePrefix() {
    return ReadSet.prefix;
  }

  public static class Builder extends BaseBuilder {

    private ReadSet readset = new ReadSet();

    public ReadSet.Builder setReadSetName(String readSetName) {
      readset.readSetName = readSetName;
      return this;
    }

    public ReadSet.Builder setReadSetPath(String readSetPath) {
      readset.readSetPath = readSetPath;
      return this;
    }

    public ReadSet.Builder setReadSetIndexPath(String readSetIndexPath) {
      readset.readSetIndexPath = readSetIndexPath;
      return this;
    }

    @Override
    public ReadSet build() {
      if (readset.getManager() != null) {
        readset.getManager().objectCreated(readset);
      }
      return readset;
    }

    @Override
    public Builder setManager(CreateUpdateManager aThis) {
      readset.setManager(aThis);
      return this;
    }

    @Override
    public Builder setFriendlyRowKey(String rowKey) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
