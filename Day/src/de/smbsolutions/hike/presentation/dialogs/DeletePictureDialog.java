package de.smbsolutions.hike.presentation.dialogs;

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
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.objects.Route;
import de.smbsolutions.hike.functions.objects.RoutePoint;


/**
 * Dialog zum Löschen eines Bildpunktes
 */
public class DeletePictureDialog extends DialogFragment {

	private Route route;
	private RoutePoint point;
	private Bundle bundle;
	private MainCallback mainCallback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		//Abrufen der mitgebenen Parameter
		bundle = this.getArguments();
		route = (Route) bundle.getParcelable("route");
		point = (RoutePoint) bundle.getParcelable("point");

		//Neuer Dialog wird erstellt
		return new AlertDialog.Builder(getActivity()).setTitle("Bild löschen")
				.setMessage("Möchten Sie das Bild wirklich löschen?")
				
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Dialog wird einfach beendet
					}
				})
				
				//Wenn der JA Button geklickt wurde, wird das Bild gelöscht
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						try {

							
							//Zu löschende Dateien wird gelesen
							File deleteFile = new File(point.getPicture());
							File deleteFilePreview = new File(point
									.getPicturePreview());

							// Wenn Löschen erfolgreich, wird auch die Route und die Datenbank aktualisiert
							if (deleteFile.delete() == true
									&& deleteFilePreview.delete() == true) {

								// Remove the picture paths
								route.deletePictureDB(point);

							}
							dismiss();
							
							//Route wird neu angezeigt
							mainCallback.onShowRoute(route);

						} catch (Exception e) {

							Log.d(getTag(), e.getMessage());
						}

					}
				}).create();
	}

	/**
	 * Wenn der Dialog attached wird, wird der Callback zur MainActivity gespeichert
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
