package com.cs.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cs.api.SwiftDataBackUp;
import com.cs.entity.BackupSummery;
import com.cs.entity.Swift;
import com.cs.utils.ClearFileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;


public class BackupJob implements Job{

	private Properties          prop;
	String orientdbTimestamp="";
	final static Logger logger = Logger.getLogger(BackupJob.class);

	public BackupJob()
	{
		try {
			String filename = "config.properties";
			InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				logger.debug("Sorry, could not find " + filename);
				return;
			}
			prop = new Properties();
			prop.load(input);
		}
		catch (IOException ex) {
			logger.error(ex);
		}
	} 

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		try {
			this.startBackup("Corn");
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	public String startBackup(String backupType) throws Exception{
		try { 
			
			Swift swift = startSwiftBackup();
			String resposne = createBackupSummery(swift, backupType);
			return resposne;
		}
		catch (Exception e) {
			logger.error(e);
			createBackupSummeryforException(e.getMessage(), backupType);
			//return e.getMessage();
			throw e;
		} 
	} 

	
	private Swift startSwiftBackup() throws Exception {
		Long start = System.currentTimeMillis();
		try {
			logger.debug("############ starting swift backup ########### \n");

			Swift swift = new SwiftDataBackUp().startBackupProcess();

			Long end = System.currentTimeMillis();
			swift.setTimetaken(end-start);

			logger.debug("############ swift Backup Successful############\n");
			return swift;
		}
		catch (Exception e) {
			throw e;
		}
	}

	private String createBackupSummery(Swift swift, String type) throws Exception{
		// check if file exists, if not write first json else get filecontent and map it into json array and append latest json and save

		try {

			//Orientdb orientdb = setOrientdbProperties(orientBackupResponseMap);
			//Elasticsearch elasticsearch = setElasticsearchProperties(elasticBackupResponseMap);
			//Swift swift = setSwiftProperties(swiftBackupResponseMap);

			BackupSummery summery = new BackupSummery();
			if(swift.getStatus().equals("Fail")){
				summery.setStatus("Fail");  
				String error = getError(swift);
				summery.setError(error);
			} else if (swift.getStatus().equals("Warning")) {
				summery.setStatus("Warning");
			}
			else{
				summery.setStatus("Success");
			}
			summery.setType(type);
			//summery.setOrientDb(orientdb);
			//summery.setElasticsearch(elasticsearch);
			summery.setSwift(swift);

			//writeBackupSummery(summery);
			 String response = writeBackupSummery(summery);
			 return response;
		}
		catch (Exception e) {
			throw e;
		}
	}
/*
	private Orientdb setOrientdbProperties(Map orientBackupResponseMap){

		String backupLocationRemote = prop.getProperty("orientdbBackupLocationRemote");

		String timestamp = orientBackupResponseMap.get("timestamp").toString();
		Long timeTaken = (Long) orientBackupResponseMap.get("timeTaken");
		List<String> response = (List) orientBackupResponseMap.get("response");
		int responseCode = (int) orientBackupResponseMap.get("responseCode");
		String backupLocation = backupLocationRemote + timestamp + ".zip";

		Orientdb orientdb = new Orientdb();

		if(responseCode != 200){
			orientdb.setStatus("Fail");
		} else {
			orientdb.setStatus("Success");
		}

		orientdb.setResponse(response);
		orientdb.setResponseCode(responseCode);
		orientdb.setTimestamp(timestamp);
		orientdb.setTimetaken(timeTaken);
		orientdb.setLocation(backupLocation);

		return orientdb;
	} 

	private Elasticsearch setElasticsearchProperties(Map elasticBackupResponseMap){
		String backupLocationRemote = prop.getProperty("elasticBackupLocationRemote");

		String timestamp = elasticBackupResponseMap.get("timestamp").toString();
		Long timeTaken = (Long)elasticBackupResponseMap.get("timeTaken");
		String response = elasticBackupResponseMap.get("response").toString();
		int responseCode = (int) elasticBackupResponseMap.get("responseCode");
		String backupLocation = backupLocationRemote +"snap-"+ timestamp + ".dat";

		Elasticsearch elasticsearch = new Elasticsearch();

		if (responseCode != 0 && response.toString().contains("error")) {
			elasticsearch.setStatus("Fail");
		} else {
			elasticsearch.setStatus("Success");
		}

		elasticsearch.setResponse(response);
		elasticsearch.setResponseCode(responseCode);
		elasticsearch.setTimestamp(timestamp);
		elasticsearch.setTimetaken(timeTaken);
		elasticsearch.setLocation(backupLocation);

		return elasticsearch;
	} 
*/
	private Swift setSwiftProperties(Map swiftBackupResponseMap){
		String backupLocationRemote = prop.getProperty("swiftBackupLocationRemote");

		//String timestamp = swiftBackupResponseMap.get("timestamp").toString();
		//Long timeTaken = (Long)swiftBackupResponseMap.get("timeTaken");
		/* String response = swiftBackupResponseMap.get("response").toString();
    int responseCode = (int) swiftBackupResponseMap.get("responseCode");
    String backupLocation = backupLocationRemote + timestamp + ".json";*/

		new ObjectMapper().convertValue(swiftBackupResponseMap, Swift.class);

		Swift swift = new Swift();

		/*if(responseCode != 201){
      swift.setStatus("Fail");
    } else {
      swift.setStatus("Success");
    }

    swift.setResponse(response);
    swift.setResponseCode(responseCode);
    swift.setTimestamp(timestamp);
    swift.setTimetaken(timeTaken);
    swift.setLocation(backupLocation);*/


		return swift;
	} 

	private void createBackupSummeryforException(String error, String type) throws Exception{
		BackupSummery summery = new BackupSummery();
		summery.setStatus("Fail");  
		summery.setType(type);
		summery.setError(error);

		writeBackupSummery(summery);
	}

	private String writeBackupSummery(BackupSummery summery) throws Exception {

		String backupHistoryLocation = prop.getProperty("backupHistoryLocation");
		String backupHistoryJsonName = prop.getProperty("backupHistoryJsonName");
		String bakupHistoryFilePath = backupHistoryLocation + backupHistoryJsonName;

		File backupHistoryFile = new File(bakupHistoryFilePath);
		ArrayList<BackupSummery> backupSummeryList = new ArrayList<BackupSummery>();
		if (!backupHistoryFile.exists()) {
			backupHistoryFile.createNewFile();
			//ArrayList<BackupSummery> backupSummeryList = new ArrayList<BackupSummery>();
			backupSummeryList.add(summery);

			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(backupHistoryFile, backupSummeryList);
		}
		else { 
			//ArrayList<BackupSummery> backupSummeryList = new ArrayList<BackupSummery>();

			backupSummeryList = new ObjectMapper().readValue(backupHistoryFile, ArrayList.class);
			backupSummeryList.add(0,summery);

			if(backupSummeryList.size()>5){
				backupSummeryList.remove(5);
			}

			Boolean clearFileStatus = ClearFileUtil.execute(bakupHistoryFilePath);
			if(!clearFileStatus){
				throw new Exception("Error While clearing file: " + bakupHistoryFilePath + "\n");
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(backupHistoryFile, backupSummeryList);
		}

	ObjectMapper mapper = new ObjectMapper();
    String response = mapper.writeValueAsString(backupSummeryList);
    return response;
	}

	private String getError(Swift swift){

		/*if(orient.getStatus().equals("Fail")){
			return orient.getResponse().toString();
		} else if(elasticseach.getStatus().equals("Fail")){
			return elasticseach.getResponse();
		} else */
		if(swift.getStatus().equals("Fail")){
			return swift.getResponse();
		}

		return null;
	}
}






