package de.smbsolutions.day.functions.objects;

import java.sql.Timestamp;

import android.os.Parcel;
import android.os.Parcelable;

public class RoutePoint implements Parcelable {

	// Route_points table
	private int id;
	private Timestamp timestamp;
	private String picture;
	private String picture_preview;
	private double latitude;
	private double longitude;
	private double altitude;

	public RoutePoint(int id, Timestamp timestamp, String picture, String picture_preview,
			double latitude, double longitude, double altitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
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
	
	public double getAltitude() {
		return altitude;
	}

	
	public String getPicturePreview() {
		return picture_preview;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


}
