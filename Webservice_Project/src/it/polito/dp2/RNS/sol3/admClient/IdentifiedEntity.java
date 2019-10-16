package it.polito.dp2.RNS.sol3.admClient;

import java.util.ArrayList;
import java.util.List;

import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.Places;

public class IdentifiedEntity implements it.polito.dp2.RNS.IdentifiedEntityReader{
	
	private List<PlaceReader> places;
	
	public IdentifiedEntity(Places placesEnt) {
		
		places = new ArrayList<PlaceReader>();
		
		for (Place place:placesEnt.getPlace()) {
			places.add(new PlaceReader(place));
		}
	}

	@Override
	public String getId() {
		
		return null;
	}

}
