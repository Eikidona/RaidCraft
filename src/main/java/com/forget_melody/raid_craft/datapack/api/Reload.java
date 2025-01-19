package com.forget_melody.raid_craft.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Reload<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
	public static final String JSON_EXTENSION = ".json";
	private final String FOLDER;
	private final String PREFIX;
	private final Codec<T> CODEC;
	private final Map<ResourceLocation, T> LOADED_DATA = new HashMap<>();
	private final Map<T, ResourceLocation> LOADED_DATA_KEY = new HashMap<>();
	
	public Reload(String FOLDER, Codec<T> CODEC) {
		this.FOLDER = FOLDER;
		this.CODEC = CODEC;
		this.PREFIX = FOLDER + '/';
	}
	
	@Override
	protected Map<ResourceLocation, T> prepare(ResourceManager resourceManager, ProfilerFiller filler) {
		return listResources(resourceManager, filler);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, T> map, ResourceManager manager, ProfilerFiller filler) {
		map.putAll(LOADED_DATA);
	}
	
	private Map<ResourceLocation, T> listResources(ResourceManager resourceManager, ProfilerFiller profiler) {
		RaidCraft.LOGGER.info("[RaidCraft] {} listResources is running", FOLDER);
		profiler.startTick();
		for (Map.Entry<ResourceLocation, Resource> resource : resourceManager.listResources(FOLDER, p -> p.getPath().endsWith(JSON_EXTENSION)).entrySet()) {
			
			if(!resource.getKey().getPath().startsWith(PREFIX)) continue;
			ResourceLocation name = new ResourceLocation(resource.getKey().getNamespace(), resource.getKey().getPath().replace(PREFIX, "").replace(JSON_EXTENSION, ""));
			
			try (Reader reader = resource.getValue().openAsReader()) {
				JsonElement element = JsonParser.parseReader(reader);
				CODEC.parse(JsonOps.INSTANCE, element)
					 .resultOrPartial(error -> RaidCraft.LOGGER.error("Failed to parse datapack entry: {}", error))
					 .ifPresent(instance -> {
						 
						 LOADED_DATA.put(name, instance);
						 LOADED_DATA_KEY.put(instance, name);
						 RaidCraft.LOGGER.info("[RaidCraft] Datapack {} is registering! Entry: {}", FOLDER, name);
					 });
			} catch (JsonParseException e) {
				RaidCraft.LOGGER.error("Failed to parse JSON for datapack entry: {}", name, e);
			} catch (Exception e) {
				RaidCraft.LOGGER.error("Failed to load datapack entry: {}", name, e);
			}
		}
		profiler.endTick();
		return LOADED_DATA;
	}
	
	public Map<ResourceLocation, T> getLoadedData() {
		return LOADED_DATA;
	}
	
	public Map<T, ResourceLocation> getLoadedDataKey() {
		return LOADED_DATA_KEY;
	}
}