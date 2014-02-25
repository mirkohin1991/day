package de.smbsolutions.day.presentation.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.location.GPSTracker;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.tasks.BitmapManager;
import de.smbsolutions.day.functions.tasks.BitmapWorkerTask;

public class DetailFragment extends android.support.v4.app.Fragment {

	private SupportMapFragment fragment;
	private View view;
	private GoogleMap map;
	private Configuration config;
	private Bundle data;
	private Route route;
	private int index;
	private MainCallback mCallback;
	private ImageButton imageButton;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean mapPrepared = false;
	private static Activity context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		config = getResources().getConfiguration();

		view = inflater.inflate(R.layout.cr_fragment, container, false);
		data = getArguments();
		route = (Route) data.getParcelable("route");
		index = data.getInt("index");
		addPhotos2Gallery();
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		android.support.v4.app.FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.cr_map);

		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().add(R.id.cr_map, fragment).commit();
		}
	}

	public void onResume() {
		super.onResume();

		context = getActivity();

		if (map == null) {
			map = fragment.getMap();
		}

		// checking device orientation for layout
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (map == null) {
				map = fragment.getMap();
			}

			initializeFragmentLandscape();

		} else {

			if (map == null) {
				map = fragment.getMap();
			}

			initializeFragmentPortrait();

		}

	}

	public void initializeFragmentLandscape() {

		// map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		// LinearLayout linleaLayout = (LinearLayout) view
		// .findViewById(R.id.LinearLayoutcR);
		// linleaLayout.getViewTreeObserver().addOnGlobalLayoutListener(
		// new OnGlobalLayoutListener() {
		//
		// @Override
		// public void onGlobalLayout() {
		//
		// map = routelist.getListRoutes().get(index).prepareMap(map,
		// getActivity(), false);
		//
		// }
		// });

	}

	public void initializeFragmentPortrait() {

		map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		map.setPadding(0, 100, 0, 100);
		map.getUiSettings().setZoomControlsEnabled(false);
		LinearLayout linleaLayout = (LinearLayout) view
				.findViewById(R.id.LinearLayoutcR);
		imageButton = (ImageButton) view.findViewById(R.id.imagebutton1);

		// If a route doesn't have a picture point, the Picture Scrollbar is
		// disabled
		if (route.hasPicturePoint() == false) {

			LinearLayout linlayout = (LinearLayout) view
					.findViewById(R.id.LinearLayoutcR);
			linlayout.removeView(view
					.findViewById(R.id.RelativeHorizontalScrollViewLayout));
		}

		// Closed routes cannot generate a new picture
		if (route.getActive().equals("")) {
			imageButton.setVisibility(View.INVISIBLE);
		}

		linleaLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (route != null) {
							if (mapPrepared == false) {
								map = route
										.prepareMap(map, getActivity(), true);
								mapPrepared = true;
								addButtonClickListener(imageButton);

							}

						}

					}

				});

	}

	public void addPhotos2Gallery() {
		LinearLayout myGallery = (LinearLayout) view
				.findViewById(R.id.LinearLayoutImage);

		myGallery.removeAllViews(); // bessere lösung, immer nur das neue bild
									// einfügen?
		BitmapWorkerTask task = new BitmapWorkerTask(myGallery, getActivity());
		task.execute(route);

	}

	public void addButtonClickListener(ImageButton imageButton) {
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				fileUri = BitmapManager.getOutputMediaFileUri(MEDIA_TYPE_IMAGE,
						false);

				// create intent with ACTION_IMAGE_CAPTURE action
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				// save the image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				// start camera activity
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			GPSTracker gps = GPSTracker.getInstance(getActivity());
			if (gps.canGetLocation()) {
              
				// Getting the current timestamp
				Timestamp tsTemp = new Timestamp(System.currentTimeMillis());
				if (resultCode == -1) {

					File small_picture = BitmapManager
							.savePreviewBitmapToStorage(fileUri);

					if (small_picture != null) {

						Bitmap bitmap = BitmapManager
								.decodeSampledBitmapFromUri(fileUri.getPath(),
										250, 250);

						FileOutputStream fOut = null;
						try {
							fOut = new FileOutputStream(small_picture);
							bitmap.compress(Bitmap.CompressFormat.PNG, 100,
									fOut);
							fOut.flush();
							fOut.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						route.addRoutePointDB(new RoutePoint(route.getId(),
								tsTemp, fileUri.getPath(), small_picture
										.getPath(), gps.getLatitude(), gps
										.getLongitude()));

						// If no small picture could be created, NULL is stored
					} else {
						route.addRoutePointDB(new RoutePoint(route.getId(),
								tsTemp, fileUri.getPath(), null, gps
										.getLatitude(), gps.getLongitude()));

					}

					// route erneut anzeigen
					mCallback.onCamStart(route);

				}

			} else {
				// route erneut anzeigen
				mCallback.onCamStart(route);

				Toast.makeText(getActivity(),
						"Keine Ortung möglich, bitte erneut versuchen",
						Toast.LENGTH_LONG);

			}

		}
	}

}