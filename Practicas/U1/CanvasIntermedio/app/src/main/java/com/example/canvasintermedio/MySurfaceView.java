package com.example.canvasintermedio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MySurfaceView extends SurfaceView {
/*
    private SurfaceHolder surfaceHolder;
    private Bitmap bmpIcon;
    private MyThread myThread;
    int xPos = 0;
    int yPos = 0;
    int deltaX = 5;
    int deltaY = 5;
    int iconWidth;
    int iconHeight;
	int Estado=0;

	public MySurfaceView(Context context) {
		super(context);
		init();
	}

	public MySurfaceView(Context context, 
			AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MySurfaceView(Context context, 
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void Arrancar () {

		Estado=(Estado ==1 ? 0 : 1);
	}

	
	private void init(){
		
		myThread = new MyThread(this);
		
		surfaceHolder = getHolder();
		bmpIcon = BitmapFactory.decodeResource(getResources(), 
				R.mipmap.ic_launcher);

		iconWidth = bmpIcon.getWidth();
		iconHeight = bmpIcon.getHeight();
		
		surfaceHolder.addCallback(new SurfaceHolder.Callback(){

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				myThread.setRunning(true);
				myThread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, 
					int format, int width, int height) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				boolean retry = true;
                myThread.setRunning(false);
                while (retry) {
                       try {
                             myThread.join();
                             retry = false;
                       } catch (InterruptedException e) {
                       }
                }
			}});
	}

	protected void drawSomething(Canvas canvas) {


        canvas.drawBitmap(bmpIcon, 
        		getWidth()/2, getHeight()/2, null);
        
        xPos += deltaX;
        if(deltaX > 0){
        	if(xPos >= getWidth() - iconWidth){
            	deltaX *= -1;
            }
        }else{
        	if(xPos <= 0){
            	deltaX *= -1;
            }
        }
        
        yPos += deltaY;
        if(deltaY > 0){
        	if(yPos >= getHeight() - iconHeight){
            	deltaY *= -1;
            }
        }else{
        	if(yPos <= 0){
            	deltaY *= -1;
            }
        }

		if (Estado==0)
        	canvas.drawColor(Color.BLACK);
		else
			canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(bmpIcon, 
        		xPos, yPos, null);

	}
*/

/*	private ArrayList<Figura> figuras;
	private int figuraActiva;*/

	private SurfaceHolder holder;
	private MyThread  gameLoopThread;
	private int x = 0;
	private int xSpeed = 1;
	private int lap = 0;
	private int extra = 0;
	private int Estado=0;
	MediaPlayer player;


	public MySurfaceView(Context context) {
		super(context);
		player = new MediaPlayer();
		init();
	}

	public MySurfaceView(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		player = new MediaPlayer();
		init();
	}

	public MySurfaceView(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		player = new MediaPlayer();
		init();
	}


	private void init(){
		// Del Proyecto de Juliana
/*		int id = 0;
		figuras = new ArrayList<Figura>();
		figuras.add(new Circulo(id++,240,270,25, "BLUE"));
		figuras.add(new Rectangulo(id++,200,500,100,100));
		figuras.add(new Circulo(id++,120,540,25, "BLUE"));
		figuras.add(new Circulo(id++,360,540,25, "BLUE"));
		figuraActiva = -1;*/

		// Resto

		gameLoopThread = new MyThread(this);
		holder = getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder surfaceHolder) {
				gameLoopThread.setRunning(true);
				gameLoopThread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
				boolean retry = true;
				gameLoopThread.setRunning(false);
				while (retry) {
					try {
						gameLoopThread.join();
						retry = false;
					} catch (InterruptedException e) {
					}
				}
			}
		});
	}



	public void Arrancar () throws IOException {
		Estado=(Estado ==1 ? 0 : 1);
		/*player.setAudioStreamType(AudioManager.STREAM_ALARM);
		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		player.setDataSource(getContext(), uri);
		player.prepare();*/
	}

	//@Override
	///protected void onDraw(Canvas canvas) {//DIBUJAR EN PANTALLA
	protected void drawSomething(Canvas canvas) {//DIBUJAR EN PANTALLA


		//
		// Proyecto Gildardo...
		//

		lap++;//CONTADOR

		int x = getWidth();//OBTENER ANCHO
		int y = getHeight();//OBTENER ALTO
		Paint paint = new Paint();
		//paint.setStyle(Paint.Style.FILL);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		// Definir color de fondo
		switch (Estado) {
			case 0: paint.setColor(Color.WHITE); break;
			case 1: paint.setColor(Color.MAGENTA); break; }

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

		if(lap>=100) {

			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.MAGENTA);
			path = new Path();//ROMBO POSICION 0
			path.setFillType(Path.FillType.EVEN_ODD);
			px = radius;
			py = (y / 3) * 2;
			path.moveTo(px, py);
			path.lineTo(px + 15, py + 15);
			path.lineTo(px, py + 30);
			path.lineTo(px - 15, py + 15);
			path.lineTo(px, py);
			path.close();
			canvas.drawPath(path, paint);

			paint.setColor(Color.RED);

			if ((lap - 100) * 6 <= (radius * 2) * 3.1416) {
				px = radius + ((lap - 100) * 6);
				paint.setStrokeWidth(5);
				canvas.drawLine(radius, (l * 2), px, (l * 2), paint);
				paint.setStrokeWidth(3);
			} else {
				extra++;
				paint.setStrokeWidth(5);
				px = (int) (radius + ((radius * 2) * 3.1416));
				canvas.drawLine(radius, (l * 2), px, (l * 2), paint);
				paint.setStrokeWidth(2);
				canvas.drawLine(px, (l * 2), px, l - 20, paint);

				Bitmap bmp = BitmapFactory.decodeResource(getResources(),
						R.mipmap.ic_launcher_2);// IMAGEN PI
				canvas.drawBitmap(bmp, px - 23, l, null);
				paint.setStrokeWidth(3);
				if (extra == 60) {
					extra = 0;
					lap = 0;
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////
			//RUEDA
			canvas.save();//RESPALDO DE LA ANIMACION ANTES DE ROTAR

			paint.setStyle(Paint.Style.STROKE);

			paint.setStrokeWidth(5);
			int z = radius + ((lap - 100) * 6);

			//canvas.translate(z, (l*2-radius));
			canvas.rotate((float) ((((lap - 100) * 6) * 360 / ((radius * 2) * 3.1416)) % 360), z, (l * 2 - radius));


			paint.setColor(Color.GREEN);
			paint.setStrokeWidth(10);
			canvas.drawCircle(z, (l * 2 - radius), radius - 8, paint);

			for (float i = 0; i <= 360; i += 360 / 7)
				canvas.drawLine((float) (z + (Math.sin(Math.toRadians(i)) * (radius - 8))), (float) ((l * 2 - radius) + (Math.cos(Math.toRadians(i)) * (radius - 8))), z, (l * 2 - radius), paint);

			paint.setStrokeWidth(3);

			paint.setColor(Color.BLUE);
			paint.setStyle(Paint.Style.FILL);
			path = new Path();
			path.setFillType(Path.FillType.EVEN_ODD);
			py = ((y / 3) * 2) - 30;
			path.moveTo(z, py);
			path.lineTo(z + 15, py + 15);
			path.lineTo(z, py + 30);
			path.lineTo(z - 15, py + 15);
			path.lineTo(z, py);
			path.close();
			canvas.drawPath(path, paint);

			canvas.restore();//RESTAURAR TRANSFORMACION

			path = new Path();//ROMBO RUEDA
			path.setFillType(Path.FillType.EVEN_ODD);

			paint.setColor(Color.GREEN);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(z, (l * 2 - radius), (int) (radius / 2.3), paint);

			paint.setStrokeWidth(10);
			canvas.drawCircle(z, (l * 2 - radius), radius / 5, paint);
			paint.setColor(Color.RED);
			paint.setStrokeWidth(5);

			//ENVOLTURA RUEDA
			for (int i = 0; i <= 360 - ((px - radius) * 360 / ((radius * 2) * 3.1416)); i++) {
				int sx = (int) (Math.sin(Math.toRadians(i)) * radius);
				int sy = (int) (Math.cos(Math.toRadians(i)) * radius);

				if (i == 0)
					path.moveTo(z + sx, (l * 2 - radius) + sy);
				else
					path.lineTo(z + sx, (l * 2 - radius) + sy);
			}

			canvas.drawPath(path, paint);
			///////////////////////////////////////////////////////////////////////////////////////////

			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.RED);

			paint.setStrokeWidth(3);

			path = new Path();//ROMBO POSICION VARIANTE RECTA
			path.setFillType(Path.FillType.EVEN_ODD);
			py = (y / 3) * 2;
			path.moveTo(px, py);
			path.lineTo(px + 15, py + 15);
			path.lineTo(px, py + 30);
			path.lineTo(px - 15, py + 15);
			path.lineTo(px, py);
			path.close();
			canvas.drawPath(path, paint);


		}

/*
		//
		// Proyecto Yuliana...
		//

			// mitad de ancho
			int mitadAncho = canvas.getWidth() / 2;
			// mitad de alto
			int mitadAlto = canvas.getHeight() / 2;

			//Paint p = new Paint();
			paint.setAntiAlias(true);
			canvas.drawColor(Color.WHITE);
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
*/

	}

}
