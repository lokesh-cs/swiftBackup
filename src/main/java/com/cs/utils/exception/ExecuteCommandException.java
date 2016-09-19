package com.cs.utils.exception;


public class ExecuteCommandException extends Exception{
  
  private int exitCode;
  private String response;

  
  public ExecuteCommandException(int exitCode, String response)
  {
    super();
    this.exitCode = exitCode;
    this.response = response;
  }

  public String getErrorMessage()
  {
    return response;
  }
  
  public int getExitCode()
  {
    return exitCode;
  }

  
}
