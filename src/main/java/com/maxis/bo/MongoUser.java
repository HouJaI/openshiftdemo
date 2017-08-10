package com.maxis.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class MongoUser {

	@Id
	private String id;

	public String username;

	public String password;

	//getter, setter, toString, Constructors
	public MongoUser (String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	
	public void setUsername (String username)
	{
		this.username = username;
	}
	
	public String getUsername ()
	{
		return username;
	}
	
	public void setPassword (String password)
	{
		this.password = password;
	}
	
	public String getPassword ()
	{
		return password;
	}
	
	
}
