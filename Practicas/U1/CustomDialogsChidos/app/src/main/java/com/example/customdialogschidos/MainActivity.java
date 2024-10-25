package com.example.customdialogschidos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button B1, B2;
    Context CX;
    ArrayList<String> ListaConceptos; // = new ArrayList<User>();
    ArrayAdapter<String> AdapterListaConceptos;

    ArrayList<EstudiantesGueros> ListaEG;
    ArrayAdapter<EstudiantesGueros> adapter;
    ListView LV1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListaConceptos= new ArrayList<String>();
        ListaConceptos.add("Primero");
        ListaConceptos.add("Segundo");
        ListaConceptos.add("Tercero");
        CX = this;

        LV1 = findViewById(R.id.lvwAlumnos);
        ListaEG = new ArrayList<>();
        ListaEG.add(new EstudiantesGueros("Torres","Colorado","Daniel"));
        ListaEG.add(new EstudiantesGueros("Nuno","Maganda","Marco"));

        adapter = new ArrayAdapter<EstudiantesGueros>(getApplication(),android.R.layout.simple_list_item_1, ListaEG);
        LV1.setAdapter(adapter);

        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView text1, text2, text3;
                EditText ETD1, ETD2, ETD3;
                // custom dialog
                final Dialog dialog = new Dialog(CX);
                dialog.setContentView(R.layout.info);
                dialog.setTitle("Modificar/eliminar");

                // set the custom dialog components - text, image and button
                text1 = dialog.findViewById(R.id.txtNombre);
                text1.setText("Nombre");

                text2 = dialog.findViewById(R.id.txtAp);
                text2.setText("AP");

                text3 = dialog.findViewById(R.id.txtAm);
                text3.setText("AM");

                ETD1 = (EditText) dialog.findViewById(R.id.etdNombre);
                ETD1.setText(ListaEG.get(i).Nombre);
                ETD2 = dialog.findViewById(R.id.etdAp);
                ETD2.setText(ListaEG.get(i).ApellidoPaterno);
                ETD3 = dialog.findViewById(R.id.etdAm);
                ETD3.setText(ListaEG.get(i).ApellidoMaterno);

                Button dialogButton1 = (Button) dialog.findViewById(R.id.btnChange);
                // if button is clicked, close the custom dialog
                dialogButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListaEG.set(i, new EstudiantesGueros(ETD2.getText().toString(), ETD3.getText().toString(), ETD1.getText().toString()));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                Button dialogButton2 = (Button) dialog.findViewById(R.id.btnDelete);
                // if button is clicked, close the custom dialog
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListaEG.remove(i);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        B1 = (Button) findViewById (R.id.button1);
        B2 = (Button) findViewById (R.id.button2);

        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CX);
                builder.setMessage("Selecciona una opción");
                builder.setCancelable(false);
                // Add the buttons
                builder.setPositiveButton("Matutino", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Toast.makeText(CX,"Seleccionaste Excelente",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("Vespertino", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(CX,"Seleccionaste ser Mediocre",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                // Set other dialog properties

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                // Mostrar el dialogl
                dialog.show();


            }
        });

        B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text1, text2, text3;
                EditText ETD1, ETD2, ETD3;
                // custom dialog
                final Dialog dialog = new Dialog(CX);
                dialog.setContentView(R.layout.info);
                dialog.setTitle("Agregar");

                // set the custom dialog components - text, image and button
                text1 = dialog.findViewById(R.id.txtNombre);
                text1.setText("Nombre");

                text2 = dialog.findViewById(R.id.txtAp);
                text2.setText("AP");

                text3 = dialog.findViewById(R.id.txtAm);
                text3.setText("AM");

                ETD1 = dialog.findViewById(R.id.etdNombre);
                ETD2 = dialog.findViewById(R.id.etdAp);
                ETD3 = dialog.findViewById(R.id.etdAm);

                Button dialogButton1 = (Button) dialog.findViewById(R.id.btnDelete);
                dialogButton1.setText("CANCEL");

                Button dialogButton2 = (Button) dialog.findViewById(R.id.btnChange);
                dialogButton2.setText("ADD");
                // if button is clicked, close the custom dialog
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListaEG.add(new EstudiantesGueros(ETD2.getText().toString(), ETD3.getText().toString(), ETD1.getText().toString()));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }
}
