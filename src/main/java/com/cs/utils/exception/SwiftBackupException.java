package com.cs.utils.exception;

public class SwiftBackupException extends Exception {
  
  String response;
  int responseCode;
  
  public SwiftBackupException(String response, int responseCode)
  {
    super();
    this.response = response;
  }
  
  public String getErrorMessage()
  {
    return response;
  }

  
  public int getResponseCode()
  {
    return responseCode;
  }
  
}
