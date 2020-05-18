package com.selenium.twitter.Controlador;

import configurations.controller.*;

public class IniciaSesion {
	
	private String username;
	private String password;
	private DriverController dr;
	private static final String TWITTER_URL_LOGIN = "https://twitter.com/login";
	private RobotController robot;
	
	
	public IniciaSesion(DriverController dr,String username, String password, RobotController robot) {
		this.dr = dr;
		this.username = username;
		this.password = password;
		this.robot = robot;
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
			System.out.println("Escribir usuario");
			dr.inputWrite(0, "js-username-field", username,110);
			//Insertar el password
			System.out.println("Escribir contraseña");
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
			System.out.println("Escribir usuario");
			dr.inputWrite(1,"/html/body/div/div/div/div[1]/main/div/div/form/div/div[1]/label/div[2]/div/input",username,110);
			//Insertar el password
			System.out.println("Escribir contraseña");
			dr.inputWrite(1,"/html/body/div/div/div/div[1]/main/div/div/form/div/div[2]/label/div[2]/div/input",password,110);
			//Presionar boton de inicio de sesi�n 
			dr.clickButton(1,"/html/body/div/div/div/div[1]/main/div/div/form/div/div[3]/div/div","Iniciar Sesion XPath");

		}else if(dr.searchElement(1, "/html/body/div/div/div/div[2]/main/div/div/form/div/div[1]/label/div[2]/div/input") != 0) {
			System.out.println("Escribir usuario");
			dr.inputWrite(1,"html/body/div/div/div/div[2]/main/div/div/form/div/div[1]/label/div[2]/div/input",username,110);
			
			System.out.println("Escribir contraseña");
			dr.inputWrite(1, "/html/body/div/div/div/div[2]/main/div/div/form/div/div[2]/label/div[2]/div/input", password, 110);
			
			dr.clickButton(1, "/html/body/div/div/div/div[2]/main/div/div/form/div/div[3]/div", "Click Button xPath");
		}else if(dr.searchElementAppium(1, "/html/body/div/div/div/div[1]/main/div/div/form/div/div[1]/label/div/div[2]/div/input") != 0) {
			System.out.println("Escribir usuario");
			dr.inputWrite(1, "/html/body/div/div/div/div[1]/main/div/div/form/div/div[1]/label/div/div[2]/div/input", username, 110);
			
			System.out.println("Escribir contraseña");
			dr.inputWrite(1, "/html/body/div/div/div/div[1]/main/div/div/form/div/div[2]/label/div/div[2]/div/input", password, 110);
			
			if(dr.searchElement(1, "/html/body/div/div/div/div[1]/main/div/div/form/div/div[3]/div") != 0) {
				dr.clickButton(1, "/html/body/div/div/div/div[1]/main/div/div/form/div/div[3]/div", "button iinit sesion xPath");
			}else if(dr.searchElement(1, "//input[@type='Iniciar sesión']") != 0) {
				dr.clickButton(1, "//input[@type='Iniciar sesión']", "Iniciar sesion xPath");
			}
		}else {
			System.out.println("Escribir usuario");
			robot.inputWrite(username);
			
			Thread.sleep(850);
			
			robot.pressTab();
			
			Thread.sleep(850);
			System.out.println("Escribir contraseña");
			robot.inputWrite(password);

			Thread.sleep(850);
			
			robot.enter();
		}
		
	}

}
