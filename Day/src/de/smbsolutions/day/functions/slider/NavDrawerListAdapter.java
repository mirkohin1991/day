package de.smbsolutions.day.functions.slider;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.smbsolutions.day.R;

public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;

	public NavDrawerListAdapter(Context context,
			ArrayList<NavDrawerItem> navDrawerItems) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		View divider = (View) convertView.findViewById(R.id.divider);

		imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		txtTitle.setText(navDrawerItems.get(position).getTitle());

		if (position != 3 && position != 6 && position != 7) {

			divider.setBackgroundColor(Color.TRANSPARENT);

		} else {
			divider.setBackgroundColor(Color.WHITE);
//			
//
//		RelativeLayout.LayoutParams layout_description = new RelativeLayout.LayoutParams(
//					10, 5);
//		slider_item_layout.setLayoutParams(layout_description);
		}

		if (txtTitle.getText().equals("Einstellungen")) {

			convertView.setEnabled(false);
			txtTitle.setTypeface(null, Typeface.BOLD);
			
			// REMOVE ICON
//			RelativeLayout slider_item_layout = (RelativeLayout) convertView
//					.findViewById(R.id.layout_slidermenu_item);		
//			slider_item_layout.removeView(imgIcon);

		}
		
		if (txtTitle.getText().equals("Infos zur App")) {
			txtTitle.setTypeface(null, Typeface.BOLD);
		}
		

		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub

		// The settings text shall not be clickable
		if (position == 3 || position == 7) {
			return false;
		}
		return super.isEnabled(position);
	}

}
