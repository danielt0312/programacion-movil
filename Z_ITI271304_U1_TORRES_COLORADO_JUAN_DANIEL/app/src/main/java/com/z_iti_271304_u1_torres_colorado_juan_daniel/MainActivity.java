package com.z_iti_271304_u1_torres_colorado_juan_daniel;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private Context CX;
    private Integer day, month, year, currentlyWeek;
    private ArrayAdapter<String> adpOption, adpMonth;
    private ArrayAdapter<Integer> adpDay;
    private List<String> options, months;
    private List<Integer> days;
    private Calendar inputDate, conceptionDate, pregnancyDate, currentlyDate, startDate, endDate;
    public SimpleDateFormat sdf = new SimpleDateFormat("d MMM, yyyy");

    private Spinner spnOption, spnDay, spnMonth;
    private EditText etYear;
    private Button btnCalculate;
    private LinearLayout lytTable, lytText;
    TextView txtDate;

    private TextWatcher wchYear = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!charSequence.toString().isEmpty() && isYearInRange()) {
                changeDays((int) spnMonth.getSelectedItemId(), Integer.parseInt(charSequence.toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spnOption = findViewById(R.id.spnCalculate);
        spnDay = findViewById(R.id.spnDay);
        spnMonth = findViewById(R.id.spnMonth);
        etYear = findViewById(R.id.etYear);
        btnCalculate = findViewById(R.id.btnCalculate);
        lytTable = findViewById(R.id.lytTable);
        lytText = findViewById(R.id.lytText);
        txtDate = findViewById(R.id.txtDate);
        CX = this;

        options = new ArrayList<>(Arrays.asList("Fecha de parto", "Último periodo", "Ultrasonido", "Fecha de concepcion", "Fecha de transferencia FIV"));
        months = new ArrayList<>(Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"));
        days = new ArrayList<>();

        inputDate = Calendar.getInstance();

        etYear.setText("2025");

        adpOption = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        spnOption.setAdapter(adpOption);

        adpMonth = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, months);
        spnMonth.setAdapter(adpMonth);

        adpDay = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, days);
        spnDay.setAdapter(adpDay);

        spnMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isYearInRange()) {
                    changeDays(i, Integer.parseInt(etYear.getText().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        txtDate.setText("Primer día de tu último período:");
                        break;
                    case 2:
                        txtDate.setText("Fecha de la ecografía:");
                        break;
                    case 3:
                        txtDate.setText("Fecha de la concepcion:");
                        break;
                    case 4:
                        txtDate.setText("Fecha de transferencia:");
                        break;
                    default:
                        txtDate.setText("Tu fecha:");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCalculate();
            }
        });

        etYear.addTextChangedListener(wchYear);
        changeDays(0, 2024);
    }

    // Revisa los parámetros necesarios antes de calcular de acuerdo a la opcion seleccionada
    public void onCalculate() {
        // Eliminar los resultados que ya han sido mostrado en pantalla
        if (lytText.getChildCount() > 0) {
            lytText.removeAllViews();
        }

        if (lytTable.getChildCount() > 0) {
            lytTable.removeAllViews();
        }

        day = spnDay.getSelectedItemPosition() + 1;
        month = spnMonth.getSelectedItemPosition();
        year = Integer.parseInt(etYear.getText().toString());

        inputDate.set(year, month, day);

        switch ((int) spnOption.getSelectedItemId()) {
            case 0:
                this.dueDate();
                break;
            case 1:
                this.lastPeriod();
                break;
            case 2:
                this.ultrasound();
                break;
            case 3:
                this.conception();
                break;
            case 4:
                this.vif();
            default:
                break;
        }
    }

    // Basado en la fecundacion in vitrio (fiv)
    public void vif() {
        final Dialog diag = new Dialog(CX);
        diag.setContentView(R.layout.vif);
        diag.setTitle("Cálculo por FIV");

        Button btnAceptar = diag.findViewById(R.id.btnAccept);
        RadioButton[] rdgEmbrionDays = {
                diag.findViewById(R.id.rbtDay3),
                diag.findViewById(R.id.rbtDay5),
                diag.findViewById(R.id.rbtDay6),
        };

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int edadEmbrion = 5;
                for (int i = 0; i < rdgEmbrionDays.length; i++) {
                    if (rdgEmbrionDays[i].isChecked()) {
                        edadEmbrion = i == 0 ? 3 : (i == 2 ? 6 : 5);
                        break;
                    }
                }

                int diasEmbarazo = 266 - edadEmbrion;

                // Crear un nuevo Calendar para la fecha estimada de parto
                Calendar fechaParto = (Calendar) inputDate.clone();

                // Añadir los días restantes del embarazo a la fecha de transferencia
                fechaParto.add(Calendar.DAY_OF_YEAR, diasEmbarazo);

                lytTable.addView(getTable(fechaParto));
                resultText();

                diag.dismiss();
            }
        });

        diag.show();
    }

    // Basado en la fecha de concepcion
    public void conception() {
        // Clonar el objeto Calendar para no modificar el original
        Calendar fechaParto = (Calendar) inputDate.clone();

        fechaParto.add(Calendar.DAY_OF_YEAR, 266);
        lytTable.addView(getTable(fechaParto));

        this.resultText();
    }

    // Basado en el ultrasonido/ecografia
    public void ultrasound() {
        final Dialog diag = new Dialog(CX);
        diag.setContentView(R.layout.ultrasound);
        diag.setTitle("Cálculo por ultrasonido");

        Button btnAceptar = diag.findViewById(R.id.btnAccept);
        EditText etWeeks = diag.findViewById(R.id.etWeeks);
        EditText etDays = diag.findViewById(R.id.etDays);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etWeeks.getText().toString().isEmpty() || etDays.getText().toString().isEmpty()) {
                    Toast.makeText(CX, "Debes ingresar un valor en las semanas o en los días", Toast.LENGTH_SHORT).show();
                    return;
                }

                int nWeeks = Integer.parseInt(etWeeks.getText().toString());
                int nDays = Integer.parseInt(etDays.getText().toString());

                Calendar ultrasoundDate = (Calendar) inputDate.clone();

                int diasTotalesGestacion = (nWeeks * 7) + nDays;
                ultrasoundDate.add(Calendar.DAY_OF_YEAR, -(diasTotalesGestacion));

                ultrasoundDate.add(Calendar.DAY_OF_YEAR, 280);

                lytTable.addView(getTable(ultrasoundDate));
                resultText();

                diag.dismiss();
            }
        });

        diag.show();
    }

    // Basado en el dia de parto
    public void dueDate() {
        lytTable.addView(this.getTable(inputDate));
        this.resultText();
    }

    // Basado en el ultimo periodo
    public void lastPeriod() {
        final Dialog diag = new Dialog(CX);
        diag.setContentView(R.layout.last_period);
        diag.setTitle("Cálculo por periodo");

        List<Integer> dias = new ArrayList<>();
        for (int i = 22; i < 45; i++) {
            dias.add(i);
        }

        Spinner spnPeriod = diag.findViewById(R.id.spnCiclo);
        ArrayAdapter<Integer> adpDiasPeriodo = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dias);
        spnPeriod.setAdapter(adpDiasPeriodo);

        Button btnAceptar = diag.findViewById(R.id.btnAccept);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calculo para el ultimo periodo
                Calendar fechaParto = (Calendar) inputDate.clone();

                // Ajustar el cálculo de los dias extra o menos según la duracion del ciclo (respecto a 28 dias estándar)
                int ajusteCiclo = ((int) spnPeriod.getSelectedItem()) - 28;

                // Sumar 280 dias (40 semanas) y ajustar según la duracion del ciclo
                fechaParto.add(Calendar.DAY_OF_YEAR, 280 + ajusteCiclo);

                lytTable.addView(getTable(fechaParto));
                resultText();

                diag.dismiss();
            }
        });

        diag.show();
    }

    // Para mostrar el texto predictivo
    public void resultText() {
        if (spnOption.getSelectedItemPosition() == 3 || spnOption.getSelectedItemId() == 4)
            lytText.addView(this.createTxtResult("La fecha de parto estimada es " + sdf.format(pregnancyDate.getTime()) + "."));
        else
            lytText.addView(this.createTxtResult("Es probable que su bebé haya sido concebido el " + sdf.format(conceptionDate.getTime()) + "."));

        /*
        * validacion de que la fecha presente se encuentre dentro del rango de fechas del cronograma de emabarazo
        * para posteriormente mostrar resultados más precisos
        * */
        if (currentlyWeek != -1) {
            if (currentlyWeek > 1 && currentlyWeek < 13) {
                lytText.addView(this.createTxtResult("Estás en el primer trimestre."));
            } else if (currentlyWeek > 12 && currentlyWeek < 28) {
                lytText.addView(this.createTxtResult("Estás en el segundo trimestre."));
            } else {
                lytText.addView(this.createTxtResult("Estás en el tercer trimestre."));
            }

            if (currentlyWeek > 0 && currentlyWeek < 9) {
                lytText.addView(this.createTxtResult("En promedio, su bebé pesa menos de 1 gramo en esta etapa."));
            } else if (currentlyWeek > 8 && currentlyWeek < 41) {
                lytText.addView(this.createTxtResult("En promedio, su bebé mide alrededor "+AVG_HEIGHT.get((currentlyWeek-9))+" cm de largo y pesa alrededor de "+AVG_WEIGHT.get((currentlyWeek-9))+" gramos."));
            } else if (currentlyWeek > 40 && currentlyWeek < 43) {
                lytText.addView(this.createTxtResult("En promedio, su bebé pesa más de "+AVG_WEIGHT.get(AVG_WEIGHT.size()-1)+" gramos en esta etapa."));
            }

            long diff = currentlyDate.getTimeInMillis() - startDate.getTimeInMillis();

            // diferencia en semanas y dias
            long totalDays = TimeUnit.MILLISECONDS.toDays(diff);
            long weeks = totalDays / 7;
            long days;

            // diferencia en meses y dias
            int startYear = startDate.get(Calendar.YEAR);
            int startMonth = startDate.get(Calendar.MONTH);
            int startDay = startDate.get(Calendar.DAY_OF_MONTH);

            int targetYear = currentlyDate.get(Calendar.YEAR);
            int targetMonth = currentlyDate.get(Calendar.MONTH);
            int targetDay = currentlyDate.get(Calendar.DAY_OF_MONTH);

            // diferencia en meses
            int months = (targetYear - startYear) * 12 + (targetMonth - startMonth);

            // Ajuste para los dias
            if (targetDay < startDay) {
                months--;
                Calendar temp = Calendar.getInstance();
                temp.set(targetYear, targetMonth - 1, startDay); // volvemos al mes anterior
                days = (currentlyDate.getTimeInMillis() - temp.getTimeInMillis()) / (1000 * 60 * 60 * 24);
            } else {
                days = targetDay - startDay;
            }

            lytText.addView(this.createTxtResult("Actualmente te encuentras en la semana #" + currentlyWeek + " de embarazo ("+weeks+" semanas "+((int) (days % 7))+" días o "+months+" meses "+days+" días)."));

            // Calcular los dias totales entre la fecha de inicio y la fecha de fin del cronograma
            diff = endDate.getTimeInMillis() - startDate.getTimeInMillis();
            totalDays = TimeUnit.MILLISECONDS.toDays(diff);

            // Calcular los dias transcurridos desde la fecha de inicio hasta la fecha actual
            long elapsedMillis = currentlyDate.getTimeInMillis() - startDate.getTimeInMillis();
            long elapsedDays = TimeUnit.MILLISECONDS.toDays(elapsedMillis);

            // Calcular el porcentaje de progreso
            int porcentaje = (int) (((double) elapsedDays / totalDays) * 100);

            lytText.addView(this.createTxtResult("Estás en el "+ porcentaje +"% de tu embarazo."));
        }
    }

    // Creacion de la tabla
    public LinearLayout getTable(Calendar date) {
        /*
         *  Debido a que se desconoce como crear una tabla u otros metodos para conseguir resultados,
         *  se realizara con  LinearLayouts enlazados, de modo que se aplicara lo aprendido en clase
         *  junto con la investigacion y uso de la documentacion como apoyo
         */

        LinearLayout lytTable = new LinearLayout(getApplicationContext());
        lytTable.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        lytTable.setOrientation(LinearLayout.VERTICAL);

        // Cabecera de la tabla
        LinearLayout lytHeader = this.getHeader();

        // Cuerpo de la tabla
        LinearLayout lytBody = this.getBody(date);

        lytTable.addView(lytHeader);
        lytTable.addView(lytBody);

        return lytTable;
    }

    // Creacion de la cabecera de la tabla
    private LinearLayout getHeader() {
        LinearLayout lytHeader = new LinearLayout(getApplicationContext());
        lytHeader.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        lytHeader.setOrientation(LinearLayout.HORIZONTAL);

        String[] cabeceraNombres = {"Semana", "Fecha", "Trimestre", "Hitos importantes"};
        for (String nombre : cabeceraNombres) {
            TextView txtNombre = new TextView(getApplicationContext());
            txtNombre.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, nombre.equals("Semana") ? 0.7f : 1f));
            txtNombre.setText(nombre);
            txtNombre.setTextColor(Color.WHITE);
            txtNombre.setBackgroundColor(Color.rgb(51, 102, 153));
            txtNombre.setGravity(Gravity.CENTER);
            lytHeader.addView(txtNombre);
        }

        return lytHeader;
    }

    // Creacion del cuerpo de la tabla
    private LinearLayout getBody(Calendar date) {
        LinearLayout lytBody = new LinearLayout(getApplicationContext());
        lytBody.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        lytBody.setOrientation(LinearLayout.HORIZONTAL);
        lytBody.setBackgroundColor(Color.DKGRAY);

        lytBody.addView(this.getColumnSemana());
        lytBody.addView(this.getColumnFecha(date));
        lytBody.addView(this.getColumnTrimestre());
        lytBody.addView(this.getColumnHitos());

        return lytBody;
    }

    /*
    * Se decidio agrupar por columnas para de esta manera lograr que algunas filas tuvieran mas espacio
    * en la tabla y dar ese "sentido" de disenio visual representativo
    */
    private LinearLayout getColumnSemana() {
        LinearLayout lytSemana = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams prmSemana = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.7f);
        lytSemana.setLayoutParams(prmSemana);
        prmSemana.rightMargin = 2;
        lytSemana.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < 42; i++) {
            TextView txtNumber = new TextView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            params.bottomMargin = 2;
            txtNumber.setBackgroundColor(Color.LTGRAY);
            txtNumber.setLayoutParams(params);
            txtNumber.setGravity(Gravity.CENTER);
            txtNumber.setBackgroundColor(i % 2 == 0 ? Color.WHITE : Color.LTGRAY);
            txtNumber.setText("" + (i+1));
            txtNumber.setTextColor(Color.BLACK);
            lytSemana.addView(txtNumber);
        }

        return lytSemana;
    }

    private LinearLayout getColumnFecha(Calendar date) {
        LinearLayout lytFecha = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams prmSemana = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lytFecha.setLayoutParams(prmSemana);
        prmSemana.rightMargin = 2;
        lytFecha.setOrientation(LinearLayout.VERTICAL);

        // Obtenemos las semanas (5 antes y 2 después)
        List<String> semanas = obtenerSemanas(date, 40, 1);

        for (int i = 0; i < 42; i++) {
            TextView txtNumber = new TextView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            params.bottomMargin = 2;
            txtNumber.setBackgroundColor(Color.LTGRAY);
            txtNumber.setLayoutParams(params);
            txtNumber.setBackgroundColor(i % 2 == 0 ? Color.WHITE : Color.LTGRAY);
            txtNumber.setText(semanas.get(i));
            txtNumber.setTextColor(Color.BLACK);
            lytFecha.addView(txtNumber);
        }

        return lytFecha;
    }

    private LinearLayout getColumnTrimestre() {
        LinearLayout lytTrimestre = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams prm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        prm.rightMargin = 2;
        lytTrimestre.setLayoutParams(prm);
        lytTrimestre.setOrientation(LinearLayout.VERTICAL);

        String[] namesTrimester = {"primer trimestre", "segundo trimestre", "tercer trimestre"};
        Float[] weightTrimester = {12f, 15f, 15f};
        for (int i = 0; i < namesTrimester.length; i++) {
            TextView txtTrimestre = new TextView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, weightTrimester[i]);
            params.bottomMargin = 2;
            txtTrimestre.setLayoutParams(params);
            txtTrimestre.setBackgroundColor(Color.LTGRAY);
            txtTrimestre.setText(namesTrimester[i]);
            txtTrimestre.setGravity(Gravity.CENTER);
            txtTrimestre.setTextColor(Color.BLACK);
            lytTrimestre.addView(txtTrimestre);
        }

        return lytTrimestre;
    }

    private LinearLayout getColumnHitos() {
        LinearLayout lyt = new LinearLayout(getApplicationContext());
        lyt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        lyt.setOrientation(LinearLayout.VERTICAL);

        lyt.addView(this.createWhiteSpace(2));
        lyt.addView(this.createHito("Concepcion del bebé", 1));
        lyt.addView(this.createHito("Prueba de embarazo positiva", 1));
        lyt.addView(this.createWhiteSpace(1));
        lyt.addView(this.createHito("Latidos del corazon detectables por ecografía", 2));
        lyt.addView(this.createWhiteSpace(5));
        lyt.addView(this.createHito("El riesgo de aborto espontáneo disminuye", 1));
        lyt.addView(this.createWhiteSpace(4));
        lyt.addView(this.createHito("El bebé comienza a hacer movimientos notables, puede escuchar sonidos y se puede determinar su género", 4));
        lyt.addView(this.createWhiteSpace(1));
        lyt.addView(this.createHito("Un bebé prematuro podría sobrevivir", 1));
        lyt.addView(this.createWhiteSpace(4));
        lyt.addView(this.createHito("El bebé puede respirar", 1));
        lyt.addView(this.createWhiteSpace(9));
        lyt.addView(this.createHito("Término", 5));

        return lyt;
    }

    /*
    * Se decidio realizar varias funciones para ahorrar lineas y ayudar un poco mas en
    * el proceso del calculo de cada opcion de la calculadora
    */
    private TextView createHito(String hito, int weight) {
        TextView txt = new TextView(getApplicationContext());
        txt.setBackgroundColor(Color.rgb(225, 254, 225));
        txt.setLayoutParams(createParams(weight));
        txt.setText(hito);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        return txt;
    }

    private TextView createWhiteSpace(int weight) {
        TextView txt = new TextView(getApplicationContext());
        txt.setBackgroundColor(Color.WHITE);
        txt.setLayoutParams(createParams(weight));
        return txt;
    }

    private TextView createTxtResult(String text) {
        TextView txt = new TextView(getApplicationContext());
        txt.setTextColor(Color.BLACK);
        txt.setText(text);
        return txt;
    }

    private LinearLayout.LayoutParams createParams(int weight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, weight);
        params.bottomMargin = 2;
        return params;
    }

    // Obtener semanas dada una fecha inicial junto con el numero de semanas previas y posteriores a esta fecha 
    public List<String> obtenerSemanas(Calendar fecha, int semanasAntes, int semanasDespues) {
        List<String> semanas = new ArrayList<>();
        Calendar fechaInicio = (Calendar) fecha.clone();
        Calendar fechaHoy = Calendar.getInstance();

        /*
        * Obtenemos la fecha de inicio del periodo de embarazo al retroceder las semanas que van
        * antes de la fecha de parto ingresada
        */
        fechaInicio.add(Calendar.WEEK_OF_YEAR, -semanasAntes);
        fechaInicio.add(Calendar.DAY_OF_WEEK, 1);

        startDate = (Calendar) fechaInicio.clone();

        currentlyWeek = -1;
        for (int i = 0; i < (semanasAntes + semanasDespues + 1); i++) {
            Calendar inicioSemana = (Calendar) fechaInicio.clone();
            Calendar finSemana = (Calendar) fechaInicio.clone();
            Calendar antesFinSemana = (Calendar) fechaInicio.clone();

            // Obtenemos el último día de la semana
            finSemana.add(Calendar.DAY_OF_WEEK, 6);
            antesFinSemana.add(Calendar.DAY_OF_WEEK, 7);

            // Si la fecha del día de hoy está dentro del cronograma de embarazo, guardar la fecha
            if (isDateInRange(fechaHoy, inicioSemana, antesFinSemana)) {
                currentlyWeek = i+1;
                currentlyDate = (Calendar) fechaHoy.clone();
            }

            // Fecha de concepcion
            if (i == 1) {
                conceptionDate = (Calendar) finSemana.clone();
            }

            // Ultima fecha del cronograma
            if (i == semanasAntes + semanasDespues) {
                endDate = (Calendar) finSemana.clone();
            }

            // Fecha de embarazo
            if (i == 39) {
                pregnancyDate = (Calendar) finSemana.clone();
            }

            semanas.add(sdf.format(inicioSemana.getTime()) + " - " + sdf.format(finSemana.getTime()));

            // Avanzamos a la siguiente semana
            fechaInicio.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return semanas;
    }

    // Pequenia validacion para la validacion del rango de anios
    public boolean isDateInRange(Calendar date, Calendar startDate, Calendar endDate) {
        return date.equals(startDate) || date.equals(endDate) || (date.after(startDate) && date.before(endDate));
    }

    public void changeDays(int month, int year) {
        days.clear();
        for (int i = 1; i <= dayOfMonth(month, year); i++) {
            days.add(i);
        }
        adpDay.notifyDataSetChanged();
    }

    public int dayOfMonth(int month, int year) {
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
                return isBissextile(year) ? 29 : 28;

            default:
                throw new IllegalArgumentException("Mes inválido: " + month);
        }
    }

    public boolean isBissextile(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    public boolean isYearInRange() {
        int input = Integer.parseInt(etYear.getText().toString());
        return input > 1800 && input < 3000; // Ajustar rango
    }

    /*
    * Dado que los promedios de la estatura y el peso varian de acuerdo al calculo promedio,
    * se utilizaran constantes definidas por la calculadora en la que se basa este proyecto
    * https://www.calculator.net/pregnancy-calculator.html
    */

    // Basado en centimetros
    public List<Float> AVG_HEIGHT = new ArrayList<>(Arrays.asList(
            // Iniciando despues de la semana 8
            2.3f, 3.1f, 4.1f, 5.4f, 7.4f, 8.7f, 10.1f, 11.6f, 13f, 14.2f, 15.3f, 16.4f, 26.7f, 27.8f, 28.9f,
            30f, 34.6f, 35.6f, 36.6f, 37.6f, 38.6f, 39.9f, 41.1f, 42.4f, 43.7f, 45f, 46.2f, 47.4f, 48.6f,
            49.8f, 50.7f, 51.2f
    ));

    // Basado en gramos
    public List<Integer> AVG_WEIGHT = new ArrayList<>(Arrays.asList(
            // Iniciando despues de la semana 8
            2, 4, 7, 14, 23, 43, 70, 100, 140, 190, 240, 300, 360, 430, 501, 600, 660, 760, 875, 1005, 1153,
            1319, 1502, 1702, 1918, 2146, 2383, 2622, 2859, 3083, 3288, 3462
    ));
}