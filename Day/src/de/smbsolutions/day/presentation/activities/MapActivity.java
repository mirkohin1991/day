package de.smbsolutions.day.presentation.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.database.RoutePoint;
import de.smbsolutions.day.functions.location.GPSTracker;
import de.smbsolutions.day.presentation.popups.RouteNameDialog;

public class MapActivity extends Activity {

	public Database db_data;
	private HashMap<Integer, Timestamp> markerMap = new HashMap<Integer, Timestamp>();

	private GPSTracker gps;
	private GoogleMap map;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cr_fragment);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		double latitude = 0, longitude = 0;

		// Aktuelle Position
		map.setMyLocationEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		PolylineOptions rectOptions = new PolylineOptions();

		for (RoutePoint element : Database

		// Zunächst einmal immer die letzte Route. Später muss das dann von
		// jeweils gedrückten Button kommen.
				.getSpecificRoute(new String[] { String.valueOf(Database
						.getCurrentRouteID()) })) {

			Bitmap bitmap = null;
			if (!(element.getPicture() == null)) {
				Uri uri = Uri.parse(element.getPicture());

				try {
					bitmap = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), uri);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (bitmap != null) {
				// Bitmap.Config conf = Bitmap.Config.ARGB_8888;
				// Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
				// Canvas canvas = new Canvas(bitmap);
				//
				// // paint defines the text color,
				// // stroke width, size
				// Paint color = new Paint();
				// color.setTextSize(35);
				// color.setColor(Color.BLACK);
				//
				// //modify canv
				// canvas.drawBitmap(bitmap, 0,0, color);
				// canvas.drawText("User Name!", 30, 40, color);
				//
				// //add marker to Map
				// map.addMarker(new MarkerOptions().position(new
				// LatLng(element.getLatitude(), element.getLongitude()))
				// .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
				// .anchor(0.5f, 1)); //Specifies the anchor to be
				// //at a particular point in the marker image.

				bitmap = getResizedBitmap(bitmap, 80, 80);
				MarkerOptions marker = new MarkerOptions()
						.position(
								new LatLng(element.getLatitude(), element
										.getLongitude()))
						.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
						.title("Ihr aktueller Standort");

				marker.hashCode();

				rectOptions.add(new LatLng(element.getLatitude(), element
						.getLongitude()));

				int code = map.addMarker(marker).hashCode();

				markerMap.put(code, element.getTimestamp());

				// no image
			} else {

				rectOptions.add(new LatLng(element.getLatitude(), element
						.getLongitude()));

				MarkerOptions marker = new MarkerOptions().position(
						new LatLng(element.getLatitude(), element
								.getLongitude()))

				.title("Ihr aktueller Standort");

				int code = map.addMarker(marker).hashCode();

			}

			longitude = element.getLongitude();
			latitude = element.getLatitude();

		}

		Polyline polyline = map.addPolyline(rectOptions);

		// \n is for new line
		// Toast.makeText(getApplicationContext(),
		// "Your Location is - \nLat: " + actLatitude() + "\nLong: " +
		// actLongitude(), Toast.LENGTH_LONG).show();

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(latitude, longitude)).zoom(15).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				int code = marker.hashCode();

				markerMap.containsValue(marker);
				Timestamp timestamp = markerMap.get(marker.hashCode());
				Intent intent = new Intent(MapActivity.this,
						PictureActivity.class);
				intent.putExtra("timestamp", timestamp.toString());
				startActivity(intent);
				return false;
			}
		});

		// gps = new GPSTracker(UnterActivity.this);
		// if(gps.canGetLocation()){
		//
		// double latitude = gps.getLatitude();
		// double longitude = gps.getLongitude();
		// MarkerOptions marker = new MarkerOptions().position(new
		// LatLng(latitude, longitude)).title("Ihr aktueller Standort");
		// MarkerOptions marker2 = new MarkerOptions().position(new
		// LatLng(latitude + 0.001, longitude -
		// 0.005)).title("Ihr aktueller Standort");
		// map.addMarker(marker);
		// map.addMarker(marker2);
		// CameraPosition cameraPosition = new CameraPosition.Builder().target(
		// new LatLng(latitude, longitude)).zoom(15).tilt(90).build();
		//
		//
		// map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		// map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		// Polyline line = map.addPolyline(new PolylineOptions()
		// .add(new LatLng(latitude, longitude), new LatLng(latitude + 0.001,
		// longitude - 0.005))
		// .width(5)
		// .color(Color.BLUE));
		// // \n is for new line
		// Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
		// + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
		// }else{
		// // can't get location
		// // GPS or Network is not enabled
		// // Ask user to enable GPS/network in settings
		// gps.showSettingsAlert();
		// }

		LinearLayout myGallery = (LinearLayout) findViewById(R.id.LinearLayoutcR);
		String targetPath = "/storage/emulated/0/Pictures/MyCameraApp/";

		Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG)
				.show();
		File targetDirector = new File(targetPath);

		File[] files = targetDirector.listFiles();
		for (File file : files) {
			myGallery.addView(insertPhoto(file.getAbsolutePath()));
			
		}
	
//		for (int i = 0; i < myGallery.getChildCount(); i++) {
//			final ImageView imgView = (ImageView) myGallery.getChildAt(i);
//			imgView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
////				int test  = imgView.getId();
////				
////					Drawable drawable= (Drawable) imgView.getDrawable();
////					Bitmap bm = BitmapFactory.decodeResource(rsrc, drawable, drawable.)
////					Toast.makeText(this, imgView.getDrawable().gte, duration)
//				}
//			});
//		}

	}

	View insertPhoto(String path) {
		Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);

		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(250, 250));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
		
	}

	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {
		Bitmap bm = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	public int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.imageButton1:
			Intent cam = new Intent(this, KameraActivity.class);
			startActivity(cam);
			break;

		default:
			break;
		}
	}

	public static Bitmap getResizedBitmap(Bitmap image, int newHeight,
			int newWidth) {
		int width = image.getWidth();
		int height = image.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

}