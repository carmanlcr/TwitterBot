package com.selenium.twitter.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.selenium.twitter.Interface.Model;


public class Post implements Model{
	
	private final String TABLE_NAME ="posts";
	private int posts_id;
	private int users_id;
	private int generes_id;
	private String link_post;
	private String created_at; 
	private int tasks_model_id;
	private int path_photos_id;
	private int phrases_id;
	private Calendar c = Calendar.getInstance();
	private Date date = c.getTime();
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
	private DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
	
	private static Conexion conn = new Conexion();
	Statement st;
	ResultSet rs;

	public void insert() {
		setCreated_at(dateFormat.format(date));
		
		try (Connection conexion = conn.conectar();){
 			String insert = "INSERT INTO "+TABLE_NAME+"(users_id,generes_id,tasks_model_id,path_photos_id,phrases_id,link_post,created_at) "
					+ " VALUE (?,?,?,?,?,?,?);";
			PreparedStatement  query = (PreparedStatement) conexion.prepareStatement(insert);
			query.setInt(1, getUsers_id());
			query.setInt(2, getGeneres_id());
			query.setInt(3, getTasks_model_id());
			query.setInt(4, getPath_photos_id());
			query.setInt(5, getPhrases_id());
			query.setString(6, getLink_post());
			query.setString(7, getCreated_at());
			query.executeUpdate();
			
		}catch(SQLException e) {
			System.err.println(e);
		}
		
		
	}
	
	public List<String[]> getCountPostUsers(){
		List<String[]> list = new ArrayList<String[]>();
		String[] array = null;
		int increment = 0;
		String created_at = dateFormat1.format(date);
		String query = " SELECT c.username usuario, COUNT(*) cuenta FROM "
				+ " (SELECT us.username, pt.created_at "
				+ " FROM users us "
				+ " LEFT JOIN "+TABLE_NAME+" pt ON pt.users_id = us.users_id "
				+ " WHERE DATE(pt.created_at) = ?) AS c "
				+ " GROUP BY c.username; ";
		ResultSet rs = null;
		try (Connection conexion = conn.conectar();){
			PreparedStatement pst = (PreparedStatement) conexion.prepareStatement(query);
			pst.setString(1, created_at);
			rs = pst.executeQuery();

			
			while (rs.next() ) {
				array = new String[2];
				array[0] = rs.getString("usuario");
				array[1] = rs.getString("cuenta");
				list.add(increment, array);
				increment++;
			}
			rs.close();
		}catch(Exception e) {
			System.err.println(e);
		}
		
		return list;
	}
	
	public int getCountPostUser() {
		int post = 0;
		String date1 = dateFormat1.format(date);
		String query = "SELECT count(*) cuenta FROM "+TABLE_NAME + 
				" WHERE users_id = ? AND DATE(created_at) = ?;";
		
		try (Connection conexion = conn.conectar();) {
			PreparedStatement pst = (PreparedStatement) conexion.prepareStatement(query);
			pst.setInt(1, getUsers_id());
			pst.setString(2, date1);
			rs = pst.executeQuery();
			
			if(rs.next()) post = rs.getInt("cuenta");
			
		}catch (SQLException e) {
			e.getStackTrace();
		}finally {
			if(rs != null) try {rs.close(); } catch(SQLException e) {e.getStackTrace(); }
		}
		return post;
	}
	
	public int getLast() {
		int id = 0;
		
		String query = "SELECT po.posts_id FROM "+TABLE_NAME+" po ORDER BY po.posts_id DESC LIMIT 1";
		
		try (Connection conexion = conn.conectar();
				Statement st = conexion.createStatement();
				ResultSet rs = st.executeQuery(query)){
			
			while (rs.next() ) {
               id =  rs.getInt("po.posts_id");
			}
		}catch(SQLException e) {
			System.err.println(e);
		}
		
		return id;
	} 
	
	
	public int getLastsTasktPublic(){
		int idTask = 0;
		
		
		try (Connection conexion = conn.conectar();){
			String queryExce = "SELECT * FROM tasks_model tm " + 
					"WHERE tm.tasks_model_id NOT IN (SELECT pt.tasks_model_id FROM "+TABLE_NAME+" pt WHERE users_id = ? AND DATE(pt.created_at) BETWEEN ? AND ?) " + 
					"ORDER BY RAND() LIMIT 1;";
	
		PreparedStatement  query = (PreparedStatement) conexion.prepareStatement(queryExce);
		query.setInt(1, getUsers_id());
		query.setString(2, dateFormat1.format(new Date( date.getTime()-86400000)));
		query.setString(3, dateFormat1.format(date));
		
		rs = query.executeQuery();
			
		if(rs.next()) {
			idTask = rs.getInt("tm.tasks_model_id");
		}
		}catch(SQLException e) {
			e.getStackTrace();
		}
		return idTask;
	}
	
	@Override
	public void update() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	public int getPosts_id() {
		return posts_id;
	}

	public void setPosts_id(int posts_id) {
		this.posts_id = posts_id;
	}

	public int getGeneres_id() {
		return generes_id;
	}

	public void setGeneres_id(int generes_id) {
		this.generes_id = generes_id;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created) {
		this.created_at = created;
	}

	public int getUsers_id() {
		return users_id;
	}

	public void setUsers_id(int users_id) {
		this.users_id = users_id;
	}

	public int getTasks_model_id() {
		return tasks_model_id;
	}

	public void setTasks_model_id(int tasks_model_id) {
		this.tasks_model_id = tasks_model_id;
	}

	public String getLink_post() {
		return link_post;
	}

	public void setLink_post(String link_post) {
		this.link_post = link_post;
	}

	public int getPath_photos_id() {
		return path_photos_id;
	}

	public void setPath_photos_id(int path_photos_id) {
		this.path_photos_id = path_photos_id;
	}

	public int getPhrases_id() {
		return phrases_id;
	}

	public void setPhrases_id(int phrases_id) {
		this.phrases_id = phrases_id;
	}


	
	
}
