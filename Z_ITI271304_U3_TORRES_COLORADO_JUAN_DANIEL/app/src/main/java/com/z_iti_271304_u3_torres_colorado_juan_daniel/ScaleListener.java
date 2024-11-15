package com.z_iti_271304_u3_torres_colorado_juan_daniel;

import android.graphics.Matrix;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    Matrix matrix;
    ImageView imageView;

    private float scale = 1f;

    public ScaleListener(Matrix matrix, ImageView imageView) {
        this.matrix = matrix;
        this.imageView = imageView;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        scale *= scaleFactor;
        matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
        imageView.setImageMatrix(matrix);
        return true;
    }
}
