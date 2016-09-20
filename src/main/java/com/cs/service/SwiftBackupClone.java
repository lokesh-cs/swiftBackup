package com.cs.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.cs.api.SwiftClone;
import com.cs.job.BackupJob;

@WebServlet("/cloneswift")
public class SwiftBackupClone extends HttpServlet{

	Properties prop = new Properties();
	final static Logger logger = Logger.getLogger(CompleteBackupService.class);
	public SwiftBackupClone()
	{
		try {
			String filename = "swift.properties";
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
		logger.debug("**********My backup servlet has been initialized****************\n");
	} 

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		try {
			Long start = System.currentTimeMillis();
			logger.debug("\n*************SERVLET BACKUP start**************\n");
			String response1 = new SwiftClone().startSwiftClone();
			logger.debug("\n*************SERVLET BACKUP end**************\n");
			Long end = System.currentTimeMillis();
			response.setContentType("text/html");

			PrintWriter out = response.getWriter();
			out.println(response1);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
