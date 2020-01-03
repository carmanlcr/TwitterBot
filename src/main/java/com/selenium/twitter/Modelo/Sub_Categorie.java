package com.selenium.twitter.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.selenium.twitter.Interface.Model;


public class Sub_Categorie implements Model {
	
	private final String TABLE_NAME = "sub_categories";
	private int sub_categories_id;
	private String name;
	private int categories_id;
	private static Conexion conn = new Conexion();
	
	public void insert() throws SQLException {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		String strDate= formatter.format(date);
			try (Connection conexion = conn.conectar();){
				String insert = "INSERT INTO "+TABLE_NAME+"(name,categories_id,created_at,updated_at) "
						+ "VALUES (?,?,?,?);";
				PreparedStatement exe = conexion.prepareStatement(insert);
				exe.setString(1, getName());
				exe.setInt(2, getCategories_id());
				exe.setString(3, strDate);
				exe.setString(4, strDate);
				exe.executeUpdate();
				
			} catch(SQLException e)  {
				System.err.println(e);
			} catch(Exception e){
				System.err.println(e);
				
			}
	}
	
	@Override
	public void update() throws SQLException {
		
	}
	
	public List<String> getSubCategories(){
		List<String> list = new ArrayList<String>();
		String queryExce = "SELECT sca.name FROM "+TABLE_NAME+" sca "
				+ "WHERE sca.categories_id = ? ; ";
		
		ResultSet rs = null;
		try (Connection conexion = conn.conectar();
				PreparedStatement  query = conexion.prepareStatement(queryExce);){
			
			query.setInt(1, getCategories_id());
			rs = query.executeQuery();
			
			while (rs.next() ) {
               list.add(rs.getString("sca.name"));
			}
		}catch(Exception e) {
			System.err.println(e);
		}
		
		return list;
	}

	public int getIdPhraseSubCategories(String name) throws SQLException {

		int indice = 0;
		
		ResultSet rs = null;
		String queryExce = "SELECT sca.sub_categories_id FROM "+TABLE_NAME+" sca "
				+ "WHERE sca.name = ? LIMIT 1; ";
		try (Connection conexion = conn.conectar();
				PreparedStatement  query = conexion.prepareStatement(queryExce);){
			
			query.setString(1, name);
			rs = query.executeQuery();

			while (rs.next() ) {
               indice =  rs.getInt("sca.sub_categories_id");
			}
		}catch(Exception e) {
			System.err.println(e);
		}
		
		return indice;
	}
	
	public Sub_Categorie getIdSubCategories() throws SQLException{
		Sub_Categorie sub_Cate = null;
		String query = "SELECT * FROM "+TABLE_NAME 
				+ " WHERE name = ? LIMIT 1;";
		ResultSet rs = null;
		try (Connection conexion = conn.conectar();
				PreparedStatement pre = conexion.prepareStatement(query);){
			pre.setString(1, getName());
			
			rs = pre.executeQuery();
			
			if(rs.next()) {
				sub_Cate = new Sub_Categorie();
				sub_Cate.setName(rs.getString("name"));
				sub_Cate.setSub_categories_id(rs.getInt("sub_categories_id"));
				sub_Cate.setCategories_id(rs.getInt("categories_id"));
			}
		}catch (SQLException e) {
			e.getStackTrace();
		}
		
		return sub_Cate;
	}
	
	public Sub_Categorie getOneRandom() throws SQLException {
		Sub_Categorie sub_c = null;
		
		String query = "SELECT * " + 
				"FROM sub_categories sc " + 
				"INNER JOIN phrases ph ON ph.sub_categories_id = sc.sub_categories_id " + 
				"INNER JOIN path_photos pp ON pp.sub_categories_id = sc.sub_categories_id " + 
				"ORDER BY RAND() LIMIT 1;";
		try(Connection conexion = conn.conectar();
				Statement st = conexion.createStatement();){
			
			ResultSet rs = st.executeQuery(query);
			if(rs.next()) {
				sub_c = new Sub_Categorie();
				sub_c.setSub_categories_id(rs.getInt("sc.sub_categories_id"));
				sub_c.setName(rs.getString("sc.name"));
				sub_c.setCategories_id(rs.getInt("sc.categories_id"));
			}
		}catch (SQLException e) {
		}
		
		return sub_c;
	}
	
	public int getSub_categories_id() {
		return sub_categories_id;
	}

	public void setSub_categories_id(int sub_categories_id) {
		this.sub_categories_id = sub_categories_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCategories_id() {
		return categories_id;
	}

	public void setCategories_id(int categories_id) {
		this.categories_id = categories_id;
	}

}
