package de.smbsolutions.day.presentation.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.presentation.fragments.DetailFragment;
import de.smbsolutions.day.presentation.fragments.MainFragment;
import de.smbsolutions.day.presentation.popups.DeleteDialog;
import de.smbsolutions.day.presentation.popups.RouteNameDialog;
import de.smbsolutions.day.presentation.popups.StopRouteDialog;
import de.smbsolutions.day.presentation.views.SliderMenu;

public class MainActivity extends FragmentActivity implements MainCallback {
	// Bijan
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private SliderMenu slidermenu;

	private android.support.v4.app.Fragment mfrag;
	private android.support.v4.app.Fragment crFrag;
	private String tag;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		setContentView(R.layout.main_activity);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		// mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		// Menu
		slidermenu = new SliderMenu(this, savedInstanceState);
		slidermenu.getNavDrawerItems();
		slidermenu.getAdapter();
		slidermenu.getActionBarDrawerToggle();
		
		
		//Small screens only have a portait mode
				int layout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
				if ( layout == Configuration.SCREENLAYOUT_SIZE_NORMAL ||
						layout == Configuration.SCREENLAYOUT_SIZE_SMALL	) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}

		mfrag = new MainFragment();
		tag = mfrag.getClass().getName();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.frame_container, mfrag, tag).addToBackStack(tag)
				.commit();

	}

	@Override
	public void onItemSelected(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewRouteStarted(Route route) {

		crFrag = new DetailFragment();
		tag = crFrag.getClass().getName();

		Bundle bundle = new Bundle();
		// �bergabe Routenliste
		bundle.putParcelable("route", route);
		// �bergabe Index selektierte Route
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		crFrag.setArguments(bundle);
		ft.replace(R.id.frame_container, crFrag, tag).addToBackStack(tag)
				.commit();
	}

	@Override
	public void onShowRoute(Route route) {
		// fragmen avaiable?
		crFrag = new DetailFragment();
		tag = crFrag.getClass().getName();

		Bundle bundle = new Bundle();
		// �bergabe Routenliste
		bundle.putParcelable("route", route);
		crFrag.setArguments(bundle);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.replace(R.id.frame_container, crFrag, tag).addToBackStack(tag)
				.commit();

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

		DeleteDialog dialog = new DeleteDialog();
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
		mfrag = new MainFragment();
		tag = mfrag.getClass().getName();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, mfrag, tag).commit();

	}

	// GLEICH WIE DELETE ROUTE
	@Override
	public void onStopRoute() {
		mfrag = new MainFragment();
		tag = mfrag.getClass().getName();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, mfrag, tag).commit();

	}

	@Override
	public void onSliderClick(Fragment frag) {

		tag = frag.getClass().getName();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_container, frag, tag).addToBackStack(tag)
				.commit();

	}

	@Override
	public void onCamStart(Route route) {
		crFrag = new DetailFragment();
		tag = crFrag.getClass().getName();

		Bundle bundle = new Bundle();
		// �bergabe Routenliste
		bundle.putParcelable("route", route);
		crFrag.setArguments(bundle);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.replace(R.id.frame_container, crFrag, tag).addToBackStack(tag)
				.commit();

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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (slidermenu.getActionBarDrawerToggle().onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = slidermenu.getmDrawerLayout().isDrawerOpen(
				slidermenu.getmDrawerList());
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

}