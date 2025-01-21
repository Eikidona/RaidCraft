package com.forget_melody.raid_craft.utils.weight_table;

import java.util.List;

public interface IWeightEntry<T> {
	T get();
	
	int getWeight();
	
	static <T> IWeightEntry<T> of(T element, int weight) {
		return WeightEntry.of(element, weight);
	}
}
