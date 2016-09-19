package com.cs.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.cs.job.RestoreJob;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author CS25 Restore swift data API
 */
// TODO: add API for restoredata and change classname

//@WebServlet("/restore")
public class CompleteRestoreService extends HttpServlet {

    Properties prop = new Properties();
    final static Logger logger = Logger.getLogger(CompleteRestoreService.class);
    
  public CompleteRestoreService()
  {
    try {
      String filename = "config.properties";
      InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
      if (input == null) {
        System.out.println("Sorry, could not find " + filename);
        return;
      }
      prop.load(input);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public void init() throws ServletException {
    logger.debug("**********My restore servlet has been initialized****************\n");
  }  
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
  {
    try {
      Long start = System.currentTimeMillis();
      ObjectMapper jsonResponse = new ObjectMapper();
      logger.debug("\n*************SERVLET RESTORE start**************\n");
      Map<String, Object> responseMap = new RestoreJob().startRestore(request);
      logger.debug("\n*************SERVLET RESTORE end**************\n");
      String response1=jsonResponse.writeValueAsString(responseMap);
      Long end = System.currentTimeMillis();
      response.setContentType("text/html");
      //SendEmail restoreMail=new SendEmail();
      //restoreMail.sendMail(response1,"Restore");
      PrintWriter out = response.getWriter();
      out.println("" + response1 + "");
      
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
