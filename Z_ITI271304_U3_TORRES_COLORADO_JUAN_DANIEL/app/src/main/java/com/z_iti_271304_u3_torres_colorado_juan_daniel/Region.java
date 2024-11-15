package com.z_iti_271304_u3_torres_colorado_juan_daniel;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class Region {
    private enum Scale { SQUARE, RECTANGLE }
    private Scale scale = Scale.SQUARE;

    private FrameLayout frameImage;
    private View cropRegion;

    public Region(FrameLayout frameImage, View cropRegion) {
        this.frameImage = frameImage;
        this.cropRegion = cropRegion;
    }

    public void customRegionSize() {
        float porcentaje = 0.9F;
        int width = (int) (frameImage.getWidth() * porcentaje);
        int height = (int) (frameImage.getHeight() * porcentaje);

        ViewGroup.LayoutParams params = cropRegion.getLayoutParams();
        switch (scale) {
            case SQUARE:
                params.width = width;
                params.height = width;
                break;
            case RECTANGLE:
                params.width = width;
                params.height = height;
                break;
            default:
                Log.e("Scale", "El tamaño de escala elegido no existe, cambiar obligatoriamente");
                break;
        }

        cropRegion.setLayoutParams(params);
    }

    public int getWidth() {
        return cropRegion.getWidth();
    }

    public int getHeight() {
        return cropRegion.getHeight();
    }
}
