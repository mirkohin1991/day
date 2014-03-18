package de.smbsolutions.day.presentation.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.smbsolutions.day.R;

public class FacebookFragment extends android.support.v4.app.Fragment {
	
	public FacebookFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_settings_facebook, container, false);
         
        return rootView;
    }
}

