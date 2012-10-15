package com.damBowling.mybolos;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * Este metodo inicializa todos los objetos y prepara la ejecución
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// Declaración e inicialización de las variables y objetos//
		prefs = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
		email = (EditText) findViewById(R.id.emailT);
		pass = (EditText) findViewById(R.id.passT);
		entrar = (Button) findViewById(R.id.button1);
		telefono = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		res = getResources();
		// Hacemos un res.getString() y obtenemos el String almacenado en el XML
		// de strings de la url del servidor
		webservice = res.getString(R.string.server)
				+ "api/getLogin.php";
		// Fin de la declaración e inicialización de variables y objetos//
		entrar.setOnClickListener(new OnClickListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 * Al hacer click se llama al metodo login()
			 */
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logIn();
			}
		});
		checkIfLoggedIn();
	}

	/**
	 * Este método comprueba si ya se ha iniciado sesión y redirige a la
	 * pantalla de menú
	 */
	public void checkIfLoggedIn() {
		// Comprueba si se ha iniciado sesión, de ser asi se procede a cambiar a
		// menú
		if (prefs.getBoolean("loggedIn", false)) {
			Intent i = new Intent(Login.this, Menu.class);
			startActivity(i);
			finish();
		}
	}

	/**
	 * Este método comprueba nombre de usuario y contraseña y tras logear,
	 * autenticar y escribir en el sharedPrefs redirige a menú
	 */
	public void logIn() {
		// Se prepara el editor del SharedPreferences

		/*
		 * Ahora se comprobará si los campos están correctos
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
			Toast.makeText(getApplicationContext(), "Contraseña Vacio",
					Toast.LENGTH_SHORT).show();
		}
		// Se comprueba que cumple con los requisitos//
		if (comprobado) {
			// Una vez comprobados los requisitos, se ejecuta el metodo que
			// llama al WebService
			executeWebService();
		}
	}

	/**
	 * 
	 * Este método ejecuta en un Thread toda la logica del parser XML, inicia
	 * sesión, obtiene el IMEI y guarda los datos en el sharedPrefs
	 * 
	 */
	public void executeWebService() {
		//Creamos un progress dialog (Espinete dando vueltas que pone iniciando sesión y bloquea la interfaz con un fundido a negro
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("Iniciando sesión...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
		
		final Handler crearToast = new Handler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message) Este
			 * metodo crea un toast y es llamado cuando el nombre o contraseña
			 * es incorrecto
			 */
			@Override
			public void handleMessage(Message msg0) {
				Toast.makeText(getApplicationContext(),
						"Nombre de usuario o contraseña erroneas",
						Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		};
		final Handler noConexion = new Handler() {
			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 * Este método handler crea un Toast cuando es llamado por un fallo en internet
			 */
			@Override
			public void handleMessage(Message msg0) {
				Toast.makeText(getApplicationContext(),
						"Hay un problema con la conexión a internet",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		};
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					// Obtener el imei del telefono
					String imei = telefono.getDeviceId();
					String finalImei = "";
					// Obtener los ultimos 4 caracteres del imei
					for (int a = imei.length() - 1; a > imei.length() - 5; a--) {
						finalImei = finalImei
								+ Character.toString(imei.charAt(a));
					}
					// Montamos la URL y le pasamos los parámetros.
					URL url = new URL(webservice + "?user="
							+ email.getText().toString() + "&pass="
							+ pass.getText().toString() + "&clave=" + finalImei);

					// Creamos el parseador SAX
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					// Creamos el lector de XML
					XMLReader xr = sp.getXMLReader();
					// Utilizamos nuestro propio parseador (CategoryHandler)
					LoginController myExampleHandler = new LoginController();
					xr.setContentHandler(myExampleHandler);
					// Además iniciamos la petición al WS incluyendo todos los
					// datos
					InputSource is = null;
					//Este Try&Catch se emplea para evitar un ForceClose cuando no hay conexión de red o bien el webservice no se encuentra disponible
					try {
						// Creamos un Stream de lectura (input)
						is = new InputSource(url.openStream());
						is.setEncoding("UTF-8");
						xr.parse(is);

						// Asignamos al vector data los datos parseados
						data = myExampleHandler.getParsedData();

						// Creamos un objeto de LoginData y obtenemos el id, que
						// sera un numero cuando sea correcto y será "Error" cuando
						// el acceso sea incorrecto
						for (int a = 0; a < data.size(); a++) {
							LoginModel loginData = data.get(a);
							id = loginData.getId();
							System.out.println(id);
						}
					} catch (IOException ioex) {
						noConexion.sendEmptyMessage(0);
					}
				} catch (Exception e) {
					// Ha ocurrido algún error
					Log.e("Ideas4All", "Error", e);
				}
				// Cuando se detecta que el ID no es "error" que es lo que
				// retorna el xml cuando se falla el login se procede a guardar
				// los datos e invocar el menú
				if (id != null) {
					if (!id.equals("error")) {
						SharedPreferences.Editor editor = prefs.edit();
						editor.putBoolean("loggedIn", true);
						editor.putString("id", id);
						// Guardar los datos en el SharedPreferences
						editor.commit();
						// Llamar al método que comprueba si el "loggedIn" está
						// en true, obtiene la id y se dirige a menú
						checkIfLoggedIn();
					} else {
						crearToast.sendEmptyMessage(0);
					}
				}
			}
		}).start();
	}
}