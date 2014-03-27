package de.smbsolutions.hike.presentation.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.smbsolutions.hike.R;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.objects.Route;
import de.smbsolutions.hike.functions.objects.RouteList;

/**
 * Dialog, der aufgerufen wird wenn der Benutzer eine neue Route anlegen will
 */
public class CreateRouteDialog extends android.support.v4.app.DialogFragment {

	private RouteList routeList;
	private Bundle bundle;
	private MainCallback mCallback;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Aufbauen des Dialoges
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Mitgegebene RouteList abrufen
		bundle = this.getArguments();
		routeList = (RouteList) bundle.getParcelable("routeList");

		// Das eigene Dialoglayout wird eingebunden
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View nameView = inflater.inflate(R.layout.dialog_createroute, null);
		builder.setView(nameView);

		// Hinzufügen des Anlegen-Buttons. Der Hanlder wird später selbst
		// gesetzt und überschreibt die Standard-Logik
		builder.setPositiveButton("Anlegen", null

		// Hinfügen des Abbrechen Buttons. Wenn dieser gedrückt wird, muss der
		// zuvor gestartete Service wieder beendet werden
		).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				mCallback.removeService();
			}
		});

		// Der AlertDialog wird erstellt und zurückgegeben
		final AlertDialog dialog = builder.create();

		// Eigener ShowDialog Listener, um die ButtonClick events selbst zu
		// definieren
		// -> Nur dadurch ist es möglich das Schließen des Dialogs bei
		// fehlerhafter Eingabe des Routenamen zu unterdrücken
		dialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button btnPositive = ((AlertDialog) dialog)
						.getButton(AlertDialog.BUTTON_POSITIVE);
				btnPositive.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						// Eigegebener Name wird ausgelesen
						EditText nameText = (EditText) getDialog()
								.findViewById(R.id.routename);
						String routeName = nameText.getText().toString();

						// Wenn der Name leer ist, erscheint eine Errormessage
						// als Hinweis
						if (routeName.isEmpty()) {

							nameText.setHint("Bitte Namen eingeben");
							nameText.setHintTextColor(Color.RED);

							// Wenn alles gut ging, wird die neue Route angelegt
						} else {

							// Dialog soll beendet werden
							dismiss();

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

	/**
	 * Wenn der Dialog attached wird, wird der Callback zur MainActivity
	 * gespeichert
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (MainCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " muss MainCallback Inteface implementieren");
		}
	}

}
