package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.raid.raid.IRaid;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Optional;

@AutoRegisterCapability
public interface IRaidManager extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "raid_manager");
	
	static Optional<IRaidManager> get(ServerLevel level) {
		return level.getCapability(Capabilities.RAID_MANAGER).resolve();
	}
	
	ServerLevel getLevel();
	
	void tick();
	
	IRaid getRaid(int id);
	
	IRaid getRaidAtPos(BlockPos blockPos);
	
	void createRaid(BlockPos blockPos, RaidType raidType);
	
	void createRaid(BlockPos blockPos, ResourceLocation raidType);
	
	HashMap<Integer, IRaid> getRaids();
}
