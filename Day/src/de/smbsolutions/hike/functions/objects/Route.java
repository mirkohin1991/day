package de.smbsolutions.hike.functions.objects;

import java.text.SimpleDateFormat;
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

import de.smbsolutions.hike.R;
import de.smbsolutions.hike.functions.database.Database;
import de.smbsolutions.hike.functions.tasks.MarkerWorkerTask;

/**
 * Diese Klasse bildet eine ganze Route ab. Damit bildet sie einen Satz an
 * RoutePoints zur entsprechenden Route in dieser Klasse als Liste ab
 * Letzendlich wird durch diese Klasse ein Mapping DB<->Objektorientierung
 * vorgenommen. Änderungen werden dem Objekt über Methoden mitgeben und durch
 * dieses an die Datenbank weitergegeben. Die Klasse implementiert Parcable, um
 * Objekte von Ihr mit einem Bundle zu übertragen
 */
public class Route implements Parcelable {

	private static final long serialVersionUID = 1L;

	// Liste aller vorhandener RoutePoints. Muss vom Type CopyOnWriteArrayList
	// sein, weil dadurch gewährleistet wird, dass bei synchronen Zugriffen auf
	// die Liste keine Fehler auftreten.
	private CopyOnWriteArrayList<RoutePoint> routePoints = new CopyOnWriteArrayList<RoutePoint>();

	// übergreifende Eingenschaften zur Route
	private String routeName;
	private String date;
	private boolean active;
	private int id;

	// Hier werden später die Polyline Punkte eingefügt, damit auf der Karte die
	// Verbindungen erscheinen
	PolylineOptions polylineOptions_back;
	PolylineOptions polylineOptions_top;

	// Notwendige Hashmap, um bei einem Klick auf einen Marker später zu
	// erkennen welcher Routenpunkt sich dahinter verbirgt.
	public LinkedHashMap<RoutePoint, Marker> markerMap;

	/**
	 * Leerer Konstruktur für Routen, die schon angelegt wurden
	 */
	public Route() {
	}

	/**
	 * Konstruktor für neue Routen
	 */
	public Route(String routeName) {

		Date currentDate = Calendar.getInstance().getTime();
		String today = new SimpleDateFormat("dd/MM/yyyy").format(currentDate);

		this.routeName = routeName;
		// Die letzte Id in der Datenbank wird um eins erhöht, um die neue ID zu
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
	 * Schließen der Route
	 */
	public void closeRoute() {

		// Schließen der Route. Nur wenn erfolgreich wird flag auf false gesetzt
		if (Database.closeRoute(id) == true) {
			active = false;
		}

	}

	/**
	 * Hinzufügen eines einzelnen RoutePoints zur Datenkbank
	 */
	public void addRoutePointDB(RoutePoint point) {

		// Nur wenn die Route aktiv ist, ist es möglich Punkte hinzuzufügen
		if (active == true) {

			// Wenn der DB Insert geklappt hat, wird die Route auch der internen
			// Liste hinzugefügt
			if (Database.addNewRoutePoint(point) == true) {

				routePoints.add(point);
			}
		}

	}

	/**
	 * Hinzufügen eines Routenpunktes zur internen Liste, nicht aber zur
	 * Datenbank. Dies wird beispielsweise beim Auslesen der vorhandenen routen
	 * aus der Datenbank benutzt
	 */
	public void addRoutePoint(RoutePoint point) {
		routePoints.add(point);
	}

	/**
	 * Methode, um der mitgegebenen Map alle zur Route vorhanden Punkte
	 * hinzufügen Preview bedeutet, dass hier eine spezielle Vorgehensweise für
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
	 * hinzufügen Details bedeutet, dass es sich hier um die Map der
	 * Detailansicht zur Route handelt. In diesem Fall müssen weit aus mehr
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

			// Zunächst werden die normalen Marker erstellt, um diese später
			// dann im Task mit den Bildern zu ersetzen
			for (RoutePoint point : this.routePoints) {

				MarkerOptions markerOpt = new MarkerOptions()
						.position(new LatLng(point.getLatitude(), point
								.getLongitude()));

				Marker marker = mapImport.addMarker(markerOpt);
				markerMap.put(point, marker);
			}

			// Die Map wird von alten Markern befreit
			mapImport.clear();

			// Die Polylines können auch schon hinzugefügt werden
			addPolylinesDetail(mapImport);

			// Anschließend werden jetzt alle Marker, die mit Bildern angezeigt
			// werden sollen, eingefügt.
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
	 * Methode die bei der mitgegebenen Map dafür sorgt, dass der Zoom alle
	 * Punkte umschließt
	 */
	private void setZoomAllMarkers(GoogleMap map) {

		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		// Locations der Polylines werden dem Builder hingefügt
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
	 * setzt, der der Methode ebenfalls übergeben wird
	 */
	public void setZoomSpecificMarker(RoutePoint point, GoogleMap map) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		// Aus der Hashmap kann über den Routepunkt die Position ermittelt
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
	 * Zoomt zu einem bestimmten Punkt auf der Map.
	 * 
	 * @param point
	 * @param map
	 */
	public void setZoomSpecificPoint(RoutePoint point, GoogleMap map) {
		LatLng latlng = new LatLng(point.getLatitude(), point.getLongitude());
		CameraUpdate camUpdate = CameraUpdateFactory.newLatLng(latlng);
		map.animateCamera(camUpdate);
	}

	/**
	 * Methoden zum Löschen einzelner Bilder aus der Datenbank und anschließend
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
	 * hinzufügt. Zudem werden Start und Ziel Flaggen angezeigt
	 */
	private void addPolylinesDetail(GoogleMap map) {

		polylineOptions_back = new PolylineOptions().width(3).color(
				Color.rgb(123, 207, 168));
		polylineOptions_top = new PolylineOptions().width(8).color(
				Color.rgb(19, 88, 5));

		for (RoutePoint point : this.routePoints) {
			// Der Punkt wird der Polyline hinzugefügt
			polylineOptions_back.add(new LatLng(point.getLatitude(), point
					.getLongitude()));
			polylineOptions_top.add(new LatLng(point.getLatitude(), point
					.getLongitude()));

			// Wenn die Route keine Bilder enthält, werden Start (und Ziel)
			// Flagge gesetzt
			if (hasPicturePoint() == false) {

				// Erster RoutePoint --> Start Flagge
				if (point == routePoints.get(0)) {
					MarkerOptions markerOpt = new MarkerOptions().position(
							new LatLng(point.getLatitude(), point
									.getLongitude())).icon(
							BitmapDescriptorFactory
									.fromResource(R.drawable.start_marker));

					// Speichern des Markers in der Hashmap
					Marker marker = map.addMarker(markerOpt);
					markerMap.put(point, marker);
				}

				// Letzter Eintrag --> Finish Flagge
				if (point == routePoints.get(routePoints.size() - 1)) {

					// Aber nur wenn die Route schon beendet ist
					if (active == false) {

						MarkerOptions markerOpt = new MarkerOptions().position(
								new LatLng(point.getLatitude(), point
										.getLongitude())).icon(
								BitmapDescriptorFactory
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
	 * Punkt hinzufügt. Zudem werden Start und Ziel Flaggen angezeigt.
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
				MarkerOptions markerOpt = new MarkerOptions().position(
						new LatLng(point.getLatitude(), point.getLongitude()))
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.start_marker));

				// Der Marker wird der map hinzugefügt
				map.addMarker(markerOpt);

			}

			// Letzter Eintrag --> Finish-Flagge (
			if (point == routePoints.get(routePoints.size() - 1)) {

				// Aber nur wenn die Route schon beendet ist
				if (active == false) {

					MarkerOptions markerOpt = new MarkerOptions().position(
							new LatLng(point.getLatitude(), point
									.getLongitude())).icon(
							BitmapDescriptorFactory
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
	 * hinzuzufügen. Wird verwendet wenn Service oder Benutzer zur Laufzeit
	 * einen Punkt hinzufügen
	 */
	public void addPoint2Polyline(RoutePoint point, GoogleMap map) {

		polylineOptions_back.add(new LatLng(point.getLatitude(), point
				.getLongitude()));
		polylineOptions_top.add(new LatLng(point.getLatitude(), point
				.getLongitude()));
		map.addPolyline(polylineOptions_top);
		map.addPolyline(polylineOptions_back);
		setZoomSpecificPoint(point, map);
	}

	/**
	 * Methode, die die Information zurückliefert, ob es Punkte in der Route
	 * gibt, die ein Bild enthalten
	 */
	public boolean hasPicturePoint() {

		for (RoutePoint point : routePoints) {

			// Sobald ein Punkt ein bild enthält wird true returned
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
	 * Nötig, weil die Klasse Parcable implementiert
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Nötig, weil die Klasse Parcable implementiert
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
