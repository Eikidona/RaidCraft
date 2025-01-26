package com.forget_melody.raid_craft.registries.datapack.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReMapReloadListener<T, K, V> extends ReloadListener<T> implements IReMapRegistry<T, K, V>{
	private final Map<K, V> reMap = new HashMap<>();
	private final Function<T, Pair<K, V>> reMapFunc;
	public ReMapReloadListener(String folder, Codec<T> codec, Function<T, Pair<K, V>> reMapFunc) {
		super(folder, codec);
		this.reMapFunc = reMapFunc;
	}
	
	@Override
	public void register(ResourceLocation name, T value) {
		super.register(name, value);
		Pair<K, V> pair = reMapFunc.apply(value);
		reMap.put(pair.getFirst(), pair.getSecond());
		
	}
	
	@Override
	public @Nullable V getValueByReMapKey(K key) {
		return reMap.get(key);
	}
	
}
