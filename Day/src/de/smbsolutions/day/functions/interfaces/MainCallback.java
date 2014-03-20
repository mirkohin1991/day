package de.smbsolutions.day.functions.interfaces;

import java.io.File;

import android.media.Image;
import android.net.Uri;
import android.support.v4.app.Fragment;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.functions.objects.RoutePoint;

/**
 * Callback-Interface um die Kommunikation zwischen den Fragments und der
 * Activity gewährleisten zu können.
 * 
 */
public interface MainCallback {
	/**
	 * Öffnet einen Dialog um eine neue Route erstellen zu können.
	 */
	public void onOpenDialogNewRoute(RouteList routeList);

	/**
	 * Öffnet das DetailFragment zur zugehörigen Route.
	 */
	public void onShowRoute(Route route);

	/**
	 * Öffnet das DetailFragment zur neu erstellen Route.
	 */
	public void onNewRouteStarted(Route route);

	/**
	 * Öffnet einen Dialog um eine Route löschen zu können.
	 */
	public void onOpenDialogDeleteRoute(RouteList routeList, int index);

	/**
	 * Aktualisiert das MainFragment nach dem Löschen einer Route
	 */
	public void onRouteDeleted();

	/**
	 * Öffnet einen Dialog um eine Route stoppen zu können.
	 */
	public void onOpenDialogStopRoute(String fragmentFlag, Route route);

	/**
	 * 
	 * Aktualisiert das MainFragment nach dem Stoppen einer Route und stoppt den
	 * Tracking-Service
	 * 
	 */
	public void onRouteStopped(String fragmentTag, Route route);

	/**
	 * Öffnet einen Dialog um ein Bild aus der Scrollbar löschen zu können.
	 */
	public void onDeletePictureClick(Route route, RoutePoint point);

	/**
	 * Öffnet die Detailansicht (PictureFragment) zu einem bestimmten Bild.
	 */
	public void onPictureClick(Route route, RoutePoint point);

	/**
	 * 
	 * Aktualisiert das DetailFragment nach dem Löschen eines Bildes.
	 * 
	 */
	public void onDeletePicture(Route route);

	/**
	 * 
	 * Aktualisiert das DetailFragment nach dem Schießen eines Fotos.
	 * 
	 */
	public void onCamStart(Route route);

	/**
	 * Garantiert, dass der User beim Klicken des Zurück-Buttons (onBackPressed)
	 * immer zum letzten Fragment gelangt. Dies muss vor allem bei
	 * EinstellungsFragments überwacht werden.
	 */
	public void onSliderClick(Fragment fragment);

	/**
	 * Wird ausgeführt wenn der User zur Laufzeit die Typ der Map ändert. Die
	 * Methode überprüft ob das aktuelle Fragment eine Map enthält und
	 * aktualisiert dort den Typ.
	 */
	public void onRefreshMap();

	/**
	 * Startet den Tracking-Service zur aktiven Route
	 */
	public void onStartTrackingService(Route route);

	/**
	 * 
	 * Wird ausgeführt wenn ein Bild geschossen wurde und der Tracking-Service
	 * die zugehörige Location speichern soll.
	 */
	public void onPictureTaken(Route route, Uri fileUri, File small_picture);

	/**
	 * Wird ausgeführt wenn eine aktive Route im DetailFragmen angezeigt werden
	 * soll und der Tracking-Service nicht ausgeführt wird. Ist dies der Fall
	 * wird der Tracking-Service neu gestartet.
	 */
	public void onActiveRouteNoService();

	/**
	 * Öffnet einen Dialog um die aktive Route pausieren zu können.
	 */
	public void onOpenDialogPauseRoute(Route Route);

	/**
	 * Holt sich das vorherige Fragment im Backstack und gibt dieses zurück.
	 */
	public Fragment getpreviousFragment();

	/**
	 * Beendet den Tracking-Service wenn der User die Route stoppt oder eine
	 * neue statet.
	 */
	public void removeService();

	/**
	 * Wird ausgeführt wenn der User den Tracking-Intervall geändert hat und der
	 * Service neu gestartet werden muss.
	 */
	public void onTrackingIntervalChanged();

	/**
	 * Wird ausgeführt wenn der User das Tracking ausstellt und der Service neu
	 * gestartet werden muss.
	 */
	public void onTrackingTurnedOnOff();

	/**
	 * Startet den Tracking-Service neu.
	 */
	public void restartTracking(Route route);

	/**
	 * Stellt fest, ob gerade ein Service aktiv ist und gibt einen Boolean
	 * zurück.
	 */
	public boolean isServiceActive();

	/**
	 * Aktualisiert die Karte im DetailFragment wenn eine neue Location
	 * gespeichert wurde.
	 */
	public void onLocationChanged(Route route, RoutePoint point);

	/**
	 * Aktualisiert das SliderMenu wenn der User dieses verlässt.
	 */
	public void refreshSliderMenu();

	/**
	 * Ändert globale Variabel "activeRouteisOpened" und setzt diese.
	 */
	public void onRouteOpenend(boolean active);

	/**
	 * Wird ausgeführt wenn beim Neustart der App ein Absturz erkannt wurde und
	 * versendet den Logcat-Log des Gerätes.
	 */
	public void onDumpDetected();

	/**
	 * Öffnent einen Dialog um den User zu fragen, ob er den Log versenden
	 * möchte.
	 */
	public void onDumpDialogShow();

}
