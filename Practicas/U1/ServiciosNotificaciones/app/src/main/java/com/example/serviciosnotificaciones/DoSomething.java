package com.example.serviciosnotificaciones;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class DoSomething extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_do_something);

        EditText ET1= findViewById(R.id.ET1);
        EditText ET2= findViewById(R.id.ET2);
        EditText ET3= findViewById(R.id.ET3);
        EditText ET4= findViewById(R.id.ET4);
        Bundle extras = getIntent().getExtras();
        String userName;
        //String P1 = savedInstanceState.getString("P1");

        if (extras!=null)
        {
            Integer I1 = extras.getInt("I1");
            String P1 = extras.getString("P1");
            String P2 = extras.getString("P2");
            String P3 = extras.getString("P3");
            Toast.makeText(getApplicationContext(),
                    "Mesanje Recibido: "+
                    "\n"+I1+
                    "\n"+P1+
                    "\n"+P2+
                    "\n"+P3,
                    Toast.LENGTH_LONG).show();

            ET1.setText(""+I1);
            ET2.setText(""+P1);
            ET3.setText(""+P2);
            ET4.setText(""+P3);

        }
        else {
            Toast.makeText(getApplicationContext(),
                    "NO HAGAS NI MADRES ",
                    Toast.LENGTH_LONG).show();
        }


	}

}
