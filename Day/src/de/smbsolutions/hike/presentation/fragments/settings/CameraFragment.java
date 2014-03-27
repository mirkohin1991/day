package de.smbsolutions.hike.presentation.fragments.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import de.smbsolutions.day.R;
import de.smbsolutions.hike.functions.database.Database;

/**
 * Speichert ob die Fotos in der Galerie App des Smartphones anzeigt werden
 * sollen oder nicht. Je nachdem wird im Ordner "Hike" ein ".nomedia" File
 * erstellt oder gelöscht, das der Galerie App sagt, ob sie diesen Ordner
 * aufnehmen soll oder nicht.
 */

public class CameraFragment extends android.support.v4.app.Fragment {

	private View view;
	private Switch switchShowInGal;

	/**
	 * Bei Aufruf das Fragment "fragment_settings_camera" aufrufen.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_settings_camera, container,
				false);
		return view;
	}

	@Override
	public void onResume() {
		// Erstellt den Pfad an dem die Bilder gespeichert werden
		final File path = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Hike");

		if (switchShowInGal == null) {
			switchShowInGal = (Switch) view.findViewById(R.id.switchShowInGal);
		}

		// Wert (On/Off) aus der Datenbank abrufen. Das Switch auf die jeweilige
		// Positzion setzen.
		if (Database.getSettingValue(Database.SETTINGS_SHOW_IN_GAL) == 1) {
			switchShowInGal.setChecked(true);
		} else {
			switchShowInGal.setChecked(false);

		}

		view.post(new Runnable() {

			@Override
			public void run() {

				// Wenn Wert geaendert wird, diesen in die Datenbank schreiben
				switchShowInGal
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// Wenn das Switch aktiv ist:
								if (isChecked) {

									// Wert in der Datenbank auf 1 setzten
									Database.changeSettingValue(
											Database.SETTINGS_SHOW_IN_GAL, 1);

									// Löscht die Datei (.nomedia) aus dem
									// Ordner "Hike"
									// Bilder werden in der Galerie App
									// angezeigt
									String nomediaFile = path.toString()
											+ "/.nomedia";
									File file = new File(nomediaFile);
									boolean deleted = file.delete();
								} else {
									// Wert in der Datenbank auf 0 setzten
									Database.changeSettingValue(
											Database.SETTINGS_SHOW_IN_GAL, 0);

									// Erstellt die Datei ".nomedia".
									// Bilder werden nicht in der Galerie App
									// angezeigt
									try {
										FileWriter fileWriter = new FileWriter(
												path + "/.nomedia");
										fileWriter.close();
										fileWriter = null;
									} catch (IOException e) {
										e.printStackTrace();
									}

								}
							}
						});
			}
		});
		super.onResume();
	}

	@Override
	public void onDestroy() {
		view = null;
		switchShowInGal = null;
		super.onDestroy();
	}
}
