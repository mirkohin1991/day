package de.smbsolutions.day.functions.location;

import java.io.File;
import java.sql.Timestamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

/* Diese Klasse kümmert sich um das dynamische Tracken der Geo-Location
 * Dynamisch, da das Intervall und der Abstand zwischen den einzelnen Punkten vom Benutzer zur Laufzeit
 * eingestellt wird.
 * Eigenschaften: - Background Service
 *                - LocationListener
 */
public class LocationTrackerPLAYSERVICE extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	// Request Code, wird in Activity Result gesetzt
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private Activity activity;
	// Interface zur MainActivity
	private MainCallback mCallback;

	// Kommunikationsschnittstelle mit dem Service
	private final IBinder binder = new LocalBinder();

	// Zentraler Client, der sich um die Ortsbestimmung kümmert
	private LocationClient locationClient;
	private Location previousLocation;

	// Dynamische Intervalle für den Tracking Service
	private static long UPDATE_INTERVAL;
	private static long FASTEST_INTERVAL; // Wird verwendet wenn andere Apps
											// ebenfalls tracken

	// Objekt, dass die Genauigkeit und das Intervall speichert
	LocationRequest mLocationRequest;

	private boolean flag_inProgress;
	private Boolean flag_servicesAvailable = false;
	private boolean flag_serviceRunning = false;

	// Flag, zur Überprüfung ob Punkt der erste der Route ist.
	private boolean flag_first = false;

	private Route route;

	/**
	 *  Methode zur Speicherung aller benötigen Objekte
	 */
	public void saveActivity(Activity activity) {
		this.activity = activity;
		mCallback = (MainCallback) activity;
		locationClient = new LocationClient(activity, this, this);
	}
	
	
	/**
	 * Interfaceschnittstelle für die Kommunikation von außerhalb mit dem Service
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * Schnittstellenmethode zum Abrufen des Services von außerhalb
	 */
	public class LocalBinder extends Binder {

		public LocationTrackerPLAYSERVICE getService() {
			return LocationTrackerPLAYSERVICE.this;
		}
	}
	

	/**
	 * Wenn diese Methode aufgerufen wird, soll ein Bilder zur aktuellen Route
	 * hinzugefügt werden
	 */
	public void addPictureLocation(Route route, Uri fileUri, File small_picture) {

		this.route = route;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		// Aktuelle Lokation wird abgerufen
		Location pictureLocation = locationClient.getLastLocation();

		if (pictureLocation != null) {
			// Das Bild wird zusammen mit den anderen Informationen zur Route
			// hinzugefügt
			route.addRoutePointDB(new RoutePoint(route.getId(), timestamp,
					fileUri.getPath(), small_picture.getPath(), pictureLocation
							.getLatitude(), pictureLocation.getLongitude(),
					pictureLocation.getAltitude()));

			// Speichern der Lokation, um sie später mit der nächsten zu
			// vergleichen.
			// Dadurch kann geprüft werden, ob die minimale Distanz
			// überschritten wurde
			previousLocation = pictureLocation;

		} else {

			// Konnte keine Location gefunden werden, müssen die
			// Location-Einstellungen angepasst werden
			showPopUpEnableSettings();

			// // Display the connection status
			Toast.makeText(activity, "Location couldn't be detected",
					Toast.LENGTH_SHORT).show();
			// If already requested, start periodic updates
		}
	}
	

	/**
	 * Methode, die aufgerufen wird wenn eine neue Route angelegt wird und diese
	 * das erste Mal angezeigt werden soll
	 */
	public void startLocationTrackingAndSaveFirst(Route route) {

		this.route = route;
		this.flag_first = true;
		startLocationTracking();

	}
	

	/**
	 * Methode, die aufgerufen wird wenn eine bereits angelegte Route
	 * wiederaufgenommen werden soll. Das entsprechende Flag wird dann auf false
	 * gesetzt
	 */
	public void reStartLocationTrackingAndSavePoint(Route route) {

		this.route = route;
		this.flag_first = false;
		startLocationTracking();
	}
	

	private void startLocationTracking() {
		// Connect the client.
		locationClient.connect();
	}
	

	private void stopLocationTracking() {
		// Disconnecting the client invalidates it.
		locationClient.disconnect();

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		// Das Google Play services Framework kann einige Fehler selbst
		// behandeln.
		// Deshalb wird die entsprechende Fehlerbehandlung eingeleitet
		if (connectionResult.hasResolution()) {

			try {
				connectionResult.startResolutionForResult(activity,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);

			} catch (IntentSender.SendIntentException e) {

			}

		}

	}

	/**
	 * Diese Methode wird vom Location Service aufgerufen, wenn die Connection
	 * mit dem Client erfolgreich war
	 */
	@Override
	public void onConnected(Bundle connectionHint) {

		// Speichern, dass Service jetzt läuft
		flag_serviceRunning = true;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		if (locationClient.isConnected()) {
			Toast.makeText(activity, "Der Client passt", Toast.LENGTH_SHORT)
					.show();
		}

		// Abrufen der letzten Location
		Location location = locationClient.getLastLocation();

		if (location != null) {

			// Hinzufügen eines neuen Punktes zur Route (ohne Bild (2x null))
			route.addRoutePointDB(new RoutePoint(route.getId(), timestamp,
					null, null, location.getLatitude(),
					location.getLongitude(), location.getAltitude()));

			// Zwischenspeichern der Location für den späteren Distanz-Vergleich
			previousLocation = location;

			// Sonderweg für neu erstellte Route
			if (flag_first == true) {
				mCallback.onNewRouteStarted(route);
			}

			// Display the connection status
			Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
			// If already requested, start periodic updates

			// Wenn Benutzer Tracking ausgestellt hat --> kein locationlistener
			// nötig
			if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 0) {
				return;
			}

			// Intervall in welchem der Service tracken soll
			UPDATE_INTERVAL = Database
					.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL);
			// Intervall, in welchem der Service tracken soll wenn er Location
			// Daten von andere App bekommt
			FASTEST_INTERVAL = UPDATE_INTERVAL / 5;

			// Anlegen, einstellen und starten des Location requests
			mLocationRequest = LocationRequest.create();
			// Use high accuracy
			mLocationRequest
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
			locationClient.requestLocationUpdates(mLocationRequest, this);

		} else {

			// Location konnte nicht getrackt werden -> Benutzer muss
			// Einstellungen ändern
			showPopUpEnableSettings();

			// // Display the connection status
			Toast.makeText(activity, "Location couldn't be detected",
					Toast.LENGTH_SHORT).show();

		}

	}

	/*
	 * Methode wird aufgerufen wenn der Service disconnected wurde
	 */
	@Override
	public void onDisconnected() {

		// Zurücksetzen der gesetzten Einstellungen
		flag_serviceRunning = false;
		flag_inProgress = false;
		locationClient = null;

	}

	/**
	 * Sofern der location_client in der onConnected Methode gestaret wurde,
	 * wird diese Methode im entsprechenden Intervall aufgerufen.
	 * Dementsprechend wird jedes Mal die Distanz überprüft und - falls
	 * außerhalb der Mindestangabe - ein neuer Punkt zur Route hinzugefügt
	 */
	@Override
	public void onLocationChanged(Location location) {

		// Refresh des Flags Service läuft
		flag_serviceRunning = true;

		// Nur wenn die Distanz groß genug ist, soll auch getrackt werden
		if (previousLocation != null) {
			if (location.distanceTo(previousLocation) < Database
					.getSettingValue(Database.SETTINGS_TRACKING_METER)) {
				Toast.makeText(activity, "Zu nahe am letzten Punkt",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		// Hinzufügen eines neuen Punktes (Ohne Bild)
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		RoutePoint point = new RoutePoint(route.getId(), timestamp, null, null,
				location.getLatitude(), location.getLongitude(),
				location.getAltitude());
		route.addRoutePointDB(point);

		// Aufrufen der MainActivity Interfacemethode, um die Map etc
		// entsprechend zu aktualisieren
		mCallback.onLocationChanged(route, point);

		// Speichern der neuen Location für den nächsten Vergleich
		previousLocation = location;

		String msg = "Updated Location: "
				+ Double.toString(location.getLatitude()) + ","
				+ Double.toString(location.getLongitude());
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * Diese Methode wird aufgerufen, wenn der Benutzer Änderungen an den Tracking-Einstellungen vorgenommen hat
	 */
	public void refreshTrackingInterval() {
		Toast.makeText(this, "Interval refreshed", Toast.LENGTH_LONG).show();
		// Intervallparameter werden angepasst
		UPDATE_INTERVAL = Database
				.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL);
		FASTEST_INTERVAL = UPDATE_INTERVAL / 5;

	}

	/**
	 * Sofern das Intervall des Services durch den Benutzer geändert wurde, muss der locationClient neu gestartet werden, um mit den neuen 
	 * Parametern weiterzuarbeiten
	 */
	public void restartLocationTracker() {

		try {
			
			//Alle bisherigen Updates werden gelöscht
			locationClient.removeLocationUpdates(this);
			
			Toast.makeText(this, "Tracker restarted", Toast.LENGTH_LONG).show();

			// Nur wenn GPS Tracking aktiviert wurde, soll auch getrackt werden
			if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 1) {

				//Die neuen Abstände werden angepasst
				//(Dazu wird zuvor immer die Methode refreshTrackingIntervall aufgerufen
				mLocationRequest.setInterval(UPDATE_INTERVAL);
				mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

				// Neustart des LocationClients
				locationClient.requestLocationUpdates(mLocationRequest, this);

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	

	

	
	/** 
	 * Methode, die aufgerufen wird wenn das Serviceobjekt selbst erstellt wurde
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		flag_inProgress = false;

		// Überprüfung ob Google Play Services verfügbar ist.
		// Wenn nicht wird später eine Behebung versucht
		flag_servicesAvailable = servicesConnected();

	}

	
	/**
	 * Wenn das Objekt komplett geschlossen wird, werden entsprechende Einstellungen zurückgesetzt
	 */
	@Override
	public void onDestroy() {

		flag_serviceRunning = false;
		flag_inProgress = false;
		
		//Wenn Playservices verfügbar und der Client noch nicht gelöscht wurde
		if (flag_servicesAvailable && locationClient != null) {
			
			//Das Tracking wird beendet
			locationClient.removeLocationUpdates(this);
			stopLocationTracking();

			// Destroy the current location client
			locationClient = null;
		}

		super.onDestroy();
	}

	/**
	 * Sorgt dafür, dass der Service auch läuft wenn die App nichtmehr im
	 * Vordergrund läuft
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (!flag_servicesAvailable || locationClient.isConnected()
				|| flag_inProgress)
			return START_STICKY;

		if (locationClient == null) {
			locationClient = new LocationClient(this, this, this);
		}
		if (!locationClient.isConnected() || !locationClient.isConnecting()
				&& !flag_inProgress) {

			flag_inProgress = true;
			locationClient.connect();
		}

		return START_STICKY;
	}


  /**
   * Methode, zur Überprüfung, ob Google Play Services verfügbar sind
   */
	private boolean servicesConnected() {

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		//Wenn google play services verfügbar
		if (ConnectionResult.SUCCESS == resultCode) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Methode zum Überprüfen ob der Service noch läuft
	 */
	public boolean isServiceRunning() {
		return flag_serviceRunning;
	}


	/**
	 * Methode, die ein Popup erzeugt, in welchem der Benutzer seine Standorteinstellungen ändern kann
	 */
	private void showPopUpEnableSettings() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

	
		alertDialog.setTitle("Standort Einstellungen");
		alertDialog.setMessage("Mit den aktuellen Einstellungen, kann der Standort nicht bestimmt werden. Bedingt durch einen Betriebssystemfehler ist in manchen Fällen leider auch ein kompletter Neustart nötig ");

		// Wenn der Benutzer seine Einstellungen ändern will
		alertDialog.setPositiveButton("Einstellungen ändern",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						activity.startActivity(intent);
					}
				});

		// Abbrechen Button
		alertDialog.setNegativeButton("Abbrechen",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		//Dialog wird angezeigt
		alertDialog.show();
	}


}
