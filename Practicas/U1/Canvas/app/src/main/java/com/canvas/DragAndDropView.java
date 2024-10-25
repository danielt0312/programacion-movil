/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 */

package com.canvas;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//public class DragAndDropView extends SurfaceView implements SurfaceHolder.Callback {
public class DragAndDropView extends SurfaceView implements SurfaceHolder.Callback {

	private DragAndDropThread thread;
	private ArrayList<Figura> figuras;
	private int figuraActiva;
	int[] colores = {Color.RED, Color.GREEN, Color.GRAY, Color.BLACK, Color.BLUE, Color.DKGRAY, Color.LTGRAY, Color};
	
	public DragAndDropView(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// nothing here
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		int id = 0;
		figuras = new ArrayList<Figura>();

		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				int color = Math.random() * 10;
				figuras.add(new Rectangulo(i++, (int) (Math.random() * 1000)));
			}
		}

		figuras.add(new Circulo(id++,200,200,100));
		figuras.add(new Rectangulo(id++,200,500,200,200));
		figuras.add(new Rectangulo(id++, , (int) (Math.random() * 1000), (int) (Math.random() * 1000), (int) (Math.random() * 1000)));
		figuras.add(new Circulo(id++,200,200, (int) (Math.random() * 100)));

		figuraActiva = -1;
		
		thread = new DragAndDropThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
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
	
	@Override
	public void onDraw(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);

		canvas.drawColor(Color.WHITE);

		for (Figura f : figuras) {
			if (f.getClass() == Circulo.class) {
				Circulo c = (Circulo) f;
				p.setColor(f.getColor());
				canvas.drawCircle(c.getX(), c.getY(), c.getRadio(), p);
			}
		}

		Rectangulo r = (Rectangulo) figuras.get(1);
		p.setColor(Color.RED);
		canvas.drawRect(r.getX(), r.getY(), r.getX()+r.getAncho(), r.getY()+r.getAlto(), p);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			for(Figura f : figuras) {
				if(f instanceof Circulo) {
					Circulo c = (Circulo) f;
					int distanciaX = x - c.getX();
					int distanciaY = y - c.getY();
					if(Math.pow(c.getRadio(), 2) > (Math.pow(distanciaX, 2) + Math.pow(distanciaY, 2))) {
						figuraActiva = c.getId();
						//break; check blog entry for explanation on why this is commented
					}
				} else {	// in this context, only instanceof Rectangulo
					Rectangulo r = (Rectangulo) f;
					if(x > r.getX() && x < r.getX()+r.getAncho() && y > r.getY() && y < r.getY()+r.getAlto()) {
						figuraActiva = r.getId();
						//break; check blog entry for explanation on why this is commented
					}
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(figuraActiva != -1) {
				if(figuras.get(figuraActiva) instanceof Circulo) {
					figuras.get(figuraActiva).setX(x);
					figuras.get(figuraActiva).setY(y);
				} else {	// in this context, only instanceof Rectangulo
					Rectangulo r = (Rectangulo) figuras.get(figuraActiva);
					r.setX(x - r.getAncho()/2);
					r.setY(y - r.getAlto()/2);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			figuraActiva = -1;
			break;
		}
		
		return true;
	}
}