package de.smbsolutions.day.functions.initialization;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import de.smbsolutions.day.R;

public class Device {

	private static Context context;
	private static AppSettings APP_SETTINGS;
	private static String DEVICE_NAME;

	private static Device instance = null;

	public Device() {

		// Screen = large, normal, small?

		// get App_settings
		APP_SETTINGS = new AppSettings();
	}

	public static Device getInstance(Context context) {
		if (Device.instance == null) {
			Device.instance = new Device();
		}

		return Device.instance;
	}

	public String initScreenDensity() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int density = metrics.densityDpi;

		if (density == DisplayMetrics.DENSITY_HIGH) {
			return "HIGH";
		} else if (density == DisplayMetrics.DENSITY_MEDIUM) {
			return "MEDIUM";
		} else if (density == DisplayMetrics.DENSITY_LOW) {
			return "LOW";
		} else {
			return "UNIDENTIFIED";
		}
	}

	public static AppSettings getAPP_SETTINGS() {
		return APP_SETTINGS;
	}

	public static void setAPP_SETTINGS(AppSettings aPP_SETTINGS) {
		APP_SETTINGS = aPP_SETTINGS;
	}

	public static String getDEVICE_NAME() {
		return DEVICE_NAME;
	}

	public static void setDEVICE_NAME(String dEVICE_NAME) {
		DEVICE_NAME = dEVICE_NAME;
	}

}
