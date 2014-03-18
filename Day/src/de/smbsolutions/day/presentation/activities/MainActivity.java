package de.smbsolutions.day.presentation.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.initialization.Device;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.location.LocationTrackerPLAYSERVICE;
import de.smbsolutions.day.functions.location.LocationTrackerPLAYSERVICE.LocalBinder;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.objects.SliderMenu;
import de.smbsolutions.day.functions.tasks.MarkerWorkerTask;
import de.smbsolutions.day.presentation.dialogs.CreateRouteDialog;
import de.smbsolutions.day.presentation.dialogs.DeletePictureDialog;
import de.smbsolutions.day.presentation.dialogs.DeleteRouteDialog;
import de.smbsolutions.day.presentation.dialogs.PauseRouteDialog;
import de.smbsolutions.day.presentation.dialogs.StopRouteDialog;
import de.smbsolutions.day.presentation.fragments.DetailFragment;
import de.smbsolutions.day.presentation.fragments.MainFragment;
import de.smbsolutions.day.presentation.fragments.PictureFragment;

public class MainActivity extends FragmentActivity implements MainCallback {

	private final String TAG_DETAILFRAGMENT = "DETAIL";
	private final String TAG_MAINFRAGMENT = "MAIN";
	private final String TAG_PICTUREFRAGMENT = "PICTURE";
	private static String CURRENT_FRAGMENT = null;
	
	private int NOTIFICATION_ID;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private int backstackcount;
	private int fragmentCount = 0;
	private ActionBarDrawerToggle mDrawerToggle;
	// nav drawer title
	private CharSequence mDrawerTitle;
	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private SliderMenu slidermenu;
	private LocationTrackerPLAYSERVICE mService = null;
	// wird in onStart() und onStop() verwendet
	private ServiceConnection mConnection;
		private boolean activeRouteisOpened = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		

		// Get Singetons
		Database.getInstance(this);
		Device.getInstance(this);
		
		
		// Service
		mConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				LocalBinder binder = (LocalBinder) service;
				mService = binder.getService();
				// Toast.makeText(getApplicationContext(), "Service angebunden",
						// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				mService = null;
			}
		};

		mTitle = mDrawerTitle = getTitle();
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);
		// Menu
		slidermenu = new SliderMenu(this, savedInstanceState);
		slidermenu.getNavDrawerItems();
		slidermenu.getAdapter();
		slidermenu.getActionBarDrawerToggle();

		if (savedInstanceState == null) {
			MainFragment main_frag = new MainFragment();

			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.add(R.id.frame_container, main_frag, TAG_MAINFRAGMENT)
					.addToBackStack(TAG_MAINFRAGMENT).commit();
			CURRENT_FRAGMENT = TAG_MAINFRAGMENT;
		}

	}

	@Override
	public void onNewRouteStarted(Route route) {
		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		DetailFragment crFrag = new DetailFragment();
		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		// Übergabe Index selektierte Route
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		crFrag.setArguments(bundle);
		ft.replace(R.id.frame_container, crFrag, TAG_DETAILFRAGMENT)
				.addToBackStack(TAG_DETAILFRAGMENT).commit();

		fragmentCount++;
		Log.wtf("fragCount", "Anzahl Aufrufe: " + String.valueOf(fragmentCount));

	}

	@Override
	public void onShowRoute(Route route) {

		Fragment detfrag = getSupportFragmentManager().findFragmentByTag(
				TAG_DETAILFRAGMENT);
		Fragment picfrag = getSupportFragmentManager().findFragmentByTag(
				TAG_PICTUREFRAGMENT);
		if (detfrag != null) {
			getSupportFragmentManager().beginTransaction().remove(detfrag)
					.commit();
			getSupportFragmentManager().popBackStack();

		}
		if (picfrag != null) {
			getSupportFragmentManager().beginTransaction().remove(picfrag)
					.commit();
			getSupportFragmentManager().popBackStack();
		}

		DetailFragment detail_frag = new DetailFragment();

		// // Only if tracking via service is enabled
		// if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 1) {

		if (route.isActive()) {

			// Active route should always have an active service
			// --> If not, the app was closed meanwhile,
			if (mService == null) {
				restartTracking(route);

				// Service has been created, but is not active any longer
				// Some error occured -> try it again
			} else if (mService.isServiceRunning() == false) {
				restartTracking(route);
			}

		}
		Bundle bundle = new Bundle();
		bundle.putParcelable("route", route);
		detail_frag.setArguments(bundle);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		ft.replace(R.id.frame_container, detail_frag, TAG_DETAILFRAGMENT);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(TAG_DETAILFRAGMENT);
		ft.commit();
		fragmentCount++;

		Log.wtf("fragCount", "Anzahl Aufrufe: " + String.valueOf(fragmentCount));

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onOpenDialogNewRoute(RouteList routeList) {
		CreateRouteDialog dialog = new CreateRouteDialog();
		Bundle bundle = new Bundle();

		bundle.putParcelable("routeList", routeList);
		dialog.setArguments(bundle);

		// AUS PERFORMANCEGRÜNDEN SERVICE SCHONMAL STARTEN

		// WENN BENUTZER DEN DIALOG VERNEINT MUSS ER WIEDER BEENDET WERDEN
		Intent intent = new Intent(this, LocationTrackerPLAYSERVICE.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		// Showing the popup / Second Parameter: Unique Name, that is
		// used
		// to identify the dialog
		dialog.show(getSupportFragmentManager(), "NameDialog");

	}

	@Override
	public void onOpenDialogDeleteRoute(RouteList routeList, int index) {

		DeleteRouteDialog dialog = new DeleteRouteDialog();
		Bundle bundle = new Bundle();
		bundle.putInt("routeIndex", index);
		bundle.putParcelable("routeList", routeList);
		dialog.setArguments(bundle);
		// Showing the popup / Second Parameter: Unique Name, that is
		// used
		// to identify the dialog
		dialog.show(getSupportFragmentManager(), "DeleteDialog");

	}

	@Override
	public void onOpenDialogStopRoute(String fragmentFlag, Route route) {

		StopRouteDialog dialog = new StopRouteDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable("route", route);
		bundle.putString("fragmentFlag", fragmentFlag);
		dialog.setArguments(bundle);
		// Showing the popup / Second Parameter: Unique Name, that is
		// used
		// to identify the dialog
		dialog.show(getSupportFragmentManager(), "StopRouteDialog");

	}

	@Override
	public void onRouteDeleted() {
		CURRENT_FRAGMENT = TAG_MAINFRAGMENT;
		MainFragment mainfrag = new MainFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, mainfrag, TAG_MAINFRAGMENT)
				.addToBackStack(TAG_MAINFRAGMENT).commit();

	}

	// GLEICH WIE SHOWROUTE
	@Override
	public void onDeletePicture(Route route) {
		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		// fragment avaiable?
		DetailFragment crFrag = new DetailFragment();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		crFrag.setArguments(bundle);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.replace(R.id.frame_container, crFrag, TAG_DETAILFRAGMENT)
				.addToBackStack(TAG_DETAILFRAGMENT).commit();
		fragmentCount++;

		Log.wtf("fragCount", "Anzahl Aufrufe: " + String.valueOf(fragmentCount));

	}

	@Override
	public void onDeletePictureClick(Route route, RoutePoint point) {

		DeletePictureDialog deletePictureDialog = new DeletePictureDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable("route", route);
		bundle.putParcelable("point", point);
		deletePictureDialog.setArguments(bundle);
		// Showing the popup / Second Parameter: Unique Name, that is
		// used
		// to identify the dialog
		deletePictureDialog.show(getSupportFragmentManager(),
				"DeletePictureDialog");

	}

	// GLEICH WIE DELETE ROUTE
	@Override
	public void onRouteStopped(String fragmentTag, Route route) {

		if (getSupportFragmentManager()
				.getBackStackEntryAt(
						getSupportFragmentManager().getBackStackEntryCount() - 1)
				.getName().equals(TAG_DETAILFRAGMENT)
				|| getSupportFragmentManager()
						.getBackStackEntryAt(
								getSupportFragmentManager()
										.getBackStackEntryCount() - 1)
						.getName().equals(TAG_MAINFRAGMENT)) {
			Fragment oldFrag = getSupportFragmentManager().findFragmentByTag(
					getSupportFragmentManager().getBackStackEntryAt(
							getSupportFragmentManager()
									.getBackStackEntryCount() - 1).getName());
			getSupportFragmentManager().beginTransaction().remove(oldFrag)
					.commit();

			getSupportFragmentManager().popBackStack();

		}

		if (fragmentTag.equals(TAG_MAINFRAGMENT)) {
			CURRENT_FRAGMENT = TAG_MAINFRAGMENT;

			MainFragment mainfrag = new MainFragment();

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.frame_container, mainfrag, TAG_MAINFRAGMENT)
					.addToBackStack(TAG_MAINFRAGMENT).commit();

			if (mService != null) {
				unbindService(mConnection);
				mService = null;
			}
		} else if (fragmentTag.equals(TAG_DETAILFRAGMENT)) {
			CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;

			Bundle bundle = new Bundle();
			bundle.putParcelable("route", route);
			DetailFragment detailfrag = new DetailFragment();
			detailfrag.setArguments(bundle);

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.frame_container, detailfrag,
							TAG_DETAILFRAGMENT)
					.addToBackStack(TAG_DETAILFRAGMENT).commit();

			if (mService != null) {
				unbindService(mConnection);
				mService = null;
			}
		}

	}

	@Override
	public void onSliderClick(Fragment frag) {

		String slidertag = frag.getClass().getName();
		CURRENT_FRAGMENT = slidertag;
		String name = getSupportFragmentManager().getBackStackEntryAt(
				getSupportFragmentManager().getBackStackEntryCount() - 1)
				.getName();
		if (!name.equals(TAG_DETAILFRAGMENT) && !name.equals(TAG_MAINFRAGMENT)) {
			Fragment oldFrag = getSupportFragmentManager().findFragmentByTag(
					name);
			getSupportFragmentManager().beginTransaction().remove(oldFrag)
					.commit();

			getSupportFragmentManager().popBackStack();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.frame_container, frag, slidertag)
					.addToBackStack(slidertag).commit();

		} else {
			CURRENT_FRAGMENT = slidertag;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.frame_container, frag, slidertag)
					.addToBackStack(slidertag).commit();

		}

	}

	@Override
	public void onCamStart(Route route) {
		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		DetailFragment crFrag = new DetailFragment();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		crFrag.setArguments(bundle);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.replace(R.id.frame_container, crFrag, TAG_DETAILFRAGMENT)
				.addToBackStack(TAG_DETAILFRAGMENT).commit();
		fragmentCount++;

		Log.wtf("fragCount", "Anzahl Aufrufe: " + String.valueOf(fragmentCount));

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		slidermenu.getActionBarDrawerToggle().syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		slidermenu.getActionBarDrawerToggle().onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (slidermenu.getActionBarDrawerToggle().onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPictureClick(Route route, RoutePoint point) {
		CURRENT_FRAGMENT = TAG_PICTUREFRAGMENT;
		// fragment avaiable?
		PictureFragment pictureFrag = new PictureFragment();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		bundle.putParcelable("point", point);
		pictureFrag.setArguments(bundle);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.replace(R.id.frame_container, pictureFrag, TAG_PICTUREFRAGMENT)
				.addToBackStack(TAG_PICTUREFRAGMENT).commit();

	}

	@Override
	public void onBackPressed() {
		// change current_fragment to right on for changing the map type
		List<Fragment> list = getSupportFragmentManager().getFragments();

		int count = 0;
		for (Fragment fragment : list) {
			count++;
		}

		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			moveTaskToBack(true);
		} else {
			if (count >= 3) {
				if (list.get(2) != null) {
					if (list.get(list.size() - 2) instanceof MainFragment) {
						CURRENT_FRAGMENT = TAG_MAINFRAGMENT;
					} else {
						CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
					}
				} else {
					if (list.get(list.size() - 3) instanceof MainFragment) {

						CURRENT_FRAGMENT = TAG_MAINFRAGMENT;
					} else {
						CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
					}
				}

			} else {
				if (list.get(list.size() - 1) instanceof DetailFragment) {
					CURRENT_FRAGMENT = TAG_MAINFRAGMENT;

				} else if (list.get(list.size() - 1) instanceof MainFragment) {
					CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;

				} else {
					CURRENT_FRAGMENT = TAG_MAINFRAGMENT;
				}
			}

			super.onBackPressed();
		}

	}

	@Override
	public void onRefreshMap() {

		Fragment frag = getSupportFragmentManager().findFragmentByTag(
				CURRENT_FRAGMENT);
		if (frag != null) {
			FragmentManager fm = frag.getChildFragmentManager();
			SupportMapFragment mapfrag = (SupportMapFragment) fm
					.findFragmentById(R.id.map);

			if (mapfrag != null) {
				GoogleMap map = mapfrag.getMap();
				if (map.getMapType() != Device.getAPP_SETTINGS().getMapType()) {
					map.setMapType(Device.getAPP_SETTINGS().getMapType());
				}

			} else {
				mapfrag = (SupportMapFragment) fm.findFragmentById(R.id.cr_map);
				if (mapfrag != null) {
					GoogleMap map = mapfrag.getMap();
					if (map.getMapType() != Device.getAPP_SETTINGS()
							.getMapType()) {
						map.setMapType(Device.getAPP_SETTINGS().getMapType());

					}
				}
			}
		}

	}

	@Override
	public void onStartTrackingService(Route route) {

		if (mService != null) {
			mService.saveActivity(this);
			// Warum liste und route übergeben??
			mService.startLocationTrackingAndSaveFirst(route);

		} else {
			// Toast.makeText(this, "Service wurde nicht gestartet",
					// Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onPictureTaken(Route route, Uri fileUri, File small_picture) {
		if (mService != null) {

			mService.addPictureLocation(route, fileUri, small_picture);

		} else {
			// Toast.makeText(this, "Service ist null", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void removeService() {

		// Stop service, because it has been started when the user pressed the
		// create route button
		// -> But now he decided to cancel to process
		if (mService != null) {
			unbindService(mConnection);
			mService = null;
		}

	}

	@Override
	public void onActiveRouteNoService() {

		// Active route should always have an active service
		// --> If not, the app was closed meanwhile,
		if (mService == null) {
			// Start service again
			Intent intent = new Intent(this, LocationTrackerPLAYSERVICE.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

			// Service has been created, but is not active any longer
			// Some error occured -> try it again
		} else if (mService.isServiceRunning() == false) {
			Intent intent = new Intent(this, LocationTrackerPLAYSERVICE.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		}

	}

	@Override
	public void onOpenDialogPauseRoute(Route route) {

		PauseRouteDialog dialog = new PauseRouteDialog();
		Bundle bundle = new Bundle();

		// MOMENTAN WIRD AN DER ROUTE SELBST JA GARNICHTS GEÄNDERT; NUR SERVICE
		// GESTOPPT
		dialog.setArguments(bundle);
		// Showing the popup / Second Parameter: Unique Name, that is
		// used
		// to identify the dialog
		dialog.show(getSupportFragmentManager(), "PauseRouteDialog");

	}

	@Override
	public void onTrackingIntervalChanged() {

		if (mService != null) {

			// Always refresh the values
			mService.refreshTrackingInterval();

			if (mService.isServiceRunning()) {

				// Restart tacker only when really running
				mService.restartLocationTracker();

			}
		}

	}

	@Override
	public void onTrackingTurnedOnOff() {
		// TODO Auto-generated method stub

		if (mService != null) {

			if (mService.isServiceRunning()) {

				// Restart tacker only when really running
				mService.restartLocationTracker();

			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mService != null) {
			unbindService(mConnection);
			mService = null;
			// Toast.makeText(this, "MainActivity destroyed -> Service unbinded",
					// Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean isServiceActive() {

		if (mService != null) {

			if (mService.isServiceRunning()) {

				return true;

			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void restartTracking(Route route) {

		mService.saveActivity(this);
		mService.reStartLocationTrackingAndSavePoint(route);

	}

	@Override
	public Fragment getpreviousFragment() {
		Fragment previousFragment = getSupportFragmentManager().getFragments()
				.get(getSupportFragmentManager().getFragments().size() - 2);
		return previousFragment;
	}

	public GoogleMap getCurrentMap() {
		SupportMapFragment mapFragment;
		Fragment frag = getSupportFragmentManager().findFragmentByTag(
				CURRENT_FRAGMENT);
		if (frag instanceof MainFragment) {
			return null;
		} else {
			mapFragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentByTag(CURRENT_FRAGMENT)
					.getChildFragmentManager().findFragmentById(R.id.cr_map);
		}

		if (mapFragment != null) {
			GoogleMap map = mapFragment.getMap();
			return map;
		} else
			return null;

	}

@Override
	public void onLocationChanged(Route route, RoutePoint point) {

		SupportMapFragment mapFragment;
		Fragment frag = getSupportFragmentManager().findFragmentByTag(
				CURRENT_FRAGMENT);
		if (frag instanceof DetailFragment) {
			((DetailFragment) frag).refreshInfoSlider();
		}

		  // Nur wenn eine aktive Route im Detailmodus angezeigt wird, sollen die Polylines ergänzt werden
		  // Sonst würden diese auch der nichtaktiven Route hinzugefügt werden
         if (activeRouteisOpened == true) {
			GoogleMap map = getCurrentMap();
			if (map != null) {
				route.addPoint2Polyline(point, map);
			}
			
         }
         
         
         
         
//         NotificationCompat.Builder mBuilder =
//        	        new NotificationCompat.Builder(this)
//        	        .setSmallIcon(R.drawable.ic_launcher)
//        	        .setContentTitle("Hike trackt")
//        	        .setContentText(route.getRouteName() + " läuft gerade");
//        	// Creates an explicit intent for an Activity in your app
//        	Intent resultIntent = this.getIntent();
//
//        	// The stack builder object will contain an artificial back stack for the
//        	// started Activity.
//        	// This ensures that navigating backward from the Activity leads out of
//        	// your application to the Home screen.
//        	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        	// Adds the back stack for the Intent (but not the Intent itself)
//        	stackBuilder.addParentStack(MainActivity.class);
//        	// Adds the Intent that starts the Activity to the top of the stack
//        	stackBuilder.addNextIntent(resultIntent);
//        	PendingIntent resultPendingIntent =
//        	        stackBuilder.getPendingIntent(
//        	            0,
//        	            PendingIntent.FLAG_UPDATE_CURRENT
//        	        );
//        	mBuilder.setContentIntent(resultPendingIntent);
//        	NotificationManager mNotificationManager =
//        	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        	// mId allows you to update the notification later on.
//        
//        	mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//         
         
         

		

	}

	@Override
	public void refreshSliderMenu() {

		slidermenu.removeSelectedItem();

	}

	@Override
	public void onRouteOpenend(boolean active) {
	
		// Globales Flag um zu wissen, ob gerade eine aktive Route im Detailview geöffnet ist
		if (active == true) {
			
			activeRouteisOpened = true;
			
		} else {
			activeRouteisOpened = false;
			
		}
		
	}
}
