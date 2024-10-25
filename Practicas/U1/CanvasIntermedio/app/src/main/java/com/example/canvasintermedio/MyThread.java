package com.example.canvasintermedio;

import android.graphics.Canvas;

public class MyThread extends Thread {
/*
	MySurfaceView myView;
	private boolean running = false;

	public MyThread(MySurfaceView view) {
		myView = view;
	}
	
	public void setRunning(boolean run) {
        running = run;    
	}

	@Override
	public void run() {
		while(running){
			
			Canvas canvas = myView.getHolder().lockCanvas();
			
			if(canvas != null){
				synchronized (myView.getHolder()) {
					myView.drawSomething(canvas);
					//myView.onDraw(canvas);
				}
				myView.getHolder().unlockCanvasAndPost(canvas);
			}
			
			try {
				sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
*/

	static final long FPS = 24;
	private MySurfaceView myView;
	private boolean running = false;

	public MyThread(MySurfaceView view) {
		myView = view;
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;
		while (running) {
			Canvas c = null;
			startTime = System.currentTimeMillis();
			try {
				c = myView.getHolder().lockCanvas();
				synchronized (myView.getHolder()) {
					//myView.onDraw(c);
					myView.drawSomething(c);
				}
			} finally {
				if (c != null) {
					myView.getHolder().unlockCanvasAndPost(c);
				}
			}
			sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			} catch (Exception e) {}
		}
	}


}
