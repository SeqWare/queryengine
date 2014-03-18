package com.github.seqware.queryengine.model;

import com.wordnik.swagger.annotations.*;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "QueryVCFParameters")
public class QueryVCFParameters {
  private String featureSetId;
  private String keyValue;
  private String query;
  private String outputFile;
  private String className;

  public String getFeatureSetId() {
    return featureSetId;
  }
  
  @XmlElement(name = "id")
  @ApiModelProperty(value = "FeatureSet Id", required=true)
  public void setFeatureSetId(String id) {
    this.featureSetId = id;
  }

  public String getKeyValue() {
    return keyValue;
  }
  
  @XmlElement(name = "keyValue")
  public void setKeyValue(String kv) {
    this.keyValue = kv;
  }
  
  public String getQuery() {
    return query;
  }
  
  @XmlElement(name = "query")
  @ApiModelProperty(value = "Query", required=true)
  public void setQuery(String query) {
    this.query = query;
  }
  
  public String getOutputFile() {
    return outputFile;
  }
  
  @XmlElement(name = "output")
  public void setOutputFile(String outputFile) {
    this.outputFile = outputFile;
  }
  
  public String getClassName() {
    return className;
  }

  @XmlElement(name = "className") 
  @ApiModelProperty(value = "QueryDumperInterface Class Name", required=true)
  public void setClassName(String className) {
    this.className = className;
  }
  
  public QueryVCFParameters() {
  }
  
}