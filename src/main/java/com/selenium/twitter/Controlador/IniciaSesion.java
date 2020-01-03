package com.selenium.twitter.Controlador;
public class IniciaSesion {
	
	private String username;
	private String password;
	private DriverController dr;
	private final String TWITTER_URL_LOGIN = "https://twitter.com/login";
	
	
	
	public IniciaSesion(DriverController dr,String username, String password) {
		this.dr = dr;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Iniciar sesión en twitter, ingresando el usuario y la contraseña y presionando el boton
	 * 
	 * @author Luis Morales
	 * @version 1.0.0
	 * @throws InterruptedException
	 */
	public void init() throws InterruptedException {
		dr.goPage(TWITTER_URL_LOGIN);
		Thread.sleep(1250);
		if(dr.searchElement(0, "js-username-field") != 0) {
			//Insertar el usuario
			dr.inputWrite(0, "js-username-field", username,110);
			//Insertar el password
			dr.inputWrite(0, "js-password-field",  password ,110);
			//Presionar el boton de sesion
			if(dr.searchElement(1, "/html/body/div[1]/div[3]/div/div/div[1]/form/div[2]/button") != 0) {
				dr.clickButton(1, "/html/body/div[1]/div[3]/div/div/div[1]/form/div[2]/button", "Iniciar Sesion xPath");
			}else if(dr.searchElement(1, "/html/body/div[1]/div[2]/div/div/div[1]/form/div[2]/button") != 0) {
				dr.clickButton(1, "/html/body/div[1]/div[2]/div/div/div[1]/form/div[2]/button", "Iniciar Sesion xPath");
			}else if(dr.searchElement(1, "//input[@type='submit']") != 0) {
				dr.clickButton(1, "//input[@type='submit']", "submit");
			}else if(dr.searchElement(0, "submit") != 0) {
				dr.clickButton(0, "submit", "Iniciar sesion className");
			}
		}else if(dr.searchElement(1, "/html/body/div/div/div/div[1]/main/div/div/form/div/div[1]/label/div[2]/div/input") != 0) {
			//Insertar el usuario
			dr.inputWrite(1,"/html/body/div/div/div/div[1]/main/div/div/form/div/div[1]/label/div[2]/div/input",username,110);
			//Insertar el password
			dr.inputWrite(1,"/html/body/div/div/div/div[1]/main/div/div/form/div/div[2]/label/div[2]/div/input",password,110);
			//Presionar boton de inicio de sesi�n 
			dr.clickButton(1,"/html/body/div/div/div/div[1]/main/div/div/form/div/div[3]/div/div","Iniciar Sesion XPath");

		}else if(dr.searchElement(1, "/html/body/div/div/div/div[2]/main/div/div/form/div/div[1]/label/div[2]/div/input") != 0) {
			dr.inputWrite(1,"html/body/div/div/div/div[2]/main/div/div/form/div/div[1]/label/div[2]/div/input",username,110);
			
			dr.inputWrite(1, "/html/body/div/div/div/div[2]/main/div/div/form/div/div[2]/label/div[2]/div/input", password, 110);
			
			dr.clickButton(1, "/html/body/div/div/div/div[2]/main/div/div/form/div/div[3]/div", "Click Button xPath");
		}
		
	}

}
