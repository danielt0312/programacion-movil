package com.z_iti_271304_u3_e02;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TorusRenderer implements GLSurfaceView.Renderer {
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexBuffer;
    private int program;

    // Torus parameters
    private final float minorRadius = 0.2f;
    private final float majorRadius = 0.5f;
    private final int numSides = 8;
    private final int numRings = 8;

    private float angleX = 0; // Ángulo de rotación en el eje X
    private float angleY = 0; // Ángulo de rotación en el eje Y
    private float rotationSpeed = 1.0f; // Velocidad de rotación


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Generar los vértices del toroide
        float[] torusVertices = generateTorusVertices(minorRadius, majorRadius, numSides, numRings);

        // Configurar el buffer de vértices
        vertexBuffer = ByteBuffer.allocateDirect(torusVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(torusVertices);
        vertexBuffer.position(0);

        // Compilar y vincular shaders
        String vertexShaderCode = getVertexShaderCode();
        String fragmentShaderCode = getFragmentShaderCode();

        int vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);
    }

    private float[] mvpMatrix = new float[16]; // Matriz de modelo-vista-proyección
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        float aspectRatio = (float) width / height;
        Matrix.perspectiveM(projectionMatrix, 0, 45, aspectRatio, 1f, 10f);

        Matrix.setLookAtM(viewMatrix, 0,
                0f, 0f, 3f, // Posición de la cámara
                0f, 0f, 0f, // Punto al que mira
                0f, 1f, 0f  // Vector "arriba"
        );
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Limpiar el buffer de color y profundidad
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        GLES30.glUseProgram(program);

        // Calcular la matriz MVP
        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0); // Inicializar como identidad

        // Aplicar las rotaciones en X e Y según el usuario
        Matrix.rotateM(modelMatrix, 0, angleX, 1, 0, 0); // Rotar en el eje X
        Matrix.rotateM(modelMatrix, 0, angleY, 0, 1, 0); // Rotar en el eje Y

        // Combinar matrices modelo, vista y proyección
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        // Pasar la matriz MVP al shader
        int mvpMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix");
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Dibujar el toroide
        int positionHandle = GLES30.glGetAttribLocation(program, "aPosition");
        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexBuffer.limit() / 3);

        GLES30.glDisableVertexAttribArray(positionHandle);
    }



    public void setAngleX(float angle) {
        this.angleX = angle;
    }

    public void setAngleY(float angle) {
        this.angleY = angle;
    }

    public float getAngleX() {
        return angleX;
    }

    public float getAngleY() {
        return angleY;
    }


    private float[] generateTorusVertices(float minorRadius, float majorRadius, int numSides, int numRings) {
        float[] vertices = new float[numSides * numRings * 6 * 3];
        int index = 0;

        for (int i = 0; i < numRings; i++) {
            double theta = 2.0 * Math.PI * i / numRings;
            double nextTheta = 2.0 * Math.PI * (i + 1) / numRings;

            for (int j = 0; j < numSides; j++) {
                double phi = 2.0 * Math.PI * j / numSides;
                double nextPhi = 2.0 * Math.PI * (j + 1) / numSides;

                // Cuatro vértices del quad
                float[] v0 = getTorusVertex(theta, phi, minorRadius, majorRadius);
                float[] v1 = getTorusVertex(nextTheta, phi, minorRadius, majorRadius);
                float[] v2 = getTorusVertex(nextTheta, nextPhi, minorRadius, majorRadius);
                float[] v3 = getTorusVertex(theta, nextPhi, minorRadius, majorRadius);

                // Primer triángulo
                vertices[index++] = v0[0];
                vertices[index++] = v0[1];
                vertices[index++] = v0[2];

                vertices[index++] = v1[0];
                vertices[index++] = v1[1];
                vertices[index++] = v1[2];

                vertices[index++] = v2[0];
                vertices[index++] = v2[1];
                vertices[index++] = v2[2];

                // Segundo triángulo
                vertices[index++] = v0[0];
                vertices[index++] = v0[1];
                vertices[index++] = v0[2];

                vertices[index++] = v2[0];
                vertices[index++] = v2[1];
                vertices[index++] = v2[2];

                vertices[index++] = v3[0];
                vertices[index++] = v3[1];
                vertices[index++] = v3[2];
            }
        }

        return vertices;
    }



    private float[] getTorusVertex(double theta, double phi, float minorRadius, float majorRadius) {
        float x = (float) ((majorRadius + minorRadius * Math.cos(phi)) * Math.cos(theta));
        float y = (float) (minorRadius * Math.sin(phi));
        float z = (float) ((majorRadius + minorRadius * Math.cos(phi)) * Math.sin(theta));
        return new float[]{x, y, z};
    }

    private int compileShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    private String getVertexShaderCode() {
        return "#version 300 es\n" +
                "layout(location = 0) in vec3 aPosition;\n" +
                "uniform mat4 uMVPMatrix;\n" +
                "out vec3 vPosition;\n" +
                "void main() {\n" +
                "    gl_Position = uMVPMatrix * vec4(aPosition, 1.0);\n" +
                "    vPosition = aPosition;\n" +
                "}";
    }


    private String getFragmentShaderCode() {
        return "#version 300 es\n" +
                "precision mediump float;\n" +
                "in vec3 vPosition;\n" +
                "out vec4 fragColor;\n" +

                "void main() {\n" +
                "    // Convertimos las coordenadas cartesianas a polares para el toroide\n" +
                "    float theta = atan(vPosition.y, vPosition.x); // Ángulo principal\n" +
                "    float radius = length(vPosition.xy);         // Distancia desde el centro\n" +
                "    float phi = atan(vPosition.z, radius);      // Ángulo secundario\n" +

                "    // Normalizamos los ángulos para obtener valores entre 0 y 1\n" +
                "    float normalizedTheta = (theta + 3.14159) / (2.0 * 3.14159);\n" +
                "    float normalizedPhi = (phi + 3.14159) / (2.0 * 3.14159);\n" +

                "    // Dividimos el toroide en secciones basadas en los ángulos\n" +
                "    if (normalizedTheta < 0.25) {\n" +
                "        if (normalizedPhi < 0.5) {\n" +
                "            fragColor = vec4(1.0, 0.0, 0.0, 1.0); // Rojo\n" +
                "        } else {\n" +
                "            fragColor = vec4(1.0, 1.0, 0.0, 1.0); // Amarillo\n" +
                "        }\n" +
                "    } else if (normalizedTheta < 0.5) {\n" +
                "        if (normalizedPhi < 0.5) {\n" +
                "            fragColor = vec4(0.0, 1.0, 0.0, 1.0); // Verde\n" +
                "        } else {\n" +
                "            fragColor = vec4(0.0, 0.0, 1.0, 1.0); // Azul\n" +
                "        }\n" +
                "    } else if (normalizedTheta < 0.75) {\n" +
                "        if (normalizedPhi < 0.5) {\n" +
                "            fragColor = vec4(1.0, 0.5, 0.0, 1.0); // Naranja\n" +
                "        } else {\n" +
                "            fragColor = vec4(0.5, 0.0, 0.5, 1.0); // Púrpura\n" +
                "        }\n" +
                "    } else {\n" +
                "        if (normalizedPhi < 0.5) {\n" +
                "            fragColor = vec4(0.0, 1.0, 1.0, 1.0); // Cyan\n" +
                "        } else {\n" +
                "            fragColor = vec4(0.5, 0.5, 0.5, 1.0); // Gris\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

}