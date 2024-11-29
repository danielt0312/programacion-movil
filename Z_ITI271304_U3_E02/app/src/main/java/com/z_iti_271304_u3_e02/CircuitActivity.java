package com.z_iti_271304_u3_e02;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CircuitActivity extends AppCompatActivity {

    private CircuitCanvas circuitCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit);

        circuitCanvas = findViewById(R.id.circuit_canvas);

        // Obtener la expresión simplificada de los extras del Intent
        String expression = getIntent().getStringExtra("expression");
        if (expression != null) {
            circuitCanvas.setExpression(expression);
        }
    }
}
