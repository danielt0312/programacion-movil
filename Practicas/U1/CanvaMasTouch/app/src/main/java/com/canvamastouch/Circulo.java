/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 */

package com.canvamastouch;

public class Circulo extends Figura {

	private int radio;
	
	public Circulo(int id, float x, float y, int radio, String color) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.radio = radio;
		this.color = color;
	}

	public int getRadio() {
		return radio;
	}
}
