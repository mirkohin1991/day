package de.smbsolutions.hike.presentation.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.objects.RouteList;

/**
 * Dialog zum Löschen einer ganzen Route
 */
public class DeleteRouteDialog extends DialogFragment {

	private RouteList routeList;
	private Bundle bundle;
	private int routeIndex;
	private MainCallback mainCallback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Beschaffen der mitgegebenen Parameter
		bundle = this.getArguments();
		routeList = (RouteList) bundle.getParcelable("routeList");
		routeIndex = bundle.getInt("routeIndex");

		// Erstellen des Dialoges
		return new AlertDialog.Builder(getActivity()).setTitle("Route löschen")
				.setMessage("Möchten Sie die Route wirklich löschen?")

				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Nichts muss getan werden, Dialog wird automatisch
						// beendet
					}
				})

				// Wenn JA geklickt wurde, wird die Route gelöscht und das
				// Callbackinterface aufgerufen
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						routeList.deleteRouteDB(routeIndex);
						mainCallback.onRouteDeleted();

						dismiss();
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
