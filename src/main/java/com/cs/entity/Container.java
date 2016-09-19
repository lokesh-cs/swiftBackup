package com.cs.entity;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@JsonIgnoreProperties(ignoreUnknown = true)
public class Container {
	
	@JsonProperty("name")
	String name;
	
	@JsonProperty("objects")
	ArrayList<Objects> objects;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Objects> getObjects() {
		return objects;
	}

	public void setObjects(ArrayList<Objects> objects) {
		this.objects = objects;
	}

}
