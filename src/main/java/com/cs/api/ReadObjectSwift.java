package com.cs.api;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



import org.apache.log4j.Logger;

import com.cs.entity.Objects;
import com.cs.utils.DeleteSwiftObject;
import com.cs.utils.GetConnectionError;
import com.cs.utils.exception.SwiftBackupException;
import com.fasterxml.jackson.databind.ObjectMapper;



public class ReadObjectSwift{
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
	private static final String STORAGE_TOKEN = "storageToken";
	final static Logger logger = Logger.getLogger(SwiftDataBackUp.class);

	private Properties  prop;
	private String      remoteSwiftBackupJsonFileLocation;
	private String      backupAccountName;
	private Map<String, Object> totalObjectsMap = new HashMap<String, Object>(); 

	public ReadObjectSwift()throws Exception
	{

		String filename = "config.properties";
		InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
		prop = new Properties();
		prop.load(input);

		remoteSwiftBackupJsonFileLocation = prop.getProperty("remoteSwiftDeltaJsonFileLocation");
		backupAccountName = prop.getProperty("backupAccountName");
	}
	
	
	
	
	
	public ArrayList<String> getSwiftObjectList(String containerName) throws Exception
	  {
	    ArrayList<String> objectList = new ArrayList<String>();
	    try {

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

	      objectList=accessAccount(output,containerName);
	      
	      return objectList;
	    }
	    catch (Exception e) {
	      logger.error(e);
	      throw e;
	    }
	    
	  }
	

	
	private ArrayList<String> accessAccount(String accountsInfo,String containerName) throws Exception
	{
		ArrayList<String> objectList = new ArrayList<String>();

		String userName = getPrimaryUserOfAccount(backupAccountName);
		HashMap<String, String> accountAccessInfo = (HashMap<String, String>) getInfoToAccessAccount(backupAccountName, userName, "pass@123");
		objectList=accessMetaDataOfContainer(containerName,accountAccessInfo);

		return objectList;
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

	public ArrayList accessMetaDataOfContainer(String containerName,
			Map<String, String> accountAccessInfo) throws Exception
	{
		ArrayList<String> listOfObjects = new ArrayList<String>();
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

	public ArrayList accessAllObjectsOfEachContainer(String responseString, String containerName,Map<String, String> accountAccessInfo) throws Exception
	{
		ArrayList<String> objectList = new ArrayList<String>();
		try {
			Objects objects[] = new ObjectMapper().readValue(responseString, Objects[].class);
			totalObjectsMap.put(containerName, objects.length);

			for (Objects object : objects) {
				logger.debug("File "+ object.getName());
				objectList.add(object.getName());
			}
		}
		catch (IOException e) {
			throw e;
		}
		return objectList;
	}
	
	
	public boolean deleteSwiftObject(String containerName,String objectName){
		
		
		String userName;
		try {
			userName = getPrimaryUserOfAccount(backupAccountName);
		
		HashMap<String, String> accountAccessInfo = (HashMap<String, String>) getInfoToAccessAccount(backupAccountName, userName, "pass@123");
		
		String urlToDeleteObject = accountAccessInfo.get(STORAGE_URL).toString();

	    String auth_token = accountAccessInfo.get(STORAGE_TOKEN).toString();
	    DeleteSwiftObject.delete(urlToDeleteObject, auth_token, containerName, objectName);
	   
		} catch (Exception e) {
			return false;
		}
		 return true;
	}
}