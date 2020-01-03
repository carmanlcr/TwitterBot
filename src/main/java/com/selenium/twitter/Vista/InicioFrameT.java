package com.selenium.twitter.Vista;



import com.selenium.twitter.Modelo.*;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import java.awt.Color;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.Font;



import javax.swing.JTextArea;
import javax.swing.JComboBox;
public class InicioFrameT extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnUsuarios = new JMenu("Usuarios");
	private final JMenuItem registrarUsuario = new JMenuItem("Registrar");
	private final JMenuItem importarUsuarios = new JMenuItem("Importar Usuarios");
	private final JMenuItem buscarUsuario = new JMenuItem("Buscar");
	private final JMenuItem actualizarUsuario = new JMenuItem("Actualizar Usuarios");
	private final JMenu mnVpn = new JMenu("Vpn");
	private final JMenuItem registrarVpn = new JMenuItem("Registrar");
	private final JMenuItem actualizarVpn = new JMenuItem("Actualizar");
	private final JMenu mnCategorias = new JMenu("Categorias");
	private final JMenuItem registrarCategoria = new JMenuItem("Registrar");
	private final JMenuItem mntmRegistrarSubCategorie = new JMenuItem("Registrar Sub Categoria");
	private final JMenu mnFrases = new JMenu("Frases");
	private final JMenuItem registrarFrase = new JMenuItem("Registrar");
	private final JMenuItem registrarHashTag = new JMenuItem("Registrar Hashtag"); 
	private final JMenu mnGenero = new JMenu("Genero");
	private final JMenuItem registrarGenero = new JMenuItem("Registrar Genero");
	private final JMenu mnTask = new JMenu("Tarea");
	private final JMenuItem registrarTarea = new JMenuItem("Registrar Tarea");
	private final JMenu mnPhotos = new JMenu("Fotos");
	private final JMenuItem registrarDireccionDeFotos = new JMenuItem("Registrar Fotos"); 
	public ArrayList<JTextArea> textA = new ArrayList<JTextArea>();
	private static Categories cate = new Categories();
	private static JComboBox<String> comboBox;
	private JButton empezar = new JButton("Comenzar");
	private static Inicio_Aplicacion iniApli = new Inicio_Aplicacion();
	
	/**
	 * Launch the application.
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		List<String> list = cate.getAllActive();
		comboBox = setComboBox(list);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InicioFrameT frame = new InicioFrameT();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	}

	/**
	 * Create the frame.
	 */
	public InicioFrameT() {
		setTitle("TwitterBot");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 559, 383);
		menuBar.setFont(new Font("Arial", Font.PLAIN, 12));
		menuBar.setBackground(new Color(51, 153, 204));
		
		setJMenuBar(menuBar);
		mnUsuarios.setFont(new Font("Arial", Font.BOLD, 12));
		
		menuBar.add(mnUsuarios);
		registrarUsuario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RegistrarUsuario registerUser;
				try {
					registerUser = new RegistrarUsuario();
					registerUser.inicio();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		});
		
//		scrapping();
		
		mnUsuarios.add(registrarUsuario);
		
		importarUsuarios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ImportUsers impo = new ImportUsers();
					impo.inicio();
				}catch (Exception e1) {
					System.err.println(e1);
				}
			}
		});
		
		mnUsuarios.add(importarUsuarios);
		
		buscarUsuario.setEnabled(false);
		
		mnUsuarios.add(buscarUsuario);
		actualizarUsuario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UpdateUsers up = new UpdateUsers();
					up.init();
				}catch (Exception e1) {
					System.err.println(e1);
				}
			}
		});
		
		
		mnUsuarios.add(actualizarUsuario);
		mnVpn.setFont(new Font("Arial", Font.BOLD, 12));
		
		menuBar.add(mnVpn);
		registrarVpn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegistrarVPN registerVpn = new RegistrarVPN();
				registerVpn.inicio();
			}
		});
		
		mnVpn.add(registrarVpn);
		actualizarVpn.setEnabled(false);
		
		mnVpn.add(actualizarVpn);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(102, 153, 204));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		mnCategorias.add(registrarCategoria);
		mnCategorias.add(mntmRegistrarSubCategorie);
		menuBar.add(mnCategorias);
		
		registrarCategoria.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				RegistrarCategories registrar = new RegistrarCategories();
				registrar.inicio();
			}
		});
		
		mntmRegistrarSubCategorie.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				RegistrarSubCategorie regis;
				try {
					regis = new RegistrarSubCategorie();
					regis.init();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				
			}
		});
		
		
		mnFrases.add(registrarFrase);
		mnFrases.add(registrarHashTag);
		menuBar.add(mnFrases);
		
		registrarFrase.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				RegistrarFrase registrar;
				
					registrar = new RegistrarFrase();
					registrar.inicio();
				
				
			}
		});
		
		registrarHashTag.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				RegistrarHashTag registrar;
				try {
					registrar = new RegistrarHashTag();
					registrar.inicio();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}
		});
		
		
		mnGenero.add(registrarGenero);
		menuBar.add(mnGenero);
		
		registrarGenero.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				RegistrarGenero registrar = new RegistrarGenero();
				registrar.inicio();
			}
			
		});
		
		mnTask.add(registrarTarea);
		menuBar.add(mnTask);
		
		registrarTarea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RegistrarTarea regi = new RegistrarTarea();
				regi.init();
			}
		});
		
		mnPhotos.add(registrarDireccionDeFotos);
		menuBar.add(mnPhotos);
		
		registrarDireccionDeFotos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RegistrarDireccionFotos dirFotos = new RegistrarDireccionFotos();
				dirFotos.inicio();
			}
		});
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(139)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(191, Short.MAX_VALUE)
					.addGap(80)
					.addComponent(empezar)
					.addContainerGap(191, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(81)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(204, Short.MAX_VALUE)
					.addGap(81)
					.addComponent(empezar,GroupLayout.PREFERRED_SIZE,50,GroupLayout.PREFERRED_SIZE)
					.addContainerGap(204, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		
		iniApli.setVersion("1.0.0");
		iniApli.insert();
		empezar.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
					int id = cate.getIdCategories((String) comboBox.getSelectedItem());
					Ejecucion eje = new Ejecucion(id);
					setExtendedState(ICONIFIED);
					eje.inicio();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				
			}
		});
	}
	
	private static JComboBox<String> setComboBox(List<String> map) {
		comboBox = new JComboBox<String>();
		
		for (String string : map) {
			comboBox.addItem(string);
		}
	    return comboBox;
	}

}
