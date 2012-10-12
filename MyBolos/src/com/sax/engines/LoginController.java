package com.sax.engines;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sax.modelos.LoginModel;

public class LoginController extends DefaultHandler {

	public Vector<LoginModel> myParsedCategoryDataSet;

	public LoginController() {
		super();
		this.myParsedCategoryDataSet = new Vector<LoginModel>();
	}

	private boolean in_usuario = false;
	private boolean in_id = false;
	// En esta variable guardamos el texto encontrado entre las etiquetas
	StringBuilder builder;
	// Aquí guardamos cada objeto categoria
	private LoginModel DataSet;

	public Vector<LoginModel> getParsedCategoryDataSets() {
		return this.myParsedCategoryDataSet;
	}

	public Vector<LoginModel> getParsedData() {
		return this.myParsedCategoryDataSet;
	}
	
	@Override
    public void startDocument() throws SAXException {
         // Comenzamos a leer el fichero xml, creamos el vector donde se guardarán las categorías
         this.myParsedCategoryDataSet = new Vector<LoginModel>();
    }
	@Override
    public void endDocument() throws SAXException {
         // Ha terminado de leer el fichero, en este paso no hacemos nada
    }     
 
    @Override
    public void startElement(String namespaceURI, String localName,
              String qName, Attributes atts) throws SAXException {
         if (localName.equals("usuario")) {
             // Ha encontrado la etiqueta principal de cada elemento "category"
             // Creamos un nuevo objeto categoría donde iremos guardando los datos
             this.in_usuario = true;
             DataSet = new LoginModel();
         }else if (localName.equals("id")) {
             // Estamos dentro de la etiqueta "id", creamos el StringBuilder que utilizaremos
             // en el método characters para guardar el contenido
             this.in_id = true;
             builder = new StringBuilder();
         }
    } 
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
              throws SAXException {
        if (localName.equals("usuario")) {
            // Hemos llegado al final de la etiqueta principal de cada elemento "category"
            // Añadimos al vector el elemento leído
            this.in_usuario = false;
            myParsedCategoryDataSet.add(DataSet);
        }else if (localName.equals("id")) {
            // Ha encontrado la etiqueta de cierre de "id"
            this.in_id = false;
        }
    } 
    @Override
    public void characters(char ch[], int start, int length) {
 
       // Si estamos dentro de la etiqueta "nombre"
       if(this.in_id){
       	   if (builder!=null) {
       	        for (int i=start; i<start+length; i++) {
                    // Añadimos al StringBuilder (definido al encontrar el comienzo de la etiqueta "id")
                    // lo que haya entre las etiquetas de inicio y fin
       	            builder.append(ch[i]);
       	        }
       	   }        	 
           // Lo asignamos al "id" del objeto categoría (DataSet)
       	   DataSet.setId(builder.toString()); 
       }  
 
   } 
}
