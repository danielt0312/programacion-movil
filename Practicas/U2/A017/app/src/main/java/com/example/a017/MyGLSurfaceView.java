package com.example.a017;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by GabrielK on 6/22/2016.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    boolean zooming = false;
    private int zoom, width, height;
    private CircleFan renderer;
    private long timeOfLastZoom;
    private final int floatsPerTrap = 42;

    public MyGLSurfaceView(Context context) {
        this(context, 0,0);
    }

    public MyGLSurfaceView(Context context, int width, int height) {
        this(context, width, height, 4);
    }

    public MyGLSurfaceView(Context context, int width, int height, int zoom) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(true);
        Point size = new Point();
        this.width = width;
        this.height = height;
        this.zoom = zoom;
        // Set the renderer to our demo renderer, defined below.
        renderer = new CircleFan(zoom);
        setRenderer(renderer);
        timeOfLastZoom = System.currentTimeMillis();
    }

    /**
     * The screen is horizontally divided in 2 parts, if it is clicked in the top part more shapes
     * are added to it, if it is clicked in the bottom part shapes are extracted from it.
     * @param event current event
     * @return true if handled
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        //renderer.setUp();
        /*float [] verticesData = new float [floatsPerTrap * (int) Math.pow(4, zoom)];

        for (int i = 0; i < Math.pow(2, zoom); i++)
            System.arraycopy(renderer.getVertexData(i,1), 0, verticesData, (int) (i) * floatsPerTrap, floatsPerTrap);*/
        //return super.onTouchEvent(event);
        if (System.currentTimeMillis() - timeOfLastZoom > 250 && !zooming) {
            //onPause();
            if (event.getY() > height / 2 && zoom <= 1 || event.getY() <= height / 2 && zoom >= 9)
                return true;
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    zooming = true;
                    if (event.getY() < height / 2) // Click en parte de arriba
                        renderer.increaseSides();
                    else // Click en parte de abajo
                        renderer.decreaseSides();
                    timeOfLastZoom = System.currentTimeMillis();

                    zooming = false;
                }
            });
        }
        return true;
    }
}
