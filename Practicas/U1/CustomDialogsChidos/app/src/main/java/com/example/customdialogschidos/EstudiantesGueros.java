package com.example.customdialogschidos;

/**
 * Created by marco on 12/05/17.
 */

public class EstudiantesGueros {
    public String ApellidoPaterno;
    public String ApellidoMaterno;
    public String Nombre;


    public EstudiantesGueros(String ap, String am, String N) {
        ApellidoPaterno=ap;
        ApellidoMaterno=am;
        Nombre=N;
    }

    public String toString () {
        return (""+Nombre+":"+ApellidoPaterno+":"+ApellidoMaterno);
    }


}
