package it.polito.dp2.RNS.sol2;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RnsReaderFactory;
import it.polito.dp2.RNS.lab2.BadStateException;
import it.polito.dp2.RNS.lab2.ModelException;
import it.polito.dp2.RNS.lab2.ServiceException;
import it.polito.dp2.RNS.lab2.UnknownIdException;
import it.polito.dp2.RNS.sol2.jaxb.*;

public class PathFinderClient implements it.polito.dp2.RNS.lab2.PathFinder {
	
	RnsReader rnsReader;
	
	Client client;
	WebTarget target;
	Map<String, URI> map;
	
	private boolean modelLoaded = false;
	
	public PathFinderClient() throws RnsReaderException {
		
		RnsReaderFactory factory = RnsReaderFactory.newInstance();
		rnsReader = factory.newRnsReader();
		
		try{
			// Create a Client 
			client = ClientBuilder.newClient();
			
			// Create a web target for the main URI
			target = client.target(System.getProperty("it.polito.dp2.RNS.lab3.Neo4JURL"));
			
			// Create the Map
			map = new HashMap<String, URI>();
		
		} 
		catch (Exception e){
			
			 System.out.println ("Fatal error: " + e);
		}
		
	}

	@Override
	public boolean isModelLoaded() {
		
		return modelLoaded;
	}

	@Override
	public void reloadModel() throws ServiceException, ModelException {
		
		Place place = new Place();
		
		// Get the list of places
		Set<PlaceReader> set = rnsReader.getPlaces(null);
		
		for(PlaceReader pl:set){
			place.setId(pl.getId());
			
			Response res = target.path("/data/node/")
					.request("application/json")
		      		.post(Entity.entity(place, MediaType.APPLICATION_JSON));
			
			if(res.getStatus()!=201){
				System.out.println("Problem in creating node");
			}
			
			// Get the URI location of an entity
	    	URI uri = res.getLocation();
	    	map.put(pl.getId(), uri);
		}
		
		Relationship relation = new Relationship();
		
		Set<ConnectionReader> connections = rnsReader.getConnections();
		
		for (ConnectionReader connect: connections) {
			
			String src = connect.getFrom().getId();
			String dst = connect.getTo().getId();
			URI srcuri = map.get(src);
			URI dsturi = map.get(dst);
			
			relation.setTo(dsturi.toString());
			relation.setType("ConnectedTo");
			
			Response res = target.path("/data/node/" + srcuri.toString().substring(srcuri.toString().lastIndexOf("/") + 1) +"/relationships")
				.request("application/json")
				.post(Entity.entity(relation, MediaType.APPLICATION_JSON));
			
			if(res.getStatus()!=201){
				System.out.println("Problem in creating relationship");
			}
			
		}
		
		modelLoaded = true;
		
	}

	@Override
	public Set<List<String>> findShortestPaths(String source, String destination, int maxlength)
			throws UnknownIdException, BadStateException, ServiceException {
		
		try {
			reloadModel();
		} catch (Exception e) {
	
		}
		
		ShortestPath sPath = new ShortestPath();
				
		PathRelationship relation = new PathRelationship();
		relation.setType("ConnectedTo");
		relation.setDirection("out");
		
		URI srcuri = map.get(source);
		URI dsturi = map.get(destination);
		
		if((srcuri==null)||(dsturi==null)){
			throw new UnknownIdException();
		}
		
		sPath.setTo(dsturi.toString());
		sPath.setMaxDepth(maxlength);
		sPath.setRelationships(relation);
		sPath.setAlgorithm("shortestPath");
		
		Response res = target.path("/data/node/" + srcuri.toString().substring(srcuri.toString().lastIndexOf("/") + 1) +"/paths")
			.request("application/json")
			.post(Entity.entity(sPath, MediaType.APPLICATION_JSON));
		
		Path[] path = res.readEntity(Path[].class);
		
		Set<List<String>> pathlist = new HashSet<>();
		List<String> shortestPath = new ArrayList<>();
		
		for(int i=0; i<path.length; i++){
			for(String str:path[i].getNodes()){
				for(Entry<String, URI> entry:map.entrySet()){
					if(str.equals(entry.getValue().toString()))
							shortestPath.add(entry.getKey());	
				}
			}
			pathlist.add(shortestPath);
		}
		
		return pathlist;

	}

}
