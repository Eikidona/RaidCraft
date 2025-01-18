package com.forget_melody.raid_craft.utils.weight_table;

public interface IWeightEntry<T> {
	int getWeight();
	
	T get();
	
	default IWeightEntry<T> of (int weight, T element){
		return new WeightEntry<>(element, weight);
	}
}
