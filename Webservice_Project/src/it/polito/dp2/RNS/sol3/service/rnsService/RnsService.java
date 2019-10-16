package it.polito.dp2.RNS.sol3.service.rnsService;

import it.polito.dp2.RNS.sol3.jaxb.*;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.RnsData;
import it.polito.dp2.RNS.lab2.BadStateException;
import it.polito.dp2.RNS.lab2.ServiceException;
import it.polito.dp2.RNS.lab2.UnknownIdException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.EntranceRefusedException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.UnknownPlaceException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.WrongPlaceException;

import java.util.List;

public class RnsService {	
	
	private RnsData data = RnsData.getData();

	public Places getPlaces() {
		Places places = new Places();
		places.getPlace().addAll(data.getPlaces());
		
		return places;
	}
	
	public List<String> addVehicle(String id, Vehicle vehicle) throws UnknownIdException, WrongPlaceException, BadStateException, ServiceException, UnknownPlaceException, EntranceRefusedException {
		
		return data.addVehicle(id, vehicle);
	}
	
	public TrackedVehicles getVehicles(String place) {
	
		TrackedVehicles vehicles = new TrackedVehicles();
		vehicles.getVehicle().addAll(data.getVehicles(place));
		
		return vehicles;
	}
	
	public Vehicle getVehicle(String id) {
		Vehicle vehicle = new Vehicle();
		vehicle = data.getVehicle(id);
		
		return vehicle;
	
	}

	public List<String> moveVehicle(String id, String newPlace) throws UnknownPlaceException, WrongPlaceException, UnknownIdException{
		
		return data.moveVehicle(id, newPlace);
	}
	
	public Vehicle changeState(String id, VehicleStateType newState) {
		
		return data.changeState(id, newState);
	}
	
	public boolean removeVehicle(String id, String newPosition) throws WrongPlaceException, UnknownPlaceException {
		
		return data.deleteVehicle(id, newPosition);
	}
}
