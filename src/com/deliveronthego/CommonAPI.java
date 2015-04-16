package com.deliveronthego;

import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/hello")
public class CommonAPI {
	@GET
	@Path("/test")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String sayPlainTextHello() {
	    return "Hello DeliverontheGo";
	  }
	
	@POST
	@Path("/driversignup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doSignup(String driverSignUpInfo) {
		
		String message;
		try {
			
			JSONObject statusMessage = new JSONObject();
			JSONObject driverSignUpInfoJsonObj = new JSONObject(driverSignUpInfo);
        	boolean var =new DbConnection().driverSignup(driverSignUpInfoJsonObj.getString("firstName"),driverSignUpInfoJsonObj.getString("firstName"),driverSignUpInfoJsonObj.getString("driverLicense")
        			,driverSignUpInfoJsonObj.getString("emailId"),driverSignUpInfoJsonObj.getString("password"),driverSignUpInfoJsonObj.getInt("phoneNumber"));
        	if(var)
        	{
        		message = "Driver Sign up Information inserted in the database successfully";
        		statusMessage.put("message", message);
                return Response.status(200).entity(statusMessage).build();
        	}
        	else
        	{
        		message = "Driver Sign up Information insertion failed";
        		statusMessage.put("message", message);
                return Response.status(404).entity(statusMessage).build();
        	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message = "Driver Sign up Information insertion failed";
	        return Response.status(404).entity(message).build();
		}
	  }
	
	@POST
	@Path("/deliver")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doDeliveryDetails(String deliver) {
		String message;
		try {
			JSONObject deliverJson = new JSONObject(deliver);
			JSONObject dimensionJson = deliverJson.getJSONObject("dimensions");
        	boolean var =new DbConnection().deliver(deliverJson.getString("emailid"),deliverJson.getString("pickup"),deliverJson.getString("dropOff"),dimensionJson.getInt("length"),dimensionJson.getInt("breadth"),dimensionJson.getInt("width"));
        	
        	if(var)
        	{
        		message = "Delivery Details Inserted successfully";
        		JSONObject statusMessage = new JSONObject();
        		statusMessage.put("message", message);
                return Response.status(200).entity(statusMessage).build();
        	}
        	else
        	{
        		message = "Delivery Details Insertion Failed";
        		JSONObject statusMessage = new JSONObject();
        		statusMessage.put("message", message);
                return Response.status(404).entity(statusMessage).build();
        	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		message = "Delivery Details Insertion Failed";
        return Response.status(404).entity(message).build();
	  }
	
	@POST
	@Path("/location")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSingUp(String location)
	{
		String message;
		try{
			JSONObject locationJsonObj = new JSONObject(location);
			JSONObject transitionJsonObj = locationJsonObj.getJSONObject("transition");
			double transitionLatitude = transitionJsonObj.getDouble("latitude");
			double transitsionLongitude = transitionJsonObj.getDouble("longitude");
			JSONObject StopJsonObj = locationJsonObj.getJSONObject("stop");
			double stoplatitude = StopJsonObj.getDouble("latitude");
			double stopLongitude = StopJsonObj.getDouble("longitude");
			
			System.out.println(transitionLatitude);
			System.out.println(transitsionLongitude);
			System.out.println(stoplatitude);
			System.out.println(stopLongitude);
			
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    int year = cal.get(Calendar.YEAR);
		    int month = cal.get(Calendar.MONTH)+ 1;
		    int day = cal.get(Calendar.DAY_OF_MONTH);
		    String dateStr = month + "/" + day + "/" + year;
		    System.out.println(dateStr);
		    JSONObject statusMessage = new JSONObject();	   
			boolean var = new DbConnection().location(dateStr,transitionLatitude,transitsionLongitude,stoplatitude,stopLongitude,locationJsonObj.getInt("driverID"));
			if(var)
			{
				message = "Location Details Inserted successfully";       		
        		statusMessage.put("message", message);
				return Response.status(200).entity(statusMessage).build();
			}
			else
			{
				message = "Location Details Insertion Failed";
        		statusMessage.put("message", message);
				return Response.status(404).entity(statusMessage).build();
			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			message = "Location Details Inserted successfully";
			return Response.status(404).entity(message).build();
		}
		
	}

	
}
