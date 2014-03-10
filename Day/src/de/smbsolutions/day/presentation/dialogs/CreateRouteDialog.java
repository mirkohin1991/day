package de.smbsolutions.day.presentation.dialogs;

import java.sql.Timestamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.location.LocationTracker;
import de.smbsolutions.day.functions.location.LocationTrackerPLAYSERVICE;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.services.TrackingService;
import de.smbsolutions.day.presentation.fragments.DetailFragment;

public class CreateRouteDialog extends android.support.v4.app.DialogFragment {

	private LocationTracker tracker;
	private RouteList routeList;
	private Bundle bundle;
	private MainCallback mCallback;
	private int index = 0;
    private LocationTrackerPLAYSERVICE locationTracker;
    
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		bundle = this.getArguments();
		routeList = (RouteList) bundle.getParcelable("routeList");
		
		//Getting the LocationTrackerService
	//	locationTracker = LocationTrackerPLAYSERVICE.getInstance(getActivity());

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View nameView = inflater.inflate(R.layout.dialog_createroute, null);
		// Adding the customized popup layout
		builder.setView(nameView);

		builder.setPositiveButton("Anlegen",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					//	tracker = LocationTracker.getInstance(getActivity());
						// New Route shall be craeted
						EditText nameText = (EditText) getDialog()
								.findViewById(R.id.routename);
						String routeName = nameText.getText().toString();
						// Calling the db
						Route route = new Route(routeName);
						
						
						
						mCallback.onStartTrackingService(routeList, route);
						
					
						
						

					}
				}).setNegativeButton("Abbrechen",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					mCallback.onDialogCreateCanceled();
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