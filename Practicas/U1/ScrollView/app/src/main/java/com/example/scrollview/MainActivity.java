package com.example.scrollview;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView LV1, LV2, LV3, LV4;
    ScrollView SV;
    ArrayAdapter<String> adapter;
    List<String> lista;
    TextView TV1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener metricas de la pantalla (ancho, alto)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        lista = new ArrayList<String>();
        for (int i=0; i<200; i++) {
            lista.add("Elemento"+i);
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, lista);

        TV1 = findViewById(R.id.textview1);
        SV = findViewById(R.id.scroll);
        LV1 = findViewById(R.id.listview1);
        LV2 = findViewById(R.id.listview2);
        LV3 = findViewById(R.id.listview3);
        LV4 = findViewById(R.id.listview4);

        // Establecer propiedades de un contro
        LinearLayout.LayoutParams param;
        param = new LinearLayout.LayoutParams(
                /*width*/ ViewGroup.LayoutParams.MATCH_PARENT,
                /*height*/ ViewGroup.LayoutParams.WRAP_CONTENT,
                /*weight*/ 1.0f
        );
        //Modificar la altura de un ListView a 1/3 de la altura de la pantalla
        param.height=height/3;
        LV1.setLayoutParams(param);
        LV2.setLayoutParams(param);
        LV3.setLayoutParams(param);
        LV4.setLayoutParams(param);


        LV1.setAdapter(adapter);
        LV2.setAdapter(adapter);
        LV3.setAdapter(adapter);
        LV4.setAdapter(adapter);

        TV1.setText("Alto: "+ height+ "Ancho"+ width);

        //Esto es requerido para evitar el "Choque" de los dos scrolls (el de la
        // actividad y el del ListView)
        LV1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SV.requestDisallowInterceptTouchEvent(true);
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        SV.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        LV2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SV.requestDisallowInterceptTouchEvent(true);
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        SV.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        LV3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SV.requestDisallowInterceptTouchEvent(true);
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        SV.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        LV4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SV.requestDisallowInterceptTouchEvent(true);
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        SV.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });



    }
}