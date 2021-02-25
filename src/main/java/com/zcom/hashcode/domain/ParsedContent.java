package com.zcom.hashcode.domain;

import java.util.List;
import java.util.Map;

import lombok.Value;

@Value
public class ParsedContent {

	int duration;

	List<Intersection> intersections;

	Map<String, Street> streetsByName;

	List<Car> cars;

	int pointsForCompletedCar;
}
