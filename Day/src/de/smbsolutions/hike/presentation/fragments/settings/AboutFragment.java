package de.smbsolutions.hike.presentation.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.smbsolutions.hike.R;

public class AboutFragment extends android.support.v4.app.Fragment {

	public AboutFragment() {
	}

	/**
	 * Ruft das fragment_settings_about Layout auf.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_settings_about,
				container, false);

		return rootView;
	}
}
