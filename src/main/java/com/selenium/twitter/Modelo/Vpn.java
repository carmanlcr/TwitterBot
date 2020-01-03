package com.selenium.twitter.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.selenium.twitter.Interface.Model;


public class Vpn implements Model{
	
	private final String TABLE_NAME = "vpn";
	private String name;
	private boolean activo;
	private static Conexion conn = new Conexion();
	
	public List<String> getAllActive() throws SQLException {
		List<String> list = new ArrayList<String>();
	    String query = "SELECT name FROM "+TABLE_NAME+" WHERE active = 1 ORDER BY name ASC";
		try (Connection conexion = conn.conectar();
				Statement st = conexion.createStatement();
				ResultSet rs = st.executeQuery(query)){
			list.add("Seleccione");
			while (rs.next() ) {
				list.add(rs.getString("name"));
               
			}
			conexion.close();
		}catch(Exception e) {
			System.err.println(e);
		}
		
		return list;
	}
	
	public String getNameVPN(String name) throws SQLException {
		String nameVpn = "";
		String query = "SELECT * FROM "+TABLE_NAME+" WHERE name = '"+nameVpn+"';";
		try (Connection conexion = conn.conectar();
				Statement st = conexion.createStatement();
				ResultSet rs = st.executeQuery(query)){
			
			while (rs.next() ) {
				nameVpn = rs.getString("name");
               
			}
			conexion.close();
		}catch(Exception e) {
			System.err.println(e);
		}
		
		return nameVpn;
	}
	
	public int getFind(String name) throws SQLException {
		int idVpn = 0;
		String query = "SELECT * FROM "+TABLE_NAME+" WHERE name = '"+name+"';";
		try (Connection conexion = conn.conectar();
				Statement st = conexion.createStatement();
				ResultSet rs = st.executeQuery(query)){
			
			
			while (rs.next() ) {
				idVpn = rs.getInt("vpn_id");
               
			}
			conexion.close();
		}catch(Exception e) {
			System.err.println(e);
		}			
		
		return idVpn;
	}
	
	public int findOrCreate(String name) throws SQLException {
		int idVpn = 0;
		String query = "SELECT * FROM "+TABLE_NAME+" WHERE UPPER(name) = '"+name.toUpperCase()+"';";
		try (Connection conexion = conn.conectar();
				Statement st = conexion.createStatement();
				ResultSet rs = st.executeQuery(query)){
			
			while (rs.next() ) {
				idVpn = rs.getInt("vpn_id");
               
			}
		}catch(Exception e) {
			System.err.println(e);
		}			
		if(idVpn==0) {
			setName(name);
			insert();
		}
		
		return idVpn;
	}
	
	public void insert() throws SQLException {
		

		try (Connection conexion = conn.conectar();){
			String insert = "INSERT INTO "+TABLE_NAME+"(name) VALUES (?);";
			PreparedStatement exe = conexion.prepareStatement(insert);
			exe.setString(1, getName());
			
			exe.executeUpdate();
		} catch(Exception e)  {
			System.err.println(e);
		}
			

		
	}
	
	@Override
	public void update() throws SQLException {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	
}
