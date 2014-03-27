package de.smbsolutions.hike.functions.objects;

import java.sql.Timestamp;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Diese Klasse bildet einen Punkt in einer Route ab. Damit stellt sie
 * gleichzeitig auch eine Zeile der Datenbanktabelle route_points dar.
 * Letzendlich wird durch diese Klasse ein Mapping DB<->Objektorientierung
 * vorgenommen. Änderungen werden dem Objekt über Methoden mitgeben und durch
 * dieses an die Datenbank weitergegeben Die Klasse implementiert Parcable, um
 * Objekte von Ihr mit einem Bundle zu übertragen
 */
public class RoutePoint implements Parcelable {

	// Einträge der route_point table
	private int id;
	private Timestamp timestamp;
	private String picture;
	private String picture_preview;
	private double latitude;
	private double longitude;
	private double altitude;

	/**
	 * Konstruktor, der mit den späteren Datenkbankfeldern gefüllt werden muss
	 * 
	 * @param id
	 * @param timestamp
	 * @param picture
	 * @param picture_preview
	 * @param latitude
	 * @param longitude
	 * @param altitude
	 */
	public RoutePoint(int id, Timestamp timestamp, String picture,
			String picture_preview, double latitude, double longitude,
			double altitude) {
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


	public String getPicturePreview() {
		return picture_preview;
	}

	/**
	 * Nötig, weil die Klasse Parcable implementiert
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Nötig, weil die Klasse Parcable implementiert
	 */
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
