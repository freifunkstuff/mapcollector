package de.ffle.mapcollector;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MapCollector {
	public static void main(String[] args) {
		
		new SpringApplicationBuilder(MapCollector.class)
			.registerShutdownHook(true)
			.run(args);
	}

}
