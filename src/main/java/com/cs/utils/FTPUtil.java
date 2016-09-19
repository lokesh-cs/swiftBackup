package com.cs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.cs.utils.exception.FTPUtilException;

/**
 * 
 * All FTP APIs for Swift and Elasticsearch backup/restore operations
 * @author CS25
 *
 */
public class FTPUtil {
  
  final static Logger logger = Logger.getLogger(FTPUtil.class);
	
	public static FTPClient openFtpConnection(String ftpServer, String ftpPort, String ftpUser, String ftpPassword ) throws Exception {
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(ftpServer, Integer.parseInt(ftpPort));
			ftpClient.login(ftpUser, ftpPassword);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
			ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			ftpClient.setKeepAlive(true);
			
		} catch (Exception e) {
			throw e;
		}
		return ftpClient;
	}

	//TODO: Rename method to context
	/**
	 * This method is used for transferring Swift Objects
	 * @param ftpClient
	 * @param url
	 * @param fileName
	 * @param authToken
	 * @return
	 * @throws FTPUtilException 
	 * @throws Exception 
	 */
  public static boolean uploadFileFromInputStream(FTPClient ftpClient, String uploadPath, InputStream inputStream) throws IOException, FTPUtilException
  {
    boolean done;
    logger.debug("******upload path : "+ uploadPath + "\n");
      done = ftpClient.storeFile(uploadPath, inputStream);
      if (done) {
        inputStream.close();
        return true;
      } else {
        throw new FTPUtilException(ftpClient.getReplyString());
      } 
  }
  
  public static boolean checkIfFileExists(FTPClient ftpClient, String path, String fileName) throws IOException
  {
    ArrayList<String> ftpFiles = new ArrayList<String>();
    
    FTPFile[] files = ftpClient.listFiles(path);
    for (FTPFile ftpFile : files) {
      ftpFiles.add(ftpFile.getName());
    }
    
    if(ftpFiles.contains(fileName)){
      return true;
    } else {
      return false;
    }
  }

	public static void uploadFileFromStringInput(FTPClient ftpClient, String uploadPath, String value) throws Exception {
	try {
		    OutputStream outputStream = ftpClient.storeFileStream(uploadPath);
		    outputStream.write(value.getBytes());
		    outputStream.close();
		    boolean status = ftpClient.completePendingCommand();
		    if(!status) {
		      logger.debug("CompletePendingCommand failed");
		      throw new FTPUtilException("File Upload To FTP failed");
		    }
		} catch (FTPUtilException e) {
			throw e;
		} catch (Exception e){
		  throw e;
		}
	}
	
	
	/**
	 * Creates the Elastic search Backup on FTP Server
	 * @return
	 * @throws IOException 
	 */
	
	public static boolean restoreData(FTPClient ftpClient, String filePath, OutputStream outputStream) throws IOException
	{
    boolean success = ftpClient.retrieveFile(filePath, outputStream);
    if (success) {
      return true;
    }
    else {
      return false;
    }
	}
	
	
	public static String getLatestBackupFile(FTPFile[] listOfRemoteBackUpFiles){
    String latestedCreatedJsonFilename = null;
    try {
      FTPFile latestCreatedFile = Collections.max(Arrays.asList(listOfRemoteBackUpFiles),
          new LastModifiedComparatorUtil());
      latestedCreatedJsonFilename = latestCreatedFile.getName();
    }
    catch (Exception e) {
      throw e;
    }
    return latestedCreatedJsonFilename;
	}
	
	public static void disconnectFromFTPServer (FTPClient ftpClient) throws IOException{
	  ftpClient.logout();
	  ftpClient.disconnect();
	}
}

