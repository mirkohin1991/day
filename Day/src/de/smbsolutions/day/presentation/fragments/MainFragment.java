package de.smbsolutions.day.presentation.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.initialization.Device;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.presentation.listviews.AllRoutesListAdapter;
import de.smbsolutions.day.presentation.listviews.AllRoutesListElement;

/**
 * 
 * Die Mainfragment-Klasse ist für den zentralen StartScreen zuständig. Hier
 * werden alle Routen aufgelistet, die Vorschau-Map gefüllt und alle
 * Interaktionen gesteuert.
 * 
 */
public class MainFragment extends android.support.v4.app.Fragment {

	private SupportMapFragment mapFragment;

	private View view;
	private TextView txtViewName;
	private TextView txtViewDate;
	private ImageView ivPlayAnim;
	private Button btnContinueRoute;
	private Button btnCreateRoute;
	private ViewFlipper vfNewOrCurrent;

	// Elemente der Liste aller Routen
	private ListView routeListView;
	private int listViewIndex = 0;
	private List<AllRoutesListElement> meineListe;

	private RouteList routeList;
	private Route selectedRoute;

	private GoogleMap map;

	private MainCallback mCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_main, container, false);

		// Eine neue Liste mit allen Routen wird erstellt
		routeList = new RouteList();

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Das Callback Interface wird erzeugt
		try {
			mCallback = (MainCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " muss MainCallback Interface implementieren");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Wenn das Map-Fragment noch nicht vorhanden ist, wird es initialisiert
		FragmentManager fm = getChildFragmentManager();

		if (mapFragment == null) {
			mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
			if (mapFragment == null) {
				mapFragment = SupportMapFragment.newInstance();
				fm.beginTransaction().replace(R.id.map, mapFragment).commit();
			}
		}
	}

	@Override
	public void onResume() {

		super.onResume();

		// Wenn das Fragment neu geladen wird, werden zunächst die
		// Map-Einstellungen aktualisiert.
		// Z.B. könnte der Maptype durch den Benutzer geändert worden sein
		if (map == null) {
			map = mapFragment.getMap();
			map.setMapType(Device.getAPP_SETTINGS().getMapType());
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setPadding(0, 70, 0, 0);
			map.getUiSettings().setCompassEnabled(false);
		} else {
			map.setMapType(Device.getAPP_SETTINGS().getMapType());
		}

		// Initialisiern aller View-Komponenten des MainFragments
		initializeFragment();

		// Refreshen des Slidermenüs
		mCallback.refreshSliderMenu();

		// Nur wenn es eine Route gibt, kann mit der routeList gearbeitet werden
		if (!(routeList.getListRoutes().isEmpty())) {

			// Wenn es noch dazu eine offene Route gibt
			if (routeList.isOpenRoute()) {

				// Sofern ein Service läuft, wird der blinkende Button angezegit
				if (mCallback.isServiceActive()) {

					ivPlayAnim.setVisibility(View.VISIBLE);
					animateRunningIcon(ivPlayAnim);

					// Ansosten wird dieser ausgeblendet
				} else {
					ivPlayAnim.setVisibility(View.INVISIBLE);
				}

				// Zum Schluss muss überprüft werden ob ein Service läuft
				mCallback.onActiveRouteNoService();

			}
		}
	}
	
	
	@Override
	public void onPause() {
	
		//Map muss gecleart werdenSS
		map.clear();

		super.onPause();
	}


	/**
	 * 
	 * Diese Methode initialisiert das Fragment
	 * 
	 */
	public void initializeFragment() {
		// portrait
		try {

			// Animationsbutton
			ivPlayAnim = (ImageView) view.findViewById(R.id.ivPlayAnim);

			// Flipper "Anlegen neuer Route" oder "Anzeigen aktiver Route"
			vfNewOrCurrent = (ViewFlipper) view.findViewById(R.id.vf);

			// Aufbauen der Liste, die alle Route enthält
			routeListView = (ListView) view.findViewById(R.id.listView1);
			meineListe = new ArrayList<AllRoutesListElement>();
			for (Route route : routeList.getListRoutes()) {

				// Nur abgeschlossene Routen werden angezeigt
				if (route.isActive() == false) {
					meineListe.add(new AllRoutesListElement(route));
				}
			}

			// Die Liste wird umgedreht, damit die neueste Route immer zuerst
			// angezeigt wird
			Collections.reverse(meineListe);

			// Adapter wird erzeugt
			routeListView.setAdapter(new AllRoutesListAdapter(getActivity(),
					R.id.listView1, meineListe, mCallback));
			routeListView.setItemChecked(listViewIndex, true);

			// Wenn garkeine Route vorhanden ist, kann auch keine angezeigt
			// werden
			if (!(routeList.getListRoutes().isEmpty())) {

				// Speichern der selektierten Route
				selectedRoute = routeList.getListRoutes().get(listViewIndex);

				// Ändern der Benamsung auf dem Mapview
				changeDisplayedRouteDesc(routeList.getlastRoute());

				// Wenn eine Route aktiv ist, werden die entsprechenden
				// ViewKomponenten befüllt
				if (routeList.isOpenRoute()) {

					TextView txtRouteName = (TextView) view
							.findViewById(R.id.textRouteNameActive);
					txtRouteName.setText(routeList.getlastRoute()
							.getRouteName());

					// Der ViewFlipper wird so eingestellt, dass der
					// "Aktiv-Route"-View angezeigt wird
					vfNewOrCurrent.setDisplayedChild(1);

					btnContinueRoute = (Button) view
							.findViewById(R.id.imagebuttonContinue);

					// Wenn keine offene Route vorhadnden ist, wird der andere
					// ViewFlipper angezegit
				} else {
					vfNewOrCurrent.setDisplayedChild(0);
					btnCreateRoute = (Button) view
							.findViewById(R.id.imagebuttonCreate);
				}

			}

		} catch (Exception e) {

		}

		/*
		 * Abschließend werden alle Listener gesetzt
		 */

		addListitemListender(routeListView);

		// 1 bedeutet, der "Aktive Route" Flipper ist angezeigt
		if (vfNewOrCurrent.getDisplayedChild() == 1) {

			// Listener auf dem Button zum Fortsetzen
			addButtonClickListenerContinue(btnContinueRoute);

			// Ebenfalls Listener auf dem ganzen View, dann wird die Preview-Map
			// angezeigt
			View viewInclude = (View) view
					.findViewById(R.id.includeCurrentElement);
			addButtonClickListenerCurrentPreview(viewInclude);

			
		// 2 bedeutet, der "Neue Route Flipper ist angezeigt
		} else if (vfNewOrCurrent.getDisplayedChild() == 0) {

			//ButtonClick Listener auf dem ganzen View
			View viewInclude = (View) view.findViewById(R.id.includeNewElement);
			addButtonClickListenerCreate(viewInclude);
		}
		
		//Wird nach Fertigstellung des Layouts ausgeführt
		view.post(new Runnable() {

			@Override
			public void run() {
				// Wenn keine Route vorhanden, kann auch keine angezeigt werden
				if (!(routeList.getListRoutes().isEmpty())) {
					map = routeList.getlastRoute().prepareMapPreview(map);
				}
			}
		});
	}

	public void addButtonClickListenerContinue(Button button) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// SOLLTE ABER EIGENTLICH IMMER OFFEN SEIN, NUR DANN WIRD
				// NÄMLICH DER LISTENER GESETZT
				if (routeList.isOpenRoute()) {

					try {
						selectedRoute = routeList.getlastRoute();
					} catch (Exception e) {
						// handle exception
					}

					// Getting the route object of the related row
					// Transfering it to the interface in order to call the
					// detailed map view

					// Der Mainactivity wird mitgeteilt, dass die geöffnete
					// Route eine aktive ist
					mCallback.onRouteOpenend(true);

					// Anzeigen der Route
					mCallback.onShowRoute(selectedRoute);

				}

			}
		});

	}


	/**
	 * Listener für den Bereich zum Erstellen einer Route
	 */
	public void addButtonClickListenerCreate(View view) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mCallback.onOpenDialogNewRoute(routeList);

			}
		});

	}

	/**
	 * Listener wenn der Benutzer auf den Namen der aktiven Route klickt (Nicht den Play-Button)
	 */
	public void addButtonClickListenerCurrentPreview(View view) {
		
		
		//Wenn der Benutzer einen einfach Klick tätigt
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			    selectedRoute = routeList.getlastRoute();
			    //Map wird refresht 
				map = selectedRoute.prepareMapPreview(map);
				//Ändern der Beschriftung
				changeDisplayedRouteDesc(selectedRoute);

			}
		});

		//Wenn der Benutzer lange auf das Element klickt
		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {

				if (routeList.isOpenRoute()) {
				
					mCallback.onOpenDialogStopRoute("MAIN",
							routeList.getlastRoute());
				}
				return false;
			}
		});

	}

	
	/**
	 * Listener für die jeweiligen List-Elemente der "Alle beendeten Routen"-Liste
	 */
	public void addListitemListender(ListView listView) {

		
		//Wenn der Benutzer einen normalen Klick tätigt
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				try {
					//Abrufen des geklickten Listitems
					AllRoutesListElement element = (AllRoutesListElement) routeListView
							.getAdapter().getItem(position);
					//Global speichern
					selectedRoute = element.getRoute();
				
				} catch (Exception e) {
					
					e.printStackTrace();
				}

				//Aktualisieren der PreviewMap und der Beschreibung
				map = selectedRoute.prepareMapPreview(map);
				changeDisplayedRouteDesc(selectedRoute);
			}
		});

		
		//Wenn der Benutzer lange auf ein Element klickt, will er es löschen
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int index, long arg3) {
				
				mCallback.onOpenDialogDeleteRoute(routeList,
						routeListView.getCount() - (index + 1));
				return false;

			}
		});
	}

	
	/**
	 * Methode zum Ändern der Beschriftung auf der Map-Preview
	 * @param route
	 */
	public void changeDisplayedRouteDesc(Route route) {
		//Name
		txtViewName = (TextView) view.findViewById(R.id.txtViewPic);
		txtViewName.setText(route.getRouteName());
		//Datum
		txtViewDate = (TextView) view.findViewById(R.id.txtViewDatePreview);
		txtViewDate.setText(route.getDate());
	}
	
   
	
	/**
	 * Methode  Animieren des "Route läuft gerade"-Icons
	 */
	public void animateRunningIcon(ImageView animateImage) {
		
		final AnimationDrawable tvAnimation = (AnimationDrawable) animateImage
				.getDrawable();
		animateImage.post(new Runnable() {

			@Override
			public void run() {
				tvAnimation.start();
			}
		});
	}

}
