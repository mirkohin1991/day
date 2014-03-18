package de.smbsolutions.day.functions.objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.tasks.MarkerWorkerTask;

public class Route implements Parcelable {

	private static final long serialVersionUID = 1L;
	private CopyOnWriteArrayList<RoutePoint> routePoints = new CopyOnWriteArrayList<RoutePoint>();
	private String routeName;
	private String date;
	private boolean active;
	private int id;

	PolylineOptions polylineOptions_back;
	PolylineOptions polylineOptions_top;

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

		mapImport.clear();

		addPolylinesPreview(mapImport);
		// Setting the zoom
		setZoomAllMarkers(mapImport);

		return mapImport;

	}

	public GoogleMap prepareMapDetails(final GoogleMap mapImport,
			Context context) {

		// Necessary to save connect timestamp and marker
		if (markerMap != null) {
			markerMap.clear();
		} else {
			markerMap = new LinkedHashMap<RoutePoint, Marker>();
		}

		// add markers to map
		if (hasPicturePoint()) {
			for (RoutePoint point : this.routePoints) {

				MarkerOptions markerOpt = new MarkerOptions().position(
						new LatLng(point.getLatitude(), point.getLongitude()))
						.title(getRouteName());

				Marker marker = mapImport.addMarker(markerOpt);
				markerMap.put(point, marker);

			}
			mapImport.clear();

			addPolylinesDetail(mapImport);

			MarkerWorkerTask task = new MarkerWorkerTask(mapImport, markerMap,
					this, context);
			task.execute(this.routePoints);

		} else {

			addPolylinesDetail(mapImport);
			setZoomAllMarkers(mapImport);

		}
		return mapImport;

	}

	// PERPARE MAP HAS TO BE CALLED BEFORE!
	private void setZoomAllMarkers(GoogleMap map) {
		// zoompoint
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		// JETZT DIREKT ÜBER POLYLINES --> MAN MUSS NICHTMEHR ALLE MARKER
		// speichern
		for (LatLng point : polylineOptions_top.getPoints()) {
			builder.include(point);
		}

		LatLngBounds bounds = builder.build();
		CameraUpdate camUpdate = CameraUpdateFactory
				.newLatLngBounds(bounds, 60);

		map.animateCamera(camUpdate);
	}

	// Method to set the zoom of the map to a certain point
	public void setZoomSpecificMarker(RoutePoint point, GoogleMap map) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		// Getting the marker for the routepoint
		// There is no way to access the KEY via VALUE directly

		LatLng latlng = markerMap.get(point).getPosition();

		if (latlng != null) {

			builder.include(latlng);

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

	public CopyOnWriteArrayList<RoutePoint> getRoutePoints() {
		return routePoints;
	}

	public void setRoutePoints(CopyOnWriteArrayList<RoutePoint> routePoints) {
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

	public void freeObjects() {

		routePoints.clear();
		routePoints = null;
		markerMap.clear();
		markerMap = null;

	}

	public void addPolylinesDetail(GoogleMap map) {
		polylineOptions_back = new PolylineOptions().width(3).color(
				Color.rgb(123, 207, 168));
		polylineOptions_top = new PolylineOptions().width(8).color(
				Color.rgb(19, 88, 5));
		for (RoutePoint point : this.routePoints) {
			polylineOptions_back.add(new LatLng(point.getLatitude(), point
					.getLongitude()));
			polylineOptions_top.add(new LatLng(point.getLatitude(), point
					.getLongitude()));

			if (hasPicturePoint() == false) {

				// Erster RoutePoint --> Flagge
				if (point == routePoints.get(0)) {
					MarkerOptions markerOpt = new MarkerOptions()
							.position(
									new LatLng(point.getLatitude(), point
											.getLongitude()))
							.title(getRouteName())
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.start_stop_marker2));

					Marker marker = map.addMarker(markerOpt);
					markerMap.put(point, marker);
				}

				// Letzter Eintrag --> Flagge (
				if (point == routePoints.get(routePoints.size() - 1)) {

					// Nur wenn die Route schon beendet ist, soll das Finish
					// Icon erscheinen
					if (active == false) {

						MarkerOptions markerOpt = new MarkerOptions()
								.position(
										new LatLng(point.getLatitude(), point
												.getLongitude()))
								.title(getRouteName())
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.start_stop_marker2));

						Marker marker = map.addMarker(markerOpt);
						markerMap.put(point, marker);

					}

				}
			}

		}
		map.addPolyline(polylineOptions_top);
		map.addPolyline(polylineOptions_back);

	}

	public void addPolylinesPreview(GoogleMap map) {
		polylineOptions_back = new PolylineOptions().width(3).color(
				Color.rgb(123, 207, 168));
		polylineOptions_top = new PolylineOptions().width(8).color(
				Color.rgb(19, 88, 5));
		for (RoutePoint point : this.routePoints) {
			polylineOptions_back.add(new LatLng(point.getLatitude(), point
					.getLongitude()));
			polylineOptions_top.add(new LatLng(point.getLatitude(), point
					.getLongitude()));

			// Erster RoutePoint --> Flagge
			if (point == routePoints.get(0)) {
				MarkerOptions markerOpt = new MarkerOptions()
						.position(
								new LatLng(point.getLatitude(), point
										.getLongitude()))
						.title(getRouteName())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.start_stop_marker2));

				Marker marker = map.addMarker(markerOpt);
				// markerMap.put(point, marker);
			}

			// Letzter Eintrag --> Flagge (
			if (point == routePoints.get(routePoints.size() - 1)) {

				// Nur wenn die Route schon beendet ist, soll das Finish Icon
				// erscheinen
				if (active == false) {

					MarkerOptions markerOpt = new MarkerOptions()
							.position(
									new LatLng(point.getLatitude(), point
											.getLongitude()))
							.title(getRouteName())
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.start_stop_marker2));

					Marker marker = map.addMarker(markerOpt);
					// markerMap.put(point, marker);

				}

			}

		}
		map.addPolyline(polylineOptions_top);
		map.addPolyline(polylineOptions_back);

	}

	public void addPoint2Polyline(RoutePoint point, GoogleMap map) {

		// map darf nicht gecleared werden, da sonst Marker gelöscht werden
		polylineOptions_back.add(new LatLng(point.getLatitude(), point
				.getLongitude()));
		polylineOptions_top.add(new LatLng(point.getLatitude(), point
				.getLongitude()));
		map.addPolyline(polylineOptions_top);
		map.addPolyline(polylineOptions_back);
		// setZoomAllMarkers(map);

	}

}
