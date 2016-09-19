package com.cs.utils;

import java.io.File;
import java.io.FileOutputStream;
import org.apache.log4j.Logger;

public class WriteFileUtil {
  
  final static Logger logger = Logger.getLogger(WriteFileUtil.class);
  
  public static Boolean execute(File file, String content) throws Exception
  {
    logger.debug("Writing To File:" + file +"\ncontent:" + content +"\n");
    try {
      byte[] contentInBytes = content.getBytes();
      FileOutputStream oFile = new FileOutputStream(file);
      oFile.write(contentInBytes);
      oFile.flush();
      oFile.close();
      
      logger.debug("file: " + file + "written successfully \n");
      return true;
    }
    catch (Exception e) {
      logger.debug(e);
      return false;
    }
    
  }
}
