package com.zcom.hashcode.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.zcom.hashcode.domain.Car;
import com.zcom.hashcode.domain.Intersection;
import com.zcom.hashcode.domain.Street;
import com.zcom.hashcode.domain.TrafficLight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor
@Getter
public class SimulationExecutor {

	int duration;

	List<Intersection> intersections;

	Map<String, Street> streetsByName;

	Map<Car, StreetLocation> carsWithLocations;

	int pointsForCompletedCar;

	private final int highestNumberOfPassingCars;

	public SimulationExecutor(int duration, List<Intersection> intersections, Map<String, Street> streetsByName, List<Car> cars,
			int pointsForCompletedCar) {
		super();
		this.duration = duration;
		this.intersections = intersections;
		this.streetsByName = streetsByName;
//		this.carsWithLocations = cars.stream()
//				.collect(Collectors.toMap(
//						Function.identity(),
//						car -> {
//							//car.getPath().get(0)
//							return null;
//						}));
		this.pointsForCompletedCar = pointsForCompletedCar;
		this.highestNumberOfPassingCars = this.streetsByName.values().stream()
				.map(Street::getNumberOfPassingCars)
				.map(AtomicInteger::get)
				.sorted(Comparator.<Integer>naturalOrder().reversed())
				.findFirst()
				.get();
		System.out.println(highestNumberOfPassingCars);
	}

	public void resolve() {
		
		intersections.forEach(intersection -> {
					intersection.getInputStreets().stream()
							.filter(this::hasTraffic)
							.forEach(inputStreet -> intersection.getTrafficLights().add(new TrafficLight(inputStreet, 1)));
		});
		
	}

	private boolean hasTraffic(String streetName) {
		return this.streetsByName.get(streetName).getNumberOfPassingCars().get() > 0;
	}

	private void simulate() {
		IntStream.range(0, this.duration)
				.forEach(x -> this.executeSecond());
	}

	private void executeSecond() {
		
	}

	@Value
	private static final class StreetLocation {
		Street street;

		int location;
	}

}
