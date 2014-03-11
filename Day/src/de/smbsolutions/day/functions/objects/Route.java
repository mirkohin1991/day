package de.smbsolutions.day.functions.objects;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

	private static final long serialVersionUID = 1L;
	private ArrayList<RoutePoint> routePoints = new ArrayList<RoutePoint>();
	private String routeName;
	private String date;
	private GoogleMap map;
	private boolean active;
	private int id;

	private PolylineOptions polylineOptions = new PolylineOptions();
	private PolylineOptions polylineOptions_back = new PolylineOptions().width(
			3).color(Color.rgb(136, 204, 0));
	private PolylineOptions polylineOptions_top = new PolylineOptions()
			.width(8).color(Color.rgb(19, 88, 5));

	public LinkedHashMap<RoutePoint, Marker> markerMap;

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
		active = true;

		// If the Database insert fails, the active flag is deleted
		if (Database.createNewRoute(this) != true) {
			this.active = false;

			// Was soll noch passieren?
			// Z.B. hat die Klasse dann die neue ID, die es in der DB
			// noch garnicht gibt
		}

	}

	public void closeRoute() {

		if (Database.closeRoute(id) == true) {
			active = false;
		}

	}

	public void addRoutePointDB(RoutePoint point) {

		if (active == true) {

			if (Database.addNewRoutePoint(point) == true) {

				routePoints.add(point);

			}

		}

	}

	public void addRoutePoint(RoutePoint point) {
		routePoints.add(point);
	}

	public GoogleMap prepareMapPreview(final GoogleMap mapImport) {

		// saving the map
		this.map = mapImport;

		Bitmap bitmap = null;
		map.clear();
		// Necessary to save in order to connect timestamp and marker
		markerMap = new LinkedHashMap<RoutePoint, Marker>();

		for (RoutePoint point : this.routePoints) {
			polylineOptions_back.add(new LatLng(point.getLatitude(), point
					.getLongitude()));
			polylineOptions_top.add(new LatLng(point.getLatitude(), point
					.getLongitude()));
			MarkerOptions markerOpt = new MarkerOptions().position(
					new LatLng(point.getLatitude(), point.getLongitude()))
					.title(getRouteName());

			Marker marker = map.addMarker(markerOpt);
			markerMap.put(point, marker);

		}

		Polyline polyline_top = map.addPolyline(polylineOptions_top);
		Polyline polyline_back = map.addPolyline(polylineOptions_back);

		// Setting the zoom
		setZoomAllMarkers();
		return map;

	}

	@SuppressWarnings("unchecked")
	public GoogleMap prepareMapDetails(final GoogleMap mapImport,
			Context context) {

		// saving the map
		this.map = mapImport;
		map.clear();
		// Intern speichern, damit der Action Listener unten anspringen kann

		// Necessary to save connect timestamp and marker
		if (markerMap != null) {
			markerMap.clear();
		} else {
			markerMap = new LinkedHashMap<RoutePoint, Marker>();
		}

		PolylineOptions polylineOptions = new PolylineOptions();

		// add markers to map
		if (hasPicturePoint()) {
			map.clear();
			for (RoutePoint point : this.routePoints) {

				MarkerOptions markerOpt = new MarkerOptions().position(
						new LatLng(point.getLatitude(), point.getLongitude()))
						.title(getRouteName());

				Marker marker = map.addMarker(markerOpt);
				markerMap.put(point, marker);

			}

			final MarkerWorkerTask task = new MarkerWorkerTask(map, markerMap,
					this, context);
			task.execute(this.routePoints);

		} else {

			for (RoutePoint point : this.routePoints) {

				polylineOptions_back.add(new LatLng(point.getLatitude(), point
						.getLongitude()));
				polylineOptions_top.add(new LatLng(point.getLatitude(), point
						.getLongitude()));
				polylineOptions.add(new LatLng(point.getLatitude(), point
						.getLongitude()));

				MarkerOptions markerOpt = new MarkerOptions().position(
						new LatLng(point.getLatitude(), point.getLongitude()))
						.title(getRouteName());

				Marker marker = map.addMarker(markerOpt);
				markerMap.put(point, marker);

			}
			map.addPolyline(polylineOptions_top);
			map.addPolyline(polylineOptions_back);
			setZoomAllMarkers();

		}
		return map;

	}

	// PERPARE MAP HAS TO BE CALLED BEFORE!
	private void setZoomAllMarkers() {
		// zoompoint
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (Map.Entry<RoutePoint, Marker> mapSet : markerMap.entrySet()) {

			builder.include(mapSet.getValue().getPosition());

		}

		LatLngBounds bounds = builder.build();
		CameraUpdate camUpdate = CameraUpdateFactory
				.newLatLngBounds(bounds, 60);

		map.animateCamera(camUpdate);
	}

	// Method to set the zoom of the map to a certain point
	public void setZoomSpecificMarker(RoutePoint point) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		// Getting the marker for the routepoint
		// There is no way to access the KEY via VALUE directly

		LatLng latlng = markerMap.get(point).getPosition();

		if (latlng != null) {

			builder.include(latlng);

			LatLngBounds bounds = builder.build();
			CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(
					bounds, 60);
			map.moveCamera(camUpdate);

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

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
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

	public void deletePictureDB(RoutePoint deletePoint) {

		if (Database.deletePicturePath(deletePoint) == true) {
			routePoints.remove(deletePoint);
		}

	}

}
