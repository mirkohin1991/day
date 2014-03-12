package de.smbsolutions.day.presentation.dialogs;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class DeletePictureDialog extends DialogFragment {

	private Route route;
	private RoutePoint point;
	private Bundle bundle;
	private MainCallback mCallback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		bundle = this.getArguments();
		route = (Route) bundle.getParcelable("route");
		point = (RoutePoint) bundle.getParcelable("point");

		return new AlertDialog.Builder(getActivity()).setTitle("Bild löschen")
				.setMessage("Möchten Sie das Bild wirklich löschen?")
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing (will close dialog)
					}
				})
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						try {

							// Getting the file
							File deleteFile = new File(point.getPicture());

							File deleteFilePreview = new File(point
									.getPicturePreview());

							// If the deletion was successful
							if (deleteFile.delete() == true
									&& deleteFilePreview.delete()) {

								// Remove the picture paths
								route.deletePictureDB(point);

							}
							dismiss();
							mCallback.onShowRoute(route);

						} catch (Exception e) {

							Log.wtf(getTag(), e.getMessage());
						}

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