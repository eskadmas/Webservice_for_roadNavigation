package it.polito.dp2.RNS.sol3.service.dataAndNeo4j;

import java.util.List;
import it.polito.dp2.RNS.sol3.jaxb.*;

public class VehicleExt {
	private String id;
	private Vehicle vehicle;
	private List<String> suggestedPath;
	

	public VehicleExt(String id, Vehicle vehicle, List<String> path){
		this.id = id;
		this.vehicle = vehicle;
		this.suggestedPath = path;
	}

	public List<String> getSuggestedPath() {
		return suggestedPath;
	}

	public void setSuggestedPath(List<String> suggestedPath) {
		this.suggestedPath = suggestedPath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
}