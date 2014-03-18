package de.smbsolutions.day.functions.interfaces;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;

public interface FragmentCallback {

	public void onTaskfinished(LinkedHashMap<Bitmap, Timestamp> bitmaps);
	
	public void onRouteStopped();
	
	public void onRoutePaused ();
	
	
}
