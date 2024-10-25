package com.example.sharedpreferences_listaobjetos;

/**
 * Created by marco on 12/05/17.
 */

public class EstudiantesGueros {
    public int Edad;
    public String ApellidoPaterno;
    public String ApellidoMaterno;
    public String Nombre;
    public int Carrera;
    public boolean BecaPronabes;
    public boolean BecaUPV;
    public double Peso;
    public double Estatura;


    public EstudiantesGueros (int ed, String ap, String am, String N, int C, boolean BP, boolean BU, double P, double Es) {
        Edad=ed;
        ApellidoPaterno=ap;
        ApellidoMaterno=am;
        Nombre=N;
        Carrera=C;
        BecaPronabes=BP;
        BecaUPV=BU;
        Peso=P;
        Estatura=Es;
    }

    public String toString () {
        return (""+Nombre+":"+ApellidoPaterno+":"+ApellidoMaterno);
    }


}
