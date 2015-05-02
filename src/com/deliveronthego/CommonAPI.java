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
		String userType = logInInfoJsonObj.getString("userType");

		System.out.println("emailId: "+emailId);
		System.out.println("password: "+password);
		boolean var =  new DbConnection().login(emailId,password,userType);
		
		JSONObject statusMessage = new JSONObject();
		
		if(var)
		{
			message = "Log In success!!";
    		statusMessage.put("message", message);
    		statusMessage.put("emailid", emailId);
    		statusMessage.put("userType", userType);
            return Response.status(200).entity(statusMessage).build();
		}
		else
		{
			message = "Log In Failed!!!";
    		statusMessage.put("message", message);
            return Response.status(400).entity(statusMessage).build();
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
		String responseMessage = new DbConnection().customerSignup(customerSignupInfoJsonObj.getString("firstName"), customerSignupInfoJsonObj.getString("lastName"),
				customerSignupInfoJsonObj.getString("emailId"),
				customerSignupInfoJsonObj.getString("password"), customerSignupInfoJsonObj.getInt("phoneNumber"));
		
		if(responseMessage.equalsIgnoreCase("Customer Sign Up Info Already Exists"))
		{
			message = "Customer Info Already Exists";
    		statusMessage.put("message", message);
    		statusMessage.put("status", "402");
            return Response.status(402).entity(statusMessage).build();
		}
		else 			
		{
<<<<<<< HEAD
			
=======
>>>>>>> 2bff7d6d4f0f47f2477bafcad74a0a5f8ba5bef1
			if(responseMessage.equalsIgnoreCase("Customer SignUp Info failed to insert"))
			{
				message = "Customer Sign up Information Insertion failed!";
	    		statusMessage.put("message", message);
	    		statusMessage.put("status", "404");
	            return Response.status(404).entity(statusMessage).build();
			}
			else
			{
				message = "Customer Sign up Information Inserted successfully!";
	    		statusMessage.put("message", message);
	    		statusMessage.put("status", "200");
	            return Response.status(200).entity(statusMessage).build();
				
			}
		}		
	}
	
	@POST
	@Path("/driversignup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response driverSignup(String driverSignUpInfo) throws JSONException 
	{
		
		String message;
		JSONObject statusMessage = new JSONObject();
		JSONObject driverSignUpInfoJsonObj = new JSONObject(driverSignUpInfo);
    	String responseMessage =new DbConnection().driverSignup(driverSignUpInfoJsonObj.getString("firstName"),driverSignUpInfoJsonObj.getString("firstName"),driverSignUpInfoJsonObj.getString("driverLicense")
    			,driverSignUpInfoJsonObj.getString("emailId"),driverSignUpInfoJsonObj.getString("password"),driverSignUpInfoJsonObj.getInt("phoneNumber"));
    	
    	if(responseMessage.equalsIgnoreCase("Driver Sign Up Info Already Exists"))
		{
			message = "Driver Info Already Exists";
    		statusMessage.put("message", message);
    		statusMessage.put("status", "402");
            return Response.status(402).entity(statusMessage).build();
		}
		else 			
		{
			if(responseMessage.equalsIgnoreCase("Driver SignUp Info failed to insert"))
			{
				message = "Driver Sign up Information Insertion failed!";
	    		statusMessage.put("message", message);
	    		statusMessage.put("status", "404");
	            return Response.status(404).entity(statusMessage).build();
			}
			else
			{
				message = "Driver Sign up Information Inserted successfully!";
	    		statusMessage.put("message", message);
	    		statusMessage.put("status", "200");
	            return Response.status(200).entity(statusMessage).build();
				
			}
		}		

	  }
	
	@POST
	@Path("/findDrivers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doDeliveryDetails(String deliver) {
		System.out.println("inside delivery details");

		String message;
		GraphTraversal g=new GraphTraversal();
		try {
			System.out.println("inside delivery details");
			JSONObject deliverJson = new JSONObject(deliver);			
        	boolean var =new DbConnection().deliver(deliverJson.getString("emailId"),
        			Double.valueOf(deliverJson.getString("pickupLatitude")),Double.valueOf(deliverJson.getString("pickupLongitude")),
        			Double.valueOf(deliverJson.getString("dropOffLatitude")),Double.valueOf(deliverJson.getString("dropOffLongitude")),
        			deliverJson.getInt("length"),deliverJson.getInt("breadth"),deliverJson.getInt("width"));
			System.out.println("after delivery details");

        	if(var)
        	{
        		System.out.println("in success");
        		message = "Delivery Details Inserted successfully";
        		JSONObject statusMessage = new JSONObject();
        		statusMessage.put("message", message);
        		String res = g.findDriver(deliverJson.getString("emailId"),deliverJson.getDouble("pickupLatitude"),deliverJson.getDouble("pickupLongitude"),deliverJson.getDouble("dropOffLatitude"),deliverJson.getDouble("dropOffLongitude"));
        		System.out.println(res);
                return Response.status(200).entity(statusMessage).build();
        	}
        	else
        	{
        		System.out.println("in fail");

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
	public Response getSingUp(String location) throws JSONException
	{
		String message;
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
		String responseMessage = new DbConnection().location(dateStr,locationJsonObj.getDouble("transitionLatitude"),locationJsonObj.getDouble("transitionLongitude"),
				locationJsonObj.getDouble("stopLatitude"),locationJsonObj.getDouble("stopLongitude"),locationJsonObj.getString("driverID"));
		
		if(responseMessage.equalsIgnoreCase("Location Details Inserted Sucessfully"))
		{
			message = "Location Details Inserted successfully";       		
    		statusMessage.put("message", message);
    		statusMessage.put("status", "200");
			return Response.status(200).entity(statusMessage).build();
		}
		else
		{
			if(responseMessage.equalsIgnoreCase("Location Details Updated"))
			{
				message = "Location Details Updated Successfully";
	    		statusMessage.put("message", message);
	    		statusMessage.put("status", "201");
				return Response.status(201).entity(statusMessage).build();
				
			}
			else
			{
				message = "Location Details Insertion Failed";
	    		statusMessage.put("message", message);
	    		statusMessage.put("status", "404");
				return Response.status(404).entity(statusMessage).build();
			}
		}
		
	}
	
	//@POST
   // @Path("/findDriver")
	//@Consumes(MediaType.APPLICATION_JSON)
	public String findDriver(String userDetails) throws JSONException {
		String driver;
		GraphTraversal g=new GraphTraversal();
		try{
		JSONObject json = new JSONObject(userDetails);
		
		//driver= g.findDriver(json.getString("emailid"));
		System.out.println("user id==="+json.getString("emailid"));
		driver= g.findDriver(json.getString("emailid"),json.getDouble("pickupLatitude"),json.getDouble("pickupLongitude"),json.getDouble("dropOffLatitude"),json.getDouble("dropOffLongitude"));
		JSONObject response=new JSONObject(driver);
		System.out.println("response value=="+response.toString());
      //	boolean var =new DbConnection().deliver(json.getString("emailid"),json.getString("pickup"),json.getString("dropOff"),json.getString("dimensions"));
		if(driver != null)
		    return driver;//Response.status(200).entity(response.toString()).build();			
		else
		    return "fail";//Response.status(404).entity("fail").build();
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return "fail";//Response.status(404).entity("fail").build();
	  }

	@POST
	@Path("/transaction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTransaction(String transactionDetails) throws JSONException
	{
		JSONObject transactionDetailsObj = new JSONObject(transactionDetails);
		String driverID = transactionDetailsObj.getString("driverID");
		Boolean pickedUp = transactionDetailsObj.getBoolean("pickedUp");
		Boolean delivered = transactionDetailsObj.getBoolean("delivered");
		
		JSONObject statusMessage = new JSONObject();
		String responseMessage = new DbConnection().transcationNotification(driverID, pickedUp, delivered);
		
		if(responseMessage.equalsIgnoreCase("New Transaction Data inserted"))
		{
			statusMessage.put("Status Message", "Transaction Details Inserted Succesfully");
			statusMessage.put("Status-Code", "200");
			return Response.status(200).entity(statusMessage).build();
		}
		else
		{
			if(responseMessage.equalsIgnoreCase("Transaction Completed"))
			{
				statusMessage.put("Status Message", "Transaction Details Updated Succesfully");
				statusMessage.put("Status-Code", "201");
				return Response.status(201).entity(statusMessage).build();
			}
			else
			{
				statusMessage.put("Status Message", "Transaction Failed");
				statusMessage.put("Status-Code", "404");
				return Response.status(404).entity(statusMessage).build();
			}
		}
	}
	
}
