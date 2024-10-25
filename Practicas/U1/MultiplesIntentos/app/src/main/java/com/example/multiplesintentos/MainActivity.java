/*
https://naveedshaikh1599.medium.com/how-to-pass-data-between-two-different-activities-using-intent-in-android-517823743e20
 */
package com.example.multiplesintentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etAge;
    private EditText etHeight;
    private EditText etWorkingStatus;
    private Button B1, B2;
    // El valor de esta variable es lo de menos...
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 69;
    private static final int TERCER_ACTIVITY_REQUEST_CODE = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
    }

    private void initView() {
        etName = findViewById(R.id.Et1);
        etName.setText("");
        etAge = findViewById(R.id.Et2);
        etAge.setText("5");
        etHeight = findViewById(R.id.Et3);
        etHeight.setText("1.5");
        etWorkingStatus = findViewById(R.id.Et4);
        etWorkingStatus.setText("false");
        B1 = findViewById(R.id.Bt1);
        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get text to pass
                String name = etName.getText().toString();
                int age = Integer.parseInt(etAge.getText().toString());
                double height = Double.parseDouble(etHeight.getText().toString());
                boolean workingStatus = getBooleanValue(etWorkingStatus.getText().toString());
                // <activity android:name=".SecondActivity"/>
                //Start the Second Activity
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("Age", age);
                intent.putExtra("Height", height);
                intent.putExtra("Working Status", workingStatus);
                //startActivity(intent);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });
        B2 = findViewById(R.id.Bt2);
        B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get text to pass
                String name = etName.getText().toString();
                int age = Integer.parseInt(etAge.getText().toString());
                double height = Double.parseDouble(etHeight.getText().toString());
                boolean workingStatus = getBooleanValue(etWorkingStatus.getText().toString());
                // <activity android:name=".SecondActivity"/>
                //Start the Second Activity
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("Age", age);
                intent.putExtra("Height", height);
                intent.putExtra("Working Status", workingStatus);
                //startActivity(intent);
                startActivityForResult(intent, TERCER_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private boolean getBooleanValue(String value) {
        if(value.equals("Yes")){
            return true;
        }else {
            return false;
        }
    }
    // This method is called when the Second Activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the Second Activity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // get Text from Intent
                Bundle extras = data.getExtras();

                String resultadoActividad = extras.getString("Resultado");
                Toast.makeText(getApplicationContext(),"Cadena Regresada: "+resultadoActividad,Toast.LENGTH_SHORT).show();
            }
        }
        // check that it is the Second Activity with an OK result
        if (requestCode == TERCER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // get Text from Intent
                Bundle extras = data.getExtras();

                String resultadoActividad = extras.getString("Resultado");
                Toast.makeText(getApplicationContext(),"Cadena Regresada: "+resultadoActividad,Toast.LENGTH_SHORT).show();
            }
        }
    }

}