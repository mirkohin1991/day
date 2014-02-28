package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class MarkerWorkerTask extends
		AsyncTask<ArrayList<RoutePoint>, Void, HashMap<Bitmap, RoutePoint>> {

	List<Marker> markers = new ArrayList<Marker>();
	private GoogleMap map;
	private Context context;
	private List<RoutePoint> routePoints;
	MarkerOptions markerOpt = new MarkerOptions();

	// Necessary to save connect timestamp and marker
	HashMap<Bitmap, RoutePoint> markerMap = new HashMap<Bitmap, RoutePoint>();

	PolylineOptions polylineOptions = new PolylineOptions();

	public MarkerWorkerTask(Context context, GoogleMap map) {
		markers = new ArrayList<Marker>();
		this.context = context;
		this.map = map;
	}

	@Override
	protected HashMap<Bitmap, RoutePoint> doInBackground(
			ArrayList<RoutePoint>... params) {
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		Bitmap bitmap = null;
		routePoints = params[0];

		for (RoutePoint point : routePoints) {
			if (point.getPicture() != null) {
				File pic = new File(point.getPicture());
				Uri uri = Uri.fromFile(pic);
				Bitmap resizedBitmap_Placeholder = BitmapFactory
						.decodeResource(context.getResources(),
								R.drawable.resizedbitmap_placeholder);

				int bgwidth = resizedBitmap_Placeholder.getWidth();
				int bgheight = resizedBitmap_Placeholder.getHeight();

				bitmap = BitmapManager.decodeSampledBitmapFromUri(
						uri.getPath(), bgwidth, bgheight);

				if (bitmap != null) {
					bitmap = getResizedBitmap(bitmap, bgheight, bgwidth);
					markerMap.put(bitmap, point);

				}

			}

		}

		return markerMap;
	}

	@Override
	protected void onPostExecute(HashMap<Bitmap, RoutePoint> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Map.Entry<Bitmap, RoutePoint> mapSet : result.entrySet()) {

			polylineOptions.add(new LatLng(mapSet.getValue().getLatitude(),
					mapSet.getValue().getLongitude()));
			Polyline polyline = map.addPolyline(polylineOptions);
			polyline.setColor(Color.rgb(136, 204,0));
			Bitmap background = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.custom_marker);

			Bitmap resizedBitmap_Placeholder = BitmapFactory.decodeResource(
					context.getResources(),
					R.drawable.resizedbitmap_placeholder);
			markerOpt = new MarkerOptions()
					.position(
							new LatLng(mapSet.getValue().getLatitude(), mapSet
									.getValue().getLongitude()))
					.icon(BitmapDescriptorFactory.fromBitmap(this.overlay(
							background, resizedBitmap_Placeholder,
							mapSet.getKey()))).title("Ihr aktueller Standort");

			Marker marker = map.addMarker(markerOpt);
			builder.include(marker.getPosition());

		}

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

		// rotate bitmap
		// if (height > width){
		// matrix.postRotate(90);
		// }

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
				matrix, true);

		return resizedBitmap;
	}

	public static Bitmap overlay(Bitmap background,
			Bitmap resizedBitmap_Placeholder, Bitmap resizedBitmap) {
		Bitmap bmOverlay = Bitmap.createBitmap(background.getWidth(),
				background.getHeight(), background.getConfig());
		Bitmap bmOverlay2 = Bitmap.createBitmap(resizedBitmap.getWidth(),
				resizedBitmap.getHeight(), resizedBitmap.getConfig());

		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(resizedBitmap, 7, 7, null);
		canvas.drawBitmap(background, 0, 0, null);

		return bmOverlay;
	}
}
