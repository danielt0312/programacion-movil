package com.z_iti_271304_u3_torres_colorado_juan_daniel;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;

public class Region {
    private enum Scale { SQUARE, RECTANGLE }
    private Scale scale = Scale.SQUARE;

    private FrameLayout frameImage;
    private View cropRegion;

    private RadioButton rdbtCuadrado, rdbtRectangulo;
    private Button btnChangeAspect;

    public Region(FrameLayout frameImage, View cropRegion) {
        this.frameImage = frameImage;
        this.cropRegion = cropRegion;
    }

    public void changeAspectRegion(Context CX) {
        final Dialog dialog = new Dialog(CX);

        dialog.setContentView(R.layout.dialog_resize_region);
        dialog.setTitle("Cambiar relacion de aspecto");

        rdbtCuadrado = dialog.findViewById(R.id.rdbtCuadrado);
        rdbtRectangulo = dialog.findViewById(R.id.rdbtRectangulo);

        if (scale.equals(Scale.SQUARE)) {
            rdbtCuadrado.setChecked(true);
        } else {
            rdbtRectangulo.setChecked(true);
        }

        btnChangeAspect = dialog.findViewById(R.id.btnAceptar);
        btnChangeAspect.setOnClickListener(v -> {
            scale = rdbtCuadrado.isChecked() ? Scale.SQUARE : Scale.RECTANGLE;
            Log.d("Scale", scale.toString());
            this.customRegionSize();
            dialog.dismiss();
        });

        dialog.show();
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
        cropRegion.setX((frameImage.getWidth() - cropRegion.getWidth()) / 2f);
        cropRegion.setY((frameImage.getHeight() - cropRegion.getHeight()) / 2f);
    }

    public int getWidth() {
        return cropRegion.getWidth();
    }

    public int getHeight() {
        return cropRegion.getHeight();
    }

    public float getX() {
        return cropRegion.getX();
    }

    public float getY() {
        return cropRegion.getY();
    }
}
