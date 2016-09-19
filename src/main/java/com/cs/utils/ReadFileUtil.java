package com.cs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class ReadFileUtil {

  public static String execute(File file) throws Exception {
    
    BufferedReader br = new BufferedReader(new FileReader(file));

    String sCurrentLine, fileContent = "";
    while ((sCurrentLine = br.readLine()) != null) {
      fileContent += sCurrentLine;
    }
    
    return fileContent;
  }
  
}
