package com.deliveronthego;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.deliveronthego.DbConnection;

import org.apache.catalina.User;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.JsonParser.NumberType;
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

	
}
