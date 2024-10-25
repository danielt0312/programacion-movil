/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 */

package com.canvamastouch;

public class Rectangulo extends Figura {

	private int ancho;
	private int alto;
	
	public Rectangulo(int id, int x, int y, int ancho, int alto) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.ancho = ancho;
		this.alto = alto;
	}

	public int getAncho() {
		return ancho;
	}

	public int getAlto() {
		return alto;
	}
}
