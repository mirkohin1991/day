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

	public Device(Context context) {
		this.context = context;
		SCREEN_LAYOUT = initScreenLayout();
		SCREEN_DENSITY = initScreenDensity();
		APP_SETTINGS = new AppSettings();
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

}
