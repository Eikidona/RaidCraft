package com.forget_melody.raid_craft.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
	// 扫描的字段
	private final String FOLDER;
	private final Codec<T> CODEC;
	private final Map<ResourceLocation, T> LOADED_DATA = new HashMap<>();
	
	/**
	 *
	 * @param FOLDER registryName
	 * @param CODEC Codec
	 */
	public Reload(String FOLDER, Codec<T> CODEC) {
		this.FOLDER = FOLDER;
		this.CODEC = CODEC;
	}
	
	@Override
	protected Map<ResourceLocation, T> prepare(ResourceManager resourceManager, ProfilerFiller filler) {
		return listResources(resourceManager, filler);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, T> map, ResourceManager manager, ProfilerFiller filler) {
		map.putAll(LOADED_DATA);
	}
	
	private Map<ResourceLocation, T> listResources(ResourceManager pResourceManager, ProfilerFiller pProfiler){
		Map<ResourceLocation, T> loader = new HashMap<>();
		pProfiler.startTick();
		for(Map.Entry<ResourceLocation, Resource> resource : pResourceManager.listResources(FOLDER, p-> p.getPath().endsWith(JSON_EXTENSION)).entrySet()){
			try	(Reader reader = resource.getValue().openAsReader()){
				JsonElement element = JsonParser.parseReader(reader);
				// Codec
				CODEC.parse(JsonOps.INSTANCE, element)
						.resultOrPartial(error -> RaidCraft.LOGGER.error("Failed to parse datapack entry {}", error))
						.ifPresent(instance -> LOADED_DATA.put(resource.getKey(), instance));
			} catch (Exception e) {
				RaidCraft.LOGGER.error("Failed to load data pack enttry: {}", resource.getKey(), e);
			}
		}
		pProfiler.endTick();
		return loader;
	}
	
	public Map<ResourceLocation, T> getLoadedData() {
		return LOADED_DATA;
	}
}
