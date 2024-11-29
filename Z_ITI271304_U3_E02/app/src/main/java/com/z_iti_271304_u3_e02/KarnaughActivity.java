package com.z_iti_271304_u3_e02;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KarnaughActivity extends AppCompatActivity {

    private LinearLayout karnaughContainer;
    private KarnaughCanvas karnaughCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karnaugh);

        karnaughContainer = findViewById(R.id.karnaugh_container);
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        int numVariables = intent.getIntExtra("numVariables", -1);
        String[] outputs = intent.getStringArrayExtra("outputs");

        if (outputs != null && numVariables > 0) {
            karnaughCanvas = new KarnaughCanvas(this, numVariables, outputs);
            karnaughContainer.addView(karnaughCanvas);
        } else {
            TextView errorText = new TextView(this);
            errorText.setText("Error: No se recibieron correctamente las salidas o el número de variables.");
            errorText.setGravity(Gravity.CENTER);
            karnaughContainer.addView(errorText);
        }
        String initialExpression = calculateExpression(numVariables, outputs);

        Button btnViewExpression = findViewById(R.id.btn_view_expression);
        btnViewExpression.setOnClickListener(v -> {
            Intent expressionIntent = new Intent(KarnaughActivity.this, ExpressionActivity.class);
            expressionIntent.putExtra("expression", initialExpression);
            startActivity(expressionIntent);
        });

        Button btn_view_3d_k_map = findViewById(R.id.btn_view_3d_k_map);
        btn_view_3d_k_map.setOnClickListener(v -> {
            Intent expressionIntent = new Intent(KarnaughActivity.this, TorusActivity.class);
            expressionIntent.putExtra("expression", initialExpression);
            startActivity(expressionIntent);
        });
    }

    private String calculateExpression(int numVariables, String[] outputs) {
        StringBuilder expressionBuilder = new StringBuilder();
        boolean[][] visited = new boolean[karnaughCanvas.getRows()][karnaughCanvas.getCols()];

        for (int i = 0; i < karnaughCanvas.getRows(); i++) {
            for (int j = 0; j < karnaughCanvas.getCols(); j++) {
                int mintermIndex = karnaughCanvas.getIndex(i, j);

                if ((outputs[mintermIndex].equals("1") || outputs[mintermIndex].equals("X")) && !visited[i][j]) {
                    for (int groupSize = 4; groupSize >= 1; groupSize /= 2) {
                        List<int[]> groupCells = karnaughCanvas.findGroupOfSize(i, j, groupSize, visited);
                        if (!groupCells.isEmpty()) {
                            String term = karnaughCanvas.createTermFromGroup(groupCells, numVariables).toUpperCase();

                            if (expressionBuilder.length() > 0) {
                                expressionBuilder.append(" + ");
                            }
                            expressionBuilder.append(term);

                            // Simplificar la expresión booleana
                            String simplifiedExpression = simplifyBooleanExpression(expressionBuilder.toString());
                            if (!simplifiedExpression.equals(expressionBuilder.toString())) {
                                expressionBuilder = new StringBuilder(simplifiedExpression);
                            }

                            karnaughCanvas.markGroupAsVisited(groupCells, visited);
                            break;
                        }
                    }
                }
            }
        }

        // Retornar solo la expresión simplificada final
        return expressionBuilder.toString();
    }


    // Método para simplificar una expresión booleana utilizando leyes booleanas aplicables a cualquier letra mayúscula
    private String simplifyBooleanExpression(String expression) {
        // Quitar espacios en blanco para trabajar de manera uniforme
        expression = expression.replace(" ", "");

        // Descomponer la expresión en términos
        String[] terms = expression.split("\\+");
        List<String> simplifiedTerms = new ArrayList<>();

        for (String term : terms) {
            String simplifiedTerm = simplifyTerm(term);
            // Agregar solo términos únicos
            if (!simplifiedTerms.contains(simplifiedTerm)) {
                simplifiedTerms.add(simplifiedTerm);
            }
        }

        // Reconstruir la expresión simplificada
        StringBuilder simplifiedExpression = new StringBuilder();
        for (int i = 0; i < simplifiedTerms.size(); i++) {
            simplifiedExpression.append(simplifiedTerms.get(i));
            if (i < simplifiedTerms.size() - 1) {
                simplifiedExpression.append(" + ");
            }
        }

        return simplifiedExpression.toString();
    }

    // Método para simplificar un término específico aplicando leyes booleanas
    private String simplifyTerm(String term) {
        // Ley de complemento: A * ~A = 0
        if (term.contains("*")) {
            String[] factors = term.split("\\*");
            for (int i = 0; i < factors.length; i++) {
                for (int j = i + 1; j < factors.length; j++) {
                    if (factors[i].equals("~" + factors[j]) || factors[j].equals("~" + factors[i])) {
                        return "0";
                    }
                }
            }
        }

        // Ley de idempotencia: A * A = A
        if (term.contains("*")) {
            Set<String> uniqueFactors = new HashSet<>(Arrays.asList(term.split("\\*")));
            if (uniqueFactors.size() < term.split("\\*").length) {
                term = String.join("*", uniqueFactors);
            }
        }

        // Ley de dominancia: A + 1 = 1 y A * 0 = 0
        if (term.equals("1") || term.contains("*0")) {
            return "0";
        }

        // Simplificación de términos complementarios: A + ~A = 1
        if (term.equals("0") || term.contains("~")) {
            return "0";
        }

        return term;
    }


}
