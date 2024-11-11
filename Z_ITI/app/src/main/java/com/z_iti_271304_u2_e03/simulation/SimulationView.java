package com.z_iti_271304_u2_e03.simulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.z_iti_271304_u2_e03.R;

import java.util.ArrayList;
import java.util.Random;

public class SimulationView extends View {
    // Constantes de simulación
    private static final int INITIAL_BUNNIES = 2;
    private static final int MAX_GENERATION = 6;
    private static final int FOOD_COUNT = 10;
    private static final long ROUND_DURATION = 10000; // 10 segundos
    private static final long PREDATOR_DURATION = 4000; // 4 segundos
    private int startOfGenerationBunnies = INITIAL_BUNNIES; // Para trackear el inicio de la generación
    private static final int VICTORY_POPULATION = 480; // Población para ganar

    // Listas de elementos de simulación
    private ArrayList<Bunny> bunnies;
    private ArrayList<Wolf> wolves;
    private ArrayList<Food> foods;
    private ArrayList<GrassPatch> grassPatches;

    // Bitmaps
    private Bitmap whiteBunnyBitmap, brownBunnyBitmap, wolfBitmap, foodBitmap;

    // Estados
    private boolean isGameOver = false;
    private boolean isPredatorActive = false;
    private boolean isRoundActive = false;
    private int currentGeneration = 0;
    private long roundStartTime;

    // Variables de dibujo
    private Paint skyPaint;
    private Paint groundPaint;
    private Paint grassPaint;
    private Paint timerPaint;
    private Paint timerBackgroundPaint;
    private Paint predatorTimerPaint;
    private Paint textPaint;
    private Paint generationTextPaint;
    private LinearGradient skyGradient;
    private Path groundPath;
    private RectF timerRect;

    // Variables de ambiente
    private int groundLevel;
    private Random random;
    private Context context;

    public SimulationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.random = new Random();
        initializeView();
    }

    private void initializeView() {
        // Inicializar colecciones
        bunnies = new ArrayList<>();
        wolves = new ArrayList<>();
        foods = new ArrayList<>();
        grassPatches = new ArrayList<>();

        // Inicializar Paints
        initializePaints();

        // Cargar bitmaps
        loadBitmaps();
    }

    private void initializePaints() {
        // Paint para el cielo
        skyPaint = new Paint();

        // Paint para el suelo
        groundPaint = new Paint();
        groundPaint.setColor(Color.parseColor("#8B4513"));
        groundPaint.setStyle(Paint.Style.FILL);

        // Paint para la hierba
        grassPaint = new Paint();
        grassPaint.setColor(Color.parseColor("#228B22"));
        grassPaint.setStyle(Paint.Style.FILL);

        // Paint para el timer
        timerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timerPaint.setColor(Color.BLUE);
        timerPaint.setStyle(Paint.Style.STROKE);
        timerPaint.setStrokeWidth(20f);

        timerBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timerBackgroundPaint.setColor(Color.LTGRAY);
        timerBackgroundPaint.setStyle(Paint.Style.STROKE);
        timerBackgroundPaint.setStrokeWidth(20f);

        predatorTimerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        predatorTimerPaint.setColor(Color.RED);
        predatorTimerPaint.setStyle(Paint.Style.STROKE);
        predatorTimerPaint.setStrokeWidth(20f);

        // Paint para texto
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);

        // Paint para texto de generación
        generationTextPaint = new Paint();
        generationTextPaint.setColor(Color.BLACK);
        generationTextPaint.setTextSize(50);
        generationTextPaint.setTextAlign(Paint.Align.CENTER);
        generationTextPaint.setAntiAlias(true);
    }

    private void loadBitmaps() {
        whiteBunnyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.conejo_blanco);
        brownBunnyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.conejo_cafe);
        wolfBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.depredador);
        foodBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.comida);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calcular nivel del suelo
        groundLevel = (int)(h * 0.6f);

        // Crear gradiente del cielo
        skyGradient = new LinearGradient(
                0, 0, 0, groundLevel,
                Color.parseColor("#87CEEB"),
                Color.parseColor("#E0F6FF"),
                Shader.TileMode.CLAMP
        );
        skyPaint.setShader(skyGradient);

        // Crear path para el terreno ondulado
        createGroundPath(w, h + 200);

        // Escalar bitmaps
        int bunnySize = h / 12;
        int wolfSize = (int)(bunnySize * 1.5f);
        int foodSize = bunnySize / 2;

        whiteBunnyBitmap = Bitmap.createScaledBitmap(whiteBunnyBitmap, bunnySize, bunnySize, true);
        brownBunnyBitmap = Bitmap.createScaledBitmap(brownBunnyBitmap, bunnySize, bunnySize, true);
        wolfBitmap = Bitmap.createScaledBitmap(wolfBitmap, wolfSize, wolfSize, true);
        foodBitmap = Bitmap.createScaledBitmap(foodBitmap, foodSize, foodSize, true);

        // Configurar rectángulo del timer
        int timerSize = Math.min(w, h) / 6;
        int margin = 30;
        timerRect = new RectF(
                w - timerSize - margin,
                margin,
                w - margin,
                timerSize + margin
        );

        // Generar hierba y inicializar simulación
        generateGrassPatches(w, h);
        initializeSimulation();
    }

    private void initializeSimulation() {
        bunnies.clear();
        wolves.clear();
        foods.clear();
        currentGeneration = 0;
        isGameOver = false;
        isPredatorActive = false;
        isRoundActive = false;

        // Crear conejos iniciales
        for (int i = 0; i < INITIAL_BUNNIES; i++) {
            float x = random.nextInt(getWidth() - whiteBunnyBitmap.getWidth());
            float y = groundLevel - whiteBunnyBitmap.getHeight();
            bunnies.add(new Bunny(x, y + 100, true, groundLevel));
        }

        spawnFood();
    }

    public void updateSimulation() {

        if (isGameOver) return;

        // Guardar la población al inicio de la generación si no hay depredadores activos
        if (!isPredatorActive) {
            startOfGenerationBunnies = bunnies.size();
        }

        if (currentGeneration < MAX_GENERATION) {
            int currentPopulation = bunnies.size();
            int newPopulation;

            if (isPredatorActive) {
                // Lógica de depredadores
                if (currentPopulation < 7) {
                    newPopulation = 1;
                } else if (currentPopulation < 30) {
                    newPopulation = 3;
                } else {
                    newPopulation = (int) Math.ceil(currentPopulation * 0.1);
                }

                updateBunnyPopulation(newPopulation);
                startOfGenerationBunnies = currentPopulation; // Guardar población antes del ataque
            } else {
                // Lógica de reproducción
                if (wolves.isEmpty()) {
                    newPopulation = currentPopulation * 3; // Reproducción normal
                } else {
                    newPopulation = (currentPopulation * 3) - 2; // Reproducción después de depredadores
                }

                // Verificar si alcanzamos la población de victoria
                if (newPopulation >= VICTORY_POPULATION) {
                    isGameOver = true;
                    Toast.makeText(context, "¡Los conejos han tomado el control del mundo!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                updateBunnyPopulation(newPopulation);
            }

            if (!isPredatorActive) {
                currentGeneration++; // Solo incrementar generación en fase de reproducción
            }
        }

        // Actualizar posiciones
        for (Bunny bunny : bunnies) {
            bunny.update(getWidth(), getHeight());
        }

        for (Wolf wolf : wolves) {
            wolf.update(getWidth(), getHeight());
            wolf.hunt(bunnies);
        }

        checkFoodCollisions();
        invalidate();
    }

    private void updateBunnyPopulation(int targetPopulation) {
        // Asegurar que no excedamos el límite de victoria
        targetPopulation = Math.min(targetPopulation, VICTORY_POPULATION);

        // Actualizar población
        while (bunnies.size() > targetPopulation) {
            bunnies.remove(bunnies.size() - 1);
        }

        while (bunnies.size() < targetPopulation) {
            float x = random.nextInt(getWidth() - whiteBunnyBitmap.getWidth());
            float y = groundLevel - whiteBunnyBitmap.getHeight();
            boolean isWhite = random.nextBoolean();
            bunnies.add(new Bunny(x, y + random.nextInt(450) + 100, isWhite, groundLevel));
        }
    }

    private void checkFoodCollisions() {
        for (Bunny bunny : bunnies) {
            for (int i = foods.size() - 1; i >= 0; i--) {
                Food food = foods.get(i);
                float dx = bunny.x - food.x;
                float dy = bunny.y - food.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < whiteBunnyBitmap.getWidth() / 2) {
                    foods.remove(i);
                }
            }
        }

        // Reponer comida si queda poca
        if (foods.size() < FOOD_COUNT / 2) {
            spawnFood();
        }
    }

    private void spawnFood() {
        foods.clear();
        for (int i = 0; i < FOOD_COUNT; i++) {
            float x = random.nextInt(getWidth() - foodBitmap.getWidth());
            float y = groundLevel - foodBitmap.getHeight()/2;
            foods.add(new Food(x, y + random.nextInt(450) + 100));
        }
    }

    private void createGroundPath(int width, int height) {
        groundPath = new Path();
        groundPath.moveTo(0, groundLevel);

        float amplitude = 50;
        float wavelength = width / 4f;

        for (float x = 0; x <= width; x += 10) {
            float y = groundLevel + amplitude * (float)Math.sin(x * 2 * Math.PI / wavelength);
            groundPath.lineTo(x, y);
        }

        groundPath.lineTo(width, height);
        groundPath.lineTo(0, height);
        groundPath.close();
    }

    private void generateGrassPatches(int width, int height) {
        grassPatches.clear();
        int numPatches = 30;

        for (int i = 0; i < numPatches; i++) {
            float x = random.nextFloat() * width;
            float y = groundLevel + random.nextFloat() * (height * 0.3f);
            grassPatches.add(new GrassPatch(x, y));
        }
    }

    public void startRound() {
        roundStartTime = System.currentTimeMillis();
        isRoundActive = true;
        isPredatorActive = true;
        invalidate();

        // Programar fin de fase de depredadores
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRoundActive) {
                    isPredatorActive = false;
                    wolves.clear();
                    invalidate();
                }
            }
        }, PREDATOR_DURATION);

        // Programar fin de ronda
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isRoundActive = false;
                updateSimulation();
                invalidate();
            }
        }, ROUND_DURATION);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        // Dibujar ambiente
        canvas.drawRect(0, 0, getWidth(), getHeight(), skyPaint);
        canvas.drawPath(groundPath, groundPaint);

        // Dibujar hierba
        for (GrassPatch patch : grassPatches) {
            patch.draw(canvas, grassPaint);
        }

        // Dibujar comida
        for (Food food : foods) {
            canvas.drawBitmap(foodBitmap, food.x, food.y, null);
        }

        // Dibujar conejos
        for (Bunny bunny : bunnies) {
            if (bunny.isAlive) {
                Bitmap bunnyBitmap = bunny.isWhite ? whiteBunnyBitmap : brownBunnyBitmap;
                bunny.draw(canvas, bunnyBitmap);
            }
        }

        // Dibujar lobos
        if (isPredatorActive) {
            for (Wolf wolf : wolves) {
                wolf.draw(canvas, wolfBitmap);
            }
        }

        // Dibujar timer si está activo
        if (isRoundActive) {
            drawTimer(canvas);
        }

        String genText = "Generation " + currentGeneration;
        String startPopText = "Start of Generation: " + startOfGenerationBunnies + " bunnies";
        String currentPopText = "Currently: " + bunnies.size() + " bunnies";

        // Posicionar textos
        canvas.drawText(genText, getWidth()/2f, 80, generationTextPaint);
        canvas.drawText(startPopText, 20, getHeight() - 100, textPaint);
        canvas.drawText(currentPopText, 20, getHeight() - 60, textPaint);
    }

    private void drawTimer(Canvas canvas) {
        canvas.drawArc(timerRect, -90, 360, false, timerBackgroundPaint);

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - roundStartTime;
        float roundProgress = 360f * elapsedTime / ROUND_DURATION;

        canvas.drawArc(timerRect, -90, roundProgress, false, timerPaint);

        if (elapsedTime <= PREDATOR_DURATION) {
            float predatorProgress = 360f * elapsedTime / PREDATOR_DURATION;
            canvas.drawArc(timerRect, -90, predatorProgress, false, predatorTimerPaint);
        }
    }
    public int getCurrentPopulation() {
        return bunnies.size();
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void restartSimulation() {
        initializeSimulation();
        invalidate();
    }

    public void togglePredator(boolean active) {
        if (active && !isRoundActive) {
            // Guardar la población actual antes de activar depredadores
            startOfGenerationBunnies = bunnies.size();
            startRound();
            if (wolves.isEmpty()) {
                wolves.add(new Wolf(getWidth() / 2f, getHeight() / 2f));
            }
        }
    }

    // ... (resto de métodos de SimulationView como updateSimulation, togglePredator, etc.)

    private class GrassPatch {
        float x, y;
        Path grassPath;

        GrassPatch(float x, float y) {
            this.x = x;
            this.y = y;
            createGrassPath();
        }

        private void createGrassPath() {
            grassPath = new Path();
            float size = 20;
            grassPath.moveTo(x, y);

            for (int i = 0; i < 5; i++) {
                float angle = (float) (Math.PI/6 + i * Math.PI/12);
                float leafX = x + size * (float)Math.cos(angle);
                float leafY = y - size * (float)Math.sin(angle);
                grassPath.lineTo(leafX, leafY);
                grassPath.lineTo(x, y);
            }
        }

        void draw(Canvas canvas, Paint paint) {
            canvas.drawPath(grassPath, paint);
        }
    }
}

class Food {
    float x, y;

    Food(float x, float y) {
        this.x = x;
        this.y = y;
    }


}