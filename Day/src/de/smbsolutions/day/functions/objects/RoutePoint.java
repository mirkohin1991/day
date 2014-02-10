package de.smbsolutions.day.functions.objects;

import java.sql.Timestamp;

public class RoutePoint {

	// Route_points table
	private int id;
	private Timestamp timestamp;
	private String picture;
	private double latitude;
	private double longitude;

	public RoutePoint(int id, Timestamp timestamp, String picture,
			double latitude, double longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestamp = timestamp;
		this.picture = picture;

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

}
