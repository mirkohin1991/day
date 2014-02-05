package de.smbsolutions.day.presentation.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

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
	private HashMap < Integer, Timestamp> markerMap= new HashMap< Integer, Timestamp>();

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

	
		

			PolylineOptions rectOptions = new PolylineOptions();

			for (RoutePoint element : Database
					
					//Zunächst einmal immer die letzte Route. Später muss das dann von jeweils gedrückten Button kommen.
					.getSpecificRoute(new String[] { String.valueOf(Database.getCurrentRouteID())})) {
				
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
//					 Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//					    Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
//					    Canvas canvas = new Canvas(bitmap);
//
//					    // paint defines the text color,
//					    // stroke width, size
//					    Paint color = new Paint();
//					    color.setTextSize(35);
//					    color.setColor(Color.BLACK);
//
//					    //modify canv
//					    canvas.drawBitmap(bitmap, 0,0, color);
//					    canvas.drawText("User Name!", 30, 40, color);
//
//					    //add marker to Map
//					    map.addMarker(new MarkerOptions().position(new LatLng(element.getLatitude(), element.getLongitude()))
//					    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//					    .anchor(0.5f, 1)); //Specifies the anchor to be
//					               //at a particular point in the marker image.
					
					
					    
					
					bitmap = getResizedBitmap(bitmap, 80, 80);
					MarkerOptions marker = new MarkerOptions().position(new LatLng(element.getLatitude(), element.getLongitude()))
							.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
							.title("Ihr aktueller Standort");
					
					marker.hashCode();
					

					
							
					
					rectOptions.add(new LatLng(element.getLatitude(), element
							.getLongitude()));
					
					int code = map.addMarker(marker).hashCode();
					
					
					markerMap.put( code, element.getTimestamp());
					
					
				// no image
				} else {
					
					rectOptions.add(new LatLng(element.getLatitude(), element
							.getLongitude()));
					
					MarkerOptions marker = new MarkerOptions().position(new LatLng(element.getLatitude(), element.getLongitude()))
							
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
		map.setOnMarkerClickListener( new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				
				int code = marker.hashCode();
				
				markerMap.containsValue(marker);
				Timestamp timestamp = markerMap.get(marker.hashCode());
				
				Intent intent = new Intent(MapActivity.this, PictureActivity.class);
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

	}

	public void onButtonClick(View view){
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