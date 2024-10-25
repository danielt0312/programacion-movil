/*
Proyecto Original
https://github.com/somil55/Android-Continuous-SpeechRecognition
** Se realizaron algunos cambios ...
 */


package com.example.reconocimientodevoz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Intent recognizerIntent;
    private SpeechRecognizer speech = null;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private ProgressBar progressBar;
    TextView TV1;
    Button B1;
    EditText ET1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ET1 = findViewById(R.id.ET1);
        ET1.setMovementMethod(new ScrollingMovementMethod());
        TV1 = findViewById(R.id.TV1);
        TV1.setMovementMethod(new ScrollingMovementMethod());
        B1 = findViewById(R.id.BT1);
        progressBar =  findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        //progressBar.setIndeterminate(true);

        // check for permission
//        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
//            return;
//        }

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech = SpeechRecognizer.createSpeechRecognizer(this);

        if(SpeechRecognizer.isRecognitionAvailable(this))
            Toast.makeText(getApplicationContext(),"Listo",Toast.LENGTH_LONG).show();
        else
            finish();

        speech.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                TV1.append("Iniciando\n");
                ET1.setEnabled(false);
                progressBar.setIndeterminate(false);
                progressBar.setMax(10);

            }

            @Override
            public void onRmsChanged(float v) {
                progressBar.setProgress((int) v);
            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                TV1.append("Terminando\n");
                progressBar.setIndeterminate(true);
                speech.stopListening();
                ET1.setEnabled(true);
                B1.setEnabled(true);
            }

            @Override
            public void onError(int i) {
                TV1.append("Error\n");

            }

            @Override
            public void onResults(Bundle bundle) {
                progressBar.setMax(0);
                TV1.append("Resultados\n");
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String text = "";
                int i=0;
                for (String result : matches) {
                    // Solamente agrega el resultado 0
                    if (i==0) ET1.append(result);
                    text += result + "\n";
                    i++;
                }


                //returnedText.setText(text);
                //Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
                //speech.startListening(recognizerIntent);
                progressBar.setIndeterminate(false);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speech.startListening(recognizerIntent);
                B1.setEnabled(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent);
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                        .LENGTH_SHORT).show();
                finish();
            }
        }
    }
}