package com.selenium.twitter.Vista;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.sikuli.script.Screen;

import com.selenium.twitter.Controlador.*;
import com.selenium.twitter.Modelo.*;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JLabel;

import java.awt.Color;

public class Ejecucion extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -280322591820670686L;
	private JPanel contentPane;
	private int categoria_id = 0;
	private int generes_id = 0;
	private List<String> list = new ArrayList<>();
	private List<String[]> listPost = new ArrayList<>(); 
	private User user = new User();
	private JPanel panelUsuario = new JPanel();
	private List<JLabel> listCheckBoxUsers = new ArrayList<>();
	private List<String> listUsers = new ArrayList<>();
	private int totalUser = 0;
	private Categories categories = new Categories();
	private Inicio_Aplicacion ini;
	private Screen screen;
	private boolean isManual;
	
	/**
	 * Launch the application.
	 */
	public void inicio() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ejecucion frame = new Ejecucion(categoria_id,generes_id,ini,screen,isManual);
					frame.setTitle(categories.getNameCategories(categoria_id));
					frame.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param s 
	 * @param iniApli 
	 * @param id_genere 
	 * @throws SQLException 
	 */
	public Ejecucion(int id, int id_genere, final Inicio_Aplicacion iniApli, Screen s,boolean isManual) throws SQLException {
		setTitle("Validacion");
		this.categoria_id = id;
		this.screen = s;
		this.generes_id = id_genere;
		this.ini = iniApli;
		this.isManual =isManual;
		setResizable(false);
		setBounds(100, 100, 665, 733);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setEnabled(false);
		
		//Se crear el boton de empezar y se agrega su ActionListener
		JButton btnEmpezar = new JButton("Empezar");
		
		if(isManual) {
			//Se empieza el proceso de post
			btnEmpezar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					for (JLabel checkbox : listCheckBoxUsers) {
						StringBuilder text = new StringBuilder("");
						for (int i = 0; i < checkbox.getText().length(); i++) {
							if(!checkbox.getText().substring(i,i+1).equals("(")) {
								text.append(checkbox.getText().substring(i,i+1));
								
							}
							if(checkbox.getText().substring(i,i+1).equals("(")) {
								break;
							}
						}
						listUsers.add(text.toString().trim());
					}

					if(listCheckBoxUsers.isEmpty()) {
						JOptionPane.showMessageDialog(null, "NO HAY USUARIOS PARA ESTA CAMPAÑA PARA PUBLICAR");
					}else {
						iniApli.setGeneres_id(generes_id);
						iniApli.insert();
						InicioController init = new InicioController(categoria_id,listUsers,screen);
						setExtendedState(ICONIFIED);
						try {
							init.init();
						} catch (SQLException | InterruptedException | IOException  e) {
							e.printStackTrace();
							Thread.currentThread().interrupt();
						}
					}					
					
				}
			});
		}else {
			for (JLabel checkbox : listCheckBoxUsers) {
				StringBuilder text = new StringBuilder("");
				for (int i = 0; i < checkbox.getText().length(); i++) {
					if(!checkbox.getText().substring(i,i+1).equals("(")) {
						text.append(checkbox.getText().substring(i,i+1));
						
					}
					if(checkbox.getText().substring(i,i+1).equals("(")) {
						break;
					}
				}
				listUsers.add(text.toString().trim());
			}

			if(listCheckBoxUsers.isEmpty()) {
				JOptionPane.showMessageDialog(null, "NO HAY USUARIOS PARA ESTA CAMPAÑA PARA PUBLICAR");
			}else {
				iniApli.setGeneres_id(generes_id);
				iniApli.insert();
				InicioController init = new InicioController(categoria_id,listUsers,screen);
				setExtendedState(ICONIFIED);
				try {
					init.init();
				} catch (SQLException | InterruptedException | IOException  e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		}
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setEnabled(false);
		
		JLabel lblTotal = new JLabel("Total");
		lblTotal.setBackground(new Color(0, 0, 128));
		lblTotal.setFont(new Font("Arial", Font.PLAIN, 12));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblTotal)
							.addGap(83))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(btnEmpezar, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
							.addGap(340))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(18)
					.addComponent(lblTotal)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 503, GroupLayout.PREFERRED_SIZE)
					.addGap(39)
					.addComponent(btnEmpezar, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(75, Short.MAX_VALUE))
		);
		
		
		list = user.getUserCategorieAndGenere(categoria_id,generes_id);
		if(list.isEmpty()) {
			JOptionPane.showMessageDialog(null, "YA NO HAY TAREAS PARA HOY PARA ESTA CAMPANA");
		}
		scrollPane.setViewportView(panelUsuario);
		

		
		JMenuItem mntmNewMenuItem = new JMenuItem("Desbloquear");
		
		panelUsuario.setLayout(new GridLayout(0, 3, 0, 0));
		
		Post post = new Post();
		listPost = post.getCountPostUsers(categoria_id);
		
		JPopupMenu popMenu = new JPopupMenu();
		popMenu.add(mntmNewMenuItem);
		//Agregar a los usuarios al panel 
		for (String usuarios : list) {
			JLabel chckbxNewCheckBox = new JLabel(usuarios);
			listCheckBoxUsers.add(chckbxNewCheckBox);
			
			//Agregar a los usuarios la cantidad de post en el d�a
			for(String[] pt : listPost) {
				if(pt[0].equals(usuarios)) {
					chckbxNewCheckBox.setText(chckbxNewCheckBox.getText()+" ("+pt[1]+")");
					break;
				}
			}
			
			
			panelUsuario.add(chckbxNewCheckBox);
			totalUser++;
		}
		
		lblTotal.setText(lblTotal.getText()+": "+totalUser);

		contentPane.setLayout(gl_contentPane);
	}
	
}
