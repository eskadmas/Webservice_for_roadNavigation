package it.polito.dp2.RNS.sol2;

import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.lab2.PathFinder;
import it.polito.dp2.RNS.lab2.PathFinderException;

public class PathFinderFactory extends it.polito.dp2.RNS.lab2.PathFinderFactory  {
	
	RnsReader monitor;
	public PathFinderFactory(RnsReader monitor) {
		this.monitor = monitor;
	}
	
	private it.polito.dp2.RNS.sol2.PathFinderClient pClient = null;
	
	@Override
	public PathFinder newPathFinder() throws PathFinderException {
		
		try {
			
			pClient = new it.polito.dp2.RNS.sol2.PathFinderClient();
			
		} catch (RnsReaderException e) {
			
			e.printStackTrace();
		}
		
		return pClient;
	}

}
