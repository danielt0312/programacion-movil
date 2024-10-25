package com.z_iti_271304_u2_torres_colorado_juan_daniel;

public class Circulo extends Figura {
	private int radio;
	
	public Circulo(int id, float x, float y, int radio, int color, int borderColor) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.radio = radio;
		this.color = color;
		this.borderColor = borderColor;
	}

	public int getRadio() {
		return radio;
	}
}
