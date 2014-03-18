package de.smbsolutions.day.functions.initialization;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.webkit.WebView.FindListener;
import de.smbsolutions.day.R;
import de.smbsolutions.day.R.integer;

public class Device {

	private static Activity context;
	private static AppSettings APP_SETTINGS;
	private static String DEVICE_NAME;

	private static Device instance = null;

	private Device(Activity context) {

	   this.context = context;
	
		// get App_settings
		APP_SETTINGS = new AppSettings();
	}

	public static Device getInstance(Activity context) {
		if (Device.instance == null) {
			Device.instance = new Device(context);
		
			
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
	

	
	public static int getPictureScrollbarDensity() {
		
		
	//Anteil des Picture Scroll View an Gesamtscreen	
	double weightPictureView  = 	context.getResources().getInteger(R.integer.weightPictureView);
		
	//Anteil der Detailmap am Gesamtscreen
	double weightMap = 	context.getResources().getInteger(R.integer.weightMap);
	
	Point size = new Point();
	context.getWindowManager().getDefaultDisplay().getSize(size);
	
	//Pixels of device
	double displayDensity = size.y;
	
   
	//Berechnung wieviel Pixel der Picture Scroll View haben muss, damit das Bild genau reinpasst
	double requiredPreviewDensity = (weightPictureView / (weightPictureView + weightMap) ) * displayDensity ;
		
	return	(int) requiredPreviewDensity;
		
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
