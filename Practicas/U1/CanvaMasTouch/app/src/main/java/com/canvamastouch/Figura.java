/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 */

package com.canvamastouch;

public abstract class Figura {

	protected int id;
	protected float x;
	protected float y;
	protected String color;
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getId() {
		return id;
	}

	protected String getColor() {
		return color;
	}

	protected void setColor(String color) {
		this.color = color;
	}
}
