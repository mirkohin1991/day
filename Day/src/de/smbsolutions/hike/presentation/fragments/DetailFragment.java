package de.smbsolutions.hike.presentation.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.hike.functions.initialization.Device;
import de.smbsolutions.hike.functions.interfaces.FragmentCallback;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.objects.Route;
import de.smbsolutions.hike.functions.objects.RoutePoint;
import de.smbsolutions.hike.functions.tasks.BitmapManager;
import de.smbsolutions.hike.functions.tasks.BitmapWorkerTask;
/**
 * 
 * Die DetailFragment-Klasse ist für die Detailansicht einer Route zuständig. Hier
 * kann der Benutzer die Route verfolgen und 
 * 
 */
public class DetailFragment extends android.support.v4.app.Fragment implements
		FragmentCallback {

	private SupportMapFragment mapFragment;
	private View view;
	private GoogleMap map;
	private Route route;
	private MainCallback mCallback;
	private LinkedHashMap<Bitmap, Timestamp> listBitmaps;
	private ImageButton ibCamera;
	private ImageButton ibInfoSliderIn;
	private ImageButton ibInfoSliderOut;
	private ImageButton ibPauseRoute;
	private ImageButton ibStopRoute;
	private ImageButton ibRestartRoute;
	private TextView tvDistance;
	private TextView tvDuration;
	private TextView tvAveSpeed;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean mapPrepared = false;
	private LinearLayout myGallery;
	private ViewFlipper flipperInfo;
	private ViewFlipper flipperStartStop;
	private String duration;
	private double distanceKm;
	private double aveSpeed;
	private BitmapWorkerTask task;
	private boolean flag_routePaused;

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

	@Override
	public void onDestroy() {
		unbindDrawables(view);

		clearFragment();
		super.onDestroy();
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
			map.getUiSettings().setCompassEnabled(false);
		} else {
			map.setMapType(Device.getAPP_SETTINGS().getMapType());
		}

		// Refreshing the slider menu, so that no item is selected any longer
		mCallback.refreshSliderMenu();

		initializeFragmentPortrait();

	}

	public void initializeFragmentPortrait() {

		ibCamera = (ImageButton) view.findViewById(R.id.ibCamera);

		ibPauseRoute = (ImageButton) view.findViewById(R.id.ibPauseRoute);

		ibStopRoute = (ImageButton) view.findViewById(R.id.ibStopRoute);
		addButtonClickListenerStopRoute(ibStopRoute);

		ibRestartRoute = (ImageButton) view.findViewById(R.id.ibRestartRoute);
		addButtonClickListenerRestartRoute(ibRestartRoute);

		ibInfoSliderIn = (ImageButton) view.findViewById(R.id.ibInfoSliderIn);

		ibInfoSliderOut = (ImageButton) view.findViewById(R.id.ibInfoSliderOut);

		refreshInfoSlider();

		flipperStartStop = (ViewFlipper) view
				.findViewById(R.id.flipperStartStop);

		// Closed routes cannot generate a new picture and cannot pause a route
		if (route.isActive() == false) {
			ibCamera.setVisibility(View.INVISIBLE);
			ibStopRoute.setVisibility(View.INVISIBLE);
			flipperStartStop.setVisibility(View.INVISIBLE);
			// route is active
		} else {

			// Callback method isServiceActive cannot be used to check which
			// icon shall be displayed,
			// because the service is connecting async.
			// But getting to the detailfragment will always restart the route

			if (flag_routePaused == true) {

				flipperStartStop.setDisplayedChild(1);

			} else {

				flipperStartStop.setDisplayedChild(0);
			}

		}

		view.post(new Runnable() {

			@Override
			public void run() {

				if (route != null) {
					if (mapPrepared == false) {
						// if point added, only edit polyline and add
						// new marker!!!
						map = route.prepareMapDetails(map, getActivity());
						mapPrepared = true;

					}

				}
			}

		});
		addButtonClickListenerCamera(ibCamera);
		addButtonClickListenerPauseRoute(ibPauseRoute);
		addButtonClickListenerStopRoute(ibStopRoute);
		addButtonClickListenerRestartRoute(ibRestartRoute);
		addButtonClickListenerSliderIn(ibInfoSliderIn);
		addButtonClickListenerSliderOut(ibInfoSliderOut);
	}

	public void addPhotos2Gallery(LinearLayout myGallery) {

		myGallery.removeAllViews();
		listBitmaps = new LinkedHashMap<Bitmap, Timestamp>();
		task = new BitmapWorkerTask(listBitmaps, this);
		task.execute(route);
		myGallery.removeAllViews();
		myGallery = null;

	}

	public void addButtonClickListenerRestartRoute(ImageButton imageButton) {

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mCallback.restartTracking(route);
				flag_routePaused = false;

				ibCamera.setVisibility(View.VISIBLE);
				flipperStartStop.setDisplayedChild(0);

			}
		});
		Log.d("Test", "addButtonClickListenerRestartRoute gestartet");
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
		Log.d("Test", "addButtonClickListenerCamera gestartet");
	}

	public void addButtonClickListenerPauseRoute(ImageButton imageButton) {
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mCallback.onOpenDialogPauseRoute(route);

			}
		});
		Log.d("Test", "addButtonClickListenerPauseRoute gestartet");
	}

	public void addButtonClickListenerStopRoute(ImageButton imageButton) {

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mCallback.onOpenDialogStopRoute("DETAIL", route);

			}
		});
		Log.d("Test", "addButtonClickListenerStopRoute gestartet");
	}

	public void addButtonClickListenerSliderIn(ImageButton imageButton) {
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				flipperInfo = (ViewFlipper) view.findViewById(R.id.flipperInfo);
				flipperInfo.setInAnimation(inFromLeftAnimation());
				flipperInfo.setOutAnimation(outToLeftAnimation());
				flipperInfo.showNext();
			}
		});
		Log.d("Test", "addButtonClickListenerSliderIn gestartet");
	}

	public void addButtonClickListenerSliderOut(ImageButton imageButton) {
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				flipperInfo = (ViewFlipper) view.findViewById(R.id.flipperInfo);
				flipperInfo.setInAnimation(inFromLeftAnimation());
				flipperInfo.setOutAnimation(outToLeftAnimation());
				flipperInfo.showNext();
			}
		});
		Log.d("Test", "addButtonClickListenerSliderOut gestartet");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

			// Getting the current timestamp

			if (resultCode == -1) {

				int scrollbarHeight = Device.getPictureScrollbarDensity();

				File small_picture = BitmapManager
						.savePreviewBitmapToStorage(fileUri);

				if (small_picture != null) {

					Bitmap bitmap = BitmapManager
							.decodeSampledBitmapFromUri(fileUri.getPath(),
									scrollbarHeight, scrollbarHeight);

					FileOutputStream fOut = null;
					try {
						fOut = new FileOutputStream(small_picture);
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
						fOut.flush();
						fOut.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					bitmap.recycle();
					bitmap = null;
					fOut = null;

					mCallback.onPictureTaken(route, fileUri, small_picture);
					small_picture = null;
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

					mCallback.onShowRoute(route);

				} else {
					// refresh the image view
					addPhotos2Gallery(myGallery);
					route.prepareMapDetails(map, getActivity());
				}

			} else {
				route.prepareMapPreview(map);
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

	private void calcRouteFacts(Route route) {
		long startDate = 0;
		long markerDate = 0;
		double markerLong = 0;
		double markerLat = 0;
		float distanceAct = 0;
		float distanceOld = 0;
		long durationAct = 0;
		long routeDuration = 0;
		long index = 0;

		// Set the start Lat and Long
		double startMarkerLat = 0;
		double startMarkerLong = 0;
		float distanceTotal = 0;

		Location locStart = new Location("start");
		Location locDest = new Location("destination");

		locStart.setLatitude(startMarkerLat);
		locStart.setLongitude(startMarkerLong);

		for (RoutePoint point : route.getRoutePoints()) {

			if (index == 0) {
				startDate = point.getTimestamp().getTime();
				startMarkerLat = point.getLatitude();
				startMarkerLong = point.getLongitude();
				locStart.setLatitude(startMarkerLat);
				locStart.setLongitude(startMarkerLong);
			} else {
				// Gets the actual timestamp
				markerDate = point.getTimestamp().getTime();
				durationAct = markerDate - startDate;
				routeDuration += durationAct;
				startDate = point.getTimestamp().getTime();
			}

			// Gets the actual lat and long
			markerLat = point.getLatitude();
			markerLong = point.getLongitude();

			locDest.setLatitude(markerLat);
			locDest.setLongitude(markerLong);

			// Calculates the distance
			distanceAct = distanceOld + locStart.distanceTo(locDest);
			distanceOld = distanceAct;
			distanceTotal = distanceAct / 1000;

			// Sets the "old" lat and long as new start lat and long
			locStart.setLatitude(markerLat);
			locStart.setLongitude(markerLong);

			index++;
		}

		// Calculates the distance from km to meter
		distanceKm = (double) Math.round(distanceTotal * 100.0) / 100.0;
		aveSpeed = (double) Math
				.round(((distanceAct / (routeDuration / 1000)) * 3.6) * 100.0) / 100.0;
		duration = getDuration(routeDuration);

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

	public String getDuration(long timeseconds) {
		String time = null;
		long duration = timeseconds;

		 duration = duration / 1000;

		long second = duration % 60;
		long minute = (duration % 3600) / 60;
		long hour = duration / 3600;
		long day = duration / 3600 / 24;
		hour = hour - (day * 24);

		String sSecond = String.format("%02d", second);
		String sMinute = String.format("%02d", minute);
		String sHour = String.format("%02d", hour);
		String sDay = String.format("%02d", day);
		if (second >= 0 || minute >= 0 || hour >= 0 || day >= 0) {
			if (minute >= 1) {
				if (hour >= 1) {
					if (day >= 1) {
						time = sDay + " T, " + sHour + ":" + sMinute
								+ " Stunden";
					} else {
						time = sHour + ":" + sMinute + " Stunden";
					}

				} else {
					time = sMinute + ":" + sSecond + " Minuten";
				}
			} else {
				time = sSecond + " Sekunden";
			}
		}

		return time;
	}

	public void addImageListener(ImageView imageView) {
		imageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			}
		});

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				for (RoutePoint point : route.getRoutePoints()) {

					// Timestamp of the clicked picture
					Timestamp tsClicked = (Timestamp) v.getTag();

					if (tsClicked == point.getTimestamp()) {

						route.setZoomSpecificMarker(point, map);
					}

				}

			}

		});

		imageView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				// Looping over the routelist to get the right picture
				for (RoutePoint point : route.getRoutePoints()) {

					// Timestamp of the clicked picture
					Timestamp tsClicked = (Timestamp) v.getTag();

					if (tsClicked == point.getTimestamp()) {

						// Call the Callback interface to execute the required
						// action
						mCallback.onDeletePictureClick(route, point);

						return true;

					}

				}
				return false;
			}
		});
	}

	@Override
	public void onTaskfinished(LinkedHashMap<Bitmap, Timestamp> bitmaps) {

		// Bilder sollten automatisch ins Layout passen
		int index = 0;
		for (Map.Entry<Bitmap, Timestamp> mapSet : bitmaps.entrySet()) {

			ImageView imageView = new ImageView(getActivity());
			imageView.setAdjustViewBounds(true);
			if (index == 0) {
				imageView.setPadding(0, 12, 0, 12);
			} else {
				imageView.setPadding(5, 12, 0, 12);
			}
			index++;
			imageView.setImageBitmap(mapSet.getKey());
			imageView.setTag(mapSet.getValue());

			addImageListener(imageView);
			myGallery.addView(imageView);

			imageView = null;
		}

		bitmaps.clear();
		bitmaps = null;

	}

	@Override
	public void onRouteStopped() {

		mCallback.removeService();

		ibCamera.setVisibility(View.INVISIBLE);
		ibPauseRoute.setVisibility(View.INVISIBLE);
		ibStopRoute.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onRoutePaused() {

		mCallback.removeService();

		flag_routePaused = true;

		ibCamera.setVisibility(View.INVISIBLE);
		flipperStartStop.setDisplayedChild(1);
	}

	public void clearFragment() {
		if (listBitmaps != null) {
			for (Map.Entry<Bitmap, Timestamp> mapSet : listBitmaps.entrySet()) {
				mapSet.getKey().recycle();
			}
			listBitmaps.clear();
			listBitmaps = null;
		}
		if (myGallery != null) {
			myGallery.removeAllViews();
			myGallery = null;
		}

		if (map != null) {
			map.clear();
			map = null;
		}

		ibCamera.setImageBitmap(null);
		ibCamera = null;

		ibPauseRoute.setImageBitmap(null);
		ibPauseRoute = null;

		ibStopRoute.setImageBitmap(null);
		ibStopRoute = null;

		if (mapFragment != null) {
			mapFragment = null;
		}
		ibInfoSliderIn.setImageBitmap(null);
		ibInfoSliderIn = null;
		ibInfoSliderOut.setImageBitmap(null);
		ibInfoSliderOut = null;
		tvDistance = null;
		tvDuration = null;
		tvAveSpeed = null;
		mCallback = null;
		view = null;
		task = null;
		fileUri = null;

	}

	public void refreshInfoSlider() {
		// Method is called when a new routepoint was added
		// Therefore, the fact slider has to be refreshed
		calcRouteFacts(route);

		tvDistance = (TextView) view.findViewById(R.id.tvDistance);

		tvDistance
				.setText(String.valueOf("Strecke: ca. " + distanceKm + " km"));

		tvDuration = (TextView) view.findViewById(R.id.tvDuration);

		tvDuration.setText(String.valueOf("Dauer: " + duration));

		tvAveSpeed = (TextView) view.findViewById(R.id.tvAveSpeed);
		tvAveSpeed.setText(String.valueOf("Gesch.: " + aveSpeed + " km/h"));

	}
}
