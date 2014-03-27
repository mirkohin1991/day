package de.smbsolutions.hike.presentation.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.objects.Route;

/**
 * Dialog zum Stoppen einer Route
 */
public class StopRouteDialog extends DialogFragment {

	private Route route;
	private Bundle bundle;
	private MainCallback mainCallback;

	private String fragmentFlag;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Übergebene Params werden ausgelesen
		bundle = this.getArguments();
		route = (Route) bundle.getParcelable("route");
		// fragmentFlag enthällt ein Kennzeichen woher der Aufruf kam
		// Denn der StopDialog kann aus MainFrag und DetailFrag erfolgen
		fragmentFlag = (String) bundle.getString("fragmentFlag");

		return new AlertDialog.Builder(getActivity()).setTitle("Route beenden")
				.setMessage("Möchten Sie die Route wirklich beenden?")
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Dialog wird automatisch geschlossen
					}
				})
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						// Schließen der übergebenen Route
						route.closeRoute();
						dismiss();

						// MainActivity kümmert sich um alle Folgeprozesse
						mainCallback.onRouteStopped(fragmentFlag, route);

					}
				}).create();
	}

	/**
	 * Wenn der Dialog attached wird, wird der Callback zur MainActivity
	 * gespeichert
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {

			mainCallback = (MainCallback) activity;

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " muss MainCallback Inteface implementieren");
		}
	}
}
