package com.iti;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button BotonHola;
    EditText EditTextUsuario;
    TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BotonHola = findViewById(R.id.boton1);
        EditTextUsuario = findViewById(R.id.editText1);
        temp = findViewById(R.id.textViewLista);
        temp.setText("*******");

        BotonHola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp.append("\n" + EditTextUsuario.getText().toString());
                EditTextUsuario.setText("");

                Toast.makeText(
                        getApplicationContext(),
                        "Hola " + EditTextUsuario.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}