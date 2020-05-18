package com.selenium.twitter.Vista;



import com.selenium.twitter.Modelo.*;

import configurations.controller.SikuliTest;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.sikuli.script.Screen;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private static final String VERSION = "2.0.6";
	private JPanel contentPane;
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnUsuarios = new JMenu("Usuarios");
	private final JMenuItem buscarUsuario = new JMenuItem("Buscar");
	private final JMenu mnVpn = new JMenu("Vpn");
	private final JMenuItem registrarVpn = new JMenuItem("Registrar");
	private final JMenuItem actualizarVpn = new JMenuItem("Actualizar");
	private final JMenu mnTask = new JMenu("Tarea");
	private final JMenuItem registrarTarea = new JMenuItem("Registrar Tarea");
	public List<JTextArea> textA = new ArrayList<>();
	private static JComboBox<String> comboBox = new JComboBox<>();
	private static JComboBox<String> comboBox_1 = new JComboBox<>();
	private static Map<String, Integer> hashCa;
	private static Map<String, Integer> hashGe = new HashMap<>();
	private static JButton empezar = new JButton("Comenzar");
	private final JLabel lblNewLabel_1 = new JLabel("Campaña");
	private final JLabel lblNewLabel_2 = new JLabel("Genero");
	private static Inicio_Aplicacion iniApli = new Inicio_Aplicacion();
	private static Screen s;
	
	/**
	 * Launch the application.
	 * @throws SQLException 
	 */
	public static void main(String[] args)  {
		SikuliTest si = new SikuliTest();
		si.run();
		s = si.getScreen();
		
		
		if(args.length > 0) {
			iniApli.setVersion(VERSION);
			Ejecucion eje;
			try {
				eje = new Ejecucion(Integer.parseInt(args[0]),Integer.parseInt(args[1]),iniApli,s,Boolean.parseBoolean(args[3]));
				eje.inicio();
			} catch (NumberFormatException| SQLException e) {
				e.printStackTrace();
			} 
			
		}else {
			final Task_Grid task_G = new Task_Grid();
			hashCa = task_G.getCategoriesToday();
			setComboBox(hashCa);
			
			comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					comboBox_1.removeAll();
					comboBox_1.removeAllItems();
					task_G.setCategories_id(Integer.parseInt(hashCa.get(comboBox.getSelectedItem().toString()).toString()));
					hashGe = task_G.getCategoriesAndGeneresToday();
					for(String st : hashGe.keySet()) comboBox_1.addItem(st);
					
					if(hashGe.size() > 0) {
						empezar.setEnabled(true);
					}
				}
			});
			
			if(hashGe.size() > 0) {
				empezar.setEnabled(true);
			}
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
		File file = new File(getClass().getResource("/com/selenium/twitter").getFile());
	
		System.out.println(file.getAbsolutePath());

		setJMenuBar(menuBar);
		mnUsuarios.setFont(new Font("Arial", Font.BOLD, 12));
		
		menuBar.add(mnUsuarios);
		
		buscarUsuario.setEnabled(false);
		
		mnUsuarios.add(buscarUsuario);
		

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
		
		mnTask.add(registrarTarea);
		menuBar.add(mnTask);
		
		registrarTarea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RegistrarTarea regi = new RegistrarTarea();
				regi.init();
			}
		});
		
		
		JLabel lblNewLabel = new JLabel(VERSION);
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(36)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
							.addGap(65)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE))
							.addContainerGap(170, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED, 348, Short.MAX_VALUE)
							.addComponent(empezar)
							.addGap(43))))
		);
		
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(62)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1))
					.addGap(38)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2))
					.addPreferredGap(ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
					.addComponent(lblNewLabel)
					.addGap(46))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(238, Short.MAX_VALUE)
					.addComponent(empezar, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
					.addGap(36))
		);
		contentPane.setLayout(gl_contentPane);
		
		iniApli.setVersion(VERSION);
		empezar.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
					int id = Integer.parseInt(hashCa.get(comboBox.getSelectedItem().toString()).toString());
					int id_genere = Integer.parseInt(hashGe.get(comboBox_1.getSelectedItem().toString()).toString());
					Ejecucion eje = new Ejecucion(id,id_genere,iniApli,s,false);
					setExtendedState(ICONIFIED);
					eje.inicio();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				
			}
		});
	}
	
	private static JComboBox<String> setComboBox(Map<String, Integer> map) {
		comboBox = new JComboBox<>();
		
		for (String string : map.keySet()) {
			comboBox.addItem(string);
		}
	    return comboBox;
	}

}
