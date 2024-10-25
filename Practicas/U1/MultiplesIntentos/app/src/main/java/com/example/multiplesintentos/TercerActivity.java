package com.example.multiplesintentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TercerActivity extends AppCompatActivity {
    private EditText etName_AT;
    private EditText etAge_AT;
    private EditText etHeight_AT;
    private EditText etWorkingStatus_AT;
    private Button B1_AT;

    String name;
    int age;
    double height;
    boolean workingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tercer_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Get Text from MainActivity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        name = extras.getString("Name");
        age = extras.getInt("Age");
        height = extras.getDouble("Height");
        workingStatus = extras.getBoolean("Working Status");

        Toast.makeText(getApplicationContext(),"Datos Recibidos" + name+","+age+","+height+","+workingStatus,Toast.LENGTH_SHORT).show();
        initView(name,age,height,workingStatus);
    }

    private void initView(String name, int age, double height, boolean status) {
        etName_AT = findViewById(R.id.Et1_AT);
        etName_AT.setText(name);

        etAge_AT = findViewById(R.id.Et2_AT);
        etAge_AT.setText(""+age);

        etHeight_AT = findViewById(R.id.Et3_AT);
        etHeight_AT.setText(""+height);

        etWorkingStatus_AT = findViewById(R.id.Et4_AT);
        etWorkingStatus_AT.setText(""+status);

        B1_AT = findViewById(R.id.BtTA);
        B1_AT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameTemp = etName_AT.getText().toString();
                int ageTemp = Integer.parseInt(etAge_AT.getText().toString());
                double heightTemp = Double.parseDouble(etHeight_AT.getText().toString());
                boolean workingStatusTemp = getBooleanValue(etWorkingStatus_AT.getText().toString());


                Intent intent = new Intent();
                intent.putExtra("Resultado", nameTemp+","+ageTemp+","+heightTemp+","+workingStatusTemp);
                setResult(RESULT_OK, intent);
                finish();
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


}