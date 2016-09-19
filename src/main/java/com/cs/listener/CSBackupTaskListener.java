package com.cs.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


public class CSBackupTaskListener implements ServletContextListener {

  final static Logger logger = Logger.getLogger(CSBackupTaskListener.class);
  
  @Override
  public void contextInitialized(ServletContextEvent arg0)
  {
    
    
  }
  
  @Override
  public void contextDestroyed(ServletContextEvent arg0)
  {

  }

}
