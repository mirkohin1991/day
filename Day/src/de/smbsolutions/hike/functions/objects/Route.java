package de.smbsolutions.hike.functions.objects;

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
import de.smbsolutions.hike.functions.database.Database;
import de.smbsolutions.hike.functions.tasks.MarkerWorkerTask;

/**
 * Diese Klasse bildet eine ganze Route ab. Damit bildet sie einen Satz an
 * RoutePoints zur entsprechenden Route in dieser Klasse als Liste ab
 * Letzendlich wird durch diese Klasse ein Mapping DB<->Objektorientierung
 * vorgenommen. �nderungen werden dem Objekt �ber Methoden mitgeben und durch
 * dieses an die Datenbank weitergegeben. Die Klasse implementiert Parcable, um
 * Objekte von Ihr mit einem Bundle zu �bertragen
 */
public class Route implements Parcelable {

	private static final long serialVersionUID = 1L;

	// Liste aller vorhandener RoutePoints. Muss vom Type CopyOnWriteArrayList
	// sein, weil dadurch gew�hrleistet wird, dass bei synchronen Zugriffen auf
	// die Liste keine Fehler auftreten.
	private CopyOnWriteArrayList<RoutePoint> routePoints = new CopyOnWriteArrayList<RoutePoint>();

	// �bergreifende Eingenschaften zur Route
	private String routeName;
	private String date;
	private boolean active;
	private int id;

	// Hier werden sp�ter die Polyline Punkte eingef�gt, damit auf der Karte die
	// Verbindungen erscheinen
	PolylineOptions polylineOptions_back;
	PolylineOptions polylineOptions_top;

	// Notwendige Hashmap, um bei einem Klick auf einen Marker sp�ter zu
	// erkennen welcher Routenpunkt sich dahinter verbirgt.
	public LinkedHashMap<RoutePoint, Marker> markerMap;

	/**
	 * Leerer Konstruktur f�r Routen, die schon angelegt wurden
	 */
	public Route() {
	}

	/**
	 * Konstruktor f�r neue Routen
	 */
	public Route(String routeName) {

		Date currentDate = Calendar.getInstance().getTime();
		String today = new SimpleDateFormat("dd/MM/yyyy").format(currentDate);

		this.routeName = routeName;
		// Die letzte Id in der Datenbank wird um eins erh�ht, um die neue ID zu
		// bekommen
		id = Database.getlastRouteID() + 1;
		date = today;
		active = true;

		// Anlegen der Route. Wenn fehlerhaft wird das Active Flag wieder
		// entfernt
		if (Database.createNewRoute(this) != true) {
			this.active = false;
		}

	}

	/**
	 * Schlie�en der Route
	 */
	public void closeRoute() {

		// Schlie�en der Route. Nur wenn erfolgreich wird flag auf false gesetzt
		if (Database.closeRoute(id) == true) {
			active = false;
		}

	}

	/**
	 * Hinzuf�gen eines einzelnen RoutePoints zur Datenkbank
	 */
	public void addRoutePointDB(RoutePoint point) {

		// Nur wenn die Route aktiv ist, ist es m�glich Punkte hinzuzuf�gen
		if (active == true) {

			// Wenn der DB Insert geklappt hat, wird die Route auch der internen
			// Liste hinzugef�gt
			if (Database.addNewRoutePoint(point) == true) {

				routePoints.add(point);
			}
		}

	}

	/**
	 * Hinzuf�gen eines Routenpunktes zur internen Liste, nicht aber zur
	 * Datenbank. Dies wird beispielsweise beim Auslesen der vorhandenen routen
	 * aus der Datenbank benutzt
	 */
	public void addRoutePoint(RoutePoint point) {
		routePoints.add(point);
	}

	/**
	 * Methode, um der mitgegebenen Map alle zur Route vorhanden Punkte
	 * hinzuf�gen Preview bedeutet, dass hier eine spezielle Vorgehensweise f�r
	 * die Vorschau auf dem Hauptfragment benutzt wird
	 */
	public GoogleMap prepareMapPreview(final GoogleMap mapImport) {

		mapImport.clear();

		addPolylinesPreview(mapImport);

		setZoomAllMarkers(mapImport);

		return mapImport;

	}

	/**
	 * Methode, um der mitgegebenen Map alle zur Route vorhanden Punkte
	 * hinzuf�gen Details bedeutet, dass es sich hier um die Map der
	 * Detailansicht zur Route handelt. In diesem Fall m�ssen weit aus mehr
	 * Dinge angezeigt werden als beim Preview
	 */
	public GoogleMap prepareMapDetails(final GoogleMap mapImport,
			Context context) {

		// Refreshen der markerMap
		if (markerMap != null) {
			markerMap.clear();
		} else {
			markerMap = new LinkedHashMap<RoutePoint, Marker>();
		}

		// Wenn die Route Bilder besitzt, werden diese in einem eigenen Task
		// geladem
		// Dadurch kann eine enorme Performancesteigerung erreicht werden
		if (hasPicturePoint()) {

			// Zun�chst werden die normalen Marker erstellt, um diese sp�ter
			// dann im Task mit den Bildern zu ersetzen
			for (RoutePoint point : this.routePoints) {

				MarkerOptions markerOpt = new MarkerOptions().position(
						new LatLng(point.getLatitude(), point.getLongitude()));

				Marker marker = mapImport.addMarker(markerOpt);
				markerMap.put(point, marker);
			}

			// Die Map wird von alten Markern befreit
			mapImport.clear();

			// Die Polylines k�nnen auch schon hinzugef�gt werden
			addPolylinesDetail(mapImport);

			// Anschlie�end werden jetzt alle Marker, die mit Bildern angezeigt
			// werden sollen, eingef�gt.
			MarkerWorkerTask task = new MarkerWorkerTask(mapImport, markerMap,
					this, context);
			task.execute(this.routePoints);

			// Wenn die Route keine Bilder besitzt, kann normal vorgegangen
			// werden
		} else {
			addPolylinesDetail(mapImport);

			setZoomAllMarkers(mapImport);

		}
		return mapImport;

	}

	/**
	 * Methode die bei der mitgegebenen Map daf�r sorgt, dass der Zoom alle
	 * Punkte umschlie�t
	 */
	private void setZoomAllMarkers(GoogleMap map) {

		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		// Locations der Polylines werden dem Builder hingef�gt
		for (LatLng point : polylineOptions_top.getPoints()) {
			builder.include(point);
		}

		// Die Kamera wird so animiert, dass alle Bounds angezeigt werden
		LatLngBounds bounds = builder.build();
		CameraUpdate camUpdate = CameraUpdateFactory
				.newLatLngBounds(bounds, 60);
		map.animateCamera(camUpdate);
	}

	/**
	 * Methode, die den Zoom der mitgegeben Map auf einen bestimmten Punkt
	 * setzt, der der Methode ebenfalls �bergeben wird
	 */
	public void setZoomSpecificMarker(RoutePoint point, GoogleMap map) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		// Aus der Hashmap kann �ber den Routepunkt die Position ermittelt
		// werden
		LatLng latlng = markerMap.get(point).getPosition();

		if (latlng != null) {

			// Die Kamera wird so animiert, der eine Punkt zentriert und mit
			// richtigem Zoom angezeigt wird
			builder.include(latlng);
			LatLngBounds bounds = builder.build();
			CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(
					bounds, 60);
			map.animateCamera(camUpdate);

		}

	}

	/**
	 * Methoden zum L�schen einzelner Bilder aus der Datenbank und anschlie�end
	 * aus der internen Liste
	 */
	public void deletePictureDB(RoutePoint deletePoint) {

		// Nur wenn DB Delete erfolgreich wird die interne Liste geleert
		if (Database.deletePicturePath(deletePoint) == true) {
			routePoints.remove(deletePoint);
		}

	}

	/**
	 * Methode, die der Map auf der Detailansichtsseite die Polylines pro Punkt
	 * hinzuf�gt. Zudem werden Start und Ziel Flaggen angezeigt
	 */
	private void addPolylinesDetail(GoogleMap map) {

		polylineOptions_back = new PolylineOptions().width(3).color(
				Color.rgb(123, 207, 168));
		polylineOptions_top = new PolylineOptions().width(8).color(
				Color.rgb(19, 88, 5));

		for (RoutePoint point : this.routePoints) {
			// Der Punkt wird der Polyline hinzugef�gt
			polylineOptions_back.add(new LatLng(point.getLatitude(), point
					.getLongitude()));
			polylineOptions_top.add(new LatLng(point.getLatitude(), point
					.getLongitude()));

			// Wenn die Route keine Bilder enth�lt, werden Start (und Ziel)
			// Flagge gesetzt
			if (hasPicturePoint() == false) {

				// Erster RoutePoint --> Start Flagge
				if (point == routePoints.get(0)) {
					MarkerOptions markerOpt = new MarkerOptions()
							.position(
									new LatLng(point.getLatitude(), point
											.getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.start_marker));

					// Speichern des Markers in der Hashmap
					Marker marker = map.addMarker(markerOpt);
					markerMap.put(point, marker);
				}

				// Letzter Eintrag --> Finish Flagge
				if (point == routePoints.get(routePoints.size() - 1)) {

					// Aber nur wenn die Route schon beendet ist
					if (active == false) {

						MarkerOptions markerOpt = new MarkerOptions()
								.position(
										new LatLng(point.getLatitude(), point
												.getLongitude()))
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.stop_marker));

						// Speichern des Markers in der Hashmap
						Marker marker = map.addMarker(markerOpt);
						markerMap.put(point, marker);

					}
				}
			}
		}

		// Zum Schluss werden alle Poylines auf der Map eingezeichnet
		map.addPolyline(polylineOptions_top);
		map.addPolyline(polylineOptions_back);

	}

	/**
	 * Methode, die der Vorschaumap auf der Hauptansichtsseite die Polylines pro
	 * Punkt hinzuf�gt. Zudem werden Start und Ziel Flaggen angezeigt.
	 */
	private void addPolylinesPreview(GoogleMap map) {

		polylineOptions_back = new PolylineOptions().width(3).color(
				Color.rgb(123, 207, 168));
		polylineOptions_top = new PolylineOptions().width(8).color(
				Color.rgb(19, 88, 5));

		for (RoutePoint point : this.routePoints) {
			polylineOptions_back.add(new LatLng(point.getLatitude(), point
					.getLongitude()));
			polylineOptions_top.add(new LatLng(point.getLatitude(), point
					.getLongitude()));

			// Erster RoutePoint --> Start-Flagge
			if (point == routePoints.get(0)) {
				MarkerOptions markerOpt = new MarkerOptions()
						.position(
								new LatLng(point.getLatitude(), point
										.getLongitude()))
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.start_marker));

				// Der Marker wird der map hinzugef�gt
				map.addMarker(markerOpt);

			}

			// Letzter Eintrag --> Finish-Flagge (
			if (point == routePoints.get(routePoints.size() - 1)) {

				// Aber nur wenn die Route schon beendet ist
				if (active == false) {

					MarkerOptions markerOpt = new MarkerOptions()
							.position(
									new LatLng(point.getLatitude(), point
											.getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.stop_marker));

					map.addMarker(markerOpt);

				}
			}
		}

		// Zum Schluss werden alle Poylines auf der Map eingezeichnet
		map.addPolyline(polylineOptions_top);
		map.addPolyline(polylineOptions_back);

	}
	

	/**
	 * Methode, um einer vorhanden Polyline-Kombination einen weiteren Punkt
	 * hinzuzuf�gen. Wird verwendet wenn Service oder Benutzer zur Laufzeit
	 * einen Punkt hinzuf�gen
	 */
	public void addPoint2Polyline(RoutePoint point, GoogleMap map) {

		polylineOptions_back.add(new LatLng(point.getLatitude(), point
				.getLongitude()));
		polylineOptions_top.add(new LatLng(point.getLatitude(), point
				.getLongitude()));
		map.addPolyline(polylineOptions_top);
		map.addPolyline(polylineOptions_back);
	}

	/**
	 * Methode, die die Information zur�ckliefert, ob es Punkte in der Route
	 * gibt, die ein Bild enthalten
	 */
	public boolean hasPicturePoint() {

		for (RoutePoint point : routePoints) {

			// Sobald ein Punkt ein bild enth�lt wird true returned
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

	/**
	 * N�tig, weil die Klasse Parcable implementiert
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * N�tig, weil die Klasse Parcable implementiert
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
