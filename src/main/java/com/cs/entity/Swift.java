package com.cs.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(value=Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Swift {
  
  private String                    timestamp;
  private String                    snapshotName;
  private String                    status;
  private Long                      timetaken;
  private String                    location;
  private String                    response;
  private int                       responseCode;
  private Map<String, Object> totalObjects    = new HashMap<String, Object>();
  private int                       numberOfModifiedObjects;
  private List<Map<String, Object>> modifiedObjects = new ArrayList<Map<String, Object>>();
  private List<String>              objectsNotFound = new ArrayList<>();
  
  public String getTimestamp()
  {
    return timestamp;
  }
  
  public String getSnapshotName()
  {
    return snapshotName;
  }
  
  public void setSnapshotName(String snapshotName)
  {
    this.snapshotName = snapshotName;
  }
  
  public Map<String, Object> getTotalObjects()
  {
    return totalObjects;
  }
  
  public void setTotalObjects(Map<String, Object> totalObjects)
  {
    this.totalObjects = totalObjects;
  }
  
  public List<Map<String, Object>> getModifiedObjects()
  {
    return modifiedObjects;
  }
  
  public void setModifiedObjects(List<Map<String, Object>> modifiedObjects)
  {
    this.modifiedObjects = modifiedObjects;
  }
  
  public List<String> getObjectsNotFound()
  {
    return objectsNotFound;
  }
  
  public void setObjectsNotFound(ArrayList<String> objectsNotFound)
  {
    this.objectsNotFound = objectsNotFound;
  }
  
  public void setTimestamp(String timestamp)
  {
    this.timestamp = timestamp;
  }
  
  public String getStatus()
  {
    return status;
  }
  
  public void setStatus(String status)
  {
    this.status = status;
  }
  
  public Long getTimetaken()
  {
    return timetaken;
  }
  
  public void setTimetaken(Long timetaken)
  {
    this.timetaken = timetaken;
  }
  
  public String getLocation()
  {
    return location;
  }
  
  public void setLocation(String location)
  {
    this.location = location;
  }
  
  public String getResponse()
  {
    return response;
  }
  
  public void setResponse(String response)
  {
    this.response = response;
  }
  
  public int getResponseCode()
  {
    return responseCode;
  }
  
  public void setResponseCode(int responseCode)
  {
    this.responseCode = responseCode;
  }
  
  public int getNumberOfModifiedObjects()
  {
    return numberOfModifiedObjects;
  }
  
  public void setNumberOfModifiedObjects(int numberOfModifiedObjects)
  {
    this.numberOfModifiedObjects = numberOfModifiedObjects;
  }
  
}
