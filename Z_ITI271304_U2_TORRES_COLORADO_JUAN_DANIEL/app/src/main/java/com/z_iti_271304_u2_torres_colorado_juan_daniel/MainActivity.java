package com.z_iti_271304_u2_torres_colorado_juan_daniel;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/*
* Esta clase se encarga de inicializar la interfaz y conectarlo con las funcionalidades de las
* operaciones
*/

public class MainActivity extends AppCompatActivity {
    DragAndDropView ddv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ddv = findViewById(R.id.ddv);

        ddv.setMode(0);
    }
    
    public void moveDot(View v) {
        ddv.setMode(0);
    }

    public void addDot(View v) {
        ddv.setMode(1);
    }

    public void deleteDot(View v) {
        ddv.setMode(2);
    }

    public void joinDot(View v) {
        ddv.setMode(3);
    }

}