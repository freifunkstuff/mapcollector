package de.ffle.mapcollector.source;

import java.io.IOException;
import java.util.List;

import de.ffle.mapcollector.model.NodeAddress;

/**
 * Source of complete node list. May optionally be pre-filtered by community
 */
public interface INodeListSource {
	
	public List<NodeAddress> fetchNodes() throws IOException;

}
