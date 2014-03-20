package de.smbsolutions.day.functions.initialization;

import de.smbsolutions.day.functions.database.Database;

/**
 * 
 * Diese Klasse läd bem Start der App alle relevanten Einstellungen aus der
 * Datenbank und stellt sie zur Laufzeit zur Verfügung. Außerdem stellt sie
 * Methoden zur Änderung dieser zur Verfügung.
 * 
 */
public class AppSettings {

	private int mapType;
	private int trackingStatus;
	private int trackingFrequency;
	private int trackingMeter;
	private int cameraShowInGallery;

	/**
	 * Läd bei der Initialiserung Einstellungen aus der Datenbank
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
	 * Getter für Maptype
	 * 
	 * @return mapType
	 */
	public int getMapType() {
		return mapType;
	}

	/**
	 * Setter für Maptype
	 * 
	 */
	public void setMapType(int mapType) {
		Database.changeSettingValue(Database.SETTINGS_MAP_TYPE, mapType);
		this.mapType = mapType;
	}

	/**
	 * Getter für TrackingStatus
	 * 
	 * @return trackingStatus
	 */
	public int getTrackingStatus() {

		return trackingStatus;
	}

	/**
	 * Setter für TrackingStatus
	 */
	public void setTrackingStatus(int trackingStatus) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING, trackingStatus);
		this.trackingStatus = trackingStatus;
	}

	/**
	 * Getter für TrackingFrequency
	 * 
	 * @return trackingFrequency
	 */
	public int getTrackingFrequency() {
		return trackingFrequency;
	}

	/**
	 * Setter für TrackingFrequency
	 */
	public void setTrackingFrequency(int trackingFrequency) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,
				trackingFrequency);
		this.trackingFrequency = trackingFrequency;
	}

	/**
	 * Getter für CameraShowInGallery
	 * 
	 * @return cameraShowInGallery
	 */
	public int getCameraShowInGallery() {
		return cameraShowInGallery;
	}

	/**
	 * Setter für CameraShowInGallery
	 */
	public void setCameraShowInGallery(int cameraShowInGallery) {
		Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL,
				cameraShowInGallery);
		this.cameraShowInGallery = cameraShowInGallery;
	}

	/**
	 * Getter für TrackingMeter
	 * 
	 * @return trackingMeter
	 */
	public int getTrackingMeter() {
		return trackingMeter;
	}

	/**
	 * Setter für TrackingMeter
	 */
	public void setTrackingMeter(int trackingMeter) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_METER,
				trackingMeter);
		this.trackingMeter = trackingMeter;
	}

}
