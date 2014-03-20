package de.smbsolutions.day.functions.interfaces;

/**
 * Diese Interface wird von der Database Klasse implementiert und liefert dem Entwickler
 * auﬂerhalb der Klasse die exakten Spaltennamen (bzw. bei der Einstellungstabelle die Zeilennamen)
 * Somit muss der Aufrufer der Databasemethoden nicht die exakte Schreibweise der DB Felder wissen
 */
public interface DatabaseInterface {
	
	 String SETTINGS_TRACKING = "tracking";
	 String SETTINGS_TRACKING_INTERVAL = "tracking_interval";
	 String SETTINGS_TRACKING_METER = "tracking_meter";
	 String SETTINGS_MAP_TYPE = "map_type";
	 String SETTINGS_SHOW_IN_GAL = "show_in_gal";
	 
	 String ROUTE_POINTS_LATITUDE = "latitude";
	 String ROUTE_POINTS_LONGITUDE = "longitude";
	 String ROUTE_POINTS_PICTURE = "picture";
	 String ROUTE_POINTS_PICTURE_PREVIEW = "picture_preview";
	 String ROUTE_POINTS_TIMESTAMP = "timestamp";
	 
	 String ROUTE_INFO_NAME = "name";
	 String ROUTE_INFO_DATE = "date";
	 String ROUTE_INFO_ACTIVE = "active";

}
