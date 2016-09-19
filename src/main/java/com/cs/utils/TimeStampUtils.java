package com.cs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;


public class TimeStampUtils {
  
  final static Logger logger = Logger.getLogger(TimeStampUtils.class);
  
  public static String getFormatedTimeStamp () {
    Date date = new Date();
    SimpleDateFormat fDate = new SimpleDateFormat("yyyy.MM.dd-hh:mm:ss");
    
    return fDate.format(date);
  }
}
