package com.forget_melody.raid_craft.registries.datapack.api.Internal;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IRegistry<T> {
	@Nullable T getValue(ResourceLocation name);
	@Nullable ResourceLocation getKey(T value);
	@Nullable T getRandomValue();
	@Nullable ResourceLocation getRandomKey();
	boolean containsValue(T value);
	boolean containsKey(ResourceLocation name);
	void register(ResourceLocation name, T value);
	Collection<T> getValues();
	Collection<ResourceLocation> getKeys();
}
