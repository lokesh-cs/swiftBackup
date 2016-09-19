package com.cs.utils;

import java.util.Comparator;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;


public class LastModifiedComparatorUtil implements Comparator<FTPFile> {
  
  public int compare(FTPFile f1, FTPFile f2)
  {
    return f1.getTimestamp().compareTo(f2.getTimestamp());
  }
}
