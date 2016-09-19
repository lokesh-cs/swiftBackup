package com.cs.utils;

import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class ClearFileUtil {
  
  final static Logger logger = Logger.getLogger(ClearFileUtil.class);
  
  public static Boolean execute(String fileName) throws Exception {
    try {
      logger.debug("Clearing File: " + fileName +"\n");
      
      PrintWriter writer = new PrintWriter(fileName);
      writer.print("");
      writer.close();
      
      logger.debug("File " + fileName +" Cleared Successfully \n");
      return true;
    }
    catch (Exception e) {
      logger.debug(e);
      return false;
    }
  }
}
