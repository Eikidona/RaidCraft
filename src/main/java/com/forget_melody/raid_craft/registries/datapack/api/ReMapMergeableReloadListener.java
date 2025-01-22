package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.registries.datapack.api.Internal.Replaceable;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 将其他一些对象映射到注册项，不仅仅是注册名与注册项的映射
 * @param <T> 注册项
 * @param <K> 新映射的Key
 */
public class ReMapMergeableReloadListener<T extends Replaceable, K> extends MergeableReloadListener<T> implements IReMapRegistry<T, K> {
	private final Map<K, T> map = new HashMap<>();
	private final Function<T, Collection<K>> reMapFunc;
	public ReMapMergeableReloadListener(String folder, Codec<T> codec, BiFunction<T, T, T> mergeFunc, Function<T, Collection<K>> reMapFunc) {
		super(folder, codec, mergeFunc);
		this.reMapFunc = reMapFunc;
	}
	
	@Override
	public void register(ResourceLocation name, T value) {
		super.register(name, value);
		reMapFunc.apply(value).forEach(k -> map.put(k, value));
	}
	
	@Override
	public @Nullable T getReMapValue(K key){
		return map.get(key);
	}
	
}
