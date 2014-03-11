package de.smbsolutions.day.presentation.listviews;


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
import de.smbsolutions.day.functions.initialization.Device;

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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.slidermenu_list_item, null);
		}
		
		
		

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		View divider = (View) convertView.findViewById(R.id.divider);

		imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		txtTitle.setText(navDrawerItems.get(position).getTitle());

		if (position != 3 && position != 7 && position != 8) {

			divider.setBackgroundColor(Color.TRANSPARENT);

		} else {
			divider.setBackgroundColor(Color.WHITE);
		}
		
		

		if (txtTitle.getText().equals("Einstellungen")) {

			convertView.setEnabled(false);
			txtTitle.setTypeface(null, Typeface.BOLD);
			
		}
		
		if (txtTitle.getText().equals("Infos zur App")) {
			txtTitle.setTypeface(null, Typeface.BOLD);
		}
		
		

		//initally set all elements white
		txtTitle.setTextColor(Color.WHITE);
		imgIcon.setImageResource(R.drawable.map_normal);
		
		
		boolean selected = false;
		String tag = (String) convertView.getTag();
		if (tag != null) {
			if (tag.contains("selected")) {
			
			selected = true;
		}
		} 
			
	   
		switch (Device.getAPP_SETTINGS().getMapType()) {
		case 1:
		 if (position == 0)
		 {   
			 
			 if ( selected == true) {
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
			if (position == 1)
			 {
				 
			 if ( selected == true) {
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
			if (position == 2)
			 {
				 
				 if ( selected == true) {
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

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub

		// The settings text shall not be clickable
		if (position == 3 || position == 8) {
			return false;
		}
		return super.isEnabled(position);
	}

}
