package de.smbsolutions.hike.functions.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.smbsolutions.day.R;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.objects.Route;
import de.smbsolutions.hike.functions.objects.RoutePoint;

/**
 * 
 * Der MarkerWorkertask erstellt Marker mit Bildervorschau und platziert Sie auf
 * der Map.
 * 
 * Diese Tasks sind notwendig, um den Main-UI-Thread nicht zu beeinflussen, und
 * der User keine unnötigen Wartezeiten verspürt.
 * 
 */
public class MarkerWorkerTask
		extends
		AsyncTask<CopyOnWriteArrayList<RoutePoint>, Void, LinkedHashMap<RoutePoint, Bitmap>> {

	private MainCallback mCallback;
	private GoogleMap map;
	private Context context;
	// CopyOnWirteArrayList um ConcurrentModifcations zu verhinden.
	private CopyOnWriteArrayList<RoutePoint> routePoints;
	private Route route;

	MarkerOptions markerOpt = new MarkerOptions();
	// Weakreference um den GarbageCollector zu sagen, dass die Hashmap wieder
	// schnell collected werden kann
	private final WeakReference<LinkedHashMap<RoutePoint, Marker>> hashMapRef;
	private WeakReference<Bitmap> weakBM;
	// Wichtig um Timestamp und Marker zu speichern
	LinkedHashMap<RoutePoint, Bitmap> bitmapMap = new LinkedHashMap<RoutePoint, Bitmap>();
	LinkedHashMap<RoutePoint, Marker> markerMap;

	public MarkerWorkerTask(GoogleMap map,
			LinkedHashMap<RoutePoint, Marker> markerMap, Route route,
			Context context) {

		hashMapRef = new WeakReference<LinkedHashMap<RoutePoint, Marker>>(
				markerMap);
		this.context = context;
		this.map = map;
		this.route = route;
		this.markerMap = markerMap;
		// Initialisierung des Callbacks
		try {
			mCallback = (MainCallback) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnButtonClick Interface");
		}

	}

	/**
	 * Hintergrundmethode zur Erstelllung der Bitmaps und der Marker
	 */
	@Override
	protected LinkedHashMap<RoutePoint, Bitmap> doInBackground(
			CopyOnWriteArrayList<RoutePoint>... params) {

		Bitmap bitmap = null;
		// get RoutePoints to check for Images
		routePoints = params[0];
		// Durchlaufen aller Punkte
		for (RoutePoint point : routePoints) {
			// Hat der Punkt ein Bild?
			if (point.getPicturePreview() != null) {
				try {
					File pic = new File(point.getPicturePreview());
					Uri uri = Uri.fromFile(pic);

					/*
					 * Hier werden die Bitmaps erstellt und abgerundet.
					 */
					Bitmap resizedBitmap_Placeholder = BitmapFactory
							.decodeResource(context.getResources(),
									R.drawable.resizedbitmap_placeholder);

					int bgwidth = resizedBitmap_Placeholder.getWidth();
					int bgheight = resizedBitmap_Placeholder.getHeight();
					bitmap = BitmapManager.decodeSampledBitmapFromUri(
							uri.getPath(), bgwidth, bgheight);

					if (bitmap != null) {
						bitmap = getResizedBitmap(bitmap, bgheight, bgwidth);

						bitmap = getRoundedCornerBitmap(
								getResizedBitmap(bitmap, bgheight, bgwidth),
								220);
						// Speichert Bitmap in WeakReference (für GC)
						weakBM = new WeakReference<Bitmap>(bitmap);
						// Fügt Referenz der Hashmap hinzu
						bitmapMap.put(point, weakBM.get());
						// Leert die Referenz wieder
						weakBM.clear();
					}
				} catch (Exception e) {
					Log.d("MarkerWorkertask", "Fehler beim Laden der Bilder");
				}

			}

		}

		return bitmapMap;
	}

	/**
	 * Wird ausgeführt wenn der Hintergrundtask beendet wurde und erstellt die
	 * Marker.
	 */
	@Override
	protected void onPostExecute(LinkedHashMap<RoutePoint, Bitmap> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (hashMapRef != null) {

			markerMap = hashMapRef.get();
			LatLngBounds.Builder builder = new LatLngBounds.Builder();

			// Gets the first marker
			Map.Entry<RoutePoint, Marker> firstMarker = markerMap.entrySet()
					.iterator().next();

			for (Map.Entry<RoutePoint, Marker> mapSet : markerMap.entrySet()) {

				if (mapSet.getKey().getPicturePreview() != null) {

					Bitmap bitmapSaved = bitmapMap.get(mapSet.getKey());

					Bitmap background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.custom_marker);

					Bitmap resizedBitmap_Placeholder = BitmapFactory
							.decodeResource(context.getResources(),
									R.drawable.resizedbitmap_placeholder);
					// Initialisiert Markeroptiones und fügt die Bitmaps hinzu
					markerOpt = new MarkerOptions().position(
							new LatLng(mapSet.getKey().getLatitude(), mapSet
									.getKey().getLongitude())).icon(
							BitmapDescriptorFactory.fromBitmap(this.overlay(
									background, resizedBitmap_Placeholder,
									bitmapSaved)));
					// Speichert den Marker in der markerMap
					mapSet.setValue(map.addMarker(markerOpt));
					// Bitmaps werden wieder recylce um Speicherprobleme zu
					// vermeiden
					bitmapSaved.recycle();
					background.recycle();
					resizedBitmap_Placeholder.recycle();
					bitmapSaved = null;
					background = null;
					resizedBitmap_Placeholder = null;
				}

				builder.include(mapSet.getValue().getPosition());

			}
			addMarkerClickListener(map);

			LatLngBounds bounds = builder.build();
			CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(
					bounds, 60);
			map.moveCamera(camUpdate);

		}

		// Leeren der Hashmaps
		hashMapRef.clear();
		bitmapMap.clear();
		context = null;

	}

	/**
	 * Fügt den Makern den Clicklistener hinzu. Wenn darauf geklickt wird,
	 * öffnet sich die Detailansicht des Bildes.
	 */
	private void addMarkerClickListener(GoogleMap map) {
		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// Iteration über MarkerMap
				for (Map.Entry<RoutePoint, Marker> mapSet : markerMap
						.entrySet()) {

					Marker markerSaved = mapSet.getValue();
					if (markerSaved.hashCode() == marker.hashCode()) {
						// Öffnet die Detailansicht des Bildes
						mCallback.onPictureClick(route, mapSet.getKey());

					}

				}

				return false;
			}

		});

	}

	/**
	 * 
	 * Skaliert ein Bitmap und gibt dieses zurück.
	 */
	public static Bitmap getResizedBitmap(Bitmap image, int bgheight,
			int bgwidth) {

		int width = image.getWidth();
		int height = image.getHeight();
		int newWidth = bgwidth;
		int newHeight = bgheight;

		// kalkuliert die skalierte Größe
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// erstellt Matrix zur Manipulation
		Matrix matrix = new Matrix();

		// Verkleinert das Bitmap
		matrix.postScale(scaleWidth, scaleHeight);

		// Erstellt es neu und gibt es zurück
		return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);

	}

	/**
	 * 
	 * Erstellt ein Bitmap mit abgerundeten Ecken.
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap resizedBitmap, int pixels) {
		Bitmap roundedBitmap = Bitmap.createBitmap(resizedBitmap.getWidth(),
				resizedBitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(roundedBitmap);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, resizedBitmap.getWidth(),
				resizedBitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(resizedBitmap, rect, rect, paint);

		return roundedBitmap;
	}

	/**
	 * Erstellt das Overlay für den Marker.
	 */
	public static Bitmap overlay(Bitmap background,
			Bitmap resizedBitmap_Placeholder, Bitmap roundedBitmap) {
		Bitmap bmOverlay = Bitmap.createBitmap(background.getWidth(),
				background.getHeight(), background.getConfig());

		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(roundedBitmap, 0, 10, null);
		canvas.drawBitmap(background, 0, 0, null);

		return bmOverlay;
	}

}
