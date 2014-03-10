package de.smbsolutions.day.functions.location;

import java.util.List;

import de.smbsolutions.day.functions.initialization.Device;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocationTracker extends Service implements LocationListener {

	private final Context mContext;

	// Singleton
	private static LocationTracker tracker = null;

	private String bestProvider;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	double altitude; // altitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1; // 10 seconds

	// Declaring a Location Manager
	protected LocationManager locationManager;

	private LocationTracker(Context context) {
		this.mContext = context;

		locationManager = (LocationManager) mContext
				.getSystemService(LOCATION_SERVICE);

	}

	public static LocationTracker getInstance(Context context) {
		if (tracker == null)
			tracker = new LocationTracker(context);
		return tracker;
	}

	public boolean isProviderAvailable() {

		// Possible providers are: - GPS
		// - Network

		List<String> test = locationManager.getProviders(true);
		if (test.isEmpty() == true) {

			return false;

		} else {
			return true;
		}

	}

	private String getBestProvider() {

		// Getting the
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		return locationManager.getBestProvider(criteria, true);
	}

	public boolean enoughDistance() {
		double distance = 0;
		Location newLocation;
		newLocation = getLocation();

		if (location == null) {
			return true;
		}

		if (newLocation != null && location != null) {
			distance = location.distanceTo(newLocation);
		}

		if (distance > Device.getAPP_SETTINGS().getTrackingMeter()) {
			return true;
		} else {
			return false;
		}
	}

	public Location getLocation() {
		
		boolean gps_failed = false;
		try {

			if (isProviderAvailable() == false) {
				// Stop method and return no Location object
				return null;
			}

			String newProvider = getBestProvider();
		
			if (bestProvider == null) {
			// Save the new provider globally
			bestProvider = newProvider;	
//			// Start another listener with the new Provider
			locationManager.requestLocationUpdates(newProvider,
					MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
				this);

			
			}
			
			if(!(bestProvider.equals(newProvider))) {
				bestProvider = newProvider;	
//				// Start another listener with the new Provider
				locationManager.requestLocationUpdates(newProvider,
						MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
					this);

				
			}
			
			
			
//			
//			 //Logik muss zweimal durchlaufen werden:
//			 // 1. wenn zum ersten Mal der requestLocationUpdates gestartet wird.
//			if (bestProvider == null ) {
//				// Save the new provider globally
//				bestProvider = newProvider;	
//				
//				// Remove the old updateListener
//				locationManager.removeUpdates(LocationTracker.this);
//
////				// Start another listener with the new Provider
//				locationManager.requestLocationUpdates(newProvider,
//						MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
//					this);
//				stopUsingGPS();
//			}
//				
//			
//			
//			 // 2. Jedes mal wenn der Provider wechselt
//				if (!(bestProvider.equals(newProvider))) {
//				// Save the new provider globally
//				bestProvider = newProvider;
//
//				// Remove the old updateListener
//				locationManager.removeUpdates(LocationTracker.this);
//
////				// Start another listener with the new Provider
//				locationManager.requestLocationUpdates(newProvider,
//						MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
//					this);
//				stopUsingGPS();
//				}
			

			if (bestProvider.equals(LocationManager.GPS_PROVIDER)) {

				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					altitude = location.getAltitude();
				} else {
					gps_failed = true;
				}
			}

			if (bestProvider.equals(LocationManager.NETWORK_PROVIDER)
					| (gps_failed == true  && locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER))
			// Could be that the GPS was selected as the best provider, but
			// failed to get the location
			// Then the Networkprovider is used, if available

			) {
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					latitude = location.getAltitude();
				}

			}

			if (bestProvider.equals(LocationManager.PASSIVE_PROVIDER)
					| (gps_failed == true  && locationManager
							.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))) {

				location = locationManager
						.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					altitude = location.getAltitude();
				}

			}

		} catch (Exception e) {

			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
		}

		// if no provider was available, the old location will be returned
		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(LocationTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {

		// If getLocation is null, the method failed to get the latest location
		if (getLocation() != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {

		// If getLocation is null, the method failed to get the latest location
		if (getLocation() != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	public double getAltitude() {

		// If getLocation is null, the method failed to get the latest location
		if (getLocation() != null) {
			altitude = location.getAltitude();
		}

		// return longitude
		return altitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		if (isProviderAvailable()) {
			return true;
		} else {

			return false;
		}
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i("test", "onProviderDisabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i("test", "onProviderEnabled");
	}

	//
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

		Log.i("test", "onStatusChanged");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}