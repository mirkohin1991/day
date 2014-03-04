package de.smbsolutions.day.functions.initialization;

import de.smbsolutions.day.functions.database.Database;

public class AppSettings {

	private int MAP_TYPE;
	private int GPS;
	private int GPS_FREQUENCY;
	private int CAMERA_SAVEINGALLERY;

	public AppSettings() {
		//get settings data from database
		MAP_TYPE = Database.getSettingValue(Database.SETTINGS_MAP_TYPE);
		GPS = Database.getSettingValue(Database.SETTINGS_TRACKING);
		GPS_FREQUENCY = Database
				.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL);
		CAMERA_SAVEINGALLERY = Database
				.getSettingValue(Database.SETTINGS_SHOW_IN_GAL);
	}

	public  int getMAP_TYPE() {
		return MAP_TYPE;
	}

	public void setMAP_TYPE(int map_type) {
		Database.changeSettingValue(Database.SETTINGS_MAP_TYPE, map_type);
		MAP_TYPE = map_type;
	}

	public  int getGPS() {

		return GPS;
	}

	public void setGPS(int gps) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING, gps);
		GPS = gps;
	}

	public int getGPS_FREQUENCY() {
		return GPS_FREQUENCY;
	}

	public void setGPS_FREQUENCY(int gps_frequency) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,
				gps_frequency);
		GPS_FREQUENCY = gps_frequency;
	}

	public int getCAMERA_SAVEINGALLERY() {
		return CAMERA_SAVEINGALLERY;
	}

	public void setCAMERA_SAVEINGALLERY(int camera_saveingallery) {
		Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL,
				camera_saveingallery);
		CAMERA_SAVEINGALLERY = camera_saveingallery;
	}

}
