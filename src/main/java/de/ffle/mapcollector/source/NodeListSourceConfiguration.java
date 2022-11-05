package de.ffle.mapcollector.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("fetcher")
public class NodeListSourceConfiguration {
	
	@Bean
	public INodeListSource nodeListSource(@Value("${nodelist.class}") String clazz) throws Exception {
		return (INodeListSource) Class.forName(getClass().getPackageName()+".impl."+clazz).getConstructor().newInstance();
	}

}
