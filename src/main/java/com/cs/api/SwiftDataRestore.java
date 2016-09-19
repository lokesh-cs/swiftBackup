package com.cs.api;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.cs.entity.Account;
import com.cs.entity.BackupDetails;
import com.cs.entity.Container;
import com.cs.entity.Objects;
import com.cs.utils.FTPUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SwiftDataRestore {
  /*
   * this code only restore account mentioned in config file as "swiftaccounttoberestored" 
   * */
  // TODO: else part in restore account
  private static final String GET                 = "GET";
  private static final String PUT                 = "PUT";
  private static final String POST                = "POST";
  private static String       AUTH_TOKEN;
  private static String       AUTH_URL;
  private OutputStream        outputStream;
  private PrintWriter         writer;
  Properties                  prop = new Properties();
  String                      ftpUser;
  String                      ftpPassword ;
  String                      ftpServer;
  String                      ftpPort;
  ArrayList<String>           backUpJsonFileNames = new ArrayList<String>();
  FTPClient                   ftpClient           = null;
  String restoreType = "";
  final static Logger logger = Logger.getLogger(SwiftDataRestore.class);
  
  public SwiftDataRestore()
  {
    /*this.prop = prop;
    setFtpParameters(prop);*/
	  try {
	      String filename = "config.properties";
	      InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
	      if (input == null) {
	        logger.debug("Sorry, could not find " + filename);
	        return;
	      }
	      prop = new Properties();
	      prop.load(input);
	      setFtpParameters(prop);
	    }
	    catch (IOException ex) {
	      logger.error(ex);
	    }
  }
  
  private void setFtpParameters(Properties prop){
    ftpUser = prop.getProperty("ftpUser");
    ftpPassword = prop.getProperty("ftpPassword");
    ftpServer = prop.getProperty("ftpServer");
    ftpPort = prop.getProperty("ftpPort");
  }
  
  public Map startSwiftRestore(String restoreFileName)
  {
    try {
      ftpClient = new FTPUtil().openFtpConnection(ftpServer, ftpPort, ftpUser, ftpPassword);
      Boolean status = false;
      
      // check if full restore or restore from some specific time 
      //restoreType = prop.getProperty("restoreType");
      
      if (restoreFileName == null) {
        // full restore
        getBackUpJson(ftpClient, null);
        for (String backUpJsonFileName : backUpJsonFileNames) {
          status = execute(backUpJsonFileName);
        }
      }
      else {
        // restore from give time
        DateFormat timeFormat = new SimpleDateFormat("yyyy.MM.dd-hh:mm:ss");
        Date restoreTime = timeFormat.parse(restoreFileName);
        getBackUpJson(ftpClient, restoreTime);
        for (String backUpJsonFileName : backUpJsonFileNames) {
          status = execute(backUpJsonFileName);
        }
      }

      if(backUpJsonFileNames.size()==0){
        status = true;
      }
      
      Map<String, Object> responseMap = new HashMap<String, Object>();
      if(status){
        responseMap.put("status", "Success");
      }
      return responseMap;
    }
    catch (Exception e) {
      Map<String, Object> exceptionMap = prepareExceptionResponse(e);
      return exceptionMap;
    }
  }
  
  private Boolean execute (String backUpJsonFileName) throws Exception
  {
    try {
        String jsonPath = prop.getProperty("localSwiftDeltaJsonFileLocation") + backUpJsonFileName;
        ObjectMapper mapper = new ObjectMapper();
        BackupDetails backupDetails = mapper.readValue(new File(jsonPath), BackupDetails.class);
        
        // Get account list from backup data
        ArrayList<Account> accountsBackUp = backupDetails.getAccounts();
        // Get Account list From server
        ArrayList<String> accountNamesServer = getAccountNameList();
        
        for (Account accountBackUp : accountsBackUp) {
          logger.debug("Back Up Account Name : " + accountBackUp.getName() +"\n");
          
          // Check for Account to be backed up
          if (accountBackUp.getName().equals(prop.getProperty("swiftaccounttoberestored"))) {
            // Check if Account Exists or not; if not create
            if (!accountNamesServer.contains(accountBackUp.getName())) {
              createAccount(accountBackUp.getName(), accountBackUp.getUserName());
            }
            
            // Get auth_token and url for account
            getAuth_token_for_account(accountBackUp.getName(), accountBackUp.getUserName());
            // get Container data from json
            ArrayList<Container> containersBackUp = accountBackUp.getContainers();
            // Get All containers in account from server and clear all objects in containers
            ArrayList<String> containerListServer = getContainerList();
            
            for (Container containerBackUp : containersBackUp) {
              String containerNameBackUp = containerBackUp.getName();
              logger.debug("Backup Container Name :" + containerNameBackUp +"\n");
              // Check if Container Exists or not; if not create
              if (!containerListServer.contains(containerNameBackUp)) {
                createContainer(containerNameBackUp);
              }
              
              ArrayList<Objects> objectsBackUp = containerBackUp.getObjects();
              for (Objects objectBackUp : objectsBackUp) {
                logger.debug("Creating Object In : " + accountBackUp.getName() + "\n");
                int createResponse = createObject(containerNameBackUp, objectBackUp);
                logger.debug("Create Object Response: " + createResponse + "\n");
                if (createResponse != 201 /*|| createResponse = 204*/) {
                  throw new Exception("Object Creation Failed, Response: "+ createResponse +"\n");
                }
              }
            }
          }
        }
        return true;
    }
    catch (Exception e) {
      throw e;
    }
  }
  
  private void getBackUpJson(FTPClient ftpClient, Date time) throws Exception
  {
    FTPFile[] files = ftpClient.listFiles(prop.getProperty("remoteSwiftDeltaJsonFileLocation"));
    ArrayList<String> backupJsonsAfterGivenTime = new ArrayList<String>();

    // clear old json files
    String localSwiftDeltaJsonFileLocation = prop.getProperty("localSwiftDeltaJsonFileLocation");
    FileUtils.cleanDirectory(new File(localSwiftDeltaJsonFileLocation));
    
    if (time == null){
      for (FTPFile ftpFile : files) {
        String fileName = ftpFile.getName();
        System.out.println("NULL FILE NAME :" + fileName );
        if(fileName.equals("last-modified.txt") || fileName.equals("objects")){
          continue;
        }
        backUpJsonFileNames.add(ftpFile.getName());
      }
    } 
    else {
      System.out.println("IN ELSE");
      for (FTPFile ftpFile : files) 
      {
        String fileName = ftpFile.getName();
        if(fileName.equals("last-modified.txt") || fileName.equals("objects")){
          continue;
        }
       
        String fileTimeStamp = fileName.substring(0, fileName.lastIndexOf("."));
        DateFormat jTimeFormat = new SimpleDateFormat("yyyy.MM.dd-hh:mm:ss");
        Date jsonTimeStamp = jTimeFormat.parse(fileTimeStamp);
       
        if (jsonTimeStamp.before(time) || jsonTimeStamp.equals(time)){
          backUpJsonFileNames.add(ftpFile.getName());
        } else {
          backupJsonsAfterGivenTime.add(ftpFile.getName());
        }
        // CASE : elasticbackuptime : 10:00 and swiftbackupTime : 10:10 then we need to get this 10:10
        //Collections.sort(backupJsonsAfterGiveTime);
        if (backupJsonsAfterGivenTime.size() != 0){
          System.out.println("IN SPECIAL CASE");
             backUpJsonFileNames.add(backupJsonsAfterGivenTime.get(0));
        }
      }
    }
    
    int i = 1;
    for (String backUpJsonFileName : backUpJsonFileNames) {
      
      // download required jsons from ftp
      File downloadFile = new File(localSwiftDeltaJsonFileLocation + backUpJsonFileName);
      OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
      boolean success = ftpClient.retrieveFile(prop.getProperty("remoteSwiftDeltaJsonFileLocation")
          + backUpJsonFileName, outputStream);
      outputStream.close();
      
      if (success) {
        logger.debug("Json #" + backUpJsonFileName + " has been downloaded successfully.");
      } else {
        throw new Exception("Unable To Download File: "+ backUpJsonFileName);
      }
      i++;
    }
  }
  
  private ArrayList<String> getAccountNameList() throws Exception
  {
    ArrayList<String> accountNamesServer = new ArrayList<String>();
    
    String uri = prop.getProperty("swiftDestAccountAccessUrl");
    Map<String, Object> requestHeaders = new HashMap<String, Object>();
    requestHeaders.put("X-Auth-Admin-User", prop.getProperty("swiftSourceAuthAdminUser"));
    requestHeaders.put("X-Auth-Admin-Key", prop.getProperty("swiftSourceSwauthKey"));
    
    HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "GET", requestHeaders);
    
    int responseCode = connection.getResponseCode();
    if (responseCode != 200 && responseCode != 202 && responseCode != 203 && responseCode != 201) {
      logger.error("Failed : HTTP error code : " + responseCode);
      throw new Exception("Unable To Get Account List From Swift Server, Response Code: "+ responseCode);
    }
    
    BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
    
    StringBuilder jsonString = new StringBuilder();
    String str;
    while ((str = br.readLine()) != null) {
      jsonString.append(str);
    }
    
    Map<String, Object> map = new ObjectMapper().readValue(jsonString.toString(), Map.class);
    ArrayList<Map<String, String>> accounts = (ArrayList<Map<String, String>>) map.get("accounts");
    
    for (Map<String, String> account : accounts) {
      String accountName = account.get("name");
      logger.debug("Destination Swift Account Name : " + accountName +"\n");
      accountNamesServer.add(accountName);
    }
    
    return accountNamesServer;
  }
  
  private void createAccount(Object accountName, String userName) throws Exception
  {
    
    String uri = prop.getProperty("swiftDestCreateAccountUrl") + accountName.toString();
    Map<String, Object> requestHeaders = new HashMap<String, Object>();
    requestHeaders.put("X-Auth-Admin-User", prop.getProperty("swiftSourceAuthAdminUser"));
    requestHeaders.put("X-Auth-Admin-Key", prop.getProperty("swiftSourceSwauthKey"));
    
    HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "PUT", requestHeaders);
    
    int responseCode = connection.getResponseCode();
    if (responseCode != 200 && responseCode != 202 && responseCode != 203 && responseCode != 201) {
      logger.error("Failed : HTTP error code : " + responseCode);
      throw new Exception("Unable Create Account: '"+ accountName +"' , Response Code: "+ responseCode);
    }
    addUserToAccount(accountName, userName);
  }
  
  private void addUserToAccount(Object accountName, String userName) throws Exception
  {
    String uri = prop.getProperty("swiftDestCreateAccountUrl") + accountName.toString() + "/" + userName;
    Map<String, Object> requestHeaders = new HashMap<String, Object>();
    requestHeaders.put("X-Auth-Admin-User", prop.getProperty("swiftSourceAuthAdminUser"));
    requestHeaders.put("X-Auth-Admin-Key", prop.getProperty("swiftSourceSwauthKey"));
    requestHeaders.put("X-Auth-User-Admin", "true");
    requestHeaders.put("X-Auth-User-Key", prop.getProperty("swiftDestSwauthKey"));
    
    HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "PUT", requestHeaders);
    
    int responseCode = connection.getResponseCode();
    if (responseCode != 200 && responseCode != 202 && responseCode != 203 && responseCode != 201) {
      logger.error("Failed : HTTP error code : " + responseCode);
      throw new Exception("Unable Add User: '"+userName+"' To Account: '"+ accountName +"' , Response Code: "+ responseCode);
    }
    logger.debug("Account " + accountName + " Created\n");
  }
  
  private void getAuth_token_for_account(Object accountName, String userName) throws Exception
  {
    String uri = prop.getProperty("swiftDestGetSwiftAuthToken");
    Map<String, Object> requestHeaders = new HashMap<String, Object>();
    requestHeaders.put("X-Auth-User", accountName.toString() + ":" + userName);
    requestHeaders.put("X-Auth-Key", prop.getProperty("swiftDestSwauthKey"));
    
    HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "GET", requestHeaders);
    
    int responseCode = connection.getResponseCode();
    if (responseCode != 200 && responseCode != 202 && responseCode != 203 && responseCode != 201) {
      logger.error("Failed : HTTP error code : " + responseCode);
      throw new Exception("Unable To Get Auth Token for User: '"+userName+"', Account: '"+ accountName +"' , Response Code: "+ responseCode);
    }
    
    AUTH_TOKEN = connection.getHeaderField("X-Auth-Token");
    AUTH_URL = connection.getHeaderField("X-Storage-Url");
  }
  
  private ArrayList<String> getContainerList() throws Exception
  {
    ArrayList<String> containerNamesServer = new ArrayList<String>();
    logger.debug("getContainer AUTH_URL: " + AUTH_URL);
    logger.debug("getContainer AUTH_TOKEN: " + AUTH_TOKEN);
    
    String uri = AUTH_URL;
    Map<String, Object> requestHeaders = new HashMap<String, Object>();
    requestHeaders.put("X-Auth-Token", AUTH_TOKEN);
    requestHeaders.put("accept", "application/json");
    
    HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "GET", requestHeaders);
    
    int responseCode = connection.getResponseCode();
    if (responseCode != 200 && responseCode != 202 && responseCode != 203 && responseCode != 201
        && responseCode != 204 && responseCode != 406) {
      logger.error("Failed getContainerList: HTTP error code : " + responseCode);
      throw new Exception("Unable To Get Container List, Response Code: "+ responseCode);
    }
    
    BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
    StringBuilder jsonString = new StringBuilder();
    String str;
    while ((str = br.readLine()) != null) {
      jsonString.append(str);
    }
    
    ArrayList<Map<String, Object>> serverContainerList = new ObjectMapper().readValue(
        jsonString.toString(), ArrayList.class);
    for (Map<String, Object> serverContainer : serverContainerList) {
      logger.debug("Destination Container Name: " + serverContainer.get("name").toString() +"\n");
      containerNamesServer.add(serverContainer.get("name").toString());
    }
    return containerNamesServer;
  }
  
  private void createContainer(String containerName) throws Exception
  {
    String uri = AUTH_URL  + "/" + containerName;
    Map<String, Object> requestHeaders = new HashMap<String, Object>();
    requestHeaders.put("X-Auth-Token", AUTH_TOKEN);
    
    HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "PUT", requestHeaders);
    
    logger.debug("Container Created :" + containerName +"\n");
    
    int responseCode = connection.getResponseCode();
    if (responseCode != 200 && responseCode != 202 && responseCode != 203 && responseCode != 201) {
      logger.error("Failed : HTTP error code : " + connection.getResponseCode());
      throw new Exception("Unable To Create Container: '"+containerName+"', Response Code: "+ responseCode);
    }
  }
  
  private int createObject(String containerName, Objects object)
      throws IOException
  {
    String charset = "UTF-8";
    String response = null;
    // check if object is marked for delete or not
    // if not - restore object else delete object from ftp
    
    if (! object.getX_Object_Meta_Deleted().equals("True")) 
    {
      String uri = AUTH_URL + "/" + containerName + "/" + object.getName();
      Map<String, Object> requestHeaders = new HashMap<String, Object>();
      requestHeaders.put("X-Auth-Token", AUTH_TOKEN);
      requestHeaders.put("Content-Length", "1");
      requestHeaders.put("Content-Type", object.getContent_type());
      requestHeaders.put("Last-Modified", object.getLast_modified());
      requestHeaders.put("X-Object-Meta-Deleted", object.getX_Object_Meta_Deleted());
      requestHeaders.put("X-Object-Meta-Format", object.getX_Object_Meta_Format());
      requestHeaders.put("X-Object-Meta-Name", object.getX_Object_Meta_Name());
      requestHeaders.put("X-Object-Meta-Resolution", object.getX_Object_Meta_Resolution());
      requestHeaders.put("X-Object-Meta-Type", object.getX_Object_Meta_Type());
      
      if(! (object.getX_Delete_After() == null)) {
        requestHeaders.put("X-Delete-After", object.getX_Delete_After());
      }
      
      if(! (object.getX_Object_Meta_Original() == null)) {
        requestHeaders.put("X-Object-Meta-Original", object.getX_Object_Meta_Original());
      }
      
      if(! (object.getX_Object_Meta_Thumb() == null)) {
        requestHeaders.put("X-Object-Meta-Thumb", object.getX_Object_Meta_Thumb());
      }
      
      
      HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "PUT", requestHeaders, false, true, true );
      
      outputStream = connection.getOutputStream();
      
      writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
      writer.flush();
      
      InputStream inputStream = ftpClient.retrieveFileStream(prop.getProperty("remoteSwiftFilesLocation") + "/" + object.getName());
      OutputStream outputStream2 = new BufferedOutputStream(outputStream);
      byte[] bytesArray = new byte[1024];
      int bytesRead = -1;
      outputStream.flush();
      while ((bytesRead = inputStream.read(bytesArray)) != -1) {
        outputStream2.write(bytesArray, 0, bytesRead);
      }
      
      Boolean success = ftpClient.completePendingCommand();
      if(success) {
        logger.debug("File " + object.getName() + " has been downloaded successfully.\n");
      }
      outputStream2.flush();
      inputStream.close();
      writer.flush();
      writer.close();
      
      logger.debug(connection.getResponseCode());
      return connection.getResponseCode();
    }
    else {
      return 204;
     /* boolean deleted = true;
      // boolean deleted = ftpClient.deleteFile(prop.getProperty("remoteSwiftFilesLocation") + "/"
      // + object.getName());
      if (deleted) {
        logger.debug("The file was deleted successfully. : " + object.getName());
        //return true;
      }
      else {
        logger.debug("Could not delete the file.");
       // return false;
      }*/
    }
   
  }
  
  private Map prepareExceptionResponse(Exception e){
    Map<String, Object> exceptionMap = new HashMap<String, Object>();
    exceptionMap.put("status", "Fail");
    exceptionMap.put("error", e.getMessage());
    exceptionMap.put("stack", e.getStackTrace());
    return exceptionMap;
  }
  
}
