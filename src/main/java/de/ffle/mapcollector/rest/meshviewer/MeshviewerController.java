package de.ffle.mapcollector.rest.meshviewer;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.ffle.mapcollector.CommunityFilter;
import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.model.NodeLink;
import de.ffle.mapcollector.model.NodeStats;
import de.ffle.mapcollector.model.NodeType;
import de.ffle.mapcollector.repository.INodeRepository;

@RestController
@RequestMapping("/meshviewer")
public class MeshviewerController {

	@Autowired
	protected INodeRepository nodeRepository;
	
	@Autowired
	protected CommunityFilter communityFilter;
	
	protected final int OFFLINE_MINUTES=10;
	
	protected final int HIDE_STALE_TEMP_NODES_AFTER_MINUTES=30;
	protected final int HIDE_STALE_NODES_AFTER_DAYS=30;
	
	protected MeshviewerResponse response;
	
	protected void fetch() {
		MeshviewerResponse response=new MeshviewerResponse();
		
		for (Node node: nodeRepository.getNodes()) {
			if (node.getId()==null || node.getInfo()==null) {
				continue;
			}
			if (!communityFilter.isShownCommunity(node.getInfo())) {
				continue;
			}
			if (isStale(node,response.timestamp)) {
				continue;
			}
			
			MeshviewerNode n=new MeshviewerNode();
			response.nodes.add(n);
			n.nodeId=node.getId();
			n.addresses=Collections.singletonList(node.getPrimaryIpAddress());
			n.mac=generateMac(node.getId());
			n.nodeName=buildNodeName(node);
			n.firstseen=node.getFirstSeen();
			n.lastseen=node.getLastSeen();
			
			n.isGateway=node.getInfo().getNodeType()==NodeType.server;
			
			n.contact=node.getInfo().getContactEmail();
			
			n.model=node.getInfo().getModel();
			
			n.cpuCount=node.getInfo().getCpuCount();
			
			if (!n.isGateway) { // don't show gateways on map
				n.location=MeshviewerLocation.of(
						node.getInfo().getLocationLatitude(),
						node.getInfo().getLocationLongitude(),
						node.getInfo().getLocationAltitude()
						);
			}
			
			n.firmware=MeshviewerFirmware.of(
					node.getInfo().getFirmwareBase(),
					node.getInfo().getFirmwareRelease()
					);

			n.autoupdater=MeshviewerAutoUpdater.of(
					node.getInfo().getAutoUpdate()
					);
			
			n.domain=node.getInfo().getCommunity();
			
			if (isOnline(node,response.timestamp)) {
				n.online=true;
				NodeStats stats=node.getStats();
				if (stats!=null) {
					if (stats.getSelectedGateway()!=null) {
						String gateway=stats.getSelectedGateway();
						if (stats.getPreferredGateway()!=null && !stats.getSelectedGateway().equals(gateway)) {
							gateway+=" ("+stats.getPreferredGateway()+")";
						}
						n.gateway=gateway;
					}
					n.clientsWifi24=Optional.ofNullable(stats.getClients2g()).orElse(0);
					n.clientsWifi5=Optional.ofNullable(stats.getClients5g()).orElse(0);
					n.clientsTotal=n.clientsWifi24+n.clientsWifi5;
					
					if (stats.getUptime()!=null) {
						n.uptime=stats.getUptime();
					}
					
					
					n.loadAvg=stats.getLoadAvg5();

					if (stats.getMemTotal()!=null && stats.getMemFree()!=null) {
						n.memoryUsage=(1d-(stats.getMemFree()*100/stats.getMemTotal())/100d);
					}
						
					
				}
			}
		}
		
		for (NodeLink link: nodeRepository.getNodeLinks()) {
			if (!link.isValid(OFFLINE_MINUTES)) {
				continue;
			}
			
			MeshviewerLink l=new MeshviewerLink();
			l.sourceId=link.getLeftNodeId();
			l.sourceMac=generateMac(link.getLeftNodeId());
			Integer sourceTq=link.getTq(OFFLINE_MINUTES);
			if (sourceTq!=null) {
				l.sourceTq=sourceTq/100d;
			}

			l.targetId=link.getRightNodeId();
			l.targetMac=generateMac(link.getRightNodeId());
			Integer targetTq=link.getRq(OFFLINE_MINUTES);
			if (targetTq!=null) {
				l.targetTq=targetTq/100d;
			}
			
			switch(link.getType()) {
				case lan:
					l.type="lan";
					break;
				case wifi:
				case wifi_mesh:
				case wifi_adhoc:
					l.type="wifi";
					break;
				case backbone:
					l.type="vpn";
					break;
				default:
					l.type="other";
					break;
			}
			response.links.add(l);
		}
		this.response=response;
		
	}

	protected boolean isStale(Node node, ZonedDateTime now) {
		if (node.getLastSeen()==null) {
			return true;
		}
		if (node.getId().matches("9\\d\\d")) { // 9xx = temp nodes
			return Duration.between(node.getLastSeen(), now).toMinutes()>HIDE_STALE_TEMP_NODES_AFTER_MINUTES;
		}
		return Duration.between(node.getLastSeen(), now).toDays()>HIDE_STALE_NODES_AFTER_DAYS;
	}
	
	protected boolean isOnline(Node node, ZonedDateTime now) {
		if (node.getLastSeen()==null) {
			return false;
		}
		return Duration.between(node.getLastSeen(), now).toMinutes()<OFFLINE_MINUTES;
	}
	
	protected String buildNodeName(Node node) {
		if (StringUtils.isBlank(node.getInfo().getName())) {
			return node.getId();
		}
		return node.getInfo().getName()+" ("+node.getId()+")";
	}
	
	protected String generateMac(String nodeIdStr) {
		try {
			int nodeId=Integer.parseInt(nodeIdStr);
			return String.format("ffdd00%06x", nodeId & 0xFFFFF).replaceAll("(..)", ":$1").substring(1);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	
	@GetMapping("/meshviewer.json")
	@CrossOrigin
	public Object getMeshviewerJson() {
		fetch();
		return response;
	}
	
	

}
