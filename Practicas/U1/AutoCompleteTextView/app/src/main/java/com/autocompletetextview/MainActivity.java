package com.autocompletetextview;
/* Fuente:
http://javatechig.com/android/android-textwatcher-example

 */

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CheckBox CB1;
    private EditText passwordEditText;
    private TextView textView;
    private AutoCompleteTextView ACtextView;
    private Button B1;
    Button B2;
    ImageView IM1;
    RadioGroup radioGroup;

    ArrayAdapter<String> adapter;
    List<String> lista;
    ListView LV1;

    private final Button.OnClickListener BL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "BOTON ", Toast.LENGTH_LONG).show();
        }
    };

    private final TextWatcher passwordWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textView.setVisibility(View.VISIBLE);
        }

        public void afterTextChanged(Editable s) {
//            if (s.length() == 0) {
                //textView.setVisibility(View.GONE);
//            } else{
                textView.setText("Ingresaste : " + passwordEditText.getText());
//            }
        }
    };

    private final TextWatcher colorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textView.setVisibility(View.VISIBLE);
            switch (ACtextView.getText().toString()) {
                case "Red":   Toast.makeText(getApplicationContext(), "ROJO: ", Toast.LENGTH_LONG).show();  B1.setBackgroundColor(Color.RED); break;
                case "Blue":  Toast.makeText(getApplicationContext(), "AZUL: ", Toast.LENGTH_LONG).show();  B1.setBackgroundColor(Color.BLUE);  break;
                case "Green": Toast.makeText(getApplicationContext(), "VERDE: ", Toast.LENGTH_LONG).show(); B1.setBackgroundColor(Color.GREEN); break;
                case "Purple": Toast.makeText(getApplicationContext(), "PURPURA: ", Toast.LENGTH_LONG).show(); B1.setBackgroundColor(Color.MAGENTA); break;
                case "White": Toast.makeText(getApplicationContext(), "BLANCO: ", Toast.LENGTH_LONG).show(); B1.setBackgroundColor(Color.WHITE); break;
                case "Grey": Toast.makeText(getApplicationContext(), "GRIS: ", Toast.LENGTH_LONG).show(); B1.setBackgroundColor(Color.GRAY); break;
                case "Orange": Toast.makeText(getApplicationContext(), "ORANGE: ", Toast.LENGTH_LONG).show(); B1.setBackgroundColor(Color.rgb(255, 69, 0)); break;
                case "Yellow": Toast.makeText(getApplicationContext(), "AMARILLO: ", Toast.LENGTH_LONG).show(); B1.setBackgroundColor(Color.YELLOW); break;
                case "Black": Toast.makeText(getApplicationContext(), "Negro: ", Toast.LENGTH_LONG).show(); B1.setBackgroundColor(Color.BLACK); break;
            }
        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                //textView.setVisibility(View.GONE);
            } else {
                //textView.setText("You have entered : " + passwordEditText.getText());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        B1 = (Button) findViewById(R.id.button1);
        B1.setOnClickListener(BL);

        CB1 = (CheckBox) findViewById(R.id.checkBox);
        CB1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String C = (b ? "Verdadero" : "Falso" );
                Toast.makeText(getApplicationContext(), C, Toast.LENGTH_SHORT).show();
            }
        });

        B2 = findViewById(R.id.button2);
        B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Estatus del Check: "+CB1.isChecked(),
                        Toast.LENGTH_LONG).show();
            }
        });

        IM1= (ImageView) findViewById(R.id.imageView);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton)findViewById(checkedId);
                Toast.makeText(getApplicationContext(), rb.getText(), Toast.LENGTH_SHORT).show();

                switch (rb.getText().toString()) {
                    case "Opcion 1": IM1.setImageResource(R.drawable.logo_sep); break;
                    case "Opcion 2": IM1.setImageResource(R.drawable.logo_tam); break;
                    case "Opcion 3": IM1.setImageResource(R.drawable.logo_direccion); break;
                    default: break;
                }
            }
        });

        LV1 = (ListView) findViewById(R.id.listView);

        lista = new ArrayList<String>();
        lista.add("Programacion");
        lista.add("Matematicas");
        lista.add("Calculo");
        lista.add("Algebra");
        lista.add("Ing. Software");

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, lista);
        LV1.setAdapter(adapter);

        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText()+"- Posicion: "+ i, Toast.LENGTH_SHORT).show();
            }
        });

        ACtextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        String colors[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, colors);
        ACtextView.setAdapter(adapter);

        /* Initializing views */
        passwordEditText = (EditText) findViewById(R.id.password);
        textView = (TextView) findViewById(R.id.passwordHint);
        //textView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);

        /* Set Text Watcher listener */
        passwordEditText.addTextChangedListener(passwordWatcher);
        ACtextView.addTextChangedListener(colorWatcher);
    }
}
