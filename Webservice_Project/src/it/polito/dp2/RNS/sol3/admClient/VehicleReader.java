package it.polito.dp2.RNS.sol3.admClient;

import java.util.Calendar;

import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.Places;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;

public class VehicleReader implements it.polito.dp2.RNS.VehicleReader{
	
	private Vehicle v = new Vehicle();
	private Places places = null;
	
	public VehicleReader(Vehicle v, Places places) {
		this.v = v;
		this.places = places;
	}
	
	@Override
	public String getId() {
		return v.getId();
	}

	@Override
	public PlaceReader getDestination() {
		it.polito.dp2.RNS.PlaceReader pr = null;
		for(Place pt:places.getPlace()){
			if(v.getIsDirectedTo().equals(pt.getId())){
				pr = new it.polito.dp2.RNS.sol3.admClient.PlaceReader(pt);
				break;
			}
		}
		return pr;
	}

	@Override
	public PlaceReader getOrigin() {
		it.polito.dp2.RNS.PlaceReader pr = null;
		for(Place pt:places.getPlace()){
			if(v.getComesFrom().equals(pt.getId())){
				pr = new it.polito.dp2.RNS.sol3.admClient.PlaceReader(pt);
				break;
			}
		}
		return pr;
	}

	@Override
	public PlaceReader getPosition() {
		it.polito.dp2.RNS.PlaceReader pr = null;
		for(Place pt:places.getPlace()){
			if(v.getPosition().equals(pt.getId())){
				pr = new it.polito.dp2.RNS.sol3.admClient.PlaceReader(pt);
				break;
			}
		}
		return pr;
	}

	@Override
	public VehicleState getState() {
		return VehicleState.valueOf(v.getVehicleState().name());
	}

	@Override
	public VehicleType getType() {
		return it.polito.dp2.RNS.VehicleType.valueOf(v.getVehType().name());
	}
	
	@Override
	public Calendar getEntryTime() {
		return v.getEntryTime().toGregorianCalendar();
	}

}
