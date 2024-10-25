package com.z_iti_271304_u2_torres_colorado_juan_daniel;

public abstract class Figura {
	protected int id;
	protected float x;
	protected float y;
	protected int color;
	protected int borderColor;

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

	public int getColor() {
		return color;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void changeColors(int color, int borderColor) {
		this.color = color;
		this.borderColor = borderColor;
	}
}
