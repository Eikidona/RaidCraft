package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.raid.Raid;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;

public interface IRaidManager extends INBTSerializable<CompoundTag> {
	ServerLevel getLevel();
	void tick();
	Raid getRaid(int id);
	Raid getRaidAtPos(BlockPos blockPos);
	Raid createRaid(BlockPos blockPos, RaidType raidType);
	Raid createRaid(BlockPos blockPos, ResourceLocation raidType);
	HashMap<Integer, Raid> getRaids();
}
