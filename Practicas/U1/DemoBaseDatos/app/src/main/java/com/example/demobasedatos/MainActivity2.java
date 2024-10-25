package com.example.demobasedatos;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity2 extends Activity {

	final String NOMBRE_BASE_DATOS = "ExperimentoTutores04.db";
	AgendaSqlite usdbh;
	SQLiteDatabase db;
	AlertDialog.Builder ADX;
	AlertDialog AD;
	Cursor cursor;

	String C1, C2, C3, C4, Fin;
	TextView TV1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {


			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main2);

			C1= "";
			C2= "";
			C3= "";
			Fin="";

			TV1 = (TextView) findViewById(R.id.TV_MARCIANO1);

			String ArchivoDB = NOMBRE_BASE_DATOS;
			// Guarda el archivo de labase de datos en el directorio RAIZ (o cualquier ruta de usuario)
			//String ArchivoDB = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+NOMBRE_BASE_DATOS;

			usdbh = new AgendaSqlite(this, ArchivoDB, null, 1);
			db = usdbh.getWritableDatabase();

			//String SELECT_QUERY = "SELECT * FROM Tutores t1 INNER JOIN Tutorados t2 ON t1._id = t2.id_tutor and t1._id = " + "1";
			//String SELECT_QUERY = "SELECT t2._id, t2.nombre_tutorado, t1._id, t1.nombre_tutor FROM Tutores t1 INNER JOIN Tutorados t2 ON t1._id = t2.id_tutor and t1._id = " + "1";
			//String SELECT_QUERY = "SELECT nombre_tutor FROM Tutores t1 INNER JOIN Tutorados t2 ON t1._id = t2.id_tutor and t1._id = " + "1";

			String SELECT_QUERY = "SELECT * FROM Tutores t1 ON t1._id = " + "1";

			//cursor = db.rawQuery("select * from TUtores", null);
			//cursor = db.rawQuery("select _id, nombre_tutor from Tutores", null);
			//cursor = db.rawQuery("select _id, nombre_tutor from Tutores where _id=1", null);
			cursor = db.rawQuery("select nombre_tutor from Tutores where _id=1", null);

			if (cursor.getCount() != 0) {
				if (cursor.moveToFirst()) {
					do {
						//C1 = cursor.getString(cursor
						//		.getColumnIndex("_id"));

						C2 = cursor.getString(cursor
								.getColumnIndex("nombre_tutor"));

						//Fin += C1 + "-" + C2 + "-" + "\n";
						Fin += C1 + "-" + "-" + "\n";

					} while (cursor.moveToNext());
				}
				//TV2.setText(Fin);
			}
			cursor.close();

			TV1.setText(Fin);



		}




}