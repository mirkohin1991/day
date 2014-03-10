package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class MarkerWorkerTask
		extends
		AsyncTask<ArrayList<RoutePoint>, Void, LinkedHashMap<RoutePoint, Bitmap>> {

	private MainCallback mCallback;
	private GoogleMap map;
	private Context context;
	private List<RoutePoint> routePoints;
	private Route route;
	private TextView tvDistance;
	private TextView tvDuration;
	private View view;
	MarkerOptions markerOpt = new MarkerOptions();
	private final WeakReference<LinkedHashMap<RoutePoint, Marker>> hashMapRef;
	private WeakReference<Bitmap> weakBM;
	// Necessary to save connect timestamp and marker
	LinkedHashMap<RoutePoint, Bitmap> bitmapMap = new LinkedHashMap<RoutePoint, Bitmap>();
	LinkedHashMap<RoutePoint, Marker> markerMap;

	PolylineOptions polylineOptions_back = new PolylineOptions().width(3)
			.color(Color.rgb(136, 204, 0));
	PolylineOptions polylineOptions_top = new PolylineOptions().width(8).color(
			Color.rgb(19, 88, 5));

	public MarkerWorkerTask(GoogleMap map,
			LinkedHashMap<RoutePoint, Marker> markerMap, Route route) {

		hashMapRef = new WeakReference<LinkedHashMap<RoutePoint, Marker>>(
				markerMap);
		
		this.map = map;
		this.route = route;

		view = View.inflate(context, R.layout.detail_infobar, null);

		tvDistance = (TextView) view.findViewById(R.id.tvDistance);
		tvDuration = (TextView) view.findViewById(R.id.tvDuration);

		try {
			tvDuration.setText("blub");
		} catch (Exception e) {
			// TODO: handle exception
		}

		tvDistance.setText("blub2");

		// Saving the markermap. Necessary, because the route object shall get
		// the changes!
		this.markerMap = markerMap;

		try {
			mCallback = (MainCallback) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnButtonClick Interface");
		}

	}

	@Override
	protected LinkedHashMap<RoutePoint, Bitmap> doInBackground(

	ArrayList<RoutePoint>... params) {

		Bitmap bitmap = null;

		routePoints = params[0];

		for (RoutePoint point : routePoints) {
			if (point.getPicturePreview() != null) {

				File pic = new File(point.getPicturePreview());
				Uri uri = Uri.fromFile(pic);

				Drawable d = context.getResources().getDrawable(
						R.drawable.resizedbitmap_placeholder);
				int bgwidth = d.getIntrinsicHeight();
				int bgheight = d.getIntrinsicWidth();

				bitmap = BitmapManager.decodeSampledBitmapFromUri(
						uri.getPath(), bgwidth, bgheight);

				if (bitmap != null) {
					bitmap = getResizedBitmap(bitmap, bgheight, bgwidth);

					bitmap = getRoundedCornerBitmap(
							getResizedBitmap(bitmap, bgheight, bgwidth), 220);
					weakBM = new WeakReference<Bitmap>(bitmap);

					bitmapMap.put(point, weakBM.get());
					weakBM.clear();
					
				}

			}

		}

		return bitmapMap;
	}

	@Override
	protected void onPostExecute(LinkedHashMap<RoutePoint, Bitmap> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (hashMapRef != null) {

			markerMap = hashMapRef.get();
			LatLngBounds.Builder builder = new LatLngBounds.Builder();

			map.clear();

			// Gets the first marker
			Map.Entry<RoutePoint, Marker> firstMarker = markerMap.entrySet()
					.iterator().next();

			// Gets the Timestamp of the first marker
			long startDate = firstMarker.getKey().getTimestamp().getTime();

			// Set the start Lat and Long
			double startMarkerLat = firstMarker.getKey().getLatitude();
			double startMarkerLong = firstMarker.getKey().getLongitude();

			int distanceMeter = 0;

			double altitudeTotal = 0;
			double maxAltitude = 0;
			double minAltitude = 0;
			double peakAltitude = 0;
			double altitudeOld = 0;
			double altitudeAct = 0;

			Location locStart = new Location("start");
			Location locDest = new Location("destination");

			locStart.setLatitude(startMarkerLat);
			locStart.setLongitude(startMarkerLong);

			long durationAct = 0;

			for (Map.Entry<RoutePoint, Marker> mapSet : markerMap.entrySet()) {

				// Gets the actual lat and long
				double markerLat = mapSet.getKey().getLatitude();
				double markerLong = mapSet.getKey().getLongitude();

				locDest.setLatitude(markerLat);
				locDest.setLongitude(markerLong);

				// Calculates the distance
				float distanceAct = locStart.distanceTo(locDest);
				float distanceTotal = distanceAct
						+ locStart.distanceTo(locDest);
				distanceTotal = distanceTotal * 1000;

				// Calculates the distance from km to meter
				distanceMeter = (int) Math.round(distanceTotal);

				// Sets the "old" lat and long as new start lat and long
				locStart.setLatitude(markerLat);
				locStart.setLongitude(markerLong);

				// Gets the actual timestamp
				long markerDate = mapSet.getKey().getTimestamp().getTime();

				// Calculates the duration of the route
				// ENDGÜLTIGE
				// ZEIT*************************************************************
				durationAct = (markerDate - startDate);

				// altitude total
				altitudeAct = mapSet.getKey().getAltitude();
				altitudeTotal = altitudeAct + altitudeOld;

				// max altitude
				if (altitudeAct > altitudeOld) {
					maxAltitude = altitudeAct;
				}

				// min altitude
				if (altitudeAct < altitudeOld) {
					minAltitude = altitudeAct;
				}

				peakAltitude = maxAltitude - minAltitude;

				altitudeOld = altitudeAct;

				polylineOptions_top.add(new LatLng(markerLat, markerLong));
				polylineOptions_back.add(new LatLng(markerLat, markerLong));

				if (mapSet.getKey().getPicturePreview() != null) {

					Bitmap bitmapSaved = bitmapMap.get(mapSet.getKey());

					Bitmap background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.custom_marker);

					Bitmap resizedBitmap_Placeholder = BitmapFactory
							.decodeResource(context.getResources(),
									R.drawable.resizedbitmap_placeholder);

					markerOpt = new MarkerOptions()
							.position(
									new LatLng(mapSet.getKey().getLatitude(),
											mapSet.getKey().getLongitude()))
							.icon(BitmapDescriptorFactory.fromBitmap(this
									.overlay(background,
											resizedBitmap_Placeholder,
											bitmapSaved)))
							.title("Ihr aktueller Standort");

					mapSet.setValue(map.addMarker(markerOpt));

					bitmapSaved.recycle();
					background.recycle();
					resizedBitmap_Placeholder.recycle();

				}

				builder.include(mapSet.getValue().getPosition());

			}

			// Formats the Duration from Miliseconds to an readable format
			// ENDGÜLTIGE
			// STRECKE*************************************************************
			Format formatter = new SimpleDateFormat("DD HH:mm:ss");
			String duration = formatter.format(durationAct);

			Polyline polyline_top = map.addPolyline(polylineOptions_top);
			Polyline polyline_back = map.addPolyline(polylineOptions_back);

			LatLngBounds bounds = builder.build();
			CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(
					bounds, 60);
			map.moveCamera(camUpdate);
			addMarkerClickListener(map);
		}
		hashMapRef.clear();
		markerMap.clear();
		bitmapMap.clear();

	}

	private void addMarkerClickListener(GoogleMap map) {
		// TODO Auto-generated method stub

		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				for (Map.Entry<RoutePoint, Marker> mapSet : markerMap
						.entrySet()) {

					Marker markerSaved = mapSet.getValue();
					if (markerSaved.hashCode() == marker.hashCode()) {

						mCallback.onPictureClick(route, mapSet.getKey());

					}

				}

				return false;
			}

		});

	}

	public static Bitmap getResizedBitmap(Bitmap image, int bgheight,
			int bgwidth) {

		int width = image.getWidth();
		int height = image.getHeight();
		int newWidth = bgwidth;
		int newHeight = bgheight;

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create matrix for the manipulation
		Matrix matrix = new Matrix();

		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap

		return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);

	}

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
