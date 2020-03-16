
package com.selenium.twitter.Controlador;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;
import configurations.controller.*;
import org.openqa.selenium.ElementClickInterceptedException;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

import com.selenium.twitter.Modelo.Post;
import com.selenium.twitter.Modelo.*;

/**
 * 
 * @author Luis Morales
 * @version 1.0.0
 */
public class InicioController {
	
	private static final String URL_TWITTER = "https://twitter.com/";
	private static final String PATH_IMAGES_SIKULI = "C:\\ImagenesSikuli\\";
	protected static final String PATH_IMAGES_SFTP = "C:\\imagesSftp\\";
	private DriverController drive;
	private Post po = new Post();
	private User users;
	private List<String> listCheckBoxUsers = new ArrayList<>();
	private RobotController robot;
	private int idUser;
	private int categoria_id;
	private int tasks_grid_id;
	private int taskModelId;
	private String phrase;
	private String image;
	private boolean isPublication;
	private boolean userBlock;
	private Screen screen;
	private Date date = new Date();
	private SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 

	public InicioController(int categoria_id, List<String> listUsers, Screen screen) {
		this.listCheckBoxUsers = listUsers;
		this.categoria_id = categoria_id;
		this.screen = screen;
	}


	/**
	 * Inicia el vpn, conecta y luego ingresa al instagram hasta que termine las publicaciones
	 * 
	 * @author Luis Morales
	 * @version 1.0.0
	 * @throws InterruptedException
	 * @throws AWTException
	 * @throws SQLException
	 * @throws MalformedURLException 
	 * @throws IOException
	 */
	public void init() throws InterruptedException, SQLException, MalformedURLException {
		int usuariosAProcesar = 0;
		Task_Grid taskG = new Task_Grid();
		taskG.setCategories_id(this.categoria_id);
		List<Task_Grid> listTask = taskG.getTaskGridToday();
		if(listTask.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Ya no quedan tareas para hoy");
		}else {
			for (String str : listCheckBoxUsers) {
				VpnController vpn = null;
				userBlock = false;
				usuariosAProcesar++;
				System.out.println(usuariosAProcesar+" usuario(s) de "+listCheckBoxUsers.size());
				users = new User();
				users.setUsername(str);
				users.setEmail(str);
				users = users.getUser();
				System.out.println("*********************"+users.getUsername()+"***********************");
				if(!users.isBlock()) {
					idUser = users.getUsers_id();
					po.setUsers_id(idUser);
					taskG = new Task_Grid();
					taskG = taskG.getTaskForUser(idUser);
					
					if(taskG != null) {
						String dateCu = simpleFormat.format(date);
						while(dateCu.compareTo(taskG.getDate_publication()) < 0) {
							Thread.sleep(5000);
							date = new Date();
							dateCu = simpleFormat.format(date);
						}
						
						phrase = taskG.getPhrase();
						image = taskG.getImage();
						isPublication = taskG.isPublication();
						tasks_grid_id = taskG.getTasks_grid_id();
						
						int idlistTask = po.getLastsTasktPublic();
						
						if(idlistTask == 0) {
							System.out.println("El usuario no tiene mas tareas por publicar");
						}else {
							robot = new RobotController();
							
							String ip = validateIP();
							
							String ipActual = "01.02.03.04";
							if(users.getVpn_id() != 0) {
								Vpn v = new Vpn();
								v.setVpn_id(users.getVpn_id());
								v = v.getVpn();
								vpn = new VpnController(v.getName());
								vpn.connectVpn();
								ipActual = validateIP();
							}
							//Valida si la vpn conecto
							if(ip.equals(ipActual)) {
								System.err.println("El usuario "+users.getUsername()+ " no se puedo conectar a la vpn");
								usuariosAProcesar++;
							}else {
								//Setear valores a google Chrome
								drive = new DriverController();	
								drive.optionsChrome();
								//Lanzamiento de la pagina   
								drive.goPage(URL_TWITTER); 
								
								Thread.sleep(2000);
								
								IniciaSesion sesion = new IniciaSesion(drive,users.getUsername(),users.getPassword(),robot);
								sesion.init();
								//Esperar que cargue la pagina para que cargue el dom completamente
								Thread.sleep(5500); 
								if(!validateUserBlock()) {
									startAfterInitSesion(idlistTask);
									System.out.println("Se cerro la sesión del usuario "+users.getUsername());
									
								}//fin del else
								//quit drive
								if(drive != null) drive.quit();
								
								//Desconectar la vpn para el siguiente usuario
								if(vpn != null) vpn.disconnectVpn();
								
								Thread.sleep(2000);
							}//Fin del if si se conecto a la ip
						}//Fin del if de validar si hay tareas
					}
				}else {
					System.out.println("Usuario bloqueado");
				}
				
			}//Fin del for
		}
		
		System.out.println("Finalizo con exito el programa");
		System.exit(1);
	}//
			
	
	private boolean validateUserBlock() {
		if(drive.searchElement(1,"//*[text()[contains(.,'Verifica tu identidad')]]") != 0 
				&& drive.searchElement(1, "//*[text()[contains(.,'Tu número de teléfono termina')]]") != 0) {
			userBlocked("Pide verificación de identidad por numero de telefono");
			System.out.println("El usuario "+users.getUsername()+" esta bloqueado");
			return true;
		}else if(drive.searchElement(1, "/html/body/div[2]/div/div/span") != 0
				|| drive.searchElement(1, "//*[text()[contains(.,'El nombre de usuario y la contraseña que ingresaste no coinciden con nuestros registros.')]]") != 0) {
			System.out.println("El nombre de usuario  o contraseña no corresponde");
			return true;
		}else if(drive.searchElement(1, "/html/body/div[2]/div/div[1]") != 0
				|| drive.searchElement(1, "/html/body/div[2]/div/form/input[6]") != 0) {
			userBlocked("We've temporarily limited some of your account features");
			System.out.println("El usuario "+users.getUsername()+" esta bloqueado");
			return true;
		}
		
		return false;
	}
	private void startAfterInitSesion(int idListTask) throws InterruptedException, SQLException {
		System.out.println("Usuario logueado");
		if(drive.searchElement(1, "/html/body/div/div/div/div[1]/div/div/div/div/div[2]/div[2]/div/div[1]/div[1]/div/span/span/span") != 0 
				|| drive.searchElement(1, "/html/body/div/div/div/div[1]/div/div/div/div/div[2]/div[2]/div/div[2]/div") != 0
				|| drive.searchElement(1, "//*[text()[contains(.,'Some updates you should know about')]]") !=0) {
			drive.clickButton(1, "/html/body/div/div/div/div[1]/div/div/div/div/div[2]/div[2]/div/div[2]/div", "Ok en terminos y condiciones");
		}
		
		Task_Model_Detail tmd = new Task_Model_Detail();
		tmd.setTasks_model_id(idListTask);
		List<Integer> listTask = tmd.getTaskModelDetailDiferent();
		System.out.println("Buscando tarea para que el usuario realice");
		taskModelId = idListTask;
		random(listTask);
		
		// Darle al panel de opciones
		if(drive.searchElement(1, "//*[@data-testid='AppTabBar_More_Menu']") != 0) {
			drive.clickButton(1, "//*[@data-testid='AppTabBar_More_Menu']", "");
		}else if (drive.searchElement(1, "/html/body/div/div/div/div/header/div/div/div/div/div[2]/nav/div") != 0) {
			drive.clickButton(1, "/html/body/div/div/div/div/header/div/div/div/div/div[2]/nav/div","Menu");
		} 

		Thread.sleep(getNumberRandomForSecond(2002, 3501));
		// Darle click a la barra de opciones
		if(drive.searchElement(1, "//*[@data-testid='logout']") != 0) {
			drive.clickButton(1, "//*[@data-testid='logout']", "Logout");
		}else if(drive.searchElement(1, "/html/body/div/div/div/div[1]/div/div/div[2]/div[3]/div/div/div/div/div[13]/a") != 0) {
			drive.clickButton(1, "/html/body/div/div/div/div[1]/div/div/div[2]/div[3]/div/div/div/div/div[13]/a", "Logout xpath");
		}
		

		System.out.println("Se cerro la sesión del usuario " + users.getUsername());
	}
	
	private void random(List<Integer> listTask) throws InterruptedException, SQLException {
		
		for (Integer li : listTask) {
			int mouseScrollNumber = getNumberRandomForSecond(9, 50);
			Thread.sleep(getNumberRandomForSecond(2501, 2654));
			robot.mouseScroll(mouseScrollNumber);
			Thread.sleep(getNumberRandomForSecond(940, 1130));
			robot.mouseScroll(mouseScrollNumber * -1);
			if(!userBlock) {
				switch (li) {
					case 1:
						// Entrar en Editar Perfil
						likeProfile();
						break;
					case 2:
						uploadImage();
						break;
					case 3:
						// Revisar los grupos
						// Ingresar en la seccion de grupos
						reviewMessage();
						break;
					case 4:
						// Publicar en Grupo
						// Ingresar en la seccion de grupos
						reviewNotifaction();
						break;
					case 5:
						// Publicacion final
						if(isPublication) {
							System.out.println("HACER PUBLICACION FINAL");
							SftpController sftp = new SftpController();
							sftp.downloadFileSftp(image);
							uploadImageFinal();
						}
						break;
					case 6:
						publicTweet();
						break;
					case 7:
						followUsers();
						break;
					default:
						break;
				}
			}
			returnHome();
		} // Fin del For
	}
	
	private void likeProfile() throws InterruptedException {
		System.out.println("ENTRAR EN PERFIL Y DAR LIKE A FOTO");
		if(drive.searchElement(1,"//*[@aria-label='Buscar y explorar']") != 0) {
			drive.clickButton(1, "//*[@aria-label='Buscar y explorar']", "Buscar aria-label");
		}else if(drive.searchElement(1, "//*[@data-testid='AppTabBar_Explore_Link']") != 0) {
			drive.clickButton(1, "//*[@data-testid='AppTabBar_Explore_Link']", "Buscar data-testid");
		}
		
		Thread.sleep(getNumberRandomForSecond(2145 ,2458));
		
		User us = new User();
		us.setUsers_id(idUser);
		String usuario = "";
		try {
			us = us.getDifferentRandomUser();
			usuario = us.getUsername();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Escribir el nombre del usuario");
		if(drive.searchElement(1, "//*[@data-testid='SearchBox_Search_Input']") != 0){
			drive.inputWrite(1, "//*[@data-testid='SearchBox_Search_Input']", usuario, 112);
			Thread.sleep(getNumberRandomForSecond(1000, 1245));
			perfilUser();
		}else if(drive.searchElement(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[1]/div[1]/div/div/div/div/div[1]/div[2]/div/div/div/form/div[1]/div/div/div[2]/input")!=0 ) {
			drive.inputWrite(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[1]/div[1]/div/div/div/div/div[1]/div[2]/div/div/div/form/div[1]/div/div/div[2]/input", usuario, 112);
			Thread.sleep(getNumberRandomForSecond(1000, 1245));
			perfilUser();
		}
		
		Thread.sleep(getNumberRandomForSecond(1250, 1540));
		
	}
	
	private void perfilUser() {
		if(drive.searchElement(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[1]/div[1]/div/div/div/div/div[1]/div[2]/div/div/div/form/div[2]/div/div[3]/div[1]") != 0) {
			try {
				drive.clickButton(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[1]/div[1]/div/div/div/div/div[1]/div[2]/div/div/div/form/div[2]/div/div[3]/div[1]","Buscar name xPath");
				System.out.println("Ingreso en el perfil");
				Thread.sleep(getNumberRandomForSecond(1249, 1540));
				if(drive.searchElement(1, "//*[text()[contains(.,'Seguir')]]") != 0) {
					drive.clickButton(1, "//*[text()[contains(.,'Seguir')]]", "Seguir usuario");
				}else if(drive.searchElement(1, "//*[text()[contains(.,'Follow')]]") != 0) {
					drive.clickButton(1, "//*[text()[contains(.,'Follow')]]", "Follow user");
				}
				Thread.sleep(getNumberRandomForSecond(1249, 1540));
				drive.clickButton(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[2]/div/div/nav/div[2]/div[3]/a", "Multimedia xPath");
				System.out.println("Multimedia");
				Thread.sleep(getNumberRandomForSecond(3249, 3540));
				robot.mouseScroll(-12);
				Thread.sleep(getNumberRandomForSecond(789, 987));
				robot.mouseScroll(12);
				Thread.sleep(getNumberRandomForSecond(1250, 1540));
				
				System.out.println("Contar cantidad de fotos");
				int quantityPhoto = drive.getQuantityElements(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[2]/div/div/div[2]/section/div/div/div/div");
				
				if(quantityPhoto < 1) {
					System.out.println("No tiene fotos el usuario");
				}else {
					int randomPhoto = getNumberRandomForSecond(1, quantityPhoto);
					
					try {
						drive.clickButton(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[2]/div/div/div[2]/section/div/div/div/div["+randomPhoto+"]/div/article", "Foto random xPath");
						System.out.println("Pulsar foto "+randomPhoto);
						Thread.sleep(getNumberRandomForSecond(3250, 3540));
						robot.mouseScroll(-5);
						Thread.sleep(getNumberRandomForSecond(789, 987));
						robot.mouseScroll(5);
						Thread.sleep(getNumberRandomForSecond(1250, 1540));
						try {
							drive.clickButton(1, "//*[@data-testid='like']", "like data-testid");
							Thread.sleep(getNumberRandomForSecond(1250, 1540));
							robot.pressEsc();
							Thread.sleep(getNumberRandomForSecond(1250, 1540));
							System.out.println("Darle like");
						}catch (Exception e) {
						}
					}catch (Exception e) {
					}
				}
				
			}catch (Exception e) {
			}
		}else {
			System.out.println("No se consigue usuario");
		}
	}
	
	private void publicTweet() throws InterruptedException {
		System.out.println("PUBLICAR TWEET NORMAL");
		if(screen.exists(PATH_IMAGES_SIKULI+"tweet-Twitter.png") != null) {
			try {
				screen.click(PATH_IMAGES_SIKULI+"tweet-Twitter.png");
			} catch (FindFailed | IllegalThreadStateException e) {
				System.out.println("No se encontro la imagen");
			}
		}else if(drive.searchElement(1, "//*[@data-testid='SideNav_NewTweet_Button']") != 0) {
			drive.clickButton(1, "//*[@data-testid='SideNav_NewTweet_Button']", "Tweet");
		}else if(drive.searchElement(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a") != 0) {
			drive.clickButton(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a", "Tweet xPath");
		}
		
		String array[] = {"Que bonito día! #followback","Los politicos no sirven -.-","No me lo creo #followback","Que genial idea jajaja #followback",
				"Siganme #followback","¿Quien me quiere y para que? #follow","Alguién por aquí? #follow","Que agradable sujeto","Esa noticia me dejo en shock",
				"Jajajajajajajajajajajaj","Todos vamos al mismo camino.","Es irrelevante eso que me dices","Si no te agrado te vas",
				"Sigueme y te sigo #followback #follow","Tusa","Really?","Cantaclaro","Simon Diaz","No es copia, jamas.",
				"Soy de las personas que prefieren netflix que estar en una rumba","¿Tu mamá sabe que usas el internet para dejarme en visto?",
				"Amo la lluvia","Odio los climas calientes","Perreo catolico","El celular nunca puede ser mas importante que la persona con la que estas comiendo.",
				"No!","O quizás si jejeje","Que cancion tan genial"};
		
		robot.inputWrite(array[getNumberRandomForSecond(0, array.length-1)]);
		
		if(screen.exists(PATH_IMAGES_SIKULI+"twittear-Twitter.png") != null) {
			try {

				screen.click(PATH_IMAGES_SIKULI+"twittear-Twitter.png");
			} catch (FindFailed | IllegalThreadStateException e) {
				System.out.println("No se encontro la imagen");
			}
		}else {
			try {
				if(drive.searchElement(1, "//*[@data-testid='tweetButton']") != 0) {
					drive.clickButton(1, "//*[@data-testid='tweetButton']", "TweetButton publicar");
				}
			}catch(ElementClickInterceptedException | org.openqa.selenium.NoSuchElementException e) {
				robot.pressTab();
				Thread.sleep(156);
				robot.pressTab();
				Thread.sleep(156);
				robot.pressTab();
				Thread.sleep(156);
				robot.enter();
			}
		}
		
		
		if(drive.searchElement(1, "//*[text()[contains(.,'Your account is suspended and is not permitted to send Tweets.')]]") != 0) {
			userBlock = true;
			System.out.println("La cuenta esta suspendida");
			robot.pressEsc();
			Thread.sleep(125);
			robot.pressEsc();
			Thread.sleep(125);
			robot.pressEsc();
			
			if(screen.exists(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png") != null) {
				try {
					screen.click(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png");
				} catch (FindFailed | IllegalThreadStateException e) {
				}
			}
		}else {
			robot.pressEsc();
			Thread.sleep(125);
			robot.pressEsc();
			Thread.sleep(125);
			robot.pressEsc();
			
			if(screen.exists(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png") != null) {
				try {
					screen.click(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png");
				} catch (FindFailed | IllegalThreadStateException e) {
				}
			}
		}

	}
	
	private void followUsers() throws InterruptedException {
		String[] hashtag = {"#followforfollow","#followback","#followme","#follow4follow","#FollowPyramid","#TEAMFOLLOWBACK",
							"#SiguemeYTeSigo","#F4F"};
		if(drive.searchElement(1, "//*[@data-testid='SearchBox_Search_Input']") != 0) {
			drive.inputWrite(1, "//*[@data-testid='SearchBox_Search_Input']", hashtag[getNumberRandomForSecond(0, hashtag.length-1)], 114);
			Thread.sleep(1450);
			robot.enter();
			
			Thread.sleep(5050);
			if(drive.searchElement(1, "//*[text()[contains(.,'Personas')]]") != 0 || drive.searchElement(1, "//*[text()[contains(.,'People')]]") != 0) {
				if(drive.searchElement(1, "/html/body/div/div/div/div[2]/main/div/div/div/div/div/div[1]/div[2]/nav/div[2]/div[3]/a") != 0) {
					drive.clickButton(1, "/html/body/div/div/div/div[2]/main/div/div/div/div/div/div[1]/div[2]/nav/div[2]/div[3]/a", "Personas xPath 1");
				}else if(drive.searchElement(1, "//*[text()[contains(.,'Personas')]]") != 0) {
					drive.clickButton(1, "//*[text()[contains(.,'Personas')]]", "Personas xPath");
				}else if(drive.searchElement(1, "//*[text()[contains(.,'People')]]") != 0) {
					drive.clickButton(1, "//*[text()[contains(.,'People')]]", "People xPath");
				}
				
				Thread.sleep(1240);
				//html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div["+i+"]/div/div/div/div[2]/div[1]/div[1]/a/div/div[1]/div[1]/span/span
				//html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div["+i+"]/div/div/div/div[2]/div[1]/div[2]/div
				//[2]/div/div/div/div[2]/div[1]/div[2]/div
				int quantityUsers = drive.getQuantityElements(1, "/html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div") -3;
				
				if(quantityUsers < 1) {
					System.out.println("No hay usuarios para seguir");
				}else {
					for(int i = 1; i<=quantityUsers; i++) {
						if(drive.searchElement(1, "/html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div["+i+"]/div/div/div/div[2]/div[1]/div[2]/div") != 0) {
							try {
								drive.clickButton(1, "/html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div["+i+"]/div/div/div/div[2]/div[1]/div[2]/div", "Follow user "+i);
								
								System.out.println("El usuario a seguir es: "+drive.getText(1, "/html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div["+i+"]/div/div/div/div[2]/div[1]/div[1]/a/div/div[1]/div[1]/span/span"));
								
								robot.mouseScroll(1);
							}catch(ElementClickInterceptedException e) {	
							}

						}
						Thread.sleep(695);
					}
				}
			}else {
				robot.pressTab();
				Thread.sleep(100);
				robot.pressTab();
				Thread.sleep(100);
				robot.pressTab();
				Thread.sleep(100);
				robot.pressTab();
				Thread.sleep(100);
				robot.pressTab();
				Thread.sleep(100);
				robot.pressTab();
				Thread.sleep(5000);
				
				int quantityUsers = drive.getQuantityElements(1, "/html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div") -3;
				
				if(quantityUsers < 1) {
					System.out.println("No hay usuarios para seguir");
				}else {
					for(int i = 1; i<=quantityUsers; i++) {
						if(drive.searchElement(1, "/html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div["+i+"]/div/div/div/div[2]/div[1]/div[2]/div") != 0) {
							drive.clickButton(1, "/html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div["+i+"]/div/div/div/div[2]/div[1]/div[2]/div", "Follow user "+i);
							
							System.out.println("El usuario a seguir es: "+drive.getText(1, "/html/body/div/div/div/div/main/div/div/div/div[1]/div/div[2]/div/div/section/div/div/div/div["+i+"]/div/div/div/div[2]/div[1]/div[1]/a/div/div[1]/div[1]/span/span"));
						}
						Thread.sleep(895);
					}
				}
				
			}
		}
	}
	
	/**
	 * Publicar imagen normal 
	 * 
	 * @author Luis Morales
	 * @version 1.0.0
	 * @param pie el pie de foto a escribir
	 */
	private void uploadImage() throws InterruptedException, SQLException {
		System.out.println("SUBIR IMAGEN NORMAL TIPO MEME, COMIDA, ETC.");
		Sub_Categorie subC = new Sub_Categorie();
		
		if(screen.exists(PATH_IMAGES_SIKULI+"tweet-Twitter.png") != null) {
			try {
				screen.click(PATH_IMAGES_SIKULI+"tweet-Twitter.png");
			} catch (FindFailed | IllegalThreadStateException e) {
			}
		}else if(drive.searchElement(1, "//*[@data-testid='SideNav_NewTweet_Button']") != 0) {
			drive.clickButton(1, "//*[@data-testid='SideNav_NewTweet_Button']", "Tweet");
		}else if(drive.searchElement(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a") != 0) {
			drive.clickButton(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a", "Tweet xPath");
		}
		
		Thread.sleep(getNumberRandomForSecond(1254, 1798));
		
		subC = subC.getOneRandom();
		
		if(subC != null) {
			
			Phrases ph = new Phrases();
			ph.setSub_categories_id(subC.getSub_categories_id());
			ph.setCategories_id(subC.getCategories_id());
			System.out.println("sub_categories_id: "+subC.getSub_categories_id()+" categoria_id: "+subC.getCategories_id());
			ph =ph.getPhraseRandomSubCategorie();
			System.out.println("Escribir pie de foto");
			robot.inputWrite(ph.getPhrase());
			Thread.sleep(getNumberRandomForSecond(1546, 1687));
			
			Path_Photo pathPhoto = new Path_Photo();
			pathPhoto.setSub_categories_id(subC.getSub_categories_id());
			pathPhoto.setCategories_id(subC.getCategories_id());
			pathPhoto = pathPhoto.getPathPhotoSubCategories();
			System.out.println("Agregar foto ");
			drive.inputWriteFile(1, "/html/body/div/div/div/div[1]/div/div/div/div/div[2]/div[2]/div/div[3]/div/div/div[1]/div/div/div/div[2]/div[2]/div/div/div[1]/input", pathPhoto.getPath());
			Thread.sleep(getNumberRandomForSecond(3546, 4677));
			
			if(screen.exists(PATH_IMAGES_SIKULI+"twittear-Twitter.png") != null) {
				try {
	
					screen.click(PATH_IMAGES_SIKULI+"twittear-Twitter.png");
				} catch (FindFailed | IllegalThreadStateException e) {
				}
			}else {
				try {
					if(drive.searchElement(1, "//*[@data-testid='tweetButton']") != 0) {
						drive.clickButton(1, "//*[@data-testid='tweetButton']", "TweetButton publicar");
					}
				}catch(ElementClickInterceptedException | org.openqa.selenium.NoSuchElementException e) {
					robot.pressTab();
					Thread.sleep(156);
					robot.pressTab();
					Thread.sleep(156);
					robot.pressTab();
					Thread.sleep(156);
					robot.enter();
				}
			}
			
			
			if(drive.searchElement(1, "//*[text()[contains(.,'Your account is suspended and is not permitted to send Tweets.')]]") != 0) {
				userBlock = true;
				
				robot.pressEsc();
				Thread.sleep(125);
				robot.pressEsc();
				Thread.sleep(125);
				robot.pressEsc();
				
				if(screen.exists(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png") != null) {
					try {
						screen.click(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png");
					} catch (FindFailed | IllegalThreadStateException e) {
						System.out.println("No se encontro la imagen");
					}
				}
			}else {
				robot.pressEsc();
				Thread.sleep(125);
				robot.pressEsc();
				Thread.sleep(125);
				robot.pressEsc();
				
				if(screen.exists(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png") != null) {
					try {
						screen.click(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png");
					} catch (FindFailed | IllegalThreadStateException e) {
						System.out.println("No se encontro la imagen");
					}
				}
			}
			
			
			
			if(drive.searchElement(1, "//*[text()[contains(.,'Your account is suspended and is not permitted to send Tweets.')]]") != 0) {
				userBlock = true;
			}
			Thread.sleep(getNumberRandomForSecond(8546, 8677));
			robot.pressEsc();
			robot.pressEsc();
		}else {
			System.out.println("No hay sub categorias para publicar");
			robot.pressEsc();
		}
	}
	
	private void reviewMessage() throws InterruptedException {
		System.out.println("REVISAR MENSAJES");
		if(drive.searchElement(1, "//*[@aria-label='Mensajes Directos']") != 0) {
			drive.clickButton(1, "//*[@aria-label='Mensajes Directos']", "Mensaje directo aria-label");
		}else if(drive.searchElement(1, "//*[@data-testid='AppTabBar_DirectMessage_Link']") != 0) {
			drive.clickButton(1, "//*[@data-testid='AppTabBar_DirectMessage_Link']", "Mensaje directo data-testid");
		}
		
		Thread.sleep(getNumberRandomForSecond(2145 ,2458));
		robot.mouseScroll(8);
		Thread.sleep(getNumberRandomForSecond(1145 ,1458));
		robot.mouseScroll(-8);
		Thread.sleep(getNumberRandomForSecond(1145 ,1457));
		
		System.out.println("Contar cantidad de notificaciones");
		int quantityMessage = drive.getQuantityElements(1, "/html/body/div/div/div/div/main/div/div/div/div[2]/div[2]/section/div/div/div/div");
		
		if(quantityMessage == 0) {
			System.out.println("No hay mensajes para ver");
		}else {
			int randomMessage = getNumberRandomForSecond(1, quantityMessage);
			System.out.println("Numero de mensaje a ingresar "+randomMessage);
			try {
				drive.clickButton(1, "/html/body/div/div/div/div/main/div/div/div/div[2]/div[2]/section/div/div/div/div["+randomMessage+"]/div/div", "Mensaje xpath random");
				Thread.sleep(getNumberRandomForSecond(2145 ,2458));
				robot.mouseScroll(-8);
				Thread.sleep(getNumberRandomForSecond(1145 ,1458));
				robot.mouseScroll(8);
				Thread.sleep(getNumberRandomForSecond(1145 ,1457));
			}catch (Exception e) {
				e.getStackTrace();
			}
		}
	}
	
	private void reviewNotifaction() throws InterruptedException {
		System.out.println("REVISAR NOTIFICACIONES");
		if(drive.searchElement(1, "//*[@data-testid='AppTabBar_Notifications_Link']") != 0) {
			drive.clickButton(1, "//*[@data-testid='AppTabBar_Notifications_Link']", "Notificaciones elemento data-testid");
		}else if(drive.searchElement(1, "//*[@aria-label='Notificaciones']") != 0) {
			drive.clickButton(1, "//*[@aria-label='Notificaciones']", "Notificaciones aria-label");
		}
		Thread.sleep(getNumberRandomForSecond(2145 ,2458));
		robot.mouseScroll(8);
		Thread.sleep(getNumberRandomForSecond(1145 ,1458));
		robot.mouseScroll(-8);
		Thread.sleep(getNumberRandomForSecond(1145 ,1457));
		
		System.out.println("Contar cantidad de notificaciones");

	}
	
	private void uploadImageFinal() throws InterruptedException {
		System.out.println("SUBIR IMAGEN FINAL");
		if(screen.exists(PATH_IMAGES_SIKULI+"tweet-Twitter.png") != null) {
			try {
				screen.click(PATH_IMAGES_SIKULI+"tweet-Twitter.png");
			} catch (FindFailed | IllegalThreadStateException e) {
				System.out.println("No se encontro la imagen");
			}
		}else if(drive.searchElement(1, "//*[@data-testid='SideNav_NewTweet_Button']") != 0) {
			drive.clickButton(1, "//*[@data-testid='SideNav_NewTweet_Button']", "Tweet");
		}else if(drive.searchElement(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a") != 0) {
			drive.clickButton(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a", "Tweet xPath");
		}
		
		Thread.sleep(getNumberRandomForSecond(1234, 1548));

			robot.inputWrite(phrase);
			

			drive.inputWriteFile(1, "/html/body/div/div/div/div[1]/div/div/div/div/div[2]/div[2]/div/div[3]/div/div/div[1]/div/div/div/div[2]/div[2]/div/div/div[1]/input", PATH_IMAGES_SFTP+image);
			Thread.sleep(getNumberRandomForSecond(4546, 5677));
			
			if(screen.exists(PATH_IMAGES_SIKULI+"twittear-Twitter.png") != null) {
				try {
	
					screen.click(PATH_IMAGES_SIKULI+"twittear-Twitter.png");
				} catch (FindFailed | IllegalThreadStateException e) {
					System.out.println("No se encontro la imagen");
				}
			}else {
				try {
					if(drive.searchElement(1, "//*[@data-testid='tweetButton']") != 0) {
						drive.clickButton(1, "//*[@data-testid='tweetButton']", "TweetButton publicar");
					}
				}catch(ElementClickInterceptedException | org.openqa.selenium.NoSuchElementException e) {
					robot.pressTab();
					Thread.sleep(156);
					robot.pressTab();
					Thread.sleep(156);
					robot.pressTab();
					Thread.sleep(156);
					robot.enter();
				}
			}
			
			
			if(drive.searchElement(1, "//*[text()[contains(.,'Your account is suspended and is not permitted to send Tweets.')]]") != 0) {
				userBlock = true;
				
				robot.pressEsc();
				Thread.sleep(125);
				robot.pressEsc();
				Thread.sleep(125);
				robot.pressEsc();
				
				if(screen.exists(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png") != null) {
					try {
						screen.click(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png");
					} catch (FindFailed | IllegalThreadStateException e) {
						System.out.println("Error al conseguir imagen en pantalla");
					}
				}
			}else {
				robot.pressEsc();
				Thread.sleep(125);
				robot.pressEsc();
				Thread.sleep(125);
				robot.pressEsc();
				
				if(screen.exists(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png") != null) {
					try {
						screen.click(PATH_IMAGES_SIKULI+"descartar_tweet-Twitter.png");
					} catch (FindFailed | IllegalThreadStateException e) {
						System.out.println("Error al conseguir imagen en pantalla");
					}
				}else {
					po.setTasks_grid_id(tasks_grid_id);
					po.setTasks_model_id(taskModelId);
					po.insert();
				}
			}
			
			Thread.sleep(getNumberRandomForSecond(8546, 8677));
			
			

		
	}
	
 
	
	/**
	 * 
	 * Pulsa el botón de retornar al inicio
	 * 
	 * @author Luis Morales
	 * @version 1.0.0
	 * @throws InterruptedException 
	 * 
	 */
	private void returnHome() throws InterruptedException {
		System.out.println("Volver al inicio");
		if(drive.searchElement(1, "//*[@data-testid='AppTabBar_Home_Link']") != 0) {
			drive.clickButton(1, "//*[@data-testid='AppTabBar_Home_Link']", "Home data-testid");
			Thread.sleep(getNumberRandomForSecond(1245, 1478));
		}
	}
	
	
	private void userBlocked(String motive) {
		User_Block userB = new User_Block();
		userB.setUsers_id(idUser);
		userB.setComentario(motive);
		if(userB.getIdUser() == 0) {
			userB.insert();
			drive.close(); 
		}
	}
	
	/**
	 * 
	 * Validar la ip actual del computador
	 * 
	 * @author Luis Morales
	 * @version 1.0.0
	 * @return la ip actual
	 */
	private String validateIP() throws MalformedURLException {
		
		try {

            URL whatismyip = new URL("http://checkip.amazonaws.com");

            BufferedReader in = new BufferedReader(new InputStreamReader(

            whatismyip.openStream()));     
            
            return in.readLine();
            
        } catch ( IOException ex) {

            System.err.println(ex);
            return "190.146.186.130";
        } 
		
	}
	
	private static int getNumberRandomForSecond(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

}
