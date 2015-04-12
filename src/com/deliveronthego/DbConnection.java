package com.deliveronthego;

import java.util.Date;

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
		@SuppressWarnings("deprecation")
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection delivery =  db.getCollection("delivery");
		BasicDBObject logObj = new BasicDBObject("emailId",emailId)
		.append("pickup",pickup).append("dropoff", dropOff).append("dimensions", dimensions);
		WriteResult userObj = delivery.insert(logObj);

		return true;
	}
	public boolean login(String username,String password){
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
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
		
	public boolean location(String date, double transitionLatitude, double transitionLongitude, double stoplatitude, double stopLongitude, int driverId)
	{
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection location = db.getCollection("location");
		System.out.println(date);
		System.out.println(driverId);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("driverID", driverId);
		DBCursor locationCursor = location.find(whereQuery);
		boolean updateFlag = false;
		
		while(locationCursor.hasNext())
		{
			locationCursor.next();
			DBObject locDB = locationCursor.curr();
			System.out.println(locDB);
			String dateStr = locDB.get("Date").toString();
			if(dateStr.equals(date))
			{
				location.update(new BasicDBObject("driverID", driverId), new BasicDBObject("$set", new BasicDBObject("transition.latitude", transitionLatitude).append("transition.longitude", transitionLongitude)));
				if((stoplatitude!=0.0) && (stopLongitude!=0.0))
				{
					BasicDBObject stopDB = (BasicDBObject) locDB.get("stop");
					stopDB.put("latitude", stoplatitude);
					stopDB.put("longitude", stopLongitude);
					location.update(new BasicDBObject("date", date), new BasicDBObject("$push", stopDB));
				}
			}
			updateFlag = true;
		}
		
		if(!updateFlag)
		{
			BasicDBObject locationObj = new BasicDBObject ("Date", date.toString())
			.append("transition", new BasicDBObject("latitude",transitionLatitude).append("longitude", transitionLongitude) )
			.append("stop", new BasicDBObject("latitude",stoplatitude).append("longitude", stopLongitude))
			.append("driverID", driverId);		
			location.insert(locationObj);
		}
		
		return true;
	}	
}
