package com.selenium.twitter.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.selenium.twitter.Interface.Model;


public class Vpn implements Model{
	
	private final String TABLE_NAME = "vpn";
	private int vpn_id;
	private String name;
	private boolean active;
	private String created_at;
	private String updated_at;
	private Date date;
	private SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	
	public Vpn getVpn() throws SQLException {
		Vpn v = null;
		String sql = "SELECT  * FROM "+TABLE_NAME+" WHERE vpn_id = ?;";
		
		try (Connection conexion = conn.conectar();
				PreparedStatement pre = conexion.prepareStatement(sql);){
		
			pre.setInt(1, getVpn_id());
			
			ResultSet rs = pre.executeQuery();
			
			if(rs.next()) {
				v = new Vpn();
				v.setVpn_id(rs.getInt("vpn_id"));
				v.setName(rs.getString("name"));
				v.setActive(rs.getBoolean("active"));
			}
		}catch (SQLException e) {
			e.getStackTrace();
		}
		
		return v;
		
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
		date = new Date();
		setCreated_at(simple.format(date));
		setUpdated_at(simple.format(date));
		String insert = "INSERT INTO "+TABLE_NAME+"(name,created_at,updated_at) VALUES (?,?,?);";
		try (Connection conexion = conn.conectar();
				PreparedStatement exe = conexion.prepareStatement(insert);){
			
			exe.setString(1, getName());
			exe.setString(2, getCreated_at());
			exe.setString(3, getUpdated_at());
			exe.executeUpdate();
		} catch(Exception e)  {
			System.err.println(e);
		}
			
	}
	
	public HashMap<String,Integer> getAllVpn(){
		HashMap<String,Integer> mapGe = new HashMap<String,Integer>();
		
		String query = "SELECT * FROM "+TABLE_NAME+" v WHERE active = 1;";
		
		try (Connection conexion = conn.conectar();){
			PreparedStatement pst = conexion.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while (rs.next() ) {
				mapGe.put(rs.getString("v.name"), rs.getInt("v.vpn_id"));
			}
		}catch(SQLException e) {
			System.err.println(e);
		}
		
		
		
		return mapGe;
	}
	
	@Override
	public void update() throws SQLException {
		
	}
	
	public int getVpn_id() {
		return vpn_id;
	}

	public void setVpn_id(int vpn_id) {
		this.vpn_id = vpn_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	
}
