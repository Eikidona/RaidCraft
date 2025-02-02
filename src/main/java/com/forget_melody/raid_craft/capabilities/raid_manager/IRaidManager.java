package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.Raid;
import com.forget_melody.raid_craft.raid.RaidType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;

@AutoRegisterCapability
public interface IRaidManager extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MOD_ID, "raid_manager");
	
	static IRaidManager get(ServerLevel level) {
		return level.getCapability(Capabilities.RAID_MANAGER).resolve().get();
	}
	
	ServerLevel getLevel();
	
	void tick();
	
	Raid getRaid(int id);
	
	Raid getRaidAtPos(BlockPos blockPos);
	
	Map<Integer, Raid> getRaids();
	
	// 创建一个袭击
	void createRaid(BlockPos blockPos, Faction faction, RaidType type);
}
