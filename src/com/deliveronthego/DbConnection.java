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
	
	public boolean driverSignup(String firstName, String LastName, String driverLicense, String emailId, String password, int phoneNumber){
		mongoclient = getConnection();
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection driverSignUpInfo =  db.getCollection("driverSignUpInfo");
		if(emailId.contains("@")&&(password.length()<=8)&& String.valueOf(phoneNumber).length()==10)
		{
		BasicDBObject driverSignUpInfoObj = new BasicDBObject("firstName",firstName)
		.append("LastName", LastName)
		.append(driverLicense, driverLicense)
		.append(emailId, emailId)
		.append("password",password).append("phoneNumber", phoneNumber);
		driverSignUpInfo.insert(driverSignUpInfoObj);
		return true;
		}
		//DBCursor userObj = login.find(logObj);
		else
		{
			return false;
		}
	}
	public boolean deliver(String emailId, String pickup,String dropOff,int length, int breadth, int width){
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection delivery =  db.getCollection("delivery");
		BasicDBObject logObj = new BasicDBObject("emailId",emailId)
		.append("pickup",pickup).append("dropOff", dropOff).append("dimensions", new BasicDBObject("length", length).append("breadth", breadth).append("width", width));
		WriteResult userObj = delivery.insert(logObj);

		return true;
	}
	public boolean login(String email,String password){
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection login =  db.getCollection("login");
		if(email.contains("@"))
		{
			BasicDBObject logObj = new BasicDBObject("username",email);
			DBCursor userObj = login.find(logObj);
			DBObject pwd = userObj.curr();//getString("Password");
			String paswrd = pwd.get("Password").toString();
			if(paswrd.equals(password)){
			return true;
			}else{
				return false;
			}
		}
		else{
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
				BasicDBObject previousStopDB = (BasicDBObject) locDB.get("stop");
				Double previousStopLatitude = previousStopDB.getDouble("latitude");
				Double previousStopLongitude = previousStopDB.getDouble("longitude");
				if((previousStopLatitude==0.0) && (previousStopLongitude==0.0))
				{
					location.update(new BasicDBObject("driverID", driverId), new BasicDBObject("$set", new BasicDBObject("stop.latitude", stoplatitude).append("stop.longitude", stopLongitude)));
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
