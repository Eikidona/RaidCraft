package com.forget_melody.raid_craft.registries.api;

import com.forget_melody.raid_craft.datapack.Reloads;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber
public class RaidTypeHelper {
	public static boolean hasKey(ResourceLocation name){
		return Reloads.RAID_TYPE.getLoadedData().containsKey(name);
	}
	@Nullable
	public static RaidType get(ResourceLocation id){
		return Reloads.RAID_TYPE.getLoadedData().get(id);
	}
	@Nullable
	public static ResourceLocation getKey(RaidType raidType){
		return Reloads.RAID_TYPE.getLoadedDataKey().get(raidType);
	}
}
