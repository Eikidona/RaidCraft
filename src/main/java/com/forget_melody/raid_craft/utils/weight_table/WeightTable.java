package com.forget_melody.raid_craft.utils.weight_table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WeightTable<T> implements IWeightTable<T> {
	
	private final List<IWeightEntry<T>> entries;
	
	public WeightTable(List<IWeightEntry<T>> list) {
		this.entries = list;
	}
	
	@Override
	public IWeightEntry<T> getEntry() {
		int totalWeight = getTotalWeight();
		int randomWeight = (int) (Math.random() * totalWeight);
		int cumulativeWeight = 0;
		
		for (IWeightEntry<T> entry : entries) {
			cumulativeWeight += entry.getWeight();
			if (cumulativeWeight >= randomWeight) {
				return entry;
			}
		}
		
		return null; // 如果没有找到条目，返回null
	}
	
	@Override
	public T getElement() {
		return getEntry() != null ? getEntry().get() : null;
	}
	
	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}
	
	@Override
	public IWeightTable<T> filter(Predicate<T> predicate) {
		return IWeightTable.of(entries.stream().filter(entry -> predicate.test(entry.get())).toList());
	}
	
	@Override
	public int getTotalWeight() {
		int totalWeight = 0;
		for (IWeightEntry<T> entry : entries) {
			totalWeight += entry.getWeight();
		}
		return totalWeight;
	}
	
	@Override
	public List<IWeightEntry<T>> getEntryList() {
		return entries;
	}
	
	public Stream<T> getElementList() {
		return getEntryList().stream().map(IWeightEntry::get);
	}
	
	public static <T> IWeightTable<T> of(List<IWeightEntry<T>> list) {
		return new WeightTable<T>(list);
	}
	
	public static <T> IWeightTable<T> of() {
		return new WeightTable<T>(new ArrayList<>());
	}
	
	public static class Builder<T> {
		private final List<IWeightEntry<T>> entries = new ArrayList<>();
		
		public Builder() {
		}
		
		public Builder<T> add(T element, int weight) {
			entries.add(WeightEntry.of(element, weight));
			return this;
		}
		
		public Builder<T> addAll(List<WeightEntry<T>> list) {
			entries.addAll(list);
			return this;
		}
		
		public WeightTable<T> build() {
			return new WeightTable<>(entries);
		}
		
		public static <T> Builder<T> create() {
			return new Builder<>();
		}
	}
}