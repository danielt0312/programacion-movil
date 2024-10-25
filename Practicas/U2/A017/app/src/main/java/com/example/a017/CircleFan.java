package com.example.a017;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This class renders a triangle fan;
 */
public class CircleFan implements GLSurfaceView.Renderer {

    // This class is only for reference of what does a vertex stores, but it is never used
   /* private class Vertex {
        private float[] position = new float[2]; // 2 coordinates
        private float[] color    = new float[3]; // 3 numbers (RGB)
    } */

    private int mProgram; // Pointer to the program
    private int positionHandle; // Pointer to the vPosition variable in the vertex shader
    private int colorHandle; // Pointer to the vColor variable in the fragment shader

    private static final int COORDS_PER_VERTEX = 2; // We only store 2 coords per vertex
    private static final int COLORS_PER_VERTEX = 3; // We only store 3f to represent a color (RGB)
    private static final int ALL_PER_VERTEX    = COORDS_PER_VERTEX + COLORS_PER_VERTEX;
    private static final int COORDS_OFFSET = COORDS_PER_VERTEX * 4;
    private static final int COLORS_OFFSET = COLORS_PER_VERTEX * 4;
    private static final int VERTEX_OFFSET = COORDS_OFFSET + COLORS_OFFSET;
    // Stride means the size of a vertex, in this case our vertex is composed of position and color
    private int vertexStride = COORDS_PER_VERTEX * 4 + COLORS_PER_VERTEX * 4; // 4 because of floats
    private String vertexShader =
            "attribute vec4 vPosition;      \n" +
            "attribute vec4 aColor;         \n" +
            "varying vec4 vColor;           \n" +
            "void main () {                 \n" +
            // TODO: Multiply positions by matrix to resize the figure given height and width
            "   vColor = aColor;            \n" +
            "   gl_Position = vPosition;    \n" +
            "}                              \n";
    private String fragmentShader =
            "precision lowp float;          \n" + // For faster rendering, set low float precision
            "varying vec4 vColor;           \n" +
            "void main() {                  \n" +
                "  gl_FragColor = vColor;   \n" +
            "}                              \n";
    private FloatBuffer vertexBuffer; // Buffer to use

    public int sides; // How many sides does the polygon will have?

    /**
     * Set the default size of the figure and allocate memory for the buffer and data
     */
    public CircleFan(int sides) {
        this.sides = (sides > 3 && sides < 360) ? sides : 3; // Set default polygon type
        calculateData();
    }

    /**
     * Compile and link the shaders
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // Set background color
        mProgram = glCreateProgram(); // Tell OpenGL to create a new program and return the id

        // Load shaders source code and attach to the created program
        int vs = loadShader(GL_VERTEX_SHADER, vertexShader);
        int fs = loadShader(GL_FRAGMENT_SHADER, fragmentShader);
        glAttachShader(mProgram, vs);
        glAttachShader(mProgram, fs);

        // Link the shaders in the created program
        glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
    }

    /**
     * Recalculate the data, that is the vertex positions and colors.
     * <p>
     * Stores the data into the OpenGL buffer
     * <p>
     * This method should be call everytime the sides attribute is changed
     */
    public void calculateData() {
        // Calculate the vertexes
        float[] positions = drawCircle(0.0f, 0.0f, 0.5f, sides);
        // Populate buffer with calculated data
        vertexBuffer = ByteBuffer.allocateDirect(positions.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(positions).position(0);

        int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0); // Generate one buffer
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]); // Use this buffer
        // Tell openGL the size of this buffer, that we will use it dynamically and store it
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity()*4, vertexBuffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * Draw calculated polygon
     * @param gl10
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT); // Clear screen to redraw
        // Use the created program
        glUseProgram(mProgram);

        // Get the pointer to the vPosition variable
        positionHandle  = glGetAttribLocation(mProgram, "vPosition");
        colorHandle     = glGetAttribLocation(mProgram, "aColor");

        // Enable writing into vertex attributes
        glEnableVertexAttribArray(positionHandle);
        glEnableVertexAttribArray(colorHandle);
        // Set the attributes of a vertex
        glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GL_FLOAT,
                           false, vertexStride, vertexBuffer);
        glVertexAttribPointer(colorHandle, COLORS_PER_VERTEX, GL_FLOAT,
                              true, vertexStride, vertexBuffer);

        // Draw the whole thing
        // NO estan dibuajando un Triangle FAN, sino TRIANGULOS individuales!!!
        glDrawArrays(GL_TRIANGLES, 0, sides*3);
        //glDrawArrays(GL_LINES, 0, sides*3);
        glDisableVertexAttribArray(positionHandle);
    }

    /**
     * Increases the sides of the triangle with a limit of 360 (too much rendering)
     */
    public void increaseSides() {
        if(this.sides < 360) {
            this.sides++; calculateData(); //System.out.println(sides);
        }
    }

    /**
     * Decreases the sides of the drawn triangle by 1, with a limit of 3 triangles (base case)
     */
    public void decreaseSides() {
        if(this.sides > 3) {
            this.sides--; calculateData(); //System.out.println(sides);
        }
    }

    /**
     * Load the given source (This is a helper function)
     * @param type type of the source (vertex or fragment)
     * @param source source code
     * @return returns the id of the loaded shader
     */
    public static int loadShader(int type, String source) {
        int id = glCreateShader(type); // Tell OpenGL to create a new shader and return its id
        glShaderSource(id, source); // Add source code to the created shader
        glCompileShader(id); // Compile shader
        return id;
    }

    /**
     * Calculate a circle in a triangle fan fashion way
     * @param x center of the circle in the x-axis
     * @param y center of the circle in the x-axis
     * @param r radius of the circle
     * @param sides how many sides the circle should have
     * @return returns an array of vertexes to draw
     */
    public float[] drawCircle(float x, float y, float r, int sides) {
        if(sides < 3) sides = 3; // Minimum amount of sides should be 3
        int vertSize = 3*sides*ALL_PER_VERTEX;

        // A vertex is composed of (x, y, r, g, b); position and color
        float[] vertexes = new float[vertSize];
        int indiceTriangulo = 0;
        for(int i = 0; i < vertSize-5; i=i+(3*ALL_PER_VERTEX)) { //Create a new triangle in each iteration
        //for(int i = 0; i < 1; i=i+(3*ALL_PER_VERTEX)) { //Create a new triangle in each iteration
        //for(int i = 0; i < sides; i=i+(3*ALL_PER_VERTEX)) { //Create a new triangle in each iteration
            int idx = i/(3*ALL_PER_VERTEX);
            // Create triangle based on their coordinates
            float[] triangle = createTriangle(0,0,
                    x + r * (float) Math.cos((idx+1) * 2 * Math.PI/sides),
                    y + r * (float) Math.sin((idx+1) * 2 * Math.PI/sides),
                    x + r * (float) Math.cos((idx+2) * 2 * Math.PI/sides),
                    y + r * (float) Math.sin((idx+2) * 2 * Math.PI/sides),indiceTriangulo);

            float[] trianglex = createTriangle(0,0,
            0.5f,0.0f,0.5f,0.5f,1);
            System.arraycopy(triangle, 0, vertexes, triangle.length*idx, triangle.length);
            indiceTriangulo+=1;
        }

        // This is for debugging purposes
        System.out.println("Number of triangles: " + sides);
        System.out.println("Number of vertexes:  " + vertSize);
        for(int i = 0; i < vertexes.length-4; i=i+5) {
            if(i%3 == 0)
                System.out.println("Triangle "+i);
            System.out.println( "Coord " + (i/5) + ": " + vertexes[i] + ", " + vertexes[i+1] + ", "
                                + vertexes[i+2] + ", " + vertexes[i+3] + ", " + vertexes[i+4]);
        }
        return vertexes;
    }

    private final Random rand = new Random();

    /**
     * Create a triangle based on the 3 given coordinates (vertices)
     * @param x_1 x-axis in first coordinate
     * @param y_1 y-axis in first coordinate
     * @param x_2 x-axis in second coordinate
     * @param y_2 y-axis in second coordinate
     * @param x_3 x-axis in third coordinate
     * @param y_3 y-axis in third coordinate
     * @return an array containing the 3 vertices data
     */
    public float[] createTriangle(float x_1, float y_1, float x_2, float y_2, float x_3, float y_3, int index){
        //float r = rand.nextFloat(), g = rand.nextFloat(), b = rand.nextFloat();
        float r = 0.0f, g = 0.0f, b = 0.0f;
        if (index==0) {
            r = 0.0f; g = 0.0f; b = 0.0f; }
        else {
            r = 0.0f; g = 1.0f; b = 0.0f; }
        // Point 1
        float[] v1 = createVertex(x_1, y_1, r, g, b);
        // Point 2
        float[] v2 = createVertex(x_2, y_2, r, g, b);
        // Point 3
        float[] v3 = createVertex(x_3, y_3, r, g, b);
        // Copy the 3 vertices into a single array
        float[] triangle = new float[3*ALL_PER_VERTEX];
        System.arraycopy(v1, 0, triangle, ALL_PER_VERTEX*0, v1.length);
        System.arraycopy(v2, 0, triangle, ALL_PER_VERTEX*1, v2.length);
        System.arraycopy(v3, 0, triangle, ALL_PER_VERTEX*2, v3.length);
        return triangle;
    }

    /**
     * Create a new vertex based; a vertex is composed of position and color:
     * <ul>
     *     <li> Position = x, y </li>
     *     <li> Color    = r, g, b</li>
     * </ul>
     * @param x position in the x-axis
     * @param y position in the y-axis
     * @param r RED component of the color
     * @param g GREEN component of the color
     * @param b BLUE component of the color
     * @return returns an array of floats representing the vertex
     */
    public float[] createVertex(float x, float y, float r, float g, float b) {
        return new float[] { x, y, r, g, b};
    }
}







