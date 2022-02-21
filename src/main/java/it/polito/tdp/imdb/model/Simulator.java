package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulator {
	
	//CODA DEGLI EVENTI (non serve qua)
	
	//PARAMETRI DI INPUT
	Integer giorni;
	List<Actor> attoriDisponibili;
	
	//MODELLO DEL MONDO
	Graph<Actor, DefaultWeightedEdge> grafo;
	
	//PARAMETRI DI OUTPUT
	Map<Integer, Actor> attoriIntervistati;
	Integer pauses;
	
	
	
	public Simulator(int n, Graph<Actor, DefaultWeightedEdge> graph) {
		this.giorni = n;
		this.grafo = graph;
	}
	
	public void init() {
		attoriIntervistati = new HashMap<Integer,Actor>();
		this.pauses=0;
		this.attoriDisponibili= new ArrayList<Actor>(this.grafo.vertexSet());
	}
	
	public void run() {
		
		for (int giorno=1; giorno<this.giorni;giorno++) 	{
			
			Random rand = new Random();
			
			if(giorno ==1 || !attoriIntervistati.containsKey(giorno-1)) {
				//scelgo a caso l'attore: rand.nextInt(numero) mi da un numero casuale tra 0 e numero
				Actor attore = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
				attoriIntervistati.put(giorno, attore);
				attoriDisponibili.remove(attore);
				System.out.println("[GIORNO " + giorno + "] - selezionato autore casualmente (" + attore.toString() + ")");
				 continue ;
				
			}
			//se vengono intervistati 2 attori dello stesso genere, con 90% di probabilità ho pausa
			if(giorno>=3 && attoriIntervistati.containsKey(giorno-1) && attoriIntervistati.containsKey(giorno-2) && 
					attoriIntervistati.get(giorno-1).gender.equals(attoriIntervistati.get(giorno-2))) {
				if (rand.nextFloat()<=0.9) {
					this.pauses ++;
					System.out.println("[GIORNO " + giorno + "] - pausa!");
					continue ;
				}
			}
			
			//il produttore si fa consigliare con una probabilita del 40%
			if(rand.nextFloat() <= 0.6) {
				//scelgo ancora casualmente
				Actor actor = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
				attoriIntervistati.put(giorno, actor);
				//rimuovo l'attore scelto da quelli disponibili
				attoriDisponibili.remove(actor);		
				System.out.println("[GIORNO " + giorno + "] - selezionato autore casualmente (" + actor.toString() + ")");
				continue ;
			} else {
				//il produttore si fa consigliare dall'ultimo intervistato
				
				Actor ultimoAttore = attoriIntervistati.get(giorno-1);
				Actor raccomandato = this.getRaccomantato(ultimoAttore);
				
				if(raccomandato == null || !attoriDisponibili.contains(raccomandato)) {
					//se non fornisce consiglio o se è gia stato intervistato, ne scelgo uno casuale
					Actor attore = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
					attoriIntervistati.put(giorno, attore);
					//rimuovo l'attore scelto da quelli disponibili
					attoriDisponibili.remove(attore);		
					System.out.println("[GIORNO " + giorno + "] - selezionato autore casualmente (" + attore.toString() + ")");
					continue ;
				} else {
					attoriIntervistati.put(giorno, raccomandato);
					attoriDisponibili.remove(giorno);
					System.out.println("[GIORNO " + giorno + "] - selezionato autore casualmente (" + raccomandato.toString() + ")");
					continue ;
				}
			}
		}
		
		}

	private Actor getRaccomantato(Actor ultimoAttore) {
		Actor raccomandato = null;
		int peso =0;
		for (Actor vicino : Graphs.neighborListOf(this.grafo, ultimoAttore)) {
			if(this.grafo.getEdgeWeight(this.grafo.getEdge(ultimoAttore, vicino)) > peso) {
				raccomandato = vicino;
				peso = (int) this.grafo.getEdgeWeight(this.grafo.getEdge(ultimoAttore, vicino));
		}
		
	}
		return raccomandato;
	}
	
	public int getPauses() {
		return this.pauses;
	}
	
	public Collection<Actor> getInterviewedActors(){
		return this.attoriIntervistati.values();
	}
}
