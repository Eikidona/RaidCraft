package com.forget_melody.raid_craft.utils.weight_table;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WeightTable<T> {
	
	private final List<WeightEntry<T>> weightEntryList;
	
	public WeightTable(List<WeightEntry<T>> list) {
		this.weightEntryList = list;
	}
	
	/**
	 * 获取随机权重项
	 *
	 * @return 权重项
	 */
	@Nullable
	public WeightEntry<T> getEntry() {
		int totalWeight = getTotalWeight();
		int randomWeight = (int) (Math.random() * totalWeight);
		int cumulativeWeight = 0;
		
		for (WeightEntry<T> entry : weightEntryList) {
			cumulativeWeight += entry.getWeight();
			if (cumulativeWeight >= randomWeight) {
				return entry;
			}
		}
		
		return null; // 如果没有找到条目，返回null
	}
	
	@Nullable
	public T getElement() {
		return getEntry() != null ? getEntry().get() : null;
	}
	
	/**
	 * 总权重
	 *
	 * @return 权重表的所有权重
	 */
	public int getTotalWeight() {
		int totalWeight = 0;
		for (WeightEntry<T> entry : weightEntryList) {
			totalWeight += entry.getWeight();
		}
		return totalWeight;
	}
	
	/**
	 * 过滤返回新权重表
	 *
	 * @param predicate 过滤条件
	 * @return 新的权重表实例
	 */
	public WeightTable<T> filter(Predicate<T> predicate) {
		List<WeightEntry<T>> filteredList = weightEntryList.stream()
														   .filter(entry -> predicate.test(entry.get()))
														   .toList();
		return new WeightTable<>(filteredList);
	}
	
	public List<WeightEntry<T>> getEntryList() {
		return weightEntryList;
	}
	
	public Stream<T> getElementList() {
		return getEntryList().stream().map(WeightEntry::get);
	}
	
	public static <T> Codec<WeightTable<T>> createCodec(Codec<WeightEntry<T>> entryCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				entryCodec.listOf().fieldOf("entries").forGetter(WeightTable::getEntryList)
		).apply(instance, WeightTable::new));
	}
	
	public static <T> WeightTable<T> of(List<WeightEntry<T>> list) {
		return new WeightTable<>(list);
	}
	
	public static <T> WeightTable<T> of() {
		return new WeightTable<>(new ArrayList<>());
	}
	
	public static class Builder<T> {
		private final List<WeightEntry<T>> entries = new ArrayList<>();
		
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