package com.selenium.twitter.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import configurations.connection.ConnectionTW;
import configurations.interfaces.Model;

public class Path_Photo implements Model {
	
	private static final String TABLE_NAME = "path_photos";
	private int path_photos_id;
	private String path;
	private boolean active;
	private String created_at;
	private int categories_id;
	private int sub_categories_id;
	private int generes_id;
	private Date date = new Date();
	private DateFormat dateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd H:m:s");
	private static ConnectionTW conn = new ConnectionTW();
	ResultSet rs;
	
	public void insert() throws SQLException {
		
		setCreated_at(dateFormatDateTime.format(date));
		
		String insert = "";
		try (Connection conexion = conn.conectar();
				Statement st = conexion.createStatement()){
			if(getGeneres_id() == 0) {
				
				
				insert = "INSERT INTO "+TABLE_NAME+"(path,created_at,categories_id,sub_categories_id) "
						+ "VALUE ('"+getPath()+"','"+getCreated_at()+"',"+getCategories_id()+","+getSub_categories_id()+");";
			}else if(getSub_categories_id() == 0) {
				insert = "INSERT INTO "+TABLE_NAME+"(path,created_at,categories_id,generes_id) "
						+ "VALUE ('"+getPath()+"','"+getCreated_at()+"',"+getCategories_id()+","+getGeneres_id()+");";
			}else {
				insert = "INSERT INTO "+TABLE_NAME+"(path,created_at,categories_id,sub_categories_id,generes_id) "
						+ "VALUE ('"+getPath()+"','"+getCreated_at()+"',"+getCategories_id()+","+getSub_categories_id()+","
						+getGeneres_id()+");";
			}
			st.executeUpdate(insert);
		} catch(SQLException e)  {
			System.err.println(e);
		} catch(Exception e){
			System.err.println(e);
		}
	}
	
	/**
	 * 
	 * @deprecated
	 */
	public void desactPathPhoto() {
		
		
		String update = "";
		try (Connection conexion = conn.conectar();){
			update = "UPDATE "+TABLE_NAME+" SET active = ? WHERE categories_id=? AND sub_categories_id = ? AND generes_id = ?";
			PreparedStatement  query = conexion.prepareStatement(update);
			query.setInt(1, 0);
			query.setInt(2, getCategories_id());
			query.setInt(3, getSub_categories_id());
			query.setInt(4, getGeneres_id());
			query.executeUpdate();
			conexion.close();
		} catch(SQLException e)  {
			System.err.println(e);
		} catch(Exception e){
			System.err.println(e);
			
		}
	}
	
	public Path_Photo getPathPhotos() {
		Path_Photo path = null;
		String query = "";
		PreparedStatement  queryE;
		try (Connection conexion = conn.conectar();){
			
			if(getSub_categories_id() == 0) {
				query = "SELECT * FROM "+TABLE_NAME+" WHERE categories_id=? AND generes_id = ? AND active = ? ORDER BY RAND() LIMIT 1;";
				queryE= conexion.prepareStatement(query);
				queryE.setInt(1, getCategories_id());
				queryE.setInt(2, getGeneres_id());
				queryE.setInt(3, 1);
			}else if(getGeneres_id() == 0) {
				query = "SELECT * FROM "+TABLE_NAME+" WHERE categories_id=? AND sub_categories_id = ? AND active = ? ORDER BY RAND() LIMIT 1;";
				queryE= (PreparedStatement) conexion.prepareStatement(query);
				queryE.setInt(1, getCategories_id());
				queryE.setInt(2, getSub_categories_id());
				queryE.setInt(3, 1);
			}else {
				query = "SELECT * FROM "+TABLE_NAME+" WHERE categories_id=? AND sub_categories_id = ? AND generes_id = ? AND active = ? ORDER BY RAND() LIMIT 1;";
				queryE= (PreparedStatement) conexion.prepareStatement(query);
				queryE.setInt(1, getCategories_id());
				queryE.setInt(2, getSub_categories_id());
				queryE.setInt(3, getGeneres_id());
				queryE.setInt(4, 1);
			}
			rs = queryE.executeQuery();
			if(rs.next()) {
				path = new Path_Photo();
				path.setPath_photos_id(rs.getInt("path_photos_id"));
				path.setPath(rs.getString("path"));
				path.setActive(rs.getBoolean("active"));
			}
		}catch(SQLException e) {
			System.err.println(e);
		}	
				
		return path;
	}
	
	public Path_Photo getPathPhotoSubCategories() {
		Path_Photo path = null;
		String queryExce = "SELECT * FROM "+TABLE_NAME+" ph "
				+ "WHERE ph.active = ? AND ph.categories_id = ? "
				+ "AND ph.sub_categories_id = ? "
				+ "ORDER BY RAND() LIMIT 1;";
		
		try (Connection conexion = conn.conectar();
				PreparedStatement  queryE = conexion.prepareStatement(queryExce);){
			queryE.setInt(1, 1);
			queryE.setInt(2, getCategories_id());
			queryE.setInt(3, getSub_categories_id());
			rs = queryE.executeQuery();
			if(rs.next()) {
				path = new Path_Photo();
				path.setPath(rs.getString("ph.path"));
				path.setActive(rs.getBoolean("ph.active"));
				path.setCategories_id(rs.getInt("ph.categories_id"));
				path.setSub_categories_id(rs.getInt("ph.sub_categories_id"));
			}
		}catch(SQLException e) {
			System.err.println(e);
		}	
				
		return path;
	}
	@Override
	public void update() throws SQLException {
		
	}
	
	public int getPath_photos_id() {
		return path_photos_id;
	}

	public void setPath_photos_id(int path_photos_id) {
		this.path_photos_id = path_photos_id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public int getCategories_id() {
		return categories_id;
	}

	public void setCategories_id(int categories_id) {
		this.categories_id = categories_id;
	}

	public int getSub_categories_id() {
		return sub_categories_id;
	}

	public void setSub_categories_id(int sub_categories_id) {
		this.sub_categories_id = sub_categories_id;
	}

	public int getGeneres_id() {
		return generes_id;
	}

	public void setGeneres_id(int generes_id) {
		this.generes_id = generes_id;
	}

}
