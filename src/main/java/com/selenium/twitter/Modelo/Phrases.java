package com.selenium.twitter.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


import configurations.connection.ConnectionTW;
import configurations.interfaces.Model;


public class Phrases implements Model{
	
	private static final String TABLE_NAME = "phrases";
	private int phrases_id;
	private String phrase;
	private boolean active;
	private int categories_id;
	private int sub_categories_id;
	private int generes_id;
	private ConnectionTW conn = new ConnectionTW();
	
	
	public void insert() throws SQLException {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd H:m:s");
		String strDate= formatter.format(date);
		String insert = "INSERT INTO "+TABLE_NAME+"(phrase,created_at,updated_at,categories_id";
			try (Connection conexion = conn.conectar();){
				
				if(getGeneres_id() == 0) {
					insert += ",sub_categories_id) "
							+ "VALUE (?,?,?,?,?);";
					PreparedStatement pre  = conexion.prepareStatement(insert);
					pre.setString(1, getPhrase());
					pre.setString(2, strDate);
					pre.setString(3, strDate);
					pre.setInt(4, getCategories_id());
					pre.setInt(5, getSub_categories_id());
					pre.executeUpdate();
				}else if(getSub_categories_id() == 0) {
					insert += ",generes_id) "
							+ "VALUE (?,?,?,?,?); ";
					PreparedStatement pre  = conexion.prepareStatement(insert);
					pre.setString(1, getPhrase());
					pre.setString(2, strDate);
					pre.setString(3, strDate);
					pre.setInt(4, getCategories_id());
					pre.setInt(5, getGeneres_id());
					pre.executeUpdate();
				}
				
				
			} catch(SQLException e)  {
				System.err.println(e);
			} catch(Exception e){
				System.err.println(e);
				
			}
	}
	
	@Override
	public void update() throws SQLException {
		
	}
	 
	public Phrases getPhraseRandom() throws SQLException{
		Phrases ph = null;
		
		
		ResultSet rs = null;
		try (Connection conexion = conn.conectar();
				Statement st = conexion.createStatement();){
			
			String queryExce = "SELECT * FROM "+TABLE_NAME+" ph "
					+ "WHERE ph.active = ? AND ph.categories_id = ? "
					+ "AND ph.generes_id = ? "
					+ "ORDER BY RAND() LIMIT 1;";
			PreparedStatement  query = (PreparedStatement) conexion.prepareStatement(queryExce);
			query.setInt(1, 1);
			query.setInt(2, getCategories_id());
			query.setInt(3,getGeneres_id());
			rs = query.executeQuery();

			if(rs.next() ) {
				ph = new Phrases();
				ph.setPhrases_id(rs.getInt("ph.phrases_id"));
				ph.setPhrase(rs.getString("ph.phrase"));
				ph.setCategories_id(rs.getInt("ph.categories_id"));
				ph.setGeneres_id(rs.getInt("ph.generes_id"));
				ph.setSub_categories_id(rs.getInt("ph.sub_categories_id"));
				ph.setActive(rs.getBoolean("ph.active"));
			}
		}catch(Exception e) {
			System.err.println(e);
		}
		
		return ph;
 	}
	
	public Phrases getPhraseRandomSubCategorie() throws SQLException{
		
		Phrases ph = null;
		
		
		ResultSet rs = null;
		try (Connection conexion = conn.conectar();
				Statement st = conexion.createStatement();){
			
			String queryExce = "SELECT * FROM "+TABLE_NAME+" ph "
					+ "WHERE ph.active = ? AND ph.categories_id = ? "
					+ "AND ph.sub_categories_id = ? "
					+ "ORDER BY RAND() LIMIT 1;";
			PreparedStatement  query = (PreparedStatement) conexion.prepareStatement(queryExce);
			query.setInt(1, 1);
			query.setInt(2, getCategories_id());
			query.setInt(3, getSub_categories_id());
			rs = query.executeQuery();

			while (rs.next() ) {
			   ph = new Phrases();
               ph.setPhrase(rs.getString("ph.phrase"));
               ph.setActive(rs.getBoolean("ph.active"));
               ph.setCategories_id(rs.getInt("ph.categories_id"));
               ph.setSub_categories_id(rs.getInt("ph.sub_categories_id"));
			}
		}catch(Exception e) {
			System.err.println(e);
		}
		
		return ph;
 	}
	
	public int getPhrases_id() {
		return phrases_id;
	}

	public void setPhrases_id(int phrases_id) {
		this.phrases_id = phrases_id;
	}

	public String getPhrase() {
		return phrase;
	}
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getCategories_id() {
		return categories_id;
	}

	public void setCategories_id(int campaings_id) {
		this.categories_id = campaings_id;
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
