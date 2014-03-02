package de.smbsolutions.day.functions.initialization;

import de.smbsolutions.day.functions.database.Database;

public class AppSettings {

	private static int MAP_TYPE;
	private static int GPS;
	private static int GPS_FREQUENCY;
	private static int CAMERA_SAVEINGALLERY;

	public AppSettings() {
		MAP_TYPE = Database.getSettingValue(Database.SETTINGS_MAP_TYPE);
		GPS = Database.getSettingValue(Database.SETTINGS_TRACKING);
		GPS_FREQUENCY = Database
				.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL);
		CAMERA_SAVEINGALLERY = Database
				.getSettingValue(Database.SETTINGS_SHOW_IN_GAL);
		
	}

	public static int getMAP_TYPE() {
		return MAP_TYPE;
	}

	public static void setMAP_TYPE(int map_type) {
		Database.changeSettingValue(Database.SETTINGS_MAP_TYPE, map_type);
		MAP_TYPE = map_type;
	}

	public static int getGPS() {

		return GPS;
	}

	public static void setGPS(int gps) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING, gps);
		GPS = gps;
	}

	public static int getGPS_FREQUENCY() {
		return GPS_FREQUENCY;
	}

	public static void setGPS_FREQUENCY(int gps_frequency) {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,
				gps_frequency);
		GPS_FREQUENCY = gps_frequency;
	}

	public static int getCAMERA_SAVEINGALLERY() {
		return CAMERA_SAVEINGALLERY;
	}

	public static void setCAMERA_SAVEINGALLERY(int camera_saveingallery) {
		Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL,
				camera_saveingallery);
		CAMERA_SAVEINGALLERY = camera_saveingallery;
	}

}
