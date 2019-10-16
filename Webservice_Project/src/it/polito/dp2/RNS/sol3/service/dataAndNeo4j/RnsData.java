package it.polito.dp2.RNS.sol3.service.dataAndNeo4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.UriInfo;

import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RnsReaderFactory;

import it.polito.dp2.RNS.sol3.jaxb.*;
import it.polito.dp2.RNS.lab2.BadStateException;
import it.polito.dp2.RNS.lab2.ModelException;
import it.polito.dp2.RNS.lab2.PathFinderException;
import it.polito.dp2.RNS.lab2.ServiceException;
import it.polito.dp2.RNS.lab2.UnknownIdException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.EntranceRefusedException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.UnknownPlaceException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.WrongPlaceException;

public class RnsData {
	
	public UriInfo uriInfo;
	private static RnsData data;
	static { 
		try {
			data = new RnsData();
		}catch(ModelException m){
			;
		}catch(ServiceException s){
			;
		}catch(PathFinderException p){
			;
		}
	}
	private static RnsReader monitor;
	private static long lastPlaceId=0;
	it.polito.dp2.RNS.sol2.PathFinderFactory pathFinder;
	private ConcurrentHashMap<Long, PlaceExt> placeExtById;
	private static ConcurrentHashMap<String, VehicleExt> vehicleExtById;
	
	Set<it.polito.dp2.RNS.sol3.jaxb.Place> places = new HashSet<>();
	
	private RnsData() throws PathFinderException, ServiceException, ModelException{
		
		placeExtById = new ConcurrentHashMap<Long, PlaceExt>();
		RnsReaderFactory factory = RnsReaderFactory.newInstance();
		try {
			monitor = factory.newRnsReader();
		} catch (RnsReaderException e) {
			e.printStackTrace();
		}
		vehicleExtById = new ConcurrentHashMap<String, VehicleExt>();
		places = createPlaces();
		pathFinder = new it.polito.dp2.RNS.sol2.PathFinderFactory(monitor);
		
	}
	
	public Set<it.polito.dp2.RNS.sol3.jaxb.Place> createPlaces(){
		
		Set<it.polito.dp2.RNS.sol3.jaxb.Place> places = new HashSet<>();			
		long id;
		
		for(RoadSegmentReader rs:monitor.getRoadSegments(null)){	
			it.polito.dp2.RNS.sol3.jaxb.Place place = new it.polito.dp2.RNS.sol3.jaxb.Place();
			place.setId(rs.getId());
			place.setCapacity(rs.getCapacity());
			RoadSegmentType roadSegment = new RoadSegmentType();
			roadSegment.setRoadName(rs.getRoadName());
			roadSegment.setName(rs.getName());
			place.setRoadSegment(roadSegment);
			
			id = RnsData.getNextPlaceId();
	    	place.setSelf("localhost:8080/RnsSystem/rest/entity/places/"+id);
			places.add(place);
		}
		
		for(GateReader gr:monitor.getGates(null)){
			it.polito.dp2.RNS.sol3.jaxb.Place place = new it.polito.dp2.RNS.sol3.jaxb.Place();
			place.setId(gr.getId());
			place.setCapacity(gr.getCapacity());
			place.setGate(gr.getType().name());
			
			id = RnsData.getNextPlaceId();
	    	place.setSelf("localhost:8080/RnsSystem/rest/entity/places/"+id);
			places.add(place);
		}
		
		for(ParkingAreaReader pr:monitor.getParkingAreas(null)){
			it.polito.dp2.RNS.sol3.jaxb.Place place = new it.polito.dp2.RNS.sol3.jaxb.Place();
			place.setId(pr.getId());
			place.setCapacity(pr.getCapacity());
			ParkingAreaType parkingArea = new ParkingAreaType();
			parkingArea.getService().addAll(pr.getServices());
			place.setParkingArea(parkingArea);	
			
			id = RnsData.getNextPlaceId();
	    	place.setSelf("localhost:8080/RnsSystem/rest/entity/places/"+id);
			places.add(place);
		}
		
		for(it.polito.dp2.RNS.sol3.jaxb.Place p:places){
			for(ConnectionReader cr:monitor.getConnections()){
				if(p.getId().equals(cr.getFrom().getId())){
					for(it.polito.dp2.RNS.sol3.jaxb.Place n:places){
						if(cr.getTo().getId().equals(n.getId()))
							p.getIsconnectedTo().add(n.getId());
					}
				}
			}
			Long placeId = Long.parseLong(p.getSelf().substring(p.getSelf().lastIndexOf("/") + 1));
			PlaceExt plExt = new PlaceExt(placeId, p);
			placeExtById.put(placeId, plExt);
			
		}
		return places;
	}
	
	public static synchronized long getNextPlaceId() {
		return ++lastPlaceId;
	}
	
	public static RnsData getData() {
		return data;
	}
	//1.
	public Collection<it.polito.dp2.RNS.sol3.jaxb.Place> getPlaces() {
		
		Collection<it.polito.dp2.RNS.sol3.jaxb.Place> col = new HashSet<>();
		for (Map.Entry<Long, PlaceExt> entry : placeExtById.entrySet()){
		   col.add(entry.getValue().getPlace());
		}
		return col;
	}
	
	public List<String> addVehicle(String id, Vehicle vehicle) throws WrongPlaceException, BadStateException, ServiceException, UnknownPlaceException, EntranceRefusedException {
			
		// Checking whether the start and destination of a vehicle are available in the set of places
		// Checking whether the start place is an IN or INOUT gate
		String origin = null;
		String destination = null;
		it.polito.dp2.RNS.sol3.jaxb.Place originP = null;
		
		for(it.polito.dp2.RNS.sol3.jaxb.Place p:places){
			if(p.getId().equals(vehicle.getIsDirectedTo()))
				destination = vehicle.getIsDirectedTo();
			
			if(p.getId().equals(vehicle.getComesFrom())){
					origin = vehicle.getComesFrom();
					originP = p;
			}			
		}
		if((origin==null)||(destination==null)){
			throw new UnknownPlaceException();
		}		
		
		else if((originP.getGate()==null)||!((originP.getGate().equals("IN"))||(originP.getGate().equals("INOUT")))){			
			throw new WrongPlaceException();
		}
		else{
			try {
				Set<List<String>> paths = pathFinder.newPathFinder().findShortestPaths(vehicle.getComesFrom(), vehicle.getIsDirectedTo(), 20);
				
				if(paths.isEmpty()){
					throw new EntranceRefusedException();
				}
				for(List<String> path:paths){
					VehicleExt vExt = new VehicleExt(id, vehicle, path);
					vehicleExtById.putIfAbsent(id, vExt);
					
					return path;
				}
			} catch (UnknownIdException | BadStateException | ServiceException | PathFinderException e){
				
			} 			
		}				
		return null;
	}
	
	public Collection<Vehicle> getVehicles(String place) {
		
		Collection<Vehicle> col = new HashSet<>();
		Vehicle v = new Vehicle();
		if(place == null){
			for (Map.Entry<String, VehicleExt> entry : vehicleExtById.entrySet()){
			   col.add(entry.getValue().getVehicle());
			}
		}
		else{
			for (Map.Entry<String, VehicleExt> entry : vehicleExtById.entrySet()){
				v =entry.getValue().getVehicle();
				if(v.getPosition().equals(place))
					col.add(v);
			}
		}			
		return col;
	}	
	
	public Vehicle getVehicle(String str) {
		for (Map.Entry<String, VehicleExt> entry : vehicleExtById.entrySet()){
			if(entry.getValue().getVehicle().getId().equals(str)){
				return entry.getValue().getVehicle();
			}
		}
		return null;
		
	}	
		
	public List<String> moveVehicle(String vid, String newPlace) throws UnknownPlaceException, WrongPlaceException, UnknownIdException {
		
		Set<List<String>> paths = null;
		List<String> pathR = null;
		Vehicle v = vehicleExtById.get(vid).getVehicle();
		
		if(vehicleExtById.get(vid).getSuggestedPath().contains(newPlace)){
			vehicleExtById.get(vid).getVehicle().setPosition(newPlace);
			throw new UnknownIdException();
		}
		
		boolean placeInRns = false;
		
		for(it.polito.dp2.RNS.sol3.jaxb.Place p:places){
			if(p.getId().equals(newPlace)){
				placeInRns = true;
				break;
			}
		}
		
		if(placeInRns){
			try {
				paths = pathFinder.newPathFinder().findShortestPaths(v.getPosition(), newPlace, 20);
			}catch (Exception e) {
				
			}
			if(paths == null)			// If the is no way to go from the previous position to the new place
				throw new WrongPlaceException();
			
			try {
				Set<List<String>> newPaths = pathFinder.newPathFinder().findShortestPaths(newPlace, v.getIsDirectedTo(), 20);
				if (newPaths != null){							
					for(List<String> path:newPaths){
						vehicleExtById.get(vid).setSuggestedPath(path);
						vehicleExtById.get(vid).getVehicle().setPosition(newPlace);
						pathR = path;
					}
				} 
			}catch (UnknownIdException | BadStateException | ServiceException | PathFinderException e){
				
			} 
			
			return pathR;
		}
		else{
			throw new UnknownPlaceException();
		}
	}
	
	public Vehicle changeState(String id, VehicleStateType newState) {
		
		Vehicle v = new Vehicle();
		v = vehicleExtById.get(id).getVehicle();
		v.setVehicleState(newState);
		
		return v;
	}	
	
	public boolean deleteVehicle(String id, String currentPos) throws WrongPlaceException, UnknownPlaceException {
		
		Set<List<String>> paths = null;
		Vehicle v = null;
		v = vehicleExtById.get(id).getVehicle();
		
		if(v!=null){			
			it.polito.dp2.RNS.sol3.jaxb.Place position = null;	
			for(it.polito.dp2.RNS.sol3.jaxb.Place p:places){
				if(p.getId().equals(currentPos)){
					position = p;
					break;
				}
			}
			if(position==null)
				throw new UnknownPlaceException();
						
			if((position.getGate().equals("OUT"))||(position.getGate().equals("INOUT"))){
				try {
					paths = pathFinder.newPathFinder().findShortestPaths(v.getPosition(), currentPos, 20);
				}catch (Exception e) {
					
				}
				if(paths == null)
					throw new WrongPlaceException();
				
				else{
					vehicleExtById.remove(id);
					
					return true;
				}					
			}
			else
				throw new WrongPlaceException();
		}
		else 
			return false;
	}	
}