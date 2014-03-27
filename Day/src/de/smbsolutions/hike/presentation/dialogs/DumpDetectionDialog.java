package de.smbsolutions.hike.presentation.dialogs;

import org.focuser.sendmelogs.LogCollector;

import de.smbsolutions.hike.functions.interfaces.MainCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DumpDetectionDialog extends DialogFragment {
	
	private MainCallback mCallback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return new AlertDialog.Builder(getActivity()).setTitle("Hike wurde das letzte Mal nicht korrekt beendet")
				.setMessage("Möchten Sie den Fehlerbereicht an die Entwickler senden?")
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing (will close dialog)
					}
				})
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mCallback != null) {
							mCallback.onDumpDetected();
						}
						
						dismiss();
					}
				}).create();
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
