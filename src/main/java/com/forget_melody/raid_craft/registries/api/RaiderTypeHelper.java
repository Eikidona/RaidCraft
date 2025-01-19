package com.forget_melody.raid_craft.registries.api;

import com.forget_melody.raid_craft.datapack.Reloads;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RaiderTypeHelper {
	public static boolean hasKey(ResourceLocation name) {
		return Reloads.RAIDER_TYPE.getLoadedData().containsKey(name);
	}
	
	@Nullable
	public static RaiderType get(ResourceLocation name) {
		return Reloads.RAIDER_TYPE.getLoadedData().get(name);
	}
	
	@Nullable
	public static ResourceLocation getKey(RaiderType raiderType) {
		return Reloads.RAIDER_TYPE.getLoadedDataKey().get(raiderType);
	}
}
