package com.cs.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BackupSummery {
  
  public String status;
  public String type;
  public Object swift;
  public String error;
  
  public String getType()
  {
    return type;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }

  public Object getSwift()
  {
    return swift;
  }
  
  public void setSwift(Object swift)
  {
    this.swift = swift;
  }
  
  public String getStatus()
  {
    return status;
  }
  
  public void setStatus(String status)
  {
    this.status = status;
  }
  
  public String getError()
  {
    return error;
  }
  
  public void setError(String error)
  {
    this.error = error;
  }
  
}
