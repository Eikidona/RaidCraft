package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.registries.datapack.api.Internal.IRegistry;
import com.forget_melody.raid_craft.registries.datapack.api.Internal.Replaceable;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 可合并的Reload重载 function 代指如何处理旧的与新的同名实例
 *
 * @param <T>
 */
public class MergableReload<T extends Replaceable> extends NormalReload<T> implements IRegistry<T> {
	private final BiFunction<T, T, T> function;
	
	public MergableReload(String folder, Codec<T> codec, BiFunction<T, T, T> function) {
		super(folder, codec);
		this.function = function;
	}
	
	@Override
	public void register(ResourceLocation name, T value) {
		if (value.isReplace()) {
			LOADED_DATA_BY_VALUE.put(name, value);
			LOADED_DATA_BY_KEY.put(value, name);
		} else {
			if (containsKey(name)) {
				T oldInstance = getValue(name);
				T mergeInstance = function.apply(oldInstance, value);
				LOADED_DATA_BY_VALUE.put(name, mergeInstance);
				LOADED_DATA_BY_KEY.put(mergeInstance, name);
			} else {
				LOADED_DATA_BY_VALUE.put(name, value);
				LOADED_DATA_BY_KEY.put(value, name);
			}
		}
	}
}