package com.cs.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;


public class PrepareHttpUrlConnection {
  
  final static Logger logger = Logger.getLogger(PrepareHttpUrlConnection.class);
  
  public PrepareHttpUrlConnection()
  {
  }
  
  public HttpURLConnection prepareConnection(String uri, String requestMethod, Map<String, Object> requestHeaders) throws IOException
  {
    URL url = new URL(uri);
    logger.debug("\n" + url);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    for (Map.Entry<String, Object> entry : requestHeaders.entrySet())
    {
        connection.setRequestProperty(entry.getKey(),entry.getValue().toString());
    }
    connection.setRequestMethod(requestMethod);
    return connection ;
  }
  
  public HttpURLConnection prepareConnection(String uri, Map<String, Object> requestHeaders) throws IOException
  {
    URL url = new URL(uri);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    for (Map.Entry<String, Object> entry : requestHeaders.entrySet())
    {
        connection.setRequestProperty(entry.getKey(),entry.getValue().toString());
    }
    return connection ;
  }
  
  public HttpURLConnection prepareConnection(String uri, String requestMethod, Map<String, Object> requestHeaders, Boolean useCaches, Boolean doOutput, Boolean doInput) throws IOException
  {
    URL url = new URL(uri);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    if (requestHeaders.containsValue("null")){
      logger.debug("here is the error");
    }
    for (Map.Entry<String, Object> entry : requestHeaders.entrySet())
    {
        connection.setRequestProperty(entry.getKey(),entry.getValue().toString());
    }
    connection.setRequestMethod(requestMethod);
    connection.setUseCaches(useCaches);
    connection.setDoOutput(doOutput);
    connection.setDoInput(doInput);
    
    return connection ;
  }
}
