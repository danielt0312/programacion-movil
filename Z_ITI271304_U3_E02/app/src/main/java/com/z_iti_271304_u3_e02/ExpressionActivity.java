package com.z_iti_271304_u3_e02;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExpressionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expression);

        ImageButton btnBack = findViewById(R.id.btn_back_expression);
        btnBack.setOnClickListener(v -> finish());

        String expressionDetails = getIntent().getStringExtra("expression");
        TextView expressionText = findViewById(R.id.expression_text);

        if (expressionDetails != null) {
            String formattedText = formatText(expressionDetails);
            expressionText.setText(formattedText);
        } else {
            expressionText.setText("Error: No se pudo calcular la expresión.");
        }

        Button btnDrawCircuit = findViewById(R.id.btn_draw_circuit);
        btnDrawCircuit.setOnClickListener(v -> {
            Intent circuitIntent = new Intent(ExpressionActivity.this, CircuitActivity.class);
            circuitIntent.putExtra("expression", expressionDetails);
            startActivity(circuitIntent);
        });
    }

    private String formatText(String expressionDetails) {
        StringBuilder formatted = new StringBuilder();
        String[] lines = expressionDetails.split("\n");

        for (String line : lines) {
            if (line.startsWith("Proceso de Simplificación:")) {
                formatted.append("=== Proceso de Simplificación ===\n");
            } else if (line.startsWith("Expresión Final Simplificada:")) {
                formatted.append("\n=== Expresión Final Simplificada ===\n");
            } else {
                formatted.append(line).append("\n");
            }
        }

        return formatted.toString();
    }
}
