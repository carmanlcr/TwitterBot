package com.selenium.twitter.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.selenium.twitter.Interface.Model;

public class Group implements Model {

	private final String TABLE_NAME = "groups";
	private String groups_id;
	private String name;
	private boolean active;
	private String created_at;
	private int users_id;
	private static Conexion conn = new Conexion();
	private Date date = new Date();
	private DateFormat dateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd H:m:s");
	private DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void insert() throws SQLException {
		setCreated_at(dateFormatDateTime.format(date));
		String insert = "INSERT INTO "+TABLE_NAME+"(groups_id,name,created_at,users_id) "
				+ "VALUES (?,?,?,?);";
			try (Connection conexion = conn.conectar();
				PreparedStatement exe = conexion.prepareStatement(insert);){
				exe.setString(1, getGroups_id());
				exe.setString(2, getName());
				exe.setString(3, getCreated_at());
				exe.setInt(4, getUsers_id());
				exe.executeUpdate();
				
			} catch(SQLException e)  {
				System.err.println(e);
			} catch(Exception e){
				System.err.println(e);
			}
	}

	@Override
	public void update() throws SQLException {
		// TODO Auto-generated method stub

	}
	
	public void deleteGroups() {
		String insert = "DELETE FROM "+TABLE_NAME+" WHERE users_id = ?";
			try (Connection conexion = conn.conectar();
				PreparedStatement exe = conexion.prepareStatement(insert);){
				exe.setInt(1, getUsers_id());
				exe.executeUpdate();
			} catch(SQLException e)  {
				System.err.println(e);
			} catch(Exception e){
				System.err.println(e);
			}
	}
	
	public Group getGroupNotPublication(String string1, String string2, int users_id) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date date1 = cal.getTime();
		Group gp = null;
		String query = "SELECT * FROM groups gp " + 
				"WHERE gp.name LIKE ? OR gp.name LIKE ? AND gp.users_id = ? " + 
				"AND gp.groups_id NOT IN (SELECT pt.groups FROM posts pt WHERE DATE(pt.created_at) BETWEEN ? AND ?) " + 
				"ORDER BY RAND() LIMIT 1;";
		try(Connection conexion = conn.conectar();
				PreparedStatement exe = conexion.prepareStatement(query);
				){
			exe.setString(1, "%"+string1+"%");
			exe.setString(2, "%"+string2+"%");
			exe.setInt(3, users_id);
			exe.setString(4, dateFormat1.format(date1));
			exe.setString(5, dateFormat1.format(date));
			ResultSet rs = exe.executeQuery();
			
			if(rs.next()) {
				gp = new Group();
				gp.setName(rs.getString("gp.name"));
				gp.setActive(rs.getBoolean("gp.active"));
				gp.setGroups_id(rs.getString("gp.groups_id"));
				gp.setCreated_at(rs.getString("gp.created_at"));
				gp.setUsers_id(rs.getInt("gp.users_id"));
			}
		}catch (SQLException e) {
			e.getStackTrace();
		}
		
		return gp;
	}

	public String getGroups_id() {
		return groups_id;
	}

	public void setGroups_id(String groups_id) {
		this.groups_id = groups_id;
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

	public int getUsers_id() {
		return users_id;
	}

	public void setUsers_id(int users_id) {
		this.users_id = users_id;
	}

}
