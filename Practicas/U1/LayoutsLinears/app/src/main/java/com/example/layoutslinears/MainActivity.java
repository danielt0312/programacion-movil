package com.example.layoutslinears;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView l = findViewById(R.id.lv);

        ArrayList<String> lista = new ArrayList<String>(Arrays.asList("Item 1", "Item 2", "Item 3", "Item 4"));
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        l.setAdapter(adapter);
    }
}