package de.smbsolutions.day.presentation.views;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.presentation.fragments.CameraFragment;
import de.smbsolutions.day.presentation.fragments.GPSFragment;

public class SliderMenu {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private MainCallback mCallback;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

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

	public DrawerLayout getmDrawerLayout() {
		return mDrawerLayout;
	}

	public ListView getmDrawerList() {
		return mDrawerList;
	}

	public ArrayList<NavDrawerItem> getNavDrawerItems(){
		
		mTitle = mDrawerTitle = context.getTitle();

		// load slide menu items
		navMenuTitles = context.getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = context.getResources().obtainTypedArray(R.array.nav_drawer_icons);



		
		navDrawerItems = new ArrayList<NavDrawerItem>();

		
		// adding nav drawer items to array
		//Landkarte
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		//Sattelite
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		//Terrain
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		
		
		// Einstellungen (Ueberschrift)
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// GPS
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
//		Kamera
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		//App Info
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		return navDrawerItems;
	}
	
	public NavDrawerListAdapter getAdapter(){
		
		// Recycle the typed array
		navMenuIcons.recycle();
				
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
				
				
		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(context.getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);
		
		// enabling action bar app icon and behaving it as toggle button
		
		context.getActionBar().setDisplayHomeAsUpEnabled(true);
		context.getActionBar().setHomeButtonEnabled(true);
		
	return adapter;	
	}

	public ActionBarDrawerToggle getActionBarDrawerToggle(){
		
	
			mDrawerToggle = new ActionBarDrawerToggle(context, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				context.getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				context.invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				context.getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				context.invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		
		return mDrawerToggle;
		
	}

	//
	//
	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {

		// Hier werden die Sachen nicht gefunden.

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item

			displayView(position);
		}
	}

	//

	//

	// /**
	// * Diplaying fragment view for selected nav drawer list item
	// * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		android.support.v4.app.Fragment fragment = null;

		switch (position) {
		case 0:
//			MapType: Karte
//			Database.changeSettingValue(Database.SETTINGS_MAP_TYPE, 1);
			break;
		case 1:
//			MapType: Sattelite
//			Database.changeSettingValue(Database.SETTINGS_MAP_TYPE, 2);
			break;
		case 2:
//			MapType: Terrain
//			Database.changeSettingValue(Database.SETTINGS_MAP_TYPE, 3);
			break;
		case 3:
//			Einstellungen
			break;
		
		case 4:
//			GPS
			fragment = new GPSFragment();
			break;
			
		case 5:
//			Kamera
			fragment = new CameraFragment();
			break;
			
		case 6:
			//Info
			break;

		default:
			break;
		}
		if (fragment != null) {

			mCallback.onSliderClick(fragment);
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			context.setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}

	}

}
