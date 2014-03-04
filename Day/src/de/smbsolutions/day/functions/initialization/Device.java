package de.smbsolutions.day.functions.initialization;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import de.smbsolutions.day.R;

public class Device {

	private static Context context;
	private static AppSettings APP_SETTINGS;
	private static String DEVICE_NAME;
	private static String SCREEN_LAYOUT;
	private static String SCREEN_DENSITY;
	
	private static Device instance = null;

	public Device(Context context) {
		this.context = context;
		// Screen = large, normal, small?
		SCREEN_LAYOUT = initScreenLayout();
		SCREEN_DENSITY = initScreenDensity();
		// get App_settings
		APP_SETTINGS = new AppSettings();
	}
	
	public static Device getInstance(Context context){
		if (Device.instance == null) {
			Device.instance = new Device(context);
		}
		
		return Device.instance;
	}

	public boolean isTablet() {
		boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);
		if (tabletSize) {

			return true;
		} else {

			return false;
		}
	}

	public String initScreenLayout() {
		if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			return "LARGE";
		} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			return "NORMAL";
		} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			return "SMALL";
		} else {
			return "UNIDENTIFIED";
		}
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

	public static String getSCREEN_LAYOUT() {
		return SCREEN_LAYOUT;
	}

	public static void setSCREEN_LAYOUT(String sCREEN_LAYOUT) {
		SCREEN_LAYOUT = sCREEN_LAYOUT;
	}

	public static String getSCREEN_DENSITY() {
		return SCREEN_DENSITY;
	}

	public static void setSCREEN_DENSITY(String sCREEN_DENSITY) {
		SCREEN_DENSITY = sCREEN_DENSITY;
	}

}
