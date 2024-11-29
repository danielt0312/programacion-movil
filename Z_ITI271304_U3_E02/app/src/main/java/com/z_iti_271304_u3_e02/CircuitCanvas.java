package com.z_iti_271304_u3_e02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CircuitCanvas extends View {

    private String expression;
    private Paint paint;

    public CircuitCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircuitCanvas(Context context, String expression) {
        super(context);
        this.expression = expression;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(40);
    }

    public void setExpression(String expression) {
        this.expression = expression;
        invalidate(); // Redibujar la vista cuando la expresión cambia
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (expression != null) {
            drawCircuit(canvas);
        }
    }

    private void drawCircuit(Canvas canvas) {
        if (expression == null || expression.isEmpty()) return;

        int startX = 100;
        int startY = 100;
        int elementWidth = 150;
        int elementHeight = 80;
        int offsetY = startY;

        // Separar términos por "+"
        String[] terms = expression.split("\\+");
        boolean hasOrGate = terms.length > 1;

        // Lista para almacenar las posiciones de las salidas de las compuertas AND
        List<int[]> andOutputs = new ArrayList<>();

        // Dibujar cada término basado en la expresión
        for (String term : terms) {
            term = term.trim();
            if (!term.isEmpty()) {
                if (term.contains("'")) {
                    drawNegatedAndGate(canvas, startX, offsetY, elementWidth, elementHeight, term);
                } else if (term.length() > 1) {
                    drawAndGate(canvas, startX, offsetY, elementWidth, elementHeight, term);
                } else {
                    drawVariable(canvas, startX, offsetY, elementWidth, elementHeight, term);
                }

                // Almacenar la posición de la salida de cada compuerta AND
                int[] andOutput = {startX + elementWidth, offsetY + elementHeight / 2};
                andOutputs.add(andOutput);

                offsetY += elementHeight + 50;
            }
        }

        // Conectar las compuertas AND con compuertas OR en pares
        while (andOutputs.size() > 1) {
            List<int[]> nextLevelOutputs = new ArrayList<>();
            for (int i = 0; i < andOutputs.size(); i += 2) {
                int[] output1 = andOutputs.get(i);
                int[] output2 = (i + 1 < andOutputs.size()) ? andOutputs.get(i + 1) : null;

                int orGateX = output1[0] + 100;
                int orGateY = output1[1];

                // Si hay dos salidas de AND, dibujamos la compuerta OR con ambas
                if (output2 != null) {
                    drawOrGate(canvas, orGateX, orGateY, elementWidth, elementHeight);

                    // Conectar las salidas de las compuertas AND a la entrada de la OR
                    canvas.drawLine(output1[0], output1[1], orGateX, orGateY + elementHeight / 2, paint);
                    canvas.drawLine(output2[0], output2[1], orGateX, orGateY + elementHeight / 2, paint);

                    // Almacenar la posición de la salida de esta compuerta OR
                    int[] orOutput = {orGateX + elementWidth, orGateY + elementHeight / 2};
                    nextLevelOutputs.add(orOutput);
                } else {
                    // Si solo hay una salida, dibujamos la compuerta OR con solo una entrada
                    drawOrGate(canvas, orGateX, orGateY, elementWidth, elementHeight);
                    canvas.drawLine(output1[0], output1[1], orGateX, orGateY + elementHeight / 2, paint);

                    // Almacenar la posición de la salida de esta compuerta OR
                    int[] orOutput = {orGateX + elementWidth, orGateY + elementHeight / 2};
                    nextLevelOutputs.add(orOutput);
                }
            }

            // Avanzar al siguiente nivel de OR gates
            andOutputs = nextLevelOutputs;
        }
    }


    private void drawVariable(Canvas canvas, int x, int y, int width, int height, String variable) {
        paint.setTextSize(40);
        canvas.drawText(variable, x - 30, y + height / 2 + 10, paint);
        canvas.drawLine(x, y + height / 2, x + width, y + height / 2, paint);
    }

    private void drawAndGate(Canvas canvas, int x, int y, int width, int height, String term) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + width / 2, y);
        path.arcTo(x + width / 2, y, x + width, y + height, -90, 180, false);
        path.lineTo(x, y + height);
        path.close();

        // Asegurarse de que el fondo sea blanco
        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
        canvas.drawPath(path, fillPaint);

        // Dibujar el contorno de la compuerta en negro
        Paint strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);
        canvas.drawPath(path, strokePaint);

        // Agregar el texto 'AND' en el centro de la compuerta
        strokePaint.setTextSize(40);
        strokePaint.setStyle(Paint.Style.FILL);
        canvas.drawText("AND", x + width / 4, y + height / 2 + 10, strokePaint);

        String[] variables = term.split("");
        drawInputs(canvas, variables, x, y, width, height);
    }

    private void drawNegatedAndGate(Canvas canvas, int x, int y, int width, int height, String term) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + width / 2, y);
        path.arcTo(x + width / 2, y, x + width, y + height, -90, 180, false);
        path.lineTo(x, y + height);
        path.close();

        // Asegurarse de que el fondo sea blanco
        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
        canvas.drawPath(path, fillPaint);

        // Dibujar el contorno de la compuerta en negro
        Paint strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);
        canvas.drawPath(path, strokePaint);

        // Agregar el texto 'AND' en el centro de la compuerta
        strokePaint.setTextSize(40);
        strokePaint.setStyle(Paint.Style.FILL);
        canvas.drawText("AND", x + width / 4, y + height / 2 + 10, strokePaint);

        // Separar las variables y determinar cuáles están negadas
        String[] variables = term.replace("'", "").split("");
        boolean[] negated = new boolean[variables.length];

        for (int i = 0; i < variables.length; i++) {
            if (term.contains(variables[i] + "'")) {
                negated[i] = true;
            }
        }

        // Dibujar las entradas con la lógica actualizada para variables negadas y positivas
        drawInputsWithNegation(canvas, variables, negated, x, y, width, height);
    }

    private void drawInputsWithNegation(Canvas canvas, String[] factors, boolean[] negated, int x, int y, int width, int height) {
        int inputY = y + height / (factors.length + 1);
        int inputSpacing = height / (factors.length + 1);

        for (int i = 0; i < factors.length; i++) {
            int inputX = x - 50;
            canvas.drawLine(inputX, inputY, x, inputY, paint);

            // Reducir el tamaño del texto para las variables y agregar un margen
            paint.setTextSize(30); // Tamaño más pequeño
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(factors[i], inputX - 40, inputY + 10, paint); // Aumentar el margen

            // Dibujar un círculo en la entrada si la variable está negada
            if (negated[i]) {
                canvas.drawCircle(inputX, inputY, 8, paint);
            }

            inputY += inputSpacing;
        }
    }





    private void drawOrGate(Canvas canvas, int x, int y, int width, int height) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + width / 4, y);
        path.arcTo(x, y, x + width, y + height, -90, 180, false);
        path.lineTo(x, y + height);
        path.close();

        // Asegurarse de que el fondo sea blanco
        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
        canvas.drawPath(path, fillPaint);

        // Dibujar el contorno de la compuerta en negro
        Paint strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);
        canvas.drawPath(path, strokePaint);

        // Dibujar el texto 'OR' en el centro de la compuerta
        strokePaint.setTextSize(40);
        strokePaint.setStyle(Paint.Style.FILL);
        canvas.drawText("OR", x + width / 4, y + height / 2 + 10, strokePaint);
    }



    private void drawInputs(Canvas canvas, String[] factors, int x, int y, int width, int height) {
        int inputY = y + height / (factors.length + 1);
        int inputSpacing = height / (factors.length + 1);

        for (String factor : factors) {
            int inputX = x - 50;
            canvas.drawLine(inputX, inputY, x, inputY, paint);

            // Reducir el tamaño del texto para las variables y agregar un margen
            paint.setTextSize(30); // Tamaño más pequeño
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(factor, inputX - 40, inputY + 10, paint); // Aumentar el margen

            inputY += inputSpacing;
        }
    }

}
