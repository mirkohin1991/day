package de.smbsolutions.hike.presentation.listviews;

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
import de.smbsolutions.hike.functions.interfaces.MainCallback;

/**
 * Adapter für die Liste aller vorhandenen Routen auf dem MainFragment
 */
public class AllRoutesListAdapter extends ArrayAdapter<AllRoutesListElement> {

	private List<AllRoutesListElement> listElements;
	private Context context;
	private MainCallback mainCallback;

	
	
	/**
	 * Konstruktor
	 * @param context
	 * @param resourceId
	 * @param listElements
	 * @param mCallback
	 */
	public AllRoutesListAdapter(Activity context, int resourceId,
			List<AllRoutesListElement> listElements, MainCallback mCallback) {
		
		super(context, resourceId, listElements);
		this.listElements = listElements;
		this.context = context;

		
		//Interface zur Kommunikation mit MainActivity, nötig für Button Click event
		this.mainCallback = mCallback;

		//allen Elementen wird der Adapter hinzugefügt
		for (int i = 0; i < listElements.size(); i++) {
			listElements.get(i).setAdapter(this);
		}
	}

	
	/**
	 * Diese Method wird für jedes Listelement aufgerufen
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		// View ist null wenn die Liste zum ersten Mal aufgerufen wird
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.allrouteslist_list_item, null);

		}

		//Abgreifen des jeweiligen Elements
		AllRoutesListElement listElement = listElements.get(position);
		if (listElement != null) {

			//Einlesen der Listelemente
			Button buttonDetails = (Button) view
					.findViewById(R.id.imagebuttonDetails);
			TextView textName = (TextView) view
					.findViewById(R.id.textRouteName);
			TextView textDate = (TextView) view
					.findViewById(R.id.textRouteDate);
		
			
			
			if (textName != null) {
				textName.setText(listElement.getTextName());
			}
			if (textDate != null) {
				textDate.setText(listElement.getTextDate());
			}

			if (buttonDetails != null) {
				//Setzen eines Tags, damit im Onclick event die Position bestimmt werden kann
				buttonDetails.setTag(position);
			}



			buttonDetails.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					//Postion des geklickten Buttons
					int position = (Integer) v.getTag();


					//Der Mainactivity wird mitgeteilt, dass es sich bei der geöffneten Route um eine bereits 
					//abgeschlossene handelt (Active = false);
					mainCallback.onRouteOpenend(false);
					
					//Die geklickte Route wird angezeigt
					mainCallback.onShowRoute(listElements.get(position).getRoute());

				}

			});

		}
		return view;
	}

	

}
