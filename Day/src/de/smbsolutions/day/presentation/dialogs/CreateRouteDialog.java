package de.smbsolutions.day.presentation.dialogs;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;

public class CreateRouteDialog extends android.support.v4.app.DialogFragment {

	private RouteList routeList;
	private Bundle bundle;
	private MainCallback mCallback;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		bundle = this.getArguments();
		routeList = (RouteList) bundle.getParcelable("routeList");

		// Getting the LocationTrackerService
		// locationTracker =
		// LocationTrackerPLAYSERVICE.getInstance(getActivity());

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View nameView = inflater.inflate(R.layout.dialog_createroute,
				null);
		// Adding the customized popup layout
		builder.setView(nameView);

		builder.setPositiveButton("Anlegen", null

		).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				mCallback.removeService();
			}
		});
		// Create the AlertDialog object and return it

		final AlertDialog dialog = builder.create();
		
		
		
		//Eigener ShowDialog Listener, um die ButtonClick events selbst zu definieren
		//-> Nur dadurch ist es möglich das Schließen des Dialogs bei fehlerhafter Eingabe des Routenamen zu unterdrücken
		dialog.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				
				Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						// tracker = LocationTracker.getInstance(getActivity());
						// New Route shall be craeted
						EditText nameText = (EditText) getDialog().findViewById(
								R.id.routename);
						String routeName = nameText.getText().toString();

						if (routeName.isEmpty()) {

                          nameText.setHint("Bitte Namen eingeben");
                          nameText.setHintTextColor(Color.RED);

						} else {

							dismiss();
							// Calling the db
							Route route = new Route(routeName);

							routeList.addRoute(route);

							mCallback.onStartTrackingService(route);
						}
						
					}
				});
				
			}
		});

	
		return dialog;
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
