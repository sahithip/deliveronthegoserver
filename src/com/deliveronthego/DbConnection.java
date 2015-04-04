package com.deliveronthego;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteResult;

public class DbConnection {
	MongoClientURI mongoclienturi;
	MongoClient mongoclient;

	
	public MongoClient getConnection(){
		 mongoclienturi = new MongoClientURI("mongodb://deliveronthego:deliveronthego@ds037097.mongolab.com:37097/deliveronthego");
		 mongoclient = new MongoClient(mongoclienturi);
		 return mongoclient;
	}
	
	public boolean signup(String username,String password,String userType){
		mongoclient = getConnection();
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection login =  db.getCollection("login");
		BasicDBObject logObj = new BasicDBObject("username",username)
		.append("password",password).append("usertype", userType);
		DBCursor userObj = login.find(logObj);
		

		login.insert(logObj);
		return true;
	}
	public boolean deliver(String emailId, String pickup,String dropOff,String dimensions){
		mongoclient = getConnection();
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection delivery =  db.getCollection("delivery");
		BasicDBObject logObj = new BasicDBObject("emailId",emailId)
		.append("pickup",pickup).append("dropoff", dropOff).append("dimensions", dimensions);
		WriteResult userObj = delivery.insert(logObj);

		return true;
	}
	public boolean login(String username,String password){
		mongoclient = getConnection();
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection login =  db.getCollection("login");
		BasicDBObject logObj = new BasicDBObject("username",username);
		DBCursor userObj = login.find(logObj);
		DBObject pwd = userObj.curr();//getString("Password");
		String paswrd = pwd.get("Password").toString();
		if(paswrd.equals(password)){
		return true;
		}else{
			return false;
		}
	}
	
}
