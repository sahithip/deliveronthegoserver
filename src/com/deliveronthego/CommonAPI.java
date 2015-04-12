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
	@Path("/signup")
	@Consumes(MediaType.APPLICATION_JSON)

	public Response doSignup(String user) {
		//String username = user.getUsername();
        //String password = user.getPassword();
      //  System.out.println("username--"+username);
		try {
			JSONObject json = new JSONObject(user);
        	boolean var =new DbConnection().signup(json.getString("emailid"),json.getString("password"),json.getString("usertype"));
        	if(var)
                return Response.status(200).entity(json.getString("usertype")).build();
        	else
                return Response.status(404).entity("fail").build();
			//System.out.println(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return Response.status(404).entity("fail").build();
	  }
	
	@POST
	@Path("/deliver")
	@Consumes(MediaType.APPLICATION_JSON)

	public Response doDeliveryDetails(String deliver) {
		//String username = user.getUsername();
        //String password = user.getPassword();
      //  System.out.println("username--"+username);
		try {
			JSONObject json = new JSONObject(deliver);
        	boolean var =new DbConnection().deliver(json.getString("emailid"),json.getString("pickup"),json.getString("dropOff"),json.getString("dimensions"));
        	if(var)
                return Response.status(200).entity("success").build();
        	else
                return Response.status(404).entity("fail").build();
			//System.out.println(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return Response.status(404).entity("fail").build();
	  }
	
	@POST
	@Path("/location")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSingUp(String location)
	{
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
				   
			boolean var = new DbConnection().location(dateStr,transitionLatitude,transitsionLongitude,stoplatitude,stopLongitude,locationJsonObj.getInt("driverID"));
			if(var)
				return Response.status(200).entity("Success").build();
			else
				return Response.status(404).entity("fail").build();
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return Response.status(200).entity("success").build();
	}

	
}
