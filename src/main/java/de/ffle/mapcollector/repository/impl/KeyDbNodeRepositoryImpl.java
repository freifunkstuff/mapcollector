package de.ffle.mapcollector.repository.impl;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.ffle.mapcollector.model.LinkType;
import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeLink;
import de.ffle.mapcollector.model.NodeLinkUpdate;
import de.ffle.mapcollector.model.NodeStats;
import de.ffle.mapcollector.repository.INodeRepository;
import de.ffle.mapcollector.util.DataHelper;
import de.ffle.mapcollector.util.JSONHelper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Repository based on KeyDB (Redis clone with some unique features)
 */
@Service
public class KeyDbNodeRepositoryImpl implements INodeRepository {
	
	protected RedisClient redis;
	
	@Value("${noderepo.redis.host}")
	protected String keyDbHost;

	@Value("${noderepo.redis.port:6379}")
	protected int keyDbPort;

	@PostConstruct
	protected void init() {
		redis = RedisClient.create(new RedisURI(keyDbHost, keyDbPort, Duration.ofSeconds(15)));
		try (StatefulRedisConnection<String, String> con=redis.connect()) {
			RedisCommands<String, String> cmd=con.sync();
			cmd.info();
		}
	}
	
	protected Collection<String> getKnownNodeIds() {
		try (StatefulRedisConnection<String, String> con=redis.connect()) {
			RedisCommands<String, String> cmd=con.sync();
			return cmd.keys("node.*");
		}
	}

	protected boolean addNode(NodeAddress node) {
		try (StatefulRedisConnection<String, String> con=redis.connect()) {
			RedisCommands<String, String> cmd=con.sync();
			String nodeKey="node."+node.getId();
			boolean added=cmd.hsetnx(nodeKey,"id", node.getId());
			ZonedDateTime now=ZonedDateTime.now();
			if (added) {
				cmd.hset(nodeKey,"ip", node.getPrimaryIpAddress());
				cmd.hsetnx(nodeKey,"firstSeen", DataHelper.encodeToString(now));
			}
			return added;
		}
	}
	
	@Override
	public List<Node> getNodes() {
		try (StatefulRedisConnection<String, String> con=redis.connect()) {
			List<Node> result=new ArrayList<>();
			RedisCommands<String, String> cmd=con.sync();
			for (String nodeKey: cmd.keys("node.*")) {
				
				Map<String,String> data=cmd.hgetall(nodeKey);
				
				Node node=new Node(data.get("id"),data.get("ip"));
				node.setFirstSeen(DataHelper.decodeZonedDateTime(data.get("firstSeen")));
				node.setLastSeen(DataHelper.decodeZonedDateTime(data.get("lastSeen")));
				node.setLastFetched(DataHelper.decodeZonedDateTime(data.get("lastFetched")));
				node.setLastUpdated(DataHelper.decodeZonedDateTime(data.get("lastUpdated")));
				node.setInfo(JSONHelper.fromJson(NodeInfo.class, data.get("info")));
				node.setStats(JSONHelper.fromJson(NodeStats.class, data.get("stats")));
				
				result.add(node);
			}
			return result;
		}
	}

	@Override
	public List<NodeLink> getNodeLinks() {
		try (StatefulRedisConnection<String, String> con=redis.connect()) {
			List<NodeLink> result=new ArrayList<>();
			RedisCommands<String, String> cmd=con.sync();
			for (String nodeKey: cmd.keys("link.*")) {
				
				Map<String,String> data=cmd.hgetall(nodeKey);
				
				NodeLink link=new NodeLink();
				link.setType(DataHelper.decodeEnum(LinkType.class,data.get("type")));
				link.setLeftNodeId(data.get("leftNodeId"));
				link.setLeftTs(DataHelper.decodeZonedDateTime(data.get("leftTs")));
				link.setLeftTq(DataHelper.decodeInteger(data.get("leftTq")));
				link.setLeftRq(DataHelper.decodeInteger(data.get("leftRq")));

				link.setRightNodeId(data.get("rightNodeId"));
				link.setRightTs(DataHelper.decodeZonedDateTime(data.get("rightTs")));
				link.setRightTq(DataHelper.decodeInteger(data.get("rightTq")));
				link.setRightRq(DataHelper.decodeInteger(data.get("rightRq")));
				
				result.add(link);
			}
			return result;
		}
	}
	
	@Override
	public void updateNode(NodeAddress node, NodeInfo info, NodeStats stats, Collection<NodeLinkUpdate> links) {
		try (StatefulRedisConnection<String, String> con=redis.connect()) {
			RedisCommands<String, String> cmd=con.sync();
			String nodeKey="node."+node.getId();
			
			Map<String,String> data=new HashMap<>();
			
			ZonedDateTime now=ZonedDateTime.now();
			
			data.put("lastFetched", DataHelper.encodeToString(now));
			
			if (info!=null) {
				data.put("lastSeen", DataHelper.encodeToString(now));
 				data.put("info",JSONHelper.toJson(info));
				if (stats!=null) {
					data.put("stats",JSONHelper.toJson(stats));
				}
			}
			
			cmd.hset(nodeKey, data);
			
			if (links!=null) {
				data.clear();
				for (NodeLinkUpdate nlu: links) {
					String left,right;
					int compare=node.getId().compareToIgnoreCase(nlu.getOtherNodeId());
					if (compare==0) {
						continue; // link to same node???
					}
					if (compare<0) { // ensure correct order for unique link identifier
						left=node.getId();
						right=nlu.getOtherNodeId();
						data.put("leftTs", DataHelper.encodeToString(now));
						data.put("leftRq", DataHelper.encodeToString(nlu.getRq()));
						data.put("leftTq", DataHelper.encodeToString(nlu.getTq()));
						
					} else {
						left=nlu.getOtherNodeId();
						right=node.getId();
						data.put("rightTs", DataHelper.encodeToString(now));
						data.put("rightRq", DataHelper.encodeToString(nlu.getRq()));
						data.put("rightTq", DataHelper.encodeToString(nlu.getTq()));
					}
					
					data.put("leftNodeId", left);
					data.put("rightNodeId", right);
					data.put("type", DataHelper.encodeToString(nlu.getType()));
					
					cmd.hset("link."+left+"."+right+"."+nlu.getType(), data);
				}
			}
			
		}
	}

	@Override
	public int addNewNodes(List<NodeAddress> nodes) {
		Collection<String> known=getKnownNodeIds();
		int newCount=0;
		for (NodeAddress n: nodes) {
			if (!known.contains(n.getId())) {
				if (addNode(n)) {
					newCount++;
				}
			}
		}
		return newCount;
	}
	
}

