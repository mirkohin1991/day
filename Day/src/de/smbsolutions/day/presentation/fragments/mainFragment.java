package de.smbsolutions.day.presentation.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.database.RouteList;

public class mainFragment extends Fragment {

	private GoogleMap map, map2;
	
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Database.getInstance(getActivity());
		Configuration config = getResources().getConfiguration();
		view = inflater.inflate(R.layout.main_fragment, container, false);
		
		
		/**
		 * Check the device orientation and act accordingly
		 */
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
			initializeFragmentLandscape();

		} else {
			
			initializeFragmentPortrait();
		}
		return view;
	}

	public void initializeFragmentPortrait() {
		// portrait
		try {
			
		
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map2 = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map2)).getMap();
			double latitude = 47.9873111, longitude = 7.79642;
			// Kartenart
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(latitude, longitude)).zoom(11).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			map2.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			map2.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			RouteList = new RouteList(2);
		
		} catch (Exception e) {
			Log.wtf("mf_p", e.getMessage());
		}

	}

	public void initializeFragmentLandscape() {
		// portrait
		try {
			// landscape
			ListView meineListView = (ListView) view
					.findViewById(R.id.listView1);
			ArrayList<String> meineListe = new ArrayList<String>();
			meineListe.add("Route 1");
			meineListe.add("Route 2");
			meineListe.add("Route 3");
			meineListe.add("Route 4");
			meineListe.add("Route 5");
			meineListe.add("Route 6");
			meineListe.add("Route 7");
			meineListe.add("Route 8");
			meineListe.add("Route 9");
			meineListe.add("Route 10");
			double latitude = 47.9873111, longitude = 7.79642;
			ListAdapter listenAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, meineListe);
			meineListView.setAdapter(listenAdapter);

			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(latitude, longitude)).zoom(11).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		} catch (Exception e) {
			Log.wtf("mf_land", e.getMessage());
		}

	}
}
