package de.smbsolutions.hike.functions.interfaces;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;

/**
 * 
 * Callback-Interface um die Kommunikation zwischen Asynctasks und Fragments
 * gewährleisten zu können.
 * 
 */
public interface FragmentCallback {
	/**
	 * Wird ausgeführt wenn Task beenden worden ist. Nun werden fertig erstellte
	 * Bitmaps in ImageViews geladen.
	 */
	public void onTaskfinished(LinkedHashMap<Bitmap, Timestamp> bitmaps);

	/**
	 * Wird ausgeführt wenn eine Route beendet wurde und der Service gestoppt
	 * werden soll. Außerdem werden Kontextabhängige Felder unsichtbar gemacht.
	 */
	public void onRouteStopped();

	/**
	 * Wird ausgeführt wenn eine Route pausiert wurde und der Service gestoppt
	 * werden soll. Außerdem werden Kontextabhängige Felder unsichtbar gemacht
	 * oder ausgetauscht (ViewFlipper).
	 */
	public void onRoutePaused();

}
