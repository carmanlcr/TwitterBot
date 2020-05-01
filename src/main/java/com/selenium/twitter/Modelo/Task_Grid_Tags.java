package com.selenium.twitter.Modelo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import configurations.connection.ConnectionTW;
import configurations.interfaces.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Task_Grid_Tags implements Model {
	
	private static final String TABLE_NAME = "tasks_grid_tags";
	private int tasks_grid_tags_id;
	private String tags;
	private int tasks_grid_id;
	private boolean active;
	private String created_at;
	private String updated_at;
	private static ConnectionTW conn = new ConnectionTW();
	
	@Override
	public void insert() throws SQLException {
		// None

	}
	
	@Override
	public void update() throws SQLException {
		// None
		
	}
	
	public List<Task_Grid_Tags> getTagsForTask(){
		List<Task_Grid_Tags> list = new ArrayList<>();
		StringBuilder str = new StringBuilder();
		str.append("SELECT * FROM "+TABLE_NAME+" WHERE tasks_grid_id = ? AND active = 1");
		
		try(Connection con = conn.conectar();
				PreparedStatement pre = con.prepareStatement(str.toString())){
			pre.setInt(1, getTasks_grid_id());
			ResultSet rs = pre.executeQuery();
			
			while(rs.next()) {
				Task_Grid_Tags tgt = new Task_Grid_Tags();
				tgt.setTasks_grid_tags_id(rs.getInt("tasks_grid_tags_id"));
				tgt.setTags(rs.getString("tags"));
				tgt.setTasks_grid_id(rs.getInt("tasks_grid_id"));
				tgt.setActive(rs.getBoolean("active"));
				tgt.setCreated_at(rs.getString("created_at"));
				tgt.setUpdated_at(rs.getString("updated_at"));
				list.add(tgt);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public int getTasks_grid_tags_id() {
		return tasks_grid_tags_id;
	}

	public void setTasks_grid_tags_id(int tasks_grid_tags_id) {
		this.tasks_grid_tags_id = tasks_grid_tags_id;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getTasks_grid_id() {
		return tasks_grid_id;
	}

	public void setTasks_grid_id(int tasks_grid_id) {
		this.tasks_grid_id = tasks_grid_id;
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
