package de.smbsolutions.hike.presentation.activities;

import java.io.File;
import java.util.List;

import org.focuser.sendmelogs.LogCollector;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.hike.R;
import de.smbsolutions.hike.functions.database.Database;
import de.smbsolutions.hike.functions.initialization.Device;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.location.TrackingService;
import de.smbsolutions.hike.functions.location.TrackingService.LocalBinder;
import de.smbsolutions.hike.functions.objects.Route;
import de.smbsolutions.hike.functions.objects.RouteList;
import de.smbsolutions.hike.functions.objects.RoutePoint;
import de.smbsolutions.hike.functions.objects.SliderMenu;
import de.smbsolutions.hike.functions.tasks.CheckForceCloseTask;
import de.smbsolutions.hike.presentation.dialogs.CreateRouteDialog;
import de.smbsolutions.hike.presentation.dialogs.DeletePictureDialog;
import de.smbsolutions.hike.presentation.dialogs.DeleteRouteDialog;
import de.smbsolutions.hike.presentation.dialogs.DumpDetectionDialog;
import de.smbsolutions.hike.presentation.dialogs.PauseRouteDialog;
import de.smbsolutions.hike.presentation.dialogs.StopRouteDialog;
import de.smbsolutions.hike.presentation.fragments.DetailFragment;
import de.smbsolutions.hike.presentation.fragments.MainFragment;
import de.smbsolutions.hike.presentation.fragments.PictureFragment;

/**
 * 
 * Die MainActivity stellt das zentrale Grundgerüst der App dar. Von hier aus
 * werden alle Fragments gesteurert und angesprochen. Über das implementierte
 * Interface "MainCallback" findet die gesamte Kommunikation statt.
 * 
 */
public class MainActivity extends FragmentActivity implements MainCallback {
	// Tags, um Fragments voneinander unterscheiden zu können
	private final String TAG_DETAILFRAGMENT = "DETAIL";
	private final String TAG_MAINFRAGMENT = "MAIN";
	private final String TAG_PICTUREFRAGMENT = "PICTURE";
	private static String CURRENT_FRAGMENT = null;
	// Nav Drawer Titel
	private CharSequence mDrawerTitle;
	// wird genutzt um den App-Titel zu speichern
	private CharSequence mTitle;
	// Slidermenu
	private SliderMenu slidermenu;
	// Slider Menu Items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	// Der zentrale TrackingService
	private TrackingService mService = null;
	// Baut Verbindung mit Service auf
	// wird in onStart() und onStop() verwendet
	private ServiceConnection mConnection;
	// Flag: Überprüfung, ob eine Route aktiv ist
	private boolean activeRouteisOpened = false;
	// Sammelt LogInformationen
	private LogCollector mLogCollector;

	/** Wird aufgerufen wenn die Activity das erste Mal aufgerufen wird. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Initiert den Logcollector und startet den CheckForceCloseTask
		// Dieser überprüft, ob die App zuletzt korrekt geschlossen wurde.
		mLogCollector = new LogCollector(this);
		CheckForceCloseTask task = new CheckForceCloseTask(mLogCollector, this);
		task.execute();

		// Erstellen der Singletons, damit diese zur Laufzeit der App überall
		// zur Verfügung stehen.
		Database.getInstance(this);
		Device.getInstance(this);

		// Initiiert den TrackingService und bindet ihn an die Activity, sobald
		// onServiceConnected() aufgerufen wird.
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
		// Füllt den App Titel
		mTitle = mDrawerTitle = getTitle();
		// Läd alle Slidermenu Items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		// Läd die Slidermenu Icons
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);
		// Initialisiert das Slidermenu
		slidermenu = new SliderMenu(this, savedInstanceState);
		slidermenu.getNavDrawerItems();
		slidermenu.getAdapter();
		slidermenu.getActionBarDrawerToggle();

		if (savedInstanceState == null) {
			// Fügt der Activity das initiale Fragment hinzu.
			MainFragment main_frag = new MainFragment();

			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.add(R.id.frame_container, main_frag, TAG_MAINFRAGMENT)
					.addToBackStack(TAG_MAINFRAGMENT).commit();
			CURRENT_FRAGMENT = TAG_MAINFRAGMENT;
		}

	}

	/*
	 * SliderMenu-Methoden:
	 */
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

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
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (slidermenu.getActionBarDrawerToggle().onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Callback-Methoden:
	 * 
	 * Genauer Informationen zu den Methoden stehen im Callback Interface.
	 */
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

	}

	@Override
	public void onShowRoute(Route route) {

		/*
		 * Löscht doppelte Fragments
		 */
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
		// Erstellt DetailFragment
		DetailFragment detail_frag = new DetailFragment();

		// Wenn die Route aktiv ist, muss der Service überprüft werden
		if (route.isActive()) {

			// Wenn der Service nicht erstellt ist , muss er neugestartet werden
			if (mService == null) {
				restartTracking(route);

				// Auch wenn er erstellt ist, aber nicht läuft, muss er
				// neugestartet werden
			} else if (mService.isServiceRunning() == false) {
				restartTracking(route);
			}
		}

		// Parameterübertragung
		Bundle bundle = new Bundle();
		bundle.putParcelable("route", route);
		detail_frag.setArguments(bundle);

		// Detailfragment wird gestartet
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		ft.replace(R.id.frame_container, detail_frag, TAG_DETAILFRAGMENT);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(TAG_DETAILFRAGMENT);
		ft.commit();

	}

	@Override
	public void onOpenDialogNewRoute(RouteList routeList) {

		CreateRouteDialog dialog = new CreateRouteDialog();

		Bundle bundle = new Bundle();
		bundle.putParcelable("routeList", routeList);
		dialog.setArguments(bundle);

		// Schon bevor der Benutzer die Route bestätigt, wird hier der Service
		// gestartet
		// Dies ist aus Laufzeitgründen nötig
		Intent intent = new Intent(this, TrackingService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		// Dialog wird angezeigt
		dialog.show(getSupportFragmentManager(), "NameDialog");

	}

	@Override
	public void onOpenDialogDeleteRoute(RouteList routeList, int index) {

		DeleteRouteDialog dialog = new DeleteRouteDialog();

		Bundle bundle = new Bundle();
		bundle.putInt("routeIndex", index);
		bundle.putParcelable("routeList", routeList);
		dialog.setArguments(bundle);

		dialog.show(getSupportFragmentManager(), "DeleteDialog");

	}

	@Override
	public void onOpenDialogStopRoute(String fragmentFlag, Route route) {

		StopRouteDialog dialog = new StopRouteDialog();

		Bundle bundle = new Bundle();
		bundle.putParcelable("route", route);
		bundle.putString("fragmentFlag", fragmentFlag);
		dialog.setArguments(bundle);

		dialog.show(getSupportFragmentManager(), "StopRouteDialog");

	}

	@Override
	public void onRouteDeleted() {

		// Löscht altes Mainfragment
		if (getSupportFragmentManager()
				.getBackStackEntryAt(
						getSupportFragmentManager().getBackStackEntryCount() - 1)
				.getName().equals(TAG_MAINFRAGMENT)) {
			Fragment oldFrag = getSupportFragmentManager().findFragmentByTag(
					getSupportFragmentManager().getBackStackEntryAt(
							getSupportFragmentManager()
									.getBackStackEntryCount() - 1).getName());
			getSupportFragmentManager().beginTransaction().remove(oldFrag)
					.commit();

			getSupportFragmentManager().popBackStack();

		}

		// Fügt das neue Mainfragment hinzu
		CURRENT_FRAGMENT = TAG_MAINFRAGMENT;
		MainFragment mainfrag = new MainFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, mainfrag, TAG_MAINFRAGMENT)
				.addToBackStack(TAG_MAINFRAGMENT).commit();

	}

	@Override
	public void onDeletePicture(Route route) {

		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		DetailFragment crFrag = new DetailFragment();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		crFrag.setArguments(bundle);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.frame_container, crFrag, TAG_DETAILFRAGMENT)
				.addToBackStack(TAG_DETAILFRAGMENT).commit();

	}

	@Override
	public void onDeletePictureClick(Route route, RoutePoint point) {

		DeletePictureDialog deletePictureDialog = new DeletePictureDialog();

		Bundle bundle = new Bundle();
		bundle.putParcelable("route", route);
		bundle.putParcelable("point", point);
		deletePictureDialog.setArguments(bundle);

		deletePictureDialog.show(getSupportFragmentManager(),
				"DeletePictureDialog");

	}

	@Override
	public void onRouteStopped(String fragmentTag, Route route) {

		// Überpüfung ob ob das aktuelle Fragment ein Detail oder Mainfragment
		// ist.
		// Falls ja, wird es gelöscht
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

		/*
		 * Hinzufügen des richtigen Fragments
		 */
		if (fragmentTag.equals(TAG_MAINFRAGMENT)) {
			CURRENT_FRAGMENT = TAG_MAINFRAGMENT;

			MainFragment mainfrag = new MainFragment();

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.frame_container, mainfrag, TAG_MAINFRAGMENT)
					.addToBackStack(TAG_MAINFRAGMENT).commit();

			// Wenn die Route gestoppt wurde, muss der Service beendet werden
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

			// Wenn die Route gestoppt wurde, muss der Service beendet werden
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

		// Wenn es sich unter einen Einstellungsfragment noch ein
		// Einstellungsfragment befindet, wird dieses vom Stack gelöscht.
		// Somit gelangt der Benutzer wieder direkt auf Main/Detailfragment
		// zurück
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
		// Detailfragment wird neugestartet
		DetailFragment crFrag = new DetailFragment();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		crFrag.setArguments(bundle);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.frame_container, crFrag, TAG_DETAILFRAGMENT)
				.addToBackStack(TAG_DETAILFRAGMENT).commit();

	}

	@Override
	public void onPictureClick(Route route, RoutePoint point) {

		CURRENT_FRAGMENT = TAG_PICTUREFRAGMENT;
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

	/**
	 * Wird aufgerufen wenn der Benutzer den Zurück-Button betätigt
	 */
	@Override
	public void onBackPressed() {

		// Alle Fragments in eine Liste
		List<Fragment> list = getSupportFragmentManager().getFragments();

		// Abfrage wieviel fragments es gibt,
		// weil auch null Objekte vorhanden sein könnten
		int count = 0;
		for (Fragment fragment : list) {
			count++;
		}

		/*
		 * 
		 * Der folgende Block garantiert, dass im CURRENT_FRAGMENT tag immer der
		 * Tag des aktuellen Fragments steht
		 */

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
		// Globales Flag zum Status der Route wird gesetzt.
		onRouteOpenend(true);
		if (mService != null) {

			mService.saveActivity(this);
			mService.startLocationTrackingAndSaveFirst(route);
		}
	}

	@Override
	public void onPictureTaken(Route route, Uri fileUri, File small_picture) {

		if (mService != null) {
			mService.addPictureLocation(route, fileUri, small_picture);
		}
	}

	@Override
	public void removeService() {

		if (mService != null) {
			unbindService(mConnection);
			mService = null;
		}
	}

	@Override
	public void onActiveRouteNoService() {

		// Aktive Routen sollten immer einen aktiven Service haben
		// Wenn nicht, wurde die App bespielsweise beendet. DAnn wird der
		// Service jetzt neu gestartet
		if (mService == null) {

			// Service neu gestartet
			Intent intent = new Intent(this, TrackingService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

			// Auch ist es möglich, dass der Service erstellt wurde,
			// aber nicht mehr läuft
		} else if (mService.isServiceRunning() == false) {
			Intent intent = new Intent(this, TrackingService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onOpenDialogPauseRoute(Route route) {

		PauseRouteDialog dialog = new PauseRouteDialog();
		Bundle bundle = new Bundle();
		dialog.setArguments(bundle);

		dialog.show(getSupportFragmentManager(), "PauseRouteDialog");
	}

	@Override
	public void onTrackingIntervalChanged() {

		if (mService != null) {

			// Der Benutzer hat die Werte geändert, also muss der Service
			// angepasst werden
			mService.refreshTrackingInterval();

			if (mService.isServiceRunning()) {

				// Neustarten des Services mit den neuen Werten
				mService.restartLocationTracker();

			}
		}
	}

	@Override
	public void onTrackingTurnedOnOff() {

		if (mService != null) {

			if (mService.isServiceRunning()) {

				// Neustarten des Services mit dem neuen Wert
				mService.restartLocationTracker();
			}
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

	@Override
	public void onLocationChanged(Route route, RoutePoint point) {

		Fragment frag = getSupportFragmentManager().findFragmentByTag(
				CURRENT_FRAGMENT);
		// Wenn es sich um das Detailfragment handelt, wird der InfoSlider
		// refresht
		if (frag instanceof DetailFragment) {
			((DetailFragment) frag).refreshInfoSlider();
		}

		// Nur wenn eine aktive Route im Detailmodus angezeigt wird, sollen die
		// Polylines ergänzt werden
		// Sonst würden diese auch der nichtaktiven Route hinzugefügt werden
		if (activeRouteisOpened == true) {
			GoogleMap map = getMapForRefresh();
			if (map != null) {
				route.addPoint2Polyline(point, map);
			}
		}
	}

	@Override
	public void refreshSliderMenu() {

		slidermenu.removeSelectedItem();

	}

	@Override
	public void onRouteOpenend(boolean active) {

		// Globales Flag um zu wissen, ob gerade eine aktive Route im Detailview
		// geöffnet ist
		if (active == true) {
			activeRouteisOpened = true;

		} else {
			activeRouteisOpened = false;
		}

	}

	@Override
	public void onDumpDetected() {
		mLogCollector.sendLog("hikelogging@gmail.com", "Log",
				"Folgender Log wurde gespeichert:");

	}

	@Override
	public void onDumpDialogShow() {
		DumpDetectionDialog dumpDialog = new DumpDetectionDialog();
		dumpDialog.show(getSupportFragmentManager(), "DUMPDIALOG");

	}

	/*
	 * Returned - wenn vorhanden - die aktuelle Map
	 */
	private GoogleMap getMapForRefresh() {
		SupportMapFragment mapFragment;
		Fragment frag = getSupportFragmentManager().findFragmentByTag(
				CURRENT_FRAGMENT);

		// Wenn das Mainfragment geöffnet ist, wird null zurück gegeben.
		// Denn dieses darf durch den Service nicht aktualisiert werden
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
	protected void onDestroy() {

		super.onDestroy();

		// Wenn App beendet wird, muss auch der Service beendet werden
		if (mService != null) {
			unbindService(mConnection);
			mService = null;

		}

	}
}
