package de.smbsolutions.hike.presentation.fragments;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import de.smbsolutions.day.R;
import de.smbsolutions.hike.functions.interfaces.MainCallback;
import de.smbsolutions.hike.functions.objects.Route;
import de.smbsolutions.hike.functions.objects.RoutePoint;
import de.smbsolutions.hike.functions.tasks.BitmapManager;

/**
 * Diese Klasse beschäftigt sich mit dem PictureView. Auf diesem wird ein Bild
 * in Großansicht angezeigt und kann geteilt sowie gelöscht werden.
 */
public class PictureFragment extends android.support.v4.app.Fragment {

	private Bundle data;
	private View view;
	private MainCallback mainCallback;
	private Route route;
	private RoutePoint routePoint;
	private ImageView pictureView;
	private ImageButton btnSharePicture;
	private ImageButton btnDeletePicture;
	private File pictureFile;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Mitgegebene Parameter werden ausgelesen
		data = getArguments();
		route = (Route) data.getParcelable("route");
		routePoint = (RoutePoint) data.getParcelable("point");

		// Benötigte Views des layouts werden ausgelesen
		view = inflater.inflate(R.layout.fragment_picture, container, false);
		pictureView = (ImageView) view.findViewById(R.id.imageViewFullPicture);
		btnSharePicture = (ImageButton) view
				.findViewById(R.id.pictureShareButton);
		btnDeletePicture = (ImageButton) view
				.findViewById(R.id.pitctureDeleteButton);

		addButtonClickListener();

		return view;

	}

	/**
	 * Wenn der Dialog attached wird, wird der Callback zur MainActivity
	 * gespeichert
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mainCallback = (MainCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " muss mainCallback Interface implementieren");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onResume() {
		super.onResume();

		// Layout wird aufgebaut
		initializeFragmentPortrait();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		data = null;
		view = null;
		mainCallback = null;
		route = null;
		routePoint = null;
		pictureView = null;
		btnSharePicture = null;
		btnDeletePicture = null;
		pictureFile = null;

	}

	/**
	 * Methode, die dem View das jeweilige Bild hinzufügt
	 */
	public void initializeFragmentPortrait() {

		try {
			pictureFile = new File(routePoint.getPicture());
			Bitmap bm = BitmapManager.decodeSampledBitmapFromUri(
					pictureFile.getPath(), 1000, 1000);// richtige gre?

			if (bm != null) {
				pictureView.setImageBitmap(bm);
			}
		} catch (Exception e) {
			Log.d("PictureFrag / Loading Picture", "Error loading Bitmap");
		}

	}

	/**
	 * ButtonClick Listener für Share- und Lösch-Funktionalität
	 */
	private void addButtonClickListener() {

		// Wenn auf den ShareButton geklickt wird, wird dem Benutzer ein Dialog
		// mit möglichen Share-Funktionen seines Smartphones angezeigt
		btnSharePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Ein Dialog mit entsprechenden Sharemöglichkeiten wird
				// geöffnet
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("image/*");
				sharingIntent.putExtra(Intent.EXTRA_TEXT,
						"Aufgenommen mit 'Hike', der interaktiven Wander-App!");
				sharingIntent.putExtra(Intent.EXTRA_STREAM,
						Uri.fromFile(pictureFile));
				startActivity(Intent.createChooser(sharingIntent,
						"Bild teilen mit"));
			}
		});

		// Löschen eines Fotos
		btnDeletePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Entsprechende MainCallback Methode wird aufgerufen
				mainCallback.onDeletePictureClick(route, routePoint);
			}
		});

	}

}
