package de.ffle.mapcollector.source;

import java.io.IOException;
import java.util.List;

import de.ffle.mapcollector.model.INodeAddress;

/**
 * Source of complete node list
 */
public interface INodeListSource {
	
	public List<INodeAddress> fetchNodes() throws IOException;

}
