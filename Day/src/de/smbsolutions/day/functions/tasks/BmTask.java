package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import de.smbsolutions.day.functions.interfaces.FragmentCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

/**
 * 
 * Der BitmapWorkertask läd im Hintergrund alle Bitmaps von der Festplatte und
 * gibt diese in einer LinkedHashMap zurück.
 * 
 * Diese Tasks sind notwendig, um den Main-UI-Thread nicht zu beeinflussen, und
 * der User keine unnötigen Wartezeiten verspürt.
 * 
 */
public class BmTask extends
		AsyncTask<Route, Void, LinkedHashMap<Bitmap, Timestamp>> {
	// Linkedhashmap und Callback zur Kommunikation mit dem Fragment
	private FragmentCallback callback;
	private LinkedHashMap<Bitmap, Timestamp> bitmaps;

	public BmTask(LinkedHashMap<Bitmap, Timestamp> bitmaps,
			FragmentCallback callback) {
		this.bitmaps = bitmaps;
		this.callback = callback;

	}

	/**
	 * Methode, welche im Hintergrund ausgeführt wird.
	 */
	@Override
	protected LinkedHashMap<Bitmap, Timestamp> doInBackground(Route... params) {
		// Übergabeparameter aus Fragment --> Erstellen der zu bearbeiten Route
		Route route = params[0];

		// Alle RoutePoints werden durchlaufen
		for (RoutePoint point : route.getRoutePoints()) {
			// Hat der Punkt ein Bild?
			if (point.getPicture() != null) {
				try {
					// Erstellen der Bitmap
					File bitmapFile = new File(point.getPicturePreview());
					Bitmap bm = BitmapManager.decodeSampledBitmapFromUri(
							bitmapFile.getPath(), 220, 220);// richtige größe?
					// Speichern in der LinkedHashMap
					bitmaps.put(bm, point.getTimestamp());
					// Leeren der Objekte um MemoryLeaks zu vermeiden.
					bitmapFile = null;
					bm = null;
				} catch (Exception e) {
					Log.d("BitmapWorkertask", "Fehler beim Laden der Bilder");
				}

			}

		}
		return bitmaps;
	}

	/**
	 * Methode, welche nach dem Beenden des Hintergrundtasks ausgeführt wird.
	 */
	@Override
	protected void onPostExecute(LinkedHashMap<Bitmap, Timestamp> result) {
		// TODO Auto-generated method stub

		super.onPostExecute(result);
		// Läd alle Bitmaps in die Scrollbar
		callback.onTaskfinished(result);
	}

}
