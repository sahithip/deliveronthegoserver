package com.deliveronthego;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class DbConnection {
	MongoClientURI mongoclienturi;
	MongoClient mongoclient;

	
	public MongoClient getConnection(){
		 mongoclienturi = new MongoClientURI("mongodb://deliveronthego:deliveronthego@ds037097.mongolab.com:37097/deliveronthego");
		 mongoclient = new MongoClient(mongoclienturi);
		 return mongoclient;
	}
	
	public String customerSignup(String firstName, String lastName, String emailId, String password, int phoneNumber)
	{
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
		DB database = mongoclient.getDB("deliveronthego");
		DBCollection customerSignUpInfo = database.getCollection("login");
		if(emailId.contains("@")&&(password.length()<=8)&&(String.valueOf(phoneNumber).length()==10))
		{
			BasicDBObject customerSignUpInfoObj = new BasicDBObject("emailId", emailId)
			.append("password", password);
			
			DBCursor customerSignUpInfoCur = customerSignUpInfo.find(customerSignUpInfoObj);
			if(customerSignUpInfoCur.hasNext())
			{
				return "Customer Sign Up Info Already Exists";
			}
			else
			{
				customerSignUpInfoObj.append("firstName",firstName)
				.append("lastName", lastName)
				.append("phoneNumber", phoneNumber)
				.append("userType", "C");
				customerSignUpInfo.insert(customerSignUpInfoObj);
				return "Customer Signup Info inserted successfully";
			}
		}
		else
		{
			return "Customer SignUp Info failed to insert";
		}
	}
	
	public String driverSignup(String firstName, String lastName, String driverLicense, String emailId, String password, int phoneNumber){
		mongoclient = getConnection();
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection driverSignUpInfo =  db.getCollection("login");
		if(emailId.contains("@")&&(password.length()<=8)&& (String.valueOf(phoneNumber).length()==10)&&!driverLicense.isEmpty())
		{
		BasicDBObject driverSignUpInfoObj = new BasicDBObject("driverLicense", driverLicense)
		.append("emailId", emailId)
		.append("password",password);
			
		DBCursor driverSignUpInfoCur = driverSignUpInfo.find(driverSignUpInfoObj);
		if(driverSignUpInfoCur.hasNext())
		{
			return "Driver Sign Up Info Already Exists";
		}
		else
		{
			driverSignUpInfoObj.append("firstName",firstName)
			.append("lastName", lastName)
			.append("phoneNumber", phoneNumber)
			.append("userType", "D");
		    driverSignUpInfo.insert(driverSignUpInfoObj);
		    return "Driver Sign Up Info Inserted Successfully";
		}
		}
		else
		{
			return "Driver SignUp Info failed to insert";
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
					String loginUserType = userDetailObj.get("userType").toString();
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
			return true;		
		}
		else
		{
			return false;
		}	
	}
		
	public String location(String date, double transitionLatitude, double transitionLongitude, double stopLatitude, double stopLongitude, String driverId)
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
					location.update(new BasicDBObject("driverID", driverId), new BasicDBObject("$set", new BasicDBObject("stopLatitude", stopLatitude).append("stopLongitude", stopLongitude)));
				}*/
			}
			updateFlag = true;
		}
		
		if(!updateFlag)
		{
			BasicDBObject locationObj = new BasicDBObject ("Date", date.toString())
			.append("transitionLatitude", transitionLatitude).append("transitionLongitude", transitionLongitude)
			.append("stopLatitude", stopLatitude).append("stopLongitude", stopLongitude)
			.append("driverID", driverId);		
			location.insert(locationObj);
			return "Location Details Inserted Sucessfully";
		}
		else
		{
			return "Location Details Updated";
		}

	}	
	public String transcationNotification(String driverID, Boolean pickedUp, Boolean delivered)
	{
		mongoclient = getConnection();
		@SuppressWarnings("deprecation")
		DB db = mongoclient.getDB("deliveronthego");
		DBCollection notification = db.getCollection("notification");
		
		BasicDBObject notificationObj = new BasicDBObject();
		notificationObj.append("driverID", driverID);
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
		
		DBCursor notificationCursor = notification.find(notificationObj);
		
		if(pickedUp && !delivered)
		{	
			notificationObj.append("pickedUp", pickedUp.toString())
			.append("delivered", delivered.toString())
			.append("date", cal.toString());
			notification.insert(notificationObj);
			
			return "New Transaction Data inserted";
		}
		else
		{
			if(notificationCursor.hasNext()) 
			{
				notificationCursor.next();
				DBObject notifyObj = notificationCursor.curr();
				Date currentDateInDatabase = (Date) notifyObj.get("date");
				if(!(boolean) notifyObj.get("delivered") && currentDateInDatabase.before(date))
				{
					notification.update(new BasicDBObject("driverID", driverID), new BasicDBObject("$set", new BasicDBObject("delivered", delivered.toString())));
					return "Transaction Completed";
				}
				else
				{
					return "Transaction failed to update";
				}
			}
			else
			{
				return "Transaction failed";
			}
		}
	}

}
