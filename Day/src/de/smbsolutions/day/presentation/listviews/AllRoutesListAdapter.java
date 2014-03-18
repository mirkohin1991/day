package de.smbsolutions.day.presentation.listviews;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;

public class AllRoutesListAdapter extends ArrayAdapter<AllRoutesListElement> {

	private List<AllRoutesListElement> listElements;
	private Context context;
	private MainCallback mCallback;

	public AllRoutesListAdapter(Activity context, int resourceId,
			List<AllRoutesListElement> listElements, MainCallback mCallback) {
		super(context, resourceId, listElements);
		this.listElements = listElements;
		this.context = context;

		// Save the interface to communication with other fragments
		// Necessary for button click event
		this.mCallback = mCallback;

		// Set up as an observer for list item changes to
		// refresh the view.
		for (int i = 0; i < listElements.size(); i++) {
			listElements.get(i).setAdapter(this);
		}
	}

	// This method is called for each list element in order to build the view
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		// View is only null when the List is created the first time
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.allrouteslist_list_item, null);

		}

		AllRoutesListElement listElement = listElements.get(position);
		if (listElement != null) {

			Button buttonDetails = (Button) view
					.findViewById(R.id.imagebuttonDetails);
			TextView textName = (TextView) view
					.findViewById(R.id.textRouteName);
			final TextView textDate = (TextView) view
					.findViewById(R.id.textRouteDate);
			// if (icon != null) {
			// icon.setImageDrawable(listElement.getIcon());
			// }
			if (textName != null) {
				textName.setText(listElement.getTextName());
			}
			if (textDate != null) {
				textDate.setText(listElement.getTextDate());
			}

			if (buttonDetails != null) {

				// Tags are used to relocate the object later on during the
				// event
				buttonDetails.setTag(position);
			}



			buttonDetails.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					// Stored position of the button
					int position = (Integer) v.getTag();

					// Getting the route object of the related row
					// Transfering it to the interface in order to call the
					// detailed map view
					
					
					//Der Mainactivity wird mitgeteilt, dass es sich bei der geöffneten Route um eine bereits 
					//abgeschlossene handelt (Active = false);
					mCallback.onRouteOpenend(false);
					mCallback.onShowRoute(listElements.get(position).getRoute());

				}

			});

		}
		return view;
	}

	public List<AllRoutesListElement> getListElements() {
		return listElements;
	}

}
