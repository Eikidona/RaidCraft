package com.forget_melody.raid_craft.utils.weight_table;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface IWeightTable<T> {
	@Nullable IWeightEntry<T> getEntry();
	
	@Nullable T getElement();
	
	IWeightTable<T> filter(Predicate<T> predicate);
	
	boolean isEmpty();
	
	int getTotalWeight();
	
	List<IWeightEntry<T>> getEntryList();
	
	static <T> IWeightTable<T> of(List<IWeightEntry<T>> entries) {
		return WeightTable.of(entries);
	}
	
}
