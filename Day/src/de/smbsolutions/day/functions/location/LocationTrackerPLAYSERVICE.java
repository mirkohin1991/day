package de.smbsolutions.day.functions.location;

import java.io.File;
import java.sql.Timestamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
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
import com.google.android.gms.maps.GoogleMap;

import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class LocationTrackerPLAYSERVICE extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private Activity activity;
	private LocationClient mLocationClient;
	private Route route;
	private MainCallback mCallback;
	private Location previousLocation;

	private static long UPDATE_INTERVAL;
	private static long FASTEST_INTERVAL;

	// object that holds accuracy and frequency parameters
	LocationRequest mLocationRequest;

	// Flag that indicates if a request is underway.
	private boolean mInProgress;

	private Boolean servicesAvailable = false;

	private final IBinder mBinder = new LocalBinder();

	// Indicating that the entered route location is the first one for the route
	private boolean flag_first = false;

	private boolean flag_serviceRunning = false;

	private boolean flag_noService_Picture = false;

	public void saveActivity(Activity activity) {
		this.activity = activity;
		mCallback = (MainCallback) activity;
		mLocationClient = new LocationClient(activity, this, this);
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	private void startLocationTracking() {

		// Connect the client.
		mLocationClient.connect();
	}

	private void stopLocationTracking() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();

	}

	public void addPictureLocation(Route route, Uri fileUri, File small_picture) {

		// Brauchen wir vielleicht auch nicht!
		this.route = route;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		// EINFACHSTE LÖSUNG. VIELLEICHT GEHT DA ABER AUCH ÜBER
		// ONLOCATIONCHANGED
		Location pictureLocation = mLocationClient.getLastLocation();

		if (pictureLocation != null) {

			route.addRoutePointDB(new RoutePoint(route.getId(), timestamp,
					fileUri.getPath(), small_picture.getPath(), pictureLocation
							.getLatitude(), pictureLocation.getLongitude(),
					pictureLocation.getAltitude()));

			previousLocation = pictureLocation;
		} else {
			showPopUpEnableSettings();

			// // Display the connection status
			// Toast.makeText(activity, "Location couldn't be detected",
			// Toast.LENGTH_SHORT).show();
			// If already requested, start periodic updates
		}

		// //AUCH NICHT DIE SPEICHERFREUNDLICHSTE LÖSUNG
		// mCallback.onShowRoute(route);

	}

	public void startLocationTrackingAndSaveFirst(Route route) {

		this.route = route;
		this.flag_first = true;

		startLocationTracking();

	}

	public void reStartLocationTrackingAndSavePoint(Route route) {
		this.route = route;
		this.flag_first = false;

		startLocationTracking();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// TODO Auto-generated method stub

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(activity,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			// showErrorDialog(connectionResult.getErrorCode());
		}

	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		flag_serviceRunning = true;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		// EINFACHSTE LÖSUNG. VIELLEICHT GEHT DA ABER AUCH ÜBER
		// ONLOCATIONCHANGED

		if (mLocationClient.isConnected()) {
			Toast.makeText(activity, "Der Client passt", Toast.LENGTH_SHORT)
					.show();
		}

		// If the location shall only be tracked when taking a picture this
		// routine has to be skipped
		if (flag_noService_Picture == true) {
			flag_noService_Picture = false;
			return;
		}

		Location location = mLocationClient.getLastLocation();

		if (location != null) {

			route.addRoutePointDB(new RoutePoint(route.getId(), timestamp,
					null, null, location.getLatitude(),
					location.getLongitude(), location.getAltitude()));

			previousLocation = location;

			if (flag_first == true) {
				mCallback.onNewRouteStarted(route);
			}

			// Display the connection status
			Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
			// If already requested, start periodic updates

			// Tracking aus --> kein locationlistener nötig
			if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 0) {
				return;
			}

			UPDATE_INTERVAL = Database
					.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL);
			FASTEST_INTERVAL = UPDATE_INTERVAL / 5;

			// Create the LocationRequest object
			mLocationRequest = LocationRequest.create();
			// Use high accuracy
			mLocationRequest
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			// Set the update interval to 5 seconds
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			// Set the fastest update interval to 1 second
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

			mLocationClient.requestLocationUpdates(mLocationRequest, this);

		} else {

			showPopUpEnableSettings();

			// // Display the connection status
			// Toast.makeText(activity, "Location couldn't be detected",
			// Toast.LENGTH_SHORT).show();
			// // If already requested, start periodic updates
		}

	}

	@Override
	public void onDisconnected() {

		flag_serviceRunning = false;

		// Turn off the request flag
		mInProgress = false;
		// Destroy the current location client
		mLocationClient = null;
		Toast.makeText(activity, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		// Report to the UI that the location was updated

		// refreshing that the service is still alive
		flag_serviceRunning = true;

		if (previousLocation != null) {
			if (location.distanceTo(previousLocation) < Database
					.getSettingValue(Database.SETTINGS_TRACKING_METER)) {
				Toast.makeText(activity, "Zu nahe am letzten Punkt",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		RoutePoint point = new RoutePoint(route.getId(), timestamp, null, null,
				location.getLatitude(), location.getLongitude(),
				location.getAltitude());
		// No picture
		route.addRoutePointDB(point);
		mCallback.onLocationChanged(route, point);
		// routeList.addRoute(route);

		previousLocation = location;

		String msg = "Updated Location: "
				+ Double.toString(location.getLatitude()) + ","
				+ Double.toString(location.getLongitude());
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {

		public LocationTrackerPLAYSERVICE getService() {
			return LocationTrackerPLAYSERVICE.this;
		}
	}

	// ONCREATE_SERVICE!
	@Override
	public void onCreate() {
		super.onCreate();

		mInProgress = false;

		// Check if google play service is available
		servicesAvailable = servicesConnected();

	}

	@Override
	public void onDestroy() {

		flag_serviceRunning = false;

		// Turn off the request flag
		mInProgress = false;
		if (servicesAvailable && mLocationClient != null) {
			mLocationClient.removeLocationUpdates(this);
			stopLocationTracking();

			// Destroy the current location client
			mLocationClient = null;
		}

		super.onDestroy();
	}

	/**
	 * Keeps the service running even after the app is closed.
	 * 
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (!servicesAvailable || mLocationClient.isConnected() || mInProgress)
			return START_STICKY;

		setUpLocationClientIfNeeded();
		if (!mLocationClient.isConnected() || !mLocationClient.isConnecting()
				&& !mInProgress) {
			// appendLog(DateFormat.getDateTimeInstance().format(new Date()) +
			// ": Started", Constants.LOG_FILE);
			mInProgress = true;
			mLocationClient.connect();
		}

		return START_STICKY;
	}

	/*
	 * Create a new location client, using the enclosing class to handle
	 * callbacks.
	 */
	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null)
			mLocationClient = new LocationClient(this, this, this);
	}

	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {

			return true;
		} else {

			Toast.makeText(activity,
					"ERROR: Google Play Services nicht verfügbar",
					Toast.LENGTH_SHORT).show();

			return false;
		}
	}

	public void refreshTrackingInterval() {
		Toast.makeText(this, "Interval refreshed", Toast.LENGTH_LONG).show();
		// Changing the interval parameters
		UPDATE_INTERVAL = Database
				.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL);
		FASTEST_INTERVAL = UPDATE_INTERVAL / 5;

	}

	public void restartLocationTracker() {

		mLocationClient.removeLocationUpdates(this);
		Toast.makeText(this, "Tracker restarted", Toast.LENGTH_LONG).show();

		// Only when gps tracking is enabled the periodical tracking is started
		if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 1) {

			// Set the update interval to 5 seconds
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			// Set the fastest update interval to 1 second
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

			// Start request with new params again
			mLocationClient.requestLocationUpdates(mLocationRequest, this);

		}

	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	private void showPopUpEnableSettings() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

		// Setting Dialog Title
		alertDialog.setTitle("Standort Einstellungen");

		// Setting Dialog Message
		alertDialog
				.setMessage("Mit den aktuellen Einstellungen, kann der Standort nicht bestimmt werden. Bedingt durch einen Betriebssystemfehler ist in manchen Fällen leider auch ein kompletter Neustart nötig ");

		// On pressing Settings button
		alertDialog.setPositiveButton("Einstellungen ändern",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						activity.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Abbrechen",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	public boolean isServiceRunning() {
		return flag_serviceRunning;
	}

}
