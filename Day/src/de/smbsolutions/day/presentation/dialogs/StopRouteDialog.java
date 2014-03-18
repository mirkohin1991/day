package de.smbsolutions.day.presentation.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import de.smbsolutions.day.functions.interfaces.FragmentCallback;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;

public class StopRouteDialog extends DialogFragment {

	private Route route;
	private Bundle bundle;
	private MainCallback mainCallback;
	private String fragmenFlag;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		bundle = this.getArguments();
		route = (Route) bundle.getParcelable("route");
		fragmenFlag = (String) bundle.getString("fragmentFlag");
		return new AlertDialog.Builder(getActivity()).setTitle("Route beenden")
				.setMessage("Möchten Sie die Route wirklich beenden?")
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing (will close dialog)
					}
				})
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// The last route is always the active one
						route.closeRoute();
						dismiss();

						// Call the communication interface to start the
						// follow-on fragment

						mainCallback.onRouteStopped(fragmenFlag, route);

					}
				}).create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {

			mainCallback = (MainCallback) activity;

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnButtonClick Interface");
		}
	}
}
