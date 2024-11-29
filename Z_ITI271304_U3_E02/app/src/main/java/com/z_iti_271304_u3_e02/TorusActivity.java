package com.z_iti_271304_u3_e02;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public class TorusActivity extends AppCompatActivity {
    private MyGLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torus);

        glSurfaceView = new MyGLSurfaceView(this);
        setContentView(glSurfaceView);
    }


    @Override
    protected void onPause() {
        super.onPause();
            glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
            glSurfaceView.onResume();
    }

    // Clase personalizada para gestionar eventos táctiles
    private static class MyGLSurfaceView extends GLSurfaceView {
        private final TorusRenderer renderer;
        private float previousX, previousY;
        private final float TOUCH_SCALE_FACTOR = 0.5f; // Sensibilidad al toque

        public MyGLSurfaceView(Context context) {
            super(context);

            // Configurar OpenGL ES 3.0
            setEGLContextClientVersion(3);

            // Configurar el renderizador
            renderer = new TorusRenderer();
            setRenderer(renderer);

            // Configurar modo de renderizado continuo
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    // Calcular desplazamientos
                    float dx = x - previousX;
                    float dy = y - previousY;

                    // Actualizar ángulos de rotación en el renderizador
                    renderer.setAngleX(renderer.getAngleX() + dy * TOUCH_SCALE_FACTOR);
                    renderer.setAngleY(renderer.getAngleY() + dx * TOUCH_SCALE_FACTOR);

                    // Solicitar redibujar
                    requestRender();
                    break;
            }

            // Guardar las coordenadas actuales para el siguiente evento
            previousX = x;
            previousY = y;
            return true;
        }
    }
}
