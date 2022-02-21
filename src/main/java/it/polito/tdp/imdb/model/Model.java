package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private ImdbDAO dao;
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private Map<Integer, Actor> attoriMap;
	
	private Simulator sim;
	
	
	public Model() {
		dao = new ImdbDAO();
		attoriMap = new HashMap<>();
		dao.listAllActors(attoriMap);
	}
	
	public void creaGrafo(String genere) {
		//inizializzo grafo
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo vertici
		Graphs.addAllVertices(this.grafo, dao.getVertici(genere, attoriMap));
		
		//aggiungo archi
		for(Adiacenza a: dao.getAdiacenze(genere, attoriMap)) {
			if (grafo.getEdge(a.getA1(), a.getA2())==null) {
				Graphs.addEdge(this.grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
	}
	
	public List<String> getGeneri() {
		// TODO Auto-generated method stub
		return dao.listaGenres();
	}

	public int vertexNumber() {
		// TODO Auto-generated method stub
		return grafo.vertexSet().size();
	}

	public int edgeNumber() {
		// TODO Auto-generated method stub
		return grafo.edgeSet().size();
	}

	public List<Actor> getAttori() {
		List<Actor> attori = new ArrayList<>(grafo.vertexSet());
		
		Collections.sort(attori, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				
				return o1.lastName.compareTo(o2.lastName);
			}
			
		}
				);
		
		return attori;
	}

		public List<Actor> getAttoriConnessi(Actor a){
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(grafo);
		List<Actor> actors = new ArrayList<>(ci.connectedSetOf(a));
		actors.remove(a);
		Collections.sort(actors, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				return o1.lastName.compareTo(o2.lastName);
			}
			
		});
		return actors;
		}

		public void simulate(int n) {
			sim = new Simulator(n, grafo);
			sim.init();
			sim.run();
		}
		
		public Collection<Actor> getInterviewedActors(){
			if(sim == null){
				return null;
			}
			return sim.getInterviewedActors();
		}
		
		public Integer getPauses(){
			if(sim == null){
				return null;
			}
			return sim.getPauses();
		}
}
