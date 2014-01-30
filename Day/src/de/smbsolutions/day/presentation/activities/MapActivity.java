package de.smbsolutions.day.presentation.activities;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.database.RoutePoint;
import de.smbsolutions.day.functions.location.GPSTracker;

public class MapActivity extends Activity {

	public Database db_data;

	private GPSTracker gps;
	private GoogleMap map;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		double latitude = 0, longitude = 0;

		// Aktuelle Position
		map.setMyLocationEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		// Kartenart
		// map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		gps = new GPSTracker(MapActivity.this);
		if (gps.canGetLocation()) {

			PolylineOptions rectOptions = new PolylineOptions();

			for (RoutePoint element : Database
					.getSpecificRoute(new String[] { "1" })) {

				Uri uri = Uri.parse(element.getPicture());
				Bitmap bitmap = null;
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
				if (bitmap != null) {
					bitmap = getResizedBitmap(bitmap, 5, 5);
//					MarkerOptions marker = new MarkerOptions().position(new LatLng(element.getLatitude(), element.getLongitude()))
//							.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//							.title("Ihr aktueller Standort");
					
					MarkerOptions marker = new MarkerOptions().position(new LatLng(element.getLatitude(), element.getLongitude()))
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
							
					
					rectOptions.add(new LatLng(element.getLatitude(), element
							.getLongitude()));
					map.addMarker(marker);

				}

				longitude = element.getLongitude();
				latitude = element.getLatitude();

			}

			Polyline polyline = map.addPolyline(rectOptions);

			// \n is for new line
			// Toast.makeText(getApplicationContext(),
			// "Your Location is - \nLat: " + actLatitude() + "\nLong: " +
			// actLongitude(), Toast.LENGTH_LONG).show();
		} else {
			gps.showSettingsAlert();
		}
		gps.stopUsingGPS();

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(latitude, longitude)).zoom(15).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

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