package de.smbsolutions.day.presentation.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.presentation.fragments.crFragment;
import de.smbsolutions.day.presentation.fragments.mainFragment;
import de.smbsolutions.day.presentation.popups.DeleteDialog;
import de.smbsolutions.day.presentation.popups.RouteNameDialog;
import de.smbsolutions.day.presentation.popups.StopRouteDialog;

public class MainActivity extends FragmentActivity implements MainCallback {

	private android.support.v4.app.Fragment mfrag;
	private android.support.v4.app.Fragment crFrag;
	private String tag;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		setContentView(R.layout.main_activity);
		mfrag = new mainFragment();
		tag = mfrag.getClass().getName();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment, mfrag, tag).commit();

	}

	@Override
	public void onItemSelected(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewRouteStarted(Route route) {

		crFrag = new crFragment();
		tag = crFrag.getClass().getName();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		// Übergabe Index selektierte Route

		crFrag.setArguments(bundle);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment, crFrag, tag).addToBackStack(tag)
				.commit();
	}

	@Override
	public void onShowRoute(Route route) {
		// fragmen avaiable?
		crFrag = new crFragment();
		tag = crFrag.getClass().getName();

		Bundle bundle = new Bundle();
		// Übergabe Routenliste
		bundle.putParcelable("route", route);
		crFrag.setArguments(bundle);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment, crFrag, tag).addToBackStack(tag)
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
		mfrag = new mainFragment();
		tag = mfrag.getClass().getName();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment, mfrag, tag).commit();

	}
	
	//GLEICH WIE DELETE ROUTE
	@Override
	public void onStopRoute() {
		mfrag = new mainFragment();
		tag = mfrag.getClass().getName();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment, mfrag, tag).commit();

	}
	
	
	

	@Override
	public void onCamStart(Route route) {

	}

}