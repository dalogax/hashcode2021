package com.zcom.hashcode.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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
	}

	public void resolve() {
		intersections.forEach(intersection -> {
			List<Street> interStreets = intersection.getInputStreets().stream()
					.filter(this::hasTraffic)
					.map(this.streetsByName::get)
					.sorted(Comparator.comparingInt(a -> a.getNumberOfPassingCars().get()))
					.collect(Collectors.toList());
			if (interStreets.size() > 0) {
				int min = interStreets.get(0).getNumberOfPassingCars().get();
				int max = interStreets.get(interStreets.size() - 1).getNumberOfPassingCars().get();

				Street smallest = interStreets.stream().min(Comparator.comparingInt(Street::getLength)).get();
				float maxDuration = smallest.getLength() * 1f / interStreets.size() * 1f;

				interStreets.forEach(inputStreet ->
						intersection.getTrafficLights().add(new TrafficLight(inputStreet.getName(), getDuration(min, max, maxDuration, inputStreet.getNumberOfPassingCars().get()))));
			}
		});
	}

	private int getDuration(int minCars, int maxCars, float maxDuration, int cars) {
		float normalCars = ((float) cars - minCars) / ((float) maxCars - minCars);
		float normalDuration = maxDuration - 1f;
		float duration = normalDuration * normalCars + 1f;
		return Math.max(1, (int) duration);
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
