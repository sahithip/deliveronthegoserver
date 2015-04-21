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
			.append("userType", "User");
			
			
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
		.append("userType", "Driver");
		driverSignUpInfo.insert(driverSignUpInfoObj);
		return true;
		}
		//DBCursor userObj = login.find(logObj);
		else
		{
			return false;
		}
	}
	public boolean deliver(String emailId, Double pickupLatitude,Double pickupLongitude, Double dropOffLatitude,Double dropOffLongitude,
			int length, int breadth, int width){
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection delivery =  db.getCollection("delivery");
		BasicDBObject logObj = new BasicDBObject("emailId",emailId)
		.append("pickupLatitude",pickupLatitude)
		.append("pickupLongitude", pickupLongitude)
		.append("dropOffLatitude", dropOffLatitude)
		.append("dropOffLongitude", dropOffLongitude)
		.append("length",length)
		.append("breadth", breadth).append("width", width);
		
		delivery.insert(logObj);

		return true;
	}
	public boolean login(String emailId,String password,String userType){
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
					String loginUserType = userDetailObj.get("usertype").toString();
					System.out.println(logInPassword);
					if((logInPassword!=null)&&(logInPassword.equalsIgnoreCase(password)) && (loginUserType !=null)&&(loginUserType.equalsIgnoreCase(userType)))
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
				location.update(new BasicDBObject("driverID", driverId), new BasicDBObject("$set", new BasicDBObject("transitionLatitude", transitionLatitude).append("transitionLongitude", transitionLongitude)));
				Double previousStopLatitude = (Double) locDB.get("stopLatitude");
				Double previousStopLongitude = (Double) locDB.get("stopLongitude");
				System.out.println("previousStopLatitude: " + previousStopLatitude);
				System.out.println("previousStopLongitude: " + previousStopLongitude);
				/*if((previousStopLatitude==0.0) && (previousStopLongitude==0.0))
				{
					location.update(new BasicDBObject("driverID", driverId), new BasicDBObject("$set", new BasicDBObject("stopLatitude", stoplatitude).append("stopLongitude", stopLongitude)));
				}*/
			}
			updateFlag = true;
		}
		
		if(!updateFlag)
		{
			BasicDBObject locationObj = new BasicDBObject ("Date", date.toString())
			.append("transitionLatitude", transitionLatitude).append("transitionLongitude", transitionLongitude)
			.append("stopLatitude", stoplatitude).append("stopLongitude", stopLongitude)
			.append("driverID", driverId);		
			location.insert(locationObj);
		}
		
		return true;
	}	
}
