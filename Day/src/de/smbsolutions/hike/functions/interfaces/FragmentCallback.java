package de.smbsolutions.hike.functions.interfaces;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;

/**
 * 
 * Callback-Interface um die Kommunikation zwischen Asynctasks und Fragments
 * gew�hrleisten zu k�nnen.
 * 
 */
public interface FragmentCallback {
	/**
	 * Wird ausgef�hrt wenn Task beenden worden ist. Nun werden fertig erstellte
	 * Bitmaps in ImageViews geladen.
	 */
	public void onTaskfinished(LinkedHashMap<Bitmap, Timestamp> bitmaps);

	/**
	 * Wird ausgef�hrt wenn eine Route beendet wurde und der Service gestoppt
	 * werden soll. Au�erdem werden Kontextabh�ngige Felder unsichtbar gemacht.
	 */
	public void onRouteStopped();

	/**
	 * Wird ausgef�hrt wenn eine Route pausiert wurde und der Service gestoppt
	 * werden soll. Au�erdem werden Kontextabh�ngige Felder unsichtbar gemacht
	 * oder ausgetauscht (ViewFlipper).
	 */
	public void onRoutePaused();

}
