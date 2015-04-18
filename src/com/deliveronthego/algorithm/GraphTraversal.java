package com.deliveronthego.algorithm;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class GraphTraversal {
	
	//private HashMap<String,ArrayList> nodeValues= new HashMap<String,ArrayList>();
	
	static MongoClient mongoClient;
	
	public static MongoClient getConnection(){
		MongoClientURI clientURI=new MongoClientURI("mongodb://deliveronthego:deliveronthego@ds037097.mongolab.com:037097/deliveronthego");
		mongoClient =new MongoClient(clientURI);
		return mongoClient;
	}
	
	public static HashMap<String, HashMap> initializeDriverSourceValues()
	{
		HashMap<String,HashMap> driverSourceValues= new HashMap<>();
		Date date=new Date();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		String dateChk;
		Double srcLat, srcLon;
		String driverId;
		mongoClient=getConnection();
		DB db=mongoClient.getDB("deliveronthego");
		DBCollection driverSourceDetails=db.getCollection("location");
		DBCursor cursor =driverSourceDetails.find();
		System.out.println("cursor=="+cursor.count());
		while(cursor.hasNext())
		{
			HashMap temp=new HashMap<String,Double>();
			BasicDBObject obj=(BasicDBObject)cursor.next();
			System.out.println("df.format(date))==="+df.format(date));
			dateChk=(String)obj.get("Date");
			if(dateChk.equals(df.format(date)))
			{
				srcLat=(Double) obj.get("transitionLatitude");
				srcLon=(Double)obj.get("transitionLongitude");
	
				driverId=(String)obj.get("driverID");
				
				temp.put("latitude",srcLat);
				temp.put("longitude",srcLon);
				temp.put("visited",0); //visited or not
				temp.put("pheromone",0.0); //pheromone
				driverSourceValues.put(driverId,temp);
			}
			
		}
				
		
		System.out.println("SourceDriver=="+driverSourceValues.toString());
		
		return driverSourceValues;
	}
	
	public static HashMap<String, HashMap> initializeDriverDestValues()
	{
		HashMap<String,HashMap> driverDestValues= new HashMap<>();
		
		Double destLat, destLon;
		String driverId;
		mongoClient=getConnection();
		DB db=mongoClient.getDB("deliveronthego");
		DBCollection driverSourceDetails=db.getCollection("location");
		DBCursor cursor =driverSourceDetails.find();
		System.out.println("cursor=="+cursor.count());
		while(cursor.hasNext())
		{
			HashMap temp=new HashMap<String,Double>();
			BasicDBObject obj=(BasicDBObject)cursor.next();
			destLat=(Double)obj.get("stopLatitude");
			destLon=(Double)obj.get("stopLongitude");
			driverId=(String)obj.get("driverID");
			temp.put("latitude",destLat);
			temp.put("longitude",destLon);
			temp.put("visited",0); //visited or not
			temp.put("pheromone",0.0); //pheromone
			driverDestValues.put(driverId,temp);
		}
		
		System.out.println("SourceDestDriver=="+driverDestValues.toString());
		
		return driverDestValues;
	}
	
	public static HashMap<String, HashMap> initializeUserSourceValues(double srcLatitude,double srcLongitude)
	{
		HashMap<String,HashMap> UserSourceValues= new HashMap<>();
		HashMap temp=new HashMap<String,Double>();
		temp.put("latitude",srcLatitude);
		temp.put("longitude",srcLongitude);
		temp.put("visited",0); //visited or not
		temp.put("pheromone",0.0); //pheromone
		UserSourceValues.put("1",temp);
		
		return UserSourceValues;
	}
	
	public static HashMap<String, HashMap> initializeUserDestValues(double destLatitude,double destLongitude)
	{
		HashMap<String,HashMap> userDestValues= new HashMap<>();
		HashMap temp=new HashMap<String,Double>();
		temp.put("latitude",destLatitude);
		temp.put("longitude",destLongitude);
		temp.put("visited",0); //visited or not
		temp.put("pheromone",0.0); //pheromone
		
		userDestValues.put("1",temp);		
		return userDestValues;
	}
	
	
	public String traversal(HashMap sourceDriver,HashMap destDriver,HashMap sourceUser,HashMap destUser, String userId)
	{
		Iterator itSrcDriver = sourceDriver.entrySet().iterator();
		Iterator itDestDriver = destDriver.entrySet().iterator();
		//Iterator itSrcUser = sourceUser.entrySet().iterator();
		//Iterator itDestUser = destUser.entrySet().iterator();
		
		String key,driver = null;
		String dummy=null;
		double weightage;
		GraphTraversal g=new GraphTraversal();
		HashMap markVisited;
		double driverSourceLat,driverSourceLon,userSourceLat,userSourceLon,distance, driverDistance;
		double driverDestLat, driverDestLon, userDestLat, userDestLon;
		
		while(itSrcDriver.hasNext())
		{
			
			Map.Entry pairSrcDriver=(Map.Entry)itSrcDriver.next();
			
			markVisited= (HashMap) pairSrcDriver.getValue();
			markVisited.replace("visited", 1);   //marking the node as visited
			driverSourceLat=(double) markVisited.get("latitude");
			driverSourceLon=(double) markVisited.get("longitude");
			sourceDriver.put(pairSrcDriver.getKey(), markVisited);	
			
									
			System.out.println("SourceDriver=="+sourceDriver.get(pairSrcDriver.getKey()).toString());
			
			markVisited= (HashMap) sourceUser.get("1");
			markVisited.replace("visited", 1);  //marking the node as visited
			userSourceLat=(double) markVisited.get("latitude");
			userSourceLon=(double) markVisited.get("longitude");
			sourceUser.put("1", markVisited);
			
			System.out.println("SourceUser=="+sourceUser.get("1").toString());
			
			//calculate the distance from driver source to user pickup source
			
			distance=g.calcDistance(driverSourceLat,driverSourceLon,userSourceLat,userSourceLon);
			
			markVisited= (HashMap) destUser.get("1");
			markVisited.replace("visited", 1);  //marking the node as visited
			userDestLat=(double) markVisited.get("latitude");
			userDestLon=(double) markVisited.get("longitude");
			 destUser.put("1", markVisited);
			 
			 distance=distance + g.calcDistance(userSourceLat, userSourceLon, userDestLat, userDestLon);
			
			System.out.println("Dest User=="+destUser.get("1").toString());
			
			Map.Entry pairDestDriver=(Map.Entry)itDestDriver.next();
			
			markVisited= (HashMap) pairDestDriver.getValue();
			markVisited.replace("visited", 1);  //marking the node as visited
			driverDestLat=(double) markVisited.get("latitude");
			driverDestLon=(double) markVisited.get("longitude");
			 destDriver.put(pairDestDriver.getKey(), markVisited);
			
			System.out.println("driverDestLat==="+driverDestLat);
			System.out.println("driverDestLon==="+driverDestLon);
			
			distance=distance + g.calcDistance(userDestLat, userDestLon, driverDestLat, driverDestLon);
			driverDistance=g.calcDistance(driverSourceLat,driverSourceLon,driverDestLat,driverDestLon);
			System.out.println("the total distance is=="+distance);
			placePhero(sourceDriver,(String)pairSrcDriver.getKey(),distance,driverDistance);
							
		}
		dummy=getBestDriver(sourceDriver);
		return dummy;
		
	}
	
	
	//good factors
	 private void placePhero (HashMap sourceDriver, String driverId, double distance, double driverDistance)
	  {                  
		 System.out.println("driverID======"+driverId);
		 System.out.println("distance=="+distance);
		 System.out.println("driverDistance=="+driverDistance);

	    String driver;            
	    HashMap placePhero;
	    double existingPheroAmount,weightage;
	    double distanceFactor;
	    distanceFactor=distance-driverDistance;
	    System.out.println("distance Factor*****"+distanceFactor);
	   /* if(distanceFactor < -50)
	    {
	    	weightage=5.0;
	    }
	    else if(distanceFactor >= -1000 && distanceFactor< -60)
	    {
	    	weightage=3.0;
	    }
	    else 
	    {
	    	weightage=0.0;
	    }*/
	    weightage=distanceFactor;
	    	placePhero= (HashMap) sourceDriver.get(driverId);  //driver id
	    	existingPheroAmount= (double) placePhero.get("pheromone");
	    	placePhero.replace("pheromone", weightage);   //adding pheromone
			sourceDriver.put(driverId, placePhero);		
			
			System.out.println("SourceDriver after pheromone=="+sourceDriver.get(driverId).toString());
			System.out.println("Source Driver hashmap=="+sourceDriver.toString());
				                            
	  }  
	 
	 public void addEvaporation()
	 {
		 
	 }
	 
	 private int allocateAnts(int i)
	 {
		 
		 return i;
	 }
	
	 
	 
	 
	 
	 //Distance
	 private static String readAll(Reader rd) throws IOException 
		{
		   StringBuilder sb = new StringBuilder();
		   int cp;
		   while ((cp = rd.read()) != -1) 
		   {
		     sb.append((char) cp);
		   }
		   return sb.toString();
		}
	 
	 public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException 
		{
			
		   InputStream is = new URL(url).openStream();
		   try 
		   {
		     BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		     String jsonText = readAll(rd);
		     JSONObject json = new JSONObject(jsonText);
		     return json;
		   }
		   finally 
		   {
		     is.close();
		   }
		}
	 
	 public float calcDistance(double srcLatitude,double srcLongitude,double destLatitude,double destLongitude) 
		{				
			JSONObject json=null;
			float dist = 0;
			try 
			{
		
			//json = readJsonFromUrl("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+string+"&destinations="+string2+"&mode=driving&sensor=false&key=AIzaSyDnT3pFyPjKeyKWTMmlXdfBs-ZCuqf6zMg");
			json = readJsonFromUrl("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+srcLatitude+","+srcLongitude+"&destinations="+destLatitude+","+destLongitude+"&mode=driving&sensor=false&key=AIzaSyDnT3pFyPjKeyKWTMmlXdfBs-ZCuqf6zMg");
			//json.get("rows");
			JSONArray arr=null;
			arr = json.getJSONArray("rows");
			System.out.println(arr.getJSONObject(0)+ " arr");
			
				
				Integer tem = (Integer)arr.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value");
				dist=(float)tem/1000;
				
			}
			catch (JSONException e) 
			{
			e.printStackTrace();
			} 
			catch (IOException e)
			{
			    e.printStackTrace();
			}
			System.out.println("Distance in calcDistance=="+dist);
			return dist;
		}
	 
	 public void calcTime()
	 {
		 
	 }
	 
	 public void calcDimensions()
	 {
		 
	 }
	 
	 public String getBestDriver(HashMap drivers)
	 {
		 HashMap driver;
		 TreeMap bestDriverTreeMap=new TreeMap();
		 TreeMap sortedBestDriver=new TreeMap();
		 String driverId="0";
		 Iterator itDriver = drivers.entrySet().iterator();
		 //ArrayList driverWeightage=new ArrayList();
		 
		 System.out.println("drivers in getting best driver==="+drivers.toString());
		
		 double weightage=0.0, bestDriverWeightage;
		 while(itDriver.hasNext())
		 {
			 Map.Entry driverMap=(Map.Entry)itDriver.next();
			 driver=(HashMap) driverMap.getValue();
			 weightage=(double) driver.get("pheromone");
			 driverId=(String) driverMap.getKey();
			 
			 bestDriverTreeMap.put(driverId,weightage);
		 }
		 
		 //weightage=5.0;
		// driverId="0";
		 
		 bestDriverTreeMap.put(driverId,weightage);
		// bestDriver.descendingMap();		 
		Map sortedBestDriverMap=sortByValues(bestDriverTreeMap);
		
		
		Set set = sortedBestDriverMap.entrySet();    //used to display the values
		 
		    // Get an iterator
		    Iterator i = set.iterator();
		 
		    // Display elements
		    while(i.hasNext()) {
		      Map.Entry me = (Map.Entry)i.next();
		      System.out.print(me.getKey() + ": ");
		      System.out.println(me.getValue());
		    }
		
		System.out.println("Best Driver after sorting=="+sortedBestDriverMap.toString());
		return sortedBestDriverMap.toString();
	 }
	 
	 public static <K, V extends Comparable<V>> Map<K, V> 
	    sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator = new Comparator<K>() {
		      public int compare(K k1, K k2) {
		        int compare = 
		              map.get(k1).compareTo(map.get(k2));
		        if (compare == 0) 
		          return 1;
		        else 
		          return compare;
		      }
	    };
	 
	    Map<K, V> sortedByValues = 
	      new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	  }
	 
	 //end
	 public String findDriver(String userId, double srcLatitude, double srcLongitude, double destLatitude, double destLongitude)
	 {
		 String driver,dummy;
		GraphTraversal g= new GraphTraversal();
		HashMap sourceDriver = initializeDriverSourceValues();
		HashMap destDriver = initializeDriverDestValues();
		HashMap sourceUser = initializeUserSourceValues(srcLatitude,srcLongitude);
		HashMap destUser = initializeUserDestValues(destLatitude,destLongitude);
		dummy=g.traversal(sourceDriver,destDriver,sourceUser,destUser,userId); //parameter is userId
		//g.allocateAnts(sourceDriver.size());	
		return dummy.toString();
	 }
	

}
;