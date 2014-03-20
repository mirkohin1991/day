package de.smbsolutions.day.functions.initialization;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import de.smbsolutions.day.R;

/**
 * 
 * Diese Klasse stellt App-Einstellungen (AppSettings.class) sowie weitere
 * Informationen �ber das Android-Ger�t zur Verf�gung.
 * 
 */
public class Device {

	private static Activity context;
	private static AppSettings APP_SETTINGS;
	private static String DEVICE_NAME;
	private static Device instance = null;

	private Device(Activity context) {

		this.context = context;
		// App_settings werden initialisiert
		APP_SETTINGS = new AppSettings();
	}

	/**
	 * 
	 * Initialisiert Device wenn noch keine Instance vorhanden ist.
	 */
	public static Device getInstance(Activity context) {
		if (Device.instance == null) {
			Device.instance = new Device(context);

		}

		return Device.instance;
	}

	/**
	 * Legt die Bildschirm Density fest und gibt diese zur�ck.
	 */
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

	/**
	 * Legt Density f�r die Scrollbar im DetailFragment fest.
	 */
	public static int getPictureScrollbarDensity() {

		// Anteil des Picture Scroll View an Gesamtscreen
		double weightPictureView = context.getResources().getInteger(
				R.integer.weightPictureView);

		// Anteil der Detailmap am Gesamtscreen
		double weightMap = context.getResources().getInteger(
				R.integer.weightMap);

		Point size = new Point();
		context.getWindowManager().getDefaultDisplay().getSize(size);

		// Pixel des Ger�tes
		double displayDensity = size.y;

		// Berechnung wieviel Pixel der Picture Scroll View haben muss, damit
		// das Bild genau reinpasst
		double requiredPreviewDensity = (weightPictureView / (weightPictureView + weightMap))
				* displayDensity;

		return (int) requiredPreviewDensity;

	}

	/**
	 * Getter f�r APP_SETTINGS
	 * 
	 * @return APP_SETTINGS
	 */
	public static AppSettings getAPP_SETTINGS() {
		return APP_SETTINGS;
	}

	/**
	 * Setter f�r APP_SETTINGS
	 * 
	 */
	public static void setAPP_SETTINGS(AppSettings aPP_SETTINGS) {
		APP_SETTINGS = aPP_SETTINGS;
	}

	/**
	 * Getter f�r DEVICE_NAME
	 * 
	 * @return DEVICE_NAME
	 */
	public static String getDEVICE_NAME() {
		return DEVICE_NAME;
	}

	/**
	 * Setter f�r DEVICE_NAME
	 * 
	 */
	public static void setDEVICE_NAME(String dEVICE_NAME) {
		DEVICE_NAME = dEVICE_NAME;
	}

}
