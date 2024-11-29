    package com.z_iti_271304_u3_e02;

    import android.app.AlertDialog;
    import android.content.Intent;
    import android.graphics.Typeface;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.InputFilter;
    import android.text.InputType;
    import android.text.TextWatcher;
    import android.view.Gravity;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.TableLayout;
    import android.widget.TableRow;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    public class MainActivity extends AppCompatActivity {

        private LinearLayout tableContainer;
        private LinearLayout welcomeLayout;
        private Button generateTableButton;
        private Button generateMapButton;
        private boolean isTableVisible = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            tableContainer = findViewById(R.id.table_container);
            welcomeLayout = findViewById(R.id.welcome_layout); // Referencia al layout de bienvenida
            generateTableButton = findViewById(R.id.btn_generate_table);
            generateMapButton = findViewById(R.id.btn_generate_map);

            // Inicialmente el botón de generar mapa está deshabilitado
            generateMapButton.setOnClickListener(view -> openKarnaughActivity());

            // Listener para el botón de generar tabla/cancelar
            generateTableButton.setOnClickListener(view -> {
                if (!isTableVisible) {
                    showVariableInputDialog();
                    welcomeLayout.setVisibility(View.GONE); // Ocultar bienvenida
                } else {
                    showCancelConfirmationDialog();
                }
            });
        }

        // Método para abrir la actividad de Karnaugh
        private void openKarnaughActivity() {
            int numVariables = getNumVariables();
            String[] outputs = getOutputsFromTable(); // Cambia a String[] para manejar "0", "1", y "X"

            Intent intent = new Intent(MainActivity.this, KarnaughActivity.class);
            intent.putExtra("numVariables", numVariables);
            intent.putExtra("outputs", outputs);
            startActivity(intent);
        }



        // Método para obtener el número de variables
        private int getNumVariables() {
            // El número de variables se puede deducir de la cantidad de filas generadas en la tabla
            TableLayout tableLayout = (TableLayout) tableContainer.getChildAt(0);

            // Verifica si hay filas en el layout para obtener el número de variables
            if (tableLayout != null && tableLayout.getChildCount() > 1) {
                // Suponiendo que la primera fila es el encabezado, el número de variables se basa en el número de columnas
                TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
                // El número de variables es igual al número de columnas - 1 (la última columna es la salida)
                return headerRow.getChildCount() - 1;
            } else {
                return 0; // Si no hay filas, no hay variables
            }
        }


        // Método para obtener las salidas de la tabla generada
        private String[] getOutputsFromTable() {
            int numRows = (int) Math.pow(2, getNumVariables());
            String[] outputs = new String[numRows];

            // Obtén el TableLayout desde el contenedor de la tabla
            TableLayout tableLayout = (TableLayout) tableContainer.getChildAt(0);

            for (int i = 1; i <= numRows; i++) {
                // Obtiene cada fila (empezando desde la segunda fila, ya que la primera es el encabezado)
                TableRow row = (TableRow) tableLayout.getChildAt(i);

                // Obtiene el EditText de la última columna de cada fila (donde se encuentra la salida)
                EditText outputCell = (EditText) row.getChildAt(row.getChildCount() - 1);

                // Convierte el valor ingresado en el EditText a string y lo almacena en el array de outputs
                String outputText = outputCell.getText().toString().toUpperCase();
                if (outputText.equals("0") || outputText.equals("1") || outputText.equals("X")) {
                    outputs[i - 1] = outputText;
                } else {
                    outputs[i - 1] = "0"; // Valor predeterminado si no se ingresó nada o es inválido
                }
            }

            return outputs;
        }



        // Mostrar un diálogo para ingresar la cantidad de variables
        private void showVariableInputDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ingrese cantidad de variables");

            // Campo de texto para ingresar el número de variables
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            // Botones de diálogo
            builder.setPositiveButton("OK", (dialog, which) -> {
                String inputText = input.getText().toString();
                if (!inputText.isEmpty()) {
                    int numVariables = Integer.parseInt(inputText);
                    generateTruthTable(numVariables);
                } else {
                    Toast.makeText(MainActivity.this, "Debe ingresar un número", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

            builder.show();
        }

        // Generar la tabla de verdad y mostrarla en un formato editable
        private void generateTruthTable(int numVariables) {
            tableContainer.removeAllViews(); // Limpiar cualquier tabla previa
            isTableVisible = true;
            generateTableButton.setText("Cancelar");

            // Ocultar el layout de bienvenida cuando se genera la tabla
            welcomeLayout.setVisibility(View.GONE);

            int numRows = (int) Math.pow(2, numVariables);
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setStretchAllColumns(true);
            tableLayout.setPadding(16, 16, 16, 16);
            tableLayout.setBackgroundResource(android.R.color.darker_gray);

            // Generar encabezado de la tabla (A, B, C, ..., F)
            TableRow headerRow = new TableRow(this);
            headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < numVariables; i++) {
                TextView header = createStyledTextView(String.valueOf((char) ('A' + i)));
                headerRow.addView(header);
            }
            TextView outputHeader = createStyledTextView("F");
            headerRow.addView(outputHeader);

            tableLayout.addView(headerRow);

            // Generar las combinaciones posibles y filas con campos editables para las salidas
            for (int i = 0; i < numRows; i++) {
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                for (int j = numVariables - 1; j >= 0; j--) {
                    TextView cell = new TextView(this);
                    cell.setText(String.valueOf((i / (int) Math.pow(2, j)) % 2));
                    cell.setPadding(16, 16, 16, 16);
                    cell.setGravity(Gravity.CENTER);
                    cell.setBackgroundResource(android.R.drawable.editbox_background);
                    row.addView(cell);
                }

                // Campo editable para la salida
                EditText outputCell = createStyledEditText();
                outputCell.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        checkIfAllFieldsFilled(tableLayout);
                    }
                });

                row.addView(outputCell);
                tableLayout.addView(row);
            }

            // Añadir la tabla generada al contenedor en la vista
            tableContainer.addView(tableLayout);
        }


        // Mostrar un diálogo de confirmación cuando se presiona "Cancelar"
        private void showCancelConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmación");
            builder.setMessage("¿Estás seguro de que deseas cancelar la tabla?");
            builder.setPositiveButton("Sí", (dialog, which) -> hideTable());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        }

        // Verificar si todos los campos de salida están llenos para habilitar el botón de "Generar Mapa"
        private void checkIfAllFieldsFilled(TableLayout tableLayout) {
            boolean allFilled = true;
            for (int i = 1; i < tableLayout.getChildCount(); i++) {
                TableRow row = (TableRow) tableLayout.getChildAt(i);
                EditText outputCell = (EditText) row.getChildAt(row.getChildCount() - 1);
                if (outputCell.getText().toString().isEmpty()) {
                    allFilled = false;
                    break;
                }
            }
            generateMapButton.setEnabled(allFilled);
        }

        // Ocultar la tabla y restaurar el estado del botón
        private void hideTable() {
            tableContainer.removeAllViews(); // Elimina la tabla generada
            isTableVisible = false;
            generateTableButton.setText("Generar Tabla");
            generateMapButton.setEnabled(false);

            // Mostrar el layout de bienvenida nuevamente
            welcomeLayout.setVisibility(View.VISIBLE);
        }


        // Crear TextView estilizado para las celdas del encabezado
        private TextView createStyledTextView(String text) {
            TextView textView = new TextView(this);
            textView.setText(text);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setAllCaps(true);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(16, 16, 16, 16);
            textView.setBackgroundResource(android.R.drawable.editbox_background);
            return textView;
        }

        // Crear EditText estilizado para las celdas de salida
        // Crear EditText estilizado para las celdas de salida
        private EditText createStyledEditText() {
            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setHint("0/1/X");
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1), createInputFilter()});
            editText.setGravity(Gravity.CENTER);
            editText.setPadding(16, 16, 16, 16);
            editText.setBackgroundResource(android.R.drawable.editbox_background);
            return editText;
        }

        // Filtro para permitir solo 0, 1 y X como entrada en los EditText
        private InputFilter createInputFilter() {
            return (source, start, end, dest, dstart, dend) -> {
                if (source.length() == 0) {
                    return null; // Permitir borrado
                }
                // Permitir solo "0", "1" o "X"
                if (source.toString().matches("[01Xx]")) {
                    return source.toString().toUpperCase(); // Convertir "x" a "X" si se ingresa en minúscula
                }
                return "";
            };
        }

    }
