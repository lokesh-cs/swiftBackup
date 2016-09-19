package com.cs.utils;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cs.api.PrepareHttpUrlConnection;


public class DeleteSwiftObject {
  
  final static Logger logger = Logger.getLogger(DeleteSwiftObject.class);
  
  public static void delete(String URL, String token, String containerName, String objectName) throws Exception {
    String uri = URL + "/" + containerName + "/" + objectName;
    Map<String, Object> requestHeaders = new HashMap<String, Object>();
    requestHeaders.put("X-Auth-Token", token);
    
    HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "DELETE", requestHeaders);
    int status = connection.getResponseCode();
    if (status == HttpURLConnection.HTTP_NO_CONTENT) {
      logger.debug("DELETED OBJECT : " + objectName );
    } else {
      logger.error("Something Went Wrong While Deleting Object - " + status);
    }
  }
}
