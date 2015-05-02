package com.deliveronthego;

import com.deliveronthego.model.Content;

public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println( "Sending POST to GCM" );

        String apiKey = "AIzaSyDzXEIMFY3EGbJ4mjc9xBYyeakjggxuTC0";
        Content content = createContent();

        DotgGCMPost.post(apiKey, content);
	}
	public static Content createContent(){

        Content c = new Content();

        c.addRegId("APA91bFeqWMKZD9Gpdzd4i1gZwyGq2yrJ-eWy4XJFW9ZIi1eTV0N9hyPe1TlDErU_ezL2VUkBVvpcX337DXuHj3M9eHk1FneiCW3daEwadP3r8DY79GC5Idf5Wu0iJkxf58SkWBzpEJN7n7C7YNJFcUtFwSmt13KPg");
        c.createData("Deliver on the Go", "Driver Pickup Request");

        return c;
    }

}
