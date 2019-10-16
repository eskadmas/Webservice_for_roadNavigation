package it.polito.dp2.RNS.sol3.admClient;

import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.Places;

public class ConnectionReader implements it.polito.dp2.RNS.ConnectionReader{
	
	private String to;
	private String from;
	private Places places;
	
	public ConnectionReader(String to, String from, Places places) {
		this.to = to;
		this.from = from;
		this.places = places;
	}
	
	@Override
	public PlaceReader getFrom() {
		
		PlaceReader pr = null;
		
		for(Place p:places.getPlace()){
			if(p.getId().equals(from)){	
				pr=new it.polito.dp2.RNS.sol3.admClient.PlaceReader(p);
				break;
			}
		}
		return pr;
	}

	@Override
	public PlaceReader getTo() {
		
		PlaceReader pr = null;
		
		for(Place p:places.getPlace()){
			if(p.getId().equals(to)){	
				pr=new it.polito.dp2.RNS.sol3.admClient.PlaceReader(p);
				break;
			}
		}
		return pr;
	}

}
