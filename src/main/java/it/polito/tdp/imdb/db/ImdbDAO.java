package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<String> listaGenres() {
		String sql ="SELECT DISTINCT genre\n"
				+ "FROM movies_genres\n"
				+ "ORDER BY genre";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(res.getString("genre"));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void listAllActors(Map<Integer, Actor> attoriMap){
		String sql = "SELECT * FROM actors";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				attoriMap.put(res.getInt("id"), actor);
			}
			conn.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Actor> getVertici(String genere, Map<Integer, Actor> attoriMap) {
		String sql = "SELECT r.actor_id AS r\n"
				+ "FROM roles r, movies_genres g, movies m\n"
				+ "WHERE r.movie_id = m.id AND m.id=g.movie_id AND g.genre = ?";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(attoriMap.containsKey(res.getInt("r")))	
					result.add(attoriMap.get(res.getInt("r")));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getAdiacenze(String genere, Map<Integer, Actor> attoriMap) {
		String sql = "SELECT r1.actor_id AS a1, r2.actor_id AS a2, COUNT(DISTINCT r1.movie_id) AS peso\n"
				+ "FROM roles r1, roles r2, movies_genres mg\n"
				+ "WHERE r1.movie_id = r2.movie_id\n"
				+ "AND r1.actor_id >r2.actor_id\n"		
				+ "AND mg.movie_id=r1.movie_id\n"
				+ "AND mg.genre = ?\n"
				+ "GROUP BY r1.actor_id, r2.actor_id";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(attoriMap.containsKey(res.getInt("a1")) && attoriMap.containsKey(res.getInt("a2"))) {
					result.add(new Adiacenza(
							attoriMap.get(res.getInt("a1")),
							attoriMap.get(res.getInt("a2")), 
							res.getInt("peso")));
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	}
	
	
	
	

