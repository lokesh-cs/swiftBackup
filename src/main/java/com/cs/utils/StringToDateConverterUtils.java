package com.cs.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StringToDateConverterUtils {
  
  public static Date convert (String value, String format) throws Exception {
    DateFormat df;
    if (format == null) {
      df = new SimpleDateFormat("yyyy.MM.dd-hh:mm:ss");
    } else {
      df = new SimpleDateFormat(format);
      //df = new SimpleDateFormat("EEEE MMM dd HH:mm:ss IST yyyy");
    }
    Date date = df.parse(value);
    return date;
  }
}
