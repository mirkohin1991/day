package de.smbsolutions.hike.functions.objects;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import de.smbsolutions.day.R;
import de.smbsolutions.hike.functions.initialization.Device;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.presentation.fragments.settings.AboutFragment;
import de.smbsolutions.hike.presentation.fragments.settings.CameraFragment;
import de.smbsolutions.hike.presentation.fragments.settings.GPSFragment;
import de.smbsolutions.hike.presentation.listviews.SliderMenuItem;
import de.smbsolutions.hike.presentation.listviews.SliderMenuListAdapter;

/**
 * Einstellungen für das SliderMenu.
 * Legt die Listeinträge mit Anzeigenamen und Icons an.
 * Je nach geklicktem Item wird das entsprechende Fragment aufgerufen, oder der Kartentyp geändert.
 */

public class SliderMenu {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private MainCallback mCallback;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer Titel
	private CharSequence mDrawerTitle;

	// App Titel
	private CharSequence mTitle;

	//Slider Menu Items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<SliderMenuItem> navDrawerItems;
	private SliderMenuListAdapter adapter;

	private Bundle savedInstanceState;

	private Activity context;

	public SliderMenu(Activity context, Bundle savedInstanceState) {
		super();
		this.context = context;
		this.savedInstanceState = savedInstanceState;

		mDrawerLayout = (DrawerLayout) context.findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) context.findViewById(R.id.list_slidermenu);

		try {
			mCallback = (MainCallback) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnButtonClick Interface");
		}
	}
	
	
	public void removeSelectedItem() {
		mDrawerList.clearChoices();
		mDrawerList.requestLayout();
	}

	
	public DrawerLayout getmDrawerLayout() {
		return mDrawerLayout;
	}

	public ListView getmDrawerList() {
		return mDrawerList;
	}

	/**
	 * Fügt dem Slider Menu die Einträge hinzu.
	 * Name und Icon jeden Eintrags werde in der string.xml festgelegt
	 */
	public ArrayList<SliderMenuItem> getNavDrawerItems() {

		mTitle = mDrawerTitle = context.getTitle();

		// Lade Slider Menu Items
		navMenuTitles = context.getResources().getStringArray(
				R.array.nav_drawer_items);

		navMenuIcons = context.getResources().obtainTypedArray(
				R.array.nav_drawer_icons);

		navDrawerItems = new ArrayList<SliderMenuItem>();

		// Nav Drawer Items dem Array hinzufügen
		// Landkarte
		navDrawerItems.add(new SliderMenuItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Sattelite
		navDrawerItems.add(new SliderMenuItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Terrain
		navDrawerItems.add(new SliderMenuItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));

		// Einstellungen (Ueberschrift)
		navDrawerItems.add(new SliderMenuItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		// GPS
		navDrawerItems.add(new SliderMenuItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1)));
		// Kamera
		navDrawerItems.add(new SliderMenuItem(navMenuTitles[5], navMenuIcons
				.getResourceId(5, -1)));
		// App Info
		navDrawerItems.add(new SliderMenuItem(navMenuTitles[6], navMenuIcons
				.getResourceId(6, -1)));

		navDrawerItems.add(new SliderMenuItem(navMenuTitles[7], navMenuIcons
				.getResourceId(7, -1)));
		return navDrawerItems;
	}

	public SliderMenuListAdapter getAdapter() {

		// Array leeren
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		adapter = new SliderMenuListAdapter(context.getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

//		Toggle Button zum öffnen des Menus einstellen
		context.getActionBar().setDisplayHomeAsUpEnabled(true);
		context.getActionBar().setHomeButtonEnabled(true);

		return adapter;
	}

	/**
	 * Einstellungen fuer das SliderMenu.
	 * Icon, Anzeigename beim Oeffnen bzw. schliessen des SliderMenus
	 */
	public ActionBarDrawerToggle getActionBarDrawerToggle() {
		mDrawerToggle = new ActionBarDrawerToggle(context, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer Anzeigename beim öffnen.
				R.string.app_name // nav drawer Anzeigename beim schließen.
		) {
			/**
			 * Einstellungen die gelten wenn das SliderMenu geschlossen wird.
			 */
			public void onDrawerClosed(View view) {
				context.getActionBar().setTitle(mTitle);
				// aufruf onPrepareOptionsMenu() um items anzuzeigen
				context.invalidateOptionsMenu();
			}

			/**
			 * Einstellungen die gelten wenn das SliderMenu geöffnet wird.
			 */
			public void onDrawerOpened(View drawerView) {
				context.getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				context.invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			// displayView(0);
		}

		return mDrawerToggle;

	}

	//
	//
	/**
	 * SliderMenu Item Click Listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {

		/**
		 * Positionsbestimmung des geklickten Items
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			displayView(position);
			view.setTag("selected");

		}
	}


	 /**
	 * Anzeige des Entsprechenden Fragments, bzw. zuweisung des Kartentyps
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		android.support.v4.app.Fragment fragment = null;

		switch (position) {
		case 0:
			// MapType: Karte
			Device.getAPP_SETTINGS().setMapType(1);
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			mDrawerLayout.closeDrawer(mDrawerList);
			mCallback.onRefreshMap();
			break;
		case 1:

			// MapType: Sattelite
			Device.getAPP_SETTINGS().setMapType(2);
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			mDrawerLayout.closeDrawer(mDrawerList);			
			mCallback.onRefreshMap();
			break;
		case 2:
			// MapType: Terrain
			Device.getAPP_SETTINGS().setMapType(3);
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			mDrawerLayout.closeDrawer(mDrawerList);
			mCallback.onRefreshMap();
			break;
		case 3:
			// Einstellungen Ueberschrift
			// Nicht klickbar
			break;

		case 4:
			// GPS Fragment öffnen
			fragment = new GPSFragment();
			break;

		case 5:
			// Kamera Fragment öffnen
			fragment = new CameraFragment();
			break;

		case 6:
			// Info Fragment öffnen
			fragment = new AboutFragment();
			break;
		default:
			break;
		}
		if (fragment != null) {
			mCallback.onSliderClick(fragment);
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			Log.d("MainActivity", "MapType changed");
		}

	}

}
