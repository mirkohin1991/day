package de.smbsolutions.day.presentation.popups;

import java.sql.Timestamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.location.GPSTracker;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.presentation.fragments.crFragment;

public class RouteNameDialog extends android.support.v4.app.DialogFragment {

	private GPSTracker tracker;
	private RouteList routeList;
	private Bundle bundle;
	private MainCallback mCallback;
	private int index = 0;
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		bundle = this.getArguments();
		routeList = (RouteList) bundle.getSerializable("routeList");

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View nameView = inflater.inflate(R.layout.routename_dialog, null);
		// Adding the customized popup layout
		builder.setView(nameView);

		builder.setPositiveButton("Create",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						
						tracker = GPSTracker.getInstance(getActivity());
						routeList = (RouteList) bundle.getSerializable("routeList");
						// New Route shall be craeted
						EditText nameText = (EditText) getDialog()
								.findViewById(R.id.routename);
						String routeName = nameText.getText().toString();
						// Calling the db
						Route route = new Route(routeName);
						route.addRoutePointDB(new RoutePoint(route.getId(),
								new Timestamp(System.currentTimeMillis()),
								null, tracker.getLatitude(), tracker
										.getLongitude()));
						routeList.addRoute(route);
						
						mCallback.onNewRouteStarted(route);
						// neues fragment-->
						// nicht sicher ob das so die beste Lösung ist
						// crFragment currenRouteFrag = new crFragment();

					}
				}).setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
						// --> Do nothing
					}
				});
		// Create the AlertDialog object and return it
		return builder.create();
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
}
