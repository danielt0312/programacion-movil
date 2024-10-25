package com.z_iti_271304_u2_torres_colorado_juan_daniel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * Esta clase unicamente se encarga de manejar la interfaz para el dibujado y la manipulacion
 * de figuras
 */

public class DragAndDropView extends SurfaceView implements SurfaceHolder.Callback {
	private DragAndDropThread thread;
	private ArrayList<Figura> figuras;
	private int figuraActiva;
	public Poligono poligono;
	Paint paint;
	Canvas canvas;

	// Constantes
	int COLOR_BASE = Color.rgb(77, 77, 255);
	int COLOR_BORDER_BASE = Color.rgb(46, 46, 131);
	int COLOR_SELECTED = Color.RED;
	int BORDER_COLOR_SELECTED = Color.rgb(97, 14, 9);
	int C_RADIO = 35;
	int C_COLOR_DENTRO = Color.rgb(153, 153, 247);
	int C_BORDER_WIDTH = 6;
	int P_COLOR = Color.rgb(245, 235, 230);
	int P_BORDER_COLOR = Color.rgb(181, 109, 72);
	int P_BORDER_WIDTH = 8;

	// Determinan cual figura es la que se selecciona (y en donde se quiere unir según sea el caso)
	Figura target, selected;

	private int mode;
	/*
	* 0 para mover los puntos
	* 1 para agregar puntos
	* 2 para eliminar puntos
	* 3 para unir puntos con el poligono
	*/

	public DragAndDropView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) { }

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		int x = getWidth();
		int y = getHeight();

		figuras = new ArrayList<>();
		figuras.add(createCirculo(x/2,y/2));
		figuras.add(createCirculo(x/2-(x/4),y/2+(y/4)));
		figuras.add(createCirculo(x/2+(x/4),y/2+(y/4)));

		// init poligono
		poligono = new Poligono(new ArrayList<>(), P_COLOR, P_BORDER_COLOR);
		for (Figura f: figuras)
			if (f instanceof Circulo)
				poligono.vertices.add(f);

		figuras.add(createCirculo(x/5+(x/3),y/7+(y/2)));
		figuras.add(createCirculo(x/18+(x/15),y/4+(y/20)));
		figuras.add(createCirculo(x/2+(x/7),y/3+(y/24)));

		figuraActiva = -1;
		
		thread = new DragAndDropThread(this.getHolder(), this);
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
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@Override
	public void onDraw(final Canvas canvas) {
		this.paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.WHITE);

		this.canvas = canvas;
		this.canvas.drawPaint(paint);

		paint.setAntiAlias(true);

		int mitadAncho = canvas.getWidth() / 2;
		int mitadAlto = canvas.getHeight() / 2;

		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setTextSize(48);
		canvas.drawText("Points in = " + figurasAdentro(),(float) (mitadAncho-(3*(mitadAncho/3.5))), (float) (mitadAlto-(2*(mitadAlto/2.2))), paint);

		// Dibujar primero el poligono
		this.drawPolygon();

		// Dibujar las figuras (circulos) que existan
		this.drawCircles();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		// Variable que servirá a lo largo de las operaciones
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				selected = null;

				if (mode == 1)
					synchronized (figuras) { figuras.add(createCirculo(x, y)); }
				// Cualquier caso menos al agregar, detectar si se presiona sobre una figura
				else
					hallarFiguraSeleccionada(x, y);

				if (mode == 3 && figuraActiva != -1) {
					unirVertice();
				} else if (figuraActiva != -1){
					target = selected;
				}

				break;
			case MotionEvent.ACTION_MOVE:
				// Mover figuras
				if (mode == 0 && figuraActiva != -1) {
					target.setX(x);
					target.setY(y);
					if (!figuraAdentro(target)) {
						target.changeColors(COLOR_SELECTED, BORDER_COLOR_SELECTED);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				// Devolver color base en modo movimiento
				if (mode == 0 && figuraActiva != -1)
					target.changeColors(COLOR_BASE, COLOR_BORDER_BASE);
				// Borrar figuras
				else if (mode == 2 && figuraActiva != -1) {
					if (poligono.vertices.contains(target) && poligono.vertices.size() == 3) {
						Toast.makeText(getContext(), "No puedes eliminarlo, debe existir una figura de 3 lados", Toast.LENGTH_SHORT).show();
						target.changeColors(COLOR_BASE, COLOR_BORDER_BASE);
					} else {
						synchronized (figuras) { figuras.remove(target); }
						synchronized (poligono.vertices) { poligono.vertices.remove(target); }
					}
				}
				// Agregar nuevo vértice
				else if (mode == 3 && target != null) {
					synchronized (poligono.vertices) { poligono.vertices.add(target); }
				}

				restartJoin();
				figuraActiva = -1;
				break;
		}

		return true;
	}

	// realiza las validaciones y muestra mensajes de error si es necesario
	public void unirVertice() {
		if (target == null) {
			target = selected;

			if (poligono.vertices.contains(target)){
				Toast.makeText(getContext(), "Figura inválida, no seleccione vértices del polígono", Toast.LENGTH_SHORT).show();
				restartJoin();
			}
		}
	}

	// cual figura fue seleccionada dado coordenadas
	public void hallarFiguraSeleccionada(int x, int y) {
		synchronized (figuras) { // evita el error "ConcurrentModificationException"
			for (Figura f : figuras)
				if (f instanceof Circulo) {
					Circulo c = (Circulo) f;
					float distanciaX = x - c.getX();
					float distanciaY = y - c.getY();

					if (Math.pow(c.getRadio(), 2) > (Math.pow(distanciaX, 2) + Math.pow(distanciaY, 2))) {
						figuraActiva = c.getId();
						selected = f;
					}
				}
		}

		// siempre "selecciona" al que esta por encima, en lugar del primero que se encuentre
		if (selected != null)
			selected.changeColors(COLOR_SELECTED, BORDER_COLOR_SELECTED);
	}

	// devuelve el tamaño de las figuras que estan dentro del poligono
	public int figurasAdentro() {
		int total_figuras = 0;
		// si una figura esta dentro del poligono, cambiar de color para dar "efecto" de animacion
		for (Figura  f : figuras)
			if (!poligono.vertices.contains(f) && figuraAdentro(f)) {
				total_figuras++;

				if (target == null || !target.equals(f))
					f.changeColors(C_COLOR_DENTRO, COLOR_BORDER_BASE);
			} else if (f.getColor() == C_COLOR_DENTRO)
				f.changeColors(COLOR_BASE, COLOR_BORDER_BASE);
		return total_figuras;
	}

	// dado una figura valida si esta dentro del poligono
	public boolean figuraAdentro(Figura figura) {
		int crossings = 0;
		float x = figura.getX();
		float y = figura.getY();

		ArrayList<Figura> vertices = poligono.vertices;
		for (int i = 0; i < vertices.size(); i++) {
			Figura vertice1 = vertices.get(i);
			Figura vertice2 = vertices.get((i + 1) % vertices.size());

			float x1 = vertice1.getX();
			float y1 = vertice1.getY();
			float x2 = vertice2.getX();
			float y2 = vertice2.getY();

			// verificar si el punto está entre los límites verticales del segmento de línea
			if (((y1 > y) != (y2 > y)) && (x < (x2 - x1) * (y - y1) / (y2 - y1) + x1))
				crossings++;
		}

		// si es impar, está dentro
		return (crossings % 2 != 0);
	}

	// Dibuja el polígono
	public void drawPolygon() {
		Path path = new Path();
		boolean b = true;
		for (Figura f : poligono.vertices) {
			if (b) { // para identificar el primer elemento unicamente
				path.moveTo(f.getX(), f.getY());
				b = false;
			} else
				path.lineTo(f.getX(), f.getY());
		}
		path.close();

		// Relleno
		paint.setColor(P_COLOR);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawPath(path, paint);

		// Bordes
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(P_BORDER_WIDTH);
		paint.setColor(P_BORDER_COLOR);
		canvas.drawPath(path, paint);
	}

	// dibuja los circulos que existan
	public void drawCircles() {
		synchronized (figuras) {
			for (Figura f : figuras) {
				if (f instanceof Circulo) {
					Circulo c = (Circulo) f;
					this.drawCicle(c, c.getColor(), c.getBorderColor());
				}
			}
		}
	}

	// dibujo de un circulo
	public void drawCicle(Circulo c, int color, int borderColor) {
		// Relleno
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		canvas.drawCircle(c.getX(), c.getY(), c.getRadio(), paint);

		// Borde
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(C_BORDER_WIDTH);
		paint.setColor(borderColor);
		canvas.drawCircle(c.getX(), c.getY(), c.getRadio(), paint);
	}

	// Creacion de circulos para la lista figuras, de esta forma se realiza de manera constante
	public Circulo createCirculo(int x, int y) {
		return new Circulo(figuras.size(), x, y, C_RADIO, COLOR_BASE, COLOR_BORDER_BASE);
	}

	// inicializar variables para agregar un nuevo vertice en el poligono
	private void initJoin() {
		target = null;
	}

	// cambiar el color de la(s) variable necesaria para agregar un nuevo vertice en el poligono
	private void changeColorJoin() {
		if (target != null)
			target.changeColors(COLOR_BASE, COLOR_BORDER_BASE);
	}

	// reiniciar variable(s) necesaria para agregar un nuevo vertice en el poligono
	public void restartJoin() {
		changeColorJoin();
		initJoin();
	}

	// Establecer el modo de operacion
	public void setMode(int mode) {
		this.mode = mode;
		switch (mode) {
			case 0:
				Toast.makeText(getContext(), "Mueve cualquiera de los puntos dibujados", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(getContext(), "Presiona en la pantalla para agregar puntos", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(getContext(),  "Presiona sobre el punto a eliminar", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(getContext(),  "Presiona sobre el punto que quieras agregar", Toast.LENGTH_SHORT).show();
				restartJoin();
				break;
			default:
				Log.e("SetMode", "Se estableció un modo inválido!");
				break;
		}
	}
}