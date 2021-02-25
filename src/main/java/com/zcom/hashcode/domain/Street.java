package com.zcom.hashcode.domain;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Value;

@Value
public class Street {

	int startIntersectionId;

	int endIntersectionId;

	String name;

	int length;

	AtomicInteger numberOfPassingCars;

	public Street(int startIntersectionId, int endIntersectionId, String name, int length) {
		this.startIntersectionId = startIntersectionId;
		this.endIntersectionId = endIntersectionId;
		this.name = name;
		this.length = length;
		this.numberOfPassingCars = new AtomicInteger(0);
	}

	public int getNumberOfPassingCarsInt() {
		return this.numberOfPassingCars.get();
	}

}
