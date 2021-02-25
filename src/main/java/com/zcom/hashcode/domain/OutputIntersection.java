package com.zcom.hashcode.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Value;

@Value
public class OutputIntersection {

	int id;

	List<TrafficLight> trafficLights;

	public OutputIntersection(int id) {
		this.id = id;
		this.trafficLights = new ArrayList<>(0);
	}
}
