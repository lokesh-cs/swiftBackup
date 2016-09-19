package com.cs.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

//import com.cs.api.ElasticsearchRestore;
//import com.cs.api.OrientDBRestore;
import com.cs.api.SwiftDataRestore;
import com.cs.utils.ReadFileUtil;
import com.cs.utils.TimeStampUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestoreJob {
  
  Properties          prop   = new Properties();
  final static Logger logger = Logger.getLogger(BackupJob.class);
  
  public RestoreJob()
  {
    try {
      String filename = "config.properties";
      InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
      if (input == null) {
        System.out.println("Sorry, could not find " + filename);
        return;
      }
      prop.load(input);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public Map<String, Object> startRestore(HttpServletRequest request) throws Exception
  {
    try {
      Map<String, String[]> requestMap = request.getParameterMap();
      Map<String, Object> responseMap = new HashMap<String, Object>();
      ArrayList<Map> backupHistoryList = getBackupHistoryList();
      
      if(backupHistoryList.size()==0){
        responseMap.put("status", "No Backup To Restore");
        return responseMap;
      }
      
      if (requestMap.containsKey("backup")) {
        String index = request.getParameter("backup");
        Map<String, Object> restoreMap = backupHistoryList.get(Integer.parseInt(index));
        //Map<String, Object> orientdbMap = (Map) restoreMap.get("orientDb");
        //Map<String, Object> elasticMap = (Map) restoreMap.get("elasticsearch");
        Map<String, Object> swiftMap = (Map) restoreMap.get("swift");
        if(!swiftMap.containsKey("snapshotName")){
        	responseMap = execute(null);
        } else {
        	responseMap = execute(swiftMap.get("snapshotName").toString());
        }
      }
      else {
        Map<String, Object> restoreMap = backupHistoryList.get(0);
        System.out.println(restoreMap);

        Map<String, Object> swiftMap = (Map) restoreMap.get("swift");
        
        if(!swiftMap.containsKey("snapshotName")){
        	responseMap = execute(null);
        } else {
        	responseMap = execute(swiftMap.get("snapshotName").toString());
        }
      }
      
      return responseMap;
      
    }
    catch (Exception e) {
      logger.error(e);
      Map<String, Object> exceptionMap = prepareExceptionResponse(e);
      return exceptionMap;
    }
  }
  
  private Map<String, Object> execute(String swift) throws Exception{
    
    String timestamp = TimeStampUtils.getFormatedTimeStamp();

    Map<String, Object> swiftRestoreResponseMap = new HashMap<String, Object>();
    
    if(!(swift==null || swift=="")){
      swiftRestoreResponseMap = startSwiftRestore(swift);
    } else {
      swiftRestoreResponseMap = startSwiftRestore("2016.09.08-08:28:34.json");
    }
    
    Map<String, Object> responseMap = prepareResposne(swiftRestoreResponseMap);
    responseMap.put("timestamp", timestamp);
    return responseMap;
  }

  private Map startSwiftRestore(String restoreFileName) throws Exception
  {
    logger.debug("############ starting swift Restore ###########");
    
    //Map responseMap = new SwiftDataRestore(prop).startSwiftRestore(restoreFileName);
    Map responseMap = new SwiftDataRestore().startSwiftRestore(restoreFileName);
    //responseMap.put("restorePoint", restoreFileName);
    
    logger.debug("############ swift Restore Successful ###########\n");
    //return responseMap;
    return responseMap;
  }
  
  private ArrayList<Map> getBackupHistoryList() throws Exception {
    String backupHistoryLocation = prop.getProperty("backupHistoryLocation");
    String backupHistoryJsonName = prop.getProperty("backupHistoryJsonName");
    String bakupHistoryFilePath = backupHistoryLocation + backupHistoryJsonName;
    
    File backupHistoryFile = new File(bakupHistoryFilePath);
    String fileContent = ReadFileUtil.execute(backupHistoryFile);
    ArrayList<Map> backupHistoryList = new ObjectMapper().readValue(fileContent, ArrayList.class);
    return backupHistoryList;
  }

  private Map<String, Object> prepareResposne(Map swiftResponseMap){
    
    Map<String, Object> responseMap = new HashMap<String, Object>();
    
    //String orientStatus = orientResponseMap.get("status").toString();
    //String elasticStatus = elasticResponseMap.get("status").toString();
    String swiftStatus = swiftResponseMap.get("status").toString();
    
    if(swiftStatus.equals("Fail")){
      responseMap.put("status", "Fail");
    } else {
      responseMap.put("status", "Success");
    }
    
    //responseMap.put("orientDb", orientResponseMap);
    //responseMap.put("elasticsearch", elasticResponseMap);
    responseMap.put("swift", swiftResponseMap);
    
    return responseMap;
  }

  private Map<String, Object> prepareExceptionResponse(Exception e){
    Map<String, Object> exceptionMap = new HashMap<String, Object>();
    String timestamp = TimeStampUtils.getFormatedTimeStamp();
    exceptionMap.put("status", "Fail");
    exceptionMap.put("error", e.getMessage());
    exceptionMap.put("stack", e.getStackTrace());
    exceptionMap.put("timestamp", timestamp);
    return exceptionMap;
  }
}
