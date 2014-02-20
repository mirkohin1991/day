package de.smbsolutions.day.presentation.activities;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;

public class PictureActivity extends Activity {

	ImageView image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture);

		image = (ImageView) findViewById(R.id.imageView1);

		String timestamp = getIntent().getExtras().getString("timestamp");

		Uri uri = Uri.parse(Database.getSingleRoutePoint(timestamp)
				.getPicture());
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		image.setImageBitmap(bitmap);

	}

}
