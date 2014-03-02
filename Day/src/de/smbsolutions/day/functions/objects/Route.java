package de.smbsolutions.day.functions.objects;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.tasks.MarkerWorkerTask;

public class Route implements Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<RoutePoint> routePoints = new ArrayList<RoutePoint>();
	private String routeName;
	private String date;
	private GoogleMap map;
	private String active;
	private int id;

	private Context context;
	public HashMap<Marker, Timestamp> markerMap;

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
	

	public GoogleMap prepareMapPreview(final GoogleMap mapImport, final Context context
			) {
		
		//saving the map
		this.map = mapImport;

		// Intern speichern, damit der Action Listener unten anspringen kann
		this.context = context;

		Bitmap bitmap = null;

		// Necessary to save in order to connect timestamp and marker
		markerMap = new HashMap<Marker, Timestamp>();

		PolylineOptions polylineOptions = new PolylineOptions();

			for (RoutePoint point : this.routePoints) {
				polylineOptions.add(new LatLng(point.getLatitude(), point
						.getLongitude()));
				MarkerOptions markerOpt = new MarkerOptions().position(
						new LatLng(point.getLatitude(), point.getLongitude()))
				.title(getRouteName());

				Marker marker = map.addMarker(markerOpt);
				markerMap.put(marker, point.getTimestamp());
				
			}

	
			Polyline polyline = map.addPolyline(polylineOptions);
			polyline.setColor(Color.rgb(136, 204,0));
						
			//Setting the zoom
			setZoomAllMarkers();


		return map;
		

	}

	@SuppressWarnings("unchecked")
	public GoogleMap prepareMapDetails(final GoogleMap mapImport, final Context context
			) {
		
		
		//saving the map
		this.map = mapImport;

		// Intern speichern, damit der Action Listener unten anspringen kann
		this.context = context;


		// Necessary to save connect timestamp and marker
		markerMap = new HashMap<Marker, Timestamp>();

		PolylineOptions polylineOptions = new PolylineOptions();
		
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (RoutePoint point : this.routePoints) {
					polylineOptions.add(new LatLng(point.getLatitude(), point
							.getLongitude()));
					
					MarkerOptions markerOpt = new MarkerOptions().position(
							new LatLng(point.getLatitude(), point.getLongitude()))
					.title(getRouteName());

					Marker marker = map.addMarker(markerOpt);
					markerMap.put(marker, point.getTimestamp());

				}
				Polyline polyline = map.addPolyline(polylineOptions);
				polyline.setColor(Color.rgb(136, 204,0));
//				LatLngBounds bounds = builder.build();
//				CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(
//						bounds, 60);
//				map.animateCamera(camUpdate)
				
//				
				
				// add markers to map
				if (hasPicturePoint()) {
				
				// Unsaubere Lösung! Oben alle hingefügt, jetzt wieder gelöscht	
			   //  map.clear();
				
				MarkerWorkerTask task = new MarkerWorkerTask(context, map, markerMap);
				task.execute(this.routePoints);
				
					
				} else {
					//Setting the zoom
 				setZoomAllMarkers();
					
				}

		
			map.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {

					Timestamp timestamp = markerMap.get(marker);

					// timestamp is null when the marker doesn't contain a
					// picture
					if (timestamp != null) {
						// Intent intent = new Intent(context,
						// PictureActivity.class);
						// intent.putExtra("timestamp", timestamp.toString());
						// context.startActivity(intent);

					}
					return false;
				}

			});

	
		return map;

	}

	
	//PERPARE MAP HAS TO BE CALLED BEFORE!
	private void setZoomAllMarkers() {
		// zoompoint
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (Map.Entry<Marker, Timestamp> mapSet : markerMap.entrySet()) {

			builder.include(mapSet.getKey().getPosition());

		}

		LatLngBounds bounds = builder.build();
		CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(
				bounds, 60);
		map.animateCamera(camUpdate);
	}
	
	
	
	// Method to set the zoom of the map to a certain point
	public void setZoomSpecificMarker (RoutePoint point) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		
		//Getting the marker for the routepoint
	    // There is no way to access the KEY via VALUE directly
		for (Map.Entry<Marker, Timestamp> mapSet : markerMap.entrySet()) {
			
			if (mapSet.getValue() == point.getTimestamp()) {
				
				builder.include(mapSet.getKey().getPosition());
				
				//leaving the for each loop
				//HAS OT BE PROVED!
				
			}
			
			LatLngBounds bounds = builder.build();
			CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(
					bounds, 60);
			map.animateCamera(camUpdate);
			

		}
		
	
		
		
	}

	public boolean hasPicturePoint() {

		for (RoutePoint point : routePoints) {
			// As soon as one point contains a picture path, true is returned
			if ((point.getPicture() != null)) {
				return true;
			}
		}
		return false;

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

	public ArrayList<RoutePoint> getRoutePoints() {
		return routePoints;
	}

	public void setRoutePoints(ArrayList<RoutePoint> routePoints) {
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
	
	public void deletePictureDB (RoutePoint deletePoint) {
		
//		for (RoutePoint routePoint : routePoints) {
//			
//			if (routePoint.getTimestamp() == deletePoint.getTimestamp() ) {
				
				
				if(Database.deletePicturePath(deletePoint) == true){
				routePoints.remove(deletePoint);
				}
				
//			}
			
//		}
		
	}

	public HashMap<Marker, Timestamp> getMarkerMap() {
		return markerMap;
	}

}
