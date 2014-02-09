package de.smbsolutions.day.presentation.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.smbsolutions.day.R;

public class crFragment extends Fragment {

	private MapFragment fragment;
	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cr_fragment, container, false);
		
		
		
	}

//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		android.app.FragmentManager fm = getChildFragmentManager();
//		fragment = (MapFragment) fm.findFragmentById(R.id.map);
//		if (fragment == null) {
//			fragment = MapFragment.newInstance();
//			fm.beginTransaction().replace(R.id.map, fragment)
//					.commit();
//		}
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		if (map == null) {
//			map = ((MapFragment) fragment).getMap();
//			map.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
//		}
//	}
}