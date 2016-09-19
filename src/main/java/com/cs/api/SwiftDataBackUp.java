   package com.cs.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.cs.entity.Account;
import com.cs.entity.BackupDetails;
import com.cs.entity.Container;
import com.cs.entity.Objects;
import com.cs.entity.Swift;
import com.cs.utils.FTPUtil;
import com.cs.utils.GetConnectionError;
import com.cs.utils.StringToDateConverterUtils;
import com.cs.utils.TimeStampUtils;
import com.cs.utils.exception.SwiftBackupException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SwiftDataBackUp {
  
  private static final String X_STORAGE_TOKEN = "X-Storage-Token";
  private static final String X_STORAGE_URL = "X-Storage-Url";
  private static final String STORAGE_URL = "storageUrl";
  private static final String NAME = "name";
  private static final String USERS = "users";
  private static final String GET = "GET";
  private static final String SWIFT_SOURCE_SWAUTH_KEY = "swiftSourceSwauthKey";
  private static final String SWIFT_SOURCE_AUTH_ADMIN_USER = "swiftSourceAuthAdminUser";
  private static final String SWIFT_SOURCE_ACCOUNTS_ACCESS_URL = "swiftSourceAccountsAccessUrl";
  private static final String X_AUTH_ADMIN_USER = "X-Auth-Admin-User";
  private static final String X_AUTH_ADMIN_KEY = "X-Auth-Admin-Key";
  private static final String SWIFT_SOURCE_URL = "swiftSourceUrl";
  private static final String APPLICATION_JSON = "application/json";
  private static final String ACCEPT = "Accept";
  private static final String X_AUTH_USER = "X-Auth-User";
  private static final String X_AUTH_KEY = "X-Auth-Key";
  private static final String X_AUTH_TOKEN = "X-Auth-Token";
  private static final String STORAGE_TOKEN = "storageToken";
  final static Logger logger = Logger.getLogger(SwiftDataBackUp.class);
  
  private static long                                 count              = 0;
  private Properties                                          prop;
  private FTPClient                                   ftpClient                        = null;
  private String                                      ftpUser;
  private String                                      ftpPassword;
  private String                                      ftpServer;
  private String                                      ftpPort;
  private static Long                                 lastBackupTime;
  private String                                      swiftObjectUploadPath;
  private String                                      remoteSwiftBackupJsonFileLocation;
  private ArrayList<Objects>                          modifiedObjects;
  private int                                         modifiedObjectCount              = 0;
 //private String                                     lastModifiedFilePath;
  private String                                      backupAccountName;
  private boolean                                     exists;
  private ArrayList<String> objectsNotFound = new ArrayList<String>();
  private Map<String, Object> totalObjectsMap = new HashMap<String, Object>(); 
  
  public SwiftDataBackUp()
      throws Exception
  {
   
    String filename = "config.properties";
    InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
    prop = new Properties();
    prop.load(input);
    ftpUser = prop.getProperty("ftpUser");
    ftpPassword = prop.getProperty("ftpPassword");
    ftpServer = prop.getProperty("ftpServer");
    ftpPort = prop.getProperty("ftpPort");
    swiftObjectUploadPath = prop.getProperty("remoteSwiftFilesLocation");
    remoteSwiftBackupJsonFileLocation = prop.getProperty("remoteSwiftDeltaJsonFileLocation");
    backupAccountName = prop.getProperty("backupAccountName");
  }
  
  public Swift startBackupProcess() throws Exception
  {
    // upload backup details json post backup process
        // TODO it will create new json for each account: merge all jsons
    
    List<Map<String, Object>> modifiedObjectsList = new ArrayList<Map<String, Object>>();
    
    boolean fileWritten;
    try {
      count++;
      ftpClient = FTPUtil.openFtpConnection(ftpServer, ftpPort, ftpUser, ftpPassword);
      exists = checkFileExists(remoteSwiftBackupJsonFileLocation + "last-modified.txt");
      
      BackupDetails backupDetails = new BackupDetails();
      backupDetails.setLastBackupTimestamp(new Timestamp(new Date().getTime()).toString());
      
      String uri = prop.getProperty(SWIFT_SOURCE_ACCOUNTS_ACCESS_URL);
      Map<String, Object> requestHeaders = new HashMap<String, Object>();
      requestHeaders.put(X_AUTH_ADMIN_USER, prop.getProperty(SWIFT_SOURCE_AUTH_ADMIN_USER));
      requestHeaders.put(X_AUTH_ADMIN_KEY, prop.getProperty(SWIFT_SOURCE_SWAUTH_KEY));
      
      HttpURLConnection swiftServerHTTPConnection = new PrepareHttpUrlConnection().prepareConnection(uri, GET,
          requestHeaders);
      
      if (swiftServerHTTPConnection.getResponseCode() != 200) {
        int responseCode = swiftServerHTTPConnection.getResponseCode();
        logger.error("Failed : HTTP error code : " + responseCode);
        String response = GetConnectionError.execute(swiftServerHTTPConnection);
        throw new SwiftBackupException("Usecase: Get All Accounts, error: " + response, responseCode);
      }
      
      BufferedReader br = new BufferedReader(new InputStreamReader((swiftServerHTTPConnection.getInputStream())));
      String str, output = "";
      while ((str = br.readLine()) != null) {
        output += str;
      }
      swiftServerHTTPConnection.disconnect();
      br.close();
      
      // start actual transfer of objects to FTP server
      backupDetails.setAccounts(accessEachAccount(output));
      
      fileWritten = false;
      String timeStamp = TimeStampUtils.getFormatedTimeStamp();
      String snapshotName = "";
      for (Account account : backupDetails.getAccounts()) {
        if (!fileWritten && account.getName().equals(backupAccountName))
        {
          for (Container container : account.getContainers()) {
            Map<String, Object> containerMap = new HashMap<String, Object>();
            containerMap.put(container.getName(), container.getObjects());
            modifiedObjectsList.add(containerMap);
            
            if (container.getObjects().size() != 0) {
              snapshotName = TimeStampUtils.getFormatedTimeStamp();
              remoteSwiftBackupJsonFileLocation += timeStamp + ".json";
              String value = new ObjectMapper().writeValueAsString(backupDetails);
              FTPUtil.uploadFileFromStringInput(ftpClient, remoteSwiftBackupJsonFileLocation, value);
              fileWritten = true;
            }
            if (fileWritten)
              break;
          }
        }
      }
      // close FTP connection
      FTPUtil.disconnectFromFTPServer(ftpClient);
      
      Swift swiftBackupResponseMap = prepareResponseMap(timeStamp, snapshotName, modifiedObjectsList);
      System.out.println(swiftBackupResponseMap);
      
      return swiftBackupResponseMap;
    }
    catch (SwiftBackupException e) {
      prepareExceptionResponse(e);
      /*swiftBackupResponseMap.put("timestamp", null);
      swiftBackupResponseMap.put("response", e.getErrorMessage());
      swiftBackupResponseMap.put("responseCode", e.getResponseCode());*/
      return null;
    }
    catch (Exception e) {
      logger.error(e);
      throw e;
    }
    
  }
  
  private void getLastBackupTime(){
    // TODO: create backuprecord file if not present and update value with latest backuptime
  }
  
  private ArrayList<Account> accessEachAccount(String accountsInfo) throws Exception
  {
    ArrayList<Account> accountList = new ArrayList<Account>();
    try {
      Map accountsMap = new ObjectMapper().readValue(accountsInfo, HashMap.class);
      ArrayList<Account> accounts = (ArrayList<Account>) accountsMap.get("accounts");
      Iterator<Account> itr = accounts.iterator();
      
      while (itr.hasNext()) {
        String acnt = new ObjectMapper().writeValueAsString(itr.next());
        //new ObjectMapper().readValue(acnt, Account.class);
        
        String accountName = new ObjectMapper().readValue(acnt, Account.class).getName();
        logger.debug("Swift Account: " + accountName + "\n");
        String userName = getPrimaryUserOfAccount(accountName);
        
        if (accountName.equals(backupAccountName)){
          HashMap<String, String> accountAccessInfo = (HashMap<String, String>) getInfoToAccessAccount(
              accountName, userName, "pass@123");
          Account account = new Account();
          account.setName(accountName);
          account.setUserName(userName);
          account.setContainers(accessAllContainers(accountAccessInfo));
          accountList.add(account);
        }
      }
    }
    catch (Exception e) {
      throw e;
    }
    return accountList;
  }
  
  private String getPrimaryUserOfAccount(String accountName) throws Exception
  {
    String userName = "";
    try {
      
      String uri = prop.getProperty(SWIFT_SOURCE_ACCOUNTS_ACCESS_URL) + accountName;
      Map<String, Object> requestHeaders = new HashMap<String, Object>();
      requestHeaders.put(X_AUTH_ADMIN_USER, prop.getProperty(SWIFT_SOURCE_AUTH_ADMIN_USER));
      requestHeaders.put(X_AUTH_ADMIN_KEY, prop.getProperty(SWIFT_SOURCE_SWAUTH_KEY));
      
      HttpURLConnection swiftServerHTTPConnection = new PrepareHttpUrlConnection().prepareConnection(uri, GET,
          requestHeaders);
      
      if (swiftServerHTTPConnection.getResponseCode() != 200) {
        int responseCode = swiftServerHTTPConnection.getResponseCode();
        logger.error("Failed : HTTP error code : " + responseCode);
        String response = GetConnectionError.execute(swiftServerHTTPConnection);
        throw new SwiftBackupException("Usecase: Get Primary User Of Account, error: " + response, responseCode);
      }
      BufferedReader br = new BufferedReader(new InputStreamReader((swiftServerHTTPConnection.getInputStream())));
      String str, output = "";
      
      while ((str = br.readLine()) != null) {
        output += str;
      }
      
      br.close();
      HashMap accountDetails = new ObjectMapper().readValue(output, HashMap.class);
      ArrayList usersOfAccount = (ArrayList) accountDetails.get(USERS);
      HashMap user = (HashMap) usersOfAccount.get(0);
      userName = (String) user.get(NAME);
      logger.debug("User Name: " + userName +"\n");
    }
    catch (Exception e) {
      throw e;
    }
    return userName;
  }
  
  private Map getInfoToAccessAccount(String accountName, String accountUser, String authKey) throws Exception
  {
    HashMap<String, String> accountAccessInfo = new HashMap<String, String>();
    try {
      
      String uri = prop.getProperty(SWIFT_SOURCE_URL);
      Map<String, Object> requestHeaders = new HashMap<String, Object>();
      requestHeaders.put(ACCEPT, APPLICATION_JSON);
      requestHeaders.put(X_AUTH_KEY, authKey);
      requestHeaders.put(X_AUTH_USER, accountUser + ":" + accountName);
      
      HttpURLConnection conn = new PrepareHttpUrlConnection().prepareConnection(uri, GET,
          requestHeaders);
      
      if (conn.getResponseCode() != 200) {
        int responseCode = conn.getResponseCode();
        logger.error("Failed : HTTP error code : " + responseCode);
        String response = GetConnectionError.execute(conn);
        throw new SwiftBackupException("Usecase: Get Auth URl and Token for Account, error: " + response, responseCode);
      }
      accountAccessInfo.put(STORAGE_URL, conn.getHeaderField(X_STORAGE_URL));
      accountAccessInfo.put(STORAGE_TOKEN, conn.getHeaderField(X_STORAGE_TOKEN));
    }
    catch (Exception e) {
      throw e;
    }
    return accountAccessInfo;
  }
  
  private ArrayList accessAllContainers(Map accountAccessInfo) throws Exception
  {
    ArrayList containerList = new ArrayList();
    try {
      String uri = accountAccessInfo.get(STORAGE_URL).toString();
      Map<String, Object> requestHeaders = new HashMap<String, Object>();
      requestHeaders.put(ACCEPT, APPLICATION_JSON);
      requestHeaders.put(X_STORAGE_TOKEN, accountAccessInfo.get(STORAGE_TOKEN).toString());
      
      HttpURLConnection conn = new PrepareHttpUrlConnection().prepareConnection(uri, GET,
          requestHeaders);
      
      if (conn.getResponseCode() != 200) {
        int responseCode = conn.getResponseCode();
        logger.error("Failed : HTTP error code : " + responseCode);
        String response = GetConnectionError.execute(conn);
        throw new SwiftBackupException("Usecase: Get All Container of Account, error: " + response, responseCode);
      }
      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
      String str, output = "";
      while ((str = br.readLine()) != null) {
        output += str;
      }
      br.close();
      containerList = accessEachContainer(output, accountAccessInfo);
    }
    catch (Exception e) {
      throw e;
    }
    return containerList;
  }
  
  private ArrayList accessEachContainer(String containersInfo, Map<String, String> accountAccessInfo) throws Exception
  {
    ArrayList<Container> containerList = new ArrayList<Container>();
    try {
      ObjectMapper mapper = new ObjectMapper();
      Map containerObjects[] = mapper.readValue(containersInfo, Map[].class);
      
      for (int i = 0; i < containerObjects.length; i++) {
        Iterator<Map.Entry> entries = containerObjects[i].entrySet().iterator();
        while (entries.hasNext()) {
          Map.Entry entry = entries.next();
          if (entry.getKey() == NAME) {
            Container container = new Container();
            String containerName = entry.getValue().toString();
            container.setName(containerName);
            container.setObjects(accessMetaDataOfContainer(containerName, accountAccessInfo));
            containerList.add(container);
          }
        }
      }
    }
    catch (Exception e) {
      throw e;
    }
    return containerList;
  }
  
  public ArrayList accessMetaDataOfContainer(String containerName,
      Map<String, String> accountAccessInfo) throws Exception
  {
    ArrayList<Objects> listOfObjects = new ArrayList<Objects>();
    try {
      String uri = accountAccessInfo.get(STORAGE_URL).toString() + "/" + containerName;
      Map<String, Object> requestHeaders = new HashMap<String, Object>();
      requestHeaders.put(ACCEPT, APPLICATION_JSON);
      requestHeaders.put(X_STORAGE_TOKEN, accountAccessInfo.get(STORAGE_TOKEN).toString());
      
      HttpURLConnection conn = new PrepareHttpUrlConnection().prepareConnection(uri, GET,
          requestHeaders);

      if (conn.getResponseCode() != 200) {
        int responseCode = conn.getResponseCode();
        logger.error("Failed : HTTP error code : " + responseCode);
        String response = GetConnectionError.execute(conn);
        throw new SwiftBackupException("Usecase: Get MetaData Of Container, error: " + response, responseCode);
      }
      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
      String str, output = "";
      while ((str = br.readLine()) != null) {
        output += str;
      }
      br.close();
      listOfObjects = accessAllObjectsOfEachContainer(output, containerName, accountAccessInfo);
      conn.disconnect();
    }
    catch (IOException e) {
      throw e;
    }
    return listOfObjects;
  }
  
  public ArrayList accessAllObjectsOfEachContainer(String responseString, String containerName,
      Map<String, String> accountAccessInfo) throws Exception
  {
    modifiedObjects = new ArrayList<Objects>();
    try {
      Objects objects[] = new ObjectMapper().readValue(responseString, Objects[].class);
      totalObjectsMap.put(containerName, objects.length);
      
      ArrayList<Date> lastModifiedList = new ArrayList<Date>();
      String lastModifiedFilePath = remoteSwiftBackupJsonFileLocation + "last-modified.txt";
      
      if (!exists) {
        for (Objects object : objects) {
          String lastModifiedString = object.getLast_modified();
          lastModifiedString = lastModifiedString.substring(0, lastModifiedString.lastIndexOf("."));
          lastModifiedString = lastModifiedString.replace('-', '.');
          lastModifiedString = lastModifiedString.replace('T', '-');
          Date lastModifiedTime = StringToDateConverterUtils.convert(lastModifiedString, null);
          lastModifiedList.add(lastModifiedTime);
         
          Boolean exists = FTPUtil.checkIfFileExists(ftpClient, swiftObjectUploadPath , object.getName());
          if(!exists){
            logger.debug("File "+ object.getName() +" Not Exists.");
            downloadObject(object, containerName, accountAccessInfo);
            //modifiedObjects.add(object);
          } else {
            logger.debug("File "+ object.getName() +" Already Exists.");
          }
        }
      }
      else {
        readLastBackupTime(lastModifiedFilePath);
        
        for (Objects object : objects) {
          String lastModifiedString = object.getLast_modified();
          lastModifiedString = lastModifiedString.substring(0, lastModifiedString.lastIndexOf("."));
          lastModifiedString = lastModifiedString.replace('-', '.');
          lastModifiedString = lastModifiedString.replace('T', '-');
          Date lastModifiedTime = StringToDateConverterUtils.convert(lastModifiedString, null);
          if (lastModifiedTime.getTime() > lastBackupTime) {
            lastModifiedList.add(lastModifiedTime);
            Boolean objectExists = FTPUtil.checkIfFileExists(ftpClient, swiftObjectUploadPath , object.getName());
            if(!objectExists){
              logger.debug("File "+ object.getName() +" Not Exists.");
              downloadObject(object, containerName, accountAccessInfo);
              //modifiedObjects.add(object);
            } else {
              logger.debug("File "+ object.getName() +" Already Exists.");
            }
          }
        }
      }
      Collections.sort(lastModifiedList, Collections.reverseOrder());
      if(lastModifiedList.size()!=0){
        updateLastBackupTime(lastModifiedList.get(0),lastModifiedFilePath);
      }
    }
    catch (IOException e) {
      throw e;
    }
    logger.debug("No Of Objects Modified: " + modifiedObjects.size() +"\n");
    logger.debug("Objects Modified: " + modifiedObjects+"\n");
    
    modifiedObjectCount += modifiedObjects.size();
    
    return modifiedObjects;
  }
  
  private void downloadObject(Objects object, String containerName, Map<String, String> accountAccessInfo) throws Exception
  {
    String urlToDownloadObject = accountAccessInfo.get(STORAGE_URL).toString() + "/" + containerName + "/";
    String objectName = object.getName();
    
    String uri = urlToDownloadObject + objectName;
    
    String auth_token = accountAccessInfo.get(STORAGE_TOKEN).toString();
    Map<String, Object> requestHeaders = new HashMap<String, Object>();
    requestHeaders.put(X_AUTH_TOKEN, auth_token);
    
    HttpURLConnection swiftServerHTTPConnection = new PrepareHttpUrlConnection().prepareConnection(uri,GET,requestHeaders);
    
    if (swiftServerHTTPConnection.getResponseCode() != 200) {
      int responseCode = swiftServerHTTPConnection.getResponseCode();
      logger.error("Failed : HTTP error code : " + responseCode + "filename:" + objectName);
      String response = GetConnectionError.execute(swiftServerHTTPConnection);
      objectsNotFound.add(objectName);
      //throw new SwiftBackupException("Usecase: Download Object, error: " + response, responseCode);
    }
    else {
        object.setContent_type(swiftServerHTTPConnection.getHeaderField("Content-Type"));
        object.setLast_modified(swiftServerHTTPConnection.getHeaderField("Last-Modified"));
        object.setX_Object_Meta_Format(swiftServerHTTPConnection.getHeaderField("X-Object-Meta-Format"));
        object.setX_Object_Meta_Name(swiftServerHTTPConnection.getHeaderField("X-Object-Meta-Name"));
        object.setX_Object_Meta_Resolution(swiftServerHTTPConnection.getHeaderField("X-Object-Meta-Resolution"));
        object.setX_Object_Meta_Type(swiftServerHTTPConnection.getHeaderField("X-Object-Meta-Type"));
        object.setX_Object_Meta_Deleted(swiftServerHTTPConnection.getHeaderField("X-Object-Meta-Deleted"));
        
        Map<String, List<String>> headerMap = swiftServerHTTPConnection.getHeaderFields();
        
        if (headerMap.containsKey("X-Object-Meta-Original")) {
          object.setX_Object_Meta_Original(swiftServerHTTPConnection.getHeaderField("X-Object-Meta-Original"));
        }
        if (headerMap.containsKey("X-Object-Meta-Thumb")) {
          object.setX_Object_Meta_Thumb(swiftServerHTTPConnection.getHeaderField("X-Object-Meta-Thumb"));
        }
        if (headerMap.containsKey("X-Delete-After")) {
          object.setX_Delete_After(swiftServerHTTPConnection.getHeaderField("X-Delete-After"));
        }
        
        InputStream inputStream = swiftServerHTTPConnection.getInputStream();
        logger.debug("Uploading file: "+ objectName +"\n");
        
        boolean status = FTPUtil.uploadFileFromInputStream(ftpClient, swiftObjectUploadPath + objectName, inputStream);
        inputStream.close();
        
        if (status) {
          logger.debug("File is uploaded successfully.");
        }
        else {
          logger.debug("Error While uploading file on ftpserver");
        }
        swiftServerHTTPConnection.disconnect();
        modifiedObjects.add(object);
      }
        
  }
  
  public void updateLastBackupTime(Date lastModifiedTime, String lastModifiedFilePath) throws Exception
  {
    if(exists) {
      Boolean status = ftpClient.deleteFile(lastModifiedFilePath);
      if(status){
        logger.debug("LastModified file Deleted Successfully \n");
      } else {
        logger.debug("LastModified file Deletion Failed");
      }
    }
    
    OutputStream outputStream = ftpClient.storeFileStream(lastModifiedFilePath);
    Long time = lastModifiedTime.getTime();
    outputStream.write(time.toString().getBytes());
    outputStream.flush();
    outputStream.close();
    boolean status = ftpClient.completePendingCommand();
    if(!status) {
      logger.debug("Update Backup file CompletePendingCommand failed");
    }
  }
 
  private boolean checkFileExists(String filePath) throws Exception{
    InputStream inputStream = ftpClient.retrieveFileStream(filePath);
    int returnCode = ftpClient.getReplyCode();
    if (inputStream == null || returnCode == 550) {
        return false;
    }
    inputStream.close();
    boolean status = ftpClient.completePendingCommand();
    if (!status) {
      logger.debug("Error while reading last backup time file.");
    }
    return true;
  }
  
  private void readLastBackupTime(String lastModifiedFilePath) throws Exception {
    
    /*File backupHistoryFile = new File(bakupHistoryFilePath);
    String fileContent = ReadFileUtil.execute(backupHistoryFile);
    ArrayList<Map> backupHistoryList = new ObjectMapper().readValue(fileContent, ArrayList.class);
    Map<String, String> lastBackupInformation = backupHistoryList.get(0);
    String out = lastBackupInformation.get("swift");*/
    
    
    InputStream inputStream = ftpClient.retrieveFileStream(lastModifiedFilePath);
    int returnCode = ftpClient.getReplyCode();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder out = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        out.append(line);
    }
    
    
    lastBackupTime = Long.parseLong(out.toString());
    //lastBackupTime = StringToDateConverter.convert(out.toString(), "EEEE MMM dd HH:mm:ss IST yyyy");
    inputStream.close();
    reader.close();
    boolean status = ftpClient.completePendingCommand();
    if (status) {
      logger.debug("Last Backup Time:"+ lastBackupTime +"\n");
    } else {
      logger.debug("Error while reading last backup time file.");
    }
  }
  
  private Swift prepareResponseMap(String timestamp, String snapshotName, List<Map<String, Object>> modifiedObjectsList) throws Exception{
    String backupLocationRemote = prop.getProperty("swiftBackupLocationRemote");
    Swift swift = new Swift();
    List<String> objectNames = new ArrayList<String>();
    
   /* int objectCount = 0;
    for (Objects object : modifiedObjects) {
      String name = object.getName();
      objectNames.add(name);
      objectCount++;
    }*/
    
    swift.setTimestamp(timestamp);
    swift.setTotalObjects(totalObjectsMap);
    swift.setNumberOfModifiedObjects(modifiedObjectCount);
    swift.setModifiedObjects(modifiedObjectsList);
    swift.setObjectsNotFound(objectsNotFound);
    
    if (objectsNotFound.size() != 0 ) {
      swift.setSnapshotName(snapshotName);
      swift.setStatus("Warning");
      swift.setResponse("Swift Backup Successful, But Some Objects Not Found");
      swift.setResponseCode(201);
      swift.setLocation(backupLocationRemote+timestamp+".json");
      
      return swift;
    }
    
    if (modifiedObjectCount == 0 ) {
      swift.setStatus("Success");
      swift.setResponse("No Objects To Backup");
    }
    
    if(modifiedObjectCount != 0){
      swift.setSnapshotName(snapshotName);
      swift.setStatus("Success");
      swift.setResponse("Swift Backup Successful");
      swift.setResponseCode(201);
      swift.setLocation(backupLocationRemote+timestamp+".json");
    } 
    
    return swift;
  }

  private Swift prepareExceptionResponse(SwiftBackupException e){
    
    String timeStamp = TimeStampUtils.getFormatedTimeStamp();
    
    Swift swift = new Swift();
    swift.setTimestamp(timeStamp);
    swift.setStatus("Fail");
    swift.setResponse(e.getMessage());
    swift.setResponseCode(e.getResponseCode());
    swift.setTotalObjects(totalObjectsMap);
    
    return swift;
  }
}
