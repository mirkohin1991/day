package de.smbsolutions.day.functions.initialization;

import de.smbsolutions.day.functions.database.Database;

/**
 * 
 * Diese Klasse l�d bem Start der App alle relevanten Einstellungen aus der
 * Datenbank und stellt sie zur Laufzeit zur Verf�gung. Au�erdem stellt sie
 * Methoden zur �nderung dieser zur Verf�gung.
 * 
 */
public class AppSettings {

	private int mapType;
	private int trackingStatus;
	private int trackingFrequency;
	private int trackingMeter;
	private int cameraShowInGallery;

	/**
	 * L�d bei der Initialiserung Einstellungen aus der Datenbank
	 */
	public AppSettings() {

		mapType = Database.getSettingValue(Database.SETTINGS_MAP_TYPE);
		trackingStatus = Database.getSettingValue(Database.SETTINGS_TRACKING);
		trackingFrequency = Database
				.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL);
		trackingMeter = Database
				.getSettingValue(Database.SETTINGS_TRACKING_METER);
		cameraShowInGallery = Database
				.getSettingValue(Database.SETTINGS_SHOW_IN_GAL);
	}

	/**
	 * Getter f�r Maptype
	 * 
	 * @return mapType
	 */
	public int getMapType() {
		return mapType;
	}

	/**
	 * Setter f�r Maptype
	 * 
	 */
	public void setMapType(int mapType) {
		Database.changeSettingValue(Database.SETTINGS_MAP_TYPE, mapType);
		this.mapType = mapType;
	}

	/**
	 * Getter f�r TrackingStatus
	 * 
	 * @return trackingStatus
	 */
	public int getTrackingStatus() {

		return trackingStatus;
	}

	/**
	 * Setter f�r TrackingStatus
	 */
	public void setTrackingStatus(int trackingStatus) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING, trackingStatus);
		this.trackingStatus = trackingStatus;
	}

	/**
	 * Getter f�r TrackingFrequency
	 * 
	 * @return trackingFrequency
	 */
	public int getTrackingFrequency() {
		return trackingFrequency;
	}

	/**
	 * Setter f�r TrackingFrequency
	 */
	public void setTrackingFrequency(int trackingFrequency) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,
				trackingFrequency);
		this.trackingFrequency = trackingFrequency;
	}

	/**
	 * Getter f�r CameraShowInGallery
	 * 
	 * @return cameraShowInGallery
	 */
	public int getCameraShowInGallery() {
		return cameraShowInGallery;
	}

	/**
	 * Setter f�r CameraShowInGallery
	 */
	public void setCameraShowInGallery(int cameraShowInGallery) {
		Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL,
				cameraShowInGallery);
		this.cameraShowInGallery = cameraShowInGallery;
	}

	/**
	 * Getter f�r TrackingMeter
	 * 
	 * @return trackingMeter
	 */
	public int getTrackingMeter() {
		return trackingMeter;
	}

	/**
	 * Setter f�r TrackingMeter
	 */
	public void setTrackingMeter(int trackingMeter) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_METER,
				trackingMeter);
		this.trackingMeter = trackingMeter;
	}

}
