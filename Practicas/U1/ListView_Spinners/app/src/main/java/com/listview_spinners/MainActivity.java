package com.listview_spinners;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView LV1;
    Button B1;
    static final String[] Frutas = new String[] { "Manzana", "Mango", "Durazno", "Platano", "Fresa",
            "Uva", "Sandia","Melon","Ranbutan","Zapote","Maracuya"};
    ArrayAdapter<String> adapter, adapter_month;
    ArrayAdapter <Integer> adapter_day, adapter_year;
    List<String> lista, month;
    List<Integer> day, year;
    Spinner SP1;
    Spinner SP_day, SP_month, SP_year;
    EditText ET1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lista = new ArrayList<String>();
        day = new ArrayList<>();
        month = new ArrayList<>();
        year = new ArrayList<>();

        B1 = findViewById(R.id.button);
        LV1 = findViewById(R.id.listView);
        ET1 = findViewById(R.id.editText1);
        SP1 = findViewById(R.id.spinner1);

        SP_day = findViewById(R.id.sp_day);
        SP_month = findViewById(R.id.sp_month);
        SP_year = findViewById(R.id.sp_year);

        lista = new ArrayList<String>();
        lista.add("Programacion");
        lista.add("Matematicas");
        lista.add("Calculo");
        lista.add("Algebra");
        lista.add("Ing. Software");

        month.add("Ene");
        month.add("Feb");
        month.add("Mar");
        month.add("Abr");
        month.add("May");
        month.add("Jun");
        month.add("Jul");
        month.add("Ago");
        month.add("Sep");
        month.add("Oct");
        month.add("Nov");
        month.add("Dic");

        for (int i = 1900; i < 2025; i++) {
            year.add(i);
        }

        adapter_day = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, day);
        SP_day.setAdapter(adapter_day);

        adapter_month = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, month);
        SP_month.setAdapter(adapter_month);

        SP_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapter_year = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, year);
        SP_year.setAdapter(adapter_year);

// Listener para el Spinner de meses
        SP_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedYear = (int) SP_year.getSelectedItem();
                actualizarDias(i, selectedYear); // Actualizamos días dependiendo del mes y año
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Listener para el Spinner de años
        SP_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedMonth = SP_month.getSelectedItemPosition();
                int selectedYear = (int) SP_year.getSelectedItem();
                actualizarDias(selectedMonth, selectedYear); // Actualizamos días dependiendo del mes y año
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, lista);
        LV1.setAdapter(adapter);

        SP1.setAdapter(adapter);
        SP1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText()+"- Posicion: "+i , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public boolean esBisiesto(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public void actualizarDias(int month, int year) {
        int dias = diasMes(month, year);
        day.clear();
        for (int i = 1; i <= dias; i++) {
            day.add(i);
        }
        adapter_day.notifyDataSetChanged();
    }

    public int diasMes(int month, int year) {
        switch (month) {
            case 0: // Enero
            case 2: // Marzo
            case 4: // Mayo
            case 6: // Julio
            case 7: // Agosto
            case 9: // Octubre
            case 11: // Diciembre
                return 31;

            case 3: // Abril
            case 5: // Junio
            case 8: // Septiembre
            case 10: // Noviembre
                return 30;

            case 1: // Febrero
                return esBisiesto(year) ? 29 : 28;

            default:
                throw new IllegalArgumentException("Mes inválido: " + month);
        }
    }

}