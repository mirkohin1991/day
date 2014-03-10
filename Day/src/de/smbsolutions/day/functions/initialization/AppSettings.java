package de.smbsolutions.day.functions.initialization;

import de.smbsolutions.day.functions.database.Database;

public class AppSettings {

	private int mapType;
	private int trackingStatus;
	private int trackingFrequency;
	private int trackingMeter;
	private int cameraShowInGallery;

	public AppSettings() {
		//get settings data from database
		mapType = Database.getSettingValue(Database.SETTINGS_MAP_TYPE);
		trackingStatus = Database.getSettingValue(Database.SETTINGS_TRACKING);
		trackingFrequency = Database
				.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL);
		trackingMeter = Database.getSettingValue(Database.SETTINGS_TRACKING_METER);
		cameraShowInGallery = Database
				.getSettingValue(Database.SETTINGS_SHOW_IN_GAL);
	}

	public  int getMapType() {
		return mapType;
	}

	public void setMapType(int mapType) {
		Database.changeSettingValue(Database.SETTINGS_MAP_TYPE, mapType);
		this.mapType = mapType;
	}

	public  int getTrackingStatus() {

		return trackingStatus;
	}

	public void setTrackingStatus(int trackingStatus) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING, trackingStatus);
		this.trackingStatus = trackingStatus;
	}

	public int getTrackingFrequency() {
		return trackingFrequency;
	}

	public void setTrackingFrequency(int trackingFrequency) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,
				trackingFrequency);
		this.trackingFrequency = trackingFrequency;
	}

	public int getCameraShowInGallery() {
		return cameraShowInGallery;
	}

	public void setCameraShowInGallery(int cameraShowInGallery) {
		Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL,
				cameraShowInGallery);
		this.cameraShowInGallery = cameraShowInGallery;
	}

	public int getTrackingMeter() {
		return trackingMeter;
	}

	public void setTrackingMeter(int trackingMeter) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_METER,
				trackingMeter);
		this.trackingMeter = trackingMeter;
	}

}
