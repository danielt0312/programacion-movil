package com.canvas;

/*  FUente: 
 * http://blog.vidasconcurrentes.com/android/detectando-drag-drop-en-un-canvas-de-android-parte-ii/
 */

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		// want fullscreen, we hide Activity's title and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(new DragAndDropView(this));
	}



}
