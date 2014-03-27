package de.smbsolutions.hike.presentation.listviews;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.smbsolutions.day.R;
import de.smbsolutions.hike.functions.initialization.Device;

/**
 * Setzt die Farben für ausgewählte Listeneinträge. Zusätzlich werden
 * separatoren in die Liste eingefügt, um sie thematisch zu trennen. Manche
 * Listeneinträge werden deaktiviert, um nicht mehr klickbar zu sein. Diese
 * gelten teilweise als Ueberschriften.
 */
public class SliderMenuListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<SliderMenuItem> navDrawerItems;

	public SliderMenuListAdapter(Context context,
			ArrayList<SliderMenuItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	/**
	 * Bereitet die Listeneintraege im Slidermenu optisch auf.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater
					.inflate(R.layout.slidermenu_list_item, null);
		}

		// Legt Icon, Titel, und Zeilentrenner fest
		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		View divider = (View) convertView.findViewById(R.id.divider);

		imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		txtTitle.setText(navDrawerItems.get(position).getTitle());

		// Setzt in der SliderMenu Liste an Position 3, 6 und 7 eine weisse
		// Trennlinie.
		if (position != 3 && position != 6 && position != 7) {
			divider.setBackgroundColor(Color.TRANSPARENT);
		} else {
			divider.setBackgroundColor(Color.WHITE);
		}

		// Eintraege "Einstellungen" und "Infos zur App" in Fett darstellen
		if (txtTitle.getText().equals("Einstellungen")) {

			convertView.setEnabled(false);
			txtTitle.setTypeface(null, Typeface.BOLD);
		}
		if (txtTitle.getText().equals("Infos zur App")) {
			txtTitle.setTypeface(null, Typeface.BOLD);
		}

		// Beim ersten laden alle Listeneinträge für die Map Typen in weisser
		// Schrift darstellen.
		if (position == 0) {
			txtTitle.setTextColor(Color.WHITE);
			imgIcon.setImageResource(R.drawable.map_normal);
		} else if (position == 1) {
			txtTitle.setTextColor(Color.WHITE);
			imgIcon.setImageResource(R.drawable.map_satelite);
		} else if (position == 2) {
			txtTitle.setTextColor(Color.WHITE);
			imgIcon.setImageResource(R.drawable.map_terrain);
		}

		// Flag fuer das ausgewaehlte Element setzten.
		boolean selected = false;
		String tag = (String) convertView.getTag();
		if (tag != null) {
			if (tag.contains("selected")) {
				selected = true;
			}
		}

		// Je nachdem welcher MapTyp angeklickt wurde, erscheint er in gruener
		// Schrift.
		switch (Device.getAPP_SETTINGS().getMapType()) {
		case 1:
			if (position == 0) {

				if (selected == true) {
					txtTitle.setTextColor(Color.WHITE);
					imgIcon.setImageResource(R.drawable.map_normal);
					convertView.setTag(null);
				} else {

					imgIcon.setImageResource(R.drawable.green_map_normal);
					txtTitle.setTextColor(Color.parseColor("#7bcfa8"));

				}
			}
			break;

		case 2:
			if (position == 1) {

				if (selected == true) {
					txtTitle.setTextColor(Color.WHITE);
					imgIcon.setImageResource(R.drawable.map_satelite);
					convertView.setTag(null);
				} else {
					imgIcon.setImageResource(R.drawable.green_map_satelite);
					txtTitle.setTextColor(Color.parseColor("#7bcfa8"));
					convertView.setTag(null);
				}
			}
			break;

		case 3:
			if (position == 2) {

				if (selected == true) {
					txtTitle.setTextColor(Color.WHITE);
					imgIcon.setImageResource(R.drawable.map_terrain);
					convertView.setTag(null);
				} else {
					imgIcon.setImageResource(R.drawable.green_map_terrain);
					txtTitle.setTextColor(Color.parseColor("#7bcfa8"));
					convertView.setTag(null);
				}
			}
			break;

		default:
			break;
		}

		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Deaktiviert die Einträge an Position 3 (Einstellungen) und 7 (leerer
	 * Eintrag). Sie sind nicht mehr klickbar.
	 */
	@Override
	public boolean isEnabled(int position) {
		if (position == 3 || position == 7) {
			return false;
		}
		return super.isEnabled(position);
	}

}
