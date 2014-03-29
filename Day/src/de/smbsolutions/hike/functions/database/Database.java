package de.smbsolutions.hike.functions.database;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.smbsolutions.hike.functions.interfaces.DatabaseInterface;
import de.smbsolutions.hike.functions.objects.Route;
import de.smbsolutions.hike.functions.objects.RoutePoint;

/**
 * Diese Klasse kümmert sich um alle Operationen die die Datenbank betreffen.
 * Dabei bedient sie sich des DatabaseMangers, der die Kommunikation mit der DB
 * erleichtert EIGENSCHAFTEN: Als Singleton implementiert
 */
public class Database implements DatabaseInterface {

	private static DatabaseManager dbHelper;
	private static SQLiteDatabase database;
	private static int lastRouteID = 0;
	private static Database db_object = null;

	/**
	 * Liefert die Instanz zurück (Singleton)
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
	 * Ein neuer RoutePoint wird der letzten Route hinzugefügt
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
			e.printStackTrace();
			return false;

		}

	}

	/**
	 * Löschen einer ganzen Route
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
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Diese Methode löscht den Pfad von in der App gelöscht Bildern, sodass die
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
			e.printStackTrace();
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
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Schließen einer Route in der Datenbank
	 */
	public static boolean closeRoute(int id) {

		try {

			database = dbHelper.getWritableDatabase();

			ContentValues route_value = new ContentValues();
			// Aktive Flag wird gelöscht
			route_value.put("active", 0);

			database.update("route_info", route_value, "_id=?",
					new String[] { String.valueOf(id) });
			database.close();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
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

		// Überprüfung obn ein Eintrag existiert, Sortierreihenfolge ist
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
	 * Methode, die eine eine oder mehrere Routen zurückliefert. Die Anzahl der
	 * returnten Routen hängt vom mit Count mitgelieferten Wert ab Count 0 ->
	 * Alle verfügbaren Routen Count 1-* -> Die letzten 1-* Routen
	 */
	public static List<Route> getSpecificRoute(int count) {

		Cursor db_cursor;

		database = dbHelper.getReadableDatabase();

		String condition;

		// count == 0 -> Benutzer will alle Routen
		// count > id -> Es gibt weniger Routen als der Benutzer wünscht
		// ---> In beiden Fällen werden alle selektiert
		if (count == 0 || count > lastRouteID) {
			condition = null;

		} else {
			condition = "_id > " + String.valueOf(lastRouteID - count)
					+ " AND _id <= " + String.valueOf(lastRouteID);
		}

		// Alle Routpunkte werden abgerufen
		db_cursor = database.query("route_points", null, condition, null, null,
				null, null);

		// neue Route wird angelegt
		Route route = new Route();

		// Liste die später alle Routen enthält
		List<Route> route_list = new ArrayList<Route>();

		// ID wird immer gespeichert um Record mit Vorgänger vergleichen zu
		// können
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

				// Und es zusätzlich nicht die erste Zeile des cursors ist
				if (cursor_id != previous_id && previous_id != -1) {
					// Die ganze Route wird zur Liste hinzugefügt
					route_list.add(route);
					// Neue Route für nächsten Durchlauf wird angelegt
					route = new Route();
				}

				// Weil es sich um eine neue Route handelt muss die ID gesetzt
				// werden
				route.setId(cursor_id);
				// Und zusätzlich die Infos in die Route_info Tabelle
				// geschrieben werden
				getRouteInfo(route);
			}

			// Die Route_id wir jetzt für den nächsten Durchlauf gespeichert
			previous_id = cursor_id;

			// Nachdem alle Infos beschafft wurden, kann der Punkt zur Route
			// hinzugefügt werden
			RoutePoint route_point = new RoutePoint(cursor_id, cursor_time,
					cursor_picture, cursor_picture_preview, cursor_latitude,
					cursor_longitude, cursor_latitude);
			route.addRoutePoint(route_point);

			// Wenn es sich um den letzten Eintrag des result_sets handelt, muss
			// die Route direkt hinzugefügt werden, weil es keinen nächsten Loop
			// geben wird
			if (db_cursor.isLast() == true) {
				route_list.add(route);
			}
		}

		db_cursor.close();
		database.close();

		// Zurückgeben der gewünschten Liste von Routen
		return route_list;

	}

	/**
	 * Diese Methode beschafft alle generellen Informationen die zu einer Route
	 * vorhanden sind und fügt diese dem Routeobjekt hinzu (Name,Datum und ob
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

			// Weil SQLite kein Boolean kennt findet hier immer eine Übersetzung
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
	 * Zu einer Einstellung wird der entsprechende Wert zurückgeliefert
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
	 * geändert
	 */
	public static void changeSettingValue(String setting, int value) {

		database = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("value", value);
		database.update("settings", values, "name=?", new String[] { setting });

		database.close();

	}

	/**
	 * Methode zum überprüfen, ob die Datenbank schon Einstellungswerte enthält
	 */
	private static boolean hasSettingValues() {

		Cursor db_cursor;

		database = dbHelper.getReadableDatabase();

		db_cursor = database.query("settings", null, "name = ?",
				new String[] { "tracking" },

				null, null, null);

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
	 * Methode zum Erstellen von drei Default-Routen, um den Benutzer schon bei
	 * Installation etwas präsentieren zu können
	 */
	public static void createDefaultRoutes() {
		try {

			database = dbHelper.getWritableDatabase();

			Route route = new Route("Fahrt zur DHBW");
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.6332747,
					7.6926668, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.633599,
					7.692146, 9000000));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.633177,
					7.691090, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.633105,
					7.690457, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.632331,
					7.688745, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.630796,
					7.685518, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.630278,
					7.684126, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.629202,
					7.682477, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.626422,
					7.678744, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.625137,
					7.677095, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.622821,
					7.675461, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.620247,
					7.672092, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.619112,
					7.670965, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.618536,
					7.672187, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.620564,
					7.674979, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.619380,
					7.678016, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.618049,
					7.677464, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.617653,
					7.677069, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.617503,
					7.677420, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.616639,
					7.677853, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.616542,
					7.678025, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.616529,
					7.678326, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.616692,
					7.678517, 5454));
			route.addRoutePointDB(new RoutePoint(route.getId(), new Timestamp(
					System.currentTimeMillis() + 300000), null, null, 47.618099,
					7.678954, 5454));
			closeRoute(route.getId());

			Route route2 = new Route("Spaziergang zum HBF");
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.999512,
					7.851523, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.999615,
					7.850700, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.998497,
					7.850233, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.998721,
					7.848568, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.998972,
					7.846750, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 48.000239,
					7.847259, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 48.000447,
					7.846110, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 48.001477,
					7.846523, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 48.001778,
					7.845757, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 48.000770,
					7.844898, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.999626,
					7.843928, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.998511,
					7.843115, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.996864,
					7.841802, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.996630,
					7.841659, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.996477,
					7.841597, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.996387,
					7.841742, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.996410,
					7.841904, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis()), null, null, 47.996692,
					7.842099, 5454));
			route.addRoutePointDB(new RoutePoint(route2.getId(), new Timestamp(
					System.currentTimeMillis() + 1500000), null, null,
					47.997373, 7.842642, 5454));
			closeRoute(route2.getId());
			database.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
