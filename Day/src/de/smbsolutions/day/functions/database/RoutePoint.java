package de.smbsolutions.day.functions.database;

import java.sql.Timestamp;

public class RoutePoint {
	
    // Route_points table
	private int id;
	private Timestamp timestamp;
	private String picture;
	private double latitude;
	private double longitude;
	
	
	//Route_info table
	private String name;
	private String date;
	private String active;

	public RoutePoint( int id, Timestamp timestamp, String picture, double latitude, double longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestamp = timestamp;
		this.picture = picture;
	}
	
	
	public RoutePoint( int id, Timestamp timestamp, String picture, double latitude, double longitude, String name, String date, String active) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestamp = timestamp;
		this.picture = picture;
		this.name = name;
		this.date = date;
		this.active = active;
	}
	
	public RoutePoint( int id, String name, String date, String active) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.active = active;
	}


	public int getId() {
		return id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getPicture() {
		return picture;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getDate() {
		return date;
	}
	
	
	public String getActive() {
		return active;
	}
	
	
	


}
