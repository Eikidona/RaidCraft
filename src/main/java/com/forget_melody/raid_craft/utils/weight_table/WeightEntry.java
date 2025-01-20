package com.forget_melody.raid_craft.utils.weight_table;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public class WeightEntry<T> {
	private final T element;
	private final int weight;
	
	public WeightEntry(T element, int weight) {
		this.element = element;
		this.weight = weight;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public T get() {
		return element;
	}
	
	public static <T> WeightEntry<T> of(T element, int weight) {
		return new WeightEntry<T>(element, weight);
	}
	
	public static <T> Codec<WeightEntry<T>> createCodec(Codec<T> elementCodec, String field) {
		return RecordCodecBuilder.create(instance -> instance.group(
				elementCodec.fieldOf(field).forGetter(WeightEntry::get),
				Codec.INT.fieldOf("weight").forGetter(WeightEntry::getWeight)
		).apply(instance, WeightEntry::new));
	}
}