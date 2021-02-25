package com.zcom.hashcode.domain;

import java.util.List;

import lombok.Value;

@Value
public class OutputContent {

	
	int numberOfIntersections;

	List<Intersection> intersections;

	List<Intersection> intersectionsById;

	List<Street> streets;

	List<Car> cars;

	int pointsForCompletedCar;
}
