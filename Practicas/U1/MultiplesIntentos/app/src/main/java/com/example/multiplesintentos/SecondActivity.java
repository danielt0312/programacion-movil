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

public class SecondActivity extends AppCompatActivity {
    private EditText etName_AS;
    private EditText etAge_AS;
    private EditText etHeight_AS;
    private EditText etWorkingStatus_AS;
    private Button B1_AS;

    String name;
    int age;
    double height;
    boolean workingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.second_activity);
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
        etName_AS = findViewById(R.id.Et1_AS);
        etName_AS.setText(name);

        etAge_AS = findViewById(R.id.Et2_AS);
        etAge_AS.setText(""+age);

        etHeight_AS = findViewById(R.id.Et3_AS);
        etHeight_AS.setText(""+height);

        etWorkingStatus_AS = findViewById(R.id.Et4_AS);
        etWorkingStatus_AS.setText(""+status);

        B1_AS = findViewById(R.id.Bt1_AS);
        B1_AS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameTemp = etName_AS.getText().toString();
                int ageTemp = Integer.parseInt(etAge_AS.getText().toString());
                double heightTemp = Double.parseDouble(etHeight_AS.getText().toString());
                boolean workingStatusTemp = getBooleanValue(etWorkingStatus_AS.getText().toString());


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