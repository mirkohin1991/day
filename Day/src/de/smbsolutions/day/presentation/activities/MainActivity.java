package de.smbsolutions.day.presentation.activities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.initialization.Device;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.objects.SliderMenu;
import de.smbsolutions.day.presentation.dialogs.DeletePictureDialog;
import de.smbsolutions.day.presentation.dialogs.DeleteRouteDialog;
import de.smbsolutions.day.presentation.dialogs.RouteNameDialog;
import de.smbsolutions.day.presentation.dialogs.StopRouteDialog;
import de.smbsolutions.day.presentation.fragments.DetailFragment;
import de.smbsolutions.day.presentation.fragments.MainFragment;
import de.smbsolutions.day.presentation.fragments.PictureFragment;

public class MainActivity extends FragmentActivity implements MainCallback {

	private List<WeakReference<Fragment>> refFragments = new ArrayList<WeakReference<Fragment>>();
	private final static String TAG_DETAILFRAGMENT = "DETAIL";
	private final static String TAG_MAINFRAGMENT = "MAIN";
	private final static String TAG_PICTUREFRAGMENT = "PICTURE";
	private static String CURRENT_FRAGMENT = null;
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


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get Singetons
		Database.getInstance(this);
		Device.getInstance(this);

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

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
	
		DetailFragment crFrag = new DetailFragment();
		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		// Übergabe Index selektierte Route
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		crFrag.setArguments(bundle);
		ft.replace(R.id.frame_container, crFrag, TAG_DETAILFRAGMENT)
				.addToBackStack(TAG_DETAILFRAGMENT).commit();
		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		fragmentCount++;
		Log.wtf("fragCount", "Anzahl Aufrufe: " + String.valueOf(fragmentCount));

	}

	@Override
	public void onShowRoute(Route route) {

		DetailFragment detail_frag = new DetailFragment();

		// fragment not in back
		// stack, create it.

		Bundle bundle = new Bundle();
		bundle.putParcelable("route", route);
		detail_frag.setArguments(bundle);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.frame_container, detail_frag, TAG_DETAILFRAGMENT);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(TAG_DETAILFRAGMENT);
		ft.commit();
		fragmentCount++;
		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		Log.wtf("fragCount", "Anzahl Aufrufe: " + String.valueOf(fragmentCount));

	}

	@Override
	public void onOpenDialogNewRoute(RouteList routeList) {

		RouteNameDialog dialog = new RouteNameDialog();
		Bundle bundle = new Bundle();

		bundle.putSerializable("routeList", routeList);
		dialog.setArguments(bundle);
		// Showing the popup / Second Parameter: Unique Name, that is
		// used
		// to identify the dialog
		dialog.show(getSupportFragmentManager(), "NameDialog");

	}

	@Override
	public void onLongItemSelected(RouteList routeList, int index) {

		DeleteRouteDialog dialog = new DeleteRouteDialog();
		Bundle bundle = new Bundle();
		bundle.putInt("routeIndex", index);
		bundle.putSerializable("routeList", routeList);
		dialog.setArguments(bundle);
		// Showing the popup / Second Parameter: Unique Name, that is
		// used
		// to identify the dialog
		dialog.show(getSupportFragmentManager(), "DeleteDialog");

	}

	@Override
	public void onStopPopup(RouteList routeList) {

		StopRouteDialog dialog = new StopRouteDialog();
		Bundle bundle = new Bundle();
		bundle.putSerializable("routeList", routeList);
		dialog.setArguments(bundle);
		// Showing the popup / Second Parameter: Unique Name, that is
		// used
		// to identify the dialog
		dialog.show(getSupportFragmentManager(), "StopRouteDialog");

	}

	@Override
	public void onDeleteRoute() {

		MainFragment mainfrag = new MainFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, mainfrag, TAG_MAINFRAGMENT)
				.addToBackStack(TAG_MAINFRAGMENT).commit();
		CURRENT_FRAGMENT = TAG_MAINFRAGMENT;

	}

	// GLEICH WIE SHOWROUTE
	@Override
	public void onDeletePicture(Route route) {

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
		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
		Log.wtf("fragCount", "Anzahl Aufrufe: " + String.valueOf(fragmentCount));

	}

	@Override
	public void onLongPictureClick(Route route, RoutePoint point) {

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
	public void onStopRoute() {
		MainFragment mainfrag = new MainFragment();

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, mainfrag, TAG_MAINFRAGMENT)
				.addToBackStack(TAG_MAINFRAGMENT).commit();
		CURRENT_FRAGMENT = TAG_MAINFRAGMENT;

	}

	@Override
	public void onSliderClick(Fragment frag) {

		String slidertag = frag.getClass().getName();

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
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.frame_container, frag, slidertag)
					.addToBackStack(slidertag).commit();
		}

	}

	@Override
	public void onCamStart(Route route) {

		DetailFragment crFrag = new DetailFragment();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		crFrag.setArguments(bundle);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.replace(R.id.frame_container, crFrag, TAG_DETAILFRAGMENT)
				.addToBackStack(TAG_DETAILFRAGMENT).commit();
		fragmentCount++;
		CURRENT_FRAGMENT = TAG_DETAILFRAGMENT;
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

		// fragment avaiable?
		PictureFragment pictureFrag = new PictureFragment();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		bundle.putParcelable("point", point);
		pictureFrag.setArguments(bundle);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.replace(R.id.frame_container, pictureFrag, TAG_PICTUREFRAGMENT)
				.commit();
		CURRENT_FRAGMENT = TAG_PICTUREFRAGMENT;
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		backstackcount = getSupportFragmentManager().getBackStackEntryCount();
		Log.wtf("Backstackcount:", String.valueOf(backstackcount));
		refFragments.add(new WeakReference<Fragment>(fragment));
		super.onAttachFragment(fragment);
	}

	@Override
	public void onBackPressed() {
		recycleFragments();
		super.onBackPressed();

	}

	@Override
	public void onShowFullPicture(Route route) {
		// TODO Was soll hier geschehen?

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
				if (map.getMapType() != Device.getAPP_SETTINGS().getMAP_TYPE()) {
					map.setMapType(Device.getAPP_SETTINGS().getMAP_TYPE());
				}

			} else {
				mapfrag = (SupportMapFragment) fm.findFragmentById(R.id.cr_map);
				if (mapfrag != null) {
					GoogleMap map = mapfrag.getMap();
					if (map.getMapType() != Device.getAPP_SETTINGS()
							.getMAP_TYPE()) {
						map.setMapType(Device.getAPP_SETTINGS().getMAP_TYPE());

					}
				}
			}
		}

	}

	private void recycleFragments() {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		for (WeakReference<Fragment> ref : refFragments) {
			Fragment fragment = ref.get();
			if (fragment != null && !(fragment instanceof MainFragment)) {

				ft.remove(fragment);
			}
		}

		ft.commit();

	}

}