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
 * Der BitmapWorkertask l�d im Hintergrund alle Bitmaps von der Festplatte und
 * gibt diese in einer LinkedHashMap zur�ck.
 * 
 * Diese Tasks sind notwendig, um den Main-UI-Thread nicht zu beeinflussen, und
 * der User keine unn�tigen Wartezeiten versp�rt.
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
	 * Methode, welche im Hintergrund ausgef�hrt wird.
	 */
	@Override
	protected LinkedHashMap<Bitmap, Timestamp> doInBackground(Route... params) {
		// �bergabeparameter aus Fragment --> Erstellen der zu bearbeiten Route
		Route route = params[0];

		// Alle RoutePoints werden durchlaufen
		for (RoutePoint point : route.getRoutePoints()) {
			// Hat der Punkt ein Bild?
			if (point.getPicture() != null) {
				try {
					// Erstellen der Bitmap
					File bitmapFile = new File(point.getPicturePreview());
					Bitmap bm = BitmapManager.decodeSampledBitmapFromUri(
							bitmapFile.getPath(), 220, 220);// richtige gr��e?
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
	 * Methode, welche nach dem Beenden des Hintergrundtasks ausgef�hrt wird.
	 */
	@Override
	protected void onPostExecute(LinkedHashMap<Bitmap, Timestamp> result) {
		// TODO Auto-generated method stub

		super.onPostExecute(result);
		// L�d alle Bitmaps in die Scrollbar
		callback.onTaskfinished(result);
	}

}
