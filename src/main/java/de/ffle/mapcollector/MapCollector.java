package de.ffle.mapcollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MapCollector {
	public static void main(String[] args) {
		SpringApplication.run(MapCollector.class, args);
	}

}
