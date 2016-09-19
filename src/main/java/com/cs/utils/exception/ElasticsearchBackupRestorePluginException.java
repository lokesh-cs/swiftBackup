package com.cs.utils.exception;

public class ElasticsearchBackupRestorePluginException extends Exception {
  
  String timestamp;
  
  public ElasticsearchBackupRestorePluginException(String timestamp)
  {
    super();
    this.timestamp = timestamp;
  }
  
  public String getTimestamp()
  {
    return timestamp;
  }
  
}
