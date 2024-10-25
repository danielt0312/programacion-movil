package com.example.canvasintermedio;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends Activity  {

	Button B1;
	MySurfaceView MSV;
	SurfaceHolder surfaceHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		B1 = (Button) findViewById(R.id.btn1);
		MSV = (MySurfaceView) findViewById(R.id.fuckingSurface);


		surfaceHolder = MSV.getHolder();
		//surfaceHolder.addCallback(this);

		//MSV.Arrancar();

		B1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					MSV.Arrancar();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
/*
	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

	}
	*/
}
