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
 * Activity gew�hrleisten zu k�nnen.
 * 
 */
public interface MainCallback {
	/**
	 * �ffnet einen Dialog um eine neue Route erstellen zu k�nnen.
	 */
	public void onOpenDialogNewRoute(RouteList routeList);

	/**
	 * �ffnet das DetailFragment zur zugeh�rigen Route.
	 */
	public void onShowRoute(Route route);

	/**
	 * �ffnet das DetailFragment zur neu erstellen Route.
	 */
	public void onNewRouteStarted(Route route);

	/**
	 * �ffnet einen Dialog um eine Route l�schen zu k�nnen.
	 */
	public void onOpenDialogDeleteRoute(RouteList routeList, int index);

	/**
	 * Aktualisiert das MainFragment nach dem L�schen einer Route
	 */
	public void onRouteDeleted();

	/**
	 * �ffnet einen Dialog um eine Route stoppen zu k�nnen.
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
	 * �ffnet einen Dialog um ein Bild aus der Scrollbar l�schen zu k�nnen.
	 */
	public void onDeletePictureClick(Route route, RoutePoint point);

	/**
	 * �ffnet die Detailansicht (PictureFragment) zu einem bestimmten Bild.
	 */
	public void onPictureClick(Route route, RoutePoint point);

	/**
	 * 
	 * Aktualisiert das DetailFragment nach dem L�schen eines Bildes.
	 * 
	 */
	public void onDeletePicture(Route route);

	/**
	 * 
	 * Aktualisiert das DetailFragment nach dem Schie�en eines Fotos.
	 * 
	 */
	public void onCamStart(Route route);

	/**
	 * Garantiert, dass der User beim Klicken des Zur�ck-Buttons (onBackPressed)
	 * immer zum letzten Fragment gelangt. Dies muss vor allem bei
	 * EinstellungsFragments �berwacht werden.
	 */
	public void onSliderClick(Fragment fragment);

	/**
	 * Wird ausgef�hrt wenn der User zur Laufzeit die Typ der Map �ndert. Die
	 * Methode �berpr�ft ob das aktuelle Fragment eine Map enth�lt und
	 * aktualisiert dort den Typ.
	 */
	public void onRefreshMap();

	/**
	 * Startet den Tracking-Service zur aktiven Route
	 */
	public void onStartTrackingService(Route route);

	/**
	 * 
	 * Wird ausgef�hrt wenn ein Bild geschossen wurde und der Tracking-Service
	 * die zugeh�rige Location speichern soll.
	 */
	public void onPictureTaken(Route route, Uri fileUri, File small_picture);

	/**
	 * Wird ausgef�hrt wenn eine aktive Route im DetailFragmen angezeigt werden
	 * soll und der Tracking-Service nicht ausgef�hrt wird. Ist dies der Fall
	 * wird der Tracking-Service neu gestartet.
	 */
	public void onActiveRouteNoService();

	/**
	 * �ffnet einen Dialog um die aktive Route pausieren zu k�nnen.
	 */
	public void onOpenDialogPauseRoute(Route Route);

	/**
	 * Holt sich das vorherige Fragment im Backstack und gibt dieses zur�ck.
	 */
	public Fragment getpreviousFragment();

	/**
	 * Beendet den Tracking-Service wenn der User die Route stoppt oder eine
	 * neue statet.
	 */
	public void removeService();

	/**
	 * Wird ausgef�hrt wenn der User den Tracking-Intervall ge�ndert hat und der
	 * Service neu gestartet werden muss.
	 */
	public void onTrackingIntervalChanged();

	/**
	 * Wird ausgef�hrt wenn der User das Tracking ausstellt und der Service neu
	 * gestartet werden muss.
	 */
	public void onTrackingTurnedOnOff();

	/**
	 * Startet den Tracking-Service neu.
	 */
	public void restartTracking(Route route);

	/**
	 * Stellt fest, ob gerade ein Service aktiv ist und gibt einen Boolean
	 * zur�ck.
	 */
	public boolean isServiceActive();

	/**
	 * Aktualisiert die Karte im DetailFragment wenn eine neue Location
	 * gespeichert wurde.
	 */
	public void onLocationChanged(Route route, RoutePoint point);

	/**
	 * Aktualisiert das SliderMenu wenn der User dieses verl�sst.
	 */
	public void refreshSliderMenu();

	/**
	 * �ndert globale Variabel "activeRouteisOpened" und setzt diese.
	 */
	public void onRouteOpenend(boolean active);

	/**
	 * Wird ausgef�hrt wenn beim Neustart der App ein Absturz erkannt wurde und
	 * versendet den Logcat-Log des Ger�tes.
	 */
	public void onDumpDetected();

	/**
	 * �ffnent einen Dialog um den User zu fragen, ob er den Log versenden
	 * m�chte.
	 */
	public void onDumpDialogShow();

}
