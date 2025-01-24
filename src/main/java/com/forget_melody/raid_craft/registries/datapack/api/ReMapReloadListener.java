package com.forget_melody.raid_craft.registries.datapack.api;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReMapReloadListener<T, K> extends NormalReloadListener<T> implements IReMapRegistry<T, K>{
	private final Map<K, T> map = new HashMap<>();
	private final Function<T, Collection<K>> reMapFunc;
	public ReMapReloadListener(String folder, Codec<T> codec, Function<T, Collection<K>> reMapFunc) {
		super(folder, codec);
		this.reMapFunc = reMapFunc;
	}
	
	@Override
	public void register(ResourceLocation name, T value) {
		super.register(name, value);
		reMapFunc.apply(value).forEach(k -> map.put(k, value));
	}
	
	@Override
	public @Nullable T getValueByReMapKey(K key) {
		return map.get(key);
	}
	
}
