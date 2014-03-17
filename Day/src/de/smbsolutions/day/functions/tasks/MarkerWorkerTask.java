package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

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
	private ArrayList<RoutePoint> routePoints;
	private Route route;

	MarkerOptions markerOpt = new MarkerOptions();
	private final WeakReference<LinkedHashMap<RoutePoint, Marker>> hashMapRef;
	private WeakReference<Bitmap> weakBM;
	// Necessary to save connect timestamp and marker
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

				// vielleicht eierverursacher!!!
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
		hashMapRef.clear();
		// markerMap.clear();
		bitmapMap.clear();
		context = null;

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
