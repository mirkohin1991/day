package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class BitmapWorkerTask extends AsyncTask<Route, Void, List<ImageView>> {
	private final WeakReference<LinearLayout> layoutReference;
	private Route route;
	private File targetDirector;
	private List<ImageView> bml;
	private Context context;
	private LinearLayout layout;
	LinearLayout myGallery;

	public BitmapWorkerTask(LinearLayout layout, Context context) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		layoutReference = new WeakReference<LinearLayout>(layout);
		this.context = context;
		bml = new ArrayList<ImageView>();

	}

	// Decode image in background.
	@Override
	protected List<ImageView> doInBackground(Route... params) {

		route = params[0];
		layout = new LinearLayout(context);
		// layout.setLayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT);
		layout.setGravity(Gravity.CENTER);
		for (RoutePoint point : route.getRoutePoints()) {
			if (point.getPicture() != null) {

				targetDirector = new File(point.getPicture());
				Bitmap bm = decodeSampledBitmapFromUri(
						targetDirector.getPath(), 220, 220);//richtige größe?
				if (bm != null) {
					
					ImageView imageView = new ImageView(context);
					imageView.setLayoutParams(new LayoutParams(220, 220));
					imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					imageView.setImageBitmap(bm);
					bml.add(imageView);
				}

			}
		}
		return bml;

	}

	@Override
	protected void onPostExecute(List<ImageView> images) {
		if (layoutReference != null && bml != null) {

			myGallery = layoutReference.get();
			if (myGallery != null) {
				for (ImageView image : images) {
					layout.addView(image);
				}
				myGallery.addView(layout);

			}
		}
	}

	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {
		Bitmap bm = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	public int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

}