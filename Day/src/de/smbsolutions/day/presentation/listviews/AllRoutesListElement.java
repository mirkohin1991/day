package de.smbsolutions.day.presentation.listviews;


import de.smbsolutions.day.functions.objects.Route;
import android.graphics.drawable.Drawable;
import android.widget.BaseAdapter;


public class AllRoutesListElement {
	
	private Drawable icon;
	private String textName;
	private String textDate;
	private Route route;
	private BaseAdapter adapter;
	
	
	public AllRoutesListElement( Route route) {
		super();
		this.route = route;
		
		textName = route.getRouteName();
		textDate = route.getDate();

	}
	

	
	//observers can call to register for data change notifications:
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}




	public String getTextName() {
		return textName;
	}
	


	public String getTextDate() {
		return textDate;
	}
	
	
	
	public Drawable getIcon() {
		return icon;
	}



	public Route getRoute() {
		return route;
	}
	
	

}
