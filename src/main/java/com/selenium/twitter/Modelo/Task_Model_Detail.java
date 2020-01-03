package com.selenium.twitter.Modelo;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.selenium.twitter.Interface.Model;


public class Task_Model_Detail implements Model {
	
	private final String TABLE_NAME = "tasks_model_detail";
	private int tasks_model_id;
	private int tasks_id;
	private String created_at;
	private Date date = new Date();
	private DateFormat dateFormatDateTime = new SimpleDateFormat("yyyy-MM-dd H:m:s");
	private static Conexion conn = new Conexion();
	Statement st;
	ResultSet rs;
	
	public void insert() throws SQLException {
		
		
		setCreated_at(dateFormatDateTime.format(date));
		
		try(Connection conexion = conn.conectar();) {
			String insert = "INSERT INTO "+TABLE_NAME+"(tasks_model_id,tasks_id,created_at) VALUE "
					+ " (?,?,?);";
			PreparedStatement exe = (PreparedStatement) conexion.prepareStatement(insert);
			exe.setInt(1, getTasks_model_id());
			exe.setInt(2, getTasks_id());
			exe.setString(3, getCreated_at());
			
			exe.executeUpdate();
		}catch(SQLException e) {
			System.err.println(e);
		}
		
	}
	
	public List<Integer> getTaskModelDetailDiferent(){
		List<Integer> list = new ArrayList<>();
		
		try (Connection conexion = conn.conectar();){
			
			String queryExce = "SELECT tmd.tasks_id FROM "+TABLE_NAME+" tmd " + 
								"WHERE tasks_model_id = ?;";
			
			PreparedStatement  query = (PreparedStatement) conexion.prepareStatement(queryExce);
			query.setInt(1, getTasks_model_id());
			rs = query.executeQuery();
			
			while (rs.next() ) {
				 list.add(rs.getInt("tmd.tasks_id"));
			}
		}catch(SQLException e) {
			System.err.println(e);
		}
		return list;
	}
	
	@Override
	public void update() throws SQLException {
		
	}

	public int getTasks_model_id() {
		return tasks_model_id;
	}

	public void setTasks_model_id(int tasks_model_id) {
		this.tasks_model_id = tasks_model_id;
	}

	public int getTasks_id() {
		return tasks_id;
	}

	public void setTasks_id(int tasks_id) {
		this.tasks_id = tasks_id;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
	
	

}
