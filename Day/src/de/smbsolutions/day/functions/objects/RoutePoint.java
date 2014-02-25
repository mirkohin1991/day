package de.smbsolutions.day.functions.objects;

import java.sql.Timestamp;

public class RoutePoint {

	// Route_points table
	private int id;
	private Timestamp timestamp;
	private String picture;
	private String picture_preview;
	private double latitude;
	private double longitude;

	public RoutePoint(int id, Timestamp timestamp, String picture, String picture_preview,
			double latitude, double longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestamp = timestamp;
		this.picture = picture;
		this.picture_preview = picture_preview;

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

	
	public String getPicturePreview() {
		return picture_preview;
	}
}
