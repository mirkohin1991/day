package de.smbsolutions.day.functions.objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

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
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.presentation.activities.PictureActivity;

public class Route implements Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<RoutePoint> routePoints = new ArrayList<RoutePoint>();
	private String routeName;
	private String date;
	private GoogleMap map;
	private String active;
	private int id;

	private Context context;
	private HashMap<Marker, Timestamp> markerMap;

	// Constructor for routes that have already been created
	public Route() {

	}

	// constructor for new routes
	// --> routeName and date are required
	public Route(String routeName) {

		Date currentDate = Calendar.getInstance().getTime();
		String today = new SimpleDateFormat("dd/MM/yyyy").format(currentDate);

		this.routeName = routeName;
		// Get the last route id and 1 to get the new id
		id = Database.getlastRouteID() + 1;
		date = today;
		active = "X";

		// If the Database insert fails, the active flag is deleted
		if (Database.createNewRoute(this) != true) {
			this.active = "";

			// Was soll noch passieren?
			// Z.B. hat die Klasse dann die neue ID, die es in der DB
			// noch garnicht gibt
		}

	}

	public void closeRoute() {

		if (Database.closeRoute(id) == true) {
			active = "";
		}

	}

	public void addRoutePointDB(RoutePoint point) {

		if (active.equals("X")) {

			if (Database.addNewRoutePoint(point) == true) {

				routePoints.add(point);

			}

		}

	}

	public void addRoutePoint(RoutePoint point) {
		routePoints.add(point);
	}

	public GoogleMap prepareMap(final GoogleMap map, final Context context,
			boolean details) {

		// Intern speichern, damit der Action Listener unten anspringen kann
		this.context = context;

		List<Marker> markers = new ArrayList<Marker>();

		Bitmap bitmap = null;

		// Necessary to save connect timestamp and marker
		markerMap = new HashMap<Marker, Timestamp>();

		PolylineOptions polylineOptions = new PolylineOptions();

		// add markers to map

		for (RoutePoint point : this.routePoints) {
		
				if (point.getPicture() != null && details == true ) {
					File pic = new File(point.getPicture());
					Uri uri = Uri.fromFile(pic);

					try {
						bitmap = MediaStore.Images.Media.getBitmap(
								context.getContentResolver(), uri);

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				

				// A image is available and it shall be displayed (details =
				// true)
				if (bitmap != null && details == true) {
					
					Bitmap background = BitmapFactory.decodeResource(context.getResources(), R.drawable.custom_marker);

                	Bitmap resizedBitmap_Placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.resizedbitmap_placeholder);
                	
 

                			
					int bgwidth = resizedBitmap_Placeholder.getWidth();
					int bgheight = resizedBitmap_Placeholder.getHeight();

                	

					bitmap = getResizedBitmap(bitmap, bgheight, bgwidth);
					MarkerOptions markerOpt = new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude()))
							.icon(BitmapDescriptorFactory.fromBitmap(this.overlay(background, resizedBitmap_Placeholder, bitmap)))
							.title("Ihr aktueller Standort");
//					
//					bitmap = getResizedBitmap(bitmap, 80, 80);
//
//					MarkerOptions markerOpt = new MarkerOptions()
//							.position(
//									new LatLng(point.getLatitude(), point
//											.getLongitude()))
//							.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//							.title("Ihr aktueller Standort");

					polylineOptions.add(new LatLng(point.getLatitude(), point
							.getLongitude()));

					// adding the marker and storing its hashcode to identify it
					// later on
					Marker marker = map.addMarker(markerOpt);
					int code = marker.hashCode();
					markerMap.put(marker, point.getTimestamp());
					markers.add(marker);

					// no image
				}
			} else {

				polylineOptions.add(new LatLng(point.getLatitude(), point
						.getLongitude()));

				MarkerOptions markerOpt = new MarkerOptions().position(
						new LatLng(point.getLatitude(), point.getLongitude()))

				.title("Ihr aktueller Standort");

				Marker marker = map.addMarker(markerOpt);
				markerMap.put(marker, point.getTimestamp());
				markers.add(marker);

			}

		}

		Polyline polyline = map.addPolyline(polylineOptions);

		// zoompoint
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (Map.Entry<Marker, Timestamp> mapSet : markerMap.entrySet()) {

			builder.include(mapSet.getKey().getPosition());

		}

		// for (Marker marker : markers) {
		// builder.include(marker.getPosition());
		// }

		LatLngBounds bounds = builder.build();
		CameraUpdate camUpdate = CameraUpdateFactory
				.newLatLngBounds(bounds, 60);
		map.animateCamera(camUpdate);
		// polyline
		// bilder

		if (details == true) {

			map.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {

					// int code = marker.hashCode();
					// markerMap.containsValue(marker);

					Timestamp timestamp = markerMap.get(marker);

					// timestamp is null when the marker doesn't contain a
					// picture
					if (timestamp != null) {
						Intent intent = new Intent(context,
								PictureActivity.class);
						intent.putExtra("timestamp", timestamp.toString());
						context.startActivity(intent);

					}
					return false;
				}

			});

		}

		return map;

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
        
        
 
        
        
//        rotate bitmap
//        if (height > width){
//            matrix.postRotate(90);
//            }
        
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0,
                          width, height, matrix, true);

		return resizedBitmap;		
	}
	
	
	
	public static Bitmap overlay(Bitmap background, Bitmap resizedBitmap_Placeholder, Bitmap resizedBitmap) {
		Bitmap bmOverlay = Bitmap.createBitmap(background.getWidth(), background.getHeight(), background.getConfig());
        Bitmap bmOverlay2 = Bitmap.createBitmap(resizedBitmap.getWidth(), resizedBitmap.getHeight(), resizedBitmap.getConfig());
		
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(resizedBitmap, 7, 7, null);  
		canvas.drawBitmap(background, 0, 0, null);
        
        
        
        return bmOverlay;
    }
	

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<RoutePoint> getRoutePoints() {
		return routePoints;
	}

	public void setRoutePoints(List<RoutePoint> routePoints) {
		this.routePoints = routePoints;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
