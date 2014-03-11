package de.smbsolutions.day.presentation.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.tasks.BitmapManager;

public class PictureFragment extends android.support.v4.app.Fragment {

	private Bundle data;
	private View view;
	private Configuration config;

	private MainCallback mCallback;

	private Route route;
	private RoutePoint routePoint;
	private ImageView pictureView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		data = getArguments();
		route = (Route) data.getParcelable("route");
		routePoint = (RoutePoint) data.getParcelable("point");

		view = inflater.inflate(R.layout.fragment_picture, container, false);
		pictureView = (ImageView) view.findViewById(R.id.imageViewFullPicture);

		return view;

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

	public void onResume() {
		super.onResume();

		initializeFragmentPortrait();

	}



	public void initializeFragmentPortrait() {
		File targetDirector;

		targetDirector = new File(routePoint.getPicturePreview());
		Bitmap bm = BitmapManager.decodeSampledBitmapFromUri(
				targetDirector.getPath(), 220, 220);// richtige größe?

		if (bm != null) {
			pictureView.setImageBitmap(bm);
		}

	}

	// BRAUCHEN WIR DAS?
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

}
