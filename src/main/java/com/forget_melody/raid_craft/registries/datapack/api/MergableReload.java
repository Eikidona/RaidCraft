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
public class MergableReload<T extends Replaceable> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> implements IRegistry<T> {
	public static final String JSON_EXTENSION = ".json";
	protected final String folder;
	protected final String prefix;
	protected final Codec<T> codec;
	protected final Map<ResourceLocation, T> LOADED_DATA_BY_VALUE = new HashMap<>();
	protected final Map<T, ResourceLocation> LOADED_DATA_BY_KEY = new HashMap<>();
	private final BiFunction<T, T, T> function;
	
	public MergableReload(String folder, Codec<T> codec, BiFunction<T, T, T> function) {
		this.folder = folder;
		this.prefix = folder + '/';
		this.codec = codec;
		this.function = function;
	}
	
	@Override
	protected Map<ResourceLocation, T> prepare(ResourceManager resourceManager, ProfilerFiller filler) {
		return listResources(resourceManager, filler);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, T> map, ResourceManager manager, ProfilerFiller filler) {
		map.putAll(LOADED_DATA_BY_VALUE);
	}
	
	private Map<ResourceLocation, T> listResources(ResourceManager resourceManager, ProfilerFiller profiler) {
		RaidCraft.LOGGER.info("[RaidCraft] {} listResources is running", folder);
		profiler.startTick();
		for (Map.Entry<ResourceLocation, Resource> resource : resourceManager.listResources(folder, p -> p.getPath().endsWith(JSON_EXTENSION)).entrySet()) {
			
			if (!resource.getKey().getPath().startsWith(prefix)) continue;
			ResourceLocation name = new ResourceLocation(resource.getKey().getNamespace(), resource.getKey().getPath().replace(prefix, "").replace(JSON_EXTENSION, ""));
			
			try (Reader reader = resource.getValue().openAsReader()) {
				JsonElement element = JsonParser.parseReader(reader);
				codec.parse(JsonOps.INSTANCE, element)
					 .resultOrPartial(error -> RaidCraft.LOGGER.error("Failed to parse datapack entry: {}", error))
					 .ifPresent(instance -> {
						 // replace == true 直接覆盖
						 if (instance.isReplace()) {
							 register(name, instance);
						 }
						 // 处理合并
						 else {
							 if (LOADED_DATA_BY_VALUE.containsKey(name)) {
								 T mergeInstance = function.apply(LOADED_DATA_BY_VALUE.get(name), instance);
								 register(name, mergeInstance);
							 }else {
								 register(name, instance);
							 }
						 }
					 });
			} catch (JsonParseException e) {
				RaidCraft.LOGGER.error("Failed to parse JSON for datapack entry: {}, {}", name, resource.getKey(), e);
			} catch (Exception e) {
				RaidCraft.LOGGER.error("Failed to parse JSON for datapack entry: {}, {}", name, resource.getKey(), e);
			}
		}
		profiler.endTick();
		return LOADED_DATA_BY_VALUE;
	}
	
	@Override
	public @Nullable T getValue(ResourceLocation name) {
		return LOADED_DATA_BY_VALUE.get(name);
	}
	
	@Override
	public @Nullable ResourceLocation getKey(T value) {
		return LOADED_DATA_BY_KEY.get(value);
	}
	
	@Override
	public boolean containsValue(T value) {
		return LOADED_DATA_BY_KEY.containsKey(value);
	}
	
	@Override
	public boolean containsKey(ResourceLocation name) {
		return LOADED_DATA_BY_VALUE.containsKey(name);
	}
	
	@Override
	public void register(ResourceLocation name, T value) {
		LOADED_DATA_BY_VALUE.put(name, value);
		LOADED_DATA_BY_KEY.put(value, name);
	}
	
	@Override
	public Collection<T> getValues() {
		return LOADED_DATA_BY_VALUE.values();
	}
	
	@Override
	public Collection<ResourceLocation> getKeys() {
		return LOADED_DATA_BY_KEY.values();
	}
}