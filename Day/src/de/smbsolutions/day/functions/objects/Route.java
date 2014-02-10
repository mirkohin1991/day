package de.smbsolutions.day.functions.objects;

import java.util.ArrayList;
import java.util.List;

import de.smbsolutions.day.functions.database.Database;

public class Route {

	private List<RoutePoint> routePoints = new ArrayList<RoutePoint>();
	private String routeName;
	private String date;

	private String active;
	private int id;
	
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}



	public Route() {

	}

	public Route(int id) {
		this.id = id;
		routePoints = Database.getSpecificRoute(new String[] { String
				.valueOf(id) });
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<RoutePoint> getRoutePoints() {
		return routePoints;
	}

	public void setRoutePoints(List<RoutePoint> routePoints) {
		this.routePoints = routePoints;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
		
		
	}
	
	public void closeRoute() {
		Database.closeRoute(String.valueOf(id));
		
	}
	
	
	public void addRoutePoint(RoutePoint point){
		
		routePoints.add(point);
	}
}
