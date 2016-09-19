package com.cs.utils.exception;

public class FTPUtilException extends Exception {
  
  String errorMessage;
  
  public FTPUtilException(String errorMessage)
  {
    super();
    this.errorMessage = errorMessage;
  }
  
  public String getErrorMessage()
  {
    return errorMessage;
  }
  
}
