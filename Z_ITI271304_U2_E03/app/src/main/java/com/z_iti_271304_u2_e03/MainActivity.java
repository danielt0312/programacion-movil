package com.z_iti_271304_u2_e03;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.z_iti_271304_u2_e03.simulation.SimulationView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SimulationView simulationView;
    private TextView generationText;
    private TextView populationText;
    private Button startButton;
    private Button predatorButton;
    private Button restartButton;
    private LineChart populationChart;
    private Handler handler;
    private static final long GENERATION_DELAY = 3000; // 3 segundos por generación
    private boolean isSimulationRunning = false;
    private ArrayList<Entry> chartEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupChart();
        setupButtons();
        handler = new Handler();
    }

    private void initializeViews() {
        simulationView = findViewById(R.id.simulation_view);
        generationText = findViewById(R.id.generation_count);
        populationText = findViewById(R.id.population_count);
        startButton = findViewById(R.id.simulation_button);
        predatorButton = findViewById(R.id.predator_button);
        restartButton = findViewById(R.id.restart_button);
        populationChart = findViewById(R.id.population_chart);
    }

    private void setupChart() {
        populationChart.setVisibility(View.VISIBLE);
        populationChart.getDescription().setEnabled(false);
        populationChart.setTouchEnabled(true);
        populationChart.setDragEnabled(true);
        populationChart.setScaleEnabled(true);

        XAxis xAxis = populationChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);

        YAxis leftAxis = populationChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);

        populationChart.getAxisRight().setEnabled(false);

        // Configurar dataset inicial
        LineDataSet dataSet = new LineDataSet(new ArrayList<>(), "Población");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        populationChart.setData(lineData);
    }

    private void setupButtons() {
        startButton.setOnClickListener(v -> {
            if (!isSimulationRunning) {
                startSimulation();
                startButton.setText("Detener");
            } else {
                stopSimulation();
                startButton.setText("Iniciar");
            }
        });

        predatorButton.setOnClickListener(v -> {
            if (isSimulationRunning) {
                predatorButton.setEnabled(false);
                simulationView.togglePredator(true);

                // Reactivar el botón después de que termine la ronda
                new Handler().postDelayed(() -> {
                    predatorButton.setEnabled(true);
                }, 10000); // 10 segundos
            }
        });

        restartButton.setOnClickListener(v -> {
            stopSimulation();
            simulationView.restartSimulation();
            startButton.setText("Iniciar");
            predatorButton.setEnabled(true);
            clearChart();
            updateUI();
        });
    }

    private void startSimulation() {
        isSimulationRunning = true;
        runSimulation();
    }

    private void stopSimulation() {
        isSimulationRunning = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void runSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSimulationRunning && !simulationView.isGameOver()) {
                    simulationView.updateSimulation();
                    updateUI();

                    if (!simulationView.isGameOver()) {
                        handler.postDelayed(this, GENERATION_DELAY);
                    } else {
                        showGameOverDialog();
                    }
                }
            }
        }, GENERATION_DELAY);
    }

    private void updateUI() {
        int generation = simulationView.getCurrentGeneration();
        int population = simulationView.getCurrentPopulation();

        generationText.setText("Generación: " + generation);
        populationText.setText("Población: " + population);

        updateChart(generation, population);
    }

    private void updateChart(int generation, int population) {
        LineData data = populationChart.getData();
        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null) {
                set = new LineDataSet(null, "Población");
                data.addDataSet(set);
            }

            data.addEntry(new Entry(generation, population), 0);
            data.notifyDataChanged();

            populationChart.notifyDataSetChanged();
            populationChart.setVisibleXRangeMaximum(6);
            populationChart.moveViewToX(data.getEntryCount());
        }
    }

    private void clearChart() {
        populationChart.clear();
        setupChart();
    }

    private void showGameOverDialog() {
        new AlertDialog.Builder(this)
                .setTitle("¡Fin de la Simulación!")
                .setMessage("¡Los conejos han tomado el control del mundo!")
                .setPositiveButton("Reiniciar", (dialog, which) -> {
                    simulationView.restartSimulation();
                    startButton.setText("Iniciar");
                    isSimulationRunning = false;
                    clearChart();
                    updateUI();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSimulation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }



}