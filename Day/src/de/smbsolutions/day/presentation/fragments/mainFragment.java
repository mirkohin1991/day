package de.smbsolutions.day.presentation.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;

public class mainFragment extends android.support.v4.app.Fragment {

	private GoogleMap map;
	private SupportMapFragment fragment;
	private View view;
	private RouteList routeList;
	private TextView txtView;
	private ListView meineListView;
	private Button startButton;
	private Configuration config;
	private Route sel_Route;
	private int index = 0;
	private MainCallback mCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Database.getInstance(getActivity());
		routeList = new RouteList();
		// Configuration for device orientation and shit
		config = getResources().getConfiguration();
		view = inflater.inflate(R.layout.main_fragment, container, false);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (MainCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnButtonClick Interface");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

			if (fragment == null) {
				fragment = SupportMapFragment.newInstance();
				fm.beginTransaction().replace(R.id.map, fragment).commit();
			}
		} else {
			fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

			if (fragment == null) {
				fragment = SupportMapFragment.newInstance();
				fm.beginTransaction().replace(R.id.map, fragment).commit();
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		

		// checking device orientation for layout
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE ) {
			
			if (map == null) {
				map = fragment.getMap();
			}

			initializeFragmentLandscape();

		} else {

			if (map == null) {
				map = fragment.getMap();
			}

			initializeFragmentPortrait();

		}

	}

	public void initializeFragmentPortrait() {
		// portrait
		try {

			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setPadding(0, 70, 0, 70);
			// get views from fragment
			meineListView = (ListView) view.findViewById(R.id.listView1);
			List<String> meineListe = new ArrayList<String>();
			for (Route route : routeList.getListRoutes()) {
				meineListe.add(route.getRouteName());
			}
			ListAdapter listenAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, meineListe);
			meineListView.setAdapter(listenAdapter);
			meineListView.setItemChecked(index, true);
			sel_Route = routeList.getListRoutes().get(index);

			// last route closed?
			changeButtontext(routeList.getlastRoute());
			changeDisplayedRouteDesc(routeList.getlastRoute());

			LinearLayout linleaLayout = (LinearLayout) view
					.findViewById(R.id.LinearLayout1);
			linleaLayout.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {

							map = routeList.getlastRoute().prepareMap(map,
									getActivity(), false);
							addListitemListender(meineListView);
							addButtonClickListener(startButton);

						}
					});

		} catch (Exception e) {
			Toast.makeText(getActivity(),
					"Fehler Initialisierung Fragment: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}

	public void initializeFragmentLandscape() {
		// portrait
		try {
			// landscape
			// get views from fragment
			final ListView meineListView = (ListView) view
					.findViewById(R.id.listView1);
			List<String> meineListe = new ArrayList<String>();
			for (Route route : routeList.getListRoutes()) {
				meineListe.add(route.getRouteName());
			}

			// last route = active route?
			changeButtontext(routeList.getlastRoute());
			changeDisplayedRouteDesc(routeList.getlastRoute());
			ListAdapter listenAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, meineListe);
			meineListView.setAdapter(listenAdapter);
			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

			LinearLayout linleaLayout = (LinearLayout) view
					.findViewById(R.id.linlayoutland);
			linleaLayout.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {

							map = routeList.getlastRoute().prepareMap(map,
									getActivity(), false);
							addListitemListender(meineListView);
							// LandscapeButtonCLick
						}
					});

		} catch (Exception e) {
			Log.wtf("mf_land", e.getMessage());
		}

	}

	public void addButtonClickListener(Button button) {

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sel_Route.getActive().equals("X")) {
					// Route ist aktiv --> fortsetzen DetailFragment aufrufen
					mCallback.onShowRoute(sel_Route);

				} else {
					// Route nicht aktiv --> Neue Route starten
					mCallback.onOpenDialogNewRoute(routeList);

				}

			}
		});

	}

	public void addListitemListender(ListView listView) {

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			
				try {
					sel_Route = routeList.getListRoutes().get(position);
					index = position;
				} catch (Exception e) {
					//handle exception

				}

				map = sel_Route.prepareMap(map, getActivity(), false);
				changeButtontext(routeList.getListRoutes().get(position));
				changeDisplayedRouteDesc(routeList.getListRoutes()
						.get(position));

			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index, long arg3) {
				mCallback.onLongItemSelected(routeList, index);
				return false;

			}
		});
	}

	public void changeButtontext(Route route) {
		// last route closed?
		startButton = (Button) view.findViewById(R.id.imagebutton1);
		if (sel_Route.getActive().equals("X")) {
			startButton.setText("Fortsetzen");
		} else {
			startButton.setText("Neue Route");
		}
	}

	public void changeDisplayedRouteDesc(Route route) {
		txtView = (TextView) view.findViewById(R.id.txtViewPic);
		txtView.setText(route.getRouteName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}
}
