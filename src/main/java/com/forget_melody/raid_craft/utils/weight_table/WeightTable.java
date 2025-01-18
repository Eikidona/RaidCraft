package com.forget_melody.raid_craft.utils.weight_table;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WeightTable<T> {
	
	private final List<IWeightEntry<T>> weightEntryList;
	
	public WeightTable(List<IWeightEntry<T>> list) {
		this.weightEntryList = list;
	}
	
	/**
	 * 获取随机权重项
	 *
	 * @return
	 */
	@Nullable
	public IWeightEntry<T> getEntry() {
		int totalWeight = getTotalWeight();
		int randomWeight = (int) (Math.random() * totalWeight);
		int cumulativeWeight = 0;
		
		for (IWeightEntry<T> entry : weightEntryList) {
			cumulativeWeight += entry.getWeight();
			if (cumulativeWeight >= randomWeight) {
				return entry;
			}
		}
		
		return null; // 如果没有找到条目，返回null
	}
	
	/**
	 * 总权重
	 *
	 * @return
	 */
	public int getTotalWeight() {
		int totalWeight = 0;
		for (IWeightEntry<T> entry : weightEntryList) {
			totalWeight += entry.getWeight();
		}
		return totalWeight;
	}
	
	/**
	 * 过滤返回新权重表
	 *
	 * @param predicate
	 * @return
	 */
	public WeightTable<T> filter(Predicate<T> predicate) {
		return new WeightTable<>(weightEntryList.stream().filter(entry -> predicate.test(entry.get())).toList());
	}
	
	public List<IWeightEntry<T>> getWeightEntryList() {
		return weightEntryList;
	}
	
	public static class Builder<T> {
		private final List<IWeightEntry<T>> list = new ArrayList<>();
		
		public Builder() {
		
		}
		
		public Builder<T> add(T element, int weight){
			list.add(WeightEntry.of(element, weight));
			return this;
		}
		
		public WeightTable<T> build(){
			return new WeightTable<>(list);
		}
		
		public static <T> Builder<T> create() {
			return new Builder<>();
		}
	}
}