package com.z_iti_271304_u2_torres_colorado_juan_daniel;

import java.util.ArrayList;

public class Poligono extends Figura {
    public ArrayList<Figura> vertices;

    public Poligono(ArrayList<Figura> vertices, int color, int borderColor) {
        this.vertices = vertices;
        this.color = color;
        this.borderColor = borderColor;
    }
}
