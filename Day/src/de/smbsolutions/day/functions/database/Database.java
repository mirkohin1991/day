package de.smbsolutions.day.functions.database;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.smbsolutions.day.functions.interfaces.DatabaseInterface;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

/**
 * Diese Klasse k�mmert sich um alle Operationen die die Datenbank betreffen.
 * Dabei bedient sie sich des DatabaseMangers, der die Kommunikation mit der DB
 * erleichtert EIGENSCHAFTEN: Als Singleton implementiert
 */
public class Database implements DatabaseInterface {

	private static DatabaseManager dbHelper;
	private static SQLiteDatabase database;
	private static int lastRouteID = 0;
	private static Database db_object = null;

	/**
	 * Liefert die Instanz zur�ck (Singleton)
	 */
	public static Database getInstance(Context context) {
		if (db_object == null)
			db_object = new Database(context);
		return db_object;
	}

	/**
	 * Privater Konstruktor -> nur ein Singleton kann erzeugt werden
	 */
	private Database(Context context) {

		dbHelper = new DatabaseManager(context);

		// Wenn die Settings noch nicht in der Tabelle vorhanden sind, werden
		// die Standardsettings eingelesen
		if (hasSettingValues() == false) {
			Database.createDefaultSettings();
		}

		// Aktuelle routeID wird gespeichert
		lastRouteID = selectIDlastRoute();

		// Wenn noch keine Routen vorhanden sind, werden Default-Routen erstellt
		if (lastRouteID == 0) {
			Database.createDefaultRoutes();
		}
	}

	/**
	 * Ein neuer RoutePoint wird der letzten Route hinzugef�gt
	 */
	public static boolean addNewRoutePoint(RoutePoint point) {

		try {

			database = dbHelper.getWritableDatabase();

			ContentValues route_values = new ContentValues();

			route_values.put("_id", lastRouteID); // Immer letzte Route
			route_values.put("timestamp", point.getTimestamp().toString());
			route_values.put("latitude", point.getLatitude());
			route_values.put("picture", point.getPicture());
			route_values.put("picture_preview", point.getPicturePreview());
			route_values.put("longitude", point.getLongitude());
			route_values.put("latitude", point.getLatitude());
			database.insert("route_points", null, route_values);
			database.close();

			return true;
		} catch (Exception e) {

			return false;

		}

	}

	/**
	 * L�schen einer ganzen Route
	 */
	public static boolean deleteRoute(Route route) {

		try {
			database = dbHelper.getWritableDatabase();
			database.delete("route_points", "_id=?",
					new String[] { String.valueOf(route.getId()) });
			database.delete("route_info", "_id=?",
					new String[] { String.valueOf(route.getId()) });
			database.close();
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	/**
	 * Diese Methode l�scht den Pfad von in der App gel�scht Bildern, sodass die
	 * App nichtmehr versucht diese anzuzeigen
	 */
	public static boolean deletePicturePath(RoutePoint routePoint) {

		try {

			database = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.putNull(Database.ROUTE_POINTS_PICTURE);
			values.putNull(Database.ROUTE_POINTS_PICTURE_PREVIEW);
			database.update("route_points", values, "timestamp=?",
					new String[] { String.valueOf(routePoint.getTimestamp()) });
			database.close();
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Wird eine komplett neue Route erstellt muss in der Route_info Tabelle die
	 * generellen Daten, wie der Name, gespeichert werden
	 */
	public static boolean createNewRoute(Route route) {

		try {
			database = dbHelper.getWritableDatabase();
			ContentValues route_info = new ContentValues();
			route_info.put("_id", String.valueOf(route.getId()));
			route_info.put("name", route.getRouteName());
			route_info.put("date", route.getDate());
			route_info.put("active", "1"); // Flag, dass Route aktiv ist
			database.insert("route_info", null, route_info);

			// Speichern der neuen ID als letzte Route
			lastRouteID = route.getId();
			database.close();

			return true;

		} catch (Exception e) {

			return false;
		}

	}

	/**
	 * Schlie�en einer Route in der Datenbank
	 */
	public static boolean closeRoute(int id) {

		try {

			database = dbHelper.getWritableDatabase();

			ContentValues route_value = new ContentValues();
			// Aktive Flag wird gel�scht
			route_value.put("active", 0);

			database.update("route_info", route_value, "_id=?",
					new String[] { String.valueOf(id) });
			database.close();

			return true;

		} catch (Exception e) {

			return false;
		}

	}

	/**
	 * Methode zur Bestimmung der letzen gespeicherten Route
	 */
	private static int selectIDlastRoute() {

		Cursor db_cursor;
		String[] db_columns = { "_ID" };
		int lastRecord;

		database = dbHelper.getWritableDatabase();

		// �berpr�fung obn ein Eintrag existiert, Sortierreihenfolge ist
		// absteigend
		db_cursor = database.query("route_points", // table
				db_columns, null, null, null, null, "_id DESC");

		// Wenn der Count nicht 0 ist, wird die erste ID (des sortierten
		// Records) als letzte ID gespeichert
		if (db_cursor.getCount() != 0) {
			db_cursor.moveToFirst();
			lastRecord = db_cursor.getInt(db_cursor.getColumnIndex("_id"));

			// Wenn es keinen Eintrag gibt, gibt es auch noch keine Route
		} else {
			lastRecord = 0;
		}

		db_cursor.close();
		database.close();
		return lastRecord;
	}

	/**
	 * Methode, die eine eine oder mehrere Routen zur�ckliefert. Die Anzahl der
	 * returnten Routen h�ngt vom mit Count mitgelieferten Wert ab Count 0 ->
	 * Alle verf�gbaren Routen Count 1-* -> Die letzten 1-* Routen
	 */
	public static List<Route> getSpecificRoute(int count) {

		Cursor db_cursor;

		database = dbHelper.getReadableDatabase();

		String condition;

		// count == 0 -> Benutzer will alle Routen
		// count > id -> Es gibt weniger Routen als der Benutzer w�nscht
		// ---> In beiden F�llen werden alle selektiert
		if (count == 0 || count > lastRouteID) {
			condition = null;

		} else {
			condition = "_id > " + String.valueOf(lastRouteID - count)
					+ " AND _id <= " + String.valueOf(lastRouteID);
		}

		// Alle Routpunkte werden abgerufen
		db_cursor = database.query("route_points", null, condition, null,
				null, null, null);

		// neue Route wird angelegt
		Route route = new Route();

		// Liste die sp�ter alle Routen enth�lt
		List<Route> route_list = new ArrayList<Route>();

		// ID wird immer gespeichert um Record mit Vorg�nger vergleichen zu
		// k�nnen
		// Initialert -1 signalisiert, dass es der erste Eintrag ist
		int previous_id = -1;
		while (db_cursor.moveToNext()) {

			// Die entsprechenden Routepunktefelder werden abgerufen
			int cursor_id = db_cursor.getInt(db_cursor.getColumnIndex("_id"));
			String cursor_picture = db_cursor.getString(db_cursor
					.getColumnIndex("picture"));
			String cursor_picture_preview = db_cursor.getString(db_cursor
					.getColumnIndex("picture_preview"));
			double cursor_longitude = db_cursor.getDouble(db_cursor
					.getColumnIndex("longitude"));
			double cursor_latitude = db_cursor.getDouble(db_cursor
					.getColumnIndex("latitude"));
			Timestamp cursor_time = Timestamp.valueOf(db_cursor
					.getString(db_cursor.getColumnIndex("timestamp")));

			// Wenn es sich um eine neue Route handelt
			if (cursor_id != previous_id) {

				// Und es zus�tzlich nicht die erste Zeile des cursors ist
				if (cursor_id != previous_id && previous_id != -1) {
					// Die ganze Route wird zur Liste hinzugef�gt
					route_list.add(route);
					// Neue Route f�r n�chsten Durchlauf wird angelegt
					route = new Route();
				}

				// Weil es sich um eine neue Route handelt muss die ID gesetzt
				// werden
				route.setId(cursor_id);
				// Und zus�tzlich die Infos in die Route_info Tabelle
				// geschrieben werden
				getRouteInfo(route);
			}

			// Die Route_id wir jetzt f�r den n�chsten Durchlauf gespeichert
			previous_id = cursor_id;

			// Nachdem alle Infos beschafft wurden, kann der Punkt zur Route
			// hinzugef�gt werden
			RoutePoint route_point = new RoutePoint(cursor_id, cursor_time,
					cursor_picture, cursor_picture_preview, cursor_latitude,
					cursor_longitude, cursor_latitude);
			route.addRoutePoint(route_point);

			// Wenn es sich um den letzten Eintrag des result_sets handelt, muss
			// die Route direkt hinzugef�gt werden, weil es keinen n�chsten Loop
			// geben wird
			if (db_cursor.isLast() == true) {
				route_list.add(route);
			}
		}

		db_cursor.close();
		database.close();

		// Zur�ckgeben der gew�nschten Liste von Routen
		return route_list;

	}

	/**
	 * Diese Methode beschafft alle generellen Informationen die zu einer Route
	 * vorhanden sind und f�gt diese dem Routeobjekt hinzu (Name,Datum und ob
	 * aktiv)
	 */
	public static void getRouteInfo(Route route) {

		Cursor db_cursor;

		database = dbHelper.getReadableDatabase();

		db_cursor = database.query("route_info", null,
				"_id = " + String.valueOf(route.getId()), null, null, null,
				null);

		while (db_cursor.moveToNext()) {

			// Die entsprechenden Datensatzfelder werden ausgelesen
			route.setRouteName(db_cursor.getString(db_cursor
					.getColumnIndex("name")));
			route.setDate(db_cursor.getString(db_cursor.getColumnIndex("date")));

			// Weil SQLite kein Boolean kennt findet hier immer eine �bersetzung
			// statt
			switch (db_cursor.getInt(db_cursor.getColumnIndex("active"))) {
			case 1:
				route.setActive(true);
				break;

			case 0:
				route.setActive(false);
				break;

			default:
				break;
			}

		}

		db_cursor.close();
		database.close();

	}

	/**
	 * Methode zur Bestimmung der letzten Route
	 */
	public static int getlastRouteID() {
		return lastRouteID;
	}

	/**
	 * Zu einer Einstellung wird der entsprechende Wert zur�ckgeliefert
	 */
	public static int getSettingValue(String name) {

		int value;

		Cursor db_cursor;

		database = dbHelper.getReadableDatabase();

		db_cursor = database.query("settings", null, "name = ?",
				new String[] { name }, // Welche Einstellung?
				null, null, null);

		db_cursor.moveToFirst();

		// Auslesen des Wertes
		value = db_cursor.getInt(db_cursor.getColumnIndex("value"));

		db_cursor.close();
		database.close();

		return value;

	}

	/**
	 * Zu einer Einstellung wird der entsprechende Wert in der Datenbank
	 * ge�ndert
	 */
	public static void changeSettingValue(String setting, int value) {

		database = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("value", value);
		database
				.update("settings", values, "name=?", new String[] { setting });

		database.close();

	}

	
	/**
	 * Methode zum �berpr�fen, ob die Datenbank schon Einstellungswerte enth�lt
	 */
    private static boolean hasSettingValues() {

		Cursor db_cursor;

		database = dbHelper.getReadableDatabase();

		db_cursor = database.query("settings", 
				null, 
				"name = ?", 
				new String[] { "tracking" }, 
												
				null, 
				null,
				null);

		db_cursor.moveToFirst();

		if (db_cursor.getCount() == 0) {

			db_cursor.close();
			database.close();

			return false;

		} else {
			db_cursor.close();
			database.close();

			return true;
		}

	}

    /**
     * Methode zur Erstellung der voreingestellten Einstellungen
     */
	public static void createDefaultSettings() {


		database = dbHelper.getWritableDatabase();

		ContentValues setting_values = new ContentValues();
	
		setting_values.put("name", "tracking");
		setting_values.put("value", 1);
		database.insert("settings", null, setting_values);
		setting_values.clear();

		setting_values.put("name", "tracking_interval");
		setting_values.put("value", 10000);
		database.insert("settings", null, setting_values);
		setting_values.clear();

		setting_values.put("name", "tracking_meter");
		setting_values.put("value", 5);
		database.insert("settings", null, setting_values);
		setting_values.clear();

		setting_values.put("name", "map_type");
		setting_values.put("value", 2);
		database.insert("settings", null, setting_values);
		setting_values.clear();

		setting_values.put("name", "show_in_gal");
		setting_values.put("value", 1);
		database.insert("settings", null, setting_values);

		database.close();
	}

	/**
	 * Methode zum Erstellen von drei Default-Routen, um den Benutzer schon bei Installation etwas pr�sentieren zu k�nnen
	 */
	public static void createDefaultRoutes() {
		try {

			database = dbHelper.getWritableDatabase();

			for (int i = 0; i <= 2; i++) {
				
				Route route = new Route("Beispielroute" + String.valueOf(i));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.9983322, 7.8018200, 5454));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.91913344, 7.8218310, 5454));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.9313355, 7.8248580, 5454));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.9513366, 7.8618999, 5454));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.9983322, 7.8018200, 5454));
				if (i < 2) {

					closeRoute(route.getId());
				}
				database.close();

			}

		} catch (Exception e) {
		

		}
	}

}
