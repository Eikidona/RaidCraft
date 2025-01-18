package com.forget_melody.raid_craft.datapack;

import com.forget_melody.raid_craft.IRaidType;
import com.forget_melody.raid_craft.RaidCraft;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Reload extends SimplePreparableReloadListener<Map<ResourceLocation, IRaidType>> {
	// 扫描的字段
	private final String FOLDER;
	
	public Reload(String FOLDER) {
		this.FOLDER = FOLDER;
	}
	
	@Override
	protected Map<ResourceLocation, IRaidType> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
		return Map.of();
	}
	
	@Override
	protected void apply(Map<ResourceLocation, IRaidType> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
	
	}
	
	private Map<ResourceLocation, IRaidType> listResources(ResourceManager pResourceManager, ProfilerFiller pProfiler){
		Map<ResourceLocation, IRaidType> loader = new HashMap<>();
		pProfiler.startTick();
		for(Map.Entry<ResourceLocation, Resource> resource : pResourceManager.listResources(FOLDER, p-> p.getPath().endsWith(".json")).entrySet()){
			try	(Reader reader = resource.getValue().openAsReader()){
				JsonElement element = JsonParser.parseReader(reader);
				// Codec
			} catch (Exception e) {
				RaidCraft.LOGGER.error("Failed to load data pack enttry: {}", resource.getKey(), e);
			}
		}
		pProfiler.endTick();
		return loader;
	}
}
