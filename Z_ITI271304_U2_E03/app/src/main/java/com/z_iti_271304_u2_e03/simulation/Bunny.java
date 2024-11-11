package com.z_iti_271304_u2_e03.simulation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import java.util.Random;

public class Bunny {
    // Posición y estado
    public float x, y;
    public boolean isWhite;
    public boolean isAlive = true;

    // Movimiento básico
    private float speedX;
    private float speedY;
    private static final float BASE_SPEED = 3.0f;
    private static final float MAX_SPEED = 5.0f;
    private static final float DIRECTION_CHANGE_PROBABILITY = 0.02f;

    // Variables para el salto
    private float groundLevel;
    private boolean isJumping = false;
    private float jumpVelocity = 0;
    private static final float GRAVITY = 0.5f;
    private static final float JUMP_POWER = -12f;
    private static final float JUMP_PROBABILITY = 0.02f;
    private static final float MAX_JUMP_HEIGHT = 150f;

    // Animación
    private float squashStretch = 1.0f;
    private static final float MAX_SQUASH = 0.8f;
    private static final float MAX_STRETCH = 1.2f;

    // Otros
    private Random random = new Random();
    private Paint shadowPaint;

    public Bunny(float x, float y, boolean isWhite, float groundLevel) {
        this.x = x;
        this.y = y;
        this.isWhite = isWhite;
        this.groundLevel = groundLevel;
        this.speedX = (random.nextFloat() * 2 - 1) * BASE_SPEED;

        // Inicializar paint para la sombra
        shadowPaint = new Paint();
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAlpha(60);
    }

    public void update(int width, int height) {
        if (!isAlive) return;

        // Cambio aleatorio de dirección
        if (random.nextFloat() < DIRECTION_CHANGE_PROBABILITY) {
            speedX = (random.nextFloat() * 2 - 1) * BASE_SPEED;
        }

        // Movimiento horizontal
        x += speedX;

        // Rebote en los bordes
        if (x <= 0 || x >= width - 50) { // 50 es aproximadamente el ancho del sprite
            speedX = -speedX;
            x = Math.max(0, Math.min(x, width - 50));
        }

        // Lógica de salto
        if (!isJumping && random.nextFloat() < JUMP_PROBABILITY) {
            startJump();
        }

        if (isJumping) {
            // Actualizar posición vertical
            y += speedY;
            speedY += GRAVITY;

            // Limitar la altura máxima del salto
            y = Math.min(y, groundLevel - MAX_JUMP_HEIGHT);

            // Actualizar el efecto squash & stretch
            float velocityFactor = Math.abs(speedY) / Math.abs(JUMP_POWER);
            if (speedY < 0) { // Subiendo
                squashStretch = 1.0f + (velocityFactor * 0.2f); // Estirar
            } else { // Bajando
                squashStretch = 1.0f - (velocityFactor * 0.1f); // Aplastar
            }

            // Mantener el squash & stretch dentro de límites
            squashStretch = Math.max(MAX_SQUASH, Math.min(MAX_STRETCH, squashStretch));

            // Verificar aterrizaje
            if (y >= groundLevel) {
                land();
            }
        } else {
            // Pequeña animación de respiración cuando está en el suelo
            squashStretch = 1.0f + (float)Math.sin(System.currentTimeMillis() * 0.005) * 0.02f;
        }
    }

    private void startJump() {
        if (!isJumping) {
            isJumping = true;
            speedY = JUMP_POWER;
            squashStretch = MAX_STRETCH; // Estiramiento inicial al saltar
        }
    }

    private void land() {
        y = groundLevel;
        isJumping = false;
        speedY = 0;
        squashStretch = MAX_SQUASH; // Aplastamiento al aterrizar

        // Pequeño rebote aleatorio al aterrizar
        if (random.nextFloat() < 0.3f) {
            startJump();
            speedY = JUMP_POWER * 0.5f; // Salto más pequeño
        }
    }

    public void draw(Canvas canvas, Bitmap bitmap) {
        if (!isAlive || bitmap == null) return;

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // Dibujar sombra
        float shadowScale = 0.7f + (groundLevel - y) / MAX_JUMP_HEIGHT * 0.3f;
        float shadowY = groundLevel - 5; // 5 pixeles arriba del suelo
        canvas.save();
        canvas.scale(shadowScale, 0.25f, x + bitmapWidth/2, shadowY);
        canvas.drawOval(x, shadowY, x + bitmapWidth, shadowY + 10, shadowPaint);
        canvas.restore();

        // Dibujar el conejo con efecto squash & stretch
        canvas.save();

        // Punto central para la transformación
        float centerX = x + bitmapWidth/2;
        float centerY = y + bitmapHeight;

        // Aplicar squash & stretch manteniendo el área constante
        float scaleY = squashStretch;
        float scaleX = 1.0f / squashStretch; // Mantener el área constante

        // Aplicar la transformación
        canvas.scale(scaleX, scaleY, centerX, centerY);

        // Voltear la imagen si se mueve hacia la izquierda
        if (speedX < 0) {
            canvas.scale(-1, 1, centerX, y + bitmapHeight/2);
        }

        // Dibujar el bitmap
        canvas.drawBitmap(bitmap, x, y, null);
        canvas.restore();
    }

    // Método para colisiones y otros eventos
    public boolean intersects(float otherX, float otherY, float radius) {
        float dx = x - otherX;
        float dy = y - otherY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < radius;
    }

    // Método para reiniciar el conejo
    public void reset(float newX, float newY) {
        x = newX;
        y = newY;
        isJumping = false;
        speedY = 0;
        speedX = (random.nextFloat() * 2 - 1) * BASE_SPEED;
        squashStretch = 1.0f;
        isAlive = true;
    }
}