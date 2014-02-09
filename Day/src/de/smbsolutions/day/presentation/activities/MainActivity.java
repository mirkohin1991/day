package de.smbsolutions.day.presentation.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;

import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.database.RouteList;
import de.smbsolutions.day.presentation.fragments.mainFragment;

public class MainActivity extends Activity {

	private GoogleMap map, map2;
	private Fragment mFragment, mFragment_land;
	private String tag_portrait, tag_landscape;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		Configuration config = getResources().getConfiguration();

		android.app.FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
//		if (savedInstanceState == null) {
			mFragment = new mainFragment();
			fragmentTransaction.replace(android.R.id.content, mFragment, "fragmenttag");
//		}
		fragmentTransaction.commit();

	}

	//
	// public void onButtonClick(View view) {
	// switch (view.getId()) {
	// case R.id.button1:
	// RouteNameDialog dialog = new RouteNameDialog();
	// // Showing the popup / Second Parameter: Unique Name, that is used
	// // to identify the dialog
	// dialog.show(getFragmentManager(), "NameDialog");
	// break;
	//
	// default:
	// break;
	// }
	// }

}