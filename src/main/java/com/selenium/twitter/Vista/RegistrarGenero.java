package com.selenium.twitter.Vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.selenium.twitter.Modelo.Categories;
import com.selenium.twitter.Modelo.Genere;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;


/**
 * Movido al app web
 * 
 * @author Luis Morales
 * @deprecated
 */
public class RegistrarGenero extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8141458851232732273L;
	private JPanel contentPane;
	private JTextField textField;
	private Categories cate = new Categories();
	
	/**
	 * Launch the application.
	 */
	public void inicio() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegistrarGenero frame = new RegistrarGenero();
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
	public RegistrarGenero() {
		setTitle("Registrar Genero");
		setResizable(false);
		setBounds(100, 100, 316, 252);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblGenero = new JLabel("Genero");
		lblGenero.setFont(new Font("Arial", Font.BOLD, 11));
		
		textField = new JTextField();
		textField.setColumns(10);
		final JComboBox<String> comboBox = new JComboBox<String>();
		List<String> list = cate.getAllActive();
		for(String st : list) comboBox.addItem(st);


		final JButton btnRegistrar = new JButton("Registrar");
		btnRegistrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String generoTextField = textField.getText();
				if(generoTextField.equals("")) {
					JOptionPane.showMessageDialog(null, "El campo de genero no debe estar vacio ");
				}else {
					btnRegistrar.setEnabled(false);
					Genere gene = new Genere();
					gene.setName(generoTextField.trim());
					gene.setCategories_id(comboBox.getSelectedIndex()+1);
					gene.insert();
					
					textField.setText("");
					btnRegistrar.setEnabled(true);
					JOptionPane.showMessageDialog(null, "Se registro el genero con exito");
				}
				
			}
		});
		btnRegistrar.setFont(new Font("Arial", Font.BOLD, 11));
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(116, Short.MAX_VALUE)
					.addComponent(lblGenero, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
					.addGap(111))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(40)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(58, Short.MAX_VALUE))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(98)
					.addComponent(btnRegistrar)
					.addContainerGap(161, Short.MAX_VALUE))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(110)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(162, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(21)
					.addComponent(lblGenero, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(29)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(49)
					.addComponent(btnRegistrar)
					.addContainerGap(161, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
