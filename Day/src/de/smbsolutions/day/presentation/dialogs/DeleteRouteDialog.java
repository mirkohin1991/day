package de.smbsolutions.day.presentation.dialogs;

import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.RouteList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DeleteRouteDialog extends DialogFragment {

	private RouteList routeList;
	private Bundle bundle;
	private int index;
	private MainCallback mCallback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		bundle = this.getArguments();
		routeList = (RouteList) bundle.getParcelable("routeList");
		index = bundle.getInt("routeIndex");

		return new AlertDialog.Builder(getActivity()).setTitle("Route löschen")
				.setMessage("Möchten Sie die Route wirklich löschen?")
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing (will close dialog)
					}
				})
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						routeList.deleteRouteDB(index);
						mCallback.onDeleteRoute();
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