package com.zcom.hashcode.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.zcom.hashcode.domain.Car;
import com.zcom.hashcode.domain.Intersection;
import com.zcom.hashcode.domain.ParsedContent;
import com.zcom.hashcode.domain.Street;

public class FileReader {

	public ParsedContent parseInputFile(File f) {
		try {
			final List<Car> cars = new ArrayList<>();
			
			final Map<String, Street> streetsByName = new HashMap<>();
			final Scanner sc = new Scanner(f);
			final int duration = sc.nextInt();
			final int numberOfIntersections = sc.nextInt();
			final int numberOfStreets = sc.nextInt();
			final int numberOfCars = sc.nextInt();
			final int bonusPoints = sc.nextInt();
			
			final List<Intersection> intersections = IntStream.range(0, numberOfIntersections)
					.mapToObj(Intersection::new)
					.collect(Collectors.toList());
			
			for(int id=0; id<numberOfStreets; id++) {
				final int startIntersection = sc.nextInt();
				final int endIntersection = sc.nextInt();
				final String name = sc.next();
				intersections.get(startIntersection).getOutputStreets().add(name);
				intersections.get(endIntersection).getInputStreets().add(name);
				final int streetDuration = sc.nextInt();
				streetsByName.put(
						name,
						new Street(
							startIntersection,
							endIntersection,
							name,
							streetDuration));
			}
			
			for(int id=0; id<numberOfCars; id++) {
				final int numberOfStreetsToTravel = sc.nextInt();
				final List<String> streetNames = new ArrayList<>(1);
				for(int j=0; j<numberOfStreetsToTravel; j++) {
					final String streetName = sc.next();
					if (j != numberOfStreetsToTravel-1) {
						streetsByName.get(streetName).getNumberOfPassingCars().incrementAndGet();
					}
					streetNames.add(streetName);
				}
				cars.add(new Car(streetNames));
			}
			sc.close();
			return new ParsedContent(duration, intersections, streetsByName, cars, bonusPoints);
		} catch (FileNotFoundException e) {
			// ignore
			throw new RuntimeException(e);
		}
	}
	
}
