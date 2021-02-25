package com.zcom.hashcode.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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

	private final int highestStreetDuration;
	
	private final int lowestStreetDuration;

	private final double averageStreetDuration;

	private final Map<String, Long> firstStepStreetNamesWithNumberOfCars;

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
		this.highestStreetDuration = this.streetsByName.values().stream()
				.map(Street::getLength)
				.sorted(Comparator.<Integer>naturalOrder().reversed())
				.findFirst()
				.get();
		this.lowestStreetDuration = this.streetsByName.values().stream()
				.map(Street::getLength)
				.sorted()
				.findFirst()
				.get();
		this.averageStreetDuration = intersections.stream()
				.flatMapToInt(intersection -> {
					return intersection.getInputStreets().stream()
							.map(this.streetsByName::get)
							.filter(this::hasTraffic)
							.mapToInt(Street::getLength);
				})
				.average()
				.getAsDouble();
		this.firstStepStreetNamesWithNumberOfCars = cars.stream()
				.map(Car::getPath)
				.map(list -> list.get(0))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
	}

	public void resolve() {
		//final Map<Integer, >
		intersections.forEach(intersection -> {
			final Set<Street> streets = intersection.getInputStreets().stream()
					.map(this.streetsByName::get)
					.filter(this::hasTraffic)
					.collect(Collectors.toSet());
			final OptionalDouble averageLength = streets.stream()
					.mapToInt(Street::getLength)
					.average();
			final boolean intersectionOfLongStreets = averageLength.orElse(0D) > this.averageStreetDuration;
			streets
				.stream()
				.sorted(Comparator.comparing(street -> Optional.of(street)
						.map(Street::getName)
						.map(this.firstStepStreetNamesWithNumberOfCars::get)
						.orElse(0L)))
				//.sorted(Comparator.comparing(Street::getNumberOfPassingCarsInt).reversed())
				.map(Street::getName)
				.forEach(inputStreet -> {
					final int numberOfPasses = this.streetsByName.get(inputStreet)
							.getNumberOfPassingCars().get();
					//final int length = this.streetsByName.get(inputStreet).getLength();
					final int numberToAssign = ((numberOfPasses*2) / this.highestNumberOfPassingCars) + 1;
					final int toAssign2 = intersectionOfLongStreets ? numberToAssign * 2 : numberToAssign;
					//final int numberToAssignByMaxDuration = ((length*3) / this.highestStreetDuration) + 1;
					System.out.println(toAssign2);
					intersection.getTrafficLights().add(new TrafficLight(inputStreet, toAssign2));
				});
		});
	}

	private boolean hasTraffic(Street street) {
		return street.getNumberOfPassingCars().get() > 0;
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
