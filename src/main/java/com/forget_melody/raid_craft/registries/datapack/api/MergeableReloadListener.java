package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.registries.datapack.api.Internal.IRegistry;
import com.forget_melody.raid_craft.registries.datapack.api.Internal.Replaceable;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

/**
 * 可合并的Reload重载 function 代指如何处理旧的与新的同名实例
 *
 * @param <T>
 */
public class MergeableReloadListener<T extends Replaceable> extends NormalReloadListener<T> implements IRegistry<T> {
	private final BiFunction<T, T, T> function;
	
	public MergeableReloadListener(String folder, Codec<T> codec, BiFunction<T, T, T> function) {
		super(folder, codec);
		this.function = function;
	}
	
	@Override
	public void register(ResourceLocation name, T value) {
		if (value.isReplace()) {
			LOADED_DATA.put(name, value);
		} else {
			if (containsKey(name)) {
				T oldInstance = getValue(name);
				T mergeInstance = function.apply(oldInstance, value);
				LOADED_DATA.put(name, mergeInstance);
			} else {
				LOADED_DATA.put(name, value);
			}
		}
	}
}