/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 */

package com.canvamastouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DragAndDropView extends SurfaceView implements SurfaceHolder.Callback {

	private DragAndDropThread thread;
	private ArrayList<Figura> figuras;
	private int figuraActiva;

	private int lap = 0;
	private int extra = 0;

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
		int x = getWidth();//OBTENER ANCHO
		int y = getHeight();//OBTENER ALTO

		figuras = new ArrayList<Figura>();
		figuras.add(new Circulo(id++,x/2,y/2,25, "BLUE"));
		figuras.add(new Rectangulo(id++,200,500,100,100));
		figuras.add(new Circulo(id++,x/2-(x/4),y/2+(y/4),25, "BLUE"));
		figuras.add(new Circulo(id++,x/2+(x/4),y/2+(y/4),25, "BLUE"));
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
	public void onDraw(final Canvas canvas) {

		lap++;//CONTADOR

		// Dibujar antes..


		int x = getWidth();//OBTENER ANCHO
		int y = getHeight();//OBTENER ALTO
		Paint paint = new Paint();
		//paint.setStyle(Paint.Style.FILL);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.WHITE);//FONDO
		canvas.drawPaint(paint);
		// Use Color.parseColor to define HTML colors
		paint.setColor(Color.BLACK);
		paint.setTextSize(25);
		//canvas.drawText(lap+"", 50, 50, paint);
		canvas.drawLine(0, (y/3)*2, x, (y/3)*2, paint);


		int m = x/10;
		int n = m;
		int k = 2*m;

		int l = y/3;
		int radius = k/2;
		int alt = (y/3)*2 - radius;

		paint.setColor(Color.BLUE);

		//ANIMACION INICIAL
		for(int i = 0; i <5; i++){
			//LINEAS
			paint.setColor(Color.BLUE);
			if(lap>=20 && i<=lap/20){
				paint.setStrokeWidth(0);
				canvas.drawText(i+"", n-8, l-20, paint);
				paint.setStrokeWidth(3);
				canvas.drawLine(n, (l*2)+(l/2), n, l, paint);
			}
			//CIRCULOS
			paint.setColor(Color.BLACK);
			if(lap<=99 && lap>=20 && i<lap/20){
				if(i==(lap/20)-1)
					paint.setColor(Color.RED);
				if(lap>=93)
					paint.setColor(Color.BLUE);
				canvas.drawCircle(n+radius, alt, radius, paint);
			}
			n+=k;
		}

		paint.setColor(Color.RED);
		Path path;
		int px,py;

		//SEGUNDA PARTE DE LA ANIMACION

		if(lap>=100){

			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.MAGENTA);
			path = new Path();//ROMBO POSICION 0
			path.setFillType(Path.FillType.EVEN_ODD);
			px = radius;
			py = (y/3)*2;
			path.moveTo(px,py);
			path.lineTo(px+15,py+15);
			path.lineTo(px,py+30);
			path.lineTo(px-15,py+15);
			path.lineTo(px,py);
			path.close();
			canvas.drawPath(path, paint);

			paint.setColor(Color.RED);

			if((lap-100)*6 <= (radius*2)*3.1416){
				px = radius+((lap-100)*6);
				paint.setStrokeWidth(5);
				canvas.drawLine(radius, (l*2), px, (l*2), paint);
				paint.setStrokeWidth(3);
			}
			else{
				extra++;
				paint.setStrokeWidth(5);
				px = (int)(radius+((radius*2)*3.1416));
				canvas.drawLine(radius, (l*2), px, (l*2), paint);
				paint.setStrokeWidth(2);
				canvas.drawLine(px, (l*2), px, l-20, paint);

				Bitmap bmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.pi);// IMAGEN PI
				canvas.drawBitmap(bmp, px-23, l, null);
				paint.setStrokeWidth(3);
				if(extra==60){
					extra = 0;
					lap = 0;
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////
			//RUEDA
			//canvas.save(Canvas.MATRIX_SAVE_FLAG);//RESPALDO DE LA ANIMACION ANTES DE ROTAR
			canvas.save();

			paint.setStyle(Paint.Style.STROKE);

			paint.setStrokeWidth(5);
			int z = radius+((lap-100)*6);

			//canvas.translate(z, (l*2-radius));
			canvas.rotate((float)((((lap-100)*6)*360/((radius*2)*3.1416))%360),z,(l*2-radius));


			paint.setColor(Color.GREEN);
			paint.setStrokeWidth(10);
			canvas.drawCircle(z, (l*2-radius), radius-8, paint);

			for(float i =0; i <=360; i+=360/7)
				canvas.drawLine((float)(z + (Math.sin(Math.toRadians(i)) * (radius-8))), (float)((l*2-radius)+ (Math.cos(Math.toRadians(i)) * (radius-8))), z, (l*2-radius), paint);

			paint.setStrokeWidth(3);

			paint.setColor(Color.BLUE);
			paint.setStyle(Paint.Style.FILL);
			path = new Path();
			path.setFillType(Path.FillType.EVEN_ODD);
			py = ((y/3)*2)-30;
			path.moveTo(z,py);
			path.lineTo(z+15,py+15);
			path.lineTo(z,py+30);
			path.lineTo(z-15,py+15);
			path.lineTo(z,py);
			path.close();
			canvas.drawPath(path, paint);

			canvas.restore();//RESTAURAR TRANSFORMACION

			path = new Path();//ROMBO RUEDA
			path.setFillType(Path.FillType.EVEN_ODD);

			paint.setColor(Color.GREEN);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(z, (l*2-radius), (int) (radius/2.3), paint);

			paint.setStrokeWidth(10);
			canvas.drawCircle(z, (l*2-radius), radius/5, paint);
			paint.setColor(Color.RED);
			paint.setStrokeWidth(5);

			//ENVOLTURA RUEDA
			for(int i = 0; i <= 360-((px-radius)*360/((radius*2)*3.1416)); i++){
				int sx = (int) (Math.sin(Math.toRadians(i)) * radius);
				int sy = (int) (Math.cos(Math.toRadians(i)) * radius);

				if(i==0)
					path.moveTo(z+sx,(l*2-radius)+sy);
				else
					path.lineTo(z+sx,(l*2-radius)+sy);
			}

			canvas.drawPath(path, paint);
			///////////////////////////////////////////////////////////////////////////////////////////

			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.RED);

			paint.setStrokeWidth(3);

			path = new Path();//ROMBO POSICION VARIANTE RECTA
			path.setFillType(Path.FillType.EVEN_ODD);
			py = (y/3)*2;
			path.moveTo(px,py);
			path.lineTo(px+15,py+15);
			path.lineTo(px,py+30);
			path.lineTo(px-15,py+15);
			path.lineTo(px,py);
			path.close();
			canvas.drawPath(path, paint);


		}

		// mitad de ancho
		int mitadAncho = canvas.getWidth() / 2;
		// mitad de alto
		int mitadAlto = canvas.getHeight() / 2;

		//Paint p = new Paint();
		paint.setAntiAlias(true);
		//canvas.drawColor(Color.WHITE);
		paint.setColor(Color.BLUE);
		paint.setTextSize(35);
		canvas.drawText("Triangulo Interactivo",mitadAncho-(3*(mitadAncho/4)), mitadAlto-(3*(mitadAlto/4)) , paint);

		//Primer Circulo, representa A
		Circulo c = (Circulo) figuras.get(0);
		if(figuraActiva == 0)
			paint.setColor(Color.RED);
		else
			paint.setColor(Color.GRAY);
		canvas.drawCircle(c.getX(), c.getY(), c.getRadio(), paint);
		paint.setTextSize(25);
		canvas.drawText("A", c.getX()+25, c.getY()-25, paint);
		/*
		Rectangulo r = (Rectangulo) figuras.get(1);
		p.setColor(Color.RED);
		canvas.drawRect(r.getX(), r.getY(), r.getX()+r.getAncho(), r.getY()+r.getAlto(), p);
		*/

		//Segundo circulo, representa B
		Circulo c2 = (Circulo) figuras.get(2);
		if(figuraActiva == 2)
			paint.setColor(Color.RED);
		else
			paint.setColor(Color.GRAY);
		canvas.drawCircle(c2.getX(), c2.getY(), c2.getRadio(), paint);
		canvas.drawText("B", c2.getX()+25, c2.getY()-25, paint);

		//Segundo circulo, representa C
		Circulo c22 = (Circulo) figuras.get(3);
		if(figuraActiva == 3)
			paint.setColor(Color.RED);
		else
			paint.setColor(Color.GRAY);
		canvas.drawCircle(c22.getX(), c22.getY(), c22.getRadio(), paint);
		canvas.drawText("C", c22.getX()+25, c22.getY()-25, paint);

		//Calcular pendientes
		float mAB, mBC, mCA;
		mAB = ( c2.getY() - c.getY() ) / ( c2.getX() - c.getX() );
		mBC = ( c22.getY() - c2.getY() ) / ( c22.getX() - c2.getX() );
		mCA = ( c.getY() - c22.getY() ) / ( c.getX() - c22.getX() );

		float tan_a1, tan_a2, tan_a3;
		double a1, a2, a3;
		// tan(α₁) = | (m(AB)-m(CA))/(1+m(AB).m(AC)) |
		tan_a1 = (mAB - mCA) / (1 + mAB * mCA);
		// tan(α₂) = | (m(AB)-m(BC))/(1+m(AB).m(BC)) |
		tan_a2 = (mAB - mBC) / (1 + mAB * mBC);
		// tan(α₃) = | (m(BC)-m(CA))/(1+m(BC).m(CA)) |
		tan_a3 = (mBC - mCA) / (1 + mBC * mCA);

		/*
		a1=(Math.cos(tan_a1))/(Math.sin(tan_a1));
		a2=(Math.cos(
		tan_a2))/(Math.sin(tan_a2));
		a3=(Math.cos(tan_a3))/(Math.sin(tan_a3));
		*/
		//a1 = Math.tanh(tan_a1);
		//a2 = Math.tanh(tan_a2);
		//	a3 = Math.tanh(tan_a3);

		float anguloRadianes1, anguloRadianes2, anguloRadianes3 ;
		anguloRadianes1 = (float)Math.atan(tan_a1);
		a1 = Math.toDegrees(anguloRadianes1);

		anguloRadianes2 = (float)Math.atan(tan_a2);
		a2 = Math.toDegrees(anguloRadianes2);

		anguloRadianes3 = (float)Math.atan(tan_a3);
		a3 = Math.toDegrees(anguloRadianes3);

		if(a1<0)
			a1 = a1*-1;
		if(a2<0)
			a2 = a2*-1;
		if(a3<0)
			a3 = a3*-1;

		DecimalFormat df = new DecimalFormat("#.##");
		//System.out.print(df.format(d));

		//System.out.println("Arco Tangente de " + tan_a1 + " = " + angulo + "º");

		paint.setColor(Color.BLACK);

		//canvas.drawText("mAB: "+mAB+" mBC: "+mBC+" mCA: "+mCA,mitadAncho-(3*(mitadAncho/4)), mitadAlto+(3*(mitadAlto/4)) , p);
		//canvas.drawText("tan_a: "+tan_a1+" tan_b: "+tan_a2+" tan_c: "+tan_a3,mitadAncho-(3*(mitadAncho/4)), mitadAlto+(3*(mitadAlto/4))+30 , p);
		canvas.drawText(""+df.format(a1)+ "º"+" + "+df.format(a2)+ "º"+" + "+df.format(a3)+ "º = 180°" ,mitadAncho-(3*(mitadAncho/4)), mitadAlto+(3*(mitadAlto/4)) , paint);

		//Dibujar el angulo en los puntos
		paint.setTextSize(20);
		canvas.drawText(df.format(a1)+"º", c.getX()-25, c.getY()+35, paint);
		canvas.drawText(df.format(a2)+"º", c2.getX()-25, c2.getY()+35, paint);
		canvas.drawText(df.format(a3)+"º", c22.getX()-25, c22.getY()+35, paint);

		//DIbujar las lineas que forman el triangulo
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		canvas.drawLine(c.getX(), c.getY(), c2.getX(), c2.getY(), paint);
		canvas.drawLine(c2.getX(), c2.getY(), c22.getX(), c22.getY(), paint);
		canvas.drawLine(c22.getX(), c22.getY(), c.getX(), c.getY(), paint);



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
					float distanciaX = x - c.getX();
					float distanciaY = y - c.getY();
					if(Math.pow(c.getRadio(), 2) > (Math.pow(distanciaX, 2) + Math.pow(distanciaY, 2))) {
						figuraActiva = c.getId();
						//break; check blog entry for explanation on why this is commented
					}
				} else {	// in this context, only instanceof Rectangulo
					/*Rectangulo r = (Rectangulo) f;
					if(x > r.getX() && x < r.getX()+r.getAncho() && y > r.getY() && y < r.getY()+r.getAlto()) {
						figuraActiva = r.getId();
						//break; check blog entry for explanation on why this is commented
					}*/
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(figuraActiva != -1) {
				if(figuras.get(figuraActiva) instanceof Circulo) {
					figuras.get(figuraActiva).setX(x);
					figuras.get(figuraActiva).setY(y);
				} else {	// in this context, only instanceof Rectangulo
					/*Rectangulo r = (Rectangulo) figuras.get(figuraActiva);
					r.setX(x - r.getAncho()/2);
					r.setY(y - r.getAlto()/2);*/
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