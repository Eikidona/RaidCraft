package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.Collection;
import java.util.Map;

public class ReloadListener<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
	public static final String JSON_EXTENSION = ".json";
	protected final String folder;
	protected final String prefix;
	protected final Codec<T> codec;
	protected final BiMap<ResourceLocation, T> loadedData = HashBiMap.create();
	
	public ReloadListener(String folder, Codec<T> codec) {
		this.folder = folder;
		this.prefix = folder + '/';
		this.codec = codec;
	}
	
	@Override
	protected @NotNull Map<ResourceLocation, T> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller filler) {
		return listResources(resourceManager, filler);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, T> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller filler) {
		map.putAll(loadedData);
	}
	
	private Map<ResourceLocation, T> listResources(ResourceManager resourceManager, ProfilerFiller profiler) {
		profiler.startTick();
		for (Map.Entry<ResourceLocation, Resource> resource : resourceManager.listResources(folder, p -> p.getPath().endsWith(JSON_EXTENSION)).entrySet()) {
			
			if (!resource.getKey().getPath().startsWith(prefix)) continue;
			ResourceLocation name = new ResourceLocation(resource.getKey().getNamespace(), resource.getKey().getPath().replace(prefix, "").replace(JSON_EXTENSION, ""));
			
			try (Reader reader = resource.getValue().openAsReader()) {
				JsonElement element = JsonParser.parseReader(reader);
				codec.parse(JsonOps.INSTANCE, element)
					 .resultOrPartial(error -> RaidCraft.LOGGER.error("Failed to parse DataPack Entry: {}", error))
					 .ifPresent(instance -> {
						 register(name, instance);
						 RaidCraft.LOGGER.info("DataPack Entry {} is register! id: {}", folder, name);
					 });
			} catch (JsonParseException e) {
				RaidCraft.LOGGER.error("Failed to parse JSON for DataPack entry: {}, {}", name, resource.getKey(), e);
			} catch (Exception e) {
				RaidCraft.LOGGER.error("Failed to load DataPack entry: {}, {}", name, resource.getKey(), e);
			}
		}
		profiler.endTick();
		return loadedData;
	}
	
	public @Nullable T getValue(ResourceLocation name) {
		return loadedData.get(name);
	}
	
	public @Nullable ResourceLocation getKey(T value) {
		return loadedData.inverse().get(value);
	}
	
	public @Nullable T getRandomValue() {
		Collection<T> collection = getValues();
		if (collection.isEmpty()) {
			return null;
		}
		return collection.stream().toList().get((int) (Math.random() * collection.size()));
	}
	
	public @Nullable ResourceLocation getRandomKey() {
		Collection<ResourceLocation> collection = getKeys();
		if(collection.isEmpty()){
			return null;
		}
		return collection.stream().toList().get((int) (Math.random() * collection.size()));
	}
	
	public boolean containsValue(T value) {
		return loadedData.containsValue(value);
	}
	
	public boolean containsKey(ResourceLocation name) {
		return loadedData.containsKey(name);
	}
	
	public void register(ResourceLocation name, T value) {
		loadedData.put(name, value);
	}
	
	public Collection<T> getValues() {
		return loadedData.values();
	}
	
	public Collection<ResourceLocation> getKeys() {
		return loadedData.keySet();
	}
}