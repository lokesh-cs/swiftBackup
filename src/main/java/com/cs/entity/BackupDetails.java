package com.cs.entity;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class BackupDetails {

	@JsonProperty("lastBackupTimestamp")
	String lastBackupTimestamp;
	
	@JsonProperty("accounts")
	ArrayList<Account> accounts;

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(ArrayList<Account> accounts) {
		this.accounts = accounts;
	}

	public String getLastBackupTimestamp() {
		return lastBackupTimestamp;
	}

	public void setLastBackupTimestamp(String lastBackupTimestamp) {
		this.lastBackupTimestamp = lastBackupTimestamp;
	}
}
