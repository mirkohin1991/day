package de.smbsolutions.hike.presentation.listviews;

import android.graphics.drawable.Drawable;
import android.widget.BaseAdapter;
import de.smbsolutions.hike.functions.objects.Route;

/**
 * Repr‰sentation eines Elementes der Liste aller Routen auf dem Mainfragment
 */
public class AllRoutesListElement {

	private Drawable icon;
	private String textName;
	private String textDate;
	private Route route;
	private BaseAdapter adapter;

	public AllRoutesListElement(Route route) {
		super();

		// Die Route wird gespeichert, damit von auﬂerhalb auf sie zugegriffen
		// werden kann
		// (Z.B. bei einem onLongClick Event in die eigentliche Liste
		this.route = route;
		textName = route.getRouteName();
		textDate = route.getDate();

	}

	// registrieren des Adapters
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
