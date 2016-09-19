package com.cs.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cs.api.SwiftDataRestore;

/**
 * @author CS25 Restore swift data API
 */
// TODO: add API for restoredata and change classname

@WebServlet("/swiftrestore")
public class SwiftRestoreService extends HttpServlet {
  
  Properties prop = new Properties();
  
  public SwiftRestoreService()
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
  
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
  {
    try {
      PrintWriter out = response.getWriter();
      String restoreFileName = request.getParameter("file");
      if (restoreFileName == null){
        out.println("Provide Restore File Name");
      } else {
      //new SwiftDataRestore(prop).startSwiftRestore(restoreFileName);
      System.out.println("!!!!!!!!!!!!!!!DONE!!!!!!!!!!!!!!!!!!!!!");
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
