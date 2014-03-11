package de.smbsolutions.day.presentation.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.initialization.Device;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.tasks.BitmapManager;
import de.smbsolutions.day.functions.tasks.BitmapWorkerTask;

public class DetailFragment extends android.support.v4.app.Fragment {

	private SupportMapFragment mapFragment;
	private View view;
	private GoogleMap map;
	private Route route;
	private WeakReference<MainCallback> weakCallBack;
	private ImageButton ibCamera;
	private ImageButton ibInfoSliderIn;
	private ImageButton ibInfoSliderOut;
	private TextView tvDistance;
	private TextView tvDuration;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean mapPrepared = false;
	private LinearLayout myGallery;
	private ViewFlipper flipper;
	private String duration;
	private int distanceMeter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle data = getArguments();
		route = (Route) data.getParcelable("route");

		// If a route doesn't have a picture point, the Picture Scrollbar is
		// disabled
		if (route.hasPicturePoint() == false) {

			view = inflater.inflate(R.layout.fragment_detail_nopicture,
					container, false);
		} else {
			view = inflater.inflate(R.layout.fragment_detail, container, false);

			myGallery = (LinearLayout) view
					.findViewById(R.id.LinearLayoutImage);
			addPhotos2Gallery(myGallery);

		}

		return view;

	}

	// /DEBUG FRAGMENT LIFECYLCE
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (myGallery != null) {
			myGallery.removeAllViews();
			myGallery = null;
		}

		unbindDrawables(view);

		if (map != null) {
			map.clear();
			map = null;
			mapFragment = null;

		}
		flipper = null;
		view = null;
		route = null;

		super.onDestroy();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			MainCallback mCallback = (MainCallback) activity;
			weakCallBack = new WeakReference<MainCallback>(mCallback);

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnButtonClick Interface");
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		android.support.v4.app.FragmentManager fm = getChildFragmentManager();

		if (mapFragment == null) {
			mapFragment = SupportMapFragment.newInstance();
			fm.beginTransaction().add(R.id.cr_map, mapFragment).commit();
		}

	}

	public void onResume() {
		super.onResume();
		if (map == null) {
			map = mapFragment.getMap();
			map.setMapType(Device.getAPP_SETTINGS().getMapType());
			// padding muss noch je nach gerätetyp gesetzt werden
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setMyLocationEnabled(true);
			map.setBuildingsEnabled(true);
			
		}

		initializeFragmentPortrait();

	}

	public void initializeFragmentPortrait() {

		if (ibCamera == null) {
			ibCamera = (ImageButton) view.findViewById(R.id.ibCamera);
			addButtonClickListenerCamera(ibCamera);
		}

		if (ibInfoSliderIn == null) {
			ibInfoSliderIn = (ImageButton) view
					.findViewById(R.id.ibInfoSliderIn);
			addButtonClickListenerSliderIn(ibInfoSliderIn);
		}
		if (ibInfoSliderOut == null) {
			ibInfoSliderOut = (ImageButton) view
					.findViewById(R.id.ibInfoSliderOut);
			addButtonClickListenerSliderOut(ibInfoSliderOut);
		}

		calcAltitude(route);
		if (tvDistance == null) {
			tvDistance = (TextView) view.findViewById(R.id.tvDistance);
			tvDistance.setText(String.valueOf(distanceMeter));
		}
		if (tvDistance == null) {
			tvDuration = (TextView) view.findViewById(R.id.tvDuration);
			tvDuration.setText(String.valueOf(duration));
		}

		// Closed routes cannot generate a new picture
		if (route.isActive() == false) {
			ibCamera.setVisibility(View.INVISIBLE);
		}

		view.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (route != null) {
							if (mapPrepared == false) {
								// if point added, only edit polyline and add
								// new marker!!! TODO
								map = route.prepareMapDetails(map, getActivity());
								mapPrepared = true;

							}

						}

					}

				});

	}

	public void addPhotos2Gallery(LinearLayout myGallery) {

		myGallery.removeAllViews();

		final BitmapWorkerTask task = new BitmapWorkerTask(myGallery,
				getActivity());
		task.execute(route);

	}

	public void addButtonClickListenerCamera(ImageButton imageButton) {
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

	public void addButtonClickListenerSliderIn(ImageButton imageButton) {
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				flipper = (ViewFlipper) view.findViewById(R.id.flipper);
				flipper.setInAnimation(inFromLeftAnimation());
				flipper.setOutAnimation(outToLeftAnimation());
				flipper.showNext();
			}
		});

	}

	public void addButtonClickListenerSliderOut(ImageButton imageButton) {
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				flipper = (ViewFlipper) view.findViewById(R.id.flipper);
				flipper.setInAnimation(inFromLeftAnimation());
				flipper.setOutAnimation(outToLeftAnimation());
				flipper.showNext();
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

			// Getting the current timestamp
			Timestamp tsTemp = new Timestamp(System.currentTimeMillis());
			if (resultCode == -1) {

				File small_picture = BitmapManager
						.savePreviewBitmapToStorage(fileUri);

				if (small_picture != null) {

					Bitmap bitmap = BitmapManager.decodeSampledBitmapFromUri(
							fileUri.getPath(), 250, 250);

					FileOutputStream fOut = null;
					try {
						fOut = new FileOutputStream(small_picture);
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
						fOut.flush();
						fOut.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bitmap.recycle();
					weakCallBack.get().onPictureTaken(route, fileUri,
							small_picture);

					// If no small picture could be created, NULL is stored
				} else {
					// SOll hier noch was passieren???
					// route.addRoutePointDB(new RoutePoint(route.getId(),
					// tsTemp,
					// fileUri.getPath(), null, gps.getLatitude(), gps
					// .getLongitude(), gps.getAltitude()));

				}

			}

			// now at least one picture was taken
			if (route.hasPicturePoint() == true) {

				// If it is the first picture ever taken the layout has to be
				// changed to the one with picture scrollbar
				if (myGallery == null) {

					// Vielleicht gibt es noch eine bessere Lösung.

					weakCallBack.get().onShowRoute(route);

				} else {
					// refresh the image view
					addPhotos2Gallery(myGallery);
					route.prepareMapDetails(map, getActivity());
				}

			} else {
				route.prepareMapDetails(map, getActivity());
			}

		}
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.5f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.5f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private void calcAltitude(Route route) {
		long startDate = 0;
		long markerDate = 0;
		double markerLong = 0;
		double markerLat = 0;

		// Set the start Lat and Long
		double startMarkerLat = 0;
		double startMarkerLong = 0;
		float distanceTotal = 0;

		Location locStart = new Location("start");
		Location locDest = new Location("destination");

		locStart.setLatitude(startMarkerLat);
		locStart.setLongitude(startMarkerLong);

		long durationAct = 0;
		int index = 0;
		for (RoutePoint point : route.getRoutePoints()) {
			if (index == 0) {
				startDate = point.getTimestamp().getTime();
				startMarkerLat = point.getLatitude();
				startMarkerLong = point.getLongitude();
			}

			// Gets the actual lat and long
			markerLat = point.getLatitude();
			markerLong = point.getLongitude();

			locDest.setLatitude(markerLat);
			locDest.setLongitude(markerLong);

			// Calculates the distance
			float distanceAct = locStart.distanceTo(locDest);
			distanceTotal = distanceAct + locStart.distanceTo(locDest);
			distanceTotal = distanceTotal * 1000;

			// Sets the "old" lat and long as new start lat and long
			locStart.setLatitude(markerLat);
			locStart.setLongitude(markerLong);

			// Gets the actual timestamp
			markerDate = point.getTimestamp().getTime();

			// Calculates the duration of the route
			// ENDGÜLTIGE
			// ZEIT*************************************************************
			durationAct = (markerDate - startDate);

			index++;
		}

		// Calculates the distance from km to meter
		distanceMeter = (int) Math.round(distanceTotal);

		// Formats the Duration from Miliseconds to an readable format
		// ENDGÜLTIGE
		// DAUER*************************************************************
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
		duration = formatter.format(durationAct);

	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);

		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

}