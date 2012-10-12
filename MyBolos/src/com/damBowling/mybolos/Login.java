package com.damBowling.mybolos;

import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sax.engines.LoginController;
import com.sax.modelos.LoginModel;

public class Login extends Activity {

	SharedPreferences prefs;
	EditText email;
	EditText pass;
	Button entrar;
	TelephonyManager telefono;
	private String webservice;
	private Vector<LoginModel> data;
	String id;
	Resources res;

	/** Called when the activity is first created. */
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * Este metodo inicializa todos los objetos y prepara la ejecuci�n
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// Declaraci�n e inicializaci�n de las variables y objetos//
		prefs = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
		email = (EditText) findViewById(R.id.emailT);
		pass = (EditText) findViewById(R.id.passT);
		entrar = (Button) findViewById(R.id.button1);
		telefono = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		res = getResources();
		//Hacemos un res.getString() y obtenemos el String almacenado en el XML de strings de la url del servidor
		webservice = res.getString(R.string.server)+"bowling/bjparser/webservices/getLogin.php";
		// Fin de la declaraci�n e inicializaci�n de variables y objetos//
		entrar.setOnClickListener(new OnClickListener() {

			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 * Al hacer click se llama al metodo login()
			 */
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logIn();
			}
		});
	}

	/**
	 * Este m�todo comprueba si ya se ha iniciado sesi�n y redirige a la
	 * pantalla de men�
	 */
	public void checkIfLoggedIn() {
		// Comprueba si se ha iniciado sesi�n, de ser asi se procede a cambiar a
		// men�
		if (prefs.getBoolean("loggedIn", false)) {
			Intent i = new Intent(Login.this, Menu.class);
			startActivity(i);
			finish();
		}
	}

	/**
	 * Este m�todo comprueba nombre de usuario y contrase�a y tras logear,
	 * autenticar y escribir en el sharedPrefs redirige a men�
	 */
	public void logIn() {
		// Se prepara el editor del SharedPreferences

		/*
		 * Ahora se comprobar� si los campos est�n correctos
		 */
		boolean comprobado = true;
		if (email.getText().toString().equals("")) {
			comprobado = false;
			Toast.makeText(getApplicationContext(), "Nombre de usuario Vacio",
					Toast.LENGTH_SHORT).show();
			if (email.getText().toString().contains("@")) {
				comprobado = false;
				Toast.makeText(
						getApplicationContext(),
						"Recuerde que esto es el nombre de usuario no el correo",
						Toast.LENGTH_SHORT).show();
			}
		} else if (pass.getText().toString().equals("")) {
			comprobado = false;
			Toast.makeText(getApplicationContext(), "Contrase�a Vacio",
					Toast.LENGTH_SHORT).show();
		}
		// Se comprueba que cumple con los requisitos//
		if (comprobado) {
			//Una vez comprobados los requisitos, se ejecuta el metodo que llama al WebService
			executeWebService();
		}
	}

	/**
	 * 
	 * Este m�todo ejecuta en un Thread toda la logica del parser XML, inicia sesi�n, obtiene el IMEI y guarda los datos en el sharedPrefs
	 * 
	 */
	public void executeWebService() {
		final Handler crearToast = new Handler(){
			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 * Este metodo crea un toast y es llamado cuando el nombre o contrase�a es incorrecto
			 */
			@Override
			public void handleMessage(Message msg0){
				Toast.makeText(getApplicationContext(), "Nombre de usuario o contrase�a erroneas", Toast.LENGTH_LONG).show();
			}
		};
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					// Obtener el imei del telefono
					String imei = telefono.getDeviceId();
					String finalImei = "";
					//Obtener los ultimos 4 caracteres del imei
					for(int a=imei.length()-1;a>imei.length()-5;a--){
							finalImei = finalImei + Character.toString(imei.charAt(a));
					}
					//Montamos la URL y le pasamos los par�metros.
					URL url = new URL(webservice + "?user="
							+ email.getText().toString() + "&pass="
							+ pass.getText().toString()+"&clave="+finalImei);

					// Creamos el parseador SAX
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					// Creamos el lector de XML
					XMLReader xr = sp.getXMLReader();
					// Utilizamos nuestro propio parseador (CategoryHandler)
					LoginController myExampleHandler = new LoginController();
					xr.setContentHandler(myExampleHandler);
					// Creamos un Stream de lectura (input)
					// Adem�s iniciamos la petici�n al WS incluyendo todos los datos
					InputSource is = new InputSource(url.openStream());
					// Le indicamos la codificaci�n para evitar errores
					is.setEncoding("UTF-8");
					xr.parse(is);

					// Asignamos al vector categories los datos parseados
					data = myExampleHandler.getParsedData();

					//Creamos un objeto de LoginData y obtenemos el id, que sera un numero cuando sea correcto y ser� "Error" cuando el acceso sea incorrecto					
					for (int a = 0; a < data.size(); a++) {
						LoginModel loginData = data.get(a);
						id = loginData.getId();
						System.out.println(id);
					}

				} catch (Exception e) {
					// Ha ocurrido alg�n error
					Log.e("Ideas4All", "Error", e);
				}
				//Cuando se detecta que el ID no es "error" que es lo que retorna el xml cuando se falla el login se procede a guardar los datos e invocar el men�
				if (!id.equals("error")) {
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean("loggedIn", true);
					editor.putString("id", id);
					//Guardar los datos en el SharedPreferences
					editor.commit();
					//Llamar al m�todo que comprueba si el "loggedIn" est� en true, obtiene la id y se dirige a men�
					checkIfLoggedIn();
				}else{
					crearToast.sendEmptyMessage(0);
				}
			}
		}).start();
	}
}