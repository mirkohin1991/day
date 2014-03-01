package de.smbsolutions.day.presentation.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
import android.widget.ViewFlipper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.presentation.views.MainListElement;
import de.smbsolutions.day.presentation.views.MainListAdapter;

public class MainFragment extends android.support.v4.app.Fragment {

	private GoogleMap map;
	private SupportMapFragment fragment;
	private View view;
	private RouteList routeList;
	private TextView txtViewPic;
	private TextView txtViewDate;
	private ListView meineListView;
	private Button startButton;
	// private Button btnStopRoute;
	private Button btnContinueRoute;
	private Button btnCreateRoute;
	private ViewFlipper vfNewOrCurrent;
	private ViewFlipper vfPauseOrRun;
	private Configuration config;
	private Route sel_Route;
	private int index = 0;
	private MainCallback mCallback;

	private boolean flag_first = true;

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
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

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

			// viewflipper are used to change views at the same position
			//--> Flipper to change between current route view and create route view
			vfNewOrCurrent = (ViewFlipper) view.findViewById(R.id.vf);
			
			
			

			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setPadding(0, 70, 0, 0);
			// get views from fragment
			meineListView = (ListView) view.findViewById(R.id.listView1);
			List<MainListElement> meineListe = new ArrayList<MainListElement>();
			for (Route route : routeList.getListRoutes()) {
				// Only completed routes shall appear in the "recent routes"
				// list
				if (route.getActive().equals("")) {
					meineListe.add(new MainListElement(route));
				}
			}
			// Set the list view adapter
			meineListView.setAdapter(new MainListAdapter(getActivity(),
					R.id.listView1, meineListe, mCallback));

			meineListView.setItemChecked(index, true);
			sel_Route = routeList.getListRoutes().get(index);

			
			changeDisplayedRouteDesc(routeList.getlastRoute());

			if (routeList.isOpenRoute()) {

				TextView txtRouteName = (TextView) view
						.findViewById(R.id.textRouteNameActive);
				txtRouteName.setText(routeList.getlastRoute().getRouteName());
				// Showing the current active route as the first item
				vfNewOrCurrent.setDisplayedChild(1);
			//	btnStopRoute = (Button) view.findViewById(R.id.imagebuttonStop);
				btnContinueRoute = (Button) view
						.findViewById(R.id.imagebuttonContinue);

			} else {
				// Showing the "create new route item"
				vfNewOrCurrent.setDisplayedChild(0);
				btnCreateRoute = (Button) view
						.findViewById(R.id.imagebuttonCreate);
			}

			LinearLayout linleaLayout = (LinearLayout) view
					.findViewById(R.id.LinearLayout1);
			linleaLayout.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {

							if (flag_first == true) {

								map = routeList.getlastRoute().prepareMap(map,
										getActivity(), false);

								flag_first = false;
							}

							addListitemListender(meineListView);
							// addButtonClickListener(startButton);

							// Child == 1 --> the "active route item" layout is
							// displayed
							if (vfNewOrCurrent.getDisplayedChild() == 1) {

								addButtonClickListenerContinue(btnContinueRoute);
							//	addButtonClickListenerStop(btnStopRoute);

								// Getting the whole line (including the two
								// buttons)
								View viewInclude = (View) view
										.findViewById(R.id.includeCurrentElement);

								addButtonClickListenerCurrentPreview(viewInclude);

								// No current route -> add listener to create
								// new one
							} else if (vfNewOrCurrent.getDisplayedChild() == 0) {

								View viewInclude = (View) view
										.findViewById(R.id.includeNewElement);

								addButtonClickListenerCreate(viewInclude);
							}

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

//	public void addButtonClickListenerStop(Button button) {
//		button.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				// SOLLTE ABER EIGENTLICH IMMER OFFEN SEIN, NUR DANN WIRD
//				// NÄMLICH DER LISTENER GESETZT
//				if (routeList.isOpenRoute()) {
//					// A route is active -> user wants to stop it
//
//					mCallback.onStopPopup(routeList);
//
//				}
//
//			}
//		});
//
//	}

	public void addButtonClickListenerContinue(Button button) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// SOLLTE ABER EIGENTLICH IMMER OFFEN SEIN, NUR DANN WIRD
				// NÄMLICH DER LISTENER GESETZT
				if (routeList.isOpenRoute()) {
					// A route is active -> user wants to stop it

					try {
						sel_Route = routeList.getlastRoute();

					} catch (Exception e) {
						// handle exception

					}

					// Getting the route object of the related row
					// Transfering it to the interface in order to call the
					// detailed map view
					mCallback.onShowRoute(sel_Route);

				}

			}
		});

	}

	// The "create new item" listener ist registered to the complete view (Text
	// and Icon togehter)
	public void addButtonClickListenerCreate(View view) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// SOLLTE ABER EIGENTLICH IMMER KEINE MEHR OFFEN SEIN, NUR DANN
				// WIRD
				// NÄMLICH DER LISTENER GESETZT
				if (routeList.isOpenRoute() == false) {

					// Route not active -> start new one
					mCallback.onOpenDialogNewRoute(routeList);
				}

			}
		});

	}

	// The "current route row" listener is registered additionally to the
	// complete view
	public void addButtonClickListenerCurrentPreview(View view) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					sel_Route = routeList.getlastRoute();

				} catch (Exception e) {
					// handle exception

				}

				map = sel_Route.prepareMap(map, getActivity(), false);
			
				changeDisplayedRouteDesc(sel_Route);

			}
		});
		
		
		view.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if (routeList.isOpenRoute()) {
					// A route is active -> user wants to stop it

					mCallback.onStopPopup(routeList);

				}
				return false;
			}
		});

	}

	public void addListitemListender(ListView listView) {

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				try {
					// Getting the route through the adapter
					MainListElement element = (MainListElement) meineListView
							.getAdapter().getItem(position);
					sel_Route = element.getRoute();
					// OLD
					// sel_Route = routeList.getListRoutes().get(position);

				} catch (Exception e) {
					// handle exception

				}

				map = sel_Route.prepareMap(map, getActivity(), false);
				
				changeDisplayedRouteDesc(routeList.getListRoutes()
						.get(position));
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index, long arg3) {
				// Call Interface to handle the deletion of the route
				mCallback.onLongItemSelected(routeList, index);
				return false;

			}
		});
	}


	public void changeDisplayedRouteDesc(Route route) {
		txtViewPic = (TextView) view.findViewById(R.id.txtViewPic);
		txtViewPic.setText(route.getRouteName());
		
		txtViewDate = (TextView) view.findViewById(R.id.txtViewDatePreview);
		txtViewDate.setText(route.getDate());
		
		
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

}
