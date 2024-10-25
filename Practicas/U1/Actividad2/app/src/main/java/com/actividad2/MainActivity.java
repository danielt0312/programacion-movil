package com.actividad2;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends AppCompatActivity {

    GridView gridView;
    private EditText editText;
    private Button button;

    EditText ed1,ed2,ed3, ed4;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Phone = "phoneKey";
    public static final String Email = "emailKey";
    public static final String Lista = "listaKey";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    ArrayList<String> lista = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lista.add("Mario");
        lista.add("Dani");

        //Vistas
        editText = findViewById(R.id.editText1);
        button = findViewById(R.id.btn1);

        gridView = (GridView) findViewById(R.id.gridView1);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, numbers);
        ArrayAdapter<String> adapterLista = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, lista);

        // Acción del botón
        button.setOnClickListener(v -> {

            //Obtener nombre del EditText
            String nombre = editText.getText().toString();

            //Limpiar EditText
            editText.setText("");

            //Añadir nombre a la lista
            lista.add(nombre);

            //Actualizar adaptador
            adapterLista.notifyDataSetChanged();
        });

        gridView.setAdapter(adapterLista);

        gridView.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(getApplicationContext(),
                ((TextView) view).getText() + " Posicion : " + position, Toast.LENGTH_SHORT).show());

        // Importante: Esto va antes de instanciar controles dentro de cada pestaña

        // Agregar las pestañas---
        Resources res = getResources();
        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("PASES LISTA");

        TabHost.TabSpec spec2 = tabHost.newTabSpec("");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Grupos");

        TabHost.TabSpec spec3 = tabHost.newTabSpec("");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Alumnos");

        TabHost.TabSpec spec4 = tabHost.newTabSpec("");
        spec4.setContent(R.id.tab4);
        spec4.setIndicator("Otros");

        //spec3.setIndicator("",getResources().getDrawable(R.mipmap.ic_launcher));

        //TabHost.TabSpec spec4 = tabHost.newTabSpec("");
        //spec4.setContent(R.id.tab4);
        //spec4.setIndicator("Estadisticas");

        tabHost.addTab(spec4);
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        //tabHost.addTab(spec4);

        ed1=(EditText)findViewById(R.id.editText);
        ed2=(EditText)findViewById(R.id.editText2);
        ed3=(EditText)findViewById(R.id.editText3);
        ed4=(EditText) findViewById(R.id.editText4);

        sharedpreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        Toast.makeText(getApplicationContext(), "Ejecución de OnCreate", Toast.LENGTH_SHORT).show();
    }

    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "Ejecución de OnStart", Toast.LENGTH_SHORT).show();
    }

    // Cuando la aplicacion deja el primer plano se ejecuta..
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        editor = sharedpreferences.edit();

        String n  = ed1.getText().toString();
        String ph  = ed2.getText().toString();
        String e  = ed3.getText().toString();
        String l = ed4.getText().toString();

        editor.putString(Name, n);
        editor.putString(Phone, ph);
        editor.putString(Email, e);
        editor.putString(Lista, l);
        //editor.commit();
        editor.apply();

    }


    // Cuando la aplicación va a primer plano (despues de OnCreate) se ejecuta..
    @Override
    public void onResume () {
        super.onResume();
        sharedpreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String Cadena = sharedpreferences.getString(Name, "");
        if (!Cadena.equalsIgnoreCase("")) {
            ed1.setText(Cadena);
        }
        Cadena = sharedpreferences.getString(Phone, "");
        if (!Cadena.equalsIgnoreCase("")) {
            ed2.setText(Cadena);
        }
        Cadena = sharedpreferences.getString(Email, "");
        if (!Cadena.equalsIgnoreCase("")) {
            ed3.setText(Cadena);
        }
        Cadena = sharedpreferences.getString(Lista, "");
        if (!Cadena.equalsIgnoreCase("")) {
            ed4.setText(Cadena);
        }

        //Toast.makeText(MainActivity.this,"Cargado!!",Toast.LENGTH_LONG).show();


    }


    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "Ejecución de OnStop", Toast.LENGTH_SHORT).show();
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Ejecución de OnDestroy", Toast.LENGTH_SHORT).show();
    }
}

/*
public class MainActivity extends AppCompatActivity {

    GridView gridView;

    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, numbers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.frutas, numbers);
        // Crear el archivo XML llamado frutas.xml en la carpeta layout con el siguiente contenido

        <?xml version="1.0" encoding="utf-8"?>
        <TextView xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:padding="10dp"
        android:textSize="20sp"
            android:background="#ffbaffb8"
            android:textColor="#ffff2137"
            android:textStyle="normal|bold|italic"
            android:gravity="center_vertical|center|center_horizontal">
        </TextView>

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText() + " Posicion : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        //gridView.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(getApplicationContext(),
        //  ((TextView) view).getText() + " Posicion : " + position, Toast.LENGTH_SHORT).show());

    }

}
*/
