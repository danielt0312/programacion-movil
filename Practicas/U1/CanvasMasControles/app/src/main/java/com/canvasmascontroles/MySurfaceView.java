package com.canvasmascontroles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private MySurfaceThread thread;
	int ColorDibujo;
	private Paint paint;// = new Paint(Paint.ANTI_ALIAS_FLAG);

	private float initX, initY, radius;
	private boolean drawing = false;
	private boolean limpiar;
	private int Conteo;

	@Override
	//protected void onDraw(Canvas canvas) {
	public void onDraw(Canvas canvas) {
		// EL 2 DEL if es un "numero del demonio" => PODRÍA NO FUNCIONAR ASI TODOS LOS DISPOSITIVOS
		// POR LO MENOS SE DEBE BORRAR 2 VECES PARA GARANTIZAR (POR LO MENOS EN EL DISPOSITIVO DONDE SE HIZO LA PRUEBA)

		if (limpiar) {
			canvas.drawColor(Color.LTGRAY);
			if (Conteo<2) {
				Conteo++;
			} else {
				Conteo = 0;
				limpiar=false;
			}
		}
		// TODO Auto-generated method stub
		// super.onDraw(canvas);
		if (drawing) {
			canvas.drawCircle(initX, initY, radius, paint);
		}


	}

	public void CambiarColor () {
		if (ColorDibujo==4)
			ColorDibujo=1;
		else
			ColorDibujo++;

		switch (ColorDibujo) {
			case 1:  paint.setColor(Color.RED); break;
			case 2:  paint.setColor(Color.BLUE); break;
			case 3:  paint.setColor(Color.MAGENTA); break;
			case 4:  paint.setColor(Color.GREEN); break;
			default: paint.setColor(Color.BLACK); break;
		}


	}
	public void Limpiar () {
		limpiar=true;
		Conteo=0;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// return super.onTouchEvent(event);
		int action = event.getAction();
		if (action == MotionEvent.ACTION_MOVE) {
			float x = event.getX();
			float y = event.getY();
			radius = (float) Math.sqrt(Math.pow(x - initX, 2)
					+ Math.pow(y - initY, 2));
		} else if (action == MotionEvent.ACTION_DOWN) {
			initX = event.getX();
			initY = event.getY();
			radius = 1;
			drawing = true;
		} else if (action == MotionEvent.ACTION_UP) {
			drawing = false;
		}

		return true;
	}

	public MySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//this.setBackgroundColor(Color.RED);
		init();

	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//this.setBackgroundColor(Color.RED);
		init();

	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		//this.setBackgroundColor(Color.RED);
		init();

	}

	private void init() {
		getHolder().addCallback(this);
		limpiar = false;
		Conteo = 0;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		ColorDibujo = 1;

		setFocusable(true); // make sure we get key events



		paint.setStyle(Paint.Style.STROKE); // Da el efecto de CIRCULOS CONCENTRICOS
		paint.setStrokeWidth(3);
		paint.setColor(Color.RED);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
							   int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		ColorDibujo=1;
		thread = new MySurfaceThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
