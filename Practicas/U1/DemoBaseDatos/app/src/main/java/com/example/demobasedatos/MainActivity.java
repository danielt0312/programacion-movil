package com.example.demobasedatos;


//import com.example.actividadsql_lite_v4.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button BT1, BT2, BT3, BT4, BT5, BT6, BT7;
	AlertDialog.Builder ADX; 
	AlertDialog AD;
	TextView TV2;
	EditText ET1;
	EditText ET2;
    EditText ET3;
	//final String NOMBRE_BASE_DATOS = "ExperimentoTutores03.db";
	final String NOMBRE_BASE_DATOS = "ExperimentoTutores04.db";
	
	final String TABLA_PRINCIPAL = "Tutores";
	final String TABLA_SECUNDARIA= "Tutorados";
	
	AgendaSqlite usdbh;
	SQLiteDatabase db;
	Cursor cursor;
	int SiguienteID;
	String[] argumentos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {


			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			
		
			
			SiguienteID=0;
			
			BT1 = (Button) findViewById (R.id.BT01);
			BT2 = (Button) findViewById (R.id.BT02);
			BT3 = (Button) findViewById (R.id.BT03);
			BT4 = (Button) findViewById (R.id.BT04);
			BT5 = (Button) findViewById (R.id.BT05);
			BT6 = (Button) findViewById (R.id.BT06);
			BT7 = (Button) findViewById (R.id.BT07);
			
			TV2 = (TextView) findViewById(R.id.TV2);
			ET1= (EditText) findViewById(R.id.ET1);
			ET2= (EditText) findViewById(R.id.ET2);
            ET3= (EditText) findViewById(R.id.ET3);

           // ET1.setEnabled(false);
           // ET3.setEnabled(false);

			argumentos = new String[1];
			
			ADX = new AlertDialog.Builder(this);
			AD = ADX.create();

			
// Opciones para almacenar la base de datos SQLLITE -> ----- 
			// Guardar el archivo en la carpeta de almacenamiento "oculto" de la aplicación"
			String ArchivoDB = NOMBRE_BASE_DATOS;
			// Guarda el archivo de labase de datos en el directorio RAIZ (o cualquier ruta de usuario)
			//String ArchivoDB = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+NOMBRE_BASE_DATOS;
			
	        usdbh = new AgendaSqlite(this, ArchivoDB, null, 1);        
	        db = usdbh.getWritableDatabase();
	        
					
			BT1.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub				
					//Si hemos abierto correctamente la base de datos
			        if(db != null)
			        {
			                //Generamos los datos
			                int codigo = SiguienteID;
			                ContentValues values = new ContentValues();
			                values.put("nombre_tutor",ET2.getText().toString());			                
			                db.insert(TABLA_PRINCIPAL,null,values);			                
			                ConsultaTabla_ActualizaControl ();
							AD.setMessage("Insertando un tutor elemento");
							AD.show();
			               
			        }			        
			        	
				}			
			});

			// Borrar - Pero tener cuidado
			BT2.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// Primero efectuar una consulta a tutorados para ver si hay tutorados
					// asignados al tutor que se desee borrar
					String SELECT_QUERY = "SELECT * FROM Tutores t1 INNER JOIN Tutorados t2 ON t1._id = t2.id_tutor and t1._id = " + ET1.getText().toString().trim();
					cursor = db.rawQuery(SELECT_QUERY, null);					
			        if (cursor.getCount() != 0) {
						//db.delete(TABLA_SECUNDARIA, "_id="+ET1.getText().toString(), null);
						//db.delete(TABLA_PRINCIPAL, "_id="+ET1.getText().toString(), null);
			        	
			        	//db.execSQL("DELETE FROM Tutorados ");
			        	String[] args = new String[1]; //{""};
			        	args[0]= ET1.getText().toString();
			        	db.execSQL("DELETE FROM Tutorados WHERE id_tutor=?", args);
			        	db.execSQL("DELETE FROM Tutores WHERE _id=?", args);
						
			        	AD.setMessage("El tutor tiene tutorados asignados, se borraran sus tutorados primero y despues el tutor!");
						AD.show();
						
						// Volver a consultar para ver los cambios
						ConsultaTabla_ActualizaControl ();			        	

					}
			        else
			        {
						// TODO Auto-generated method stub
						//db.delete(TABLA_PRINCIPAL, "_id="+ET1.getText().toString(), null);
			        	String[] args = new String[1]; //{""};
			        	args[0]= ET1.getText().toString();
						db.execSQL("DELETE FROM Tutores WHERE _id=?", args);
						AD.setMessage("Borrando id: "+ET1.getText().toString());
						AD.show();
						// Volver a consultar para ver los cambios
						ConsultaTabla_ActualizaControl ();			        	
			        }
			        cursor.close();
				}			
			});
			
			BT3.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					ConsultaTabla_ActualizaControl ();
				}			
			});

			// ACTUALIZAR
			BT4.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					//(codigo, nombre,apellido,telefono)
					// TODO Auto-generated method stub
					ContentValues contentValues = new ContentValues();
					contentValues.put("nombre_tutor", ET2.getText().toString());									
					// Hace una actualización de los que tengan codigo=1
					db.update(TABLA_PRINCIPAL, contentValues, "_id="+ET1.getText().toString().trim(), null);
					//db.update(TABLA_PRINCIPAL, contentValues, "_id=1", null);
					
					ConsultaTabla_ActualizaControl ();
				}			
			});

			// INSERTAR UN TUTORADO A UN TUTOR
			BT5.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					//(codigo, nombre,apellido,telefono)
					// TODO Auto-generated method stub
					ContentValues contentValues = new ContentValues();
					contentValues.put("nombre_tutorado", ET3.getText().toString());
					contentValues.put("id_tutor", ET1.getText().toString().trim());
					// Hace una actualización de los que tengan codigo=1
					//db.update(TABLA_SECUNDARIA, contentValues, "id="+ET1.getText(), null);
					db.insert(TABLA_SECUNDARIA,null,contentValues);
					
					AD.setMessage("AGregando un tutorado al tutor: "+ET1.getText().toString().trim());
					AD.show();
					
					
					ConsultaTabla_ActualizaControl ();
				}			
			});

			BT6.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					String C1, C2, C3, C4;
					String Fin="";


					if (ET1.getText().toString().equals(""))
					{
						Fin="Ningun Resultado";
					}
					else
					{					
						String SELECT_QUERY = "SELECT * FROM Tutores t1 INNER JOIN Tutorados t2 ON t1._id = t2.id_tutor and t1._id = " + ET1.getText().toString().trim();
						cursor = db.rawQuery(SELECT_QUERY, null);
						
				        if (cursor.getCount() != 0) {
							if (cursor.moveToFirst()) {
								do {
									C1 = cursor.getString(cursor
											.getColumnIndex("_id"));
	
									C2 = cursor.getString(cursor
											.getColumnIndex("nombre_tutorado"));
	
									C3 = cursor.getString(cursor
											.getColumnIndex("id_tutor"));
									
									C4 = cursor.getString(cursor
											.getColumnIndex("nombre_tutor"));
									Fin += C1 + "-" + C2 + "-" + C3 + "-"+ C4 + "\n";
	
								} while (cursor.moveToNext());
							}						
						}
				        cursor.close();
					}
					TV2.setText(Fin);
					
				}			
			});

			BT7.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent I = new Intent (MainActivity.this, MainActivity2.class);
					startActivityForResult(I,0);

				}
			});
			
		}
		
		void ConsultaTabla_ActualizaControl ()
		{
			String C1, C2, C3;
			String Fin="";
			cursor = db.rawQuery("select * from "+TABLA_PRINCIPAL, null);
			
	        if (cursor.getCount() != 0) {
				if (cursor.moveToFirst()) {
					do {
						C1 = cursor.getString(cursor
								.getColumnIndex("_id"));

						C2 = cursor.getString(cursor
								.getColumnIndex("nombre_tutor"));

						Fin += C1 + "-" + C2 + "-" + "\n";

					} while (cursor.moveToNext());
				}
				TV2.setText(Fin);
			}
			cursor.close();			
	}

	
    
}