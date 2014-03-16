package de.smbsolutions.day.presentation.dialogs;

import com.google.android.gms.internal.ac;

import de.smbsolutions.day.functions.interfaces.FragmentCallback;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.presentation.activities.MainActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;


public class PauseRouteDialog extends DialogFragment {

	private FragmentCallback fragCallback;
	private MainCallback mainCallback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		

		return new AlertDialog.Builder(getActivity()).setTitle("Route anhalten")
				.setMessage("Wenn Sie die Route pausieren, werden keine neuen Standortdaten gespeichert bis Sie die Route das nächste Mal aufrufen.")
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
						//routeList.getlastRoute().closeRoute();
						dismiss();
						//Call the communication interface to start the follow-on fragment
						fragCallback.onRoutePaused();
						mainCallback.onActiveRouteNoService();
					}
				}).create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			android.support.v4.app.FragmentActivity frag = (FragmentActivity) activity;
			fragCallback = (FragmentCallback) frag.getSupportFragmentManager().findFragmentByTag("DETAIL");
			mainCallback = (MainCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnButtonClick Interface");
		}
	}
}
