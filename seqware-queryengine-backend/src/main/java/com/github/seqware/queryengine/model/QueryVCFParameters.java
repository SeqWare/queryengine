package com.github.seqware.queryengine.model;

import com.wordnik.swagger.annotations.*;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "QueryVCFParameters")
public class QueryVCFParameters {
  private String featureSetId;
  private String query;

  public String getFeatureSetId() {
    return featureSetId;
  }
  
  @XmlElement(name = "id")
  @ApiModelProperty(value = "FeatureSet Id", required=true)
  public void setFeatureSetId(String id) {
    this.featureSetId = id;
  }
  
  public String getQuery() {
    return query;
  }
  
  @XmlElement(name = "query")
  @ApiModelProperty(value = "Query", required=true)
  public void setQuery(String query) {
    this.query = query;
  }
  
  public QueryVCFParameters() {
    featureSetId = "";
    query = "";
  }
  
}