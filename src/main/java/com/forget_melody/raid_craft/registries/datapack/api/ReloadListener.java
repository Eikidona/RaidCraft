package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class ReloadListener<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
	public static final String JSON_EXTENSION = ".json";
	protected final String directory;
	protected final String prefix;
	protected Codec<T> codec;
	protected Function<JsonElement, @Nullable T> function;
	protected Gson gson;
	protected final BiMap<ResourceLocation, T> loadedData = HashBiMap.create();
	protected ResourceLocation defaultKey;
	protected T defaultValue;
	
	public ReloadListener(String directory, Function<JsonElement, @Nullable T> function) {
		this(directory, function, RaidCraft.DEFAULT_KEY, null);
	}
	
	public ReloadListener(String directory, Function<JsonElement, @Nullable T> function, T defaultValue) {
		this(directory, function, RaidCraft.DEFAULT_KEY, defaultValue);
	}
	
	public ReloadListener(String directory, Function<JsonElement, @Nullable T> function, ResourceLocation defaultKey, T defaultValue) {
		this.directory = directory;
		this.prefix = directory + '/';
		this.function = function;
		this.gson = (new GsonBuilder()).create();
		this.defaultKey = defaultKey;
		this.defaultValue = defaultValue;
	}
	
	public ReloadListener(String directory, Codec<T> codec) {
		this(directory, codec, RaidCraft.DEFAULT_KEY, null);
	}
	
	public ReloadListener(String directory, Codec<T> codec, T defaultValue) {
		this(directory, codec, RaidCraft.DEFAULT_KEY, defaultValue);
	}
	
	public ReloadListener(String directory, Codec<T> codec, ResourceLocation defaultKey, T defaultValue) {
		this.directory = directory;
		this.prefix = directory + '/';
		this.codec = codec;
		this.defaultKey = defaultKey;
		this.defaultValue = defaultValue;
	}
	
	@Override
	protected @NotNull Map<ResourceLocation, T> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller filler) {
		if (defaultValue != null && defaultKey != null) {
			loadedData.put(defaultKey, defaultValue);
		}
		listResources(resourceManager, filler);
		RaidCraft.LOGGER.info("DataPackEntry: {}, Count: {}", directory, loadedData.size());
		return loadedData;
	}
	
	@Override
	protected void apply(Map<ResourceLocation, T> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller filler) {
		map.putAll(loadedData);
	}
	
	private void listResources(ResourceManager resourceManager, ProfilerFiller profiler) {
		for (Map.Entry<ResourceLocation, Resource> resource : resourceManager.listResources(directory, p -> p.getPath().endsWith(JSON_EXTENSION)).entrySet()) {
			
			if (!resource.getKey().getPath().startsWith(prefix)) continue;
			ResourceLocation name = new ResourceLocation(resource.getKey().getNamespace(), resource.getKey().getPath().replace(prefix, "").replace(JSON_EXTENSION, ""));
			
			try (Reader reader = resource.getValue().openAsReader()) {
				JsonElement element = JsonParser.parseReader(reader);
				if (function != null) {
					T instance = function.apply(element);
					if (instance != null) {
						register(name, instance);
					}
				} else if (codec != null) {
					codec.parse(JsonOps.INSTANCE, element)
						 .resultOrPartial(error -> RaidCraft.LOGGER.error("Failed to parse DataPack Entry: {}, {}, {}", name, resource.getKey(), error))
						 .ifPresent(instance -> register(name, instance));
				}
			} catch (JsonParseException e) {
				RaidCraft.LOGGER.error("Failed to parse JSON for DataPack entry: {}, {}", name, resource.getKey(), e);
			} catch (Exception e) {
				RaidCraft.LOGGER.error("Failed to load DataPack entry: {}, {}", name, resource.getKey(), e);
			}
		}
		
	}
	
	public T getValue(ResourceLocation name) {
		return loadedData.get(name);
	}
	
	public ResourceLocation getKey(T value) {
		return loadedData.inverse().get(value);
	}
	
	public boolean containsValue(T value) {
		return loadedData.containsValue(value);
	}
	
	public boolean containsKey(ResourceLocation name) {
		return loadedData.containsKey(name);
	}
	
	public void register(ResourceLocation name, T value) {
		RaidCraft.LOGGER.info("DataPack Entry: {} id: {} is register", directory, name);
		loadedData.put(name, value);
	}
	
	public Collection<T> getValues() {
		return loadedData.values();
	}
	
	public Collection<ResourceLocation> getKeys() {
		return loadedData.keySet();
	}
}