
package com.selenium.twitter.Controlador;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.selenium.twitter.Modelo.Genere;
import com.selenium.twitter.Modelo.Post;
import com.selenium.twitter.Controlador.RobotController;
import com.selenium.twitter.Modelo.Task_Model_Detail;
import com.selenium.twitter.Modelo.HashTag;
import com.selenium.twitter.Modelo.Post_Detail;
import com.selenium.twitter.Controlador.DriverController;
import com.selenium.twitter.Modelo.*;

/**
 * 
 * @author Luis Morales
 * @version 1.0.0
 */
public class InicioController {
	
	private static final String URL_TWITTER = "https://twitter.com/";
	private static DriverController drive;
	private Post po = new Post();
	private static User users;
	private VpnController vp;
	private RobotController robot;
	private List<JCheckBox> usuarios;
	private List<JTextArea> pieDeFoto = new ArrayList<JTextArea>();
	private List<List<String>> checkBoxHashTag = new ArrayList<List<String>>();
	private List<JComboBox<String>> comboBoxGenere = new ArrayList<JComboBox<String>>();
	private List<JTextField> listUsers = new ArrayList<JTextField>();
	private boolean banderaVpn = false;
	private int count  = 0;
	private int ini = 0;
	private int users_id;
	private int categoria_id;
	private int idGenere;
	private int usuariosAProcesar;
	 
	

	public InicioController(int categoria_id, List<JCheckBox> listCheckBoxUsersSend, List<JTextArea> listTextARea,
			List<List<String>> listChechBoxSelected, List<JTextField> listTextFieldUser,
			List<JComboBox<String>> listJComboBoxGenere) {
		this.categoria_id = categoria_id;
		this.usuarios = listCheckBoxUsersSend;
		this.pieDeFoto = listTextARea;
		this.checkBoxHashTag = listChechBoxSelected;
		this.listUsers = listTextFieldUser;
		this.comboBoxGenere = listJComboBoxGenere;
	}


	/**
	 * Inicia el vpn, conecta y luego ingresa al instagram hasta que termine las publicaciones
	 * 
	 * @author Luis Morales
	 * @version 1.0.0
	 * @throws InterruptedException
	 * @throws AWTException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void init() throws InterruptedException, AWTException, SQLException, IOException {
		count = comboBoxGenere.size() - 1;
		usuariosAProcesar = 0;
		
		for (JCheckBox jCheckBox : usuarios) {
			usuariosAProcesar++;
			users = new User();
			users.setUsername(jCheckBox.getText());
			users.setEmail(jCheckBox.getText());
			users = users.getUser();
			users_id = users.getUsers_id();
			po.setUsers_id(users_id);
			drive = null;
			String generes = (String) comboBoxGenere.get(ini).getSelectedItem();
			Genere gene = new Genere();
			gene.setName(generes);
			idGenere = gene.getIdGenere();
			robot = new RobotController();
			System.out.println(usuariosAProcesar+" usuario(s) de "+usuarios.size()+" usuario(s)");
			int idlistTask = po.getLastsTasktPublic();
			
			if(idlistTask == 0) {
				System.out.println("El usuario no tiene mas tareas por publicar");
			}else {
				String ip = validateIP();
				vp = new VpnController(robot);
				Vpn v = new Vpn();
				v.setVpn_id(users.getVpn_id());
				v = v.getVpn();
				vp.iniciarVpn(v.getName(), banderaVpn);
				String ipActual = validateIP();
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
					
					IniciaSesion sesion = new IniciaSesion(drive,users.getUsername(),users.getPassword());
					sesion.init();
					//Esperar que cargue la pagina para que cargue el dom completamente
					Thread.sleep(5500); 
					System.out.println("*********************"+users.getUsername()+"***********************");
					if(!validateUserBlock()) {
						startAfterInitSesion(idlistTask);
						System.out.println("Se cerro la sesión del usuario "+users.getUsername());
						
					}//fin del else
					//quit drive
					if(drive != null) drive.quit();
					//Desconectar la vpn para el siguiente usuario
					if(vp != null) vp.desconectVpn();
					
					banderaVpn = true;
					Thread.sleep(2000);
				}//Fin del if si se conecto a la ip
			}//Fin del if de validar si hay tareas
		}//Fin del for
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
		random(listTask, idListTask);
		
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
	
	private void random(List<Integer> listTask, int idListTask) throws InterruptedException, SQLException {
		int taskModelId = idListTask;
		for (Integer li : listTask) {
			int mouseScrollNumber = getNumberRandomForSecond(9, 50);
			Thread.sleep(getNumberRandomForSecond(2501, 2654));
			robot.mouseScroll(mouseScrollNumber);
			Thread.sleep(getNumberRandomForSecond(940, 1130));
			robot.mouseScroll(mouseScrollNumber * -1);
			switch (li) {
				case 1:
					// Entrar en Editar Perfil
					System.out.println("ENTRAR EN PERFIL Y DAR LIKE A FOTO");
					likeProfile();
					break;
				case 2:
					System.out.println("SUBIR IMAGEN NORMAL TIPO COMIDA, MEME, BEBIDA");
					uploadImage();
					break;
				case 3:
					// Revisar los grupos
					// Ingresar en la seccion de grupos
					System.out.println("ENTRAR EN MENSAJES");
					reviewMessage();
					break;
				case 4:
					// Publicar en Grupo
					// Ingresar en la seccion de grupos
					reviewNotifaction();
					break;
				case 5:
					// Publicacion final
					if(categoria_id != 1) {
						System.out.println("HACER PUBLICACION FINAL");
						String pieFoto = pieDeFoto.get(ini).getText();
						String user = listUsers.get(ini).getText();
						List<String> hashTag = checkBoxHashTag.get(ini);
						String hash = uploadImageFinal(pieFoto, user, hashTag);
						if(!hash.isEmpty()) {
							String[] ha = hash.split(" ");
							System.out.println("Registrando post");
							po.setTasks_model_id(taskModelId);
							goProfileForUrl();
							System.out.println("Post insertado");
							po.insert();
							Post_Detail poDe = new Post_Detail();
							poDe.setPosts_id(po.getLast());
							HashTag ht = new HashTag();
							ht.setCategories_id(categoria_id);
							ht.setGeneres_id(idGenere);
							System.out.println("Registrando HashTag");
							for (int j = 0; j < ha.length; j++) {

								ht.setName(ha[j]);

								poDe.setHashtag_id(ht.getIdCategorieHashTag());

								poDe.insert();
							}
							System.out.println("El usuario publico correctamente");
						}else {
							System.out.println("El usuario no publico");
						}
						ini++;
						if(ini >= count) {
							ini = 0;
						}
					}
					break;
				case 6:
					publicTweet();
					break;
				default:
					break;
			}
			returnHome();
		} // Fin del For
	}
	
	private void likeProfile() throws InterruptedException {
		if(drive.searchElement(1,"//*[@aria-label='Buscar y explorar']") != 0) {
			drive.clickButton(1, "//*[@aria-label='Buscar y explorar']", "Buscar aria-label");
		}else if(drive.searchElement(1, "//*[@data-testid='AppTabBar_Explore_Link']") != 0) {
			drive.clickButton(1, "//*[@data-testid='AppTabBar_Explore_Link']", "Buscar data-testid");
		}
		
		Thread.sleep(getNumberRandomForSecond(2145 ,2458));
		
		User us = new User();
		us.setUsers_id(users_id);
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
		if(drive.searchElement(1, "//*[@data-testid='SideNav_NewTweet_Button']") != 0) {
			drive.clickButton(1, "//*[@data-testid='SideNav_NewTweet_Button']", "Tweet");
		}else if(drive.searchElement(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a") != 0) {
			drive.clickButton(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a", "Tweet xPath");
		}
		
		String array[] = {"Que bonito día!","Los politicos no sirven -.-","No me lo creo","Que genial idea jajaja","Siganme #followback"
				,"¿Quien me quiere y para que?","Alguién por aquí?","Que agradable sujeto","Esa noticia me dejo en shock","Jajajajajajajajajajajaj"
				,"Todos vamos al mismo camino.","Es irrelevante eso que me dices","Si no te agrado te vas","Sigueme y te sigo #followback #follow"
				,"Tusa","Really?","Cantaclaro","Simon Diaz","No es copia, jamas.","Soy de las personas que prefieren netflix que estar en una rumba"
				,"¿Tu mamá sabe que usas el internet para dejarme en visto?","Amo la lluvia","Odio los climas calientes","Perro catolico"
				,"El celular nunca puede ser mas importante que la persona con la que estas comiendo.","No!","O quizás si jejeje","Que cancion tan genial"};
		
		robot.inputWrite(array[getNumberRandomForSecond(0, array.length-1)]);
		Thread.sleep(getNumberRandomForSecond(1254, 1798));
		
		drive.clickButton(1, "//*[@data-testid='tweetButton']", "TweetButton publicar");
		Thread.sleep(getNumberRandomForSecond(8546, 8677));
		robot.pressEsc();
		robot.pressEsc();
	}
	/**
	 * Publicar imagen normal 
	 * 
	 * @author Luis Morales
	 * @version 1.0.0
	 * @param pie el pie de foto a escribir
	 */
	private void uploadImage() throws InterruptedException, SQLException {
		Sub_Categorie subC = new Sub_Categorie();
		
		if(drive.searchElement(1, "//*[@data-testid='SideNav_NewTweet_Button']") != 0) {
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
			
			Path_Photo path_P = new Path_Photo();
			path_P.setSub_categories_id(subC.getSub_categories_id());
			path_P.setCategories_id(subC.getCategories_id());
			path_P = path_P.getPathPhotoSubCategories();
			System.out.println("Agregar foto ");
			drive.inputWriteFile(1, "/html/body/div/div/div/div[1]/div/div/div/div/div[2]/div[2]/div/div[3]/div/div/div[1]/div/div/div/div[2]/div[2]/div/div/div[1]/input", path_P.getPath());
			Thread.sleep(getNumberRandomForSecond(3546, 4677));
			
			drive.clickButton(1, "//*[@data-testid='tweetButton']", "TweetButton publicar");
			Thread.sleep(getNumberRandomForSecond(8546, 8677));
			robot.pressEsc();
			robot.pressEsc();
		}else {
			System.out.println("No hay sub categorias para publicar");
			robot.pressEsc();
		}
	}
	
	private void reviewMessage() throws InterruptedException {
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
//		int quantityNotifications = drive.getQuantityElements(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[2]/div/section/div/div/div/div");
//		
//		if(quantityNotifications == 0) {
//			System.out.println("No hay notificaciones");
//		}else {
//			int randomNotification = getNumberRandomForSecond(1, quantityNotifications);
//			
//			try {
//				drive.clickButton(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[2]/div/section/div/div/div/div["+randomNotification+"]/div/article", "Notificacion ");
//				Thread.sleep(getNumberRandomForSecond(2145 ,2458));
//				robot.mouseScroll(8);
//				Thread.sleep(getNumberRandomForSecond(1145 ,1458));
//				robot.mouseScroll(-8);
//				Thread.sleep(getNumberRandomForSecond(1145 ,1457));
//			}catch (Exception e) {
//				e.getStackTrace();
//			}
//		}

	}
	
	private String uploadImageFinal(String pie, String user, List<String> hash) throws InterruptedException {
		if(drive.searchElement(1, "//*[@data-testid='SideNav_NewTweet_Button']") != 0) {
			drive.clickButton(1, "//*[@data-testid='SideNav_NewTweet_Button']", "Tweet");
		}else if(drive.searchElement(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a") != 0) {
			drive.clickButton(1, "/html/body/div/div/div/div/header/div/div/div/div/div[3]/a", "Tweet xPath");
		}
		
		Thread.sleep(getNumberRandomForSecond(1234, 1548));
		Phrases phr = new Phrases();
		phr.setCategories_id(categoria_id);
		phr.setGeneres_id(idGenere);
		String hashT = "";
		try {
			phr = phr.getPhraseRandom();
			if (hash.size() > 2) {
				Collections.shuffle(hash);
				
				hashT += hash.get(0) +  " ";
				hashT += hash.get(1) +  " ";
				hashT += hash.get(2) +  " ";
				
			} else if (hash.size() > 0 && hash.size() < 2) {
				Collections.shuffle(hash);
				
				hashT += hash.get(0) +  " ";
			}
			robot.inputWrite(phr.getPhrase()+ " "+pie+" "+user+ " "+ hashT);
			
			Path_Photo path = new Path_Photo();
			path.setCategories_id(categoria_id);
			path.setGeneres_id(idGenere);
			path = path.getPathPhotos();
			
			System.out.println("Agregar foto"+path.getPath());
			drive.inputWriteFile(1, "/html/body/div/div/div/div[1]/div/div/div/div/div[2]/div[2]/div/div[3]/div/div/div[1]/div/div/div/div[2]/div[2]/div/div/div[1]/input", path.getPath());
			Thread.sleep(getNumberRandomForSecond(3546, 4677));
			
			drive.clickButton(1, "//*[@data-testid='tweetButton']", "TweetButton publicar");
			Thread.sleep(getNumberRandomForSecond(8546, 8677));
			po.setGeneres_id(idGenere);
			po.setPath_photos_id(path.getPath_photos_id());
			po.setPhrases_id(phr.getPhrases_id());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return hashT;
		
		
	}
	
	private void goProfileForUrl() throws InterruptedException {
		if(drive.searchElement(1, "/html/body/div/div/div/div/header/div/div/div/div/div[2]/nav/a[7]") != 0) {
			drive.clickButton(1, "/html/body/div/div/div/div/header/div/div/div/div/div[2]/nav/a[7]", "Perfil");
			Thread.sleep(getNumberRandomForSecond(1456, 1789));
			drive.clickButton(1, "/html/body/div/div/div/div/main/div/div/div/div/div/div[2]/div/div/div[2]/section/div/div/div/div[1]/div/article", "Ultima publicacion");
			Thread.sleep(getNumberRandomForSecond(1456, 1789));
			po.setLink_post(drive.getCurrentUrl());
			Thread.sleep(getNumberRandomForSecond(1345, 1654));
			robot.pressEsc();
			Thread.sleep(getNumberRandomForSecond(845, 1054));
		}
		
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
	private static void returnHome() throws InterruptedException {
		System.out.println("Volver al inicio");
		if(drive.searchElement(1, "//*[@data-testid='AppTabBar_Home_Link']") != 0) {
			drive.clickButton(1, "//*[@data-testid='AppTabBar_Home_Link']", "Home data-testid");
			Thread.sleep(getNumberRandomForSecond(1245, 1478));
		}
	}
	
	
	private void userBlocked(String motive) {
		User_Block userB = new User_Block();
		userB.setUsers_id(users_id);
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
	private String validateIP() {
		
		try {

            URL whatismyip = new URL("http://checkip.amazonaws.com");

            BufferedReader in = new BufferedReader(new InputStreamReader(

            whatismyip.openStream()));     
            
            return in.readLine();
            
        } catch (MalformedURLException ex) {

            System.err.println(ex);
            return "190.146.186.130";
        } catch (IOException ex) {
            System.err.println(ex);
            return "190.146.186.130";
        }
		
	}
	
	private static int getNumberRandomForSecond(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

}
