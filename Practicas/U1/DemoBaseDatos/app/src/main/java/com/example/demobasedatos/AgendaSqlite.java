package com.example.demobasedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AgendaSqlite extends SQLiteOpenHelper {
	
	 //
    //private static String DB_PATH = "/data/data/YOUR_PACKAGE/databases/"; 
//	private static String DB_PATH = "/sdcard/";
//    private static String DB_NAME = "Experimento01";	

	//Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate   = "CREATE TABLE Tutores       (_id INTEGER PRIMARY KEY, nombre_tutor TEXT)";
    String sqlCreate2  = "CREATE TABLE Tutorados     (_id INTEGER PRIMARY KEY, nombre_tutorado TEXT, id_tutor REFERENCES Tutores(_id))";
	
	public AgendaSqlite(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 //Se ejecuta la sentencia SQL de creaci�n de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        // Enable foreign key constraints
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//eliminamos la version anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Tutores"); 
        db.execSQL("DROP TABLE IF EXISTS Tutorados"); 

        //aqu� creamos la nueva versi�n de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
        
	}

}