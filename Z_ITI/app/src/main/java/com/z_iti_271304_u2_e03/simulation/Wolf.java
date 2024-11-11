package com.z_iti_271304_u2_e03.simulation;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Random;

public class Wolf {
    public float x, y;
    private static final int WOLF_SIZE = 60;
    private float speedX;
    private float speedY;
    private static final float HUNT_SPEED = 4.0f;
    private Bunny targetBunny;
    private final Random random = new Random();
    private static final float TARGET_CHANGE_PROBABILITY = 0.02f;
    private static final float HUNT_RADIUS = 200f;

    public Wolf(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.speedX = HUNT_SPEED;
        this.speedY = 0;
    }

    public void update(int width, int height) {
        // Movimiento de patrulla básico si no hay objetivo
        if (random.nextFloat() < 0.05) {
            speedX = HUNT_SPEED * (random.nextBoolean() ? 1 : -1);
            speedY = HUNT_SPEED * (random.nextFloat() - 0.5f);
        }

        // Actualizar posición
        x += speedX;
        y += speedY;

        // Mantener dentro de los límites
        if (x <= 0 || x >= width - WOLF_SIZE) {
            speedX = -speedX;
            x = Math.max(0, Math.min(x, width - WOLF_SIZE));
        }
        if (y <= 0 || y >= height - WOLF_SIZE) {
            speedY = -speedY;
            y = Math.max(0, Math.min(y, height - WOLF_SIZE));
        }
    }

    public void hunt(ArrayList<Bunny> bunnies) {
        // Buscar nuevo objetivo
        if (targetBunny == null || !targetBunny.isAlive || random.nextFloat() < TARGET_CHANGE_PROBABILITY) {
            findNewTarget(bunnies);
        }

        // Si hay un objetivo, perseguirlo
        if (targetBunny != null && targetBunny.isAlive) {
            float dx = targetBunny.x - x;
            float dy = targetBunny.y - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                // Normalizar y aplicar velocidad
                speedX = (dx / distance) * HUNT_SPEED;
                speedY = (dy / distance) * HUNT_SPEED;
            }
        }

        // Verificar colisiones
        checkCollisions(bunnies);
    }

    private void findNewTarget(ArrayList<Bunny> bunnies) {
        float closestDistance = Float.MAX_VALUE;
        Bunny closest = null;

        for (Bunny bunny : bunnies) {
            if (bunny.isAlive) {
                float distance = (float) Math.sqrt(
                        Math.pow(x - bunny.x, 2) + Math.pow(y - bunny.y, 2)
                );
                if (distance < closestDistance && distance < HUNT_RADIUS) {
                    closestDistance = distance;
                    closest = bunny;
                }
            }
        }
        targetBunny = closest;
    }

    public void checkCollisions(ArrayList<Bunny> bunnies) {
        for (Bunny bunny : bunnies) {
            if (bunny.isAlive) {
                float distance = (float) Math.sqrt(
                        Math.pow(x - bunny.x, 2) + Math.pow(y - bunny.y, 2)
                );
                if (distance < (WOLF_SIZE + 30) / 2) {
                    bunny.isAlive = false;
                }
            }
        }
    }

    public void draw(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            // Dibujar el lobo con rotación basada en la dirección
            float angle = (float) Math.toDegrees(Math.atan2(speedY, speedX));
            canvas.save();
            canvas.rotate(angle, x + WOLF_SIZE/2, y + WOLF_SIZE/2);
            canvas.drawBitmap(bitmap, x, y, null);
            canvas.restore();
        }
    }
}