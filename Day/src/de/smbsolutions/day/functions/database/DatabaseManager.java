package de.smbsolutions.day.functions.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Klasse, die sich um das Erstellen der einzelnen Datenbanken kümmert Dies
 * geschieht mittels des SQLiteOpenHelper, der die Kommunikation abstrahiert und
 * einfache Methoden zur Bearbeitung liefert
 */
public class DatabaseManager extends SQLiteOpenHelper {

	private static final String DB_NAME = "routes.db";
	private static final int DB_VERSION = 1;

	// Strings, die bei Installation der App ausgeführt werden
	private static final String ROUTES_CREATE = "CREATE TABLE route_points ( _id INTEGER NOT NULL, timestamp NOT NULL, picture TEXT, picture_preview TEXT , longitude DOUBLE, latitude DOUBLE, PRIMARY KEY (_id, timestamp) ) ";
	private static final String ROUTE_INFO_CREATE = "CREATE TABLE route_info ( _id INTEGER NOT NULL, name TEXT NOT NULL, date TEXT NOT NULL, active INTEGER, PRIMARY KEY (_id) ) ";
	private static final String SETTINGS_CREATE = "CREATE TABLE settings (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, value INTEGER NOT NULL ) ";

	private static final String CLASS_DROP = "DROP TABLE IF EXISTS routes";

	public DatabaseManager(Context context) {

		super(context, DB_NAME, null, DB_VERSION);

	}

	/**
	 * Diese Methode wird aufgerufen, wenn die Datenbank noch nicht vorhanden
	 * ist
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		// Erstellen der drei Tabellen
		db.execSQL(ROUTES_CREATE); // Route_points enthält später alle Punkte zu
									// den Routen
		db.execSQL(ROUTE_INFO_CREATE); // Route_info enthält zu jeder Route
										// generelle Infos
		db.execSQL(SETTINGS_CREATE); // Die Settingstabelle speichert die
										// Einstellungen zur App

	}

	/**
	 * Diese Methode wird angestoßen wenn sich die installierte DB Version von
	 * der neuen unterscheidet. Dies ist nur der Fall wenn sich an der Struktur
	 * etwas ändert
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		// Löschen der Datenbank und erzeugen einer neuen mit der aktualisierten
		// Struktur
		db.execSQL(CLASS_DROP);
		onCreate(db);

	}

}
