package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.IRaidType;
import com.forget_melody.raid_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;

public interface IRaidManager extends INBTSerializable<CompoundTag> {
	ServerLevel getLevel();
	void tick();
	Raid getRaid(int id);
	Raid getRaidAtPos(BlockPos blockPos);
	Raid createRaid(BlockPos blockPos, IRaidType raidType);
	HashMap<Integer, Raid> getRaids();
}
