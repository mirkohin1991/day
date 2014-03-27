package de.smbsolutions.hike.presentation.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import de.smbsolutions.hike.functions.interfaces.FragmentCallback;
import de.smbsolutions.hike.functions.interfaces.MainCallback;

/**
 * Dialog zum Pausieren einer Route
 */
public class PauseRouteDialog extends DialogFragment {

	private FragmentCallback fragCallback;
	private MainCallback mainCallback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Dialog wird erstellt
		return new AlertDialog.Builder(getActivity())
				.setTitle("Route anhalten")
				.setMessage(
						"Wenn Sie die Route pausieren, werden keine neuen Standortdaten gespeichert bis Sie die Route das nächste Mal aufrufen.")

				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Dialog wird automatisch beendet
					}
				})

				// JA wurde geklickt
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						// Das Detailfragment wird aktualisiert
						fragCallback.onRoutePaused();
						// Der Service wird gestoppt
						mainCallback.onActiveRouteNoService();
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
			android.support.v4.app.FragmentActivity frag = (FragmentActivity) activity;
			fragCallback = (FragmentCallback) frag.getSupportFragmentManager()
					.findFragmentByTag("DETAIL");
			mainCallback = (MainCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " muss MainCallback Inteface implementieren");
		}
	}
}
