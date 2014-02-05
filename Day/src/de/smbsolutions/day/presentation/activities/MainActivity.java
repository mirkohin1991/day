package de.smbsolutions.day.presentation.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.services.TrackingService;
import de.smbsolutions.day.presentation.popups.RouteNameDialog;

public class MainActivity extends Activity {

	private GoogleMap map, map2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		double latitude = 47.9873111, longitude = 7.79642;
		// Create DB singleton
		Database.getInstance(this);

		if (mDisplay.getRotation() == 0 || mDisplay.getRotation() == 2) {
			// portrait
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map2 = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map2)).getMap();

			// Kartenart
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(latitude, longitude)).zoom(11).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			map2.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			map2.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));

		} else if (mDisplay.getRotation() == 1 || mDisplay.getRotation() == 3) {
			// landscape
			ListView meineListView = (ListView) findViewById(R.id.listView1);
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

			ListAdapter listenAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, meineListe);
			meineListView.setAdapter(listenAdapter);

			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(latitude, longitude)).zoom(11).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		} else {
		}

	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			RouteNameDialog dialog = new RouteNameDialog();
			// Showing the popup / Second Parameter: Unique Name, that is used
			// to identify the dialog
			dialog.show(getFragmentManager(), "NameDialog");
			break;

		default:
			break;
		}
	}

}