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
	
	public boolean customerSignup(String firstName, String lastName, String emailId, String password, int phoneNumber)
	{
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
		DB database = mongoclient.getDB("deliveronthego");
		DBCollection customerSignUpInfo = database.getCollection("login");
		if(emailId.contains("@")&&(password.length()<=8)&&(String.valueOf(phoneNumber).length()==10))
		{
			BasicDBObject customerSignUpInfoObj = new BasicDBObject("firstName",firstName)
			.append("lastName", lastName)
			.append("emailId", emailId)
			.append("password", password)
			.append("phoneNumber", phoneNumber)
			.append("userType", "C");
			
			
			customerSignUpInfo.insert(customerSignUpInfoObj);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean driverSignup(String firstName, String lastName, String driverLicense, String emailId, String password, int phoneNumber){
		mongoclient = getConnection();
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection driverSignUpInfo =  db.getCollection("login");
		if(emailId.contains("@")&&(password.length()<=8)&& String.valueOf(phoneNumber).length()==10)
		{
		BasicDBObject driverSignUpInfoObj = new BasicDBObject("firstName",firstName)
		.append("lastName", lastName)
		.append("driverLicense", driverLicense)
		.append("emailId", emailId)
		.append("password",password).append("phoneNumber", phoneNumber)
		.append("userType", "D");
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
	public boolean login(String emailId,String password){
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection login =  db.getCollection("login");
		if(emailId.contains("@"))
		{
			BasicDBObject logInObj = new BasicDBObject();
			logInObj.put("emailId", emailId);
			DBCursor logInCursor = login.find(logInObj);
			
			while(logInCursor.hasNext())
			{
				logInCursor.next();
				DBObject userDetailObj = logInCursor.curr();
				if(userDetailObj!=null)
				{
					String logInPassword = userDetailObj.get("password").toString();
					System.out.println(logInPassword);
					if((logInPassword!=null)&&(logInPassword.equalsIgnoreCase(password)))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
				
			}
			
		}
		else
		{
			return false;
		}
		return true;		
	}
		
	public boolean location(String date, double transitionLatitude, double transitionLongitude, double stoplatitude, double stopLongitude, String driverId)
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
