package com.cs.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@JsonIgnoreProperties(ignoreUnknown = true)
public class Objects {

	@JsonProperty("hash")
	private String hash;
	
//	@JsonProperty("last_modified")
	private String last_modified;
	
	@JsonProperty("bytes")
	private String bytes;
	
	@JsonProperty("name")
	private String name;
	
//	@JsonProperty("content_type")
	private String content_type;
	
//	@JsonProperty("X-Object-Meta-Deleted")
	private String X_Object_Meta_Deleted;
	
//	@JsonProperty("X-Object-Meta-Format")
  private String X_Object_Meta_Format;
	
//	@JsonProperty("X-Object-Meta-Name")
  private String X_Object_Meta_Name;
	
//	@JsonProperty("X-Object-Meta-Resolution")
  private String X_Object_Meta_Resolution;
	
//	@JsonProperty("X-Delete-After")
  private String X_Delete_After;
	
//	@JsonProperty("X-Object-Meta-Type")
  private String X_Object_Meta_Type;

//	@JsonProperty("X-Object-Meta-Original")
	private String X_Object_Meta_Original;
	
//	@JsonProperty("X-Object-Meta-Thumb")
  private String X_Object_Meta_Thumb;

  
  public String getHash()
  {
    return hash;
  }

  
  public void setHash(String hash)
  {
    this.hash = hash;
  }

  
  public String getLast_modified()
  {
    return last_modified;
  }

  
  public void setLast_modified(String last_modified)
  {
    this.last_modified = last_modified;
  }

  
  public String getBytes()
  {
    return bytes;
  }

  
  public void setBytes(String bytes)
  {
    this.bytes = bytes;
  }

  
  public String getName()
  {
    return name;
  }

  
  public void setName(String name)
  {
    this.name = name;
  }

  
  public String getContent_type()
  {
    return content_type;
  }

  
  public void setContent_type(String content_type)
  {
    this.content_type = content_type;
  }

  
  public String getX_Object_Meta_Deleted()
  {
    return X_Object_Meta_Deleted;
  }

  
  public void setX_Object_Meta_Deleted(String x_Object_Meta_Deleted)
  {
    X_Object_Meta_Deleted = x_Object_Meta_Deleted;
  }

  
  public String getX_Object_Meta_Format()
  {
    return X_Object_Meta_Format;
  }

  
  public void setX_Object_Meta_Format(String x_Object_Meta_Format)
  {
    X_Object_Meta_Format = x_Object_Meta_Format;
  }

  
  public String getX_Object_Meta_Name()
  {
    return X_Object_Meta_Name;
  }

  
  public void setX_Object_Meta_Name(String x_Object_Meta_Name)
  {
    X_Object_Meta_Name = x_Object_Meta_Name;
  }

  
  public String getX_Object_Meta_Resolution()
  {
    return X_Object_Meta_Resolution;
  }

  
  public void setX_Object_Meta_Resolution(String x_Object_Meta_Resolution)
  {
    X_Object_Meta_Resolution = x_Object_Meta_Resolution;
  }

  
  public String getX_Delete_After()
  {
    return X_Delete_After;
  }

  
  public void setX_Delete_After(String x_Delete_After)
  {
    X_Delete_After = x_Delete_After;
  }

  
  public String getX_Object_Meta_Type()
  {
    return X_Object_Meta_Type;
  }

  
  public void setX_Object_Meta_Type(String x_Object_Meta_Type)
  {
    X_Object_Meta_Type = x_Object_Meta_Type;
  }

  
  public String getX_Object_Meta_Original()
  {
    return X_Object_Meta_Original;
  }

  
  public void setX_Object_Meta_Original(String x_Object_Meta_Original)
  {
    X_Object_Meta_Original = x_Object_Meta_Original;
  }

  
  public String getX_Object_Meta_Thumb()
  {
    return X_Object_Meta_Thumb;
  }

  
  public void setX_Object_Meta_Thumb(String x_Object_Meta_Thumb)
  {
    X_Object_Meta_Thumb = x_Object_Meta_Thumb;
  }
	
	/*@Override
  public String toString() {
    return "{ \"hash \": \"" + getHash() + "\", " +
              "\"X-Object-Meta-Deleted \": \"" + getX_Object_Meta_Deleted() + "\", " +
              "\"X-Object-Meta-Format \": \"" + getX_Object_Meta_Format() + "\", " + 
              "\"X-Object-Meta-Name \": \"" + getX_Object_Meta_Name() + "\", " + 
              "\"last-modified \": \"" + getLast_modified() + "\", " +
              "\"bytes \": \"" + getBytes() + "\", " +
              "\"name \": \"" + getName() + "\", " + 
              "\"content-type \": \"" + getContent_type() + "\", " +
              "\"X-Object-Meta-Resolution \": \"" + getX_Object_Meta_Resolution() + "\", " +
              "\"X-Delete-After \": \"" + getX_Delete_After() + "\", " +
              "\"X-Object-Meta-Type \": \"" + getX_Object_Meta_Type() + "\" " +
              " }";
  }*/
	
}
