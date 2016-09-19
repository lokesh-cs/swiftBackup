package com.cs.entity;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
	
  @JsonProperty("userName")
  String userName;
  
  @JsonProperty("name")
  String name;

  @JsonProperty("containers")
  ArrayList<Container> containers;
  
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Container> getContainers() {
		return containers;
	}

	public void setContainers(ArrayList<Container> containers) {
		this.containers = containers;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
