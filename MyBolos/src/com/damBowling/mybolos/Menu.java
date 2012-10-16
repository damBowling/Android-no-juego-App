package com.damBowling.mybolos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Menu extends Activity implements OnClickListener{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.menu);
	   
	    //se crean y se configuran los escuchadores para los botones.
	    View estadisticasBoton = findViewById(R.id.estadisticaBtn);
	    estadisticasBoton.setOnClickListener(this);
	    
	    
	    
	    
	    
	}
	
	
	//metodo que define lo que haran los botones al ser pulsados.
	public void onClick(View v) {
		
		switch (v.getId()){
		
		case R.id.estadisticaBtn:
			Intent i = new Intent (this, Estadisticas.class);
			startActivity(i);
		break;
		
		}
		
	}

}
