package de.smbsolutions.hike.functions.objects;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import de.smbsolutions.hike.functions.database.Database;

/**
 * Diese Klasse bildet stellt eine Liste an Routen dar. Letzendlich wird durch
 * diese Klasse ein Mapping DB<->Objektorientierung vorgenommen. Änderungen
 * werden dem Objekt über Methoden mitgeben und durch dieses an die Datenbank
 * weitergegeben. Die Klasse implementiert Parcable, um Objekte von Ihr mit
 * einem Bundle zu übertragen
 */
public class RouteList implements Parcelable {

	private List<Route> listRoutes;

	/**
	 * Konstruktur, über den eine bestimmte Anzahl von Routen, die das Objekt
	 * enthalten soll, mitgegeben werden kann
	 */
	public RouteList(int count) {
		// DB Select aller gewünschten Routen
		listRoutes = Database.getSpecificRoute(count);

	}

	/**
	 * Leerer Konstruktor, um alle Routen zu bekommen
	 */
	public RouteList() {
		listRoutes = Database.getSpecificRoute(0);

	}

	/**
	 * Liefert alle Routen, die das Objekt hält, zurück
	 */
	public List<Route> getListRoutes() {
		return listRoutes;
	}


	/**
	 * Methode zur Bestimmung der letzten gespeichert Route
	 * Dies ist gleichzeitig immer die aktuellste
	 */
	public Route getlastRoute() {
		Route route = listRoutes.get(listRoutes.size() - 1);
		return route;
	}

	
	/**
	 * Methode zur Abfrage ob es eine offene Route gibt
	 */
	public boolean isOpenRoute() {

		//Es genügt die letzte Route auf Aktiv zu überprüfen.
		if (getlastRoute().isActive()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Hinzufügen einer neuen Route zur Liste
	 */
	public void addRoute(Route route) {
		listRoutes.add(route);
	}

	
	/**
	 * Löschen einer vorhandenen Route aus der Datenbank
	 */
	public void deleteRouteDB(int index) {

		// Nur wenn der DB Delete klappt, wird die interne Tabelle auch geändert
		if (Database.deleteRoute(listRoutes.get(index)) == true) {
			listRoutes.remove(index);
		}
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
