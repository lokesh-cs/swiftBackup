package com.cs.api;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import com.cs.entity.Container;
import com.cs.entity.Objects;


public class SwiftClone {
	private static Properties prop;
	//private static final String SWIFT_SOURCE_ACCOUNTS_ACCESS_URL = "swiftSourceIp";
	//private static final String SWIFT_DESTINATION_ACCOUNTS_ACCESS_URL = "swiftDestinationIp";
	
	private static final String SWIFT_SOURCE_URL = "swiftSourceUrl";
	private static final String SWIFT_SOURCE_ACCOUNTS_ACCESS_URL = "swiftSourceAccountsAccessUrl";
	private static final String SWIFT_DESTINATION_URL = "swiftDestinationUrl";
  //private static final String SWIFT_DESTINATION_ACCOUNT_ACCESS_URL = "swiftDestinationAccountsAccessUrl";
	
	
	private static final String X_STORAGE_TOKEN = "X-Storage-Token";
	private static final String X_STORAGE_URL = "X-Storage-Url";
	private static final String X_AUTH_USER = "X-Auth-User";
	private static final String X_AUTH_KEY = "X-Auth-Key";
	private static final String X_AUTH_TOKEN = "X-Auth-Token";
	private static final String STORAGE_TOKEN = "storageToken";
	private static final String STORAGE_URL = "storageUrl";
	private static String sourceUsername;
	private static String destinationUsername;
	private static final String APPLICATION_JSON = "application/json";
	private static final String ACCEPT = "Accept";
	private static final String GET = "GET";
	private static final String NAME = "name";
	private static String sourceAccount;
	private static String destinationAccount;
	private static String DEST_AUTH_TOKEN;
	private static String DEST_AUTH_URL;
	private static String storageToken;
	private static String storageURL;

	public SwiftClone() throws Exception
	{

		String filename = "swift.properties";
		InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
		prop = new Properties();
		prop.load(input);
		

	}

	public static String startSwiftClone () throws Exception
	{
		String []srcAccounts=prop.getProperty("swiftSourceAccount").split(",");
		String []destAccounts=prop.getProperty("swiftDestinationAccount").split(",");
		if(hasDuplicate(srcAccounts,destAccounts)){
			throw new Exception("Cloning to Same account");
		}
		String []srcUsername=prop.getProperty("swiftSourceUser").split(",");
		String []destUsername=prop.getProperty("swiftDestinationUser").split(",");
		sourceAccount=srcAccounts[0];
		sourceUsername=srcUsername[0];
		destinationAccount=destAccounts[0];
		destinationUsername=destUsername[0];
		
		String uri = prop.getProperty(SWIFT_SOURCE_ACCOUNTS_ACCESS_URL);
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		requestHeaders.put("X-Auth-Admin-User", ".super_admin");
		requestHeaders.put("X_AUTH_ADMIN_KEY", "pass@123");

		HttpURLConnection swiftServerHTTPConnection = new PrepareHttpUrlConnection().prepareConnection(uri, "GET", requestHeaders);

		if (swiftServerHTTPConnection.getResponseCode() != 200) {
			System.out.println("Failed : HTTP error code : " + swiftServerHTTPConnection.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader((swiftServerHTTPConnection.getInputStream())));
		String str, output = "";
		while ((str = br.readLine()) != null) {
			output += str;
		}
		swiftServerHTTPConnection.disconnect();
		br.close();

		//System.out.println(output);
		Map accountsMap = new ObjectMapper().readValue(output, HashMap.class);

		ArrayList<Map<String, String>> accounts = (ArrayList<Map<String, String>>) accountsMap.get("accounts");
		for (Map<String, String> account : accounts) {
			String accountName = account.get("name");
			//getPrimaryUserOfAccount(accountName);
			if(accountName.equals(sourceAccount)) {
				HashMap<String, String> accountAccessInfo = (HashMap<String, String>) getInfoToAccessAccount(accountName, sourceUsername, "pass@123");
				accessAllContainers(accountAccessInfo);
			}
		}
		return "";
	}
	public static boolean hasDuplicate(String src[],String des[]) {
		List<String> srcList = Arrays.asList(src);
		List<String> destList = Arrays.asList(des);
		//String sourceURI = prop.getProperty(SWIFT_SOURCE_ACCOUNTS_ACCESS_URL);
		//String targetURI = prop.getProperty(SWIFT_DESTINATION_ACCOUNTS_ACCESS_URL);
		//if(sourceURI.equals(targetURI)){
			for (String finalval : srcList) {
				if (destList.contains(finalval)) {
					return true;
				} 
			}
		//}
		return false;
	}
	/*private static String getPrimaryUserOfAccount(String accountName){

    return "";
  }*/

	private static Map getInfoToAccessAccount(String accountName, String accountUser, String authKey)
	{
		HashMap<String, String> accountAccessInfo = new HashMap<String, String>();
		try {
			String uri = prop.getProperty(SWIFT_SOURCE_URL);
			Map<String, Object> requestHeaders = new HashMap<String, Object>();
			requestHeaders.put("Accept", "application/json");
			requestHeaders.put(X_AUTH_KEY, authKey);
			requestHeaders.put(X_AUTH_USER, accountUser + ":" + accountName);

			HttpURLConnection conn = new PrepareHttpUrlConnection().prepareConnection(uri, GET,
					requestHeaders);

			if (conn.getResponseCode() != 200) {
				throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
			}
			accountAccessInfo.put(STORAGE_URL, conn.getHeaderField(X_STORAGE_URL));
			accountAccessInfo.put(STORAGE_TOKEN, conn.getHeaderField(X_STORAGE_TOKEN));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return accountAccessInfo;
	}

	private static ArrayList accessAllContainers(Map accountAccessInfo)
	{
		ArrayList containerList = new ArrayList();
		try {
			System.out.println("URL:" + accountAccessInfo.get(STORAGE_URL).toString());
			System.out.println("TOKEN:" + accountAccessInfo.get(STORAGE_TOKEN).toString());

			String uri = accountAccessInfo.get(STORAGE_URL).toString();
			Map<String, Object> requestHeaders = new HashMap<String, Object>();
			requestHeaders.put(ACCEPT, APPLICATION_JSON);
			requestHeaders.put(X_STORAGE_TOKEN, accountAccessInfo.get(STORAGE_TOKEN).toString());

			HttpURLConnection conn = new PrepareHttpUrlConnection().prepareConnection(uri, GET,
					requestHeaders);

			if (conn.getResponseCode() != 200) {
				throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
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
			e.printStackTrace();
		}
		return containerList;
	}

	private static ArrayList accessEachContainer(String containersInfo, Map<String, String> accountAccessInfo)
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
						// get all objects of this container
						getObjectListOfContainer(containerName, accountAccessInfo);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return containerList;
	}

	private static void getObjectListOfContainer(String containerName, Map<String, String> accountAccessInfo) throws Exception{
		try {
			String uri = accountAccessInfo.get(STORAGE_URL).toString() + "/" + containerName;
			System.out.println(uri);
			Map<String, Object> requestHeaders = new HashMap<String, Object>();
			requestHeaders.put(ACCEPT, APPLICATION_JSON);
			requestHeaders.put(X_STORAGE_TOKEN, accountAccessInfo.get(STORAGE_TOKEN).toString());

			HttpURLConnection conn = new PrepareHttpUrlConnection().prepareConnection(uri, GET,
					requestHeaders);

			if (conn.getResponseCode() != 200) {
				throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String str, output = "";
			while ((str = br.readLine()) != null) {
				output += str;
			}
			br.close();

			accessEachObject(output, containerName, accountAccessInfo);
			conn.disconnect();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void accessEachObject(String responseString, String containerName,
			Map<String, String> accountAccessInfo) throws Exception
	{
	  /*Map<String, Object> objects = new ObjectMapper().readValue(responseString, Map.class);
	  
		for (Map.Entry<String, Object> object : objects.entrySet()) {
			if (object.getKey().equals("name")) {
				String objectName = object.getValue().toString();
				getObjectFromSourceSwift(objectName, containerName, accountAccessInfo);
			}
		}*/
	  
	  Objects objects[] = new ObjectMapper().readValue(responseString, Objects[].class);
	  for (Objects object : objects) {
	    getObjectFromSourceSwift( object.getName(), containerName, accountAccessInfo);
	  }
	}

	private static void getObjectFromSourceSwift(String objectName, String containerName,
			Map<String, String> accountAccessInfo) throws Exception
	{
		String uri = accountAccessInfo.get(STORAGE_URL).toString() + "/"
				+ containerName + "/" + objectName;

		String auth_token = accountAccessInfo.get(STORAGE_TOKEN).toString();
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		requestHeaders.put(X_AUTH_TOKEN, auth_token);

		HttpURLConnection swiftServerHTTPConnection = new PrepareHttpUrlConnection().prepareConnection(uri,GET,requestHeaders);

		if (swiftServerHTTPConnection.getResponseCode() != 200) {
			System.out.println("Failed : HTTP error code : "
					+ swiftServerHTTPConnection.getResponseCode() + "filename:" + objectName);
		}

		InputStream inputStream = swiftServerHTTPConnection.getInputStream();
		// upload this inputstream to target swift

		uploadObjectToTarget(inputStream, swiftServerHTTPConnection, containerName, objectName);
		inputStream.close();
	}


	private static void uploadObjectToTarget(InputStream sourceInputStream,
			HttpURLConnection sourceSwiftHTTPConnection, String containerName, String objectName) throws Exception
	{    
		String charset = "UTF-8";
		PrintWriter writer;
		getAuthTokenForDestinationSwiftAccount(destinationAccount, destinationUsername);

		String uri = DEST_AUTH_URL + "/" + containerName + "/" + objectName;

		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		requestHeaders.put("X-Auth-Token", DEST_AUTH_TOKEN);
		requestHeaders.put("Content-Length", "1");
		requestHeaders.put("Content-Type", sourceSwiftHTTPConnection.getHeaderField("Content-Type"));
		requestHeaders.put("last-modified", sourceSwiftHTTPConnection.getHeaderField("Last-Modified"));
		requestHeaders.put("X-Object-Meta-Deleted", sourceSwiftHTTPConnection.getHeaderField("X-Object-Meta-Deleted"));
		requestHeaders.put("X-Object-Meta-Format", sourceSwiftHTTPConnection.getHeaderField("X-Object-Meta-Format"));
		requestHeaders.put("X-Object-Meta-Name", sourceSwiftHTTPConnection.getHeaderField("X-Object-Meta-Name"));
		requestHeaders.put("X-Object-Meta-Resolution", sourceSwiftHTTPConnection.getHeaderField("X-Object-Meta-Resolution"));
		requestHeaders.put("X-Object-Meta-Type", sourceSwiftHTTPConnection.getHeaderField("X-Object-Meta-Type"));

		Map<String, List<String>> headerMap = sourceSwiftHTTPConnection.getHeaderFields();

		if (headerMap.containsKey("X-Delete-After")) {
			requestHeaders.put("X-Delete-After", sourceSwiftHTTPConnection.getHeaderField("X-Delete-After"));
		}

		if (headerMap.containsKey("X-Object-Meta-Original")) {
			requestHeaders.put("X-Object-Meta-Original", sourceSwiftHTTPConnection.getHeaderField("X-Object-Meta-Original"));
		}

		if (headerMap.containsKey("X-Object-Meta-Thumb")) {
			requestHeaders.put("X-Object-Meta-Thumb", sourceSwiftHTTPConnection.getHeaderField("X-Object-Meta-Thumb"));
		}


		HttpURLConnection destSwiftHTTPConnection = new PrepareHttpUrlConnection().prepareConnection(uri, "PUT", requestHeaders, false, true, true );

		OutputStream outputStream = destSwiftHTTPConnection.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
		writer.flush();

		OutputStream outputStream2 = new BufferedOutputStream(outputStream);
		byte[] bytesArray = new byte[1024];
		int bytesRead = -1;
		outputStream.flush();

		while ((bytesRead = sourceInputStream.read(bytesArray)) != -1) {
			outputStream2.write(bytesArray, 0, bytesRead);
		}
		outputStream2.flush();
		writer.flush();
		writer.close();

		int responseCode = destSwiftHTTPConnection.getResponseCode();
		if (responseCode != 200 && responseCode != 202 && responseCode != 203 && responseCode != 201) {
			throw new Exception("Failed : HTTP error code : " + destSwiftHTTPConnection.getResponseCode());
		}
		else {
			System.out.println("RESPONSE : " + destSwiftHTTPConnection.getResponseCode()
					+ "\n Message : " + objectName + " uploaded successfully");
			sourceInputStream.close();
			writer.close();
			destSwiftHTTPConnection.disconnect();
		}
	}

	private static void getAuthTokenForDestinationSwiftAccount(String accountName, String userName) throws Exception
	{
		String uri = prop.getProperty(SWIFT_DESTINATION_URL);
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		requestHeaders.put("X-Auth-User", accountName.toString() + ":" + userName);
		requestHeaders.put("X-Auth-Key", "pass@123");

		HttpURLConnection connection = new PrepareHttpUrlConnection().prepareConnection(uri, "GET", requestHeaders);

		if (connection.getResponseCode() != 200 && connection.getResponseCode() != 202
				&& connection.getResponseCode() != 203 && connection.getResponseCode() != 201) {
			throw new Exception("Failed : HTTP error code : " + connection.getResponseCode());
		}

		DEST_AUTH_TOKEN = connection.getHeaderField("X-Auth-Token");
		DEST_AUTH_URL = connection.getHeaderField("X-Storage-Url");
	}

}
