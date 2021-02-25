package com.zcom.hashcode.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.zcom.hashcode.domain.Intersection;
import com.zcom.hashcode.domain.TrafficLight;

public class HashCodeFileWriter {

	public void writeToOutputFile(List<Intersection> intersections, String filePath) {
		try(final FileWriter fw = new FileWriter(filePath)) {
			final List<Intersection> intersectionsToWrite = intersections.stream()
					.filter(intersection -> !intersection.getTrafficLights().isEmpty())
					.collect(Collectors.toList());
			fw.write(String.valueOf(intersectionsToWrite.size()));
			intersectionsToWrite.stream()
			.forEach(intersection -> {
				
				final List<TrafficLight> trafficLights = intersection.getTrafficLights();
				try {
					fw.write("\n");
					fw.write(String.valueOf(intersection.getId()));
					fw.write("\n");
					fw.write(String.valueOf(trafficLights.size()));
					trafficLights.forEach(trafficLight -> {
						try {
							fw.write("\n");
							fw.write(trafficLight.getStreet() + " " + trafficLight.getDuration());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			// ignore
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
