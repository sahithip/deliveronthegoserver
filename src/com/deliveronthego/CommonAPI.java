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

import com.deliveronthego.algorithm.GraphTraversal;

@Path("/home")
public class CommonAPI {
	@GET
	@Path("/test")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String sayPlainTextHello() {
	    return "Hello DeliverontheGo";
	  }
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logIn(String logInInfo) throws JSONException
	{
		String message = null;		
		JSONObject logInInfoJsonObj = new JSONObject(logInInfo);
		String emailId = logInInfoJsonObj.getString("emailId");
		String password = logInInfoJsonObj.getString("password");
		System.out.println("emailId: "+emailId);
		System.out.println("password: "+password);
		boolean var =  new DbConnection().login(emailId,password);
		
		JSONObject statusMessage = new JSONObject();
		
		if(var)
		{
			message = "Log In success!!";
    		statusMessage.put("message", message);
            return Response.status(200).entity(statusMessage).build();
		}
		else
		{
			message = "Log In Failed!!!";
    		statusMessage.put("message", message);
            return Response.status(200).entity(statusMessage).build();
		}		
	}
	
	
	@POST
	@Path("/customersignup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response customerSignup(String customerSignUpInfo) throws JSONException
	{
		String message;
		JSONObject statusMessage = new JSONObject();
		JSONObject customerSignupInfoJsonObj = new JSONObject (customerSignUpInfo);
		boolean var = new DbConnection().customerSignup(customerSignupInfoJsonObj.getString("firstName"), customerSignupInfoJsonObj.getString("lastName"),
				customerSignupInfoJsonObj.getString("emailId"),
				customerSignupInfoJsonObj.getString("password"), customerSignupInfoJsonObj.getInt("phoneNumber"));
		
		if(var)
		{
			message = "Customer Sign up Information inserted in the database successfully";
    		statusMessage.put("message", message);
            return Response.status(200).entity(statusMessage).build();
		}
		else
		{
			message = "Customer Sign up Information Insertion failed!";
    		statusMessage.put("message", message);
            return Response.status(200).entity(statusMessage).build();
		}		
	}
	
	@POST
	@Path("/driversignup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response driverSignup(String driverSignUpInfo) {
		
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
        	boolean var =new DbConnection().deliver(deliverJson.getString("emailId"),
        			deliverJson.getDouble("pickupLatitude"),deliverJson.getDouble("pickupLongitude"),
        			deliverJson.getDouble("dropOffLatitude"),deliverJson.getDouble("dropOffLongitude"),
        			deliverJson.getInt("length"),deliverJson.getInt("breadth"),deliverJson.getInt("width"));
        	
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
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    int year = cal.get(Calendar.YEAR);
		    int month = cal.get(Calendar.MONTH)+ 1;
		    int day = cal.get(Calendar.DAY_OF_MONTH);
		    String dateStr = month + "/" + day + "/" + year;
		    System.out.println(dateStr);
		    JSONObject statusMessage = new JSONObject();	   
			boolean var = new DbConnection().location(dateStr,locationJsonObj.getDouble("transitionLatitude"),locationJsonObj.getDouble("transitionLongitude"),
					locationJsonObj.getDouble("stopLatitude"),locationJsonObj.getDouble("stopLongitude"),locationJsonObj.getString("driverID"));
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
			message = "Location Details Insertion Failed";
			return Response.status(404).entity(message).build();
		}
		
	}
	
	@POST
    @Path("/findDriver")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findDriver(String userDetails) throws JSONException {
		String driver;
		try{
		JSONObject json = new JSONObject(userDetails);
		GraphTraversal g=new GraphTraversal();
		//driver= g.findDriver(json.getString("emailid"));
		System.out.println("user id==="+json.getString("emailid"));
		driver= g.findDriver(json.getString("emailid"),json.getDouble("pickupLatitude"),json.getDouble("pickupLongitude"),json.getDouble("dropOffLatitude"),json.getDouble("dropOffLongitude"));
		JSONObject response=new JSONObject(driver);
		System.out.println("response value=="+response.toString());
      //	boolean var =new DbConnection().deliver(json.getString("emailid"),json.getString("pickup"),json.getString("dropOff"),json.getString("dimensions"));
		if(driver != null)
		    return Response.status(200).entity(response.toString()).build();			
		else
		    return Response.status(404).entity("fail").build();
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return Response.status(404).entity("fail").build();
	  }

	
}
