package com.cs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;


public class GetConnectionError {
  
  public static String execute(HttpURLConnection connection) throws IOException{
    BufferedReader br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
    String str, output = "";
    while ((str = br.readLine()) != null) {
      output += str;
    }
    
    br.close();
    connection.disconnect();
    return output;
  }
  
}
