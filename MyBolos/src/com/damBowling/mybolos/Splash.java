package com.damBowling.mybolos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

public class Splash extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Esto quita la action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        /*
         * Se crea un Thread para contar un segundo y a continuación lanzar el siguiente activity (es decir un delay)
         * */
        new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent i = new Intent(Splash.this, Login.class);
				startActivity(i);
			}
		}).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }
}
