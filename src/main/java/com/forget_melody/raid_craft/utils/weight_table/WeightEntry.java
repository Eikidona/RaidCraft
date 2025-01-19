package com.forget_melody.raid_craft.utils.weight_table;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public class WeightEntry<T> implements IWeightEntry<T> {
	private T element;
	private int weight;
	
	public WeightEntry(T element, int weight) {
		this.element = element;
		this.weight = weight;
	}
	
	public int getWeight() {
		return weight;
	}
	
	@Override
	public T get() {
		return element;
	}
	
	public static <T> WeightEntry<T> of(T element, int weight) {
		return new WeightEntry<T>(element, weight);
	}
	
	// 使用泛型方法来创建Codec，避免使用具体的类型
	public static <T> Codec<WeightEntry<T>> createCodec(Codec<T> elementCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				elementCodec.fieldOf("element").forGetter(WeightEntry::get),
				Codec.INT.fieldOf("weight").forGetter(WeightEntry::getWeight)
		).apply(instance, WeightEntry::new));
	}
}