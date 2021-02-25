package com.zcom.hashcode.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class Intersection {

	int id;

	Set<String> inputStreets;

	Set<String> outputStreets;

	List<TrafficLight> trafficLights;

	public Intersection(int id) {
		this.id = id;
		this.inputStreets = new HashSet<>(0);
		this.outputStreets = new HashSet<>(0);
		this.trafficLights = new ArrayList<>(0);
	}
}
