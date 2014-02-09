package de.smbsolutions.day.presentation.popups;

import java.sql.Timestamp;

import com.google.android.gms.maps.MapView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.location.GPSTracker;
import de.smbsolutions.day.presentation.activities.MainActivity;
import de.smbsolutions.day.presentation.activities.MapActivity;
import de.smbsolutions.day.presentation.fragments.crFragment;

public class RouteNameDialog extends DialogFragment {

	private Database db;
	private GPSTracker tracker;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View nameView = inflater.inflate(R.layout.routename_dialog, null);
		// Adding the customized popup layout
		builder.setView(nameView);

		builder.setPositiveButton("Create",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						db = Database.getInstance(getActivity());
						tracker = GPSTracker.getInstance(getActivity());

						// New Route shall be craeted
						EditText nameText = (EditText) getDialog()
								.findViewById(R.id.routename);
						String test = nameText.getText().toString();
						// Calling the db
						Database.registerNewRoute(test);

						db.addNewRoutePoint(tracker.getLatitude(),
								tracker.getLongitude(),
								new Timestamp(System.currentTimeMillis()));

						// nicht sicher ob das so die beste Lösung ist
						crFragment currenRouteFrag = new crFragment();

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

}
