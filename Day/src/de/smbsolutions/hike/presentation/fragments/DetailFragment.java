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

import de.smbsolutions.hike.R;
import de.smbsolutions.hike.functions.initialization.Device;
import de.smbsolutions.hike.functions.interfaces.FragmentCallback;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.objects.Route;
import de.smbsolutions.hike.functions.objects.RoutePoint;
import de.smbsolutions.hike.functions.tasks.BitmapManager;
import de.smbsolutions.hike.functions.tasks.BitmapWorkerTask;

/**
 * 
 * Die DetailFragment-Klasse ist für die Detailansicht einer Route zuständig.
 * Hier kann der Benutzer die Route verfolgen und und Details dazu einsehen.
 * Außerdem kann von hier aus die Kamera gestartet und die Detailansicht eines
 * Bildes geöffnent werden.
 * 
 */
public class DetailFragment extends android.support.v4.app.Fragment implements
		FragmentCallback {
	// Mapfragment für das Initialisieren der GoogleMap
	private SupportMapFragment mapFragment;
	// Hauptview des Fragments
	private View view;
	// GoogleMap der aktuellen Route
	private GoogleMap map;
	// Die angezeigte Route
	private Route route;
	// MainCallback zur Kommunikation mit der Activity
	private MainCallback mCallback;
	// Hashmap mit allen anzuzeigen Bitmaps
	private LinkedHashMap<Bitmap, Timestamp> listBitmaps;
	// ImageButtons des Fragments
	private ImageButton ibCamera;
	private ImageButton ibInfoSliderIn;
	private ImageButton ibInfoSliderOut;
	private ImageButton ibPauseRoute;
	private ImageButton ibStopRoute;
	private ImageButton ibRestartRoute;
	// TextViews des Fragments
	private TextView tvDistance;
	private TextView tvDuration;
	private TextView tvAveSpeed;
	// Request Code für Kamera-Intent
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	// Pfad eines Bilder
	private Uri fileUri;
	// Media-Typ für Kamera-Intent (Wwelche Datei soll die Kamera erstellen?)
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	// Flag für Fertigstellung der Map
	private boolean mapPrepared = false;
	// Gallery für die Bilder
	private LinearLayout myGallery;
	// ViewFlipper des Fragments
	private ViewFlipper flipperInfo;
	private ViewFlipper flipperStartStop;
	// Dauer, Distanz, Durchschnittsgeschwindigkeit der aktuellen Route
	private String duration;
	private double distanceKm;
	private double aveSpeed;
	// Hintergrundtask zur Erstellung der Bitmaps
	private BitmapWorkerTask task;
	// Flag zum Status der Route
	private boolean flag_routePaused;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle data = getArguments();
		route = (Route) data.getParcelable("route");

		/*
		 * Hat die Route Bilder? Ja --> Gallery wird initialisiert. Nein -->
		 * Gallery wird nicht initialisiert.
		 */
		if (route.hasPicturePoint() == false) {

			view = inflater.inflate(R.layout.fragment_detail_nopicture,
					container, false);
		} else {
			view = inflater.inflate(R.layout.fragment_detail, container, false);

			myGallery = (LinearLayout) view
					.findViewById(R.id.LinearLayoutImage);
			// Fügt Bilder zur Gallery hinzu.
			addPhotos2Gallery(myGallery);

		}

		return view;

	}

	@Override
	public void onDestroy() {
		/*
		 * Wird ausgeführt wenn das Fragment wieder zerstört wird. Befreit
		 * Objekte zur Vermeidung von Memory Leaks
		 */

		unbindDrawables(view);
		clearFragment();
		super.onDestroy();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		/*
		 * Fügt das Maincallback hinzu.
		 */
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
		/*
		 * Initialisiert das MapFragment wenn es nicht null ist und die Activity
		 * erstellt wurde.
		 */
		android.support.v4.app.FragmentManager fm = getChildFragmentManager();

		if (mapFragment == null) {
			mapFragment = SupportMapFragment.newInstance();
			fm.beginTransaction().add(R.id.cr_map, mapFragment).commit();
		}

	}

	/**
	 * Initialisiert die Map und stellt Einstellungen zum Fragment fest. Welche
	 * Buttons und Views müssen initialisiert werden?
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();

		if (map == null) {
			// Initialierung der GoogleMap
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

		// aktualisiert das SliderMenu wenn kein Button mehr geklickt wurde.
		mCallback.refreshSliderMenu();
		// Initialisierung des Fragments
		initializeFragment();

	}

	public void initializeFragment() {
		// Initialisierung der Views und Buttons
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

		// Beendete Route sollen nicht pausiert oder gestartet werden können
		if (route.isActive() == false) {
			ibCamera.setVisibility(View.INVISIBLE);
			ibStopRoute.setVisibility(View.INVISIBLE);
			flipperStartStop.setVisibility(View.INVISIBLE);
			// route is active
		} else {

			// Legt fest welches Flipper-Kind angezeigt werden soll
			if (flag_routePaused == true) {

				flipperStartStop.setDisplayedChild(1);

			} else {

				flipperStartStop.setDisplayedChild(0);
			}

		}
		/*
		 * Wird ausgeführt wenn das Layout fertig erstellt wurde. Erst dann
		 * dürfen Einstellungen zur Map vorgenommen werden. (Zoom z.B.)
		 */

		view.post(new Runnable() {

			@Override
			public void run() {

				if (route != null) {
					if (mapPrepared == false) {
						// Erstellen der Map mit Detailinformationen
						map = route.prepareMapDetails(map, getActivity());
						mapPrepared = true;

					}

				}
			}

		});

		// Hinzufpgen aller ClickListener
		addButtonClickListenerCamera(ibCamera);
		addButtonClickListenerPauseRoute(ibPauseRoute);
		addButtonClickListenerStopRoute(ibStopRoute);
		addButtonClickListenerRestartRoute(ibRestartRoute);
		addButtonClickListenerSliderIn(ibInfoSliderIn);
		addButtonClickListenerSliderOut(ibInfoSliderOut);
	}

	/**
	 * Fügt der Gallery alle Bitmaps hinzu.
	 * 
	 * @param myGallery
	 */
	public void addPhotos2Gallery(LinearLayout myGallery) {

		myGallery.removeAllViews();
		listBitmaps = new LinkedHashMap<Bitmap, Timestamp>();
		/*
		 * Ausführen des Hintergrundtasks zur Erstellung der Bitmaps. Läuft
		 * nicht im Main-UI-Thread!
		 */
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

	/**
	 * onActivityResult wird ausgeführt wenn das Kamera-Intent ein Ergebnis
	 * zurückliefert. Dies kann ein Bilder oder Video sein.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Ist das Ergebnis ein Bild?
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			// Gibt es überhaupt ein Ergebnis?
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
					// Wenn kein Bild erstellt wird, wird null gespeichert.
				}

			}

			// Wurde überhaupt ein Bild geschossen?
			if (route.hasPicturePoint() == true) {

				// Nun muss das Layout verändert werden.
				if (myGallery == null) {
					// Neuladen des Fragments
					mCallback.onShowRoute(route);

				} else {
					// aktualisiert das ImageView
					addPhotos2Gallery(myGallery);
					route.prepareMapDetails(map, getActivity());
				}

			} else {
				// Führe PrepareMap nochmal aus.
				route.prepareMapPreview(map);
			}

		}
	}

	// Animation für den ViewFlipper
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

	// Animation für den ViewFlipper
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

	// Berechnet die Detailinformationen für die aktuelle Route
	private void calcRouteFacts(Route route) {
		// Variablen zur Berechnung
		long startDate = 0;
		long markerDate = 0;
		double markerLong = 0;
		double markerLat = 0;
		float distanceAct = 0;
		float distanceOld = 0;
		long durationAct = 0;
		long routeDuration = 0;
		long index = 0;

		double startMarkerLat = 0;
		double startMarkerLong = 0;
		float distanceTotal = 0;

		Location locStart = new Location("start");
		Location locDest = new Location("destination");

		locStart.setLatitude(startMarkerLat);
		locStart.setLongitude(startMarkerLong);
		// Iteration über alle Punkte der Route
		for (RoutePoint point : route.getRoutePoints()) {

			if (index == 0) {
				startDate = point.getTimestamp().getTime();
				startMarkerLat = point.getLatitude();
				startMarkerLong = point.getLongitude();
				locStart.setLatitude(startMarkerLat);
				locStart.setLongitude(startMarkerLong);
			} else {
				// speichert den aktuellen TimeStamp
				markerDate = point.getTimestamp().getTime();
				durationAct = markerDate - startDate;
				routeDuration += durationAct;
				startDate = point.getTimestamp().getTime();
			}

			// speichert den aktuellen TimeStamp
			markerLat = point.getLatitude();
			markerLong = point.getLongitude();

			locDest.setLatitude(markerLat);
			locDest.setLongitude(markerLong);

			// Kalkuliert die Distanz
			distanceAct = distanceOld + locStart.distanceTo(locDest);
			distanceOld = distanceAct;
			distanceTotal = distanceAct / 1000;

			// Speichert oldLat/Long als aktuelle Werte ab.
			locStart.setLatitude(markerLat);
			locStart.setLongitude(markerLong);

			index++;
		}

		// Formatiert km zu m.
		distanceKm = (double) Math.round(distanceTotal * 100.0) / 100.0;
		aveSpeed = (double) Math
				.round(((distanceAct / (routeDuration / 1000)) * 3.6) * 100.0) / 100.0;
		duration = getDuration(routeDuration);

	}

	// Löscht alle Views inkl. der Kinder eines Views
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

	// Berechnet die Dauer der aktuellen Route
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

					// Timestamp des aktuellen Punktes
					Timestamp tsClicked = (Timestamp) v.getTag();

					if (tsClicked == point.getTimestamp()) {
						// Zoom einstellen
						route.setZoomSpecificMarker(point, map);
					}

				}

			}

		});

		imageView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				// Welches ist das richtige Bild?
				for (RoutePoint point : route.getRoutePoints()) {

					// Timestamp des aktuellen Punktes
					Timestamp tsClicked = (Timestamp) v.getTag();

					if (tsClicked == point.getTimestamp()) {

						mCallback.onDeletePictureClick(route, point);

						return true;

					}

				}
				return false;
			}
		});
	}

	/**
	 * Wird ausgeführt wenn ein Task beendet wurde.
	 */
	@Override
	public void onTaskfinished(LinkedHashMap<Bitmap, Timestamp> bitmaps) {

		// Bilder sollten automatisch ins Layout passen
		int index = 0;
		for (Map.Entry<Bitmap, Timestamp> mapSet : bitmaps.entrySet()) {
			// Fügt die Bitmaps den ImageViews hinzu und läd sie in die Hashmap
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
			// Clicklistener für die einzelnen Bilder
			addImageListener(imageView);
			myGallery.addView(imageView);

			imageView = null;
		}

		bitmaps.clear();
		bitmaps = null;

	}

	@Override
	public void onRouteStopped() {
		// Beenden des Services
		mCallback.removeService();
		ibCamera.setVisibility(View.INVISIBLE);
		ibPauseRoute.setVisibility(View.INVISIBLE);
		ibStopRoute.setVisibility(View.INVISIBLE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.smbsolutions.hike.functions.interfaces.FragmentCallback#onRoutePaused
	 * ()
	 */
	@Override
	public void onRoutePaused() {
		// Beenden des Services
		mCallback.removeService();
		flag_routePaused = true;
		ibCamera.setVisibility(View.INVISIBLE);
		flipperStartStop.setDisplayedChild(1);
	}

	/**
	 *  Befreit das Fragment und löst Verknüpfungen zu bestehenden Java-Objekten
	 */
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

	/**
	 * Aktualisiert den InfoSlider mit den neuen Werten
	 */
	public void refreshInfoSlider() {

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
